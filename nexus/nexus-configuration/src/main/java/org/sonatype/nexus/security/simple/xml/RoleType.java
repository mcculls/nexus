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

import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Collections;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "roleType", propOrder = {
    "roleName",
    "subRoles",
    "permissions"
    })
public class RoleType implements Keyable<String>
{
    @XmlID
    @XmlElement(name = "role-name", required = true)
    private String roleName;

    @XmlIDREF
    @XmlElement(name = "sub-role")
    private final KeyedCollection<String, RoleType> subRoles = new KeyedCollection<String, RoleType>();

    @XmlIDREF
    @XmlElement(name = "permission")
    private final KeyedCollection<String, PermissionType> permissions = new KeyedCollection<String, PermissionType>();

    @XmlTransient
    private SecurityType securityType;

    protected RoleType()
    {
    }

    public RoleType( String roleName )
    {
        if ( roleName == null )
        {
            throw new NullPointerException( "roleName is null" );
        }
        this.roleName = roleName;
    }

    public String getRoleName()
    {
        return roleName;
    }

    public String getKey()
    {
        return getRoleName();
    }

    public Map<String, RoleType> getSubRoles()
    {
        return Collections.unmodifiableMap( subRoles.toMap() );
    }

    public void addSubRole( RoleType subRole )
    {
        subRoles.add( subRole );
    }

    public void removeSubRole( RoleType subRole )
    {
        subRoles.remove( subRole );
    }

    public Map<String, PermissionType> getPermissions()
    {
        return Collections.unmodifiableMap( permissions.toMap() );
    }

    public void addPermission( PermissionType permission )
    {
        securityType.addPermission( permission );
        permissions.add( permission );
    }

    public void removePermission( String permissionId )
    {
        permissions.toMap().remove( permissionId );
    }

    void setSecurityType( SecurityType securityType )
    {
        if (this.securityType == securityType) return;

        if (this.securityType == null || securityType == null) {
            this.securityType = securityType;
        } else {
            throw new IllegalStateException("Role " + roleName + " is assigned to another SecurityType");
        }
    }

    void afterUnmarshal( Unmarshaller unmarshaller, Object parent ) throws UnmarshalException
    {
        if ( roleName == null )
        {
            throw new UnmarshalException( "roleName is null" );
        }
        securityType = (SecurityType) parent;
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        RoleType roleType = (RoleType) o;
        return roleName.equals( roleType.roleName );
    }

    public int hashCode()
    {
        return roleName.hashCode();
    }

    public String toString()
    {
        return "[Role: roleName=" + roleName + ", permissions=" + permissions + "]";
    }
}
