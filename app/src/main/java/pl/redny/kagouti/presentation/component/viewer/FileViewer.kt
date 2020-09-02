package pl.redny.kagouti.presentation.component.viewer

import android.app.Activity
import android.net.Uri

interface FileViewer {
    fun viewFile(activity: Activity, uri: Uri)

}
