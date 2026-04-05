package com.example.project.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.project.model.Book
import com.example.project.model.BookBranch
import com.example.project.model.Branch

@Database(
    entities = [Book::class, Branch::class, BookBranch::class],
    version = 5
)
abstract class LibraryDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
}
