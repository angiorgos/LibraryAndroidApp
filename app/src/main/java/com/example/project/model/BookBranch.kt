package com.example.project.model
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    primaryKeys = ["bookId", "branchId"],
    foreignKeys = [
        ForeignKey(
            entity = Book::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Branch::class,
            parentColumns = ["id"],
            childColumns = ["branchId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class BookBranch(
    val bookId: Long,
    val branchId: Long
)
