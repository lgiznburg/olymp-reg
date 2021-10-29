package ru.rsmu.olympreg.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author leonid.
 */
@Entity
@Table(name="attached_files")
public class AttachedFile implements Serializable {
    private static final long serialVersionUID = 6222122239076905391L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "source_name")
    private String sourceName;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private CompetitorProfile profile;

    @Column( name = "attachment_role")
    @Enumerated( EnumType.STRING )
    private AttachmentRole attachmentRole;

    @Column(name = "file_size")
    private Integer size;

    @Column(name = "content_id")
    private Long contentId;

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }


    public Long getContentId() {
        return contentId;
    }

    public void setContentId( Long contentId ) {
        this.contentId = contentId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType( String contentType ) {
        this.contentType = contentType;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName( String sourceName ) {
        this.sourceName = sourceName;
    }

    public CompetitorProfile getProfile() {
        return profile;
    }

    public void setProfile( CompetitorProfile profile ) {
        this.profile = profile;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize( Integer size ) {
        this.size = size;
    }

    public AttachmentRole getAttachmentRole() {
        return attachmentRole;
    }

    public void setAttachmentRole( AttachmentRole attachmentRole ) {
        this.attachmentRole = attachmentRole;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        AttachedFile that = (AttachedFile) o;
        return Objects.equals( id, that.id ) &&
                Objects.equals( contentType, that.contentType ) &&
                Objects.equals( sourceName, that.sourceName );
    }

    @Override
    public int hashCode() {
        return Objects.hash( id, contentType, sourceName );
    }
}
