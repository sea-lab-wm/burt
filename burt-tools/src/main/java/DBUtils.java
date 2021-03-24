import edu.semeru.android.core.entity.model.fusion.Execution;
import edu.semeru.android.core.entity.model.fusion.Step;
import edu.semeru.android.core.helpers.device.DeviceHelper;
import edu.semeru.android.core.service.PersistDataService;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;

public class DBUtils {

    public static final String DEFAULT_EM = "CrashScope-Bug-Reporoduction";

    public static EntityManager createEntityManager() {
        return createEntityManager(DEFAULT_EM);
    }

    public static EntityManager createEntityManager(String dbEntityManager) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(dbEntityManager);
        return emf.createEntityManager();
    }

    public static Execution getExecution(String executionName) {
        Execution execution = PersistDataService.getExecutionByExecutionType(executionName, DEFAULT_EM,
                new HashMap<String, String>());
        if (execution == null) {
            execution = new Execution();
            execution.setExecutionType(executionName);
        }
        return execution;
    }

    public static Step getOpenAppStep(String packageName, Execution execution, int sequence) {
        Step openApp = new Step();
        openApp.setAction(DeviceHelper.OPEN_APP);
        openApp.setExecution(execution);
        openApp.setTextEntry(packageName);
        openApp.setSequenceStep(sequence);
        return openApp;
    }
}
