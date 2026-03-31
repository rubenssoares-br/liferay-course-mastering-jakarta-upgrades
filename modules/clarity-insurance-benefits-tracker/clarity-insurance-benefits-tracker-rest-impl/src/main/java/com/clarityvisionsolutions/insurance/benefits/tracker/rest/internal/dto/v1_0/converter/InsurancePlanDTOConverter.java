package com.clarityvisionsolutions.insurance.benefits.tracker.rest.internal.dto.v1_0.converter;

import com.clarityvisionsolutions.insurance.benefits.tracker.rest.dto.v1_0.InsurancePlan;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.internal.dto.v1_0.util.CreatorUtil;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.internal.resource.v1_0.BaseInsurancePlanResourceImpl;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.InsurancePlanLocalService;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.InsurancePlanService;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.PlanEnrollmentLocalService;
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
        property = "dto.class.name=com.clarityvisionsolutions.insurance.benefits.tracker.model.InsurancePlan",
        service = { DTOConverter.class, InsurancePlanDTOConverter.class }
)
public class InsurancePlanDTOConverter implements DTOConverter<com.clarityvisionsolutions.insurance.benefits.tracker.model.InsurancePlan, InsurancePlan> {
    @Override
    public String getContentType() {
        return InsurancePlan.class.getSimpleName();
    }

    @Override
    public String getJaxRsLink(long classPK, UriInfo uriInfo) {
        return JaxRsLinkUtil.getJaxRsLink("headless-clarity-insurance-benefits-tracker", BaseInsurancePlanResourceImpl.class, "getSiteInsurancePlan", uriInfo, classPK);
    }

    @Override
    public com.clarityvisionsolutions.insurance.benefits.tracker.model.InsurancePlan getObject(String externalReferenceCode) throws Exception {
        return _InsurancePlanLocalService.getInsurancePlanByExternalReferenceCode(externalReferenceCode);
    }

    @Override
    public InsurancePlan toDTO(DTOConverterContext dtoConverterContext) throws Exception {
        com.clarityvisionsolutions.insurance.benefits.tracker.model.InsurancePlan model;

        model = _InsurancePlanService.getInsurancePlan((long) dtoConverterContext.getId());

        return toDTO(dtoConverterContext, model);
    }

    @Override
    public InsurancePlan toDTO(DTOConverterContext dtoConverterContext, com.clarityvisionsolutions.insurance.benefits.tracker.model.InsurancePlan model) throws Exception {
        return toDTO((dtoConverterContext == null ? new HashMap<>() : dtoConverterContext.getActions()), model);
    }

    public InsurancePlan toDTO(Map<String, Map<String, String>> actions, com.clarityvisionsolutions.insurance.benefits.tracker.model.InsurancePlan model) throws Exception {
        return new InsurancePlan() {
            {
                setActions(actions);
                setActive(model::isActive);
                setAnnualContactsAllowanceCents(model::getAnnualContactsAllowanceCents);
                setAnnualExamAllowanceCents(model::getAnnualExamAllowanceCents);
                setAnnualFramesAllowanceCents(model::getAnnualFramesAllowanceCents);
                setAnnualLensesAllowanceCents(model::getAnnualLensesAllowanceCents);
                setCoveragePeriodMonths(model::getCoveragePeriodMonths);
                setCreator(() -> CreatorUtil.toCreator(_portal, _userLocalService.getUser(model.getUserId())));
                setDateCreated(model::getCreateDate);
                setDateModified(model::getModifiedDate);
                setExternalReferenceCode(model::getExternalReferenceCode);
                setId(model::getInsurancePlanId);
                setPlanName(model::getPlanName);
                setProviderName(model::getProviderName);
                setSiteId(model::getGroupId);
                setStatus(model::getStatus);
            }
        };
    }

    @Reference
    private InsurancePlanLocalService _InsurancePlanLocalService;

    @Reference
    private InsurancePlanService _InsurancePlanService;

    @Reference
    private Portal _portal;

    @Reference
    private UserLocalService _userLocalService;
}
