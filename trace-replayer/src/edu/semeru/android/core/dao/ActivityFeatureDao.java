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
 * ActivityFeatureDao.java
 * 
 * Created on Aug 12, 2015, 4:16:58 PM
 * 
 */
package edu.semeru.android.core.dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;

import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;

import edu.semeru.android.core.entity.model.App;
import edu.semeru.android.core.entity.model.fusion.ActivityFeature;

/**
 * {Insert class description here}
 *
 * @author Carlos Bernal
 * @since Aug 12, 2015
 */

public class ActivityFeatureDao extends GeneralDao<ActivityFeature, Long> implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1173530798119926089L;

    
    public static void main(String[] args){
	
	int i = 1;
	
	try {
	    getActivityFeatureList(i, "crashscope_test");
	} catch (InstantiationException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IllegalAccessException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (ClassNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	
    }
    

    public static int setAppId(String appPackage, App app, String databaseName) throws InstantiationException, IllegalAccessException,
    ClassNotFoundException, SQLException {

	Connection conn;
	Class.forName("com.mysql.jdbc.Driver").newInstance();
	conn = DriverManager.getConnection("jdbc:mysql://localhost/" + databaseName, "root", "");
	Statement stm4 = conn.createStatement();
	int appid = 1000;
	try {
	    ResultSet rs4 = stm4
		    .executeQuery("SELECT * FROM " + databaseName + ".APP WHERE package_name = " + "\""
			    + appPackage + "\"" + ";");
	    if (rs4.next()) {
		appid = rs4.getInt("ID");
	    }
	    stm4.close();
	    conn.close();
	    if (appid != 1000) {
		app.setId((long) appid);
	    }
	} catch (MySQLSyntaxErrorException error) {
	    System.out.println("SQL error, no database");
	}

	return appid;
	
    }

    public List<ActivityFeature> getActivityFeatureListDerby(Long appId, EntityManager em) throws SQLException {
	HashMap<String, Object> params = getEmptyMap();
	params.put("AppId", appId);
	return findGeneric(em, "SELECT app.actFeatures FROM App app WHERE app.id = :AppId", params);
    }
    
    public static ArrayList<ActivityFeature> getActivityFeatureList(long appId, String databaseName) throws InstantiationException, IllegalAccessException,
    ClassNotFoundException, SQLException {

	ArrayList<ActivityFeature> featureList = new ArrayList<ActivityFeature>();
	
	Connection conn;
	Class.forName("com.mysql.jdbc.Driver").newInstance();
	conn = DriverManager.getConnection("jdbc:mysql://localhost/" + databaseName, "root", "");
	Statement stm4 = conn.createStatement();
	System.out.println("---Printing Application Activity Feature List---");
	System.out.println();
	try {
	    ResultSet rs4 = stm4
		    .executeQuery("SELECT ACTIVITY, FEATURE FROM " + databaseName + ".ACTIVITY_FEATURE WHERE APP = " + "\""
			    + appId + "\"" + ";");
	    while (rs4.next()) {
		ActivityFeature feature = new ActivityFeature();
		System.out.println("Activity = " + rs4.getString("ACTIVITY") + " Feature = " + rs4.getString("FEATURE"));
		feature.setActivity(rs4.getString("ACTIVITY"));
		feature.setFeature(rs4.getString("FEATURE"));
		featureList.add(feature);
	    }
	    stm4.close();
	    conn.close();
	  
	} catch (MySQLSyntaxErrorException error) {
	    System.out.println("SQL error, no database");
	}

	return featureList;
	
    }
    

}
