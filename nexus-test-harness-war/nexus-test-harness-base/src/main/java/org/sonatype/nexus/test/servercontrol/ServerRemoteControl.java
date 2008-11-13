package org.sonatype.nexus.test.servercontrol;

import java.util.List;

public class ServerRemoteControl
{

    public static ServerControl getInstance()
    {
        if ( instance == null )
        {
            instance = new BundleServerControl();
        }
        return instance;
    }

    public static void setInstance( ServerControl instance )
    {
        ServerRemoteControl.instance = instance;
    }

    private static ServerControl instance;

    public static void startServer()
        throws Exception
    {
        getInstance().startServer();
    }

    public static void stopServer()
        throws Exception
    {
        getInstance().stopServer();
    }

    public static List<String> getResetableFilesNames()
    {
        return getInstance().getResetableFilesNames();
    }

    public static void setupServer()
        throws Exception
    {
        getInstance().setupServer();
    }

}
