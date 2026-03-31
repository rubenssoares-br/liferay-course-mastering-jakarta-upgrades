/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.clarityvisionsolutions.insurance.benefits.tracker.internal.model;

import com.clarityvisionsolutions.insurance.benefits.tracker.constants.InsuranceBenefitsTrackerPortletKeys;
import com.clarityvisionsolutions.insurance.benefits.tracker.model.InsurancePlan;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.InsurancePlanLocalService;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.liferay.portal.kernel.exception.PortalException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(
	property = "jakarta.portlet.name=" + InsuranceBenefitsTrackerPortletKeys.IBT_PLANS_ADMIN,
	service = AssetRendererFactory.class
)
public class InsurancePlanAssetRendererFactory
	extends BaseAssetRendererFactory<InsurancePlan> {

	public static final String TYPE = "insurance_plan";

	public InsurancePlanAssetRendererFactory() {
		setCategorizable(false);
		setClassName(InsurancePlan.class.getName());
		setPortletId(InsuranceBenefitsTrackerPortletKeys.IBT_PLANS_ADMIN);
		setSearchable(false);
	}

	@Override
	public AssetRenderer<InsurancePlan> getAssetRenderer(long classPK, int type)
		throws PortalException {

		return new InsurancePlanAssetRenderer(
			_insurancePlanLocalService.getInsurancePlan(classPK));
	}

	@Override
	public String getIconCssClass() {
		return "briefcase";
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Reference
	private InsurancePlanLocalService _insurancePlanLocalService;

}