package pl.redny.kagouti.application.download

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

import pl.redny.kagouti.domain.DownloadResult
import java.io.OutputStream
import kotlin.math.roundToInt

class DownloadFileMemoryImpl(private val client: HttpClient) : DownloadFile {

    override fun downloadFile(file: OutputStream, url: String): Flow<DownloadResult> {
        return flow {
            try {
                val response: HttpResponse = client.get(url)

                val data = ByteArray(response.contentLength()!!.toInt())
                var offset = 0

                do {
                    val currentRead = response.content.readAvailable(data, offset, data.size)
                    offset += currentRead
                    val progress = (offset * 100f / data.size).roundToInt()
                    emit(DownloadResult.Progress(progress))
                } while (currentRead > 0)

                if (response.status.isSuccess()) {
                    withContext(Dispatchers.IO) {
                        file.write(data)
                    }
                    emit(DownloadResult.Success)
                } else {
                    emit(DownloadResult.Error("File not downloaded"))
                }
            } catch (e: TimeoutCancellationException) {
                emit(DownloadResult.Error("Connection timed out", e))
            } catch (t: Throwable) {
                emit(DownloadResult.Error("Failed to connect"))
            }
        }
    }
}