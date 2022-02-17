package ru.rsmu.olympreg.dao.internal;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import ru.rsmu.olympreg.dao.CompetitorDao;
import ru.rsmu.olympreg.entities.CompetitorProfile;
import ru.rsmu.olympreg.entities.OlympiadSubject;
import ru.rsmu.olympreg.entities.ParticipationInfo;
import ru.rsmu.olympreg.entities.ProfileStage;
import ru.rsmu.olympreg.viewentities.CompetitorFilter;
import ru.rsmu.olympreg.viewentities.SortCriterion;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author leonid.
 */
@SuppressWarnings( "unchecked" )
public class CompetitorDaoImpl extends BaseDaoImpl implements CompetitorDao {
    @Override
    public int countProfiles( CompetitorFilter filter ) {
        Criteria criteria = buildCriteria( filter, null )
                .setProjection( Projections.countDistinct("id") );
        return ((Long)criteria.uniqueResult()).intValue();
    }

    @Override
    public List<CompetitorProfile> findProfiles( CompetitorFilter filter, List<SortCriterion> sortCriteria, int startIndex, int size ) {
        Criteria criteria = buildCriteria( filter, sortCriteria )
                .setFirstResult( startIndex )
                .setMaxResults( size );
        return criteria.list();
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
                        .add( Projections.countDistinct( "profile" ) )
                );
        return criteria.list();
    }

    @Override
    public List<Object[]> findGlobalDetailedStats() {
        Criteria criteria = session.createCriteria( ParticipationInfo.class )
                .createAlias( "profile", "profile" )
                .add( Restrictions.isNotNull( "profile.classNumber" ) )
                .add( Restrictions.isNotNull( "profile.region" ) )
                .add( Restrictions.isNull( "examName" ) )
                .setProjection( Projections.projectionList()
                        .add( Projections.groupProperty( "profile.classNumber" ) )
                        .add( Projections.groupProperty( "olympiadSubject" ) )
                        .add( Projections.countDistinct( "profile" ) )
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

    @Override
    public List<ParticipationInfo> findNewParticipations( int classNumber, OlympiadSubject olympiadSubject, int maxResults ) {
        Criteria criteria = session.createCriteria( ParticipationInfo.class )
                .createAlias( "profile", "profile" )
                .add( Restrictions.eq( "profile.classNumber", classNumber ) )
                .add( Restrictions.isNotNull( "profile.region" ) )
                .add( Restrictions.eq( "olympiadSubject", olympiadSubject ) )
                .add( Restrictions.isNull( "examName" ) )
                .setMaxResults( maxResults );
        return criteria.list();
    }

    @Override
    public List<Object[]> findExamsWithNoResults() {
        Criteria criteria = session.createCriteria( ParticipationInfo.class )
                .add( Restrictions.isNotNull( "examId" ) )
                .add( Restrictions.lt("endDate", new Date()) )
                .add( Restrictions.isNull( "result" ) )
                .setProjection( Projections.projectionList()
                        .add( Projections.groupProperty( "examId" ) )
                        .add( Projections.countDistinct( "profile" ) )
                );

        return criteria.list();
    }

    @Override
    public List<ParticipationInfo> findParticipation( long examId, String caseNumber ) {
        Criteria criteria = session.createCriteria( ParticipationInfo.class )
                .createAlias( "profile", "profile" )
                .add( Restrictions.eq( "profile.caseNumber", caseNumber ) )
                .add( Restrictions.eq( "examId", examId ) );
        return criteria.list();
    }

    @Override
    public List<CompetitorProfile> findForSecondStage( OlympiadSubject subject, int classNumber, int secondStagePassScore ) {
        Criteria criteria = session.createCriteria( CompetitorProfile.class )
                .createAlias( "participation", "participation" )
                .add( Restrictions.eq( "classNumber", classNumber ) )
                .add( Restrictions.eq( "participation.olympiadSubject", subject ) )
                .add( Restrictions.eq( "participation.stage", 0 ) )
                .add( Restrictions.ge( "participation.result", secondStagePassScore ) )
                .setResultTransformer( CriteriaSpecification.DISTINCT_ROOT_ENTITY );

        return criteria.list();
    }

    private Criteria buildCriteria( CompetitorFilter filter, List<SortCriterion> sortCriteria ) {
        Criteria criteria = session.createCriteria( CompetitorProfile.class )
                .createAlias( "user", "user" );
        if ( filter.getSubject() != null || filter.isSecondStage() || filter.isNeedApproval() ) {
            criteria.createAlias( "participation", "participation" );
        }
        if ( StringUtils.isNotBlank( filter.getEmail() ) ) {
            criteria.add( Restrictions.like( "user.username", "%" + filter.getEmail() + "%" ) );
        }
        if ( StringUtils.isNotBlank( filter.getFirstName() ) ) {
            criteria.add( Restrictions.like( "user.firstName", "%" + filter.getFirstName() + "%" ) );
        }
        if ( StringUtils.isNotBlank( filter.getLastName() ) ) {
            criteria.add( Restrictions.like( "user.lastName", "%" + filter.getLastName() + "%" ) );
        }
        if ( StringUtils.isNotBlank( filter.getPersonalNumber() ) ) {
            criteria.add( Restrictions.like( "caseNumber", "%" + filter.getPersonalNumber() ) );
        }
        if ( filter.getClassNumber() != null ) {
            criteria.add( Restrictions.eq( "classNumber", filter.getClassNumber() ));
        }
        if ( filter.getSubject() != null ) {
            criteria.add( Restrictions.eq( "participation.olympiadSubject", filter.getSubject() ) );
        }
        if ( filter.isSecondStage() ) {
            criteria.add( Restrictions.eq( "participation.stage", 1 ) );
        }
        if ( filter.isNeedApproval() ) {
            criteria.add( Restrictions.eq( "participation.approved", false ) );
        }

        if ( sortCriteria != null ) {
            AtomicBoolean lastNameOrder = new AtomicBoolean( false );
            sortCriteria.forEach( sc -> {
                if ( sc.getPropertyName().equalsIgnoreCase( "lastName" ) ) {
                    lastNameOrder.set( true );
                }
                if ( sc.getDirection() == SortCriterion.ASCENDING ) {
                    criteria.addOrder( Order.asc( "user."+ sc.getPropertyName() ) );
                }
                else if ( sc.getDirection() == SortCriterion.DESCENDING ) {
                    criteria.addOrder( Order.desc( "user." + sc.getPropertyName() ) );
                }
            } );
            if ( !lastNameOrder.get() ) {
                criteria.addOrder( Order.asc("user.lastName") );
            }
        }
        if ( filter.getSubject() != null || filter.isSecondStage() || filter.isNeedApproval() ) {
            criteria.setResultTransformer( CriteriaSpecification.DISTINCT_ROOT_ENTITY );
        }

        return  criteria;
    }
}
