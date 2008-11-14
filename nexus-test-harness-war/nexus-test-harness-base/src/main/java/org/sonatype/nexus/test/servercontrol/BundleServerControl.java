package org.sonatype.nexus.test.servercontrol;

import java.io.File;
import java.io.IOException;
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

    private static boolean isSetup = false;

    public List<String> getResetableFilesNames()
    {
        return Arrays.asList( "nexus.xml", "security.xml" );
    }

    public void setupServer()
        throws Exception
    {
        if ( isSetup )
        {
            return;
        }

        // copy
        copyToBaseDirConf( "plexus.properties" );
        copyToBaseDirConf( "log4j.properties" );

        copyToWorkDirConf( "nexus.xml" );

        copyToRuntimeDirConf( "log4j.properties" );

        isSetup = true;
    }

    private void copyToWorkDirConf( String configFileName )
        throws IOException
    {
        File testConfigFile = AbstractNexusIntegrationTest.getResource( "default-config/" + configFileName );
        File configDir = new File( AbstractNexusIntegrationTest.nexusWorkDir, "conf" );
        if ( !configDir.exists() )
        {
            configDir.mkdirs();
        }
        File outputFile = new File( configDir, configFileName );
        FileTestingUtils.fileCopy( testConfigFile, outputFile );
    }

    private void copyToRuntimeDirConf( String configFileName )
        throws IOException
    {
        File testConfigFile = AbstractNexusIntegrationTest.getResource( "default-config/" + configFileName );
        File configDir = new File( AbstractNexusIntegrationTest.nexusBaseDir, "runtime/apps/nexus/conf" );
        if ( !configDir.exists() )
        {
            configDir.mkdirs();
        }
        File outputFile = new File( configDir, configFileName );
        FileTestingUtils.fileCopy( testConfigFile, outputFile );
    }

    private void copyToBaseDirConf( String configFileName )
        throws IOException
    {
        File plexusConfig = AbstractNexusIntegrationTest.getResource( "default-config/" + configFileName );
        File baseConfigDir = new File( AbstractNexusIntegrationTest.nexusBaseDir, "conf" );
        if ( !baseConfigDir.exists() )
        {
            baseConfigDir.mkdirs();
        }
        FileTestingUtils.fileCopy( plexusConfig, new File( baseConfigDir, configFileName ) );
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
