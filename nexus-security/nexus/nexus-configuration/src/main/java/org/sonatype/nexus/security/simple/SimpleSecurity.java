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
package org.sonatype.nexus.security.simple;

import org.sonatype.nexus.security.AuthorizationSource;
import org.sonatype.nexus.security.User;

import java.security.Permission;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * Immutable implementation of AuthorizationSource.
 * @see org.sonatype.nexus.security.SimpleAuthorizationSource for thread safe mutable wrapper
 */
public class SimpleSecurity implements AuthorizationSource
{
    private final Map<String, SimpleUser> users;
    private final Map<String, SimpleRole> roles;

    public SimpleSecurity( Collection<SimpleUser> users, Collection<SimpleRole> roles )
    {
        Map<String, SimpleUser> usersMap = new TreeMap<String, SimpleUser>();
        for ( SimpleUser user : users )
        {
            usersMap.put( user.getUserName(), user );
        }
        this.users = Collections.unmodifiableMap( usersMap );

        Map<String, SimpleRole> rolesMap = new TreeMap<String, SimpleRole>();
        for ( SimpleRole role : roles )
        {
            rolesMap.put( role.getRoleName(), role );
        }
        this.roles = Collections.unmodifiableMap( rolesMap );
    }

    public boolean check( User user, Permission permission )
    {
        if ( user == null )
        {
            throw new NullPointerException( "user is null" );
        }
        if ( permission == null )
        {
            throw new NullPointerException( "permission is null" );
        }

        SimpleUser simpleUser = users.get( user.getUsername() );
        if ( simpleUser == null )
        {
            return false;
        }
        for ( SimpleRole role : simpleUser.getRoles() )
        {
            if ( role.implies( permission ) )
            {
                return true;
            }
        }
        return false;
    }

    public boolean check( String roleName, Permission permission )
    {
        if ( roleName == null )
        {
            throw new NullPointerException( "roleName is null" );
        }
        if ( permission == null )
        {
            throw new NullPointerException( "permission is null" );
        }


        SimpleRole simpleRole = roles.get( roleName );
        return simpleRole != null && simpleRole.implies( permission );
    }
}
