package com.wisepenny.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.wisepenny.db.WisepennyDatabase

actual class DatabaseDriverFactory {
    actual fun create(): SqlDriver = NativeSqliteDriver(
        schema = WisepennyDatabase.Schema,
        name = "wisepenny.db",
    )
}
