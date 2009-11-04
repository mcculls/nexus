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
package org.sonatype.nexus.proxy;

import org.codehaus.plexus.util.FileUtils;
import org.sonatype.jettytestsuite.ServletServer;
import org.sonatype.nexus.proxy.item.StorageFileItem;

public class SimplePushTest
    extends AbstractProxyTestEnvironment
{
    private M2TestsuiteEnvironmentBuilder jettyTestsuiteEnvironmentBuilder;

    @Override
    protected EnvironmentBuilder getEnvironmentBuilder()
        throws Exception
    {
        ServletServer ss = (ServletServer) lookup( ServletServer.ROLE );
        this.jettyTestsuiteEnvironmentBuilder = new M2TestsuiteEnvironmentBuilder( ss );
        return jettyTestsuiteEnvironmentBuilder;
    }

    public void testSimplePush()
        throws Exception
    {
        ResourceStoreRequest request =
            new ResourceStoreRequest( "/repositories/inhouse/activemq/activemq-core/1.2/activemq-core-1.2.jar", true );
        StorageFileItem item =
            (StorageFileItem) getRootRouter()
                .retrieveItem(
                    new ResourceStoreRequest( "/repositories/repo1/activemq/activemq-core/1.2/activemq-core-1.2.jar",
                        false ) );

        getRootRouter().storeItem( request, item.getInputStream(), null );

        assertTrue( FileUtils.contentEquals( getFile( getRepositoryRegistry().getRepository( "repo1" ),
            "/activemq/activemq-core/1.2/activemq-core-1.2.jar" ), getFile( getRepositoryRegistry().getRepository(
            "inhouse" ), "/activemq/activemq-core/1.2/activemq-core-1.2.jar" ) ) );
    }

    public void testSimplePushNEXUS2956_1()
        throws Exception
    {
        ResourceStoreRequest request =
            new ResourceStoreRequest( "/repositories/inhouse/activemq/activemq-core/1.2/./activemq-core-1.2.jar", true );

        StorageFileItem item =
            (StorageFileItem) getRootRouter()
                .retrieveItem(
                    new ResourceStoreRequest( "/repositories/repo1/activemq/activemq-core/1.2/activemq-core-1.2.jar",
                        false ) );

        getRootRouter().storeItem( request, item.getInputStream(), null );

        assertTrue( FileUtils.contentEquals( getFile( getRepositoryRegistry().getRepository( "repo1" ),
            "/activemq/activemq-core/1.2/activemq-core-1.2.jar" ), getFile( getRepositoryRegistry().getRepository(
            "inhouse" ), "/activemq/activemq-core/1.2/activemq-core-1.2.jar" ) ) );
    }

    public void testSimplePushNEXUS2956_2()
        throws Exception
    {
        ResourceStoreRequest request =
            new ResourceStoreRequest( "/repositories/inhouse/activemq/activemq-core/1.2/./aFile.txt", true );

        StorageFileItem item =
            (StorageFileItem) getRootRouter()
                .retrieveItem(
                    new ResourceStoreRequest( "/repositories/repo1/activemq/activemq-core/1.2/activemq-core-1.2.jar",
                        false ) );

        getRootRouter().storeItem( request, item.getInputStream(), null );

        assertTrue( FileUtils.contentEquals( getFile( getRepositoryRegistry().getRepository( "repo1" ),
            "/activemq/activemq-core/1.2/activemq-core-1.2.jar" ), getFile( getRepositoryRegistry().getRepository(
            "inhouse" ), "/activemq/activemq-core/1.2/aFile.txt" ) ) );
    }

}
