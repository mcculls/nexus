package org.sonatype.nexus.jsecurity.realms;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.codehaus.plexus.util.StringUtils;
import org.jsecurity.authz.Permission;
import org.jsecurity.authz.permission.WildcardPermission;
import org.sonatype.jsecurity.model.CPrivilege;
import org.sonatype.jsecurity.realms.SecurityXmlRealm;
import org.sonatype.nexus.Nexus;
import org.sonatype.nexus.configuration.ConfigurationChangeEvent;
import org.sonatype.nexus.configuration.ConfigurationChangeListener;
import org.sonatype.nexus.proxy.NoSuchRepositoryGroupException;
import org.sonatype.nexus.proxy.repository.Repository;

/**
 * @plexus.component role="org.jsecurity.realm.Realm" role-hint="TargetRealm"
 *
 */
public class NexusTargetRealm
    extends SecurityXmlRealm
    implements ConfigurationChangeListener
{
    /**
     * @plexus.requirement
     */
    private Nexus nexus;
    
    @Override
    protected Set<Permission> getPermissions( String privilegeId )
    {
        CPrivilege privilege = getConfigurationManager().readPrivilege( privilegeId );
        
        if ( privilege != null )
        {            
            String repositoryTarget = getConfigurationManager().getPrivilegeProperty( privilege, "repositoryTarget" );
            String method = getConfigurationManager().getPrivilegeProperty( privilege, "method" );
            String repositoryId = getConfigurationManager().getPrivilegeProperty( privilege, "repositoryId" );
            String repositoryGroupId = getConfigurationManager().getPrivilegeProperty( privilege, "repositoryGroupId" );
         
            StringBuilder basePermString = new StringBuilder();
            
            basePermString.append( "nexus:target:" );            
            basePermString.append( repositoryTarget );            
            basePermString.append( ":" );
            
            StringBuilder postPermString = new StringBuilder();
            
            if ( StringUtils.isEmpty( method ) )
            {
                postPermString.append( "*" );
            }
            else
            {
                postPermString.append( method );
            }
            
            if ( !StringUtils.isEmpty( repositoryId ) )
            {
                return Collections.singleton( ( Permission ) new WildcardPermission( basePermString + repositoryId + postPermString ) );
            }
            else if ( !StringUtils.isEmpty( repositoryGroupId ) )
            {
                try
                {
                    Set<Permission> permissions = new HashSet<Permission>();
                    
                    List<Repository> repositories = nexus.getRepositoryGroup( repositoryGroupId );
                    
                    for ( Repository repository : repositories )
                    {
                        WildcardPermission permission = new WildcardPermission( basePermString + repository.getId() + postPermString );

                        permissions.add( permission );
                    }
                    
                    return permissions;
                }
                catch ( NoSuchRepositoryGroupException e )
                {
                    // If there is no such group you don't have permission to it
                }
            }
            else
            {
                return Collections.singleton( ( Permission ) new WildcardPermission( basePermString + "*" + postPermString ) );
            }
        }       

        return Collections.emptySet();
    }
    
    public void onConfigurationChange( ConfigurationChangeEvent evt )
    {
        this.clearCache();
    }
}
