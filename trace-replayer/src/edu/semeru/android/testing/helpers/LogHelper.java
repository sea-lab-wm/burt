

/*******************************************************************************
 * Copyright (c) 2016, SEMERU
 * All rights reserved.
 *  
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 *******************************************************************************/
/**
 * LogHelper.java
 * 
 * Created on Oct 18, 2013, 7:25:04 AM
 */
package edu.semeru.android.testing.helpers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * {Insert class description here}
 * 
 * @author Carlos Bernal
 * @since Oct 18, 2013
 */
public class LogHelper {

    private static LogHelper INSTANCE;
    private static Writer output;
    private boolean printLog = true;

    private LogHelper() {
    }

    private static void createFile(String fileName) {
    // System.out.println("new object " + fileName);
    try {
        System.out.println(fileName);
        System.out.println(fileName.substring(0, fileName.lastIndexOf(File.separator)));
        File f = new File(fileName.substring(0, fileName.lastIndexOf(File.separator)));
        if (!f.exists()) {
        f.mkdirs();
        f.createNewFile();
        }
        output = new BufferedWriter(new FileWriter(fileName, true));
    } catch (IOException e) {
        e.printStackTrace();
    }
    }

    public static LogHelper getInstance() {
    return getInstance("Log.log");
    }

    public static LogHelper getInstance(String fileName) {
    createFile(fileName);
    if (INSTANCE != null) {
        return INSTANCE;
    } else {
        INSTANCE = new LogHelper();
        return INSTANCE;
    }
    }

    public void addLine(String line) {
    try {
        output.append(line);
        output.append("\n");
        if (printLog) {
        System.out.println(line);
        }
        output.flush();
    } catch (IOException e) {
        e.printStackTrace();
    }
    }

    public void printLog(boolean print) {
    printLog = print;
    if (print) {
        System.out.println("Log ON console");
    } else {
        System.out.println("Log OFF console");
    }
    }
}

