/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.clarityvisionsolutions.insurance.benefits.tracker.internal.model;

import com.clarityvisionsolutions.insurance.benefits.tracker.model.BenefitUsage;
import com.clarityvisionsolutions.insurance.benefits.tracker.model.PlanEnrollment;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.PlanEnrollmentLocalService;
import com.liferay.asset.kernel.model.BaseAssetRenderer;
import com.liferay.portal.kernel.util.Validator;
import org.osgi.service.component.annotations.Reference;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @author Drew Brokke
 */
public class BenefitUsageAssetRenderer extends BaseAssetRenderer<BenefitUsage> {

	public BenefitUsageAssetRenderer(BenefitUsage benefitUsage, PlanEnrollmentLocalService planEnrollmentLocalService) {
		_benefitUsage = benefitUsage;
		_planEnrollmentLocalService = planEnrollmentLocalService;
	}

	@Override
	public BenefitUsage getAssetObject() {
		return _benefitUsage;
	}

	@Override
	public String getClassName() {
		return BenefitUsage.class.getName();
	}

	@Override
	public long getClassPK() {
		return _benefitUsage.getBenefitUsageId();
	}

	@Override
	public long getGroupId() {
		return 0;
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		PlanEnrollment enrollment = _planEnrollmentLocalService.fetchPlanEnrollment(_benefitUsage.getPlanEnrollmentId());

		if (Validator.isNotNull(enrollment)) {
			String summary = enrollment.getMemberUserName() + " Usage: $" + (_benefitUsage.getAmountUsedCents() / 100.0)
					+ " on " + SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT).format(_benefitUsage.getServiceDate());

			return summary;
		}

		return "Usage: $" + (_benefitUsage.getAmountUsedCents() / 100.0);
	}

	@Override
	public String getTitle(Locale locale) {
		PlanEnrollment enrollment = _planEnrollmentLocalService.fetchPlanEnrollment(_benefitUsage.getPlanEnrollmentId());

		if (Validator.isNotNull(enrollment)) {
			return enrollment.getMemberUserName() + " Benefit Usage";
		}


		return "Benefit Usage";
	}

	@Override
	public long getUserId() {
		return _benefitUsage.getUserId();
	}

	@Override
	public String getUserName() {
		return _benefitUsage.getUserName();
	}

	@Override
	public String getUuid() {
		return _benefitUsage.getUuid();
	}

	@Override
	public boolean include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String template)
		throws Exception {

		return false;
	}

	private final PlanEnrollmentLocalService _planEnrollmentLocalService;

	private final BenefitUsage _benefitUsage;

}