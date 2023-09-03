/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - DeleteOperation.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.manager.files.filelist


import android.util.Log
import com.etb.filemanager.manager.files.root.OperationCommand
import java.io.File
import java.io.IOException
import java.nio.file.Files

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



        try {
            return file.delete()
        }catch (e: IOException){
            Log.e("Operation delete", "Errro: $e")
        }
        try {
            return file.deleteRecursively()
        }catch (e: IOException){
            Log.e("Operation delete", "Errro: $e")
        }


        //tenta apagar o arquivo com comando
        return OperationCommand.deleteDir(file)


    }

}