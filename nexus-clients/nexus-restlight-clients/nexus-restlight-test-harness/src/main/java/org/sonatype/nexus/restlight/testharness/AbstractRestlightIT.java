package org.sonatype.nexus.restlight.testharness;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthPolicy;
import org.apache.commons.httpclient.auth.AuthScope;
import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.sonatype.nexus.restlight.common.NxBasicScheme;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRestlightIT
    extends AbstractNexusIntegrationTest
{

    protected static final String DEFAULT_EXPECTED_USER = "testuser";

    protected static final String DEFAULT_EXPECTED_PASSWORD = "password";

    protected void setupAuthentication( final HttpClient client )
    {
        setupAuthentication( client, getExpectedUser(), getExpectedPassword() );
    }

    protected void setupAuthentication( final HttpClient client, final String user, final String password )
    {
        UsernamePasswordCredentials creds = new UsernamePasswordCredentials( user, password );

        List<String> policies = new ArrayList<String>();
        policies.add( NxBasicScheme.POLICY_NAME );

        AuthPolicy.registerAuthScheme( NxBasicScheme.POLICY_NAME, NxBasicScheme.class );

        client.getParams().setParameter( AuthPolicy.AUTH_SCHEME_PRIORITY, policies );

        client.getState().setCredentials( AuthScope.ANY, creds );
    }

    protected String getExpectedUser()
    {
        return DEFAULT_EXPECTED_USER;
    }

    protected String getExpectedPassword()
    {
        return DEFAULT_EXPECTED_PASSWORD;
    }

}
