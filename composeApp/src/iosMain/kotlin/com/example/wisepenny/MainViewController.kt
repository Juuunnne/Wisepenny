package com.example.wisepenny

import androidx.compose.ui.window.ComposeUIViewController
import com.wisepenny.App
import com.wisepenny.data.local.DatabaseDriverFactory
import com.wisepenny.di.initKoin
import org.koin.dsl.module
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    initKoin(
        module { single { DatabaseDriverFactory() } },
    )
    return ComposeUIViewController { App() }
}