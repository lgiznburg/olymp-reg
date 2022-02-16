package ru.rsmu.olympreg.dao.internal;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import ru.rsmu.olympreg.dao.OlympiadDao;
import ru.rsmu.olympreg.entities.OlympiadConfig;
import ru.rsmu.olympreg.entities.OlympiadSubject;

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
                .setMaxResults( 1 );
        return (OlympiadConfig) criteria.uniqueResult();
    }
}
