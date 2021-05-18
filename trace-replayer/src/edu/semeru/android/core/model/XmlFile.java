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
 * XmlFile.java
 * 
 * Created on Jul 7, 2014, 1:15:58 AM
 * 
 */
package edu.semeru.android.core.model;

/**
 * {Insert class description here}
 *
 * @author Carlos Bernal
 * @since Jul 7, 2014
 */
public class XmlFile {

    private String fileName;
    private String className;
    private String variable;
    private String xmlId;
    private String num;

    public String getNum() {
	return num;
    }
    
    public void setNum(String num) {
   	this.num = num;
       }
    
    
    /**
     * @return the fileName
     */
    public String getFileName() {
	return fileName;
    }

    /**
     * @param fileName
     *            the fileName to set
     */
    public void setFileName(String fileName) {
	this.fileName = fileName;
    }

    /**
     * @return the variable
     */
    public String getVariable() {
	return variable;
    }

    /**
     * @param variable
     *            the variable to set
     */
    public void setVariable(String variable) {
	this.variable = variable;
    }

    /**
     * @return the xmlId
     */
    public String getXmlId() {
	return xmlId;
    }

    /**
     * @param xmlId
     *            the xmlId to set
     */
    public void setXmlId(String xmlId) {
	this.xmlId = xmlId;
    }

    /**
     * @return the className
     */
    public String getClassName() {
	return className;
    }

    /**
     * @param className
     *            the className to set
     */
    public void setClassName(String className) {
	this.className = className;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "XmlFile [fileName=" + fileName + ", className=" + className + ", variable=" + variable + ", xmlId="
		+ xmlId + ", ID Number=" + num + "]";
    }

}
