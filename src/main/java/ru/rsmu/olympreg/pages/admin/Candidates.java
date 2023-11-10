package ru.rsmu.olympreg.pages.admin;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.tapestry5.annotations.ActivationRequestParameter;
import org.apache.tapestry5.annotations.Property;
import ru.rsmu.olympreg.entities.UserCandidate;

/**
 * @author leonid.
 */
@RequiresRoles("admin")
public class Candidates {

    @Property
    @ActivationRequestParameter
    private UserCandidate candidate;

    public void onCandidateSelectedFromList( UserCandidate selected ) {
        candidate = selected;
    }

    public void onSuccess() {
        candidate = null;
    }
}
