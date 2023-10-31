package ru.rsmu.olympreg.entities;

/**
 * @author leonid.
 */
public enum SchoolLocation {
    LOCATION_CITY ( "Город" ),
    LOCATION_COUNTRY ( "Сельская месность" );

    private String translated;

    SchoolLocation( String translated ) {
        this.translated = translated;
    }

    public String getTranslated(){
        return translated;
    }
}
