/**
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2007-2012 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.events;

import java.io.IOException;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.sonatype.nexus.index.IndexerManager;
import org.sonatype.nexus.proxy.events.AbstractEventInspector;
import org.sonatype.nexus.proxy.events.EventInspector;
import org.sonatype.nexus.proxy.events.NexusStoppedEvent;
import org.sonatype.plexus.appevents.Event;

/**
 * Catches Nexus shutdown event and cleanly stops the IndexManager
 * 
 * @author bdemers
 */
@Component( role = EventInspector.class, hint = "LuceneIndexerNexusStoppedEventInspector" )
public class IndexerNexusStoppedEventInspector
    extends AbstractEventInspector
{

    @Requirement
    private IndexerManager indexerManager;

    protected IndexerManager getIndexerManager()
    {
        return indexerManager;
    }

    public boolean accepts( Event<?> evt )
    {
        // listen for STORE, CACHE, DELETE only
        return ( NexusStoppedEvent.class.isAssignableFrom( evt.getClass() ) );
    }

    public void inspect( Event<?> evt )
    {
        try
        {
            indexerManager.shutdown( false );
        }
        catch ( IOException e )
        {
            getLogger().error( "Error while stopping IndexerManager:", e );
        }
    }
}
