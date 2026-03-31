/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.clarityvisionsolutions.insurance.benefits.tracker.web.plans.admin.portlet.action;

import com.clarityvisionsolutions.insurance.benefits.tracker.constants.InsuranceBenefitsTrackerPortletKeys;
import com.clarityvisionsolutions.insurance.benefits.tracker.model.InsurancePlan;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.InsurancePlanService;
import com.liferay.change.tracking.spi.history.util.CTTimelineUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + InsuranceBenefitsTrackerPortletKeys.IBT_PLANS_ADMIN,
		"mvc.command.name=/", "mvc.command.name=/insurance-plans/view"
	},
	service = MVCRenderCommand.class
)
public class ViewInsurancePlansMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(renderRequest);
		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
		long siteId = themeDisplay.getScopeGroupId();

		try {
			List<InsurancePlan> plans = _insurancePlanService.getGroupInsurancePlans(siteId, WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			renderRequest.setAttribute(InsurancePlan.class.getName() + 's', plans);

			CTTimelineUtil.setClassName(renderRequest, InsurancePlan.class);
		}
		catch (Exception exception) {
			if (exception instanceof PrincipalException) {

				SessionErrors.add(renderRequest, exception.getClass());

				return "/error.jsp";
			}

			throw new PortletException(exception);
		}

		return "/insurance-plans/view.jsp";
	}

	@Reference
	private InsurancePlanService _insurancePlanService;

	@Reference
	private Portal _portal;
}