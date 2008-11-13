package org.sonatype.nexus.test.servercontrol;

import java.io.File;
import java.io.IOException;
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
    implements ServerControl
{

    private static Server server;

    public List<String> getResetableFilesNames()
    {
        return Arrays.asList( "nexus.xml", "security.xml" );
    }

    public void setupServer()
        throws Exception
    {
        // copy default nexus.xml
        File testConfigFile = AbstractNexusIntegrationTest.getResource( "default-config/nexus.xml" );
        File outputFile =
            new File( AbstractNexusIntegrationTest.nexusBaseDir + "/"
                + AbstractNexusIntegrationTest.RELATIVE_WORK_CONF_DIR, "nexus.xml" );
        FileTestingUtils.fileCopy( testConfigFile, outputFile );
    }

    public void startServer()
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

    public void stopServer()
        throws Exception
    {
        Server server = getServer();
        server.stop();
    }

}
