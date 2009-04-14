package org.sonatype.nexus.mock;

import junit.framework.TestCase;
import org.sonatype.nexus.mock.rest.MockHelper;

public class NexusTestCase extends TestCase {
    private static MockNexusEnvironment env;

    @Override
    protected void setUp() throws Exception {
        if (env == null) {
            env = new MockNexusEnvironment(12345, "/nexus");
            env.start();

            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        env.stop();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }));
        }
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            MockHelper.checkAssertions();
        } finally {
            // always clear out the mocks
            MockHelper.clearMocks();
        }
    }
}
