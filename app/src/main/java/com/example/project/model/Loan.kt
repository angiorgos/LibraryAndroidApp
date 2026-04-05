package com.example.project.model

data class Loan(
    val loanId: String = "",
    val bookId: Long = 0,
    val branchId: Long = 0,
    val user: String = "",
    val dateLoaned: String = "",
    val dateReturn: String = ""
)