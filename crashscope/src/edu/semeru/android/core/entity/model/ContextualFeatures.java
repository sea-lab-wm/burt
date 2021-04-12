

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
 * ContextualFeatures.java
 * 
 * Created on Feb 20, 2016, 3:38:16 PM
 * 
 */
package edu.semeru.android.core.entity.model;

/**
 * {Insert class description here}
 *
 * @author Kevin Moran
 * @since Feb 20, 2016
 */
public class ContextualFeatures {

    private boolean network;
    private boolean gps;
    private boolean accelNormal;
    private boolean magnetNormal;
    private boolean tempNormal;
    private boolean appAccel;
    private boolean appNetwork;
    private boolean appMagnet;
    private boolean appTemp;
    private boolean appGPS;
    
    
    public void ContexutalFeatures(){
    network = true;
    gps = true;
    accelNormal = true;
    magnetNormal = true;
    tempNormal = true;
    appAccel = true;
    appNetwork = true;
    appMagnet = true;
    appTemp = true;
    appGPS = true;
    }


    /**
     * @return the network
     */
    public boolean isNetwork() {
        return network;
    }


    /**
     * @param network the network to set
     */
    public void setNetwork(boolean network) {
        this.network = network;
    }


    /**
     * @return the gps
     */
    public boolean isGps() {
        return gps;
    }


    /**
     * @param gps the gps to set
     */
    public void setGps(boolean gps) {
        this.gps = gps;
    }


    /**
     * @return the accelNormal
     */
    public boolean isAccelNormal() {
        return accelNormal;
    }


    /**
     * @param accelNormal the accelNormal to set
     */
    public void setAccelNormal(boolean accelNormal) {
        this.accelNormal = accelNormal;
    }


    /**
     * @return the magnetNormal
     */
    public boolean isMagnetNormal() {
        return magnetNormal;
    }


    /**
     * @param magnetNormal the magnetNormal to set
     */
    public void setMagnetNormal(boolean magnetNormal) {
        this.magnetNormal = magnetNormal;
    }


    /**
     * @return the tempNormal
     */
    public boolean isTempNormal() {
        return tempNormal;
    }


    /**
     * @param tempNormal the tempNormal to set
     */
    public void setTempNormal(boolean tempNormal) {
        this.tempNormal = tempNormal;
    }

    /**
     * @return the appAccel
     */
    public boolean isAppAccel() {
        return appAccel;
    }


    /**
     * @param appAccel the appAccel to set
     */
    public void setAppAccel(boolean appAccel) {
        this.appAccel = appAccel;
    }


    /**
     * @return the appNetwork
     */
    public boolean isAppNetwork() {
        return appNetwork;
    }


    /**
     * @param appNetwork the appNetwork to set
     */
    public void setAppNetwork(boolean appNetwork) {
        this.appNetwork = appNetwork;
    }


    /**
     * @return the appMagnet
     */
    public boolean isAppMagnet() {
        return appMagnet;
    }


    /**
     * @param appMagnet the appMagnet to set
     */
    public void setAppMagnet(boolean appMagnet) {
        this.appMagnet = appMagnet;
    }


    /**
     * @return the appTemp
     */
    public boolean isAppTemp() {
        return appTemp;
    }


    /**
     * @param appTemp the appTemp to set
     */
    public void setAppTemp(boolean appTemp) {
        this.appTemp = appTemp;
    }


    /**
     * @return the appGPS
     */
    public boolean isAppGPS() {
        return appGPS;
    }


    /**
     * @param appGPS the appGPS to set
     */
    public void setAppGPS(boolean appGPS) {
        this.appGPS = appGPS;
    }
    
   
    
}

