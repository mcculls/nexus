/**
 * Copyright (c) 2008-2011 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://www.sonatype.com/products/nexus/attributions.
 *
 * This program is free software: you can redistribute it and/or modify it only under the terms of the GNU Affero General
 * Public License Version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License Version 3
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License Version 3 along with this program.  If not, see
 * http://www.gnu.org/licenses.
 *
 * Sonatype Nexus (TM) Open Source Version is available from Sonatype, Inc. Sonatype and Sonatype Nexus are trademarks of
 * Sonatype, Inc. Apache Maven is a trademark of the Apache Foundation. M2Eclipse is a trademark of the Eclipse Foundation.
 * All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.integrationtests;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthPolicy;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.log4j.Logger;
import org.apache.maven.wagon.authentication.AuthenticationInfo;
import org.codehaus.plexus.util.IOUtil;
import org.restlet.Client;
import org.restlet.Context;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Protocol;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;

/**
 * HTTP Request helper.
 * <p>
 * <b>IMPORTANT</b>: Any {@link Response} instances returned from methods here should have their {@link Response#release()} method called in a finally block
 * when you are done with it.
 * 
 */
public class RequestFacade
{
    public static final String SERVICE_LOCAL = "service/local/";

    private static final Logger LOG = Logger.getLogger( RequestFacade.class );
    
    /**
     * Null safe method to release a Response ( its streams and sockets )
     */
    public static void releaseResponse(final Response response){
        if(response != null){
            response.release();
         }
    }
    
    /**
     * Sends a GET request to the specified uri and returns the text of the entity.
     * <p>
     * Using this method is RECOMMENDED if you simply want the text of a response and nothing more since 
     * this method ensures proper cleanup of any sockets, streams, etc., by releasing the response.
     * <p>
     * Of course the entire response text is buffered in memory so use this wisely.
     * 
     * @param serviceURIpart the part of the uri to fetch that is appended to the Nexus base URI.
     * @return the complete response body text, or possibly null if no entity in the response
     */
    public static String doGetRequestEntityText(final String serviceURIpart) throws IOException{
        Response response = null;
        try {
            response = doGetRequest(serviceURIpart);
            Representation rep = response.getEntity();
            if(rep != null){
                return rep.getText();
            }
        }finally{
            releaseResponse(response);
        }
        return null;
    }
    
    /**
     * Send a message to a resource as a GET request and return the response.
     * <p>
     * Ensure you explicity clean up the response entity returned by this method by calling
     * {@link Response#release()}}
     * 
     * @param serviceURIpart the part of the uri to fetch that is appended to the Nexus base URI.
     * @return the response of the request
     * @throws IOException if there is a problem communicating the response
     */
    public static Response doGetRequest( String serviceURIpart )
        throws IOException
    {
        return sendMessage( serviceURIpart, Method.GET );
    }

    /**
     * Send a message to a resource and return the response.
    * <p>
     * Ensure you explicity clean up the response entity returned by this method by calling
     * {@link Response#release()}}
     
     * @param serviceURIpart the part of the uri to fetch that is appended to the Nexus base URI.
     * @param method the method type of the request
     * @return the response of the request
     * @throws IOException if there is a problem communicating the response
     */
    public static Response sendMessage( String serviceURIpart, Method method )
        throws IOException
    {
        return sendMessage( serviceURIpart, method, null );
    }

    /**
     * Send a message to a resource and return the response.
     * <p>
     * Ensure you explicity clean up the response entity returned by this method by calling
     * {@link Response#release()}}
      
     * @param serviceURIpart the part of the uri to fetch that is appended to the Nexus base URI.
     * @param method the method type of the request
     * @param representation the representation to map the response to, may be null
     * @return the response of the request
     * @throws IOException if there is a problem communicating the response
     */
    public static Response sendMessage( String serviceURIpart, Method method, Representation representation )
        throws IOException
    {
        String serviceURI = AbstractNexusIntegrationTest.nexusBaseUrl + serviceURIpart;
        return sendMessage( new URL( serviceURI ), method, representation );
    }
    
    /**
     * Send a message to a resource and return the response.
     * <p>
     * Ensure you explicity clean up the response entity returned by this method by calling
     * {@link Response#release()}}
     * 
     * @param url the absolute url of the resource to request
     * @param method the method type of the request
     * @param representation the representation to map the response to, may be null
     * @return the response of the request
     * @throws IOException if there is a problem communicating the response
     */
    public static Response sendMessage( URL url, Method method, Representation representation )
        throws IOException
    {

        Request request = new Request();
        request.setResourceRef( url.toString() );
        request.setMethod( method );

        if ( !Method.GET.equals( method ) && !Method.DELETE.equals( method ) )
        {
            request.setEntity( representation );
        }

        // change the MediaType if this is a GET, default to application/xml
        if( Method.GET.equals( method ) )
        {
            if( representation != null)
            {
                request.getClientInfo().getAcceptedMediaTypes().
                add(new Preference<MediaType>(representation.getMediaType()));
            }
        }

        // check the text context to see if this is a secure test
        TestContext context = TestContainer.getInstance().getTestContext();
        if ( context.isSecureTest() )
        {
            // ChallengeScheme scheme = new ChallengeScheme( "HTTP_NxBasic", "NxBasic", "Nexus Basic" );
            ChallengeResponse authentication = new ChallengeResponse(
                ChallengeScheme.HTTP_BASIC,
                context.getUsername(),
                context.getPassword() );
            request.setChallengeResponse( authentication );
        }

        Context ctx = new Context();

        Client client = new Client( ctx, Protocol.HTTP );

        LOG.debug( "sendMessage: " + method.getName() + " " + url );
        return client.handle( request );
    }

    /**
     * Download a file at a url and save it to the target file location specified by targetFile.
     * @param url the url to fetch the file from
     * @param targetFile the location where to save the download
     * @return a File instance for the saved file
     * @throws IOException if there is a problem saving the file
     */
    public static File downloadFile( URL url, String targetFile )
        throws IOException
    {
        OutputStream out = null;
        InputStream in = null;
        File downloadedFile = new File( targetFile );
        Response response = null;
        try
        {
            response = sendMessage( url, Method.GET, null );

            if ( !response.getStatus().isSuccess() )
            {
                throw new FileNotFoundException( response.getStatus() + " - " + url );
            }

            // if this is null then someone was getting really creative with the tests, but hey, we will let them...
            if ( downloadedFile.getParentFile() != null )
            {
                downloadedFile.getParentFile().mkdirs();
            }

            in = response.getEntity().getStream();
            out = new BufferedOutputStream( new FileOutputStream( downloadedFile ) );

            IOUtil.copy( in, out, 1024 );
        }
        finally
        {
            IOUtil.close( in );
            IOUtil.close( out );
            releaseResponse(response);
        }
        return downloadedFile;
    }

    /**
     * Execute a HTTPClient method in the context of a test. ie it will use {@link TestContainer#getTestContext()} to make decisions how to execute.
     * <p>
     * NOTE: Before being returned, {@link HttpMethod#releaseConnection()} is called on the {@link HttpMethod} instance, therefore subsequent calls to get response body as string may return nulls.
     */
    public static HttpMethod executeHTTPClientMethod(HttpMethod method)
        throws HttpException,
            IOException
    {
        return executeHTTPClientMethod(method, true);
    }
    
    /**
     * Execute a HTTPClient method, optionally in the context of a test. ie {@link TestContainer#getTestContext()}
     * <p>
     * NOTE: Before being returned, {@link HttpMethod#releaseConnection()} is called on the {@link HttpMethod} instance, therefore subsequent calls to get response body as string may return nulls.
     * 
     * @param method the method to execute
     * @param useTestContext if true, execute this request in the context of a Test, false means ignore the testContext settings
     * @return the HttpMethod instance passed into this method
     * @throws HttpException
     * @throws IOException 
     */
    public static HttpMethod executeHTTPClientMethod(final HttpMethod method, final boolean useTestContext)
        throws HttpException, IOException
    {
        HttpClient client = new HttpClient();
        client.getHttpConnectionManager().getParams().setConnectionTimeout( 5000 );

        if(useTestContext)
        {
            // check the text context to see if this is a secure test
            TestContext context = TestContainer.getInstance().getTestContext();
            if ( context.isSecureTest() )
            {
                client.getState().setCredentials(
                    AuthScope.ANY,
                    new UsernamePasswordCredentials( context.getUsername(), context.getPassword() ) );

                List<String> authPrefs = new ArrayList<String>( 1 );
                authPrefs.add( AuthPolicy.BASIC );
                client.getParams().setParameter( AuthPolicy.AUTH_SCHEME_PRIORITY, authPrefs );
                client.getParams().setAuthenticationPreemptive( true );
            }
        }
        
        try
        {
            client.executeMethod( method );
            method.getResponseBodyAsString(); //forced consumption of response I guess
            return method;
        }
        finally
        {
            method.releaseConnection();
        }
    }

    
    public static AuthenticationInfo getWagonAuthenticationInfo()
    {
        AuthenticationInfo authInfo = null;
        // check the text context to see if this is a secure test
        TestContext context = TestContainer.getInstance().getTestContext();
        if ( context.isSecureTest() )
        {
            authInfo = new AuthenticationInfo();
            authInfo.setUserName( context.getUsername() );
            authInfo.setPassword( context.getPassword() );
        }
        return authInfo;
    }

}
