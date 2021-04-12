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
 * AppDao.java
 * 
 * Created on Jun 19, 2014, 12:35:48 AM
 * 
 */

package edu.semeru.android.core.dao;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;

import edu.semeru.android.core.entity.model.App;

/**
 * {Insert class description here}
 * 
 * @author Carlos Bernal
 * @since Build Jun 19, 2014
 */
public class AppDao extends GeneralDao<App, Long> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1173530798119926089L;

	/**
	 * @param packageName
	 * @param em
	 * @return
	 * @throws SQLException
	 */
	public App getByNamePackage(String packageName, EntityManager em) throws SQLException {

		HashMap<String, Object> params = getEmptyMap();
		params.put("packageName", packageName);
		return getGeneric(em, "SELECT app FROM App app WHERE app.packageName = :packageName", params);

	}

	public App getUniqueByPackage(String packageName, String version, EntityManager em) throws SQLException {

		HashMap<String, Object> params = getEmptyMap();
		params.put("packageName", packageName);
		params.put("version", version);
		return getGeneric(em, "SELECT app FROM App app WHERE app.packageName = :packageName and app.version = :version",
				params);

	}

	public App getUniqueByNameAndVersion(String appName, String version, EntityManager em) throws SQLException {

		HashMap<String, Object> params = getEmptyMap();
		params.put("name", appName);
		params.put("version", version);
		return getGeneric(em, "SELECT app FROM App app WHERE app.name = :name and app.version = :version", params);

	}

	public List<App> getUniqueByName(String appName, EntityManager em) throws SQLException {
		HashMap<String, Object> params = getEmptyMap();
		params.put("name", appName);
		return findGeneric(em, "SELECT app FROM App app WHERE lower(app.name) = lower(:name) ", params);
	}
	
	public App getUniqueById(Long id, EntityManager em) throws SQLException {
        HashMap<String, Object> params = getEmptyMap();
        params.put("id", id);
        return getGeneric(em, "SELECT app FROM App app WHERE app.id = :id", params);
    }

	public List<App> getUniqueByPackage(String packageName, EntityManager em) throws SQLException {
		HashMap<String, Object> params = getEmptyMap();
		params.put("packageName", packageName);
		return findGeneric(em, "SELECT app FROM App app WHERE app.packageName = :packageName", params);
	}

}