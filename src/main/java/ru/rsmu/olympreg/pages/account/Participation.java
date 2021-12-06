package ru.rsmu.olympreg.pages.account;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.olympreg.dao.SystemPropertyDao;
import ru.rsmu.olympreg.dao.UserDao;
import ru.rsmu.olympreg.entities.CompetitorProfile;
import ru.rsmu.olympreg.entities.OlympiadSubject;
import ru.rsmu.olympreg.entities.ParticipationInfo;
import ru.rsmu.olympreg.entities.User;
import ru.rsmu.olympreg.entities.system.StoredPropertyName;
import ru.rsmu.olympreg.services.SecurityUserHelper;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author leonid.
 */
@RequiresRoles( "competitor" )
public class Participation {

    @Property
    private User user;

    @Property
    private CompetitorProfile profile;

    @Property
    private ParticipationInfo participationInfo;

    @Property
    @Persist(PersistenceConstants.FLASH)
    private String formErrorMessage;

    @Property
    private boolean addToChemistry;

    @Property
    private boolean addToBiology;

    @Inject
    private UserDao userDao;

    @Inject
    private SystemPropertyDao systemPropertyDao;

    @Inject
    private SecurityUserHelper securityUserHelper;

    public void onPrepareForRender() {
        prepare();
    }

    public void onPrepareForSubmit() {
        prepare();
    }

    private void prepare() {
        user = securityUserHelper.getCurrentUser();
        profile = userDao.findProfile( user );
    }

    public List<ParticipationInfo> getChemistryList() {
        if ( profile.isSubjectSelected() ) {
            return profile.getParticipation().stream().filter( pt -> pt.getOlympiadSubject() == OlympiadSubject.CHEMISTRY )
                    .collect( Collectors.toList());
        }
        return Collections.emptyList();
    }

    public List<ParticipationInfo> getBiologyList() {
        if ( profile.isSubjectSelected() ) {
            return profile.getParticipation().stream().filter( pt -> pt.getOlympiadSubject() == OlympiadSubject.BIOLOGY )
                    .collect( Collectors.toList());
        }
        return Collections.emptyList();
    }

    public void onSuccess() {
        if ( addToChemistry && isChemistryOpen() && getChemistryList().isEmpty() ) {
            ParticipationInfo info = new ParticipationInfo();
            info.setProfile( profile );
            info.setOlympiadSubject( OlympiadSubject.CHEMISTRY );
            userDao.save( info );
        }
        if ( addToBiology && isBiologyOpen() && getBiologyList().isEmpty()  ) {
            ParticipationInfo info = new ParticipationInfo();
            info.setProfile( profile );
            info.setOlympiadSubject( OlympiadSubject.BIOLOGY );
            userDao.save( info );
        }
    }

    public boolean isChemistryOpen() {
        return systemPropertyDao.getPropertyAsInt( StoredPropertyName.REGISTRATION_CHEMISTRY_AVAILABLE ) > 0;
    }

    public boolean isBiologyOpen() {
        return systemPropertyDao.getPropertyAsInt( StoredPropertyName.REGISTRATION_BIOLOGY_AVAILABLE ) > 0;
    }

    public ParticipationInfo getChemistryParticipation() {
        List<ParticipationInfo> chemList = getChemistryList();
        if ( chemList.size() == 1 ) {
            return chemList.get( 0 );
        }
        else if (chemList.size() > 0) {
            return chemList.stream()
                    .min(
                            (pt1, pt2) -> {
                                if ( pt1.getExamName() == null ) {
                                    return 1;
                                }
                                else if ( pt2.getExamName() == null ) {
                                    return -1;
                                }
                                else {
                                    return pt1.getExamName().compareTo( pt2.getExamName() );
                                }
                            }
                    )
                    .orElse( null );
        }
        return null;
    }

    public ParticipationInfo getBiologyParticipation() {
        List<ParticipationInfo> bioList = getBiologyList();
        if ( bioList.size() == 1 ) {
            return bioList.get( 0 );
        }
        else if (bioList.size() > 0) {
            return bioList.stream()
                    .min(
                            (pt1, pt2) -> {
                                if ( pt1.getExamName() == null ) {
                                    return 1;
                                }
                                else if ( pt2.getExamName() == null ) {
                                    return -1;
                                }
                                else {
                                    return pt1.getExamName().compareTo( pt2.getExamName() );
                                }
                            }
                    )
                    .orElse( null );
        }
        return null;
    }

}
