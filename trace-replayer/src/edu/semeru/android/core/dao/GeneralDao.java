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
 * AppInfoShortDao.java
 * 
 * Created on Sep 12, 2012, 12:35:00 AM
 */
package edu.semeru.android.core.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import edu.semeru.android.core.dao.exception.CRUDException;

/**
 * This class contains the CRUD methods for any entity
 * 
 * @author Carlos Bernal
 * @since Build Sep 12, 2012
 * @param <T>
 *            the Entity
 * @param <K>
 *            type of key (Long)
 */
public abstract class GeneralDao<T extends Serializable, K extends Serializable> implements IDao<T, K> {

    /**
     * Persist an entity into a DB specified in the Persistence Unit in the em
     * 
     * @param object
     *            the entity object
     * @param em
     *            the entity manager
     * @return T, the saved entity
     * @throws CRUDException
     */
    @SuppressWarnings("finally")
    @Override
    public T save(T object, EntityManager em) throws CRUDException {
	if (object != null) {
	    try {
		em.persist(object);
		object = em.merge(object);
	    } catch (Exception e) {
		Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
		throw new CRUDException(e);
	    } finally {
		return object;
	    }
	} else {
	    return null;
	}
    }

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
    @SuppressWarnings("unchecked")
    @Override
    public T getById(K key, EntityManager em) throws CRUDException {
	if (key != null) {
	    try {
		return (T) em.find(getTClass().getClass(), key);
	    } catch (Exception e) {
		Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
		throw new CRUDException(e);
	    }
	} else {
	    return null;
	}
    }

    /**
     * Delete an entity in a DB specified in the Persistence Unit in the em
     * 
     * @param toDelete
     *            the entity object to delete
     * @param em
     *            the entity manager
     * @throws CRUDException
     */
    @Override
    public void delete(T toDelete, EntityManager em) throws CRUDException {
	try {
	    em.remove(em.merge(toDelete));
	} catch (Exception e) {
	    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
	    throw new CRUDException(e);
	}

    }

    /**
     * Update an entity in a DB specified in the Persistence Unit in the em
     * 
     * @param toUpdate
     *            the entity object to update
     * @param em
     *            the entity manager
     * @return The Updated entity
     * @throws CRUDException
     */
    @SuppressWarnings("finally")
    @Override
    public T update(T toUpdate, EntityManager em) throws CRUDException {
	try {
	    em.merge(toUpdate);
	} catch (Exception e) {
	    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
	    throw new CRUDException(e);
	} finally {
	    return toUpdate;
	}
    }

    /**
     * Find all the objects of the Class T in a DB specified in the Persistence
     * Unit in the em
     * 
     * @param em
     *            the entity manager
     * @return a List of objects of the Class T
     * @throws SQLException
     */
    @Override
    public List<T> findAll(EntityManager em) throws SQLException {
	return findNamedQueryGeneric(em, getTClass().getClass().getSimpleName() + ".findAll",
		new HashMap<String, Object>());
    }

    /**
     * Find all the objects of the Class T in a DB in the Persistence Unit in
     * the em according to the specified query
     * 
     * @param em
     *            the entity manager
     * @param query
     *            to make the search in the DB
     * @param params
     *            a map with the parameters of the query
     * @return a List of objects of the Class T
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<T> findGeneric(EntityManager em, String query, Map<String, Object> params) throws SQLException {
	Query q = getQuery(em, query, params);
	try {
	    return q.getResultList();
	} catch (NoResultException e) {
	    return new ArrayList<T>();
	}
    }

    /**
     * @param em
     * @param query
     * @param params
     * @return
     * @throws SQLException
     */
    public Query getQuery(EntityManager em, String query, Map<String, Object> params) throws SQLException {
        Query q = null;
        try {
            q = em.createQuery(query);
        } catch (Exception e) {
            throw new SQLException(e);
        }
        for (String str : params.keySet()) {
            q.setParameter(str, params.get(str));
        }
        return q;
    }

    /**
     * Returns the number of results for a query
     * 
     * @param em
     *            the entity manager
     * @param query
     *            to make the search in the DB
     * @param params
     *            a map with the parameters of the query
     * @return number of results, 0 if nothing found
     * @throws SQLException
     */
    @Override
    public Integer findGenericCount(EntityManager em, String query, Map<String, Object> params) throws SQLException {
	Query q = getQuery(em, query, params);
	try {
	    return Integer.parseInt(String.valueOf(q.getSingleResult()));
	} catch (NoResultException e) {
	    return 0;
	}
    }

    /**
     * Find all the objects of the Class T in a DB in the Persistence Unit in
     * the em according to the specified query in a range
     * 
     * @param em
     *            the entity manager
     * @param query
     *            to make the search in the DB
     * @param params
     *            a map with the parameters of the query
     * @param size
     *            if it's not -1 it sets the max results for the query
     * @return a List of objects of the Class T
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<T> findGeneric(EntityManager em, String query, int startPosition, int size, Map<String, Object> params)
	    throws SQLException {
	Query q = getQuery(em, query, params);
	if (size != -1) {
	    q.setFirstResult(startPosition);
	    q.setMaxResults(size);
	}
	try {
	    return q.getResultList();
	} catch (NoResultException e) {
	    return new ArrayList<T>();
	}
    }

    /**
     * Find all the objects of the Class T in a DB specified in the Persistence
     * Unit in the em according to the named query
     * 
     * @param em
     *            the entity manager
     * @param queryName
     *            name of the query to make the search
     * @param params
     *            a map with the parameters of the query
     * @return a List of objects of the Class T
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<T> findNamedQueryGeneric(EntityManager em, String queryName, Map<String, Object> params)
	    throws SQLException {
	Query q = getNamedQuery(em, queryName, params);
	try {
	    return q.getResultList();
	} catch (NoResultException e) {
	    return new ArrayList<T>();
	}
    }

    /**
     * Get the object of the Class T in a DB in the Persistence Unit in the em
     * according to the specified query
     * 
     * @param em
     *            the entity manager
     * @param query
     *            to make the search in the DB
     * @param params
     *            a map with the parameters of the query
     * @return The found object
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    @Override
    public T getGeneric(EntityManager em, String query, Map<String, Object> params) throws SQLException {
	Query q = getQuery(em, query, params);
	try {
	    return (T) q.getSingleResult();
	} catch (NoResultException e) {
	    return null;
	}
    }

    /**
     * Get the object of the Class T in a DB in the Persistence Unit in the em
     * according to the specified query
     * 
     * @param em
     *            the entity manager
     * @param queryName
     *            name of the query to make the search
     * @param params
     *            a map with the parameters of the query
     * @return The found object
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    @Override
    public T getNamedQueryGeneric(EntityManager em, String queryName, Map<String, Object> params) throws SQLException {
	Query q = getNamedQuery(em, queryName, params);
	try {
	    return (T) q.getSingleResult();
	} catch (NoResultException e) {
	    return null;
	}
    }

    /**
     * @param em
     * @param queryName
     * @param params
     * @return
     * @throws SQLException
     */
    private Query getNamedQuery(EntityManager em, String queryName, Map<String, Object> params) throws SQLException {
        Query q = null;
        try {
            q = em.createNamedQuery(queryName);
        } catch (Exception e) {
            throw new SQLException(e);
        }
        for (String str : params.keySet()) {
            q.setParameter(str, params.get(str));
        }
        return q;
    }

    /**
     * Obtain the instance of the General Dao class
     * 
     * @return an instance of the General Dao class
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private T getTClass() {
	Type type = getClass().getGenericSuperclass();
	T vo = null;
	if (type instanceof ParameterizedType) {
	    try {
		ParameterizedType paramType = (ParameterizedType) type;
		Class<T> tClass = (Class<T>) paramType.getActualTypeArguments()[0];
		vo = tClass.newInstance();
	    } catch (InstantiationException ex) {
		Logger.getLogger(GeneralDao.class.getName()).log(Level.SEVERE, null, ex);
		return null;
	    } catch (IllegalAccessException ex) {
		Logger.getLogger(GeneralDao.class.getName()).log(Level.SEVERE, null, ex);
		return null;
	    } catch (Exception ex) {
		return null;
	    }
	}
	return vo;
    }

    @Override
    public HashMap<String, Object> getEmptyMap() {
	return new HashMap<String, Object>();
    }
}
