package ru.rsmu.olympreg.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author leonid.
 */
@Entity
@Table(name = "files_contents")
public class AttachedFileContent  implements Serializable {
    private static final long serialVersionUID = -8982504561457275186L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private byte[] content;

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent( byte[] content ) {
        this.content = content;
    }
}
