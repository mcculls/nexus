package org.sonatype.nexus.test.servercontrol;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.sonatype.appbooter.AbstractForkedAppBooter;
import org.sonatype.appbooter.ForkedAppBooter;
import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.sonatype.nexus.integrationtests.TestContainer;

public class BundleServerControl extends AbstractServerControl
    implements ServerControl
{

    private static AbstractForkedAppBooter appBooter;

    public List<String> getResetableFilesNames()
    {
        return Arrays.asList( "nexus.xml", "security.xml" );
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

    @Override
    protected File getWorkDirConf()
    {
        File configDir = new File( AbstractNexusIntegrationTest.nexusWorkDir, "conf" );
        return configDir;
    }

    @Override
    protected File getRuntimeDirConf()
    {
        File configDir = new File( AbstractNexusIntegrationTest.nexusBaseDir, "runtime/apps/nexus/conf" );
        return configDir;
    }

    @Override
    protected File getBaseDirConf()
    {
        File baseConfigDir = new File( AbstractNexusIntegrationTest.nexusBaseDir, "conf" );
        return baseConfigDir;
    }

}
