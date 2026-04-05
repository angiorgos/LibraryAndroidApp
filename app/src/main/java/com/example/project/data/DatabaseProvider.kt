package com.example.project.data

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    @Volatile
    private var INSTANCE: LibraryDatabase? = null

    fun provideDatabase(context: Context): LibraryDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                LibraryDatabase::class.java,
                "library_database"
            )
                .fallbackToDestructiveMigration() //Η Room θα διαγράψει την παλιά βάση και θα δημιουργήσει τη νέα (με τα σωστά tables & schema)
                .build()

            INSTANCE = instance
            instance
        }
    }
}
