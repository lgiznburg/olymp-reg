package ru.rsmu.olympreg.components.admin;

import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.olympreg.dao.UserDao;
import ru.rsmu.olympreg.entities.*;
import ru.rsmu.olympreg.utils.YearHelper;

import java.util.ArrayList;

/**
 * @author leonid.
 */
public class CandidateView {
    @Parameter(required = true)
    @Property
    private UserCandidate candidate;

    @InjectComponent
    private Form createForm;

    @Inject
    private UserDao userDao;

    @Inject
    private Messages messages;

    public boolean onValidateFromCreateForm() {
        if ( candidate != null ) {
            User otherUser = userDao.findByUsername( candidate.getEmail() );
            if ( otherUser != null ) {
                //this user already exists
                createForm.recordError( messages.get( "candidate-email-non-unique" ) );
            }
        }
        return true;
    }

    public void onSuccessFromCreateForm() {
        if ( candidate != null ) {
            User user = new User();
            user.setUsername( candidate.getEmail() );
            user.setFirstName( candidate.getFirstName() );
            user.setMiddleName( candidate.getMiddleName() );
            user.setLastName( candidate.getLastName() );
            user.setPassword( candidate.getPassword() );
            user.setRoles( new ArrayList<>() );
            UserRole role = userDao.findRole( UserRoleName.competitor );
            user.getRoles().add( role );

            userDao.save( user );
            userDao.delete( candidate );

            CompetitorProfile profile = new CompetitorProfile();
            profile.setUser( user );
            int caseNumber = userDao.getNextPersonalNumber();
            profile.setCaseNumber( String.valueOf( caseNumber ) );
            profile.setProfileStage( ProfileStage.NEW );
            profile.setYear( YearHelper.getActualYear() );
            userDao.save( profile );
        }
    }
}
