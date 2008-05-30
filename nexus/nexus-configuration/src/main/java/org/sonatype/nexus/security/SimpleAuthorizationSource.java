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

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.sonatype.nexus.configuration.ApplicationConfiguration;
import org.sonatype.nexus.security.simple.SimpleSecurity;
import org.sonatype.nexus.security.simple.xml.SecurityType;
import org.sonatype.nexus.security.simple.xml.SecurityXmlUtil;

import java.io.File;
import java.security.Permission;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The Class OpenAuthenticationSource.
 *
 * @plexus.component role="org.sonatype.nexus.security.AuthorizationSource" instantiation-strategy="per-lookup" role-hint="simple"
 */
public class SimpleAuthorizationSource implements AuthorizationSource, Initializable
{
    private final AtomicReference<SimpleSecurity> simpleSecurity = new AtomicReference<SimpleSecurity>();
    private final ClassLoader classLoader;
    private SecurityType securityType;

    /** @plexus.requirement */
    protected ApplicationConfiguration applicationConfiguration;

    public SimpleAuthorizationSource()
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if ( classLoader == null )
        {
            classLoader = getClass().getClassLoader();
        }
        this.classLoader = classLoader;
    }

    public SimpleAuthorizationSource( SecurityType securityType, ClassLoader classLoader )
    {
        SimpleSecurity simpleSecurity = SecurityXmlUtil.toSimpleSecurity( securityType, classLoader );
        this.simpleSecurity.set( simpleSecurity );
        this.classLoader = classLoader;
        this.securityType = securityType;
    }

    public synchronized void initialize() throws InitializationException
    {
        if ( applicationConfiguration == null )
        {
            throw new IllegalStateException( "applicationConfiguration is null" );
        }
        File securityXmlFile = new File( applicationConfiguration.getConfigurationDirectory(), "../../../conf/security.xml" );
        try
        {
            SecurityType securityType = SecurityXmlUtil.readSecurity( securityXmlFile );

            SimpleSecurity simpleSecurity = SecurityXmlUtil.toSimpleSecurity( securityType, classLoader );
            this.simpleSecurity.set( simpleSecurity );
            this.securityType = securityType;
        }
        catch ( Exception e )
        {
            throw new InitializationException( "Error loading security.xml file", e );
        }
    }

    public synchronized ApplicationConfiguration getApplicationConfiguration()
    {
        return applicationConfiguration;
    }

    public synchronized void setApplicationConfiguration( ApplicationConfiguration applicationConfiguration )
    {
        this.applicationConfiguration = applicationConfiguration;
    }

    public synchronized SecurityType getSecurityType()
    {
        if ( securityType != null )
        {
            return new SecurityType( securityType );
        }
        else
        {
            return new SecurityType();
        }
    }

    public synchronized void setSecurityType( SecurityType securityType )
    {
        SimpleSecurity simpleSecurity = SecurityXmlUtil.toSimpleSecurity( securityType, classLoader );

        if ( applicationConfiguration != null )
        {
            File securityXmlFile = new File( applicationConfiguration.getConfigurationDirectory(), "security.xml" );
            try
            {
                SecurityXmlUtil.writeSecurity( securityType, securityXmlFile );
            }
            catch ( Exception e )
            {
                throw new RuntimeException( "Error storing security.xml file", e );
            }
        }

        this.simpleSecurity.set( simpleSecurity );
        this.securityType = securityType;
    }

    public boolean check( User user, Permission permission )
    {
        SimpleSecurity simpleSecurity = this.simpleSecurity.get();
        return securityType != null && simpleSecurity.check( user, permission );
    }

    public boolean check( String roleName, Permission permission )
    {
        SimpleSecurity simpleSecurity = this.simpleSecurity.get();
        return securityType != null && simpleSecurity.check( roleName, permission );
    }
}
