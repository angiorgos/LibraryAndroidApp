package com.example.project.model
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class Branch(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val location: String
)