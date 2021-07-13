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
 * StepDao.java
 * 
 * Created on Jul 23, 2015, 2:06:25 PM
 * 
 */
package edu.semeru.android.core.dao;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import edu.semeru.android.core.entity.model.fusion.Step;

/**
 * {Insert class description here}
 *
 * @author Carlos Bernal
 * @since Jul 23, 2015
 */
public class StepDao extends GeneralDao<Step, Long> implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 3085149408591868738L;

    /**
     * 
     * @param idExecution
     * @param windowName
     * @param em
     * @return
     * @throws SQLException
     */
    public List<Step> findByWindowName(Long idExecution, String windowName, EntityManager em) throws SQLException {
        HashMap<String, Object> params = getEmptyMap();
        params.put("idExecution", idExecution);
        params.put("windowName", windowName);
        return findGeneric(em,
                "SELECT step FROM Step step WHERE step.execution.id = :idExecution and concat(step.dynGuiComponent.currentWindow,COALESCE(step.dynGuiComponent.titleWindow,'null')) = :windowName order by step.sequenceStep",
                params);
    }

    /**
     * 
     * @param idExecution
     * @param windowName
     * @param em
     * @return
     * @throws SQLException
     */
    public List<Step> findByExecutionId(Long idExecution, EntityManager em) throws SQLException {
        HashMap<String, Object> params = getEmptyMap();
        params.put("idExecution", idExecution);
        return findGeneric(em,
                "SELECT step FROM Step step WHERE step.execution.id = :idExecution order by step.sequenceStep", params);
    }

    /**
     * 
     * @param idExecution
     * @param windowName
     * @param em
     * @return
     * @throws SQLException
     */
    public Step findByLastExecutionId(Long idExecution, EntityManager em) throws SQLException {
        HashMap<String, Object> params = getEmptyMap();
        params.put("idExecution", idExecution);
        return getGeneric(em, "SELECT step FROM Step step " + "WHERE step.execution.id = :idExecution "
                + "AND step.sequenceStep = (SELECT max(s.sequenceStep) FROM Step s WHERE s.execution.id = :idExecution) ",
                params);
    }

    /**
     * 
     * @param hashStep
     * @param em
     * @return
     * @throws SQLException
     */
    public List<Step> findByHash(String hashStep, EntityManager em) throws SQLException {
        HashMap<String, Object> params = getEmptyMap();
        params.put("hashStep", hashStep);
        return findGeneric(em, "SELECT step FROM Step step WHERE step.hashStep = :hashStep", params);
    }
    
    /**
     * 
     * @param hashStep
     * @param em
     * @return
     * @throws SQLException
     */
    public Step findStepbyExecutionAndSequence(Long idExecution, int stepSeq, EntityManager em) throws SQLException {
        HashMap<String, Object> params = getEmptyMap();
        params.put("idExecution", idExecution);
        params.put("stepSeq", stepSeq);
        return getGeneric(em, "SELECT step FROM Step step WHERE step.execution.id = :idExecution AND step.sequenceStep = :stepSeq", params);
    }

    /**
     * 
     * @param idExecution
     * @param em
     * @return
     * @throws NumberFormatException
     * @throws SQLException
     */
    public int getMaxSequenceByExecution(Long idExecution, EntityManager em) throws SQLException {
        HashMap<String, Object> params = getEmptyMap();
        params.put("idExecution", idExecution);
        String sQuery = "SELECT max(step.sequenceStep) FROM Step step WHERE step.execution.id = :idExecution";
        try {
            Query q = getQuery(em, sQuery, params);
            return Integer.parseInt(String.valueOf(q.getSingleResult()));
        } catch (NoResultException e) {
            return 0;
        }
    }

}
