package com.clarityvisionsolutions.insurance.benefits.tracker.web.plans.admin.portlet.action;

import com.clarityvisionsolutions.insurance.benefits.tracker.constants.InsuranceBenefitsTrackerPortletKeys;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.InsurancePlanService;

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
		"jakarta.portlet.name=" + InsuranceBenefitsTrackerPortletKeys.IBT_PLANS_ADMIN,
		"mvc.command.name=/insurance-plans/delete"
	},
	service = MVCActionCommand.class
)
public class DeleteInsurancePlanMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long insurancePlanId = ParamUtil.getLong(
			actionRequest, "insurancePlanId");

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long userId = themeDisplay.getUserId();

		try {
			_insurancePlanService.moveInsurancePlanToTrash(
				userId, insurancePlanId);

			SessionMessages.add(
				actionRequest, "insurancePlanDeleted");
		}
		catch (Exception exception) {
			_log.error(
				"Unable to delete insurance plan: " + exception.getMessage(),
				exception);

			SessionErrors.add(
				actionRequest, exception.getClass().getName());
		}

		sendRedirect(actionRequest, actionResponse);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DeleteInsurancePlanMVCActionCommand.class);

	@Reference
	private InsurancePlanService _insurancePlanService;

}
