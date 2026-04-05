package com.example.project

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.project.ui.theme.ProjectTheme
import com.example.project.ui.navigation.AppNavigation


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val isDarkTheme = isSystemInDarkTheme()
            val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

            ProjectTheme(
                darkTheme = isDarkTheme,
                dynamicColor = dynamicColor
            ) {
                val navController = rememberNavController()
                AppNavigation(navController = navController)
            }
        }
    }
}
