package com.etb.filemanager.manager.files.filelist

import android.os.Parcelable
import android.util.Log
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.Path

class TrailData private constructor(
    val trail: List<Path>, private val states: MutableList<Parcelable?>, val currentIndex: Int
) {

    fun navigateTo(lastState: Parcelable, path: Path): TrailData {
        val newTrail = createTrail(path)
        val newStates = mutableListOf<Parcelable?>()
        val newIndex = newTrail.size - 1
        var isPrefix = true

        for (index in newTrail.indices) {
            if (isPrefix && index < trail.size) {
                if (newTrail[index] == trail[index]){
                    newStates.add(if (index != currentIndex) states[index] else lastState)

                } else{
                    isPrefix = false
                    newStates.add(null)
                }
            } else{
                newStates.add(null)
            }
        }
        if (isPrefix){
            for (index in newTrail.size until trail.size){
                newTrail.add(trail[index])
                newStates.add(if (index != currentIndex) states[index] else lastState)
            }
        }
        return TrailData(newTrail, newStates, newIndex)
    }

    fun navigateUp(): TrailData? {
        if (currentIndex == 0) {
            return null
        }
        val newIndex = currentIndex - 1
        return TrailData(trail, states, newIndex)
    }

    val pendigSate: Parcelable?
        get() = states.set(currentIndex, null)
    val currentPath: Path
        get() = trail[currentIndex]

    companion object {
        fun of(path: Path): TrailData {
            val trail: List<Path> = createTrail(path)
            val states = MutableList<Parcelable?>(trail.size) { null }
            val index = trail.size - 1
            return TrailData(trail, states, index)
        }


        private fun createTrail(path: Path): MutableList<Path> {
            var mPath = path
            val trail = mutableListOf<Path>()
            val archiveFile = if (mPath.isArchivePath()) mPath.archiveFile() else null
            while (true) {
                trail.add(mPath)
                mPath = mPath.parent ?: break
            }
            trail.reverse()
            if (archiveFile != null) {
                val archiveFileParent = archiveFile.parent
                if (archiveFileParent != null) {
                    trail.addAll(
                        0, createTrail(Paths.get(archiveFile.path)))

                }
                Log.i("TRAIL", "PARENTE:: $archiveFileParent")
                Log.i("TRAIL", "PATH:: ${Paths.get(archiveFile.path)}")
            }
            return trail
        }
    }
}

fun Path.isArchivePath(): Boolean {
    val archiveExtensions = listOf(".zip", ".jar", ".tar", ".tar.gz")
    val fileExtension = this.fileName.toString().substringAfterLast('.', "")
    return archiveExtensions.contains(fileExtension)
}

fun Path.archiveFile(): File? {
    return if (this.isArchivePath()) {
        this.toFile()
    } else {
        null
    }
}