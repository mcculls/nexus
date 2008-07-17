package org.sonatype.nexus.integrationtests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class IntegrationTestSuite
{

    public static Test suite()
    {
        TestSuite suite = new TestSuite( "Nexus Integration Tests" );
        //$JUnit-BEGIN$

//        suite.addTestSuite( SimpleNexusTest.class );
        
        //$JUnit-END$
        return suite;
    }

}
