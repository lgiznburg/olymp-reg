package ru.rsmu.olympreg.pages;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.tynamo.security.services.SecurityService;
import ru.rsmu.olympreg.dao.SystemPropertyDao;
import ru.rsmu.olympreg.dao.UserDao;
import ru.rsmu.olympreg.entities.CompetitorProfile;
import ru.rsmu.olympreg.entities.User;
import ru.rsmu.olympreg.entities.UserRoleName;
import ru.rsmu.olympreg.entities.system.StoredPropertyName;
import ru.rsmu.olympreg.services.SecurityUserHelper;

import java.util.List;
import java.util.Locale;

/**
 * @author leonid.
 */
public class Index {


    @Inject
    private SecurityUserHelper securityUserHelper;

    @Inject
    private SecurityService securityService;

    @Inject
    private Locale currentLocale;

    @Property
    private List<String> output;

    @Property
    private CompetitorProfile profile;

    @Inject
    private Messages messages;

    @Inject
    private UserDao userDao;

    @Inject
    private SystemPropertyDao systemPropertyDao;

    public void onActivate() {
        if ( securityService.isUser() && securityService.hasRole( UserRoleName.competitor.name() )) {
            User user = securityUserHelper.getCurrentUser();
            profile = userDao.findProfile( user );
        }
    }

    public void onLogoutTestee()
    {
        if ( securityService.isUser() ) {
            securityService.getSubject().logout();
        }
    }


    public boolean isRegistrationOpen() {
        return systemPropertyDao.getPropertyAsInt( StoredPropertyName.REGISTRATION_CHEMISTRY_AVAILABLE ) > 0
        || systemPropertyDao.getPropertyAsInt( StoredPropertyName.REGISTRATION_BIOLOGY_AVAILABLE ) > 0;
    }

}
