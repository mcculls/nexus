package org.sonatype.nexus.integrationtests.nexus393;

import org.junit.Assert;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.sonatype.nexus.integrationtests.RequestFacade;

public class ResetPasswordUtils
{

    public static void resetPassword( String username )
        throws Exception
    {
        String serviceURI = "service/local/users/reset/" + username;
        Status status = RequestFacade.sendMessage( serviceURI, Method.DELETE ).getStatus();
        Assert.assertEquals( Status.SUCCESS_OK.getCode(), status.getCode() );
    }

}
