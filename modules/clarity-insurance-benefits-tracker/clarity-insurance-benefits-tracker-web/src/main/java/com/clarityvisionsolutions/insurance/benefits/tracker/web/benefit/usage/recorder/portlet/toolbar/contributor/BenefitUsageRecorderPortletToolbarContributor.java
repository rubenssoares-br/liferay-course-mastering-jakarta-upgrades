package com.clarityvisionsolutions.insurance.benefits.tracker.web.benefit.usage.recorder.portlet.toolbar.contributor;

import com.clarityvisionsolutions.insurance.benefits.tracker.constants.InsuranceBenefitsTrackerPortletKeys;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.toolbar.contributor.BasePortletToolbarContributor;
import com.liferay.portal.kernel.portlet.toolbar.contributor.PortletToolbarContributor;
import com.liferay.portal.kernel.servlet.taglib.ui.MenuItem;
import com.liferay.portal.kernel.servlet.taglib.ui.URLMenuItem;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.ArrayList;
import java.util.List;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author dnebinger
 */
@Component(
	immediate = true,
	property = {
		"jakarta.portlet.name=" + InsuranceBenefitsTrackerPortletKeys.IBT_BENEFIT_USAGE_RECORDER,
		"mvc.render.command.name=/benefit-usage/view"
	},
	service = PortletToolbarContributor.class
)
public class BenefitUsageRecorderPortletToolbarContributor
	extends BasePortletToolbarContributor {

	@Override
	protected List<MenuItem> getPortletTitleMenuItems(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		List<MenuItem> menuItems = new ArrayList<>();

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long planEnrollmentId = ParamUtil.getLong(
			portletRequest, "planEnrollmentId");

		URLMenuItem urlMenuItem = new URLMenuItem();

		urlMenuItem.setLabel(
			_language.get(themeDisplay.getLocale(), "add-benefit-usage"));

		PortletURL portletURL =
			((RenderResponse)portletResponse).createRenderURL();

		portletURL.setParameter(
			"mvcRenderCommandName", "/benefit-usage/edit");
		portletURL.setParameter(
			"planEnrollmentId", String.valueOf(planEnrollmentId));

		urlMenuItem.setURL(portletURL.toString());

		menuItems.add(urlMenuItem);

		return menuItems;
	}

	@Reference
	private Language _language;

}
