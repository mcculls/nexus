package org.sonatype.nexus.jsecurity.realms;

import org.sonatype.jsecurity.realms.MethodRealm;
import org.sonatype.nexus.configuration.ConfigurationChangeEvent;

public abstract class AbstractNexusRealm
    extends MethodRealm
        implements NexusRealm
{
    public void onConfigurationChange( ConfigurationChangeEvent evt )
    {
        this.clearCache();
    }
}
