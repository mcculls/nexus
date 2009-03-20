/**
 * Sonatype Nexus (TM) Open Source Version.
 * Copyright (c) 2008 Sonatype, Inc. All rights reserved.
 * Includes the third-party code listed at http://nexus.sonatype.org/dev/attributions.html
 * This program is licensed to you under Version 3 only of the GNU General Public License as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License Version 3 for more details.
 * You should have received a copy of the GNU General Public License Version 3 along with this program.
 * If not, see http://www.gnu.org/licenses/.
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc.
 * "Sonatype" and "Sonatype Nexus" are trademarks of Sonatype, Inc.
 */
package org.sonatype.nexus.seleniumtests;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.server.SeleniumServer;
import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.sonatype.nexus.test.utils.TestProperties;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

public class UserSeleniumTest
    extends AbstractNexusIntegrationTest
{
    private static Logger log = Logger.getLogger( UserSeleniumTest.class );

    private Selenium selenium;

    @BeforeClass
    public static void init()
        throws Exception
    {
         SeleniumServer.main( new String[] { "-port", TestProperties.getString( "selenium-server-port" ) } );
//        final SeleniumServer seleniumProxy = new SeleniumServer( TestProperties.getInteger( "selenium-server-port" ) );
//        seleniumProxy.start();
    }

    @Before
    public void setUp()
        throws Exception
    {
        selenium =
            new DefaultSelenium( "localhost", TestProperties.getInteger( "selenium-server-port" ), "firefox",
                                 nexusBaseUrl );
        selenium.start();
        selenium.setContext( this.getClass().getName() );
    }

    @Test
    public void findNexus()
        throws Throwable
    {
        selenium.open( "/" );
        selenium.type( "q", "sonatype nexus" );
        selenium.click( "btnG" );
        selenium.waitForPageToLoad( "30000" );
        selenium.click( "link=Download Nexus" );
        selenium.waitForPageToLoad( "30000" );
        Assert.assertTrue( selenium.isTextPresent( "Release 1.3.1" ) );
    }

    @After
    public void tearDown()
        throws Exception
    {
        selenium.stop();
    }

}
