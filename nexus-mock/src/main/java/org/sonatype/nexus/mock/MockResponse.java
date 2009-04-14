package org.sonatype.nexus.mock;

import org.restlet.data.Status;
import org.sonatype.plexus.rest.resource.PlexusResource;

public class MockResponse
{
    protected Status status;
    protected Object response;
    protected Object payload;

    public MockResponse( Status status, Object payload )
    {
        this.status = status;

        this.response = payload;
    }

    public Status getStatus()
    {
        return status;
    }

    public void setStatus( Status status )
    {
        this.status = status;
    }

    public Object getResponse()
    {
        return response;
    }

    public void setResponse( Object response )
    {
        this.response = response;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}
