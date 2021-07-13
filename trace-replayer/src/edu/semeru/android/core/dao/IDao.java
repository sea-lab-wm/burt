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
 * IDao.java
 *
 * Created on 26-jul-2011, 18:17:39
 */
package edu.semeru.android.core.dao;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import edu.semeru.android.core.dao.exception.CRUDException;

/**
 * @param <T>
 *            The entity
 * @param <K>
 *            Type of the Id (Long)
 * @author Carlos Bernal
 * @since Build Jul 26, 2011
 */
public interface IDao<T extends Serializable, K extends Serializable> {

    /**
     * Persist an entity into a DB specified in the Persistence Unit in the em
     * 
     * @param object
     * @param em
     *            the entity manager
     * @return T, the saved entity
     * @throws CRUDException
     */
    T save(T object, EntityManager em) throws CRUDException;

    /**
     * Find an entity by the Id in a DB specified in the Persistence Unit in the
     * em
     * 
     * @param key
     *            that is the id of the entity that will be found, EntityManager
     *            em the entity manager
     * @param em
     *            the entity manager
     * @return The entity found
     * @throws CRUDException
     */
    T getById(K key, EntityManager em) throws CRUDException;

    /**
     * Delete an entity in a DB specified in the Persistence Unit in the em
     * 
     * @param toDelete
     * @param em
     *            the entity manager
     * @throws CRUDException
     */
    void delete(T toDelete, EntityManager em) throws CRUDException;

    /**
     * Update an entity in a DB specified in the Persistence Unit in the em
     * 
     * @param toUpdate
     *            the entity to update
     * @param em
     *            the entity manager
     * @return The Updated entity
     * @throws CRUDException
     */
    T update(T toUpdate, EntityManager em) throws CRUDException;

    /**
     * Find all the objects of the Class T in a DB specified in the Persistence
     * Unit in the em
     * 
     * @param em
     *            the entity mangaer
     * @return a List of objects of the Class T
     * @throws SQLException
     */
    List<T> findAll(EntityManager em) throws SQLException;

    /**
     * Find all the objects of the Class T in a DB in the Persistence Unit in
     * the em according to the specified query
     * 
     * @param em
     *            the entity manager
     * @param query
     *            to make the search in the DB
     * @param params
     *            a map with the params of the query
     * @return a List of objects of the Class T
     * @throws SQLException
     */
    List<T> findGeneric(EntityManager em, String query, Map<String, Object> params) throws SQLException;

    /**
     * Find all the objects of the Class T in a DB in the Persistence Unit in
     * the em according to the specified query in a range
     * 
     * @param em
     *            the entity manager
     * @param query
     *            to make the search in the DB
     * @param startPosition
     *            initial position of the query
     * @param params
     *            a map with the params of the query
     * @param size
     *            of the list to return
     * @return a List of objects of the Class T
     * @throws SQLException
     */
    List<T> findGeneric(EntityManager em, String query, int startPosition, int size, Map<String, Object> params)
	    throws SQLException;

    /**
     * Find all the objects of the Class T in a DB specified in the Persistence
     * Unit in the em acoording to the named query
     * 
     * @param em
     *            the entity manager
     * @param queryName
     *            name of the query to make the search
     * @param params
     *            a map with the params of the query
     * @return a List of objects of the Class T
     * @throws SQLException
     */
    List<T> findNamedQueryGeneric(EntityManager em, String queryName, Map<String, Object> params) throws SQLException;

    /**
     * Get the object of the Class T in a DB in the Persistence Unit in the em
     * according to the specified query
     * 
     * @param em
     *            the entity manager
     * @param query
     *            to make the search in the DB
     * @param params
     *            a map with the params of the query
     * @return The found object
     * @throws SQLException
     */
    T getGeneric(EntityManager em, String query, Map<String, Object> params) throws SQLException;

    /**
     * Get the object of the Class T in a DB in the Persistence Unit in the em
     * according to the specified query
     * 
     * @param em
     *            the entity manager
     * @param queryName
     *            name of the query to make the search
     * @param params
     *            a map with the params of the query
     * @return The found object
     * @throws SQLException
     */
    T getNamedQueryGeneric(EntityManager em, String queryName, Map<String, Object> params) throws SQLException;

    /**
     * Find the count of all the objects of the Class T in a DB in the
     * Persistence Unit in the em according to the specified query in a range
     * 
     * @param em
     * @param query
     * @param params
     * @return
     * @throws SQLException
     */
    Integer findGenericCount(EntityManager em, String query, Map<String, Object> params) throws SQLException;

    /**
     * Gets an empty map
     * 
     * @return an empty map
     */
    HashMap<String, Object> getEmptyMap();
}
