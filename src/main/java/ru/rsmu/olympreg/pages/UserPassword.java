package ru.rsmu.olympreg.pages;

import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.tapestry5.Field;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.olympreg.dao.UserDao;
import ru.rsmu.olympreg.entities.User;
import ru.rsmu.olympreg.entities.UserRole;
import ru.rsmu.olympreg.services.SecurityUserHelper;

import java.util.List;

@RequiresUser
public class UserPassword {

    /** properties */

    @Property
    private User user;

    @Property
    private String userPassword;

    @Property
    private String currentPassword;

    @Property
    private String userPasswordConfirm;


    @Property
    private String fullname;

    /** injections */

    @Inject
    private SecurityUserHelper securityUserHelper;

    @Inject
    private UserDao userDao;

    @Inject
    private Messages messages;

    @InjectComponent
    private Form updatePasswordForm;

    @InjectComponent
    private Field newpassword;

    @InjectComponent
    private Field currentpassword;

    private List<UserRole> roles;


    public void setupRender() {
        user = securityUserHelper.getCurrentUser();
        fullname = user.getLastName() + " " + user.getFirstName() + " " + user.getMiddleName();

    }

    boolean onValidateFromUpdatePasswordForm() {
        if ( userPassword != null && !userPassword.isEmpty() ) {
            this.user = userDao.find( User.class, securityUserHelper.getCurrentUser().getId() );
            if(currentPassword == null) {
                updatePasswordForm.recordError( currentpassword, messages.get("current-password-empty"));
            }
            else if ( ! userDao.encrypt(currentPassword).equals( user.getPassword())) { //check if user knows old password
                updatePasswordForm.recordError( currentpassword, messages.get("current-password-wrong"));
            }
            else if ( userPassword.length() < 7 || (!userPassword.matches( ".*\\W+.*" ) && !userPassword.matches( ".*\\w+.*" ) ) ) {
                updatePasswordForm.recordError( newpassword, messages.get("password-form-wrong"));
            }
            else if ( ! userPassword.equals( userPasswordConfirm ) ) {
                updatePasswordForm.recordError( newpassword, messages.get("passwords-dont-match"));
            }
            else {
                // Finally, update the password
                this.user.setPassword( userDao.encrypt( userPassword ) );
            }
        }
        else {
            updatePasswordForm.recordError(messages.get("password-form-empty"));
        }

        return true;
    }

    Object onSuccessFromUpdatePasswordForm() {
        userDao.save( user );
        return Index.class;
    }

    Object onCancelUpdate() {
        return Index.class;
    }
}
