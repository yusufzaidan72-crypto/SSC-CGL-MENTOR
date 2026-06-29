package com.example.data

import com.example.network.GeminiHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*

class StudyRepository(private val db: AppDatabase) {

    val userProfileFlow: Flow<UserProfile?> = db.userDao().getUserProfileFlow()
    val allMockResults: Flow<List<MockResult>> = db.mockResultDao().getAllMockResultsFlow()
    val allDoubts: Flow<List<Doubt>> = db.doubtDao().getAllDoubtsFlow()
    val allCurrentAffairs: Flow<List<CurrentAffair>> = db.currentAffairsDao().getAllCurrentAffairsFlow()
    val allVocabWords: Flow<List<VocabWord>> = db.vocabDao().getAllVocabWordsFlow()
    val allProgressLogs: Flow<List<ProgressLog>> = db.progressLogDao().getAllLogsFlow()
    val allPyqs: Flow<List<PyqQuestion>> = db.pyqDao().getAllQuestionsFlow()

    fun getTasksForDateFlow(date: String): Flow<List<StudyTask>> = db.studyTaskDao().getTasksByDateFlow(date)

    suspend fun getProfileDirect(): UserProfile {
        var profile = db.userDao().getUserProfile()
        if (profile == null) {
            profile = UserProfile()
            db.userDao().insertUserProfile(profile)
        }
        return profile
    }

    suspend fun updateProfile(profile: UserProfile) {
        db.userDao().insertUserProfile(profile)
    }

    suspend fun insertTask(task: StudyTask) {
        db.studyTaskDao().insertTask(task)
    }

    suspend fun updateTaskCompletion(id: Int, isCompleted: Boolean) {
        db.studyTaskDao().updateTaskCompletion(id, isCompleted)
    }

    suspend fun insertDoubt(doubt: Doubt) {
        db.doubtDao().insertDoubt(doubt)
    }

    suspend fun updateVocabBookmark(id: Int, isBookmarked: Boolean) {
        db.vocabDao().updateBookmarkStatus(id, isBookmarked)
    }

    suspend fun updateVocabLearned(id: Int, isLearned: Boolean) {
        db.vocabDao().updateLearnedStatus(id, isLearned)
    }

    suspend fun updateCurrentAffairBookmark(id: Int, isBookmarked: Boolean) {
        db.currentAffairsDao().updateBookmarkStatus(id, isBookmarked)
    }

    suspend fun updatePyqBookmark(id: Int, isBookmarked: Boolean) {
        db.pyqDao().updateBookmarkStatus(id, isBookmarked)
    }

    suspend fun submitMockResult(result: MockResult) {
        db.mockResultDao().insertMockResult(result)
        
        // Let's update Selection Probability dynamically based on score
        val currentProfile = getProfileDirect()
        val scorePercent = result.score / result.maxScore
        val newProbability = (currentProfile.selectionProbability * 0.7f + scorePercent * 0.3f).coerceIn(0.1f, 0.99f)
        
        // Add XP points
        val currentXp = currentProfile.xp + 100
        val currentStreak = currentProfile.streak + 1

        db.userDao().insertUserProfile(
            currentProfile.copy(
                selectionProbability = newProbability,
                xp = currentXp,
                streak = currentStreak
            )
        )

        // Log progress
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = sdf.format(Date())
        db.progressLogDao().insertLog(
            ProgressLog(
                date = today,
                studyHours = currentProfile.studyHours.toFloat(),
                completedChapters = currentProfile.xp / 100,
                accuracy = scorePercent,
                speedSecPerQuestion = (result.durationSec / (result.correctAnswers + result.wrongAnswers).coerceAtLeast(1)).toInt()
            )
        )
    }

    suspend fun addStudyHours(hours: Float) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = sdf.format(Date())
        val profile = getProfileDirect()
        db.progressLogDao().insertLog(
            ProgressLog(
                date = today,
                studyHours = hours,
                completedChapters = profile.xp / 100,
                accuracy = 0.75f,
                speedSecPerQuestion = 45
            )
        )
    }

    suspend fun solvePyq(id: Int, answer: String, originalQuestion: PyqQuestion) {
        // Query AI for detailed explanation
        val prompt = """
            Explain this SSC CHSL PYQ.
            Question: ${originalQuestion.question}
            Options: A) ${originalQuestion.optionA}, B) ${originalQuestion.optionB}, C) ${originalQuestion.optionC}, D) ${originalQuestion.optionD}
            Correct Answer: Option ${originalQuestion.correctOption}
            Student Answered: Option $answer
            
            Format response with:
            1. Short, conversational, friendly tone.
            2. Step-by-step solution.
            3. Why other options are incorrect.
            4. **Exam Trick** or formula.
        """.trimIndent()

        val aiExplanation = GeminiHelper.generateText(prompt)
        db.pyqDao().updateAnswerAndExplanation(id, answer, aiExplanation)

        // Give some XP
        val profile = getProfileDirect()
        val isCorrect = answer == originalQuestion.correctOption
        val xpGain = if (isCorrect) 15 else 5
        db.userDao().insertUserProfile(profile.copy(xp = profile.xp + xpGain))
    }

    suspend fun askDoubt(question: String): Doubt {
        val systemPrompt = "You are an expert SSC CHSL AI Mentor. Solve the student's doubt step-by-step, explaining mathematical shortcuts, English grammar guidelines, or static memory triggers where relevant. Keep it encouraging!"
        val answer = GeminiHelper.generateText(question, systemPrompt)
        val doubt = Doubt(questionText = question, answerText = answer)
        db.doubtDao().insertDoubt(doubt)
        
        // Give XP
        val profile = getProfileDirect()
        db.userDao().insertUserProfile(profile.copy(xp = profile.xp + 10))
        return doubt
    }

    suspend fun generateDailyPlanner(date: String) {
        val profile = getProfileDirect()
        db.studyTaskDao().deleteTasksForDate(date)

        val prompt = """
            Create an intensive, customized study plan for SSC CHSL for today ($date).
            Student attributes:
            - Name: ${profile.name}
            - Study hours: ${profile.studyHours} hours
            - Strong Subject: ${profile.strongSubjects}
            - Weak Subject: ${profile.weakSubjects}
            - Current Skill Level: ${if (profile.isBeginner) "Beginner" else "Experienced"}
            
            Distribute study slots across: Morning, Afternoon, Evening, Night.
            Focus heavily on patching the weak subject (${profile.weakSubjects}) while maintaining practice on the strong subject (${profile.strongSubjects}).
        """.trimIndent()

        val jsonString = GeminiHelper.generateStructuredPlan(prompt)
        try {
            val jsonArray = JSONArray(jsonString)
            val tasks = mutableListOf<StudyTask>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                tasks.add(
                    StudyTask(
                        timeSlot = obj.optString("timeSlot", "Morning"),
                        subject = obj.optString("subject", "General Awareness"),
                        topic = obj.optString("topic", "Constitutional Articles"),
                        description = obj.optString("description", "Read summary notes."),
                        date = date
                    )
                )
            }
            db.studyTaskDao().insertTasks(tasks)
        } catch (e: Exception) {
            // Log backup tasks
            val backup = listOf(
                StudyTask(timeSlot = "Morning", subject = "English", topic = "Idioms & Phrases", description = "Revise top 30 frequently asked idioms in SSC exams.", date = date),
                StudyTask(timeSlot = "Afternoon", subject = "Mathematics", topic = "Compound Interest Shortcuts", description = "Practice 2-year and 3-year difference shortcuts.", date = date),
                StudyTask(timeSlot = "Evening", subject = "Reasoning", topic = "Syllogism - Possibility Cases", description = "Solve 20 Venn-diagram questions. Master the 'some-not' exception.", date = date),
                StudyTask(timeSlot = "Night", subject = "General Awareness", topic = "Modern Indian History - 1857 Revolt", description = "Memorize centers of revolt and leaders using a chronological timeline.", date = date)
            )
            db.studyTaskDao().insertTasks(backup)
        }
    }

    suspend fun seedInitialData() {
        val vocabCount = db.vocabDao().getAllVocabWordsFlow().first().size
        if (vocabCount == 0) {
            // Seed Vocab
            val words = listOf(
                VocabWord(word = "Diligent", meaning = "Having or showing care and conscientiousness in one's work.", synonyms = "Assiduous, Industrious, Meticulous", antonyms = "Lazy, Negligent, Idle", example = "The diligent SSC CHSL aspirant secured rank 1.", type = "Synonym"),
                VocabWord(word = "Anomalous", meaning = "Deviating from what is standard, normal, or expected.", synonyms = "Atypical, Abnormal, Peculiar", antonyms = "Normal, Regular, Standard", example = "The mock exam results displayed an anomalous high score.", type = "Antonym"),
                VocabWord(word = "Bite the bullet", meaning = "Decide to do something difficult that you have been delaying.", synonyms = "Face up to, Endure", antonyms = "Avoid, Evade", example = "It's time to bite the bullet and complete the trigonometry formulas.", type = "Idioms & Phrases"),
                VocabWord(word = "Ambiguous", meaning = "Open to more than one interpretation; not having one obvious meaning.", synonyms = "Equivocal, Obscure, Vague", antonyms = "Clear, Explicit, Lucid", example = "The questions in English GA are rarely ambiguous.", type = "One-Word"),
                VocabWord(word = "Chronicle", meaning = "A factual written account of important historical events.", synonyms = "Record, Register, Journal", antonyms = "Erasure", example = "This app chronicles your journey to selection.", type = "Root Words")
            )
            db.vocabDao().insertVocabWords(words)

            // Seed Current Affairs
            val currentAffairs = listOf(
                CurrentAffair(title = "69th National Film Awards declared", content = "The National Film Awards ceremony was held in New Delhi, celebrating the finest in Indian cinema. Best Actor went to Allu Arjun for Pushpa, while Alia Bhatt and Kriti Sanon shared the Best Actress title.", date = "2026-06-28", category = "Awards"),
                CurrentAffair(title = "ISRO Launches Aditya-L1 Solar Mission", content = "India's premier space research organization successfully placed Aditya-L1 into its halo orbit around Lagrangian Point 1 to study solar flares, solar wind, and coronal heating over a 5-year duration.", date = "2026-06-27", category = "National"),
                CurrentAffair(title = "IMF Projecting 6.8% GDP Growth for India", content = "The International Monetary Fund has upwardly revised India's GDP growth forecast for the current fiscal cycle to 6.8%, driven by robust private demand and strong public infrastructure investments.", date = "2026-06-25", category = "Economy")
            )
            db.currentAffairsDao().insertCurrentAffairs(currentAffairs)

            // Seed PYQ Questions
            val pyqs = listOf(
                PyqQuestion(
                    question = "If A:B = 3:4 and B:C = 8:9, find A:C.",
                    optionA = "1:3", optionB = "2:3", optionC = "3:2", optionD = "1:2",
                    correctOption = "B", subject = "Mathematics", topic = "Ratio & Proportion",
                    difficulty = "Easy", year = 2024, shift = "Shift 1"
                ),
                PyqQuestion(
                    question = "Select the antonym of the given word: 'ABOLISH'",
                    optionA = "Destroy", optionB = "Establish", optionC = "Cancel", optionD = "Eradicate",
                    correctOption = "B", subject = "English Language", topic = "Synonyms & Antonyms",
                    difficulty = "Medium", year = 2024, shift = "Shift 2"
                ),
                PyqQuestion(
                    question = "Which article of the Indian Constitution is related to 'Equality before Law'?",
                    optionA = "Article 14", optionB = "Article 15", optionC = "Article 16", optionD = "Article 17",
                    correctOption = "A", subject = "General Awareness", topic = "Indian Polity",
                    difficulty = "Easy", year = 2023, shift = "Shift 3"
                ),
                PyqQuestion(
                    question = "If CAT is coded as 24 and DOG is coded as 26, then how will TIGER be coded?",
                    optionA = "59", optionB = "49", optionC = "60", optionD = "54",
                    correctOption = "A", subject = "Reasoning", topic = "Coding-Decoding",
                    difficulty = "Hard", year = 2024, shift = "Shift 1"
                )
            )
            db.pyqDao().insertQuestions(pyqs)

            // Seed Progress Log
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val calendar = Calendar.getInstance()
            for (i in 6 downTo 1) {
                calendar.time = Date()
                calendar.add(Calendar.DAY_OF_YEAR, -i)
                val logDate = sdf.format(calendar.time)
                db.progressLogDao().insertLog(
                    ProgressLog(
                        date = logDate,
                        studyHours = (3..6).random().toFloat(),
                        completedChapters = i,
                        accuracy = 0.65f + (i * 0.04f),
                        speedSecPerQuestion = 55 - i
                    )
                )
            }
        }
    }
}
