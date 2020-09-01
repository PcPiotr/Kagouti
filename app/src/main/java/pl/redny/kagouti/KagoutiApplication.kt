package pl.redny.kagouti

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import pl.redny.kagouti.di.module.appModule

class KagoutiApplication : Application() {
    override fun onCreate(){
        super.onCreate()
        // start Koin!
//        startKoin {
//            modules(appModule)
//            androidContext(this@KagoutiApplication)
//        }
    }
}