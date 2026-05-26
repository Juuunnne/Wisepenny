package com.wisepenny

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform