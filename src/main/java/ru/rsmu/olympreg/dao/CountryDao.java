package ru.rsmu.olympreg.dao;

import ru.rsmu.olympreg.entities.Country;

import java.util.List;

/**
 * @author leonid.
 */
public interface CountryDao extends BaseDao {
    List<String> findNames( String startsWith, int maxResults );

    Country findByName( String name );
    Country findByIso2( String iso2 );
}
