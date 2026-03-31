/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.clarityvisionsolutions.insurance.benefits.tracker.service.persistence.impl;

import com.clarityvisionsolutions.insurance.benefits.tracker.exception.DuplicateBenefitUsageExternalReferenceCodeException;
import com.clarityvisionsolutions.insurance.benefits.tracker.exception.NoSuchBenefitUsageException;
import com.clarityvisionsolutions.insurance.benefits.tracker.model.BenefitUsage;
import com.clarityvisionsolutions.insurance.benefits.tracker.model.BenefitUsageTable;
import com.clarityvisionsolutions.insurance.benefits.tracker.model.impl.BenefitUsageImpl;
import com.clarityvisionsolutions.insurance.benefits.tracker.model.impl.BenefitUsageModelImpl;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.persistence.BenefitUsagePersistence;
import com.clarityvisionsolutions.insurance.benefits.tracker.service.persistence.BenefitUsageUtil;
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

import java.sql.Timestamp;

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
 * The persistence implementation for the Benefit Usage service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = BenefitUsagePersistence.class)
public class BenefitUsagePersistenceImpl
	extends BasePersistenceImpl<BenefitUsage>
	implements BenefitUsagePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>BenefitUsageUtil</code> to access the Benefit Usage persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		BenefitUsageImpl.class.getName();

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
	 * Returns all the Benefit Usages where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByUuid(String uuid) {
		return findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Benefit Usages where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @return the range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByUuid(String uuid, int start, int end) {
		return findByUuid(uuid, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BenefitUsage.class)) {

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

			List<BenefitUsage> list = null;

			if (useFinderCache) {
				list = (List<BenefitUsage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BenefitUsage benefitUsage : list) {
						if (!uuid.equals(benefitUsage.getUuid())) {
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

				sb.append(_SQL_SELECT_BENEFITUSAGE_WHERE);

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
					sb.append(BenefitUsageModelImpl.ORDER_BY_JPQL);
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

					list = (List<BenefitUsage>)QueryUtil.list(
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
	 * Returns the first Benefit Usage in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Benefit Usage
	 * @throws NoSuchBenefitUsageException if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage findByUuid_First(
			String uuid, OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = fetchByUuid_First(uuid, orderByComparator);

		if (benefitUsage != null) {
			return benefitUsage;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchBenefitUsageException(sb.toString());
	}

	/**
	 * Returns the first Benefit Usage in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Benefit Usage, or <code>null</code> if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage fetchByUuid_First(
		String uuid, OrderByComparator<BenefitUsage> orderByComparator) {

		List<BenefitUsage> list = findByUuid(uuid, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Benefit Usage in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Benefit Usage
	 * @throws NoSuchBenefitUsageException if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage findByUuid_Last(
			String uuid, OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = fetchByUuid_Last(uuid, orderByComparator);

		if (benefitUsage != null) {
			return benefitUsage;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append("}");

		throw new NoSuchBenefitUsageException(sb.toString());
	}

	/**
	 * Returns the last Benefit Usage in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Benefit Usage, or <code>null</code> if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage fetchByUuid_Last(
		String uuid, OrderByComparator<BenefitUsage> orderByComparator) {

		int count = countByUuid(uuid);

		if (count == 0) {
			return null;
		}

		List<BenefitUsage> list = findByUuid(
			uuid, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Benefit Usages before and after the current Benefit Usage in the ordered set where uuid = &#63;.
	 *
	 * @param benefitUsageId the primary key of the current Benefit Usage
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Benefit Usage
	 * @throws NoSuchBenefitUsageException if a Benefit Usage with the primary key could not be found
	 */
	@Override
	public BenefitUsage[] findByUuid_PrevAndNext(
			long benefitUsageId, String uuid,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		uuid = Objects.toString(uuid, "");

		BenefitUsage benefitUsage = findByPrimaryKey(benefitUsageId);

		Session session = null;

		try {
			session = openSession();

			BenefitUsage[] array = new BenefitUsageImpl[3];

			array[0] = getByUuid_PrevAndNext(
				session, benefitUsage, uuid, orderByComparator, true);

			array[1] = benefitUsage;

			array[2] = getByUuid_PrevAndNext(
				session, benefitUsage, uuid, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected BenefitUsage getByUuid_PrevAndNext(
		Session session, BenefitUsage benefitUsage, String uuid,
		OrderByComparator<BenefitUsage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_BENEFITUSAGE_WHERE);

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
			sb.append(BenefitUsageModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(benefitUsage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BenefitUsage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Benefit Usages where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		for (BenefitUsage benefitUsage :
				findByUuid(uuid, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(benefitUsage);
		}
	}

	/**
	 * Returns the number of Benefit Usages where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching Benefit Usages
	 */
	@Override
	public int countByUuid(String uuid) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BenefitUsage.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid;

			Object[] finderArgs = new Object[] {uuid};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_BENEFITUSAGE_WHERE);

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
		"benefitUsage.uuid = ?";

	private static final String _FINDER_COLUMN_UUID_UUID_3 =
		"(benefitUsage.uuid IS NULL OR benefitUsage.uuid = '')";

	private FinderPath _finderPathFetchByUUID_G;

	/**
	 * Returns the Benefit Usage where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchBenefitUsageException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching Benefit Usage
	 * @throws NoSuchBenefitUsageException if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage findByUUID_G(String uuid, long groupId)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = fetchByUUID_G(uuid, groupId);

		if (benefitUsage == null) {
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

			throw new NoSuchBenefitUsageException(sb.toString());
		}

		return benefitUsage;
	}

	/**
	 * Returns the Benefit Usage where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching Benefit Usage, or <code>null</code> if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage fetchByUUID_G(String uuid, long groupId) {
		return fetchByUUID_G(uuid, groupId, true);
	}

	/**
	 * Returns the Benefit Usage where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching Benefit Usage, or <code>null</code> if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BenefitUsage.class)) {

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

			if (result instanceof BenefitUsage) {
				BenefitUsage benefitUsage = (BenefitUsage)result;

				if (!Objects.equals(uuid, benefitUsage.getUuid()) ||
					(groupId != benefitUsage.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_BENEFITUSAGE_WHERE);

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

					List<BenefitUsage> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByUUID_G, finderArgs, list);
						}
					}
					else {
						BenefitUsage benefitUsage = list.get(0);

						result = benefitUsage;

						cacheResult(benefitUsage);
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
				return (BenefitUsage)result;
			}
		}
	}

	/**
	 * Removes the Benefit Usage where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the Benefit Usage that was removed
	 */
	@Override
	public BenefitUsage removeByUUID_G(String uuid, long groupId)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = findByUUID_G(uuid, groupId);

		return remove(benefitUsage);
	}

	/**
	 * Returns the number of Benefit Usages where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching Benefit Usages
	 */
	@Override
	public int countByUUID_G(String uuid, long groupId) {
		BenefitUsage benefitUsage = fetchByUUID_G(uuid, groupId);

		if (benefitUsage == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_UUID_G_UUID_2 =
		"benefitUsage.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_G_UUID_3 =
		"(benefitUsage.uuid IS NULL OR benefitUsage.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_G_GROUPID_2 =
		"benefitUsage.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByUuid_C;
	private FinderPath _finderPathWithoutPaginationFindByUuid_C;
	private FinderPath _finderPathCountByUuid_C;

	/**
	 * Returns all the Benefit Usages where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByUuid_C(String uuid, long companyId) {
		return findByUuid_C(
			uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Benefit Usages where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @return the range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BenefitUsage.class)) {

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

			List<BenefitUsage> list = null;

			if (useFinderCache) {
				list = (List<BenefitUsage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BenefitUsage benefitUsage : list) {
						if (!uuid.equals(benefitUsage.getUuid()) ||
							(companyId != benefitUsage.getCompanyId())) {

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

				sb.append(_SQL_SELECT_BENEFITUSAGE_WHERE);

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
					sb.append(BenefitUsageModelImpl.ORDER_BY_JPQL);
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

					list = (List<BenefitUsage>)QueryUtil.list(
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
	 * Returns the first Benefit Usage in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Benefit Usage
	 * @throws NoSuchBenefitUsageException if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = fetchByUuid_C_First(
			uuid, companyId, orderByComparator);

		if (benefitUsage != null) {
			return benefitUsage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchBenefitUsageException(sb.toString());
	}

	/**
	 * Returns the first Benefit Usage in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Benefit Usage, or <code>null</code> if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<BenefitUsage> orderByComparator) {

		List<BenefitUsage> list = findByUuid_C(
			uuid, companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Benefit Usage in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Benefit Usage
	 * @throws NoSuchBenefitUsageException if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);

		if (benefitUsage != null) {
			return benefitUsage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("uuid=");
		sb.append(uuid);

		sb.append(", companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchBenefitUsageException(sb.toString());
	}

	/**
	 * Returns the last Benefit Usage in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Benefit Usage, or <code>null</code> if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<BenefitUsage> orderByComparator) {

		int count = countByUuid_C(uuid, companyId);

		if (count == 0) {
			return null;
		}

		List<BenefitUsage> list = findByUuid_C(
			uuid, companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Benefit Usages before and after the current Benefit Usage in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param benefitUsageId the primary key of the current Benefit Usage
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Benefit Usage
	 * @throws NoSuchBenefitUsageException if a Benefit Usage with the primary key could not be found
	 */
	@Override
	public BenefitUsage[] findByUuid_C_PrevAndNext(
			long benefitUsageId, String uuid, long companyId,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		uuid = Objects.toString(uuid, "");

		BenefitUsage benefitUsage = findByPrimaryKey(benefitUsageId);

		Session session = null;

		try {
			session = openSession();

			BenefitUsage[] array = new BenefitUsageImpl[3];

			array[0] = getByUuid_C_PrevAndNext(
				session, benefitUsage, uuid, companyId, orderByComparator,
				true);

			array[1] = benefitUsage;

			array[2] = getByUuid_C_PrevAndNext(
				session, benefitUsage, uuid, companyId, orderByComparator,
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

	protected BenefitUsage getByUuid_C_PrevAndNext(
		Session session, BenefitUsage benefitUsage, String uuid, long companyId,
		OrderByComparator<BenefitUsage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_BENEFITUSAGE_WHERE);

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
			sb.append(BenefitUsageModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(benefitUsage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BenefitUsage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Benefit Usages where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		for (BenefitUsage benefitUsage :
				findByUuid_C(
					uuid, companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(benefitUsage);
		}
	}

	/**
	 * Returns the number of Benefit Usages where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching Benefit Usages
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BenefitUsage.class)) {

			uuid = Objects.toString(uuid, "");

			FinderPath finderPath = _finderPathCountByUuid_C;

			Object[] finderArgs = new Object[] {uuid, companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_BENEFITUSAGE_WHERE);

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
		"benefitUsage.uuid = ? AND ";

	private static final String _FINDER_COLUMN_UUID_C_UUID_3 =
		"(benefitUsage.uuid IS NULL OR benefitUsage.uuid = '') AND ";

	private static final String _FINDER_COLUMN_UUID_C_COMPANYID_2 =
		"benefitUsage.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByGroupId;
	private FinderPath _finderPathWithoutPaginationFindByGroupId;
	private FinderPath _finderPathCountByGroupId;

	/**
	 * Returns all the Benefit Usages where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByGroupId(long groupId) {
		return findByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Benefit Usages where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @return the range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByGroupId(long groupId, int start, int end) {
		return findByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator) {

		return findByGroupId(groupId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BenefitUsage.class)) {

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

			List<BenefitUsage> list = null;

			if (useFinderCache) {
				list = (List<BenefitUsage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BenefitUsage benefitUsage : list) {
						if (groupId != benefitUsage.getGroupId()) {
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

				sb.append(_SQL_SELECT_BENEFITUSAGE_WHERE);

				sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BenefitUsageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					list = (List<BenefitUsage>)QueryUtil.list(
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
	 * Returns the first Benefit Usage in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Benefit Usage
	 * @throws NoSuchBenefitUsageException if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage findByGroupId_First(
			long groupId, OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = fetchByGroupId_First(
			groupId, orderByComparator);

		if (benefitUsage != null) {
			return benefitUsage;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchBenefitUsageException(sb.toString());
	}

	/**
	 * Returns the first Benefit Usage in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Benefit Usage, or <code>null</code> if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage fetchByGroupId_First(
		long groupId, OrderByComparator<BenefitUsage> orderByComparator) {

		List<BenefitUsage> list = findByGroupId(
			groupId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Benefit Usage in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Benefit Usage
	 * @throws NoSuchBenefitUsageException if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage findByGroupId_Last(
			long groupId, OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = fetchByGroupId_Last(
			groupId, orderByComparator);

		if (benefitUsage != null) {
			return benefitUsage;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append("}");

		throw new NoSuchBenefitUsageException(sb.toString());
	}

	/**
	 * Returns the last Benefit Usage in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Benefit Usage, or <code>null</code> if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage fetchByGroupId_Last(
		long groupId, OrderByComparator<BenefitUsage> orderByComparator) {

		int count = countByGroupId(groupId);

		if (count == 0) {
			return null;
		}

		List<BenefitUsage> list = findByGroupId(
			groupId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Benefit Usages before and after the current Benefit Usage in the ordered set where groupId = &#63;.
	 *
	 * @param benefitUsageId the primary key of the current Benefit Usage
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Benefit Usage
	 * @throws NoSuchBenefitUsageException if a Benefit Usage with the primary key could not be found
	 */
	@Override
	public BenefitUsage[] findByGroupId_PrevAndNext(
			long benefitUsageId, long groupId,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = findByPrimaryKey(benefitUsageId);

		Session session = null;

		try {
			session = openSession();

			BenefitUsage[] array = new BenefitUsageImpl[3];

			array[0] = getByGroupId_PrevAndNext(
				session, benefitUsage, groupId, orderByComparator, true);

			array[1] = benefitUsage;

			array[2] = getByGroupId_PrevAndNext(
				session, benefitUsage, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected BenefitUsage getByGroupId_PrevAndNext(
		Session session, BenefitUsage benefitUsage, long groupId,
		OrderByComparator<BenefitUsage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_BENEFITUSAGE_WHERE);

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
			sb.append(BenefitUsageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(benefitUsage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BenefitUsage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the Benefit Usages that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching Benefit Usages that the user has permission to view
	 */
	@Override
	public List<BenefitUsage> filterFindByGroupId(long groupId) {
		return filterFindByGroupId(
			groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Benefit Usages that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @return the range of matching Benefit Usages that the user has permission to view
	 */
	@Override
	public List<BenefitUsage> filterFindByGroupId(
		long groupId, int start, int end) {

		return filterFindByGroupId(groupId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Benefit Usages that the user has permission to view
	 */
	@Override
	public List<BenefitUsage> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_BENEFITUSAGE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_BENEFITUSAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_BENEFITUSAGE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BenefitUsageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BenefitUsageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BenefitUsage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, BenefitUsageImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, BenefitUsageImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			return (List<BenefitUsage>)QueryUtil.list(
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
	 * Returns the Benefit Usages before and after the current Benefit Usage in the ordered set of Benefit Usages that the user has permission to view where groupId = &#63;.
	 *
	 * @param benefitUsageId the primary key of the current Benefit Usage
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Benefit Usage
	 * @throws NoSuchBenefitUsageException if a Benefit Usage with the primary key could not be found
	 */
	@Override
	public BenefitUsage[] filterFindByGroupId_PrevAndNext(
			long benefitUsageId, long groupId,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByGroupId_PrevAndNext(
				benefitUsageId, groupId, orderByComparator);
		}

		BenefitUsage benefitUsage = findByPrimaryKey(benefitUsageId);

		Session session = null;

		try {
			session = openSession();

			BenefitUsage[] array = new BenefitUsageImpl[3];

			array[0] = filterGetByGroupId_PrevAndNext(
				session, benefitUsage, groupId, orderByComparator, true);

			array[1] = benefitUsage;

			array[2] = filterGetByGroupId_PrevAndNext(
				session, benefitUsage, groupId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected BenefitUsage filterGetByGroupId_PrevAndNext(
		Session session, BenefitUsage benefitUsage, long groupId,
		OrderByComparator<BenefitUsage> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_BENEFITUSAGE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_BENEFITUSAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_BENEFITUSAGE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BenefitUsageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BenefitUsageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BenefitUsage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, BenefitUsageImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, BenefitUsageImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(benefitUsage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BenefitUsage> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Benefit Usages where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		for (BenefitUsage benefitUsage :
				findByGroupId(
					groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(benefitUsage);
		}
	}

	/**
	 * Returns the number of Benefit Usages where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching Benefit Usages
	 */
	@Override
	public int countByGroupId(long groupId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BenefitUsage.class)) {

			FinderPath finderPath = _finderPathCountByGroupId;

			Object[] finderArgs = new Object[] {groupId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_BENEFITUSAGE_WHERE);

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
	 * Returns the number of Benefit Usages that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching Benefit Usages that the user has permission to view
	 */
	@Override
	public int filterCountByGroupId(long groupId) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByGroupId(groupId);
		}

		StringBundler sb = new StringBundler(2);

		sb.append(_FILTER_SQL_COUNT_BENEFITUSAGE_WHERE);

		sb.append(_FINDER_COLUMN_GROUPID_GROUPID_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BenefitUsage.class.getName(),
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
		"benefitUsage.groupId = ?";

	private FinderPath _finderPathWithPaginationFindByCompanyId;
	private FinderPath _finderPathWithoutPaginationFindByCompanyId;
	private FinderPath _finderPathCountByCompanyId;

	/**
	 * Returns all the Benefit Usages where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByCompanyId(long companyId) {
		return findByCompanyId(
			companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Benefit Usages where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @return the range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByCompanyId(
		long companyId, int start, int end) {

		return findByCompanyId(companyId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator) {

		return findByCompanyId(companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BenefitUsage.class)) {

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

			List<BenefitUsage> list = null;

			if (useFinderCache) {
				list = (List<BenefitUsage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BenefitUsage benefitUsage : list) {
						if (companyId != benefitUsage.getCompanyId()) {
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

				sb.append(_SQL_SELECT_BENEFITUSAGE_WHERE);

				sb.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BenefitUsageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					list = (List<BenefitUsage>)QueryUtil.list(
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
	 * Returns the first Benefit Usage in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Benefit Usage
	 * @throws NoSuchBenefitUsageException if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage findByCompanyId_First(
			long companyId, OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = fetchByCompanyId_First(
			companyId, orderByComparator);

		if (benefitUsage != null) {
			return benefitUsage;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchBenefitUsageException(sb.toString());
	}

	/**
	 * Returns the first Benefit Usage in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Benefit Usage, or <code>null</code> if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage fetchByCompanyId_First(
		long companyId, OrderByComparator<BenefitUsage> orderByComparator) {

		List<BenefitUsage> list = findByCompanyId(
			companyId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Benefit Usage in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Benefit Usage
	 * @throws NoSuchBenefitUsageException if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage findByCompanyId_Last(
			long companyId, OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = fetchByCompanyId_Last(
			companyId, orderByComparator);

		if (benefitUsage != null) {
			return benefitUsage;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append("}");

		throw new NoSuchBenefitUsageException(sb.toString());
	}

	/**
	 * Returns the last Benefit Usage in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Benefit Usage, or <code>null</code> if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage fetchByCompanyId_Last(
		long companyId, OrderByComparator<BenefitUsage> orderByComparator) {

		int count = countByCompanyId(companyId);

		if (count == 0) {
			return null;
		}

		List<BenefitUsage> list = findByCompanyId(
			companyId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Benefit Usages before and after the current Benefit Usage in the ordered set where companyId = &#63;.
	 *
	 * @param benefitUsageId the primary key of the current Benefit Usage
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Benefit Usage
	 * @throws NoSuchBenefitUsageException if a Benefit Usage with the primary key could not be found
	 */
	@Override
	public BenefitUsage[] findByCompanyId_PrevAndNext(
			long benefitUsageId, long companyId,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = findByPrimaryKey(benefitUsageId);

		Session session = null;

		try {
			session = openSession();

			BenefitUsage[] array = new BenefitUsageImpl[3];

			array[0] = getByCompanyId_PrevAndNext(
				session, benefitUsage, companyId, orderByComparator, true);

			array[1] = benefitUsage;

			array[2] = getByCompanyId_PrevAndNext(
				session, benefitUsage, companyId, orderByComparator, false);

			return array;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	protected BenefitUsage getByCompanyId_PrevAndNext(
		Session session, BenefitUsage benefitUsage, long companyId,
		OrderByComparator<BenefitUsage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_BENEFITUSAGE_WHERE);

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
			sb.append(BenefitUsageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(companyId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(benefitUsage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BenefitUsage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Benefit Usages where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	@Override
	public void removeByCompanyId(long companyId) {
		for (BenefitUsage benefitUsage :
				findByCompanyId(
					companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			remove(benefitUsage);
		}
	}

	/**
	 * Returns the number of Benefit Usages where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching Benefit Usages
	 */
	@Override
	public int countByCompanyId(long companyId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BenefitUsage.class)) {

			FinderPath finderPath = _finderPathCountByCompanyId;

			Object[] finderArgs = new Object[] {companyId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_BENEFITUSAGE_WHERE);

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
		"benefitUsage.companyId = ?";

	private FinderPath _finderPathWithPaginationFindByG_S;
	private FinderPath _finderPathWithoutPaginationFindByG_S;
	private FinderPath _finderPathCountByG_S;

	/**
	 * Returns all the Benefit Usages where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByG_S(long groupId, int status) {
		return findByG_S(
			groupId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Benefit Usages where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @return the range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByG_S(
		long groupId, int status, int start, int end) {

		return findByG_S(groupId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator) {

		return findByG_S(groupId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BenefitUsage.class)) {

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

			List<BenefitUsage> list = null;

			if (useFinderCache) {
				list = (List<BenefitUsage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BenefitUsage benefitUsage : list) {
						if ((groupId != benefitUsage.getGroupId()) ||
							(status != benefitUsage.getStatus())) {

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

				sb.append(_SQL_SELECT_BENEFITUSAGE_WHERE);

				sb.append(_FINDER_COLUMN_G_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BenefitUsageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(status);

					list = (List<BenefitUsage>)QueryUtil.list(
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
	 * Returns the first Benefit Usage in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Benefit Usage
	 * @throws NoSuchBenefitUsageException if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage findByG_S_First(
			long groupId, int status,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = fetchByG_S_First(
			groupId, status, orderByComparator);

		if (benefitUsage != null) {
			return benefitUsage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchBenefitUsageException(sb.toString());
	}

	/**
	 * Returns the first Benefit Usage in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Benefit Usage, or <code>null</code> if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage fetchByG_S_First(
		long groupId, int status,
		OrderByComparator<BenefitUsage> orderByComparator) {

		List<BenefitUsage> list = findByG_S(
			groupId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Benefit Usage in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Benefit Usage
	 * @throws NoSuchBenefitUsageException if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage findByG_S_Last(
			long groupId, int status,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = fetchByG_S_Last(
			groupId, status, orderByComparator);

		if (benefitUsage != null) {
			return benefitUsage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchBenefitUsageException(sb.toString());
	}

	/**
	 * Returns the last Benefit Usage in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Benefit Usage, or <code>null</code> if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage fetchByG_S_Last(
		long groupId, int status,
		OrderByComparator<BenefitUsage> orderByComparator) {

		int count = countByG_S(groupId, status);

		if (count == 0) {
			return null;
		}

		List<BenefitUsage> list = findByG_S(
			groupId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Benefit Usages before and after the current Benefit Usage in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param benefitUsageId the primary key of the current Benefit Usage
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Benefit Usage
	 * @throws NoSuchBenefitUsageException if a Benefit Usage with the primary key could not be found
	 */
	@Override
	public BenefitUsage[] findByG_S_PrevAndNext(
			long benefitUsageId, long groupId, int status,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = findByPrimaryKey(benefitUsageId);

		Session session = null;

		try {
			session = openSession();

			BenefitUsage[] array = new BenefitUsageImpl[3];

			array[0] = getByG_S_PrevAndNext(
				session, benefitUsage, groupId, status, orderByComparator,
				true);

			array[1] = benefitUsage;

			array[2] = getByG_S_PrevAndNext(
				session, benefitUsage, groupId, status, orderByComparator,
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

	protected BenefitUsage getByG_S_PrevAndNext(
		Session session, BenefitUsage benefitUsage, long groupId, int status,
		OrderByComparator<BenefitUsage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_BENEFITUSAGE_WHERE);

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
			sb.append(BenefitUsageModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(benefitUsage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BenefitUsage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the Benefit Usages that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching Benefit Usages that the user has permission to view
	 */
	@Override
	public List<BenefitUsage> filterFindByG_S(long groupId, int status) {
		return filterFindByG_S(
			groupId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Benefit Usages that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @return the range of matching Benefit Usages that the user has permission to view
	 */
	@Override
	public List<BenefitUsage> filterFindByG_S(
		long groupId, int status, int start, int end) {

		return filterFindByG_S(groupId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages that the user has permissions to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Benefit Usages that the user has permission to view
	 */
	@Override
	public List<BenefitUsage> filterFindByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_BENEFITUSAGE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_BENEFITUSAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_BENEFITUSAGE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BenefitUsageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BenefitUsageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BenefitUsage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, BenefitUsageImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, BenefitUsageImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(status);

			return (List<BenefitUsage>)QueryUtil.list(
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
	 * Returns the Benefit Usages before and after the current Benefit Usage in the ordered set of Benefit Usages that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param benefitUsageId the primary key of the current Benefit Usage
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Benefit Usage
	 * @throws NoSuchBenefitUsageException if a Benefit Usage with the primary key could not be found
	 */
	@Override
	public BenefitUsage[] filterFindByG_S_PrevAndNext(
			long benefitUsageId, long groupId, int status,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_S_PrevAndNext(
				benefitUsageId, groupId, status, orderByComparator);
		}

		BenefitUsage benefitUsage = findByPrimaryKey(benefitUsageId);

		Session session = null;

		try {
			session = openSession();

			BenefitUsage[] array = new BenefitUsageImpl[3];

			array[0] = filterGetByG_S_PrevAndNext(
				session, benefitUsage, groupId, status, orderByComparator,
				true);

			array[1] = benefitUsage;

			array[2] = filterGetByG_S_PrevAndNext(
				session, benefitUsage, groupId, status, orderByComparator,
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

	protected BenefitUsage filterGetByG_S_PrevAndNext(
		Session session, BenefitUsage benefitUsage, long groupId, int status,
		OrderByComparator<BenefitUsage> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_BENEFITUSAGE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_BENEFITUSAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_BENEFITUSAGE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BenefitUsageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BenefitUsageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BenefitUsage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, BenefitUsageImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, BenefitUsageImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(benefitUsage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BenefitUsage> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Benefit Usages where groupId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 */
	@Override
	public void removeByG_S(long groupId, int status) {
		for (BenefitUsage benefitUsage :
				findByG_S(
					groupId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(benefitUsage);
		}
	}

	/**
	 * Returns the number of Benefit Usages where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching Benefit Usages
	 */
	@Override
	public int countByG_S(long groupId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BenefitUsage.class)) {

			FinderPath finderPath = _finderPathCountByG_S;

			Object[] finderArgs = new Object[] {groupId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_BENEFITUSAGE_WHERE);

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
	 * Returns the number of Benefit Usages that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching Benefit Usages that the user has permission to view
	 */
	@Override
	public int filterCountByG_S(long groupId, int status) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_S(groupId, status);
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_BENEFITUSAGE_WHERE);

		sb.append(_FINDER_COLUMN_G_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_S_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BenefitUsage.class.getName(),
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
		"benefitUsage.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_S_STATUS_2 =
		"benefitUsage.status = ?";

	private FinderPath _finderPathWithPaginationFindByG_NotS;
	private FinderPath _finderPathWithPaginationCountByG_NotS;

	/**
	 * Returns all the Benefit Usages where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByG_NotS(long groupId, int status) {
		return findByG_NotS(
			groupId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Benefit Usages where groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @return the range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByG_NotS(
		long groupId, int status, int start, int end) {

		return findByG_NotS(groupId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages where groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByG_NotS(
		long groupId, int status, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator) {

		return findByG_NotS(
			groupId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages where groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByG_NotS(
		long groupId, int status, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BenefitUsage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByG_NotS;
			finderArgs = new Object[] {
				groupId, status, start, end, orderByComparator
			};

			List<BenefitUsage> list = null;

			if (useFinderCache) {
				list = (List<BenefitUsage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BenefitUsage benefitUsage : list) {
						if ((groupId != benefitUsage.getGroupId()) ||
							(status == benefitUsage.getStatus())) {

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

				sb.append(_SQL_SELECT_BENEFITUSAGE_WHERE);

				sb.append(_FINDER_COLUMN_G_NOTS_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_NOTS_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BenefitUsageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(status);

					list = (List<BenefitUsage>)QueryUtil.list(
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
	 * Returns the first Benefit Usage in the ordered set where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Benefit Usage
	 * @throws NoSuchBenefitUsageException if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage findByG_NotS_First(
			long groupId, int status,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = fetchByG_NotS_First(
			groupId, status, orderByComparator);

		if (benefitUsage != null) {
			return benefitUsage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchBenefitUsageException(sb.toString());
	}

	/**
	 * Returns the first Benefit Usage in the ordered set where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Benefit Usage, or <code>null</code> if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage fetchByG_NotS_First(
		long groupId, int status,
		OrderByComparator<BenefitUsage> orderByComparator) {

		List<BenefitUsage> list = findByG_NotS(
			groupId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Benefit Usage in the ordered set where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Benefit Usage
	 * @throws NoSuchBenefitUsageException if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage findByG_NotS_Last(
			long groupId, int status,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = fetchByG_NotS_Last(
			groupId, status, orderByComparator);

		if (benefitUsage != null) {
			return benefitUsage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchBenefitUsageException(sb.toString());
	}

	/**
	 * Returns the last Benefit Usage in the ordered set where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Benefit Usage, or <code>null</code> if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage fetchByG_NotS_Last(
		long groupId, int status,
		OrderByComparator<BenefitUsage> orderByComparator) {

		int count = countByG_NotS(groupId, status);

		if (count == 0) {
			return null;
		}

		List<BenefitUsage> list = findByG_NotS(
			groupId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Benefit Usages before and after the current Benefit Usage in the ordered set where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param benefitUsageId the primary key of the current Benefit Usage
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Benefit Usage
	 * @throws NoSuchBenefitUsageException if a Benefit Usage with the primary key could not be found
	 */
	@Override
	public BenefitUsage[] findByG_NotS_PrevAndNext(
			long benefitUsageId, long groupId, int status,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = findByPrimaryKey(benefitUsageId);

		Session session = null;

		try {
			session = openSession();

			BenefitUsage[] array = new BenefitUsageImpl[3];

			array[0] = getByG_NotS_PrevAndNext(
				session, benefitUsage, groupId, status, orderByComparator,
				true);

			array[1] = benefitUsage;

			array[2] = getByG_NotS_PrevAndNext(
				session, benefitUsage, groupId, status, orderByComparator,
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

	protected BenefitUsage getByG_NotS_PrevAndNext(
		Session session, BenefitUsage benefitUsage, long groupId, int status,
		OrderByComparator<BenefitUsage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_BENEFITUSAGE_WHERE);

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
			sb.append(BenefitUsageModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(benefitUsage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BenefitUsage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the Benefit Usages that the user has permission to view where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching Benefit Usages that the user has permission to view
	 */
	@Override
	public List<BenefitUsage> filterFindByG_NotS(long groupId, int status) {
		return filterFindByG_NotS(
			groupId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Benefit Usages that the user has permission to view where groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @return the range of matching Benefit Usages that the user has permission to view
	 */
	@Override
	public List<BenefitUsage> filterFindByG_NotS(
		long groupId, int status, int start, int end) {

		return filterFindByG_NotS(groupId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages that the user has permissions to view where groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Benefit Usages that the user has permission to view
	 */
	@Override
	public List<BenefitUsage> filterFindByG_NotS(
		long groupId, int status, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_BENEFITUSAGE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_BENEFITUSAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_NOTS_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_BENEFITUSAGE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BenefitUsageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BenefitUsageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BenefitUsage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, BenefitUsageImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, BenefitUsageImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(status);

			return (List<BenefitUsage>)QueryUtil.list(
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
	 * Returns the Benefit Usages before and after the current Benefit Usage in the ordered set of Benefit Usages that the user has permission to view where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param benefitUsageId the primary key of the current Benefit Usage
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Benefit Usage
	 * @throws NoSuchBenefitUsageException if a Benefit Usage with the primary key could not be found
	 */
	@Override
	public BenefitUsage[] filterFindByG_NotS_PrevAndNext(
			long benefitUsageId, long groupId, int status,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_NotS_PrevAndNext(
				benefitUsageId, groupId, status, orderByComparator);
		}

		BenefitUsage benefitUsage = findByPrimaryKey(benefitUsageId);

		Session session = null;

		try {
			session = openSession();

			BenefitUsage[] array = new BenefitUsageImpl[3];

			array[0] = filterGetByG_NotS_PrevAndNext(
				session, benefitUsage, groupId, status, orderByComparator,
				true);

			array[1] = benefitUsage;

			array[2] = filterGetByG_NotS_PrevAndNext(
				session, benefitUsage, groupId, status, orderByComparator,
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

	protected BenefitUsage filterGetByG_NotS_PrevAndNext(
		Session session, BenefitUsage benefitUsage, long groupId, int status,
		OrderByComparator<BenefitUsage> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_BENEFITUSAGE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_BENEFITUSAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_NOTS_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_BENEFITUSAGE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BenefitUsageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BenefitUsageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BenefitUsage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, BenefitUsageImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, BenefitUsageImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(benefitUsage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BenefitUsage> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Benefit Usages where groupId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 */
	@Override
	public void removeByG_NotS(long groupId, int status) {
		for (BenefitUsage benefitUsage :
				findByG_NotS(
					groupId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(benefitUsage);
		}
	}

	/**
	 * Returns the number of Benefit Usages where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching Benefit Usages
	 */
	@Override
	public int countByG_NotS(long groupId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BenefitUsage.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByG_NotS;

			Object[] finderArgs = new Object[] {groupId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_BENEFITUSAGE_WHERE);

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
	 * Returns the number of Benefit Usages that the user has permission to view where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching Benefit Usages that the user has permission to view
	 */
	@Override
	public int filterCountByG_NotS(long groupId, int status) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_NotS(groupId, status);
		}

		StringBundler sb = new StringBundler(3);

		sb.append(_FILTER_SQL_COUNT_BENEFITUSAGE_WHERE);

		sb.append(_FINDER_COLUMN_G_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_NOTS_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BenefitUsage.class.getName(),
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
		"benefitUsage.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_NOTS_STATUS_2 =
		"benefitUsage.status != ?";

	private FinderPath _finderPathWithPaginationFindByPE_S;
	private FinderPath _finderPathWithoutPaginationFindByPE_S;
	private FinderPath _finderPathCountByPE_S;

	/**
	 * Returns all the Benefit Usages where planEnrollmentId = &#63; and status = &#63;.
	 *
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @return the matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByPE_S(long planEnrollmentId, int status) {
		return findByPE_S(
			planEnrollmentId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the Benefit Usages where planEnrollmentId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @return the range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByPE_S(
		long planEnrollmentId, int status, int start, int end) {

		return findByPE_S(planEnrollmentId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages where planEnrollmentId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByPE_S(
		long planEnrollmentId, int status, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator) {

		return findByPE_S(
			planEnrollmentId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages where planEnrollmentId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByPE_S(
		long planEnrollmentId, int status, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BenefitUsage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByPE_S;
					finderArgs = new Object[] {planEnrollmentId, status};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByPE_S;
				finderArgs = new Object[] {
					planEnrollmentId, status, start, end, orderByComparator
				};
			}

			List<BenefitUsage> list = null;

			if (useFinderCache) {
				list = (List<BenefitUsage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BenefitUsage benefitUsage : list) {
						if ((planEnrollmentId !=
								benefitUsage.getPlanEnrollmentId()) ||
							(status != benefitUsage.getStatus())) {

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

				sb.append(_SQL_SELECT_BENEFITUSAGE_WHERE);

				sb.append(_FINDER_COLUMN_PE_S_PLANENROLLMENTID_2);

				sb.append(_FINDER_COLUMN_PE_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BenefitUsageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(planEnrollmentId);

					queryPos.add(status);

					list = (List<BenefitUsage>)QueryUtil.list(
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
	 * Returns the first Benefit Usage in the ordered set where planEnrollmentId = &#63; and status = &#63;.
	 *
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Benefit Usage
	 * @throws NoSuchBenefitUsageException if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage findByPE_S_First(
			long planEnrollmentId, int status,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = fetchByPE_S_First(
			planEnrollmentId, status, orderByComparator);

		if (benefitUsage != null) {
			return benefitUsage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("planEnrollmentId=");
		sb.append(planEnrollmentId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchBenefitUsageException(sb.toString());
	}

	/**
	 * Returns the first Benefit Usage in the ordered set where planEnrollmentId = &#63; and status = &#63;.
	 *
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Benefit Usage, or <code>null</code> if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage fetchByPE_S_First(
		long planEnrollmentId, int status,
		OrderByComparator<BenefitUsage> orderByComparator) {

		List<BenefitUsage> list = findByPE_S(
			planEnrollmentId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Benefit Usage in the ordered set where planEnrollmentId = &#63; and status = &#63;.
	 *
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Benefit Usage
	 * @throws NoSuchBenefitUsageException if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage findByPE_S_Last(
			long planEnrollmentId, int status,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = fetchByPE_S_Last(
			planEnrollmentId, status, orderByComparator);

		if (benefitUsage != null) {
			return benefitUsage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("planEnrollmentId=");
		sb.append(planEnrollmentId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchBenefitUsageException(sb.toString());
	}

	/**
	 * Returns the last Benefit Usage in the ordered set where planEnrollmentId = &#63; and status = &#63;.
	 *
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Benefit Usage, or <code>null</code> if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage fetchByPE_S_Last(
		long planEnrollmentId, int status,
		OrderByComparator<BenefitUsage> orderByComparator) {

		int count = countByPE_S(planEnrollmentId, status);

		if (count == 0) {
			return null;
		}

		List<BenefitUsage> list = findByPE_S(
			planEnrollmentId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Benefit Usages before and after the current Benefit Usage in the ordered set where planEnrollmentId = &#63; and status = &#63;.
	 *
	 * @param benefitUsageId the primary key of the current Benefit Usage
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Benefit Usage
	 * @throws NoSuchBenefitUsageException if a Benefit Usage with the primary key could not be found
	 */
	@Override
	public BenefitUsage[] findByPE_S_PrevAndNext(
			long benefitUsageId, long planEnrollmentId, int status,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = findByPrimaryKey(benefitUsageId);

		Session session = null;

		try {
			session = openSession();

			BenefitUsage[] array = new BenefitUsageImpl[3];

			array[0] = getByPE_S_PrevAndNext(
				session, benefitUsage, planEnrollmentId, status,
				orderByComparator, true);

			array[1] = benefitUsage;

			array[2] = getByPE_S_PrevAndNext(
				session, benefitUsage, planEnrollmentId, status,
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

	protected BenefitUsage getByPE_S_PrevAndNext(
		Session session, BenefitUsage benefitUsage, long planEnrollmentId,
		int status, OrderByComparator<BenefitUsage> orderByComparator,
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

		sb.append(_SQL_SELECT_BENEFITUSAGE_WHERE);

		sb.append(_FINDER_COLUMN_PE_S_PLANENROLLMENTID_2);

		sb.append(_FINDER_COLUMN_PE_S_STATUS_2);

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
			sb.append(BenefitUsageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(planEnrollmentId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(benefitUsage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BenefitUsage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Benefit Usages where planEnrollmentId = &#63; and status = &#63; from the database.
	 *
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 */
	@Override
	public void removeByPE_S(long planEnrollmentId, int status) {
		for (BenefitUsage benefitUsage :
				findByPE_S(
					planEnrollmentId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(benefitUsage);
		}
	}

	/**
	 * Returns the number of Benefit Usages where planEnrollmentId = &#63; and status = &#63;.
	 *
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @return the number of matching Benefit Usages
	 */
	@Override
	public int countByPE_S(long planEnrollmentId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BenefitUsage.class)) {

			FinderPath finderPath = _finderPathCountByPE_S;

			Object[] finderArgs = new Object[] {planEnrollmentId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_BENEFITUSAGE_WHERE);

				sb.append(_FINDER_COLUMN_PE_S_PLANENROLLMENTID_2);

				sb.append(_FINDER_COLUMN_PE_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(planEnrollmentId);

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

	private static final String _FINDER_COLUMN_PE_S_PLANENROLLMENTID_2 =
		"benefitUsage.planEnrollmentId = ? AND ";

	private static final String _FINDER_COLUMN_PE_S_STATUS_2 =
		"benefitUsage.status = ?";

	private FinderPath _finderPathWithPaginationFindByPE_NotS;
	private FinderPath _finderPathWithPaginationCountByPE_NotS;

	/**
	 * Returns all the Benefit Usages where planEnrollmentId = &#63; and status &ne; &#63;.
	 *
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @return the matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByPE_NotS(long planEnrollmentId, int status) {
		return findByPE_NotS(
			planEnrollmentId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the Benefit Usages where planEnrollmentId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @return the range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByPE_NotS(
		long planEnrollmentId, int status, int start, int end) {

		return findByPE_NotS(planEnrollmentId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages where planEnrollmentId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByPE_NotS(
		long planEnrollmentId, int status, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator) {

		return findByPE_NotS(
			planEnrollmentId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages where planEnrollmentId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByPE_NotS(
		long planEnrollmentId, int status, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BenefitUsage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByPE_NotS;
			finderArgs = new Object[] {
				planEnrollmentId, status, start, end, orderByComparator
			};

			List<BenefitUsage> list = null;

			if (useFinderCache) {
				list = (List<BenefitUsage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BenefitUsage benefitUsage : list) {
						if ((planEnrollmentId !=
								benefitUsage.getPlanEnrollmentId()) ||
							(status == benefitUsage.getStatus())) {

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

				sb.append(_SQL_SELECT_BENEFITUSAGE_WHERE);

				sb.append(_FINDER_COLUMN_PE_NOTS_PLANENROLLMENTID_2);

				sb.append(_FINDER_COLUMN_PE_NOTS_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BenefitUsageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(planEnrollmentId);

					queryPos.add(status);

					list = (List<BenefitUsage>)QueryUtil.list(
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
	 * Returns the first Benefit Usage in the ordered set where planEnrollmentId = &#63; and status &ne; &#63;.
	 *
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Benefit Usage
	 * @throws NoSuchBenefitUsageException if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage findByPE_NotS_First(
			long planEnrollmentId, int status,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = fetchByPE_NotS_First(
			planEnrollmentId, status, orderByComparator);

		if (benefitUsage != null) {
			return benefitUsage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("planEnrollmentId=");
		sb.append(planEnrollmentId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchBenefitUsageException(sb.toString());
	}

	/**
	 * Returns the first Benefit Usage in the ordered set where planEnrollmentId = &#63; and status &ne; &#63;.
	 *
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Benefit Usage, or <code>null</code> if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage fetchByPE_NotS_First(
		long planEnrollmentId, int status,
		OrderByComparator<BenefitUsage> orderByComparator) {

		List<BenefitUsage> list = findByPE_NotS(
			planEnrollmentId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Benefit Usage in the ordered set where planEnrollmentId = &#63; and status &ne; &#63;.
	 *
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Benefit Usage
	 * @throws NoSuchBenefitUsageException if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage findByPE_NotS_Last(
			long planEnrollmentId, int status,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = fetchByPE_NotS_Last(
			planEnrollmentId, status, orderByComparator);

		if (benefitUsage != null) {
			return benefitUsage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("planEnrollmentId=");
		sb.append(planEnrollmentId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchBenefitUsageException(sb.toString());
	}

	/**
	 * Returns the last Benefit Usage in the ordered set where planEnrollmentId = &#63; and status &ne; &#63;.
	 *
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Benefit Usage, or <code>null</code> if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage fetchByPE_NotS_Last(
		long planEnrollmentId, int status,
		OrderByComparator<BenefitUsage> orderByComparator) {

		int count = countByPE_NotS(planEnrollmentId, status);

		if (count == 0) {
			return null;
		}

		List<BenefitUsage> list = findByPE_NotS(
			planEnrollmentId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Benefit Usages before and after the current Benefit Usage in the ordered set where planEnrollmentId = &#63; and status &ne; &#63;.
	 *
	 * @param benefitUsageId the primary key of the current Benefit Usage
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Benefit Usage
	 * @throws NoSuchBenefitUsageException if a Benefit Usage with the primary key could not be found
	 */
	@Override
	public BenefitUsage[] findByPE_NotS_PrevAndNext(
			long benefitUsageId, long planEnrollmentId, int status,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = findByPrimaryKey(benefitUsageId);

		Session session = null;

		try {
			session = openSession();

			BenefitUsage[] array = new BenefitUsageImpl[3];

			array[0] = getByPE_NotS_PrevAndNext(
				session, benefitUsage, planEnrollmentId, status,
				orderByComparator, true);

			array[1] = benefitUsage;

			array[2] = getByPE_NotS_PrevAndNext(
				session, benefitUsage, planEnrollmentId, status,
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

	protected BenefitUsage getByPE_NotS_PrevAndNext(
		Session session, BenefitUsage benefitUsage, long planEnrollmentId,
		int status, OrderByComparator<BenefitUsage> orderByComparator,
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

		sb.append(_SQL_SELECT_BENEFITUSAGE_WHERE);

		sb.append(_FINDER_COLUMN_PE_NOTS_PLANENROLLMENTID_2);

		sb.append(_FINDER_COLUMN_PE_NOTS_STATUS_2);

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
			sb.append(BenefitUsageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(planEnrollmentId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(benefitUsage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BenefitUsage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Benefit Usages where planEnrollmentId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 */
	@Override
	public void removeByPE_NotS(long planEnrollmentId, int status) {
		for (BenefitUsage benefitUsage :
				findByPE_NotS(
					planEnrollmentId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(benefitUsage);
		}
	}

	/**
	 * Returns the number of Benefit Usages where planEnrollmentId = &#63; and status &ne; &#63;.
	 *
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @return the number of matching Benefit Usages
	 */
	@Override
	public int countByPE_NotS(long planEnrollmentId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BenefitUsage.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByPE_NotS;

			Object[] finderArgs = new Object[] {planEnrollmentId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_BENEFITUSAGE_WHERE);

				sb.append(_FINDER_COLUMN_PE_NOTS_PLANENROLLMENTID_2);

				sb.append(_FINDER_COLUMN_PE_NOTS_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(planEnrollmentId);

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

	private static final String _FINDER_COLUMN_PE_NOTS_PLANENROLLMENTID_2 =
		"benefitUsage.planEnrollmentId = ? AND ";

	private static final String _FINDER_COLUMN_PE_NOTS_STATUS_2 =
		"benefitUsage.status != ?";

	private FinderPath _finderPathWithPaginationFindByG_PE_S;
	private FinderPath _finderPathWithoutPaginationFindByG_PE_S;
	private FinderPath _finderPathCountByG_PE_S;

	/**
	 * Returns all the Benefit Usages where groupId = &#63; and planEnrollmentId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @return the matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByG_PE_S(
		long groupId, long planEnrollmentId, int status) {

		return findByG_PE_S(
			groupId, planEnrollmentId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Benefit Usages where groupId = &#63; and planEnrollmentId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @return the range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByG_PE_S(
		long groupId, long planEnrollmentId, int status, int start, int end) {

		return findByG_PE_S(
			groupId, planEnrollmentId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages where groupId = &#63; and planEnrollmentId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByG_PE_S(
		long groupId, long planEnrollmentId, int status, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator) {

		return findByG_PE_S(
			groupId, planEnrollmentId, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages where groupId = &#63; and planEnrollmentId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByG_PE_S(
		long groupId, long planEnrollmentId, int status, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BenefitUsage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath = _finderPathWithoutPaginationFindByG_PE_S;
					finderArgs = new Object[] {
						groupId, planEnrollmentId, status
					};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByG_PE_S;
				finderArgs = new Object[] {
					groupId, planEnrollmentId, status, start, end,
					orderByComparator
				};
			}

			List<BenefitUsage> list = null;

			if (useFinderCache) {
				list = (List<BenefitUsage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BenefitUsage benefitUsage : list) {
						if ((groupId != benefitUsage.getGroupId()) ||
							(planEnrollmentId !=
								benefitUsage.getPlanEnrollmentId()) ||
							(status != benefitUsage.getStatus())) {

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

				sb.append(_SQL_SELECT_BENEFITUSAGE_WHERE);

				sb.append(_FINDER_COLUMN_G_PE_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_PE_S_PLANENROLLMENTID_2);

				sb.append(_FINDER_COLUMN_G_PE_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BenefitUsageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(planEnrollmentId);

					queryPos.add(status);

					list = (List<BenefitUsage>)QueryUtil.list(
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
	 * Returns the first Benefit Usage in the ordered set where groupId = &#63; and planEnrollmentId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Benefit Usage
	 * @throws NoSuchBenefitUsageException if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage findByG_PE_S_First(
			long groupId, long planEnrollmentId, int status,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = fetchByG_PE_S_First(
			groupId, planEnrollmentId, status, orderByComparator);

		if (benefitUsage != null) {
			return benefitUsage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", planEnrollmentId=");
		sb.append(planEnrollmentId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchBenefitUsageException(sb.toString());
	}

	/**
	 * Returns the first Benefit Usage in the ordered set where groupId = &#63; and planEnrollmentId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Benefit Usage, or <code>null</code> if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage fetchByG_PE_S_First(
		long groupId, long planEnrollmentId, int status,
		OrderByComparator<BenefitUsage> orderByComparator) {

		List<BenefitUsage> list = findByG_PE_S(
			groupId, planEnrollmentId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Benefit Usage in the ordered set where groupId = &#63; and planEnrollmentId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Benefit Usage
	 * @throws NoSuchBenefitUsageException if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage findByG_PE_S_Last(
			long groupId, long planEnrollmentId, int status,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = fetchByG_PE_S_Last(
			groupId, planEnrollmentId, status, orderByComparator);

		if (benefitUsage != null) {
			return benefitUsage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", planEnrollmentId=");
		sb.append(planEnrollmentId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchBenefitUsageException(sb.toString());
	}

	/**
	 * Returns the last Benefit Usage in the ordered set where groupId = &#63; and planEnrollmentId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Benefit Usage, or <code>null</code> if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage fetchByG_PE_S_Last(
		long groupId, long planEnrollmentId, int status,
		OrderByComparator<BenefitUsage> orderByComparator) {

		int count = countByG_PE_S(groupId, planEnrollmentId, status);

		if (count == 0) {
			return null;
		}

		List<BenefitUsage> list = findByG_PE_S(
			groupId, planEnrollmentId, status, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Benefit Usages before and after the current Benefit Usage in the ordered set where groupId = &#63; and planEnrollmentId = &#63; and status = &#63;.
	 *
	 * @param benefitUsageId the primary key of the current Benefit Usage
	 * @param groupId the group ID
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Benefit Usage
	 * @throws NoSuchBenefitUsageException if a Benefit Usage with the primary key could not be found
	 */
	@Override
	public BenefitUsage[] findByG_PE_S_PrevAndNext(
			long benefitUsageId, long groupId, long planEnrollmentId,
			int status, OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = findByPrimaryKey(benefitUsageId);

		Session session = null;

		try {
			session = openSession();

			BenefitUsage[] array = new BenefitUsageImpl[3];

			array[0] = getByG_PE_S_PrevAndNext(
				session, benefitUsage, groupId, planEnrollmentId, status,
				orderByComparator, true);

			array[1] = benefitUsage;

			array[2] = getByG_PE_S_PrevAndNext(
				session, benefitUsage, groupId, planEnrollmentId, status,
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

	protected BenefitUsage getByG_PE_S_PrevAndNext(
		Session session, BenefitUsage benefitUsage, long groupId,
		long planEnrollmentId, int status,
		OrderByComparator<BenefitUsage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_BENEFITUSAGE_WHERE);

		sb.append(_FINDER_COLUMN_G_PE_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_PE_S_PLANENROLLMENTID_2);

		sb.append(_FINDER_COLUMN_G_PE_S_STATUS_2);

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
			sb.append(BenefitUsageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(planEnrollmentId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(benefitUsage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BenefitUsage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the Benefit Usages that the user has permission to view where groupId = &#63; and planEnrollmentId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @return the matching Benefit Usages that the user has permission to view
	 */
	@Override
	public List<BenefitUsage> filterFindByG_PE_S(
		long groupId, long planEnrollmentId, int status) {

		return filterFindByG_PE_S(
			groupId, planEnrollmentId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Benefit Usages that the user has permission to view where groupId = &#63; and planEnrollmentId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @return the range of matching Benefit Usages that the user has permission to view
	 */
	@Override
	public List<BenefitUsage> filterFindByG_PE_S(
		long groupId, long planEnrollmentId, int status, int start, int end) {

		return filterFindByG_PE_S(
			groupId, planEnrollmentId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages that the user has permissions to view where groupId = &#63; and planEnrollmentId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Benefit Usages that the user has permission to view
	 */
	@Override
	public List<BenefitUsage> filterFindByG_PE_S(
		long groupId, long planEnrollmentId, int status, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_PE_S(
				groupId, planEnrollmentId, status, start, end,
				orderByComparator);
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
			sb.append(_FILTER_SQL_SELECT_BENEFITUSAGE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_BENEFITUSAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_PE_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_PE_S_PLANENROLLMENTID_2);

		sb.append(_FINDER_COLUMN_G_PE_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_BENEFITUSAGE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BenefitUsageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BenefitUsageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BenefitUsage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, BenefitUsageImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, BenefitUsageImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(planEnrollmentId);

			queryPos.add(status);

			return (List<BenefitUsage>)QueryUtil.list(
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
	 * Returns the Benefit Usages before and after the current Benefit Usage in the ordered set of Benefit Usages that the user has permission to view where groupId = &#63; and planEnrollmentId = &#63; and status = &#63;.
	 *
	 * @param benefitUsageId the primary key of the current Benefit Usage
	 * @param groupId the group ID
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Benefit Usage
	 * @throws NoSuchBenefitUsageException if a Benefit Usage with the primary key could not be found
	 */
	@Override
	public BenefitUsage[] filterFindByG_PE_S_PrevAndNext(
			long benefitUsageId, long groupId, long planEnrollmentId,
			int status, OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_PE_S_PrevAndNext(
				benefitUsageId, groupId, planEnrollmentId, status,
				orderByComparator);
		}

		BenefitUsage benefitUsage = findByPrimaryKey(benefitUsageId);

		Session session = null;

		try {
			session = openSession();

			BenefitUsage[] array = new BenefitUsageImpl[3];

			array[0] = filterGetByG_PE_S_PrevAndNext(
				session, benefitUsage, groupId, planEnrollmentId, status,
				orderByComparator, true);

			array[1] = benefitUsage;

			array[2] = filterGetByG_PE_S_PrevAndNext(
				session, benefitUsage, groupId, planEnrollmentId, status,
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

	protected BenefitUsage filterGetByG_PE_S_PrevAndNext(
		Session session, BenefitUsage benefitUsage, long groupId,
		long planEnrollmentId, int status,
		OrderByComparator<BenefitUsage> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_BENEFITUSAGE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_BENEFITUSAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_PE_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_PE_S_PLANENROLLMENTID_2);

		sb.append(_FINDER_COLUMN_G_PE_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_BENEFITUSAGE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BenefitUsageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BenefitUsageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BenefitUsage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, BenefitUsageImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, BenefitUsageImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(planEnrollmentId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(benefitUsage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BenefitUsage> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Benefit Usages where groupId = &#63; and planEnrollmentId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 */
	@Override
	public void removeByG_PE_S(
		long groupId, long planEnrollmentId, int status) {

		for (BenefitUsage benefitUsage :
				findByG_PE_S(
					groupId, planEnrollmentId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(benefitUsage);
		}
	}

	/**
	 * Returns the number of Benefit Usages where groupId = &#63; and planEnrollmentId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @return the number of matching Benefit Usages
	 */
	@Override
	public int countByG_PE_S(long groupId, long planEnrollmentId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BenefitUsage.class)) {

			FinderPath finderPath = _finderPathCountByG_PE_S;

			Object[] finderArgs = new Object[] {
				groupId, planEnrollmentId, status
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_BENEFITUSAGE_WHERE);

				sb.append(_FINDER_COLUMN_G_PE_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_PE_S_PLANENROLLMENTID_2);

				sb.append(_FINDER_COLUMN_G_PE_S_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(planEnrollmentId);

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
	 * Returns the number of Benefit Usages that the user has permission to view where groupId = &#63; and planEnrollmentId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @return the number of matching Benefit Usages that the user has permission to view
	 */
	@Override
	public int filterCountByG_PE_S(
		long groupId, long planEnrollmentId, int status) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_PE_S(groupId, planEnrollmentId, status);
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_BENEFITUSAGE_WHERE);

		sb.append(_FINDER_COLUMN_G_PE_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_PE_S_PLANENROLLMENTID_2);

		sb.append(_FINDER_COLUMN_G_PE_S_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BenefitUsage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(planEnrollmentId);

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

	private static final String _FINDER_COLUMN_G_PE_S_GROUPID_2 =
		"benefitUsage.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_PE_S_PLANENROLLMENTID_2 =
		"benefitUsage.planEnrollmentId = ? AND ";

	private static final String _FINDER_COLUMN_G_PE_S_STATUS_2 =
		"benefitUsage.status = ?";

	private FinderPath _finderPathWithPaginationFindByG_PE_NotS;
	private FinderPath _finderPathWithPaginationCountByG_PE_NotS;

	/**
	 * Returns all the Benefit Usages where groupId = &#63; and planEnrollmentId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @return the matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByG_PE_NotS(
		long groupId, long planEnrollmentId, int status) {

		return findByG_PE_NotS(
			groupId, planEnrollmentId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Benefit Usages where groupId = &#63; and planEnrollmentId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @return the range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByG_PE_NotS(
		long groupId, long planEnrollmentId, int status, int start, int end) {

		return findByG_PE_NotS(
			groupId, planEnrollmentId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages where groupId = &#63; and planEnrollmentId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByG_PE_NotS(
		long groupId, long planEnrollmentId, int status, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator) {

		return findByG_PE_NotS(
			groupId, planEnrollmentId, status, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages where groupId = &#63; and planEnrollmentId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByG_PE_NotS(
		long groupId, long planEnrollmentId, int status, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BenefitUsage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByG_PE_NotS;
			finderArgs = new Object[] {
				groupId, planEnrollmentId, status, start, end, orderByComparator
			};

			List<BenefitUsage> list = null;

			if (useFinderCache) {
				list = (List<BenefitUsage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BenefitUsage benefitUsage : list) {
						if ((groupId != benefitUsage.getGroupId()) ||
							(planEnrollmentId !=
								benefitUsage.getPlanEnrollmentId()) ||
							(status == benefitUsage.getStatus())) {

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

				sb.append(_SQL_SELECT_BENEFITUSAGE_WHERE);

				sb.append(_FINDER_COLUMN_G_PE_NOTS_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_PE_NOTS_PLANENROLLMENTID_2);

				sb.append(_FINDER_COLUMN_G_PE_NOTS_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BenefitUsageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(planEnrollmentId);

					queryPos.add(status);

					list = (List<BenefitUsage>)QueryUtil.list(
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
	 * Returns the first Benefit Usage in the ordered set where groupId = &#63; and planEnrollmentId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Benefit Usage
	 * @throws NoSuchBenefitUsageException if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage findByG_PE_NotS_First(
			long groupId, long planEnrollmentId, int status,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = fetchByG_PE_NotS_First(
			groupId, planEnrollmentId, status, orderByComparator);

		if (benefitUsage != null) {
			return benefitUsage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", planEnrollmentId=");
		sb.append(planEnrollmentId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchBenefitUsageException(sb.toString());
	}

	/**
	 * Returns the first Benefit Usage in the ordered set where groupId = &#63; and planEnrollmentId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Benefit Usage, or <code>null</code> if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage fetchByG_PE_NotS_First(
		long groupId, long planEnrollmentId, int status,
		OrderByComparator<BenefitUsage> orderByComparator) {

		List<BenefitUsage> list = findByG_PE_NotS(
			groupId, planEnrollmentId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Benefit Usage in the ordered set where groupId = &#63; and planEnrollmentId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Benefit Usage
	 * @throws NoSuchBenefitUsageException if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage findByG_PE_NotS_Last(
			long groupId, long planEnrollmentId, int status,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = fetchByG_PE_NotS_Last(
			groupId, planEnrollmentId, status, orderByComparator);

		if (benefitUsage != null) {
			return benefitUsage;
		}

		StringBundler sb = new StringBundler(8);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("groupId=");
		sb.append(groupId);

		sb.append(", planEnrollmentId=");
		sb.append(planEnrollmentId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchBenefitUsageException(sb.toString());
	}

	/**
	 * Returns the last Benefit Usage in the ordered set where groupId = &#63; and planEnrollmentId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Benefit Usage, or <code>null</code> if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage fetchByG_PE_NotS_Last(
		long groupId, long planEnrollmentId, int status,
		OrderByComparator<BenefitUsage> orderByComparator) {

		int count = countByG_PE_NotS(groupId, planEnrollmentId, status);

		if (count == 0) {
			return null;
		}

		List<BenefitUsage> list = findByG_PE_NotS(
			groupId, planEnrollmentId, status, count - 1, count,
			orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Benefit Usages before and after the current Benefit Usage in the ordered set where groupId = &#63; and planEnrollmentId = &#63; and status &ne; &#63;.
	 *
	 * @param benefitUsageId the primary key of the current Benefit Usage
	 * @param groupId the group ID
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Benefit Usage
	 * @throws NoSuchBenefitUsageException if a Benefit Usage with the primary key could not be found
	 */
	@Override
	public BenefitUsage[] findByG_PE_NotS_PrevAndNext(
			long benefitUsageId, long groupId, long planEnrollmentId,
			int status, OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = findByPrimaryKey(benefitUsageId);

		Session session = null;

		try {
			session = openSession();

			BenefitUsage[] array = new BenefitUsageImpl[3];

			array[0] = getByG_PE_NotS_PrevAndNext(
				session, benefitUsage, groupId, planEnrollmentId, status,
				orderByComparator, true);

			array[1] = benefitUsage;

			array[2] = getByG_PE_NotS_PrevAndNext(
				session, benefitUsage, groupId, planEnrollmentId, status,
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

	protected BenefitUsage getByG_PE_NotS_PrevAndNext(
		Session session, BenefitUsage benefitUsage, long groupId,
		long planEnrollmentId, int status,
		OrderByComparator<BenefitUsage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				6 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(5);
		}

		sb.append(_SQL_SELECT_BENEFITUSAGE_WHERE);

		sb.append(_FINDER_COLUMN_G_PE_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_PE_NOTS_PLANENROLLMENTID_2);

		sb.append(_FINDER_COLUMN_G_PE_NOTS_STATUS_2);

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
			sb.append(BenefitUsageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(groupId);

		queryPos.add(planEnrollmentId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(benefitUsage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BenefitUsage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the Benefit Usages that the user has permission to view where groupId = &#63; and planEnrollmentId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @return the matching Benefit Usages that the user has permission to view
	 */
	@Override
	public List<BenefitUsage> filterFindByG_PE_NotS(
		long groupId, long planEnrollmentId, int status) {

		return filterFindByG_PE_NotS(
			groupId, planEnrollmentId, status, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Benefit Usages that the user has permission to view where groupId = &#63; and planEnrollmentId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @return the range of matching Benefit Usages that the user has permission to view
	 */
	@Override
	public List<BenefitUsage> filterFindByG_PE_NotS(
		long groupId, long planEnrollmentId, int status, int start, int end) {

		return filterFindByG_PE_NotS(
			groupId, planEnrollmentId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages that the user has permissions to view where groupId = &#63; and planEnrollmentId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Benefit Usages that the user has permission to view
	 */
	@Override
	public List<BenefitUsage> filterFindByG_PE_NotS(
		long groupId, long planEnrollmentId, int status, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_PE_NotS(
				groupId, planEnrollmentId, status, start, end,
				orderByComparator);
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
			sb.append(_FILTER_SQL_SELECT_BENEFITUSAGE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_BENEFITUSAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_PE_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_PE_NOTS_PLANENROLLMENTID_2);

		sb.append(_FINDER_COLUMN_G_PE_NOTS_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_BENEFITUSAGE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BenefitUsageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BenefitUsageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BenefitUsage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, BenefitUsageImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, BenefitUsageImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(planEnrollmentId);

			queryPos.add(status);

			return (List<BenefitUsage>)QueryUtil.list(
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
	 * Returns the Benefit Usages before and after the current Benefit Usage in the ordered set of Benefit Usages that the user has permission to view where groupId = &#63; and planEnrollmentId = &#63; and status &ne; &#63;.
	 *
	 * @param benefitUsageId the primary key of the current Benefit Usage
	 * @param groupId the group ID
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Benefit Usage
	 * @throws NoSuchBenefitUsageException if a Benefit Usage with the primary key could not be found
	 */
	@Override
	public BenefitUsage[] filterFindByG_PE_NotS_PrevAndNext(
			long benefitUsageId, long groupId, long planEnrollmentId,
			int status, OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_PE_NotS_PrevAndNext(
				benefitUsageId, groupId, planEnrollmentId, status,
				orderByComparator);
		}

		BenefitUsage benefitUsage = findByPrimaryKey(benefitUsageId);

		Session session = null;

		try {
			session = openSession();

			BenefitUsage[] array = new BenefitUsageImpl[3];

			array[0] = filterGetByG_PE_NotS_PrevAndNext(
				session, benefitUsage, groupId, planEnrollmentId, status,
				orderByComparator, true);

			array[1] = benefitUsage;

			array[2] = filterGetByG_PE_NotS_PrevAndNext(
				session, benefitUsage, groupId, planEnrollmentId, status,
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

	protected BenefitUsage filterGetByG_PE_NotS_PrevAndNext(
		Session session, BenefitUsage benefitUsage, long groupId,
		long planEnrollmentId, int status,
		OrderByComparator<BenefitUsage> orderByComparator, boolean previous) {

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
			sb.append(_FILTER_SQL_SELECT_BENEFITUSAGE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_BENEFITUSAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_PE_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_PE_NOTS_PLANENROLLMENTID_2);

		sb.append(_FINDER_COLUMN_G_PE_NOTS_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_BENEFITUSAGE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BenefitUsageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BenefitUsageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BenefitUsage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, BenefitUsageImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, BenefitUsageImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(planEnrollmentId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(benefitUsage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BenefitUsage> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Benefit Usages where groupId = &#63; and planEnrollmentId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 */
	@Override
	public void removeByG_PE_NotS(
		long groupId, long planEnrollmentId, int status) {

		for (BenefitUsage benefitUsage :
				findByG_PE_NotS(
					groupId, planEnrollmentId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(benefitUsage);
		}
	}

	/**
	 * Returns the number of Benefit Usages where groupId = &#63; and planEnrollmentId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @return the number of matching Benefit Usages
	 */
	@Override
	public int countByG_PE_NotS(
		long groupId, long planEnrollmentId, int status) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BenefitUsage.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByG_PE_NotS;

			Object[] finderArgs = new Object[] {
				groupId, planEnrollmentId, status
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_BENEFITUSAGE_WHERE);

				sb.append(_FINDER_COLUMN_G_PE_NOTS_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_PE_NOTS_PLANENROLLMENTID_2);

				sb.append(_FINDER_COLUMN_G_PE_NOTS_STATUS_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(planEnrollmentId);

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
	 * Returns the number of Benefit Usages that the user has permission to view where groupId = &#63; and planEnrollmentId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param planEnrollmentId the plan enrollment ID
	 * @param status the status
	 * @return the number of matching Benefit Usages that the user has permission to view
	 */
	@Override
	public int filterCountByG_PE_NotS(
		long groupId, long planEnrollmentId, int status) {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_PE_NotS(groupId, planEnrollmentId, status);
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_BENEFITUSAGE_WHERE);

		sb.append(_FINDER_COLUMN_G_PE_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_PE_NOTS_PLANENROLLMENTID_2);

		sb.append(_FINDER_COLUMN_G_PE_NOTS_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BenefitUsage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			sqlQuery.addScalar(
				COUNT_COLUMN_NAME, com.liferay.portal.kernel.dao.orm.Type.LONG);

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(planEnrollmentId);

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

	private static final String _FINDER_COLUMN_G_PE_NOTS_GROUPID_2 =
		"benefitUsage.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_PE_NOTS_PLANENROLLMENTID_2 =
		"benefitUsage.planEnrollmentId = ? AND ";

	private static final String _FINDER_COLUMN_G_PE_NOTS_STATUS_2 =
		"benefitUsage.status != ?";

	private FinderPath _finderPathWithPaginationFindByC_S;
	private FinderPath _finderPathWithoutPaginationFindByC_S;
	private FinderPath _finderPathCountByC_S;

	/**
	 * Returns all the Benefit Usages where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByC_S(long companyId, int status) {
		return findByC_S(
			companyId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Benefit Usages where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @return the range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByC_S(
		long companyId, int status, int start, int end) {

		return findByC_S(companyId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByC_S(
		long companyId, int status, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator) {

		return findByC_S(
			companyId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByC_S(
		long companyId, int status, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BenefitUsage.class)) {

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

			List<BenefitUsage> list = null;

			if (useFinderCache) {
				list = (List<BenefitUsage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BenefitUsage benefitUsage : list) {
						if ((companyId != benefitUsage.getCompanyId()) ||
							(status != benefitUsage.getStatus())) {

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

				sb.append(_SQL_SELECT_BENEFITUSAGE_WHERE);

				sb.append(_FINDER_COLUMN_C_S_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BenefitUsageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(status);

					list = (List<BenefitUsage>)QueryUtil.list(
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
	 * Returns the first Benefit Usage in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Benefit Usage
	 * @throws NoSuchBenefitUsageException if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage findByC_S_First(
			long companyId, int status,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = fetchByC_S_First(
			companyId, status, orderByComparator);

		if (benefitUsage != null) {
			return benefitUsage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchBenefitUsageException(sb.toString());
	}

	/**
	 * Returns the first Benefit Usage in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Benefit Usage, or <code>null</code> if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage fetchByC_S_First(
		long companyId, int status,
		OrderByComparator<BenefitUsage> orderByComparator) {

		List<BenefitUsage> list = findByC_S(
			companyId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Benefit Usage in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Benefit Usage
	 * @throws NoSuchBenefitUsageException if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage findByC_S_Last(
			long companyId, int status,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = fetchByC_S_Last(
			companyId, status, orderByComparator);

		if (benefitUsage != null) {
			return benefitUsage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", status=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchBenefitUsageException(sb.toString());
	}

	/**
	 * Returns the last Benefit Usage in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Benefit Usage, or <code>null</code> if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage fetchByC_S_Last(
		long companyId, int status,
		OrderByComparator<BenefitUsage> orderByComparator) {

		int count = countByC_S(companyId, status);

		if (count == 0) {
			return null;
		}

		List<BenefitUsage> list = findByC_S(
			companyId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Benefit Usages before and after the current Benefit Usage in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param benefitUsageId the primary key of the current Benefit Usage
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Benefit Usage
	 * @throws NoSuchBenefitUsageException if a Benefit Usage with the primary key could not be found
	 */
	@Override
	public BenefitUsage[] findByC_S_PrevAndNext(
			long benefitUsageId, long companyId, int status,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = findByPrimaryKey(benefitUsageId);

		Session session = null;

		try {
			session = openSession();

			BenefitUsage[] array = new BenefitUsageImpl[3];

			array[0] = getByC_S_PrevAndNext(
				session, benefitUsage, companyId, status, orderByComparator,
				true);

			array[1] = benefitUsage;

			array[2] = getByC_S_PrevAndNext(
				session, benefitUsage, companyId, status, orderByComparator,
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

	protected BenefitUsage getByC_S_PrevAndNext(
		Session session, BenefitUsage benefitUsage, long companyId, int status,
		OrderByComparator<BenefitUsage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_BENEFITUSAGE_WHERE);

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
			sb.append(BenefitUsageModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(benefitUsage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BenefitUsage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Benefit Usages where companyId = &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 */
	@Override
	public void removeByC_S(long companyId, int status) {
		for (BenefitUsage benefitUsage :
				findByC_S(
					companyId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(benefitUsage);
		}
	}

	/**
	 * Returns the number of Benefit Usages where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching Benefit Usages
	 */
	@Override
	public int countByC_S(long companyId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BenefitUsage.class)) {

			FinderPath finderPath = _finderPathCountByC_S;

			Object[] finderArgs = new Object[] {companyId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_BENEFITUSAGE_WHERE);

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
		"benefitUsage.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_S_STATUS_2 =
		"benefitUsage.status = ?";

	private FinderPath _finderPathWithPaginationFindByC_NotS;
	private FinderPath _finderPathWithPaginationCountByC_NotS;

	/**
	 * Returns all the Benefit Usages where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByC_NotS(long companyId, int status) {
		return findByC_NotS(
			companyId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Benefit Usages where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @return the range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByC_NotS(
		long companyId, int status, int start, int end) {

		return findByC_NotS(companyId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByC_NotS(
		long companyId, int status, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator) {

		return findByC_NotS(
			companyId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByC_NotS(
		long companyId, int status, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BenefitUsage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByC_NotS;
			finderArgs = new Object[] {
				companyId, status, start, end, orderByComparator
			};

			List<BenefitUsage> list = null;

			if (useFinderCache) {
				list = (List<BenefitUsage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BenefitUsage benefitUsage : list) {
						if ((companyId != benefitUsage.getCompanyId()) ||
							(status == benefitUsage.getStatus())) {

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

				sb.append(_SQL_SELECT_BENEFITUSAGE_WHERE);

				sb.append(_FINDER_COLUMN_C_NOTS_COMPANYID_2);

				sb.append(_FINDER_COLUMN_C_NOTS_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BenefitUsageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(companyId);

					queryPos.add(status);

					list = (List<BenefitUsage>)QueryUtil.list(
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
	 * Returns the first Benefit Usage in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Benefit Usage
	 * @throws NoSuchBenefitUsageException if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage findByC_NotS_First(
			long companyId, int status,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = fetchByC_NotS_First(
			companyId, status, orderByComparator);

		if (benefitUsage != null) {
			return benefitUsage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchBenefitUsageException(sb.toString());
	}

	/**
	 * Returns the first Benefit Usage in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Benefit Usage, or <code>null</code> if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage fetchByC_NotS_First(
		long companyId, int status,
		OrderByComparator<BenefitUsage> orderByComparator) {

		List<BenefitUsage> list = findByC_NotS(
			companyId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Benefit Usage in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Benefit Usage
	 * @throws NoSuchBenefitUsageException if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage findByC_NotS_Last(
			long companyId, int status,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = fetchByC_NotS_Last(
			companyId, status, orderByComparator);

		if (benefitUsage != null) {
			return benefitUsage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", status!=");
		sb.append(status);

		sb.append("}");

		throw new NoSuchBenefitUsageException(sb.toString());
	}

	/**
	 * Returns the last Benefit Usage in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Benefit Usage, or <code>null</code> if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage fetchByC_NotS_Last(
		long companyId, int status,
		OrderByComparator<BenefitUsage> orderByComparator) {

		int count = countByC_NotS(companyId, status);

		if (count == 0) {
			return null;
		}

		List<BenefitUsage> list = findByC_NotS(
			companyId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Benefit Usages before and after the current Benefit Usage in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param benefitUsageId the primary key of the current Benefit Usage
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Benefit Usage
	 * @throws NoSuchBenefitUsageException if a Benefit Usage with the primary key could not be found
	 */
	@Override
	public BenefitUsage[] findByC_NotS_PrevAndNext(
			long benefitUsageId, long companyId, int status,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = findByPrimaryKey(benefitUsageId);

		Session session = null;

		try {
			session = openSession();

			BenefitUsage[] array = new BenefitUsageImpl[3];

			array[0] = getByC_NotS_PrevAndNext(
				session, benefitUsage, companyId, status, orderByComparator,
				true);

			array[1] = benefitUsage;

			array[2] = getByC_NotS_PrevAndNext(
				session, benefitUsage, companyId, status, orderByComparator,
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

	protected BenefitUsage getByC_NotS_PrevAndNext(
		Session session, BenefitUsage benefitUsage, long companyId, int status,
		OrderByComparator<BenefitUsage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				5 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(4);
		}

		sb.append(_SQL_SELECT_BENEFITUSAGE_WHERE);

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
			sb.append(BenefitUsageModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(benefitUsage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BenefitUsage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Benefit Usages where companyId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 */
	@Override
	public void removeByC_NotS(long companyId, int status) {
		for (BenefitUsage benefitUsage :
				findByC_NotS(
					companyId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(benefitUsage);
		}
	}

	/**
	 * Returns the number of Benefit Usages where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching Benefit Usages
	 */
	@Override
	public int countByC_NotS(long companyId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BenefitUsage.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByC_NotS;

			Object[] finderArgs = new Object[] {companyId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_BENEFITUSAGE_WHERE);

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
		"benefitUsage.companyId = ? AND ";

	private static final String _FINDER_COLUMN_C_NOTS_STATUS_2 =
		"benefitUsage.status != ?";

	private FinderPath _finderPathWithPaginationFindByG_U_S;
	private FinderPath _finderPathWithoutPaginationFindByG_U_S;
	private FinderPath _finderPathCountByG_U_S;
	private FinderPath _finderPathWithPaginationCountByG_U_S;

	/**
	 * Returns all the Benefit Usages where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByG_U_S(
		long groupId, long userId, int status) {

		return findByG_U_S(
			groupId, userId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the Benefit Usages where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @return the range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByG_U_S(
		long groupId, long userId, int status, int start, int end) {

		return findByG_U_S(groupId, userId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByG_U_S(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator) {

		return findByG_U_S(
			groupId, userId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByG_U_S(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BenefitUsage.class)) {

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

			List<BenefitUsage> list = null;

			if (useFinderCache) {
				list = (List<BenefitUsage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BenefitUsage benefitUsage : list) {
						if ((groupId != benefitUsage.getGroupId()) ||
							(userId != benefitUsage.getUserId()) ||
							(status != benefitUsage.getStatus())) {

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

				sb.append(_SQL_SELECT_BENEFITUSAGE_WHERE);

				sb.append(_FINDER_COLUMN_G_U_S_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_U_S_USERID_2);

				sb.append(_FINDER_COLUMN_G_U_S_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BenefitUsageModelImpl.ORDER_BY_JPQL);
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

					list = (List<BenefitUsage>)QueryUtil.list(
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
	 * Returns the first Benefit Usage in the ordered set where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Benefit Usage
	 * @throws NoSuchBenefitUsageException if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage findByG_U_S_First(
			long groupId, long userId, int status,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = fetchByG_U_S_First(
			groupId, userId, status, orderByComparator);

		if (benefitUsage != null) {
			return benefitUsage;
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

		throw new NoSuchBenefitUsageException(sb.toString());
	}

	/**
	 * Returns the first Benefit Usage in the ordered set where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Benefit Usage, or <code>null</code> if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage fetchByG_U_S_First(
		long groupId, long userId, int status,
		OrderByComparator<BenefitUsage> orderByComparator) {

		List<BenefitUsage> list = findByG_U_S(
			groupId, userId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Benefit Usage in the ordered set where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Benefit Usage
	 * @throws NoSuchBenefitUsageException if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage findByG_U_S_Last(
			long groupId, long userId, int status,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = fetchByG_U_S_Last(
			groupId, userId, status, orderByComparator);

		if (benefitUsage != null) {
			return benefitUsage;
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

		throw new NoSuchBenefitUsageException(sb.toString());
	}

	/**
	 * Returns the last Benefit Usage in the ordered set where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Benefit Usage, or <code>null</code> if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage fetchByG_U_S_Last(
		long groupId, long userId, int status,
		OrderByComparator<BenefitUsage> orderByComparator) {

		int count = countByG_U_S(groupId, userId, status);

		if (count == 0) {
			return null;
		}

		List<BenefitUsage> list = findByG_U_S(
			groupId, userId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Benefit Usages before and after the current Benefit Usage in the ordered set where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param benefitUsageId the primary key of the current Benefit Usage
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Benefit Usage
	 * @throws NoSuchBenefitUsageException if a Benefit Usage with the primary key could not be found
	 */
	@Override
	public BenefitUsage[] findByG_U_S_PrevAndNext(
			long benefitUsageId, long groupId, long userId, int status,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = findByPrimaryKey(benefitUsageId);

		Session session = null;

		try {
			session = openSession();

			BenefitUsage[] array = new BenefitUsageImpl[3];

			array[0] = getByG_U_S_PrevAndNext(
				session, benefitUsage, groupId, userId, status,
				orderByComparator, true);

			array[1] = benefitUsage;

			array[2] = getByG_U_S_PrevAndNext(
				session, benefitUsage, groupId, userId, status,
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

	protected BenefitUsage getByG_U_S_PrevAndNext(
		Session session, BenefitUsage benefitUsage, long groupId, long userId,
		int status, OrderByComparator<BenefitUsage> orderByComparator,
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

		sb.append(_SQL_SELECT_BENEFITUSAGE_WHERE);

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
			sb.append(BenefitUsageModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(benefitUsage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BenefitUsage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the Benefit Usages that the user has permission to view where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the matching Benefit Usages that the user has permission to view
	 */
	@Override
	public List<BenefitUsage> filterFindByG_U_S(
		long groupId, long userId, int status) {

		return filterFindByG_U_S(
			groupId, userId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the Benefit Usages that the user has permission to view where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @return the range of matching Benefit Usages that the user has permission to view
	 */
	@Override
	public List<BenefitUsage> filterFindByG_U_S(
		long groupId, long userId, int status, int start, int end) {

		return filterFindByG_U_S(groupId, userId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages that the user has permissions to view where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Benefit Usages that the user has permission to view
	 */
	@Override
	public List<BenefitUsage> filterFindByG_U_S(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_BENEFITUSAGE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_BENEFITUSAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_U_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_S_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_BENEFITUSAGE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BenefitUsageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BenefitUsageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BenefitUsage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, BenefitUsageImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, BenefitUsageImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(userId);

			queryPos.add(status);

			return (List<BenefitUsage>)QueryUtil.list(
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
	 * Returns the Benefit Usages before and after the current Benefit Usage in the ordered set of Benefit Usages that the user has permission to view where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param benefitUsageId the primary key of the current Benefit Usage
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Benefit Usage
	 * @throws NoSuchBenefitUsageException if a Benefit Usage with the primary key could not be found
	 */
	@Override
	public BenefitUsage[] filterFindByG_U_S_PrevAndNext(
			long benefitUsageId, long groupId, long userId, int status,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_U_S_PrevAndNext(
				benefitUsageId, groupId, userId, status, orderByComparator);
		}

		BenefitUsage benefitUsage = findByPrimaryKey(benefitUsageId);

		Session session = null;

		try {
			session = openSession();

			BenefitUsage[] array = new BenefitUsageImpl[3];

			array[0] = filterGetByG_U_S_PrevAndNext(
				session, benefitUsage, groupId, userId, status,
				orderByComparator, true);

			array[1] = benefitUsage;

			array[2] = filterGetByG_U_S_PrevAndNext(
				session, benefitUsage, groupId, userId, status,
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

	protected BenefitUsage filterGetByG_U_S_PrevAndNext(
		Session session, BenefitUsage benefitUsage, long groupId, long userId,
		int status, OrderByComparator<BenefitUsage> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_BENEFITUSAGE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_BENEFITUSAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_U_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_S_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_S_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_BENEFITUSAGE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BenefitUsageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BenefitUsageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BenefitUsage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, BenefitUsageImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, BenefitUsageImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(userId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(benefitUsage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BenefitUsage> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the Benefit Usages that the user has permission to view where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @return the matching Benefit Usages that the user has permission to view
	 */
	@Override
	public List<BenefitUsage> filterFindByG_U_S(
		long groupId, long userId, int[] statuses) {

		return filterFindByG_U_S(
			groupId, userId, statuses, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the Benefit Usages that the user has permission to view where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @return the range of matching Benefit Usages that the user has permission to view
	 */
	@Override
	public List<BenefitUsage> filterFindByG_U_S(
		long groupId, long userId, int[] statuses, int start, int end) {

		return filterFindByG_U_S(groupId, userId, statuses, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages that the user has permission to view where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Benefit Usages that the user has permission to view
	 */
	@Override
	public List<BenefitUsage> filterFindByG_U_S(
		long groupId, long userId, int[] statuses, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_BENEFITUSAGE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_BENEFITUSAGE_NO_INLINE_DISTINCT_WHERE_1);
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
				_FILTER_SQL_SELECT_BENEFITUSAGE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BenefitUsageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BenefitUsageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BenefitUsage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, BenefitUsageImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, BenefitUsageImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(userId);

			return (List<BenefitUsage>)QueryUtil.list(
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
	 * Returns all the Benefit Usages where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @return the matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByG_U_S(
		long groupId, long userId, int[] statuses) {

		return findByG_U_S(
			groupId, userId, statuses, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the Benefit Usages where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @return the range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByG_U_S(
		long groupId, long userId, int[] statuses, int start, int end) {

		return findByG_U_S(groupId, userId, statuses, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByG_U_S(
		long groupId, long userId, int[] statuses, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator) {

		return findByG_U_S(
			groupId, userId, statuses, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages where groupId = &#63; and userId = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByG_U_S(
		long groupId, long userId, int[] statuses, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator,
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
					BenefitUsage.class)) {

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

			List<BenefitUsage> list = null;

			if (useFinderCache) {
				list = (List<BenefitUsage>)finderCache.getResult(
					_finderPathWithPaginationFindByG_U_S, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BenefitUsage benefitUsage : list) {
						if ((groupId != benefitUsage.getGroupId()) ||
							(userId != benefitUsage.getUserId()) ||
							!ArrayUtil.contains(
								statuses, benefitUsage.getStatus())) {

							list = null;

							break;
						}
					}
				}
			}

			if (list == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_SELECT_BENEFITUSAGE_WHERE);

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
					sb.append(BenefitUsageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(groupId);

					queryPos.add(userId);

					list = (List<BenefitUsage>)QueryUtil.list(
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
	 * Removes all the Benefit Usages where groupId = &#63; and userId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 */
	@Override
	public void removeByG_U_S(long groupId, long userId, int status) {
		for (BenefitUsage benefitUsage :
				findByG_U_S(
					groupId, userId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(benefitUsage);
		}
	}

	/**
	 * Returns the number of Benefit Usages where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the number of matching Benefit Usages
	 */
	@Override
	public int countByG_U_S(long groupId, long userId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BenefitUsage.class)) {

			FinderPath finderPath = _finderPathCountByG_U_S;

			Object[] finderArgs = new Object[] {groupId, userId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_BENEFITUSAGE_WHERE);

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
	 * Returns the number of Benefit Usages where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @return the number of matching Benefit Usages
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
					BenefitUsage.class)) {

			Object[] finderArgs = new Object[] {
				groupId, userId, StringUtil.merge(statuses)
			};

			Long count = (Long)finderCache.getResult(
				_finderPathWithPaginationCountByG_U_S, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler();

				sb.append(_SQL_COUNT_BENEFITUSAGE_WHERE);

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
	 * Returns the number of Benefit Usages that the user has permission to view where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the number of matching Benefit Usages that the user has permission to view
	 */
	@Override
	public int filterCountByG_U_S(long groupId, long userId, int status) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_U_S(groupId, userId, status);
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_BENEFITUSAGE_WHERE);

		sb.append(_FINDER_COLUMN_G_U_S_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_S_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_S_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BenefitUsage.class.getName(),
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
	 * Returns the number of Benefit Usages that the user has permission to view where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @return the number of matching Benefit Usages that the user has permission to view
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

		sb.append(_FILTER_SQL_COUNT_BENEFITUSAGE_WHERE);

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
			sb.toString(), BenefitUsage.class.getName(),
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
		"benefitUsage.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_U_S_USERID_2 =
		"benefitUsage.userId = ? AND ";

	private static final String _FINDER_COLUMN_G_U_S_STATUS_2 =
		"benefitUsage.status = ?";

	private static final String _FINDER_COLUMN_G_U_S_STATUS_7 =
		"benefitUsage.status IN (";

	private FinderPath _finderPathWithPaginationFindByG_U_NotS;
	private FinderPath _finderPathWithPaginationCountByG_U_NotS;

	/**
	 * Returns all the Benefit Usages where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByG_U_NotS(
		long groupId, long userId, int status) {

		return findByG_U_NotS(
			groupId, userId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the Benefit Usages where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @return the range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByG_U_NotS(
		long groupId, long userId, int status, int start, int end) {

		return findByG_U_NotS(groupId, userId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByG_U_NotS(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator) {

		return findByG_U_NotS(
			groupId, userId, status, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByG_U_NotS(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BenefitUsage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			finderPath = _finderPathWithPaginationFindByG_U_NotS;
			finderArgs = new Object[] {
				groupId, userId, status, start, end, orderByComparator
			};

			List<BenefitUsage> list = null;

			if (useFinderCache) {
				list = (List<BenefitUsage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BenefitUsage benefitUsage : list) {
						if ((groupId != benefitUsage.getGroupId()) ||
							(userId != benefitUsage.getUserId()) ||
							(status == benefitUsage.getStatus())) {

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

				sb.append(_SQL_SELECT_BENEFITUSAGE_WHERE);

				sb.append(_FINDER_COLUMN_G_U_NOTS_GROUPID_2);

				sb.append(_FINDER_COLUMN_G_U_NOTS_USERID_2);

				sb.append(_FINDER_COLUMN_G_U_NOTS_STATUS_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BenefitUsageModelImpl.ORDER_BY_JPQL);
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

					list = (List<BenefitUsage>)QueryUtil.list(
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
	 * Returns the first Benefit Usage in the ordered set where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Benefit Usage
	 * @throws NoSuchBenefitUsageException if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage findByG_U_NotS_First(
			long groupId, long userId, int status,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = fetchByG_U_NotS_First(
			groupId, userId, status, orderByComparator);

		if (benefitUsage != null) {
			return benefitUsage;
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

		throw new NoSuchBenefitUsageException(sb.toString());
	}

	/**
	 * Returns the first Benefit Usage in the ordered set where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Benefit Usage, or <code>null</code> if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage fetchByG_U_NotS_First(
		long groupId, long userId, int status,
		OrderByComparator<BenefitUsage> orderByComparator) {

		List<BenefitUsage> list = findByG_U_NotS(
			groupId, userId, status, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Benefit Usage in the ordered set where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Benefit Usage
	 * @throws NoSuchBenefitUsageException if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage findByG_U_NotS_Last(
			long groupId, long userId, int status,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = fetchByG_U_NotS_Last(
			groupId, userId, status, orderByComparator);

		if (benefitUsage != null) {
			return benefitUsage;
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

		throw new NoSuchBenefitUsageException(sb.toString());
	}

	/**
	 * Returns the last Benefit Usage in the ordered set where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Benefit Usage, or <code>null</code> if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage fetchByG_U_NotS_Last(
		long groupId, long userId, int status,
		OrderByComparator<BenefitUsage> orderByComparator) {

		int count = countByG_U_NotS(groupId, userId, status);

		if (count == 0) {
			return null;
		}

		List<BenefitUsage> list = findByG_U_NotS(
			groupId, userId, status, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Benefit Usages before and after the current Benefit Usage in the ordered set where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param benefitUsageId the primary key of the current Benefit Usage
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Benefit Usage
	 * @throws NoSuchBenefitUsageException if a Benefit Usage with the primary key could not be found
	 */
	@Override
	public BenefitUsage[] findByG_U_NotS_PrevAndNext(
			long benefitUsageId, long groupId, long userId, int status,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = findByPrimaryKey(benefitUsageId);

		Session session = null;

		try {
			session = openSession();

			BenefitUsage[] array = new BenefitUsageImpl[3];

			array[0] = getByG_U_NotS_PrevAndNext(
				session, benefitUsage, groupId, userId, status,
				orderByComparator, true);

			array[1] = benefitUsage;

			array[2] = getByG_U_NotS_PrevAndNext(
				session, benefitUsage, groupId, userId, status,
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

	protected BenefitUsage getByG_U_NotS_PrevAndNext(
		Session session, BenefitUsage benefitUsage, long groupId, long userId,
		int status, OrderByComparator<BenefitUsage> orderByComparator,
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

		sb.append(_SQL_SELECT_BENEFITUSAGE_WHERE);

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
			sb.append(BenefitUsageModelImpl.ORDER_BY_JPQL);
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
					orderByComparator.getOrderByConditionValues(benefitUsage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BenefitUsage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Returns all the Benefit Usages that the user has permission to view where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the matching Benefit Usages that the user has permission to view
	 */
	@Override
	public List<BenefitUsage> filterFindByG_U_NotS(
		long groupId, long userId, int status) {

		return filterFindByG_U_NotS(
			groupId, userId, status, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the Benefit Usages that the user has permission to view where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @return the range of matching Benefit Usages that the user has permission to view
	 */
	@Override
	public List<BenefitUsage> filterFindByG_U_NotS(
		long groupId, long userId, int status, int start, int end) {

		return filterFindByG_U_NotS(groupId, userId, status, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages that the user has permissions to view where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Benefit Usages that the user has permission to view
	 */
	@Override
	public List<BenefitUsage> filterFindByG_U_NotS(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator) {

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
			sb.append(_FILTER_SQL_SELECT_BENEFITUSAGE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_BENEFITUSAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_U_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_NOTS_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_NOTS_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_BENEFITUSAGE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BenefitUsageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BenefitUsageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BenefitUsage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		Session session = null;

		try {
			session = openSession();

			SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

			if (getDB().isSupportsInlineDistinct()) {
				sqlQuery.addEntity(
					_FILTER_ENTITY_ALIAS, BenefitUsageImpl.class);
			}
			else {
				sqlQuery.addEntity(
					_FILTER_ENTITY_TABLE, BenefitUsageImpl.class);
			}

			QueryPos queryPos = QueryPos.getInstance(sqlQuery);

			queryPos.add(groupId);

			queryPos.add(userId);

			queryPos.add(status);

			return (List<BenefitUsage>)QueryUtil.list(
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
	 * Returns the Benefit Usages before and after the current Benefit Usage in the ordered set of Benefit Usages that the user has permission to view where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param benefitUsageId the primary key of the current Benefit Usage
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Benefit Usage
	 * @throws NoSuchBenefitUsageException if a Benefit Usage with the primary key could not be found
	 */
	@Override
	public BenefitUsage[] filterFindByG_U_NotS_PrevAndNext(
			long benefitUsageId, long groupId, long userId, int status,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return findByG_U_NotS_PrevAndNext(
				benefitUsageId, groupId, userId, status, orderByComparator);
		}

		BenefitUsage benefitUsage = findByPrimaryKey(benefitUsageId);

		Session session = null;

		try {
			session = openSession();

			BenefitUsage[] array = new BenefitUsageImpl[3];

			array[0] = filterGetByG_U_NotS_PrevAndNext(
				session, benefitUsage, groupId, userId, status,
				orderByComparator, true);

			array[1] = benefitUsage;

			array[2] = filterGetByG_U_NotS_PrevAndNext(
				session, benefitUsage, groupId, userId, status,
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

	protected BenefitUsage filterGetByG_U_NotS_PrevAndNext(
		Session session, BenefitUsage benefitUsage, long groupId, long userId,
		int status, OrderByComparator<BenefitUsage> orderByComparator,
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
			sb.append(_FILTER_SQL_SELECT_BENEFITUSAGE_WHERE);
		}
		else {
			sb.append(
				_FILTER_SQL_SELECT_BENEFITUSAGE_NO_INLINE_DISTINCT_WHERE_1);
		}

		sb.append(_FINDER_COLUMN_G_U_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_NOTS_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_NOTS_STATUS_2);

		if (!getDB().isSupportsInlineDistinct()) {
			sb.append(
				_FILTER_SQL_SELECT_BENEFITUSAGE_NO_INLINE_DISTINCT_WHERE_2);
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
				sb.append(BenefitUsageModelImpl.ORDER_BY_SQL_INLINE_DISTINCT);
			}
			else {
				sb.append(BenefitUsageModelImpl.ORDER_BY_SQL);
			}
		}

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BenefitUsage.class.getName(),
			_FILTER_ENTITY_TABLE_FILTER_PK_COLUMN, groupId);

		SQLQuery sqlQuery = session.createSynchronizedSQLQuery(sql);

		sqlQuery.setFirstResult(0);
		sqlQuery.setMaxResults(2);

		if (getDB().isSupportsInlineDistinct()) {
			sqlQuery.addEntity(_FILTER_ENTITY_ALIAS, BenefitUsageImpl.class);
		}
		else {
			sqlQuery.addEntity(_FILTER_ENTITY_TABLE, BenefitUsageImpl.class);
		}

		QueryPos queryPos = QueryPos.getInstance(sqlQuery);

		queryPos.add(groupId);

		queryPos.add(userId);

		queryPos.add(status);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(benefitUsage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BenefitUsage> list = sqlQuery.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Benefit Usages where groupId = &#63; and userId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 */
	@Override
	public void removeByG_U_NotS(long groupId, long userId, int status) {
		for (BenefitUsage benefitUsage :
				findByG_U_NotS(
					groupId, userId, status, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(benefitUsage);
		}
	}

	/**
	 * Returns the number of Benefit Usages where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the number of matching Benefit Usages
	 */
	@Override
	public int countByG_U_NotS(long groupId, long userId, int status) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BenefitUsage.class)) {

			FinderPath finderPath = _finderPathWithPaginationCountByG_U_NotS;

			Object[] finderArgs = new Object[] {groupId, userId, status};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_COUNT_BENEFITUSAGE_WHERE);

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
	 * Returns the number of Benefit Usages that the user has permission to view where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the number of matching Benefit Usages that the user has permission to view
	 */
	@Override
	public int filterCountByG_U_NotS(long groupId, long userId, int status) {
		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return countByG_U_NotS(groupId, userId, status);
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_FILTER_SQL_COUNT_BENEFITUSAGE_WHERE);

		sb.append(_FINDER_COLUMN_G_U_NOTS_GROUPID_2);

		sb.append(_FINDER_COLUMN_G_U_NOTS_USERID_2);

		sb.append(_FINDER_COLUMN_G_U_NOTS_STATUS_2);

		String sql = InlineSQLHelperUtil.replacePermissionCheck(
			sb.toString(), BenefitUsage.class.getName(),
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
		"benefitUsage.groupId = ? AND ";

	private static final String _FINDER_COLUMN_G_U_NOTS_USERID_2 =
		"benefitUsage.userId = ? AND ";

	private static final String _FINDER_COLUMN_G_U_NOTS_STATUS_2 =
		"benefitUsage.status != ?";

	private FinderPath _finderPathWithPaginationFindByPlanEnrollment;
	private FinderPath _finderPathWithoutPaginationFindByPlanEnrollment;
	private FinderPath _finderPathCountByPlanEnrollment;

	/**
	 * Returns all the Benefit Usages where planEnrollmentId = &#63;.
	 *
	 * @param planEnrollmentId the plan enrollment ID
	 * @return the matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByPlanEnrollment(long planEnrollmentId) {
		return findByPlanEnrollment(
			planEnrollmentId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Benefit Usages where planEnrollmentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param planEnrollmentId the plan enrollment ID
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @return the range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByPlanEnrollment(
		long planEnrollmentId, int start, int end) {

		return findByPlanEnrollment(planEnrollmentId, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages where planEnrollmentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param planEnrollmentId the plan enrollment ID
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByPlanEnrollment(
		long planEnrollmentId, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator) {

		return findByPlanEnrollment(
			planEnrollmentId, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages where planEnrollmentId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param planEnrollmentId the plan enrollment ID
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByPlanEnrollment(
		long planEnrollmentId, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BenefitUsage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath =
						_finderPathWithoutPaginationFindByPlanEnrollment;
					finderArgs = new Object[] {planEnrollmentId};
				}
			}
			else if (useFinderCache) {
				finderPath = _finderPathWithPaginationFindByPlanEnrollment;
				finderArgs = new Object[] {
					planEnrollmentId, start, end, orderByComparator
				};
			}

			List<BenefitUsage> list = null;

			if (useFinderCache) {
				list = (List<BenefitUsage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BenefitUsage benefitUsage : list) {
						if (planEnrollmentId !=
								benefitUsage.getPlanEnrollmentId()) {

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

				sb.append(_SQL_SELECT_BENEFITUSAGE_WHERE);

				sb.append(_FINDER_COLUMN_PLANENROLLMENT_PLANENROLLMENTID_2);

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BenefitUsageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(planEnrollmentId);

					list = (List<BenefitUsage>)QueryUtil.list(
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
	 * Returns the first Benefit Usage in the ordered set where planEnrollmentId = &#63;.
	 *
	 * @param planEnrollmentId the plan enrollment ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Benefit Usage
	 * @throws NoSuchBenefitUsageException if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage findByPlanEnrollment_First(
			long planEnrollmentId,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = fetchByPlanEnrollment_First(
			planEnrollmentId, orderByComparator);

		if (benefitUsage != null) {
			return benefitUsage;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("planEnrollmentId=");
		sb.append(planEnrollmentId);

		sb.append("}");

		throw new NoSuchBenefitUsageException(sb.toString());
	}

	/**
	 * Returns the first Benefit Usage in the ordered set where planEnrollmentId = &#63;.
	 *
	 * @param planEnrollmentId the plan enrollment ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Benefit Usage, or <code>null</code> if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage fetchByPlanEnrollment_First(
		long planEnrollmentId,
		OrderByComparator<BenefitUsage> orderByComparator) {

		List<BenefitUsage> list = findByPlanEnrollment(
			planEnrollmentId, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Benefit Usage in the ordered set where planEnrollmentId = &#63;.
	 *
	 * @param planEnrollmentId the plan enrollment ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Benefit Usage
	 * @throws NoSuchBenefitUsageException if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage findByPlanEnrollment_Last(
			long planEnrollmentId,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = fetchByPlanEnrollment_Last(
			planEnrollmentId, orderByComparator);

		if (benefitUsage != null) {
			return benefitUsage;
		}

		StringBundler sb = new StringBundler(4);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("planEnrollmentId=");
		sb.append(planEnrollmentId);

		sb.append("}");

		throw new NoSuchBenefitUsageException(sb.toString());
	}

	/**
	 * Returns the last Benefit Usage in the ordered set where planEnrollmentId = &#63;.
	 *
	 * @param planEnrollmentId the plan enrollment ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Benefit Usage, or <code>null</code> if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage fetchByPlanEnrollment_Last(
		long planEnrollmentId,
		OrderByComparator<BenefitUsage> orderByComparator) {

		int count = countByPlanEnrollment(planEnrollmentId);

		if (count == 0) {
			return null;
		}

		List<BenefitUsage> list = findByPlanEnrollment(
			planEnrollmentId, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Benefit Usages before and after the current Benefit Usage in the ordered set where planEnrollmentId = &#63;.
	 *
	 * @param benefitUsageId the primary key of the current Benefit Usage
	 * @param planEnrollmentId the plan enrollment ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Benefit Usage
	 * @throws NoSuchBenefitUsageException if a Benefit Usage with the primary key could not be found
	 */
	@Override
	public BenefitUsage[] findByPlanEnrollment_PrevAndNext(
			long benefitUsageId, long planEnrollmentId,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = findByPrimaryKey(benefitUsageId);

		Session session = null;

		try {
			session = openSession();

			BenefitUsage[] array = new BenefitUsageImpl[3];

			array[0] = getByPlanEnrollment_PrevAndNext(
				session, benefitUsage, planEnrollmentId, orderByComparator,
				true);

			array[1] = benefitUsage;

			array[2] = getByPlanEnrollment_PrevAndNext(
				session, benefitUsage, planEnrollmentId, orderByComparator,
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

	protected BenefitUsage getByPlanEnrollment_PrevAndNext(
		Session session, BenefitUsage benefitUsage, long planEnrollmentId,
		OrderByComparator<BenefitUsage> orderByComparator, boolean previous) {

		StringBundler sb = null;

		if (orderByComparator != null) {
			sb = new StringBundler(
				4 + (orderByComparator.getOrderByConditionFields().length * 3) +
					(orderByComparator.getOrderByFields().length * 3));
		}
		else {
			sb = new StringBundler(3);
		}

		sb.append(_SQL_SELECT_BENEFITUSAGE_WHERE);

		sb.append(_FINDER_COLUMN_PLANENROLLMENT_PLANENROLLMENTID_2);

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
			sb.append(BenefitUsageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(planEnrollmentId);

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(benefitUsage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BenefitUsage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Benefit Usages where planEnrollmentId = &#63; from the database.
	 *
	 * @param planEnrollmentId the plan enrollment ID
	 */
	@Override
	public void removeByPlanEnrollment(long planEnrollmentId) {
		for (BenefitUsage benefitUsage :
				findByPlanEnrollment(
					planEnrollmentId, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
					null)) {

			remove(benefitUsage);
		}
	}

	/**
	 * Returns the number of Benefit Usages where planEnrollmentId = &#63;.
	 *
	 * @param planEnrollmentId the plan enrollment ID
	 * @return the number of matching Benefit Usages
	 */
	@Override
	public int countByPlanEnrollment(long planEnrollmentId) {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BenefitUsage.class)) {

			FinderPath finderPath = _finderPathCountByPlanEnrollment;

			Object[] finderArgs = new Object[] {planEnrollmentId};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(2);

				sb.append(_SQL_COUNT_BENEFITUSAGE_WHERE);

				sb.append(_FINDER_COLUMN_PLANENROLLMENT_PLANENROLLMENTID_2);

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(planEnrollmentId);

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

	private static final String
		_FINDER_COLUMN_PLANENROLLMENT_PLANENROLLMENTID_2 =
			"benefitUsage.planEnrollmentId = ?";

	private FinderPath _finderPathWithPaginationFindByPlanEnrollmentServiceDate;
	private FinderPath
		_finderPathWithoutPaginationFindByPlanEnrollmentServiceDate;
	private FinderPath _finderPathCountByPlanEnrollmentServiceDate;

	/**
	 * Returns all the Benefit Usages where planEnrollmentId = &#63; and serviceDate = &#63;.
	 *
	 * @param planEnrollmentId the plan enrollment ID
	 * @param serviceDate the service date
	 * @return the matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByPlanEnrollmentServiceDate(
		long planEnrollmentId, Date serviceDate) {

		return findByPlanEnrollmentServiceDate(
			planEnrollmentId, serviceDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	/**
	 * Returns a range of all the Benefit Usages where planEnrollmentId = &#63; and serviceDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param planEnrollmentId the plan enrollment ID
	 * @param serviceDate the service date
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @return the range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByPlanEnrollmentServiceDate(
		long planEnrollmentId, Date serviceDate, int start, int end) {

		return findByPlanEnrollmentServiceDate(
			planEnrollmentId, serviceDate, start, end, null);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages where planEnrollmentId = &#63; and serviceDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param planEnrollmentId the plan enrollment ID
	 * @param serviceDate the service date
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByPlanEnrollmentServiceDate(
		long planEnrollmentId, Date serviceDate, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator) {

		return findByPlanEnrollmentServiceDate(
			planEnrollmentId, serviceDate, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages where planEnrollmentId = &#63; and serviceDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param planEnrollmentId the plan enrollment ID
	 * @param serviceDate the service date
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findByPlanEnrollmentServiceDate(
		long planEnrollmentId, Date serviceDate, int start, int end,
		OrderByComparator<BenefitUsage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BenefitUsage.class)) {

			FinderPath finderPath = null;
			Object[] finderArgs = null;

			if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
				(orderByComparator == null)) {

				if (useFinderCache) {
					finderPath =
						_finderPathWithoutPaginationFindByPlanEnrollmentServiceDate;
					finderArgs = new Object[] {
						planEnrollmentId, _getTime(serviceDate)
					};
				}
			}
			else if (useFinderCache) {
				finderPath =
					_finderPathWithPaginationFindByPlanEnrollmentServiceDate;
				finderArgs = new Object[] {
					planEnrollmentId, _getTime(serviceDate), start, end,
					orderByComparator
				};
			}

			List<BenefitUsage> list = null;

			if (useFinderCache) {
				list = (List<BenefitUsage>)finderCache.getResult(
					finderPath, finderArgs, this);

				if ((list != null) && !list.isEmpty()) {
					for (BenefitUsage benefitUsage : list) {
						if ((planEnrollmentId !=
								benefitUsage.getPlanEnrollmentId()) ||
							!Objects.equals(
								serviceDate, benefitUsage.getServiceDate())) {

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

				sb.append(_SQL_SELECT_BENEFITUSAGE_WHERE);

				sb.append(
					_FINDER_COLUMN_PLANENROLLMENTSERVICEDATE_PLANENROLLMENTID_2);

				boolean bindServiceDate = false;

				if (serviceDate == null) {
					sb.append(
						_FINDER_COLUMN_PLANENROLLMENTSERVICEDATE_SERVICEDATE_1);
				}
				else {
					bindServiceDate = true;

					sb.append(
						_FINDER_COLUMN_PLANENROLLMENTSERVICEDATE_SERVICEDATE_2);
				}

				if (orderByComparator != null) {
					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);
				}
				else {
					sb.append(BenefitUsageModelImpl.ORDER_BY_JPQL);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(planEnrollmentId);

					if (bindServiceDate) {
						queryPos.add(new Timestamp(serviceDate.getTime()));
					}

					list = (List<BenefitUsage>)QueryUtil.list(
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
	 * Returns the first Benefit Usage in the ordered set where planEnrollmentId = &#63; and serviceDate = &#63;.
	 *
	 * @param planEnrollmentId the plan enrollment ID
	 * @param serviceDate the service date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Benefit Usage
	 * @throws NoSuchBenefitUsageException if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage findByPlanEnrollmentServiceDate_First(
			long planEnrollmentId, Date serviceDate,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = fetchByPlanEnrollmentServiceDate_First(
			planEnrollmentId, serviceDate, orderByComparator);

		if (benefitUsage != null) {
			return benefitUsage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("planEnrollmentId=");
		sb.append(planEnrollmentId);

		sb.append(", serviceDate=");
		sb.append(serviceDate);

		sb.append("}");

		throw new NoSuchBenefitUsageException(sb.toString());
	}

	/**
	 * Returns the first Benefit Usage in the ordered set where planEnrollmentId = &#63; and serviceDate = &#63;.
	 *
	 * @param planEnrollmentId the plan enrollment ID
	 * @param serviceDate the service date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching Benefit Usage, or <code>null</code> if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage fetchByPlanEnrollmentServiceDate_First(
		long planEnrollmentId, Date serviceDate,
		OrderByComparator<BenefitUsage> orderByComparator) {

		List<BenefitUsage> list = findByPlanEnrollmentServiceDate(
			planEnrollmentId, serviceDate, 0, 1, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the last Benefit Usage in the ordered set where planEnrollmentId = &#63; and serviceDate = &#63;.
	 *
	 * @param planEnrollmentId the plan enrollment ID
	 * @param serviceDate the service date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Benefit Usage
	 * @throws NoSuchBenefitUsageException if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage findByPlanEnrollmentServiceDate_Last(
			long planEnrollmentId, Date serviceDate,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = fetchByPlanEnrollmentServiceDate_Last(
			planEnrollmentId, serviceDate, orderByComparator);

		if (benefitUsage != null) {
			return benefitUsage;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(_NO_SUCH_ENTITY_WITH_KEY);

		sb.append("planEnrollmentId=");
		sb.append(planEnrollmentId);

		sb.append(", serviceDate=");
		sb.append(serviceDate);

		sb.append("}");

		throw new NoSuchBenefitUsageException(sb.toString());
	}

	/**
	 * Returns the last Benefit Usage in the ordered set where planEnrollmentId = &#63; and serviceDate = &#63;.
	 *
	 * @param planEnrollmentId the plan enrollment ID
	 * @param serviceDate the service date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching Benefit Usage, or <code>null</code> if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage fetchByPlanEnrollmentServiceDate_Last(
		long planEnrollmentId, Date serviceDate,
		OrderByComparator<BenefitUsage> orderByComparator) {

		int count = countByPlanEnrollmentServiceDate(
			planEnrollmentId, serviceDate);

		if (count == 0) {
			return null;
		}

		List<BenefitUsage> list = findByPlanEnrollmentServiceDate(
			planEnrollmentId, serviceDate, count - 1, count, orderByComparator);

		if (!list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * Returns the Benefit Usages before and after the current Benefit Usage in the ordered set where planEnrollmentId = &#63; and serviceDate = &#63;.
	 *
	 * @param benefitUsageId the primary key of the current Benefit Usage
	 * @param planEnrollmentId the plan enrollment ID
	 * @param serviceDate the service date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next Benefit Usage
	 * @throws NoSuchBenefitUsageException if a Benefit Usage with the primary key could not be found
	 */
	@Override
	public BenefitUsage[] findByPlanEnrollmentServiceDate_PrevAndNext(
			long benefitUsageId, long planEnrollmentId, Date serviceDate,
			OrderByComparator<BenefitUsage> orderByComparator)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = findByPrimaryKey(benefitUsageId);

		Session session = null;

		try {
			session = openSession();

			BenefitUsage[] array = new BenefitUsageImpl[3];

			array[0] = getByPlanEnrollmentServiceDate_PrevAndNext(
				session, benefitUsage, planEnrollmentId, serviceDate,
				orderByComparator, true);

			array[1] = benefitUsage;

			array[2] = getByPlanEnrollmentServiceDate_PrevAndNext(
				session, benefitUsage, planEnrollmentId, serviceDate,
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

	protected BenefitUsage getByPlanEnrollmentServiceDate_PrevAndNext(
		Session session, BenefitUsage benefitUsage, long planEnrollmentId,
		Date serviceDate, OrderByComparator<BenefitUsage> orderByComparator,
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

		sb.append(_SQL_SELECT_BENEFITUSAGE_WHERE);

		sb.append(_FINDER_COLUMN_PLANENROLLMENTSERVICEDATE_PLANENROLLMENTID_2);

		boolean bindServiceDate = false;

		if (serviceDate == null) {
			sb.append(_FINDER_COLUMN_PLANENROLLMENTSERVICEDATE_SERVICEDATE_1);
		}
		else {
			bindServiceDate = true;

			sb.append(_FINDER_COLUMN_PLANENROLLMENTSERVICEDATE_SERVICEDATE_2);
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
			sb.append(BenefitUsageModelImpl.ORDER_BY_JPQL);
		}

		String sql = sb.toString();

		Query query = session.createQuery(sql);

		query.setFirstResult(0);
		query.setMaxResults(2);

		QueryPos queryPos = QueryPos.getInstance(query);

		queryPos.add(planEnrollmentId);

		if (bindServiceDate) {
			queryPos.add(new Timestamp(serviceDate.getTime()));
		}

		if (orderByComparator != null) {
			for (Object orderByConditionValue :
					orderByComparator.getOrderByConditionValues(benefitUsage)) {

				queryPos.add(orderByConditionValue);
			}
		}

		List<BenefitUsage> list = query.list();

		if (list.size() == 2) {
			return list.get(1);
		}
		else {
			return null;
		}
	}

	/**
	 * Removes all the Benefit Usages where planEnrollmentId = &#63; and serviceDate = &#63; from the database.
	 *
	 * @param planEnrollmentId the plan enrollment ID
	 * @param serviceDate the service date
	 */
	@Override
	public void removeByPlanEnrollmentServiceDate(
		long planEnrollmentId, Date serviceDate) {

		for (BenefitUsage benefitUsage :
				findByPlanEnrollmentServiceDate(
					planEnrollmentId, serviceDate, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			remove(benefitUsage);
		}
	}

	/**
	 * Returns the number of Benefit Usages where planEnrollmentId = &#63; and serviceDate = &#63;.
	 *
	 * @param planEnrollmentId the plan enrollment ID
	 * @param serviceDate the service date
	 * @return the number of matching Benefit Usages
	 */
	@Override
	public int countByPlanEnrollmentServiceDate(
		long planEnrollmentId, Date serviceDate) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BenefitUsage.class)) {

			FinderPath finderPath = _finderPathCountByPlanEnrollmentServiceDate;

			Object[] finderArgs = new Object[] {
				planEnrollmentId, _getTime(serviceDate)
			};

			Long count = (Long)finderCache.getResult(
				finderPath, finderArgs, this);

			if (count == null) {
				StringBundler sb = new StringBundler(3);

				sb.append(_SQL_COUNT_BENEFITUSAGE_WHERE);

				sb.append(
					_FINDER_COLUMN_PLANENROLLMENTSERVICEDATE_PLANENROLLMENTID_2);

				boolean bindServiceDate = false;

				if (serviceDate == null) {
					sb.append(
						_FINDER_COLUMN_PLANENROLLMENTSERVICEDATE_SERVICEDATE_1);
				}
				else {
					bindServiceDate = true;

					sb.append(
						_FINDER_COLUMN_PLANENROLLMENTSERVICEDATE_SERVICEDATE_2);
				}

				String sql = sb.toString();

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					QueryPos queryPos = QueryPos.getInstance(query);

					queryPos.add(planEnrollmentId);

					if (bindServiceDate) {
						queryPos.add(new Timestamp(serviceDate.getTime()));
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

	private static final String
		_FINDER_COLUMN_PLANENROLLMENTSERVICEDATE_PLANENROLLMENTID_2 =
			"benefitUsage.planEnrollmentId = ? AND ";

	private static final String
		_FINDER_COLUMN_PLANENROLLMENTSERVICEDATE_SERVICEDATE_1 =
			"benefitUsage.serviceDate IS NULL";

	private static final String
		_FINDER_COLUMN_PLANENROLLMENTSERVICEDATE_SERVICEDATE_2 =
			"benefitUsage.serviceDate = ?";

	private FinderPath _finderPathFetchByERC_G;

	/**
	 * Returns the Benefit Usage where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchBenefitUsageException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching Benefit Usage
	 * @throws NoSuchBenefitUsageException if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage findByERC_G(String externalReferenceCode, long groupId)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = fetchByERC_G(
			externalReferenceCode, groupId);

		if (benefitUsage == null) {
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

			throw new NoSuchBenefitUsageException(sb.toString());
		}

		return benefitUsage;
	}

	/**
	 * Returns the Benefit Usage where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching Benefit Usage, or <code>null</code> if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return fetchByERC_G(externalReferenceCode, groupId, true);
	}

	/**
	 * Returns the Benefit Usage where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching Benefit Usage, or <code>null</code> if a matching Benefit Usage could not be found
	 */
	@Override
	public BenefitUsage fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BenefitUsage.class)) {

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

			if (result instanceof BenefitUsage) {
				BenefitUsage benefitUsage = (BenefitUsage)result;

				if (!Objects.equals(
						externalReferenceCode,
						benefitUsage.getExternalReferenceCode()) ||
					(groupId != benefitUsage.getGroupId())) {

					result = null;
				}
			}

			if (result == null) {
				StringBundler sb = new StringBundler(4);

				sb.append(_SQL_SELECT_BENEFITUSAGE_WHERE);

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

					List<BenefitUsage> list = query.list();

					if (list.isEmpty()) {
						if (useFinderCache) {
							finderCache.putResult(
								_finderPathFetchByERC_G, finderArgs, list);
						}
					}
					else {
						BenefitUsage benefitUsage = list.get(0);

						result = benefitUsage;

						cacheResult(benefitUsage);
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
				return (BenefitUsage)result;
			}
		}
	}

	/**
	 * Removes the Benefit Usage where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the Benefit Usage that was removed
	 */
	@Override
	public BenefitUsage removeByERC_G(
			String externalReferenceCode, long groupId)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = findByERC_G(externalReferenceCode, groupId);

		return remove(benefitUsage);
	}

	/**
	 * Returns the number of Benefit Usages where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching Benefit Usages
	 */
	@Override
	public int countByERC_G(String externalReferenceCode, long groupId) {
		BenefitUsage benefitUsage = fetchByERC_G(
			externalReferenceCode, groupId);

		if (benefitUsage == null) {
			return 0;
		}

		return 1;
	}

	private static final String _FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_2 =
		"benefitUsage.externalReferenceCode = ? AND ";

	private static final String _FINDER_COLUMN_ERC_G_EXTERNALREFERENCECODE_3 =
		"(benefitUsage.externalReferenceCode IS NULL OR benefitUsage.externalReferenceCode = '') AND ";

	private static final String _FINDER_COLUMN_ERC_G_GROUPID_2 =
		"benefitUsage.groupId = ?";

	public BenefitUsagePersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");

		setDBColumnNames(dbColumnNames);

		setModelClass(BenefitUsage.class);

		setModelImplClass(BenefitUsageImpl.class);
		setModelPKClass(long.class);

		setTable(BenefitUsageTable.INSTANCE);
	}

	/**
	 * Caches the Benefit Usage in the entity cache if it is enabled.
	 *
	 * @param benefitUsage the Benefit Usage
	 */
	@Override
	public void cacheResult(BenefitUsage benefitUsage) {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					benefitUsage.getCtCollectionId())) {

			entityCache.putResult(
				BenefitUsageImpl.class, benefitUsage.getPrimaryKey(),
				benefitUsage);

			finderCache.putResult(
				_finderPathFetchByUUID_G,
				new Object[] {
					benefitUsage.getUuid(), benefitUsage.getGroupId()
				},
				benefitUsage);

			finderCache.putResult(
				_finderPathFetchByERC_G,
				new Object[] {
					benefitUsage.getExternalReferenceCode(),
					benefitUsage.getGroupId()
				},
				benefitUsage);
		}
	}

	private int _valueObjectFinderCacheListThreshold;

	/**
	 * Caches the Benefit Usages in the entity cache if it is enabled.
	 *
	 * @param benefitUsages the Benefit Usages
	 */
	@Override
	public void cacheResult(List<BenefitUsage> benefitUsages) {
		if ((_valueObjectFinderCacheListThreshold == 0) ||
			((_valueObjectFinderCacheListThreshold > 0) &&
			 (benefitUsages.size() > _valueObjectFinderCacheListThreshold))) {

			return;
		}

		for (BenefitUsage benefitUsage : benefitUsages) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						benefitUsage.getCtCollectionId())) {

				if (entityCache.getResult(
						BenefitUsageImpl.class, benefitUsage.getPrimaryKey()) ==
							null) {

					cacheResult(benefitUsage);
				}
			}
		}
	}

	/**
	 * Clears the cache for all Benefit Usages.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(BenefitUsageImpl.class);

		finderCache.clearCache(BenefitUsageImpl.class);
	}

	/**
	 * Clears the cache for the Benefit Usage.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(BenefitUsage benefitUsage) {
		entityCache.removeResult(BenefitUsageImpl.class, benefitUsage);
	}

	@Override
	public void clearCache(List<BenefitUsage> benefitUsages) {
		for (BenefitUsage benefitUsage : benefitUsages) {
			entityCache.removeResult(BenefitUsageImpl.class, benefitUsage);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(BenefitUsageImpl.class);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(BenefitUsageImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		BenefitUsageModelImpl benefitUsageModelImpl) {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					benefitUsageModelImpl.getCtCollectionId())) {

			Object[] args = new Object[] {
				benefitUsageModelImpl.getUuid(),
				benefitUsageModelImpl.getGroupId()
			};

			finderCache.putResult(
				_finderPathFetchByUUID_G, args, benefitUsageModelImpl);

			args = new Object[] {
				benefitUsageModelImpl.getExternalReferenceCode(),
				benefitUsageModelImpl.getGroupId()
			};

			finderCache.putResult(
				_finderPathFetchByERC_G, args, benefitUsageModelImpl);
		}
	}

	/**
	 * Creates a new Benefit Usage with the primary key. Does not add the Benefit Usage to the database.
	 *
	 * @param benefitUsageId the primary key for the new Benefit Usage
	 * @return the new Benefit Usage
	 */
	@Override
	public BenefitUsage create(long benefitUsageId) {
		BenefitUsage benefitUsage = new BenefitUsageImpl();

		benefitUsage.setNew(true);
		benefitUsage.setPrimaryKey(benefitUsageId);

		String uuid = PortalUUIDUtil.generate();

		benefitUsage.setUuid(uuid);

		benefitUsage.setCompanyId(CompanyThreadLocal.getCompanyId());

		return benefitUsage;
	}

	/**
	 * Removes the Benefit Usage with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param benefitUsageId the primary key of the Benefit Usage
	 * @return the Benefit Usage that was removed
	 * @throws NoSuchBenefitUsageException if a Benefit Usage with the primary key could not be found
	 */
	@Override
	public BenefitUsage remove(long benefitUsageId)
		throws NoSuchBenefitUsageException {

		return remove((Serializable)benefitUsageId);
	}

	/**
	 * Removes the Benefit Usage with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the Benefit Usage
	 * @return the Benefit Usage that was removed
	 * @throws NoSuchBenefitUsageException if a Benefit Usage with the primary key could not be found
	 */
	@Override
	public BenefitUsage remove(Serializable primaryKey)
		throws NoSuchBenefitUsageException {

		Session session = null;

		try {
			session = openSession();

			BenefitUsage benefitUsage = (BenefitUsage)session.get(
				BenefitUsageImpl.class, primaryKey);

			if (benefitUsage == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchBenefitUsageException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(benefitUsage);
		}
		catch (NoSuchBenefitUsageException noSuchEntityException) {
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
	protected BenefitUsage removeImpl(BenefitUsage benefitUsage) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(benefitUsage)) {
				benefitUsage = (BenefitUsage)session.get(
					BenefitUsageImpl.class, benefitUsage.getPrimaryKeyObj());
			}

			if ((benefitUsage != null) &&
				ctPersistenceHelper.isRemove(benefitUsage)) {

				session.delete(benefitUsage);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (benefitUsage != null) {
			clearCache(benefitUsage);
		}

		return benefitUsage;
	}

	@Override
	public BenefitUsage updateImpl(BenefitUsage benefitUsage) {
		boolean isNew = benefitUsage.isNew();

		if (!(benefitUsage instanceof BenefitUsageModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(benefitUsage.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					benefitUsage);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in benefitUsage proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom BenefitUsage implementation " +
					benefitUsage.getClass());
		}

		BenefitUsageModelImpl benefitUsageModelImpl =
			(BenefitUsageModelImpl)benefitUsage;

		if (Validator.isNull(benefitUsage.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			benefitUsage.setUuid(uuid);
		}

		if (Validator.isNull(benefitUsage.getExternalReferenceCode())) {
			benefitUsage.setExternalReferenceCode(benefitUsage.getUuid());
		}
		else {
			if (!Objects.equals(
					benefitUsageModelImpl.getColumnOriginalValue(
						"externalReferenceCode"),
					benefitUsage.getExternalReferenceCode())) {

				long userId = GetterUtil.getLong(
					PrincipalThreadLocal.getName());

				if (userId > 0) {
					long companyId = benefitUsage.getCompanyId();

					long groupId = benefitUsage.getGroupId();

					long classPK = 0;

					if (!isNew) {
						classPK = benefitUsage.getPrimaryKey();
					}

					try {
						benefitUsage.setExternalReferenceCode(
							SanitizerUtil.sanitize(
								companyId, groupId, userId,
								BenefitUsage.class.getName(), classPK,
								ContentTypes.TEXT_HTML, Sanitizer.MODE_ALL,
								benefitUsage.getExternalReferenceCode(), null));
					}
					catch (SanitizerException sanitizerException) {
						throw new SystemException(sanitizerException);
					}
				}
			}

			BenefitUsage ercBenefitUsage = fetchByERC_G(
				benefitUsage.getExternalReferenceCode(),
				benefitUsage.getGroupId());

			if (isNew) {
				if (ercBenefitUsage != null) {
					throw new DuplicateBenefitUsageExternalReferenceCodeException(
						"Duplicate Benefit Usage with external reference code " +
							benefitUsage.getExternalReferenceCode() +
								" and group " + benefitUsage.getGroupId());
				}
			}
			else {
				if ((ercBenefitUsage != null) &&
					(benefitUsage.getBenefitUsageId() !=
						ercBenefitUsage.getBenefitUsageId())) {

					throw new DuplicateBenefitUsageExternalReferenceCodeException(
						"Duplicate Benefit Usage with external reference code " +
							benefitUsage.getExternalReferenceCode() +
								" and group " + benefitUsage.getGroupId());
				}
			}
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (benefitUsage.getCreateDate() == null)) {
			if (serviceContext == null) {
				benefitUsage.setCreateDate(date);
			}
			else {
				benefitUsage.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!benefitUsageModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				benefitUsage.setModifiedDate(date);
			}
			else {
				benefitUsage.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(benefitUsage)) {
				if (!isNew) {
					session.evict(
						BenefitUsageImpl.class,
						benefitUsage.getPrimaryKeyObj());
				}

				session.save(benefitUsage);
			}
			else {
				benefitUsage = (BenefitUsage)session.merge(benefitUsage);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		entityCache.putResult(
			BenefitUsageImpl.class, benefitUsageModelImpl, false, true);

		cacheUniqueFindersCache(benefitUsageModelImpl);

		if (isNew) {
			benefitUsage.setNew(false);
		}

		benefitUsage.resetOriginalValues();

		return benefitUsage;
	}

	/**
	 * Returns the Benefit Usage with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the Benefit Usage
	 * @return the Benefit Usage
	 * @throws NoSuchBenefitUsageException if a Benefit Usage with the primary key could not be found
	 */
	@Override
	public BenefitUsage findByPrimaryKey(Serializable primaryKey)
		throws NoSuchBenefitUsageException {

		BenefitUsage benefitUsage = fetchByPrimaryKey(primaryKey);

		if (benefitUsage == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchBenefitUsageException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return benefitUsage;
	}

	/**
	 * Returns the Benefit Usage with the primary key or throws a <code>NoSuchBenefitUsageException</code> if it could not be found.
	 *
	 * @param benefitUsageId the primary key of the Benefit Usage
	 * @return the Benefit Usage
	 * @throws NoSuchBenefitUsageException if a Benefit Usage with the primary key could not be found
	 */
	@Override
	public BenefitUsage findByPrimaryKey(long benefitUsageId)
		throws NoSuchBenefitUsageException {

		return findByPrimaryKey((Serializable)benefitUsageId);
	}

	/**
	 * Returns the Benefit Usage with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the Benefit Usage
	 * @return the Benefit Usage, or <code>null</code> if a Benefit Usage with the primary key could not be found
	 */
	@Override
	public BenefitUsage fetchByPrimaryKey(Serializable primaryKey) {
		if (ctPersistenceHelper.isProductionMode(
				BenefitUsage.class, primaryKey)) {

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKey(primaryKey);
			}
		}

		BenefitUsage benefitUsage = (BenefitUsage)entityCache.getResult(
			BenefitUsageImpl.class, primaryKey);

		if (benefitUsage != null) {
			return benefitUsage;
		}

		Session session = null;

		try {
			session = openSession();

			benefitUsage = (BenefitUsage)session.get(
				BenefitUsageImpl.class, primaryKey);

			if (benefitUsage != null) {
				cacheResult(benefitUsage);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return benefitUsage;
	}

	/**
	 * Returns the Benefit Usage with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param benefitUsageId the primary key of the Benefit Usage
	 * @return the Benefit Usage, or <code>null</code> if a Benefit Usage with the primary key could not be found
	 */
	@Override
	public BenefitUsage fetchByPrimaryKey(long benefitUsageId) {
		return fetchByPrimaryKey((Serializable)benefitUsageId);
	}

	@Override
	public Map<Serializable, BenefitUsage> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (ctPersistenceHelper.isProductionMode(BenefitUsage.class)) {
			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.
						setProductionModeWithSafeCloseable()) {

				return super.fetchByPrimaryKeys(primaryKeys);
			}
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, BenefitUsage> map =
			new HashMap<Serializable, BenefitUsage>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			BenefitUsage benefitUsage = fetchByPrimaryKey(primaryKey);

			if (benefitUsage != null) {
				map.put(primaryKey, benefitUsage);
			}

			return map;
		}

		Set<Serializable> uncachedPrimaryKeys = null;

		for (Serializable primaryKey : primaryKeys) {
			try (SafeCloseable safeCloseable =
					ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
						BenefitUsage.class, primaryKey)) {

				BenefitUsage benefitUsage = (BenefitUsage)entityCache.getResult(
					BenefitUsageImpl.class, primaryKey);

				if (benefitUsage == null) {
					if (uncachedPrimaryKeys == null) {
						uncachedPrimaryKeys = new HashSet<>();
					}

					uncachedPrimaryKeys.add(primaryKey);
				}
				else {
					map.put(primaryKey, benefitUsage);
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

			for (BenefitUsage benefitUsage : (List<BenefitUsage>)query.list()) {
				map.put(benefitUsage.getPrimaryKeyObj(), benefitUsage);

				cacheResult(benefitUsage);
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
	 * Returns all the Benefit Usages.
	 *
	 * @return the Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the Benefit Usages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @return the range of Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findAll(int start, int end) {
		return findAll(start, end, null);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findAll(
		int start, int end, OrderByComparator<BenefitUsage> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the Benefit Usages.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>BenefitUsageModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of Benefit Usages
	 * @param end the upper bound of the range of Benefit Usages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of Benefit Usages
	 */
	@Override
	public List<BenefitUsage> findAll(
		int start, int end, OrderByComparator<BenefitUsage> orderByComparator,
		boolean useFinderCache) {

		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BenefitUsage.class)) {

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

			List<BenefitUsage> list = null;

			if (useFinderCache) {
				list = (List<BenefitUsage>)finderCache.getResult(
					finderPath, finderArgs, this);
			}

			if (list == null) {
				StringBundler sb = null;
				String sql = null;

				if (orderByComparator != null) {
					sb = new StringBundler(
						2 + (orderByComparator.getOrderByFields().length * 2));

					sb.append(_SQL_SELECT_BENEFITUSAGE);

					appendOrderByComparator(
						sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

					sql = sb.toString();
				}
				else {
					sql = _SQL_SELECT_BENEFITUSAGE;

					sql = sql.concat(BenefitUsageModelImpl.ORDER_BY_JPQL);
				}

				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(sql);

					list = (List<BenefitUsage>)QueryUtil.list(
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
	 * Removes all the Benefit Usages from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (BenefitUsage benefitUsage : findAll()) {
			remove(benefitUsage);
		}
	}

	/**
	 * Returns the number of Benefit Usages.
	 *
	 * @return the number of Benefit Usages
	 */
	@Override
	public int countAll() {
		try (SafeCloseable safeCloseable =
				ctPersistenceHelper.setCTCollectionIdWithSafeCloseable(
					BenefitUsage.class)) {

			Long count = (Long)finderCache.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);

			if (count == null) {
				Session session = null;

				try {
					session = openSession();

					Query query = session.createQuery(_SQL_COUNT_BENEFITUSAGE);

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
		return "benefitUsageId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_BENEFITUSAGE;
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
		return BenefitUsageModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "CIBT_BenefitUsage";
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
		ctMergeColumnNames.add("planEnrollmentId");
		ctMergeColumnNames.add("benefitType");
		ctMergeColumnNames.add("amountUsedCents");
		ctMergeColumnNames.add("serviceDate");
		ctMergeColumnNames.add("reference");
		ctMergeColumnNames.add("notes");
		ctMergeColumnNames.add("sourceType");
		ctMergeColumnNames.add("sourceReference");
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
			CTColumnResolutionType.PK, Collections.singleton("benefitUsageId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"uuid_", "groupId"});

		_uniqueIndexColumnNames.add(
			new String[] {"externalReferenceCode", "groupId"});
	}

	/**
	 * Initializes the Benefit Usage persistence.
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

		_finderPathWithPaginationFindByPE_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByPE_S",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"planEnrollmentId", "status"}, true);

		_finderPathWithoutPaginationFindByPE_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByPE_S",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"planEnrollmentId", "status"}, true);

		_finderPathCountByPE_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByPE_S",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"planEnrollmentId", "status"}, false);

		_finderPathWithPaginationFindByPE_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByPE_NotS",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				OrderByComparator.class.getName()
			},
			new String[] {"planEnrollmentId", "status"}, true);

		_finderPathWithPaginationCountByPE_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByPE_NotS",
			new String[] {Long.class.getName(), Integer.class.getName()},
			new String[] {"planEnrollmentId", "status"}, false);

		_finderPathWithPaginationFindByG_PE_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_PE_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "planEnrollmentId", "status"}, true);

		_finderPathWithoutPaginationFindByG_PE_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_PE_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "planEnrollmentId", "status"}, true);

		_finderPathCountByG_PE_S = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_PE_S",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "planEnrollmentId", "status"}, false);

		_finderPathWithPaginationFindByG_PE_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_PE_NotS",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"groupId", "planEnrollmentId", "status"}, true);

		_finderPathWithPaginationCountByG_PE_NotS = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByG_PE_NotS",
			new String[] {
				Long.class.getName(), Long.class.getName(),
				Integer.class.getName()
			},
			new String[] {"groupId", "planEnrollmentId", "status"}, false);

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

		_finderPathWithPaginationFindByPlanEnrollment = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByPlanEnrollment",
			new String[] {
				Long.class.getName(), Integer.class.getName(),
				Integer.class.getName(), OrderByComparator.class.getName()
			},
			new String[] {"planEnrollmentId"}, true);

		_finderPathWithoutPaginationFindByPlanEnrollment = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByPlanEnrollment",
			new String[] {Long.class.getName()},
			new String[] {"planEnrollmentId"}, true);

		_finderPathCountByPlanEnrollment = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByPlanEnrollment",
			new String[] {Long.class.getName()},
			new String[] {"planEnrollmentId"}, false);

		_finderPathWithPaginationFindByPlanEnrollmentServiceDate =
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
				"findByPlanEnrollmentServiceDate",
				new String[] {
					Long.class.getName(), Date.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"planEnrollmentId", "serviceDate"}, true);

		_finderPathWithoutPaginationFindByPlanEnrollmentServiceDate =
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
				"findByPlanEnrollmentServiceDate",
				new String[] {Long.class.getName(), Date.class.getName()},
				new String[] {"planEnrollmentId", "serviceDate"}, true);

		_finderPathCountByPlanEnrollmentServiceDate = new FinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
			"countByPlanEnrollmentServiceDate",
			new String[] {Long.class.getName(), Date.class.getName()},
			new String[] {"planEnrollmentId", "serviceDate"}, false);

		_finderPathFetchByERC_G = new FinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByERC_G",
			new String[] {String.class.getName(), Long.class.getName()},
			new String[] {"externalReferenceCode", "groupId"}, true);

		BenefitUsageUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		BenefitUsageUtil.setPersistence(null);

		entityCache.removeCache(BenefitUsageImpl.class.getName());
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

	private static Long _getTime(Date date) {
		if (date == null) {
			return null;
		}

		return date.getTime();
	}

	private static final String _SQL_SELECT_BENEFITUSAGE =
		"SELECT benefitUsage FROM BenefitUsage benefitUsage";

	private static final String _SQL_SELECT_BENEFITUSAGE_WHERE =
		"SELECT benefitUsage FROM BenefitUsage benefitUsage WHERE ";

	private static final String _SQL_COUNT_BENEFITUSAGE =
		"SELECT COUNT(benefitUsage) FROM BenefitUsage benefitUsage";

	private static final String _SQL_COUNT_BENEFITUSAGE_WHERE =
		"SELECT COUNT(benefitUsage) FROM BenefitUsage benefitUsage WHERE ";

	private static final String _FILTER_ENTITY_TABLE_FILTER_PK_COLUMN =
		"benefitUsage.benefitUsageId";

	private static final String _FILTER_SQL_SELECT_BENEFITUSAGE_WHERE =
		"SELECT DISTINCT {benefitUsage.*} FROM CIBT_BenefitUsage benefitUsage WHERE ";

	private static final String
		_FILTER_SQL_SELECT_BENEFITUSAGE_NO_INLINE_DISTINCT_WHERE_1 =
			"SELECT {CIBT_BenefitUsage.*} FROM (SELECT DISTINCT benefitUsage.benefitUsageId FROM CIBT_BenefitUsage benefitUsage WHERE ";

	private static final String
		_FILTER_SQL_SELECT_BENEFITUSAGE_NO_INLINE_DISTINCT_WHERE_2 =
			") TEMP_TABLE INNER JOIN CIBT_BenefitUsage ON TEMP_TABLE.benefitUsageId = CIBT_BenefitUsage.benefitUsageId";

	private static final String _FILTER_SQL_COUNT_BENEFITUSAGE_WHERE =
		"SELECT COUNT(DISTINCT benefitUsage.benefitUsageId) AS COUNT_VALUE FROM CIBT_BenefitUsage benefitUsage WHERE ";

	private static final String _FILTER_ENTITY_ALIAS = "benefitUsage";

	private static final String _FILTER_ENTITY_TABLE = "CIBT_BenefitUsage";

	private static final String _ORDER_BY_ENTITY_ALIAS = "benefitUsage.";

	private static final String _ORDER_BY_ENTITY_TABLE = "CIBT_BenefitUsage.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No BenefitUsage exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No BenefitUsage exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		BenefitUsagePersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}