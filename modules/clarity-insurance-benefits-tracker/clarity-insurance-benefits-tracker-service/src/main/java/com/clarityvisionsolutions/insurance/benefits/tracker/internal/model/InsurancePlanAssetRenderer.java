/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.clarityvisionsolutions.insurance.benefits.tracker.internal.model;

import com.clarityvisionsolutions.insurance.benefits.tracker.model.InsurancePlan;
import com.liferay.account.model.AccountEntry;
import com.liferay.asset.kernel.model.BaseAssetRenderer;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * @author Drew Brokke
 */
public class InsurancePlanAssetRenderer extends BaseAssetRenderer<InsurancePlan> {

	public InsurancePlanAssetRenderer(InsurancePlan insurancePlan) {
		_insurancePlan = insurancePlan;
	}

	@Override
	public InsurancePlan getAssetObject() {
		return _insurancePlan;
	}

	@Override
	public String getClassName() {
		return InsurancePlan.class.getName();
	}

	@Override
	public long getClassPK() {
		return _insurancePlan.getInsurancePlanId();
	}

	@Override
	public long getGroupId() {
		return 0;
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		return _insurancePlan.getPlanName() + " From " + _insurancePlan.getProviderName();
	}

	@Override
	public String getTitle(Locale locale) {
		return _insurancePlan.getPlanName();
	}

	@Override
	public long getUserId() {
		return _insurancePlan.getUserId();
	}

	@Override
	public String getUserName() {
		return _insurancePlan.getUserName();
	}

	@Override
	public String getUuid() {
		return _insurancePlan.getUuid();
	}

	@Override
	public boolean include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String template)
		throws Exception {

		return false;
	}

	private final InsurancePlan _insurancePlan;

}