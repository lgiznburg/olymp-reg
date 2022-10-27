package ru.rsmu.olympreg.pages.account;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.olympreg.dao.OlympiadDao;
import ru.rsmu.olympreg.dao.SystemPropertyDao;
import ru.rsmu.olympreg.dao.UserDao;
import ru.rsmu.olympreg.entities.*;
import ru.rsmu.olympreg.entities.system.StoredPropertyName;
import ru.rsmu.olympreg.pages.Index;
import ru.rsmu.olympreg.services.SecurityUserHelper;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Property
    private OlympiadConfig chemistryConfig;

    @Property
    private OlympiadConfig biologyConfig;

    @Inject
    private UserDao userDao;

    @Inject
    private SystemPropertyDao systemPropertyDao;

    @Inject
    private OlympiadDao olympiadDao;

    @Inject
    private SecurityUserHelper securityUserHelper;

    public Object onActivate() {
        prepare();
        if ( profile == null ) return Index.class;
        return null;
    }

/*
    public void onPrepareForRender() {
        prepare();
    }

    public void onPrepareForSubmit() {
        prepare();
    }
*/

    private void prepare() {
        user = securityUserHelper.getCurrentUser();
        profile = userDao.findProfile( user );

        if ( profile != null && profile.getClassNumber() != null ) {
            chemistryConfig = olympiadDao.findActiveForClassAndSubject( profile.getClassNumber(), OlympiadSubject.CHEMISTRY );
            biologyConfig = olympiadDao.findActiveForClassAndSubject( profile.getClassNumber(), OlympiadSubject.BIOLOGY );
        }
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

    //todo change from checkboxes to event button
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
        ParticipationInfo participationInfo1 = getChemistryParticipation();
        return chemistryConfig != null
                && checkRegistrationDate( chemistryConfig.getRegistrationStart(), chemistryConfig.getRegistrationEnd() )
                && participationInfo1 == null;
    }

    public boolean isBiologyOpen() {
        ParticipationInfo participationInfo1 = getBiologyParticipation();
        return biologyConfig != null
                && checkRegistrationDate( biologyConfig.getRegistrationStart(), biologyConfig.getRegistrationEnd() )
                && participationInfo1 == null;
    }

    public boolean isChemistryOpen2() {
        ParticipationInfo participationInfo1 = getChemistryParticipation();
        ParticipationInfo participationInfo2 = getChemistryParticipation2();
        return chemistryConfig != null
                && checkRegistrationDate( chemistryConfig.getSecondStageRegistrationStart(), chemistryConfig.getSecondStageRegistrationEnd() )
                && participationInfo2 == null
                && participationInfo1 != null
                && participationInfo1.getResult() != null
                && participationInfo1.getResult().compareTo( chemistryConfig.getSecondStagePassScore() ) >= 0;
    }

    public boolean isBiologyOpen2() {
        ParticipationInfo participationInfo1 = getBiologyParticipation();
        ParticipationInfo participationInfo2 = getBiologyParticipation2();
        return biologyConfig != null
                && checkRegistrationDate( biologyConfig.getSecondStageRegistrationStart(), biologyConfig.getSecondStageRegistrationEnd() )
                && participationInfo2 == null
                && participationInfo1 != null
                && participationInfo1.getResult() != null
                && participationInfo1.getResult().compareTo( biologyConfig.getSecondStagePassScore() ) >= 0;
    }

    public ParticipationInfo getChemistryParticipation() {
         return getExactParticipationInfo( profile.getParticipation(), OlympiadSubject.CHEMISTRY, 0 );
    }

    private ParticipationInfo getExactParticipationInfo( List<ParticipationInfo> list, OlympiadSubject subject, int stage ) {
        List<ParticipationInfo> infoList = list.stream()
                .filter( pt -> pt.getStage() == stage && pt.getOlympiadSubject() == subject )
                .collect( Collectors.toList());
        if ( infoList.size() == 1 ) {
            return infoList.stream().findFirst().orElse( null );
        }
        else if (infoList.size() > 1) {
            return infoList.stream()
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
        return getExactParticipationInfo( profile.getParticipation(), OlympiadSubject.BIOLOGY, 0 );
    }

    public ParticipationInfo getChemistryParticipation2() {
        return getExactParticipationInfo( profile.getParticipation(), OlympiadSubject.CHEMISTRY, 1 );
    }

    public ParticipationInfo getBiologyParticipation2() {
        return getExactParticipationInfo( profile.getParticipation(), OlympiadSubject.BIOLOGY, 1 );
    }

    public boolean onCreateChemistrySecond() {
        prepare();
        if ( isChemistryOpen2() ) {
            ParticipationInfo info = new ParticipationInfo();
            info.setProfile( profile );
            info.setOlympiadSubject( OlympiadSubject.CHEMISTRY );
            info.setStage( 1 );
            info.setApproved( false );
            userDao.save( info );
        }
        return true;
    }

    public boolean onCreateBiologySecond() {
        prepare();
        if ( isBiologyOpen2() ) {
            ParticipationInfo info = new ParticipationInfo();
            info.setProfile( profile );
            info.setOlympiadSubject( OlympiadSubject.BIOLOGY );
            info.setStage( 1 );
            info.setApproved( false );
            userDao.save( info );
        }
        return true;
    }

    private boolean checkRegistrationDate( Date startDate, Date endDate ) {
        Calendar calendar = Calendar.getInstance();
        Date thisDay = calendar.getTime();
        calendar.add( Calendar.DAY_OF_YEAR, -1 );
        return  startDate.before( thisDay ) && endDate.after( calendar.getTime() );
    }

    public boolean isNothingFound() {
        return profile == null || profile.getParticipation() == null
                || profile.getParticipation().size() == 0
                || ( chemistryConfig == null && biologyConfig == null );
    }
}
