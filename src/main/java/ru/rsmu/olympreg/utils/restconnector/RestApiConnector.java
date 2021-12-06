package ru.rsmu.olympreg.utils.restconnector;

import org.apache.http.client.methods.HttpGet;

import java.net.URISyntaxException;
import java.util.Map;

/**
 * @author leonid.
 */
public interface RestApiConnector {
    void setDebugFormattedJSON( boolean debugFormattedJSON ) ;

    void setDebugNoCache( boolean debugNoCache ) ;

    /**
     * Do RESTFul API call with http GET
     * @param methodPath Method path
     * @param parameters Call parameters (will be added to url after '?')
     * @return Response string if there is no error
     */
    String callMethodGet( String methodPath, String additionalParam, Map<String, String> parameters ) ;

    /**
     * Do RESTFul API call with http GET
     * @param methodPath Method path
     * @param parameters Call parameters (will be added to url after '?')
     * @return Response string if there is no error
     */
    String callMethod( String methodPath, Map<String, String> parameters ) ;

    /**
     * Do RESTFul API call with http POST
     * @param methodPath Method path
     * @param parameters Call parameters (will be added to url after '?')
     * @param payload String with JSON to send in POST request
     * @return Response string if there is no error
     */
    String callMethod( String methodPath, Map<String, String> parameters, String payload );

}
