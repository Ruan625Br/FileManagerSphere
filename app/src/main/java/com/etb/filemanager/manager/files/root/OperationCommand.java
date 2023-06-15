package com.etb.filemanager.manager.files.root;

import java.io.File;
import java.io.IOException;

public class OperationCommand {
    static public boolean deleteDir(File file) throws IOException, InterruptedException {
        if (file.exists()){
            String deleteCommand = "rm -rf " + file.getAbsolutePath();
            Runtime runtime = Runtime.getRuntime();

            Process process = runtime.exec(deleteCommand);
            process.waitFor();

            return true;
        }
        return false;
    }
}
