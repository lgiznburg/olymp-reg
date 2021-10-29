package ru.rsmu.olympreg.pages.account;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ValueEncoderSource;
import org.apache.tapestry5.util.AbstractSelectModel;
import ru.rsmu.olympreg.dao.UserDao;
import ru.rsmu.olympreg.entities.*;
import ru.rsmu.olympreg.services.SecurityUserHelper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author leonid.
 */
@RequiresRoles( "competitor" )
@Import( module = "bootstrap/collapse" )
public class EditProfile {
    @Property
    private User user;

    @Property
    private CompetitorProfile profile;


    @Inject
    private UserDao userDao;

    @Inject
    private SecurityUserHelper securityUserHelper;

    @Inject
    private ValueEncoderSource valueEncoderSource;

    @Inject
    private Messages messages;


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

    public String getProfileSex() {
        if ( profile.getSex() != null ) {
            if ( profile.getSex().equalsIgnoreCase( "M" ) ) {
                return "Мужской";
            }
            else if ( profile.getSex().equalsIgnoreCase( "F" ) ) {
                return "Женский";
            }
        }
        return "";
    }

    public String getSchoolLocationName() {
        if ( profile.getSchoolLocation() != null ) {
            return messages.get( profile.getSchoolLocation().name() );
        }
        return "";
    }

    public String getProfileRegionName() {
        return profile.getRegion() == null ? "Регион не выбран" : profile.getRegion().getName();
    }

    public ValueEncoder<SubjectRegion> getRegionsEncoder() {
        return valueEncoderSource.getValueEncoder( SubjectRegion.class );
    }

    public SelectModel getRegionsModel() {
        return new AbstractSelectModel() {
            @Override
            public List<OptionGroupModel> getOptionGroups() {
                return null;
            }

            @Override
            public List<OptionModel> getOptions() {
                List<SubjectRegion> regions = userDao.findAll( SubjectRegion.class );
                return regions.stream().map( r -> new OptionModelImpl( r.getName(), r ) ).collect( Collectors.toList());
            }
        };
    }

    public void onSuccess() {
        userDao.save( user );

        if ( profile.isProfileCompleted() && profile.getProfileStage() == ProfileStage.NEW ) {
            profile.setProfileStage( ProfileStage.INFO_COMPLETED );
        } else if ( !profile.isProfileCompleted() && profile.getProfileStage() != ProfileStage.NEW ) {
            profile.setProfileStage( ProfileStage.NEW );
        }
        userDao.save( profile );
    }
}
