package com.wisepenny.data.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.wisepenny.db.WisepennyDatabase

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun create(): SqlDriver = AndroidSqliteDriver(
        schema = WisepennyDatabase.Schema,
        context = context,
        name = "wisepenny.db",
    )
}
