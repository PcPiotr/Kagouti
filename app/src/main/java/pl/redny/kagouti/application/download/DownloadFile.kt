package pl.redny.kagouti.application.download

import kotlinx.coroutines.flow.Flow
import pl.redny.kagouti.domain.DownloadResult
import java.io.OutputStream

interface DownloadFile {

    fun downloadFile(file: OutputStream, url: String): Flow<DownloadResult>

}