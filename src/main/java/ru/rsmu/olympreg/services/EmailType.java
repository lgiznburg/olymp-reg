package ru.rsmu.olympreg.services;

/**
 * @author leonid.
 */
public enum EmailType {
    SIGNUP_RU("/emails/SignupRu.vm", "Регистрация для участия в олимпиаде для школьников в РНИМУ им.Н.И.Пирогова"),
    REMIND_PASSWORD( "/emails/RemindPassword.vm", "Восстановление пароля участника олимпиады для школьников в РНИМУ им.Н.И.Пирогова");

    public static final String EXAM_PASSWORD_SHORT_NAME = "EXAM_PASSWORD_";

    private final String fileName;
    private final String subject;

    EmailType( String fileName, String subject ) {
        this.fileName = fileName;
        this.subject = subject;
    }

    public String getFileName() {
        return fileName;
    }

    public String getSubject() {
        return subject;
    }

    public static EmailType findForLocale( String shortName, String localeName ) {
        return EmailType.valueOf( shortName + localeName.toUpperCase() );
    }
}
