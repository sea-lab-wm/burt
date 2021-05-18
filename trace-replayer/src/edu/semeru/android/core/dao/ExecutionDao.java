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
 * ExecutionDao.java
 * 
 * Created on Oct 21, 2014, 07:33:00 PM
 * 
 */

package edu.semeru.android.core.dao;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;

import edu.semeru.android.core.entity.model.fusion.Execution;

/**
 * {Insert class description here}
 * 
 * @author Carlos Bernal
 * @since Build Oct 21, 2014
 */
public class ExecutionDao extends GeneralDao<Execution, Long> implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public List<Execution> findByCrash(EntityManager em) throws SQLException {
        HashMap<String, Object> params = getEmptyMap();
        params.put("crash", true);
        return findGeneric(em, "SELECT execution FROM Execution execution WHERE execution.crash = :crash", params);
    }

    /**
     * @param packageName
     * @param versionApp
     * @param em
     * @return
     */
    public List<Execution> findByApp(String packageName, String versionApp, EntityManager em) throws SQLException {
	HashMap<String, Object> params = getEmptyMap();
        params.put("packageName", packageName);
        params.put("version", versionApp);
        return findGeneric(em, "SELECT execution FROM Execution execution WHERE execution.app.packageName = :packageName and execution.app.version = :version", params);
    }
    
    /**
     * @param packageName
     * @param versionApp
     * @param em
     * @return
     */
    public Execution getByType(String executionType, EntityManager em) throws SQLException {
        HashMap<String, Object> params = getEmptyMap();
        params.put("executionType", executionType);
        return getGeneric(em, "SELECT execution FROM Execution execution WHERE execution.executionType = :executionType", params);
    }
    
}