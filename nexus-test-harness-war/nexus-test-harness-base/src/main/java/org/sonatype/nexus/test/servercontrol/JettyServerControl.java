package org.sonatype.nexus.test.servercontrol;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.sonatype.nexus.test.utils.FileTestingUtils;
import org.sonatype.nexus.test.utils.TestProperties;

public class JettyServerControl
    extends AbstractServerControl
    implements ServerControl
{

    private static Server server;

    private ClassLoader previousLoader;

    public List<String> getResetableFilesNames()
    {
        return Arrays.asList( "nexus.xml", "security.xml" );
    }

    @Override
    public void setupServer()
        throws Exception
    {
        if ( isSetup )
        {
            return;
        }

        super.setupServer();

        copyToBaseDirConf( "plexus.war.properties", "plexus.properties" );

    }

    public synchronized void startServer()
        throws Exception
    {
        Server server = getServer();
        server.start();
    }

    private Server getServer()
        throws IOException
    {
        if ( server == null )
        {
            previousLoader = Thread.currentThread().getContextClassLoader();
            URLClassLoader cl = new URLClassLoader( new URL[0], previousLoader );
            Thread.currentThread().setContextClassLoader( cl );
            int port = TestProperties.getInteger( "nexus.application.port" );
            String war = TestProperties.getString( "nexus.war.dir" );
            war = new File( war ).getCanonicalPath();

            server = new Server();
            Connector connector = new SelectChannelConnector();
            connector.setPort( port );
            connector.setHost( "127.0.0.1" );
            server.addConnector( connector );

            WebAppContext wac = new WebAppContext();
            wac.setContextPath( "/nexus" );
            wac.setWar( war );
            server.setHandler( wac );
            server.setStopAtShutdown( true );
        }
        return server;
    }

    public synchronized void stopServer()
        throws Exception
    {
        if ( server == null )
        {
            return;
        }
        try
        {
            Server server = getServer();
            server.stop();
            Thread.currentThread().setContextClassLoader( previousLoader );
        }
        finally
        {
            JettyServerControl.server = null;
            previousLoader = null;
        }
    }

    @Override
    protected File getBaseDirConf()
    {
        File configDir = new File( AbstractNexusIntegrationTest.nexusWarDir, "WEB-INF" );
        return configDir;
    }

    @Override
    protected File getRuntimeDirConf()
    {
        File configDir = getBaseDirConf();
        return configDir;
    }

    @Override
    protected File getWorkDirConf()
    {
        File configDir = new File( AbstractNexusIntegrationTest.nexusWorkDir, "conf" );
        return configDir;
    }

}
