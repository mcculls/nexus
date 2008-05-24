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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlType;
import java.util.LinkedHashSet;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "roleType", propOrder = {
    "roleName",
    "subRoles",
    "permissions"
    })
public class RoleType
{
    @XmlID
    @XmlElement(name = "role-name", required = true)
    private String roleName;

    @XmlIDREF
    @XmlElement(name = "sub-role")
    private final Set<RoleType> subRoles = new LinkedHashSet<RoleType>();

    @XmlIDREF
    @XmlElement(name = "permission")
    private final Set<PermissionType> permissions = new LinkedHashSet<PermissionType>();

    public RoleType()
    {
    }

    public RoleType( String roleName )
    {
        this.roleName = roleName;
    }

    public String getRoleName()
    {
        return roleName;
    }

    public void setRoleName( String roleName )
    {
        this.roleName = roleName;
    }

    public Set<RoleType> getSubRoles()
    {
        return subRoles;
    }

    public void addSubRole( RoleType subRole )
    {
        subRoles.add( subRole );
    }

    public void removeSubRole( RoleType subRole )
    {
        subRoles.remove( subRole );
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

    public String toString()
    {
        return "[Role: roleName=" + roleName + ", permissions=" + permissions + "]";
    }
}
