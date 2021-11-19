package ru.rsmu.olympreg.pages;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tapestry5.Field;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.ValidationException;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
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
public class RemindPassword {

    @Property
    private String loginMessage;

    @Property
    private String userLogin;

    @Property
    @Persist(PersistenceConstants.FLASH)
    private boolean signUpSuccess;

    @InjectComponent
    private Form tempoSignupForm;

    @InjectComponent
    private Field login, newPassword;

    @Property
    private String userPassword;

    @Property
    private String userPasswordConfirm;

    @Inject
    private UserDao userDao;

    @Inject
    private EmailDao emailDao;

    @Inject
    private SystemPropertyDao systemPropertyDao;

    @Inject
    private Messages messages;

    private static final SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");

    private void onPrepare() {
        signUpSuccess = false;
    }

    private void onPrepareForSubmit() {
        signUpSuccess = false;
    }

    public boolean onValidateFromTempoSignupForm() throws ValidationException {
        User user = userDao.findByUsername( userLogin );
        if ( user == null ) {
            tempoSignupForm.recordError( login, "Указанный адрес электронной почты не найден");
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
        User user = userDao.findByUsername( userLogin );

        if ( user != null ) {
            UserCandidate candidate = new UserCandidate();
            candidate.setEmail( userLogin );
            candidate.setCreatedDate( new Date() );
            candidate.setPassword( userDao.encrypt( userPassword ) );
            String keyRaw = candidate.getEmail() + user.getLastName() + df.format( candidate.getCreatedDate() );
            candidate.setKeyCode( userDao.encrypt( keyRaw ) );
            userDao.save( candidate );

            Map<String,Object> model = new HashMap<>();

            String thisServerUri = systemPropertyDao.getProperty( StoredPropertyName.MY_OWN_URI );
            thisServerUri += "/RemindPasswordConfirm?reg=" + candidate.getId() + "&key=" + candidate.getKeyCode();
            model.put( "fullName", user.getFullName() );
            model.put( "serverAddress", thisServerUri );

            EmailQueue emailQueue = new EmailQueue();
            emailQueue.setEmailType( EmailType.REMIND_PASSWORD );
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

}
