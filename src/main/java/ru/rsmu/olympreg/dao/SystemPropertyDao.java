package ru.rsmu.olympreg.dao;

import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import ru.rsmu.olympreg.entities.system.StoredProperty;
import ru.rsmu.olympreg.entities.system.StoredPropertyName;

import java.util.Date;

/**
 * @author leonid.
 * Interfece for service of handling system properties.
 */
public interface SystemPropertyDao extends BaseDao {

    String getProperty( StoredPropertyName propertyName );
    Integer getPropertyAsInt( StoredPropertyName propertyName );
    Long getPropertyAsLong( StoredPropertyName propertyName );
    Date getPropertyAsDate( StoredPropertyName name );

    @CommitAfter
    void saveProperty( StoredProperty property );
    StoredProperty find( StoredPropertyName propertyName );
}
