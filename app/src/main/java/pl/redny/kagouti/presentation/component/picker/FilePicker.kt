package pl.redny.kagouti.presentation.component.picker

import android.app.Activity

interface FilePicker {
    fun openChooseFilePicker(activity: Activity, code: Int)

    fun openSaveFilePicker(activity: Activity, defaultSaveFile: String, code: Int)

}