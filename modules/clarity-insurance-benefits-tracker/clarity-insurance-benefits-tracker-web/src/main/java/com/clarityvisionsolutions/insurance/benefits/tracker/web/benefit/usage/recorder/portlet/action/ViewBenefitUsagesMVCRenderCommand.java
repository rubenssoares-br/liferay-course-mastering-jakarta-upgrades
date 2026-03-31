package com.clarityvisionsolutions.insurance.benefits.tracker.web.benefit.usage.recorder.portlet.action;

import com.clarityvisionsolutions.insurance.benefits.tracker.constants.InsuranceBenefitsTrackerPortletKeys;
import com.clarityvisionsolutions.insurance.benefits.tracker.model.BenefitUsage;
import com.clarityvisionsolutions.insurance.benefits.tracker.model.InsurancePlan;
import com.clarityvisionsolutions.insurance.benefits.tracker.model.PlanEnrollment;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.BenefitUsageLocalService;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.InsurancePlanLocalService;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.PlanEnrollmentService;

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
		"mvc.command.name=/benefit-usage/view"
	},
	service = MVCRenderCommand.class
)
public class ViewBenefitUsagesMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		long planEnrollmentId = ParamUtil.getLong(
			renderRequest, "planEnrollmentId");

		try {
			PlanEnrollment planEnrollment =
				_planEnrollmentService.getPlanEnrollment(planEnrollmentId);

			renderRequest.setAttribute("planEnrollment", planEnrollment);

			InsurancePlan insurancePlan =
				_insurancePlanLocalService.getInsurancePlan(
					planEnrollment.getInsurancePlanId());

			renderRequest.setAttribute("insurancePlan", insurancePlan);

			// Fetch all approved benefit usages for this enrollment

			List<BenefitUsage> benefitUsages =
				_benefitUsageLocalService.getBenefitUsagesByPlanEnrollmentStatus(
					planEnrollmentId, WorkflowConstants.STATUS_APPROVED,
					com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
					com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS);

			renderRequest.setAttribute("benefitUsages", benefitUsages);

			// Compute current usage totals per benefit type

			Map<String, Long> usageTotals = new HashMap<>();

			usageTotals.put("exam", 0L);
			usageTotals.put("frames", 0L);
			usageTotals.put("lenses", 0L);
			usageTotals.put("contacts", 0L);

			for (BenefitUsage benefitUsage : benefitUsages) {
				String benefitType = benefitUsage.getBenefitType();

				if (usageTotals.containsKey(benefitType)) {
					usageTotals.put(
						benefitType,
						usageTotals.get(benefitType) +
							benefitUsage.getAmountUsedCents());
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
				"Unable to load benefit usages: " + exception.getMessage(),
				exception);

			throw new PortletException(exception);
		}

		return "/benefit-usage/view.jsp";
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ViewBenefitUsagesMVCRenderCommand.class);

	@Reference
	private BenefitUsageLocalService _benefitUsageLocalService;

	@Reference
	private InsurancePlanLocalService _insurancePlanLocalService;

	@Reference
	private PlanEnrollmentService _planEnrollmentService;

}
