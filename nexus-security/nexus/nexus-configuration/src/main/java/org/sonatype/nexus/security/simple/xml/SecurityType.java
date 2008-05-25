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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

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
    private final KeyedCollection<String, UserType> users = new KeyedCollection<String, UserType>();

    @XmlElement(name = "role")
    private final KeyedCollection<String, RoleType> roles = new KeyedCollection<String, RoleType>();

    @XmlElement(name = "permission")
    private final KeyedCollection<String, PermissionType> permissions = new KeyedCollection<String, PermissionType>();

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
            for ( PermissionType permission : role.getPermissions().values() )
            {
                roleCopy.addPermission( permissionsCopy.get( permission ) );
            }
        }
        // Role - SubRoles
        for ( Map.Entry<RoleType, RoleType> entry : rolesCopy.entrySet() )
        {
            RoleType role = entry.getKey();
            RoleType roleCopy = entry.getValue();

            for ( RoleType subRole : role.getSubRoles().values() )
            {
                roleCopy.addSubRole( rolesCopy.get( subRole ) );
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
            for ( RoleType role : user.getRoles().values() )
            {
                userCopy.addRole( rolesCopy.get( role ) );
            }
        }
        for ( UserType userType : usersCopy.values() )
        {
            addUser( userType );
        }
    }

    public Map<String, UserType> getUsers()
    {
        return Collections.unmodifiableMap( users.toMap() );
    }

    public UserType getUser( String userName )
    {
        return users.toMap().get( userName );
    }

    public void addUser( UserType user )
    {
        user.setSecurityType( this );
        users.add( user );
    }

    public void removeUser( String userName )
    {
        users.toMap().remove( userName );
    }

    public Map<String, RoleType> getRoles()
    {
        return Collections.unmodifiableMap( roles.toMap() );
    }

    public RoleType getRole( String roleName )
    {
        return roles.toMap().get( roleName );
    }

    public void addRole( RoleType role )
    {
        role.setSecurityType( this );
        roles.add( role );
    }

    public void removeRole( String roleName )
    {
        // remove role from local list
        RoleType roleType = roles.toMap().remove( roleName );

        // remove role from user roles
        if ( roleType != null )
        {
            for ( UserType user : users )
            {
                user.removeRole( roleName );
            }
        }
    }

    public Map<String, PermissionType> getPermissions()
    {
        return Collections.unmodifiableMap( permissions.toMap() );
    }

    public void addPermission( PermissionType permission )
    {
        permission.setSecurityType( this );
        permissions.add( permission );
    }

    public void removePermission( String permissionId )
    {
        // remove permission from local list
        PermissionType permissionType = permissions.toMap().remove( permissionId );

        // remove permission from role permissions
        if ( permissionType != null )
        {
            for ( RoleType role : roles )
            {
                role.removePermission( permissionId );
            }
        }
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
            roles.toMap().putAll( user.getRoles() );
        }
        for ( RoleType role : roles )
        {
            syncRole( role );

        }
    }

    private void syncRole( RoleType role )
    {
        permissions.toMap().putAll( role.getPermissions() );
        for ( RoleType subRole : role.getSubRoles().values() )
        {
            if ( !roles.contains( subRole ) )
            {
                syncRole( subRole );
            }
        }
    }
}
