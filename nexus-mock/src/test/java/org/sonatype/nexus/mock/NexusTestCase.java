package org.sonatype.nexus.mock;

import junit.framework.TestCase;

public class NexusTestCase extends TestCase {
    private static MockNexusEnvironment env;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

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
        super.tearDown();
    }
}
