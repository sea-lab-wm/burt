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
package edu.wm.cs.semeru.core.jcie.helper;

public class BytecodeHelper {
	public static String parseMethodLine(String method) {
        String line = method.replace(" ", "");
        line = line.replace("/", ".");

        if (line.contains("<init>")) {
        	line = line.replace("<init>", "#init#");

        }

        line = line.replaceFirst("<.*?>", ""); // Removing generics

        
        if (line.contains("#init#")) {
        	String name = line.substring(0, line.indexOf("#init#") - 1);
            name = name.substring(name.lastIndexOf(".") + 1, name.length());

            line = line.replace("#init#", name);
        }
        

        line = line.replace("$", ".");
        
        int startParams = line.indexOf("(");
        int endParams = line.indexOf(")");

        String methodName = line.substring(0, startParams);
        String params = line.substring(startParams + 1, endParams);
        StringBuffer parsedParams = new StringBuffer();
        int arrayDimensions = 0;
        boolean appendComma = true;
        for (int i = 0; i < params.length(); i++) {

            char currentChar = params.charAt(i);
            appendComma = true;
            if (Character.isUpperCase(currentChar) || currentChar == '[') {
                boolean isArray = false;
                switch (currentChar) {
                    case 'Z':
                        parsedParams.append("boolean");

                        break;
                    case 'C':
                        parsedParams.append("char");

                        break;
                    case 'B':
                        parsedParams.append("byte");

                        break;
                    case 'S':
                        parsedParams.append("short");

                        break;
                    case 'I':
                        parsedParams.append("int");

                        break;
                    case 'F':
                        parsedParams.append("float");

                        break;
                    case 'J':
                        parsedParams.append("long");

                        break;
                    case 'D':
                        parsedParams.append("double");

                        break;
                    case 'L':

                        String temp = params.substring(i);
                        int endMark = temp.indexOf(";");
                        parsedParams.append(temp.substring(1, endMark));
                        i += endMark;

                        break;

                    case '[':
                        arrayDimensions++;
                        isArray = true;
                        appendComma = false;
                        break;



                }
                if (!isArray && arrayDimensions > 0) {
                    for (int j = 0; j < arrayDimensions; j++) {
                        parsedParams.append("[]");
                    }
                    arrayDimensions = 0;

                }
                if (appendComma) {
                    parsedParams.append("#");
                }
            }
        }


        params = parsedParams.toString();
        if (params.endsWith("#")) {
            params = params.substring(0, params.length() - 1);
        }
        methodName = methodName.replace(";", "");
        return methodName + "(" + params + ")";

    }
}
