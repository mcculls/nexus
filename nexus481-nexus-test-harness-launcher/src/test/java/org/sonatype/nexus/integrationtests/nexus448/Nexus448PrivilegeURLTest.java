package org.sonatype.nexus.integrationtests.nexus448;

import java.io.IOException;

import org.testng.Assert;

import org.restlet.data.MediaType;
import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.sonatype.nexus.rest.model.PrivilegeBaseStatusResource;
import org.sonatype.nexus.test.utils.PrivilegesMessageUtil;
import org.testng.annotations.Test;

/**
 * GETS for application privileges where returning an error, so this is a really simple test to make sure the GET will work.
 *
 */
public class Nexus448PrivilegeURLTest extends AbstractNexusIntegrationTest
{

    private PrivilegesMessageUtil messageUtil;

    public Nexus448PrivilegeURLTest()
    {
        this.messageUtil =
            new PrivilegesMessageUtil( this.getXMLXStream(), MediaType.APPLICATION_XML );
    }
    
    @Test
    public void testUrls() throws IOException
    {
        
        PrivilegeBaseStatusResource resource = this.messageUtil.getPrivilegeResource( "T2" );
        Assert.assertEquals( "repositoryTarget", resource.getType(), "Type" );
        
        resource = this.messageUtil.getPrivilegeResource( "1" );
        Assert.assertEquals( "application", resource.getType(), "Type" );
        
    }
    
    
}
