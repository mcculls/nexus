package org.sonatype.nexus.restlight.core;

import java.util.ArrayList;
import java.util.List;

public class User
{
    private String resourceURI;

    private String userId;

    private String name;

    private String email;

    private String status;

    private boolean userManaged = true;

    private List<String> roles = new ArrayList<String>();

    public String getUserId()
    {
        return userId;
    }

    public void setUserId( final String userId )
    {
        this.userId = userId;
    }

    public String getName()
    {
        return name;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail( final String email )
    {
        this.email = email;
    }

    public String getResourceURI()
    {
        return resourceURI;
    }

    public void setResourceURI( final String resourceURI )
    {
        this.resourceURI = resourceURI;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus( final String status )
    {
        this.status = status;
    }

    public boolean isUserManaged()
    {
        return userManaged;
    }

    public void setUserManaged( final boolean userManaged )
    {
        this.userManaged = userManaged;
    }

    public List<String> getRoles()
    {
        return roles;
    }

    public void setRoles( final List<String> roles )
    {
        this.roles = roles;
    }

    @Override
    public String toString()
    {
        return "User [email=" + email + "\n  name=" + name + "\n  resourceURI=" + resourceURI + "\n  roles=" + roles
            + "\n  status=" + status + "\n  userId=" + userId + "\n  userManaged=" + userManaged + "]";
    }

}
