/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.clarityvisionsolutions.insurance.benefits.tracker.service.persistence.impl;

import com.clarityvisionsolutions.insurance.benefits.tracker.exception.DuplicatePlanEnrollmentExternalReferenceCodeException;
import com.clarityvisionsolutions.insurance.benefits.tracker.exception.NoSuchPlanEnrollmentException;
import com.clarityvisionsolutions.insurance.benefits.tracker.model.PlanEnrollment;
import com.clarityvisionsolutions.insurance.benefits.tracker.model.PlanEnrollmentTable;
import com.clarityvisionsolutions.insurance.benefits.tracker.model.impl.PlanEnrollmentImpl;
import com.clarityvisionsolutions.insurance.benefits.tracker.model.impl.PlanEnrollmentModelImpl;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.persistence.PlanEnrollmentPersistence;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.persistence.PlanEnrollmentUtil;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.persistence.impl.constants.CIBTPersistenceConstants;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.sanitizer.Sanitizer;
import com.liferay.portal.kernel.sanitizer.SanitizerException;
import com.liferay.portal.kernel.sanitizer.SanitizerUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the Plan Enrollment service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = PlanEnrollmentPersistence.class)
public class PlanEnrollmentPersistenceImpl
	extends BasePersistenceImpl<PlanEnrollment>
	implements PlanEnrollmentPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>PlanEnrollmentUtil</code> to access the Plan Enrollment persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		PlanEnrollmentImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathWithPaginationFindByUuid;
	private FinderPath _finderPathWithoutPaginationFindByUuid;
	private FinderPath _finderPathCountByUuid;

	/**
	 * Returns all the Plan Enrollments where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Plan Enrollments where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @return the range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByUuid;
					finderArgs = new Object[] {uuid};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByUuid;
				finderArgs = new Object[] {uuid, start, end, orderByComparator};
			}

			List<PlanEnrollment> list = null;

			if (useFinderCache) {
				list = (List<PlanEnrollment>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (PlanEnrollment planEnrollment : list) {
						if (!uuid.equals(planEnrollment.getUuid())) {
							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						3 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(3);
				}

				sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

				boolean bindUuid = false;

				if (uuid.isEmpty()) {
					sb.append(_FINDER_COLUMN_UUID_UUID_3);
				}
				else {
					bindUuid = true;

					sb.append(_FINDER_COLUMN_UUID_UUID_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindUuid) {
						queryPos.add(uuid);
					}

					list = (List<PlanEnrollment>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first Plan Enrollment in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByUuid_First(
			String uuid, OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByUuid_First(
			uuid, orderByComparator);

		if (planEnrollment != null) {
			return planEnrollment;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchPlanEnrollmentException(sb.toString());
	}

	/**
	 * Returns the first Plan Enrollment in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByUuid_First(
		String uuid, OrderByComparator<PlanEnrollment> orderByComparator) {

		List<PlanEnrollment> list = findByUuid(uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Plan Enrollment in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByUuid_Last(
			String uuid, OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByUuid_Last(
			uuid, orderByComparator);

		if (planEnrollment != null) {
			return planEnrollment;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchPlanEnrollmentException(sb.toString());
	}

	/**
	 * Returns the last Plan Enrollment in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByUuid_Last(
		String uuid, OrderByComparator<PlanEnrollment> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<PlanEnrollment> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Plan Enrollments before and after the current Plan Enrollment in the ordered set where uuid = &#63;.
	 *
	 * @param planEnrollmentId the primary key of the current Plan Enrollment
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a Plan Enrollment with the primary key could not be found
	 */
	@Override
	public PlanEnrollment[] findByUuid_PrevAndNext(
			long planEnrollmentId, String uuid,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		uuid = Objects.toString(uuid, "");

		PlanEnrollment planEnrollment = findByPrimaryKey(planEnrollmentId);

		Session session = null;

		try {
			session = openSession();

			PlanEnrollment[] array = new PlanEnrollmentImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, planEnrollment, uuid, orderByComparator, true);

			array[1] = planEnrollment;

			array[2] = getByUuid_PrevAndNext(
				session, planEnrollment, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PlanEnrollment getByUuid_PrevAndNext(
		Session session, PlanEnrollment planEnrollment, String uuid,
		OrderByComparator<PlanEnrollment> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_UUID_UUID_3);
		}
		else {
			bindUuid = true;

			sb.append(_FINDER_COLUMN_UUID_UUID_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindUuid) {
			queryPos.add(uuid);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						planEnrollment)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PlanEnrollment> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Plan Enrollments where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (PlanEnrollment planEnrollment :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(planEnrollment);
		}
	}

	/**
	 * Returns the number of Plan Enrollments where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching Plan Enrollments
	 */
	@Override
	public int countByUuid(String uuid) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid;

			Object[] finderArgs = new Object[] {uuid};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_PLANENROLLMENT_WHERE);

				boolean bindUuid = false;

				if (uuid.isEmpty()) {
					sb.append(_FINDER_COLUMN_UUID_UUID_3);
				}
				else {
					bindUuid = true;

					sb.append(_FINDER_COLUMN_UUID_UUID_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindUuid) {
						queryPos.add(uuid);
					}

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	private static final String _FINDER_COLUMN_UUID_UUID_2 =
		"planEnrollment.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(planEnrollment.uuid IS NULL OR planEnrollment.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;

	/**
	 * Returns the Plan Enrollment where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchPlanEnrollmentException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByUUID_G(String uuid, long groupId)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByUUID_G(uuid, groupId);

		if (planEnrollment == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("uuid=");
			sb.append(uuid);

			sb.append(", groupId=");
			sb.append(groupId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchPlanEnrollmentException(sb.toString());
		}

		return planEnrollment;
	}

	/**
	 * Returns the Plan Enrollment where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the Plan Enrollment where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			uuid = Objects.toString(uuid, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {uuid, groupId};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByUUID_G, finderArgs, this);
			}

			if (result instanceof PlanEnrollment) {
				PlanEnrollment planEnrollment = (PlanEnrollment)result;

				if (!Objects.equals(uuid, planEnrollment.getUuid()) ||
					(groupId != planEnrollment.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

				boolean bindUuid = false;

				if (uuid.isEmpty()) {
					sb.append(_FINDER_COLUMN_UUID_G_UUID_3);
				}
				else {
					bindUuid = true;

					sb.append(_FINDER_COLUMN_UUID_G_UUID_2);
				}

				sb.append(_FINDER_COLUMN_UUID_G_GROUPID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindUuid) {
						queryPos.add(uuid);
					}

					queryPos.add(groupId);

					List<PlanEnrollment> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByUUID_G, finderArgs, list);
						}
					}
					else {
						PlanEnrollment planEnrollment = list.get(0);

						result = planEnrollment;

						cacheResult(planEnrollment);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			if (result instanceof List<?>) {
				return null;
			}
			else {
				return (PlanEnrollment)result;
			}
		}
	}

	/**
	 * Removes the Plan Enrollment where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the Plan Enrollment that was removed
	 */
	@Override
	public PlanEnrollment removeByUUID_G(String uuid, long groupId)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = findByUUID_G(uuid, groupId);

		return remove(planEnrollment);
	}

	/**
	 * Returns the number of Plan Enrollments where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching Plan Enrollments
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		PlanEnrollment planEnrollment = fetchByUUID_G(uuid, groupId);

		if (planEnrollment == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_UUID_G_UUID_2 =
		"planEnrollment.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(planEnrollment.uuid IS NULL OR planEnrollment.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"planEnrollment.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the Plan Enrollments where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Plan Enrollments where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @return the range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByUuid_C;
					finderArgs = new Object[] {uuid, companyId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByUuid_C;
				finderArgs = new Object[] {
					uuid, companyId, start, end, orderByComparator
				};
			}

			List<PlanEnrollment> list = null;

			if (useFinderCache) {
				list = (List<PlanEnrollment>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (PlanEnrollment planEnrollment : list) {
						if (!uuid.equals(planEnrollment.getUuid()) ||
							(companyId != planEnrollment.getCompanyId())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						4 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(4);
				}

				sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

				boolean bindUuid = false;

				if (uuid.isEmpty()) {
					sb.append(_FINDER_COLUMN_UUID_C_UUID_3);
				}
				else {
					bindUuid = true;

					sb.append(_FINDER_COLUMN_UUID_C_UUID_2);
				}

				sb.append(_FINDER_COLUMN_UUID_C_COMPANYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindUuid) {
						queryPos.add(uuid);
					}

					queryPos.add(companyId);

					list = (List<PlanEnrollment>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first Plan Enrollment in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (planEnrollment != null) {
			return planEnrollment;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchPlanEnrollmentException(sb.toString());
	}

	/**
	 * Returns the first Plan Enrollment in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		List<PlanEnrollment> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Plan Enrollment in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (planEnrollment != null) {
			return planEnrollment;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchPlanEnrollmentException(sb.toString());
	}

	/**
	 * Returns the last Plan Enrollment in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<PlanEnrollment> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Plan Enrollments before and after the current Plan Enrollment in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param planEnrollmentId the primary key of the current Plan Enrollment
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a Plan Enrollment with the primary key could not be found
	 */
	@Override
	public PlanEnrollment[] findByUuid_C_PrevAndNext(
			long planEnrollmentId, String uuid, long companyId,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		uuid = Objects.toString(uuid, "");

		PlanEnrollment planEnrollment = findByPrimaryKey(planEnrollmentId);

		Session session = null;

		try {
			session = openSession();

			PlanEnrollment[] array = new PlanEnrollmentImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, planEnrollment, uuid, companyId, orderByComparator,
				true);

			array[1] = planEnrollment;

			array[2] = getByUuid_C_PrevAndNext(
				session, planEnrollment, uuid, companyId, orderByComparator,
				false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PlanEnrollment getByUuid_C_PrevAndNext(
		Session session, PlanEnrollment planEnrollment, String uuid,
		long companyId, OrderByComparator<PlanEnrollment> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

		boolean bindUuid = false;

		if (uuid.isEmpty()) {
			sb.append(_FINDER_COLUMN_UUID_C_UUID_3);
		}
		else {
			bindUuid = true;

			sb.append(_FINDER_COLUMN_UUID_C_UUID_2);
		}

		sb.append(_FINDER_COLUMN_UUID_C_COMPANYID_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindUuid) {
			queryPos.add(uuid);
		}

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						planEnrollment)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PlanEnrollment> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Plan Enrollments where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (PlanEnrollment planEnrollment :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(planEnrollment);
		}
	}

	/**
	 * Returns the number of Plan Enrollments where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching Plan Enrollments
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid_C;

			Object[] finderArgs = new Object[] {uuid, companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_PLANENROLLMENT_WHERE);

				boolean bindUuid = false;

				if (uuid.isEmpty()) {
					sb.append(_FINDER_COLUMN_UUID_C_UUID_3);
				}
				else {
					bindUuid = true;

					sb.append(_FINDER_COLUMN_UUID_C_UUID_2);
				}

				sb.append(_FINDER_COLUMN_UUID_C_COMPANYID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindUuid) {
						queryPos.add(uuid);
					}

					queryPos.add(companyId);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	private static final String _FINDER_COLUMN_UUID_C_UUID_2 =
		"planEnrollment.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(planEnrollment.uuid IS NULL OR planEnrollment.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"planEnrollment.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByGroupId;
	private FinderPath _finderPathWithoutPaginationFindByGroupId;
	private FinderPath _finderPathCountByGroupId;

	/**
	 * Returns all the Plan Enrollments where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Plan Enrollments where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @return the range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByGroupId(
		long groupId, int start, int end) {

		return findByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByGroupId;
					finderArgs = new Object[] {groupId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByGroupId;
				finderArgs = new Object[] {
					groupId, start, end, orderByComparator
				};
			}

			List<PlanEnrollment> list = null;

			if (useFinderCache) {
				list = (List<PlanEnrollment>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (PlanEnrollment planEnrollment : list) {
						if (groupId != planEnrollment.getGroupId()) {
							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						3 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(3);
				}

				sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

				sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					list = (List<PlanEnrollment>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first Plan Enrollment in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByGroupId_First(
			long groupId, OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByGroupId_First(
			groupId, orderByComparator);

		if (planEnrollment != null) {
			return planEnrollment;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchPlanEnrollmentException(sb.toString());
	}

	/**
	 * Returns the first Plan Enrollment in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByGroupId_First(
		long groupId, OrderByComparator<PlanEnrollment> orderByComparator) {

		List<PlanEnrollment> list = findByGroupId(
			groupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Plan Enrollment in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByGroupId_Last(
			long groupId, OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByGroupId_Last(
			groupId, orderByComparator);

		if (planEnrollment != null) {
			return planEnrollment;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchPlanEnrollmentException(sb.toString());
	}

	/**
	 * Returns the last Plan Enrollment in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByGroupId_Last(
		long groupId, OrderByComparator<PlanEnrollment> orderByComparator) {

		int count = countByGroupId(groupId);

		if (count == 0) {
			return null;
		}

		List<PlanEnrollment> list = findByGroupId(
			groupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Plan Enrollments before and after the current Plan Enrollment in the ordered set where groupId = &#63;.
	 *
	 * @param planEnrollmentId the primary key of the current Plan Enrollment
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a Plan Enrollment with the primary key could not be found
	 */
	@Override
	public PlanEnrollment[] findByGroupId_PrevAndNext(
			long planEnrollmentId, long groupId,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = findByPrimaryKey(planEnrollmentId);

		Session session = null;

		try {
			session = openSession();

			PlanEnrollment[] array = new PlanEnrollmentImpl[3];

			array[0] = getByGroupId_PrevAndNext(
				session, planEnrollment, groupId, orderByComparator, true);

			array[1] = planEnrollment;

			array[2] = getByGroupId_PrevAndNext(
				session, planEnrollment, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PlanEnrollment getByGroupId_PrevAndNext(
		Session session, PlanEnrollment planEnrollment, long groupId,
		OrderByComparator<PlanEnrollment> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						planEnrollment)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PlanEnrollment> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the Plan Enrollments that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching Plan Enrollments that the user has permission to view
	 */
	@Override
	public List<PlanEnrollment> filterFindByGroupId(long groupId) {
		return filterFindByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Plan Enrollments that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @return the range of matching Plan Enrollments that the user has permission to view
	 */
	@Override
	public List<PlanEnrollment> filterFindByGroupId(
		long groupId, int start, int end) {

		return filterFindByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Plan Enrollments that the user has permission to view
	 */
	@Override
	public List<PlanEnrollment> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByGroupId(groupId, start, end, orderByComparator);
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				3 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(4);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PLANENROLLMENT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PLANENROLLMENT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PLANENROLLMENT_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			if (getDB().isSupportsInlineDistinct()) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator, true);
			}
			else {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_TABLE, orderByComparator, true);
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(PlanEnrollmentModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PlanEnrollmentModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PlanEnrollment.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, PlanEnrollmentImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, PlanEnrollmentImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			return (List<PlanEnrollment>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * Returns the Plan Enrollments before and after the current Plan Enrollment in the ordered set of Plan Enrollments that the user has permission to view where groupId = &#63;.
	 *
	 * @param planEnrollmentId the primary key of the current Plan Enrollment
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a Plan Enrollment with the primary key could not be found
	 */
	@Override
	public PlanEnrollment[] filterFindByGroupId_PrevAndNext(
			long planEnrollmentId, long groupId,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByGroupId_PrevAndNext(
				planEnrollmentId, groupId, orderByComparator);
		}

		PlanEnrollment planEnrollment = findByPrimaryKey(planEnrollmentId);

		Session session = null;

		try {
			session = openSession();

			PlanEnrollment[] array = new PlanEnrollmentImpl[3];

			array[0] = filterGetByGroupId_PrevAndNext(
				session, planEnrollment, groupId, orderByComparator, true);

			array[1] = planEnrollment;

			array[2] = filterGetByGroupId_PrevAndNext(
				session, planEnrollment, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PlanEnrollment filterGetByGroupId_PrevAndNext(
		Session session, PlanEnrollment planEnrollment, long groupId,
		OrderByComparator<PlanEnrollment> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PLANENROLLMENT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PLANENROLLMENT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PLANENROLLMENT_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByConditionFields[i],
							true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByConditionFields[i],
							true));
				}

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByFields[i], true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByFields[i], true));
				}

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(PlanEnrollmentModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PlanEnrollmentModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PlanEnrollment.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PlanEnrollmentImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PlanEnrollmentImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						planEnrollment)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PlanEnrollment> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Plan Enrollments where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		for (PlanEnrollment planEnrollment :
				findByGroupId(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(planEnrollment);
		}
	}

	/**
	 * Returns the number of Plan Enrollments where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching Plan Enrollments
	 */
	@Override
	public int countByGroupId(long groupId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			FinderPath finderPath = _finderPathCountByGroupId;

			Object[] finderArgs = new Object[] {groupId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_PLANENROLLMENT_WHERE);

				sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	/**
	 * Returns the number of Plan Enrollments that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching Plan Enrollments that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByGroupId(groupId);
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_PLANENROLLMENT_WHERE);

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PlanEnrollment.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			Long count = (Long)sqlQuery.uniqueResult();

			return count.intValue();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private static final String _FINDER_COLUMN_GROUPID_GROUPID_2 =
		"planEnrollment.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByCompanyId;
	private FinderPath _finderPathWithoutPaginationFindByCompanyId;
	private FinderPath _finderPathCountByCompanyId;

	/**
	 * Returns all the Plan Enrollments where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Plan Enrollments where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @return the range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByCompanyId;
					finderArgs = new Object[] {companyId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByCompanyId;
				finderArgs = new Object[] {
					companyId, start, end, orderByComparator
				};
			}

			List<PlanEnrollment> list = null;

			if (useFinderCache) {
				list = (List<PlanEnrollment>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (PlanEnrollment planEnrollment : list) {
						if (companyId != planEnrollment.getCompanyId()) {
							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						3 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(3);
				}

				sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

				sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					list = (List<PlanEnrollment>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first Plan Enrollment in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByCompanyId_First(
			long companyId, OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByCompanyId_First(
			companyId, orderByComparator);

		if (planEnrollment != null) {
			return planEnrollment;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchPlanEnrollmentException(sb.toString());
	}

	/**
	 * Returns the first Plan Enrollment in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByCompanyId_First(
		long companyId, OrderByComparator<PlanEnrollment> orderByComparator) {

		List<PlanEnrollment> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Plan Enrollment in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByCompanyId_Last(
			long companyId, OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByCompanyId_Last(
			companyId, orderByComparator);

		if (planEnrollment != null) {
			return planEnrollment;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchPlanEnrollmentException(sb.toString());
	}

	/**
	 * Returns the last Plan Enrollment in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByCompanyId_Last(
		long companyId, OrderByComparator<PlanEnrollment> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<PlanEnrollment> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Plan Enrollments before and after the current Plan Enrollment in the ordered set where companyId = &#63;.
	 *
	 * @param planEnrollmentId the primary key of the current Plan Enrollment
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a Plan Enrollment with the primary key could not be found
	 */
	@Override
	public PlanEnrollment[] findByCompanyId_PrevAndNext(
			long planEnrollmentId, long companyId,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = findByPrimaryKey(planEnrollmentId);

		Session session = null;

		try {
			session = openSession();

			PlanEnrollment[] array = new PlanEnrollmentImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, planEnrollment, companyId, orderByComparator, true);

			array[1] = planEnrollment;

			array[2] = getByCompanyId_PrevAndNext(
				session, planEnrollment, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PlanEnrollment getByCompanyId_PrevAndNext(
		Session session, PlanEnrollment planEnrollment, long companyId,
		OrderByComparator<PlanEnrollment> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

		sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						planEnrollment)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PlanEnrollment> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Plan Enrollments where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (PlanEnrollment planEnrollment :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(planEnrollment);
		}
	}

	/**
	 * Returns the number of Plan Enrollments where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching Plan Enrollments
	 */
	@Override
	public int countByCompanyId(long companyId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			FinderPath finderPath = _finderPathCountByCompanyId;

			Object[] finderArgs = new Object[] {companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_PLANENROLLMENT_WHERE);

				sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	private static final String _FINDER_COLUMN_COMPANYID_COMPANYID_2 =
		"planEnrollment.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByG_S;
	private FinderPath _finderPathWithoutPaginationFindByG_S;
	private FinderPath _finderPathCountByG_S;

	/**
	 * Returns all the Plan Enrollments where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByG_S(long groupId, int status) {
		return findByG_S(
			groupId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Plan Enrollments where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @return the range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByG_S(
		long groupId, int status, int start, int end) {

		return findByG_S(groupId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		return findByG_S(groupId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_S;
					finderArgs = new Object[] {groupId, status};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_S;
				finderArgs = new Object[] {
					groupId, status, start, end, orderByComparator
				};
			}

			List<PlanEnrollment> list = null;

			if (useFinderCache) {
				list = (List<PlanEnrollment>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (PlanEnrollment planEnrollment : list) {
						if ((groupId != planEnrollment.getGroupId()) ||
							(status != planEnrollment.getStatus())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						4 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(4);
				}

				sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

				sb.append(_FINDER_COLUMN_G_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(status);

					list = (List<PlanEnrollment>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first Plan Enrollment in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByG_S_First(
			long groupId, int status,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByG_S_First(
			groupId, status, orderByComparator);

		if (planEnrollment != null) {
			return planEnrollment;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPlanEnrollmentException(sb.toString());
	}

	/**
	 * Returns the first Plan Enrollment in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByG_S_First(
		long groupId, int status,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		List<PlanEnrollment> list = findByG_S(
			groupId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Plan Enrollment in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByG_S_Last(
			long groupId, int status,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByG_S_Last(
			groupId, status, orderByComparator);

		if (planEnrollment != null) {
			return planEnrollment;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPlanEnrollmentException(sb.toString());
	}

	/**
	 * Returns the last Plan Enrollment in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByG_S_Last(
		long groupId, int status,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		int count = countByG_S(groupId, status);

		if (count == 0) {
			return null;
		}

		List<PlanEnrollment> list = findByG_S(
			groupId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Plan Enrollments before and after the current Plan Enrollment in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param planEnrollmentId the primary key of the current Plan Enrollment
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a Plan Enrollment with the primary key could not be found
	 */
	@Override
	public PlanEnrollment[] findByG_S_PrevAndNext(
			long planEnrollmentId, long groupId, int status,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = findByPrimaryKey(planEnrollmentId);

		Session session = null;

		try {
			session = openSession();

			PlanEnrollment[] array = new PlanEnrollmentImpl[3];

			array[0] = getByG_S_PrevAndNext(
				session, planEnrollment, groupId, status, orderByComparator,
				true);

			array[1] = planEnrollment;

			array[2] = getByG_S_PrevAndNext(
				session, planEnrollment, groupId, status, orderByComparator,
				false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PlanEnrollment getByG_S_PrevAndNext(
		Session session, PlanEnrollment planEnrollment, long groupId,
		int status, OrderByComparator<PlanEnrollment> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

		sb.append(_FINDER_COLUMN_G_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_S_STATUS_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						planEnrollment)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PlanEnrollment> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the Plan Enrollments that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching Plan Enrollments that the user has permission to view
	 */
	@Override
	public List<PlanEnrollment> filterFindByG_S(long groupId, int status) {
		return filterFindByG_S(
			groupId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Plan Enrollments that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @return the range of matching Plan Enrollments that the user has permission to view
	 */
	@Override
	public List<PlanEnrollment> filterFindByG_S(
		long groupId, int status, int start, int end) {

		return filterFindByG_S(groupId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments that the user has permissions to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Plan Enrollments that the user has permission to view
	 */
	@Override
	public List<PlanEnrollment> filterFindByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_S(groupId, status, start, end, orderByComparator);
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PLANENROLLMENT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PLANENROLLMENT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PLANENROLLMENT_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			if (getDB().isSupportsInlineDistinct()) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator, true);
			}
			else {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_TABLE, orderByComparator, true);
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(PlanEnrollmentModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PlanEnrollmentModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PlanEnrollment.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, PlanEnrollmentImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, PlanEnrollmentImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(status);

			return (List<PlanEnrollment>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * Returns the Plan Enrollments before and after the current Plan Enrollment in the ordered set of Plan Enrollments that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param planEnrollmentId the primary key of the current Plan Enrollment
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a Plan Enrollment with the primary key could not be found
	 */
	@Override
	public PlanEnrollment[] filterFindByG_S_PrevAndNext(
			long planEnrollmentId, long groupId, int status,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_S_PrevAndNext(
				planEnrollmentId, groupId, status, orderByComparator);
		}

		PlanEnrollment planEnrollment = findByPrimaryKey(planEnrollmentId);

		Session session = null;

		try {
			session = openSession();

			PlanEnrollment[] array = new PlanEnrollmentImpl[3];

			array[0] = filterGetByG_S_PrevAndNext(
				session, planEnrollment, groupId, status, orderByComparator,
				true);

			array[1] = planEnrollment;

			array[2] = filterGetByG_S_PrevAndNext(
				session, planEnrollment, groupId, status, orderByComparator,
				false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PlanEnrollment filterGetByG_S_PrevAndNext(
		Session session, PlanEnrollment planEnrollment, long groupId,
		int status, OrderByComparator<PlanEnrollment> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PLANENROLLMENT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PLANENROLLMENT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PLANENROLLMENT_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByConditionFields[i],
							true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByConditionFields[i],
							true));
				}

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByFields[i], true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByFields[i], true));
				}

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(PlanEnrollmentModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PlanEnrollmentModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PlanEnrollment.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PlanEnrollmentImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PlanEnrollmentImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						planEnrollment)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PlanEnrollment> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Plan Enrollments where groupId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 */
	@Override
	public void removeByG_S(long groupId, int status) {
		for (PlanEnrollment planEnrollment :
				findByG_S(
					groupId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(planEnrollment);
		}
	}

	/**
	 * Returns the number of Plan Enrollments where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching Plan Enrollments
	 */
	@Override
	public int countByG_S(long groupId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			FinderPath finderPath = _finderPathCountByG_S;

			Object[] finderArgs = new Object[] {groupId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_PLANENROLLMENT_WHERE);

				sb.append(_FINDER_COLUMN_G_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(status);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	/**
	 * Returns the number of Plan Enrollments that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching Plan Enrollments that the user has permission to view
	 */
	@Override
	public int filterCountByG_S(long groupId, int status) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_S(groupId, status);
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_PLANENROLLMENT_WHERE);

		sb.append(_FINDER_COLUMN_G_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_S_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PlanEnrollment.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(status);

			Long count = (Long)sqlQuery.uniqueResult();

			return count.intValue();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private static final String _FINDER_COLUMN_G_S_GROUPID_2 =
		"planEnrollment.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_S_STATUS_2 =
		"planEnrollment.status = ?";

	private FinderPath _finderPathWithPaginationFindByG_NotS;
	private FinderPath _finderPathWithPaginationCountByG_NotS;

	/**
	 * Returns all the Plan Enrollments where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByG_NotS(long groupId, int status) {
		return findByG_NotS(
			groupId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Plan Enrollments where groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @return the range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByG_NotS(
		long groupId, int status, int start, int end) {

		return findByG_NotS(groupId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByG_NotS(
		long groupId, int status, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		return findByG_NotS(
			groupId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByG_NotS(
		long groupId, int status, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByG_NotS;
			finderArgs = new Object[] {
				groupId, status, start, end, orderByComparator
			};

			List<PlanEnrollment> list = null;

			if (useFinderCache) {
				list = (List<PlanEnrollment>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (PlanEnrollment planEnrollment : list) {
						if ((groupId != planEnrollment.getGroupId()) ||
							(status == planEnrollment.getStatus())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						4 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(4);
				}

				sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

				sb.append(_FINDER_COLUMN_G_NOTS_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_NOTS_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(status);

					list = (List<PlanEnrollment>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first Plan Enrollment in the ordered set where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByG_NotS_First(
			long groupId, int status,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByG_NotS_First(
			groupId, status, orderByComparator);

		if (planEnrollment != null) {
			return planEnrollment;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPlanEnrollmentException(sb.toString());
	}

	/**
	 * Returns the first Plan Enrollment in the ordered set where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByG_NotS_First(
		long groupId, int status,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		List<PlanEnrollment> list = findByG_NotS(
			groupId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Plan Enrollment in the ordered set where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByG_NotS_Last(
			long groupId, int status,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByG_NotS_Last(
			groupId, status, orderByComparator);

		if (planEnrollment != null) {
			return planEnrollment;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPlanEnrollmentException(sb.toString());
	}

	/**
	 * Returns the last Plan Enrollment in the ordered set where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByG_NotS_Last(
		long groupId, int status,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		int count = countByG_NotS(groupId, status);

		if (count == 0) {
			return null;
		}

		List<PlanEnrollment> list = findByG_NotS(
			groupId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Plan Enrollments before and after the current Plan Enrollment in the ordered set where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param planEnrollmentId the primary key of the current Plan Enrollment
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a Plan Enrollment with the primary key could not be found
	 */
	@Override
	public PlanEnrollment[] findByG_NotS_PrevAndNext(
			long planEnrollmentId, long groupId, int status,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = findByPrimaryKey(planEnrollmentId);

		Session session = null;

		try {
			session = openSession();

			PlanEnrollment[] array = new PlanEnrollmentImpl[3];

			array[0] = getByG_NotS_PrevAndNext(
				session, planEnrollment, groupId, status, orderByComparator,
				true);

			array[1] = planEnrollment;

			array[2] = getByG_NotS_PrevAndNext(
				session, planEnrollment, groupId, status, orderByComparator,
				false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PlanEnrollment getByG_NotS_PrevAndNext(
		Session session, PlanEnrollment planEnrollment, long groupId,
		int status, OrderByComparator<PlanEnrollment> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

		sb.append(_FINDER_COLUMN_G_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_NOTS_STATUS_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						planEnrollment)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PlanEnrollment> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the Plan Enrollments that the user has permission to view where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching Plan Enrollments that the user has permission to view
	 */
	@Override
	public List<PlanEnrollment> filterFindByG_NotS(long groupId, int status) {
		return filterFindByG_NotS(
			groupId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Plan Enrollments that the user has permission to view where groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @return the range of matching Plan Enrollments that the user has permission to view
	 */
	@Override
	public List<PlanEnrollment> filterFindByG_NotS(
		long groupId, int status, int start, int end) {

		return filterFindByG_NotS(groupId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments that the user has permissions to view where groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Plan Enrollments that the user has permission to view
	 */
	@Override
	public List<PlanEnrollment> filterFindByG_NotS(
		long groupId, int status, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_NotS(groupId, status, start, end, orderByComparator);
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PLANENROLLMENT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PLANENROLLMENT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_NOTS_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PLANENROLLMENT_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			if (getDB().isSupportsInlineDistinct()) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator, true);
			}
			else {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_TABLE, orderByComparator, true);
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(PlanEnrollmentModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PlanEnrollmentModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PlanEnrollment.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, PlanEnrollmentImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, PlanEnrollmentImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(status);

			return (List<PlanEnrollment>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * Returns the Plan Enrollments before and after the current Plan Enrollment in the ordered set of Plan Enrollments that the user has permission to view where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param planEnrollmentId the primary key of the current Plan Enrollment
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a Plan Enrollment with the primary key could not be found
	 */
	@Override
	public PlanEnrollment[] filterFindByG_NotS_PrevAndNext(
			long planEnrollmentId, long groupId, int status,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_NotS_PrevAndNext(
				planEnrollmentId, groupId, status, orderByComparator);
		}

		PlanEnrollment planEnrollment = findByPrimaryKey(planEnrollmentId);

		Session session = null;

		try {
			session = openSession();

			PlanEnrollment[] array = new PlanEnrollmentImpl[3];

			array[0] = filterGetByG_NotS_PrevAndNext(
				session, planEnrollment, groupId, status, orderByComparator,
				true);

			array[1] = planEnrollment;

			array[2] = filterGetByG_NotS_PrevAndNext(
				session, planEnrollment, groupId, status, orderByComparator,
				false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PlanEnrollment filterGetByG_NotS_PrevAndNext(
		Session session, PlanEnrollment planEnrollment, long groupId,
		int status, OrderByComparator<PlanEnrollment> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PLANENROLLMENT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PLANENROLLMENT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_NOTS_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PLANENROLLMENT_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByConditionFields[i],
							true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByConditionFields[i],
							true));
				}

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByFields[i], true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByFields[i], true));
				}

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(PlanEnrollmentModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PlanEnrollmentModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PlanEnrollment.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PlanEnrollmentImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PlanEnrollmentImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						planEnrollment)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PlanEnrollment> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Plan Enrollments where groupId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 */
	@Override
	public void removeByG_NotS(long groupId, int status) {
		for (PlanEnrollment planEnrollment :
				findByG_NotS(
					groupId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(planEnrollment);
		}
	}

	/**
	 * Returns the number of Plan Enrollments where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching Plan Enrollments
	 */
	@Override
	public int countByG_NotS(long groupId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByG_NotS;

			Object[] finderArgs = new Object[] {groupId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_PLANENROLLMENT_WHERE);

				sb.append(_FINDER_COLUMN_G_NOTS_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_NOTS_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(status);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	/**
	 * Returns the number of Plan Enrollments that the user has permission to view where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching Plan Enrollments that the user has permission to view
	 */
	@Override
	public int filterCountByG_NotS(long groupId, int status) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_NotS(groupId, status);
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_PLANENROLLMENT_WHERE);

		sb.append(_FINDER_COLUMN_G_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_NOTS_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PlanEnrollment.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(status);

			Long count = (Long)sqlQuery.uniqueResult();

			return count.intValue();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private static final String _FINDER_COLUMN_G_NOTS_GROUPID_2 =
		"planEnrollment.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_NOTS_STATUS_2 =
		"planEnrollment.status != ?";

	private FinderPath _finderPathWithPaginationFindByIP_S;
	private FinderPath _finderPathWithoutPaginationFindByIP_S;
	private FinderPath _finderPathCountByIP_S;

	/**
	 * Returns all the Plan Enrollments where insurancePlanId = &#63; and status = &#63;.
	 *
	 * @param insurancePlanId the insurance plan ID
	 * @param status the status
	 * @return the matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByIP_S(long insurancePlanId, int status) {
		return findByIP_S(
			insurancePlanId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the Plan Enrollments where insurancePlanId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param insurancePlanId the insurance plan ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @return the range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByIP_S(
		long insurancePlanId, int status, int start, int end) {

		return findByIP_S(insurancePlanId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where insurancePlanId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param insurancePlanId the insurance plan ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByIP_S(
		long insurancePlanId, int status, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		return findByIP_S(
			insurancePlanId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where insurancePlanId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param insurancePlanId the insurance plan ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByIP_S(
		long insurancePlanId, int status, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByIP_S;
					finderArgs = new Object[] {insurancePlanId, status};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByIP_S;
				finderArgs = new Object[] {
					insurancePlanId, status, start, end, orderByComparator
				};
			}

			List<PlanEnrollment> list = null;

			if (useFinderCache) {
				list = (List<PlanEnrollment>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (PlanEnrollment planEnrollment : list) {
						if ((insurancePlanId !=
								planEnrollment.getInsurancePlanId()) ||
							(status != planEnrollment.getStatus())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						4 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(4);
				}

				sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

				sb.append(_FINDER_COLUMN_IP_S_INSURANCEPLANID_2);

				sb.append(_FINDER_COLUMN_IP_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(insurancePlanId);

					queryPos.add(status);

					list = (List<PlanEnrollment>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first Plan Enrollment in the ordered set where insurancePlanId = &#63; and status = &#63;.
	 *
	 * @param insurancePlanId the insurance plan ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByIP_S_First(
			long insurancePlanId, int status,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByIP_S_First(
			insurancePlanId, status, orderByComparator);

		if (planEnrollment != null) {
			return planEnrollment;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("insurancePlanId=");
		sb.append(insurancePlanId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPlanEnrollmentException(sb.toString());
	}

	/**
	 * Returns the first Plan Enrollment in the ordered set where insurancePlanId = &#63; and status = &#63;.
	 *
	 * @param insurancePlanId the insurance plan ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByIP_S_First(
		long insurancePlanId, int status,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		List<PlanEnrollment> list = findByIP_S(
			insurancePlanId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Plan Enrollment in the ordered set where insurancePlanId = &#63; and status = &#63;.
	 *
	 * @param insurancePlanId the insurance plan ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByIP_S_Last(
			long insurancePlanId, int status,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByIP_S_Last(
			insurancePlanId, status, orderByComparator);

		if (planEnrollment != null) {
			return planEnrollment;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("insurancePlanId=");
		sb.append(insurancePlanId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPlanEnrollmentException(sb.toString());
	}

	/**
	 * Returns the last Plan Enrollment in the ordered set where insurancePlanId = &#63; and status = &#63;.
	 *
	 * @param insurancePlanId the insurance plan ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByIP_S_Last(
		long insurancePlanId, int status,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		int count = countByIP_S(insurancePlanId, status);

		if (count == 0) {
			return null;
		}

		List<PlanEnrollment> list = findByIP_S(
			insurancePlanId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Plan Enrollments before and after the current Plan Enrollment in the ordered set where insurancePlanId = &#63; and status = &#63;.
	 *
	 * @param planEnrollmentId the primary key of the current Plan Enrollment
	 * @param insurancePlanId the insurance plan ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a Plan Enrollment with the primary key could not be found
	 */
	@Override
	public PlanEnrollment[] findByIP_S_PrevAndNext(
			long planEnrollmentId, long insurancePlanId, int status,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = findByPrimaryKey(planEnrollmentId);

		Session session = null;

		try {
			session = openSession();

			PlanEnrollment[] array = new PlanEnrollmentImpl[3];

			array[0] = getByIP_S_PrevAndNext(
				session, planEnrollment, insurancePlanId, status,
				orderByComparator, true);

			array[1] = planEnrollment;

			array[2] = getByIP_S_PrevAndNext(
				session, planEnrollment, insurancePlanId, status,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PlanEnrollment getByIP_S_PrevAndNext(
		Session session, PlanEnrollment planEnrollment, long insurancePlanId,
		int status, OrderByComparator<PlanEnrollment> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

		sb.append(_FINDER_COLUMN_IP_S_INSURANCEPLANID_2);

		sb.append(_FINDER_COLUMN_IP_S_STATUS_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(insurancePlanId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						planEnrollment)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PlanEnrollment> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Plan Enrollments where insurancePlanId = &#63; and status = &#63; from the database.
	 *
	 * @param insurancePlanId the insurance plan ID
	 * @param status the status
	 */
	@Override
	public void removeByIP_S(long insurancePlanId, int status) {
		for (PlanEnrollment planEnrollment :
				findByIP_S(
					insurancePlanId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(planEnrollment);
		}
	}

	/**
	 * Returns the number of Plan Enrollments where insurancePlanId = &#63; and status = &#63;.
	 *
	 * @param insurancePlanId the insurance plan ID
	 * @param status the status
	 * @return the number of matching Plan Enrollments
	 */
	@Override
	public int countByIP_S(long insurancePlanId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			FinderPath finderPath = _finderPathCountByIP_S;

			Object[] finderArgs = new Object[] {insurancePlanId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_PLANENROLLMENT_WHERE);

				sb.append(_FINDER_COLUMN_IP_S_INSURANCEPLANID_2);

				sb.append(_FINDER_COLUMN_IP_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(insurancePlanId);

					queryPos.add(status);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	private static final String _FINDER_COLUMN_IP_S_INSURANCEPLANID_2 =
		"planEnrollment.insurancePlanId = ? AND ";

	private static final String _FINDER_COLUMN_IP_S_STATUS_2 =
		"planEnrollment.status = ?";

	private FinderPath _finderPathWithPaginationFindByIP_NotS;
	private FinderPath _finderPathWithPaginationCountByIP_NotS;

	/**
	 * Returns all the Plan Enrollments where insurancePlanId = &#63; and status &ne; &#63;.
	 *
	 * @param insurancePlanId the insurance plan ID
	 * @param status the status
	 * @return the matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByIP_NotS(
		long insurancePlanId, int status) {

		return findByIP_NotS(
			insurancePlanId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the Plan Enrollments where insurancePlanId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param insurancePlanId the insurance plan ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @return the range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByIP_NotS(
		long insurancePlanId, int status, int start, int end) {

		return findByIP_NotS(insurancePlanId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where insurancePlanId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param insurancePlanId the insurance plan ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByIP_NotS(
		long insurancePlanId, int status, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		return findByIP_NotS(
			insurancePlanId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where insurancePlanId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param insurancePlanId the insurance plan ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByIP_NotS(
		long insurancePlanId, int status, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByIP_NotS;
			finderArgs = new Object[] {
				insurancePlanId, status, start, end, orderByComparator
			};

			List<PlanEnrollment> list = null;

			if (useFinderCache) {
				list = (List<PlanEnrollment>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (PlanEnrollment planEnrollment : list) {
						if ((insurancePlanId !=
								planEnrollment.getInsurancePlanId()) ||
							(status == planEnrollment.getStatus())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						4 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(4);
				}

				sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

				sb.append(_FINDER_COLUMN_IP_NOTS_INSURANCEPLANID_2);

				sb.append(_FINDER_COLUMN_IP_NOTS_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(insurancePlanId);

					queryPos.add(status);

					list = (List<PlanEnrollment>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first Plan Enrollment in the ordered set where insurancePlanId = &#63; and status &ne; &#63;.
	 *
	 * @param insurancePlanId the insurance plan ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByIP_NotS_First(
			long insurancePlanId, int status,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByIP_NotS_First(
			insurancePlanId, status, orderByComparator);

		if (planEnrollment != null) {
			return planEnrollment;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("insurancePlanId=");
		sb.append(insurancePlanId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPlanEnrollmentException(sb.toString());
	}

	/**
	 * Returns the first Plan Enrollment in the ordered set where insurancePlanId = &#63; and status &ne; &#63;.
	 *
	 * @param insurancePlanId the insurance plan ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByIP_NotS_First(
		long insurancePlanId, int status,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		List<PlanEnrollment> list = findByIP_NotS(
			insurancePlanId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Plan Enrollment in the ordered set where insurancePlanId = &#63; and status &ne; &#63;.
	 *
	 * @param insurancePlanId the insurance plan ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByIP_NotS_Last(
			long insurancePlanId, int status,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByIP_NotS_Last(
			insurancePlanId, status, orderByComparator);

		if (planEnrollment != null) {
			return planEnrollment;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("insurancePlanId=");
		sb.append(insurancePlanId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPlanEnrollmentException(sb.toString());
	}

	/**
	 * Returns the last Plan Enrollment in the ordered set where insurancePlanId = &#63; and status &ne; &#63;.
	 *
	 * @param insurancePlanId the insurance plan ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByIP_NotS_Last(
		long insurancePlanId, int status,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		int count = countByIP_NotS(insurancePlanId, status);

		if (count == 0) {
			return null;
		}

		List<PlanEnrollment> list = findByIP_NotS(
			insurancePlanId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Plan Enrollments before and after the current Plan Enrollment in the ordered set where insurancePlanId = &#63; and status &ne; &#63;.
	 *
	 * @param planEnrollmentId the primary key of the current Plan Enrollment
	 * @param insurancePlanId the insurance plan ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a Plan Enrollment with the primary key could not be found
	 */
	@Override
	public PlanEnrollment[] findByIP_NotS_PrevAndNext(
			long planEnrollmentId, long insurancePlanId, int status,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = findByPrimaryKey(planEnrollmentId);

		Session session = null;

		try {
			session = openSession();

			PlanEnrollment[] array = new PlanEnrollmentImpl[3];

			array[0] = getByIP_NotS_PrevAndNext(
				session, planEnrollment, insurancePlanId, status,
				orderByComparator, true);

			array[1] = planEnrollment;

			array[2] = getByIP_NotS_PrevAndNext(
				session, planEnrollment, insurancePlanId, status,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PlanEnrollment getByIP_NotS_PrevAndNext(
		Session session, PlanEnrollment planEnrollment, long insurancePlanId,
		int status, OrderByComparator<PlanEnrollment> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

		sb.append(_FINDER_COLUMN_IP_NOTS_INSURANCEPLANID_2);

		sb.append(_FINDER_COLUMN_IP_NOTS_STATUS_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(insurancePlanId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						planEnrollment)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PlanEnrollment> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Plan Enrollments where insurancePlanId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param insurancePlanId the insurance plan ID
	 * @param status the status
	 */
	@Override
	public void removeByIP_NotS(long insurancePlanId, int status) {
		for (PlanEnrollment planEnrollment :
				findByIP_NotS(
					insurancePlanId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(planEnrollment);
		}
	}

	/**
	 * Returns the number of Plan Enrollments where insurancePlanId = &#63; and status &ne; &#63;.
	 *
	 * @param insurancePlanId the insurance plan ID
	 * @param status the status
	 * @return the number of matching Plan Enrollments
	 */
	@Override
	public int countByIP_NotS(long insurancePlanId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByIP_NotS;

			Object[] finderArgs = new Object[] {insurancePlanId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_PLANENROLLMENT_WHERE);

				sb.append(_FINDER_COLUMN_IP_NOTS_INSURANCEPLANID_2);

				sb.append(_FINDER_COLUMN_IP_NOTS_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(insurancePlanId);

					queryPos.add(status);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	private static final String _FINDER_COLUMN_IP_NOTS_INSURANCEPLANID_2 =
		"planEnrollment.insurancePlanId = ? AND ";

	private static final String _FINDER_COLUMN_IP_NOTS_STATUS_2 =
		"planEnrollment.status != ?";

	private FinderPath _finderPathWithPaginationFindByG_IP_M_S;
	private FinderPath _finderPathWithoutPaginationFindByG_IP_M_S;
	private FinderPath _finderPathCountByG_IP_M_S;

	/**
	 * Returns all the Plan Enrollments where groupId = &#63; and insurancePlanId = &#63; and memberUserId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param insurancePlanId the insurance plan ID
	 * @param memberUserId the member user ID
	 * @param status the status
	 * @return the matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByG_IP_M_S(
		long groupId, long insurancePlanId, long memberUserId, int status) {

		return findByG_IP_M_S(
			groupId, insurancePlanId, memberUserId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Plan Enrollments where groupId = &#63; and insurancePlanId = &#63; and memberUserId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param insurancePlanId the insurance plan ID
	 * @param memberUserId the member user ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @return the range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByG_IP_M_S(
		long groupId, long insurancePlanId, long memberUserId, int status,
		int start, int end) {

		return findByG_IP_M_S(
			groupId, insurancePlanId, memberUserId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where groupId = &#63; and insurancePlanId = &#63; and memberUserId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param insurancePlanId the insurance plan ID
	 * @param memberUserId the member user ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByG_IP_M_S(
		long groupId, long insurancePlanId, long memberUserId, int status,
		int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		return findByG_IP_M_S(
			groupId, insurancePlanId, memberUserId, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where groupId = &#63; and insurancePlanId = &#63; and memberUserId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param insurancePlanId the insurance plan ID
	 * @param memberUserId the member user ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByG_IP_M_S(
		long groupId, long insurancePlanId, long memberUserId, int status,
		int start, int end, OrderByComparator<PlanEnrollment> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_IP_M_S;
					finderArgs = new Object[] {
						groupId, insurancePlanId, memberUserId, status
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_IP_M_S;
				finderArgs = new Object[] {
					groupId, insurancePlanId, memberUserId, status, start, end,
					orderByComparator
				};
			}

			List<PlanEnrollment> list = null;

			if (useFinderCache) {
				list = (List<PlanEnrollment>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (PlanEnrollment planEnrollment : list) {
						if ((groupId != planEnrollment.getGroupId()) ||
							(insurancePlanId !=
								planEnrollment.getInsurancePlanId()) ||
							(memberUserId !=
								planEnrollment.getMemberUserId()) ||
							(status != planEnrollment.getStatus())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						6 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(6);
				}

				sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

				sb.append(_FINDER_COLUMN_G_IP_M_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_IP_M_S_INSURANCEPLANID_2);

				sb.append(_FINDER_COLUMN_G_IP_M_S_MEMBERUSERID_2);

				sb.append(_FINDER_COLUMN_G_IP_M_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(insurancePlanId);

					queryPos.add(memberUserId);

					queryPos.add(status);

					list = (List<PlanEnrollment>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first Plan Enrollment in the ordered set where groupId = &#63; and insurancePlanId = &#63; and memberUserId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param insurancePlanId the insurance plan ID
	 * @param memberUserId the member user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByG_IP_M_S_First(
			long groupId, long insurancePlanId, long memberUserId, int status,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByG_IP_M_S_First(
			groupId, insurancePlanId, memberUserId, status, orderByComparator);

		if (planEnrollment != null) {
			return planEnrollment;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", insurancePlanId=");
		sb.append(insurancePlanId);

		sb.append(", memberUserId=");
		sb.append(memberUserId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPlanEnrollmentException(sb.toString());
	}

	/**
	 * Returns the first Plan Enrollment in the ordered set where groupId = &#63; and insurancePlanId = &#63; and memberUserId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param insurancePlanId the insurance plan ID
	 * @param memberUserId the member user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByG_IP_M_S_First(
		long groupId, long insurancePlanId, long memberUserId, int status,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		List<PlanEnrollment> list = findByG_IP_M_S(
			groupId, insurancePlanId, memberUserId, status, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Plan Enrollment in the ordered set where groupId = &#63; and insurancePlanId = &#63; and memberUserId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param insurancePlanId the insurance plan ID
	 * @param memberUserId the member user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByG_IP_M_S_Last(
			long groupId, long insurancePlanId, long memberUserId, int status,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByG_IP_M_S_Last(
			groupId, insurancePlanId, memberUserId, status, orderByComparator);

		if (planEnrollment != null) {
			return planEnrollment;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", insurancePlanId=");
		sb.append(insurancePlanId);

		sb.append(", memberUserId=");
		sb.append(memberUserId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPlanEnrollmentException(sb.toString());
	}

	/**
	 * Returns the last Plan Enrollment in the ordered set where groupId = &#63; and insurancePlanId = &#63; and memberUserId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param insurancePlanId the insurance plan ID
	 * @param memberUserId the member user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByG_IP_M_S_Last(
		long groupId, long insurancePlanId, long memberUserId, int status,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		int count = countByG_IP_M_S(
			groupId, insurancePlanId, memberUserId, status);

		if (count == 0) {
			return null;
		}

		List<PlanEnrollment> list = findByG_IP_M_S(
			groupId, insurancePlanId, memberUserId, status, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Plan Enrollments before and after the current Plan Enrollment in the ordered set where groupId = &#63; and insurancePlanId = &#63; and memberUserId = &#63; and status = &#63;.
	 *
	 * @param planEnrollmentId the primary key of the current Plan Enrollment
	 * @param groupId the group ID
	 * @param insurancePlanId the insurance plan ID
	 * @param memberUserId the member user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a Plan Enrollment with the primary key could not be found
	 */
	@Override
	public PlanEnrollment[] findByG_IP_M_S_PrevAndNext(
			long planEnrollmentId, long groupId, long insurancePlanId,
			long memberUserId, int status,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = findByPrimaryKey(planEnrollmentId);

		Session session = null;

		try {
			session = openSession();

			PlanEnrollment[] array = new PlanEnrollmentImpl[3];

			array[0] = getByG_IP_M_S_PrevAndNext(
				session, planEnrollment, groupId, insurancePlanId, memberUserId,
				status, orderByComparator, true);

			array[1] = planEnrollment;

			array[2] = getByG_IP_M_S_PrevAndNext(
				session, planEnrollment, groupId, insurancePlanId, memberUserId,
				status, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PlanEnrollment getByG_IP_M_S_PrevAndNext(
		Session session, PlanEnrollment planEnrollment, long groupId,
		long insurancePlanId, long memberUserId, int status,
		OrderByComparator<PlanEnrollment> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

		sb.append(_FINDER_COLUMN_G_IP_M_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_IP_M_S_INSURANCEPLANID_2);

		sb.append(_FINDER_COLUMN_G_IP_M_S_MEMBERUSERID_2);

		sb.append(_FINDER_COLUMN_G_IP_M_S_STATUS_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(insurancePlanId);

		queryPos.add(memberUserId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						planEnrollment)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PlanEnrollment> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the Plan Enrollments that the user has permission to view where groupId = &#63; and insurancePlanId = &#63; and memberUserId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param insurancePlanId the insurance plan ID
	 * @param memberUserId the member user ID
	 * @param status the status
	 * @return the matching Plan Enrollments that the user has permission to view
	 */
	@Override
	public List<PlanEnrollment> filterFindByG_IP_M_S(
		long groupId, long insurancePlanId, long memberUserId, int status) {

		return filterFindByG_IP_M_S(
			groupId, insurancePlanId, memberUserId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Plan Enrollments that the user has permission to view where groupId = &#63; and insurancePlanId = &#63; and memberUserId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param insurancePlanId the insurance plan ID
	 * @param memberUserId the member user ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @return the range of matching Plan Enrollments that the user has permission to view
	 */
	@Override
	public List<PlanEnrollment> filterFindByG_IP_M_S(
		long groupId, long insurancePlanId, long memberUserId, int status,
		int start, int end) {

		return filterFindByG_IP_M_S(
			groupId, insurancePlanId, memberUserId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments that the user has permissions to view where groupId = &#63; and insurancePlanId = &#63; and memberUserId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param insurancePlanId the insurance plan ID
	 * @param memberUserId the member user ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Plan Enrollments that the user has permission to view
	 */
	@Override
	public List<PlanEnrollment> filterFindByG_IP_M_S(
		long groupId, long insurancePlanId, long memberUserId, int status,
		int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_IP_M_S(
				groupId, insurancePlanId, memberUserId, status, start, end,
				orderByComparator);
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(7);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PLANENROLLMENT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PLANENROLLMENT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_IP_M_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_IP_M_S_INSURANCEPLANID_2);

		sb.append(_FINDER_COLUMN_G_IP_M_S_MEMBERUSERID_2);

		sb.append(_FINDER_COLUMN_G_IP_M_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PLANENROLLMENT_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			if (getDB().isSupportsInlineDistinct()) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator, true);
			}
			else {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_TABLE, orderByComparator, true);
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(PlanEnrollmentModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PlanEnrollmentModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PlanEnrollment.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, PlanEnrollmentImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, PlanEnrollmentImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(insurancePlanId);

			queryPos.add(memberUserId);

			queryPos.add(status);

			return (List<PlanEnrollment>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * Returns the Plan Enrollments before and after the current Plan Enrollment in the ordered set of Plan Enrollments that the user has permission to view where groupId = &#63; and insurancePlanId = &#63; and memberUserId = &#63; and status = &#63;.
	 *
	 * @param planEnrollmentId the primary key of the current Plan Enrollment
	 * @param groupId the group ID
	 * @param insurancePlanId the insurance plan ID
	 * @param memberUserId the member user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a Plan Enrollment with the primary key could not be found
	 */
	@Override
	public PlanEnrollment[] filterFindByG_IP_M_S_PrevAndNext(
			long planEnrollmentId, long groupId, long insurancePlanId,
			long memberUserId, int status,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_IP_M_S_PrevAndNext(
				planEnrollmentId, groupId, insurancePlanId, memberUserId,
				status, orderByComparator);
		}

		PlanEnrollment planEnrollment = findByPrimaryKey(planEnrollmentId);

		Session session = null;

		try {
			session = openSession();

			PlanEnrollment[] array = new PlanEnrollmentImpl[3];

			array[0] = filterGetByG_IP_M_S_PrevAndNext(
				session, planEnrollment, groupId, insurancePlanId, memberUserId,
				status, orderByComparator, true);

			array[1] = planEnrollment;

			array[2] = filterGetByG_IP_M_S_PrevAndNext(
				session, planEnrollment, groupId, insurancePlanId, memberUserId,
				status, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PlanEnrollment filterGetByG_IP_M_S_PrevAndNext(
		Session session, PlanEnrollment planEnrollment, long groupId,
		long insurancePlanId, long memberUserId, int status,
		OrderByComparator<PlanEnrollment> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				8 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(7);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PLANENROLLMENT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PLANENROLLMENT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_IP_M_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_IP_M_S_INSURANCEPLANID_2);

		sb.append(_FINDER_COLUMN_G_IP_M_S_MEMBERUSERID_2);

		sb.append(_FINDER_COLUMN_G_IP_M_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PLANENROLLMENT_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByConditionFields[i],
							true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByConditionFields[i],
							true));
				}

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByFields[i], true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByFields[i], true));
				}

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(PlanEnrollmentModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PlanEnrollmentModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PlanEnrollment.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PlanEnrollmentImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PlanEnrollmentImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(insurancePlanId);

		queryPos.add(memberUserId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						planEnrollment)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PlanEnrollment> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Plan Enrollments where groupId = &#63; and insurancePlanId = &#63; and memberUserId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param insurancePlanId the insurance plan ID
	 * @param memberUserId the member user ID
	 * @param status the status
	 */
	@Override
	public void removeByG_IP_M_S(
		long groupId, long insurancePlanId, long memberUserId, int status) {

		for (PlanEnrollment planEnrollment :
				findByG_IP_M_S(
					groupId, insurancePlanId, memberUserId, status,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(planEnrollment);
		}
	}

	/**
	 * Returns the number of Plan Enrollments where groupId = &#63; and insurancePlanId = &#63; and memberUserId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param insurancePlanId the insurance plan ID
	 * @param memberUserId the member user ID
	 * @param status the status
	 * @return the number of matching Plan Enrollments
	 */
	@Override
	public int countByG_IP_M_S(
		long groupId, long insurancePlanId, long memberUserId, int status) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			FinderPath finderPath = _finderPathCountByG_IP_M_S;

			Object[] finderArgs = new Object[] {
				groupId, insurancePlanId, memberUserId, status
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_PLANENROLLMENT_WHERE);

				sb.append(_FINDER_COLUMN_G_IP_M_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_IP_M_S_INSURANCEPLANID_2);

				sb.append(_FINDER_COLUMN_G_IP_M_S_MEMBERUSERID_2);

				sb.append(_FINDER_COLUMN_G_IP_M_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(insurancePlanId);

					queryPos.add(memberUserId);

					queryPos.add(status);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	/**
	 * Returns the number of Plan Enrollments that the user has permission to view where groupId = &#63; and insurancePlanId = &#63; and memberUserId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param insurancePlanId the insurance plan ID
	 * @param memberUserId the member user ID
	 * @param status the status
	 * @return the number of matching Plan Enrollments that the user has permission to view
	 */
	@Override
	public int filterCountByG_IP_M_S(
		long groupId, long insurancePlanId, long memberUserId, int status) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_IP_M_S(
				groupId, insurancePlanId, memberUserId, status);
		}

		StringBundler sb = new StringBundler(5);

		sb.append(_FILTER_SQL_COUNT_PLANENROLLMENT_WHERE);

		sb.append(_FINDER_COLUMN_G_IP_M_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_IP_M_S_INSURANCEPLANID_2);

		sb.append(_FINDER_COLUMN_G_IP_M_S_MEMBERUSERID_2);

		sb.append(_FINDER_COLUMN_G_IP_M_S_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PlanEnrollment.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(insurancePlanId);

			queryPos.add(memberUserId);

			queryPos.add(status);

			Long count = (Long)sqlQuery.uniqueResult();

			return count.intValue();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private static final String _FINDER_COLUMN_G_IP_M_S_GROUPID_2 =
		"planEnrollment.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_IP_M_S_INSURANCEPLANID_2 =
		"planEnrollment.insurancePlanId = ? AND ";

	private static final String _FINDER_COLUMN_G_IP_M_S_MEMBERUSERID_2 =
		"planEnrollment.memberUserId = ? AND ";

	private static final String _FINDER_COLUMN_G_IP_M_S_STATUS_2 =
		"planEnrollment.status = ?";

	private FinderPath _finderPathWithPaginationFindByG_IP_M_NotS;
	private FinderPath _finderPathWithPaginationCountByG_IP_M_NotS;

	/**
	 * Returns all the Plan Enrollments where groupId = &#63; and insurancePlanId = &#63; and memberUserId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param insurancePlanId the insurance plan ID
	 * @param memberUserId the member user ID
	 * @param status the status
	 * @return the matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByG_IP_M_NotS(
		long groupId, long insurancePlanId, long memberUserId, int status) {

		return findByG_IP_M_NotS(
			groupId, insurancePlanId, memberUserId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Plan Enrollments where groupId = &#63; and insurancePlanId = &#63; and memberUserId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param insurancePlanId the insurance plan ID
	 * @param memberUserId the member user ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @return the range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByG_IP_M_NotS(
		long groupId, long insurancePlanId, long memberUserId, int status,
		int start, int end) {

		return findByG_IP_M_NotS(
			groupId, insurancePlanId, memberUserId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where groupId = &#63; and insurancePlanId = &#63; and memberUserId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param insurancePlanId the insurance plan ID
	 * @param memberUserId the member user ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByG_IP_M_NotS(
		long groupId, long insurancePlanId, long memberUserId, int status,
		int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		return findByG_IP_M_NotS(
			groupId, insurancePlanId, memberUserId, status, start, end,
			orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where groupId = &#63; and insurancePlanId = &#63; and memberUserId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param insurancePlanId the insurance plan ID
	 * @param memberUserId the member user ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByG_IP_M_NotS(
		long groupId, long insurancePlanId, long memberUserId, int status,
		int start, int end, OrderByComparator<PlanEnrollment> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByG_IP_M_NotS;
			finderArgs = new Object[] {
				groupId, insurancePlanId, memberUserId, status, start, end,
				orderByComparator
			};

			List<PlanEnrollment> list = null;

			if (useFinderCache) {
				list = (List<PlanEnrollment>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (PlanEnrollment planEnrollment : list) {
						if ((groupId != planEnrollment.getGroupId()) ||
							(insurancePlanId !=
								planEnrollment.getInsurancePlanId()) ||
							(memberUserId !=
								planEnrollment.getMemberUserId()) ||
							(status == planEnrollment.getStatus())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						6 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(6);
				}

				sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

				sb.append(_FINDER_COLUMN_G_IP_M_NOTS_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_IP_M_NOTS_INSURANCEPLANID_2);

				sb.append(_FINDER_COLUMN_G_IP_M_NOTS_MEMBERUSERID_2);

				sb.append(_FINDER_COLUMN_G_IP_M_NOTS_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(insurancePlanId);

					queryPos.add(memberUserId);

					queryPos.add(status);

					list = (List<PlanEnrollment>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first Plan Enrollment in the ordered set where groupId = &#63; and insurancePlanId = &#63; and memberUserId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param insurancePlanId the insurance plan ID
	 * @param memberUserId the member user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByG_IP_M_NotS_First(
			long groupId, long insurancePlanId, long memberUserId, int status,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByG_IP_M_NotS_First(
			groupId, insurancePlanId, memberUserId, status, orderByComparator);

		if (planEnrollment != null) {
			return planEnrollment;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", insurancePlanId=");
		sb.append(insurancePlanId);

		sb.append(", memberUserId=");
		sb.append(memberUserId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPlanEnrollmentException(sb.toString());
	}

	/**
	 * Returns the first Plan Enrollment in the ordered set where groupId = &#63; and insurancePlanId = &#63; and memberUserId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param insurancePlanId the insurance plan ID
	 * @param memberUserId the member user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByG_IP_M_NotS_First(
		long groupId, long insurancePlanId, long memberUserId, int status,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		List<PlanEnrollment> list = findByG_IP_M_NotS(
			groupId, insurancePlanId, memberUserId, status, 0, 1,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Plan Enrollment in the ordered set where groupId = &#63; and insurancePlanId = &#63; and memberUserId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param insurancePlanId the insurance plan ID
	 * @param memberUserId the member user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByG_IP_M_NotS_Last(
			long groupId, long insurancePlanId, long memberUserId, int status,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByG_IP_M_NotS_Last(
			groupId, insurancePlanId, memberUserId, status, orderByComparator);

		if (planEnrollment != null) {
			return planEnrollment;
		}

		StringBundler sb = new StringBundler(10);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", insurancePlanId=");
		sb.append(insurancePlanId);

		sb.append(", memberUserId=");
		sb.append(memberUserId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPlanEnrollmentException(sb.toString());
	}

	/**
	 * Returns the last Plan Enrollment in the ordered set where groupId = &#63; and insurancePlanId = &#63; and memberUserId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param insurancePlanId the insurance plan ID
	 * @param memberUserId the member user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByG_IP_M_NotS_Last(
		long groupId, long insurancePlanId, long memberUserId, int status,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		int count = countByG_IP_M_NotS(
			groupId, insurancePlanId, memberUserId, status);

		if (count == 0) {
			return null;
		}

		List<PlanEnrollment> list = findByG_IP_M_NotS(
			groupId, insurancePlanId, memberUserId, status, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Plan Enrollments before and after the current Plan Enrollment in the ordered set where groupId = &#63; and insurancePlanId = &#63; and memberUserId = &#63; and status &ne; &#63;.
	 *
	 * @param planEnrollmentId the primary key of the current Plan Enrollment
	 * @param groupId the group ID
	 * @param insurancePlanId the insurance plan ID
	 * @param memberUserId the member user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a Plan Enrollment with the primary key could not be found
	 */
	@Override
	public PlanEnrollment[] findByG_IP_M_NotS_PrevAndNext(
			long planEnrollmentId, long groupId, long insurancePlanId,
			long memberUserId, int status,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = findByPrimaryKey(planEnrollmentId);

		Session session = null;

		try {
			session = openSession();

			PlanEnrollment[] array = new PlanEnrollmentImpl[3];

			array[0] = getByG_IP_M_NotS_PrevAndNext(
				session, planEnrollment, groupId, insurancePlanId, memberUserId,
				status, orderByComparator, true);

			array[1] = planEnrollment;

			array[2] = getByG_IP_M_NotS_PrevAndNext(
				session, planEnrollment, groupId, insurancePlanId, memberUserId,
				status, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PlanEnrollment getByG_IP_M_NotS_PrevAndNext(
		Session session, PlanEnrollment planEnrollment, long groupId,
		long insurancePlanId, long memberUserId, int status,
		OrderByComparator<PlanEnrollment> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

		sb.append(_FINDER_COLUMN_G_IP_M_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_IP_M_NOTS_INSURANCEPLANID_2);

		sb.append(_FINDER_COLUMN_G_IP_M_NOTS_MEMBERUSERID_2);

		sb.append(_FINDER_COLUMN_G_IP_M_NOTS_STATUS_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(insurancePlanId);

		queryPos.add(memberUserId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						planEnrollment)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PlanEnrollment> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the Plan Enrollments that the user has permission to view where groupId = &#63; and insurancePlanId = &#63; and memberUserId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param insurancePlanId the insurance plan ID
	 * @param memberUserId the member user ID
	 * @param status the status
	 * @return the matching Plan Enrollments that the user has permission to view
	 */
	@Override
	public List<PlanEnrollment> filterFindByG_IP_M_NotS(
		long groupId, long insurancePlanId, long memberUserId, int status) {

		return filterFindByG_IP_M_NotS(
			groupId, insurancePlanId, memberUserId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Plan Enrollments that the user has permission to view where groupId = &#63; and insurancePlanId = &#63; and memberUserId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param insurancePlanId the insurance plan ID
	 * @param memberUserId the member user ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @return the range of matching Plan Enrollments that the user has permission to view
	 */
	@Override
	public List<PlanEnrollment> filterFindByG_IP_M_NotS(
		long groupId, long insurancePlanId, long memberUserId, int status,
		int start, int end) {

		return filterFindByG_IP_M_NotS(
			groupId, insurancePlanId, memberUserId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments that the user has permissions to view where groupId = &#63; and insurancePlanId = &#63; and memberUserId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param insurancePlanId the insurance plan ID
	 * @param memberUserId the member user ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Plan Enrollments that the user has permission to view
	 */
	@Override
	public List<PlanEnrollment> filterFindByG_IP_M_NotS(
		long groupId, long insurancePlanId, long memberUserId, int status,
		int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_IP_M_NotS(
				groupId, insurancePlanId, memberUserId, status, start, end,
				orderByComparator);
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(7);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PLANENROLLMENT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PLANENROLLMENT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_IP_M_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_IP_M_NOTS_INSURANCEPLANID_2);

		sb.append(_FINDER_COLUMN_G_IP_M_NOTS_MEMBERUSERID_2);

		sb.append(_FINDER_COLUMN_G_IP_M_NOTS_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PLANENROLLMENT_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			if (getDB().isSupportsInlineDistinct()) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator, true);
			}
			else {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_TABLE, orderByComparator, true);
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(PlanEnrollmentModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PlanEnrollmentModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PlanEnrollment.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, PlanEnrollmentImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, PlanEnrollmentImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(insurancePlanId);

			queryPos.add(memberUserId);

			queryPos.add(status);

			return (List<PlanEnrollment>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * Returns the Plan Enrollments before and after the current Plan Enrollment in the ordered set of Plan Enrollments that the user has permission to view where groupId = &#63; and insurancePlanId = &#63; and memberUserId = &#63; and status &ne; &#63;.
	 *
	 * @param planEnrollmentId the primary key of the current Plan Enrollment
	 * @param groupId the group ID
	 * @param insurancePlanId the insurance plan ID
	 * @param memberUserId the member user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a Plan Enrollment with the primary key could not be found
	 */
	@Override
	public PlanEnrollment[] filterFindByG_IP_M_NotS_PrevAndNext(
			long planEnrollmentId, long groupId, long insurancePlanId,
			long memberUserId, int status,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_IP_M_NotS_PrevAndNext(
				planEnrollmentId, groupId, insurancePlanId, memberUserId,
				status, orderByComparator);
		}

		PlanEnrollment planEnrollment = findByPrimaryKey(planEnrollmentId);

		Session session = null;

		try {
			session = openSession();

			PlanEnrollment[] array = new PlanEnrollmentImpl[3];

			array[0] = filterGetByG_IP_M_NotS_PrevAndNext(
				session, planEnrollment, groupId, insurancePlanId, memberUserId,
				status, orderByComparator, true);

			array[1] = planEnrollment;

			array[2] = filterGetByG_IP_M_NotS_PrevAndNext(
				session, planEnrollment, groupId, insurancePlanId, memberUserId,
				status, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PlanEnrollment filterGetByG_IP_M_NotS_PrevAndNext(
		Session session, PlanEnrollment planEnrollment, long groupId,
		long insurancePlanId, long memberUserId, int status,
		OrderByComparator<PlanEnrollment> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				8 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(7);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PLANENROLLMENT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PLANENROLLMENT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_IP_M_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_IP_M_NOTS_INSURANCEPLANID_2);

		sb.append(_FINDER_COLUMN_G_IP_M_NOTS_MEMBERUSERID_2);

		sb.append(_FINDER_COLUMN_G_IP_M_NOTS_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PLANENROLLMENT_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByConditionFields[i],
							true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByConditionFields[i],
							true));
				}

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByFields[i], true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByFields[i], true));
				}

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(PlanEnrollmentModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PlanEnrollmentModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PlanEnrollment.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PlanEnrollmentImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PlanEnrollmentImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(insurancePlanId);

		queryPos.add(memberUserId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						planEnrollment)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PlanEnrollment> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Plan Enrollments where groupId = &#63; and insurancePlanId = &#63; and memberUserId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param insurancePlanId the insurance plan ID
	 * @param memberUserId the member user ID
	 * @param status the status
	 */
	@Override
	public void removeByG_IP_M_NotS(
		long groupId, long insurancePlanId, long memberUserId, int status) {

		for (PlanEnrollment planEnrollment :
				findByG_IP_M_NotS(
					groupId, insurancePlanId, memberUserId, status,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(planEnrollment);
		}
	}

	/**
	 * Returns the number of Plan Enrollments where groupId = &#63; and insurancePlanId = &#63; and memberUserId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param insurancePlanId the insurance plan ID
	 * @param memberUserId the member user ID
	 * @param status the status
	 * @return the number of matching Plan Enrollments
	 */
	@Override
	public int countByG_IP_M_NotS(
		long groupId, long insurancePlanId, long memberUserId, int status) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByG_IP_M_NotS;

			Object[] finderArgs = new Object[] {
				groupId, insurancePlanId, memberUserId, status
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(5);

				sb.append(_SQL_COUNT_PLANENROLLMENT_WHERE);

				sb.append(_FINDER_COLUMN_G_IP_M_NOTS_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_IP_M_NOTS_INSURANCEPLANID_2);

				sb.append(_FINDER_COLUMN_G_IP_M_NOTS_MEMBERUSERID_2);

				sb.append(_FINDER_COLUMN_G_IP_M_NOTS_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(insurancePlanId);

					queryPos.add(memberUserId);

					queryPos.add(status);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	/**
	 * Returns the number of Plan Enrollments that the user has permission to view where groupId = &#63; and insurancePlanId = &#63; and memberUserId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param insurancePlanId the insurance plan ID
	 * @param memberUserId the member user ID
	 * @param status the status
	 * @return the number of matching Plan Enrollments that the user has permission to view
	 */
	@Override
	public int filterCountByG_IP_M_NotS(
		long groupId, long insurancePlanId, long memberUserId, int status) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_IP_M_NotS(
				groupId, insurancePlanId, memberUserId, status);
		}

		StringBundler sb = new StringBundler(5);

		sb.append(_FILTER_SQL_COUNT_PLANENROLLMENT_WHERE);

		sb.append(_FINDER_COLUMN_G_IP_M_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_IP_M_NOTS_INSURANCEPLANID_2);

		sb.append(_FINDER_COLUMN_G_IP_M_NOTS_MEMBERUSERID_2);

		sb.append(_FINDER_COLUMN_G_IP_M_NOTS_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PlanEnrollment.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(insurancePlanId);

			queryPos.add(memberUserId);

			queryPos.add(status);

			Long count = (Long)sqlQuery.uniqueResult();

			return count.intValue();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private static final String _FINDER_COLUMN_G_IP_M_NOTS_GROUPID_2 =
		"planEnrollment.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_IP_M_NOTS_INSURANCEPLANID_2 =
		"planEnrollment.insurancePlanId = ? AND ";

	private static final String _FINDER_COLUMN_G_IP_M_NOTS_MEMBERUSERID_2 =
		"planEnrollment.memberUserId = ? AND ";

	private static final String _FINDER_COLUMN_G_IP_M_NOTS_STATUS_2 =
		"planEnrollment.status != ?";

	private FinderPath _finderPathWithPaginationFindByC_S;
	private FinderPath _finderPathWithoutPaginationFindByC_S;
	private FinderPath _finderPathCountByC_S;

	/**
	 * Returns all the Plan Enrollments where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByC_S(long companyId, int status) {
		return findByC_S(
			companyId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Plan Enrollments where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @return the range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByC_S(
		long companyId, int status, int start, int end) {

		return findByC_S(companyId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByC_S(
		long companyId, int status, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		return findByC_S(
			companyId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByC_S(
		long companyId, int status, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByC_S;
					finderArgs = new Object[] {companyId, status};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByC_S;
				finderArgs = new Object[] {
					companyId, status, start, end, orderByComparator
				};
			}

			List<PlanEnrollment> list = null;

			if (useFinderCache) {
				list = (List<PlanEnrollment>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (PlanEnrollment planEnrollment : list) {
						if ((companyId != planEnrollment.getCompanyId()) ||
							(status != planEnrollment.getStatus())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						4 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(4);
				}

				sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

				sb.append(_FINDER_COLUMN_C_S_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(status);

					list = (List<PlanEnrollment>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first Plan Enrollment in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByC_S_First(
			long companyId, int status,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByC_S_First(
			companyId, status, orderByComparator);

		if (planEnrollment != null) {
			return planEnrollment;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPlanEnrollmentException(sb.toString());
	}

	/**
	 * Returns the first Plan Enrollment in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByC_S_First(
		long companyId, int status,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		List<PlanEnrollment> list = findByC_S(
			companyId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Plan Enrollment in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByC_S_Last(
			long companyId, int status,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByC_S_Last(
			companyId, status, orderByComparator);

		if (planEnrollment != null) {
			return planEnrollment;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPlanEnrollmentException(sb.toString());
	}

	/**
	 * Returns the last Plan Enrollment in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByC_S_Last(
		long companyId, int status,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		int count = countByC_S(companyId, status);

		if (count == 0) {
			return null;
		}

		List<PlanEnrollment> list = findByC_S(
			companyId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Plan Enrollments before and after the current Plan Enrollment in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param planEnrollmentId the primary key of the current Plan Enrollment
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a Plan Enrollment with the primary key could not be found
	 */
	@Override
	public PlanEnrollment[] findByC_S_PrevAndNext(
			long planEnrollmentId, long companyId, int status,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = findByPrimaryKey(planEnrollmentId);

		Session session = null;

		try {
			session = openSession();

			PlanEnrollment[] array = new PlanEnrollmentImpl[3];

			array[0] = getByC_S_PrevAndNext(
				session, planEnrollment, companyId, status, orderByComparator,
				true);

			array[1] = planEnrollment;

			array[2] = getByC_S_PrevAndNext(
				session, planEnrollment, companyId, status, orderByComparator,
				false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PlanEnrollment getByC_S_PrevAndNext(
		Session session, PlanEnrollment planEnrollment, long companyId,
		int status, OrderByComparator<PlanEnrollment> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

		sb.append(_FINDER_COLUMN_C_S_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_S_STATUS_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						planEnrollment)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PlanEnrollment> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Plan Enrollments where companyId = &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 */
	@Override
	public void removeByC_S(long companyId, int status) {
		for (PlanEnrollment planEnrollment :
				findByC_S(
					companyId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(planEnrollment);
		}
	}

	/**
	 * Returns the number of Plan Enrollments where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching Plan Enrollments
	 */
	@Override
	public int countByC_S(long companyId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			FinderPath finderPath = _finderPathCountByC_S;

			Object[] finderArgs = new Object[] {companyId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_PLANENROLLMENT_WHERE);

				sb.append(_FINDER_COLUMN_C_S_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(status);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	private static final String _FINDER_COLUMN_C_S_COMPANYID_2 =
		"planEnrollment.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_S_STATUS_2 =
		"planEnrollment.status = ?";

	private FinderPath _finderPathWithPaginationFindByC_NotS;
	private FinderPath _finderPathWithPaginationCountByC_NotS;

	/**
	 * Returns all the Plan Enrollments where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByC_NotS(long companyId, int status) {
		return findByC_NotS(
			companyId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Plan Enrollments where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @return the range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByC_NotS(
		long companyId, int status, int start, int end) {

		return findByC_NotS(companyId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByC_NotS(
		long companyId, int status, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		return findByC_NotS(
			companyId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByC_NotS(
		long companyId, int status, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByC_NotS;
			finderArgs = new Object[] {
				companyId, status, start, end, orderByComparator
			};

			List<PlanEnrollment> list = null;

			if (useFinderCache) {
				list = (List<PlanEnrollment>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (PlanEnrollment planEnrollment : list) {
						if ((companyId != planEnrollment.getCompanyId()) ||
							(status == planEnrollment.getStatus())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						4 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(4);
				}

				sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

				sb.append(_FINDER_COLUMN_C_NOTS_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_NOTS_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(status);

					list = (List<PlanEnrollment>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first Plan Enrollment in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByC_NotS_First(
			long companyId, int status,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByC_NotS_First(
			companyId, status, orderByComparator);

		if (planEnrollment != null) {
			return planEnrollment;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPlanEnrollmentException(sb.toString());
	}

	/**
	 * Returns the first Plan Enrollment in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByC_NotS_First(
		long companyId, int status,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		List<PlanEnrollment> list = findByC_NotS(
			companyId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Plan Enrollment in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByC_NotS_Last(
			long companyId, int status,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByC_NotS_Last(
			companyId, status, orderByComparator);

		if (planEnrollment != null) {
			return planEnrollment;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPlanEnrollmentException(sb.toString());
	}

	/**
	 * Returns the last Plan Enrollment in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByC_NotS_Last(
		long companyId, int status,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		int count = countByC_NotS(companyId, status);

		if (count == 0) {
			return null;
		}

		List<PlanEnrollment> list = findByC_NotS(
			companyId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Plan Enrollments before and after the current Plan Enrollment in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param planEnrollmentId the primary key of the current Plan Enrollment
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a Plan Enrollment with the primary key could not be found
	 */
	@Override
	public PlanEnrollment[] findByC_NotS_PrevAndNext(
			long planEnrollmentId, long companyId, int status,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = findByPrimaryKey(planEnrollmentId);

		Session session = null;

		try {
			session = openSession();

			PlanEnrollment[] array = new PlanEnrollmentImpl[3];

			array[0] = getByC_NotS_PrevAndNext(
				session, planEnrollment, companyId, status, orderByComparator,
				true);

			array[1] = planEnrollment;

			array[2] = getByC_NotS_PrevAndNext(
				session, planEnrollment, companyId, status, orderByComparator,
				false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PlanEnrollment getByC_NotS_PrevAndNext(
		Session session, PlanEnrollment planEnrollment, long companyId,
		int status, OrderByComparator<PlanEnrollment> orderByComparator,
		boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

		sb.append(_FINDER_COLUMN_C_NOTS_COMPANYID_2);

		sb.append(_FINDER_COLUMN_C_NOTS_STATUS_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						planEnrollment)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PlanEnrollment> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Plan Enrollments where companyId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 */
	@Override
	public void removeByC_NotS(long companyId, int status) {
		for (PlanEnrollment planEnrollment :
				findByC_NotS(
					companyId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(planEnrollment);
		}
	}

	/**
	 * Returns the number of Plan Enrollments where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching Plan Enrollments
	 */
	@Override
	public int countByC_NotS(long companyId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByC_NotS;

			Object[] finderArgs = new Object[] {companyId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_PLANENROLLMENT_WHERE);

				sb.append(_FINDER_COLUMN_C_NOTS_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_NOTS_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(status);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	private static final String _FINDER_COLUMN_C_NOTS_COMPANYID_2 =
		"planEnrollment.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_NOTS_STATUS_2 =
		"planEnrollment.status != ?";

	private FinderPath _finderPathWithPaginationFindByG_U_S;
	private FinderPath _finderPathWithoutPaginationFindByG_U_S;
	private FinderPath _finderPathCountByG_U_S;
	private FinderPath _finderPathWithPaginationCountByG_U_S;

	/**
	 * Returns all the Plan Enrollments where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByG_U_S(
		long groupId, long userId, int status) {

		return findByG_U_S(
			groupId, userId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the Plan Enrollments where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @return the range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByG_U_S(
		long groupId, long userId, int status, int start, int end) {

		return findByG_U_S(groupId, userId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByG_U_S(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		return findByG_U_S(
			groupId, userId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByG_U_S(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_U_S;
					finderArgs = new Object[] {groupId, userId, status};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_U_S;
				finderArgs = new Object[] {
					groupId, userId, status, start, end, orderByComparator
				};
			}

			List<PlanEnrollment> list = null;

			if (useFinderCache) {
				list = (List<PlanEnrollment>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (PlanEnrollment planEnrollment : list) {
						if ((groupId != planEnrollment.getGroupId()) ||
							(userId != planEnrollment.getUserId()) ||
							(status != planEnrollment.getStatus())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						5 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(5);
				}

				sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

				sb.append(_FINDER_COLUMN_G_U_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_U_S_USERID_2);

				sb.append(_FINDER_COLUMN_G_U_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(userId);

					queryPos.add(status);

					list = (List<PlanEnrollment>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first Plan Enrollment in the ordered set where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByG_U_S_First(
			long groupId, long userId, int status,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByG_U_S_First(
			groupId, userId, status, orderByComparator);

		if (planEnrollment != null) {
			return planEnrollment;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPlanEnrollmentException(sb.toString());
	}

	/**
	 * Returns the first Plan Enrollment in the ordered set where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByG_U_S_First(
		long groupId, long userId, int status,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		List<PlanEnrollment> list = findByG_U_S(
			groupId, userId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Plan Enrollment in the ordered set where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByG_U_S_Last(
			long groupId, long userId, int status,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByG_U_S_Last(
			groupId, userId, status, orderByComparator);

		if (planEnrollment != null) {
			return planEnrollment;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPlanEnrollmentException(sb.toString());
	}

	/**
	 * Returns the last Plan Enrollment in the ordered set where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByG_U_S_Last(
		long groupId, long userId, int status,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		int count = countByG_U_S(groupId, userId, status);

		if (count == 0) {
			return null;
		}

		List<PlanEnrollment> list = findByG_U_S(
			groupId, userId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Plan Enrollments before and after the current Plan Enrollment in the ordered set where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param planEnrollmentId the primary key of the current Plan Enrollment
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a Plan Enrollment with the primary key could not be found
	 */
	@Override
	public PlanEnrollment[] findByG_U_S_PrevAndNext(
			long planEnrollmentId, long groupId, long userId, int status,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = findByPrimaryKey(planEnrollmentId);

		Session session = null;

		try {
			session = openSession();

			PlanEnrollment[] array = new PlanEnrollmentImpl[3];

			array[0] = getByG_U_S_PrevAndNext(
				session, planEnrollment, groupId, userId, status,
				orderByComparator, true);

			array[1] = planEnrollment;

			array[2] = getByG_U_S_PrevAndNext(
				session, planEnrollment, groupId, userId, status,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PlanEnrollment getByG_U_S_PrevAndNext(
		Session session, PlanEnrollment planEnrollment, long groupId,
		long userId, int status,
		OrderByComparator<PlanEnrollment> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

		sb.append(_FINDER_COLUMN_G_U_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_S_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_S_STATUS_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(userId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						planEnrollment)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PlanEnrollment> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the Plan Enrollments that the user has permission to view where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the matching Plan Enrollments that the user has permission to view
	 */
	@Override
	public List<PlanEnrollment> filterFindByG_U_S(
		long groupId, long userId, int status) {

		return filterFindByG_U_S(
			groupId, userId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the Plan Enrollments that the user has permission to view where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @return the range of matching Plan Enrollments that the user has permission to view
	 */
	@Override
	public List<PlanEnrollment> filterFindByG_U_S(
		long groupId, long userId, int status, int start, int end) {

		return filterFindByG_U_S(groupId, userId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments that the user has permissions to view where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Plan Enrollments that the user has permission to view
	 */
	@Override
	public List<PlanEnrollment> filterFindByG_U_S(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_U_S(
				groupId, userId, status, start, end, orderByComparator);
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PLANENROLLMENT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PLANENROLLMENT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_U_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_S_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PLANENROLLMENT_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			if (getDB().isSupportsInlineDistinct()) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator, true);
			}
			else {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_TABLE, orderByComparator, true);
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(PlanEnrollmentModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PlanEnrollmentModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PlanEnrollment.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, PlanEnrollmentImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, PlanEnrollmentImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(userId);

			queryPos.add(status);

			return (List<PlanEnrollment>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * Returns the Plan Enrollments before and after the current Plan Enrollment in the ordered set of Plan Enrollments that the user has permission to view where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param planEnrollmentId the primary key of the current Plan Enrollment
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a Plan Enrollment with the primary key could not be found
	 */
	@Override
	public PlanEnrollment[] filterFindByG_U_S_PrevAndNext(
			long planEnrollmentId, long groupId, long userId, int status,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_U_S_PrevAndNext(
				planEnrollmentId, groupId, userId, status, orderByComparator);
		}

		PlanEnrollment planEnrollment = findByPrimaryKey(planEnrollmentId);

		Session session = null;

		try {
			session = openSession();

			PlanEnrollment[] array = new PlanEnrollmentImpl[3];

			array[0] = filterGetByG_U_S_PrevAndNext(
				session, planEnrollment, groupId, userId, status,
				orderByComparator, true);

			array[1] = planEnrollment;

			array[2] = filterGetByG_U_S_PrevAndNext(
				session, planEnrollment, groupId, userId, status,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PlanEnrollment filterGetByG_U_S_PrevAndNext(
		Session session, PlanEnrollment planEnrollment, long groupId,
		long userId, int status,
		OrderByComparator<PlanEnrollment> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PLANENROLLMENT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PLANENROLLMENT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_U_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_S_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PLANENROLLMENT_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByConditionFields[i],
							true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByConditionFields[i],
							true));
				}

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByFields[i], true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByFields[i], true));
				}

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(PlanEnrollmentModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PlanEnrollmentModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PlanEnrollment.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PlanEnrollmentImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PlanEnrollmentImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(userId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						planEnrollment)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PlanEnrollment> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the Plan Enrollments that the user has permission to view where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @return the matching Plan Enrollments that the user has permission to view
	 */
	@Override
	public List<PlanEnrollment> filterFindByG_U_S(
		long groupId, long userId, int[] statuses) {

		return filterFindByG_U_S(
			groupId, userId, statuses, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the Plan Enrollments that the user has permission to view where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @return the range of matching Plan Enrollments that the user has permission to view
	 */
	@Override
	public List<PlanEnrollment> filterFindByG_U_S(
		long groupId, long userId, int[] statuses, int start, int end) {

		return filterFindByG_U_S(groupId, userId, statuses, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments that the user has permission to view where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Plan Enrollments that the user has permission to view
	 */
	@Override
	public List<PlanEnrollment> filterFindByG_U_S(
		long groupId, long userId, int[] statuses, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_U_S(
				groupId, userId, statuses, start, end, orderByComparator);
		}

		if (statuses == null) {
			statuses = new int[0];
		}
		else if (statuses.length > 1) {
			statuses = ArrayUtil.sortedUnique(statuses);
		}

		StringBundler sb = new StringBundler();

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PLANENROLLMENT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PLANENROLLMENT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_U_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_S_USERID_2);

		if (statuses.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_U_S_STATUS_7);

			sb.append(StringUtil.merge(statuses));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PLANENROLLMENT_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			if (getDB().isSupportsInlineDistinct()) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator, true);
			}
			else {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_TABLE, orderByComparator, true);
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(PlanEnrollmentModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PlanEnrollmentModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PlanEnrollment.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, PlanEnrollmentImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, PlanEnrollmentImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(userId);

			return (List<PlanEnrollment>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * Returns all the Plan Enrollments where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @return the matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByG_U_S(
		long groupId, long userId, int[] statuses) {

		return findByG_U_S(
			groupId, userId, statuses, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the Plan Enrollments where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @return the range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByG_U_S(
		long groupId, long userId, int[] statuses, int start, int end) {

		return findByG_U_S(groupId, userId, statuses, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByG_U_S(
		long groupId, long userId, int[] statuses, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		return findByG_U_S(
			groupId, userId, statuses, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where groupId = &#63; and userId = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByG_U_S(
		long groupId, long userId, int[] statuses, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator,
		boolean useFinderCache) {

		if (statuses == null) {
			statuses = new int[0];
		}
		else if (statuses.length > 1) {
			statuses = ArrayUtil.sortedUnique(statuses);
		}

		if (statuses.length == 1) {
			return findByG_U_S(
				groupId, userId, statuses[0], start, end, orderByComparator);
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderArgs = new Object[] {
						groupId, userId, StringUtil.merge(statuses)
					};
				}
			}
			else if (useFinderCache) {
				finderArgs = new Object[] {
					groupId, userId, StringUtil.merge(statuses), start, end,
					orderByComparator
				};
			}

			List<PlanEnrollment> list = null;

			if (useFinderCache) {
				list = (List<PlanEnrollment>)finderCache.getResult(
					_finderPathWithPaginationFindByG_U_S, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (PlanEnrollment planEnrollment : list) {
						if ((groupId != planEnrollment.getGroupId()) ||
							(userId != planEnrollment.getUserId()) ||
							!ArrayUtil.contains(
								statuses, planEnrollment.getStatus())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

				sb.append(_FINDER_COLUMN_G_U_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_U_S_USERID_2);

				if (statuses.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_G_U_S_STATUS_7);

					sb.append(StringUtil.merge(statuses));

					sb.append(")");

					sb.append(")");
				}

				sb.setStringAt(
					removeConjunction(sb.stringAt(sb.index() - 1)),
					sb.index() - 1);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(userId);

					list = (List<PlanEnrollment>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(
							_finderPathWithPaginationFindByG_U_S, finderArgs,
							list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Removes all the Plan Enrollments where groupId = &#63; and userId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 */
	@Override
	public void removeByG_U_S(long groupId, long userId, int status) {
		for (PlanEnrollment planEnrollment :
				findByG_U_S(
					groupId, userId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(planEnrollment);
		}
	}

	/**
	 * Returns the number of Plan Enrollments where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the number of matching Plan Enrollments
	 */
	@Override
	public int countByG_U_S(long groupId, long userId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			FinderPath finderPath = _finderPathCountByG_U_S;

			Object[] finderArgs = new Object[] {groupId, userId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_PLANENROLLMENT_WHERE);

				sb.append(_FINDER_COLUMN_G_U_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_U_S_USERID_2);

				sb.append(_FINDER_COLUMN_G_U_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(userId);

					queryPos.add(status);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	/**
	 * Returns the number of Plan Enrollments where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @return the number of matching Plan Enrollments
	 */
	@Override
	public int countByG_U_S(long groupId, long userId, int[] statuses) {
		if (statuses == null) {
			statuses = new int[0];
		}
		else if (statuses.length > 1) {
			statuses = ArrayUtil.sortedUnique(statuses);
		}

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			Object[] finderArgs = new Object[] {
				groupId, userId, StringUtil.merge(statuses)
			};

			Long count = (Long)finderCache.getResult(
				_finderPathWithPaginationCountByG_U_S, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_COUNT_PLANENROLLMENT_WHERE);

				sb.append(_FINDER_COLUMN_G_U_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_U_S_USERID_2);

				if (statuses.length > 0) {
					sb.append("(");

					sb.append(_FINDER_COLUMN_G_U_S_STATUS_7);

					sb.append(StringUtil.merge(statuses));

					sb.append(")");

					sb.append(")");
				}

				sb.setStringAt(
					removeConjunction(sb.stringAt(sb.index() - 1)),
					sb.index() - 1);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(userId);

					count = (Long)query.uniqueResult();

					finderCache.putResult(
						_finderPathWithPaginationCountByG_U_S, finderArgs,
						count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	/**
	 * Returns the number of Plan Enrollments that the user has permission to view where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the number of matching Plan Enrollments that the user has permission to view
	 */
	@Override
	public int filterCountByG_U_S(long groupId, long userId, int status) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_U_S(groupId, userId, status);
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_PLANENROLLMENT_WHERE);

		sb.append(_FINDER_COLUMN_G_U_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_S_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_S_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PlanEnrollment.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(userId);

			queryPos.add(status);

			Long count = (Long)sqlQuery.uniqueResult();

			return count.intValue();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * Returns the number of Plan Enrollments that the user has permission to view where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @return the number of matching Plan Enrollments that the user has permission to view
	 */
	@Override
	public int filterCountByG_U_S(long groupId, long userId, int[] statuses) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_U_S(groupId, userId, statuses);
		}

		if (statuses == null) {
			statuses = new int[0];
		}
		else if (statuses.length > 1) {
			statuses = ArrayUtil.sortedUnique(statuses);
		}

		StringBundler sb = new StringBundler();

		sb.append(_FILTER_SQL_COUNT_PLANENROLLMENT_WHERE);

		sb.append(_FINDER_COLUMN_G_U_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_S_USERID_2);

		if (statuses.length > 0) {
			sb.append("(");

			sb.append(_FINDER_COLUMN_G_U_S_STATUS_7);

			sb.append(StringUtil.merge(statuses));

			sb.append(")");

			sb.append(")");
		}

		sb.setStringAt(
			removeConjunction(sb.stringAt(sb.index() - 1)), sb.index() - 1);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PlanEnrollment.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(userId);

			Long count = (Long)sqlQuery.uniqueResult();

			return count.intValue();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private static final String _FINDER_COLUMN_G_U_S_GROUPID_2 =
		"planEnrollment.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_U_S_USERID_2 =
		"planEnrollment.userId = ? AND ";

	private static final String _FINDER_COLUMN_G_U_S_STATUS_2 =
		"planEnrollment.status = ?";

	private static final String _FINDER_COLUMN_G_U_S_STATUS_7 =
		"planEnrollment.status IN (";

	private FinderPath _finderPathWithPaginationFindByG_U_NotS;
	private FinderPath _finderPathWithPaginationCountByG_U_NotS;

	/**
	 * Returns all the Plan Enrollments where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByG_U_NotS(
		long groupId, long userId, int status) {

		return findByG_U_NotS(
			groupId, userId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the Plan Enrollments where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @return the range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByG_U_NotS(
		long groupId, long userId, int status, int start, int end) {

		return findByG_U_NotS(groupId, userId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByG_U_NotS(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		return findByG_U_NotS(
			groupId, userId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByG_U_NotS(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByG_U_NotS;
			finderArgs = new Object[] {
				groupId, userId, status, start, end, orderByComparator
			};

			List<PlanEnrollment> list = null;

			if (useFinderCache) {
				list = (List<PlanEnrollment>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (PlanEnrollment planEnrollment : list) {
						if ((groupId != planEnrollment.getGroupId()) ||
							(userId != planEnrollment.getUserId()) ||
							(status == planEnrollment.getStatus())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						5 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(5);
				}

				sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

				sb.append(_FINDER_COLUMN_G_U_NOTS_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_U_NOTS_USERID_2);

				sb.append(_FINDER_COLUMN_G_U_NOTS_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(userId);

					queryPos.add(status);

					list = (List<PlanEnrollment>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first Plan Enrollment in the ordered set where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByG_U_NotS_First(
			long groupId, long userId, int status,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByG_U_NotS_First(
			groupId, userId, status, orderByComparator);

		if (planEnrollment != null) {
			return planEnrollment;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPlanEnrollmentException(sb.toString());
	}

	/**
	 * Returns the first Plan Enrollment in the ordered set where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByG_U_NotS_First(
		long groupId, long userId, int status,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		List<PlanEnrollment> list = findByG_U_NotS(
			groupId, userId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Plan Enrollment in the ordered set where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByG_U_NotS_Last(
			long groupId, long userId, int status,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByG_U_NotS_Last(
			groupId, userId, status, orderByComparator);

		if (planEnrollment != null) {
			return planEnrollment;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", userId=");
		sb.append(userId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchPlanEnrollmentException(sb.toString());
	}

	/**
	 * Returns the last Plan Enrollment in the ordered set where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByG_U_NotS_Last(
		long groupId, long userId, int status,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		int count = countByG_U_NotS(groupId, userId, status);

		if (count == 0) {
			return null;
		}

		List<PlanEnrollment> list = findByG_U_NotS(
			groupId, userId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Plan Enrollments before and after the current Plan Enrollment in the ordered set where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param planEnrollmentId the primary key of the current Plan Enrollment
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a Plan Enrollment with the primary key could not be found
	 */
	@Override
	public PlanEnrollment[] findByG_U_NotS_PrevAndNext(
			long planEnrollmentId, long groupId, long userId, int status,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = findByPrimaryKey(planEnrollmentId);

		Session session = null;

		try {
			session = openSession();

			PlanEnrollment[] array = new PlanEnrollmentImpl[3];

			array[0] = getByG_U_NotS_PrevAndNext(
				session, planEnrollment, groupId, userId, status,
				orderByComparator, true);

			array[1] = planEnrollment;

			array[2] = getByG_U_NotS_PrevAndNext(
				session, planEnrollment, groupId, userId, status,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PlanEnrollment getByG_U_NotS_PrevAndNext(
		Session session, PlanEnrollment planEnrollment, long groupId,
		long userId, int status,
		OrderByComparator<PlanEnrollment> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

		sb.append(_FINDER_COLUMN_G_U_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_NOTS_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_NOTS_STATUS_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(userId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						planEnrollment)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PlanEnrollment> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the Plan Enrollments that the user has permission to view where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the matching Plan Enrollments that the user has permission to view
	 */
	@Override
	public List<PlanEnrollment> filterFindByG_U_NotS(
		long groupId, long userId, int status) {

		return filterFindByG_U_NotS(
			groupId, userId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the Plan Enrollments that the user has permission to view where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @return the range of matching Plan Enrollments that the user has permission to view
	 */
	@Override
	public List<PlanEnrollment> filterFindByG_U_NotS(
		long groupId, long userId, int status, int start, int end) {

		return filterFindByG_U_NotS(groupId, userId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments that the user has permissions to view where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Plan Enrollments that the user has permission to view
	 */
	@Override
	public List<PlanEnrollment> filterFindByG_U_NotS(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_U_NotS(
				groupId, userId, status, start, end, orderByComparator);
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PLANENROLLMENT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PLANENROLLMENT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_U_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_NOTS_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_NOTS_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PLANENROLLMENT_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			if (getDB().isSupportsInlineDistinct()) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator, true);
			}
			else {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_TABLE, orderByComparator, true);
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(PlanEnrollmentModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PlanEnrollmentModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PlanEnrollment.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, PlanEnrollmentImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, PlanEnrollmentImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(userId);

			queryPos.add(status);

			return (List<PlanEnrollment>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * Returns the Plan Enrollments before and after the current Plan Enrollment in the ordered set of Plan Enrollments that the user has permission to view where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param planEnrollmentId the primary key of the current Plan Enrollment
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a Plan Enrollment with the primary key could not be found
	 */
	@Override
	public PlanEnrollment[] filterFindByG_U_NotS_PrevAndNext(
			long planEnrollmentId, long groupId, long userId, int status,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_U_NotS_PrevAndNext(
				planEnrollmentId, groupId, userId, status, orderByComparator);
		}

		PlanEnrollment planEnrollment = findByPrimaryKey(planEnrollmentId);

		Session session = null;

		try {
			session = openSession();

			PlanEnrollment[] array = new PlanEnrollmentImpl[3];

			array[0] = filterGetByG_U_NotS_PrevAndNext(
				session, planEnrollment, groupId, userId, status,
				orderByComparator, true);

			array[1] = planEnrollment;

			array[2] = filterGetByG_U_NotS_PrevAndNext(
				session, planEnrollment, groupId, userId, status,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PlanEnrollment filterGetByG_U_NotS_PrevAndNext(
		Session session, PlanEnrollment planEnrollment, long groupId,
		long userId, int status,
		OrderByComparator<PlanEnrollment> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				7 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(6);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PLANENROLLMENT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PLANENROLLMENT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_U_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_NOTS_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_NOTS_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PLANENROLLMENT_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByConditionFields[i],
							true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByConditionFields[i],
							true));
				}

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByFields[i], true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByFields[i], true));
				}

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(PlanEnrollmentModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PlanEnrollmentModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PlanEnrollment.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PlanEnrollmentImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PlanEnrollmentImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(userId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						planEnrollment)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PlanEnrollment> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Plan Enrollments where groupId = &#63; and userId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 */
	@Override
	public void removeByG_U_NotS(long groupId, long userId, int status) {
		for (PlanEnrollment planEnrollment :
				findByG_U_NotS(
					groupId, userId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(planEnrollment);
		}
	}

	/**
	 * Returns the number of Plan Enrollments where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the number of matching Plan Enrollments
	 */
	@Override
	public int countByG_U_NotS(long groupId, long userId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByG_U_NotS;

			Object[] finderArgs = new Object[] {groupId, userId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_PLANENROLLMENT_WHERE);

				sb.append(_FINDER_COLUMN_G_U_NOTS_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_U_NOTS_USERID_2);

				sb.append(_FINDER_COLUMN_G_U_NOTS_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(userId);

					queryPos.add(status);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	/**
	 * Returns the number of Plan Enrollments that the user has permission to view where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the number of matching Plan Enrollments that the user has permission to view
	 */
	@Override
	public int filterCountByG_U_NotS(long groupId, long userId, int status) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_U_NotS(groupId, userId, status);
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_PLANENROLLMENT_WHERE);

		sb.append(_FINDER_COLUMN_G_U_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_NOTS_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_NOTS_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PlanEnrollment.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(userId);

			queryPos.add(status);

			Long count = (Long)sqlQuery.uniqueResult();

			return count.intValue();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private static final String _FINDER_COLUMN_G_U_NOTS_GROUPID_2 =
		"planEnrollment.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_U_NOTS_USERID_2 =
		"planEnrollment.userId = ? AND ";

	private static final String _FINDER_COLUMN_G_U_NOTS_STATUS_2 =
		"planEnrollment.status != ?";

	private FinderPath _finderPathWithPaginationFindByUserId;
	private FinderPath _finderPathWithoutPaginationFindByUserId;
	private FinderPath _finderPathCountByUserId;

	/**
	 * Returns all the Plan Enrollments where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByUserId(long userId) {
		return findByUserId(userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Plan Enrollments where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @return the range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByUserId(long userId, int start, int end) {
		return findByUserId(userId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByUserId(
		long userId, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		return findByUserId(userId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByUserId(
		long userId, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByUserId;
					finderArgs = new Object[] {userId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByUserId;
				finderArgs = new Object[] {
					userId, start, end, orderByComparator
				};
			}

			List<PlanEnrollment> list = null;

			if (useFinderCache) {
				list = (List<PlanEnrollment>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (PlanEnrollment planEnrollment : list) {
						if (userId != planEnrollment.getUserId()) {
							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						3 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(3);
				}

				sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

				sb.append(_FINDER_COLUMN_USERID_USERID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(userId);

					list = (List<PlanEnrollment>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first Plan Enrollment in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByUserId_First(
			long userId, OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByUserId_First(
			userId, orderByComparator);

		if (planEnrollment != null) {
			return planEnrollment;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("userId=");
		sb.append(userId);

		sb.append("}");

		throw new NoSuchPlanEnrollmentException(sb.toString());
	}

	/**
	 * Returns the first Plan Enrollment in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByUserId_First(
		long userId, OrderByComparator<PlanEnrollment> orderByComparator) {

		List<PlanEnrollment> list = findByUserId(
			userId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Plan Enrollment in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByUserId_Last(
			long userId, OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByUserId_Last(
			userId, orderByComparator);

		if (planEnrollment != null) {
			return planEnrollment;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("userId=");
		sb.append(userId);

		sb.append("}");

		throw new NoSuchPlanEnrollmentException(sb.toString());
	}

	/**
	 * Returns the last Plan Enrollment in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByUserId_Last(
		long userId, OrderByComparator<PlanEnrollment> orderByComparator) {

		int count = countByUserId(userId);

		if (count == 0) {
			return null;
		}

		List<PlanEnrollment> list = findByUserId(
			userId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Plan Enrollments before and after the current Plan Enrollment in the ordered set where userId = &#63;.
	 *
	 * @param planEnrollmentId the primary key of the current Plan Enrollment
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a Plan Enrollment with the primary key could not be found
	 */
	@Override
	public PlanEnrollment[] findByUserId_PrevAndNext(
			long planEnrollmentId, long userId,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = findByPrimaryKey(planEnrollmentId);

		Session session = null;

		try {
			session = openSession();

			PlanEnrollment[] array = new PlanEnrollmentImpl[3];

			array[0] = getByUserId_PrevAndNext(
				session, planEnrollment, userId, orderByComparator, true);

			array[1] = planEnrollment;

			array[2] = getByUserId_PrevAndNext(
				session, planEnrollment, userId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PlanEnrollment getByUserId_PrevAndNext(
		Session session, PlanEnrollment planEnrollment, long userId,
		OrderByComparator<PlanEnrollment> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

		sb.append(_FINDER_COLUMN_USERID_USERID_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(userId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						planEnrollment)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PlanEnrollment> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Plan Enrollments where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	@Override
	public void removeByUserId(long userId) {
		for (PlanEnrollment planEnrollment :
				findByUserId(
					userId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(planEnrollment);
		}
	}

	/**
	 * Returns the number of Plan Enrollments where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching Plan Enrollments
	 */
	@Override
	public int countByUserId(long userId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			FinderPath finderPath = _finderPathCountByUserId;

			Object[] finderArgs = new Object[] {userId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_PLANENROLLMENT_WHERE);

				sb.append(_FINDER_COLUMN_USERID_USERID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(userId);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	private static final String _FINDER_COLUMN_USERID_USERID_2 =
		"planEnrollment.userId = ?";

	private FinderPath _finderPathWithPaginationFindByUserIdEnrollmentStatus;
	private FinderPath _finderPathWithoutPaginationFindByUserIdEnrollmentStatus;
	private FinderPath _finderPathCountByUserIdEnrollmentStatus;

	/**
	 * Returns all the Plan Enrollments where userId = &#63; and enrollmentStatus = &#63;.
	 *
	 * @param userId the user ID
	 * @param enrollmentStatus the enrollment status
	 * @return the matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByUserIdEnrollmentStatus(
		long userId, int enrollmentStatus) {

		return findByUserIdEnrollmentStatus(
			userId, enrollmentStatus, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the Plan Enrollments where userId = &#63; and enrollmentStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param enrollmentStatus the enrollment status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @return the range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByUserIdEnrollmentStatus(
		long userId, int enrollmentStatus, int start, int end) {

		return findByUserIdEnrollmentStatus(
			userId, enrollmentStatus, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where userId = &#63; and enrollmentStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param enrollmentStatus the enrollment status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByUserIdEnrollmentStatus(
		long userId, int enrollmentStatus, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		return findByUserIdEnrollmentStatus(
			userId, enrollmentStatus, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where userId = &#63; and enrollmentStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param enrollmentStatus the enrollment status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByUserIdEnrollmentStatus(
		long userId, int enrollmentStatus, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath =
						_finderPathWithoutPaginationFindByUserIdEnrollmentStatus;
					finderArgs = new Object[] {userId, enrollmentStatus};
				}
			}
			else if (useFinderCache) {
				finderPath =
					_finderPathWithPaginationFindByUserIdEnrollmentStatus;
				finderArgs = new Object[] {
					userId, enrollmentStatus, start, end, orderByComparator
				};
			}

			List<PlanEnrollment> list = null;

			if (useFinderCache) {
				list = (List<PlanEnrollment>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (PlanEnrollment planEnrollment : list) {
						if ((userId != planEnrollment.getUserId()) ||
							(enrollmentStatus !=
								planEnrollment.getEnrollmentStatus())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						4 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(4);
				}

				sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

				sb.append(_FINDER_COLUMN_USERIDENROLLMENTSTATUS_USERID_2);

				sb.append(
					_FINDER_COLUMN_USERIDENROLLMENTSTATUS_ENROLLMENTSTATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(userId);

					queryPos.add(enrollmentStatus);

					list = (List<PlanEnrollment>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first Plan Enrollment in the ordered set where userId = &#63; and enrollmentStatus = &#63;.
	 *
	 * @param userId the user ID
	 * @param enrollmentStatus the enrollment status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByUserIdEnrollmentStatus_First(
			long userId, int enrollmentStatus,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByUserIdEnrollmentStatus_First(
			userId, enrollmentStatus, orderByComparator);

		if (planEnrollment != null) {
			return planEnrollment;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("userId=");
		sb.append(userId);

		sb.append(", enrollmentStatus=");
		sb.append(enrollmentStatus);

		sb.append("}");

		throw new NoSuchPlanEnrollmentException(sb.toString());
	}

	/**
	 * Returns the first Plan Enrollment in the ordered set where userId = &#63; and enrollmentStatus = &#63;.
	 *
	 * @param userId the user ID
	 * @param enrollmentStatus the enrollment status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByUserIdEnrollmentStatus_First(
		long userId, int enrollmentStatus,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		List<PlanEnrollment> list = findByUserIdEnrollmentStatus(
			userId, enrollmentStatus, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Plan Enrollment in the ordered set where userId = &#63; and enrollmentStatus = &#63;.
	 *
	 * @param userId the user ID
	 * @param enrollmentStatus the enrollment status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByUserIdEnrollmentStatus_Last(
			long userId, int enrollmentStatus,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByUserIdEnrollmentStatus_Last(
			userId, enrollmentStatus, orderByComparator);

		if (planEnrollment != null) {
			return planEnrollment;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("userId=");
		sb.append(userId);

		sb.append(", enrollmentStatus=");
		sb.append(enrollmentStatus);

		sb.append("}");

		throw new NoSuchPlanEnrollmentException(sb.toString());
	}

	/**
	 * Returns the last Plan Enrollment in the ordered set where userId = &#63; and enrollmentStatus = &#63;.
	 *
	 * @param userId the user ID
	 * @param enrollmentStatus the enrollment status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByUserIdEnrollmentStatus_Last(
		long userId, int enrollmentStatus,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		int count = countByUserIdEnrollmentStatus(userId, enrollmentStatus);

		if (count == 0) {
			return null;
		}

		List<PlanEnrollment> list = findByUserIdEnrollmentStatus(
			userId, enrollmentStatus, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Plan Enrollments before and after the current Plan Enrollment in the ordered set where userId = &#63; and enrollmentStatus = &#63;.
	 *
	 * @param planEnrollmentId the primary key of the current Plan Enrollment
	 * @param userId the user ID
	 * @param enrollmentStatus the enrollment status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a Plan Enrollment with the primary key could not be found
	 */
	@Override
	public PlanEnrollment[] findByUserIdEnrollmentStatus_PrevAndNext(
			long planEnrollmentId, long userId, int enrollmentStatus,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = findByPrimaryKey(planEnrollmentId);

		Session session = null;

		try {
			session = openSession();

			PlanEnrollment[] array = new PlanEnrollmentImpl[3];

			array[0] = getByUserIdEnrollmentStatus_PrevAndNext(
				session, planEnrollment, userId, enrollmentStatus,
				orderByComparator, true);

			array[1] = planEnrollment;

			array[2] = getByUserIdEnrollmentStatus_PrevAndNext(
				session, planEnrollment, userId, enrollmentStatus,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PlanEnrollment getByUserIdEnrollmentStatus_PrevAndNext(
		Session session, PlanEnrollment planEnrollment, long userId,
		int enrollmentStatus,
		OrderByComparator<PlanEnrollment> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

		sb.append(_FINDER_COLUMN_USERIDENROLLMENTSTATUS_USERID_2);

		sb.append(_FINDER_COLUMN_USERIDENROLLMENTSTATUS_ENROLLMENTSTATUS_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(userId);

		queryPos.add(enrollmentStatus);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						planEnrollment)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PlanEnrollment> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Plan Enrollments where userId = &#63; and enrollmentStatus = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param enrollmentStatus the enrollment status
	 */
	@Override
	public void removeByUserIdEnrollmentStatus(
		long userId, int enrollmentStatus) {

		for (PlanEnrollment planEnrollment :
				findByUserIdEnrollmentStatus(
					userId, enrollmentStatus, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(planEnrollment);
		}
	}

	/**
	 * Returns the number of Plan Enrollments where userId = &#63; and enrollmentStatus = &#63;.
	 *
	 * @param userId the user ID
	 * @param enrollmentStatus the enrollment status
	 * @return the number of matching Plan Enrollments
	 */
	@Override
	public int countByUserIdEnrollmentStatus(
		long userId, int enrollmentStatus) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			FinderPath finderPath = _finderPathCountByUserIdEnrollmentStatus;

			Object[] finderArgs = new Object[] {userId, enrollmentStatus};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_PLANENROLLMENT_WHERE);

				sb.append(_FINDER_COLUMN_USERIDENROLLMENTSTATUS_USERID_2);

				sb.append(
					_FINDER_COLUMN_USERIDENROLLMENTSTATUS_ENROLLMENTSTATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(userId);

					queryPos.add(enrollmentStatus);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	private static final String _FINDER_COLUMN_USERIDENROLLMENTSTATUS_USERID_2 =
		"planEnrollment.userId = ? AND ";

	private static final String
		_FINDER_COLUMN_USERIDENROLLMENTSTATUS_ENROLLMENTSTATUS_2 =
			"planEnrollment.enrollmentStatus = ?";

	private FinderPath _finderPathWithPaginationFindByGroupIdEnrollmentStatus;
	private FinderPath
		_finderPathWithoutPaginationFindByGroupIdEnrollmentStatus;
	private FinderPath _finderPathCountByGroupIdEnrollmentStatus;

	/**
	 * Returns all the Plan Enrollments where groupId = &#63; and enrollmentStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param enrollmentStatus the enrollment status
	 * @return the matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByGroupIdEnrollmentStatus(
		long groupId, int enrollmentStatus) {

		return findByGroupIdEnrollmentStatus(
			groupId, enrollmentStatus, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the Plan Enrollments where groupId = &#63; and enrollmentStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param enrollmentStatus the enrollment status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @return the range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByGroupIdEnrollmentStatus(
		long groupId, int enrollmentStatus, int start, int end) {

		return findByGroupIdEnrollmentStatus(
			groupId, enrollmentStatus, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where groupId = &#63; and enrollmentStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param enrollmentStatus the enrollment status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByGroupIdEnrollmentStatus(
		long groupId, int enrollmentStatus, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		return findByGroupIdEnrollmentStatus(
			groupId, enrollmentStatus, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where groupId = &#63; and enrollmentStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param enrollmentStatus the enrollment status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByGroupIdEnrollmentStatus(
		long groupId, int enrollmentStatus, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath =
						_finderPathWithoutPaginationFindByGroupIdEnrollmentStatus;
					finderArgs = new Object[] {groupId, enrollmentStatus};
				}
			}
			else if (useFinderCache) {
				finderPath =
					_finderPathWithPaginationFindByGroupIdEnrollmentStatus;
				finderArgs = new Object[] {
					groupId, enrollmentStatus, start, end, orderByComparator
				};
			}

			List<PlanEnrollment> list = null;

			if (useFinderCache) {
				list = (List<PlanEnrollment>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (PlanEnrollment planEnrollment : list) {
						if ((groupId != planEnrollment.getGroupId()) ||
							(enrollmentStatus !=
								planEnrollment.getEnrollmentStatus())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						4 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(4);
				}

				sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

				sb.append(_FINDER_COLUMN_GROUPIDENROLLMENTSTATUS_GROUPID_2);

				sb.append(
					_FINDER_COLUMN_GROUPIDENROLLMENTSTATUS_ENROLLMENTSTATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(enrollmentStatus);

					list = (List<PlanEnrollment>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first Plan Enrollment in the ordered set where groupId = &#63; and enrollmentStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param enrollmentStatus the enrollment status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByGroupIdEnrollmentStatus_First(
			long groupId, int enrollmentStatus,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByGroupIdEnrollmentStatus_First(
			groupId, enrollmentStatus, orderByComparator);

		if (planEnrollment != null) {
			return planEnrollment;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", enrollmentStatus=");
		sb.append(enrollmentStatus);

		sb.append("}");

		throw new NoSuchPlanEnrollmentException(sb.toString());
	}

	/**
	 * Returns the first Plan Enrollment in the ordered set where groupId = &#63; and enrollmentStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param enrollmentStatus the enrollment status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByGroupIdEnrollmentStatus_First(
		long groupId, int enrollmentStatus,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		List<PlanEnrollment> list = findByGroupIdEnrollmentStatus(
			groupId, enrollmentStatus, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Plan Enrollment in the ordered set where groupId = &#63; and enrollmentStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param enrollmentStatus the enrollment status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByGroupIdEnrollmentStatus_Last(
			long groupId, int enrollmentStatus,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByGroupIdEnrollmentStatus_Last(
			groupId, enrollmentStatus, orderByComparator);

		if (planEnrollment != null) {
			return planEnrollment;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", enrollmentStatus=");
		sb.append(enrollmentStatus);

		sb.append("}");

		throw new NoSuchPlanEnrollmentException(sb.toString());
	}

	/**
	 * Returns the last Plan Enrollment in the ordered set where groupId = &#63; and enrollmentStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param enrollmentStatus the enrollment status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByGroupIdEnrollmentStatus_Last(
		long groupId, int enrollmentStatus,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		int count = countByGroupIdEnrollmentStatus(groupId, enrollmentStatus);

		if (count == 0) {
			return null;
		}

		List<PlanEnrollment> list = findByGroupIdEnrollmentStatus(
			groupId, enrollmentStatus, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Plan Enrollments before and after the current Plan Enrollment in the ordered set where groupId = &#63; and enrollmentStatus = &#63;.
	 *
	 * @param planEnrollmentId the primary key of the current Plan Enrollment
	 * @param groupId the group ID
	 * @param enrollmentStatus the enrollment status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a Plan Enrollment with the primary key could not be found
	 */
	@Override
	public PlanEnrollment[] findByGroupIdEnrollmentStatus_PrevAndNext(
			long planEnrollmentId, long groupId, int enrollmentStatus,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = findByPrimaryKey(planEnrollmentId);

		Session session = null;

		try {
			session = openSession();

			PlanEnrollment[] array = new PlanEnrollmentImpl[3];

			array[0] = getByGroupIdEnrollmentStatus_PrevAndNext(
				session, planEnrollment, groupId, enrollmentStatus,
				orderByComparator, true);

			array[1] = planEnrollment;

			array[2] = getByGroupIdEnrollmentStatus_PrevAndNext(
				session, planEnrollment, groupId, enrollmentStatus,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PlanEnrollment getByGroupIdEnrollmentStatus_PrevAndNext(
		Session session, PlanEnrollment planEnrollment, long groupId,
		int enrollmentStatus,
		OrderByComparator<PlanEnrollment> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

		sb.append(_FINDER_COLUMN_GROUPIDENROLLMENTSTATUS_GROUPID_2);

		sb.append(_FINDER_COLUMN_GROUPIDENROLLMENTSTATUS_ENROLLMENTSTATUS_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(enrollmentStatus);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						planEnrollment)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PlanEnrollment> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the Plan Enrollments that the user has permission to view where groupId = &#63; and enrollmentStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param enrollmentStatus the enrollment status
	 * @return the matching Plan Enrollments that the user has permission to view
	 */
	@Override
	public List<PlanEnrollment> filterFindByGroupIdEnrollmentStatus(
		long groupId, int enrollmentStatus) {

		return filterFindByGroupIdEnrollmentStatus(
			groupId, enrollmentStatus, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the Plan Enrollments that the user has permission to view where groupId = &#63; and enrollmentStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param enrollmentStatus the enrollment status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @return the range of matching Plan Enrollments that the user has permission to view
	 */
	@Override
	public List<PlanEnrollment> filterFindByGroupIdEnrollmentStatus(
		long groupId, int enrollmentStatus, int start, int end) {

		return filterFindByGroupIdEnrollmentStatus(
			groupId, enrollmentStatus, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments that the user has permissions to view where groupId = &#63; and enrollmentStatus = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param enrollmentStatus the enrollment status
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Plan Enrollments that the user has permission to view
	 */
	@Override
	public List<PlanEnrollment> filterFindByGroupIdEnrollmentStatus(
		long groupId, int enrollmentStatus, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByGroupIdEnrollmentStatus(
				groupId, enrollmentStatus, start, end, orderByComparator);
		}

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByFields().length * 2));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PLANENROLLMENT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PLANENROLLMENT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPIDENROLLMENTSTATUS_GROUPID_2);

		sb.append(_FINDER_COLUMN_GROUPIDENROLLMENTSTATUS_ENROLLMENTSTATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PLANENROLLMENT_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			if (getDB().isSupportsInlineDistinct()) {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator, true);
			}
			else {
				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_TABLE, orderByComparator, true);
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(PlanEnrollmentModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PlanEnrollmentModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PlanEnrollment.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, PlanEnrollmentImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, PlanEnrollmentImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(enrollmentStatus);

			return (List<PlanEnrollment>)QueryUtil.list(
				sqlQuery, getDialect(), start, end);
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * Returns the Plan Enrollments before and after the current Plan Enrollment in the ordered set of Plan Enrollments that the user has permission to view where groupId = &#63; and enrollmentStatus = &#63;.
	 *
	 * @param planEnrollmentId the primary key of the current Plan Enrollment
	 * @param groupId the group ID
	 * @param enrollmentStatus the enrollment status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a Plan Enrollment with the primary key could not be found
	 */
	@Override
	public PlanEnrollment[] filterFindByGroupIdEnrollmentStatus_PrevAndNext(
			long planEnrollmentId, long groupId, int enrollmentStatus,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByGroupIdEnrollmentStatus_PrevAndNext(
				planEnrollmentId, groupId, enrollmentStatus, orderByComparator);
		}

		PlanEnrollment planEnrollment = findByPrimaryKey(planEnrollmentId);

		Session session = null;

		try {
			session = openSession();

			PlanEnrollment[] array = new PlanEnrollmentImpl[3];

			array[0] = filterGetByGroupIdEnrollmentStatus_PrevAndNext(
				session, planEnrollment, groupId, enrollmentStatus,
				orderByComparator, true);

			array[1] = planEnrollment;

			array[2] = filterGetByGroupIdEnrollmentStatus_PrevAndNext(
				session, planEnrollment, groupId, enrollmentStatus,
				orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PlanEnrollment filterGetByGroupIdEnrollmentStatus_PrevAndNext(
		Session session, PlanEnrollment planEnrollment, long groupId,
		int enrollmentStatus,
		OrderByComparator<PlanEnrollment> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		if (getDB().isSupportsInlineDistinct()) {
			sb.append(_FILTER_SQL_SELECT_PLANENROLLMENT_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_PLANENROLLMENT_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPIDENROLLMENTSTATUS_GROUPID_2);

		sb.append(_FINDER_COLUMN_GROUPIDENROLLMENTSTATUS_ENROLLMENTSTATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_PLANENROLLMENT_NO_INLINE_DISTINCT_WHERE_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByConditionFields[i],
							true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByConditionFields[i],
							true));
				}

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				if (getDB().isSupportsInlineDistinct()) {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_ALIAS, orderByFields[i], true));
				}
				else {
					sb.append(
						getColumnName(
							_ORDER_BY_ENTITY_TABLE, orderByFields[i], true));
				}

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			if (getDB().isSupportsInlineDistinct()) {
				sb.append(PlanEnrollmentModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(PlanEnrollmentModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PlanEnrollment.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, PlanEnrollmentImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, PlanEnrollmentImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(enrollmentStatus);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						planEnrollment)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PlanEnrollment> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Plan Enrollments where groupId = &#63; and enrollmentStatus = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param enrollmentStatus the enrollment status
	 */
	@Override
	public void removeByGroupIdEnrollmentStatus(
		long groupId, int enrollmentStatus) {

		for (PlanEnrollment planEnrollment :
				findByGroupIdEnrollmentStatus(
					groupId, enrollmentStatus, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(planEnrollment);
		}
	}

	/**
	 * Returns the number of Plan Enrollments where groupId = &#63; and enrollmentStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param enrollmentStatus the enrollment status
	 * @return the number of matching Plan Enrollments
	 */
	@Override
	public int countByGroupIdEnrollmentStatus(
		long groupId, int enrollmentStatus) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			FinderPath finderPath = _finderPathCountByGroupIdEnrollmentStatus;

			Object[] finderArgs = new Object[] {groupId, enrollmentStatus};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_PLANENROLLMENT_WHERE);

				sb.append(_FINDER_COLUMN_GROUPIDENROLLMENTSTATUS_GROUPID_2);

				sb.append(
					_FINDER_COLUMN_GROUPIDENROLLMENTSTATUS_ENROLLMENTSTATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(enrollmentStatus);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	/**
	 * Returns the number of Plan Enrollments that the user has permission to view where groupId = &#63; and enrollmentStatus = &#63;.
	 *
	 * @param groupId the group ID
	 * @param enrollmentStatus the enrollment status
	 * @return the number of matching Plan Enrollments that the user has permission to view
	 */
	@Override
	public int filterCountByGroupIdEnrollmentStatus(
		long groupId, int enrollmentStatus) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByGroupIdEnrollmentStatus(groupId, enrollmentStatus);
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_PLANENROLLMENT_WHERE);

		sb.append(_FINDER_COLUMN_GROUPIDENROLLMENTSTATUS_GROUPID_2);

		sb.append(_FINDER_COLUMN_GROUPIDENROLLMENTSTATUS_ENROLLMENTSTATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), PlanEnrollment.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(enrollmentStatus);

			Long count = (Long)sqlQuery.uniqueResult();

			return count.intValue();
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	private static final String
		_FINDER_COLUMN_GROUPIDENROLLMENTSTATUS_GROUPID_2 =
			"planEnrollment.groupId = ? AND ";

	private static final String
		_FINDER_COLUMN_GROUPIDENROLLMENTSTATUS_ENROLLMENTSTATUS_2 =
			"planEnrollment.enrollmentStatus = ?";

	private FinderPath _finderPathWithPaginationFindByMemberUserId;
	private FinderPath _finderPathWithoutPaginationFindByMemberUserId;
	private FinderPath _finderPathCountByMemberUserId;

	/**
	 * Returns all the Plan Enrollments where memberUserId = &#63;.
	 *
	 * @param memberUserId the member user ID
	 * @return the matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByMemberUserId(long memberUserId) {
		return findByMemberUserId(
			memberUserId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Plan Enrollments where memberUserId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param memberUserId the member user ID
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @return the range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByMemberUserId(
		long memberUserId, int start, int end) {

		return findByMemberUserId(memberUserId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where memberUserId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param memberUserId the member user ID
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByMemberUserId(
		long memberUserId, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		return findByMemberUserId(
			memberUserId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where memberUserId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param memberUserId the member user ID
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByMemberUserId(
		long memberUserId, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByMemberUserId;
					finderArgs = new Object[] {memberUserId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByMemberUserId;
				finderArgs = new Object[] {
					memberUserId, start, end, orderByComparator
				};
			}

			List<PlanEnrollment> list = null;

			if (useFinderCache) {
				list = (List<PlanEnrollment>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (PlanEnrollment planEnrollment : list) {
						if (memberUserId != planEnrollment.getMemberUserId()) {
							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						3 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(3);
				}

				sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

				sb.append(_FINDER_COLUMN_MEMBERUSERID_MEMBERUSERID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(memberUserId);

					list = (List<PlanEnrollment>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first Plan Enrollment in the ordered set where memberUserId = &#63;.
	 *
	 * @param memberUserId the member user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByMemberUserId_First(
			long memberUserId,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByMemberUserId_First(
			memberUserId, orderByComparator);

		if (planEnrollment != null) {
			return planEnrollment;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("memberUserId=");
		sb.append(memberUserId);

		sb.append("}");

		throw new NoSuchPlanEnrollmentException(sb.toString());
	}

	/**
	 * Returns the first Plan Enrollment in the ordered set where memberUserId = &#63;.
	 *
	 * @param memberUserId the member user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByMemberUserId_First(
		long memberUserId,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		List<PlanEnrollment> list = findByMemberUserId(
			memberUserId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Plan Enrollment in the ordered set where memberUserId = &#63;.
	 *
	 * @param memberUserId the member user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByMemberUserId_Last(
			long memberUserId,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByMemberUserId_Last(
			memberUserId, orderByComparator);

		if (planEnrollment != null) {
			return planEnrollment;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("memberUserId=");
		sb.append(memberUserId);

		sb.append("}");

		throw new NoSuchPlanEnrollmentException(sb.toString());
	}

	/**
	 * Returns the last Plan Enrollment in the ordered set where memberUserId = &#63;.
	 *
	 * @param memberUserId the member user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByMemberUserId_Last(
		long memberUserId,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		int count = countByMemberUserId(memberUserId);

		if (count == 0) {
			return null;
		}

		List<PlanEnrollment> list = findByMemberUserId(
			memberUserId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Plan Enrollments before and after the current Plan Enrollment in the ordered set where memberUserId = &#63;.
	 *
	 * @param planEnrollmentId the primary key of the current Plan Enrollment
	 * @param memberUserId the member user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a Plan Enrollment with the primary key could not be found
	 */
	@Override
	public PlanEnrollment[] findByMemberUserId_PrevAndNext(
			long planEnrollmentId, long memberUserId,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = findByPrimaryKey(planEnrollmentId);

		Session session = null;

		try {
			session = openSession();

			PlanEnrollment[] array = new PlanEnrollmentImpl[3];

			array[0] = getByMemberUserId_PrevAndNext(
				session, planEnrollment, memberUserId, orderByComparator, true);

			array[1] = planEnrollment;

			array[2] = getByMemberUserId_PrevAndNext(
				session, planEnrollment, memberUserId, orderByComparator,
				false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PlanEnrollment getByMemberUserId_PrevAndNext(
		Session session, PlanEnrollment planEnrollment, long memberUserId,
		OrderByComparator<PlanEnrollment> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

		sb.append(_FINDER_COLUMN_MEMBERUSERID_MEMBERUSERID_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(memberUserId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						planEnrollment)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PlanEnrollment> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Plan Enrollments where memberUserId = &#63; from the database.
	 *
	 * @param memberUserId the member user ID
	 */
	@Override
	public void removeByMemberUserId(long memberUserId) {
		for (PlanEnrollment planEnrollment :
				findByMemberUserId(
					memberUserId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(planEnrollment);
		}
	}

	/**
	 * Returns the number of Plan Enrollments where memberUserId = &#63;.
	 *
	 * @param memberUserId the member user ID
	 * @return the number of matching Plan Enrollments
	 */
	@Override
	public int countByMemberUserId(long memberUserId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			FinderPath finderPath = _finderPathCountByMemberUserId;

			Object[] finderArgs = new Object[] {memberUserId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_PLANENROLLMENT_WHERE);

				sb.append(_FINDER_COLUMN_MEMBERUSERID_MEMBERUSERID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(memberUserId);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	private static final String _FINDER_COLUMN_MEMBERUSERID_MEMBERUSERID_2 =
		"planEnrollment.memberUserId = ?";

	private FinderPath _finderPathWithPaginationFindByMemberId;
	private FinderPath _finderPathWithoutPaginationFindByMemberId;
	private FinderPath _finderPathCountByMemberId;

	/**
	 * Returns all the Plan Enrollments where memberId = &#63;.
	 *
	 * @param memberId the member ID
	 * @return the matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByMemberId(String memberId) {
		return findByMemberId(
			memberId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Plan Enrollments where memberId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param memberId the member ID
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @return the range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByMemberId(
		String memberId, int start, int end) {

		return findByMemberId(memberId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where memberId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param memberId the member ID
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByMemberId(
		String memberId, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		return findByMemberId(memberId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where memberId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param memberId the member ID
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByMemberId(
		String memberId, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			memberId = Objects.toString(memberId, "");

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByMemberId;
					finderArgs = new Object[] {memberId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByMemberId;
				finderArgs = new Object[] {
					memberId, start, end, orderByComparator
				};
			}

			List<PlanEnrollment> list = null;

			if (useFinderCache) {
				list = (List<PlanEnrollment>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (PlanEnrollment planEnrollment : list) {
						if (!memberId.equals(planEnrollment.getMemberId())) {
							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						3 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(3);
				}

				sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

				boolean bindMemberId = false;

				if (memberId.isEmpty()) {
					sb.append(_FINDER_COLUMN_MEMBERID_MEMBERID_3);
				}
				else {
					bindMemberId = true;

					sb.append(_FINDER_COLUMN_MEMBERID_MEMBERID_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindMemberId) {
						queryPos.add(memberId);
					}

					list = (List<PlanEnrollment>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first Plan Enrollment in the ordered set where memberId = &#63;.
	 *
	 * @param memberId the member ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByMemberId_First(
			String memberId,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByMemberId_First(
			memberId, orderByComparator);

		if (planEnrollment != null) {
			return planEnrollment;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("memberId=");
		sb.append(memberId);

		sb.append("}");

		throw new NoSuchPlanEnrollmentException(sb.toString());
	}

	/**
	 * Returns the first Plan Enrollment in the ordered set where memberId = &#63;.
	 *
	 * @param memberId the member ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByMemberId_First(
		String memberId, OrderByComparator<PlanEnrollment> orderByComparator) {

		List<PlanEnrollment> list = findByMemberId(
			memberId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Plan Enrollment in the ordered set where memberId = &#63;.
	 *
	 * @param memberId the member ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByMemberId_Last(
			String memberId,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByMemberId_Last(
			memberId, orderByComparator);

		if (planEnrollment != null) {
			return planEnrollment;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("memberId=");
		sb.append(memberId);

		sb.append("}");

		throw new NoSuchPlanEnrollmentException(sb.toString());
	}

	/**
	 * Returns the last Plan Enrollment in the ordered set where memberId = &#63;.
	 *
	 * @param memberId the member ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByMemberId_Last(
		String memberId, OrderByComparator<PlanEnrollment> orderByComparator) {

		int count = countByMemberId(memberId);

		if (count == 0) {
			return null;
		}

		List<PlanEnrollment> list = findByMemberId(
			memberId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Plan Enrollments before and after the current Plan Enrollment in the ordered set where memberId = &#63;.
	 *
	 * @param planEnrollmentId the primary key of the current Plan Enrollment
	 * @param memberId the member ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a Plan Enrollment with the primary key could not be found
	 */
	@Override
	public PlanEnrollment[] findByMemberId_PrevAndNext(
			long planEnrollmentId, String memberId,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		memberId = Objects.toString(memberId, "");

		PlanEnrollment planEnrollment = findByPrimaryKey(planEnrollmentId);

		Session session = null;

		try {
			session = openSession();

			PlanEnrollment[] array = new PlanEnrollmentImpl[3];

			array[0] = getByMemberId_PrevAndNext(
				session, planEnrollment, memberId, orderByComparator, true);

			array[1] = planEnrollment;

			array[2] = getByMemberId_PrevAndNext(
				session, planEnrollment, memberId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PlanEnrollment getByMemberId_PrevAndNext(
		Session session, PlanEnrollment planEnrollment, String memberId,
		OrderByComparator<PlanEnrollment> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

		boolean bindMemberId = false;

		if (memberId.isEmpty()) {
			sb.append(_FINDER_COLUMN_MEMBERID_MEMBERID_3);
		}
		else {
			bindMemberId = true;

			sb.append(_FINDER_COLUMN_MEMBERID_MEMBERID_2);
		}

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		if (bindMemberId) {
			queryPos.add(memberId);
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						planEnrollment)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PlanEnrollment> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Plan Enrollments where memberId = &#63; from the database.
	 *
	 * @param memberId the member ID
	 */
	@Override
	public void removeByMemberId(String memberId) {
		for (PlanEnrollment planEnrollment :
				findByMemberId(
					memberId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(planEnrollment);
		}
	}

	/**
	 * Returns the number of Plan Enrollments where memberId = &#63;.
	 *
	 * @param memberId the member ID
	 * @return the number of matching Plan Enrollments
	 */
	@Override
	public int countByMemberId(String memberId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			memberId = Objects.toString(memberId, "");

			FinderPath finderPath = _finderPathCountByMemberId;

			Object[] finderArgs = new Object[] {memberId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_PLANENROLLMENT_WHERE);

				boolean bindMemberId = false;

				if (memberId.isEmpty()) {
					sb.append(_FINDER_COLUMN_MEMBERID_MEMBERID_3);
				}
				else {
					bindMemberId = true;

					sb.append(_FINDER_COLUMN_MEMBERID_MEMBERID_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindMemberId) {
						queryPos.add(memberId);
					}

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	private static final String _FINDER_COLUMN_MEMBERID_MEMBERID_2 =
		"planEnrollment.memberId = ?";

	private static final String _FINDER_COLUMN_MEMBERID_MEMBERID_3 =
		"(planEnrollment.memberId IS NULL OR planEnrollment.memberId = '')";

	private FinderPath _finderPathWithPaginationFindByInsurancePlan;
	private FinderPath _finderPathWithoutPaginationFindByInsurancePlan;
	private FinderPath _finderPathCountByInsurancePlan;

	/**
	 * Returns all the Plan Enrollments where insurancePlanId = &#63;.
	 *
	 * @param insurancePlanId the insurance plan ID
	 * @return the matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByInsurancePlan(long insurancePlanId) {
		return findByInsurancePlan(
			insurancePlanId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Plan Enrollments where insurancePlanId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param insurancePlanId the insurance plan ID
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @return the range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByInsurancePlan(
		long insurancePlanId, int start, int end) {

		return findByInsurancePlan(insurancePlanId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where insurancePlanId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param insurancePlanId the insurance plan ID
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByInsurancePlan(
		long insurancePlanId, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		return findByInsurancePlan(
			insurancePlanId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments where insurancePlanId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param insurancePlanId the insurance plan ID
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findByInsurancePlan(
		long insurancePlanId, int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath =
						_finderPathWithoutPaginationFindByInsurancePlan;
					finderArgs = new Object[] {insurancePlanId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByInsurancePlan;
				finderArgs = new Object[] {
					insurancePlanId, start, end, orderByComparator
				};
			}

			List<PlanEnrollment> list = null;

			if (useFinderCache) {
				list = (List<PlanEnrollment>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (PlanEnrollment planEnrollment : list) {
						if (insurancePlanId !=
								planEnrollment.getInsurancePlanId()) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						3 + (orderByComparator.getOrderByFields().length * 2));
				}
				else {
					sb = new StringBundler(3);
				}

				sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

				sb.append(_FINDER_COLUMN_INSURANCEPLAN_INSURANCEPLANID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(insurancePlanId);

					list = (List<PlanEnrollment>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Returns the first Plan Enrollment in the ordered set where insurancePlanId = &#63;.
	 *
	 * @param insurancePlanId the insurance plan ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByInsurancePlan_First(
			long insurancePlanId,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByInsurancePlan_First(
			insurancePlanId, orderByComparator);

		if (planEnrollment != null) {
			return planEnrollment;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("insurancePlanId=");
		sb.append(insurancePlanId);

		sb.append("}");

		throw new NoSuchPlanEnrollmentException(sb.toString());
	}

	/**
	 * Returns the first Plan Enrollment in the ordered set where insurancePlanId = &#63;.
	 *
	 * @param insurancePlanId the insurance plan ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByInsurancePlan_First(
		long insurancePlanId,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		List<PlanEnrollment> list = findByInsurancePlan(
			insurancePlanId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Plan Enrollment in the ordered set where insurancePlanId = &#63;.
	 *
	 * @param insurancePlanId the insurance plan ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByInsurancePlan_Last(
			long insurancePlanId,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByInsurancePlan_Last(
			insurancePlanId, orderByComparator);

		if (planEnrollment != null) {
			return planEnrollment;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("insurancePlanId=");
		sb.append(insurancePlanId);

		sb.append("}");

		throw new NoSuchPlanEnrollmentException(sb.toString());
	}

	/**
	 * Returns the last Plan Enrollment in the ordered set where insurancePlanId = &#63;.
	 *
	 * @param insurancePlanId the insurance plan ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByInsurancePlan_Last(
		long insurancePlanId,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		int count = countByInsurancePlan(insurancePlanId);

		if (count == 0) {
			return null;
		}

		List<PlanEnrollment> list = findByInsurancePlan(
			insurancePlanId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Plan Enrollments before and after the current Plan Enrollment in the ordered set where insurancePlanId = &#63;.
	 *
	 * @param planEnrollmentId the primary key of the current Plan Enrollment
	 * @param insurancePlanId the insurance plan ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a Plan Enrollment with the primary key could not be found
	 */
	@Override
	public PlanEnrollment[] findByInsurancePlan_PrevAndNext(
			long planEnrollmentId, long insurancePlanId,
			OrderByComparator<PlanEnrollment> orderByComparator)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = findByPrimaryKey(planEnrollmentId);

		Session session = null;

		try {
			session = openSession();

			PlanEnrollment[] array = new PlanEnrollmentImpl[3];

			array[0] = getByInsurancePlan_PrevAndNext(
				session, planEnrollment, insurancePlanId, orderByComparator,
				true);

			array[1] = planEnrollment;

			array[2] = getByInsurancePlan_PrevAndNext(
				session, planEnrollment, insurancePlanId, orderByComparator,
				false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected PlanEnrollment getByInsurancePlan_PrevAndNext(
		Session session, PlanEnrollment planEnrollment, long insurancePlanId,
		OrderByComparator<PlanEnrollment> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

		sb.append(_FINDER_COLUMN_INSURANCEPLAN_INSURANCEPLANID_2);

		if (orderByComparator != null) {
			String[] orderByConditionFields =
				orderByComparator.getOrderByConditionFields();

			if (orderByConditionFields.length > 0) {
				sb.append(WHERE_AND);
			}

			for (int i = 0; i < orderByConditionFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByConditionFields[i]);

				if ((i + 1) < orderByConditionFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN_HAS_NEXT);
					}
					else {
						sb.append(WHERE_LESSER_THAN_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(WHERE_GREATER_THAN);
					}
					else {
						sb.append(WHERE_LESSER_THAN);
					}
				}
			}

			sb.append(ORDER_BY_CLAUSE);

			String[] orderByFields = orderByComparator.getOrderByFields();

			for (int i = 0; i < orderByFields.length; i++) {
				sb.append(_ORDER_BY_ENTITY_ALIAS);
				sb.append(orderByFields[i]);

				if ((i + 1) < orderByFields.length) {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC_HAS_NEXT);
					}
					else {
						sb.append(ORDER_BY_DESC_HAS_NEXT);
					}
				}
				else {
					if (orderByComparator.isAscending() ^ previous) {
						sb.append(ORDER_BY_ASC);
					}
					else {
						sb.append(ORDER_BY_DESC);
					}
				}
			}
		}
		else {
			sb.append(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(insurancePlanId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						planEnrollment)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<PlanEnrollment> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Plan Enrollments where insurancePlanId = &#63; from the database.
	 *
	 * @param insurancePlanId the insurance plan ID
	 */
	@Override
	public void removeByInsurancePlan(long insurancePlanId) {
		for (PlanEnrollment planEnrollment :
				findByInsurancePlan(
					insurancePlanId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(planEnrollment);
		}
	}

	/**
	 * Returns the number of Plan Enrollments where insurancePlanId = &#63;.
	 *
	 * @param insurancePlanId the insurance plan ID
	 * @return the number of matching Plan Enrollments
	 */
	@Override
	public int countByInsurancePlan(long insurancePlanId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			FinderPath finderPath = _finderPathCountByInsurancePlan;

			Object[] finderArgs = new Object[] {insurancePlanId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_PLANENROLLMENT_WHERE);

				sb.append(_FINDER_COLUMN_INSURANCEPLAN_INSURANCEPLANID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(insurancePlanId);

					count = (Long)query.uniqueResult();

					finderCache.putResult(finderPath, finderArgs, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	private static final String _FINDER_COLUMN_INSURANCEPLAN_INSURANCEPLANID_2 =
		"planEnrollment.insurancePlanId = ?";

	private FinderPath _finderPathFetchByERC_G;

	/**
	 * Returns the Plan Enrollment where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchPlanEnrollmentException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment findByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByERC_G(
			externalReferenceCode, groupId);

		if (planEnrollment == null) {
			StringBundler sb = new StringBundler(6);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("externalReferenceCode=");
			sb.append(externalReferenceCode);

			sb.append(", groupId=");
			sb.append(groupId);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchPlanEnrollmentException(sb.toString());
		}

		return planEnrollment;
	}

	/**
	 * Returns the Plan Enrollment where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return fetchByERC_G(externalReferenceCode, groupId, true);
	}

	/**
	 * Returns the Plan Enrollment where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching Plan Enrollment, or <code>null</code> if a matching Plan Enrollment could not be found
	 */
	@Override
	public PlanEnrollment fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			externalReferenceCode = Objects.toString(externalReferenceCode, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {externalReferenceCode, groupId};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByERC_G, finderArgs, this);
			}

			if (result instanceof PlanEnrollment) {
				PlanEnrollment planEnrollment = (PlanEnrollment)result;

				if (!Objects.equals(
						externalReferenceCode,
						planEnrollment.getExternalReferenceCode()) ||
					(groupId != planEnrollment.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_PLANENROLLMENT_WHERE);

				boolean bindExternalReferenceCode = false;

				if (externalReferenceCode.isEmpty()) {
					sb.append(_FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_3);
				}
				else {
					bindExternalReferenceCode = true;

					sb.append(_FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_2);
				}

				sb.append(_FINDER_COLUMN_ERC_G_GROUPID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindExternalReferenceCode) {
						queryPos.add(externalReferenceCode);
					}

					queryPos.add(groupId);

					List<PlanEnrollment> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByERC_G, finderArgs, list);
						}
					}
					else {
						PlanEnrollment planEnrollment = list.get(0);

						result = planEnrollment;

						cacheResult(planEnrollment);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			if (result instanceof List<?>) {
				return null;
			}
			else {
				return (PlanEnrollment)result;
			}
		}
	}

	/**
	 * Removes the Plan Enrollment where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the Plan Enrollment that was removed
	 */
	@Override
	public PlanEnrollment removeByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = findByERC_G(
			externalReferenceCode, groupId);

		return remove(planEnrollment);
	}

	/**
	 * Returns the number of Plan Enrollments where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching Plan Enrollments
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		PlanEnrollment planEnrollment = fetchByERC_G(
			externalReferenceCode, groupId);

		if (planEnrollment == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_2 =
		"planEnrollment.externalReferenceCode = ? AND ";

	private static final String _FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_3 =
		"(planEnrollment.externalReferenceCode IS NULL OR planEnrollment.externalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_ERC_G_GROUPID_2 =
		"planEnrollment.groupId = ?";

	public PlanEnrollmentPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(PlanEnrollment.class);

		setModelImplClass(PlanEnrollmentImpl.class);
		setModelPKClass(long.class);

		setTable(PlanEnrollmentTable.INSTANCE);
	}

	/**
	 * Caches the Plan Enrollment in the entity cache if it is enabled.
	 *
	 * @param planEnrollment the Plan Enrollment
	 */
	@Override
	public void cacheResult(PlanEnrollment planEnrollment) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					planEnrollment.getCtCollectionId())) {

			entityCache.putResult(
				PlanEnrollmentImpl.class, planEnrollment.getPrimaryKey(),
				planEnrollment);

			finderCache.putResult(
				_finderPathFetchByUUID_G,
				new Object[] {
					planEnrollment.getUuid(), planEnrollment.getGroupId()
				},
				planEnrollment);

			finderCache.putResult(
				_finderPathFetchByERC_G,
				new Object[] {
					planEnrollment.getExternalReferenceCode(),
					planEnrollment.getGroupId()
				},
				planEnrollment);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the Plan Enrollments in the entity cache if it is enabled.
	 *
	 * @param planEnrollments the Plan Enrollments
	 */
	@Override
	public void cacheResult(List<PlanEnrollment> planEnrollments) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (planEnrollments.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (PlanEnrollment planEnrollment : planEnrollments) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						planEnrollment.getCtCollectionId())) {

				if (entityCache.getResult(
						PlanEnrollmentImpl.class,
						planEnrollment.getPrimaryKey()) == null) {

					cacheResult(planEnrollment);
				}
			}
		}
	}

	/**
	 * Clears the cache for all Plan Enrollments.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(PlanEnrollmentImpl.class);

		finderCache.clearCache(PlanEnrollmentImpl.class);
	}

	/**
	 * Clears the cache for the Plan Enrollment.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(PlanEnrollment planEnrollment) {
		entityCache.removeResult(PlanEnrollmentImpl.class, planEnrollment);
	}

	@Override
	public void clearCache(List<PlanEnrollment> planEnrollments) {
		for (PlanEnrollment planEnrollment : planEnrollments) {
			entityCache.removeResult(PlanEnrollmentImpl.class, planEnrollment);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(PlanEnrollmentImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(PlanEnrollmentImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		PlanEnrollmentModelImpl planEnrollmentModelImpl) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					planEnrollmentModelImpl.getCtCollectionId())) {

			Object[] args = new Object[] {
				planEnrollmentModelImpl.getUuid(),
				planEnrollmentModelImpl.getGroupId()
			};

			finderCache.putResult(
				_finderPathFetchByUUID_G, args, planEnrollmentModelImpl);

			args = new Object[] {
				planEnrollmentModelImpl.getExternalReferenceCode(),
				planEnrollmentModelImpl.getGroupId()
			};

			finderCache.putResult(
				_finderPathFetchByERC_G, args, planEnrollmentModelImpl);
		}
	}

	/**
	 * Creates a new Plan Enrollment with the primary key. Does not add the Plan Enrollment to the database.
	 *
	 * @param planEnrollmentId the primary key for the new Plan Enrollment
	 * @return the new Plan Enrollment
	 */
	@Override
	public PlanEnrollment create(long planEnrollmentId) {
		PlanEnrollment planEnrollment = new PlanEnrollmentImpl();

		planEnrollment.setNew(true);
		planEnrollment.setPrimaryKey(planEnrollmentId);

		String uuid = PortalUUIDUtil.generate();

		planEnrollment.setUuid(uuid);

		planEnrollment.setCompanyId(CompanyThreadLocal.getCompanyId());

		return planEnrollment;
	}

	/**
	 * Removes the Plan Enrollment with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param planEnrollmentId the primary key of the Plan Enrollment
	 * @return the Plan Enrollment that was removed
	 * @throws NoSuchPlanEnrollmentException if a Plan Enrollment with the primary key could not be found
	 */
	@Override
	public PlanEnrollment remove(long planEnrollmentId)
		throws NoSuchPlanEnrollmentException {

		return remove((Serializable)planEnrollmentId);
	}

	/**
	 * Removes the Plan Enrollment with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the Plan Enrollment
	 * @return the Plan Enrollment that was removed
	 * @throws NoSuchPlanEnrollmentException if a Plan Enrollment with the primary key could not be found
	 */
	@Override
	public PlanEnrollment remove(Serializable primaryKey)
		throws NoSuchPlanEnrollmentException {

		Session session = null;

		try {
			session = openSession();

			PlanEnrollment planEnrollment = (PlanEnrollment)session.get(
				PlanEnrollmentImpl.class, primaryKey);

			if (planEnrollment == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchPlanEnrollmentException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(planEnrollment);
		}
		catch (NoSuchPlanEnrollmentException noSuchEntityException) {
			throw noSuchEntityException;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	protected PlanEnrollment removeImpl(PlanEnrollment planEnrollment) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(planEnrollment)) {
				planEnrollment = (PlanEnrollment)session.get(
					PlanEnrollmentImpl.class,
					planEnrollment.getPrimaryKeyObj());
			}

			if ((planEnrollment != null) &&
				ctPersistenceHelper.isRemove(planEnrollment)) {

				session.delete(planEnrollment);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (planEnrollment != null) {
			clearCache(planEnrollment);
		}

		return planEnrollment;
	}

	@Override
	public PlanEnrollment updateImpl(PlanEnrollment planEnrollment) {
		boolean isNew = planEnrollment.isNew();

		if (!(planEnrollment instanceof PlanEnrollmentModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(planEnrollment.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					planEnrollment);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in planEnrollment proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom PlanEnrollment implementation " +
					planEnrollment.getClass());
		}

		PlanEnrollmentModelImpl planEnrollmentModelImpl =
			(PlanEnrollmentModelImpl)planEnrollment;

		if (Validator.isNull(planEnrollment.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			planEnrollment.setUuid(uuid);
		}

		if (Validator.isNull(planEnrollment.getExternalReferenceCode())) {
			planEnrollment.setExternalReferenceCode(planEnrollment.getUuid());
		}
		else {
			if (!Objects.equals(
					planEnrollmentModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					planEnrollment.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = planEnrollment.getCompanyId();

					long groupId = planEnrollment.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = planEnrollment.getPrimaryKey();
					}

					try {
						planEnrollment.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								PlanEnrollment.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								planEnrollment.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			PlanEnrollment ercPlanEnrollment = fetchByERC_G(
				planEnrollment.getExternalReferenceCode(),
				planEnrollment.getGroupId());

			if (isNew) {
				if (ercPlanEnrollment != null) {
					throw new DuplicatePlanEnrollmentExternalReferenceCodeException(
						"Duplicate Plan Enrollment with external reference code " +
							planEnrollment.getExternalReferenceCode() +
								" and group " + planEnrollment.getGroupId());
				}
			}
			else {
				if ((ercPlanEnrollment != null) &&
					(planEnrollment.getPlanEnrollmentId() !=
						ercPlanEnrollment.getPlanEnrollmentId())) {

					throw new DuplicatePlanEnrollmentExternalReferenceCodeException(
						"Duplicate Plan Enrollment with external reference code " +
							planEnrollment.getExternalReferenceCode() +
								" and group " + planEnrollment.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (planEnrollment.getCreateDate() == null)) {
			if (serviceContext == null) {
				planEnrollment.setCreateDate(date);
			}
			else {
				planEnrollment.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!planEnrollmentModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				planEnrollment.setModifiedDate(date);
			}
			else {
				planEnrollment.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(planEnrollment)) {
				if (!isNew) {
					session.evict(
						PlanEnrollmentImpl.class,
						planEnrollment.getPrimaryKeyObj());
				}

				session.save(planEnrollment);
			}
			else {
				planEnrollment = (PlanEnrollment)session.merge(planEnrollment);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			PlanEnrollmentImpl.class, planEnrollmentModelImpl, false, true);

		cacheUniqueFindersCache(planEnrollmentModelImpl);

		if (isNew) {
			planEnrollment.setNew(false);
		}

		planEnrollment.resetOriginalValues();

		return planEnrollment;
	}

	/**
	 * Returns the Plan Enrollment with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the Plan Enrollment
	 * @return the Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a Plan Enrollment with the primary key could not be found
	 */
	@Override
	public PlanEnrollment findByPrimaryKey(Serializable primaryKey)
		throws NoSuchPlanEnrollmentException {

		PlanEnrollment planEnrollment = fetchByPrimaryKey(primaryKey);

		if (planEnrollment == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchPlanEnrollmentException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return planEnrollment;
	}

	/**
	 * Returns the Plan Enrollment with the primary key or throws a <code>NoSuchPlanEnrollmentException</code> if it could not be found.
	 *
	 * @param planEnrollmentId the primary key of the Plan Enrollment
	 * @return the Plan Enrollment
	 * @throws NoSuchPlanEnrollmentException if a Plan Enrollment with the primary key could not be found
	 */
	@Override
	public PlanEnrollment findByPrimaryKey(long planEnrollmentId)
		throws NoSuchPlanEnrollmentException {

		return findByPrimaryKey((Serializable)planEnrollmentId);
	}

	/**
	 * Returns the Plan Enrollment with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the Plan Enrollment
	 * @return the Plan Enrollment, or <code>null</code> if a Plan Enrollment with the primary key could not be found
	 */
	@Override
	public PlanEnrollment fetchByPrimaryKey(Serializable primaryKey) {
		if (ctPersistenceHelper.isProductionMode(
				PlanEnrollment.class, primaryKey)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		PlanEnrollment planEnrollment = (PlanEnrollment)entityCache.getResult(
			PlanEnrollmentImpl.class, primaryKey);

		if (planEnrollment != null) {
			return planEnrollment;
		}

		Session session = null;

		try {
			session = openSession();

			planEnrollment = (PlanEnrollment)session.get(
				PlanEnrollmentImpl.class, primaryKey);

			if (planEnrollment != null) {
				cacheResult(planEnrollment);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return planEnrollment;
	}

	/**
	 * Returns the Plan Enrollment with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param planEnrollmentId the primary key of the Plan Enrollment
	 * @return the Plan Enrollment, or <code>null</code> if a Plan Enrollment with the primary key could not be found
	 */
	@Override
	public PlanEnrollment fetchByPrimaryKey(long planEnrollmentId) {
		return fetchByPrimaryKey((Serializable)planEnrollmentId);
	}

	@Override
	public Map<Serializable, PlanEnrollment> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (ctPersistenceHelper.isProductionMode(PlanEnrollment.class)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, PlanEnrollment> map =
			new HashMap<Serializable, PlanEnrollment>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			PlanEnrollment planEnrollment = fetchByPrimaryKey(primaryKey);

			if (planEnrollment != null) {
				map.put(primaryKey, planEnrollment);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
						PlanEnrollment.class, primaryKey)) {

				PlanEnrollment planEnrollment =
					(PlanEnrollment)entityCache.getResult(
						PlanEnrollmentImpl.class, primaryKey);

				if (planEnrollment == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, planEnrollment);
				}
			}
		}

		if (uncachedPrimaryKeys == null) {
			return map;
		}

		if ((databaseInMaxParameters > 0) &&
			(primaryKeys.size() > databaseInMaxParameters)) {

			Iterator<Serializable> iterator = primaryKeys.iterator();

			while (iterator.hasNext()) {
				Set<Serializable> page = new HashSet<>();

				for (int i = 0;
					 (i < databaseInMaxParameters) && iterator.hasNext(); i++) {

					page.add(iterator.next());
				}

				map.putAll(fetchByPrimaryKeys(page));
			}

			return map;
		}

		StringBundler sb = new StringBundler((primaryKeys.size() * 2) + 1);

		sb.append(getSelectSQL());
		sb.append(" WHERE ");
		sb.append(getPKDBName());
		sb.append(" IN (");

		for (Serializable primaryKey : primaryKeys) {
			sb.append((long)primaryKey);

			sb.append(",");
		}

		sb.setIndex(sb.index() - 1);

		sb.append(")");

		String sql = sb.toString();

		Session session = null;

		try {
			session = openSession();

			Query query = session.createQuery(sql);

			for (PlanEnrollment planEnrollment :
					(List<PlanEnrollment>)query.list()) {

				map.put(planEnrollment.getPrimaryKeyObj(), planEnrollment);

				cacheResult(planEnrollment);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return map;
	}

	/**
	 * Returns all the Plan Enrollments.
	 *
	 * @return the Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Plan Enrollments.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @return the range of Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findAll(
		int start, int end,
		OrderByComparator<PlanEnrollment> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Plan Enrollments.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>PlanEnrollmentModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of Plan Enrollments
	 * @param end the upper bound of the range of Plan Enrollments (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of Plan Enrollments
	 */
	@Override
	public List<PlanEnrollment> findAll(
		int start, int end, OrderByComparator<PlanEnrollment> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindAll;
					finderArgs = FINDER_ARGS_EMPTY;
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindAll;
				finderArgs = new Object[] {start, end, orderByComparator};
			}

			List<PlanEnrollment> list = null;

			if (useFinderCache) {
				list = (List<PlanEnrollment>)finderCache.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_PLANENROLLMENT);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_PLANENROLLMENT;

					sql = sql.concat(PlanEnrollmentModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<PlanEnrollment>)QueryUtil.list(
						query, getDialect(), start, end);

					cacheResult(list);

					if (useFinderCache) {
						finderCache.putResult(finderPath, finderArgs, list);
					}
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return list;
		}
	}

	/**
	 * Removes all the Plan Enrollments from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (PlanEnrollment planEnrollment : findAll()) {
			remove(planEnrollment);
		}
	}

	/**
	 * Returns the number of Plan Enrollments.
	 *
	 * @return the number of Plan Enrollments
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					PlanEnrollment.class)) {

			Long count = (Long)finderCache.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(
						_SQL_COUNT_PLANENROLLMENT);

					count = (Long)query.uniqueResult();

					finderCache.putResult(
						_finderPathCountAll, FINDER_ARGS_EMPTY, count);
				}
				catch (Exception exception) {
					throw processException(exception);
				}
				finally {
					closeSession(session);
				}
			}

			return count.intValue();
		}
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "planEnrollmentId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_PLANENROLLMENT;
	}

	@Override
	public Set<String> getCTColumnNames(
		CTColumnResolutionType ctColumnResolutionType) {

		return _ctColumnNamesMap.getOrDefault(
			ctColumnResolutionType, Collections.emptySet());
	}

	@Override
	public List<String> getMappingTableNames() {
		return _mappingTableNames;
	}

	@Override
	public Map<String, Integer> getTableColumnsMap() {
		return PlanEnrollmentModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CIBT_PlanEnrollment";
	}

	@Override
	public List<String[]> getUniqueIndexColumnNames() {
		return _uniqueIndexColumnNames;
	}

	private static final Map<CTColumnResolutionType, Set<String>>
		_ctColumnNamesMap = new EnumMap<CTColumnResolutionType, Set<String>>(
			CTColumnResolutionType.class);
	private static final List<String> _mappingTableNames =
		new ArrayList<String>();
	private static final List<String[]> _uniqueIndexColumnNames =
		new ArrayList<String[]>();

	static {
		Set<String> ctControlColumnNames = new HashSet<String>();
		Set<String> ctIgnoreColumnNames = new HashSet<String>();
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("uuid_");
		ctStrictColumnNames.add("externalReferenceCode");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("insurancePlanId");
		ctMergeColumnNames.add("memberUserId");
		ctMergeColumnNames.add("memberUserName");
		ctMergeColumnNames.add("memberId");
		ctMergeColumnNames.add("groupNumber");
		ctMergeColumnNames.add("startDate");
		ctMergeColumnNames.add("endDate");
		ctMergeColumnNames.add("enrollmentStatus");
		ctMergeColumnNames.add("notes");
		ctMergeColumnNames.add("status");
		ctMergeColumnNames.add("statusByUserId");
		ctMergeColumnNames.add("statusByUserName");
		ctMergeColumnNames.add("statusDate");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("planEnrollmentId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the Plan Enrollment persistence.
	 */
	@Activate
	public void activate() {
		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		_finderPathWithPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathWithoutPaginationFindAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathCountAll = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0], new String[0], false);

		_finderPathWithPaginationFindByUuid = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid",
			new String[] {
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"uuid_"}, true);

		_finderPathWithoutPaginationFindByUuid = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
			new String[] {String.class.getName()}, new String[] {"uuid_"},
			true);

		_finderPathCountByUuid = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
			new String[] {String.class.getName()}, new String[] {"uuid_"},
			false);

		_finderPathFetchByUUID_G = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByUUID_G",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"uuid_", "groupId"}, true);

		_finderPathWithPaginationFindByUuid_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid_C",
			new String[] {
				String.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"uuid_", "companyId"}, true);

		_finderPathWithoutPaginationFindByUuid_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid_C",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"uuid_", "companyId"}, true);

		_finderPathCountByUuid_C = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid_C",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"uuid_", "companyId"}, false);

		_finderPathWithPaginationFindByGroupId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGroupId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId"}, true);

		_finderPathWithoutPaginationFindByGroupId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByGroupId",
			new String[] {Long.class.getName()}, new String[] {"groupId"},
			true);

		_finderPathCountByGroupId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByGroupId",
			new String[] {Long.class.getName()}, new String[] {"groupId"},
			false);

		_finderPathWithPaginationFindByCompanyId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCompanyId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"companyId"}, true);

		_finderPathWithoutPaginationFindByCompanyId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCompanyId",
			new String[] {Long.class.getName()}, new String[] {"companyId"},
			true);

		_finderPathCountByCompanyId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCompanyId",
			new String[] {Long.class.getName()}, new String[] {"companyId"},
			false);

		_finderPathWithPaginationFindByG_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_S",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "status"}, true);

		_finderPathWithoutPaginationFindByG_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_S",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"groupId", "status"}, true);

		_finderPathCountByG_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_S",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"groupId", "status"}, false);

		_finderPathWithPaginationFindByG_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_NotS",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "status"}, true);

		_finderPathWithPaginationCountByG_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_NotS",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"groupId", "status"}, false);

		_finderPathWithPaginationFindByIP_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByIP_S",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"insurancePlanId", "status"}, true);

		_finderPathWithoutPaginationFindByIP_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByIP_S",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"insurancePlanId", "status"}, true);

		_finderPathCountByIP_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByIP_S",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"insurancePlanId", "status"}, false);

		_finderPathWithPaginationFindByIP_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByIP_NotS",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"insurancePlanId", "status"}, true);

		_finderPathWithPaginationCountByIP_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByIP_NotS",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"insurancePlanId", "status"}, false);

		_finderPathWithPaginationFindByG_IP_M_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_IP_M_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {
				"groupId", "insurancePlanId", "memberUserId", "status"
			},
			true);

		_finderPathWithoutPaginationFindByG_IP_M_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_IP_M_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName()
			},
			new String[] {
				"groupId", "insurancePlanId", "memberUserId", "status"
			},
			true);

		_finderPathCountByG_IP_M_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_IP_M_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName()
			},
			new String[] {
				"groupId", "insurancePlanId", "memberUserId", "status"
			},
			false);

		_finderPathWithPaginationFindByG_IP_M_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_IP_M_NotS",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {
				"groupId", "insurancePlanId", "memberUserId", "status"
			},
			true);

		_finderPathWithPaginationCountByG_IP_M_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_IP_M_NotS",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Long.class.getName(), Integer.class.getName()
			},
			new String[] {
				"groupId", "insurancePlanId", "memberUserId", "status"
			},
			false);

		_finderPathWithPaginationFindByC_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_S",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "status"}, true);

		_finderPathWithoutPaginationFindByC_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_S",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"companyId", "status"}, true);

		_finderPathCountByC_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_S",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"companyId", "status"}, false);

		_finderPathWithPaginationFindByC_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_NotS",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "status"}, true);

		_finderPathWithPaginationCountByC_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_NotS",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"companyId", "status"}, false);

		_finderPathWithPaginationFindByG_U_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_U_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "userId", "status"}, true);

		_finderPathWithoutPaginationFindByG_U_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_U_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "userId", "status"}, true);

		_finderPathCountByG_U_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_U_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "userId", "status"}, false);

		_finderPathWithPaginationCountByG_U_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_U_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "userId", "status"}, false);

		_finderPathWithPaginationFindByG_U_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_U_NotS",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "userId", "status"}, true);

		_finderPathWithPaginationCountByG_U_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_U_NotS",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "userId", "status"}, false);

		_finderPathWithPaginationFindByUserId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUserId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"userId"}, true);

		_finderPathWithoutPaginationFindByUserId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUserId",
			new String[] {Long.class.getName()}, new String[] {"userId"}, true);

		_finderPathCountByUserId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUserId",
			new String[] {Long.class.getName()}, new String[] {"userId"},
			false);

		_finderPathWithPaginationFindByUserIdEnrollmentStatus = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
			"findByUserIdEnrollmentStatus",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"userId", "enrollmentStatus"}, true);

		_finderPathWithoutPaginationFindByUserIdEnrollmentStatus =
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
				"findByUserIdEnrollmentStatus",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"userId", "enrollmentStatus"}, true);

		_finderPathCountByUserIdEnrollmentStatus = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByUserIdEnrollmentStatus",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"userId", "enrollmentStatus"}, false);

		_finderPathWithPaginationFindByGroupIdEnrollmentStatus = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
			"findByGroupIdEnrollmentStatus",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "enrollmentStatus"}, true);

		_finderPathWithoutPaginationFindByGroupIdEnrollmentStatus =
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
				"findByGroupIdEnrollmentStatus",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"groupId", "enrollmentStatus"}, true);

		_finderPathCountByGroupIdEnrollmentStatus = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByGroupIdEnrollmentStatus",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"groupId", "enrollmentStatus"}, false);

		_finderPathWithPaginationFindByMemberUserId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByMemberUserId",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"memberUserId"}, true);

		_finderPathWithoutPaginationFindByMemberUserId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByMemberUserId",
			new String[] {Long.class.getName()}, new String[] {"memberUserId"},
			true);

		_finderPathCountByMemberUserId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByMemberUserId",
			new String[] {Long.class.getName()}, new String[] {"memberUserId"},
			false);

		_finderPathWithPaginationFindByMemberId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByMemberId",
			new String[] {
				String.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"memberId"}, true);

		_finderPathWithoutPaginationFindByMemberId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByMemberId",
			new String[] {String.class.getName()}, new String[] {"memberId"},
			true);

		_finderPathCountByMemberId = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByMemberId",
			new String[] {String.class.getName()}, new String[] {"memberId"},
			false);

		_finderPathWithPaginationFindByInsurancePlan = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByInsurancePlan",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"insurancePlanId"}, true);

		_finderPathWithoutPaginationFindByInsurancePlan = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByInsurancePlan",
			new String[] {Long.class.getName()},
			new String[] {"insurancePlanId"}, true);

		_finderPathCountByInsurancePlan = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByInsurancePlan",
			new String[] {Long.class.getName()},
			new String[] {"insurancePlanId"}, false);

		_finderPathFetchByERC_G = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"externalReferenceCode", "groupId"}, true);

		PlanEnrollmentUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		PlanEnrollmentUtil.setPersistence(null);

		entityCache.removeCache(PlanEnrollmentImpl.class.getName());
	}

	@Override
	@Reference(
		target = CIBTPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = CIBTPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = CIBTPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected CTPersistenceHelper ctPersistenceHelper;

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_PLANENROLLMENT =
		"SELECT planEnrollment FROM PlanEnrollment planEnrollment";

	private static final String _SQL_SELECT_PLANENROLLMENT_WHERE =
		"SELECT planEnrollment FROM PlanEnrollment planEnrollment WHERE ";

	private static final String _SQL_COUNT_PLANENROLLMENT =
		"SELECT COUNT(planEnrollment) FROM PlanEnrollment planEnrollment";

	private static final String _SQL_COUNT_PLANENROLLMENT_WHERE =
		"SELECT COUNT(planEnrollment) FROM PlanEnrollment planEnrollment WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"planEnrollment.planEnrollmentId";

	private static final String _FILTER_SQL_SELECT_PLANENROLLMENT_WHERE =
		"SELECT DISTINCT {planEnrollment.*} FROM CIBT_PlanEnrollment planEnrollment WHERE ";

	private static final String
		_FILTER_SQL_SELECT_PLANENROLLMENT_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {CIBT_PlanEnrollment.*} FROM (SELECT DISTINCT planEnrollment.planEnrollmentId FROM CIBT_PlanEnrollment planEnrollment WHERE ";

	private static final String
		_FILTER_SQL_SELECT_PLANENROLLMENT_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN CIBT_PlanEnrollment ON TEMP_TABLE.planEnrollmentId = CIBT_PlanEnrollment.planEnrollmentId";

	private static final String _FILTER_SQL_COUNT_PLANENROLLMENT_WHERE =
		"SELECT COUNT(DISTINCT planEnrollment.planEnrollmentId) AS COUNT_VALUE FROM CIBT_PlanEnrollment planEnrollment WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "planEnrollment";

	private static final String _FILTER_ENTITY_TABLE = "CIBT_PlanEnrollment";

	private static final String _ORDER_BY_ENTITY_ALIAS = "planEnrollment.";

	private static final String _ORDER_BY_ENTITY_TABLE = "CIBT_PlanEnrollment.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No PlanEnrollment exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No PlanEnrollment exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		PlanEnrollmentPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}