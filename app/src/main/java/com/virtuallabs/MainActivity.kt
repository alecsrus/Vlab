package com.virtuallabs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.virtuallabs.ui.navigation.AppNav
import com.virtuallabs.ui.theme.VirtualSchoolLabsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VirtualSchoolLabsTheme {
                AppNav()
            }
        }
    }
}
