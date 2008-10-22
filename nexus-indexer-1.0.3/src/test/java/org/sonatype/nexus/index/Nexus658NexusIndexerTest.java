/*******************************************************************************
 * Copyright (c) 2007-2008 Sonatype Inc
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eugene Kuleshov (Sonatype)
 *    Tam�s Cserven�k (Sonatype)
 *    Brian Fox (Sonatype)
 *    Jason Van Zyl (Sonatype)
 *******************************************************************************/
package org.sonatype.nexus.index;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.lucene.search.Query;

/** http://issues.sonatype.org/browse/NEXUS-13 */
public class Nexus658NexusIndexerTest
    extends AbstractNexusIndexerTest
{
    protected File repo = new File( getBasedir(), "src/test/nexus-658" );

    @Override
    protected void prepareNexusIndexer( NexusIndexer nexusIndexer )
        throws Exception
    {
        context = nexusIndexer.addIndexingContext(
            "nexus-658",
            "nexus-658",
            repo,
            indexDir,
            null,
            null,
            NexusIndexer.DEFAULT_INDEX );
        nexusIndexer.scan( context );
    }

    public void testSearchFlat()
        throws Exception
    {
        Query q = nexusIndexer.constructQuery( ArtifactInfo.GROUP_ID, "org.sonatype.nexus" );

        Collection<ArtifactInfo> r = nexusIndexer.searchFlat( q );

        assertEquals( 3, r.size() );

        List<ArtifactInfo> list = new ArrayList<ArtifactInfo>( r );

        ArtifactInfo ai = null;

        // g a v p c #1
        ai = list.get( 0 );

        assertEquals( "org.sonatype.nexus", ai.groupId );
        assertEquals( "nexus-webapp", ai.artifactId );
        assertEquals( "1.0.0-SNAPSHOT", ai.version );
        assertEquals( "jar", ai.packaging );
        assertEquals( null, ai.classifier );
        assertEquals( ArtifactAvailablility.PRESENT, ai.sourcesExists );
        assertEquals( "nexus-658", ai.repository );

        // g a v p c #2
        ai = list.get( 1 );

        assertEquals( "org.sonatype.nexus", ai.groupId );
        assertEquals( "nexus-webapp", ai.artifactId );
        assertEquals( "1.0.0-SNAPSHOT", ai.version );
        assertEquals( "tar.gz", ai.packaging );
        assertEquals( "bundle", ai.classifier );
        assertEquals( ArtifactAvailablility.NOT_AVAILABLE, ai.sourcesExists );
        assertEquals( "nexus-658", ai.repository );

        // g a v p c #3
        ai = list.get( 2 );

        assertEquals( "org.sonatype.nexus", ai.groupId );
        assertEquals( "nexus-webapp", ai.artifactId );
        assertEquals( "1.0.0-SNAPSHOT", ai.version );
        assertEquals( "zip", ai.packaging );
        assertEquals( "bundle", ai.classifier );
        assertEquals( ArtifactAvailablility.NOT_AVAILABLE, ai.sourcesExists );
        assertEquals( "nexus-658", ai.repository );
    }

}
