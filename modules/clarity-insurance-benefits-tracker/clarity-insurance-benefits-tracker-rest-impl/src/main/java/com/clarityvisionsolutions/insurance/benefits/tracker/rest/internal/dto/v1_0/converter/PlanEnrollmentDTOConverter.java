package com.clarityvisionsolutions.insurance.benefits.tracker.rest.internal.dto.v1_0.converter;

import com.clarityvisionsolutions.insurance.benefits.tracker.rest.dto.v1_0.PlanEnrollment;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.internal.dto.v1_0.util.CreatorUtil;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.internal.dto.v1_0.util.MemberUtil;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.internal.resource.v1_0.BasePlanEnrollmentResourceImpl;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.InsurancePlanLocalService;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.PlanEnrollmentLocalService;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.PlanEnrollmentLocalService;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.PlanEnrollmentService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.util.JaxRsLinkUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import jakarta.ws.rs.core.UriInfo;
import java.util.HashMap;
import java.util.Map;

@Component(
        property = "dto.class.name=com.clarityvisionsolutions.insurance.benefits.tracker.model.PlanEnrollment",
        service = { DTOConverter.class, PlanEnrollmentDTOConverter.class }
)
public class PlanEnrollmentDTOConverter implements DTOConverter<com.clarityvisionsolutions.insurance.benefits.tracker.model.PlanEnrollment, PlanEnrollment> {
    @Override
    public String getContentType() {
        return PlanEnrollment.class.getSimpleName();
    }

    @Override
    public String getJaxRsLink(long classPK, UriInfo uriInfo) {
        return JaxRsLinkUtil.getJaxRsLink("headless-clarity-insurance-benefits-tracker", BasePlanEnrollmentResourceImpl.class, "getSitePlanEnrollment", uriInfo, classPK);
    }

    @Override
    public com.clarityvisionsolutions.insurance.benefits.tracker.model.PlanEnrollment getObject(String externalReferenceCode) throws Exception {
        return _planEnrollmentLocalService.getPlanEnrollmentByExternalReferenceCode(externalReferenceCode);
    }

    @Override
    public PlanEnrollment toDTO(DTOConverterContext dtoConverterContext) throws Exception {
        com.clarityvisionsolutions.insurance.benefits.tracker.model.PlanEnrollment model;

        model = _planEnrollmentService.getPlanEnrollment((long) dtoConverterContext.getId());

        return toDTO(dtoConverterContext, model);
    }

    @Override
    public PlanEnrollment toDTO(DTOConverterContext dtoConverterContext, com.clarityvisionsolutions.insurance.benefits.tracker.model.PlanEnrollment model) throws Exception {
        return toDTO((dtoConverterContext == null ? new HashMap<>() : dtoConverterContext.getActions()), model);
    }

    public PlanEnrollment toDTO(Map<String, Map<String, String>> actions, com.clarityvisionsolutions.insurance.benefits.tracker.model.PlanEnrollment model) throws Exception {
        return new PlanEnrollment() {
            {
                setActions(actions);
                setCreator(() -> CreatorUtil.toCreator(_portal, _userLocalService.getUser(model.getUserId())));
                setDateCreated(model::getCreateDate);
                setDateModified(model::getModifiedDate);
                setEndDate(model::getEndDate);
                setEnrollmentStatus(model::getEnrollmentStatus);
                setExternalReferenceCode(model::getExternalReferenceCode);
                setGroupNumber(model::getGroupNumber);
                setInsurancePlanERC(() -> _getInsurancePlanExternalReferenceCode(model.getInsurancePlanId()));
                setInsurancePlanId(model::getInsurancePlanId);
                setId(model::getPlanEnrollmentId);
                setMember(() -> MemberUtil.toMember(_portal, _userLocalService.getUser(model.getMemberUserId())));
                setMemberId(model::getMemberId);
                setNotes(model::getNotes);
                setSiteId(model::getGroupId);
                setStartDate(model::getStartDate);
                setStatus(model::getStatus);
            }
        };
    }

    private String _getInsurancePlanExternalReferenceCode(final long insurancePlanId) {
        com.clarityvisionsolutions.insurance.benefits.tracker.model.InsurancePlan plan = _insurancePlanLocalService.fetchInsurancePlan(insurancePlanId);

        if (Validator.isNotNull(plan)) {
            return plan.getExternalReferenceCode();
        }

        return null;
    }

    @Reference
    private InsurancePlanLocalService _insurancePlanLocalService;

    @Reference
    private PlanEnrollmentLocalService _planEnrollmentLocalService;

    @Reference
    private PlanEnrollmentService _planEnrollmentService;

    @Reference
    private Portal _portal;

    @Reference
    private UserLocalService _userLocalService;
}
