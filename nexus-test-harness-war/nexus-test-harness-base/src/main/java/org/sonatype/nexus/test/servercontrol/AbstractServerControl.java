package org.sonatype.nexus.test.servercontrol;

import java.io.File;
import java.io.IOException;

import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.sonatype.nexus.test.utils.FileTestingUtils;

public abstract class AbstractServerControl
{

    protected static boolean isSetup = false;

    public AbstractServerControl()
    {
        super();
    }

    protected void copyToWorkDirConf( String configFileName )
        throws IOException
    {
        File testConfigFile = AbstractNexusIntegrationTest.getResource( "default-config/" + configFileName );
        File configDir = getWorkDirConf();
        createDir( configDir );
        File outputFile = new File( configDir, configFileName );
        FileTestingUtils.fileCopy( testConfigFile, outputFile );
    }

    protected abstract File getWorkDirConf();

    protected void copyToRuntimeDirConf( String configFileName )
        throws IOException
    {
        File testConfigFile = AbstractNexusIntegrationTest.getResource( "default-config/" + configFileName );
        File configDir = getRuntimeDirConf();
        createDir( configDir );
        File outputFile = new File( configDir, configFileName );
        FileTestingUtils.fileCopy( testConfigFile, outputFile );
    }

    protected abstract File getRuntimeDirConf();

    protected void copyToBaseDirConf( String configFileName, String finalFileName )
        throws IOException
    {
        File plexusConfig = AbstractNexusIntegrationTest.getResource( "default-config/" + configFileName );
        File baseConfigDir = getBaseDirConf().getCanonicalFile();
        createDir( baseConfigDir );
        FileTestingUtils.fileCopy( plexusConfig, new File( baseConfigDir, finalFileName ) );
    }

    protected void copyToBaseDirConf( String configFileName )
        throws IOException
    {
        copyToBaseDirConf( configFileName, configFileName );
    }

    private void createDir( File dir )
    {
        if ( !dir.exists() )
        {
            dir.mkdirs();
        }
    }

    protected abstract File getBaseDirConf();

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

}