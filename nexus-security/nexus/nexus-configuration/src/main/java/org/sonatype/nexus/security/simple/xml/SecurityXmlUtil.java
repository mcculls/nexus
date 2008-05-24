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

import org.sonatype.nexus.security.simple.SimpleRole;
import org.sonatype.nexus.security.simple.SimpleSecurity;
import org.sonatype.nexus.security.simple.SimpleUser;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.parsers.ParserConfigurationException;
import java.lang.reflect.Constructor;
import java.security.Permission;
import java.security.Permissions;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.io.Writer;
import java.io.Reader;
import java.io.IOException;
import java.io.File;
import java.net.URL;

public class SecurityXmlUtil
{
    public static final XMLInputFactory XMLINPUT_FACTORY = XMLInputFactory.newInstance();
    public static final JAXBContext JAXB_CONTEXT;

    static {
        try {
            JAXB_CONTEXT = JAXBContext.newInstance(SecurityType.class);
        } catch ( JAXBException e) {
            throw new RuntimeException("Could not create jaxb contexts for security types");
        }
    }

    public static SecurityType readSecurity( URL url) throws ParserConfigurationException, IOException, SAXException, JAXBException, XMLStreamException {
        Unmarshaller unmarshaller = JAXB_CONTEXT.createUnmarshaller();

        Object element = unmarshaller.unmarshal(url);
        if ( !( element instanceof SecurityType ) )
        {
            throw new UnmarshalException( "Expected to loade an instance of " + SecurityType.class.getName() + " but loaded " + element.getClass().getName() + ": url=" + url );
        }

        SecurityType securityType = (SecurityType) element;
        return securityType;
    }

    public static SecurityType readSecurity( File file) throws ParserConfigurationException, IOException, SAXException, JAXBException, XMLStreamException {
        Unmarshaller unmarshaller = JAXB_CONTEXT.createUnmarshaller();

        Object element = unmarshaller.unmarshal(file);
        if ( !( element instanceof SecurityType ) )
        {
            throw new UnmarshalException( "Expected to loade an instance of " + SecurityType.class.getName() + " but loaded " + element.getClass().getName() + ": file=" + file );
        }

        SecurityType securityType = (SecurityType) element;
        return securityType;
    }

    public static SecurityType readSecurity( Reader in) throws ParserConfigurationException, IOException, SAXException, JAXBException, XMLStreamException {
        XMLStreamReader xmlStream = XMLINPUT_FACTORY.createXMLStreamReader(in);
        return readSecurity(xmlStream);
    }

    public static SecurityType readSecurity(XMLStreamReader in) throws ParserConfigurationException, IOException, SAXException, JAXBException, XMLStreamException {
        Unmarshaller unmarshaller = JAXB_CONTEXT.createUnmarshaller();
        JAXBElement<SecurityType> element = unmarshaller.unmarshal(in, SecurityType.class);
        SecurityType securityType = element.getValue();
        return securityType;
    }

    public static void writeSecurity(SecurityType securityType, Writer out) throws XMLStreamException, JAXBException {
        Marshaller marshaller = JAXB_CONTEXT.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", true);
        marshaller.marshal(securityType, out);
    }

    public static SimpleSecurity toSimpleSecurity(SecurityType securityType, ClassLoader classLoader) {
        securityType.syncModel();

        IdentityHashMap<PermissionType, Permission> permissions = new IdentityHashMap<PermissionType, Permission>( securityType.getPermissions().size() );
        for ( PermissionType permissionType : securityType.getPermissions() )
        {
            Permission permission = toPermission(permissionType, classLoader);
            permissions.put( permissionType, permission );
        }

        IdentityHashMap<RoleType, SimpleRole> roles = new IdentityHashMap<RoleType, SimpleRole>( securityType.getRoles().size() );
        for ( RoleType roleType : securityType.getRoles() )
        {
            SimpleRole role = toSimpleRole(roleType, permissions);
            roles.put( roleType, role );
        }

        Set<SimpleUser> users = new LinkedHashSet<SimpleUser>(securityType.getUsers().size());
        for ( UserType userType : securityType.getUsers() )
        {
            SimpleUser user = toSimpleUer( userType, roles );
            users.add(user);
        }

        SimpleSecurity simpleSecurity = new SimpleSecurity( users, roles.values() );
        return simpleSecurity;
    }

    public static Permission toPermission( PermissionType permissionType, ClassLoader classLoader) {
        Permission permission;
        try {
            String clazzName = permissionType.getClazz();
            Class<? extends Permission> clazz = classLoader.loadClass(clazzName).asSubclass(Permission.class);
            Constructor<? extends Permission> constructor = clazz.getConstructor(String.class, String.class);
            permission = constructor.newInstance(permissionType.getName(), permissionType.getActions());
        } catch (Exception e) {
            throw new InvalidModelException("Unable to create a permission " + permissionType.getPermissionId());
        }
        return permission;
    }

    public static SimpleRole toSimpleRole(RoleType roleType, Map<PermissionType, Permission> allPermissions) {
        // Holds the actual permission objects for the role
        // We use a map here to eliminate duplicates (the permission object may not properly implement equals and hashcode)
        Map<PermissionType, Permission> rolePermissions = new HashMap<PermissionType, Permission>();

        // Roles we have already visited (breaks circular references)
        Set<RoleType> processedRole = new LinkedHashSet<RoleType>();

        // Roles we still need to visit
        LinkedList<RoleType> unprocessedRoles = new LinkedList<RoleType>();
        unprocessedRoles.add(roleType);

        // While there are still roles to visit...
        while (!unprocessedRoles.isEmpty()) {
            // Get the next role to visit
            RoleType role = unprocessedRoles.removeFirst();

            // If we haven't already visited the role...
            if (processedRole.add( role )) {
                // Add it's sub roles to the list of roles to visit
                unprocessedRoles.addAll( role.getSubRoles() );
                // Add the role's permissions to the permission map
                for ( PermissionType permissionType : role.getPermissions() )
                {
                    rolePermissions.put(permissionType, allPermissions.get(permissionType));
                }
            }
        }

        // Gather role's permissions into a Permissions object
        Permissions permissions = new Permissions();
        for ( Permission permission : rolePermissions.values() )
        {
            permissions.add(permission);
        }

        // Build the simple role
        SimpleRole simpleRole = new SimpleRole( roleType.getRoleName(), permissions );
        return simpleRole;
    }

    public static SimpleUser toSimpleUer(UserType userType, Map<RoleType, SimpleRole> allRoles) {
        Set<SimpleRole> roles = new LinkedHashSet<SimpleRole>(userType.getRoles().size());
        for ( RoleType roleType : userType.getRoles() )
        {
            roles.add(allRoles.get( roleType ));
        }

        // Build the simple user
        SimpleUser simpleUser = new SimpleUser( userType.getUserName(), userType.getPassword(), roles );
        return simpleUser;
    }
}
