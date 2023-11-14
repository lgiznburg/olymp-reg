package ru.rsmu.olympreg.entities;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author leonid.
 */
@Entity
@Table(name = "user_candidates")
public class UserCandidate implements Serializable {
    private static final long serialVersionUID = -8753285088404856390L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Long id;

    @Column
    private String email = "";

    @Column( name = "created_date")
    @Temporal( TemporalType.TIMESTAMP )
    private Date createdDate;

    @Column
    private String password = "";

    @Column
    private String firstName = "";

    @Column
    private String middleName = "";

    @Column
    private String lastName = "";

    @Column(name = "key_code")
    private String keyCode;

    @Column
    @Enumerated(EnumType.ORDINAL)
    private UserCandidateType type = UserCandidateType.account;

    public UserCandidateType getType() {
        return type;
    }

    public void setType( UserCandidateType type ) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail( String email ) {
        this.email = email;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate( Date createdDate ) {
        this.createdDate = createdDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword( String password ) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName( String firstName ) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName( String middleName ) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName( String lastName ) {
        this.lastName = lastName;
    }

    public String getKeyCode() {
        return keyCode;
    }

    public void setKeyCode( String keyCode ) {
        this.keyCode = keyCode;
    }

    @Transient
    public String getFullName() {
        StringBuilder builder = new StringBuilder( lastName );
        if ( StringUtils.isNotBlank( firstName ) ) {
            builder.append( " " ).append( firstName );
        }
        if ( StringUtils.isNotBlank( middleName ) ) {
            builder.append( " " ).append( middleName );
        }
        return builder.toString();
    }
}
