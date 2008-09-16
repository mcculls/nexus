package org.sonatype.nexus.jsecurity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StoppingException;
import org.sonatype.jsecurity.model.CPrivilege;
import org.sonatype.jsecurity.model.CRole;
import org.sonatype.jsecurity.model.CUser;
import org.sonatype.jsecurity.realms.tools.ConfigurationManager;
import org.sonatype.nexus.configuration.ConfigurationChangeEvent;
import org.sonatype.nexus.configuration.ConfigurationChangeListener;
import org.sonatype.nexus.configuration.ConfigurationException;
import org.sonatype.nexus.configuration.NotifiableConfiguration;
import org.sonatype.nexus.configuration.security.source.SecurityConfigurationSource;

/**
 *  @plexus.component
 */
public class DefaultNexusSecurity
    extends AbstractLogEnabled
    implements NexusSecurity, 
        NotifiableConfiguration
{
    /**
     * @plexus.requirement
     */
    private ConfigurationManager manager;
    
    /**
     * @plexus.requirement role-hint="file"
     */
    private SecurityConfigurationSource configSource;
    
    private List<ConfigurationChangeListener> listeners = new ArrayList<ConfigurationChangeListener>();
    
    public void startService()
        throws StartingException
    {
        // Do this simply to upgrade the configuration if necessary
        try
        {
            configSource.loadConfiguration();
            getLogger().info( "Security Configuration loaded properly." );
        }
        catch ( ConfigurationException e )
        {
            getLogger().fatalError( "Security Configuration is invalid!!!", e );
        }
        catch ( IOException e )
        {
            getLogger().fatalError( "Security Configuration is invalid!!!", e );
        }
        
        getLogger().info( "Started Nexus Security" );
    }
    
    public void stopService()
        throws StoppingException
    {
        getLogger().info( "Stopped Nexus Security" );
    }
    
    public void clearCache()
    {
        manager.clearCache();
    }

    public void createPrivilege( CPrivilege privilege )
    {
        manager.createPrivilege( privilege );
        save();
    }

    public void createRole( CRole role )
    {
        manager.createRole( role );
        save();
    }

    public void createUser( CUser user )
    {
        manager.createUser( user );
        save();
    }

    public void deletePrivilege( String id )
    {
        manager.deletePrivilege( id );
        save();
    }

    public void deleteRole( String id )
    {
        manager.deleteRole( id );
        save();
    }

    public void deleteUser( String id )
    {
        manager.deleteUser( id );
        save();
    }

    public String getPrivilegeProperty( CPrivilege privilege, String key )
    {
        return manager.getPrivilegeProperty( privilege, key );
    }

    public String getPrivilegeProperty( String id, String key )
    {
        return manager.getPrivilegeProperty( id, key );
    }
    
    public List<CPrivilege> listPrivileges()
    {
        return manager.listPrivileges();
    }
    
    public List<CRole> listRoles()
    {
        return manager.listRoles();
    }
    
    public List<CUser> listUsers()
    {
        return manager.listUsers();
    }

    public CPrivilege readPrivilege( String id )
    {
        return manager.readPrivilege( id );
    }

    public CRole readRole( String id )
    {
        return manager.readRole( id );
    }

    public CUser readUser( String id )
    {
        return manager.readUser( id );
    }

    public void save()
    {
        manager.save();
        notifyConfigurationChangeListeners();
    }

    public void updatePrivilege( CPrivilege privilege )
    {
        manager.updatePrivilege( privilege );
        save();
    }

    public void updateRole( CRole role )
    {
        manager.updateRole( role );
        save();
    }

    public void updateUser( CUser user )
    {
        manager.updateUser( user );
        save();
    }
    
    public void addConfigurationChangeListener( ConfigurationChangeListener listener )
    {
        listeners.add( listener );
    }
    
    public void notifyConfigurationChangeListeners()
    {
        notifyConfigurationChangeListeners( new ConfigurationChangeEvent( this ) );
    }
    
    public void notifyConfigurationChangeListeners( ConfigurationChangeEvent evt )
    {
        for ( ConfigurationChangeListener l : listeners )
        {
            try
            {
                if ( getLogger().isDebugEnabled() )
                {
                    getLogger().debug( "Notifying component about security config change: " + l.getClass().getName() );
                }

                l.onConfigurationChange( evt );
            }
            catch ( Exception e )
            {
                getLogger().info( "Unexpected exception in listener", e );
            }
        }
    }
    
    public void removeConfigurationChangeListener( ConfigurationChangeListener listener )
    {
        listeners.remove( listener );        
    }
    
    /**
     * @deprecated Use save()
     */
    public void saveConfiguration()
        throws IOException
    {
        // Don't use this for security
    }
}
