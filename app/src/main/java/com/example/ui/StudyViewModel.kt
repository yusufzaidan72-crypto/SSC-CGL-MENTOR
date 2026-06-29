package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.network.GeminiHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class StudyViewModel(application: Application, private val repository: StudyRepository) : AndroidViewModel(application) {

    // --- Flows from Database ---
    val userProfile: StateFlow<UserProfile?> = repository.userProfileFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _currentDate = MutableStateFlow(getTodayDateString())
    val currentDate: StateFlow<String> = _currentDate.asStateFlow()

    val todayTasks: StateFlow<List<StudyTask>> = _currentDate
        .flatMapLatest { date -> repository.getTasksForDateFlow(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mockResults: StateFlow<List<MockResult>> = repository.allMockResults
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val doubts: StateFlow<List<Doubt>> = repository.allDoubts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentAffairs: StateFlow<List<CurrentAffair>> = repository.allCurrentAffairs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val vocabWords: StateFlow<List<VocabWord>> = repository.allVocabWords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val progressLogs: StateFlow<List<ProgressLog>> = repository.allProgressLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pyqs: StateFlow<List<PyqQuestion>> = repository.allPyqs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Interactive State Managers ---
    private val _isGeneratingPlan = MutableStateFlow(false)
    val isGeneratingPlan: StateFlow<Boolean> = _isGeneratingPlan.asStateFlow()

    private val _isSolvingDoubt = MutableStateFlow(false)
    val isSolvingDoubt: StateFlow<Boolean> = _isSolvingDoubt.asStateFlow()

    private val _lastCreatedDoubt = MutableStateFlow<Doubt?>(null)
    val lastCreatedDoubt: StateFlow<Doubt?> = _lastCreatedDoubt.asStateFlow()

    // --- Active Mock Test State ---
    private val _activeTestQuestions = MutableStateFlow<List<PyqQuestion>>(emptyList())
    val activeTestQuestions: StateFlow<List<PyqQuestion>> = _activeTestQuestions.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _selectedAnswers = MutableStateFlow<Map<Int, String>>(emptyMap()) // pyqId -> "A"/"B"/"C"/"D"
    val selectedAnswers: StateFlow<Map<Int, String>> = _selectedAnswers.asStateFlow()

    private val _testTimeRemaining = MutableStateFlow(600L) // 10 minutes in seconds
    val testTimeRemaining: StateFlow<Long> = _testTimeRemaining.asStateFlow()

    private val _isTestActive = MutableStateFlow(false)
    val isTestActive: StateFlow<Boolean> = _isTestActive.asStateFlow()

    private val _latestMockResult = MutableStateFlow<MockResult?>(null)
    val latestMockResult: StateFlow<MockResult?> = _latestMockResult.asStateFlow()

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            repository.seedInitialData()
            ensureTodayTasksExist()
        }
    }

    private suspend fun ensureTodayTasksExist() {
        val tasks = todayTasks.first()
        if (tasks.isEmpty()) {
            repository.generateDailyPlanner(getTodayDateString())
        }
    }

    // --- User Actions ---

    fun completeOnboarding(
        name: String,
        studyHours: Int,
        targetYear: Int,
        targetScore: Int,
        weakSubject: String,
        strongSubject: String,
        isBeginner: Boolean
    ) {
        viewModelScope.launch {
            val profile = UserProfile(
                name = name,
                studyHours = studyHours,
                targetYear = targetYear,
                targetScore = targetScore,
                weakSubjects = weakSubject,
                strongSubjects = strongSubject,
                isBeginner = isBeginner,
                onboarded = true,
                xp = 200,
                streak = 1,
                selectionProbability = if (isBeginner) 0.35f else 0.52f
            )
            repository.updateProfile(profile)
            repository.generateDailyPlanner(getTodayDateString())
        }
    }

    fun completeTask(id: Int, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.updateTaskCompletion(id, isCompleted)
            
            // Increment XP on task completion
            val profile = repository.getProfileDirect()
            val bonusXp = if (isCompleted) 25 else -25
            repository.updateProfile(profile.copy(xp = (profile.xp + bonusXp).coerceAtLeast(0)))
        }
    }

    fun addManualStudyLog(hours: Float) {
        viewModelScope.launch {
            repository.addStudyHours(hours)
        }
    }

    fun regenerateStudyPlan() {
        viewModelScope.launch {
            _isGeneratingPlan.value = true
            repository.generateDailyPlanner(getTodayDateString())
            _isGeneratingPlan.value = false
        }
    }

    fun toggleVocabBookmark(id: Int, isBookmarked: Boolean) {
        viewModelScope.launch {
            repository.updateVocabBookmark(id, isBookmarked)
        }
    }

    fun toggleVocabLearned(id: Int, isLearned: Boolean) {
        viewModelScope.launch {
            repository.updateVocabLearned(id, isLearned)
        }
    }

    fun toggleCurrentAffairBookmark(id: Int, isBookmarked: Boolean) {
        viewModelScope.launch {
            repository.updateCurrentAffairBookmark(id, isBookmarked)
        }
    }

    fun togglePyqBookmark(id: Int, isBookmarked: Boolean) {
        viewModelScope.launch {
            repository.updatePyqBookmark(id, isBookmarked)
        }
    }

    fun solvePyqSingle(pyqId: Int, answer: String) {
        viewModelScope.launch {
            val list = pyqs.value
            val q = list.find { it.id == pyqId } ?: return@launch
            repository.solvePyq(pyqId, answer, q)
        }
    }

    fun askDoubtStepByStep(query: String) {
        viewModelScope.launch {
            _isSolvingDoubt.value = true
            val doubt = repository.askDoubt(query)
            _lastCreatedDoubt.value = doubt
            _isSolvingDoubt.value = false
        }
    }

    fun clearLastDoubt() {
        _lastCreatedDoubt.value = null
    }

    // --- Mock Testing State Machine ---

    fun startMockTest() {
        val availablePyqs = pyqs.value.shuffled()
        // Create a 10-question compact mini-mock test
        _activeTestQuestions.value = availablePyqs.take(10)
        _currentQuestionIndex.value = 0
        _selectedAnswers.value = emptyMap()
        _testTimeRemaining.value = 600 // 10 minutes
        _isTestActive.value = true
        _latestMockResult.value = null

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_testTimeRemaining.value > 0 && _isTestActive.value) {
                delay(1000)
                _testTimeRemaining.value -= 1
            }
            if (_isTestActive.value) {
                submitMockTest()
            }
        }
    }

    fun selectMockAnswer(questionId: Int, option: String) {
        val current = _selectedAnswers.value.toMutableMap()
        current[questionId] = option
        _selectedAnswers.value = current
    }

    fun navigateMockQuestion(offset: Int) {
        val next = _currentQuestionIndex.value + offset
        if (next in 0 until _activeTestQuestions.value.size) {
            _currentQuestionIndex.value = next
        }
    }

    fun submitMockTest() {
        timerJob?.cancel()
        _isTestActive.value = false

        viewModelScope.launch {
            val questions = _activeTestQuestions.value
            val answers = _selectedAnswers.value
            var correctCount = 0
            var incorrectCount = 0
            var skippedCount = 0

            questions.forEach { q ->
                val chosen = answers[q.id]
                if (chosen == null) {
                    skippedCount++
                } else if (chosen == q.correctOption) {
                    correctCount++
                } else {
                    incorrectCount++
                }
            }

            // Mock calculations: +2 for correct, -0.5 for wrong
            val score = (correctCount * 2.0f) - (incorrectCount * 0.5f)
            val maxScore = questions.size * 2.0f
            val elapsedSec = 600 - _testTimeRemaining.value

            val prompt = """
                Analyze SSC CHSL Mock test results.
                Correct: $correctCount, Incorrect: $incorrectCount, Skipped: $skippedCount.
                Score: $score out of $maxScore.
                Syllabus Areas: ${questions.joinToString { "${it.subject} (${it.topic})" }}
                
                Identify:
                1. Main mistakes (Accuracy vs Speed).
                2. Personalized review strategy for the next 24 hours.
                3. High-priority chapters to revise.
                
                Write in an encouraging mentor style. Keep it professional and visually crisp.
            """.trimIndent()

            val aiFeedback = GeminiHelper.generateText(prompt)

            val result = MockResult(
                title = "CHSL Mock Test #${(10..99).random()}",
                score = score,
                maxScore = maxScore,
                correctAnswers = correctCount,
                wrongAnswers = incorrectCount,
                skippedAnswers = skippedCount,
                durationSec = elapsedSec,
                aiFeedback = aiFeedback
            )

            repository.submitMockResult(result)
            _latestMockResult.value = result
        }
    }

    fun cancelActiveTest() {
        timerJob?.cancel()
        _isTestActive.value = false
        _activeTestQuestions.value = emptyList()
    }

    // --- Helpers ---

    private fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun getXpProgress(xp: Int): Pair<Int, Float> {
        val level = (xp / 100) + 1
        val xpInCurrentLevel = xp % 100
        val progress = xpInCurrentLevel / 100f
        return Pair(level, progress)
    }
}

class StudyViewModelFactory(
    private val application: Application,
    private val repository: StudyRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StudyViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
