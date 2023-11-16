package ru.rsmu.olympreg.components;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.olympreg.dao.CompetitorDao;
import ru.rsmu.olympreg.dao.UserDao;
import ru.rsmu.olympreg.entities.*;
import ru.rsmu.olympreg.services.SecurityUserHelper;
import ru.rsmu.olympreg.utils.YearHelper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author leonid.
 */
public class CompetitorInfo {

    @Parameter( defaultPrefix = "literal")
    private String currentPage;

    @Property
    private CompetitorProfile profile;

    @Property
    private AttachedFile attachedTemp;

    @Inject
    private Block emptyProfile, documentsMissed, selectSubject, waitForLink;

    @Inject
    private UserDao userDao;

    @Inject
    private CompetitorDao competitorDao;

    @Inject
    private SecurityUserHelper securityUserHelper;

    public void setupRender() {
        User user = securityUserHelper.getCurrentUser();
        profile = userDao.findProfile( user );
    }

    public Block getCurrentBlock() {
        if ( !profile.isProfileCompleted() ) {
            return emptyProfile;
        }
        if ( !profile.isAttachmentsCompleted() ) {
            return documentsMissed;
        }
        if ( !profile.isSubjectSelected() ) {
            return selectSubject;
        }
        return waitForLink;
    }

    public boolean showMe() {
        return (profile.isProfileCompleted() || !"profile".equalsIgnoreCase( currentPage ))
                && (profile.isAttachmentsCompleted() || !"documents".equalsIgnoreCase( currentPage ))
                && (profile.isSubjectSelected() || !"participation".equalsIgnoreCase( currentPage ));
    }

    public boolean showInstructions() {
        return !profile.isProfileCompleted() || !profile.isAttachmentsCompleted() || !profile.isSubjectSelected();
    }

    public List<AttachedFile> getDiplomaFiles() {
        List<CompetitorProfile> profiles = competitorDao.findPreviousUserProfiles( profile.getUser() );
        return profiles.stream().flatMap( pr -> pr.getAttachments().stream() )
                .filter( at -> at.getAttachmentRole() == AttachmentRole.DIPLOMA )
                .collect( Collectors.toList());
    }

    public boolean isDiplomaPresent() {
        List<CompetitorProfile> profiles = competitorDao.findPreviousUserProfiles( profile.getUser() );
        return profiles.stream().flatMap( pr -> pr.getAttachments().stream() )
                .anyMatch( at -> at.getAttachmentRole() == AttachmentRole.DIPLOMA );
    }

    public String getAttachmentYears() {
        return String.format( "%d/%d", attachedTemp.getProfile().getYear(), attachedTemp.getProfile().getYear() + 1 );
    }

}
