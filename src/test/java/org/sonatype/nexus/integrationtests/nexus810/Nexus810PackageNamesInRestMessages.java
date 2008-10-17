package org.sonatype.nexus.integrationtests.nexus810;

import java.io.IOException;

import org.testng.Assert;

import org.restlet.data.Response;
import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.sonatype.nexus.integrationtests.RequestFacade;
import org.testng.annotations.Test;

/**
 * Checks to make sure the tasks don't have packages in the type field.
 */
public class Nexus810PackageNamesInRestMessages extends AbstractNexusIntegrationTest
{

    @Test
    public void checkForPackageNamesInResponse() throws IOException
    {
        // I like simple tests
        Response response = RequestFacade.doGetRequest( "service/local/schedule_types" );
        String responseText = response.getEntity().getText();
        Assert.assertFalse( responseText.contains( "org.sonatype." ), "Found package names in response." );
    }
}
