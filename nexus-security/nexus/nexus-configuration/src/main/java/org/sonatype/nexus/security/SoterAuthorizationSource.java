/*
 * Nexus: Maven Repository Manager
 * Copyright (C) 2008 Sonatype Inc.                                                                                                                          
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
package org.sonatype.nexus.security;

import org.soter.rbac.model.RbacType;
import org.soter.rbac.model.RbacXmlUtil;
import org.soter.rbac.model.RoleType;
import org.soter.rbac.model.RoleRefType;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.security.Permission;
import java.util.Collections;
import java.util.List;

/**
 * Authorization implementation using soter
 *
 * @plexus.component role="org.sonatype.nexus.security.AuthorizationSource" role-hint="soter"
 */
public class SoterAuthorizationSource implements AuthorizationSource
{
    private RbacType rbac;

    public SoterAuthorizationSource()
        throws Exception
    {
        rbac = new RbacType( );
        for ( URL url : Collections.list( getClass().getClassLoader().getResources( "META-INF/nexus/permissions.xml" ) ) )
        {
            Reader reader = new InputStreamReader(url.openStream());
            RbacType permissions = RbacXmlUtil.loadRbac(reader);
            rbac.mergeData( permissions );
        }
        for ( URL url : Collections.list( getClass().getClassLoader().getResources( "META-INF/nexus/roles.xml" ) ) )
        {
            Reader reader = new InputStreamReader(url.openStream());
            RbacType permissions = RbacXmlUtil.loadRbac(reader);
            rbac.mergeData( permissions );
        }
        rbac.start(getClass().getClassLoader(), null);
    }

    public SoterAuthorizationSource( RbacType rbac )
    {
        this.rbac = rbac;
    }

    public boolean check( User user, Permission permission, String scope )
    {
        List<RoleType> roles = rbac.getUserRoles( user.getUsername() );
        for ( RoleType role : roles )
        {
            if ( role.implies( permission, scope ) )
            {
                return true;
            }
        }
        return false;
    }

    public boolean check( String roleName, String roleScope, Permission permission, String scope )
    {
        RoleType role = rbac.getRole( new RoleRefType( roleName, roleScope ) );
        return role != null && role.implies( permission, scope );
    }
}
