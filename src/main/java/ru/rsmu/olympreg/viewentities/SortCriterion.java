package ru.rsmu.olympreg.viewentities;

import org.apache.tapestry5.grid.SortConstraint;

/**
 * @author leonid.
 */
public class SortCriterion {
    public static final int UNSORTED = 0;
    public static final int ASCENDING = 1;
    public static final int DESCENDING = 2;

    private String propertyName;
    private int direction = UNSORTED;

    public SortCriterion( SortConstraint constraint ) {
        propertyName = constraint.getPropertyModel().getPropertyName();
        if ( propertyName.equals( "username" ) ) {
            propertyName = "user.username";
        }
        if ( propertyName.equals( "fullName" ) ) {
            propertyName = "user.lastName";
        }
        switch ( constraint.getColumnSort() ) {
            case ASCENDING:
                direction = SortCriterion.ASCENDING;
                break;
            case DESCENDING:
                direction = SortCriterion.DESCENDING;
                break;
            default:
                direction = SortCriterion.UNSORTED;
        }
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName( String propertyName ) {
        this.propertyName = propertyName;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection( int direction ) {
        this.direction = direction;
    }
}
