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
package edu.wm.cs.semeru.core.jcie;



import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import edu.wm.cs.semeru.core.jcie.model.ClassInformationVO;

public class AppSignatureGenerator {

	
	public static List<String> getFilesPathRecursively(String rootFolderPath) throws Exception{
		List<String> filesPath = new ArrayList<String>();  
		BufferedReader stdOutput = null;
		String output = null;
		Runtime rt = Runtime.getRuntime();
	    Process proc = null;
	    proc = rt.exec("find "+rootFolderPath+" -name *.java -print");
        
        stdOutput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        while ((output = stdOutput.readLine()) != null) {

        	filesPath.add(output);
        }
        proc.waitFor();      
        
        stdOutput.close();
	    return filesPath;
	}
	
	public static List<ClassInformationVO> getClassesInformation(String sourceFolder, String binariesFolder) throws Exception{
		List<ClassInformationVO> classesInfo = new ArrayList<ClassInformationVO>();
		List<String> filesPath = getFilesPathRecursively(sourceFolder);
		for (String filePath : filesPath) {
			classesInfo.add(ClassSignatureGenerator.getClassInfo(filePath, binariesFolder, sourceFolder));
		}
		return classesInfo;
	}
	
	public static void main(String args[]) throws Exception{
		AppSignatureGenerator.getClassesInformation(args[0], args[1]);
	}
	
	
}
