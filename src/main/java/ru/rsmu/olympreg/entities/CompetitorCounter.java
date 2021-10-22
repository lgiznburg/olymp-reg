package ru.rsmu.olympreg.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author leonid.
 */
@Entity
@Table( name = "competitor_counter")
public class CompetitorCounter implements Serializable {
    private static final long serialVersionUID = -6558587244719447920L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private long id;

    @Column(unique = true)
    private int year;

    @Column
    private int counter;

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear( int year ) {
        this.year = year;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter( int counter ) {
        this.counter = counter;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !(o instanceof CompetitorCounter) ) return false;
        CompetitorCounter that = (CompetitorCounter) o;
        return year == that.year;
    }

    @Override
    public int hashCode() {
        return Objects.hash( year );
    }
}
