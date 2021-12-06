package ru.rsmu.olympreg.utils.restconnector;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.rsmu.olympreg.dao.SystemPropertyDao;
import ru.rsmu.olympreg.entities.system.StoredPropertyName;
import ru.rsmu.olympreg.utils.restconnector.examsapimodel.ExamFacade;
import ru.rsmu.olympreg.utils.restconnector.examsapimodel.ExamResult;
import ru.rsmu.olympreg.utils.restconnector.examsapimodel.PersonInfo;
import ru.rsmu.olympreg.utils.restconnector.examsapimodel.TesteeExamInfo;

import java.io.IOException;
import java.util.*;

/**
 * @author leonid.
 */
public class TempolwConnector extends BaseRestApiConnector {
    private Logger log = LoggerFactory.getLogger( TempolwConnector.class );

    public static final String ALL_EXAMS_METHOD = "rest/exam/list";
    public static final String UPCOMING_EXAMS_METHOD = "rest/exam/list/upcoming";
    public static final String ADD_PARTICIPANTS_METHOD = "rest/exam/{id}/participants";
    public static final String GET_RESULTS_METHOD = "rest/exam/{id}/results";

    @Inject
    private SystemPropertyDao systemPropertyDao;

    ObjectMapper mapper = new ObjectMapper();

    @Override
    public String getServiceUrl() {
        return systemPropertyDao.getProperty( StoredPropertyName.TEMPOLW_SYSTEM_URL );
    }

    @Override
    public String getAuthorizationMethod() {
        return "Basic";
    }

    @Override
    public String getAuthorizationToken() {
        String username = systemPropertyDao.getProperty( StoredPropertyName.TEMPOLW_USER );
        String password = systemPropertyDao.getProperty( StoredPropertyName.TEMPOLW_PASSWORD );

        String token = username + ":" + password;
        return new String( Base64.getEncoder().encode( token.getBytes() ) );
    }

    @Override
    public Logger getLogger() {
        return log;
    }


    public List<ExamFacade> getUpcomingExams() {
        return getExamsByMethod( UPCOMING_EXAMS_METHOD );
    }

    public List<TesteeExamInfo> sendPersonToExam( long examExtarnalId, List<PersonInfo> personInfos ) {
        String method = ADD_PARTICIPANTS_METHOD.replace( "{id}", String.valueOf( examExtarnalId ) );
        String payload = null;
        try {
            payload = mapper.writeValueAsString( personInfos );
        } catch (JsonProcessingException e) {
            log.error( "JSON can't create the payload", e );
            return Collections.emptyList();
        }
        String answer = callMethod( method, null, payload );

        if ( answer != null ) {
            try {
                return mapper.readValue( answer, new TypeReference<List<TesteeExamInfo>>() {});
            } catch (IOException e) {
                log.error( "JSON can't parse the response", e );
            }
        }
        return Collections.emptyList();
    }

    public ExamResult getExamResult( long examId, int pageSize, int firstResult ) {
        String method = GET_RESULTS_METHOD.replace( "{id}", String.valueOf( examId ) );
        Map<String, String> parameters = new HashMap<>();
        parameters.put( "pageSize", String.valueOf( pageSize ) );
        parameters.put( "firstResult", String.valueOf( firstResult ) );

        String answer = callMethod( method, parameters );
        if ( answer != null ) {
            try {
                return mapper.readValue( answer, ExamResult.class );
            } catch (IOException e) {
                log.error( "JSON can't parse the response", e );
            }
        }
        return null;
    }

    public List<ExamFacade> getAllExams() {
        return getExamsByMethod( ALL_EXAMS_METHOD );
    }

    private List<ExamFacade> getExamsByMethod( String method ) {
        String answer = callMethod( method, null );
        if ( answer != null ) {
            try {
                return mapper.readValue( answer, new TypeReference<List<ExamFacade>>() {} );
            } catch (IOException e) {
                log.error( "JSON can't parse the response", e );
            }
        }
        return Collections.emptyList();
    }
}
