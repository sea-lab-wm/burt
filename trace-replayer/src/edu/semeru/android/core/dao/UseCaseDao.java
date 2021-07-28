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
 * UseCaseDao.java
 * 
 * Created on Aug 14, 2015, 2:11:42 PM
 * 
 */
package edu.semeru.android.core.dao;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;

import edu.semeru.android.core.entity.model.fusion.UseCase;

/**
 * {Insert class description here}
 *
 * @author Carlos Bernal
 * @since Aug 14, 2015
 */
public class UseCaseDao extends GeneralDao<UseCase, Long> implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -8433074889119146965L;

    /**
     * 
     * @param windowName
     * @param em
     * @return
     * @throws SQLException
     */
    public List<UseCase> findUseCaseByWindow(String windowName, String packageName, EntityManager em) throws SQLException {
        HashMap<String, Object> params = getEmptyMap();
        params.put("windowName", windowName);
        params.put("packageName", packageName);
        return findGeneric(
                em,
//                "SELECT u FROM UseCase u WHERE u.id in (SELECT step.useCase.id FROM Step step WHERE concat(step.dynGuiComponent.currentWindow,COALESCE(step.dynGuiComponent.titleWindow,''))= :windowName)",
//                "SELECT u FROM UseCase u WHERE u.window = :windowName",
                "SELECT u FROM UseCase u WHERE u.window = :windowName AND u.packageName = :packageName",
                params);
    }

    /**
     * 
     * @param packageName
     * @param em
     * @return
     * @throws SQLException
     */
    public List<UseCase> findUseCaseByApp(String packageName, EntityManager em) throws SQLException {
        HashMap<String, Object> params = getEmptyMap();
        params.put("packageName", packageName);
        return findGeneric(
                em,
//                "SELECT u FROM UseCase u WHERE u.id IN (SELECT s.useCase.id FROM Step s WHERE s.execution.id IN (SELECT e.id FROM Execution e WHERE e.app.id IN (SELECT a.id FROM App a WHERE a.packageName = :packageName)))",
                "SELECT u FROM UseCase u WHERE u.packageName = :packageName",
                params);
    }

}
