/**
 * Copyright (c) 2008-2011 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://www.sonatype.com/products/nexus/attributions.
 *
 * This program is free software: you can redistribute it and/or modify it only under the terms of the GNU Affero General
 * Public License Version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License Version 3
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License Version 3 along with this program.  If not, see
 * http://www.gnu.org/licenses.
 *
 * Sonatype Nexus (TM) Open Source Version is available from Sonatype, Inc. Sonatype and Sonatype Nexus are trademarks of
 * Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation. M2Eclipse is a trademark of the Eclipse Foundation.
 * All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.proxy.maven.uid;

import org.apache.maven.index.artifact.Gav;
import org.sonatype.nexus.proxy.item.RepositoryItemUid;
import org.sonatype.nexus.proxy.item.uid.Attribute;
import org.sonatype.nexus.proxy.maven.MavenRepository;

/**
 * Boolean Attribute that returns true if UID represents a path in Maven repository, and path obeys Maven repository
 * layout and points to a snapshot artifact. So, the path points to a POM (is Artifact when packaging is POM!), to main
 * or secondary (with classifier) artifact. If this attribute returns true, the {@link IsMavenArtifactAttribute} returns
 * true too.
 * 
 * @author cstamas
 */
public class IsMavenSnapshotArtifactAttribute
    implements Attribute<Boolean>
{
    @Override
    public Boolean getValueFor( RepositoryItemUid subject )
    {
        return subject.getRepository().getRepositoryKind().isFacetAvailable( MavenRepository.class )
            && pathIsValidSnapshotGav( subject.getRepository().adaptToFacet( MavenRepository.class ), subject.getPath() );
    }

    protected boolean pathIsValidSnapshotGav( MavenRepository repository, String path )
    {
        final Gav gav = repository.getGavCalculator().pathToGav( path );

        return gav != null && gav.isSnapshot() && !gav.isHash() && !gav.isSignature();
    }
}
