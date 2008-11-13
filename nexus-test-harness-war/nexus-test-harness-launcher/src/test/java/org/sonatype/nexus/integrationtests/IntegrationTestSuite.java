package org.sonatype.nexus.integrationtests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.sonatype.nexus.test.servercontrol.BundleServerControl;
import org.sonatype.nexus.test.servercontrol.ServerRemoteControl;
import org.sonatype.nexus.test.utils.NexusStateUtil;

@RunWith( Suite.class )
@SuiteClasses( { IntegrationTestSuiteClasses.class, IntegrationTestSuiteClassesSecurity.class } )
public class IntegrationTestSuite
{
    @BeforeClass
    public static void beforeSuite()
        throws Exception
    {
        ServerRemoteControl.setInstance( new BundleServerControl() );

        ServerRemoteControl.setupServer();

        NexusStateUtil.doHardStart();

        NexusStateUtil.doSoftStop();
    }

    @AfterClass
    public static void afterSuite()
        throws Exception
    {
        NexusStateUtil.doHardStop( false );
    }

}
