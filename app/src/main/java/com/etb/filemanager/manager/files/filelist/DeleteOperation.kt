package com.etb.filemanager.manager.files.filelist

import android.content.ContentValues
import android.content.Context
import android.os.Build
import com.etb.filemanager.manager.files.root.OperationCommand
import java.io.File

object DeleteOperation {


     fun deleteFilesOrDir(path: String){
        val file = File(path)
         if (file.exists()){
             deleteDir(file)
         }

    }

    private fun deleteDir(file: File): Boolean{
        val files = file.listFiles()
        if (files != null){
            for (child in files){
                deleteDir(child)
            }
        }


        if (file.delete()){
            return true
        }
        if (file.deleteRecursively()){
            return true
        }
        return OperationCommand.deleteDir(file)


    }

}