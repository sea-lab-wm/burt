
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
 * Created by Kevin Moran on Dec 12, 2016
 */

package edu.semeru.android.testing.helpers;

import java.io.*;
import java.util.*;

/**
 * @author KevinMoran
 *
 */
public class CmdProcessBuilder {

    public static String executeCommand(String[] commands) throws InterruptedException,IOException{

        StringBuilder processOutputBuilder = new StringBuilder();
        String output = null;

        //Get list of all commands to be run
        List<String> command = new ArrayList<String>();
        for(int i=0; i < commands.length; i++){
            command.add(commands[i]);
        }

        ProcessBuilder builder = new ProcessBuilder(command);
        Map<String, String> environ = builder.environment();

        final Process process = builder.start();
        InputStream is = process.getInputStream();
        InputStream es = process.getErrorStream();
        InputStreamReader esr = new InputStreamReader(es);
        BufferedReader ebr = new BufferedReader(esr);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;
        while ((line = br.readLine()) != null) {
            processOutputBuilder.append(line);
            processOutputBuilder.append('\n');
        }
        
        while ((line = ebr.readLine()) != null) {
            processOutputBuilder.append(line);
            processOutputBuilder.append('\n');
        }

        output = processOutputBuilder.toString();
        
        return output;

    }
    
    public static Process executeCommandReDirect(String[] commands, String outputFile) throws InterruptedException,IOException{

        StringBuilder processOutputBuilder = new StringBuilder();
        String output = null;

        //Get list of all commands to be run
        List<String> command = new ArrayList<String>();
        for(int i=0; i < commands.length; i++){
            command.add(commands[i]);
        }

        ProcessBuilder builder = new ProcessBuilder(command);
        Map<String, String> environ = builder.environment();
        File processOutput = new File(outputFile);
        builder.redirectOutput(processOutput);
        
        final Process process = builder.start();
        
        
        return process;

    }
    
    public static String executeCommand(String[] commands, Map<String,String> enviornment) throws InterruptedException,IOException{

        StringBuilder processOutputBuilder = new StringBuilder();
        String output = null;

        //Get list of all commands to be run
        List<String> command = new ArrayList<String>();
        for(int i=0; i < commands.length; i++){
            command.add(commands[i]);
        }

        ProcessBuilder builder = new ProcessBuilder(command);
        builder.environment().putAll(enviornment);

        final Process process = builder.start();
        InputStream is = process.getInputStream();
        InputStream es = process.getErrorStream();
        InputStreamReader esr = new InputStreamReader(es);
        BufferedReader ebr = new BufferedReader(esr);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;
        while ((line = br.readLine()) != null) {
            processOutputBuilder.append(line);
            processOutputBuilder.append('\n');
        }
        
        while ((line = ebr.readLine()) != null) {
            processOutputBuilder.append(line);
            processOutputBuilder.append('\n');
        }

        output = processOutputBuilder.toString();
        
        return output;

    }
    
    public static String executeCommand(String[] commands, String workingDirectory, Map<String,String> enviornment) throws InterruptedException,IOException{

        StringBuilder processOutputBuilder = new StringBuilder();
        String output = null;

        //Get list of all commands to be run
        List<String> command = new ArrayList<String>();
        for(int i=0; i < commands.length; i++){
            command.add(commands[i]);
        }

        ProcessBuilder builder = new ProcessBuilder(command);
        if(enviornment != null && !enviornment.isEmpty()) {
        builder.environment().putAll(enviornment);
        }
        if(workingDirectory != null && !workingDirectory.isEmpty())
        builder.directory(new File(workingDirectory));

        final Process process = builder.start();
        InputStream is = process.getInputStream();
        InputStream es = process.getErrorStream();
        InputStreamReader esr = new InputStreamReader(es);
        BufferedReader ebr = new BufferedReader(esr);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;
        while ((line = br.readLine()) != null) {
            processOutputBuilder.append(line);
            processOutputBuilder.append('\n');
        }
        
        while ((line = ebr.readLine()) != null) {
            processOutputBuilder.append(line);
            processOutputBuilder.append('\n');
        }

        output = processOutputBuilder.toString();
        
        return output;

    }
    
}

