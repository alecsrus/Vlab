package com.virtuallabs.tutor

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import kotlin.math.ln
import kotlin.math.sqrt

data class TutorDocument(
    val id: String,
    val title: String,
    val tags: List<String>,
    val body: String
)

data class TutorAnswer(
    val title: String,
    val score: Double,
    val answerText: String
)

/**
 * Маленькая полностью локальная “AI‑модель”:
 * TF‑IDF + cosine similarity по встроенной базе знаний.
 *
 * Плюсы:
 * - работает офлайн,
 * - занимает мало места,
 * - быстро,
 * - легко расширяется.
 */
class LocalTutor(private val context: Context) {

    // Ленивая загрузка — чтобы не тормозить старт приложения
    private val model: TfIdfModel by lazy {
        val docs = loadDocuments()
        TfIdfModel.build(docs)
    }

    suspend fun answer(question: String): TutorAnswer = withContext(Dispatchers.Default) {
        val result = model.query(question)
        val doc = result.document
        val score = result.score

        if (doc == null || score < 0.08) {
            return@withContext TutorAnswer(
                title = "Не уверен, что понял тему",
                score = score,
                answerText = buildString {
                    appendLine("Я работаю офлайн и опираюсь на встроенную базу знаний.")
                    appendLine("Попробуйте переформулировать вопрос и добавить ключевые слова, например:")
                    appendLine("• «маятник период формула»")
                    appendLine("• «парабола вершина ось симметрии»")
                    appendLine("• «французская революция 1789 бастилия»")
                }
            )
        }

        val short = doc.body
            .replace("\n", " ")
            // raw-string чтобы избежать "Illegal escape: \\s" в Kotlin
            .split(Regex("""(?<=[.!?])\s+"""))
            .take(2)
            .joinToString(" ")

        val tags = doc.tags.take(6).joinToString(", ")

        val text = buildString {
            appendLine("**Похоже, вопрос про:** ${doc.title}")
            appendLine()
            appendLine(short)
            appendLine()
            appendLine("**Ключевые слова:** $tags")
            appendLine()
            appendLine("Если хотите — откройте лабораторию по этой теме и попробуйте изменить параметры, а потом спросите меня «почему так происходит?»")
        }

        TutorAnswer(
            title = doc.title,
            score = score,
            answerText = text
        )
    }

    private fun loadDocuments(): List<TutorDocument> {
        val jsonText = context.assets.open("knowledge_base.json")
            .bufferedReader()
            .use { it.readText() }

        val root = JSONObject(jsonText)
        val arr = root.getJSONArray("documents")

        return buildList {
            for (i in 0 until arr.length()) {
                val d = arr.getJSONObject(i)
                val tagsJson = d.optJSONArray("tags")
                val tags = buildList {
                    if (tagsJson != null) {
                        for (j in 0 until tagsJson.length()) add(tagsJson.getString(j))
                    }
                }
                add(
                    TutorDocument(
                        id = d.getString("id"),
                        title = d.getString("title"),
                        tags = tags,
                        body = d.getString("body")
                    )
                )
            }
        }
    }

    private data class QueryResult(val document: TutorDocument?, val score: Double)

    private class TfIdfModel(
        private val docs: List<TutorDocument>,
        private val vocab: Map<String, Int>,
        private val idf: DoubleArray,
        private val docVectors: List<DoubleArray>
    ) {

        companion object {
            fun build(docs: List<TutorDocument>): TfIdfModel {
                val docTokens = docs.map { d ->
                    Tokenizer.tokenize(d.title + " " + d.body + " " + d.tags.joinToString(" "))
                }

                // 1) vocabulary
                val vocab = LinkedHashMap<String, Int>()
                docTokens.flatten().distinct().forEach { token ->
                    if (!vocab.containsKey(token)) vocab[token] = vocab.size
                }

                val nDocs = docs.size
                val df = IntArray(vocab.size)

                // 2) document frequency
                for (tokens in docTokens) {
                    val seen = HashSet<Int>()
                    for (t in tokens) {
                        val idx = vocab[t] ?: continue
                        if (seen.add(idx)) df[idx] += 1
                    }
                }

                // 3) idf
                val idf = DoubleArray(vocab.size) { i ->
                    // сглаженный idf
                    ln((nDocs + 1.0) / (df[i] + 1.0)) + 1.0
                }

                // 4) document vectors (tf * idf), normalized
                val docVectors = docs.indices.map { docIndex ->
                    val vec = DoubleArray(vocab.size)
                    val tokens = docTokens[docIndex]
                    if (tokens.isNotEmpty()) {
                        val tf = HashMap<Int, Int>()
                        for (t in tokens) {
                            val idx = vocab[t] ?: continue
                            tf[idx] = (tf[idx] ?: 0) + 1
                        }
                        for ((idx, count) in tf) {
                            vec[idx] = count.toDouble() * idf[idx]
                        }
                        normalizeInPlace(vec)
                    }
                    vec
                }

                return TfIdfModel(docs, vocab, idf, docVectors)
            }
        }

        fun query(question: String): QueryResult {
            val qTokens = Tokenizer.tokenize(question)
            if (qTokens.isEmpty()) return QueryResult(null, 0.0)

            val qVec = DoubleArray(vocab.size)
            val tf = HashMap<Int, Int>()
            for (t in qTokens) {
                val idx = vocab[t] ?: continue
                tf[idx] = (tf[idx] ?: 0) + 1
            }
            for ((idx, count) in tf) {
                qVec[idx] = count.toDouble() * idf[idx]
            }
            normalizeInPlace(qVec)

            var bestScore = -1.0
            var bestIdx: Int? = null

            for (i in docVectors.indices) {
                val s = dot(qVec, docVectors[i])
                if (s > bestScore) {
                    bestScore = s
                    bestIdx = i
                }
            }

            return QueryResult(bestIdx?.let { docs[it] }, bestScore.coerceAtLeast(0.0))
        }
    }
}

private fun normalizeInPlace(v: DoubleArray) {
    var sumSq = 0.0
    for (x in v) sumSq += x * x
    val norm = sqrt(sumSq)
    if (norm > 0.0) {
        for (i in v.indices) v[i] /= norm
    }
}

private fun dot(a: DoubleArray, b: DoubleArray): Double {
    var s = 0.0
    val n = minOf(a.size, b.size)
    for (i in 0 until n) s += a[i] * b[i]
    return s
}
