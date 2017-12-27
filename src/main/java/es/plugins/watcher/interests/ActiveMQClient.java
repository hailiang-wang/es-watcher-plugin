package es.plugins.watcher.interests;

import javax.jms.*;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.slf4j.Slf4jESLoggerFactory;
import org.apache.commons.lang3.StringUtils;


public class ActiveMQClient {

    private static String uri;
    private static String port;
    private static String topic;
    private ConnectionFactory _connFactory = null;
    private Connection _conn = null;
    private static ActiveMQClient _instance = null;
    private final static ESLogger LOGGER = Slf4jESLoggerFactory.getLogger(ActiveMQClient.class.getName());

    public String getTopic() {
        return topic;
    }

    // Exists only to defeat instantiation.
    protected ActiveMQClient(final String uri, final String port, final String topic) throws Exception {
        this.uri = uri;
        this.port = port;
        this.topic = topic;
        final String brokerUrl = "tcp://" + this.uri + ":" + this.port;
        LOGGER.info("BrokerUrl" + brokerUrl + ", Topic:" + this.topic);
        this._connFactory = new ActiveMQConnectionFactory(brokerUrl);
        this._conn = this._connFactory.createConnection();
    }

    /**
     * Init service
     *
     * @param uri
     * @param port
     * @return
     */
    public static ActiveMQClient getInstance(final String uri, final String port, final String topic) {
        if (_instance == null) {
            try {
                _instance = new ActiveMQClient(uri, port, topic);
                LOGGER.info("Get init with " + uri + ":" + port);
            } catch (java.net.SocketException e) {
                LOGGER.error("Connection Error: SocketException", e);
            } catch (Exception e) {
                LOGGER.error("Can not get instance of ActiveMQ Client", e);
            }
        }
        return _instance;
    }

    /**
     * Retrieve Instance
     *
     * @return
     * @throws JMSException
     */
    public static ActiveMQClient getInstance() throws JMSException {
        if (_instance != null) {
            return _instance;
        } else if(StringUtils.isNotEmpty(ActiveMQClient.uri)){
            return ActiveMQClient.getInstance(ActiveMQClient.uri, ActiveMQClient.port, ActiveMQClient.topic);
        } else {
            throw new JMSException("Instantiation info missing.");
        }
    }

    private void _publish(final String topic, final String payload) throws JMSException {
        final Session sess = this._conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
        final Destination dest = sess.createTopic(topic);
        final MessageProducer prod = sess.createProducer(dest);
        final Message msg = sess.createTextMessage(payload);
        prod.send(msg);
        sess.close();
        LOGGER.debug("_publish done.");
    }

    /**
     * Send Message out
     *
     * @param payload
     * @throws JMSException
     */
    public void publish(final String topic, final String payload) throws JMSException {
        if (this._conn != null) {
            try {
                _publish(topic, payload);
            } catch (org.apache.activemq.ConnectionFailedException ex) {
                this._conn = this._connFactory.createConnection();
                _publish(topic, payload);
            }
        } else {
            throw new JMSException("Connection unavailable.");
        }
    }

    /**
     * Close Connection
     */
    public void close() {
        try {
            this._conn.close();
        } catch (JMSException e) {
            LOGGER.warn("Caught Exception when closing connection", e);
        }
    }

}
