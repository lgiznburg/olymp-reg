package ru.rsmu.olympreg.pages.control;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import ru.rsmu.olympreg.dao.CompetitorDao;
import ru.rsmu.olympreg.dao.UserDao;
import ru.rsmu.olympreg.entities.CompetitorProfile;
import ru.rsmu.olympreg.viewentities.CompetitorDataSource;
import ru.rsmu.olympreg.viewentities.CompetitorFilter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author leonid.
 */
@RequiresRoles( value = {"admin", "manager", "moderator"}, logical = Logical.OR)
public class Profiles {

    @Property
    private CompetitorProfile profile;

    @Property
    private CompetitorFilter filter = new CompetitorFilter();

   /* @Property
    private BeanModel<CompetitorProfile> profileBeanModel;*/

    @Inject
    private CompetitorDao competitorDao;

    @Inject
    private BeanModelSource beanModelSource;

    @Inject
    private Messages messages;

    @Inject
    private Request request;

    @InjectComponent
    private Zone profilesZone;

    @Inject
    private AjaxResponseRenderer ajaxResponseRenderer;


    public BeanModel<CompetitorProfile> getProfileBeanModel() {
        BeanModel<CompetitorProfile> profileBeanModel = beanModelSource.createDisplayModel( CompetitorProfile.class, messages );

        profileBeanModel.addExpression( "username", "user.username" );
        profileBeanModel.addExpression( "fullName","user.lastName" );
        profileBeanModel.include( "caseNumber", "username", "fullName", "profileCompleted", "attachmentsCompleted", "subjectSelected" )
                .exclude( "id", "sex", "birthDate", "region", "schoolNumber", "phoneNumber", "classNumber", "schoolLocation", "profileStage" )
                .reorder( "caseNumber", "username", "fullName", "profileCompleted", "attachmentsCompleted", "subjectSelected" );
        profileBeanModel.get( "username").sortable( true ).label( "Email" );
        profileBeanModel.get( "fullName").sortable( true );
        profileBeanModel.get( "profileCompleted").sortable( false );
        profileBeanModel.get( "attachmentsCompleted").sortable( false );
        profileBeanModel.get( "subjectSelected").sortable( false );
        return profileBeanModel;
    }

    public GridDataSource getProfileDataSource() {
        return new CompetitorDataSource( competitorDao, filter );
    }

    public void onSuccessFromFilterForm() {
        if ( request.isXHR() ) {
            ajaxResponseRenderer.addRender( profilesZone );
        }
    }

    public Map<String, Object> getExportParams() {
        Map<String, Object> exportParams = new HashMap<String, Object>();
        exportParams.put( "personalNumber", filter.getPersonalNumber() );
        exportParams.put( "email", filter.getEmail() );
        exportParams.put( "firstName", filter.getFirstName() );
        exportParams.put( "lastName", filter.getLastName() );
        exportParams.put( "subject", filter.getSubject() == null ? "" : filter.getSubject().name() );
        exportParams.put( "secondStage", filter.isSecondStage() );
        exportParams.put( "needApproval", filter.isNeedApproval() );
        exportParams.put( "classNumber", filter.getClassNumber() );
        return exportParams;
    }
}
