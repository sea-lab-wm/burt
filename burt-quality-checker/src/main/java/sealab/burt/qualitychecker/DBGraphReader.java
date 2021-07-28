package sealab.burt.qualitychecker;

import lombok.extern.slf4j.Slf4j;
import sealab.burt.qualitychecker.graph.AppGraphInfo;
import sealab.burt.qualitychecker.graph.GraphDataSource;
import sealab.burt.qualitychecker.graph.db.DBUtils;
import sealab.burt.qualitychecker.graph.db.GraphGenerator;

import javax.persistence.EntityManager;
import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;

@Deprecated
public @Slf4j
class DBGraphReader {

    private static final ConcurrentHashMap<String, AppGraphInfo> graphs = new ConcurrentHashMap<>();

    public static AppGraphInfo getGraph(String appName, String appVersion) throws Exception {
        AppGraphInfo graph = graphs.get(getKey(appName, appVersion));
        if (graph == null) {
            readGraph(appName, appVersion);
            graph = graphs.get(getKey(appName, appVersion));
        }
        return graph;
    }

    private static String getKey(String app, String appVersion) {
        return MessageFormat.format("{0}-{1}", app, appVersion);
    }

    private static void readGraph(String appName, String appVersion) throws Exception {
        String key = getKey(appName, appVersion);
        log.debug("Reading graph from DB for " + key);
        EntityManager em = DBUtils.createEntityManager(DBUtils.DEFAULT_EM);
        GraphGenerator generator = new GraphGenerator();
        AppGraphInfo appInfo = generator.generateGraph(em, appName, appVersion, GraphDataSource.CS);
        graphs.put(key, appInfo);
    }
}
