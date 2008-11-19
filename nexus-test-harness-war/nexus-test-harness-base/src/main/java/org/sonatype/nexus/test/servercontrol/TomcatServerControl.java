package org.sonatype.nexus.test.servercontrol;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Embedded;
import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.sonatype.nexus.test.utils.TestProperties;

public class TomcatServerControl
    extends AbstractServerControl
    implements ServerControl
{

    private ClassLoader previousLoader;

    private static Embedded embedded;

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

    public void startServer()
        throws Exception
    {
        synchronized ( TomcatServerControl.class )
        {
            Embedded emb = getEmbedded();
            emb.start();
        }
    }

    private Embedded getEmbedded()
        throws IOException, Exception
    {
        if ( embedded == null )
        {
            int port = TestProperties.getInteger( "nexus.application.port" );
            String war = TestProperties.getString( "nexus.war.dir" );
            File warFile = new File( war ).getCanonicalFile();
            war = warFile.getCanonicalPath();
            String tomcat = "C:/temp/nexus/apache-tomcat-6.0.18";

            // Create an embedded server
            embedded = new Embedded();
            embedded.setAwait( true );
            embedded.setCatalinaHome( tomcat );
            embedded.setCatalinaBase( tomcat );

            // Create an engine
            Engine engine = embedded.createEngine();
            engine.setDefaultHost( "localhost" );

            // Create a default virtual host
            Host host = embedded.createHost( "localhost", tomcat + "/webapps" );
            engine.addChild( host );

            // Create the nexus context
            Context context = embedded.createContext( "/nexus", war );
            host.addChild( context );

            // Install the assembled container hierarchy
            embedded.addEngine( engine );

            // Assemble and install a default HTTP connector
            Connector connector = embedded.createConnector( "127.0.0.1", port, false );
            embedded.addConnector( connector );

            embedded.initialize();
        }
        return embedded;
    }

    public synchronized void stopServer()
        throws Exception
    {
        if ( embedded == null )
        {
            return;
        }
        synchronized ( TomcatServerControl.class )
        {
            try
            {
                Embedded server = getEmbedded();
                server.stop();
                Thread.currentThread().setContextClassLoader( previousLoader );
            }
            finally
            {
                TomcatServerControl.embedded = null;
                previousLoader = null;
            }
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
