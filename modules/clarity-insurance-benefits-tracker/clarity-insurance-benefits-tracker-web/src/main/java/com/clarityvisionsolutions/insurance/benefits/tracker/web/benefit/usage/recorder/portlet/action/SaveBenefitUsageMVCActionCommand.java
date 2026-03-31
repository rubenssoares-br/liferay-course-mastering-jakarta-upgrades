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
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author dnebinger
 */
@Component(
	property = {
		"jakarta.portlet.name=" + InsuranceBenefitsTrackerPortletKeys.IBT_BENEFIT_USAGE_RECORDER,
		"mvc.command.name=/benefit-usage/save"
	},
	service = MVCActionCommand.class
)
public class SaveBenefitUsageMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long benefitUsageId = ParamUtil.getLong(
			actionRequest, "benefitUsageId");
		long planEnrollmentId = ParamUtil.getLong(
			actionRequest, "planEnrollmentId");

		String benefitType = ParamUtil.getString(
			actionRequest, "benefitType");
		String amountUsedDollarsStr = ParamUtil.getString(
			actionRequest, "amountUsedDollars");
		int serviceDateMonth = ParamUtil.getInteger(
			actionRequest, "serviceDateMonth");
		int serviceDateDay = ParamUtil.getInteger(
			actionRequest, "serviceDateDay");
		int serviceDateYear = ParamUtil.getInteger(
			actionRequest, "serviceDateYear");
		String reference = ParamUtil.getString(actionRequest, "reference");
		String notes = ParamUtil.getString(actionRequest, "notes");

		// Parse amount: dollars to cents

		long amountUsedCents = 0;

		try {
			double dollars = Double.parseDouble(
				amountUsedDollarsStr.replace(",", ""));

			amountUsedCents = Math.round(dollars * 100);
		}
		catch (NumberFormatException numberFormatException) {
			_log.warn(
				"Unable to parse amount: " + amountUsedDollarsStr,
				numberFormatException);

			SessionErrors.add(actionRequest, "invalidAmount");

			_redirectToEdit(
				actionResponse, benefitUsageId, planEnrollmentId);

			return;
		}

		// Parse service date

		Date serviceDate = _getDateFromParts(
			serviceDateMonth, serviceDateDay, serviceDateYear);

		// Allowance validation

		try {

			// 1. Get the plan enrollment and insurance plan

			PlanEnrollment planEnrollment =
				_planEnrollmentService.getPlanEnrollment(planEnrollmentId);

			InsurancePlan insurancePlan =
				_insurancePlanLocalService.getInsurancePlan(
					planEnrollment.getInsurancePlanId());

			// 2. Get the allowance cap for this benefit type

			long allowanceCap = _getAllowanceCap(
				insurancePlan, benefitType);

			// 3. Get all approved benefit usages for this enrollment

			List<BenefitUsage> existingUsages =
				_benefitUsageLocalService.
					getBenefitUsagesByPlanEnrollmentStatus(
						planEnrollmentId,
						WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
						QueryUtil.ALL_POS);

			// 4. Sum existing usage for this benefit type
			// (excluding the record being edited)

			long currentUsedCents = 0;

			for (BenefitUsage bu : existingUsages) {
				if (benefitType.equals(bu.getBenefitType()) &&
					(bu.getBenefitUsageId() != benefitUsageId)) {

					currentUsedCents += bu.getAmountUsedCents();
				}
			}

			// 5. Check if the new amount would exceed the cap

			if ((currentUsedCents + amountUsedCents) > allowanceCap) {
				SessionErrors.add(
					actionRequest, "benefitUsageExceedsAllowance");

				_redirectToEdit(
					actionResponse, benefitUsageId, planEnrollmentId);

				return;
			}

			// 6. Validation passed — save the record

			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				BenefitUsage.class.getName(), actionRequest);

			if (benefitUsageId > 0) {
				_benefitUsageService.updateBenefitUsage(
					benefitUsageId, benefitType, amountUsedCents,
					serviceDate, reference, notes, "manual", "",
					serviceContext);
			}
			else {
				_benefitUsageService.addBenefitUsage(
					planEnrollmentId, benefitType, amountUsedCents,
					serviceDate, reference, notes, "manual", "",
					serviceContext);
			}

			SessionMessages.add(actionRequest, "benefitUsageSaved");

			// Redirect back to the view page

			actionResponse.setRenderParameter(
				"mvcRenderCommandName", "/benefit-usage/view");
			actionResponse.setRenderParameter(
				"planEnrollmentId", String.valueOf(planEnrollmentId));

			sendRedirect(actionRequest, actionResponse);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to save benefit usage: " + exception.getMessage(),
				exception);

			SessionErrors.add(
				actionRequest, exception.getClass().getName());

			_redirectToEdit(
				actionResponse, benefitUsageId, planEnrollmentId);
		}
	}

	private long _getAllowanceCap(
		InsurancePlan insurancePlan, String benefitType) {

		switch (benefitType) {
			case "exam":
				return insurancePlan.getAnnualExamAllowanceCents();
			case "frames":
				return insurancePlan.getAnnualFramesAllowanceCents();
			case "lenses":
				return insurancePlan.getAnnualLensesAllowanceCents();
			case "contacts":
				return insurancePlan.getAnnualContactsAllowanceCents();
			default:
				return 0;
		}
	}

	private Date _getDateFromParts(int month, int day, int year) {
		if (year == 0) {
			return null;
		}

		Calendar calendar = new GregorianCalendar(year, month, day);

		return calendar.getTime();
	}

	private void _redirectToEdit(
		ActionResponse actionResponse, long benefitUsageId,
		long planEnrollmentId) {

		actionResponse.setRenderParameter(
			"mvcRenderCommandName", "/benefit-usage/edit");
		actionResponse.setRenderParameter(
			"benefitUsageId", String.valueOf(benefitUsageId));
		actionResponse.setRenderParameter(
			"planEnrollmentId", String.valueOf(planEnrollmentId));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SaveBenefitUsageMVCActionCommand.class);

	@Reference
	private BenefitUsageLocalService _benefitUsageLocalService;

	@Reference
	private BenefitUsageService _benefitUsageService;

	@Reference
	private InsurancePlanLocalService _insurancePlanLocalService;

	@Reference
	private PlanEnrollmentService _planEnrollmentService;

}
