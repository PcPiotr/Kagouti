package pl.redny.kagouti.presentation.component.viewer

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast

class DefaultAndroidFileViewer : FileViewer {

    override fun viewFile(activity: Activity, uri: Uri) {
        activity?.let { context ->
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val chooser = Intent.createChooser(intent, "Open with")
            if (intent.resolveActivity(context.packageManager) != null) {
                activity.startActivity(chooser)
            } else {
                Toast.makeText(context, "No suitable application to open file", Toast.LENGTH_LONG).show()
            }
        }
    }
}