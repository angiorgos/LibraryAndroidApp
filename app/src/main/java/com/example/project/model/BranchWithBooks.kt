package com.example.project.model


import androidx.room.Embedded
import androidx.room.Relation
import androidx.room.Junction

data class BranchWithBooks(
    @Embedded val branch: Branch,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = BookBranch::class,
            parentColumn = "branchId",
            entityColumn = "bookId"
        )
    )
    val books: List<Book>
)
