package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserProfile::class,
        StudyTask::class,
        MockResult::class,
        PyqQuestion::class,
        Doubt::class,
        CurrentAffair::class,
        VocabWord::class,
        ProgressLog::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun studyTaskDao(): StudyTaskDao
    abstract fun mockResultDao(): MockResultDao
    abstract fun pyqDao(): PyqDao
    abstract fun doubtDao(): DoubtDao
    abstract fun currentAffairsDao(): CurrentAffairsDao
    abstract fun vocabDao(): VocabDao
    abstract fun progressLogDao(): ProgressLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ssc_chsl_mentor_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
