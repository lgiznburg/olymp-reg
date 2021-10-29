package ru.rsmu.olympreg.pages.account;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.olympreg.dao.UserDao;
import ru.rsmu.olympreg.entities.CompetitorProfile;
import ru.rsmu.olympreg.entities.OlympiadSubject;
import ru.rsmu.olympreg.entities.ParticipationInfo;
import ru.rsmu.olympreg.entities.User;
import ru.rsmu.olympreg.services.SecurityUserHelper;

import java.util.Collections;
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
        if ( addToChemistry ) {
            ParticipationInfo info = new ParticipationInfo();
            info.setProfile( profile );
            info.setOlympiadSubject( OlympiadSubject.CHEMISTRY );
            userDao.save( info );
        }
        if ( addToBiology ) {
            ParticipationInfo info = new ParticipationInfo();
            info.setProfile( profile );
            info.setOlympiadSubject( OlympiadSubject.BIOLOGY );
            userDao.save( info );
        }
    }
}
