package pl.redny.kagouti.presentation.component.picker

import android.app.Activity
import android.content.Intent


class AndroidFilePicker(private val pickerText: String) : FilePicker {
    override fun openChooseFilePicker(activity: Activity, code: Int) {
        val intent = Intent()
            .setType("*/*")
            .setAction(Intent.ACTION_GET_CONTENT)

        activity.startActivityForResult(
            Intent.createChooser(intent, pickerText),
            code
        )

    }

    override fun openSaveFilePicker(activity: Activity, defaultSaveFile: String, code: Int) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
            .addCategory(Intent.CATEGORY_OPENABLE)
            .setType("*/*")
            .putExtra(Intent.EXTRA_TITLE, defaultSaveFile)

        activity.startActivityForResult(
            Intent.createChooser(intent, pickerText),
            code
        )
    }

}
