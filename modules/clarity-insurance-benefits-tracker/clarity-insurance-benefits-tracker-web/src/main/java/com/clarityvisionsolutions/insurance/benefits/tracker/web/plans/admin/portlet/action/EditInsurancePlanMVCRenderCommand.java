package com.clarityvisionsolutions.insurance.benefits.tracker.web.plans.admin.portlet.action;

import com.clarityvisionsolutions.insurance.benefits.tracker.constants.InsuranceBenefitsTrackerPortletKeys;
import com.clarityvisionsolutions.insurance.benefits.tracker.model.InsurancePlan;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.InsurancePlanService;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;

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
		"jakarta.portlet.name=" + InsuranceBenefitsTrackerPortletKeys.IBT_PLANS_ADMIN,
		"mvc.command.name=/insurance-plans/edit"
	},
	service = MVCRenderCommand.class
)
public class EditInsurancePlanMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		long insurancePlanId = ParamUtil.getLong(
			renderRequest, "insurancePlanId");

		if (insurancePlanId > 0) {
			try {
				InsurancePlan insurancePlan =
					_insurancePlanService.getInsurancePlan(insurancePlanId);

				renderRequest.setAttribute("insurancePlan", insurancePlan);
			}
			catch (Exception exception) {
				if (exception instanceof PrincipalException) {
					SessionErrors.add(
						renderRequest, exception.getClass());

					return "/error.jsp";
				}

				throw new PortletException(exception);
			}
		}

		return "/insurance-plans/edit.jsp";
	}

	@Reference
	private InsurancePlanService _insurancePlanService;

}
