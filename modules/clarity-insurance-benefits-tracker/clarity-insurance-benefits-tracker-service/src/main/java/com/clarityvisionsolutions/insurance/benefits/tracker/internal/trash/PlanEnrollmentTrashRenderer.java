/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.clarityvisionsolutions.insurance.benefits.tracker.internal.trash;

import com.clarityvisionsolutions.insurance.benefits.tracker.constants.InsuranceBenefitsTrackerPortletKeys;
import com.clarityvisionsolutions.insurance.benefits.tracker.model.PlanEnrollment;
import com.liferay.asset.constants.AssetWebKeys;
import com.liferay.asset.util.AssetHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.trash.BaseJSPTrashRenderer;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * @author Zsolt Berentey
 */
public class PlanEnrollmentTrashRenderer extends BaseJSPTrashRenderer {

	public static final String TYPE = "plan_enrollment";

	public PlanEnrollmentTrashRenderer(PlanEnrollment enrollment, AssetHelper assetHelper)
		throws PortalException {

		_enrollment = enrollment;
		_assetHelper = assetHelper;
	}

	@Override
	public String getClassName() {
		return PlanEnrollment.class.getName();
	}

	@Override
	public long getClassPK() {
		return _enrollment.getPlanEnrollmentId();
	}

	@Override
	public String getIconCssClass() {
		return "comments";
	}

	@Override
	public String getJspPath(
		HttpServletRequest httpServletRequest, String template) {

		return "/plan_enrollment/view_plan_enrollment.jsp";
	}

	@Override
	public String getPortletId() {
		return InsuranceBenefitsTrackerPortletKeys.IBT_ENROLLMENT_ADMIN;
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		return null;
	}

	@Override
	public String getTitle(Locale locale) {
		return HtmlUtil.stripHtml(_enrollment.getSummary());
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

		httpServletRequest.setAttribute(
			AssetWebKeys.ASSET_HELPER, _assetHelper);

		return super.include(httpServletRequest, httpServletResponse, template);
	}

	private final AssetHelper _assetHelper;
	private final PlanEnrollment _enrollment;

}