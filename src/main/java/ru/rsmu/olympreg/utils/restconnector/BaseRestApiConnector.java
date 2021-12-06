package ru.rsmu.olympreg.utils.restconnector;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author leonid.
 */
public abstract class BaseRestApiConnector implements RestApiConnector {

    /**
     * @return the URL of the server to do a call
     */
    abstract public String getServiceUrl();

    /**
     * @return Authorization method ( "Basic", "Oauth", and so on )
     */
    abstract public String getAuthorizationMethod();

    /**
     * @return Authorization token
     */
    abstract public String getAuthorizationToken();

    /**
     * @return Logger
     */
    abstract public Logger getLogger();


    // return formatted JSON object from the API
    private boolean debugFormattedJSON;

    // always return the latest version of the data (ignore cache)
    private boolean debugNoCache;


    public boolean isDebugFormattedJSON() {
        return debugFormattedJSON;
    }

    public void setDebugFormattedJSON(boolean debugFormattedJSON) {
        this.debugFormattedJSON = debugFormattedJSON;
    }

    public boolean isDebugNoCache() {
        return debugNoCache;
    }

    public void setDebugNoCache(boolean debugNoCache) {
        this.debugNoCache = debugNoCache;
    }


    /**
     * Do RESTFul API call with http GET
     * @param methodPath Method path
     * @param parameters Call parameters (will be added to url after '?')
     * @return Response string if there is no error
     */
    public String callMethodGet( String methodPath, String additionalParam, Map<String, String> parameters ) {
        return callMethod( methodPath + "/" + additionalParam, parameters );
    }

    /**
     * Do RESTFul API call with http GET
     * @param methodPath Method path
     * @param parameters Call parameters (will be added to url after '?')
     * @return Response string if there is no error
     */
    public String callMethod(String methodPath, Map<String, String> parameters ) {

        try {
            HttpGet method = new HttpGet( prepareCallingUrl( methodPath, parameters ) );

            return doCall( method );
        } catch (URISyntaxException e) {
            getLogger().error( "Can't create the URL for the call; ", e );
            return null;
        }
    }

    /**
     * Do RESTFul API call with http POST
     * @param methodPath Method path
     * @param parameters Call parameters (will be added to url after '?')
     * @param payload String with JSON to send in POST request
     * @return Response string if there is no error
     */
    public String callMethod(String methodPath, Map<String, String> parameters, String payload ) {

        try {
            HttpPost method = new HttpPost( prepareCallingUrl( methodPath, parameters ) );

            method.setEntity( new StringEntity( payload, StandardCharsets.UTF_8 ) );

            return doCall( method );
/*
        } catch (UnsupportedEncodingException e) {
            getLogger().error( "Exception while calling RESTfull API; ", e );
*/
        } catch (URISyntaxException e) {
            getLogger().error( "Can't create the URL for the call; ", e );
        }
        return null;
    }

    private String prepareCallingUrl( String methodPath, Map<String, String> parameters ) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder( getServiceUrl() );
        uriBuilder.setPath( uriBuilder.getPath() + "/" + methodPath );

        if ( parameters != null ) {
            for ( String paramName : parameters.keySet() ) {
                uriBuilder.addParameter( paramName, parameters.get( paramName ) );
            }
        }
        return uriBuilder.build().toString();
    }

    private String doCall( HttpUriRequest method ) {
        String result = null;
        // Authorization. It could be changed to CredentialsProvider
        method.addHeader( "Authorization", getAuthorizationMethod() + " " + getAuthorizationToken() );

        method.addHeader( "Accept", "application/json" );

        if ( method instanceof HttpPost ) {
            method.setHeader( "Content-Type", "application/json"  );
        }
        if (this.debugNoCache) {
            method.addHeader( "Cache-Control", "no-cache" );
        }

        try ( CloseableHttpClient httpClient = HttpClients.createDefault();
              CloseableHttpResponse httpResponse = httpClient.execute( method )){

            if ( httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK ) {
                result = EntityUtils.toString( httpResponse.getEntity() );
            } else {
                getLogger().error( String.format( "Unable to get response from RESTfull API; HTTP status \"%d\"; Detail \"%s\", Requested: \"%s\"",
                        httpResponse.getStatusLine().getStatusCode(),
                        httpResponse.getStatusLine().getReasonPhrase(),
                        method.getURI().toString() ) );
            }
        } catch (IOException e) {
            getLogger().error( "Exception while calling RESTfull API; ", e );
        }
        return result;
    }
}
