package com.clarityvisionsolutions.insurance.benefits.tracker.web.enrollments.admin.portlet.action;

import com.clarityvisionsolutions.insurance.benefits.tracker.constants.InsuranceBenefitsTrackerPortletKeys;
import com.clarityvisionsolutions.insurance.benefits.tracker.model.InsurancePlan;
import com.clarityvisionsolutions.insurance.benefits.tracker.model.PlanEnrollment;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.InsurancePlanService;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.PlanEnrollmentService;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.List;

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
		"jakarta.portlet.name=" + InsuranceBenefitsTrackerPortletKeys.IBT_ENROLLMENT_ADMIN,
		"mvc.command.name=/plan-enrollments/edit"
	},
	service = MVCRenderCommand.class
)
public class EditPlanEnrollmentMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long planEnrollmentId = ParamUtil.getLong(
			renderRequest, "planEnrollmentId");

		if (planEnrollmentId > 0) {
			try {
				PlanEnrollment planEnrollment =
					_planEnrollmentService.getPlanEnrollment(
						planEnrollmentId);

				renderRequest.setAttribute("planEnrollment", planEnrollment);
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

		// Fetch active insurance plans for the dropdown

		try {
			List<InsurancePlan> insurancePlans =
				_insurancePlanService.getGroupInsurancePlans(
					themeDisplay.getScopeGroupId(),
					WorkflowConstants.STATUS_APPROVED, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS);

			renderRequest.setAttribute("insurancePlans", insurancePlans);
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}

		// Check if the user has the Plan Enrollment Administrator role

		boolean isAdmin = false;

		try {
			isAdmin = _roleLocalService.hasUserRole(
				themeDisplay.getUserId(), themeDisplay.getCompanyId(),
				"Plan Enrollment Administrator", true);
		}
		catch (Exception exception) {
			// If role doesn't exist or check fails, default to false
		}

		renderRequest.setAttribute("isAdmin", isAdmin);

		return "/plan-enrollments/edit.jsp";
	}

	@Reference
	private InsurancePlanService _insurancePlanService;

	@Reference
	private PlanEnrollmentService _planEnrollmentService;

	@Reference
	private RoleLocalService _roleLocalService;

}
