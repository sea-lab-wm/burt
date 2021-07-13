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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

public class APKInfoExtractor {

	public static HashSet<String> getActivitiesFromAPK(String apkPath){
		
		HashSet<String> activities = new HashSet<String>();
		Runtime rt = Runtime.getRuntime();
	   
	    Process proc = null;
	   
		
		try {
			String command[] ={"/bin/sh","-c","/usr/local/bin/aapt dump xmltree "+apkPath+" AndroidManifest.xml"};
			proc = rt.exec(command );
			BufferedReader is = new BufferedReader(new InputStreamReader(
					    proc.getInputStream()));
			String line = null;
		    while ((line = is.readLine()) != null) {
		    	line = line.trim();
		    	if(line.contains("E: activity")){
		    		
		    		String temp = null;
		    		do{
		    			 temp = is.readLine().trim();
		    		}while(!temp.contains("A: android:name"));
		    		temp = temp.substring( temp.indexOf("(Raw:")).replace("(Raw","").replace("\")", "").trim();
		    		activities.add(temp);
		    	}
		    }
		    is.close();
		    proc.waitFor();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

				return activities;
	}
	
	
	public static void main(String[] args){
//		Runtime rt = Runtime.getRuntime();
//		  try {
//			 String[] comands ={"/bin/sh","-c", "echo 'hi he ho' | awk '{print $2}'"};
//			Process proc = rt.exec(comands );
//			BufferedReader is = new BufferedReader(new InputStreamReader(
//				    proc.getInputStream()));
//		String line = null;
//	    while ((line = is.readLine()) != null) {
//	    	System.out.println(line);
//	    }
//			
//			proc.waitFor();
//			
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
		
		int a= APKInfoExtractor.getActivitiesFromAPK("/Users/charlyb07/Documents/workspace/semeru/Data-collector/study/apps/instrumented/Keepscore.apk").size();
		System.out.println("Size:"+a);
	}

}
