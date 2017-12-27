package es.plugins;

import es.plugins.watcher.interests.ActiveMQClient;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import javax.jms.JMSException;

/**
 * Unit test for simple App.
 */
public class ActiveMQClientTest
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ActiveMQClientTest(String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( ActiveMQClientTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testInit()
    {
        ActiveMQClient ac = ActiveMQClient.getInstance("10.0.0.14", "61616", "es:index");
        try {
            ac.publish("event:test", "test-payload");
        } catch (JMSException e) {
            e.printStackTrace();
        }
        assertTrue( true );
    }
}
