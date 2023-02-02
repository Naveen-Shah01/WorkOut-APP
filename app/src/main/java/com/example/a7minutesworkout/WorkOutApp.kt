package com.example.a7minutesworkout

import android.app.Application

/**create the application class and initialize the database */
class WorkOutApp : Application() {
    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val db by lazy { HistoryDatabase.getInstance(this) }
}
