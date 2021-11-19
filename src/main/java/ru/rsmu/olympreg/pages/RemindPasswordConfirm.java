package ru.rsmu.olympreg.pages;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.ActivationRequestParameter;
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
public class RemindPasswordConfirm {
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
    private Block successBlock,failBlock;

    private Object onActivate() {
        if ( candidate != null && candidate.getKeyCode().equalsIgnoreCase( key ) ) {
            User user = userDao.findByUsername( candidate.getEmail() );
            if ( user != null ) {
                user.setPassword( candidate.getPassword() );

                userDao.save( user );
                userDao.delete( candidate );

                resultBlock = successBlock;

                //do log in
                SimpleUsernameToken token = new SimpleUsernameToken( user.getUsername(), user.getPassword() );
                Subject currentUser = securityService.getSubject();

                if ( currentUser == null ) {
                    throw new IllegalStateException( "Subject can't be null" );
                }
                try {
                    currentUser.login( token );
                } catch (AuthenticationException e) {
                    // log unauthorized try to login
                }
            }
        }
        if ( resultBlock == null ) {
            resultBlock = failBlock;
        }

        return null;
    }
}
