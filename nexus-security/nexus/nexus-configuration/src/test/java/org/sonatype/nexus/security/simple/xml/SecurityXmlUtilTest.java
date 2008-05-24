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
import java.io.StringWriter;
import java.util.Map;

public class SecurityXmlUtilTest extends TestCase
{
    public void testRead() throws Exception
    {
        URL resource = getClass().getClassLoader().getResource( "META-INF/nexus/security.xml" );
        assertNotNull( "resource is null", resource );
        SecurityType securityType = SecurityXmlUtil.readSecurity( resource );

        // Users
        Map<String,UserType> users = securityType.getUsers();
        assertEquals( 2, users.size() );
        UserType dain = users.get( "dain" );
        assertNotNull("dain is null", dain);
        assertEquals( "naid", dain.getPassword() );
        UserType jason = users.get( "jason" );
        assertNotNull("jason is null", jason );
        assertEquals( "nosaj", jason.getPassword() );

        // Roles
        Map<String, RoleType> roles = securityType.getRoles();
        assertEquals( 2, roles.size() );
        RoleType admin = roles.get( "admin" );
        assertNotNull("admin is null", admin);
        RoleType developer = roles.get( "developer" );
        assertNotNull("developer is null", developer );

        // Permissions
        Map<String, PermissionType> permissions = securityType.getPermissions();
        assertEquals( 4, permissions.size() );
        PermissionType readArtifact = permissions.get( "read-artifact" );
        assertNotNull("readArtifact is null", readArtifact);
        PermissionType deleteArtifact = permissions.get( "delete-artifact" );
        assertNotNull("deleteArtifact is null", deleteArtifact );
        PermissionType deployArtifact = permissions.get( "deploy-artifact" );
        assertNotNull("deployArtifact is null", deployArtifact );
        PermissionType indexRepository = permissions.get( "index-repository" );
        assertNotNull("indexRepository is null", indexRepository );

        // User Roles
        Map<String, RoleType> dainRoles = dain.getRoles();
        assertEquals( 1, dainRoles.size() );
        RoleType dainDeveloperRole = dainRoles.get( "developer" );
        assertNotNull("dainDeveloperRole is null", dainDeveloperRole);
        assertSame( developer, dainDeveloperRole );
        Map<String, RoleType> jasonRoles = jason.getRoles();
        assertEquals( 1, jasonRoles.size() );
        RoleType jasonAdminRole = jasonRoles.get( "admin" );
        assertNotNull("jasonAdminRole is null", jasonAdminRole);
        assertSame( admin, jasonAdminRole );

        // Role SubRole
        Map<String, RoleType> developerSubRoles = developer.getSubRoles();
        assertEquals( 0, developerSubRoles.size() );
        Map<String, RoleType> adminSubRoles = admin.getSubRoles();
        assertEquals( 1, adminSubRoles.size() );
        RoleType adminDeveloperSubRole = adminSubRoles.get( "developer" );
        assertNotNull("adminDeveloperSubRole is null", adminDeveloperSubRole);
        assertSame( developer, adminDeveloperSubRole );

        // Role Permission
        Map<String, PermissionType> adminPermissions = admin.getPermissions();
        assertEquals( 3, adminPermissions.size() );
        PermissionType adminDeleteArtifact = adminPermissions.get( "delete-artifact" );
        assertNotNull("adminDeleteArtifact is null", adminDeleteArtifact );
        assertSame( deleteArtifact, adminDeleteArtifact );
    }

    public void testWrite() throws Exception
    {
        SecurityType securityType = new SecurityType();
        UserType userType = new UserType( "dain", "naid" );
        securityType.addUser( userType );
        StringWriter stringWriter = new StringWriter();
        SecurityXmlUtil.writeSecurity( securityType, stringWriter );
    }
}
