package ru.rsmu.olympreg.components;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.olympreg.dao.UserDao;
import ru.rsmu.olympreg.entities.CompetitorProfile;
import ru.rsmu.olympreg.entities.User;
import ru.rsmu.olympreg.services.SecurityUserHelper;

/**
 * @author leonid.
 */
public class CompetitorBreadcrumb {
    @Parameter( defaultPrefix = "literal")
    private String currentPage;

    @Property
    private CompetitorProfile profile;

    @Inject
    private UserDao userDao;

    @Inject
    private SecurityUserHelper securityUserHelper;

    public void setupRender() {
        User user = securityUserHelper.getCurrentUser();
        profile = userDao.findProfile( user );
    }

    public boolean isSubjectSelected() {
        return profile.getParticipation() != null && !profile.getParticipation().isEmpty();
    }

    public boolean isProfilePage() {
        return "profile".equalsIgnoreCase( currentPage );
    }

    public boolean isDocumentsPage() {
        return "documents".equalsIgnoreCase( currentPage );
    }

    public boolean isParticipationPage() {
        return "participation".equalsIgnoreCase( currentPage );
    }
}
