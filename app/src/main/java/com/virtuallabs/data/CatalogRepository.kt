package com.virtuallabs.data

import android.content.Context
import org.json.JSONObject

class CatalogRepository(private val context: Context) {

    private var cached: Catalog? = null

    fun loadCatalog(): Catalog {
        cached?.let { return it }

        val jsonText = context.assets.open("catalog.json")
            .bufferedReader()
            .use { it.readText() }

        val root = JSONObject(jsonText)

        val subjectsJson = root.getJSONArray("subjects")
        val subjects = buildList {
            for (i in 0 until subjectsJson.length()) {
                val s = subjectsJson.getJSONObject(i)
                add(
                    Subject(
                        id = s.getString("id"),
                        title = s.getString("title"),
                        emoji = s.optString("emoji", "📚"),
                        category = s.optString("category", "OTHER")
                    )
                )
            }
        }

        val topicsJson = root.getJSONArray("topics")
        val topics = buildList {
            for (i in 0 until topicsJson.length()) {
                val t = topicsJson.getJSONObject(i)
                add(
                    Topic(
                        id = t.getString("id"),
                        subjectId = t.getString("subjectId"),
                        title = t.getString("title"),
                        gradeFrom = t.optInt("gradeFrom", 1),
                        gradeTo = t.optInt("gradeTo", 11),
                        premium = t.optBoolean("premium", false),
                        labType = runCatching { LabType.valueOf(t.getString("labType")) }
                            .getOrElse { LabType.PLACEHOLDER },
                        description = t.optString("description", "")
                    )
                )
            }
        }

        return Catalog(subjects = subjects, topics = topics).also { cached = it }
    }

    fun subjects(): List<Subject> = loadCatalog().subjects

    fun topicsForSubject(subjectId: String): List<Topic> =
        loadCatalog().topics.filter { it.subjectId == subjectId }.sortedBy { it.title }

    fun topicById(topicId: String): Topic? =
        loadCatalog().topics.firstOrNull { it.id == topicId }

    fun subjectById(subjectId: String): Subject? =
        loadCatalog().subjects.firstOrNull { it.id == subjectId }
}
