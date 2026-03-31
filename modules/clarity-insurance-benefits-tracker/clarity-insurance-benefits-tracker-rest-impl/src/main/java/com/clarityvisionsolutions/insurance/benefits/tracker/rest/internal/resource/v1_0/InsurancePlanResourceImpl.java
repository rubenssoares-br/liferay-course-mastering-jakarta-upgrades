package com.clarityvisionsolutions.insurance.benefits.tracker.rest.internal.resource.v1_0;

import com.clarityvisionsolutions.insurance.benefits.tracker.constants.InsuranceBenefitsTrackerConstants;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.dto.v1_0.InsurancePlan;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.dto.v1_0.PlanEnrollment;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.internal.dto.v1_0.converter.InsurancePlanDTOConverter;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.internal.dto.v1_0.converter.PlanEnrollmentDTOConverter;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.resource.v1_0.InsurancePlanResource;

import com.clarityvisionsolutions.insurance.benefits.tracker.service.InsurancePlanLocalService;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.InsurancePlanService;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.PlanEnrollmentLocalService;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.PlanEnrollmentService;
import com.liferay.headless.common.spi.service.context.ServiceContextBuilder;
import com.liferay.list.type.constants.ListTypeActionKeys;
import com.liferay.list.type.constants.ListTypeConstants;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.LocalDateTimeUtil;
import com.liferay.portal.vulcan.util.SearchUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * @author dnebinger
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/insurance-plan.properties",
	scope = ServiceScope.PROTOTYPE, service = InsurancePlanResource.class
)
public class InsurancePlanResourceImpl extends BaseInsurancePlanResourceImpl {

	@Override
	public void deleteInsurancePlan(
			Long insurancePlanId)
			throws Exception {

		_insurancePlanService.deleteInsurancePlan(insurancePlanId);
	}

	@Override
	public InsurancePlan getInsurancePlan(
			Long insurancePlanId)
			throws Exception {

		com.clarityvisionsolutions.insurance.benefits.tracker.model.InsurancePlan plan = _insurancePlanService.getInsurancePlan(insurancePlanId);

		return _insurancePlanDTOConverter.toDTO(_getActions(plan), plan);
	}

	@Override
	public Page<PlanEnrollment> getInsurancePlanPlanEnrollmentsPage(
			Long insurancePlanId,
			String search,
			com.liferay.portal.vulcan.aggregation.Aggregation aggregation,
			com.liferay.portal.kernel.search.filter.Filter filter,
			Pagination pagination,
			com.liferay.portal.kernel.search.Sort[]
					sorts)
			throws Exception {

		com.clarityvisionsolutions.insurance.benefits.tracker.model.InsurancePlan plan = _insurancePlanService.getInsurancePlan(insurancePlanId);

		return SearchUtil.search(
				HashMapBuilder.put(
						"create",
						addAction(
								ActionKeys.UPDATE, "postInsurancePlan",
								com.clarityvisionsolutions.insurance.benefits.tracker.model.InsurancePlan.class.getName(), insurancePlanId)
				).put(
						"createBatch",
						addAction(
								ActionKeys.UPDATE, "postInsurancePlanBatch",
								com.clarityvisionsolutions.insurance.benefits.tracker.model.InsurancePlan.class.getName(), insurancePlanId)
				).put(
						"get",
						addAction(
								ActionKeys.VIEW, "getInsurancePlanPlanEnrollmentsPage",
								com.clarityvisionsolutions.insurance.benefits.tracker.model.InsurancePlan.class.getName(), insurancePlanId)
				).build(),
				booleanQuery -> {
				},
				filter, com.clarityvisionsolutions.insurance.benefits.tracker.model.PlanEnrollment.class.getName(),
				search, pagination,
				queryConfig -> queryConfig.setSelectedFieldNames(
						Field.ENTRY_CLASS_PK),
				searchContext -> {
					searchContext.setAttribute(Field.NAME, search);
					searchContext.setAttribute("key", search);
					searchContext.setAttribute(
							"insurancePlanId", insurancePlanId);
					searchContext.setCompanyId(contextCompany.getCompanyId());
					searchContext.setGroupIds(new long[] {plan.getGroupId()});
				},
				sorts,
				document -> {
					com.clarityvisionsolutions.insurance.benefits.tracker.model.PlanEnrollment enrollment =
							_planEnrollmentService.getPlanEnrollment(
									GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)));

					return _planEnrollmentDTOConverter.toDTO(_getActions(enrollment), enrollment);
				});
	}

	@Override
	public InsurancePlan getSiteInsurancePlanByExternalReferenceCode(
			Long siteId,
			String externalReferenceCode)
			throws Exception {

		com.clarityvisionsolutions.insurance.benefits.tracker.model.InsurancePlan model = _insurancePlanLocalService.getInsurancePlanByExternalReferenceCode(externalReferenceCode, siteId);

		return getInsurancePlan(model.getInsurancePlanId());
	}

	@Override
	public Page<InsurancePlan> getSiteInsurancePlansPage(
			Long siteId,
			String search,
			com.liferay.portal.vulcan.aggregation.Aggregation aggregation,
			com.liferay.portal.kernel.search.filter.Filter filter,
			Pagination pagination,
			com.liferay.portal.kernel.search.Sort[]
					sorts)
			throws Exception {

		return SearchUtil.search(
				HashMapBuilder.put(
						"create",
						addAction(
								ActionKeys.ADD_ENTRY,
								"postSiteInsurancePlan", InsuranceBenefitsTrackerConstants.RESOURCE_NAME,
								siteId)
				).put(
						"createBatch",
						addAction(
								ActionKeys.ADD_ENTRY,
								"postSiteInsurancePlanBatch",
								InsuranceBenefitsTrackerConstants.RESOURCE_NAME,
								siteId)
				).put(
						"get",
						addAction(
								ActionKeys.VIEW, "getSiteInsurancePlansPage",
								InsuranceBenefitsTrackerConstants.RESOURCE_NAME,
								siteId)
				).build(),
				booleanQuery -> {
				},
				filter,
				com.clarityvisionsolutions.insurance.benefits.tracker.model.InsurancePlan.class.getName(),
				search, pagination,
				queryConfig -> queryConfig.setSelectedFieldNames(
						Field.ENTRY_CLASS_PK),
				searchContext -> {
					searchContext.setAttribute(Field.NAME, search);
					searchContext.setCompanyId(contextCompany.getCompanyId());
					searchContext.setGroupIds(new long[] {siteId});
				},
				sorts,
				document -> {
					com.clarityvisionsolutions.insurance.benefits.tracker.model.InsurancePlan model =
							_insurancePlanService.getInsurancePlan(
									GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)));

					return _insurancePlanDTOConverter.toDTO(_getActions(model), model);
				});
	}

	@Override
	public PlanEnrollment postInsurancePlan(
			Long insurancePlanId,
			PlanEnrollment planEnrollment)
			throws Exception {

		return postInsurancePlanPlanEnrollment(insurancePlanId, planEnrollment);
	}

	@Override
	public PlanEnrollment postInsurancePlanPlanEnrollment(
			Long insurancePlanId,
			PlanEnrollment planEnrollment)
			throws Exception {

		if (Validator.isNull(planEnrollment)) {
			_log.warn("Invalid plan enrollment");
			throw new IllegalArgumentException("Invalid plan enrollment");
		}

		// okay, so we're adding a new plan enrollment to the insurance plan.
		com.clarityvisionsolutions.insurance.benefits.tracker.model.InsurancePlan plan = _insurancePlanService.getInsurancePlan(insurancePlanId);
		com.clarityvisionsolutions.insurance.benefits.tracker.model.PlanEnrollment enrollment = null;

		// plan is valid. Need to validate the plan enrollment, start with the erc
		String enrollmentErc = planEnrollment.getExternalReferenceCode();
		if (Validator.isNotNull(enrollmentErc)) {
			// we should not have an existing enrollment with the same erc
			try {
				enrollment = _planEnrollmentLocalService.getPlanEnrollmentByExternalReferenceCode(enrollmentErc, plan.getGroupId());
			} catch (Exception ignored) {
				// ignored because if we didn't get a match, we don't care about the exception
			}

			if (Validator.isNotNull(enrollment)) {
				_log.warn("An enrollment already exists with this ERC");
				throw new IllegalArgumentException("A plan enrollment already exists with this ERC");
			}
		}

		// also need to validate the member
		if (Validator.isNull(planEnrollment.getMember())) {
			_log.warn("Invalid member");
			throw new IllegalArgumentException("Invalid member");
		} else if (Validator.isNull(planEnrollment.getMember().getId())) {
			_log.warn("Invalid member id");
			throw new IllegalArgumentException("Invalid member id");
		} else {
			// member id is valid, is it a valid user?
			_userService.getUserById(planEnrollment.getMember().getId());

			// The user was retrieved successfully, otherwise an exception would have been thrown
		}

		LocalDateTime localDateTime = LocalDateTimeUtil.toLocalDateTime(
				planEnrollment.getStartDate(), null,
				ZoneId.of(contextUser.getTimeZoneId()));

		Date startDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

		localDateTime = LocalDateTimeUtil.toLocalDateTime(
				planEnrollment.getEndDate(), null,
				ZoneId.of(contextUser.getTimeZoneId()));

		Date endDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

		ServiceContext serviceContext = _createServiceContext(planEnrollment, plan.getGroupId());

		// create the new enrollment
		enrollment = _planEnrollmentService.addPlanEnrollment(insurancePlanId, planEnrollment.getMember().getId(), planEnrollment.getMemberId(),
				planEnrollment.getGroupNumber(), startDate, endDate, planEnrollment.getEnrollmentStatus(), planEnrollment.getNotes(), serviceContext);

		return _planEnrollmentDTOConverter.toDTO(_getActions(enrollment), enrollment);
	}

	@Override
	public InsurancePlan postSiteInsurancePlan(
			Long siteId,
			InsurancePlan insurancePlan)
			throws Exception {

		return new InsurancePlan();
	}

	@Override
	public InsurancePlan putInsurancePlan(
			Long insurancePlanId,
			InsurancePlan insurancePlan)
			throws Exception {

		return new InsurancePlan();
	}

	private Map<String, Map<String, String>> _getActions(
			com.clarityvisionsolutions.insurance.benefits.tracker.model.PlanEnrollment model) {

		return HashMapBuilder
				.<String, Map<String, String>>put("delete", addAction(ActionKeys.DELETE, model, "deletePlanEnrollment")
				).put("get", addAction(ActionKeys.VIEW, model, "getPlanEnrollment")
				).put("patch", addAction(ActionKeys.UPDATE, model, "patchPlanEnrollment")
				).put("update", addAction(ActionKeys.UPDATE, model, "putPlanEnrollment")
				).build();
	}

	private Map<String, Map<String, String>> _getActions(
			com.clarityvisionsolutions.insurance.benefits.tracker.model.InsurancePlan model) {

		return HashMapBuilder
				.<String, Map<String, String>>put("delete", addAction(ActionKeys.DELETE, model, "deleteInsurancePlan")
				).put("get", addAction(ActionKeys.VIEW, model, "getInsurancePlan")
				).put("patch", addAction(ActionKeys.UPDATE, model, "patchInsurancePlan")
				).put("update", addAction(ActionKeys.UPDATE, model, "putInsurancePlan")
				).build();
	}

	private ServiceContext _createServiceContext(
			PlanEnrollment planEnrollment, long groupId) {

		return ServiceContextBuilder.create(
				groupId, contextHttpServletRequest,
				"nobody" // need some kind of value that will not trigger adding default perms
		).build();
	}

	@Reference
	private InsurancePlanService _insurancePlanService;

	@Reference
	private InsurancePlanLocalService _insurancePlanLocalService;

	@Reference
	private PlanEnrollmentService _planEnrollmentService;

	@Reference
	private PlanEnrollmentLocalService _planEnrollmentLocalService;

	@Reference
	private InsurancePlanDTOConverter _insurancePlanDTOConverter;

	@Reference
	private PlanEnrollmentDTOConverter _planEnrollmentDTOConverter;

	@Reference
	private UserService _userService;

	private static final Log _log = LogFactoryUtil.getLog(InsurancePlanResourceImpl.class);
}