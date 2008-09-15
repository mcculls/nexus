package org.sonatype.nexus.jsecurity.locators;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.ServiceLocator;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Serviceable;
import org.jsecurity.realm.Realm;
import org.sonatype.jsecurity.locators.RealmLocator;
import org.sonatype.nexus.configuration.application.NexusConfiguration;
import org.sonatype.nexus.jsecurity.realms.NexusMethodRealm;
import org.sonatype.nexus.jsecurity.realms.NexusTargetRealm;

/**
 * The nexus implementation of the realm locator
 * will load from the nexus.xml file
 * 
 * @plexus.component role="org.sonatype.jsecurity.locators.RealmLocator"
 */
public class NexusRealmLocator
    extends AbstractLogEnabled
    implements
    RealmLocator, Contextualizable, Serviceable
{
    private static final String PLEXUS_SECURITY_XML_FILE = "security-xml-file";
    
    /**
     * @plexus.requirement
     */
    NexusConfiguration configuration;
    
    private ServiceLocator container;
    
    public void contextualize( Context context )
        throws ContextException
    {
        // Simply add the path of the config file
        context.put( PLEXUS_SECURITY_XML_FILE, configuration.getWorkingDirectory() + "/conf/security.xml" );
    }
    
    public void service( ServiceLocator locator )
    {
        this.container = locator;
    }
    
    public List<Realm> getRealms()
    {
        // Until nexus.xml code is done, simply returning hardcoded realms
        
        List<Realm> realms = new ArrayList<Realm>();
        
        try
        {
            realms.add( ( Realm ) container.lookup( NexusMethodRealm.class.getName() ) );
            realms.add( ( Realm ) container.lookup( NexusTargetRealm.class.getName() ) );
        }
        catch ( ComponentLookupException e )
        {
            getLogger().error( "Unable to lookup security realms", e );
        }
        
        return realms;
    }

}
