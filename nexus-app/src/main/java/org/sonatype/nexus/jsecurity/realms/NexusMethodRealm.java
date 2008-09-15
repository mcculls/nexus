package org.sonatype.nexus.jsecurity.realms;

import org.sonatype.jsecurity.realms.MethodRealm;
import org.sonatype.nexus.configuration.ConfigurationChangeEvent;
import org.sonatype.nexus.configuration.ConfigurationChangeListener;

/**
 * @plexus.component role="org.jsecurity.realm.Realm" role-hint="TargetRealm"
 */
public class NexusMethodRealm
    extends MethodRealm
    implements ConfigurationChangeListener
{
    public void onConfigurationChange( ConfigurationChangeEvent evt )
    {
        this.clearCache();
    }
}
