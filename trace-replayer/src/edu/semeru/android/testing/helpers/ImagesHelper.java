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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * 
 * {Insert class description here}
 *
 * @author Mario Linares and Carlos Bernal
 * @since Sep 22, 2015
 */
public class ImagesHelper {

    public static String getHashFromImage(String imagePath) throws IOException {
        String md5 = null;
        FileInputStream fis = new FileInputStream(new File(imagePath));
        md5 = DigestUtils.md5Hex(fis);
        fis.close();
        return md5;
    }

    public static String getHashFromString(String text) throws IOException {
        String md5 = null;
        DigestUtils.md5Hex(text);
        md5 = DigestUtils.md5Hex(text);
        return md5;
    }
    
    public static String getHashFromStringExcludeDateAndTime(String guiHiearchy, String pathToUIDump) throws IOException {
        
    	//Check to See if the GUI Hierarchy contains a Date or Time Picker component
    	
    	if(guiHiearchy.contains("DatePicker")){
    		return "Date-Picker";
    	}if(guiHiearchy.contains("TimePicker")){
    		return "Time-Picker";
    	}
    	
    		guiHiearchy = guiHiearchy.replaceAll("[0-9]{2}/[0-9]{2}/[0-9]{4}", "DateTime");
    		guiHiearchy = guiHiearchy.replaceAll("[0-9]{2}/[0-9]{2}/[0-9]{2}", "DateTime");
    		guiHiearchy = guiHiearchy.replaceAll("[0-9]{1}/[0-9]{2}/[0-9]{4}", "DateTime");
    		guiHiearchy = guiHiearchy.replaceAll("[0-9]{1}/[0-9]{2}/[0-9]{2}", "DateTime");
    		guiHiearchy = guiHiearchy.replaceAll("[0-9]{1}/[0-9]{1}/[0-9]{4}", "DateTime");
    		guiHiearchy = guiHiearchy.replaceAll("[0-9]{1}/[0-9]{1}/[0-9]{2}", "DateTime");
    		//System.out.println(guiHiearchy);
    		
    		FileWriter fw = new FileWriter(pathToUIDump + ".hierarchy",false);
    		
    		try {
    		   
    		    fw.write(guiHiearchy);
    	
    		    fw.close();
    		} catch (IOException e) {
    		        // TODO Auto-generated catch block
    		        e.printStackTrace();
    		}           
    		
    		//System.out.println("Found Date Expression!!");
    	
    	String md5 = null;
        DigestUtils.md5Hex(guiHiearchy);
        md5 = DigestUtils.md5Hex(guiHiearchy);
        return md5;
    }

    public static void cropImageAndSave(String sourceImagePath, String croppedImagePath, int x, int y, int width,
            int height) throws IOException {
        BufferedImage source = ImageIO.read(new File(sourceImagePath));
        BufferedImage cropped = source.getSubimage(x, y, width, height);
        ImageIO.write(cropped, "png", new File(croppedImagePath));
    }

    public static void main(String[] args) {
    	
    	try {
    		//List<String> lines = Files.readAllLines(Paths.get("/Users/KevinMoran/Desktop/ui-dump.xml"));
            System.out.println(getHashFromStringExcludeDateAndTime("8/20/2016", ""));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
