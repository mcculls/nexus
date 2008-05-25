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

import junit.framework.TestCase;

import java.net.URL;

public class SecurityExamplesTest extends TestCase
{
    private SecurityType securityType;

    public void testAddUser()
    {
        // precondition
        assertEquals( 2, securityType.getUsers().size() );
        assertNull( securityType.getUsers().get( "brian" ) );

        // change
        UserType brian = new UserType( "brian", "nairb" );
        securityType.addUser( brian );

        // post-condition
        assertEquals( 3, securityType.getUsers().size() );
        assertSame( brian, securityType.getUsers().get( "brian" ) );
    }

    public void testRemoveUser()
    {
        // precondition
        assertEquals( 2, securityType.getUsers().size() );
        assertNotNull( securityType.getUsers().get( "dain" ) );

        // change
        securityType.removeUser( "dain" );

        // post-condition
        assertEquals( 1, securityType.getUsers().size() );
        assertNull( securityType.getUsers().get( "dain" ) );
    }

    public void testAddRole()
    {
        // precondition
        assertEquals( 2, securityType.getRoles().size() );
        assertNull( securityType.getRoles().get( "guest" ) );

        // change
        RoleType guest = new RoleType( "guest" );
        securityType.addRole( guest );

        // post-condition
        assertEquals( 3, securityType.getRoles().size() );
        assertSame( guest, securityType.getRoles().get( "guest" ) );
    }

    public void testRemoveRole()
    {
        // precondition
        assertEquals( 2, securityType.getRoles().size() );
        assertNotNull( securityType.getRoles().get( "admin" ) );
        assertNotNull( securityType.getUsers().get("jason").getRoles().get("admin"));

        // change
        securityType.removeRole( "admin" );

        // post-condition
        assertEquals( 1, securityType.getRoles().size() );
        assertNull( securityType.getRoles().get( "admin" ) );

        // role is removed from user.getRoles() also
        assertNull( securityType.getUsers().get("jason").getRoles().get("admin"));
    }

    public void testAddPermission()
    {
        // precondition
        assertEquals( 4, securityType.getPermissions().size() );
        assertNull( securityType.getPermissions().get( "update-artifact" ) );

        // change
        PermissionType updateArtifact = new PermissionType( "update-artifact", "org.sonatype.nexus.security.RestPermission", "/repositories/*", "POST" );
        securityType.addPermission( updateArtifact );

        // post-condition
        assertEquals( 5, securityType.getPermissions().size() );
        assertSame( updateArtifact, securityType.getPermissions().get( "update-artifact" ) );
    }

    public void testRemovePermission()
    {
        // precondition
        assertEquals( 4, securityType.getPermissions().size() );
        assertNotNull( securityType.getPermissions().get( "delete-artifact" ) );
        assertNotNull( securityType.getRole("admin").getPermissions().get("delete-artifact"));

        // change
        securityType.removePermission( "delete-artifact" );

        // post-condition
        assertEquals( 3, securityType.getPermissions().size() );
        assertNull( securityType.getPermissions().get( "delete-artifact" ) );

        // permission is removed from role.getPermissions() also
        assertNull( securityType.getRole("admin").getPermissions().get("delete-artifact"));
    }

    public void testAddUserRole()
    {
        // precondition
        assertEquals( 2, securityType.getRoles().size() );
        assertNull( securityType.getRoles().get( "guest" ) );

        // change
        RoleType guest = new RoleType( "guest" );
        securityType.getUser("dain").addRole( guest );

        // post-condition
        assertEquals( 3, securityType.getRoles().size() );
        assertSame( guest, securityType.getRoles().get( "guest" ) );
        assertSame( guest, securityType.getUser("dain").getRoles().get("guest"));
    }

    public void testRemoveUserRole()
    {
        // precondition
        assertEquals( 2, securityType.getRoles().size() );
        assertNotNull( securityType.getRoles().get( "developer" ) );
        assertNotNull( securityType.getUser("dain").getRoles().get("developer"));

        // change
        securityType.getUser( "dain" ).removeRole( "developer" );

        // post-condition
        assertEquals( 2, securityType.getRoles().size() );
        assertNotNull( securityType.getRoles().get( "developer" ) );
        assertNull( securityType.getUser("dain").getRoles().get("developer"));
    }

    public void testAddRolePermission()
    {
        // precondition
        assertEquals( 4, securityType.getPermissions().size() );
        assertNull( securityType.getPermissions().get( "update-artifact" ) );

        // change
        PermissionType updateArtifact = new PermissionType( "update-artifact", "org.sonatype.nexus.security.RestPermission", "/repositories/*", "POST" );
        securityType.getRole("admin").addPermission( updateArtifact );

        // post-condition
        assertEquals( 5, securityType.getPermissions().size() );
        assertSame( updateArtifact, securityType.getPermissions().get( "update-artifact" ) );
        assertSame( updateArtifact, securityType.getRole("admin").getPermissions().get("update-artifact"));
    }

    public void testRemoveRolePermission()
    {
        // precondition
        assertEquals( 4, securityType.getPermissions().size() );
        assertNotNull( securityType.getPermissions().get( "delete-artifact" ) );
        assertNotNull( securityType.getRole("admin").getPermissions().get("delete-artifact"));

        // change
        securityType.getRole( "admin" ).removePermission( "delete-artifact" );

        // post-condition
        assertEquals( 4, securityType.getPermissions().size() );
        assertNotNull( securityType.getPermissions().get( "delete-artifact" ) );
        assertNull( securityType.getRole("admin").getPermissions().get("delete-artifact"));
    }

    protected void setUp() throws Exception
    {
        super.setUp();

        URL resource = getClass().getClassLoader().getResource( "META-INF/nexus/security.xml" );
        assertNotNull( "resource is null", resource );
        securityType = SecurityXmlUtil.readSecurity( resource );
    }
}
