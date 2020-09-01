package pl.redny.kagouti.di.module

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import org.koin.dsl.module

val appModule = module {
    single {
        fun initKtorClient() = HttpClient(Android)
    }
}


