package com.virtuallabs.tutor

object Tokenizer {

    private val stopwords = setOf(
        // RU
        "и","в","во","на","но","а","что","это","как","к","ко","из","у","по","за","от","до","для",
        "с","со","о","об","про","при","над","под","между","же","ли","бы","не","ни","да","нет","то",
        "все","всё","или","если","тогда","когда","где","куда","откуда","почему","зачем","какой","какая","какие",
        // EN
        "the","a","an","and","or","to","in","on","of","for","with","is","are","was","were","be","as","at","by"
    )

    // ВАЖНО: используем raw-string, чтобы не ловить "Illegal escape: \\p" в Kotlin строках.
    // \p{L}  — любая буква (любой язык), \p{Nd} — десятичные цифры.
    private val splitter = Regex("""[^\p{L}\p{Nd}]+""")

    fun tokenize(text: String): List<String> {
        return text
            .lowercase()
            .split(splitter)
            .asSequence()
            .map { it.trim() }
            .filter { it.length >= 2 }
            .filterNot { it in stopwords }
            .toList()
    }
}
