package ru.rsmu.olympreg.pages;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tapestry5.Field;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.ValidationException;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import ru.rsmu.olympreg.dao.EmailDao;
import ru.rsmu.olympreg.dao.SystemPropertyDao;
import ru.rsmu.olympreg.dao.UserDao;
import ru.rsmu.olympreg.entities.EmailQueue;
import ru.rsmu.olympreg.entities.User;
import ru.rsmu.olympreg.entities.UserCandidate;
import ru.rsmu.olympreg.entities.system.StoredPropertyName;
import ru.rsmu.olympreg.services.EmailType;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author leonid.
 */
public class SignUp {

    @Property
    private UserCandidate candidate;

    @Property
    private String loginMessage;

    @Property
    private String userPassword;

    @Property
    private String userPasswordConfirm;

    @Property
    @Persist(PersistenceConstants.FLASH)
    private boolean signUpSuccess;

    @Inject
    private UserDao userDao;

    @Inject
    private EmailDao emailDao;

    @Inject
    private Messages messages;

    @Inject
    private SystemPropertyDao systemPropertyDao;

    @InjectComponent
    private Form tempoSignupForm;

    @InjectComponent
    private Field newPassword, login;

    @Environmental
    private JavaScriptSupport javaScriptSupport;

    private static final SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");

    private void setupRender() {
        javaScriptSupport.require( "agreement" );
    }

    private void onPrepare() {
        candidate = new UserCandidate();
        signUpSuccess = false;
    }

    private void onPrepareForSubmit() {
        candidate = new UserCandidate();
        signUpSuccess = false;
    }

    public boolean onValidateFromTempoSignupForm() throws ValidationException {
        User user = userDao.findByUsername( candidate.getEmail() );
        if ( user != null ) {
            tempoSignupForm.recordError( login, messages.get( "password-form-email-non-unique" ));
        }
        if ( userPassword != null && !userPassword.isEmpty() ) {
            if ( userPassword.length() < 7 || (!userPassword.matches( ".*\\W+.*" ) && !userPassword.matches( ".*\\w+.*" ) ) ) {
                tempoSignupForm.recordError( newPassword, messages.get("password-form-wrong"));
            }
            else if ( ! userPassword.equals( userPasswordConfirm ) ) {
                tempoSignupForm.recordError( newPassword, messages.get("passwords-dont-match"));
            }
        }
        else {
            tempoSignupForm.recordError(messages.get("password-form-empty"));
        }

        return true;

    }

    public void onSuccessFromTempoSignupForm() {
            candidate.setCreatedDate( new Date() );
            candidate.setPassword( userDao.encrypt( userPassword ) );
            String keyRaw = candidate.getEmail() + candidate.getLastName() + df.format( candidate.getCreatedDate() );
            candidate.setKeyCode( userDao.encrypt( keyRaw ) );
            userDao.save( candidate );

        Map<String,Object> model = new HashMap<>();

        String thisServerUri = systemPropertyDao.getProperty( StoredPropertyName.MY_OWN_URI );
        thisServerUri += "/RegistrationConfirm?reg=" + candidate.getId() + "&key=" + candidate.getKeyCode();
        model.put( "fullName", candidate.getFullName() );
        model.put( "serverAddress", thisServerUri );

        EmailQueue emailQueue = new EmailQueue();
        emailQueue.setEmailType( EmailType.SIGNUP_RU );
        emailQueue.setEmailAddress( candidate.getEmail() );
        ObjectMapper mapper = new ObjectMapper();
        try {
            emailQueue.setModel( mapper.writeValueAsString( model ) );
        } catch (IOException e) {
            // do nothing
        }
        emailDao.save( emailQueue );


            signUpSuccess = true;
    }
}
