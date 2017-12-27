/**
 * @author - Hai Liang Wang
 */
package es.plugins.watcher;

import es.plugins.watcher.beans.IndexChanges;
import es.plugins.watcher.interests.ActiveMQClient;
import org.elasticsearch.common.component.AbstractComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.slf4j.Slf4jESLoggerFactory;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.indexing.IndexingOperationListener;
import org.elasticsearch.index.shard.IndexShard;
import org.elasticsearch.indices.IndicesLifecycle.Listener;
import org.elasticsearch.indices.IndicesService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Service extends AbstractComponent {
    private IndicesService indicesService;
    private IndexingOperationListener indexingOperationListener;

    private ESLogger LOGGER;
    private Map<String, IndexChanges> changes;
    private static final String SETTING_HISTORY_SIZE = "changes.history.size";
    private static final String SETTING_ACTIVEMQ_URI = "watcher.activemq.uri";
    private static final String SETTING_ACTIVEMQ_PORT = "watcher.activemq.port";
    private static final String SETTING_ACTIVEMQ_TOPIC = "watcher.activemq.topic";

    @Inject
    public Service(final Settings indexSettings,
                   final IndicesService indicesService) {
        super(indexSettings);
        LOGGER = Slf4jESLoggerFactory.getLogger(Service.class.getName());
        this.indicesService = indicesService;
        this.changes = new ConcurrentHashMap<String, IndexChanges>();
        registerLifecycleHandler();
    }

    private void registerLifecycleHandler() {
        indicesService.indicesLifecycle().addListener(new Listener() {
            @Override
            public void afterIndexShardStarted(IndexShard indexShard) {
                if (indexShard.routingEntry().primary()) {
                    LOGGER.info("Inject new Index with:" + indexShard.shardId().index().name());
                    LOGGER.info("ActiveMQ URI:" + settings.get(SETTING_ACTIVEMQ_URI, ""));
                    ActiveMQClient.getInstance(settings.get(SETTING_ACTIVEMQ_URI, "localhost"), settings.get(SETTING_ACTIVEMQ_PORT, "61616"), settings.get(SETTING_ACTIVEMQ_TOPIC, "SimilaritySync"));
                    IndexChanges indexChanges;
                    synchronized (changes) {
                        indexChanges = changes.get(indexShard.shardId().index().name());
                        if (indexChanges == null) {
                            indexChanges = new IndexChanges(indexShard.shardId().index().name(), settings.getAsInt(SETTING_HISTORY_SIZE, 100));
                            changes.put(indexShard.shardId().index().name(), indexChanges);
                        }
                    }
                    indexChanges.addShard();
                    indexShard.indexingService().addListener(indexChanges);
                }
            }
        });

    }
}
