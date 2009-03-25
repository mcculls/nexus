package org.sonatype.nexus.test.utils;

import org.junit.Test;
import org.sonatype.appbooter.ForkedAppBooter;
import org.sonatype.nexus.integrationtests.TestContainer;

public class TestUnforkedAppBooter
{

    @Test
    public void start()
        throws Exception
    {
        UnforkedAppBooter booter =
            (UnforkedAppBooter) TestContainer.getInstance().lookup( ForkedAppBooter.ROLE, "TestUnforkedAppBooter" );
        booter.start();

        Thread.sleep( 100000 );
    }
}
