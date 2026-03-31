package com.clarityvisionsolutions.insurance.benefits.tracker.web.enrollments.admin.portlet.action;

import com.clarityvisionsolutions.insurance.benefits.tracker.constants.InsuranceBenefitsTrackerPortletKeys;
import com.clarityvisionsolutions.insurance.benefits.tracker.model.PlanEnrollment;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.PlanEnrollmentService;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import java.text.SimpleDateFormat;

import java.util.Date;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author dnebinger
 */
@Component(
	property = {
		"jakarta.portlet.name=" + InsuranceBenefitsTrackerPortletKeys.IBT_ENROLLMENT_ADMIN,
		"mvc.command.name=/plan-enrollments/save"
	},
	service = MVCActionCommand.class
)
public class SavePlanEnrollmentMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long planEnrollmentId = ParamUtil.getLong(
			actionRequest, "planEnrollmentId");
		long insurancePlanId = ParamUtil.getLong(
			actionRequest, "insurancePlanId");

		String memberId = ParamUtil.getString(actionRequest, "memberId");
		String groupNumber = ParamUtil.getString(actionRequest, "groupNumber");
		int enrollmentStatus = ParamUtil.getInteger(
			actionRequest, "enrollmentStatus");
		String notes = ParamUtil.getString(actionRequest, "notes");

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		// Parse dates

		Date startDate = _parseDate(
			ParamUtil.getString(actionRequest, "startDate"));
		Date endDate = _parseDate(
			ParamUtil.getString(actionRequest, "endDate"));

		// Validate: start date must come before end date

		if ((startDate != null) && (endDate != null) &&
			startDate.after(endDate)) {

			SessionErrors.add(actionRequest, "startDateAfterEndDate");

			actionResponse.setRenderParameter(
				"mvcRenderCommandName", "/plan-enrollments/edit");
			actionResponse.setRenderParameter(
				"planEnrollmentId", String.valueOf(planEnrollmentId));

			return;
		}

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			PlanEnrollment.class.getName(), actionRequest);

		try {
			if (planEnrollmentId > 0) {
				_planEnrollmentService.updatePlanEnrollment(
					planEnrollmentId, memberId, groupNumber, startDate,
					endDate, enrollmentStatus, notes, serviceContext);
			}
			else {
				_planEnrollmentService.addPlanEnrollment(
					insurancePlanId, themeDisplay.getUserId(), memberId,
					groupNumber, startDate, endDate, enrollmentStatus, notes,
					serviceContext);
			}

			SessionMessages.add(actionRequest, "planEnrollmentSaved");

			sendRedirect(actionRequest, actionResponse);
		}
		catch (Exception exception) {
			_log.error(
				"Unable to save plan enrollment: " + exception.getMessage(),
				exception);

			SessionErrors.add(
				actionRequest, exception.getClass().getName());

			actionResponse.setRenderParameter(
				"mvcRenderCommandName", "/plan-enrollments/edit");
			actionResponse.setRenderParameter(
				"planEnrollmentId", String.valueOf(planEnrollmentId));
		}
	}

	private Date _parseDate(String dateString) {
		if (Validator.isNull(dateString)) {
			return null;
		}

		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"MM/dd/yyyy");

			return simpleDateFormat.parse(dateString);
		}
		catch (Exception exception) {
			_log.warn("Unable to parse date: " + dateString, exception);

			return null;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SavePlanEnrollmentMVCActionCommand.class);

	@Reference
	private PlanEnrollmentService _planEnrollmentService;

}
