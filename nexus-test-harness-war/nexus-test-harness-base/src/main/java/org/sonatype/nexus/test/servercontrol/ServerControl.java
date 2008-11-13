package org.sonatype.nexus.test.servercontrol;

import java.util.List;

public interface ServerControl
{

    void startServer()
        throws Exception;

    void stopServer()
        throws Exception;

    List<String> getResetableFilesNames();

    void setupServer()
        throws Exception;

}
