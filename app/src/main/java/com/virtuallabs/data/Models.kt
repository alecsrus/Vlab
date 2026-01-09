package com.virtuallabs.data

enum class LabType {
    PENDULUM,
    QUADRATIC,
    TIMELINE,
    PLACEHOLDER
}

data class Subject(
    val id: String,
    val title: String,
    val emoji: String,
    val category: String
)

data class Topic(
    val id: String,
    val subjectId: String,
    val title: String,
    val gradeFrom: Int,
    val gradeTo: Int,
    val premium: Boolean,
    val labType: LabType,
    val description: String
)

data class Catalog(
    val subjects: List<Subject>,
    val topics: List<Topic>
)
