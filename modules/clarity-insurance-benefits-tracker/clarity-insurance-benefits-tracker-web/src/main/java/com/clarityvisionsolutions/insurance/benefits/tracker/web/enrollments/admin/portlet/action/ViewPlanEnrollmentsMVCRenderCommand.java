package com.clarityvisionsolutions.insurance.benefits.tracker.web.enrollments.admin.portlet.action;

import com.clarityvisionsolutions.insurance.benefits.tracker.constants.InsuranceBenefitsTrackerPortletKeys;
import com.clarityvisionsolutions.insurance.benefits.tracker.model.InsurancePlan;
import com.clarityvisionsolutions.insurance.benefits.tracker.model.PlanEnrollment;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.InsurancePlanLocalService;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.PlanEnrollmentService;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
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
		"jakarta.portlet.name=" + InsuranceBenefitsTrackerPortletKeys.IBT_ENROLLMENT_ADMIN,
		"mvc.command.name=/", "mvc.command.name=/plan-enrollments/view"
	},
	service = MVCRenderCommand.class
)
public class ViewPlanEnrollmentsMVCRenderCommand implements MVCRenderCommand {

	private static final String[] ADMIN_ROLES = {
			"Insurance Plan Administrator",
			"Benefits Usage Clerk",
			"Administrator"
	};

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long groupId = themeDisplay.getScopeGroupId();
		long userId = themeDisplay.getUserId();

		User user = _userLocalService.fetchUser(userId);
		boolean showAll = false;

        try {
            if (_roleLocalService.hasUserRoles(userId, user.getCompanyId(), ADMIN_ROLES, true)) {
                showAll = true;
            }
        } catch (PortalException e) {
            _log.info("Failed to check user for admin roles, not showing all.", e);
        }

        try {
			List<PlanEnrollment> enrollments;

			if (showAll) {
				enrollments = _planEnrollmentService.getActiveGroupEnrollments(groupId);
			} else {
				enrollments =
						_planEnrollmentService.getMemberPlanEnrollments(
								groupId, userId, 1);
			}

			renderRequest.setAttribute(
				PlanEnrollment.class.getName() + 's', enrollments);

			// Build a map of insurancePlanId -> InsurancePlan for display

			Map<Long, InsurancePlan> insurancePlanMap = new HashMap<>();

			for (PlanEnrollment enrollment : enrollments) {
				long insurancePlanId = enrollment.getInsurancePlanId();

				if (!insurancePlanMap.containsKey(insurancePlanId)) {
					try {
						InsurancePlan insurancePlan =
							_insurancePlanLocalService.getInsurancePlan(
								insurancePlanId);

						insurancePlanMap.put(insurancePlanId, insurancePlan);
					}
					catch (Exception exception) {
						_log.warn(
							"Unable to find insurance plan: " +
								insurancePlanId,
							exception);
					}
				}
			}

			renderRequest.setAttribute("insurancePlanMap", insurancePlanMap);
		}
		catch (Exception exception) {
			if (exception instanceof PrincipalException) {
				SessionErrors.add(renderRequest, exception.getClass());

				return "/error.jsp";
			}

			throw new PortletException(exception);
		}

		return "/plan-enrollments/view.jsp";
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ViewPlanEnrollmentsMVCRenderCommand.class);

	@Reference
	private InsurancePlanLocalService _insurancePlanLocalService;

	@Reference
	private PlanEnrollmentService _planEnrollmentService;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private RoleLocalService _roleLocalService;
}
