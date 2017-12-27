/**
 * IndexChanges
 */
package es.plugins.watcher.beans;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import es.plugins.watcher.interests.ActiveMQClient;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.slf4j.Slf4jESLoggerFactory;
import org.elasticsearch.index.engine.Engine;
import org.elasticsearch.index.engine.Engine.Create;
import org.elasticsearch.index.engine.Engine.Delete;
import org.elasticsearch.index.engine.Engine.Index;
import org.elasticsearch.index.indexing.IndexingOperationListener;
import es.plugins.watcher.beans.Change.Type;
import es.plugins.watcher.util.ConcurrentCircularBuffer;

import javax.jms.JMSException;

public class IndexChanges extends IndexingOperationListener {
    String indexName;
    private final static ESLogger LOGGER = Slf4jESLoggerFactory.getLogger(IndexChanges.class.getName());

    long lastChange;
    ConcurrentCircularBuffer<Change> changes;
    AtomicInteger shardCount;
    List<IndexChangeWatcher> watchers;

    public IndexChanges(String indexName, int capacity) {
        this.indexName = indexName;
        this.lastChange = System.currentTimeMillis();
        this.changes = new ConcurrentCircularBuffer<Change>(Change.class, capacity);
        this.shardCount = new AtomicInteger();
        this.watchers = new CopyOnWriteArrayList<IndexChangeWatcher>();
    }

    public void removeWatcher(IndexChangeWatcher watcher) {
        watchers.remove(watcher);
    }

    public void addWatcher(IndexChangeWatcher watcher) {
        watchers.add(watcher);
    }

    public void triggerWatchers() {
        triggerWatchers(new Change());
    }

    private void triggerWatchers(Change c) {
        for (IndexChangeWatcher watch : watchers) {
            watch.setIndexName(indexName);
            watch.setChange(c);
            watch.permit();
        }
    }

    /**
     * Publish events to MQ Service.
     *
     * @param c
     */
    private void triggerPublishEvent(Change c) throws JMSException {
        LOGGER.debug("Get Event:" + c.getId());
        if(c.getIndex().startsWith("faq_")) {
            ActiveMQClient.getInstance().publish(ActiveMQClient.getInstance().getTopic(), c.toString());
        }
    }

    public long getLastChangeMillis() {
        return lastChange;
    }

    public long getLastChange() {
        return lastChange;
    }

    public List<Change> getChanges() {
        List<Change> snapshot = changes.snapshot();

        return snapshot;
    }

    public int addShard() {
        return shardCount.incrementAndGet();
    }

    public int removeShard() {
        return shardCount.decrementAndGet();
    }

    protected void addChange(Change c) {
        lastChange = c.getTimestamp();
        changes.add(c);
        // Currently, comment out watchers
        // triggerWatchers(c);
        try {
            triggerPublishEvent(c);
        } catch (JMSException e) {
            LOGGER.warn("triggerPublishEvent", e);
        }
    }

    @Override
    public void postCreate(Create create) {
        Change change = new Change();
        change.setIndex(indexName);
        change.setId(create.id());
        change.setType(Type.CREATE);
        change.setVersion(create.version());
        change.setTimestamp(System.currentTimeMillis());
        change.setPayload(create.parsedDoc().source().toUtf8());

        addChange(change);
    }

    @Override
    public void postDelete(Delete delete) {
        LOGGER.debug("postDelete", delete.toString());
        Change change = new Change();
        change.setIndex(indexName);
        change.setId(delete.id());
        change.setType(Type.DELETE);
        change.setVersion(delete.version());
        change.setTimestamp(System.currentTimeMillis());

        addChange(change);
    }

    @Override
    public void postIndex(Index index) {
        LOGGER.debug("postIndex", index.toString());
        Change change = new Change();
        change.setIndex(indexName);
        change.setId(index.id());
        change.setType(Type.INDEX);
        change.setVersion(index.version());
        change.setTimestamp(System.currentTimeMillis());
        change.setPayload(index.parsedDoc().source().toUtf8());

        addChange(change);
    }
}