package ru.rsmu.olympreg.components.admin;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.olympreg.dao.OlympiadDao;
import ru.rsmu.olympreg.entities.OlympiadConfig;
import ru.rsmu.olympreg.entities.User;
import ru.rsmu.olympreg.entities.UserRole;
import ru.rsmu.olympreg.utils.CrudMode;

import java.util.ArrayList;

/**
 * @author leonid.
 */
public class ConfigCrud {

    // Parameters

    @Parameter(required = true)
    @Property
    private CrudMode mode;

    @Parameter(required = true)
    @Property
    private OlympiadConfig olympiadConfig;


    @Inject
    private OlympiadDao olympiadDao;

    @Inject
    private Messages messages;

    @InjectComponent
    private Form createForm;

    @InjectComponent
    private Form updateForm;

    @Inject
    private ComponentResources componentResources;

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
        OlympiadConfig activeConfig = olympiadDao.findActiveForClassAndSubject( olympiadConfig.getClassNumber(), olympiadConfig.getSubject() );
        if ( activeConfig != null ) {

            return false;
        }
        return true;
    }

    boolean onSuccessFromCreateForm() {
        olympiadDao.save( olympiadConfig );

        // We want to tell our containing page explicitly what person we've created, so we trigger a new event with a
        // parameter. It will bubble up because we don't have a handler method for it.
        componentResources.triggerEvent( "created", new Object[]{olympiadConfig}, null );

        // We don't want the original event to bubble up, so we return true to say we've handled it.
        return true;
    }

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
        return true;
    }

    boolean onSuccessFromUpdateForm() {
        olympiadDao.save( olympiadConfig );

        // We want to tell our containing page explicitly what person we've created, so we trigger a new event with a
        // parameter. It will bubble up because we don't have a handler method for it.
        componentResources.triggerEvent( "updated", new Object[]{olympiadConfig}, null );

        // We don't want the original event to bubble up, so we return true to say we've handled it.
        return true;
    }

//----
    public String getSubjectName() {
        if ( olympiadConfig != null ) {
            return messages.get( olympiadConfig.getSubject().name() );
        }
        return "";
    }

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
                olympiadConfig = new OlympiadConfig();
                break;
            case UPDATE:
                break;
        }
    }


}
