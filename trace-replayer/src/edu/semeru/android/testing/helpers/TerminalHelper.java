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
 * TerminalHelper.java
 * 
 * Created on Jun 19, 2014, 7:15:30 PM
 * 
 */
package edu.semeru.android.testing.helpers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.lang3.SystemUtils;


/**
 * This class can simulate an execution of commands in a terminal
 * 
 * @author Carlos Bernal
 * @since Jun 19, 2014
 */
public class TerminalHelper {

    /**
     * This method executes a command in the terminal
     * 
     * @param command
     *            to be executed
     * @return output of command execution
     */
    public static String executeCommand(String command) {
        String output = "";
        Process proc;
        Runtime rt;
        try {
            BufferedReader stdOutput;

            rt = Runtime.getRuntime();
            
            if(SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_LINUX) {
            proc = rt.exec(new String[] { "/bin/sh", "-c", command });
            }else {           
            proc = rt.exec(new String[] { "cmd", "/c", command });
            }
            InputStreamReader procReader = new InputStreamReader(proc.getInputStream());
            InputStreamReader errorReader = null;
            stdOutput = new BufferedReader(procReader);
            output = getConsoleString(stdOutput, output);
            if (output.trim().isEmpty()) {
                errorReader = new InputStreamReader(proc.getErrorStream());
                stdOutput = new BufferedReader(errorReader);
                output = getConsoleString(stdOutput, output);
                errorReader.close();
            }
            proc.waitFor();

            stdOutput.close();
            if (procReader != null) {
                procReader.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(TerminalHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(TerminalHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        // System.out.println(command);
        return output.trim();
    }

    public static DefaultExecutor executeProcess(String command) {
       CommandLine commandLine = new CommandLine("/bin/sh");
       commandLine.addArguments(new String[] { "-c", command},false);
       System.out.println();
       DefaultExecutor executor = new DefaultExecutor();
       executor.setExitValue(1);
       ExecuteWatchdog watchdog = new ExecuteWatchdog(1000000);
       
       try {
        int exitValue = executor.execute(commandLine);
    } catch (ExecuteException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
       try {
        Thread.sleep(10000);
    } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
      watchdog.destroyProcess();
       return executor;
    }
    
    public static void executeCommandNoText(String command) {
        String output = "";
        Process proc;
        Runtime rt;
        try {
            // BufferedReader stdOutput;

            rt = Runtime.getRuntime();
            proc = rt.exec(command);

            proc.waitFor();

        } catch (IOException ex) {
            Logger.getLogger(TerminalHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(TerminalHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        // System.out.println(command);

    }

    public static String executeOldCommand(String command) {
        String output = "";
        try {
            BufferedReader stdOutput;

            Runtime rt = Runtime.getRuntime();
            Process proc;
            proc = rt.exec(command);
            stdOutput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            output = getConsoleString(stdOutput, output);
            if (output.trim().isEmpty()) {
                stdOutput = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
                output = getConsoleString(stdOutput, output);
            }
            proc.waitFor();

            stdOutput.close();
        } catch (IOException ex) {
            Logger.getLogger(TerminalHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(TerminalHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        // System.out.println(command);
        return output.trim();
    }

    /**
     * Returns list of files with an specific extension
     * 
     * @param path
     *            to search in
     * @param extension
     *            of files
     * @return list of files that corresponds to criteria
     */
    public static Scanner getFilesByExtension(String path, String extension) {
        return getFilesByName(path, "*." + extension);
    }

    /**
     * Returns a file with an specific name
     * 
     * @param path
     *            to search in
     * @param name
     *            of filr
     * @return a file with an specific name
     */
    public static Scanner getFilesByName(String path, String name) {
        String command = "find " + path + " -name " + " " + name;
        String output = TerminalHelper.executeCommand(command);
        // System.out.println(output);
        return new Scanner(output);
    }

    public static String getFilesByNameString(String path, String name) {
        String command = "find " + path + " -name " + " " + name;
        String output = TerminalHelper.executeCommand(command);
        // System.out.println(output);
        return output;
    }

    private static String getConsoleString(BufferedReader stdOutput, String output) throws IOException {
        String line;
        while ((line = stdOutput.readLine()) != null) {
            output += line + "\n";
        }
        return output;
    }

    public static void runAsRoot(String[] cmds, String firstCmd, boolean doExit) {
        Process p = null;
        BufferedReader stdOutput = null;
        try {
            p = Runtime.getRuntime().exec(firstCmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
        DataOutputStream os = new DataOutputStream(p.getOutputStream());
        for (String tmpCmd : cmds) {
            try {
                os.writeBytes(tmpCmd + "\n");
                os.flush();
                Thread.sleep(500);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        // if (doExit) {
        try {
            os.writeBytes("exit\n");
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
            // }
            // }
//             try {
//             String line;
//             String output = "";
//             stdOutput = new BufferedReader(new
//             InputStreamReader(p.getInputStream()));
//             while ((line = stdOutput.readLine()) != null) {
//             output += line + "\n";
//             }
//             System.out.println(output);
//             } catch (IOException ee) {
//             ee.printStackTrace();
//             }
        } finally {
            try {
                // stdOutput.close();
                os.close();
                p.destroy();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static String executeCommands(String[] commands) {
        // Init shell
        ProcessBuilder builder = new ProcessBuilder("/bin/bash");
        Process p = null;
        try {
            p = builder.start();
        } catch (IOException e) {
            System.out.println(e);
        }
        // Get stdin of shell
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));

        // Execute all the commands
        for (String command : commands) {
            try {
                // single execution
                writer.write(command);
                writer.newLine();
                writer.flush();
            } catch (IOException e) {
                System.out.println(e);
            }
        }

        // finally close the shell by execution exit command
        try {
            writer.write("exit");
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // write stdout of shell (=output of all commands)
        Scanner s = new Scanner(p.getInputStream());
        StringBuilder stringBuilder = new StringBuilder();
        while (s.hasNext()) {
            stringBuilder.append(s.nextLine());
        }
        s.close();
        return stringBuilder.toString();
    }

}
