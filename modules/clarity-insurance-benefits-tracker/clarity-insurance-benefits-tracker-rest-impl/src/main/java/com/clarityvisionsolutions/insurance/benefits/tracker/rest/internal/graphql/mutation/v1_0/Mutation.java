package com.clarityvisionsolutions.insurance.benefits.tracker.rest.internal.graphql.mutation.v1_0;

import com.clarityvisionsolutions.insurance.benefits.tracker.rest.dto.v1_0.BenefitUsage;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.dto.v1_0.InsurancePlan;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.dto.v1_0.PlanEnrollment;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.resource.v1_0.BenefitUsageResource;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.resource.v1_0.InsurancePlanResource;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.resource.v1_0.PlanEnrollmentResource;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineExportTaskResource;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.validation.constraints.NotEmpty;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author dnebinger
 * @generated
 */
@Generated("")
public class Mutation {

	public static void setBenefitUsageResourceComponentServiceObjects(
		ComponentServiceObjects<BenefitUsageResource>
			benefitUsageResourceComponentServiceObjects) {

		_benefitUsageResourceComponentServiceObjects =
			benefitUsageResourceComponentServiceObjects;
	}

	public static void setInsurancePlanResourceComponentServiceObjects(
		ComponentServiceObjects<InsurancePlanResource>
			insurancePlanResourceComponentServiceObjects) {

		_insurancePlanResourceComponentServiceObjects =
			insurancePlanResourceComponentServiceObjects;
	}

	public static void setPlanEnrollmentResourceComponentServiceObjects(
		ComponentServiceObjects<PlanEnrollmentResource>
			planEnrollmentResourceComponentServiceObjects) {

		_planEnrollmentResourceComponentServiceObjects =
			planEnrollmentResourceComponentServiceObjects;
	}

	@GraphQLField
	public boolean deleteBenefitUsage(
			@GraphQLName("benefitUsageId") Long benefitUsageId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_benefitUsageResourceComponentServiceObjects,
			this::_populateResourceContext,
			benefitUsageResource -> benefitUsageResource.deleteBenefitUsage(
				benefitUsageId));

		return true;
	}

	@GraphQLField
	public Response deleteBenefitUsageBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_benefitUsageResourceComponentServiceObjects,
			this::_populateResourceContext,
			benefitUsageResource ->
				benefitUsageResource.deleteBenefitUsageBatch(
					callbackURL, object));
	}

	@GraphQLField
	public BenefitUsage patchBenefitUsage(
			@GraphQLName("benefitUsageId") Long benefitUsageId,
			@GraphQLName("benefitUsage") BenefitUsage benefitUsage)
		throws Exception {

		return _applyComponentServiceObjects(
			_benefitUsageResourceComponentServiceObjects,
			this::_populateResourceContext,
			benefitUsageResource -> benefitUsageResource.patchBenefitUsage(
				benefitUsageId, benefitUsage));
	}

	@GraphQLField
	public BenefitUsage updateBenefitUsage(
			@GraphQLName("benefitUsageId") Long benefitUsageId,
			@GraphQLName("benefitUsage") BenefitUsage benefitUsage)
		throws Exception {

		return _applyComponentServiceObjects(
			_benefitUsageResourceComponentServiceObjects,
			this::_populateResourceContext,
			benefitUsageResource -> benefitUsageResource.putBenefitUsage(
				benefitUsageId, benefitUsage));
	}

	@GraphQLField
	public Response updateBenefitUsageBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_benefitUsageResourceComponentServiceObjects,
			this::_populateResourceContext,
			benefitUsageResource -> benefitUsageResource.putBenefitUsageBatch(
				callbackURL, object));
	}

	@GraphQLField
	public boolean deleteInsurancePlan(
			@GraphQLName("insurancePlanId") Long insurancePlanId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_insurancePlanResourceComponentServiceObjects,
			this::_populateResourceContext,
			insurancePlanResource -> insurancePlanResource.deleteInsurancePlan(
				insurancePlanId));

		return true;
	}

	@GraphQLField
	public Response deleteInsurancePlanBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_insurancePlanResourceComponentServiceObjects,
			this::_populateResourceContext,
			insurancePlanResource ->
				insurancePlanResource.deleteInsurancePlanBatch(
					callbackURL, object));
	}

	@GraphQLField
	public InsurancePlan patchInsurancePlan(
			@GraphQLName("insurancePlanId") Long insurancePlanId,
			@GraphQLName("insurancePlan") InsurancePlan insurancePlan)
		throws Exception {

		return _applyComponentServiceObjects(
			_insurancePlanResourceComponentServiceObjects,
			this::_populateResourceContext,
			insurancePlanResource -> insurancePlanResource.patchInsurancePlan(
				insurancePlanId, insurancePlan));
	}

	@GraphQLField
	public PlanEnrollment createInsurancePlan(
			@GraphQLName("insurancePlanId") Long insurancePlanId,
			@GraphQLName("planEnrollment") PlanEnrollment planEnrollment)
		throws Exception {

		return _applyComponentServiceObjects(
			_insurancePlanResourceComponentServiceObjects,
			this::_populateResourceContext,
			insurancePlanResource -> insurancePlanResource.postInsurancePlan(
				insurancePlanId, planEnrollment));
	}

	@GraphQLField
	public Response createInsurancePlanBatch(
			@GraphQLName("planEnrollment") PlanEnrollment planEnrollment,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_insurancePlanResourceComponentServiceObjects,
			this::_populateResourceContext,
			insurancePlanResource ->
				insurancePlanResource.postInsurancePlanBatch(
					planEnrollment, callbackURL, object));
	}

	@GraphQLField
	public PlanEnrollment createInsurancePlanPlanEnrollment(
			@GraphQLName("insurancePlanId") Long insurancePlanId,
			@GraphQLName("planEnrollment") PlanEnrollment planEnrollment)
		throws Exception {

		return _applyComponentServiceObjects(
			_insurancePlanResourceComponentServiceObjects,
			this::_populateResourceContext,
			insurancePlanResource ->
				insurancePlanResource.postInsurancePlanPlanEnrollment(
					insurancePlanId, planEnrollment));
	}

	@GraphQLField
	public InsurancePlan createSiteInsurancePlan(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("insurancePlan") InsurancePlan insurancePlan)
		throws Exception {

		return _applyComponentServiceObjects(
			_insurancePlanResourceComponentServiceObjects,
			this::_populateResourceContext,
			insurancePlanResource ->
				insurancePlanResource.postSiteInsurancePlan(
					Long.valueOf(siteKey), insurancePlan));
	}

	@GraphQLField
	public Response createSiteInsurancePlanBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_insurancePlanResourceComponentServiceObjects,
			this::_populateResourceContext,
			insurancePlanResource ->
				insurancePlanResource.postSiteInsurancePlanBatch(
					Long.valueOf(siteKey), callbackURL, object));
	}

	@GraphQLField
	public Response createSiteInsurancePlansPageExportBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_insurancePlanResourceComponentServiceObjects,
			this::_populateResourceContext,
			insurancePlanResource ->
				insurancePlanResource.postSiteInsurancePlansPageExportBatch(
					Long.valueOf(siteKey), search,
					_filterBiFunction.apply(
						insurancePlanResource, filterString),
					_sortsBiFunction.apply(insurancePlanResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public InsurancePlan updateInsurancePlan(
			@GraphQLName("insurancePlanId") Long insurancePlanId,
			@GraphQLName("insurancePlan") InsurancePlan insurancePlan)
		throws Exception {

		return _applyComponentServiceObjects(
			_insurancePlanResourceComponentServiceObjects,
			this::_populateResourceContext,
			insurancePlanResource -> insurancePlanResource.putInsurancePlan(
				insurancePlanId, insurancePlan));
	}

	@GraphQLField
	public Response updateInsurancePlanBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_insurancePlanResourceComponentServiceObjects,
			this::_populateResourceContext,
			insurancePlanResource ->
				insurancePlanResource.putInsurancePlanBatch(
					callbackURL, object));
	}

	@GraphQLField
	public boolean deletePlanEnrollment(
			@GraphQLName("planEnrollmentId") Long planEnrollmentId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_planEnrollmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			planEnrollmentResource ->
				planEnrollmentResource.deletePlanEnrollment(planEnrollmentId));

		return true;
	}

	@GraphQLField
	public Response deletePlanEnrollmentBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_planEnrollmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			planEnrollmentResource ->
				planEnrollmentResource.deletePlanEnrollmentBatch(
					callbackURL, object));
	}

	@GraphQLField
	public PlanEnrollment patchPlanEnrollment(
			@GraphQLName("planEnrollmentId") Long planEnrollmentId,
			@GraphQLName("planEnrollment") PlanEnrollment planEnrollment)
		throws Exception {

		return _applyComponentServiceObjects(
			_planEnrollmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			planEnrollmentResource ->
				planEnrollmentResource.patchPlanEnrollment(
					planEnrollmentId, planEnrollment));
	}

	@GraphQLField
	public BenefitUsage createPlanEnrollment(
			@GraphQLName("planEnrollmentId") Long planEnrollmentId,
			@GraphQLName("benefitUsage") BenefitUsage benefitUsage)
		throws Exception {

		return _applyComponentServiceObjects(
			_planEnrollmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			planEnrollmentResource -> planEnrollmentResource.postPlanEnrollment(
				planEnrollmentId, benefitUsage));
	}

	@GraphQLField
	public Response createPlanEnrollmentBatch(
			@GraphQLName("benefitUsage") BenefitUsage benefitUsage,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_planEnrollmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			planEnrollmentResource ->
				planEnrollmentResource.postPlanEnrollmentBatch(
					benefitUsage, callbackURL, object));
	}

	@GraphQLField
	public BenefitUsage createPlanEnrollmentBenefitUsage(
			@GraphQLName("planEnrollmentId") Long planEnrollmentId,
			@GraphQLName("benefitUsage") BenefitUsage benefitUsage)
		throws Exception {

		return _applyComponentServiceObjects(
			_planEnrollmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			planEnrollmentResource ->
				planEnrollmentResource.postPlanEnrollmentBenefitUsage(
					planEnrollmentId, benefitUsage));
	}

	@GraphQLField
	public Response createSitePlanEnrollmentsPageExportBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_planEnrollmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			planEnrollmentResource ->
				planEnrollmentResource.postSitePlanEnrollmentsPageExportBatch(
					Long.valueOf(siteKey), search,
					_filterBiFunction.apply(
						planEnrollmentResource, filterString),
					_sortsBiFunction.apply(planEnrollmentResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public PlanEnrollment updatePlanEnrollment(
			@GraphQLName("planEnrollmentId") Long planEnrollmentId,
			@GraphQLName("planEnrollment") PlanEnrollment planEnrollment)
		throws Exception {

		return _applyComponentServiceObjects(
			_planEnrollmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			planEnrollmentResource -> planEnrollmentResource.putPlanEnrollment(
				planEnrollmentId, planEnrollment));
	}

	@GraphQLField
	public Response updatePlanEnrollmentBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_planEnrollmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			planEnrollmentResource ->
				planEnrollmentResource.putPlanEnrollmentBatch(
					callbackURL, object));
	}

	private <T, R, E1 extends Throwable, E2 extends Throwable> R
			_applyComponentServiceObjects(
				ComponentServiceObjects<T> componentServiceObjects,
				UnsafeConsumer<T, E1> unsafeConsumer,
				UnsafeFunction<T, R, E2> unsafeFunction)
		throws E1, E2 {

		T resource = componentServiceObjects.getService();

		try {
			unsafeConsumer.accept(resource);

			return unsafeFunction.apply(resource);
		}
		finally {
			componentServiceObjects.ungetService(resource);
		}
	}

	private <T, E1 extends Throwable, E2 extends Throwable> void
			_applyVoidComponentServiceObjects(
				ComponentServiceObjects<T> componentServiceObjects,
				UnsafeConsumer<T, E1> unsafeConsumer,
				UnsafeConsumer<T, E2> unsafeFunction)
		throws E1, E2 {

		T resource = componentServiceObjects.getService();

		try {
			unsafeConsumer.accept(resource);

			unsafeFunction.accept(resource);
		}
		finally {
			componentServiceObjects.ungetService(resource);
		}
	}

	private void _populateResourceContext(
			BenefitUsageResource benefitUsageResource)
		throws Exception {

		benefitUsageResource.setContextAcceptLanguage(_acceptLanguage);
		benefitUsageResource.setContextCompany(_company);
		benefitUsageResource.setContextHttpServletRequest(_httpServletRequest);
		benefitUsageResource.setContextHttpServletResponse(
			_httpServletResponse);
		benefitUsageResource.setContextUriInfo(_uriInfo);
		benefitUsageResource.setContextUser(_user);
		benefitUsageResource.setGroupLocalService(_groupLocalService);
		benefitUsageResource.setRoleLocalService(_roleLocalService);

		benefitUsageResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		benefitUsageResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			InsurancePlanResource insurancePlanResource)
		throws Exception {

		insurancePlanResource.setContextAcceptLanguage(_acceptLanguage);
		insurancePlanResource.setContextCompany(_company);
		insurancePlanResource.setContextHttpServletRequest(_httpServletRequest);
		insurancePlanResource.setContextHttpServletResponse(
			_httpServletResponse);
		insurancePlanResource.setContextUriInfo(_uriInfo);
		insurancePlanResource.setContextUser(_user);
		insurancePlanResource.setGroupLocalService(_groupLocalService);
		insurancePlanResource.setRoleLocalService(_roleLocalService);

		insurancePlanResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		insurancePlanResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			PlanEnrollmentResource planEnrollmentResource)
		throws Exception {

		planEnrollmentResource.setContextAcceptLanguage(_acceptLanguage);
		planEnrollmentResource.setContextCompany(_company);
		planEnrollmentResource.setContextHttpServletRequest(
			_httpServletRequest);
		planEnrollmentResource.setContextHttpServletResponse(
			_httpServletResponse);
		planEnrollmentResource.setContextUriInfo(_uriInfo);
		planEnrollmentResource.setContextUser(_user);
		planEnrollmentResource.setGroupLocalService(_groupLocalService);
		planEnrollmentResource.setRoleLocalService(_roleLocalService);

		planEnrollmentResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		planEnrollmentResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private static ComponentServiceObjects<BenefitUsageResource>
		_benefitUsageResourceComponentServiceObjects;
	private static ComponentServiceObjects<InsurancePlanResource>
		_insurancePlanResourceComponentServiceObjects;
	private static ComponentServiceObjects<PlanEnrollmentResource>
		_planEnrollmentResourceComponentServiceObjects;

	private AcceptLanguage _acceptLanguage;
	private com.liferay.portal.kernel.model.Company _company;
	private BiFunction
		<Object, String, com.liferay.portal.kernel.search.filter.Filter>
			_filterBiFunction;
	private GroupLocalService _groupLocalService;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private RoleLocalService _roleLocalService;
	private BiFunction<Object, String, com.liferay.portal.kernel.search.Sort[]>
		_sortsBiFunction;
	private UriInfo _uriInfo;
	private com.liferay.portal.kernel.model.User _user;
	private VulcanBatchEngineExportTaskResource
		_vulcanBatchEngineExportTaskResource;
	private VulcanBatchEngineImportTaskResource
		_vulcanBatchEngineImportTaskResource;

}