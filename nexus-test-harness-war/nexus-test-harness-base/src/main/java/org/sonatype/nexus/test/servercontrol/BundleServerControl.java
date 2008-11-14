package org.sonatype.nexus.test.servercontrol;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.sonatype.appbooter.AbstractForkedAppBooter;
import org.sonatype.appbooter.ForkedAppBooter;
import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.sonatype.nexus.integrationtests.TestContainer;
import org.sonatype.nexus.test.utils.FileTestingUtils;

public class BundleServerControl
    implements ServerControl
{

    private static AbstractForkedAppBooter appBooter;

    public List<String> getResetableFilesNames()
    {
        return Arrays.asList( "nexus.xml", "security.xml" );
    }

    public void setupServer()
        throws Exception
    {
        // copy default nexus.xml
        File testConfigFile = AbstractNexusIntegrationTest.getResource( "default-config/nexus.xml" );
        File configDir = new File( AbstractNexusIntegrationTest.nexusWorkDir, "conf" );
        if ( !configDir.exists() )
        {
            configDir.mkdirs();
        }
        File outputFile = new File( configDir, "nexus.xml" );
        FileTestingUtils.fileCopy( testConfigFile, outputFile );
    }

    public void startServer()
        throws Exception
    {
        AbstractForkedAppBooter appBooter = getAppBooter();

        appBooter.setSleepAfterStart( 0 );
        appBooter.start();

    }

    private static AbstractForkedAppBooter getAppBooter()
        throws Exception
    {
        if ( appBooter == null )
        {
            appBooter =
                (AbstractForkedAppBooter) TestContainer.getInstance().lookup( ForkedAppBooter.ROLE,
                                                                              "TestForkedAppBooter" );
        }
        return appBooter;
    }

    public void stopServer()
        throws Exception
    {
        AbstractForkedAppBooter appBooter = getAppBooter();
        appBooter.stop();
    }

}
