package ru.rsmu.olympreg.pages.admin;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.tapestry5.annotations.ActivationRequestParameter;
import org.apache.tapestry5.annotations.Property;
import ru.rsmu.olympreg.entities.OlympiadConfig;
import ru.rsmu.olympreg.entities.OlympiadConfig;
import ru.rsmu.olympreg.utils.CrudMode;

/**
 * @author leonid.
 */
@RequiresRoles("admin")
public class OlympConfigs {
    @Property
    @ActivationRequestParameter
    private CrudMode mode;

    @Property
    @ActivationRequestParameter
    private OlympiadConfig olympiadConfig;
    
    @Property
    private OlympiadConfig listConfig;

    public void setupRender() {
        listConfig = olympiadConfig;
    }

    public void onToCreate() {
        mode = CrudMode.CREATE;
        olympiadConfig = null;
    }

    public void onConfigSelectedFromList( OlympiadConfig selectedConfig ) {
        olympiadConfig = selectedConfig;
        mode = CrudMode.REVIEW;
    }

    //------ Create

    void onCancelCreateFromEditor() {
        mode = null;
        olympiadConfig = null;
    }

    void onCreatedFromEditor( OlympiadConfig olympiadConfig ) {
        mode = CrudMode.REVIEW;
        this.olympiadConfig = olympiadConfig;
    }

    //--------- Update

    void onCancelUpdateFromEditor( OlympiadConfig updatedConfig ) {
        mode = CrudMode.REVIEW;
        olympiadConfig = updatedConfig;
    }

    void onUpdatedFromEditor( OlympiadConfig updatedConfig ) {
        mode = CrudMode.REVIEW;
        olympiadConfig = updatedConfig;
    }

    //----------- Review

    void onToUpdateFromEditor( OlympiadConfig updatedConfig ) {
        mode = CrudMode.UPDATE;
        olympiadConfig = updatedConfig;
    }


}
