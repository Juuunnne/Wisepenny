package com.example.wisepenny

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.wisepenny.App
import com.wisepenny.data.local.DatabaseDriverFactory
import com.wisepenny.di.initKoin
import org.koin.dsl.module

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        initKoin(
            module { single { DatabaseDriverFactory(applicationContext) } },
        )

        setContent {
            App()
        }
    }
}