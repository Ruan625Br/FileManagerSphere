package com.etb.filemanager.manager.files.filelist
import kotlinx.coroutines.*
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchService
import java.util.concurrent.TimeUnit

@OptIn(DelicateCoroutinesApi::class)
class DirectoryObserver(private val path: Path, private val onChange: () -> Unit) {
    private var watchService: WatchService? = null
    private var closed = false

    init {
        startObserving()
    }

    @DelicateCoroutinesApi
    private fun startObserving() {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val directory = path
                    watchService = FileSystems.getDefault().newWatchService()
                    directory.register(
                        watchService,
                        StandardWatchEventKinds.ENTRY_DELETE,
                        StandardWatchEventKinds.ENTRY_MODIFY,
                        StandardWatchEventKinds.ENTRY_CREATE
                    )
                    while (!closed) {
                        val key = watchService?.poll(1, TimeUnit.SECONDS)
                        key?.let {
                            for (event in key.pollEvents()) {
                                when (event.kind()) {
                                    StandardWatchEventKinds.ENTRY_DELETE,
                                    StandardWatchEventKinds.ENTRY_MODIFY,
                                    StandardWatchEventKinds.ENTRY_CREATE -> {
                                        onChange.invoke()
                                    }
                                }
                            }
                            key.reset()
                        }
                    }
                } catch (e: Exception){
                    e.printStackTrace()
                } finally {
                    watchService?.close()
                }
            }

        }
    }

    fun close(){
        closed = true
    }
}