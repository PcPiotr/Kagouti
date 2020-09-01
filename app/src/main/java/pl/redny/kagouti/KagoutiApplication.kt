package pl.redny.kagouti

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import pl.redny.kagouti.module.ktorModules
import pl.redny.kagouti.module.mainActivityModules

class KagoutiApplication : Application() {
    override fun onCreate(){
        super.onCreate()
        // start Koin!
        startKoin {
            modules(ktorModules, mainActivityModules)
            androidContext(this@KagoutiApplication)
        }
    }
}