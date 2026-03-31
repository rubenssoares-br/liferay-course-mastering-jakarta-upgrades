package com.clarityvisionsolutions.insurance.benefits.tracker.web.benefit.usage.recorder.portlet.action;

import com.clarityvisionsolutions.insurance.benefits.tracker.constants.InsuranceBenefitsTrackerPortletKeys;
import com.clarityvisionsolutions.insurance.benefits.tracker.model.BenefitUsage;
import com.clarityvisionsolutions.insurance.benefits.tracker.model.InsurancePlan;
import com.clarityvisionsolutions.insurance.benefits.tracker.model.PlanEnrollment;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.BenefitUsageLocalService;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.BenefitUsageService;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.InsurancePlanLocalService;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.PlanEnrollmentService;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author dnebinger
 */
@Component(
	property = {
		"jakarta.portlet.name=" + InsuranceBenefitsTrackerPortletKeys.IBT_BENEFIT_USAGE_RECORDER,
		"mvc.command.name=/benefit-usage/edit"
	},
	service = MVCRenderCommand.class
)
public class EditBenefitUsageMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		long benefitUsageId = ParamUtil.getLong(
			renderRequest, "benefitUsageId");
		long planEnrollmentId = ParamUtil.getLong(
			renderRequest, "planEnrollmentId");

		try {

			// Load the benefit usage if editing

			if (benefitUsageId > 0) {
				BenefitUsage benefitUsage =
					_benefitUsageService.getBenefitUsage(benefitUsageId);

				renderRequest.setAttribute("benefitUsage", benefitUsage);

				// If planEnrollmentId wasn't passed, get it from the record

				if (planEnrollmentId <= 0) {
					planEnrollmentId =
						benefitUsage.getPlanEnrollmentId();
				}
			}

			// Load plan enrollment and insurance plan for context

			PlanEnrollment planEnrollment =
				_planEnrollmentService.getPlanEnrollment(planEnrollmentId);

			renderRequest.setAttribute("planEnrollment", planEnrollment);

			InsurancePlan insurancePlan =
				_insurancePlanLocalService.getInsurancePlan(
					planEnrollment.getInsurancePlanId());

			renderRequest.setAttribute("insurancePlan", insurancePlan);

			// Compute current usage totals per benefit type
			// (excluding the record being edited)

			List<BenefitUsage> allUsages =
				_benefitUsageLocalService.getBenefitUsagesByPlanEnrollmentStatus(
					planEnrollmentId, WorkflowConstants.STATUS_APPROVED,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			Map<String, Long> usageTotals = new HashMap<>();

			usageTotals.put("exam", 0L);
			usageTotals.put("frames", 0L);
			usageTotals.put("lenses", 0L);
			usageTotals.put("contacts", 0L);

			for (BenefitUsage bu : allUsages) {

				// Exclude the record being edited from totals

				if (bu.getBenefitUsageId() == benefitUsageId) {
					continue;
				}

				String benefitType = bu.getBenefitType();

				if (usageTotals.containsKey(benefitType)) {
					usageTotals.put(
						benefitType,
						usageTotals.get(benefitType) +
							bu.getAmountUsedCents());
				}
			}

			renderRequest.setAttribute("usageTotals", usageTotals);
		}
		catch (Exception exception) {
			if (exception instanceof PrincipalException) {
				SessionErrors.add(renderRequest, exception.getClass());

				return "/error.jsp";
			}

			_log.error(
				"Unable to load benefit usage edit data: " +
					exception.getMessage(),
				exception);

			throw new PortletException(exception);
		}

		return "/benefit-usage/edit.jsp";
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditBenefitUsageMVCRenderCommand.class);

	@Reference
	private BenefitUsageLocalService _benefitUsageLocalService;

	@Reference
	private BenefitUsageService _benefitUsageService;

	@Reference
	private InsurancePlanLocalService _insurancePlanLocalService;

	@Reference
	private PlanEnrollmentService _planEnrollmentService;

}
