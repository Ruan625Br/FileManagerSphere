/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - OperationCommand.java
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

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
