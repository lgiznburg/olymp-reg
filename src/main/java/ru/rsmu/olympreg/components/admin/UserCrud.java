package ru.rsmu.olympreg.components.admin;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.Field;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.SelectModelFactory;
import org.apache.tapestry5.services.ValueEncoderSource;
import ru.rsmu.olympreg.dao.UserDao;
import ru.rsmu.olympreg.entities.User;
import ru.rsmu.olympreg.entities.UserRole;
import ru.rsmu.olympreg.utils.CrudMode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author leonid.
 */
public class UserCrud {
    public static final String CANCEL_CREATE = "cancelCreate";
    public static final String CREATED = "created";
    public static final String TO_UPDATE = "toUpdate";
    public static final String CANCEL_UPDATE = "cancelUpdate";
    public static final String UPDATED = "updated";
    public static final String DELETED = "deleted";

    // Parameters

    @Parameter(required = true)
    @Property
    private CrudMode mode;

    @Parameter(required = true)
    @Property
    private User user;

    @Property
    private SelectModel rolesModel;

    @Property
    private SelectModel subjectsModel;

    @Property
    private String userPassword;

    @Property
    private String userPasswordConfirm;

    // ----
    @Inject
    private UserDao userDao;

    @InjectComponent
    private Form createForm;

    @InjectComponent
    private Form updateForm;

    @InjectComponent
    private Field password;

    @Inject
    private ComponentResources componentResources;

    @Inject
    private SelectModelFactory modelFactory;

    private List<UserRole> roles;

    @Inject
    private Messages messages;

    @Inject
    private ValueEncoderSource valueEncoderSource;

    public void setupRender() {
/*
        if ( mode == CrudMode.REVIEW ) {
            user = userId == null ? null : userDao.find( User.class, userId );
        }
*/
    }

    //------ Create

    void onPrepareForRenderFromCreateForm() throws Exception {
        // If fresh start, make sure there's a User object available.
        if (createForm.isValid()) {
            prepare();
        }
    }

    void onPrepareForSubmitFromCreateForm() throws Exception {
        prepare();
    }

    boolean onValidateFromCreateForm() {
        if ( userPassword == null
                || userPassword.length() < 7
                || (!userPassword.matches( ".*\\W+.*" ) && !userPassword.matches( ".*\\w+.*" ) ) ) {
            createForm.recordError( password, messages.get("password-form-wrong"));
        }
        else if ( !userPassword.equals( userPasswordConfirm ) ) {
            createForm.recordError( password, messages.get("passwords-dont-match"));
        }
        else {
            user.setPassword( userDao.encrypt( userPassword ) );
        }

        return true;
    }

    boolean onSuccessFromCreateForm() {
        userDao.save( user );

        // We want to tell our containing page explicitly what person we've created, so we trigger a new event with a
        // parameter. It will bubble up because we don't have a handler method for it.
        componentResources.triggerEvent( CREATED, new Object[]{user}, null );

        // We don't want the original event to bubble up, so we return true to say we've handled it.
        return true;
    }


    //------ Review

    //------ Update

    void onPrepareForRenderFromUpdateForm() throws Exception {
        // If fresh start, make sure there's a User object available.
        if (updateForm.isValid()) {
            prepare();
        }
    }

    void onPrepareForSubmitFromUpdateForm() throws Exception {
        prepare();
    }

    boolean onValidateFromUpdateForm() {
        if ( userPassword != null && !userPassword.isEmpty() ) {
            if ( userPassword.length() < 7 || (!userPassword.matches( ".*\\W+.*" ) && !userPassword.matches( ".*\\w+.*" ) ) ) {
                updateForm.recordError( password, messages.get("password-form-wrong"));
            }
            else if ( ! userPassword.equals( userPasswordConfirm ) ) {
                updateForm.recordError( password, messages.get("passwords-dont-match"));
            }
            else {
                user.setPassword( userDao.encrypt( userPassword ) );
            }
        }

        return true;
    }

    boolean onSuccessFromUpdateForm() {
        userDao.save( user );

        // We want to tell our containing page explicitly what person we've created, so we trigger a new event with a
        // parameter. It will bubble up because we don't have a handler method for it.
        componentResources.triggerEvent( UPDATED, new Object[]{user}, null );

        // We don't want the original event to bubble up, so we return true to say we've handled it.
        return true;
    }


    //------

    public boolean isModeCreate() {
        return mode == CrudMode.CREATE;
    }

    public boolean isModeReview() {
        return mode == CrudMode.REVIEW;
    }

    public boolean isModeUpdate() {
        return mode == CrudMode.UPDATE;
    }

    private void prepare() {
        switch ( mode ) {
            case CREATE:
                user = new User();
                user.setRoles( new ArrayList<>() );
                break;
            case UPDATE:
                break;
        }
        roles = userDao.findAll( UserRole.class );
        rolesModel = modelFactory.create( roles, "roleName" );
    }

    public ValueEncoder<UserRole> getRoleEncoder() {
        return valueEncoderSource.getValueEncoder( UserRole.class );
    }


    public String getRoleNames() {
        if ( user == null ) return "";
        List<String> roleNames = user.getRoles().stream().map( role -> role.getRoleName().name() ).collect( Collectors.toList() );
        return StringUtils.join( roleNames, ", " );
    }

}
