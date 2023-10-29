package ru.rsmu.olympreg.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author leonid.
 */
@Entity
@Table(name = "countries")
public class Country implements Serializable {
    private static final long serialVersionUID = -5608192823126010471L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "short_name")
    private String shortName;

    @Column
    private String name;

    @Column
    private String iso2;

    @Column(unique = true)
    private String iso3;

    @Column
    private String oksm;

    public Country() {
    }

    public Country( String shortName, String name, String iso3, String iso2, String oksm ) {
        this.shortName = shortName;
        this.name = name;
        this.iso2 = iso2;
        this.iso3 = iso3;
        this.oksm = oksm;
    }

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName( String shortName ) {
        this.shortName = shortName;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getIso2() {
        return iso2;
    }

    public void setIso2( String iso2 ) {
        this.iso2 = iso2;
    }

    public String getIso3() {
        return iso3;
    }

    public void setIso3( String iso3 ) {
        this.iso3 = iso3;
    }

    public String getOksm() {
        return oksm;
    }

    public void setOksm( String oksm ) {
        this.oksm = oksm;
    }
}
