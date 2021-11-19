package ru.rsmu.olympreg.pages;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.ActivationRequestParameter;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.tynamo.security.services.SecurityService;
import ru.rsmu.olympreg.dao.UserDao;
import ru.rsmu.olympreg.entities.*;
import ru.rsmu.olympreg.services.SimpleUsernameToken;

import java.util.ArrayList;

/**
 * @author leonid.
 */
public class RegistrationConfirm {
    @ActivationRequestParameter(value = "reg")
    private UserCandidate candidate;

    @ActivationRequestParameter(value = "key")
    private String key;

    @Inject
    private SecurityService securityService;

    @Inject
    private UserDao userDao;

    @Property
    private Block resultBlock;

    @Inject
    private Block successBlock,failBlock, alreadyExistBlock;

    private Object onActivate() {
        if ( candidate != null && candidate.getKeyCode().equalsIgnoreCase( key ) ) {
            // check for existed user
            User otherUser = userDao.findByUsername( candidate.getEmail() );
            if ( otherUser != null ) {
                //this user already exists
                resultBlock = alreadyExistBlock;
                return null;
            }
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
            userDao.save( profile );

            resultBlock = successBlock;

            //do log in
            SimpleUsernameToken token = new SimpleUsernameToken( user.getUsername(), user.getPassword() );
            Subject currentUser = securityService.getSubject();

            if (currentUser == null)
            {
                throw new IllegalStateException("Subject can't be null");
            }
            try {
                currentUser.login( token );
            } catch (AuthenticationException e) {
                // log unauthorized try to login
            }
        }
        else {
            resultBlock = failBlock;
        }

        return null;
    }
}
