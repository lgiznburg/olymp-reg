package ru.rsmu.olympreg.services;

import ru.rsmu.olympreg.entities.User;

import java.util.Map;

/**
 * @author leonid.
 */
public interface EmailService {
    void sendEmail( User user, EmailType emailType, Map<String,Object> model );
    void sendEmail( String to, EmailType emailType, Map<String,Object> model );
}
