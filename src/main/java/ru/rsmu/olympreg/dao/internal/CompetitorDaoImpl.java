package ru.rsmu.olympreg.dao.internal;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.*;
import org.hibernate.type.IntegerType;
import ru.rsmu.olympreg.dao.CompetitorDao;
import ru.rsmu.olympreg.entities.CompetitorProfile;
import ru.rsmu.olympreg.entities.OlympiadSubject;
import ru.rsmu.olympreg.entities.ParticipationInfo;
import ru.rsmu.olympreg.entities.ProfileStage;
import ru.rsmu.olympreg.utils.YearHelper;
import ru.rsmu.olympreg.viewentities.CompetitorFilter;
import ru.rsmu.olympreg.viewentities.SortCriterion;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

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
        ProjectionList projectionList = Projections.projectionList()
                .add( Projections.distinct( Projections.id() ) );

        if ( sortCriteria != null ) {
            AtomicBoolean lastNameOrder = new AtomicBoolean( false );
            sortCriteria.forEach( sc -> {
                if ( sc.getPropertyName().equalsIgnoreCase( "lastName" ) ) {
                    lastNameOrder.set( true );
                }
                projectionList.add( Projections.property( "user." + sc.getPropertyName() ) );
            } );
            if ( !lastNameOrder.get() ) {
                projectionList.add( Projections.property( "user.lastName") );
            }
            criteria.setProjection( projectionList );
        }

        List<Long> idList = (List<Long>) criteria.list().stream()
                .map( a -> ((Object[])a)[0] )
                .collect( Collectors.toList());
        if ( idList.isEmpty() ) return Collections.emptyList();
        Criteria second = session.createCriteria( CompetitorProfile.class )
                .createAlias( "user", "user" )
                .add( Restrictions.in( "id", idList ) );
        addSortCriteria( second, sortCriteria );

        return second.list();
    }

    @Override
    public int countSubjectSelectedProfiles() {
        Criteria criteria = session.createCriteria( ParticipationInfo.class )
                .createAlias( "profile", "profile" )
                .add( Restrictions.eq( "profile.year", YearHelper.getActualYear() ) )
                .setProjection( Projections.countDistinct( "profile" ) );
        return ((Long)criteria.uniqueResult()).intValue();
    }

    @Override
    public List<Object[]> findDetailedStats() {
        Criteria criteria = session.createCriteria( ParticipationInfo.class )
                .createAlias( "profile", "profile" )
                .add( Restrictions.eq( "profile.year", YearHelper.getActualYear() ) )
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
                .add( Restrictions.eq( "profile.year", YearHelper.getActualYear() ) )
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
                .add( Restrictions.eq( "year", YearHelper.getActualYear() ) )
                .add( Restrictions.ne( "profileStage", ProfileStage.NEW ) )
                .setProjection( Projections.rowCount() );
        return ((Long)criteria.uniqueResult()).intValue();
    }

    @Override
    public int countUploadCompleted() {
        Query query = session.createQuery(
                "SELECT count(*) FROM CompetitorProfile cp " +
                        "WHERE cp.year = :year " +
                        "AND 3 = (SELECT count( distinct af.attachmentRole) FROM AttachedFile af WHERE af.profile = cp )"
        )
                .setParameter( "year", YearHelper.getActualYear() );
        return ((Long)query.uniqueResult()).intValue();
    }

    @Override
    public List<ParticipationInfo> findNewParticipations( int classNumber, OlympiadSubject olympiadSubject, int maxResults ) {
        Criteria criteria = session.createCriteria( ParticipationInfo.class )
                .createAlias( "profile", "profile" )
                .add( Restrictions.eq( "profile.year", YearHelper.getActualYear() ) )
                .add( Restrictions.eq( "profile.classNumber", classNumber ) )
                .add( Restrictions.isNotNull( "profile.region" ) )
                .add( Restrictions.eq( "olympiadSubject", olympiadSubject ) )
                .add( Restrictions.isNull( "examName" ) )
                .add( Restrictions.eq( "approved", true ))
                .setMaxResults( maxResults );
        return criteria.list();
    }

    @Override
    public List<Object[]> findExamsWithNoResults() {
        Criteria criteria = session.createCriteria( ParticipationInfo.class )
                .createAlias( "profile", "profile" )
                .add( Restrictions.eq( "profile.year", YearHelper.getActualYear() ) )
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
                .add( Restrictions.eq( "profile.year", YearHelper.getActualYear() ) )
                .add( Restrictions.eq( "profile.caseNumber", caseNumber ) )
                .add( Restrictions.eq( "examId", examId ) );
        return criteria.list();
    }

    @Override
    public List<CompetitorProfile> findForSecondStage( OlympiadSubject subject, int classNumber, int secondStagePassScore ) {
        Criteria criteria = session.createCriteria( CompetitorProfile.class )
                .createAlias( "participation", "participation" )
                .add( Restrictions.eq( "classNumber", classNumber ) )
                .add( Restrictions.eq( "year", YearHelper.getActualYear() ) )
                .add( Restrictions.eq( "participation.olympiadSubject", subject ) )
                .add( Restrictions.eq( "participation.stage", 0 ) )
                .add( Restrictions.ge( "participation.result", secondStagePassScore ) )
                .setResultTransformer( CriteriaSpecification.DISTINCT_ROOT_ENTITY );

        return criteria.list();
    }

    @Override
    public CompetitorProfile findProfile( String personalNumber ) {
        Criteria criteria = session.createCriteria( CompetitorProfile.class )
                .add( Restrictions.eq( "caseNumber", personalNumber ) )
                .setMaxResults( 1 );
        return (CompetitorProfile) criteria.uniqueResult();
    }

    private Criteria buildCriteria( CompetitorFilter filter, List<SortCriterion> sortCriteria ) {
        Criteria criteria = session.createCriteria( CompetitorProfile.class )
                .createAlias( "user", "user" )
                .add( Restrictions.eq( "year", YearHelper.getActualYear() ) );
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

        addSortCriteria( criteria, sortCriteria );
        if ( filter.getSubject() != null || filter.isSecondStage() || filter.isNeedApproval() ) {
            criteria.setResultTransformer( CriteriaSpecification.DISTINCT_ROOT_ENTITY );
        }

        return  criteria;
    }

    private void addSortCriteria( Criteria criteria, List<SortCriterion> sortCriteria ) {
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
    }
}
