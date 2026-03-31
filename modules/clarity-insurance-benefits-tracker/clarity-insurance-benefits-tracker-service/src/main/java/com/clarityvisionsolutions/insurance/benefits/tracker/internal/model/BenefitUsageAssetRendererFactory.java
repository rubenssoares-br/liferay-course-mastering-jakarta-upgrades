/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.clarityvisionsolutions.insurance.benefits.tracker.internal.model;

import com.clarityvisionsolutions.insurance.benefits.tracker.constants.InsuranceBenefitsTrackerPortletKeys;
import com.clarityvisionsolutions.insurance.benefits.tracker.model.BenefitUsage;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.BenefitUsageLocalService;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.PlanEnrollmentLocalService;
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
	property = "jakarta.portlet.name=" + InsuranceBenefitsTrackerPortletKeys.IBT_ENROLLMENT_ADMIN,
	service = AssetRendererFactory.class
)
public class BenefitUsageAssetRendererFactory
	extends BaseAssetRendererFactory<BenefitUsage> {

	public static final String TYPE = "benefit_usage";

	public BenefitUsageAssetRendererFactory() {
		setCategorizable(false);
		setClassName(BenefitUsage.class.getName());
		setPortletId(InsuranceBenefitsTrackerPortletKeys.IBT_ENROLLMENT_ADMIN);
		setSearchable(false);
	}

	@Override
	public AssetRenderer<BenefitUsage> getAssetRenderer(long classPK, int type)
		throws PortalException {

		return new BenefitUsageAssetRenderer(
			_benefitUsageLocalService.getBenefitUsage(classPK), _planEnrollmentLocalService);
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
	private BenefitUsageLocalService _benefitUsageLocalService;

	@Reference
	private PlanEnrollmentLocalService _planEnrollmentLocalService;
}