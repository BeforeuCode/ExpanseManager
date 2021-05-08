package pl.edu.pja.proj1

import android.app.Application
import pl.edu.pja.proj1.database.AppDatabase

class App:Application() {
    val database by lazy { AppDatabase.open(this) }

    override fun onCreate() {
        super.onCreate()
        database
    }
}