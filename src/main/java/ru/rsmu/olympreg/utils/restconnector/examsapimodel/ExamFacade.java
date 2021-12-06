package ru.rsmu.olympreg.utils.restconnector.examsapimodel;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * @author leonid.
 */
@XmlRootElement( name = "exam" )
public class ExamFacade {
    private long id;
    private String subject;
    private String eventType;
    private String name;
    private Date date;


    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject( String subject ) {
        this.subject = subject;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType( String eventType ) {
        this.eventType = eventType;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate( Date date ) {
        this.date = date;
    }
}
