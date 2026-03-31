package com.clarityvisionsolutions.insurance.benefits.tracker.web.benefit.usage.recorder.portlet.action;

import com.clarityvisionsolutions.insurance.benefits.tracker.constants.InsuranceBenefitsTrackerPortletKeys;
import com.clarityvisionsolutions.insurance.benefits.tracker.model.InsurancePlan;
import com.clarityvisionsolutions.insurance.benefits.tracker.model.PlanEnrollment;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.InsurancePlanLocalService;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.PlanEnrollmentService;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.HashMap;
import java.util.LinkedHashMap;
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
		"mvc.command.name=/", "mvc.command.name=/benefit-usage/search"
	},
	service = MVCRenderCommand.class
)
public class SearchEnrollmentsMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long groupId = themeDisplay.getScopeGroupId();
		long companyId = themeDisplay.getCompanyId();

		String keywords = ParamUtil.getString(renderRequest, "keywords", "");
		long memberUserId = ParamUtil.getLong(
			renderRequest, "memberUserId", 0);

		// If keywords are present (forwarded from action), search for users

		if (Validator.isNotNull(keywords)) {
			try {
				List<User> matchedUsers = _userLocalService.search(
					companyId, keywords, WorkflowConstants.STATUS_APPROVED,
					new LinkedHashMap<>(), 0, 20,
					(OrderByComparator<User>)null);

				renderRequest.setAttribute("matchedUsers", matchedUsers);
			}
			catch (Exception exception) {
				_log.error(
					"Error searching for users: " + exception.getMessage(),
					exception);
			}
		}

		// If a specific memberUserId is selected, load their enrollments

		if (memberUserId > 0) {
			try {
				User selectedUser = _userLocalService.getUser(memberUserId);

				renderRequest.setAttribute("selectedUser", selectedUser);

				_log.info("Looking for enrollments for group " + groupId + ", member " + memberUserId + " and status 1 (active)");

				List<PlanEnrollment> enrollments =
					_planEnrollmentService.getMemberPlanEnrollments(
						groupId, memberUserId, 1);

				_log.info("Found " + (Validator.isNull(enrollments) ? 0 : enrollments.size()) + " enrollments");

				renderRequest.setAttribute("enrollments", enrollments);

				Map<Long, InsurancePlan> insurancePlanMap = new HashMap<>();

				for (PlanEnrollment enrollment : enrollments) {
					long insurancePlanId = enrollment.getInsurancePlanId();

					if (!insurancePlanMap.containsKey(insurancePlanId)) {
						try {
							InsurancePlan insurancePlan =
								_insurancePlanLocalService.getInsurancePlan(
									insurancePlanId);

							insurancePlanMap.put(
								insurancePlanId, insurancePlan);
						}
						catch (Exception exception) {
							_log.warn(
								"Unable to find insurance plan: " +
									insurancePlanId,
								exception);
						}
					}
				}

				renderRequest.setAttribute(
					"insurancePlanMap", insurancePlanMap);
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

		renderRequest.setAttribute("keywords", keywords);
		renderRequest.setAttribute("memberUserId", memberUserId);

		return "/benefit-usage/search.jsp";
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SearchEnrollmentsMVCRenderCommand.class);

	@Reference
	private InsurancePlanLocalService _insurancePlanLocalService;

	@Reference
	private PlanEnrollmentService _planEnrollmentService;

	@Reference
	private UserLocalService _userLocalService;

}
