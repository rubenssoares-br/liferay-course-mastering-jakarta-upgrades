/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.clarityvisionsolutions.insurance.benefits.tracker.internal.trash;

import com.clarityvisionsolutions.insurance.benefits.tracker.model.InsurancePlan;
import com.clarityvisionsolutions.insurance.benefits.tracker.model.PlanEnrollment;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.InsurancePlanLocalService;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.PlanEnrollmentLocalService;
import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ContainerModel;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashRenderer;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.trash.BaseTrashHandler;
import com.liferay.trash.TrashHelper;
import com.liferay.trash.constants.TrashActionKeys;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements trash handling for the message boards category entity.
 *
 * @author Eduardo García
 */
@Component(
	property = "model.class.name=com.clarityvisionsolutions.insurance.benefits.tracker.model.InsurancePlan",
	service = TrashHandler.class
)
public class InsurancePlanTrashHandler extends BaseTrashHandler {

	@Override
	public void deleteTrashEntry(long classPK) throws PortalException {
		_insurancePlanLocalService.deleteInsurancePlan(
			_insurancePlanLocalService.getInsurancePlan(classPK));
	}

	@Override
	public String getClassName() {
		return InsurancePlan.class.getName();
	}

	@Override
	public ContainerModel getContainerModel(long containerModelId)
		throws PortalException {

		return _insurancePlanLocalService.getInsurancePlan(containerModelId);
	}

	@Override
	public String getContainerModelClassName(long classPK) {
		return InsurancePlan.class.getName();
	}

	@Override
	public String getContainerModelName() {
		return "insurance_plan";
	}

	@Override
	public List<ContainerModel> getContainerModels(
			long classPK, long parentContainerModelId, int start, int end)
		throws PortalException {

		List<ContainerModel> containerModels = new ArrayList<>();

		InsurancePlan plan = _insurancePlanLocalService.getInsurancePlan(classPK);

		containerModels.add(plan);

		return containerModels;
	}

	@Override
	public int getContainerModelsCount(
			long classPK, long parentContainerModelId)
		throws PortalException {

		_insurancePlanLocalService.getInsurancePlan(classPK);

		return 1;
	}

	@Override
	public String getDeleteMessage() {
		return "found-in-deleted-insurance-plan-x";
	}

	@Override
	public List<ContainerModel> getParentContainerModels(long containerModelId)
		throws PortalException {

		List<ContainerModel> containerModels = new ArrayList<>();

		ContainerModel containerModel = getContainerModel(containerModelId);

		while (containerModel.getParentContainerModelId() > 0) {
			containerModel = getContainerModel(
				containerModel.getParentContainerModelId());

			if (containerModel == null) {
				break;
			}

			containerModels.add(containerModel);
		}

		return containerModels;
	}

	@Override
	public String getRestoreContainedModelLink(
			PortletRequest portletRequest, long classPK)
		throws PortalException {

		InsurancePlan plan = _insurancePlanLocalService.getInsurancePlan(classPK);

		return PortletURLBuilder.create(
			getRestoreURL(portletRequest, classPK)
		).setParameter(
			"insurancePlanId", plan.getInsurancePlanId()
		).buildString();
	}

	@Override
	public String getRestoreContainerModelLink(
			PortletRequest portletRequest, long classPK)
		throws PortalException {

		InsurancePlan plan = _insurancePlanLocalService.getInsurancePlan(classPK);

		return PortletURLBuilder.create(
			getRestoreURL(portletRequest, classPK)
		).setParameter(
			"insurancePlanId", plan.getInsurancePlanId()
		).buildString();
	}

	@Override
	public String getRestoreMessage(PortletRequest portletRequest, long classPK)
		throws PortalException {

		InsurancePlan plan = _insurancePlanLocalService.getInsurancePlan(classPK);

		return getAbsolutePath(
			portletRequest, plan.getInsurancePlanId());
	}

	protected String getAbsolutePath(PortletRequest portletRequest, long insurancePlanId)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (insurancePlanId == MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) {
			return themeDisplay.translate("home");
		}

		InsurancePlan plan = _insurancePlanLocalService.fetchInsurancePlan(insurancePlanId);

		StringBundler sb = new StringBundler(8);

		sb.append(themeDisplay.translate("home"));
		sb.append(StringPool.SPACE);

		sb.append(StringPool.RAQUO_CHAR);
		sb.append(StringPool.SPACE);
		sb.append(plan.getPlanName());

		return sb.toString();
	}

	@Override
	public String getRootContainerModelName() {
		return "insurance_plan";
	}

	@Override
	public String getSubcontainerModelName() {
		return "insurance_plan";
	}

	@Override
	public String getTrashContainedModelName() {
		return "plan_enrollments";
	}

	@Override
	public int getTrashContainedModelsCount(long classPK)
		throws PortalException {

		InsurancePlan plan = _insurancePlanLocalService.getInsurancePlan(classPK);

		return _planEnrollmentLocalService.getPlanEnrollmentCounts(classPK, WorkflowConstants.STATUS_IN_TRASH);
	}

	@Override
	public String getTrashContainerModelName() {
		return "insurance_plans";
	}

	@Override
	public int getTrashContainerModelsCount(long classPK)
		throws PortalException {

		InsurancePlan plan = _insurancePlanLocalService.getInsurancePlan(classPK);

		return _insurancePlanLocalService.getInsurancePlansCount(
			plan.getGroupId(), WorkflowConstants.STATUS_IN_TRASH);
	}

	@Override
	public TrashedModel getTrashedModel(long classPK) {
		return _insurancePlanLocalService.fetchInsurancePlan(classPK);
	}

	@Override
	public int getTrashModelsCount(long classPK) throws PortalException {
		InsurancePlan plan = _insurancePlanLocalService.getInsurancePlan(classPK);

		return _planEnrollmentLocalService.getPlanEnrollmentAndBenefitUsageCounts(
			classPK, WorkflowConstants.STATUS_IN_TRASH);
	}

	@Override
	public List<TrashedModel> getTrashModelTrashedModels(
			long classPK, int start, int end,
			OrderByComparator<?> orderByComparator)
		throws PortalException {

		InsurancePlan plan = _insurancePlanLocalService.getInsurancePlan(classPK);

		List<Object> plansAndEnrollments = new ArrayList<>();
		plansAndEnrollments.add(plan);

		List<PlanEnrollment> planEnrollments = _planEnrollmentLocalService.getPlanEnrollments(classPK, WorkflowConstants.STATUS_IN_TRASH, start, end);

		if (Validator.isNotNull(planEnrollments)) {
			if (end == QueryUtil.ALL_POS) {
				plansAndEnrollments.addAll(planEnrollments);
			}
			else {
				plansAndEnrollments.addAll(planEnrollments.subList(0, end - 1));
			}
		}

		List<TrashedModel> trashedModels = new ArrayList<>(
				plansAndEnrollments.size());

		for (Object planOrBenefit : plansAndEnrollments) {
			if (planOrBenefit instanceof PlanEnrollment) {
				PlanEnrollment enrollment = (PlanEnrollment)planOrBenefit;

				trashedModels.add(enrollment);
			}
			else if (planOrBenefit instanceof InsurancePlan) {
				InsurancePlan plan2 = (InsurancePlan) planOrBenefit;

				trashedModels.add(plan2);
			}
			else {
				throw new IllegalStateException(
					"Expected InsurancePlan or PlanEnrollment, received " +
							planOrBenefit.getClass());
			}
		}

		return trashedModels;
	}

	@Override
	public TrashRenderer getTrashRenderer(long classPK) throws PortalException {
		return new InsurancePlanTrashRenderer(
			_insurancePlanLocalService.getInsurancePlan(classPK));
	}

	@Override
	public boolean hasTrashPermission(
			PermissionChecker permissionChecker, long groupId, long classPK,
			String trashActionId)
		throws PortalException {

		return super.hasTrashPermission(
			permissionChecker, groupId, classPK, trashActionId);
	}

	@Override
	public boolean isContainerModel() {
		return true;
	}

	@Override
	public boolean isMovable(long classPK) throws PortalException {
		return false;
	}

	@Override
	public boolean isRestorable(long classPK) throws PortalException {
		InsurancePlan plan = _insurancePlanLocalService.getInsurancePlan(classPK);

		if (!hasTrashPermission(
				PermissionThreadLocal.getPermissionChecker(),
				plan.getGroupId(), classPK, TrashActionKeys.RESTORE)) {

			return false;
		}

		return !_trashHelper.isInTrashContainer(plan);
	}

	@Override
	public void restoreTrashEntry(long userId, long classPK)
		throws PortalException {

		_insurancePlanLocalService.restoreInsurancePlanFromTrash(userId, classPK);
	}

	@Override
	public void updateTitle(long classPK, String name) throws PortalException {
		InsurancePlan plan = _insurancePlanLocalService.getInsurancePlan(classPK);

		plan.setPlanName(name);

		_insurancePlanLocalService.updateInsurancePlan(plan);
	}

	protected PortletURL getRestoreURL(
			PortletRequest portletRequest, long classPK)
		throws PortalException {

		PortletURL portletURL = null;

		InsurancePlan plan = _insurancePlanLocalService.getInsurancePlan(classPK);
		String portletId = PortletProviderUtil.getPortletId(
			InsurancePlan.class.getName(), PortletProvider.Action.EDIT);

		long plid = _portal.getPlidFromPortletId(
			plan.getGroupId(), portletId);

		if (plid == LayoutConstants.DEFAULT_PLID) {
			portletId = PortletProviderUtil.getPortletId(
				InsurancePlan.class.getName(), PortletProvider.Action.MANAGE);

			portletURL = _portal.getControlPanelPortletURL(
				portletRequest, portletId, PortletRequest.RENDER_PHASE);
		}
		else {
			portletURL = PortletURLFactoryUtil.create(
				portletRequest, portletId, plid, PortletRequest.RENDER_PHASE);
		}

		portletURL.setParameter(
			"mvcRenderCommandName", "/insurance_plans/view_plan");

		return portletURL;
	}

	@Override
	protected boolean hasPermission(
			PermissionChecker permissionChecker, long classPK, String actionId)
		throws PortalException {

		return _planModelResourcePermission.contains(
			permissionChecker, _insurancePlanLocalService.getInsurancePlan(classPK),
			actionId);
	}

	@Reference(
		target = "(model.class.name=com.clarityvisionsolutions.insurance.benefits.tracker.model.InsurancePlan)"
	)
	private ModelResourcePermission<InsurancePlan>
		_planModelResourcePermission;

	@Reference
	private InsurancePlanLocalService _insurancePlanLocalService;

	@Reference
	private PlanEnrollmentLocalService _planEnrollmentLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private TrashHelper _trashHelper;

}