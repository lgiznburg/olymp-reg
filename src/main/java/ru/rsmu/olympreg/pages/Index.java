package ru.rsmu.olympreg.pages;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.tynamo.security.services.SecurityService;
import ru.rsmu.olympreg.dao.OlympiadDao;
import ru.rsmu.olympreg.dao.SystemPropertyDao;
import ru.rsmu.olympreg.dao.UserDao;
import ru.rsmu.olympreg.entities.*;
import ru.rsmu.olympreg.entities.system.StoredPropertyName;
import ru.rsmu.olympreg.services.SecurityUserHelper;
import ru.rsmu.olympreg.utils.YearHelper;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

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

    @Property
    private AttachedFile attachedTemp;

    @Inject
    private Messages messages;

    @Inject
    private UserDao userDao;

    @Inject
    private SystemPropertyDao systemPropertyDao;

    @Inject
    private OlympiadDao olympiadDao;

    @Inject
    private Block competitorBlock, registrationClosedBlock;

    public void onActivate() {
        if ( securityService.isUser() && securityService.hasRole( UserRoleName.competitor.name() )) {
            User user = securityUserHelper.getCurrentUser();
            profile = userDao.findProfile( user );
            if ( profile == null && isRegistrationOpen() ) {
                // user registered in previous year, need to create new profile
                profile = new CompetitorProfile();
                profile.setUser( user );
                int caseNumber = userDao.getNextPersonalNumber();
                profile.setCaseNumber( String.valueOf( caseNumber ) );
                profile.setProfileStage( ProfileStage.NEW );
                profile.setYear( YearHelper.getActualYear() );
                userDao.save( profile );
            }
        }
    }

    public void onLogoutTestee()
    {
        if ( securityService.isUser() ) {
            securityService.getSubject().logout();
        }
    }

    public boolean isRegistrationOpen() {
        return olympiadDao.checkRegistrationOpen();
    }

    public List<AttachedFile> getDiplomaFiles() {
        if ( profile.getAttachments() != null ) {
            return profile.getAttachments().stream().filter( at -> at.getAttachmentRole() == AttachmentRole.DIPLOMA )
                    .collect( Collectors.toList());
        }
        return Collections.emptyList();
    }

    public boolean isDiplomaPresent() {
        if ( profile.getAttachments() != null ) {
            return profile.getAttachments().stream().anyMatch( at -> at.getAttachmentRole() == AttachmentRole.DIPLOMA );
        }
        return false;
    }

    public Block getCompetitorPresentation() {
        return profile == null ? registrationClosedBlock : competitorBlock;
    }
}
