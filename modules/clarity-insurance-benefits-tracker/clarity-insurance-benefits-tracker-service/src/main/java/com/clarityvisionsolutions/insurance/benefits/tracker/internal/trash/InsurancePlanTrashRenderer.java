/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.clarityvisionsolutions.insurance.benefits.tracker.internal.trash;

import com.clarityvisionsolutions.insurance.benefits.tracker.model.InsurancePlan;
import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.trash.BaseTrashRenderer;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * @author Eduardo García
 */
public class InsurancePlanTrashRenderer extends BaseTrashRenderer {

	public static final String TYPE = "insurance_plan";

	public InsurancePlanTrashRenderer(InsurancePlan insurancePlan) {
		_insurancePlan = insurancePlan;
	}

	@Override
	public String getClassName() {
		return InsurancePlan.class.getName();
	}

	@Override
	public long getClassPK() {
		return _insurancePlan.getPrimaryKey();
	}

	@Override
	public String getIconCssClass() {
		return "comments";
	}

	@Override
	public String getPortletId() {
		AssetRendererFactory<InsurancePlan> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClass(
				InsurancePlan.class);

		return assetRendererFactory.getPortletId();
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		return HtmlUtil.stripHtml(_insurancePlan.getSummary());
	}

	@Override
	public String getTitle(Locale locale) {
		return _insurancePlan.getPlanName();
	}

	@Override
	public String getType() {
		return TYPE;
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