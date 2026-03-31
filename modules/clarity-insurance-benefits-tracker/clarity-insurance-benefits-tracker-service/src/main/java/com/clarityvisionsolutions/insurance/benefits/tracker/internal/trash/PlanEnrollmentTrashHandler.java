/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.clarityvisionsolutions.insurance.benefits.tracker.internal.trash;

import com.clarityvisionsolutions.insurance.benefits.tracker.model.BenefitUsage;
import com.clarityvisionsolutions.insurance.benefits.tracker.model.InsurancePlan;
import com.clarityvisionsolutions.insurance.benefits.tracker.model.PlanEnrollment;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.InsurancePlanLocalService;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.PlanEnrollmentLocalService;
import com.liferay.asset.util.AssetHelper;
import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ContainerModel;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashRenderer;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.trash.BaseTrashHandler;
import com.liferay.trash.TrashHelper;
import com.liferay.trash.constants.TrashActionKeys;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements trash handling for message boards enrollment entity.
 *
 * @author Zsolt Berentey
 */
@Component(
	property = "model.class.name=com.clarityvisionsolutions.insurance.benefits.tracker.model.PlanEnrollment",
	service = TrashHandler.class
)
public class PlanEnrollmentTrashHandler extends BaseTrashHandler {

	@Override
	public void deleteTrashEntry(long classPK) throws PortalException {
		_planEnrollmentLocalService.deletePlanEnrollment(classPK);
	}

	@Override
	public String getClassName() {
		return PlanEnrollment.class.getName();
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

		PlanEnrollment enrollment = _planEnrollmentLocalService.getPlanEnrollment(classPK);

		List<InsurancePlan> plans = _insurancePlanLocalService.getInsurancePlans(
			enrollment.getGroupId(), WorkflowConstants.STATUS_APPROVED, start, end);

		for (InsurancePlan insurancePlan : plans) {
			containerModels.add(insurancePlan);
		}

		return containerModels;
	}

	@Override
	public int getContainerModelsCount(
			long classPK, long parentContainerModelId)
		throws PortalException {

		PlanEnrollment enrollment = _planEnrollmentLocalService.getPlanEnrollment(classPK);

		return _insurancePlanLocalService.getInsurancePlansCount(
			enrollment.getGroupId(), WorkflowConstants.STATUS_APPROVED);
	}

	@Override
	public String getRestoreContainedModelLink(
			PortletRequest portletRequest, long classPK)
		throws PortalException {

		PlanEnrollment enrollment = _planEnrollmentLocalService.getPlanEnrollment(classPK);

		return PortletURLBuilder.create(
			getRestoreURL(portletRequest, classPK, false)
		).setParameter(
			"insurancePlanId", enrollment.getInsurancePlanId()
		).buildString();
	}

	@Override
	public String getRestoreContainerModelLink(
			PortletRequest portletRequest, long classPK)
		throws PortalException {

		PlanEnrollment enrollment = _planEnrollmentLocalService.getPlanEnrollment(classPK);

		return PortletURLBuilder.create(
			getRestoreURL(portletRequest, classPK, true)
		).setParameter(
			"insurancePlanId", enrollment.getInsurancePlanId()
		).buildString();
	}

	@Override
	public String getRestoreMessage(PortletRequest portletRequest, long classPK)
		throws PortalException {

		PlanEnrollment enrollment = _planEnrollmentLocalService.getPlanEnrollment(classPK);

		return getAbsolutePath(
			portletRequest, enrollment.getInsurancePlanId());
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
	public TrashedModel getTrashedModel(long classPK) {
		return _planEnrollmentLocalService.fetchPlanEnrollment(classPK);
	}

	@Override
	public TrashRenderer getTrashRenderer(long classPK) throws PortalException {
		PlanEnrollmentTrashRenderer planEnrollmentTrashRenderer = new PlanEnrollmentTrashRenderer(
			_planEnrollmentLocalService.getPlanEnrollment(classPK), _assetHelper);

		planEnrollmentTrashRenderer.setServletContext(_servletContext);

		return planEnrollmentTrashRenderer;
	}

	@Override
	public boolean hasTrashPermission(
			PermissionChecker permissionChecker, long groupId, long classPK,
			String trashActionId)
		throws PortalException {

		if (trashActionId.equals(TrashActionKeys.MOVE)) {
			return ModelResourcePermissionUtil.contains(
				_insurancePlanModelResourcePermission, permissionChecker, groupId,
				classPK, ActionKeys.ADD_MESSAGE);
		}

		return super.hasTrashPermission(
			permissionChecker, groupId, classPK, trashActionId);
	}

	@Override
	public boolean isMovable(long classPK) throws PortalException {
		// plan enrollments cannot move to different insurance plans.

		return false;
	}

	@Override
	public boolean isRestorable(long classPK) throws PortalException {
		PlanEnrollment enrollment = _planEnrollmentLocalService.getPlanEnrollment(classPK);

		if (((enrollment.getInsurancePlanId() > 0) &&
			 (_insurancePlanLocalService.fetchInsurancePlan(enrollment.getInsurancePlanId()) ==
				 null)) ||
			!hasTrashPermission(
				PermissionThreadLocal.getPermissionChecker(),
				enrollment.getGroupId(), classPK, TrashActionKeys.RESTORE)) {

			return false;
		}

		return !_trashHelper.isInTrashContainer(enrollment);
	}

	@Override
	public void restoreTrashEntry(long userId, long classPK)
		throws PortalException {

		_planEnrollmentLocalService.restorePlanEnrollmentFromTrash(userId, classPK);
	}

	protected PortletURL getRestoreURL(
			PortletRequest portletRequest, long classPK, boolean containerModel)
		throws PortalException {

		PortletURL portletURL = null;

		PlanEnrollment enrollment = _planEnrollmentLocalService.getPlanEnrollment(classPK);
		String portletId = PortletProviderUtil.getPortletId(
			PlanEnrollment.class.getName(), PortletProvider.Action.EDIT);

		long plid = _portal.getPlidFromPortletId(
			enrollment.getGroupId(), portletId);

		if (plid == LayoutConstants.DEFAULT_PLID) {
			portletId = PortletProviderUtil.getPortletId(
				PlanEnrollment.class.getName(), PortletProvider.Action.MANAGE);

			portletURL = _portal.getControlPanelPortletURL(
				portletRequest, portletId, PortletRequest.RENDER_PHASE);
		}
		else {
			portletURL = PortletURLFactoryUtil.create(
				portletRequest, portletId, plid, PortletRequest.RENDER_PHASE);
		}

		if (containerModel) {
			String mvcRenderCommandName = "/plan_enrollments/view";

			if (enrollment.getInsurancePlanId() !=
					-1) {

				mvcRenderCommandName = "/plan_enrollments/view_insurance_plan";
			}

			portletURL.setParameter(
				"mvcRenderCommandName", mvcRenderCommandName);
		}
		else {
			portletURL.setParameter(
				"mvcRenderCommandName", "/plan_enrollments/view_enrollment");
		}

		return portletURL;
	}

	@Override
	protected boolean hasPermission(
			PermissionChecker permissionChecker, long classPK, String actionId)
		throws PortalException {

		PlanEnrollment enrollment = _planEnrollmentLocalService.getPlanEnrollment(classPK);

		return _planEnrollmentModelResourcePermission.contains(
			permissionChecker, classPK, actionId);
	}

	@Reference
	private AssetHelper _assetHelper;

	@Reference(
		target = "(model.class.name=com.clarityvisionsolutions.insurance.benefits.tracker.model.InsurancePlan)"
	)
	private ModelResourcePermission<InsurancePlan>
		_insurancePlanModelResourcePermission;

	@Reference
	private InsurancePlanLocalService _insurancePlanLocalService;

	@Reference
	private PlanEnrollmentLocalService _planEnrollmentLocalService;

	@Reference(
		target = "(model.class.name=com.clarityvisionsolutions.insurance.benefits.tracker.model.PlanEnrollment)"
	)
	private ModelResourcePermission<PlanEnrollment> _planEnrollmentModelResourcePermission;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.clarityvisionsolutions.insurance.benefits.tracker.web)"
	)
	private ServletContext _servletContext;

	@Reference
	private TrashHelper _trashHelper;

}