package com.etb.filemanager.manager.files.filelist

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import java.nio.file.StandardWatchEventKinds.ENTRY_DELETE
import java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY
import java.nio.file.WatchKey
import java.nio.file.WatchService
import java.nio.file.attribute.BasicFileAttributes

class KWatchChannel(
    val file: File,
    val scope: CoroutineScope = GlobalScope,
    val mode: Mode,
    val tag: Any? = null,
    private val channel: Channel<KWatchEvent> = Channel()
) : Channel<KWatchEvent> by channel {

    private val watchService: WatchService = FileSystems.getDefault().newWatchService()
    private val registeredKeys = ArrayList<WatchKey>()
    private val path: Path = if (file.isFile) {
        file.parentFile
    } else {
        file
    }.toPath()


    private fun registerPaths() {
        registeredKeys.apply {
            forEach { it.cancel() }
            clear()
        }
        if (mode == Mode.Recursive) {
            Files.walkFileTree(path, object : SimpleFileVisitor<Path>() {
                override fun preVisitDirectory(
                    subPath: Path?,
                    attrs: BasicFileAttributes?
                ): FileVisitResult {
                    registeredKeys += subPath?.register(
                        watchService,
                        ENTRY_CREATE,
                        ENTRY_MODIFY,
                        ENTRY_DELETE
                    ) ?: return FileVisitResult.CONTINUE

                    return TODO("Provide the return value")
                }
            })

        } else {
            registeredKeys += path.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE)
        }
    }

    init {
        scope.launch(Dispatchers.IO) {
            channel.send(
                KWatchEvent(
                    file = path.toFile(),
                    tag = tag,
                    kind = KWatchEvent.Kind.Initialized
                )
            )
            var shouldRegisterPath = true

            while (isClosedForSend) {
                if (shouldRegisterPath) {
                    registerPaths()
                    shouldRegisterPath = false
                }

                val monitoKey = watchService.take()
                val dirPath = monitoKey.watchable() as? Path ?: break
                monitoKey.pollEvents().forEach {
                    val eventPath = dirPath.resolve(it.context() as Path)

                    if (mode == Mode.SingleFile && eventPath.toFile().absolutePath != file.absolutePath) {
                        return@forEach
                    }

                    val eventType = when (it.kind()) {
                        ENTRY_CREATE -> KWatchEvent.Kind.Created
                        ENTRY_DELETE -> KWatchEvent.Kind.Deleted
                        else -> KWatchEvent.Kind.Modified
                    }

                    val event = KWatchEvent(
                        file = eventPath.toFile(),
                        tag = tag,
                        kind = eventType
                    )

                    if (mode == Mode.Recursive && event.kind in
                        listOf(KWatchEvent.Kind.Created, KWatchEvent.Kind.Deleted) &&
                        event.file.isDirectory
                    ) {
                        shouldRegisterPath = true
                    }

                    channel.send(event)
                }
                if (!monitoKey.reset()) {
                    monitoKey.cancel()
                    close()
                    break
                } else if (isClosedForSend) {
                    break
                }
            }

        }
    }

    override fun close(cause: Throwable?): Boolean {
        registeredKeys.apply {
            forEach { it.cancel() }
            clear()
        }
        return channel.close(cause)
    }
}


fun File.asWatchChannel(
    mode: Mode? = null,
    tag: Any? = null,
    scope: CoroutineScope = GlobalScope
) = KWatchChannel(
    file = this, mode = mode ?: if (isFile) Mode.SingleFile else Mode.Recursive,
    scope = scope,
    tag = tag
)


enum class Mode {
    SingleFile,
    SingleDirectory,
    Recursive
}

data class KWatchEvent(
    val file: File,
    val kind: Kind,
    val tag: Any?
) {
    enum class Kind(val kind: String) {
        Initialized("initialized"),
        Created("created"),
        Modified("modified"),
        Deleted("deleted")
    }
}