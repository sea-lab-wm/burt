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
 * DynGuiComponentDao.java
 * 
 * Created on Jul 21, 2015, 4:52:05 PM
 * 
 */
package edu.semeru.android.core.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;

import edu.semeru.android.core.entity.model.fusion.DynGuiComponent;

/**
 * {Insert class description here}
 *
 * @author Carlos Bernal
 * @since Jul 21, 2015
 */
public class DynGuiComponentDao extends GeneralDao<DynGuiComponent, Long> {

    /**
     * 
     */
    private static final long serialVersionUID = 1252342993395491768L;

    public List<DynGuiComponent> findByWindowName(Long idExecution, String windowName, EntityManager em)
            throws SQLException {

        HashMap<String, Object> params = getEmptyMap();
        params.put("idExecution", idExecution);
        params.put("windowName", windowName);
        return findGeneric(
                em,
                "SELECT step.dynGuiComponent FROM Step step WHERE step.execution.id = :idExecution and concat(step.dynGuiComponent.currentWindow,COALESCE(step.dynGuiComponent.titleWindow,'')) = :windowName",
                params);

    }

    /**
     * 
     * @param em
     * @return
     * @throws SQLException
     */
    public List<DynGuiComponent> findNoParent(EntityManager em) throws SQLException {
        HashMap<String, Object> params = getEmptyMap();
        return findGeneric(em, "SELECT d FROM DynGuiComponent d WHERE d.id NOT IN (SELECT s.dynGuiComponent.id FROM Step s)", params);
    }

    /**
     * Returns the sibling components of the component identified by componentId
     *
     * @param componentId the id of the component
     * @param em the entity manager
     * @return list of sibling components
     * @throws SQLException if any error
     */
    public List<DynGuiComponent> findSiblings(Long componentId, EntityManager em) throws SQLException {
        HashMap<String, Object> params = getEmptyMap();
        params.put("componentId", componentId);

        return findGeneric(em, "SELECT c2 FROM DynGuiComponent c2\n" +
                " WHERE c2.parent.id = (SELECT c.parent.id FROM DynGuiComponent c WHERE c.id = :componentId )\n" +
                " AND c2.id  !=  (SELECT c.id FROM DynGuiComponent c WHERE c.id = :componentId ) ", params);
    }

}
