package ru.rsmu.olympreg.dao.internal;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import ru.rsmu.olympreg.dao.EmailDao;
import ru.rsmu.olympreg.entities.EmailQueue;
import ru.rsmu.olympreg.entities.EmailQueueStatus;

import java.util.List;

/**
 * @author leonid.
 */
@SuppressWarnings( "unchecked" )
public class EmailDaoImpl extends BaseDaoImpl implements EmailDao {
    @Override
    public List<EmailQueue> findNewEmails() {
        Criteria criteria = session.createCriteria( EmailQueue.class )
                .add( Restrictions.eq( "status", EmailQueueStatus.NEW ) )
                .addOrder( Order.asc( "createdDate" ) )
                .setMaxResults( 50 );
        return criteria.list();
    }
}
