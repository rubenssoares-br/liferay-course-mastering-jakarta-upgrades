package com.clarityvisionsolutions.insurance.benefits.tracker.web.enrollments.admin.portlet.toolbar.contributor;

import com.clarityvisionsolutions.insurance.benefits.tracker.constants.InsuranceBenefitsTrackerPortletKeys;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.toolbar.contributor.BasePortletToolbarContributor;
import com.liferay.portal.kernel.portlet.toolbar.contributor.PortletToolbarContributor;
import com.liferay.portal.kernel.servlet.taglib.ui.MenuItem;
import com.liferay.portal.kernel.servlet.taglib.ui.URLMenuItem;
import com.liferay.portal.kernel.theme.ThemeDisplay;
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
		"jakarta.portlet.name=" + InsuranceBenefitsTrackerPortletKeys.IBT_ENROLLMENT_ADMIN,
		"mvc.render.command.name=/plan-enrollments/view"
	},
	service = PortletToolbarContributor.class
)
public class PlanEnrollmentAdminPortletToolbarContributor
	extends BasePortletToolbarContributor {

	@Override
	protected List<MenuItem> getPortletTitleMenuItems(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		List<MenuItem> menuItems = new ArrayList<>();

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		URLMenuItem urlMenuItem = new URLMenuItem();

		urlMenuItem.setLabel(
			_language.get(themeDisplay.getLocale(), "add-plan-enrollment"));

		PortletURL portletURL =
			((RenderResponse)portletResponse).createRenderURL();

		portletURL.setParameter(
			"mvcRenderCommandName", "/plan-enrollments/edit");

		urlMenuItem.setURL(portletURL.toString());

		menuItems.add(urlMenuItem);

		return menuItems;
	}

	@Reference
	private Language _language;

}
