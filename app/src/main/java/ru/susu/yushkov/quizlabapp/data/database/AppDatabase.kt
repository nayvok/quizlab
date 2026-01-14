package ru.susu.yushkov.quizlabapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.susu.yushkov.quizlabapp.data.dao.QuestionDao
import ru.susu.yushkov.quizlabapp.data.dao.QuizDao
import ru.susu.yushkov.quizlabapp.data.entities.QuestionEntity
import ru.susu.yushkov.quizlabapp.data.entities.QuizEntity

@Database(
    entities = [QuizEntity::class, QuestionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun quizDao(): QuizDao
    abstract fun questionDao(): QuestionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "quiz_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
