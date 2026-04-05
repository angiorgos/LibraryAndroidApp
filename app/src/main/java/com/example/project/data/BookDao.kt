package com.example.project.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.project.model.Book
import com.example.project.model.BookBranch
import com.example.project.model.Branch
import com.example.project.model.BranchWithBooks

@Dao
interface BookDao {
    @Insert
    suspend fun insertBook(book: Book)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<Book>)


    @Insert
    suspend fun insertBranch(branch: Branch)

    @Insert
    suspend fun insertBookBranchCrossRef(bookBranch: BookBranch)

    @Insert
    suspend fun insertBranchReturningId(branch: Branch): Long

    @Transaction
    @Query("SELECT * FROM branch WHERE id = :branchId")
    suspend fun getBooksForBranch(branchId: Long): BranchWithBooks

    @Query("SELECT * FROM books")
    suspend fun getAllBooks(): List<Book>

    @Query("SELECT * FROM branch")
    suspend fun getAllBranches(): List<Branch>

    @Query("DELETE FROM books WHERE id = :id")
    suspend fun deleteBookById(id: Long)

    @Query("DELETE FROM branch WHERE id = :id")
    suspend fun deleteBranchById(id: Long)

    @Query("SELECT COUNT(*) FROM BookBranch WHERE bookId = :bookId AND branchId = :branchId")
    suspend fun countBookBranchCrossRef(bookId: Long, branchId: Long): Int

}
