package org.sonatype.nexus.integrationtests.nexus395;

import org.junit.Assert;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.sonatype.nexus.integrationtests.RequestFacade;
import org.sonatype.nexus.rest.xstream.XStreamInitializer;
import org.sonatype.plexus.rest.representation.XStreamRepresentation;

import com.thoughtworks.xstream.XStream;

public class ForgotUsernameUtils
{

    private static XStream xstream;

    static
    {
        xstream = new XStream();
        XStreamInitializer.initialize( xstream );
    }

    public static void recoverUsername( String email )
        throws Exception
    {
        String serviceURI = "service/local/users/forgotid/" + email;
        XStreamRepresentation representation = new XStreamRepresentation( xstream, "", MediaType.APPLICATION_XML );
        representation.setPayload( null );

        Status status = RequestFacade.sendMessage( serviceURI, Method.POST, representation ).getStatus();
        Assert.assertEquals( Status.SUCCESS_OK.getCode(), status.getCode() );
    }

}
