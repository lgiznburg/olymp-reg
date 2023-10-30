package ru.rsmu.olympreg.encoders;

import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ValueEncoderFactory;
import ru.rsmu.olympreg.dao.CountryDao;
import ru.rsmu.olympreg.entities.Country;

/**
 * @author leonid.
 */
public class CountryValueEncoder implements ValueEncoder<Country>, ValueEncoderFactory<Country> {

    @Inject
    private CountryDao countryDao;

    @Override
    public String toClient( Country value ) {
        return value == null ? "" : value.getName();
    }

    @Override
    public Country toValue( String clientValue ) {
        long id = Long.parseLong( clientValue );
        return countryDao.find( Country.class, id );
    }

    @Override
    public ValueEncoder<Country> create( Class<Country> type ) {
        return this;
    }
}
