package ru.rsmu.olympreg.dao.internal;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import ru.rsmu.olympreg.dao.EmailDao;
import ru.rsmu.olympreg.entities.AttachedFile;
import ru.rsmu.olympreg.entities.AttachedFileContent;
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

    @Override
    public void removeOldEmailAttachment() {
        AttachedFile file = findEmailAttachment();
        if ( file != null ) {
            AttachedFileContent content = find( AttachedFileContent.class, file.getContentId() );
            delete( file );
            if ( content != null ) {
                delete( content );
            }        }
    }

    @Override
    public AttachedFile findEmailAttachment() {
        Criteria criteria = session.createCriteria( AttachedFile.class )
                .add( Restrictions.isNull( "profile" ) )
                .add( Restrictions.isNull( "attachmentRole" ) )
                .setMaxResults( 1 );
        return (AttachedFile) criteria.uniqueResult();
    }
}
