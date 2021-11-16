package ru.rsmu.olympreg.dao.internal;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import ru.rsmu.olympreg.dao.CompetitorDao;
import ru.rsmu.olympreg.entities.CompetitorProfile;
import ru.rsmu.olympreg.entities.ParticipationInfo;
import ru.rsmu.olympreg.entities.ProfileStage;
import ru.rsmu.olympreg.viewentities.CompetitorFilter;
import ru.rsmu.olympreg.viewentities.SortCriterion;

import java.util.List;

/**
 * @author leonid.
 */
@SuppressWarnings( "unchecked" )
public class CompetitorDaoImpl extends BaseDaoImpl implements CompetitorDao {
    @Override
    public int countProfiles( CompetitorFilter filter ) {
        Criteria criteria = buildCriteria( filter, null )
                .setProjection( Projections.rowCount() );
        return ((Long)criteria.uniqueResult()).intValue();
    }

    @Override
    public List<CompetitorProfile> findProfiles( CompetitorFilter filter, List<SortCriterion> sortCriteria ) {
        return buildCriteria( filter, sortCriteria ).list();
    }

    @Override
    public int countSubjectSelectedProfiles() {
        Criteria criteria = session.createCriteria( ParticipationInfo.class )
                .setProjection( Projections.countDistinct( "profile" ) );
        return ((Long)criteria.uniqueResult()).intValue();
    }

    @Override
    public List<Object[]> findDetailedStats() {
        Criteria criteria = session.createCriteria( ParticipationInfo.class )
                .createAlias( "profile", "profile" )
                .add( Restrictions.isNotNull( "profile.classNumber" ) )
                .add( Restrictions.isNotNull( "profile.region" ) )
                .setProjection( Projections.projectionList()
                        .add( Projections.groupProperty( "profile.region" ) )
                        .add( Projections.groupProperty( "profile.classNumber" ) )
                        .add( Projections.groupProperty( "olympiadSubject" ) )
                        .add( Projections.count( "id" ) )
                );
        return criteria.list();
    }

    @Override
    public int countFilledBaseInfo() {
        Criteria criteria = session.createCriteria( CompetitorProfile.class )
                .add( Restrictions.ne( "profileStage", ProfileStage.NEW ) )
                .setProjection( Projections.rowCount() );
        return ((Long)criteria.uniqueResult()).intValue();
    }

    @Override
    public int countUploadCompleted() {
        Query query = session.createQuery(
                "SELECT count(*) FROM CompetitorProfile cp " +
                        "WHERE 3 = (SELECT count( distinct af.attachmentRole) FROM AttachedFile af WHERE af.profile = cp )"
        );
        return ((Long)query.uniqueResult()).intValue();
    }

    private Criteria buildCriteria( CompetitorFilter filter, List<SortCriterion> sortCriteria ) {
        Criteria criteria = session.createCriteria( CompetitorProfile.class )
                .createAlias( "user", "user" );
        if ( StringUtils.isNotBlank( filter.getEmail() ) ) {
            criteria.add( Restrictions.like( "user.username", "%" + filter.getEmail() + "%" ) );
        }
        if ( StringUtils.isNotBlank( filter.getFirstName() ) ) {
            criteria.add( Restrictions.like( "user.firstName", "%" + filter.getFirstName() + "%" ) );
        }
        if ( StringUtils.isNotBlank( filter.getLastName() ) ) {
            criteria.add( Restrictions.like( "user.firstName", "%" + filter.getLastName() + "%" ) );
        }
        if ( StringUtils.isNotBlank( filter.getPersonalNumber() ) ) {
            criteria.add( Restrictions.like( "caseNumber", "%" + filter.getPersonalNumber() ) );
        }

        if ( sortCriteria != null ) {
            sortCriteria.forEach( sc -> {
                if ( sc.getDirection() == SortCriterion.ASCENDING ) {
                    criteria.addOrder( Order.asc( sc.getPropertyName() ) );
                }
                else if ( sc.getDirection() == SortCriterion.DESCENDING ) {
                    criteria.addOrder( Order.desc( sc.getPropertyName() ) );
                }
            } );
            criteria.addOrder( Order.asc("user.lastName") );
        }
        return  criteria;
    }
}
