package ru.rsmu.olympreg.components.admin;

import org.apache.tapestry5.annotations.Events;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.internal.grid.CollectionGridDataSource;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import ru.rsmu.olympreg.dao.UserDao;
import ru.rsmu.olympreg.entities.UserCandidate;
import ru.rsmu.olympreg.viewentities.CandidateDataSource;
import ru.rsmu.olympreg.viewentities.CompetitorFilter;

import java.util.Objects;

/**
 * @author leonid.
 */
@Events( {CandidatesList.CANDIDATE_SELECTED} )  // just for documentation
public class CandidatesList {
    public static final String CANDIDATE_SELECTED = "candidateSelected";

    @Parameter(required = true)
    @Property
    private UserCandidate candidate;

    @Property
    private UserCandidate candidateIt;

    @Property
    private CompetitorFilter filter = new CompetitorFilter();

    @Inject
    private UserDao userDao;

    @Inject
    private BeanModelSource beanModelSource;

    @Inject
    private Messages messages;

    @Inject
    private Request request;

    @InjectComponent
    private Zone candidateListZone;

    @Inject
    private AjaxResponseRenderer ajaxResponseRenderer;

    public GridDataSource getCandidateDatasource() {
        return new CandidateDataSource( userDao, filter );
    }

    public String getLinkCssClass() {
        if ( candidate != null && candidateIt != null && Objects.equals( candidate.getId(), candidateIt.getId() ) ) {
            return "active";
        }
        return "";
    }

    public void onSuccessFromFilterForm() {
        if ( request.isXHR() ) {
            ajaxResponseRenderer.addRender( candidateListZone );
        }
    }
}
