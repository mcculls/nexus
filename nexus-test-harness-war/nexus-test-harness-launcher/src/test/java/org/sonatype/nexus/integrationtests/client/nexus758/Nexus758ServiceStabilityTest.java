package org.sonatype.nexus.integrationtests.client.nexus758;

import static org.sonatype.nexus.test.utils.NexusStateUtil.doClientStart;
import static org.sonatype.nexus.test.utils.NexusStateUtil.doClientStop;
import static org.sonatype.nexus.test.utils.NexusStateUtil.doHardStart;
import static org.sonatype.nexus.test.utils.NexusStateUtil.doHardStop;
import static org.sonatype.nexus.test.utils.NexusStateUtil.doSoftStart;
import static org.sonatype.nexus.test.utils.NexusStateUtil.doSoftStop;

import org.junit.BeforeClass;
import org.junit.Test;
import org.sonatype.nexus.test.servercontrol.ServerRemoteControl;

/**
 * Tests the Soft Start, Stop, Restart, and isNexusStarted methods in the rest-client.
 */
public class Nexus758ServiceStabilityTest
{
    @BeforeClass
    public static void init()
        throws Exception
    {
        ServerRemoteControl.setupServer();
    }

    @Test
    public void hardRestarts()
        throws Exception
    {
        // this could be done using a for, but I wanna to know how may times it run just looking to stack trace
        // 1
        doHardStart();
        doHardStop( );

        // 2
        doHardStart();
        doHardStop( );

        // 3
        doHardStart();
        doHardStop( );

        // 4
        doHardStart();
        doHardStop( );

        // 5
        doHardStart();
        doHardStop( );

        // 6
        doHardStart();
        doHardStop( );

        // 7
        doHardStart();
        doHardStop( );

        // 8
        doHardStart();
        doHardStop( );

        // 9
        doHardStart();
        doHardStop( );

        // 10
        doHardStart();
        doHardStop( );

    }

    @Test
    public void softRestarts()
        throws Exception
    {
        doHardStart();

        doSoftStop();

        // 1
        doSoftStart();
        doSoftStop();

        // 2
        doSoftStart();
        doSoftStop();

        // 3
        doSoftStart();
        doSoftStop();

        // 4
        doSoftStart();
        doSoftStop();

        // 5
        doSoftStart();
        doSoftStop();

        // 6
        doSoftStart();
        doSoftStop();

        // 7
        doSoftStart();
        doSoftStop();

        // 8
        doSoftStart();
        doSoftStop();

        // 9
        doSoftStart();
        doSoftStop();

        // 10
        doSoftStart();
        doSoftStop();

        doSoftStart();
        doHardStop( );
    }

    @Test
    public void clientRestarts()
        throws Exception
    {
        doHardStart();

        doClientStop();

        // 1
        doClientStart();
        doClientStop();

        // 2
        doClientStart();
        doClientStop();

        // 3
        doClientStart();
        doClientStop();

        // 4
        doClientStart();
        doClientStop();

        // 5
        doClientStart();
        doClientStop();

        // 6
        doClientStart();
        doClientStop();

        // 7
        doClientStart();
        doClientStop();

        // 8
        doClientStart();
        doClientStop();

        // 9
        doClientStart();
        doClientStop();

        // 10
        doClientStart();
        doClientStop();

        doClientStart();
        doHardStop( );
    }

}
