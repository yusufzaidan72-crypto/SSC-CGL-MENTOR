package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.data.AppDatabase
import com.example.data.StudyRepository
import com.example.ui.StudyViewModel
import com.example.ui.StudyViewModelFactory
import com.example.ui.screens.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize local database and Repository
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = StudyRepository(database)
        val factory = StudyViewModelFactory(application, repository)

        setContent {
            var isDarkTheme by remember { mutableStateOf(true) } // Premium dark mode by default

            MyApplicationTheme(darkTheme = isDarkTheme) {
                val viewModel: StudyViewModel = ViewModelProvider(this, factory)[StudyViewModel::class.java]
                val profileState by viewModel.userProfile.collectAsState()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (profileState == null || !profileState!!.onboarded) {
                        // User needs onboarding setup first
                        OnboardingScreen(viewModel = viewModel)
                    } else {
                        // Core application navigation hub
                        MainNavigationHub(
                            viewModel = viewModel,
                            isDarkTheme = isDarkTheme,
                            onThemeToggle = { isDarkTheme = !isDarkTheme }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainNavigationHub(
    viewModel: StudyViewModel,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    var activeTab by remember { mutableStateOf(0) } // 0: Home/Dashboard, 1: AI Mentor/Doubt, 2: Syllabus, 3: Practice/Mocks, 4: Analytics/Profile

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("main_scaffold"),
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = AmberSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "CHSL AI Mentor",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onThemeToggle) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Theme Toggle",
                            tint = AmberSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CardSlate,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = CardSlate,
                tonalElevation = 8.dp,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Cockpit", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MidnightBg,
                        selectedTextColor = AmberSecondary,
                        indicatorColor = AmberSecondary,
                        unselectedIconColor = SoftMutedText,
                        unselectedTextColor = SoftMutedText
                    ),
                    modifier = Modifier.testTag("nav_tab_cockpit")
                )

                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    icon = { Icon(Icons.Default.HelpCenter, contentDescription = "Doubt Solver") },
                    label = { Text("Doubt Solver", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MidnightBg,
                        selectedTextColor = AmberSecondary,
                        indicatorColor = AmberSecondary,
                        unselectedIconColor = SoftMutedText,
                        unselectedTextColor = SoftMutedText
                    ),
                    modifier = Modifier.testTag("nav_tab_doubts")
                )

                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    icon = { Icon(Icons.Default.MenuBook, contentDescription = "Syllabus Notes") },
                    label = { Text("Library", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MidnightBg,
                        selectedTextColor = AmberSecondary,
                        indicatorColor = AmberSecondary,
                        unselectedIconColor = SoftMutedText,
                        unselectedTextColor = SoftMutedText
                    ),
                    modifier = Modifier.testTag("nav_tab_syllabus")
                )

                NavigationBarItem(
                    selected = activeTab == 3,
                    onClick = { activeTab = 3 },
                    icon = { Icon(Icons.Default.Assignment, contentDescription = "Practice Mock") },
                    label = { Text("Practice", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MidnightBg,
                        selectedTextColor = AmberSecondary,
                        indicatorColor = AmberSecondary,
                        unselectedIconColor = SoftMutedText,
                        unselectedTextColor = SoftMutedText
                    ),
                    modifier = Modifier.testTag("nav_tab_practice")
                )

                NavigationBarItem(
                    selected = activeTab == 4,
                    onClick = { activeTab = 4 },
                    icon = { Icon(Icons.Default.Leaderboard, contentDescription = "Performance Profile") },
                    label = { Text("Profile", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MidnightBg,
                        selectedTextColor = AmberSecondary,
                        indicatorColor = AmberSecondary,
                        unselectedIconColor = SoftMutedText,
                        unselectedTextColor = SoftMutedText
                    ),
                    modifier = Modifier.testTag("nav_tab_profile")
                )
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeTab) {
                0 -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToTab = { tabIndex -> activeTab = tabIndex }
                )
                1 -> MentorDoubtScreen(viewModel = viewModel)
                2 -> SyllabusLibraryScreen(viewModel = viewModel)
                3 -> PracticeMockScreen(viewModel = viewModel)
                4 -> ProfileAnalyticsScreen(viewModel = viewModel)
            }
        }
    }
}
