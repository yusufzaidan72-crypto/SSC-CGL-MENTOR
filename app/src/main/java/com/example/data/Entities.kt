package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "CHSL Aspirant",
    val studyHours: Int = 4,
    val targetYear: Int = 2026,
    val targetScore: Int = 160,
    val weakSubjects: String = "General Awareness",
    val strongSubjects: String = "Mathematics",
    val isBeginner: Boolean = true,
    val xp: Int = 120,
    val streak: Int = 3,
    val selectionProbability: Float = 0.42f,
    val onboarded: Boolean = false
)

@Entity(tableName = "study_tasks")
data class StudyTask(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timeSlot: String, // "Morning", "Afternoon", "Evening", "Night"
    val subject: String,
    val topic: String,
    val description: String,
    val isCompleted: Boolean = false,
    val date: String // YYYY-MM-DD
)

@Entity(tableName = "mock_results")
data class MockResult(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val score: Float,
    val maxScore: Float = 200f,
    val correctAnswers: Int,
    val wrongAnswers: Int,
    val skippedAnswers: Int,
    val durationSec: Long,
    val date: Long = System.currentTimeMillis(),
    val aiFeedback: String = ""
)

@Entity(tableName = "pyq_questions")
data class PyqQuestion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val question: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOption: String, // A, B, C, D
    val subject: String,
    val topic: String,
    val difficulty: String, // "Easy", "Medium", "Hard"
    val year: Int,
    val shift: String,
    val isBookmarked: Boolean = false,
    val userAnswer: String? = null,
    val aiExplanation: String? = null
)

@Entity(tableName = "doubts")
data class Doubt(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val questionText: String,
    val imageUrl: String? = null,
    val answerText: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "current_affairs")
data class CurrentAffair(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val date: String, // YYYY-MM-DD
    val category: String, // "National", "International", "Sports", "Economy", "Awards"
    val isBookmarked: Boolean = false
)

@Entity(tableName = "vocab_words")
data class VocabWord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val word: String,
    val meaning: String,
    val synonyms: String,
    val antonyms: String,
    val example: String,
    val type: String, // "Synonym", "Antonym", "One-Word", "Idioms & Phrases", "Root Words"
    val isLearned: Boolean = false,
    val isBookmarked: Boolean = false
)

@Entity(tableName = "progress_logs")
data class ProgressLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String, // YYYY-MM-DD
    val studyHours: Float,
    val completedChapters: Int,
    val accuracy: Float,
    val speedSecPerQuestion: Int
)
