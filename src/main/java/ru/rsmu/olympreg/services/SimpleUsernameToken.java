package ru.rsmu.olympreg.services;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author leonid.
 */
public class SimpleUsernameToken implements AuthenticationToken {

    private String username;

    private char[] passwordHash;

    public SimpleUsernameToken() {
    }

    public SimpleUsernameToken( final String username, final char[] passwordHash ) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public SimpleUsernameToken( final String username, final String passwordHash ) {
        this.username = username;
        this.passwordHash = passwordHash.toCharArray();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername( String username ) {
        this.username = username;
    }

    public char[] getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash( char[] passwordHash ) {
        this.passwordHash = passwordHash;
    }

    @Override
    public Object getPrincipal() {
        return getUsername();
    }

    @Override
    public Object getCredentials() {
        return getPasswordHash();
    }
}
