package com.clarityvisionsolutions.insurance.benefits.tracker.web.benefit.usage.recorder.portlet.action;

import com.clarityvisionsolutions.insurance.benefits.tracker.constants.InsuranceBenefitsTrackerPortletKeys;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.BenefitUsageService;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

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
		"mvc.command.name=/benefit-usage/delete"
	},
	service = MVCActionCommand.class
)
public class DeleteBenefitUsageMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long benefitUsageId = ParamUtil.getLong(
			actionRequest, "benefitUsageId");
		long planEnrollmentId = ParamUtil.getLong(
			actionRequest, "planEnrollmentId");

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long userId = themeDisplay.getUserId();

		try {
			_benefitUsageService.moveBenefitUsageToTrash(
				userId, benefitUsageId);

			SessionMessages.add(actionRequest, "benefitUsageDeleted");
		}
		catch (Exception exception) {
			_log.error(
				"Unable to delete benefit usage: " + exception.getMessage(),
				exception);

			SessionErrors.add(
				actionRequest, exception.getClass().getName());
		}

		// Redirect back to the view page

		actionResponse.setRenderParameter(
			"mvcRenderCommandName", "/benefit-usage/view");
		actionResponse.setRenderParameter(
			"planEnrollmentId", String.valueOf(planEnrollmentId));

		sendRedirect(actionRequest, actionResponse);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DeleteBenefitUsageMVCActionCommand.class);

	@Reference
	private BenefitUsageService _benefitUsageService;

}
