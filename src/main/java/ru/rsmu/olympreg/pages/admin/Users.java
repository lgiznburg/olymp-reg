package ru.rsmu.olympreg.pages.admin;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.tapestry5.annotations.ActivationRequestParameter;
import org.apache.tapestry5.annotations.Property;
import ru.rsmu.olympreg.entities.User;
import ru.rsmu.olympreg.utils.CrudMode;

/**
 * @author leonid.
 */
@RequiresRoles("admin")
public class Users {
    @Property
    @ActivationRequestParameter
    private CrudMode mode;

    @Property
    @ActivationRequestParameter
    private User user;

    @Property
    private User listUser;

    public void setupRender() {
        listUser = user;
    }

    public void onToCreate() {
        mode = CrudMode.CREATE;
        user = null;
    }

    public void onUserSelectedFromList( User selectedUser ) {
        user = selectedUser;
        mode = CrudMode.REVIEW;
    }

    //------ Create

    void onCancelCreateFromEditor() {
        mode = null;
        user = null;
    }

    void onCreatedFromEditor( User createdUser ) {
        mode = CrudMode.REVIEW;
        user = createdUser;
    }

    //--------- Update

    void onCancelUpdateFromEditor( User updatedUser ) {
        mode = CrudMode.REVIEW;
        user = updatedUser;
    }

    void onUpdatedFromEditor( User updatedUser ) {
        mode = CrudMode.REVIEW;
        user = updatedUser;
    }

    //----------- Review

    void onToUpdateFromEditor( User updatedUser ) {
        mode = CrudMode.UPDATE;
        user = updatedUser;
    }

}
