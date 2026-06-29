package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfileFlow(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfile(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(userProfile: UserProfile)

    @Update
    suspend fun updateUserProfile(userProfile: UserProfile)
}

@Dao
interface StudyTaskDao {
    @Query("SELECT * FROM study_tasks WHERE date = :date ORDER BY id ASC")
    fun getTasksByDateFlow(date: String): Flow<List<StudyTask>>

    @Query("SELECT * FROM study_tasks ORDER BY id ASC")
    fun getAllTasksFlow(): Flow<List<StudyTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<StudyTask>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: StudyTask)

    @Query("UPDATE study_tasks SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateTaskCompletion(id: Int, isCompleted: Boolean)

    @Query("DELETE FROM study_tasks WHERE date = :date")
    suspend fun deleteTasksForDate(date: String)

    @Query("DELETE FROM study_tasks")
    suspend fun clearAllTasks()
}

@Dao
interface MockResultDao {
    @Query("SELECT * FROM mock_results ORDER BY date DESC")
    fun getAllMockResultsFlow(): Flow<List<MockResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMockResult(result: MockResult)
}

@Dao
interface PyqDao {
    @Query("SELECT * FROM pyq_questions ORDER BY id DESC")
    fun getAllQuestionsFlow(): Flow<List<PyqQuestion>>

    @Query("SELECT * FROM pyq_questions WHERE subject = :subject ORDER BY id DESC")
    fun getQuestionsBySubjectFlow(subject: String): Flow<List<PyqQuestion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<PyqQuestion>)

    @Query("UPDATE pyq_questions SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmarkStatus(id: Int, isBookmarked: Boolean)

    @Query("UPDATE pyq_questions SET userAnswer = :userAnswer, aiExplanation = :aiExplanation WHERE id = :id")
    suspend fun updateAnswerAndExplanation(id: Int, userAnswer: String, aiExplanation: String)
}

@Dao
interface DoubtDao {
    @Query("SELECT * FROM doubts ORDER BY timestamp DESC")
    fun getAllDoubtsFlow(): Flow<List<Doubt>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoubt(doubt: Doubt)
}

@Dao
interface CurrentAffairsDao {
    @Query("SELECT * FROM current_affairs ORDER BY date DESC")
    fun getAllCurrentAffairsFlow(): Flow<List<CurrentAffair>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentAffairs(articles: List<CurrentAffair>)

    @Query("UPDATE current_affairs SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmarkStatus(id: Int, isBookmarked: Boolean)
}

@Dao
interface VocabDao {
    @Query("SELECT * FROM vocab_words ORDER BY id DESC")
    fun getAllVocabWordsFlow(): Flow<List<VocabWord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVocabWords(words: List<VocabWord>)

    @Query("UPDATE vocab_words SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmarkStatus(id: Int, isBookmarked: Boolean)

    @Query("UPDATE vocab_words SET isLearned = :isLearned WHERE id = :id")
    suspend fun updateLearnedStatus(id: Int, isLearned: Boolean)
}

@Dao
interface ProgressLogDao {
    @Query("SELECT * FROM progress_logs ORDER BY date ASC")
    fun getAllLogsFlow(): Flow<List<ProgressLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ProgressLog)
}
