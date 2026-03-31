package com.clarityvisionsolutions.insurance.benefits.tracker.rest.internal.graphql.servlet.v1_0;

import com.clarityvisionsolutions.insurance.benefits.tracker.rest.internal.graphql.mutation.v1_0.Mutation;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.internal.graphql.query.v1_0.Query;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.internal.resource.v1_0.BenefitUsageResourceImpl;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.internal.resource.v1_0.InsurancePlanResourceImpl;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.internal.resource.v1_0.PlanEnrollmentResourceImpl;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.resource.v1_0.BenefitUsageResource;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.resource.v1_0.InsurancePlanResource;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.resource.v1_0.PlanEnrollmentResource;

import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.vulcan.graphql.servlet.ServletData;

import jakarta.annotation.Generated;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceScope;

/**
 * @author dnebinger
 * @generated
 */
@Component(service = ServletData.class)
@Generated("")
public class ServletDataImpl implements ServletData {

	@Activate
	public void activate(BundleContext bundleContext) {
		Mutation.setBenefitUsageResourceComponentServiceObjects(
			_benefitUsageResourceComponentServiceObjects);
		Mutation.setInsurancePlanResourceComponentServiceObjects(
			_insurancePlanResourceComponentServiceObjects);
		Mutation.setPlanEnrollmentResourceComponentServiceObjects(
			_planEnrollmentResourceComponentServiceObjects);

		Query.setBenefitUsageResourceComponentServiceObjects(
			_benefitUsageResourceComponentServiceObjects);
		Query.setInsurancePlanResourceComponentServiceObjects(
			_insurancePlanResourceComponentServiceObjects);
		Query.setPlanEnrollmentResourceComponentServiceObjects(
			_planEnrollmentResourceComponentServiceObjects);
	}

	public String getApplicationName() {
		return "ClarityInsuranceBenefitsTrackerRest";
	}

	@Override
	public Mutation getMutation() {
		return new Mutation();
	}

	@Override
	public String getPath() {
		return "/clarity-insurance-benefits-tracker-rest-graphql/v1_0";
	}

	@Override
	public Query getQuery() {
		return new Query();
	}

	public ObjectValuePair<Class<?>, String> getResourceMethodObjectValuePair(
		String methodName, boolean mutation) {

		if (mutation) {
			return _resourceMethodObjectValuePairs.get(
				"mutation#" + methodName);
		}

		return _resourceMethodObjectValuePairs.get("query#" + methodName);
	}

	private static final Map<String, ObjectValuePair<Class<?>, String>>
		_resourceMethodObjectValuePairs =
			new HashMap<String, ObjectValuePair<Class<?>, String>>() {
				{
					put(
						"mutation#deleteBenefitUsage",
						new ObjectValuePair<>(
							BenefitUsageResourceImpl.class,
							"deleteBenefitUsage"));
					put(
						"mutation#deleteBenefitUsageBatch",
						new ObjectValuePair<>(
							BenefitUsageResourceImpl.class,
							"deleteBenefitUsageBatch"));
					put(
						"mutation#patchBenefitUsage",
						new ObjectValuePair<>(
							BenefitUsageResourceImpl.class,
							"patchBenefitUsage"));
					put(
						"mutation#updateBenefitUsage",
						new ObjectValuePair<>(
							BenefitUsageResourceImpl.class, "putBenefitUsage"));
					put(
						"mutation#updateBenefitUsageBatch",
						new ObjectValuePair<>(
							BenefitUsageResourceImpl.class,
							"putBenefitUsageBatch"));
					put(
						"mutation#deleteInsurancePlan",
						new ObjectValuePair<>(
							InsurancePlanResourceImpl.class,
							"deleteInsurancePlan"));
					put(
						"mutation#deleteInsurancePlanBatch",
						new ObjectValuePair<>(
							InsurancePlanResourceImpl.class,
							"deleteInsurancePlanBatch"));
					put(
						"mutation#patchInsurancePlan",
						new ObjectValuePair<>(
							InsurancePlanResourceImpl.class,
							"patchInsurancePlan"));
					put(
						"mutation#createInsurancePlan",
						new ObjectValuePair<>(
							InsurancePlanResourceImpl.class,
							"postInsurancePlan"));
					put(
						"mutation#createInsurancePlanBatch",
						new ObjectValuePair<>(
							InsurancePlanResourceImpl.class,
							"postInsurancePlanBatch"));
					put(
						"mutation#createInsurancePlanPlanEnrollment",
						new ObjectValuePair<>(
							InsurancePlanResourceImpl.class,
							"postInsurancePlanPlanEnrollment"));
					put(
						"mutation#createSiteInsurancePlan",
						new ObjectValuePair<>(
							InsurancePlanResourceImpl.class,
							"postSiteInsurancePlan"));
					put(
						"mutation#createSiteInsurancePlanBatch",
						new ObjectValuePair<>(
							InsurancePlanResourceImpl.class,
							"postSiteInsurancePlanBatch"));
					put(
						"mutation#createSiteInsurancePlansPageExportBatch",
						new ObjectValuePair<>(
							InsurancePlanResourceImpl.class,
							"postSiteInsurancePlansPageExportBatch"));
					put(
						"mutation#updateInsurancePlan",
						new ObjectValuePair<>(
							InsurancePlanResourceImpl.class,
							"putInsurancePlan"));
					put(
						"mutation#updateInsurancePlanBatch",
						new ObjectValuePair<>(
							InsurancePlanResourceImpl.class,
							"putInsurancePlanBatch"));
					put(
						"mutation#deletePlanEnrollment",
						new ObjectValuePair<>(
							PlanEnrollmentResourceImpl.class,
							"deletePlanEnrollment"));
					put(
						"mutation#deletePlanEnrollmentBatch",
						new ObjectValuePair<>(
							PlanEnrollmentResourceImpl.class,
							"deletePlanEnrollmentBatch"));
					put(
						"mutation#patchPlanEnrollment",
						new ObjectValuePair<>(
							PlanEnrollmentResourceImpl.class,
							"patchPlanEnrollment"));
					put(
						"mutation#createPlanEnrollment",
						new ObjectValuePair<>(
							PlanEnrollmentResourceImpl.class,
							"postPlanEnrollment"));
					put(
						"mutation#createPlanEnrollmentBatch",
						new ObjectValuePair<>(
							PlanEnrollmentResourceImpl.class,
							"postPlanEnrollmentBatch"));
					put(
						"mutation#createPlanEnrollmentBenefitUsage",
						new ObjectValuePair<>(
							PlanEnrollmentResourceImpl.class,
							"postPlanEnrollmentBenefitUsage"));
					put(
						"mutation#createSitePlanEnrollmentsPageExportBatch",
						new ObjectValuePair<>(
							PlanEnrollmentResourceImpl.class,
							"postSitePlanEnrollmentsPageExportBatch"));
					put(
						"mutation#updatePlanEnrollment",
						new ObjectValuePair<>(
							PlanEnrollmentResourceImpl.class,
							"putPlanEnrollment"));
					put(
						"mutation#updatePlanEnrollmentBatch",
						new ObjectValuePair<>(
							PlanEnrollmentResourceImpl.class,
							"putPlanEnrollmentBatch"));

					put(
						"query#benefitUsage",
						new ObjectValuePair<>(
							BenefitUsageResourceImpl.class, "getBenefitUsage"));
					put(
						"query#benefitUsageByExternalReferenceCode",
						new ObjectValuePair<>(
							BenefitUsageResourceImpl.class,
							"getSiteBenefitUsageByExternalReferenceCode"));
					put(
						"query#insurancePlan",
						new ObjectValuePair<>(
							InsurancePlanResourceImpl.class,
							"getInsurancePlan"));
					put(
						"query#insurancePlanPlanEnrollments",
						new ObjectValuePair<>(
							InsurancePlanResourceImpl.class,
							"getInsurancePlanPlanEnrollmentsPage"));
					put(
						"query#insurancePlanByExternalReferenceCode",
						new ObjectValuePair<>(
							InsurancePlanResourceImpl.class,
							"getSiteInsurancePlanByExternalReferenceCode"));
					put(
						"query#insurancePlans",
						new ObjectValuePair<>(
							InsurancePlanResourceImpl.class,
							"getSiteInsurancePlansPage"));
					put(
						"query#planEnrollment",
						new ObjectValuePair<>(
							PlanEnrollmentResourceImpl.class,
							"getPlanEnrollment"));
					put(
						"query#planEnrollmentBenefitUsages",
						new ObjectValuePair<>(
							PlanEnrollmentResourceImpl.class,
							"getPlanEnrollmentBenefitUsagesPage"));
					put(
						"query#planEnrollmentUsageDetail",
						new ObjectValuePair<>(
							PlanEnrollmentResourceImpl.class,
							"getPlanEnrollmentUsageDetail"));
					put(
						"query#planEnrollmentByExternalReferenceCode",
						new ObjectValuePair<>(
							PlanEnrollmentResourceImpl.class,
							"getSitePlanEnrollmentByExternalReferenceCode"));
					put(
						"query#planEnrollments",
						new ObjectValuePair<>(
							PlanEnrollmentResourceImpl.class,
							"getSitePlanEnrollmentsPage"));

					put(
						"query#PlanEnrollment.insurancePlan",
						new ObjectValuePair<>(
							InsurancePlanResourceImpl.class,
							"getInsurancePlan"));
					put(
						"query#PlanEnrollment.benefitUsages",
						new ObjectValuePair<>(
							PlanEnrollmentResourceImpl.class,
							"getPlanEnrollmentBenefitUsagesPage"));
					put(
						"query#PlanEnrollment.usageDetail",
						new ObjectValuePair<>(
							PlanEnrollmentResourceImpl.class,
							"getPlanEnrollmentUsageDetail"));
					put(
						"query#InsurancePlan.planEnrollments",
						new ObjectValuePair<>(
							InsurancePlanResourceImpl.class,
							"getInsurancePlanPlanEnrollmentsPage"));
					put(
						"query#BenefitUsageDetails.planEnrollment",
						new ObjectValuePair<>(
							PlanEnrollmentResourceImpl.class,
							"getPlanEnrollment"));
				}
			};

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<BenefitUsageResource>
		_benefitUsageResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<InsurancePlanResource>
		_insurancePlanResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<PlanEnrollmentResource>
		_planEnrollmentResourceComponentServiceObjects;

}