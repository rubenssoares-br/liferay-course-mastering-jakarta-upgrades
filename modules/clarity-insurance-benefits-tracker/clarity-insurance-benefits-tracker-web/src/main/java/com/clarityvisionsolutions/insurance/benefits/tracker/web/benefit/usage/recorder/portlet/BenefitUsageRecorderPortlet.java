package com.clarityvisionsolutions.insurance.benefits.tracker.web.benefit.usage.recorder.portlet;

import com.clarityvisionsolutions.insurance.benefits.tracker.constants.InsuranceBenefitsTrackerPortletKeys;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;

import jakarta.portlet.Portlet;

import org.osgi.service.component.annotations.Component;

/**
 * @author dnebinger
 */
@Component(
	property = {
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.instanceable=true",
		"jakarta.portlet.display-name=BenefitUsageRecorder",
		"jakarta.portlet.init-param.mvc-command-names-default-views=/benefit-usage/search",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/benefit-usage/view.jsp",
		"jakarta.portlet.name=" + InsuranceBenefitsTrackerPortletKeys.IBT_BENEFIT_USAGE_RECORDER,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class BenefitUsageRecorderPortlet extends MVCPortlet {
}
