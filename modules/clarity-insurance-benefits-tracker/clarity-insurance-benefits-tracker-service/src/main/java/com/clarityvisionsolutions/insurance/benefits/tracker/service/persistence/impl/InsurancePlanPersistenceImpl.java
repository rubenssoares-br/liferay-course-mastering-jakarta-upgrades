/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.clarityvisionsolutions.insurance.benefits.tracker.service.persistence.impl;

import com.clarityvisionsolutions.insurance.benefits.tracker.exception.DuplicateInsurancePlanExternalReferenceCodeException;
import com.clarityvisionsolutions.insurance.benefits.tracker.exception.NoSuchInsurancePlanException;
import com.clarityvisionsolutions.insurance.benefits.tracker.model.InsurancePlan;
import com.clarityvisionsolutions.insurance.benefits.tracker.model.InsurancePlanTable;
import com.clarityvisionsolutions.insurance.benefits.tracker.model.impl.InsurancePlanImpl;
import com.clarityvisionsolutions.insurance.benefits.tracker.model.impl.InsurancePlanModelImpl;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.persistence.InsurancePlanPersistence;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.persistence.InsurancePlanUtil;
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
 * The persistence implementation for the Insurance Plan service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = InsurancePlanPersistence.class)
public class InsurancePlanPersistenceImpl
	extends BasePersistenceImpl<InsurancePlan>
	implements InsurancePlanPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>InsurancePlanUtil</code> to access the Insurance Plan persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		InsurancePlanImpl.class.getName();

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
	 * Returns all the Insurance Plans where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Insurance Plans where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @return the range of matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Insurance Plans where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<InsurancePlan> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Insurance Plans where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<InsurancePlan> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					InsurancePlan.class)) {

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

			List<InsurancePlan> list = null;

			if (useFinderCache) {
				list = (List<InsurancePlan>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (InsurancePlan insurancePlan : list) {
						if (!uuid.equals(insurancePlan.getUuid())) {
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

				sb.append(_SQL_SELECT_INSURANCEPLAN_WHERE);

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
					sb.append(InsurancePlanModelImpl.ORDER_BY_JPQL);
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

					list = (List<InsurancePlan>)QueryUtil.list(
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
	 * Returns the first Insurance Plan in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Insurance Plan
	 * @throws NoSuchInsurancePlanException if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan findByUuid_First(
			String uuid, OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		InsurancePlan insurancePlan = fetchByUuid_First(
			uuid, orderByComparator);

		if (insurancePlan != null) {
			return insurancePlan;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchInsurancePlanException(sb.toString());
	}

	/**
	 * Returns the first Insurance Plan in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Insurance Plan, or <code>null</code> if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan fetchByUuid_First(
		String uuid, OrderByComparator<InsurancePlan> orderByComparator) {

		List<InsurancePlan> list = findByUuid(uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Insurance Plan in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Insurance Plan
	 * @throws NoSuchInsurancePlanException if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan findByUuid_Last(
			String uuid, OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		InsurancePlan insurancePlan = fetchByUuid_Last(uuid, orderByComparator);

		if (insurancePlan != null) {
			return insurancePlan;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchInsurancePlanException(sb.toString());
	}

	/**
	 * Returns the last Insurance Plan in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Insurance Plan, or <code>null</code> if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan fetchByUuid_Last(
		String uuid, OrderByComparator<InsurancePlan> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<InsurancePlan> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Insurance Plans before and after the current Insurance Plan in the ordered set where uuid = &#63;.
	 *
	 * @param insurancePlanId the primary key of the current Insurance Plan
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Insurance Plan
	 * @throws NoSuchInsurancePlanException if a Insurance Plan with the primary key could not be found
	 */
	@Override
	public InsurancePlan[] findByUuid_PrevAndNext(
			long insurancePlanId, String uuid,
			OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		uuid = Objects.toString(uuid, "");

		InsurancePlan insurancePlan = findByPrimaryKey(insurancePlanId);

		Session session = null;

		try {
			session = openSession();

			InsurancePlan[] array = new InsurancePlanImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, insurancePlan, uuid, orderByComparator, true);

			array[1] = insurancePlan;

			array[2] = getByUuid_PrevAndNext(
				session, insurancePlan, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected InsurancePlan getByUuid_PrevAndNext(
		Session session, InsurancePlan insurancePlan, String uuid,
		OrderByComparator<InsurancePlan> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_INSURANCEPLAN_WHERE);

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
			sb.append(InsurancePlanModelImpl.ORDER_BY_JPQL);
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
						insurancePlan)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<InsurancePlan> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Insurance Plans where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (InsurancePlan insurancePlan :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(insurancePlan);
		}
	}

	/**
	 * Returns the number of Insurance Plans where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching Insurance Plans
	 */
	@Override
	public int countByUuid(String uuid) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					InsurancePlan.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid;

			Object[] finderArgs = new Object[] {uuid};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_INSURANCEPLAN_WHERE);

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
		"insurancePlan.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(insurancePlan.uuid IS NULL OR insurancePlan.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;

	/**
	 * Returns the Insurance Plan where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchInsurancePlanException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching Insurance Plan
	 * @throws NoSuchInsurancePlanException if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan findByUUID_G(String uuid, long groupId)
		throws NoSuchInsurancePlanException {

		InsurancePlan insurancePlan = fetchByUUID_G(uuid, groupId);

		if (insurancePlan == null) {
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

			throw new NoSuchInsurancePlanException(sb.toString());
		}

		return insurancePlan;
	}

	/**
	 * Returns the Insurance Plan where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching Insurance Plan, or <code>null</code> if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the Insurance Plan where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching Insurance Plan, or <code>null</code> if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					InsurancePlan.class)) {

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

			if (result instanceof InsurancePlan) {
				InsurancePlan insurancePlan = (InsurancePlan)result;

				if (!Objects.equals(uuid, insurancePlan.getUuid()) ||
					(groupId != insurancePlan.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_INSURANCEPLAN_WHERE);

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

					List<InsurancePlan> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByUUID_G, finderArgs, list);
						}
					}
					else {
						InsurancePlan insurancePlan = list.get(0);

						result = insurancePlan;

						cacheResult(insurancePlan);
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
				return (InsurancePlan)result;
			}
		}
	}

	/**
	 * Removes the Insurance Plan where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the Insurance Plan that was removed
	 */
	@Override
	public InsurancePlan removeByUUID_G(String uuid, long groupId)
		throws NoSuchInsurancePlanException {

		InsurancePlan insurancePlan = findByUUID_G(uuid, groupId);

		return remove(insurancePlan);
	}

	/**
	 * Returns the number of Insurance Plans where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching Insurance Plans
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		InsurancePlan insurancePlan = fetchByUUID_G(uuid, groupId);

		if (insurancePlan == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_UUID_G_UUID_2 =
		"insurancePlan.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(insurancePlan.uuid IS NULL OR insurancePlan.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"insurancePlan.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the Insurance Plans where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Insurance Plans where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @return the range of matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Insurance Plans where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<InsurancePlan> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Insurance Plans where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<InsurancePlan> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					InsurancePlan.class)) {

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

			List<InsurancePlan> list = null;

			if (useFinderCache) {
				list = (List<InsurancePlan>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (InsurancePlan insurancePlan : list) {
						if (!uuid.equals(insurancePlan.getUuid()) ||
							(companyId != insurancePlan.getCompanyId())) {

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

				sb.append(_SQL_SELECT_INSURANCEPLAN_WHERE);

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
					sb.append(InsurancePlanModelImpl.ORDER_BY_JPQL);
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

					list = (List<InsurancePlan>)QueryUtil.list(
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
	 * Returns the first Insurance Plan in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Insurance Plan
	 * @throws NoSuchInsurancePlanException if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		InsurancePlan insurancePlan = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (insurancePlan != null) {
			return insurancePlan;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchInsurancePlanException(sb.toString());
	}

	/**
	 * Returns the first Insurance Plan in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Insurance Plan, or <code>null</code> if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<InsurancePlan> orderByComparator) {

		List<InsurancePlan> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Insurance Plan in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Insurance Plan
	 * @throws NoSuchInsurancePlanException if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		InsurancePlan insurancePlan = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (insurancePlan != null) {
			return insurancePlan;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchInsurancePlanException(sb.toString());
	}

	/**
	 * Returns the last Insurance Plan in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Insurance Plan, or <code>null</code> if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<InsurancePlan> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<InsurancePlan> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Insurance Plans before and after the current Insurance Plan in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param insurancePlanId the primary key of the current Insurance Plan
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Insurance Plan
	 * @throws NoSuchInsurancePlanException if a Insurance Plan with the primary key could not be found
	 */
	@Override
	public InsurancePlan[] findByUuid_C_PrevAndNext(
			long insurancePlanId, String uuid, long companyId,
			OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		uuid = Objects.toString(uuid, "");

		InsurancePlan insurancePlan = findByPrimaryKey(insurancePlanId);

		Session session = null;

		try {
			session = openSession();

			InsurancePlan[] array = new InsurancePlanImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, insurancePlan, uuid, companyId, orderByComparator,
				true);

			array[1] = insurancePlan;

			array[2] = getByUuid_C_PrevAndNext(
				session, insurancePlan, uuid, companyId, orderByComparator,
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

	protected InsurancePlan getByUuid_C_PrevAndNext(
		Session session, InsurancePlan insurancePlan, String uuid,
		long companyId, OrderByComparator<InsurancePlan> orderByComparator,
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

		sb.append(_SQL_SELECT_INSURANCEPLAN_WHERE);

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
			sb.append(InsurancePlanModelImpl.ORDER_BY_JPQL);
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
						insurancePlan)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<InsurancePlan> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Insurance Plans where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (InsurancePlan insurancePlan :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(insurancePlan);
		}
	}

	/**
	 * Returns the number of Insurance Plans where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching Insurance Plans
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					InsurancePlan.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid_C;

			Object[] finderArgs = new Object[] {uuid, companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_INSURANCEPLAN_WHERE);

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
		"insurancePlan.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(insurancePlan.uuid IS NULL OR insurancePlan.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"insurancePlan.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByGroupId;
	private FinderPath _finderPathWithoutPaginationFindByGroupId;
	private FinderPath _finderPathCountByGroupId;

	/**
	 * Returns all the Insurance Plans where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Insurance Plans where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @return the range of matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByGroupId(long groupId, int start, int end) {
		return findByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Insurance Plans where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<InsurancePlan> orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Insurance Plans where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<InsurancePlan> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					InsurancePlan.class)) {

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

			List<InsurancePlan> list = null;

			if (useFinderCache) {
				list = (List<InsurancePlan>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (InsurancePlan insurancePlan : list) {
						if (groupId != insurancePlan.getGroupId()) {
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

				sb.append(_SQL_SELECT_INSURANCEPLAN_WHERE);

				sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(InsurancePlanModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					list = (List<InsurancePlan>)QueryUtil.list(
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
	 * Returns the first Insurance Plan in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Insurance Plan
	 * @throws NoSuchInsurancePlanException if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan findByGroupId_First(
			long groupId, OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		InsurancePlan insurancePlan = fetchByGroupId_First(
			groupId, orderByComparator);

		if (insurancePlan != null) {
			return insurancePlan;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchInsurancePlanException(sb.toString());
	}

	/**
	 * Returns the first Insurance Plan in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Insurance Plan, or <code>null</code> if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan fetchByGroupId_First(
		long groupId, OrderByComparator<InsurancePlan> orderByComparator) {

		List<InsurancePlan> list = findByGroupId(
			groupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Insurance Plan in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Insurance Plan
	 * @throws NoSuchInsurancePlanException if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan findByGroupId_Last(
			long groupId, OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		InsurancePlan insurancePlan = fetchByGroupId_Last(
			groupId, orderByComparator);

		if (insurancePlan != null) {
			return insurancePlan;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchInsurancePlanException(sb.toString());
	}

	/**
	 * Returns the last Insurance Plan in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Insurance Plan, or <code>null</code> if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan fetchByGroupId_Last(
		long groupId, OrderByComparator<InsurancePlan> orderByComparator) {

		int count = countByGroupId(groupId);

		if (count == 0) {
			return null;
		}

		List<InsurancePlan> list = findByGroupId(
			groupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Insurance Plans before and after the current Insurance Plan in the ordered set where groupId = &#63;.
	 *
	 * @param insurancePlanId the primary key of the current Insurance Plan
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Insurance Plan
	 * @throws NoSuchInsurancePlanException if a Insurance Plan with the primary key could not be found
	 */
	@Override
	public InsurancePlan[] findByGroupId_PrevAndNext(
			long insurancePlanId, long groupId,
			OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		InsurancePlan insurancePlan = findByPrimaryKey(insurancePlanId);

		Session session = null;

		try {
			session = openSession();

			InsurancePlan[] array = new InsurancePlanImpl[3];

			array[0] = getByGroupId_PrevAndNext(
				session, insurancePlan, groupId, orderByComparator, true);

			array[1] = insurancePlan;

			array[2] = getByGroupId_PrevAndNext(
				session, insurancePlan, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected InsurancePlan getByGroupId_PrevAndNext(
		Session session, InsurancePlan insurancePlan, long groupId,
		OrderByComparator<InsurancePlan> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_INSURANCEPLAN_WHERE);

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
			sb.append(InsurancePlanModelImpl.ORDER_BY_JPQL);
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
						insurancePlan)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<InsurancePlan> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the Insurance Plans that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching Insurance Plans that the user has permission to view
	 */
	@Override
	public List<InsurancePlan> filterFindByGroupId(long groupId) {
		return filterFindByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Insurance Plans that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @return the range of matching Insurance Plans that the user has permission to view
	 */
	@Override
	public List<InsurancePlan> filterFindByGroupId(
		long groupId, int start, int end) {

		return filterFindByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Insurance Plans that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Insurance Plans that the user has permission to view
	 */
	@Override
	public List<InsurancePlan> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<InsurancePlan> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_INSURANCEPLAN_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_INSURANCEPLAN_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_INSURANCEPLAN_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(InsurancePlanModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(InsurancePlanModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), InsurancePlan.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, InsurancePlanImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, InsurancePlanImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			return (List<InsurancePlan>)QueryUtil.list(
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
	 * Returns the Insurance Plans before and after the current Insurance Plan in the ordered set of Insurance Plans that the user has permission to view where groupId = &#63;.
	 *
	 * @param insurancePlanId the primary key of the current Insurance Plan
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Insurance Plan
	 * @throws NoSuchInsurancePlanException if a Insurance Plan with the primary key could not be found
	 */
	@Override
	public InsurancePlan[] filterFindByGroupId_PrevAndNext(
			long insurancePlanId, long groupId,
			OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByGroupId_PrevAndNext(
				insurancePlanId, groupId, orderByComparator);
		}

		InsurancePlan insurancePlan = findByPrimaryKey(insurancePlanId);

		Session session = null;

		try {
			session = openSession();

			InsurancePlan[] array = new InsurancePlanImpl[3];

			array[0] = filterGetByGroupId_PrevAndNext(
				session, insurancePlan, groupId, orderByComparator, true);

			array[1] = insurancePlan;

			array[2] = filterGetByGroupId_PrevAndNext(
				session, insurancePlan, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected InsurancePlan filterGetByGroupId_PrevAndNext(
		Session session, InsurancePlan insurancePlan, long groupId,
		OrderByComparator<InsurancePlan> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_INSURANCEPLAN_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_INSURANCEPLAN_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_INSURANCEPLAN_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(InsurancePlanModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(InsurancePlanModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), InsurancePlan.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, InsurancePlanImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, InsurancePlanImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						insurancePlan)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<InsurancePlan> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Insurance Plans where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		for (InsurancePlan insurancePlan :
				findByGroupId(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(insurancePlan);
		}
	}

	/**
	 * Returns the number of Insurance Plans where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching Insurance Plans
	 */
	@Override
	public int countByGroupId(long groupId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					InsurancePlan.class)) {

			FinderPath finderPath = _finderPathCountByGroupId;

			Object[] finderArgs = new Object[] {groupId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_INSURANCEPLAN_WHERE);

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
	 * Returns the number of Insurance Plans that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching Insurance Plans that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByGroupId(groupId);
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_INSURANCEPLAN_WHERE);

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), InsurancePlan.class.getName(),
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
		"insurancePlan.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByCompanyIdActive;
	private FinderPath _finderPathWithoutPaginationFindByCompanyIdActive;
	private FinderPath _finderPathCountByCompanyIdActive;

	/**
	 * Returns all the Insurance Plans where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @return the matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByCompanyIdActive(
		long companyId, boolean active) {

		return findByCompanyIdActive(
			companyId, active, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Insurance Plans where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @return the range of matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByCompanyIdActive(
		long companyId, boolean active, int start, int end) {

		return findByCompanyIdActive(companyId, active, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Insurance Plans where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByCompanyIdActive(
		long companyId, boolean active, int start, int end,
		OrderByComparator<InsurancePlan> orderByComparator) {

		return findByCompanyIdActive(
			companyId, active, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Insurance Plans where companyId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByCompanyIdActive(
		long companyId, boolean active, int start, int end,
		OrderByComparator<InsurancePlan> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					InsurancePlan.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath =
						_finderPathWithoutPaginationFindByCompanyIdActive;
					finderArgs = new Object[] {companyId, active};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByCompanyIdActive;
				finderArgs = new Object[] {
					companyId, active, start, end, orderByComparator
				};
			}

			List<InsurancePlan> list = null;

			if (useFinderCache) {
				list = (List<InsurancePlan>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (InsurancePlan insurancePlan : list) {
						if ((companyId != insurancePlan.getCompanyId()) ||
							(active != insurancePlan.isActive())) {

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

				sb.append(_SQL_SELECT_INSURANCEPLAN_WHERE);

				sb.append(_FINDER_COLUMN_COMPANYIDACTIVE_COMPANYID_2);

				sb.append(_FINDER_COLUMN_COMPANYIDACTIVE_ACTIVE_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(InsurancePlanModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(active);

					list = (List<InsurancePlan>)QueryUtil.list(
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
	 * Returns the first Insurance Plan in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Insurance Plan
	 * @throws NoSuchInsurancePlanException if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan findByCompanyIdActive_First(
			long companyId, boolean active,
			OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		InsurancePlan insurancePlan = fetchByCompanyIdActive_First(
			companyId, active, orderByComparator);

		if (insurancePlan != null) {
			return insurancePlan;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", active=");
		sb.append(active);

		sb.append("}");

		throw new NoSuchInsurancePlanException(sb.toString());
	}

	/**
	 * Returns the first Insurance Plan in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Insurance Plan, or <code>null</code> if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan fetchByCompanyIdActive_First(
		long companyId, boolean active,
		OrderByComparator<InsurancePlan> orderByComparator) {

		List<InsurancePlan> list = findByCompanyIdActive(
			companyId, active, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Insurance Plan in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Insurance Plan
	 * @throws NoSuchInsurancePlanException if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan findByCompanyIdActive_Last(
			long companyId, boolean active,
			OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		InsurancePlan insurancePlan = fetchByCompanyIdActive_Last(
			companyId, active, orderByComparator);

		if (insurancePlan != null) {
			return insurancePlan;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", active=");
		sb.append(active);

		sb.append("}");

		throw new NoSuchInsurancePlanException(sb.toString());
	}

	/**
	 * Returns the last Insurance Plan in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Insurance Plan, or <code>null</code> if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan fetchByCompanyIdActive_Last(
		long companyId, boolean active,
		OrderByComparator<InsurancePlan> orderByComparator) {

		int count = countByCompanyIdActive(companyId, active);

		if (count == 0) {
			return null;
		}

		List<InsurancePlan> list = findByCompanyIdActive(
			companyId, active, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Insurance Plans before and after the current Insurance Plan in the ordered set where companyId = &#63; and active = &#63;.
	 *
	 * @param insurancePlanId the primary key of the current Insurance Plan
	 * @param companyId the company ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Insurance Plan
	 * @throws NoSuchInsurancePlanException if a Insurance Plan with the primary key could not be found
	 */
	@Override
	public InsurancePlan[] findByCompanyIdActive_PrevAndNext(
			long insurancePlanId, long companyId, boolean active,
			OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		InsurancePlan insurancePlan = findByPrimaryKey(insurancePlanId);

		Session session = null;

		try {
			session = openSession();

			InsurancePlan[] array = new InsurancePlanImpl[3];

			array[0] = getByCompanyIdActive_PrevAndNext(
				session, insurancePlan, companyId, active, orderByComparator,
				true);

			array[1] = insurancePlan;

			array[2] = getByCompanyIdActive_PrevAndNext(
				session, insurancePlan, companyId, active, orderByComparator,
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

	protected InsurancePlan getByCompanyIdActive_PrevAndNext(
		Session session, InsurancePlan insurancePlan, long companyId,
		boolean active, OrderByComparator<InsurancePlan> orderByComparator,
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

		sb.append(_SQL_SELECT_INSURANCEPLAN_WHERE);

		sb.append(_FINDER_COLUMN_COMPANYIDACTIVE_COMPANYID_2);

		sb.append(_FINDER_COLUMN_COMPANYIDACTIVE_ACTIVE_2);

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
			sb.append(InsurancePlanModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		queryPos.add(active);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						insurancePlan)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<InsurancePlan> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Insurance Plans where companyId = &#63; and active = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 */
	@Override
	public void removeByCompanyIdActive(long companyId, boolean active) {
		for (InsurancePlan insurancePlan :
				findByCompanyIdActive(
					companyId, active, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(insurancePlan);
		}
	}

	/**
	 * Returns the number of Insurance Plans where companyId = &#63; and active = &#63;.
	 *
	 * @param companyId the company ID
	 * @param active the active
	 * @return the number of matching Insurance Plans
	 */
	@Override
	public int countByCompanyIdActive(long companyId, boolean active) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					InsurancePlan.class)) {

			FinderPath finderPath = _finderPathCountByCompanyIdActive;

			Object[] finderArgs = new Object[] {companyId, active};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_INSURANCEPLAN_WHERE);

				sb.append(_FINDER_COLUMN_COMPANYIDACTIVE_COMPANYID_2);

				sb.append(_FINDER_COLUMN_COMPANYIDACTIVE_ACTIVE_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(active);

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

	private static final String _FINDER_COLUMN_COMPANYIDACTIVE_COMPANYID_2 =
		"insurancePlan.companyId = ? AND ";

	private static final String _FINDER_COLUMN_COMPANYIDACTIVE_ACTIVE_2 =
		"insurancePlan.active = ?";

	private FinderPath _finderPathWithPaginationFindByGroupIdActive;
	private FinderPath _finderPathWithoutPaginationFindByGroupIdActive;
	private FinderPath _finderPathCountByGroupIdActive;

	/**
	 * Returns all the Insurance Plans where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @return the matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByGroupIdActive(
		long groupId, boolean active) {

		return findByGroupIdActive(
			groupId, active, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Insurance Plans where groupId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @return the range of matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByGroupIdActive(
		long groupId, boolean active, int start, int end) {

		return findByGroupIdActive(groupId, active, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Insurance Plans where groupId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByGroupIdActive(
		long groupId, boolean active, int start, int end,
		OrderByComparator<InsurancePlan> orderByComparator) {

		return findByGroupIdActive(
			groupId, active, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Insurance Plans where groupId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByGroupIdActive(
		long groupId, boolean active, int start, int end,
		OrderByComparator<InsurancePlan> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					InsurancePlan.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath =
						_finderPathWithoutPaginationFindByGroupIdActive;
					finderArgs = new Object[] {groupId, active};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByGroupIdActive;
				finderArgs = new Object[] {
					groupId, active, start, end, orderByComparator
				};
			}

			List<InsurancePlan> list = null;

			if (useFinderCache) {
				list = (List<InsurancePlan>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (InsurancePlan insurancePlan : list) {
						if ((groupId != insurancePlan.getGroupId()) ||
							(active != insurancePlan.isActive())) {

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

				sb.append(_SQL_SELECT_INSURANCEPLAN_WHERE);

				sb.append(_FINDER_COLUMN_GROUPIDACTIVE_GROUPID_2);

				sb.append(_FINDER_COLUMN_GROUPIDACTIVE_ACTIVE_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(InsurancePlanModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(active);

					list = (List<InsurancePlan>)QueryUtil.list(
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
	 * Returns the first Insurance Plan in the ordered set where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Insurance Plan
	 * @throws NoSuchInsurancePlanException if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan findByGroupIdActive_First(
			long groupId, boolean active,
			OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		InsurancePlan insurancePlan = fetchByGroupIdActive_First(
			groupId, active, orderByComparator);

		if (insurancePlan != null) {
			return insurancePlan;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", active=");
		sb.append(active);

		sb.append("}");

		throw new NoSuchInsurancePlanException(sb.toString());
	}

	/**
	 * Returns the first Insurance Plan in the ordered set where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Insurance Plan, or <code>null</code> if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan fetchByGroupIdActive_First(
		long groupId, boolean active,
		OrderByComparator<InsurancePlan> orderByComparator) {

		List<InsurancePlan> list = findByGroupIdActive(
			groupId, active, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Insurance Plan in the ordered set where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Insurance Plan
	 * @throws NoSuchInsurancePlanException if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan findByGroupIdActive_Last(
			long groupId, boolean active,
			OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		InsurancePlan insurancePlan = fetchByGroupIdActive_Last(
			groupId, active, orderByComparator);

		if (insurancePlan != null) {
			return insurancePlan;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", active=");
		sb.append(active);

		sb.append("}");

		throw new NoSuchInsurancePlanException(sb.toString());
	}

	/**
	 * Returns the last Insurance Plan in the ordered set where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Insurance Plan, or <code>null</code> if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan fetchByGroupIdActive_Last(
		long groupId, boolean active,
		OrderByComparator<InsurancePlan> orderByComparator) {

		int count = countByGroupIdActive(groupId, active);

		if (count == 0) {
			return null;
		}

		List<InsurancePlan> list = findByGroupIdActive(
			groupId, active, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Insurance Plans before and after the current Insurance Plan in the ordered set where groupId = &#63; and active = &#63;.
	 *
	 * @param insurancePlanId the primary key of the current Insurance Plan
	 * @param groupId the group ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Insurance Plan
	 * @throws NoSuchInsurancePlanException if a Insurance Plan with the primary key could not be found
	 */
	@Override
	public InsurancePlan[] findByGroupIdActive_PrevAndNext(
			long insurancePlanId, long groupId, boolean active,
			OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		InsurancePlan insurancePlan = findByPrimaryKey(insurancePlanId);

		Session session = null;

		try {
			session = openSession();

			InsurancePlan[] array = new InsurancePlanImpl[3];

			array[0] = getByGroupIdActive_PrevAndNext(
				session, insurancePlan, groupId, active, orderByComparator,
				true);

			array[1] = insurancePlan;

			array[2] = getByGroupIdActive_PrevAndNext(
				session, insurancePlan, groupId, active, orderByComparator,
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

	protected InsurancePlan getByGroupIdActive_PrevAndNext(
		Session session, InsurancePlan insurancePlan, long groupId,
		boolean active, OrderByComparator<InsurancePlan> orderByComparator,
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

		sb.append(_SQL_SELECT_INSURANCEPLAN_WHERE);

		sb.append(_FINDER_COLUMN_GROUPIDACTIVE_GROUPID_2);

		sb.append(_FINDER_COLUMN_GROUPIDACTIVE_ACTIVE_2);

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
			sb.append(InsurancePlanModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(active);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						insurancePlan)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<InsurancePlan> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the Insurance Plans that the user has permission to view where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @return the matching Insurance Plans that the user has permission to view
	 */
	@Override
	public List<InsurancePlan> filterFindByGroupIdActive(
		long groupId, boolean active) {

		return filterFindByGroupIdActive(
			groupId, active, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Insurance Plans that the user has permission to view where groupId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @return the range of matching Insurance Plans that the user has permission to view
	 */
	@Override
	public List<InsurancePlan> filterFindByGroupIdActive(
		long groupId, boolean active, int start, int end) {

		return filterFindByGroupIdActive(groupId, active, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Insurance Plans that the user has permissions to view where groupId = &#63; and active = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Insurance Plans that the user has permission to view
	 */
	@Override
	public List<InsurancePlan> filterFindByGroupIdActive(
		long groupId, boolean active, int start, int end,
		OrderByComparator<InsurancePlan> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByGroupIdActive(
				groupId, active, start, end, orderByComparator);
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
			sb.append(_FILTER_SQL_SELECT_INSURANCEPLAN_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_INSURANCEPLAN_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPIDACTIVE_GROUPID_2);

		sb.append(_FINDER_COLUMN_GROUPIDACTIVE_ACTIVE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_INSURANCEPLAN_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(InsurancePlanModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(InsurancePlanModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), InsurancePlan.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, InsurancePlanImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, InsurancePlanImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(active);

			return (List<InsurancePlan>)QueryUtil.list(
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
	 * Returns the Insurance Plans before and after the current Insurance Plan in the ordered set of Insurance Plans that the user has permission to view where groupId = &#63; and active = &#63;.
	 *
	 * @param insurancePlanId the primary key of the current Insurance Plan
	 * @param groupId the group ID
	 * @param active the active
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Insurance Plan
	 * @throws NoSuchInsurancePlanException if a Insurance Plan with the primary key could not be found
	 */
	@Override
	public InsurancePlan[] filterFindByGroupIdActive_PrevAndNext(
			long insurancePlanId, long groupId, boolean active,
			OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByGroupIdActive_PrevAndNext(
				insurancePlanId, groupId, active, orderByComparator);
		}

		InsurancePlan insurancePlan = findByPrimaryKey(insurancePlanId);

		Session session = null;

		try {
			session = openSession();

			InsurancePlan[] array = new InsurancePlanImpl[3];

			array[0] = filterGetByGroupIdActive_PrevAndNext(
				session, insurancePlan, groupId, active, orderByComparator,
				true);

			array[1] = insurancePlan;

			array[2] = filterGetByGroupIdActive_PrevAndNext(
				session, insurancePlan, groupId, active, orderByComparator,
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

	protected InsurancePlan filterGetByGroupIdActive_PrevAndNext(
		Session session, InsurancePlan insurancePlan, long groupId,
		boolean active, OrderByComparator<InsurancePlan> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_INSURANCEPLAN_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_INSURANCEPLAN_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPIDACTIVE_GROUPID_2);

		sb.append(_FINDER_COLUMN_GROUPIDACTIVE_ACTIVE_2_SQL);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_INSURANCEPLAN_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(InsurancePlanModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(InsurancePlanModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), InsurancePlan.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, InsurancePlanImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, InsurancePlanImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(active);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						insurancePlan)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<InsurancePlan> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Insurance Plans where groupId = &#63; and active = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 */
	@Override
	public void removeByGroupIdActive(long groupId, boolean active) {
		for (InsurancePlan insurancePlan :
				findByGroupIdActive(
					groupId, active, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(insurancePlan);
		}
	}

	/**
	 * Returns the number of Insurance Plans where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @return the number of matching Insurance Plans
	 */
	@Override
	public int countByGroupIdActive(long groupId, boolean active) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					InsurancePlan.class)) {

			FinderPath finderPath = _finderPathCountByGroupIdActive;

			Object[] finderArgs = new Object[] {groupId, active};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_INSURANCEPLAN_WHERE);

				sb.append(_FINDER_COLUMN_GROUPIDACTIVE_GROUPID_2);

				sb.append(_FINDER_COLUMN_GROUPIDACTIVE_ACTIVE_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(active);

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
	 * Returns the number of Insurance Plans that the user has permission to view where groupId = &#63; and active = &#63;.
	 *
	 * @param groupId the group ID
	 * @param active the active
	 * @return the number of matching Insurance Plans that the user has permission to view
	 */
	@Override
	public int filterCountByGroupIdActive(long groupId, boolean active) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByGroupIdActive(groupId, active);
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_INSURANCEPLAN_WHERE);

		sb.append(_FINDER_COLUMN_GROUPIDACTIVE_GROUPID_2);

		sb.append(_FINDER_COLUMN_GROUPIDACTIVE_ACTIVE_2_SQL);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), InsurancePlan.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(active);

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

	private static final String _FINDER_COLUMN_GROUPIDACTIVE_GROUPID_2 =
		"insurancePlan.groupId = ? AND ";

	private static final String _FINDER_COLUMN_GROUPIDACTIVE_ACTIVE_2 =
		"insurancePlan.active = ?";

	private static final String _FINDER_COLUMN_GROUPIDACTIVE_ACTIVE_2_SQL =
		"insurancePlan.active_ = ?";

	private FinderPath _finderPathWithPaginationFindByCompanyId;
	private FinderPath _finderPathWithoutPaginationFindByCompanyId;
	private FinderPath _finderPathCountByCompanyId;

	/**
	 * Returns all the Insurance Plans where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Insurance Plans where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @return the range of matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Insurance Plans where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<InsurancePlan> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Insurance Plans where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<InsurancePlan> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					InsurancePlan.class)) {

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

			List<InsurancePlan> list = null;

			if (useFinderCache) {
				list = (List<InsurancePlan>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (InsurancePlan insurancePlan : list) {
						if (companyId != insurancePlan.getCompanyId()) {
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

				sb.append(_SQL_SELECT_INSURANCEPLAN_WHERE);

				sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(InsurancePlanModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					list = (List<InsurancePlan>)QueryUtil.list(
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
	 * Returns the first Insurance Plan in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Insurance Plan
	 * @throws NoSuchInsurancePlanException if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan findByCompanyId_First(
			long companyId, OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		InsurancePlan insurancePlan = fetchByCompanyId_First(
			companyId, orderByComparator);

		if (insurancePlan != null) {
			return insurancePlan;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchInsurancePlanException(sb.toString());
	}

	/**
	 * Returns the first Insurance Plan in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Insurance Plan, or <code>null</code> if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan fetchByCompanyId_First(
		long companyId, OrderByComparator<InsurancePlan> orderByComparator) {

		List<InsurancePlan> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Insurance Plan in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Insurance Plan
	 * @throws NoSuchInsurancePlanException if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan findByCompanyId_Last(
			long companyId, OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		InsurancePlan insurancePlan = fetchByCompanyId_Last(
			companyId, orderByComparator);

		if (insurancePlan != null) {
			return insurancePlan;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchInsurancePlanException(sb.toString());
	}

	/**
	 * Returns the last Insurance Plan in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Insurance Plan, or <code>null</code> if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan fetchByCompanyId_Last(
		long companyId, OrderByComparator<InsurancePlan> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<InsurancePlan> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Insurance Plans before and after the current Insurance Plan in the ordered set where companyId = &#63;.
	 *
	 * @param insurancePlanId the primary key of the current Insurance Plan
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Insurance Plan
	 * @throws NoSuchInsurancePlanException if a Insurance Plan with the primary key could not be found
	 */
	@Override
	public InsurancePlan[] findByCompanyId_PrevAndNext(
			long insurancePlanId, long companyId,
			OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		InsurancePlan insurancePlan = findByPrimaryKey(insurancePlanId);

		Session session = null;

		try {
			session = openSession();

			InsurancePlan[] array = new InsurancePlanImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, insurancePlan, companyId, orderByComparator, true);

			array[1] = insurancePlan;

			array[2] = getByCompanyId_PrevAndNext(
				session, insurancePlan, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected InsurancePlan getByCompanyId_PrevAndNext(
		Session session, InsurancePlan insurancePlan, long companyId,
		OrderByComparator<InsurancePlan> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_INSURANCEPLAN_WHERE);

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
			sb.append(InsurancePlanModelImpl.ORDER_BY_JPQL);
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
						insurancePlan)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<InsurancePlan> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Insurance Plans where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (InsurancePlan insurancePlan :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(insurancePlan);
		}
	}

	/**
	 * Returns the number of Insurance Plans where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching Insurance Plans
	 */
	@Override
	public int countByCompanyId(long companyId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					InsurancePlan.class)) {

			FinderPath finderPath = _finderPathCountByCompanyId;

			Object[] finderArgs = new Object[] {companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_INSURANCEPLAN_WHERE);

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
		"insurancePlan.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByG_S;
	private FinderPath _finderPathWithoutPaginationFindByG_S;
	private FinderPath _finderPathCountByG_S;

	/**
	 * Returns all the Insurance Plans where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByG_S(long groupId, int status) {
		return findByG_S(
			groupId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Insurance Plans where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @return the range of matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByG_S(
		long groupId, int status, int start, int end) {

		return findByG_S(groupId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Insurance Plans where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<InsurancePlan> orderByComparator) {

		return findByG_S(groupId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Insurance Plans where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<InsurancePlan> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					InsurancePlan.class)) {

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

			List<InsurancePlan> list = null;

			if (useFinderCache) {
				list = (List<InsurancePlan>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (InsurancePlan insurancePlan : list) {
						if ((groupId != insurancePlan.getGroupId()) ||
							(status != insurancePlan.getStatus())) {

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

				sb.append(_SQL_SELECT_INSURANCEPLAN_WHERE);

				sb.append(_FINDER_COLUMN_G_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(InsurancePlanModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(status);

					list = (List<InsurancePlan>)QueryUtil.list(
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
	 * Returns the first Insurance Plan in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Insurance Plan
	 * @throws NoSuchInsurancePlanException if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan findByG_S_First(
			long groupId, int status,
			OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		InsurancePlan insurancePlan = fetchByG_S_First(
			groupId, status, orderByComparator);

		if (insurancePlan != null) {
			return insurancePlan;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchInsurancePlanException(sb.toString());
	}

	/**
	 * Returns the first Insurance Plan in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Insurance Plan, or <code>null</code> if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan fetchByG_S_First(
		long groupId, int status,
		OrderByComparator<InsurancePlan> orderByComparator) {

		List<InsurancePlan> list = findByG_S(
			groupId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Insurance Plan in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Insurance Plan
	 * @throws NoSuchInsurancePlanException if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan findByG_S_Last(
			long groupId, int status,
			OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		InsurancePlan insurancePlan = fetchByG_S_Last(
			groupId, status, orderByComparator);

		if (insurancePlan != null) {
			return insurancePlan;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchInsurancePlanException(sb.toString());
	}

	/**
	 * Returns the last Insurance Plan in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Insurance Plan, or <code>null</code> if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan fetchByG_S_Last(
		long groupId, int status,
		OrderByComparator<InsurancePlan> orderByComparator) {

		int count = countByG_S(groupId, status);

		if (count == 0) {
			return null;
		}

		List<InsurancePlan> list = findByG_S(
			groupId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Insurance Plans before and after the current Insurance Plan in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param insurancePlanId the primary key of the current Insurance Plan
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Insurance Plan
	 * @throws NoSuchInsurancePlanException if a Insurance Plan with the primary key could not be found
	 */
	@Override
	public InsurancePlan[] findByG_S_PrevAndNext(
			long insurancePlanId, long groupId, int status,
			OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		InsurancePlan insurancePlan = findByPrimaryKey(insurancePlanId);

		Session session = null;

		try {
			session = openSession();

			InsurancePlan[] array = new InsurancePlanImpl[3];

			array[0] = getByG_S_PrevAndNext(
				session, insurancePlan, groupId, status, orderByComparator,
				true);

			array[1] = insurancePlan;

			array[2] = getByG_S_PrevAndNext(
				session, insurancePlan, groupId, status, orderByComparator,
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

	protected InsurancePlan getByG_S_PrevAndNext(
		Session session, InsurancePlan insurancePlan, long groupId, int status,
		OrderByComparator<InsurancePlan> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_INSURANCEPLAN_WHERE);

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
			sb.append(InsurancePlanModelImpl.ORDER_BY_JPQL);
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
						insurancePlan)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<InsurancePlan> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the Insurance Plans that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching Insurance Plans that the user has permission to view
	 */
	@Override
	public List<InsurancePlan> filterFindByG_S(long groupId, int status) {
		return filterFindByG_S(
			groupId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Insurance Plans that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @return the range of matching Insurance Plans that the user has permission to view
	 */
	@Override
	public List<InsurancePlan> filterFindByG_S(
		long groupId, int status, int start, int end) {

		return filterFindByG_S(groupId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Insurance Plans that the user has permissions to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Insurance Plans that the user has permission to view
	 */
	@Override
	public List<InsurancePlan> filterFindByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<InsurancePlan> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_INSURANCEPLAN_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_INSURANCEPLAN_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_INSURANCEPLAN_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(InsurancePlanModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(InsurancePlanModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), InsurancePlan.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, InsurancePlanImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, InsurancePlanImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(status);

			return (List<InsurancePlan>)QueryUtil.list(
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
	 * Returns the Insurance Plans before and after the current Insurance Plan in the ordered set of Insurance Plans that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param insurancePlanId the primary key of the current Insurance Plan
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Insurance Plan
	 * @throws NoSuchInsurancePlanException if a Insurance Plan with the primary key could not be found
	 */
	@Override
	public InsurancePlan[] filterFindByG_S_PrevAndNext(
			long insurancePlanId, long groupId, int status,
			OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_S_PrevAndNext(
				insurancePlanId, groupId, status, orderByComparator);
		}

		InsurancePlan insurancePlan = findByPrimaryKey(insurancePlanId);

		Session session = null;

		try {
			session = openSession();

			InsurancePlan[] array = new InsurancePlanImpl[3];

			array[0] = filterGetByG_S_PrevAndNext(
				session, insurancePlan, groupId, status, orderByComparator,
				true);

			array[1] = insurancePlan;

			array[2] = filterGetByG_S_PrevAndNext(
				session, insurancePlan, groupId, status, orderByComparator,
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

	protected InsurancePlan filterGetByG_S_PrevAndNext(
		Session session, InsurancePlan insurancePlan, long groupId, int status,
		OrderByComparator<InsurancePlan> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_INSURANCEPLAN_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_INSURANCEPLAN_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_INSURANCEPLAN_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(InsurancePlanModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(InsurancePlanModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), InsurancePlan.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, InsurancePlanImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, InsurancePlanImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						insurancePlan)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<InsurancePlan> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Insurance Plans where groupId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 */
	@Override
	public void removeByG_S(long groupId, int status) {
		for (InsurancePlan insurancePlan :
				findByG_S(
					groupId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(insurancePlan);
		}
	}

	/**
	 * Returns the number of Insurance Plans where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching Insurance Plans
	 */
	@Override
	public int countByG_S(long groupId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					InsurancePlan.class)) {

			FinderPath finderPath = _finderPathCountByG_S;

			Object[] finderArgs = new Object[] {groupId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_INSURANCEPLAN_WHERE);

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
	 * Returns the number of Insurance Plans that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching Insurance Plans that the user has permission to view
	 */
	@Override
	public int filterCountByG_S(long groupId, int status) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_S(groupId, status);
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_INSURANCEPLAN_WHERE);

		sb.append(_FINDER_COLUMN_G_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_S_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), InsurancePlan.class.getName(),
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
		"insurancePlan.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_S_STATUS_2 =
		"insurancePlan.status = ?";

	private FinderPath _finderPathWithPaginationFindByG_NotS;
	private FinderPath _finderPathWithPaginationCountByG_NotS;

	/**
	 * Returns all the Insurance Plans where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByG_NotS(long groupId, int status) {
		return findByG_NotS(
			groupId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Insurance Plans where groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @return the range of matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByG_NotS(
		long groupId, int status, int start, int end) {

		return findByG_NotS(groupId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Insurance Plans where groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByG_NotS(
		long groupId, int status, int start, int end,
		OrderByComparator<InsurancePlan> orderByComparator) {

		return findByG_NotS(
			groupId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Insurance Plans where groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByG_NotS(
		long groupId, int status, int start, int end,
		OrderByComparator<InsurancePlan> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					InsurancePlan.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByG_NotS;
			finderArgs = new Object[] {
				groupId, status, start, end, orderByComparator
			};

			List<InsurancePlan> list = null;

			if (useFinderCache) {
				list = (List<InsurancePlan>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (InsurancePlan insurancePlan : list) {
						if ((groupId != insurancePlan.getGroupId()) ||
							(status == insurancePlan.getStatus())) {

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

				sb.append(_SQL_SELECT_INSURANCEPLAN_WHERE);

				sb.append(_FINDER_COLUMN_G_NOTS_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_NOTS_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(InsurancePlanModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(status);

					list = (List<InsurancePlan>)QueryUtil.list(
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
	 * Returns the first Insurance Plan in the ordered set where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Insurance Plan
	 * @throws NoSuchInsurancePlanException if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan findByG_NotS_First(
			long groupId, int status,
			OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		InsurancePlan insurancePlan = fetchByG_NotS_First(
			groupId, status, orderByComparator);

		if (insurancePlan != null) {
			return insurancePlan;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchInsurancePlanException(sb.toString());
	}

	/**
	 * Returns the first Insurance Plan in the ordered set where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Insurance Plan, or <code>null</code> if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan fetchByG_NotS_First(
		long groupId, int status,
		OrderByComparator<InsurancePlan> orderByComparator) {

		List<InsurancePlan> list = findByG_NotS(
			groupId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Insurance Plan in the ordered set where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Insurance Plan
	 * @throws NoSuchInsurancePlanException if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan findByG_NotS_Last(
			long groupId, int status,
			OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		InsurancePlan insurancePlan = fetchByG_NotS_Last(
			groupId, status, orderByComparator);

		if (insurancePlan != null) {
			return insurancePlan;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchInsurancePlanException(sb.toString());
	}

	/**
	 * Returns the last Insurance Plan in the ordered set where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Insurance Plan, or <code>null</code> if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan fetchByG_NotS_Last(
		long groupId, int status,
		OrderByComparator<InsurancePlan> orderByComparator) {

		int count = countByG_NotS(groupId, status);

		if (count == 0) {
			return null;
		}

		List<InsurancePlan> list = findByG_NotS(
			groupId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Insurance Plans before and after the current Insurance Plan in the ordered set where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param insurancePlanId the primary key of the current Insurance Plan
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Insurance Plan
	 * @throws NoSuchInsurancePlanException if a Insurance Plan with the primary key could not be found
	 */
	@Override
	public InsurancePlan[] findByG_NotS_PrevAndNext(
			long insurancePlanId, long groupId, int status,
			OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		InsurancePlan insurancePlan = findByPrimaryKey(insurancePlanId);

		Session session = null;

		try {
			session = openSession();

			InsurancePlan[] array = new InsurancePlanImpl[3];

			array[0] = getByG_NotS_PrevAndNext(
				session, insurancePlan, groupId, status, orderByComparator,
				true);

			array[1] = insurancePlan;

			array[2] = getByG_NotS_PrevAndNext(
				session, insurancePlan, groupId, status, orderByComparator,
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

	protected InsurancePlan getByG_NotS_PrevAndNext(
		Session session, InsurancePlan insurancePlan, long groupId, int status,
		OrderByComparator<InsurancePlan> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_INSURANCEPLAN_WHERE);

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
			sb.append(InsurancePlanModelImpl.ORDER_BY_JPQL);
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
						insurancePlan)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<InsurancePlan> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the Insurance Plans that the user has permission to view where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching Insurance Plans that the user has permission to view
	 */
	@Override
	public List<InsurancePlan> filterFindByG_NotS(long groupId, int status) {
		return filterFindByG_NotS(
			groupId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Insurance Plans that the user has permission to view where groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @return the range of matching Insurance Plans that the user has permission to view
	 */
	@Override
	public List<InsurancePlan> filterFindByG_NotS(
		long groupId, int status, int start, int end) {

		return filterFindByG_NotS(groupId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Insurance Plans that the user has permissions to view where groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Insurance Plans that the user has permission to view
	 */
	@Override
	public List<InsurancePlan> filterFindByG_NotS(
		long groupId, int status, int start, int end,
		OrderByComparator<InsurancePlan> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_INSURANCEPLAN_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_INSURANCEPLAN_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_NOTS_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_INSURANCEPLAN_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(InsurancePlanModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(InsurancePlanModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), InsurancePlan.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, InsurancePlanImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, InsurancePlanImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(status);

			return (List<InsurancePlan>)QueryUtil.list(
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
	 * Returns the Insurance Plans before and after the current Insurance Plan in the ordered set of Insurance Plans that the user has permission to view where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param insurancePlanId the primary key of the current Insurance Plan
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Insurance Plan
	 * @throws NoSuchInsurancePlanException if a Insurance Plan with the primary key could not be found
	 */
	@Override
	public InsurancePlan[] filterFindByG_NotS_PrevAndNext(
			long insurancePlanId, long groupId, int status,
			OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_NotS_PrevAndNext(
				insurancePlanId, groupId, status, orderByComparator);
		}

		InsurancePlan insurancePlan = findByPrimaryKey(insurancePlanId);

		Session session = null;

		try {
			session = openSession();

			InsurancePlan[] array = new InsurancePlanImpl[3];

			array[0] = filterGetByG_NotS_PrevAndNext(
				session, insurancePlan, groupId, status, orderByComparator,
				true);

			array[1] = insurancePlan;

			array[2] = filterGetByG_NotS_PrevAndNext(
				session, insurancePlan, groupId, status, orderByComparator,
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

	protected InsurancePlan filterGetByG_NotS_PrevAndNext(
		Session session, InsurancePlan insurancePlan, long groupId, int status,
		OrderByComparator<InsurancePlan> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_INSURANCEPLAN_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_INSURANCEPLAN_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_NOTS_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_INSURANCEPLAN_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(InsurancePlanModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(InsurancePlanModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), InsurancePlan.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, InsurancePlanImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, InsurancePlanImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						insurancePlan)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<InsurancePlan> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Insurance Plans where groupId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 */
	@Override
	public void removeByG_NotS(long groupId, int status) {
		for (InsurancePlan insurancePlan :
				findByG_NotS(
					groupId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(insurancePlan);
		}
	}

	/**
	 * Returns the number of Insurance Plans where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching Insurance Plans
	 */
	@Override
	public int countByG_NotS(long groupId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					InsurancePlan.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByG_NotS;

			Object[] finderArgs = new Object[] {groupId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_INSURANCEPLAN_WHERE);

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
	 * Returns the number of Insurance Plans that the user has permission to view where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching Insurance Plans that the user has permission to view
	 */
	@Override
	public int filterCountByG_NotS(long groupId, int status) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_NotS(groupId, status);
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_INSURANCEPLAN_WHERE);

		sb.append(_FINDER_COLUMN_G_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_NOTS_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), InsurancePlan.class.getName(),
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
		"insurancePlan.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_NOTS_STATUS_2 =
		"insurancePlan.status != ?";

	private FinderPath _finderPathWithPaginationFindByC_S;
	private FinderPath _finderPathWithoutPaginationFindByC_S;
	private FinderPath _finderPathCountByC_S;

	/**
	 * Returns all the Insurance Plans where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByC_S(long companyId, int status) {
		return findByC_S(
			companyId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Insurance Plans where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @return the range of matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByC_S(
		long companyId, int status, int start, int end) {

		return findByC_S(companyId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Insurance Plans where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByC_S(
		long companyId, int status, int start, int end,
		OrderByComparator<InsurancePlan> orderByComparator) {

		return findByC_S(
			companyId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Insurance Plans where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByC_S(
		long companyId, int status, int start, int end,
		OrderByComparator<InsurancePlan> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					InsurancePlan.class)) {

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

			List<InsurancePlan> list = null;

			if (useFinderCache) {
				list = (List<InsurancePlan>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (InsurancePlan insurancePlan : list) {
						if ((companyId != insurancePlan.getCompanyId()) ||
							(status != insurancePlan.getStatus())) {

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

				sb.append(_SQL_SELECT_INSURANCEPLAN_WHERE);

				sb.append(_FINDER_COLUMN_C_S_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(InsurancePlanModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(status);

					list = (List<InsurancePlan>)QueryUtil.list(
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
	 * Returns the first Insurance Plan in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Insurance Plan
	 * @throws NoSuchInsurancePlanException if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan findByC_S_First(
			long companyId, int status,
			OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		InsurancePlan insurancePlan = fetchByC_S_First(
			companyId, status, orderByComparator);

		if (insurancePlan != null) {
			return insurancePlan;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchInsurancePlanException(sb.toString());
	}

	/**
	 * Returns the first Insurance Plan in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Insurance Plan, or <code>null</code> if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan fetchByC_S_First(
		long companyId, int status,
		OrderByComparator<InsurancePlan> orderByComparator) {

		List<InsurancePlan> list = findByC_S(
			companyId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Insurance Plan in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Insurance Plan
	 * @throws NoSuchInsurancePlanException if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan findByC_S_Last(
			long companyId, int status,
			OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		InsurancePlan insurancePlan = fetchByC_S_Last(
			companyId, status, orderByComparator);

		if (insurancePlan != null) {
			return insurancePlan;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchInsurancePlanException(sb.toString());
	}

	/**
	 * Returns the last Insurance Plan in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Insurance Plan, or <code>null</code> if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan fetchByC_S_Last(
		long companyId, int status,
		OrderByComparator<InsurancePlan> orderByComparator) {

		int count = countByC_S(companyId, status);

		if (count == 0) {
			return null;
		}

		List<InsurancePlan> list = findByC_S(
			companyId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Insurance Plans before and after the current Insurance Plan in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param insurancePlanId the primary key of the current Insurance Plan
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Insurance Plan
	 * @throws NoSuchInsurancePlanException if a Insurance Plan with the primary key could not be found
	 */
	@Override
	public InsurancePlan[] findByC_S_PrevAndNext(
			long insurancePlanId, long companyId, int status,
			OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		InsurancePlan insurancePlan = findByPrimaryKey(insurancePlanId);

		Session session = null;

		try {
			session = openSession();

			InsurancePlan[] array = new InsurancePlanImpl[3];

			array[0] = getByC_S_PrevAndNext(
				session, insurancePlan, companyId, status, orderByComparator,
				true);

			array[1] = insurancePlan;

			array[2] = getByC_S_PrevAndNext(
				session, insurancePlan, companyId, status, orderByComparator,
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

	protected InsurancePlan getByC_S_PrevAndNext(
		Session session, InsurancePlan insurancePlan, long companyId,
		int status, OrderByComparator<InsurancePlan> orderByComparator,
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

		sb.append(_SQL_SELECT_INSURANCEPLAN_WHERE);

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
			sb.append(InsurancePlanModelImpl.ORDER_BY_JPQL);
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
						insurancePlan)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<InsurancePlan> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Insurance Plans where companyId = &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 */
	@Override
	public void removeByC_S(long companyId, int status) {
		for (InsurancePlan insurancePlan :
				findByC_S(
					companyId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(insurancePlan);
		}
	}

	/**
	 * Returns the number of Insurance Plans where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching Insurance Plans
	 */
	@Override
	public int countByC_S(long companyId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					InsurancePlan.class)) {

			FinderPath finderPath = _finderPathCountByC_S;

			Object[] finderArgs = new Object[] {companyId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_INSURANCEPLAN_WHERE);

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
		"insurancePlan.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_S_STATUS_2 =
		"insurancePlan.status = ?";

	private FinderPath _finderPathWithPaginationFindByC_NotS;
	private FinderPath _finderPathWithPaginationCountByC_NotS;

	/**
	 * Returns all the Insurance Plans where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByC_NotS(long companyId, int status) {
		return findByC_NotS(
			companyId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Insurance Plans where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @return the range of matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByC_NotS(
		long companyId, int status, int start, int end) {

		return findByC_NotS(companyId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Insurance Plans where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByC_NotS(
		long companyId, int status, int start, int end,
		OrderByComparator<InsurancePlan> orderByComparator) {

		return findByC_NotS(
			companyId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Insurance Plans where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByC_NotS(
		long companyId, int status, int start, int end,
		OrderByComparator<InsurancePlan> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					InsurancePlan.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByC_NotS;
			finderArgs = new Object[] {
				companyId, status, start, end, orderByComparator
			};

			List<InsurancePlan> list = null;

			if (useFinderCache) {
				list = (List<InsurancePlan>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (InsurancePlan insurancePlan : list) {
						if ((companyId != insurancePlan.getCompanyId()) ||
							(status == insurancePlan.getStatus())) {

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

				sb.append(_SQL_SELECT_INSURANCEPLAN_WHERE);

				sb.append(_FINDER_COLUMN_C_NOTS_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_NOTS_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(InsurancePlanModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(status);

					list = (List<InsurancePlan>)QueryUtil.list(
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
	 * Returns the first Insurance Plan in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Insurance Plan
	 * @throws NoSuchInsurancePlanException if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan findByC_NotS_First(
			long companyId, int status,
			OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		InsurancePlan insurancePlan = fetchByC_NotS_First(
			companyId, status, orderByComparator);

		if (insurancePlan != null) {
			return insurancePlan;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchInsurancePlanException(sb.toString());
	}

	/**
	 * Returns the first Insurance Plan in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Insurance Plan, or <code>null</code> if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan fetchByC_NotS_First(
		long companyId, int status,
		OrderByComparator<InsurancePlan> orderByComparator) {

		List<InsurancePlan> list = findByC_NotS(
			companyId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Insurance Plan in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Insurance Plan
	 * @throws NoSuchInsurancePlanException if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan findByC_NotS_Last(
			long companyId, int status,
			OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		InsurancePlan insurancePlan = fetchByC_NotS_Last(
			companyId, status, orderByComparator);

		if (insurancePlan != null) {
			return insurancePlan;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchInsurancePlanException(sb.toString());
	}

	/**
	 * Returns the last Insurance Plan in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Insurance Plan, or <code>null</code> if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan fetchByC_NotS_Last(
		long companyId, int status,
		OrderByComparator<InsurancePlan> orderByComparator) {

		int count = countByC_NotS(companyId, status);

		if (count == 0) {
			return null;
		}

		List<InsurancePlan> list = findByC_NotS(
			companyId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Insurance Plans before and after the current Insurance Plan in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param insurancePlanId the primary key of the current Insurance Plan
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Insurance Plan
	 * @throws NoSuchInsurancePlanException if a Insurance Plan with the primary key could not be found
	 */
	@Override
	public InsurancePlan[] findByC_NotS_PrevAndNext(
			long insurancePlanId, long companyId, int status,
			OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		InsurancePlan insurancePlan = findByPrimaryKey(insurancePlanId);

		Session session = null;

		try {
			session = openSession();

			InsurancePlan[] array = new InsurancePlanImpl[3];

			array[0] = getByC_NotS_PrevAndNext(
				session, insurancePlan, companyId, status, orderByComparator,
				true);

			array[1] = insurancePlan;

			array[2] = getByC_NotS_PrevAndNext(
				session, insurancePlan, companyId, status, orderByComparator,
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

	protected InsurancePlan getByC_NotS_PrevAndNext(
		Session session, InsurancePlan insurancePlan, long companyId,
		int status, OrderByComparator<InsurancePlan> orderByComparator,
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

		sb.append(_SQL_SELECT_INSURANCEPLAN_WHERE);

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
			sb.append(InsurancePlanModelImpl.ORDER_BY_JPQL);
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
						insurancePlan)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<InsurancePlan> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Insurance Plans where companyId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 */
	@Override
	public void removeByC_NotS(long companyId, int status) {
		for (InsurancePlan insurancePlan :
				findByC_NotS(
					companyId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(insurancePlan);
		}
	}

	/**
	 * Returns the number of Insurance Plans where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching Insurance Plans
	 */
	@Override
	public int countByC_NotS(long companyId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					InsurancePlan.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByC_NotS;

			Object[] finderArgs = new Object[] {companyId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_INSURANCEPLAN_WHERE);

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
		"insurancePlan.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_NOTS_STATUS_2 =
		"insurancePlan.status != ?";

	private FinderPath _finderPathWithPaginationFindByG_U_S;
	private FinderPath _finderPathWithoutPaginationFindByG_U_S;
	private FinderPath _finderPathCountByG_U_S;
	private FinderPath _finderPathWithPaginationCountByG_U_S;

	/**
	 * Returns all the Insurance Plans where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByG_U_S(
		long groupId, long userId, int status) {

		return findByG_U_S(
			groupId, userId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the Insurance Plans where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @return the range of matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByG_U_S(
		long groupId, long userId, int status, int start, int end) {

		return findByG_U_S(groupId, userId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Insurance Plans where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByG_U_S(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<InsurancePlan> orderByComparator) {

		return findByG_U_S(
			groupId, userId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Insurance Plans where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByG_U_S(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<InsurancePlan> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					InsurancePlan.class)) {

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

			List<InsurancePlan> list = null;

			if (useFinderCache) {
				list = (List<InsurancePlan>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (InsurancePlan insurancePlan : list) {
						if ((groupId != insurancePlan.getGroupId()) ||
							(userId != insurancePlan.getUserId()) ||
							(status != insurancePlan.getStatus())) {

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

				sb.append(_SQL_SELECT_INSURANCEPLAN_WHERE);

				sb.append(_FINDER_COLUMN_G_U_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_U_S_USERID_2);

				sb.append(_FINDER_COLUMN_G_U_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(InsurancePlanModelImpl.ORDER_BY_JPQL);
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

					list = (List<InsurancePlan>)QueryUtil.list(
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
	 * Returns the first Insurance Plan in the ordered set where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Insurance Plan
	 * @throws NoSuchInsurancePlanException if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan findByG_U_S_First(
			long groupId, long userId, int status,
			OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		InsurancePlan insurancePlan = fetchByG_U_S_First(
			groupId, userId, status, orderByComparator);

		if (insurancePlan != null) {
			return insurancePlan;
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

		throw new NoSuchInsurancePlanException(sb.toString());
	}

	/**
	 * Returns the first Insurance Plan in the ordered set where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Insurance Plan, or <code>null</code> if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan fetchByG_U_S_First(
		long groupId, long userId, int status,
		OrderByComparator<InsurancePlan> orderByComparator) {

		List<InsurancePlan> list = findByG_U_S(
			groupId, userId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Insurance Plan in the ordered set where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Insurance Plan
	 * @throws NoSuchInsurancePlanException if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan findByG_U_S_Last(
			long groupId, long userId, int status,
			OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		InsurancePlan insurancePlan = fetchByG_U_S_Last(
			groupId, userId, status, orderByComparator);

		if (insurancePlan != null) {
			return insurancePlan;
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

		throw new NoSuchInsurancePlanException(sb.toString());
	}

	/**
	 * Returns the last Insurance Plan in the ordered set where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Insurance Plan, or <code>null</code> if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan fetchByG_U_S_Last(
		long groupId, long userId, int status,
		OrderByComparator<InsurancePlan> orderByComparator) {

		int count = countByG_U_S(groupId, userId, status);

		if (count == 0) {
			return null;
		}

		List<InsurancePlan> list = findByG_U_S(
			groupId, userId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Insurance Plans before and after the current Insurance Plan in the ordered set where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param insurancePlanId the primary key of the current Insurance Plan
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Insurance Plan
	 * @throws NoSuchInsurancePlanException if a Insurance Plan with the primary key could not be found
	 */
	@Override
	public InsurancePlan[] findByG_U_S_PrevAndNext(
			long insurancePlanId, long groupId, long userId, int status,
			OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		InsurancePlan insurancePlan = findByPrimaryKey(insurancePlanId);

		Session session = null;

		try {
			session = openSession();

			InsurancePlan[] array = new InsurancePlanImpl[3];

			array[0] = getByG_U_S_PrevAndNext(
				session, insurancePlan, groupId, userId, status,
				orderByComparator, true);

			array[1] = insurancePlan;

			array[2] = getByG_U_S_PrevAndNext(
				session, insurancePlan, groupId, userId, status,
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

	protected InsurancePlan getByG_U_S_PrevAndNext(
		Session session, InsurancePlan insurancePlan, long groupId, long userId,
		int status, OrderByComparator<InsurancePlan> orderByComparator,
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

		sb.append(_SQL_SELECT_INSURANCEPLAN_WHERE);

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
			sb.append(InsurancePlanModelImpl.ORDER_BY_JPQL);
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
						insurancePlan)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<InsurancePlan> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the Insurance Plans that the user has permission to view where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the matching Insurance Plans that the user has permission to view
	 */
	@Override
	public List<InsurancePlan> filterFindByG_U_S(
		long groupId, long userId, int status) {

		return filterFindByG_U_S(
			groupId, userId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the Insurance Plans that the user has permission to view where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @return the range of matching Insurance Plans that the user has permission to view
	 */
	@Override
	public List<InsurancePlan> filterFindByG_U_S(
		long groupId, long userId, int status, int start, int end) {

		return filterFindByG_U_S(groupId, userId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Insurance Plans that the user has permissions to view where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Insurance Plans that the user has permission to view
	 */
	@Override
	public List<InsurancePlan> filterFindByG_U_S(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<InsurancePlan> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_INSURANCEPLAN_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_INSURANCEPLAN_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_U_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_S_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_INSURANCEPLAN_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(InsurancePlanModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(InsurancePlanModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), InsurancePlan.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, InsurancePlanImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, InsurancePlanImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(userId);

			queryPos.add(status);

			return (List<InsurancePlan>)QueryUtil.list(
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
	 * Returns the Insurance Plans before and after the current Insurance Plan in the ordered set of Insurance Plans that the user has permission to view where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param insurancePlanId the primary key of the current Insurance Plan
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Insurance Plan
	 * @throws NoSuchInsurancePlanException if a Insurance Plan with the primary key could not be found
	 */
	@Override
	public InsurancePlan[] filterFindByG_U_S_PrevAndNext(
			long insurancePlanId, long groupId, long userId, int status,
			OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_U_S_PrevAndNext(
				insurancePlanId, groupId, userId, status, orderByComparator);
		}

		InsurancePlan insurancePlan = findByPrimaryKey(insurancePlanId);

		Session session = null;

		try {
			session = openSession();

			InsurancePlan[] array = new InsurancePlanImpl[3];

			array[0] = filterGetByG_U_S_PrevAndNext(
				session, insurancePlan, groupId, userId, status,
				orderByComparator, true);

			array[1] = insurancePlan;

			array[2] = filterGetByG_U_S_PrevAndNext(
				session, insurancePlan, groupId, userId, status,
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

	protected InsurancePlan filterGetByG_U_S_PrevAndNext(
		Session session, InsurancePlan insurancePlan, long groupId, long userId,
		int status, OrderByComparator<InsurancePlan> orderByComparator,
		boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_INSURANCEPLAN_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_INSURANCEPLAN_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_U_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_S_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_INSURANCEPLAN_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(InsurancePlanModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(InsurancePlanModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), InsurancePlan.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, InsurancePlanImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, InsurancePlanImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(userId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						insurancePlan)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<InsurancePlan> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the Insurance Plans that the user has permission to view where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @return the matching Insurance Plans that the user has permission to view
	 */
	@Override
	public List<InsurancePlan> filterFindByG_U_S(
		long groupId, long userId, int[] statuses) {

		return filterFindByG_U_S(
			groupId, userId, statuses, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the Insurance Plans that the user has permission to view where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @return the range of matching Insurance Plans that the user has permission to view
	 */
	@Override
	public List<InsurancePlan> filterFindByG_U_S(
		long groupId, long userId, int[] statuses, int start, int end) {

		return filterFindByG_U_S(groupId, userId, statuses, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Insurance Plans that the user has permission to view where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Insurance Plans that the user has permission to view
	 */
	@Override
	public List<InsurancePlan> filterFindByG_U_S(
		long groupId, long userId, int[] statuses, int start, int end,
		OrderByComparator<InsurancePlan> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_INSURANCEPLAN_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_INSURANCEPLAN_NO_INLINE_DISTINCT_WHERE_1);
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
				_FILTER_SQL_SELECT_INSURANCEPLAN_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(InsurancePlanModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(InsurancePlanModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), InsurancePlan.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, InsurancePlanImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, InsurancePlanImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(userId);

			return (List<InsurancePlan>)QueryUtil.list(
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
	 * Returns all the Insurance Plans where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @return the matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByG_U_S(
		long groupId, long userId, int[] statuses) {

		return findByG_U_S(
			groupId, userId, statuses, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the Insurance Plans where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @return the range of matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByG_U_S(
		long groupId, long userId, int[] statuses, int start, int end) {

		return findByG_U_S(groupId, userId, statuses, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Insurance Plans where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByG_U_S(
		long groupId, long userId, int[] statuses, int start, int end,
		OrderByComparator<InsurancePlan> orderByComparator) {

		return findByG_U_S(
			groupId, userId, statuses, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Insurance Plans where groupId = &#63; and userId = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByG_U_S(
		long groupId, long userId, int[] statuses, int start, int end,
		OrderByComparator<InsurancePlan> orderByComparator,
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
					InsurancePlan.class)) {

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

			List<InsurancePlan> list = null;

			if (useFinderCache) {
				list = (List<InsurancePlan>)finderCache.getResult(
					_finderPathWithPaginationFindByG_U_S, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (InsurancePlan insurancePlan : list) {
						if ((groupId != insurancePlan.getGroupId()) ||
							(userId != insurancePlan.getUserId()) ||
							!ArrayUtil.contains(
								statuses, insurancePlan.getStatus())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_SELECT_INSURANCEPLAN_WHERE);

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
					sb.append(InsurancePlanModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(userId);

					list = (List<InsurancePlan>)QueryUtil.list(
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
	 * Removes all the Insurance Plans where groupId = &#63; and userId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 */
	@Override
	public void removeByG_U_S(long groupId, long userId, int status) {
		for (InsurancePlan insurancePlan :
				findByG_U_S(
					groupId, userId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(insurancePlan);
		}
	}

	/**
	 * Returns the number of Insurance Plans where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the number of matching Insurance Plans
	 */
	@Override
	public int countByG_U_S(long groupId, long userId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					InsurancePlan.class)) {

			FinderPath finderPath = _finderPathCountByG_U_S;

			Object[] finderArgs = new Object[] {groupId, userId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_INSURANCEPLAN_WHERE);

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
	 * Returns the number of Insurance Plans where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @return the number of matching Insurance Plans
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
					InsurancePlan.class)) {

			Object[] finderArgs = new Object[] {
				groupId, userId, StringUtil.merge(statuses)
			};

			Long count = (Long)finderCache.getResult(
				_finderPathWithPaginationCountByG_U_S, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_COUNT_INSURANCEPLAN_WHERE);

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
	 * Returns the number of Insurance Plans that the user has permission to view where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the number of matching Insurance Plans that the user has permission to view
	 */
	@Override
	public int filterCountByG_U_S(long groupId, long userId, int status) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_U_S(groupId, userId, status);
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_INSURANCEPLAN_WHERE);

		sb.append(_FINDER_COLUMN_G_U_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_S_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_S_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), InsurancePlan.class.getName(),
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
	 * Returns the number of Insurance Plans that the user has permission to view where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @return the number of matching Insurance Plans that the user has permission to view
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

		sb.append(_FILTER_SQL_COUNT_INSURANCEPLAN_WHERE);

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
			sb.toString(), InsurancePlan.class.getName(),
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
		"insurancePlan.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_U_S_USERID_2 =
		"insurancePlan.userId = ? AND ";

	private static final String _FINDER_COLUMN_G_U_S_STATUS_2 =
		"insurancePlan.status = ?";

	private static final String _FINDER_COLUMN_G_U_S_STATUS_7 =
		"insurancePlan.status IN (";

	private FinderPath _finderPathWithPaginationFindByG_U_NotS;
	private FinderPath _finderPathWithPaginationCountByG_U_NotS;

	/**
	 * Returns all the Insurance Plans where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByG_U_NotS(
		long groupId, long userId, int status) {

		return findByG_U_NotS(
			groupId, userId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the Insurance Plans where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @return the range of matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByG_U_NotS(
		long groupId, long userId, int status, int start, int end) {

		return findByG_U_NotS(groupId, userId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Insurance Plans where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByG_U_NotS(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<InsurancePlan> orderByComparator) {

		return findByG_U_NotS(
			groupId, userId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Insurance Plans where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findByG_U_NotS(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<InsurancePlan> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					InsurancePlan.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByG_U_NotS;
			finderArgs = new Object[] {
				groupId, userId, status, start, end, orderByComparator
			};

			List<InsurancePlan> list = null;

			if (useFinderCache) {
				list = (List<InsurancePlan>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (InsurancePlan insurancePlan : list) {
						if ((groupId != insurancePlan.getGroupId()) ||
							(userId != insurancePlan.getUserId()) ||
							(status == insurancePlan.getStatus())) {

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

				sb.append(_SQL_SELECT_INSURANCEPLAN_WHERE);

				sb.append(_FINDER_COLUMN_G_U_NOTS_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_U_NOTS_USERID_2);

				sb.append(_FINDER_COLUMN_G_U_NOTS_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(InsurancePlanModelImpl.ORDER_BY_JPQL);
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

					list = (List<InsurancePlan>)QueryUtil.list(
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
	 * Returns the first Insurance Plan in the ordered set where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Insurance Plan
	 * @throws NoSuchInsurancePlanException if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan findByG_U_NotS_First(
			long groupId, long userId, int status,
			OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		InsurancePlan insurancePlan = fetchByG_U_NotS_First(
			groupId, userId, status, orderByComparator);

		if (insurancePlan != null) {
			return insurancePlan;
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

		throw new NoSuchInsurancePlanException(sb.toString());
	}

	/**
	 * Returns the first Insurance Plan in the ordered set where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Insurance Plan, or <code>null</code> if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan fetchByG_U_NotS_First(
		long groupId, long userId, int status,
		OrderByComparator<InsurancePlan> orderByComparator) {

		List<InsurancePlan> list = findByG_U_NotS(
			groupId, userId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Insurance Plan in the ordered set where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Insurance Plan
	 * @throws NoSuchInsurancePlanException if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan findByG_U_NotS_Last(
			long groupId, long userId, int status,
			OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		InsurancePlan insurancePlan = fetchByG_U_NotS_Last(
			groupId, userId, status, orderByComparator);

		if (insurancePlan != null) {
			return insurancePlan;
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

		throw new NoSuchInsurancePlanException(sb.toString());
	}

	/**
	 * Returns the last Insurance Plan in the ordered set where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Insurance Plan, or <code>null</code> if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan fetchByG_U_NotS_Last(
		long groupId, long userId, int status,
		OrderByComparator<InsurancePlan> orderByComparator) {

		int count = countByG_U_NotS(groupId, userId, status);

		if (count == 0) {
			return null;
		}

		List<InsurancePlan> list = findByG_U_NotS(
			groupId, userId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Insurance Plans before and after the current Insurance Plan in the ordered set where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param insurancePlanId the primary key of the current Insurance Plan
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Insurance Plan
	 * @throws NoSuchInsurancePlanException if a Insurance Plan with the primary key could not be found
	 */
	@Override
	public InsurancePlan[] findByG_U_NotS_PrevAndNext(
			long insurancePlanId, long groupId, long userId, int status,
			OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		InsurancePlan insurancePlan = findByPrimaryKey(insurancePlanId);

		Session session = null;

		try {
			session = openSession();

			InsurancePlan[] array = new InsurancePlanImpl[3];

			array[0] = getByG_U_NotS_PrevAndNext(
				session, insurancePlan, groupId, userId, status,
				orderByComparator, true);

			array[1] = insurancePlan;

			array[2] = getByG_U_NotS_PrevAndNext(
				session, insurancePlan, groupId, userId, status,
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

	protected InsurancePlan getByG_U_NotS_PrevAndNext(
		Session session, InsurancePlan insurancePlan, long groupId, long userId,
		int status, OrderByComparator<InsurancePlan> orderByComparator,
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

		sb.append(_SQL_SELECT_INSURANCEPLAN_WHERE);

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
			sb.append(InsurancePlanModelImpl.ORDER_BY_JPQL);
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
						insurancePlan)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<InsurancePlan> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the Insurance Plans that the user has permission to view where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the matching Insurance Plans that the user has permission to view
	 */
	@Override
	public List<InsurancePlan> filterFindByG_U_NotS(
		long groupId, long userId, int status) {

		return filterFindByG_U_NotS(
			groupId, userId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the Insurance Plans that the user has permission to view where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @return the range of matching Insurance Plans that the user has permission to view
	 */
	@Override
	public List<InsurancePlan> filterFindByG_U_NotS(
		long groupId, long userId, int status, int start, int end) {

		return filterFindByG_U_NotS(groupId, userId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Insurance Plans that the user has permissions to view where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Insurance Plans that the user has permission to view
	 */
	@Override
	public List<InsurancePlan> filterFindByG_U_NotS(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<InsurancePlan> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_INSURANCEPLAN_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_INSURANCEPLAN_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_U_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_NOTS_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_NOTS_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_INSURANCEPLAN_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(InsurancePlanModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(InsurancePlanModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), InsurancePlan.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, InsurancePlanImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, InsurancePlanImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(userId);

			queryPos.add(status);

			return (List<InsurancePlan>)QueryUtil.list(
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
	 * Returns the Insurance Plans before and after the current Insurance Plan in the ordered set of Insurance Plans that the user has permission to view where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param insurancePlanId the primary key of the current Insurance Plan
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Insurance Plan
	 * @throws NoSuchInsurancePlanException if a Insurance Plan with the primary key could not be found
	 */
	@Override
	public InsurancePlan[] filterFindByG_U_NotS_PrevAndNext(
			long insurancePlanId, long groupId, long userId, int status,
			OrderByComparator<InsurancePlan> orderByComparator)
		throws NoSuchInsurancePlanException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_U_NotS_PrevAndNext(
				insurancePlanId, groupId, userId, status, orderByComparator);
		}

		InsurancePlan insurancePlan = findByPrimaryKey(insurancePlanId);

		Session session = null;

		try {
			session = openSession();

			InsurancePlan[] array = new InsurancePlanImpl[3];

			array[0] = filterGetByG_U_NotS_PrevAndNext(
				session, insurancePlan, groupId, userId, status,
				orderByComparator, true);

			array[1] = insurancePlan;

			array[2] = filterGetByG_U_NotS_PrevAndNext(
				session, insurancePlan, groupId, userId, status,
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

	protected InsurancePlan filterGetByG_U_NotS_PrevAndNext(
		Session session, InsurancePlan insurancePlan, long groupId, long userId,
		int status, OrderByComparator<InsurancePlan> orderByComparator,
		boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_INSURANCEPLAN_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_INSURANCEPLAN_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_U_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_NOTS_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_NOTS_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_INSURANCEPLAN_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(InsurancePlanModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(InsurancePlanModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), InsurancePlan.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, InsurancePlanImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, InsurancePlanImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(userId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(
						insurancePlan)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<InsurancePlan> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Insurance Plans where groupId = &#63; and userId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 */
	@Override
	public void removeByG_U_NotS(long groupId, long userId, int status) {
		for (InsurancePlan insurancePlan :
				findByG_U_NotS(
					groupId, userId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(insurancePlan);
		}
	}

	/**
	 * Returns the number of Insurance Plans where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the number of matching Insurance Plans
	 */
	@Override
	public int countByG_U_NotS(long groupId, long userId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					InsurancePlan.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByG_U_NotS;

			Object[] finderArgs = new Object[] {groupId, userId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_INSURANCEPLAN_WHERE);

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
	 * Returns the number of Insurance Plans that the user has permission to view where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the number of matching Insurance Plans that the user has permission to view
	 */
	@Override
	public int filterCountByG_U_NotS(long groupId, long userId, int status) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_U_NotS(groupId, userId, status);
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_INSURANCEPLAN_WHERE);

		sb.append(_FINDER_COLUMN_G_U_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_NOTS_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_NOTS_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), InsurancePlan.class.getName(),
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
		"insurancePlan.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_U_NOTS_USERID_2 =
		"insurancePlan.userId = ? AND ";

	private static final String _FINDER_COLUMN_G_U_NOTS_STATUS_2 =
		"insurancePlan.status != ?";

	private FinderPath _finderPathFetchByExternalReferenceCode;

	/**
	 * Returns the Insurance Plan where externalReferenceCode = &#63; or throws a <code>NoSuchInsurancePlanException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @return the matching Insurance Plan
	 * @throws NoSuchInsurancePlanException if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan findByExternalReferenceCode(
			String externalReferenceCode)
		throws NoSuchInsurancePlanException {

		InsurancePlan insurancePlan = fetchByExternalReferenceCode(
			externalReferenceCode);

		if (insurancePlan == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("externalReferenceCode=");
			sb.append(externalReferenceCode);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchInsurancePlanException(sb.toString());
		}

		return insurancePlan;
	}

	/**
	 * Returns the Insurance Plan where externalReferenceCode = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @return the matching Insurance Plan, or <code>null</code> if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan fetchByExternalReferenceCode(
		String externalReferenceCode) {

		return fetchByExternalReferenceCode(externalReferenceCode, true);
	}

	/**
	 * Returns the Insurance Plan where externalReferenceCode = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching Insurance Plan, or <code>null</code> if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan fetchByExternalReferenceCode(
		String externalReferenceCode, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					InsurancePlan.class)) {

			externalReferenceCode = Objects.toString(externalReferenceCode, "");

			Object[] finderArgs = null;

			if (useFinderCache) {
				finderArgs = new Object[] {externalReferenceCode};
			}

			Object result = null;

			if (useFinderCache) {
				result = finderCache.getResult(
					_finderPathFetchByExternalReferenceCode, finderArgs, this);
			}

			if (result instanceof InsurancePlan) {
				InsurancePlan insurancePlan = (InsurancePlan)result;

				if (!Objects.equals(
						externalReferenceCode,
						insurancePlan.getExternalReferenceCode())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_SELECT_INSURANCEPLAN_WHERE);

				boolean bindExternalReferenceCode = false;

				if (externalReferenceCode.isEmpty()) {
					sb.append(
						_FINDER_COLUMN_EXTERNALREFERENCECODE_EXTERNALREFERENCECODE_3);
				}
				else {
					bindExternalReferenceCode = true;

					sb.append(
						_FINDER_COLUMN_EXTERNALREFERENCECODE_EXTERNALREFERENCECODE_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					if (bindExternalReferenceCode) {
						queryPos.add(externalReferenceCode);
					}

					List<InsurancePlan> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByExternalReferenceCode,
								finderArgs, list);
						}
					}
					else {
						if (list.size() > 1) {
							Collections.sort(list, Collections.reverseOrder());

							if (_log.isWarnEnabled()) {
								if (!useFinderCache) {
									finderArgs = new Object[] {
										externalReferenceCode
									};
								}

								_log.warn(
									"InsurancePlanPersistenceImpl.fetchByExternalReferenceCode(String, boolean) with parameters (" +
										StringUtil.merge(finderArgs) +
											") yields a result set with more than 1 result. This violates the logical unique restriction. There is no order guarantee on which result is returned by this finder.");
							}
						}

						InsurancePlan insurancePlan = list.get(0);

						result = insurancePlan;

						cacheResult(insurancePlan);
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
				return (InsurancePlan)result;
			}
		}
	}

	/**
	 * Removes the Insurance Plan where externalReferenceCode = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @return the Insurance Plan that was removed
	 */
	@Override
	public InsurancePlan removeByExternalReferenceCode(
			String externalReferenceCode)
		throws NoSuchInsurancePlanException {

		InsurancePlan insurancePlan = findByExternalReferenceCode(
			externalReferenceCode);

		return remove(insurancePlan);
	}

	/**
	 * Returns the number of Insurance Plans where externalReferenceCode = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @return the number of matching Insurance Plans
	 */
	@Override
	public int countByExternalReferenceCode(String externalReferenceCode) {
		InsurancePlan insurancePlan = fetchByExternalReferenceCode(
			externalReferenceCode);

		if (insurancePlan == null) {
			return 0;
		}

		return 1;
	}

	private static final String
		_FINDER_COLUMN_EXTERNALREFERENCECODE_EXTERNALREFERENCECODE_2 =
			"insurancePlan.externalReferenceCode = ?";

	private static final String
		_FINDER_COLUMN_EXTERNALREFERENCECODE_EXTERNALREFERENCECODE_3 =
			"(insurancePlan.externalReferenceCode IS NULL OR insurancePlan.externalReferenceCode = '')";

	private FinderPath _finderPathFetchByERC_G;

	/**
	 * Returns the Insurance Plan where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchInsurancePlanException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching Insurance Plan
	 * @throws NoSuchInsurancePlanException if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan findByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchInsurancePlanException {

		InsurancePlan insurancePlan = fetchByERC_G(
			externalReferenceCode, groupId);

		if (insurancePlan == null) {
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

			throw new NoSuchInsurancePlanException(sb.toString());
		}

		return insurancePlan;
	}

	/**
	 * Returns the Insurance Plan where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching Insurance Plan, or <code>null</code> if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return fetchByERC_G(externalReferenceCode, groupId, true);
	}

	/**
	 * Returns the Insurance Plan where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching Insurance Plan, or <code>null</code> if a matching Insurance Plan could not be found
	 */
	@Override
	public InsurancePlan fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					InsurancePlan.class)) {

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

			if (result instanceof InsurancePlan) {
				InsurancePlan insurancePlan = (InsurancePlan)result;

				if (!Objects.equals(
						externalReferenceCode,
						insurancePlan.getExternalReferenceCode()) ||
					(groupId != insurancePlan.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_INSURANCEPLAN_WHERE);

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

					List<InsurancePlan> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByERC_G, finderArgs, list);
						}
					}
					else {
						InsurancePlan insurancePlan = list.get(0);

						result = insurancePlan;

						cacheResult(insurancePlan);
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
				return (InsurancePlan)result;
			}
		}
	}

	/**
	 * Removes the Insurance Plan where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the Insurance Plan that was removed
	 */
	@Override
	public InsurancePlan removeByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchInsurancePlanException {

		InsurancePlan insurancePlan = findByERC_G(
			externalReferenceCode, groupId);

		return remove(insurancePlan);
	}

	/**
	 * Returns the number of Insurance Plans where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching Insurance Plans
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		InsurancePlan insurancePlan = fetchByERC_G(
			externalReferenceCode, groupId);

		if (insurancePlan == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_2 =
		"insurancePlan.externalReferenceCode = ? AND ";

	private static final String _FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_3 =
		"(insurancePlan.externalReferenceCode IS NULL OR insurancePlan.externalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_ERC_G_GROUPID_2 =
		"insurancePlan.groupId = ?";

	public InsurancePlanPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("active", "active_");

		setDBColumnNames(dbColumnNames);

		setModelClass(InsurancePlan.class);

		setModelImplClass(InsurancePlanImpl.class);
		setModelPKClass(long.class);

		setTable(InsurancePlanTable.INSTANCE);
	}

	/**
	 * Caches the Insurance Plan in the entity cache if it is enabled.
	 *
	 * @param insurancePlan the Insurance Plan
	 */
	@Override
	public void cacheResult(InsurancePlan insurancePlan) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					insurancePlan.getCtCollectionId())) {

			entityCache.putResult(
				InsurancePlanImpl.class, insurancePlan.getPrimaryKey(),
				insurancePlan);

			finderCache.putResult(
				_finderPathFetchByUUID_G,
				new Object[] {
					insurancePlan.getUuid(), insurancePlan.getGroupId()
				},
				insurancePlan);

			finderCache.putResult(
				_finderPathFetchByExternalReferenceCode,
				new Object[] {insurancePlan.getExternalReferenceCode()},
				insurancePlan);

			finderCache.putResult(
				_finderPathFetchByERC_G,
				new Object[] {
					insurancePlan.getExternalReferenceCode(),
					insurancePlan.getGroupId()
				},
				insurancePlan);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the Insurance Plans in the entity cache if it is enabled.
	 *
	 * @param insurancePlans the Insurance Plans
	 */
	@Override
	public void cacheResult(List<InsurancePlan> insurancePlans) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (insurancePlans.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (InsurancePlan insurancePlan : insurancePlans) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						insurancePlan.getCtCollectionId())) {

				if (entityCache.getResult(
						InsurancePlanImpl.class,
						insurancePlan.getPrimaryKey()) == null) {

					cacheResult(insurancePlan);
				}
			}
		}
	}

	/**
	 * Clears the cache for all Insurance Plans.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(InsurancePlanImpl.class);

		finderCache.clearCache(InsurancePlanImpl.class);
	}

	/**
	 * Clears the cache for the Insurance Plan.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(InsurancePlan insurancePlan) {
		entityCache.removeResult(InsurancePlanImpl.class, insurancePlan);
	}

	@Override
	public void clearCache(List<InsurancePlan> insurancePlans) {
		for (InsurancePlan insurancePlan : insurancePlans) {
			entityCache.removeResult(InsurancePlanImpl.class, insurancePlan);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(InsurancePlanImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(InsurancePlanImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		InsurancePlanModelImpl insurancePlanModelImpl) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					insurancePlanModelImpl.getCtCollectionId())) {

			Object[] args = new Object[] {
				insurancePlanModelImpl.getUuid(),
				insurancePlanModelImpl.getGroupId()
			};

			finderCache.putResult(
				_finderPathFetchByUUID_G, args, insurancePlanModelImpl);

			args = new Object[] {
				insurancePlanModelImpl.getExternalReferenceCode()
			};

			finderCache.putResult(
				_finderPathFetchByExternalReferenceCode, args,
				insurancePlanModelImpl);

			args = new Object[] {
				insurancePlanModelImpl.getExternalReferenceCode(),
				insurancePlanModelImpl.getGroupId()
			};

			finderCache.putResult(
				_finderPathFetchByERC_G, args, insurancePlanModelImpl);
		}
	}

	/**
	 * Creates a new Insurance Plan with the primary key. Does not add the Insurance Plan to the database.
	 *
	 * @param insurancePlanId the primary key for the new Insurance Plan
	 * @return the new Insurance Plan
	 */
	@Override
	public InsurancePlan create(long insurancePlanId) {
		InsurancePlan insurancePlan = new InsurancePlanImpl();

		insurancePlan.setNew(true);
		insurancePlan.setPrimaryKey(insurancePlanId);

		String uuid = PortalUUIDUtil.generate();

		insurancePlan.setUuid(uuid);

		insurancePlan.setCompanyId(CompanyThreadLocal.getCompanyId());

		return insurancePlan;
	}

	/**
	 * Removes the Insurance Plan with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param insurancePlanId the primary key of the Insurance Plan
	 * @return the Insurance Plan that was removed
	 * @throws NoSuchInsurancePlanException if a Insurance Plan with the primary key could not be found
	 */
	@Override
	public InsurancePlan remove(long insurancePlanId)
		throws NoSuchInsurancePlanException {

		return remove((Serializable)insurancePlanId);
	}

	/**
	 * Removes the Insurance Plan with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the Insurance Plan
	 * @return the Insurance Plan that was removed
	 * @throws NoSuchInsurancePlanException if a Insurance Plan with the primary key could not be found
	 */
	@Override
	public InsurancePlan remove(Serializable primaryKey)
		throws NoSuchInsurancePlanException {

		Session session = null;

		try {
			session = openSession();

			InsurancePlan insurancePlan = (InsurancePlan)session.get(
				InsurancePlanImpl.class, primaryKey);

			if (insurancePlan == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchInsurancePlanException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(insurancePlan);
		}
		catch (NoSuchInsurancePlanException noSuchEntityException) {
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
	protected InsurancePlan removeImpl(InsurancePlan insurancePlan) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(insurancePlan)) {
				insurancePlan = (InsurancePlan)session.get(
					InsurancePlanImpl.class, insurancePlan.getPrimaryKeyObj());
			}

			if ((insurancePlan != null) &&
				ctPersistenceHelper.isRemove(insurancePlan)) {

				session.delete(insurancePlan);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (insurancePlan != null) {
			clearCache(insurancePlan);
		}

		return insurancePlan;
	}

	@Override
	public InsurancePlan updateImpl(InsurancePlan insurancePlan) {
		boolean isNew = insurancePlan.isNew();

		if (!(insurancePlan instanceof InsurancePlanModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(insurancePlan.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					insurancePlan);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in insurancePlan proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom InsurancePlan implementation " +
					insurancePlan.getClass());
		}

		InsurancePlanModelImpl insurancePlanModelImpl =
			(InsurancePlanModelImpl)insurancePlan;

		if (Validator.isNull(insurancePlan.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			insurancePlan.setUuid(uuid);
		}

		if (Validator.isNull(insurancePlan.getExternalReferenceCode())) {
			insurancePlan.setExternalReferenceCode(insurancePlan.getUuid());
		}
		else {
			if (!Objects.equals(
					insurancePlanModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					insurancePlan.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = insurancePlan.getCompanyId();

					long groupId = insurancePlan.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = insurancePlan.getPrimaryKey();
					}

					try {
						insurancePlan.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								InsurancePlan.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								insurancePlan.getExternalReferenceCode(),
								null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			InsurancePlan ercInsurancePlan = fetchByERC_G(
				insurancePlan.getExternalReferenceCode(),
				insurancePlan.getGroupId());

			if (isNew) {
				if (ercInsurancePlan != null) {
					throw new DuplicateInsurancePlanExternalReferenceCodeException(
						"Duplicate Insurance Plan with external reference code " +
							insurancePlan.getExternalReferenceCode() +
								" and group " + insurancePlan.getGroupId());
				}
			}
			else {
				if ((ercInsurancePlan != null) &&
					(insurancePlan.getInsurancePlanId() !=
						ercInsurancePlan.getInsurancePlanId())) {

					throw new DuplicateInsurancePlanExternalReferenceCodeException(
						"Duplicate Insurance Plan with external reference code " +
							insurancePlan.getExternalReferenceCode() +
								" and group " + insurancePlan.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (insurancePlan.getCreateDate() == null)) {
			if (serviceContext == null) {
				insurancePlan.setCreateDate(date);
			}
			else {
				insurancePlan.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!insurancePlanModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				insurancePlan.setModifiedDate(date);
			}
			else {
				insurancePlan.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(insurancePlan)) {
				if (!isNew) {
					session.evict(
						InsurancePlanImpl.class,
						insurancePlan.getPrimaryKeyObj());
				}

				session.save(insurancePlan);
			}
			else {
				insurancePlan = (InsurancePlan)session.merge(insurancePlan);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			InsurancePlanImpl.class, insurancePlanModelImpl, false, true);

		cacheUniqueFindersCache(insurancePlanModelImpl);

		if (isNew) {
			insurancePlan.setNew(false);
		}

		insurancePlan.resetOriginalValues();

		return insurancePlan;
	}

	/**
	 * Returns the Insurance Plan with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the Insurance Plan
	 * @return the Insurance Plan
	 * @throws NoSuchInsurancePlanException if a Insurance Plan with the primary key could not be found
	 */
	@Override
	public InsurancePlan findByPrimaryKey(Serializable primaryKey)
		throws NoSuchInsurancePlanException {

		InsurancePlan insurancePlan = fetchByPrimaryKey(primaryKey);

		if (insurancePlan == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchInsurancePlanException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return insurancePlan;
	}

	/**
	 * Returns the Insurance Plan with the primary key or throws a <code>NoSuchInsurancePlanException</code> if it could not be found.
	 *
	 * @param insurancePlanId the primary key of the Insurance Plan
	 * @return the Insurance Plan
	 * @throws NoSuchInsurancePlanException if a Insurance Plan with the primary key could not be found
	 */
	@Override
	public InsurancePlan findByPrimaryKey(long insurancePlanId)
		throws NoSuchInsurancePlanException {

		return findByPrimaryKey((Serializable)insurancePlanId);
	}

	/**
	 * Returns the Insurance Plan with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the Insurance Plan
	 * @return the Insurance Plan, or <code>null</code> if a Insurance Plan with the primary key could not be found
	 */
	@Override
	public InsurancePlan fetchByPrimaryKey(Serializable primaryKey) {
		if (ctPersistenceHelper.isProductionMode(
				InsurancePlan.class, primaryKey)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		InsurancePlan insurancePlan = (InsurancePlan)entityCache.getResult(
			InsurancePlanImpl.class, primaryKey);

		if (insurancePlan != null) {
			return insurancePlan;
		}

		Session session = null;

		try {
			session = openSession();

			insurancePlan = (InsurancePlan)session.get(
				InsurancePlanImpl.class, primaryKey);

			if (insurancePlan != null) {
				cacheResult(insurancePlan);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return insurancePlan;
	}

	/**
	 * Returns the Insurance Plan with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param insurancePlanId the primary key of the Insurance Plan
	 * @return the Insurance Plan, or <code>null</code> if a Insurance Plan with the primary key could not be found
	 */
	@Override
	public InsurancePlan fetchByPrimaryKey(long insurancePlanId) {
		return fetchByPrimaryKey((Serializable)insurancePlanId);
	}

	@Override
	public Map<Serializable, InsurancePlan> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (ctPersistenceHelper.isProductionMode(InsurancePlan.class)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, InsurancePlan> map =
			new HashMap<Serializable, InsurancePlan>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			InsurancePlan insurancePlan = fetchByPrimaryKey(primaryKey);

			if (insurancePlan != null) {
				map.put(primaryKey, insurancePlan);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
						InsurancePlan.class, primaryKey)) {

				InsurancePlan insurancePlan =
					(InsurancePlan)entityCache.getResult(
						InsurancePlanImpl.class, primaryKey);

				if (insurancePlan == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, insurancePlan);
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

			for (InsurancePlan insurancePlan :
					(List<InsurancePlan>)query.list()) {

				map.put(insurancePlan.getPrimaryKeyObj(), insurancePlan);

				cacheResult(insurancePlan);
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
	 * Returns all the Insurance Plans.
	 *
	 * @return the Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Insurance Plans.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @return the range of Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the Insurance Plans.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findAll(
		int start, int end,
		OrderByComparator<InsurancePlan> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Insurance Plans.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>InsurancePlanModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of Insurance Plans
	 * @param end the upper bound of the range of Insurance Plans (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of Insurance Plans
	 */
	@Override
	public List<InsurancePlan> findAll(
		int start, int end, OrderByComparator<InsurancePlan> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					InsurancePlan.class)) {

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

			List<InsurancePlan> list = null;

			if (useFinderCache) {
				list = (List<InsurancePlan>)finderCache.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_INSURANCEPLAN);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_INSURANCEPLAN;

					sql = sql.concat(InsurancePlanModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<InsurancePlan>)QueryUtil.list(
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
	 * Removes all the Insurance Plans from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (InsurancePlan insurancePlan : findAll()) {
			remove(insurancePlan);
		}
	}

	/**
	 * Returns the number of Insurance Plans.
	 *
	 * @return the number of Insurance Plans
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					InsurancePlan.class)) {

			Long count = (Long)finderCache.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(_SQL_COUNT_INSURANCEPLAN);

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
		return "insurancePlanId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_INSURANCEPLAN;
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
		return InsurancePlanModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CIBT_InsurancePlan";
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
		ctMergeColumnNames.add("planName");
		ctMergeColumnNames.add("providerName");
		ctMergeColumnNames.add("active_");
		ctMergeColumnNames.add("annualExamAllowanceCents");
		ctMergeColumnNames.add("annualFramesAllowanceCents");
		ctMergeColumnNames.add("annualLensesAllowanceCents");
		ctMergeColumnNames.add("annualContactsAllowanceCents");
		ctMergeColumnNames.add("coveragePeriodMonths");
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
			Collections.singleton("insurancePlanId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the Insurance Plan persistence.
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

		_finderPathWithPaginationFindByCompanyIdActive = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByCompanyIdActive",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"companyId", "active_"}, true);

		_finderPathWithoutPaginationFindByCompanyIdActive = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByCompanyIdActive",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"companyId", "active_"}, true);

		_finderPathCountByCompanyIdActive = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByCompanyIdActive",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"companyId", "active_"}, false);

		_finderPathWithPaginationFindByGroupIdActive = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGroupIdActive",
			new String[] {
				Long.class.getName(), Boolean.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"groupId", "active_"}, true);

		_finderPathWithoutPaginationFindByGroupIdActive = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByGroupIdActive",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"groupId", "active_"}, true);

		_finderPathCountByGroupIdActive = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByGroupIdActive",
			new String[] {Long.class.getName(), Boolean.class.getName()},
			new String[] {"groupId", "active_"}, false);

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

		_finderPathFetchByExternalReferenceCode = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByExternalReferenceCode",
			new String[] {String.class.getName()},
			new String[] {"externalReferenceCode"}, true);

		_finderPathFetchByERC_G = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"externalReferenceCode", "groupId"}, true);

		InsurancePlanUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		InsurancePlanUtil.setPersistence(null);

		entityCache.removeCache(InsurancePlanImpl.class.getName());
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

	private static final String _SQL_SELECT_INSURANCEPLAN =
		"SELECT insurancePlan FROM InsurancePlan insurancePlan";

	private static final String _SQL_SELECT_INSURANCEPLAN_WHERE =
		"SELECT insurancePlan FROM InsurancePlan insurancePlan WHERE ";

	private static final String _SQL_COUNT_INSURANCEPLAN =
		"SELECT COUNT(insurancePlan) FROM InsurancePlan insurancePlan";

	private static final String _SQL_COUNT_INSURANCEPLAN_WHERE =
		"SELECT COUNT(insurancePlan) FROM InsurancePlan insurancePlan WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"insurancePlan.insurancePlanId";

	private static final String _FILTER_SQL_SELECT_INSURANCEPLAN_WHERE =
		"SELECT DISTINCT {insurancePlan.*} FROM CIBT_InsurancePlan insurancePlan WHERE ";

	private static final String
		_FILTER_SQL_SELECT_INSURANCEPLAN_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {CIBT_InsurancePlan.*} FROM (SELECT DISTINCT insurancePlan.insurancePlanId FROM CIBT_InsurancePlan insurancePlan WHERE ";

	private static final String
		_FILTER_SQL_SELECT_INSURANCEPLAN_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN CIBT_InsurancePlan ON TEMP_TABLE.insurancePlanId = CIBT_InsurancePlan.insurancePlanId";

	private static final String _FILTER_SQL_COUNT_INSURANCEPLAN_WHERE =
		"SELECT COUNT(DISTINCT insurancePlan.insurancePlanId) AS COUNT_VALUE FROM CIBT_InsurancePlan insurancePlan WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "insurancePlan";

	private static final String _FILTER_ENTITY_TABLE = "CIBT_InsurancePlan";

	private static final String _ORDER_BY_ENTITY_ALIAS = "insurancePlan.";

	private static final String _ORDER_BY_ENTITY_TABLE = "CIBT_InsurancePlan.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No InsurancePlan exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No InsurancePlan exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		InsurancePlanPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "active"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}