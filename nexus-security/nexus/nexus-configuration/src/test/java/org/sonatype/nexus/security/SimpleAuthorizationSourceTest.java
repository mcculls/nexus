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

import junit.framework.TestCase;
import org.sonatype.nexus.security.simple.xml.SecurityType;
import org.sonatype.nexus.security.simple.xml.SecurityXmlUtil;

import java.net.URL;

public class SimpleAuthorizationSourceTest extends TestCase
{
    private AuthorizationSource authorizationSource;

    public void testDeveloperUser() throws Exception
    {
        User user = new SimpleUser( "dain" );
        assertTrue( authorizationSource.check( user, new RestPermission( "/repositories/cheese", "GET" ) ) );
        assertTrue( !authorizationSource.check( user, new RestPermission( "/repositories/cheese", "PUT" ) ) );
        assertTrue( !authorizationSource.check( user, new RestPermission( "/repository_index/cheese", "GET" ) ) );
        assertTrue( !authorizationSource.check( user, new RestPermission( "/unknown", "GET" ) ) );
    }

    public void testAdminUser() throws Exception
    {
        User user = new SimpleUser( "jason" );
        assertTrue( authorizationSource.check( user, new RestPermission( "/repositories/cheese", "GET" ) ) );
        assertTrue( authorizationSource.check( user, new RestPermission( "/repositories/cheese", "DELETE" ) ) );
        assertTrue( authorizationSource.check( user, new RestPermission( "/repositories/cheese", "PUT" ) ) );
        assertTrue( authorizationSource.check( user, new RestPermission( "/repository_index/cheese", "GET" ) ) );
        assertTrue( !authorizationSource.check( user, new RestPermission( "/unknown", "GET" ) ) );
    }

    public void testDeveloperRole() throws Exception
    {
        assertTrue( authorizationSource.check( "developer", new RestPermission( "/repositories/cheese", "GET" ) ) );
        assertTrue( !authorizationSource.check( "developer", new RestPermission( "/repositories/cheese", "PUT" ) ) );
        assertTrue( !authorizationSource.check( "developer", new RestPermission( "/repository_index/cheese", "GET" ) ) );
        assertTrue( !authorizationSource.check( "developer", new RestPermission( "/unknown", "GET" ) ) );
    }

    public void testAdminRole() throws Exception
    {
        assertTrue( authorizationSource.check( "admin", new RestPermission( "/repositories/cheese", "GET" ) ) );
        assertTrue( authorizationSource.check( "admin", new RestPermission( "/repositories/cheese", "DELETE" ) ) );
        assertTrue( authorizationSource.check( "admin", new RestPermission( "/repositories/cheese", "PUT" ) ) );
        assertTrue( authorizationSource.check( "admin", new RestPermission( "/repository_index/cheese", "GET" ) ) );
        assertTrue( !authorizationSource.check( "admin", new RestPermission( "/unknown", "GET" ) ) );
    }

    protected void setUp() throws Exception
    {
        super.setUp();

        URL resource = getClass().getClassLoader().getResource( "META-INF/nexus/security.xml" );
        assertNotNull("resource is null", resource);
        SecurityType securityType = SecurityXmlUtil.readSecurity( resource );

        authorizationSource = new SimpleAuthorizationSource( securityType, getClass().getClassLoader() );
    }
}