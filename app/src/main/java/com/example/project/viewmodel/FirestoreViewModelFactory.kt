package com.example.project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.project.data.FirestoreRepository

class FirestoreViewModelFactory(
    private val repository: FirestoreRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FirestoreViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FirestoreViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
