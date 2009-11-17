/*
 * Nexus: RESTLight Client
 * Copyright (C) 2009 Sonatype, Inc.                                                                                                                          
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
package org.sonatype.nexus.restlight.stage.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.sonatype.nexus.artifact.IllegalArtifactCoordinateException;
import org.sonatype.nexus.restlight.common.RESTLightClientException;
import org.sonatype.nexus.restlight.stage.StageClient;
import org.sonatype.nexus.restlight.stage.StageRepository;
import org.sonatype.nexus.restlight.stage.it.utils.Coordinate;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class QueryClosedReposForUserIT
    extends AbstractStageClientIT
{
    @Test
    public void queryClosedRepositoryForUser()
        throws IOException, RESTLightClientException, IllegalArtifactCoordinateException
    {
        String profileId = createStagingProfile( "all-closed-repos-for-user" );

        File jarFile = getArtifactFile( "dummy-1.0.jar" );

        Coordinate coord = new Coordinate( "dummy.test.group", "dummy", "1.0", "jar" );
        deployTo( profileId, jarFile, coord );

        StageClient client = new StageClient( getBaseNexusUrl(), getExpectedUser(), getExpectedPassword() );

        client.finishRepositoryForUser( coord.getGroupId(), coord.getArtifactId(), coord.getVersion(), "Description" );

        List<StageRepository> repos = client.getClosedStageRepositoriesForUser();

        assertNotNull( repos );
        assertEquals( 1, repos.size() );
    }
}
