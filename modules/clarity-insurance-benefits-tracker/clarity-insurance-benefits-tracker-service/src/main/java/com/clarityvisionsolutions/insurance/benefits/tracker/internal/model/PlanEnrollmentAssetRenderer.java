/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.clarityvisionsolutions.insurance.benefits.tracker.internal.model;

import com.clarityvisionsolutions.insurance.benefits.tracker.model.InsurancePlan;
import com.clarityvisionsolutions.insurance.benefits.tracker.model.PlanEnrollment;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.InsurancePlanLocalService;
import com.liferay.account.model.AccountEntry;
import com.liferay.asset.kernel.model.BaseAssetRenderer;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * @author Drew Brokke
 */
public class PlanEnrollmentAssetRenderer extends BaseAssetRenderer<PlanEnrollment> {

	public PlanEnrollmentAssetRenderer(PlanEnrollment planEnrollment, final InsurancePlanLocalService insurancePlanLocalService) {
		_planEnrollment = planEnrollment;
		_insurancePlanLocalService = insurancePlanLocalService;
	}

	@Override
	public PlanEnrollment getAssetObject() {
		return _planEnrollment;
	}

	@Override
	public String getClassName() {
		return PlanEnrollment.class.getName();
	}

	@Override
	public long getClassPK() {
		return _planEnrollment.getPlanEnrollmentId();
	}

	@Override
	public long getGroupId() {
		return 0;
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		InsurancePlan insurancePlan = _insurancePlanLocalService.fetchInsurancePlan(_planEnrollment.getInsurancePlanId());

		if (Validator.isNull(insurancePlan)) {
			return insurancePlan.getPlanName() + " For " + _planEnrollment.getMemberUserName();
		}

		return "Plan For " + _planEnrollment.getMemberUserName();
	}

	@Override
	public String getTitle(Locale locale) {
		return _planEnrollment.getMemberUserName();
	}

	@Override
	public long getUserId() {
		return _planEnrollment.getUserId();
	}

	@Override
	public String getUserName() {
		return _planEnrollment.getUserName();
	}

	@Override
	public String getUuid() {
		return _planEnrollment.getUuid();
	}

	@Override
	public boolean include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String template)
		throws Exception {

		return false;
	}

	private final PlanEnrollment _planEnrollment;
	private final InsurancePlanLocalService _insurancePlanLocalService;
}