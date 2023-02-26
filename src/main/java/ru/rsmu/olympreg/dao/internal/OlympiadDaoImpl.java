package ru.rsmu.olympreg.dao.internal;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.IntegerType;
import ru.rsmu.olympreg.dao.OlympiadDao;
import ru.rsmu.olympreg.entities.OlympiadConfig;
import ru.rsmu.olympreg.entities.OlympiadSubject;
import ru.rsmu.olympreg.utils.YearHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author leonid.
 */
@SuppressWarnings( "unchecked" )
public class OlympiadDaoImpl extends BaseDaoImpl implements OlympiadDao {
    @Override
    public List<OlympiadConfig> getAllConfigs() {
        Criteria criteria = session.createCriteria( OlympiadConfig.class )
                .addOrder( Order.desc( "active" ) )
                .addOrder( Order.desc( "registrationStart" ) )
                .addOrder( Order.asc( "classNumber" ) )
                .addOrder( Order.asc( "subject" ) );
        return criteria.list();
    }

    @Override
    public OlympiadConfig findActiveForClassAndSubject( int classNumber, OlympiadSubject subject ) {
        Criteria criteria = session.createCriteria( OlympiadConfig.class )
                .add( Restrictions.eq( "classNumber", classNumber) )
                .add( Restrictions.eq( "subject", subject) )
                .add( Restrictions.eq( "active", true ) )
                .add( Restrictions.sqlRestriction( "year(registration_start) = ?",
                        YearHelper.getActualYear(), IntegerType.INSTANCE ) )
                .setMaxResults( 1 );
        return (OlympiadConfig) criteria.uniqueResult();
    }

    @Override
    public boolean checkRegistrationOpen() {
        Calendar calendar = Calendar.getInstance();
        Date currTime = calendar.getTime();
        calendar.add( Calendar.DAY_OF_YEAR, -1 );
        Date nextDay = calendar.getTime();
        Criteria criteria = session.createCriteria( OlympiadConfig.class )
                .add( Restrictions.le( "registrationStart", currTime) )
                .add( Restrictions.gt( "registrationEnd", nextDay ) )
                .setMaxResults( 1 );
        return criteria.uniqueResult() != null;
    }

    @Override
    public boolean checkSecondRegistrationOpen() {
        Calendar calendar = Calendar.getInstance();
        Date currTime = calendar.getTime();
        calendar.add( Calendar.DAY_OF_YEAR, -1 );
        Date nextDay = calendar.getTime();
        Criteria criteria = session.createCriteria( OlympiadConfig.class )
                .add( Restrictions.le( "secondStageRegistrationStart", currTime) )
                .add( Restrictions.gt( "secondStageRegistrationEnd", nextDay ) )
                .setMaxResults( 1 );
        return criteria.uniqueResult() != null;
    }
}
