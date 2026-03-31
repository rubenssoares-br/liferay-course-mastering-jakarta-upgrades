package com.clarityvisionsolutions.insurance.benefits.tracker.rest.internal.jaxrs.application;

import jakarta.annotation.Generated;

import jakarta.ws.rs.core.Application;

import org.osgi.service.component.annotations.Component;

/**
 * @author dnebinger
 * @generated
 */
@Component(
	property = {
		"liferay.jackson=false",
		"osgi.jaxrs.application.base=/clarity-insurance-benefits-tracker-rest",
		"osgi.jaxrs.extension.select=(osgi.jaxrs.name=Liferay.Vulcan)",
		"osgi.jaxrs.name=ClarityInsuranceBenefitsTrackerRest"
	},
	service = Application.class
)
@Generated("")
public class ClarityInsuranceBenefitsTrackerRestApplication
	extends Application {
}