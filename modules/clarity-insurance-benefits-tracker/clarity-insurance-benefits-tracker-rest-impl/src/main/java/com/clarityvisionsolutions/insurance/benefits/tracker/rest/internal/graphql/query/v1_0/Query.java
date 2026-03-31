package com.clarityvisionsolutions.insurance.benefits.tracker.rest.internal.graphql.query.v1_0;

import com.clarityvisionsolutions.insurance.benefits.tracker.rest.dto.v1_0.BenefitUsage;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.dto.v1_0.BenefitUsageDetails;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.dto.v1_0.InsurancePlan;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.dto.v1_0.PlanEnrollment;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.resource.v1_0.BenefitUsageResource;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.resource.v1_0.InsurancePlanResource;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.resource.v1_0.PlanEnrollmentResource;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.aggregation.Facet;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLTypeExtension;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.validation.constraints.NotEmpty;

import jakarta.ws.rs.core.UriInfo;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author dnebinger
 * @generated
 */
@Generated("")
public class Query {

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

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {benefitUsage(benefitUsageId: ___){actions, id, externalReferenceCode, siteId, creator, dateCreated, dateModified, planEnrollmentId, planEnrollmentERC, benefitType, amountUsedCents, serviceDate, reference, notes, sourceType, sourceReference, status}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public BenefitUsage benefitUsage(
			@GraphQLName("benefitUsageId") Long benefitUsageId)
		throws Exception {

		return _applyComponentServiceObjects(
			_benefitUsageResourceComponentServiceObjects,
			this::_populateResourceContext,
			benefitUsageResource -> benefitUsageResource.getBenefitUsage(
				benefitUsageId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {benefitUsageByExternalReferenceCode(externalReferenceCode: ___, siteKey: ___){actions, id, externalReferenceCode, siteId, creator, dateCreated, dateModified, planEnrollmentId, planEnrollmentERC, benefitType, amountUsedCents, serviceDate, reference, notes, sourceType, sourceReference, status}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public BenefitUsage benefitUsageByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_benefitUsageResourceComponentServiceObjects,
			this::_populateResourceContext,
			benefitUsageResource ->
				benefitUsageResource.getSiteBenefitUsageByExternalReferenceCode(
					Long.valueOf(siteKey), externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {insurancePlan(insurancePlanId: ___){actions, id, externalReferenceCode, siteId, creator, dateCreated, dateModified, planName, providerName, active, annualExamAllowanceCents, annualFramesAllowanceCents, annualLensesAllowanceCents, annualContactsAllowanceCents, coveragePeriodMonths, status}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public InsurancePlan insurancePlan(
			@GraphQLName("insurancePlanId") Long insurancePlanId)
		throws Exception {

		return _applyComponentServiceObjects(
			_insurancePlanResourceComponentServiceObjects,
			this::_populateResourceContext,
			insurancePlanResource -> insurancePlanResource.getInsurancePlan(
				insurancePlanId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {insurancePlanPlanEnrollments(aggregation: ___, filter: ___, insurancePlanId: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public InsurancePlanPage insurancePlanPlanEnrollments(
			@GraphQLName("insurancePlanId") Long insurancePlanId,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_insurancePlanResourceComponentServiceObjects,
			this::_populateResourceContext,
			insurancePlanResource -> new InsurancePlanPage(
				insurancePlanResource.getInsurancePlanPlanEnrollmentsPage(
					insurancePlanId, search,
					_aggregationBiFunction.apply(
						insurancePlanResource, aggregations),
					_filterBiFunction.apply(
						insurancePlanResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						insurancePlanResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {insurancePlanByExternalReferenceCode(externalReferenceCode: ___, siteKey: ___){actions, id, externalReferenceCode, siteId, creator, dateCreated, dateModified, planName, providerName, active, annualExamAllowanceCents, annualFramesAllowanceCents, annualLensesAllowanceCents, annualContactsAllowanceCents, coveragePeriodMonths, status}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public InsurancePlan insurancePlanByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_insurancePlanResourceComponentServiceObjects,
			this::_populateResourceContext,
			insurancePlanResource ->
				insurancePlanResource.
					getSiteInsurancePlanByExternalReferenceCode(
						Long.valueOf(siteKey), externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {insurancePlans(aggregation: ___, filter: ___, page: ___, pageSize: ___, search: ___, siteKey: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public InsurancePlanPage insurancePlans(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_insurancePlanResourceComponentServiceObjects,
			this::_populateResourceContext,
			insurancePlanResource -> new InsurancePlanPage(
				insurancePlanResource.getSiteInsurancePlansPage(
					Long.valueOf(siteKey), search,
					_aggregationBiFunction.apply(
						insurancePlanResource, aggregations),
					_filterBiFunction.apply(
						insurancePlanResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						insurancePlanResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {planEnrollment(planEnrollmentId: ___){actions, id, externalReferenceCode, siteId, creator, dateCreated, dateModified, insurancePlanId, insurancePlanERC, member, memberId, groupNumber, startDate, endDate, enrollmentStatus, notes, status}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PlanEnrollment planEnrollment(
			@GraphQLName("planEnrollmentId") Long planEnrollmentId)
		throws Exception {

		return _applyComponentServiceObjects(
			_planEnrollmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			planEnrollmentResource -> planEnrollmentResource.getPlanEnrollment(
				planEnrollmentId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {planEnrollmentBenefitUsages(aggregation: ___, filter: ___, page: ___, pageSize: ___, planEnrollmentId: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PlanEnrollmentPage planEnrollmentBenefitUsages(
			@GraphQLName("planEnrollmentId") Long planEnrollmentId,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_planEnrollmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			planEnrollmentResource -> new PlanEnrollmentPage(
				planEnrollmentResource.getPlanEnrollmentBenefitUsagesPage(
					planEnrollmentId, search,
					_aggregationBiFunction.apply(
						planEnrollmentResource, aggregations),
					_filterBiFunction.apply(
						planEnrollmentResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						planEnrollmentResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {planEnrollmentUsageDetail(planEnrollmentId: ___){planEnrollmentId, insurancePlanId, planName, providerName, startDate, endDate, enrollmentStatus, annualExamAllowanceCents, annualFramesAllowanceCents, annualLensesAllowanceCents, annualContactsAllowanceCents, examUsedCents, framesUsedCents, lensesUsedCents, contactsUsedCents}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public BenefitUsageDetails planEnrollmentUsageDetail(
			@GraphQLName("planEnrollmentId") Long planEnrollmentId)
		throws Exception {

		return _applyComponentServiceObjects(
			_planEnrollmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			planEnrollmentResource ->
				planEnrollmentResource.getPlanEnrollmentUsageDetail(
					planEnrollmentId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {planEnrollmentByExternalReferenceCode(externalReferenceCode: ___, siteKey: ___){actions, id, externalReferenceCode, siteId, creator, dateCreated, dateModified, insurancePlanId, insurancePlanERC, member, memberId, groupNumber, startDate, endDate, enrollmentStatus, notes, status}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PlanEnrollment planEnrollmentByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_planEnrollmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			planEnrollmentResource ->
				planEnrollmentResource.
					getSitePlanEnrollmentByExternalReferenceCode(
						Long.valueOf(siteKey), externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {planEnrollments(aggregation: ___, filter: ___, page: ___, pageSize: ___, search: ___, siteKey: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public PlanEnrollmentPage planEnrollments(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_planEnrollmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			planEnrollmentResource -> new PlanEnrollmentPage(
				planEnrollmentResource.getSitePlanEnrollmentsPage(
					Long.valueOf(siteKey), search,
					_aggregationBiFunction.apply(
						planEnrollmentResource, aggregations),
					_filterBiFunction.apply(
						planEnrollmentResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						planEnrollmentResource, sortsString))));
	}

	@GraphQLTypeExtension(PlanEnrollment.class)
	public class GetInsurancePlanTypeExtension {

		public GetInsurancePlanTypeExtension(PlanEnrollment planEnrollment) {
			_planEnrollment = planEnrollment;
		}

		@GraphQLField
		public InsurancePlan insurancePlan() throws Exception {
			return _applyComponentServiceObjects(
				_insurancePlanResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				insurancePlanResource -> insurancePlanResource.getInsurancePlan(
					_planEnrollment.getInsurancePlanId()));
		}

		private PlanEnrollment _planEnrollment;

	}

	@GraphQLTypeExtension(PlanEnrollment.class)
	public class GetPlanEnrollmentBenefitUsagesPageTypeExtension {

		public GetPlanEnrollmentBenefitUsagesPageTypeExtension(
			PlanEnrollment planEnrollment) {

			_planEnrollment = planEnrollment;
		}

		@GraphQLField
		public PlanEnrollmentPage benefitUsages(
				@GraphQLName("search") String search,
				@GraphQLName("aggregation") List<String> aggregations,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_planEnrollmentResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				planEnrollmentResource -> new PlanEnrollmentPage(
					planEnrollmentResource.getPlanEnrollmentBenefitUsagesPage(
						_planEnrollment.getId(), search,
						_aggregationBiFunction.apply(
							planEnrollmentResource, aggregations),
						_filterBiFunction.apply(
							planEnrollmentResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							planEnrollmentResource, sortsString))));
		}

		private PlanEnrollment _planEnrollment;

	}

	@GraphQLTypeExtension(PlanEnrollment.class)
	public class GetPlanEnrollmentUsageDetailTypeExtension {

		public GetPlanEnrollmentUsageDetailTypeExtension(
			PlanEnrollment planEnrollment) {

			_planEnrollment = planEnrollment;
		}

		@GraphQLField
		public BenefitUsageDetails usageDetail() throws Exception {
			return _applyComponentServiceObjects(
				_planEnrollmentResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				planEnrollmentResource ->
					planEnrollmentResource.getPlanEnrollmentUsageDetail(
						_planEnrollment.getId()));
		}

		private PlanEnrollment _planEnrollment;

	}

	@GraphQLTypeExtension(InsurancePlan.class)
	public class GetInsurancePlanPlanEnrollmentsPageTypeExtension {

		public GetInsurancePlanPlanEnrollmentsPageTypeExtension(
			InsurancePlan insurancePlan) {

			_insurancePlan = insurancePlan;
		}

		@GraphQLField
		public InsurancePlanPage planEnrollments(
				@GraphQLName("search") String search,
				@GraphQLName("aggregation") List<String> aggregations,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_insurancePlanResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				insurancePlanResource -> new InsurancePlanPage(
					insurancePlanResource.getInsurancePlanPlanEnrollmentsPage(
						_insurancePlan.getId(), search,
						_aggregationBiFunction.apply(
							insurancePlanResource, aggregations),
						_filterBiFunction.apply(
							insurancePlanResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							insurancePlanResource, sortsString))));
		}

		private InsurancePlan _insurancePlan;

	}

	@GraphQLTypeExtension(BenefitUsageDetails.class)
	public class GetPlanEnrollmentTypeExtension {

		public GetPlanEnrollmentTypeExtension(
			BenefitUsageDetails benefitUsageDetails) {

			_benefitUsageDetails = benefitUsageDetails;
		}

		@GraphQLField
		public PlanEnrollment planEnrollment() throws Exception {
			return _applyComponentServiceObjects(
				_planEnrollmentResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				planEnrollmentResource ->
					planEnrollmentResource.getPlanEnrollment(
						_benefitUsageDetails.getPlanEnrollmentId()));
		}

		private BenefitUsageDetails _benefitUsageDetails;

	}

	@GraphQLName("BenefitUsagePage")
	public class BenefitUsagePage {

		public BenefitUsagePage(Page benefitUsagePage) {
			actions = benefitUsagePage.getActions();

			facets = benefitUsagePage.getFacets();

			items = benefitUsagePage.getItems();
			lastPage = benefitUsagePage.getLastPage();
			page = benefitUsagePage.getPage();
			pageSize = benefitUsagePage.getPageSize();
			totalCount = benefitUsagePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<BenefitUsage> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("InsurancePlanPage")
	public class InsurancePlanPage {

		public InsurancePlanPage(Page insurancePlanPage) {
			actions = insurancePlanPage.getActions();

			facets = insurancePlanPage.getFacets();

			items = insurancePlanPage.getItems();
			lastPage = insurancePlanPage.getLastPage();
			page = insurancePlanPage.getPage();
			pageSize = insurancePlanPage.getPageSize();
			totalCount = insurancePlanPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<InsurancePlan> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("PlanEnrollmentPage")
	public class PlanEnrollmentPage {

		public PlanEnrollmentPage(Page planEnrollmentPage) {
			actions = planEnrollmentPage.getActions();

			facets = planEnrollmentPage.getFacets();

			items = planEnrollmentPage.getItems();
			lastPage = planEnrollmentPage.getLastPage();
			page = planEnrollmentPage.getPage();
			pageSize = planEnrollmentPage.getPageSize();
			totalCount = planEnrollmentPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<PlanEnrollment> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

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
		benefitUsageResource.setResourceActionLocalService(
			_resourceActionLocalService);
		benefitUsageResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		benefitUsageResource.setRoleLocalService(_roleLocalService);
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
		insurancePlanResource.setResourceActionLocalService(
			_resourceActionLocalService);
		insurancePlanResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		insurancePlanResource.setRoleLocalService(_roleLocalService);
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
		planEnrollmentResource.setResourceActionLocalService(
			_resourceActionLocalService);
		planEnrollmentResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		planEnrollmentResource.setRoleLocalService(_roleLocalService);
	}

	private static ComponentServiceObjects<BenefitUsageResource>
		_benefitUsageResourceComponentServiceObjects;
	private static ComponentServiceObjects<InsurancePlanResource>
		_insurancePlanResourceComponentServiceObjects;
	private static ComponentServiceObjects<PlanEnrollmentResource>
		_planEnrollmentResourceComponentServiceObjects;

	private AcceptLanguage _acceptLanguage;
	private BiFunction<Object, List<String>, Aggregation>
		_aggregationBiFunction;
	private com.liferay.portal.kernel.model.Company _company;
	private BiFunction
		<Object, String, com.liferay.portal.kernel.search.filter.Filter>
			_filterBiFunction;
	private GroupLocalService _groupLocalService;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private ResourceActionLocalService _resourceActionLocalService;
	private ResourcePermissionLocalService _resourcePermissionLocalService;
	private RoleLocalService _roleLocalService;
	private BiFunction<Object, String, com.liferay.portal.kernel.search.Sort[]>
		_sortsBiFunction;
	private UriInfo _uriInfo;
	private com.liferay.portal.kernel.model.User _user;

}