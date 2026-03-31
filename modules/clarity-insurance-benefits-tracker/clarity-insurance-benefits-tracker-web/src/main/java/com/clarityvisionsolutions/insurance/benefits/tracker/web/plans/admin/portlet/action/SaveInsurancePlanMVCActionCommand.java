package com.clarityvisionsolutions.insurance.benefits.tracker.web.plans.admin.portlet.action;

import com.clarityvisionsolutions.insurance.benefits.tracker.constants.InsuranceBenefitsTrackerPortletKeys;
import com.clarityvisionsolutions.insurance.benefits.tracker.model.InsurancePlan;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.InsurancePlanService;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author dnebinger
 */
@Component(
	property = {
		"jakarta.portlet.name=" + InsuranceBenefitsTrackerPortletKeys.IBT_PLANS_ADMIN,
		"mvc.command.name=/insurance-plans/save"
	},
	service = MVCActionCommand.class
)
public class SaveInsurancePlanMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long insurancePlanId = ParamUtil.getLong(
			actionRequest, "insurancePlanId");

		String providerName = ParamUtil.getString(
			actionRequest, "providerName");
		String planName = ParamUtil.getString(actionRequest, "planName");
		boolean active = ParamUtil.getBoolean(actionRequest, "active");
		int coveragePeriodMonths = ParamUtil.getInteger(
			actionRequest, "coveragePeriodMonths", 12);

		long annualExamAllowanceCents = _toCents(
			ParamUtil.getString(actionRequest, "annualExamAllowance"));
		long annualFramesAllowanceCents = _toCents(
			ParamUtil.getString(actionRequest, "annualFramesAllowance"));
		long annualLensesAllowanceCents = _toCents(
			ParamUtil.getString(actionRequest, "annualLensesAllowance"));
		long annualContactsAllowanceCents = _toCents(
			ParamUtil.getString(actionRequest, "annualContactsAllowance"));

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			InsurancePlan.class.getName(), actionRequest);

		try {
			if (insurancePlanId > 0) {
				_insurancePlanService.updateInsurancePlan(
					insurancePlanId, providerName, planName, active,
					annualExamAllowanceCents, annualFramesAllowanceCents,
					annualLensesAllowanceCents, annualContactsAllowanceCents,
					coveragePeriodMonths, serviceContext);
			}
			else {
				_insurancePlanService.addInsurancePlan(
					providerName, planName, active,
					annualExamAllowanceCents, annualFramesAllowanceCents,
					annualLensesAllowanceCents, annualContactsAllowanceCents,
					coveragePeriodMonths, serviceContext);
			}

			SessionMessages.add(
				actionRequest, "insurancePlanSaved");

			sendRedirect(actionRequest, actionResponse);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to save insurance plan: " + exception.getMessage(),
				exception);

			SessionErrors.add(
				actionRequest, exception.getClass().getName());

			actionResponse.setRenderParameter(
				"mvcRenderCommandName", "/insurance-plans/edit");
			actionResponse.setRenderParameter(
				"insurancePlanId", String.valueOf(insurancePlanId));
		}
	}

	private long _toCents(String dollarString) {
		if ((dollarString == null) || dollarString.isEmpty()) {
			return 0L;
		}

		try {
			double dollars = Double.parseDouble(dollarString);

			return Math.round(dollars * 100);
		}
		catch (NumberFormatException numberFormatException) {
			_log.warn(
				"Unable to parse dollar amount: " + dollarString,
				numberFormatException);

			return 0L;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SaveInsurancePlanMVCActionCommand.class);

	@Reference
	private InsurancePlanService _insurancePlanService;

}
