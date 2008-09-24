package org.sonatype.nexus.proxy.events;

public interface ItemInspector
{
    boolean isHandled( RepositoryItemEvent e );

    void handleEvent( RepositoryItemEvent e );
}
