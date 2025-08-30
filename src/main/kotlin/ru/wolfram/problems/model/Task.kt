package ru.wolfram.problems.model

import jakarta.persistence.*

@Entity
@Table(name = "tasks")
class TaskDbo(
    @Id
    @Column(name = "task_name", nullable = false)
    var taskName: String? = null,

    @Column(nullable = false, columnDefinition = "TEXT")
    var descriptionMarkdown: String? = null,

    @Convert(converter = StringListConverter::class)
    @Column(name = "test_input", nullable = false, columnDefinition = "TEXT")
    var input: List<String>? = null,

    @Convert(converter = StringListConverter::class)
    @Column(name = "test_output", nullable = false, columnDefinition = "TEXT")
    var output: List<String>? = null,
) {
    companion object {
        fun toTask(taskDbo: TaskDbo): Task? {
            val name = taskDbo.taskName
            val descriptionMarkdown = taskDbo.descriptionMarkdown
            if (name == null || descriptionMarkdown == null) {
                return null
            }
            return Task(
                name = name,
                descriptionMarkdown = descriptionMarkdown
            )
        }
    }
}

@Converter
class StringListConverter : AttributeConverter<MutableList<String?>?, String?> {
    override fun convertToDatabaseColumn(stringList: MutableList<String?>?): String {
        return stringList?.joinToString(SPLIT_CHAR) ?: ""
    }

    override fun convertToEntityAttribute(string: String?): MutableList<String?> {
        return string?.split(SPLIT_CHAR)?.toMutableList() ?: mutableListOf()
    }

    companion object {
        private const val SPLIT_CHAR = "@%@"
    }
}

data class Task(
    val name: String,
    val descriptionMarkdown: String
)