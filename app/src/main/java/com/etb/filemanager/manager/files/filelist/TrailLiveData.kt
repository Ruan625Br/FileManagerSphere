package com.etb.filemanager.manager.files.filelist

import android.os.Parcelable
import androidx.lifecycle.LiveData
import java.nio.file.Path

class TrailLiveData : LiveData<TrailData>() {
    fun navigateTo(lastState: Parcelable, path: Path) {
        val oldTrailData = value
        if (oldTrailData == null) {
            resetTo(path)
            return
        }
        value = oldTrailData.navigateTo(lastState, path)

    }

    fun resetTo(path: Path) {
        value = TrailData.of(path)
    }

    fun navigateUp(): Boolean {
        val oldTrailData = value ?: return false
        val trailData = oldTrailData.navigateUp() ?: return false
        value = trailData
        return true
    }

    fun reload() {
        value = value
    }
}