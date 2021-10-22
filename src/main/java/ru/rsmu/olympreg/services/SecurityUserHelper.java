package ru.rsmu.olympreg.services;

import org.tynamo.security.services.SecurityService;
import ru.rsmu.olympreg.dao.UserDao;
import ru.rsmu.olympreg.entities.User;
import ru.rsmu.olympreg.entities.UserRoleName;

/**
 * @author leonid.
 */
public class SecurityUserHelper {

    private SecurityService securityService;

    private UserDao userDao;

    public SecurityUserHelper( SecurityService securityService, UserDao userDao ) {
        this.securityService = securityService;
        this.userDao = userDao;
    }

    public User getCurrentUser() {
        if ( !securityService.isUser()  ) {
            return null; // no user
        }
        String username = (String) securityService.getSubject().getPrincipal();
        User user = userDao.findByUsername( username );
        return user;
    }

}
