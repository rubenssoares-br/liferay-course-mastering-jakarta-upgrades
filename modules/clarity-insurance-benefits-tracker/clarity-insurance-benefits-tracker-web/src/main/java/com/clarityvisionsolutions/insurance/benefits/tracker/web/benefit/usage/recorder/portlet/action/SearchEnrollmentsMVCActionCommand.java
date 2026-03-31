package com.clarityvisionsolutions.insurance.benefits.tracker.web.benefit.usage.recorder.portlet.action;

import com.clarityvisionsolutions.insurance.benefits.tracker.constants.InsuranceBenefitsTrackerPortletKeys;

import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author dnebinger
 */
@Component(
	property = {
		"jakarta.portlet.name=" + InsuranceBenefitsTrackerPortletKeys.IBT_BENEFIT_USAGE_RECORDER,
		"mvc.command.name=/benefit-usage/search"
	},
	service = MVCActionCommand.class
)
public class SearchEnrollmentsMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String keywords = ParamUtil.getString(actionRequest, "keywords", "");
		long memberUserId = ParamUtil.getLong(
			actionRequest, "memberUserId", 0);

		// Forward parameters to the render phase

		actionResponse.setRenderParameter(
			"mvcRenderCommandName", "/benefit-usage/search");

		if (memberUserId > 0) {
			actionResponse.setRenderParameter(
				"memberUserId", String.valueOf(memberUserId));
		}
		else {
			actionResponse.setRenderParameter("keywords", keywords);
		}
	}

}
