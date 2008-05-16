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

public class SoterAuthorizationSourceTest extends TestCase
{
    private AuthorizationSource authorizationSource;

    public void testDeveloperUser() throws Exception
    {
        User user = new SimpleUser( "dain" );
        assertTrue( authorizationSource.check( user, new RestPermission( "/repositories/cheese", "GET" ), "nexus" ) );
        assertTrue( !authorizationSource.check( user, new RestPermission( "/repositories/cheese", "PUT" ), "nexus" ) );
        assertTrue( !authorizationSource.check( user, new RestPermission( "/repository_index/cheese", "GET" ), "nexus" ) );
        assertTrue( !authorizationSource.check( user, new RestPermission( "/unknown", "GET" ), "nexus" ) );
    }

    public void testAdminUser() throws Exception
    {
        User user = new SimpleUser( "jason" );
        assertTrue( authorizationSource.check( user, new RestPermission( "/repositories/cheese", "GET" ), "nexus" ) );
        assertTrue( authorizationSource.check( user, new RestPermission( "/repositories/cheese", "DELETE" ), "nexus" ) );
        assertTrue( authorizationSource.check( user, new RestPermission( "/repositories/cheese", "PUT" ), "nexus" ) );
        assertTrue( authorizationSource.check( user, new RestPermission( "/repository_index/cheese", "GET" ), "nexus" ) );
        assertTrue( !authorizationSource.check( user, new RestPermission( "/unknown", "GET" ), "nexus" ) );
    }

    public void testDeveloperRole() throws Exception
    {
        assertTrue( authorizationSource.check( "sonatype-developer", "sonatype", new RestPermission( "/repositories/cheese", "GET" ), "nexus" ) );
        assertTrue( !authorizationSource.check( "sonatype-developer", "sonatype", new RestPermission( "/repositories/cheese", "PUT" ), "nexus" ) );
        assertTrue( !authorizationSource.check( "sonatype-developer", "sonatype", new RestPermission( "/repository_index/cheese", "GET" ), "nexus" ) );
        assertTrue( !authorizationSource.check( "sonatype-developer", "sonatype", new RestPermission( "/unknown", "GET" ), "nexus" ) );
    }

    public void testAdminRole() throws Exception
    {
        assertTrue( authorizationSource.check( "sonatype-admin", "sonatype", new RestPermission( "/repositories/cheese", "GET" ), "nexus" ) );
        assertTrue( authorizationSource.check( "sonatype-admin", "sonatype", new RestPermission( "/repositories/cheese", "DELETE" ), "nexus" ) );
        assertTrue( authorizationSource.check( "sonatype-admin", "sonatype", new RestPermission( "/repositories/cheese", "PUT" ), "nexus" ) );
        assertTrue( authorizationSource.check( "sonatype-admin", "sonatype", new RestPermission( "/repository_index/cheese", "GET" ), "nexus" ) );
        assertTrue( !authorizationSource.check( "sonatype-admin", "sonatype", new RestPermission( "/unknown", "GET" ), "nexus" ) );
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        authorizationSource = new SoterAuthorizationSource();
    }
}
