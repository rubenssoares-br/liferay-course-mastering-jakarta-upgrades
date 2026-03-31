package com.clarityvisionsolutions.insurance.benefits.tracker.rest.internal.dto.v1_0.converter;

import com.clarityvisionsolutions.insurance.benefits.tracker.rest.dto.v1_0.BenefitUsage;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.dto.v1_0.PlanEnrollment;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.internal.dto.v1_0.util.CreatorUtil;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.internal.resource.v1_0.BaseBenefitUsageResourceImpl;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.BenefitUsageLocalService;
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
        property = "dto.class.name=com.clarityvisionsolutions.insurance.benefits.tracker.model.BenefitUsage",
        service = {DTOConverter.class, BenefitUsageDTOConverter.class}
)
public class BenefitUsageDTOConverter implements DTOConverter<com.clarityvisionsolutions.insurance.benefits.tracker.model.BenefitUsage, BenefitUsage> {
    @Override
    public String getContentType() {
        return BenefitUsage.class.getSimpleName();
    }

    @Override
    public String getJaxRsLink(long classPK, UriInfo uriInfo) {
        return JaxRsLinkUtil.getJaxRsLink("headless-clarity-insurance-benefits-tracker", BaseBenefitUsageResourceImpl.class, "getSiteBenefitUsage", uriInfo, classPK);
    }

    @Override
    public com.clarityvisionsolutions.insurance.benefits.tracker.model.BenefitUsage getObject(String externalReferenceCode) throws Exception {
        return _benefitUsageLocalService.getBenefitUsageByExternalReferenceCode(externalReferenceCode);
    }

    @Override
    public BenefitUsage toDTO(DTOConverterContext dtoConverterContext) throws Exception {
        com.clarityvisionsolutions.insurance.benefits.tracker.model.BenefitUsage model;

        model = _benefitUsageLocalService.getBenefitUsage((long) dtoConverterContext.getId());

        return toDTO(dtoConverterContext, model);
    }

    @Override
    public BenefitUsage toDTO(DTOConverterContext dtoConverterContext, com.clarityvisionsolutions.insurance.benefits.tracker.model.BenefitUsage model) throws Exception {
        return toDTO((dtoConverterContext == null ? new HashMap<>() : dtoConverterContext.getActions()), model);
    }

    public BenefitUsage toDTO(Map<String, Map<String, String>> actions, com.clarityvisionsolutions.insurance.benefits.tracker.model.BenefitUsage model) {
        return new BenefitUsage() {
            {
                setActions(actions);
                setAmountUsedCents(model::getAmountUsedCents);
                setBenefitType(model::getBenefitType);
                setCreator(() -> CreatorUtil.toCreator(_portal, _userLocalService.getUser(model.getUserId())));
                setDateCreated(model::getCreateDate);
                setDateModified(model::getModifiedDate);
                setExternalReferenceCode(model::getExternalReferenceCode);
                setId(model::getBenefitUsageId);
                setNotes(model::getNotes);
                setPlanEnrollmentId(model::getPlanEnrollmentId);
                setPlanEnrollmentERC(() -> _getPlanEnrollmentExternalReferenceCode(model.getPlanEnrollmentId()));
                setReference(model::getReference);
                setServiceDate(model::getServiceDate);
                setSiteId(model::getGroupId);
                setSourceReference(model::getSourceReference);
                setSourceType(model::getSourceType);
                setStatus(model::getStatus);
            }
        };
    }

    private String _getPlanEnrollmentExternalReferenceCode(final long planEnrollmentId) {
        com.clarityvisionsolutions.insurance.benefits.tracker.model.PlanEnrollment plan = _planEnrollmentLocalService.fetchPlanEnrollment(planEnrollmentId);

        if (Validator.isNotNull(plan)) {
            return plan.getExternalReferenceCode();
        }

        return null;
    }

    @Reference
    private PlanEnrollmentLocalService _planEnrollmentLocalService;

    @Reference
    private BenefitUsageLocalService _benefitUsageLocalService;

    @Reference
    private Portal _portal;

    @Reference
    private UserLocalService _userLocalService;
}
