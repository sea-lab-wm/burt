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

import java.util.Random;




/**
 * This class is for determining the current type of Android keyboard
 * present on the screen and generate the corresponding "expected"
 * and "unexpected" input values for DFS+.
 *
 * @author Kevin Moran
 * @since April 5th, 2015
 */

public class InputHelper {

	/**
	 * Main method for testing purposes, no input required.
	 */
	
	public static void main(String[] args){
		
		//String test = randomString(1);
		
		String test = generateInput(2, "unexpected");
		
		System.out.println("Random String: " + test);
		
		String sdk = "/Applications/AndroidSDK/sdk/";
		
		int input_type = checkInputType(sdk,"");
		
		System.out.println("Input Type = " + input_type);
	}
	
	
	public static int checkInputTypeDevice(String androidSDKPath){
	    return checkInputTypeDevice(androidSDKPath, null);
	}
	
	/**
	 * This method checks for the input type of the current Android keyboard
	 * currently displayed on the screen and returns the corresponding value
	 * of the input type according to the table below:
	 * 
	 * inputType = 0 = no Input available
	 * inputType = 1 = String
	 * inputType = 2 = Number without decimal or negative
	 * inputType = 3 = Number with signs
	 * inputType = 4 = Number with decimal
	 * inputType = 5 = Phone Number
	 * inputType = 6 = date and time
	 * 
	 * 
	 * Method Inputs: None
	 * 
	 * Method Outputs: The input type of the keyboard as listed above
	 * 
	 */
	
	public static int checkInputTypeDevice(String androidSDKPath, String device){
	    if (device == null || (device != null && device.isEmpty())) {
                device = "";
            } else {
                device = "-s " + device + " ";
            }
		
		int inputType = 0; // declare variable to hold our custom input type.
		
		String androidToolsPath = androidSDKPath + "/platform-tools/";
		
		String command = (androidToolsPath + "adb " + device + "shell dumpsys input_method"); // | grep 'mInputAttributes'
		String output = TerminalHelper.executeCommand(command);
		
//		System.out.println(output);
		output = output.substring(output.indexOf("inputType=")+10,output.indexOf("inputType=")+20);
		//java.lang.System.out.println(output);
		//output = output.substring(9,10);
		//System.out.println(output);
		
		if(output.equals("0x00000000") || output == null || output.equals(null)){
			inputType = 0; 		//No input type
		}else if (output.substring(9,10).equals("1")){
			inputType = 1;		// Input type equals string
		}else if(output.substring(6,10).equals("1002") || output.substring(6,10).equals("2002")){
			inputType = 3;		//Input type equals number with negatives and the following symbols: -+.,/*()=#
		}else if (output.substring(9,10).equals("2")){
			inputType = 2;		// Input type equals number with no negatives or decimal values
		}else if(output.substring(9,10).equals("3")){
			inputType = 4;		// Input Type equals phone number, or number with the following possible symbols: -+,.()N*#
		}else if(output.substring(9,10).equals("4")){
			inputType = 5;		// Input Type equals date/time, or number with the following symbols: -/:
		}
			
	
			return inputType;
			
	}
	
	// This is for emulator
	
public static int checkInputType(String androidSDKPath, String deviceCommand){
        if (deviceCommand == null || (deviceCommand != null && deviceCommand.isEmpty())) {
            //default port
            deviceCommand = "5037";
        }
	int inputType = 0; // declare variable to hold our custom input type.
	
	String androidToolsPath = androidSDKPath + "/platform-tools/";
	
	String command = (androidToolsPath + "adb -P " + deviceCommand + " shell dumpsys input_method"); // | grep 'mInputAttributes'
	String output = TerminalHelper.executeCommand(command);
	
	//System.out.println(output);
	output = output.substring(output.indexOf("inputType=")+10,output.indexOf("imeOptions=")-1);
	//java.lang.System.out.println(output);
	//output = output.substring(9,10);
	//System.out.println(output);	//For debugging
	if(output.equals("0x0") || output == null || output.equals(null)){
		inputType = 0; 		//No input type
	}else if (output.substring(output.length()-1,output.length()).equals("1")){
		inputType = 1;		// Input type equals string
	}else if(output.length() == 6){
		if(output.substring(output.length()-4,output.length()).equals("1002") || output.substring(output.length()-4,output.length()).equals("2002")){
			inputType = 3;		//Input type equals number with negatives and the following symbols: -+.,/*()=#
		}
	}else if (output.substring(output.length()-1,output.length()).equals("2")){
		inputType = 2;		// Input type equals number with no negatives or decimal values
	}else if(output.substring(output.length()-1,output.length()).equals("3")){
		inputType = 4;		// Input Type equals phone number, or number with the following possible symbols: -+,.()N*#
	}else if(output.substring(output.length()-1,output.length()).equals("4")){
		inputType = 5;		// Input Type equals date/time, or number with the following symbols: -/:
	}
		
		//System.out.println(inputType);		//For Debugging
		return inputType;
		
}

	/**
	 * This method generates relevant input corresponding to the 
	 * type of the EditTest box detected above.
	 * 
	 * 
	 * 
	 * Method Inputs: the inputType and the executionType (Expected or Unexpected)
	 * 
	 * Method Outputs: The input text for the corresponding inputType detected in
	 * the previous function.
	 * 
	 */
	
	public static String generateInput(int inputType, String executionType){
		
		String genInput = null;
		
		if (executionType.equals("No_Text")){
			return "";
		}
		
		if (inputType == 0){
			//do nothing
		}else if (inputType == 1){
			if (executionType.toLowerCase().equals("expected")){
				genInput = "TestTest%sTest";
			}else if (executionType.toLowerCase().equals("unexpected")){
				genInput = "\\"+'\''+randomString(1)+'\\'+'@'+'\\'+'#'+'\\'+'$'+'\\'+'%'+'\\'+'^'+'\\'+'&'+'\\'+'*'+'\\'+'('+'\\'+')'+'\\'+'\'';
			}// end check for expected/unexpected input
		} else if (inputType == 2){
			if (executionType.toLowerCase().equals("expected")){
				genInput = "1234567";
			}else if (executionType.toLowerCase().equals("unexpected")){
				genInput = "\\"+'\''+randomString(2)+'\\'+'@'+'\\'+'#'+'\\'+'$'+'\\'+'%'+'\\'+'^'+'\\'+'&'+'\\'+'*'+'\\'+'('+'\\'+')'+'\\'+'\'';
			}// end check for expected/unexpected input
		}else if (inputType == 3){
			if (executionType.toLowerCase().equals("expected")){
				genInput = "1234567";
			}else if (executionType.toLowerCase().equals("unexpected")){
				genInput = "1"+'\\'+'\''+'\\'+'@'+'\\'+'#'+'\\'+'$'+'\\'+'%'+'\\'+'^'+'\\'+'&'+'\\'+'*'+'\\'+'('+'\\'+')'+'\\'+'\'';
			}// end check for expected/unexpected input
		}else if (inputType == 4){
			if (executionType.toLowerCase().equals("expected")){
				genInput = "8001234567";
			}else if (executionType.toLowerCase().equals("unexpected")){
				genInput = "1"+'2'+'3'+'\\'+'\''+'\\'+'@'+'\\'+'#'+'\\'+'$'+'\\'+'%'+'\\'+'^'+'\\'+'&'+'\\'+'*'+'\\'+'('+'\\'+')'+'\\'+'\'';
			}// end check for expected/unexpected input
		}else if (inputType == 4){
			if (executionType.toLowerCase().equals("expected")){
				genInput = "8001234567";
			}else if (executionType.toLowerCase().equals("unexpected")){
				genInput = "-/:1%s23";
			}// end check for expected/unexpected input
		}
		
		return genInput;
		
	}
	
	/**
	 * This method generates a random sequence of text, including at least one space, or a random
	 * series of numbers with no spaces.
	 * 
	 * Method Inputs: an integer corresponding to the type of random number to be generated
	 * with "1" corresponding to text, and "2" corresponding to numerals
	 * 
	 * Method Outputs: a random series of text with a guaranteed space, or a random series
	 * of numbers without text.
	 * 
	 */
	
	public static String randomString(int type){
		char[] symbols;
		StringBuilder ran = new StringBuilder();
		
		if (type == 1){
			for (char ch = 'a'; ch <= 'z'; ++ch){
			 ran.append(ch);
			}
		}else if(type == 2){
			 for (char ch = '0'; ch <= '9'; ++ch) {
                ran.append(ch);
            }
		}
		    symbols = ran.toString().toCharArray();
		    Random randomgen = new Random();
		    char[] buf;
		    buf = new char[10];
		    
		    for (int i = 0; i < buf.length; i++){
		    	buf[i] = symbols[randomgen.nextInt(symbols.length)];
		    }
		    
		    if (type == 1){	
		    
		    String randomOut = new String(buf);
		    randomOut = randomOut + "%sz";
		    return randomOut;
		    }else{
		    	return new String(buf);
		    }
	}
	
}
