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
package org.sonatype.nexus.security.simple.xml;

import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "securityType", propOrder = {
    "users",
    "roles",
    "permissions"
    })
@XmlRootElement(name = "security")
public class SecurityType
{
    @XmlElement(name = "user")
    private final Set<UserType> users = new LinkedHashSet<UserType>();

    @XmlElement(name = "role")
    private final Set<RoleType> roles = new LinkedHashSet<RoleType>();

    @XmlElement(name = "permission")
    private final Set<PermissionType> permissions = new LinkedHashSet<PermissionType>();

    public SecurityType()
    {
    }

    public SecurityType( SecurityType securityType )
    {
        // Permissions
        IdentityHashMap<PermissionType, PermissionType> permissionsCopy = new IdentityHashMap<PermissionType, PermissionType>( securityType.permissions.size() );
        for ( PermissionType permission : securityType.permissions )
        {
            PermissionType permissionCopy = new PermissionType( permission.getPermissionId(), permission.getClazz(), permission.getName(), permission.getActions() );
            permissionsCopy.put( permission, permissionCopy );
        }
        permissions.addAll( permissionsCopy.values() );

        // Roles
        IdentityHashMap<RoleType, RoleType> rolesCopy = new IdentityHashMap<RoleType, RoleType>( securityType.roles.size() );
        for ( RoleType role : securityType.roles )
        {
            RoleType roleCopy = new RoleType( role.getRoleName() );
            rolesCopy.put( role, roleCopy );

            // Role - Permissions
            Set<PermissionType> permissions = role.getPermissions();
            for ( PermissionType permission : permissions )
            {
                roleCopy.getPermissions().add( permissionsCopy.get( permission ) );
            }
        }
        // Role - SubRoles
        for ( Map.Entry<RoleType, RoleType> entry : rolesCopy.entrySet() )
        {
            RoleType role = entry.getKey();
            RoleType roleCopy = entry.getValue();

            Set<RoleType> subRoles = role.getSubRoles();
            for ( RoleType subRole : subRoles )
            {
                roleCopy.getSubRoles().add( rolesCopy.get( subRole ) );
            }
        }
        roles.addAll( rolesCopy.values() );

        // User
        IdentityHashMap<UserType, UserType> usersCopy = new IdentityHashMap<UserType, UserType>( securityType.users.size() );
        for ( UserType user : securityType.users )
        {
            UserType userCopy = new UserType( user.getUserName(), user.getPassword() );
            usersCopy.put( user, userCopy );

            // User - Roles
            Set<RoleType> roles = user.getRoles();
            for ( RoleType role : roles )
            {
                userCopy.getRoles().add( rolesCopy.get( role ) );
            }
        }
        users.addAll( usersCopy.values() );
    }

    public Set<UserType> getUsers()
    {
        return users;
    }

    public void addUser( UserType user )
    {
        users.add( user );
    }

    public void removeUser( UserType user )
    {
        users.remove( user );
    }

    public Set<RoleType> getRoles()
    {
        return roles;
    }

    public void addRole( RoleType role )
    {
        roles.add( role );
    }

    public void removeRole( RoleType role )
    {
        roles.remove( role );
    }

    public Set<PermissionType> getPermissions()
    {
        return permissions;
    }

    public void addPermission( PermissionType permission )
    {
        permissions.add( permission );
    }

    public void removePermission( PermissionType permission )
    {
        permissions.remove( permission );
    }

    boolean beforeMarshal( Marshaller marshaller, Object parent )
    {
        syncModel();
        return true;
    }

    public void syncModel()
    {
        for ( UserType user : users )
        {
            roles.addAll( user.getRoles() );
        }
        for ( RoleType role : roles )
        {
            syncRole( role );

        }
    }

    private void syncRole( RoleType role )
    {
        permissions.addAll( role.getPermissions() );
        for ( RoleType subRole : role.getSubRoles() )
        {
            if ( !roles.contains( subRole ) )
            {
                syncRole( subRole );
            }
        }
    }
}
