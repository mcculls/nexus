/**
 * Sonatype Nexus (TM) Professional Version.
 * Copyright (c) 2008 Sonatype, Inc. All rights reserved.
 * Includes the third-party code listed at http://www.sonatype.com/products/nexus/attributions/.
 * "Sonatype" and "Sonatype Nexus" are trademarks of Sonatype, Inc.
 */
package org.sonatype.nexus.restlight.stage.it.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Response;
import org.sonatype.nexus.integrationtests.AbstractNexusIntegrationTest;
import org.sonatype.nexus.integrationtests.RequestFacade;
import org.sonatype.plexus.rest.representation.XStreamRepresentation;

import com.sonatype.nexus.staging.api.dto.StagingProfileDTO;
import com.sonatype.nexus.staging.api.dto.StagingProfileListResponseDTO;
import com.sonatype.nexus.staging.api.dto.StagingProfileOrderRequestDTO;
import com.sonatype.nexus.staging.api.dto.StagingProfileRepositoriesListResponseDTO;
import com.sonatype.nexus.staging.api.dto.StagingProfileRepositoryDTO;
import com.sonatype.nexus.staging.api.dto.StagingProfileRequestDTO;
import com.sonatype.nexus.staging.api.dto.StagingProfileResponseDTO;
import com.sonatype.nexus.staging.api.dto.StagingPromoteDTO;
import com.sonatype.nexus.staging.api.dto.StagingPromoteRequestDTO;
import com.sonatype.nexus.staging.api.dto.StagingRuleSetDTO;
import com.sonatype.nexus.staging.api.dto.StagingRuleSetListResponseDTO;
import com.sonatype.nexus.staging.api.dto.StagingRuleSetRequestDTO;
import com.sonatype.nexus.staging.api.dto.StagingRuleSetResponseDTO;
import com.sonatype.nexus.staging.api.dto.StagingRuleTypeDTO;
import com.sonatype.nexus.staging.api.dto.StagingRuleTypeListResponseDTO;
import com.thoughtworks.xstream.XStream;

public class StagingMessageUtil
{
    private static XStream jsonXstream;

    private static XStream xmlXstream;

    private static final Logger LOG = Logger.getLogger( StagingMessageUtil.class );

    static
    {
        jsonXstream = XStreamFactory.getJsonXStream();
        xmlXstream = XStreamFactory.getXmlXStream();
    }

    public StagingProfileDTO createProfile( StagingProfileDTO profile )
        throws IOException
    {
        Response response = this.sendMessage( Method.POST, profile );

        if ( !response.getStatus().isSuccess() )
        {
            String responseText = response.getEntity().getText();
            Assert.fail( "Could not create profile: " + response.getStatus() + ":\n" + responseText );
        }

        StagingProfileDTO dto = this.getResourceFromResponse( response );
        Assert.assertNotNull( dto.getId() );
        Assert.assertEquals( dto.getName(), profile.getName() );
        Assert.assertEquals( dto.getRepositoryTargetId(), profile.getRepositoryTargetId() );
        Assert.assertEquals( dto.getRepositoryTemplateId(), profile.getRepositoryTemplateId() );
        Assert.assertEquals( dto.getRepositoryType(), profile.getRepositoryType() );
        Assert.assertEquals( dto.getTargetGroups().size(), profile.getTargetGroups().size() );

        return dto;
    }

    public StagingProfileDTO readProfile( String profileId )
        throws IOException
    {
        Response response = RequestFacade.doGetRequest( "service/local/staging/profiles/" + profileId );

        if ( !response.getStatus().isSuccess() )
        {
            return null;
        }

        String responseText = response.getEntity().getText();
        LOG.debug( "responseText: \n" + responseText );

        XStreamRepresentation representation = new XStreamRepresentation(
            XStreamFactory.getXmlXStream(),
            responseText,
            MediaType.APPLICATION_XML );

        StagingProfileRequestDTO resourceResponse = (StagingProfileRequestDTO) representation
            .getPayload( new StagingProfileRequestDTO() );

        return resourceResponse.getData();
    }

    public StagingProfileDTO updateProfile( StagingProfileDTO profile )
        throws IOException
    {
        Response response = this.sendMessage( Method.PUT, profile );

        if ( !response.getStatus().isSuccess() )
        {
            String responseText = response.getEntity().getText();
            Assert.fail( "Could not update profile: " + response.getStatus() + "\n" + responseText );
        }

        StagingProfileDTO dto = this.getResourceFromResponse( response );

        Assert.assertEquals( dto.getId(), profile.getId() );
        Assert.assertEquals( dto.getName(), profile.getName() );
        // Assert.assertEquals( dto.getRepositoryTargetId(), profile.getRepositoryTargetId() );
        Assert.assertEquals( dto.getRepositoryTemplateId(), profile.getRepositoryTemplateId() );
        Assert.assertEquals( dto.getRepositoryType(), profile.getRepositoryType() );
        Assert.assertEquals( dto.getTargetGroups().size(), profile.getTargetGroups().size() );

        return dto;
    }

    public Response deleteProfile( String profileId )
        throws IOException
    {
        return RequestFacade.sendMessage( "service/local/staging/profiles/" + profileId, Method.DELETE );
    }

    public List<StagingProfileDTO> getList()
        throws IOException
    {
        Response resp = RequestFacade.doGetRequest( "service/local/staging/profiles" );

        if ( resp.getStatus().isSuccess() )
        {
            String responseText = resp.getEntity().getText();
            LOG.debug( "responseText: \n" + responseText );
            Assert.assertNotNull( responseText );

            XStreamRepresentation representation = new XStreamRepresentation(
                xmlXstream,
                responseText,
                MediaType.APPLICATION_XML );

            StagingProfileListResponseDTO response = (StagingProfileListResponseDTO) representation
                .getPayload( new StagingProfileListResponseDTO() );

            return response.getData();
        }

        return new ArrayList<StagingProfileDTO>();
    }

    public List<StagingRuleTypeDTO> listStagingRuleTypes()
        throws IOException
    {
        Response resp = RequestFacade.doGetRequest( "service/local/staging/rule_types" );

        if ( resp.getStatus().isSuccess() )
        {
            String responseText = resp.getEntity().getText();
            LOG.debug( "responseText: \n" + responseText );
            Assert.assertNotNull( responseText );

            XStreamRepresentation representation = new XStreamRepresentation(
                xmlXstream,
                responseText,
                MediaType.APPLICATION_XML );

            StagingRuleTypeListResponseDTO response = (StagingRuleTypeListResponseDTO) representation
                .getPayload( new StagingRuleTypeListResponseDTO() );

            return response.getData();
        }

        return new ArrayList<StagingRuleTypeDTO>();
    }

    public List<StagingProfileRepositoryDTO> getRepositoryList( String profileId )
        throws IOException
    {
        String responseText = RequestFacade
            .doGetRequest( "service/local/staging/profile_repositories/" + profileId ).getEntity().getText();
        LOG.debug( "responseText: \n" + responseText );

        XStreamRepresentation representation = new XStreamRepresentation(
            xmlXstream,
            responseText,
            MediaType.APPLICATION_XML );

        StagingProfileRepositoriesListResponseDTO response = (StagingProfileRepositoriesListResponseDTO) representation
            .getPayload( new StagingProfileRepositoriesListResponseDTO() );

        return response.getData();
    }

    public Response sendMessage( Method method, StagingProfileDTO profile )
        throws IOException
    {
        XStreamRepresentation representation = new XStreamRepresentation( jsonXstream, "", MediaType.APPLICATION_JSON );

        String profileId = ( method == Method.POST ) ? "" : "/" + profile.getId();

        String serviceURI = "service/local/staging/profiles" + profileId;

        StagingProfileRequestDTO dto = new StagingProfileRequestDTO();
        dto.setData( profile );

        representation.setPayload( dto );

        return RequestFacade.sendMessage( serviceURI, method, representation );
    }

    public StagingProfileDTO getResourceFromResponse( Response response )
        throws IOException
    {
        return getResourceFromResponse( response, jsonXstream, MediaType.APPLICATION_JSON );
    }

    public StagingProfileDTO getResourceFromResponse( Response response, XStream xstream, MediaType mediaType )
        throws IOException
    {
        String responseString = response.getEntity().getText();
        LOG.debug( " getResourceFromResponse: " + responseString );

        if ( !response.getStatus().isSuccess() )
        {
            Assert.fail( "Invalid server response: \n" + response.getStatus() + " \n" + responseString );
        }

        XStreamRepresentation representation = new XStreamRepresentation( xstream, responseString, mediaType );

        StagingProfileResponseDTO resourceResponse = (StagingProfileResponseDTO) representation
            .getPayload( new StagingProfileResponseDTO() );

        return resourceResponse.getData();
    }

    public StagingProfileDTO getProfile( String name )
        throws IOException
    {
        List<StagingProfileDTO> profiles = getList();
        for ( StagingProfileDTO profile : profiles )
        {
            if ( profile.getName().equals( name ) )
            {
                return profile;
            }
        }

        return null;
    }

    public StagingProfileDTO evaluateProfile( String groupId, String artifactId, String version, String type )
        throws IOException
    {
        StringBuffer serviceURI = new StringBuffer( "service/local/staging/profile_evaluate" );

        serviceURI.append( "?g=" );
        serviceURI.append( groupId );
        serviceURI.append( "&a=" );
        serviceURI.append( artifactId );
        serviceURI.append( "&v=" );
        serviceURI.append( version );
        serviceURI.append( "&t=" );
        serviceURI.append( type );

        Response response = RequestFacade.sendMessage( serviceURI.toString(), Method.GET, null );

        return getResourceFromResponse( response, xmlXstream, MediaType.APPLICATION_XML );
    }

    public static Response promoteRepository( StagingProfileDTO profile, StagingPromoteDTO promote )
        throws IOException
    {
        XStreamRepresentation representation = new XStreamRepresentation( jsonXstream, "", MediaType.APPLICATION_JSON );

        // http://localhost:2477/nexus/service/local/staging/profiles/198c6bdfb31d0/promote?undefined
        String serviceURI = "service/local/staging/profiles/" + profile.getId() + "/promote";

        StagingPromoteRequestDTO request = new StagingPromoteRequestDTO();
        request.setData( promote );

        representation.setPayload( request );

        return RequestFacade.sendMessage( serviceURI, Method.POST, representation );
    }

    public static Response closeRepository( String profileId, String repositoryId )
        throws IOException
    {
        XStreamRepresentation representation = new XStreamRepresentation( jsonXstream, "", MediaType.APPLICATION_JSON );

        String serviceURI = "service/local/staging/profiles/" + profileId + "/finish";

        StagingPromoteDTO promote = new StagingPromoteDTO();
        promote.setStagedRepositoryId( repositoryId );

        StagingPromoteRequestDTO request = new StagingPromoteRequestDTO();
        request.setData( promote );

        representation.setPayload( request );

        return RequestFacade.sendMessage( serviceURI, Method.POST, representation );
    }

    public static Response dropRepository( String profileId, String repositoryId )
        throws IOException
    {
        XStreamRepresentation representation = new XStreamRepresentation( jsonXstream, "", MediaType.APPLICATION_JSON );

        String serviceURI = "service/local/staging/profiles/" + profileId + "/drop";

        StagingPromoteDTO promote = new StagingPromoteDTO();
        promote.setStagedRepositoryId( repositoryId );

        StagingPromoteRequestDTO request = new StagingPromoteRequestDTO();
        request.setData( promote );

        representation.setPayload( request );

        return RequestFacade.sendMessage( serviceURI, Method.POST, representation );
    }

    public Response setProfilesOrder( String... profilesName )
        throws IOException
    {
        XStreamRepresentation representation = new XStreamRepresentation( jsonXstream, "", MediaType.APPLICATION_JSON );

        String serviceURI = "service/local/staging/profile_order";

        List<String> order = new ArrayList<String>();
        for ( String name : profilesName )
        {
            order.add( getProfile( name ).getId() );
        }

        StagingProfileOrderRequestDTO request = new StagingProfileOrderRequestDTO();
        request.setData( order );

        representation.setPayload( request );

        return RequestFacade.sendMessage( serviceURI, Method.POST, representation );
    }

    /**
     * ATTENTION: this method only work when the profile has only one repository
     */
    public Response closeRepository( String profileName )
        throws IOException
    {
        StagingProfileDTO profile = getProfile( profileName );
        Assert.assertNotNull( "Unable to retrieve profile " + profileName, profile );

        List<StagingProfileRepositoryDTO> repositories = getRepositoryList( profile.getId() );
        Assert.assertEquals(
            "Only one repository was expected! " + new XStream().toXML( repositories ),
            1,
            repositories.size() );

        StagingProfileRepositoryDTO repository = repositories.get( 0 );
        return closeRepository( profile.getId(), repository.getRepositoryId() );
    }

    /**
     * ATTENTION: this method only work when the profile has only one repository
     */
    public StagingProfileRepositoryDTO getRepository( String profileName )
        throws IOException
    {
        StagingProfileDTO profile = getProfile( profileName );
        Assert.assertNotNull( "Unable to retrieve profile " + profileName, profile );

        List<StagingProfileRepositoryDTO> repositories = getRepositoryList( profile.getId() );
        Assert.assertEquals( "Only one repository was expected! " + repositories, 1, repositories.size() );

        StagingProfileRepositoryDTO repository = repositories.get( 0 );
        return repository;
    }

    /**
     * ATTENTION: this method only work when the profile has only one repository
     * 
     * @return
     */
    public Response promoteRepository( String profileName, String targetRepositoryId )
        throws IOException
    {
        StagingProfileDTO profile = getProfile( profileName );

        StagingProfileRepositoryDTO stagedRepo = getRepository( profileName );

        StagingPromoteDTO promote = new StagingPromoteDTO();
        promote.setStagedRepositoryId( stagedRepo.getRepositoryId() );
        promote.setTargetRepositoryId( targetRepositoryId );

        return promoteRepository( profile, promote );
    }

    public Response dropRepository( String profileName )
        throws IOException
    {
        StagingProfileDTO profile = getProfile( profileName );

        StagingProfileRepositoryDTO stagedRepo = getRepository( profileName );

        return dropRepository( profile.getId(), stagedRepo.getRepositoryId() );
    }

    public static int uploadBundle( File bundle )
        throws IOException
    {
        String serviceURI = AbstractNexusIntegrationTest.nexusBaseUrl + "service/local/staging/bundle_upload";

        PostMethod filePost = new PostMethod( serviceURI );
        filePost.getParams().setBooleanParameter( HttpMethodParams.USE_EXPECT_CONTINUE, true );

        Part[] parts = { new FilePart( bundle.getName(), bundle ) };
        filePost.setRequestEntity( new MultipartRequestEntity( parts, filePost.getParams() ) );

        LOG.debug( "POST to: " + serviceURI );
        LOG.debug( "Uploding file: " + bundle.getName() );

        return RequestFacade.executeHTTPClientMethod( new URL( serviceURI ), filePost ).getStatusCode();
    }

    public List<StagingRuleSetDTO> listRuleSets()
        throws IOException
    {
        String serviceURI = "service/local/staging/rule_sets";

        LOG.info( "HTTP GET: '" + serviceURI + "'" );

        Response response = RequestFacade.doGetRequest( serviceURI );

        if ( response.getStatus().isSuccess() )
        {
            String responseText = response.getEntity().getText();

            LOG.debug( "Response Text: \n" + responseText );

            XStreamRepresentation representation = new XStreamRepresentation(
                xmlXstream,
                responseText,
                MediaType.APPLICATION_XML );

            StagingRuleSetListResponseDTO dto = (StagingRuleSetListResponseDTO) representation
                .getPayload( new StagingRuleSetListResponseDTO() );

            return dto.getData();
        }
        else
        {
            handleFailedResponse( response );

            return null;
        }
    }

    public StagingRuleSetDTO createRuleSet( StagingRuleSetDTO ruleSet )
        throws IOException
    {
        String serviceURI = "service/local/staging/rule_sets";

        XStreamRepresentation representation = new XStreamRepresentation( xmlXstream, "", MediaType.APPLICATION_XML );

        StagingRuleSetRequestDTO dto = new StagingRuleSetRequestDTO();

        dto.setData( ruleSet );

        representation.setPayload( dto );

        LOG.info( "HTTP POST: '" + serviceURI + "'" );

        Response response = RequestFacade.sendMessage( serviceURI, Method.POST, representation );

        return readRuleSet( response );
    }

    public StagingRuleSetDTO readRuleSet( String id )
        throws IOException
    {
        String serviceURI = "service/local/staging/rule_sets/" + id;

        LOG.info( "HTTP GET: '" + serviceURI + "'" );

        Response response = RequestFacade.doGetRequest( serviceURI );

        return readRuleSet( response );
    }

    public StagingRuleSetDTO updateRuleSet( StagingRuleSetDTO ruleSet )
        throws IOException
    {
        String serviceURI = "service/local/staging/rule_sets/" + ruleSet.getId();

        XStreamRepresentation representation = new XStreamRepresentation( xmlXstream, "", MediaType.APPLICATION_XML );

        StagingRuleSetRequestDTO dto = new StagingRuleSetRequestDTO();

        dto.setData( ruleSet );

        representation.setPayload( dto );

        LOG.info( "HTTP PUT: '" + serviceURI + "'" );

        Response response = RequestFacade.sendMessage( serviceURI, Method.PUT, representation );

        return readRuleSet( response );
    }

    public Response deleteRuleSet( String id )
        throws IOException
    {
        String serviceURI = "service/local/staging/rule_sets/" + id;

        LOG.info( "HTTP DELETE: '" + serviceURI + "'" );

        return RequestFacade.sendMessage( serviceURI, Method.DELETE );
    }

    private StagingRuleSetDTO readRuleSet( Response response )
        throws IOException
    {
        if ( response.getStatus().isSuccess() )
        {
            String responseText = response.getEntity().getText();

            LOG.debug( "Response Text: \n" + responseText );

            XStreamRepresentation representation = new XStreamRepresentation(
                xmlXstream,
                responseText,
                MediaType.APPLICATION_XML );

            StagingRuleSetResponseDTO dto = (StagingRuleSetResponseDTO) representation
                .getPayload( new StagingRuleSetResponseDTO() );

            return dto.getData();
        }
        else
        {
            handleFailedResponse( response );

            return null;
        }
    }

    private void handleFailedResponse( Response response )
        throws IOException
    {
        LOG.warn( "HTTP Error: '" + response.getStatus().getCode() + "'" );

        LOG.warn( response.getEntity().getText() );
    }

}
