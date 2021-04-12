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
package edu.semeru.android.testing.helpers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataExtractor {

    public static void extractDumpFromTrace(String pTracePath,
	    String androidSDKPath, String tracesFolder, String fileName) {
	try {
	    Runtime rt = Runtime.getRuntime();
	    String androidToolsPath = androidSDKPath + File.separator + "tools";

	    Process proc = null;
	    try {
		// case dmtracedump is in tools/ folder
		proc = rt.exec(androidToolsPath + File.separator
			+ "dmtracedump -o " + pTracePath);
	    } catch (IOException e) {
		// case dmtracedump is in platform-tools/ folder
		proc = rt.exec(androidToolsPath + File.separator + ".."
			+ File.separator + "platform-tools" + File.separator
			+ "dmtracedump -o " + pTracePath);
	    }
	    BufferedReader is = new BufferedReader(new InputStreamReader(
		    proc.getInputStream()));
	    String output = null;
	    BufferedWriter writer = new BufferedWriter(new FileWriter(
		    tracesFolder + File.separator + fileName));

	    while ((output = is.readLine()) != null) {
		writer.write(output);
		writer.newLine();
		writer.flush();
	    }
	    is.close();
	    writer.close();
	} catch (IOException ex) {
	    Logger.getLogger(DataExtractor.class.getName()).log(Level.SEVERE,
		    null, ex);
	}

    }

    public static void filterMethodsFromDump(String packageName,
	    String dumpFilePath, String tracesFolder, String fileName) {
	try {
	    BufferedReader reader = new BufferedReader(new FileReader(
		    dumpFilePath));
	    BufferedWriter writer = new BufferedWriter(new FileWriter(
		    tracesFolder + File.separator + fileName));
	    String line = null;
	    String targetString = packageName.replace(".", "/");
	    while ((line = reader.readLine()) != null) {
		if (line.contains(targetString)) {
		    writer.write(line);
		    writer.newLine();
		    writer.flush();
		}
	    }

	    reader.close();
	    writer.close();
	} catch (Exception ex) {
	    Logger.getLogger(DataExtractor.class.getName()).log(Level.SEVERE,
		    null, ex);
	}

    }
}
