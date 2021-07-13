
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
 * CrashScopeStrategies.java
 * 
 * Created on Feb 20, 2016, 9:25:28 PM
 * 
 */
package edu.semeru.android.testing;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * {Insert class description here}
 *
 * @author 
 * @since Feb 20, 2016
 */
public class CrashScopeSettings {
    
//  private static CrashScopeSettings instance = null;

//  public static CrashScopeSettings getInstance() {
//      if(instance == null) {
//          instance = new CrashScopeSettings();
//      }
//      return instance;
//  }
    public CrashScopeSettings(){
        //Constructor
    }
//  /**
//   * @param instance the instance to set
//   */
//  public static void setInstance(CrashScopeSettings instance) {
//      CrashScopeSettings.instance = instance;
//  }

    
    @SerializedName("topDown")
    @Expose     
    private boolean topDown;
    @SerializedName("bottomUp")
    @Expose
    private boolean bottomUp;
    @SerializedName("noText")
    @Expose
    private boolean noText;
    @SerializedName("expectedText")
    @Expose
    private boolean expectedText;
    @SerializedName("unexpectedText")
    @Expose
    private boolean unexpectedText;
    @SerializedName("contextFeatsEnabled")
    @Expose
    private boolean contextFeatsEnabled;
    @SerializedName("contextFeatsDisabled")
    @Expose
    private boolean contextFeatsDisabled;
    

    /**
     * @return the topDown
     */
    public boolean isTopDown() {
        return topDown;
    }
    /**
     * @param topDown the topDown to set
     */
    public void setTopDown(boolean topDown) {
        this.topDown = topDown;
    }
    /**
     * @return the bottomUp
     */
    public boolean isBottomUp() {
        return bottomUp;
    }
    /**
     * @param bottomUp the bottomUp to set
     */
    public void setBottomUp(boolean bottomUp) {
        this.bottomUp = bottomUp;
    }
    /**
     * @return the noText
     */
    public boolean isNoText() {
        return noText;
    }
    /**
     * @param noText the noText to set
     */
    public void setNoText(boolean noText) {
        this.noText = noText;
    }
    /**
     * @return the expectedText
     */
    public boolean isExpectedText() {
        return expectedText;
    }
    /**
     * @param expectedText the expectedText to set
     */
    public void setExpectedText(boolean expectedText) {
        this.expectedText = expectedText;
    }
    /**
     * @return the unexpectedText
     */
    public boolean isUnexpectedText() {
        return unexpectedText;
    }
    /**
     * @param unexpectedText the unexpectedText to set
     */
    public void setUnexpectedText(boolean unexpectedText) {
        this.unexpectedText = unexpectedText;
    }
    /**
     * @return the contextFeatsEnabled
     */
    public boolean isContextFeatsEnabled() {
        return contextFeatsEnabled;
    }
    /**
     * @param contextFeatsEnabled the contextFeatsEnabled to set
     */
    public void setContextFeatsEnabled(boolean contextFeatsEnabled) {
        this.contextFeatsEnabled = contextFeatsEnabled;
    }
    /**
     * @return the contextFeatsDisabled
     */
    public boolean isContextFeatsDisabled() {
        return contextFeatsDisabled;
    }
    /**
     * @param contextFeatsDisabled the contextFeatsDisabled to set
     */
    public void setContextFeatsDisabled(boolean contextFeatsDisabled) {
        this.contextFeatsDisabled = contextFeatsDisabled;
    }
    
}

