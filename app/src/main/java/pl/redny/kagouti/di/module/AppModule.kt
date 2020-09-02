package pl.redny.kagouti.di.module

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import org.koin.dsl.module
import pl.redny.kagouti.application.download.DownloadFile
import pl.redny.kagouti.application.download.DownloadFileMemoryImpl
import pl.redny.kagouti.presentation.component.picker.AndroidFilePicker
import pl.redny.kagouti.presentation.component.picker.FilePicker
import pl.redny.kagouti.presentation.component.viewer.DefaultAndroidFileViewer
import pl.redny.kagouti.presentation.component.viewer.FileViewer

class AppModule

val appModule = module {
    single { HttpClient(Android) }
    single<DownloadFile> { DownloadFileMemoryImpl(get()) }
    single<FilePicker> { AndroidFilePicker("temp") }
    single<FileViewer> { DefaultAndroidFileViewer() }

}
