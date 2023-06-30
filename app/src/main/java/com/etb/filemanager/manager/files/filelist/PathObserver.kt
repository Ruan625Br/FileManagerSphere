package com.etb.filemanager.manager.files.filelist



import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import androidx.annotation.MainThread
import com.etb.filemanager.files.provider.archive.commo.PathObservable
import com.etb.filemanager.files.provider.archive.commo.observe
import java.nio.file.Path
import java.io.Closeable
import java.io.IOException

class PathObserver(path: Path, @MainThread onChange: () -> Unit) : Closeable {
    private var pathObservable: PathObservable? = null

    private var closed = false
    private val lock = Any()

    init {
        AsyncTask.THREAD_POOL_EXECUTOR.execute {
            synchronized(lock) {
                if (closed) {
                    return@execute
                }
                pathObservable = try {
                    path.observe(THROTTLE_INTERVAL_MILLIS)
                } catch (e: UnsupportedOperationException) {
                    // Ignored.
                    return@execute
                } catch (e: IOException) {
                    // Ignored.
                    e.printStackTrace()
                    return@execute
                }.apply {
                    val mainHandler = Handler(Looper.getMainLooper())
                    addObserver { mainHandler.post(onChange) }
                }
            }
        }
    }

    override fun close() {
        AsyncTask.THREAD_POOL_EXECUTOR.execute {
            synchronized(lock) {
                if (closed) {
                    return@execute
                }
                closed = true
                pathObservable?.closeSafe()
            }
        }
    }

    companion object {
        private const val THROTTLE_INTERVAL_MILLIS = 1000L
    }
}

fun AutoCloseable.closeSafe() {
    try {
        close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}