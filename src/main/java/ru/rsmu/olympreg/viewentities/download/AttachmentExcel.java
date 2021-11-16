
package ru.rsmu.olympreg.viewentities.download;

/**
 * @author leonid.
 */
public class AttachmentExcel extends AttachmentFile {

    public AttachmentExcel( byte[] document, String attachmentName ) {
        super( document, attachmentName );
    }

    @Override
    public String getContentType() {
        if ( getAttachmentName().matches( ".*\\.xlsx$" ) ) {
            return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        }
        return "application/vnd.ms-excel";
    }
}