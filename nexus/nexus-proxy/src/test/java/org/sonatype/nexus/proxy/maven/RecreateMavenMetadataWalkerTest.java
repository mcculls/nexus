/*
 * Nexus: Maven Repository Manager
 * Copyright (C) 2008 Sonatype Inc.                                                                                                                          
 * 
 * This file is part of Nexus.                                                                                                                                  
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 */

package org.sonatype.nexus.proxy.maven;

import org.sonatype.jettytestsuite.ServletServer;
import org.sonatype.nexus.proxy.AbstractProxyTestEnvironment;
import org.sonatype.nexus.proxy.EnvironmentBuilder;
import org.sonatype.nexus.proxy.M2TestsuiteEnvironmentBuilder;
import org.sonatype.nexus.proxy.ResourceStoreRequest;
import org.sonatype.nexus.proxy.item.StorageFileItem;
import org.sonatype.nexus.proxy.repository.Repository;
import org.sonatype.nexus.proxy.utils.StoreWalker;

/**
 * @author Juven Xu
 */
public class RecreateMavenMetadataWalkerTest
    extends AbstractProxyTestEnvironment
{

    private M2TestsuiteEnvironmentBuilder jettyTestsuiteEnvironmentBuilder;

    private Repository inhouse;

    private String[] releaseArtifactFiles = {
        "/junit/junit/3.8.1/junit-3.8.1.jar",
        "/junit/junit/3.8.1/junit-3.8.1.pom",
        "/junit/junit/3.8.2/junit-3.8.2.jar",
        "/junit/junit/3.8.2/junit-3.8.2.pom",
        "/junit/junit/4.0/junit-4.0.jar",
        "/junit/junit/4.0/junit-4.0.pom",
        "/junit/junit/4.4/junit-4.4.jar",
        "/junit/junit/4.4/junit-4.4.pom"};

    @Override
    protected EnvironmentBuilder getEnvironmentBuilder()
        throws Exception
    {
        ServletServer ss = (ServletServer) lookup( ServletServer.ROLE );
       
        this.jettyTestsuiteEnvironmentBuilder = new M2TestsuiteEnvironmentBuilder( ss );
       
        return jettyTestsuiteEnvironmentBuilder;
    }

    @Override
    public void setUp()
        throws Exception
    {

        super.setUp();

        // copy all release artifact fils from proxy repo4 to hosted inhouse repo
        for ( String releaseArtifactFile : releaseArtifactFiles )
        {
            Repository repo4 = getRepositoryRegistry().getRepository( "repo4" );

            inhouse = getRepositoryRegistry().getRepository( "inhouse" );

            StorageFileItem item = (StorageFileItem) repo4.retrieveItem( new ResourceStoreRequest(
                releaseArtifactFile,
                false ) );

            ResourceStoreRequest request = new ResourceStoreRequest( releaseArtifactFile, true );

            inhouse.storeItem( request, item.getInputStream(), null );
        }

    }

    public void testStoreWalker()
        throws Exception
    {
        StoreWalker storeWalker = new RecreateMavenMetadataWalker( inhouse, getLogger() );

        storeWalker.walk();

        assertNotNull( inhouse
            .retrieveItem( new ResourceStoreRequest( "/junit/junit/maven-metadata.xml", false ) ) );

    }

}
