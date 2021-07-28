

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
 * PersistDataService.java
 * 
 * Created on Jun 20, 2014, 12:46:58 AM
 * 
 */
package edu.semeru.android.core.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import edu.semeru.android.core.dao.ActivityFeatureDao;
import edu.semeru.android.core.dao.AppDao;
import edu.semeru.android.core.dao.DynGuiComponentDao;
import edu.semeru.android.core.dao.ExecutionDao;
import edu.semeru.android.core.dao.ScreenDao;
import edu.semeru.android.core.dao.StepDao;
import edu.semeru.android.core.dao.exception.CRUDException;
import edu.semeru.android.core.entity.model.App;
import edu.semeru.android.core.entity.model.GuiComponent;
import edu.semeru.android.core.entity.model.fusion.ActivityFeature;
import edu.semeru.android.core.entity.model.fusion.DynGuiComponent;
import edu.semeru.android.core.entity.model.fusion.Execution;
import edu.semeru.android.core.entity.model.fusion.Screen;
import edu.semeru.android.core.entity.model.fusion.Step;
import edu.semeru.android.testing.helpers.Constants;

/**
 * {Insert class description here}
 * 
 * @author Carlos Bernal
 * @since Jun 20, 2014
 */
public class PersistDataService {

    public static App saveAppData(App app, String db, Map properties) {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        if (properties != null) {
            emf = Persistence.createEntityManagerFactory(db, properties);
            em = emf.createEntityManager();
        } else {
            emf = Persistence.createEntityManagerFactory(db);
            em = emf.createEntityManager();
        }

        AppDao dao = new AppDao();

        em.getTransaction().begin();
        try {
            app = dao.save(app, em);
        } catch (CRUDException e) {
            e.printStackTrace();
        }
        em.getTransaction().commit();
        em.close();
        emf.close();
        return app;
    }
    
    public static App updateAppData(App app, String db, Map properties) {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        if (properties != null) {
            emf = Persistence.createEntityManagerFactory(db, properties);
            em = emf.createEntityManager();
        } else {
            emf = Persistence.createEntityManagerFactory(db);
            em = emf.createEntityManager();
        }

        AppDao dao = new AppDao();

        em.getTransaction().begin();
        try {
            app = dao.update(app, em);
        } catch (CRUDException e) {
            e.printStackTrace();
        }
        em.getTransaction().commit();
        em.close();
        emf.close();
        return app;
    }

    public static App saveAppData(App app, String db) {
        return saveAppData(app, db, null);
    }

    public static Execution saveExecutionData(Execution execution, String db, Map properties) {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        if (properties != null) {
            emf = Persistence.createEntityManagerFactory(db, properties);
            em = emf.createEntityManager();
        } else {
            emf = Persistence.createEntityManagerFactory(db);
            em = emf.createEntityManager();
        }

        ExecutionDao dao = new ExecutionDao();

        em.getTransaction().begin();
        try {
            execution = dao.save(execution, em);
        } catch (CRUDException e) {
            e.printStackTrace();
        }
        em.getTransaction().commit();
        em.close();
        emf.close();
        return execution;
    }

    public static Execution saveExecutionData(Execution execution, String db) {
        return saveExecutionData(execution, db, null);
    }

    public static Execution saveExecutionData(Execution execution, EntityManager em) {
        ExecutionDao dao = new ExecutionDao();

        em.getTransaction().begin();
        try {
            execution = dao.save(execution, em);
        } catch (CRUDException e) {
            e.printStackTrace();
        }
        em.getTransaction().commit();
        return execution;
    }
    
    public static Execution updateExecution(Execution execution, String db, Map properties) {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        if (properties != null) {
            emf = Persistence.createEntityManagerFactory(db, properties);
            em = emf.createEntityManager();
        } else {
            emf = Persistence.createEntityManagerFactory(db);
            em = emf.createEntityManager();
        }

        ExecutionDao dao = new ExecutionDao();

        em.getTransaction().begin();
        try {
            execution = dao.update(execution, em);
        } catch (CRUDException e) {
            e.printStackTrace();
        }
        em.getTransaction().commit();
//        em.close();
//        emf.close();
        return execution;
    }

    public static Execution getExecutionById(Long id, String db, Map properties) {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        if (properties != null) {
            emf = Persistence.createEntityManagerFactory(db, properties);
            em = emf.createEntityManager();
        } else {
            emf = Persistence.createEntityManagerFactory(db);
            em = emf.createEntityManager();
        }

        ExecutionDao dao = new ExecutionDao();

        Execution execution = null;
        try {
            execution = dao.getById(id, em);
        } catch (CRUDException e) {
            e.printStackTrace();
        }
        return execution;
    }
    
    public static Execution getExecutionByExecutionType(String executionType, String db, Map properties) {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        if (properties != null) {
            emf = Persistence.createEntityManagerFactory(db, properties);
            em = emf.createEntityManager();
        } else {
            emf = Persistence.createEntityManagerFactory(db);
            em = emf.createEntityManager();
        }
        
        ExecutionDao dao = new ExecutionDao();
        
        Execution execution = null;
        try {
            execution = dao.getByType(executionType, em);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return execution;
    }

    public static List<Execution> getExecutionByApp(App app, String db, Map properties) {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        if (properties != null) {
            emf = Persistence.createEntityManagerFactory(db, properties);
            em = emf.createEntityManager();
        } else {
            emf = Persistence.createEntityManagerFactory(db);
            em = emf.createEntityManager();
        }

        ExecutionDao dao = new ExecutionDao();

        List<Execution> executions = null;
        try {
            executions = dao.findByApp(app.getPackageName(), app.getVersion(), em);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return executions;
    }
    
    public static int getMaxStepSequenceByExecution(Long idExecution, String db, Map properties) {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        if (properties != null) {
            emf = Persistence.createEntityManagerFactory(db, properties);
            em = emf.createEntityManager();
        } else {
            emf = Persistence.createEntityManagerFactory(db);
            em = emf.createEntityManager();
        }
        
        StepDao dao = new StepDao();
        
        int max = 0;
        try {
            max = dao.getMaxSequenceByExecution(idExecution, em);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return max;
    }

    public static List<Execution> findExecutions(String db, Map properties) {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        if (properties != null) {
            emf = Persistence.createEntityManagerFactory(db, properties);
            em = emf.createEntityManager();
        } else {
            emf = Persistence.createEntityManagerFactory(db);
            em = emf.createEntityManager();
        }

        ExecutionDao dao = new ExecutionDao();

        List<Execution> executions = null;
        try {
            executions = dao.findAll(em);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return executions;
    }

    public static Screen saveScreen(Screen screen, String db, Map properties) {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        if (properties != null) {
            emf = Persistence.createEntityManagerFactory(db, properties);
            em = emf.createEntityManager();
        } else {
            emf = Persistence.createEntityManagerFactory(db);
            em = emf.createEntityManager();
        }

        ScreenDao dao = new ScreenDao();

        em.getTransaction().begin();
        try {
            screen = dao.save(screen, em);
        } catch (CRUDException e) {
            e.printStackTrace();
        }
        em.getTransaction().commit();
        em.close();
        emf.close();
        return screen;
    }

    public static Screen getScreenByScreenshot(String screenshot, String db, Map properties) {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        if (properties != null) {
            emf = Persistence.createEntityManagerFactory(db, properties);
            em = emf.createEntityManager();
        } else {
            emf = Persistence.createEntityManagerFactory(db);
            em = emf.createEntityManager();
        }

        ScreenDao dao = new ScreenDao();

        Screen screen = null;
        try {
            screen = dao.getByScreenshot(em, screenshot);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return screen;
    }

    public static Screen getScreenByHash(String hash, String db, Map properties) {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        if (properties != null) {
            emf = Persistence.createEntityManagerFactory(db, properties);
            em = emf.createEntityManager();
        } else {
            emf = Persistence.createEntityManagerFactory(db);
            em = emf.createEntityManager();
        }

        ScreenDao dao = new ScreenDao();

        Screen screen = null;
        try {
            screen = dao.getByHash(em, hash);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return screen;
    }
    
    public static DynGuiComponent getDynGuiComponentById(Long id, String db, Map properties) {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        if (properties != null) {
            emf = Persistence.createEntityManagerFactory(db, properties);
            em = emf.createEntityManager();
        } else {
            emf = Persistence.createEntityManagerFactory(db);
            em = emf.createEntityManager();
        }

        DynGuiComponentDao dao = new DynGuiComponentDao();

        DynGuiComponent component = null;
        try {
            component = dao.getById(id, em);
        } catch (CRUDException e) {
            e.printStackTrace();
        }
        return component;
    }

    public static List<Execution> findExecutionsByApp(String packageName, String versionpp, String db, Map properties) {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        if (properties != null) {
            emf = Persistence.createEntityManagerFactory(db, properties);
            em = emf.createEntityManager();
        } else {
            emf = Persistence.createEntityManagerFactory(db);
            em = emf.createEntityManager();
        }

        ExecutionDao dao = new ExecutionDao();

        List<Execution> executions = null;
        try {
            executions = dao.findByApp(packageName, versionpp, em);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return executions;
    }
    
    public static List<Step> findStepsByExecution(Long executionId, String db, Map properties) {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        if (properties != null) {
            emf = Persistence.createEntityManagerFactory(db, properties);
            em = emf.createEntityManager();
        } else {
            emf = Persistence.createEntityManagerFactory(db);
            em = emf.createEntityManager();
        }
        
        StepDao dao = new StepDao();
        
        List<Step> executions = null;
        try {
            executions = dao.findByExecutionId(executionId, em);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return executions;
    }
    
    public static Long findStepBySequence(Long executionId, int execSeq, String db, Map properties) {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        if (properties != null) {
            emf = Persistence.createEntityManagerFactory(db, properties);
            em = emf.createEntityManager();
        } else {
            emf = Persistence.createEntityManagerFactory(db);
            em = emf.createEntityManager();
        }
        
        StepDao dao = new StepDao();
        
        Long stepId = (long)0;
        try {
            Step tempStep = dao.findStepbyExecutionAndSequence(executionId, execSeq, em);
            stepId = tempStep.getId();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stepId;
    }

    public static void saveActivityData(List<ActivityFeature> features, String db) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(db);
        EntityManager em = emf.createEntityManager();

        ActivityFeatureDao dao = new ActivityFeatureDao();

        em.getTransaction().begin();
        try {
            for (ActivityFeature persist : features) {
                persist = dao.save(persist, em);
            }
        } catch (CRUDException e) {
            e.printStackTrace();
        }
        em.getTransaction().commit();
        em.close();
        emf.close();
    }

    public static App getAppByPackage(String packageName, String db) {
        return getAppByPackage(packageName, db, null);
    }

    public static App getAppByPackage(String packageName, String db, HashMap<String, String> properties) {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        if (properties != null) {
            emf = Persistence.createEntityManagerFactory(db, properties);
            em = emf.createEntityManager();
        } else {
            emf = Persistence.createEntityManagerFactory(db);
            em = emf.createEntityManager();
        }

        AppDao dao = new AppDao();
        App app = null;
        try {
            app = dao.getByNamePackage(packageName, em);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        em.close();
        emf.close();
        return app;
    }

    public static App getAppByPackageAndVersion(String packageName, String version, String db) {
        return getAppByPackageAndVersion(packageName, version, db, null);
    }

    public static App getAppByPackageAndVersion(String packageName, String version, String db,
            HashMap<String, String> properties) {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        if (properties != null) {
            emf = Persistence.createEntityManagerFactory(db, properties);
            em = emf.createEntityManager();
        } else {
            emf = Persistence.createEntityManagerFactory(db);
            em = emf.createEntityManager();
        }

        AppDao dao = new AppDao();
        App app = null;
        try {
            app = dao.getUniqueByPackage(packageName, version, em);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        em.close();
        emf.close();
        return app;
    }

    public static App getAppById(Long id, String db,
            HashMap<String, String> properties) {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        if (properties != null) {
            emf = Persistence.createEntityManagerFactory(db, properties);
            em = emf.createEntityManager();
        } else {
            emf = Persistence.createEntityManagerFactory(db);
            em = emf.createEntityManager();
        }

        AppDao dao = new AppDao();
        App app = null;
        try {
            app = dao.getUniqueById(id, em);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        em.close();
        emf.close();
        return app;
    }
    
    public static List<ActivityFeature> getActivityFeatsByAppId(Long appId, String db, Map properties) {

        EntityManagerFactory emf = null;
        EntityManager em = null;
        if (properties != null) {
            emf = Persistence.createEntityManagerFactory(db, properties);
            em = emf.createEntityManager();
        } else {
            emf = Persistence.createEntityManagerFactory(db);
            em = emf.createEntityManager();
        }

        ActivityFeatureDao dao = new ActivityFeatureDao();
        List<ActivityFeature> actFeats = new ArrayList<ActivityFeature>();
        try {
            actFeats = dao.getActivityFeatureListDerby(appId, em);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        em.close();
        emf.close();
        return actFeats;

    }

    public static DynGuiComponent getGuiComponent(long id, String db, Map properties) {
        EntityManagerFactory emf = null;
        EntityManager em = null;
        if (properties != null) {
            emf = Persistence.createEntityManagerFactory(db, properties);
            em = emf.createEntityManager();
        } else {
            emf = Persistence.createEntityManagerFactory(db);
            em = emf.createEntityManager();
        }

        DynGuiComponentDao dao = new DynGuiComponentDao();
        DynGuiComponent byId = null;

        try {
            byId = dao.getById(id, em);
        } catch (CRUDException e) {
            e.printStackTrace();
        }
        em.close();
        emf.close();
        return byId;
    }
    

    public static List<GuiComponent> getAllGuiComponents(String packageName) throws SQLException {
        List<GuiComponent> result = new ArrayList<GuiComponent>();
        AppDao appPackage = new AppDao();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(Constants.DB_SCHEMA);
        EntityManager em = emf.createEntityManager();

        App app = appPackage.getByNamePackage(packageName, em);

        // app.getClazzs().get(0).getGuiComponents();

        return result;
    }

}
