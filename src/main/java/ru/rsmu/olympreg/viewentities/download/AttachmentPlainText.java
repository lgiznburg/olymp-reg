package ru.rsmu.olympreg.viewentities.download;

/**
 * @author leonid.
 */
public class AttachmentPlainText extends AttachmentFile {

    public AttachmentPlainText( byte[] document, String attachmentName ) {
        super( document, attachmentName );
    }

    @Override
    public String getContentType() {
        return "text/plain";
    }
}
