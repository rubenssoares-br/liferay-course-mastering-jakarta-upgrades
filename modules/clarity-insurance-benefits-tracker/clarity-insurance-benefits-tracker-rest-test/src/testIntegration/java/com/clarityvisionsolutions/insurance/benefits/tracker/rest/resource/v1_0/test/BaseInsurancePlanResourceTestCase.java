package com.clarityvisionsolutions.insurance.benefits.tracker.rest.resource.v1_0.test;

import com.clarityvisionsolutions.insurance.benefits.tracker.rest.client.dto.v1_0.InsurancePlan;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.client.dto.v1_0.PlanEnrollment;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.client.http.HttpInvoker;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.client.pagination.Page;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.client.pagination.Pagination;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.client.resource.v1_0.InsurancePlanResource;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.client.serdes.v1_0.InsurancePlanSerDes;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONDeserializer;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsValues;
import com.liferay.portal.vulcan.resource.EntityModelResource;

import jakarta.annotation.Generated;

import jakarta.ws.rs.core.MultivaluedHashMap;

import java.lang.reflect.Method;

import java.text.Format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author dnebinger
 * @generated
 */
@Generated("")
public abstract class BaseInsurancePlanResourceTestCase {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_format = FastDateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");
	}

	@Before
	public void setUp() throws Exception {
		irrelevantGroup = GroupTestUtil.addGroup();
		testGroup = GroupTestUtil.addGroup();

		testCompany = CompanyLocalServiceUtil.getCompany(
			testGroup.getCompanyId());

		_insurancePlanResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		insurancePlanResource = InsurancePlanResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	@After
	public void tearDown() throws Exception {
		GroupTestUtil.deleteGroup(irrelevantGroup);
		GroupTestUtil.deleteGroup(testGroup);
	}

	@Test
	public void testClientSerDesToDTO() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		InsurancePlan insurancePlan1 = randomInsurancePlan();

		String json = objectMapper.writeValueAsString(insurancePlan1);

		InsurancePlan insurancePlan2 = InsurancePlanSerDes.toDTO(json);

		Assert.assertTrue(equals(insurancePlan1, insurancePlan2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		InsurancePlan insurancePlan = randomInsurancePlan();

		String json1 = objectMapper.writeValueAsString(insurancePlan);
		String json2 = InsurancePlanSerDes.toJSON(insurancePlan);

		Assert.assertEquals(
			objectMapper.readTree(json1), objectMapper.readTree(json2));
	}

	protected ObjectMapper getClientSerDesObjectMapper() {
		return new ObjectMapper() {
			{
				configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
				configure(
					SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
				enable(SerializationFeature.INDENT_OUTPUT);
				setDateFormat(new ISO8601DateFormat());
				setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
				setSerializationInclusion(JsonInclude.Include.NON_NULL);
				setVisibility(
					PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
				setVisibility(
					PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
			}
		};
	}

	@Test
	public void testEscapeRegexInStringFields() throws Exception {
		String regex = "^[0-9]+(\\.[0-9]{1,2})\"?";

		InsurancePlan insurancePlan = randomInsurancePlan();

		insurancePlan.setExternalReferenceCode(regex);
		insurancePlan.setPlanName(regex);
		insurancePlan.setProviderName(regex);

		String json = InsurancePlanSerDes.toJSON(insurancePlan);

		Assert.assertFalse(json.contains(regex));

		insurancePlan = InsurancePlanSerDes.toDTO(json);

		Assert.assertEquals(regex, insurancePlan.getExternalReferenceCode());
		Assert.assertEquals(regex, insurancePlan.getPlanName());
		Assert.assertEquals(regex, insurancePlan.getProviderName());
	}

	@Test
	public void testDeleteInsurancePlan() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		InsurancePlan insurancePlan =
			testDeleteInsurancePlan_addInsurancePlan();

		assertHttpResponseStatusCode(
			204,
			insurancePlanResource.deleteInsurancePlanHttpResponse(
				insurancePlan.getId()));

		assertHttpResponseStatusCode(
			404,
			insurancePlanResource.getInsurancePlanHttpResponse(
				insurancePlan.getId()));
		assertHttpResponseStatusCode(
			404, insurancePlanResource.getInsurancePlanHttpResponse(0L));
	}

	protected InsurancePlan testDeleteInsurancePlan_addInsurancePlan()
		throws Exception {

		return insurancePlanResource.postSiteInsurancePlan(
			testGroup.getGroupId(), randomInsurancePlan());
	}

	@Test
	public void testGraphQLDeleteInsurancePlan() throws Exception {

		// No namespace

		InsurancePlan insurancePlan1 =
			testGraphQLDeleteInsurancePlan_addInsurancePlan();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteInsurancePlan",
						new HashMap<String, Object>() {
							{
								put("insurancePlanId", insurancePlan1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteInsurancePlan"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"insurancePlan",
					new HashMap<String, Object>() {
						{
							put("insurancePlanId", insurancePlan1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace clarityInsuranceBenefitsTracker_v1_0

		InsurancePlan insurancePlan2 =
			testGraphQLDeleteInsurancePlan_addInsurancePlan();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"clarityInsuranceBenefitsTracker_v1_0",
						new GraphQLField(
							"deleteInsurancePlan",
							new HashMap<String, Object>() {
								{
									put(
										"insurancePlanId",
										insurancePlan2.getId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/clarityInsuranceBenefitsTracker_v1_0",
				"Object/deleteInsurancePlan"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"clarityInsuranceBenefitsTracker_v1_0",
					new GraphQLField(
						"insurancePlan",
						new HashMap<String, Object>() {
							{
								put("insurancePlanId", insurancePlan2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected InsurancePlan testGraphQLDeleteInsurancePlan_addInsurancePlan()
		throws Exception {

		return testGraphQLInsurancePlan_addInsurancePlan();
	}

	@Test
	public void testGetInsurancePlan() throws Exception {
		InsurancePlan postInsurancePlan =
			testGetInsurancePlan_addInsurancePlan();

		InsurancePlan getInsurancePlan = insurancePlanResource.getInsurancePlan(
			postInsurancePlan.getId());

		assertEquals(postInsurancePlan, getInsurancePlan);
		assertValid(getInsurancePlan);
	}

	protected InsurancePlan testGetInsurancePlan_addInsurancePlan()
		throws Exception {

		return insurancePlanResource.postSiteInsurancePlan(
			testGroup.getGroupId(), randomInsurancePlan());
	}

	@Test
	public void testGraphQLGetInsurancePlan() throws Exception {
		InsurancePlan insurancePlan =
			testGraphQLGetInsurancePlan_addInsurancePlan();

		// No namespace

		Assert.assertTrue(
			equals(
				insurancePlan,
				InsurancePlanSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"insurancePlan",
								new HashMap<String, Object>() {
									{
										put(
											"insurancePlanId",
											insurancePlan.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/insurancePlan"))));

		// Using the namespace clarityInsuranceBenefitsTracker_v1_0

		Assert.assertTrue(
			equals(
				insurancePlan,
				InsurancePlanSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"clarityInsuranceBenefitsTracker_v1_0",
								new GraphQLField(
									"insurancePlan",
									new HashMap<String, Object>() {
										{
											put(
												"insurancePlanId",
												insurancePlan.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/clarityInsuranceBenefitsTracker_v1_0",
						"Object/insurancePlan"))));
	}

	@Test
	public void testGraphQLGetInsurancePlanNotFound() throws Exception {
		Long irrelevantInsurancePlanId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"insurancePlan",
						new HashMap<String, Object>() {
							{
								put(
									"insurancePlanId",
									irrelevantInsurancePlanId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace clarityInsuranceBenefitsTracker_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"clarityInsuranceBenefitsTracker_v1_0",
						new GraphQLField(
							"insurancePlan",
							new HashMap<String, Object>() {
								{
									put(
										"insurancePlanId",
										irrelevantInsurancePlanId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected InsurancePlan testGraphQLGetInsurancePlan_addInsurancePlan()
		throws Exception {

		return testGraphQLInsurancePlan_addInsurancePlan();
	}

	@Test
	public void testGetInsurancePlanPlanEnrollmentsPage() throws Exception {
		Long insurancePlanId =
			testGetInsurancePlanPlanEnrollmentsPage_getInsurancePlanId();
		Long irrelevantInsurancePlanId =
			testGetInsurancePlanPlanEnrollmentsPage_getIrrelevantInsurancePlanId();

		Page<InsurancePlan> page =
			insurancePlanResource.getInsurancePlanPlanEnrollmentsPage(
				insurancePlanId, null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantInsurancePlanId != null) {
			InsurancePlan irrelevantInsurancePlan =
				testGetInsurancePlanPlanEnrollmentsPage_addInsurancePlan(
					irrelevantInsurancePlanId, randomIrrelevantInsurancePlan());

			page = insurancePlanResource.getInsurancePlanPlanEnrollmentsPage(
				irrelevantInsurancePlanId, null, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantInsurancePlan, (List<InsurancePlan>)page.getItems());
			assertValid(
				page,
				testGetInsurancePlanPlanEnrollmentsPage_getExpectedActions(
					irrelevantInsurancePlanId));
		}

		InsurancePlan insurancePlan1 =
			testGetInsurancePlanPlanEnrollmentsPage_addInsurancePlan(
				insurancePlanId, randomInsurancePlan());

		InsurancePlan insurancePlan2 =
			testGetInsurancePlanPlanEnrollmentsPage_addInsurancePlan(
				insurancePlanId, randomInsurancePlan());

		page = insurancePlanResource.getInsurancePlanPlanEnrollmentsPage(
			insurancePlanId, null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(insurancePlan1, (List<InsurancePlan>)page.getItems());
		assertContains(insurancePlan2, (List<InsurancePlan>)page.getItems());
		assertValid(
			page,
			testGetInsurancePlanPlanEnrollmentsPage_getExpectedActions(
				insurancePlanId));

		insurancePlanResource.deleteInsurancePlan(insurancePlan1.getId());

		insurancePlanResource.deleteInsurancePlan(insurancePlan2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetInsurancePlanPlanEnrollmentsPage_getExpectedActions(
				Long insurancePlanId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetInsurancePlanPlanEnrollmentsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long insurancePlanId =
			testGetInsurancePlanPlanEnrollmentsPage_getInsurancePlanId();

		InsurancePlan insurancePlan1 = randomInsurancePlan();

		insurancePlan1 =
			testGetInsurancePlanPlanEnrollmentsPage_addInsurancePlan(
				insurancePlanId, insurancePlan1);

		for (EntityField entityField : entityFields) {
			Page<InsurancePlan> page =
				insurancePlanResource.getInsurancePlanPlanEnrollmentsPage(
					insurancePlanId, null, null,
					getFilterString(entityField, "between", insurancePlan1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(insurancePlan1),
				(List<InsurancePlan>)page.getItems());
		}
	}

	@Test
	public void testGetInsurancePlanPlanEnrollmentsPageWithFilterDoubleEquals()
		throws Exception {

		testGetInsurancePlanPlanEnrollmentsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetInsurancePlanPlanEnrollmentsPageWithFilterStringContains()
		throws Exception {

		testGetInsurancePlanPlanEnrollmentsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetInsurancePlanPlanEnrollmentsPageWithFilterStringEquals()
		throws Exception {

		testGetInsurancePlanPlanEnrollmentsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetInsurancePlanPlanEnrollmentsPageWithFilterStringStartsWith()
		throws Exception {

		testGetInsurancePlanPlanEnrollmentsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetInsurancePlanPlanEnrollmentsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long insurancePlanId =
			testGetInsurancePlanPlanEnrollmentsPage_getInsurancePlanId();

		InsurancePlan insurancePlan1 =
			testGetInsurancePlanPlanEnrollmentsPage_addInsurancePlan(
				insurancePlanId, randomInsurancePlan());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		InsurancePlan insurancePlan2 =
			testGetInsurancePlanPlanEnrollmentsPage_addInsurancePlan(
				insurancePlanId, randomInsurancePlan());

		for (EntityField entityField : entityFields) {
			Page<InsurancePlan> page =
				insurancePlanResource.getInsurancePlanPlanEnrollmentsPage(
					insurancePlanId, null, null,
					getFilterString(entityField, operator, insurancePlan1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(insurancePlan1),
				(List<InsurancePlan>)page.getItems());
		}
	}

	@Test
	public void testGetInsurancePlanPlanEnrollmentsPageWithPagination()
		throws Exception {

		Long insurancePlanId =
			testGetInsurancePlanPlanEnrollmentsPage_getInsurancePlanId();

		Page<InsurancePlan> insurancePlansPage =
			insurancePlanResource.getInsurancePlanPlanEnrollmentsPage(
				insurancePlanId, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			insurancePlansPage.getTotalCount());

		InsurancePlan insurancePlan1 =
			testGetInsurancePlanPlanEnrollmentsPage_addInsurancePlan(
				insurancePlanId, randomInsurancePlan());

		InsurancePlan insurancePlan2 =
			testGetInsurancePlanPlanEnrollmentsPage_addInsurancePlan(
				insurancePlanId, randomInsurancePlan());

		InsurancePlan insurancePlan3 =
			testGetInsurancePlanPlanEnrollmentsPage_addInsurancePlan(
				insurancePlanId, randomInsurancePlan());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<InsurancePlan> page1 =
				insurancePlanResource.getInsurancePlanPlanEnrollmentsPage(
					insurancePlanId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				insurancePlan1, (List<InsurancePlan>)page1.getItems());

			Page<InsurancePlan> page2 =
				insurancePlanResource.getInsurancePlanPlanEnrollmentsPage(
					insurancePlanId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				insurancePlan2, (List<InsurancePlan>)page2.getItems());

			Page<InsurancePlan> page3 =
				insurancePlanResource.getInsurancePlanPlanEnrollmentsPage(
					insurancePlanId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				insurancePlan3, (List<InsurancePlan>)page3.getItems());
		}
		else {
			Page<InsurancePlan> page1 =
				insurancePlanResource.getInsurancePlanPlanEnrollmentsPage(
					insurancePlanId, null, null, null,
					Pagination.of(1, totalCount + 2), null);

			List<InsurancePlan> insurancePlans1 =
				(List<InsurancePlan>)page1.getItems();

			Assert.assertEquals(
				insurancePlans1.toString(), totalCount + 2,
				insurancePlans1.size());

			Page<InsurancePlan> page2 =
				insurancePlanResource.getInsurancePlanPlanEnrollmentsPage(
					insurancePlanId, null, null, null,
					Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<InsurancePlan> insurancePlans2 =
				(List<InsurancePlan>)page2.getItems();

			Assert.assertEquals(
				insurancePlans2.toString(), 1, insurancePlans2.size());

			Page<InsurancePlan> page3 =
				insurancePlanResource.getInsurancePlanPlanEnrollmentsPage(
					insurancePlanId, null, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				insurancePlan1, (List<InsurancePlan>)page3.getItems());
			assertContains(
				insurancePlan2, (List<InsurancePlan>)page3.getItems());
			assertContains(
				insurancePlan3, (List<InsurancePlan>)page3.getItems());
		}
	}

	@Test
	public void testGetInsurancePlanPlanEnrollmentsPageWithSortDateTime()
		throws Exception {

		testGetInsurancePlanPlanEnrollmentsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, insurancePlan1, insurancePlan2) -> {
				BeanTestUtil.setProperty(
					insurancePlan1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetInsurancePlanPlanEnrollmentsPageWithSortDouble()
		throws Exception {

		testGetInsurancePlanPlanEnrollmentsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, insurancePlan1, insurancePlan2) -> {
				BeanTestUtil.setProperty(
					insurancePlan1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					insurancePlan2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetInsurancePlanPlanEnrollmentsPageWithSortInteger()
		throws Exception {

		testGetInsurancePlanPlanEnrollmentsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, insurancePlan1, insurancePlan2) -> {
				BeanTestUtil.setProperty(
					insurancePlan1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					insurancePlan2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetInsurancePlanPlanEnrollmentsPageWithSortString()
		throws Exception {

		testGetInsurancePlanPlanEnrollmentsPageWithSort(
			EntityField.Type.STRING,
			(entityField, insurancePlan1, insurancePlan2) -> {
				Class<?> clazz = insurancePlan1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						insurancePlan1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						insurancePlan2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						insurancePlan1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						insurancePlan2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						insurancePlan1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						insurancePlan2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetInsurancePlanPlanEnrollmentsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, InsurancePlan, InsurancePlan, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long insurancePlanId =
			testGetInsurancePlanPlanEnrollmentsPage_getInsurancePlanId();

		InsurancePlan insurancePlan1 = randomInsurancePlan();
		InsurancePlan insurancePlan2 = randomInsurancePlan();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, insurancePlan1, insurancePlan2);
		}

		insurancePlan1 =
			testGetInsurancePlanPlanEnrollmentsPage_addInsurancePlan(
				insurancePlanId, insurancePlan1);

		insurancePlan2 =
			testGetInsurancePlanPlanEnrollmentsPage_addInsurancePlan(
				insurancePlanId, insurancePlan2);

		Page<InsurancePlan> page =
			insurancePlanResource.getInsurancePlanPlanEnrollmentsPage(
				insurancePlanId, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<InsurancePlan> ascPage =
				insurancePlanResource.getInsurancePlanPlanEnrollmentsPage(
					insurancePlanId, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				insurancePlan1, (List<InsurancePlan>)ascPage.getItems());
			assertContains(
				insurancePlan2, (List<InsurancePlan>)ascPage.getItems());

			Page<InsurancePlan> descPage =
				insurancePlanResource.getInsurancePlanPlanEnrollmentsPage(
					insurancePlanId, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				insurancePlan2, (List<InsurancePlan>)descPage.getItems());
			assertContains(
				insurancePlan1, (List<InsurancePlan>)descPage.getItems());
		}
	}

	protected InsurancePlan
			testGetInsurancePlanPlanEnrollmentsPage_addInsurancePlan(
				Long insurancePlanId, InsurancePlan insurancePlan)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetInsurancePlanPlanEnrollmentsPage_getInsurancePlanId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetInsurancePlanPlanEnrollmentsPage_getIrrelevantInsurancePlanId()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetInsurancePlanPlanEnrollmentsPage()
		throws Exception {

		Long insurancePlanId =
			testGetInsurancePlanPlanEnrollmentsPage_getInsurancePlanId();

		GraphQLField graphQLField = new GraphQLField(
			"insurancePlanPlanEnrollments",
			new HashMap<String, Object>() {
				{
					put("insurancePlanId", insurancePlanId);
					put("search", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject insurancePlanPlanEnrollmentsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/insurancePlanPlanEnrollments");

		long totalCount = insurancePlanPlanEnrollmentsJSONObject.getLong(
			"totalCount");

		InsurancePlan insurancePlan1 =
			testGraphQLInsurancePlan_addInsurancePlan(
				insurancePlanId, randomInsurancePlan());

		InsurancePlan insurancePlan2 =
			testGraphQLInsurancePlan_addInsurancePlan(
				insurancePlanId, randomInsurancePlan());

		insurancePlanPlanEnrollmentsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/insurancePlanPlanEnrollments");

		Assert.assertEquals(
			totalCount + 2,
			insurancePlanPlanEnrollmentsJSONObject.getLong("totalCount"));

		assertContains(
			insurancePlan1,
			Arrays.asList(
				InsurancePlanSerDes.toDTOs(
					insurancePlanPlanEnrollmentsJSONObject.getString(
						"items"))));
		assertContains(
			insurancePlan2,
			Arrays.asList(
				InsurancePlanSerDes.toDTOs(
					insurancePlanPlanEnrollmentsJSONObject.getString(
						"items"))));

		// Using the namespace clarityInsuranceBenefitsTracker_v1_0

		insurancePlanPlanEnrollmentsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField(
					"clarityInsuranceBenefitsTracker_v1_0", graphQLField)),
			"JSONObject/data",
			"JSONObject/clarityInsuranceBenefitsTracker_v1_0",
			"JSONObject/insurancePlanPlanEnrollments");

		Assert.assertEquals(
			totalCount + 2,
			insurancePlanPlanEnrollmentsJSONObject.getLong("totalCount"));

		assertContains(
			insurancePlan1,
			Arrays.asList(
				InsurancePlanSerDes.toDTOs(
					insurancePlanPlanEnrollmentsJSONObject.getString(
						"items"))));
		assertContains(
			insurancePlan2,
			Arrays.asList(
				InsurancePlanSerDes.toDTOs(
					insurancePlanPlanEnrollmentsJSONObject.getString(
						"items"))));
	}

	@Test
	public void testGetSiteInsurancePlanByExternalReferenceCode()
		throws Exception {

		InsurancePlan postInsurancePlan =
			testGetSiteInsurancePlanByExternalReferenceCode_addInsurancePlan();

		InsurancePlan getInsurancePlan =
			insurancePlanResource.getSiteInsurancePlanByExternalReferenceCode(
				postInsurancePlan.getSiteId(),
				postInsurancePlan.getExternalReferenceCode());

		assertEquals(postInsurancePlan, getInsurancePlan);
		assertValid(getInsurancePlan);
	}

	protected InsurancePlan
			testGetSiteInsurancePlanByExternalReferenceCode_addInsurancePlan()
		throws Exception {

		return insurancePlanResource.postSiteInsurancePlan(
			testGroup.getGroupId(), randomInsurancePlan());
	}

	@Test
	public void testGraphQLGetSiteInsurancePlanByExternalReferenceCode()
		throws Exception {

		InsurancePlan insurancePlan =
			testGraphQLGetSiteInsurancePlanByExternalReferenceCode_addInsurancePlan();

		// No namespace

		Assert.assertTrue(
			equals(
				insurancePlan,
				InsurancePlanSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"insurancePlanByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"siteKey",
											"\"" + insurancePlan.getSiteId() +
												"\"");
										put(
											"externalReferenceCode",
											"\"" +
												insurancePlan.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/insurancePlanByExternalReferenceCode"))));

		// Using the namespace clarityInsuranceBenefitsTracker_v1_0

		Assert.assertTrue(
			equals(
				insurancePlan,
				InsurancePlanSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"clarityInsuranceBenefitsTracker_v1_0",
								new GraphQLField(
									"insurancePlanByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"siteKey",
												"\"" +
													insurancePlan.getSiteId() +
														"\"");
											put(
												"externalReferenceCode",
												"\"" +
													insurancePlan.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/clarityInsuranceBenefitsTracker_v1_0",
						"Object/insurancePlanByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetSiteInsurancePlanByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"insurancePlanByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + irrelevantGroup.getGroupId() + "\"");
								put(
									"externalReferenceCode",
									irrelevantExternalReferenceCode);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace clarityInsuranceBenefitsTracker_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"clarityInsuranceBenefitsTracker_v1_0",
						new GraphQLField(
							"insurancePlanByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" + irrelevantGroup.getGroupId() +
											"\"");
									put(
										"externalReferenceCode",
										irrelevantExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected InsurancePlan
			testGraphQLGetSiteInsurancePlanByExternalReferenceCode_addInsurancePlan()
		throws Exception {

		return testGraphQLSiteInsurancePlan_addInsurancePlan();
	}

	@Test
	public void testGetSiteInsurancePlansPage() throws Exception {
		Long siteId = testGetSiteInsurancePlansPage_getSiteId();
		Long irrelevantSiteId =
			testGetSiteInsurancePlansPage_getIrrelevantSiteId();

		Page<InsurancePlan> page =
			insurancePlanResource.getSiteInsurancePlansPage(
				siteId, null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantSiteId != null) {
			InsurancePlan irrelevantInsurancePlan =
				testGetSiteInsurancePlansPage_addInsurancePlan(
					irrelevantSiteId, randomIrrelevantInsurancePlan());

			page = insurancePlanResource.getSiteInsurancePlansPage(
				irrelevantSiteId, null, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantInsurancePlan, (List<InsurancePlan>)page.getItems());
			assertValid(
				page,
				testGetSiteInsurancePlansPage_getExpectedActions(
					irrelevantSiteId));
		}

		InsurancePlan insurancePlan1 =
			testGetSiteInsurancePlansPage_addInsurancePlan(
				siteId, randomInsurancePlan());

		InsurancePlan insurancePlan2 =
			testGetSiteInsurancePlansPage_addInsurancePlan(
				siteId, randomInsurancePlan());

		page = insurancePlanResource.getSiteInsurancePlansPage(
			siteId, null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(insurancePlan1, (List<InsurancePlan>)page.getItems());
		assertContains(insurancePlan2, (List<InsurancePlan>)page.getItems());
		assertValid(
			page, testGetSiteInsurancePlansPage_getExpectedActions(siteId));

		insurancePlanResource.deleteInsurancePlan(insurancePlan1.getId());

		insurancePlanResource.deleteInsurancePlan(insurancePlan2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetSiteInsurancePlansPage_getExpectedActions(Long siteId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/clarity-insurance-benefits-tracker-rest/v1.0/sites/{siteId}/insurance-plans/batch".
				replace("{siteId}", String.valueOf(siteId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetSiteInsurancePlansPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteInsurancePlansPage_getSiteId();

		InsurancePlan insurancePlan1 = randomInsurancePlan();

		insurancePlan1 = testGetSiteInsurancePlansPage_addInsurancePlan(
			siteId, insurancePlan1);

		for (EntityField entityField : entityFields) {
			Page<InsurancePlan> page =
				insurancePlanResource.getSiteInsurancePlansPage(
					siteId, null, null,
					getFilterString(entityField, "between", insurancePlan1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(insurancePlan1),
				(List<InsurancePlan>)page.getItems());
		}
	}

	@Test
	public void testGetSiteInsurancePlansPageWithFilterDoubleEquals()
		throws Exception {

		testGetSiteInsurancePlansPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetSiteInsurancePlansPageWithFilterStringContains()
		throws Exception {

		testGetSiteInsurancePlansPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteInsurancePlansPageWithFilterStringEquals()
		throws Exception {

		testGetSiteInsurancePlansPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteInsurancePlansPageWithFilterStringStartsWith()
		throws Exception {

		testGetSiteInsurancePlansPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetSiteInsurancePlansPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteInsurancePlansPage_getSiteId();

		InsurancePlan insurancePlan1 =
			testGetSiteInsurancePlansPage_addInsurancePlan(
				siteId, randomInsurancePlan());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		InsurancePlan insurancePlan2 =
			testGetSiteInsurancePlansPage_addInsurancePlan(
				siteId, randomInsurancePlan());

		for (EntityField entityField : entityFields) {
			Page<InsurancePlan> page =
				insurancePlanResource.getSiteInsurancePlansPage(
					siteId, null, null,
					getFilterString(entityField, operator, insurancePlan1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(insurancePlan1),
				(List<InsurancePlan>)page.getItems());
		}
	}

	@Test
	public void testGetSiteInsurancePlansPageWithPagination() throws Exception {
		Long siteId = testGetSiteInsurancePlansPage_getSiteId();

		Page<InsurancePlan> insurancePlansPage =
			insurancePlanResource.getSiteInsurancePlansPage(
				siteId, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			insurancePlansPage.getTotalCount());

		InsurancePlan insurancePlan1 =
			testGetSiteInsurancePlansPage_addInsurancePlan(
				siteId, randomInsurancePlan());

		InsurancePlan insurancePlan2 =
			testGetSiteInsurancePlansPage_addInsurancePlan(
				siteId, randomInsurancePlan());

		InsurancePlan insurancePlan3 =
			testGetSiteInsurancePlansPage_addInsurancePlan(
				siteId, randomInsurancePlan());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<InsurancePlan> page1 =
				insurancePlanResource.getSiteInsurancePlansPage(
					siteId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				insurancePlan1, (List<InsurancePlan>)page1.getItems());

			Page<InsurancePlan> page2 =
				insurancePlanResource.getSiteInsurancePlansPage(
					siteId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				insurancePlan2, (List<InsurancePlan>)page2.getItems());

			Page<InsurancePlan> page3 =
				insurancePlanResource.getSiteInsurancePlansPage(
					siteId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				insurancePlan3, (List<InsurancePlan>)page3.getItems());
		}
		else {
			Page<InsurancePlan> page1 =
				insurancePlanResource.getSiteInsurancePlansPage(
					siteId, null, null, null, Pagination.of(1, totalCount + 2),
					null);

			List<InsurancePlan> insurancePlans1 =
				(List<InsurancePlan>)page1.getItems();

			Assert.assertEquals(
				insurancePlans1.toString(), totalCount + 2,
				insurancePlans1.size());

			Page<InsurancePlan> page2 =
				insurancePlanResource.getSiteInsurancePlansPage(
					siteId, null, null, null, Pagination.of(2, totalCount + 2),
					null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<InsurancePlan> insurancePlans2 =
				(List<InsurancePlan>)page2.getItems();

			Assert.assertEquals(
				insurancePlans2.toString(), 1, insurancePlans2.size());

			Page<InsurancePlan> page3 =
				insurancePlanResource.getSiteInsurancePlansPage(
					siteId, null, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				insurancePlan1, (List<InsurancePlan>)page3.getItems());
			assertContains(
				insurancePlan2, (List<InsurancePlan>)page3.getItems());
			assertContains(
				insurancePlan3, (List<InsurancePlan>)page3.getItems());
		}
	}

	@Test
	public void testGetSiteInsurancePlansPageWithSortDateTime()
		throws Exception {

		testGetSiteInsurancePlansPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, insurancePlan1, insurancePlan2) -> {
				BeanTestUtil.setProperty(
					insurancePlan1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetSiteInsurancePlansPageWithSortDouble() throws Exception {
		testGetSiteInsurancePlansPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, insurancePlan1, insurancePlan2) -> {
				BeanTestUtil.setProperty(
					insurancePlan1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					insurancePlan2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetSiteInsurancePlansPageWithSortInteger()
		throws Exception {

		testGetSiteInsurancePlansPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, insurancePlan1, insurancePlan2) -> {
				BeanTestUtil.setProperty(
					insurancePlan1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					insurancePlan2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetSiteInsurancePlansPageWithSortString() throws Exception {
		testGetSiteInsurancePlansPageWithSort(
			EntityField.Type.STRING,
			(entityField, insurancePlan1, insurancePlan2) -> {
				Class<?> clazz = insurancePlan1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						insurancePlan1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						insurancePlan2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						insurancePlan1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						insurancePlan2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						insurancePlan1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						insurancePlan2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetSiteInsurancePlansPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, InsurancePlan, InsurancePlan, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteInsurancePlansPage_getSiteId();

		InsurancePlan insurancePlan1 = randomInsurancePlan();
		InsurancePlan insurancePlan2 = randomInsurancePlan();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, insurancePlan1, insurancePlan2);
		}

		insurancePlan1 = testGetSiteInsurancePlansPage_addInsurancePlan(
			siteId, insurancePlan1);

		insurancePlan2 = testGetSiteInsurancePlansPage_addInsurancePlan(
			siteId, insurancePlan2);

		Page<InsurancePlan> page =
			insurancePlanResource.getSiteInsurancePlansPage(
				siteId, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<InsurancePlan> ascPage =
				insurancePlanResource.getSiteInsurancePlansPage(
					siteId, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				insurancePlan1, (List<InsurancePlan>)ascPage.getItems());
			assertContains(
				insurancePlan2, (List<InsurancePlan>)ascPage.getItems());

			Page<InsurancePlan> descPage =
				insurancePlanResource.getSiteInsurancePlansPage(
					siteId, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				insurancePlan2, (List<InsurancePlan>)descPage.getItems());
			assertContains(
				insurancePlan1, (List<InsurancePlan>)descPage.getItems());
		}
	}

	protected InsurancePlan testGetSiteInsurancePlansPage_addInsurancePlan(
			Long siteId, InsurancePlan insurancePlan)
		throws Exception {

		return insurancePlanResource.postSiteInsurancePlan(
			siteId, insurancePlan);
	}

	protected Long testGetSiteInsurancePlansPage_getSiteId() throws Exception {
		return testGroup.getGroupId();
	}

	protected Long testGetSiteInsurancePlansPage_getIrrelevantSiteId()
		throws Exception {

		return irrelevantGroup.getGroupId();
	}

	@Test
	public void testGraphQLGetSiteInsurancePlansPage() throws Exception {
		Long siteId = testGetSiteInsurancePlansPage_getSiteId();

		GraphQLField graphQLField = new GraphQLField(
			"insurancePlans",
			new HashMap<String, Object>() {
				{
					put("siteKey", "\"" + siteId + "\"");
					put("search", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject insurancePlansJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/insurancePlans");

		long totalCount = insurancePlansJSONObject.getLong("totalCount");

		InsurancePlan insurancePlan1 =
			testGraphQLSiteInsurancePlan_addInsurancePlan(
				siteId, randomInsurancePlan());

		InsurancePlan insurancePlan2 =
			testGraphQLSiteInsurancePlan_addInsurancePlan(
				siteId, randomInsurancePlan());

		insurancePlansJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/insurancePlans");

		Assert.assertEquals(
			totalCount + 2, insurancePlansJSONObject.getLong("totalCount"));

		assertContains(
			insurancePlan1,
			Arrays.asList(
				InsurancePlanSerDes.toDTOs(
					insurancePlansJSONObject.getString("items"))));
		assertContains(
			insurancePlan2,
			Arrays.asList(
				InsurancePlanSerDes.toDTOs(
					insurancePlansJSONObject.getString("items"))));

		// Using the namespace clarityInsuranceBenefitsTracker_v1_0

		insurancePlansJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField(
					"clarityInsuranceBenefitsTracker_v1_0", graphQLField)),
			"JSONObject/data",
			"JSONObject/clarityInsuranceBenefitsTracker_v1_0",
			"JSONObject/insurancePlans");

		Assert.assertEquals(
			totalCount + 2, insurancePlansJSONObject.getLong("totalCount"));

		assertContains(
			insurancePlan1,
			Arrays.asList(
				InsurancePlanSerDes.toDTOs(
					insurancePlansJSONObject.getString("items"))));
		assertContains(
			insurancePlan2,
			Arrays.asList(
				InsurancePlanSerDes.toDTOs(
					insurancePlansJSONObject.getString("items"))));
	}

	@Test
	public void testPatchInsurancePlan() throws Exception {
		InsurancePlan postInsurancePlan =
			testPatchInsurancePlan_addInsurancePlan();

		InsurancePlan randomPatchInsurancePlan = randomPatchInsurancePlan();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		InsurancePlan patchInsurancePlan =
			insurancePlanResource.patchInsurancePlan(
				postInsurancePlan.getId(), randomPatchInsurancePlan);

		InsurancePlan expectedPatchInsurancePlan = postInsurancePlan.clone();

		BeanTestUtil.copyProperties(
			randomPatchInsurancePlan, expectedPatchInsurancePlan);

		InsurancePlan getInsurancePlan = insurancePlanResource.getInsurancePlan(
			patchInsurancePlan.getId());

		assertEquals(expectedPatchInsurancePlan, getInsurancePlan);
		assertValid(getInsurancePlan);
	}

	protected InsurancePlan testPatchInsurancePlan_addInsurancePlan()
		throws Exception {

		return insurancePlanResource.postSiteInsurancePlan(
			testGroup.getGroupId(), randomInsurancePlan());
	}

	@Test
	public void testPostSiteInsurancePlan() throws Exception {
		InsurancePlan randomInsurancePlan = randomInsurancePlan();

		InsurancePlan postInsurancePlan =
			testPostSiteInsurancePlan_addInsurancePlan(randomInsurancePlan);

		assertEquals(randomInsurancePlan, postInsurancePlan);
		assertValid(postInsurancePlan);
	}

	protected InsurancePlan testPostSiteInsurancePlan_addInsurancePlan(
			InsurancePlan insurancePlan)
		throws Exception {

		return insurancePlanResource.postSiteInsurancePlan(
			testGetSiteInsurancePlansPage_getSiteId(), insurancePlan);
	}

	@Test
	public void testGraphQLPostSiteInsurancePlan() throws Exception {
		InsurancePlan randomInsurancePlan = randomInsurancePlan();

		InsurancePlan insurancePlan =
			testGraphQLSiteInsurancePlan_addInsurancePlan(
				testGroup.getGroupId(), randomInsurancePlan);

		Assert.assertTrue(equals(randomInsurancePlan, insurancePlan));
	}

	@Test
	public void testPutInsurancePlan() throws Exception {
		InsurancePlan postInsurancePlan =
			testPutInsurancePlan_addInsurancePlan();

		InsurancePlan randomInsurancePlan = randomInsurancePlan();

		InsurancePlan putInsurancePlan = insurancePlanResource.putInsurancePlan(
			postInsurancePlan.getId(), randomInsurancePlan);

		assertEquals(randomInsurancePlan, putInsurancePlan);
		assertValid(putInsurancePlan);

		InsurancePlan getInsurancePlan = insurancePlanResource.getInsurancePlan(
			putInsurancePlan.getId());

		assertEquals(randomInsurancePlan, getInsurancePlan);
		assertValid(getInsurancePlan);
	}

	protected InsurancePlan testPutInsurancePlan_addInsurancePlan()
		throws Exception {

		return insurancePlanResource.postSiteInsurancePlan(
			testGroup.getGroupId(), randomInsurancePlan());
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	@Test
	public void testPostInsurancePlan() throws Exception {
		Assert.assertTrue(true);
	}

	@Test
	public void testPostInsurancePlanPlanEnrollment() throws Exception {
		Assert.assertTrue(true);
	}

	protected InsurancePlan testGraphQLInsurancePlan_addInsurancePlan()
		throws Exception {

		return testGraphQLInsurancePlan_addInsurancePlan(
			testGraphQLInsurancePlan_getInsurancePlanId(),
			randomInsurancePlan());
	}

	protected Long testGraphQLInsurancePlan_getInsurancePlanId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected InsurancePlan testGraphQLInsurancePlan_addInsurancePlan(
			Long insurancePlanId, InsurancePlan insurancePlan)
		throws Exception {

		JSONDeserializer<InsurancePlan> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(InsurancePlan.class)) {

			if (getGraphQLValue(field.get(insurancePlan)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(insurancePlan)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createInsurancePlan",
						new HashMap<String, Object>() {
							{
								put("insurancePlanId", insurancePlanId);
								put("insurancePlan", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createInsurancePlan"),
			InsurancePlan.class);
	}

	protected InsurancePlan testGraphQLSiteInsurancePlan_addInsurancePlan()
		throws Exception {

		return testGraphQLSiteInsurancePlan_addInsurancePlan(
			testGroup.getGroupId(), randomInsurancePlan());
	}

	protected InsurancePlan testGraphQLSiteInsurancePlan_addInsurancePlan(
			Long siteId, InsurancePlan insurancePlan)
		throws Exception {

		JSONDeserializer<InsurancePlan> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(InsurancePlan.class)) {

			if (getGraphQLValue(field.get(insurancePlan)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(insurancePlan)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createSiteInsurancePlan",
						new HashMap<String, Object>() {
							{
								put("siteKey", "\"" + siteId + "\"");
								put("insurancePlan", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createSiteInsurancePlan"),
			InsurancePlan.class);
	}

	protected String getGraphQLValue(Object value) throws Exception {
		if (value == null) {
			return null;
		}
		else if (value instanceof Boolean || value instanceof Number) {
			return value.toString();
		}
		else if (value instanceof Date date) {
			return "\"" +
				DateUtil.getDate(
					date, "yyyy-MM-dd'T'HH:mm:ss'Z'", LocaleUtil.getDefault(),
					TimeZone.getTimeZone("UTC")) + "\"";
		}
		else if (value instanceof Enum<?> enm) {
			return enm.name();
		}
		else if (value instanceof Map<?, ?> map) {
			List<String> entries = new ArrayList<>();

			for (Map.Entry<?, ?> entry : map.entrySet()) {
				String graphQLValue = getGraphQLValue(entry.getValue());

				if (graphQLValue != null) {
					entries.add(entry.getKey() + ": " + graphQLValue);
				}
			}

			return "{" + String.join(", ", entries) + "}";
		}
		else if (value instanceof Object[] array) {
			List<String> entries = new ArrayList<>();

			for (Object entry : array) {
				String graphQLValue = getGraphQLValue(entry);

				if (graphQLValue != null) {
					entries.add(graphQLValue);
				}
			}

			return "[" + String.join(", ", entries) + "]";
		}
		else if (value instanceof String) {
			return "\"" + value + "\"";
		}
		else {
			List<String> entries = new ArrayList<>();

			Class<?> clazz = value.getClass();
			java.lang.reflect.Field[] declaredFields = getDeclaredFields(clazz);

			if (declaredFields.length == 0) {
				declaredFields = getDeclaredFields(clazz.getSuperclass());
			}

			for (java.lang.reflect.Field field : declaredFields) {
				String graphQLValue = getGraphQLValue(field.get(value));

				if (graphQLValue != null) {
					entries.add(field.getName() + ": " + graphQLValue);
				}
			}

			return "{" + String.join(", ", entries) + "}";
		}
	}

	protected void assertContains(
		InsurancePlan insurancePlan, List<InsurancePlan> insurancePlans) {

		boolean contains = false;

		for (InsurancePlan item : insurancePlans) {
			if (equals(insurancePlan, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			insurancePlans + " does not contain " + insurancePlan, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		InsurancePlan insurancePlan1, InsurancePlan insurancePlan2) {

		Assert.assertTrue(
			insurancePlan1 + " does not equal " + insurancePlan2,
			equals(insurancePlan1, insurancePlan2));
	}

	protected void assertEquals(
		List<InsurancePlan> insurancePlans1,
		List<InsurancePlan> insurancePlans2) {

		Assert.assertEquals(insurancePlans1.size(), insurancePlans2.size());

		for (int i = 0; i < insurancePlans1.size(); i++) {
			InsurancePlan insurancePlan1 = insurancePlans1.get(i);
			InsurancePlan insurancePlan2 = insurancePlans2.get(i);

			assertEquals(insurancePlan1, insurancePlan2);
		}
	}

	protected void assertEquals(
		PlanEnrollment planEnrollment1, PlanEnrollment planEnrollment2) {

		Assert.assertTrue(
			planEnrollment1 + " does not equal " + planEnrollment2,
			equals(planEnrollment1, planEnrollment2));
	}

	protected void assertEqualsIgnoringOrder(
		List<InsurancePlan> insurancePlans1,
		List<InsurancePlan> insurancePlans2) {

		Assert.assertEquals(insurancePlans1.size(), insurancePlans2.size());

		for (InsurancePlan insurancePlan1 : insurancePlans1) {
			boolean contains = false;

			for (InsurancePlan insurancePlan2 : insurancePlans2) {
				if (equals(insurancePlan1, insurancePlan2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				insurancePlans2 + " does not contain " + insurancePlan1,
				contains);
		}
	}

	protected void assertValid(InsurancePlan insurancePlan) throws Exception {
		boolean valid = true;

		if (insurancePlan.getDateCreated() == null) {
			valid = false;
		}

		if (insurancePlan.getDateModified() == null) {
			valid = false;
		}

		if (insurancePlan.getId() == null) {
			valid = false;
		}

		if (!Objects.equals(
				insurancePlan.getSiteId(), testGroup.getGroupId())) {

			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (insurancePlan.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (insurancePlan.getActive() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"annualContactsAllowanceCents",
					additionalAssertFieldName)) {

				if (insurancePlan.getAnnualContactsAllowanceCents() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"annualExamAllowanceCents", additionalAssertFieldName)) {

				if (insurancePlan.getAnnualExamAllowanceCents() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"annualFramesAllowanceCents", additionalAssertFieldName)) {

				if (insurancePlan.getAnnualFramesAllowanceCents() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"annualLensesAllowanceCents", additionalAssertFieldName)) {

				if (insurancePlan.getAnnualLensesAllowanceCents() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"coveragePeriodMonths", additionalAssertFieldName)) {

				if (insurancePlan.getCoveragePeriodMonths() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (insurancePlan.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (insurancePlan.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("planName", additionalAssertFieldName)) {
				if (insurancePlan.getPlanName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("providerName", additionalAssertFieldName)) {
				if (insurancePlan.getProviderName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (insurancePlan.getStatus() == null) {
					valid = false;
				}

				continue;
			}

			throw new IllegalArgumentException(
				"Invalid additional assert field name " +
					additionalAssertFieldName);
		}

		Assert.assertTrue(valid);
	}

	protected void assertValid(Page<InsurancePlan> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<InsurancePlan> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<InsurancePlan> insurancePlans = page.getItems();

		int size = insurancePlans.size();

		if ((page.getLastPage() > 0) && (page.getPage() > 0) &&
			(page.getPageSize() > 0) && (page.getTotalCount() > 0) &&
			(size > 0)) {

			valid = true;
		}

		Assert.assertTrue(valid);

		assertValid(page.getActions(), expectedActions);
	}

	protected void assertValid(
		Map<String, Map<String, String>> actions1,
		Map<String, Map<String, String>> actions2) {

		for (String key : actions2.keySet()) {
			Map action = actions1.get(key);

			Assert.assertNotNull(key + " does not contain an action", action);

			Map<String, String> expectedAction = actions2.get(key);

			Assert.assertEquals(
				expectedAction.get("method"), action.get("method"));
			Assert.assertEquals(expectedAction.get("href"), action.get("href"));
		}
	}

	protected void assertValid(PlanEnrollment planEnrollment) {
		boolean valid = true;

		if (planEnrollment.getDateCreated() == null) {
			valid = false;
		}

		if (planEnrollment.getDateModified() == null) {
			valid = false;
		}

		if (planEnrollment.getExternalReferenceCode() == null) {
			valid = false;
		}

		if (planEnrollment.getId() == null) {
			valid = false;
		}

		if (!Objects.equals(
				planEnrollment.getSiteId(), testGroup.getGroupId())) {

			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalPlanEnrollmentAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (planEnrollment.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (planEnrollment.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("endDate", additionalAssertFieldName)) {
				if (planEnrollment.getEndDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("enrollmentStatus", additionalAssertFieldName)) {
				if (planEnrollment.getEnrollmentStatus() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (planEnrollment.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("groupNumber", additionalAssertFieldName)) {
				if (planEnrollment.getGroupNumber() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("insurancePlanERC", additionalAssertFieldName)) {
				if (planEnrollment.getInsurancePlanERC() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("insurancePlanId", additionalAssertFieldName)) {
				if (planEnrollment.getInsurancePlanId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("member", additionalAssertFieldName)) {
				if (planEnrollment.getMember() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("memberId", additionalAssertFieldName)) {
				if (planEnrollment.getMemberId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("notes", additionalAssertFieldName)) {
				if (planEnrollment.getNotes() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("startDate", additionalAssertFieldName)) {
				if (planEnrollment.getStartDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (planEnrollment.getStatus() == null) {
					valid = false;
				}

				continue;
			}

			throw new IllegalArgumentException(
				"Invalid additional assert field name " +
					additionalAssertFieldName);
		}

		Assert.assertTrue(valid);
	}

	protected String[] getAdditionalAssertFieldNames() {
		return new String[0];
	}

	protected String[] getAdditionalPlanEnrollmentAssertFieldNames() {
		return new String[0];
	}

	protected List<GraphQLField> getGraphQLFields() throws Exception {
		List<GraphQLField> graphQLFields = new ArrayList<>();

		graphQLFields.add(new GraphQLField("externalReferenceCode"));

		graphQLFields.add(new GraphQLField("id"));

		graphQLFields.add(new GraphQLField("siteId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.clarityvisionsolutions.insurance.benefits.tracker.rest.
						dto.v1_0.InsurancePlan.class)) {

			if (!ArrayUtil.contains(
					getAdditionalAssertFieldNames(), field.getName())) {

				continue;
			}

			graphQLFields.addAll(getGraphQLFields(field));
		}

		return graphQLFields;
	}

	protected List<GraphQLField> getGraphQLFields(
			java.lang.reflect.Field... fields)
		throws Exception {

		List<GraphQLField> graphQLFields = new ArrayList<>();

		for (java.lang.reflect.Field field : fields) {
			com.liferay.portal.vulcan.graphql.annotation.GraphQLField
				vulcanGraphQLField = field.getAnnotation(
					com.liferay.portal.vulcan.graphql.annotation.GraphQLField.
						class);

			if (vulcanGraphQLField != null) {
				Class<?> clazz = field.getType();

				if (clazz.isArray()) {
					clazz = clazz.getComponentType();
				}

				List<GraphQLField> childrenGraphQLFields = getGraphQLFields(
					getDeclaredFields(clazz));

				graphQLFields.add(
					new GraphQLField(field.getName(), childrenGraphQLFields));
			}
		}

		return graphQLFields;
	}

	protected String[] getIgnoredEntityFieldNames() {
		return new String[0];
	}

	protected boolean equals(
		InsurancePlan insurancePlan1, InsurancePlan insurancePlan2) {

		if (insurancePlan1 == insurancePlan2) {
			return true;
		}

		if (!Objects.equals(
				insurancePlan1.getSiteId(), insurancePlan2.getSiteId())) {

			return false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)insurancePlan1.getActions(),
						(Map)insurancePlan2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						insurancePlan1.getActive(),
						insurancePlan2.getActive())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"annualContactsAllowanceCents",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						insurancePlan1.getAnnualContactsAllowanceCents(),
						insurancePlan2.getAnnualContactsAllowanceCents())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"annualExamAllowanceCents", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						insurancePlan1.getAnnualExamAllowanceCents(),
						insurancePlan2.getAnnualExamAllowanceCents())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"annualFramesAllowanceCents", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						insurancePlan1.getAnnualFramesAllowanceCents(),
						insurancePlan2.getAnnualFramesAllowanceCents())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"annualLensesAllowanceCents", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						insurancePlan1.getAnnualLensesAllowanceCents(),
						insurancePlan2.getAnnualLensesAllowanceCents())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"coveragePeriodMonths", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						insurancePlan1.getCoveragePeriodMonths(),
						insurancePlan2.getCoveragePeriodMonths())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						insurancePlan1.getCreator(),
						insurancePlan2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						insurancePlan1.getDateCreated(),
						insurancePlan2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						insurancePlan1.getDateModified(),
						insurancePlan2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						insurancePlan1.getExternalReferenceCode(),
						insurancePlan2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						insurancePlan1.getId(), insurancePlan2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("planName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						insurancePlan1.getPlanName(),
						insurancePlan2.getPlanName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("providerName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						insurancePlan1.getProviderName(),
						insurancePlan2.getProviderName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						insurancePlan1.getStatus(),
						insurancePlan2.getStatus())) {

					return false;
				}

				continue;
			}

			throw new IllegalArgumentException(
				"Invalid additional assert field name " +
					additionalAssertFieldName);
		}

		return true;
	}

	protected boolean equals(
		Map<String, Object> map1, Map<String, Object> map2) {

		if (Objects.equals(map1.keySet(), map2.keySet())) {
			for (Map.Entry<String, Object> entry : map1.entrySet()) {
				if (entry.getValue() instanceof Map) {
					if (!equals(
							(Map)entry.getValue(),
							(Map)map2.get(entry.getKey()))) {

						return false;
					}
				}
				else if (!Objects.deepEquals(
							entry.getValue(), map2.get(entry.getKey()))) {

					return false;
				}
			}

			return true;
		}

		return false;
	}

	protected boolean equals(
		PlanEnrollment planEnrollment1, PlanEnrollment planEnrollment2) {

		if (planEnrollment1 == planEnrollment2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalPlanEnrollmentAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						planEnrollment1.getActions(),
						planEnrollment2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						planEnrollment1.getCreator(),
						planEnrollment2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						planEnrollment1.getDateCreated(),
						planEnrollment2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						planEnrollment1.getDateModified(),
						planEnrollment2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("endDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						planEnrollment1.getEndDate(),
						planEnrollment2.getEndDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("enrollmentStatus", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						planEnrollment1.getEnrollmentStatus(),
						planEnrollment2.getEnrollmentStatus())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						planEnrollment1.getExternalReferenceCode(),
						planEnrollment2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("groupNumber", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						planEnrollment1.getGroupNumber(),
						planEnrollment2.getGroupNumber())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						planEnrollment1.getId(), planEnrollment2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("insurancePlanERC", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						planEnrollment1.getInsurancePlanERC(),
						planEnrollment2.getInsurancePlanERC())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("insurancePlanId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						planEnrollment1.getInsurancePlanId(),
						planEnrollment2.getInsurancePlanId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("member", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						planEnrollment1.getMember(),
						planEnrollment2.getMember())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("memberId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						planEnrollment1.getMemberId(),
						planEnrollment2.getMemberId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("notes", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						planEnrollment1.getNotes(),
						planEnrollment2.getNotes())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("siteId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						planEnrollment1.getSiteId(),
						planEnrollment2.getSiteId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("startDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						planEnrollment1.getStartDate(),
						planEnrollment2.getStartDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						planEnrollment1.getStatus(),
						planEnrollment2.getStatus())) {

					return false;
				}

				continue;
			}

			throw new IllegalArgumentException(
				"Invalid additional assert field name " +
					additionalAssertFieldName);
		}

		return true;
	}

	protected java.lang.reflect.Field[] getDeclaredFields(Class clazz)
		throws Exception {

		if (clazz.getClassLoader() == null) {
			return new java.lang.reflect.Field[0];
		}

		return TransformUtil.transform(
			ReflectionUtil.getDeclaredFields(clazz),
			field -> {
				if (field.isSynthetic()) {
					return null;
				}

				return field;
			},
			java.lang.reflect.Field.class);
	}

	protected java.util.Collection<EntityField> getEntityFields()
		throws Exception {

		if (!(_insurancePlanResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_insurancePlanResource;

		EntityModel entityModel = entityModelResource.getEntityModel(
			new MultivaluedHashMap());

		if (entityModel == null) {
			return Collections.emptyList();
		}

		Map<String, EntityField> entityFieldsMap =
			entityModel.getEntityFieldsMap();

		return entityFieldsMap.values();
	}

	protected List<EntityField> getEntityFields(EntityField.Type type)
		throws Exception {

		return TransformUtil.transform(
			getEntityFields(),
			entityField -> {
				if (!Objects.equals(entityField.getType(), type) ||
					ArrayUtil.contains(
						getIgnoredEntityFieldNames(), entityField.getName())) {

					return null;
				}

				return entityField;
			});
	}

	protected String getFilterString(
		EntityField entityField, String operator, InsurancePlan insurancePlan) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("actions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("active")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("annualContactsAllowanceCents")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("annualExamAllowanceCents")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("annualFramesAllowanceCents")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("annualLensesAllowanceCents")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("coveragePeriodMonths")) {
			sb.append(String.valueOf(insurancePlan.getCoveragePeriodMonths()));

			return sb.toString();
		}

		if (entityFieldName.equals("creator")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = insurancePlan.getDateCreated();

				sb = new StringBundler();

				sb.append("(");
				sb.append(entityFieldName);
				sb.append(" gt ");
				sb.append(_format.format(date.getTime() - (2 * Time.SECOND)));
				sb.append(" and ");
				sb.append(entityFieldName);
				sb.append(" lt ");
				sb.append(_format.format(date.getTime() + (2 * Time.SECOND)));
				sb.append(")");
			}
			else {
				sb.append(entityFieldName);

				sb.append(" ");
				sb.append(operator);
				sb.append(" ");

				sb.append(_format.format(insurancePlan.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = insurancePlan.getDateModified();

				sb = new StringBundler();

				sb.append("(");
				sb.append(entityFieldName);
				sb.append(" gt ");
				sb.append(_format.format(date.getTime() - (2 * Time.SECOND)));
				sb.append(" and ");
				sb.append(entityFieldName);
				sb.append(" lt ");
				sb.append(_format.format(date.getTime() + (2 * Time.SECOND)));
				sb.append(")");
			}
			else {
				sb.append(entityFieldName);

				sb.append(" ");
				sb.append(operator);
				sb.append(" ");

				sb.append(_format.format(insurancePlan.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = insurancePlan.getExternalReferenceCode();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("id")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("planName")) {
			Object object = insurancePlan.getPlanName();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("providerName")) {
			Object object = insurancePlan.getProviderName();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("siteId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("status")) {
			sb.append(String.valueOf(insurancePlan.getStatus()));

			return sb.toString();
		}

		throw new IllegalArgumentException(
			"Invalid entity field " + entityFieldName);
	}

	protected String invoke(String query) throws Exception {
		HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

		httpInvoker.body(
			JSONUtil.put(
				"query", query
			).toString(),
			"application/json");
		httpInvoker.httpMethod(HttpInvoker.HttpMethod.POST);
		httpInvoker.path("http://localhost:8080/o/graphql");
		httpInvoker.userNameAndPassword(
			"test@liferay.com:" + PropsValues.DEFAULT_ADMIN_PASSWORD);

		HttpInvoker.HttpResponse httpResponse = httpInvoker.invoke();

		return httpResponse.getContent();
	}

	protected JSONObject invokeGraphQLMutation(GraphQLField graphQLField)
		throws Exception {

		GraphQLField mutationGraphQLField = new GraphQLField(
			"mutation", graphQLField);

		return JSONFactoryUtil.createJSONObject(
			invoke(mutationGraphQLField.toString()));
	}

	protected JSONObject invokeGraphQLQuery(GraphQLField graphQLField)
		throws Exception {

		GraphQLField queryGraphQLField = new GraphQLField(
			"query", graphQLField);

		return JSONFactoryUtil.createJSONObject(
			invoke(queryGraphQLField.toString()));
	}

	protected InsurancePlan randomInsurancePlan() throws Exception {
		return new InsurancePlan() {
			{
				active = RandomTestUtil.randomBoolean();
				annualContactsAllowanceCents = RandomTestUtil.randomLong();
				annualExamAllowanceCents = RandomTestUtil.randomLong();
				annualFramesAllowanceCents = RandomTestUtil.randomLong();
				annualLensesAllowanceCents = RandomTestUtil.randomLong();
				coveragePeriodMonths = RandomTestUtil.randomInt();
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				planName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				providerName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				siteId = testGroup.getGroupId();
				status = RandomTestUtil.randomInt();
			}
		};
	}

	protected InsurancePlan randomIrrelevantInsurancePlan() throws Exception {
		InsurancePlan randomIrrelevantInsurancePlan = randomInsurancePlan();

		randomIrrelevantInsurancePlan.setSiteId(irrelevantGroup.getGroupId());

		return randomIrrelevantInsurancePlan;
	}

	protected InsurancePlan randomPatchInsurancePlan() throws Exception {
		return randomInsurancePlan();
	}

	protected PlanEnrollment randomPlanEnrollment() throws Exception {
		return new PlanEnrollment() {
			{
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				endDate = RandomTestUtil.nextDate();
				enrollmentStatus = RandomTestUtil.randomInt();
				externalReferenceCode = RandomTestUtil.randomString();
				groupNumber = RandomTestUtil.randomString();
				id = RandomTestUtil.randomLong();
				insurancePlanERC = RandomTestUtil.randomString();
				insurancePlanId = RandomTestUtil.randomLong();
				memberId = RandomTestUtil.randomString();
				notes = RandomTestUtil.randomString();
				siteId = RandomTestUtil.randomLong();
				startDate = RandomTestUtil.nextDate();
				status = RandomTestUtil.randomInt();
			}
		};
	}

	protected InsurancePlanResource insurancePlanResource;
	protected com.liferay.portal.kernel.model.Group irrelevantGroup;
	protected com.liferay.portal.kernel.model.Company testCompany;
	protected com.liferay.portal.kernel.model.Group testGroup;

	protected static class BeanTestUtil {

		public static void copyProperties(Object source, Object target)
			throws Exception {

			Class<?> sourceClass = source.getClass();

			Class<?> targetClass = target.getClass();

			for (java.lang.reflect.Field field :
					_getAllDeclaredFields(sourceClass)) {

				if (field.isSynthetic()) {
					continue;
				}

				Method getMethod = _getMethod(
					sourceClass, field.getName(), "get");

				try {
					Method setMethod = _getMethod(
						targetClass, field.getName(), "set",
						getMethod.getReturnType());

					setMethod.invoke(target, getMethod.invoke(source));
				}
				catch (Exception e) {
					continue;
				}
			}
		}

		public static boolean hasProperty(Object bean, String name) {
			Method setMethod = _getMethod(
				bean.getClass(), "set" + StringUtil.upperCaseFirstLetter(name));

			if (setMethod != null) {
				return true;
			}

			return false;
		}

		public static void setProperty(Object bean, String name, Object value)
			throws Exception {

			Class<?> clazz = bean.getClass();

			Method setMethod = _getMethod(
				clazz, "set" + StringUtil.upperCaseFirstLetter(name));

			if (setMethod == null) {
				throw new NoSuchMethodException();
			}

			Class<?>[] parameterTypes = setMethod.getParameterTypes();

			setMethod.invoke(bean, _translateValue(parameterTypes[0], value));
		}

		private static List<java.lang.reflect.Field> _getAllDeclaredFields(
			Class<?> clazz) {

			List<java.lang.reflect.Field> fields = new ArrayList<>();

			while ((clazz != null) && (clazz != Object.class)) {
				for (java.lang.reflect.Field field :
						clazz.getDeclaredFields()) {

					fields.add(field);
				}

				clazz = clazz.getSuperclass();
			}

			return fields;
		}

		private static Method _getMethod(Class<?> clazz, String name) {
			for (Method method : clazz.getMethods()) {
				if (name.equals(method.getName()) &&
					(method.getParameterCount() == 1) &&
					_parameterTypes.contains(method.getParameterTypes()[0])) {

					return method;
				}
			}

			return null;
		}

		private static Method _getMethod(
				Class<?> clazz, String fieldName, String prefix,
				Class<?>... parameterTypes)
			throws Exception {

			return clazz.getMethod(
				prefix + StringUtil.upperCaseFirstLetter(fieldName),
				parameterTypes);
		}

		private static Object _translateValue(
			Class<?> parameterType, Object value) {

			if ((value instanceof Integer) &&
				parameterType.equals(Long.class)) {

				Integer intValue = (Integer)value;

				return intValue.longValue();
			}

			return value;
		}

		private static final Set<Class<?>> _parameterTypes = new HashSet<>(
			Arrays.asList(
				Boolean.class, Date.class, Double.class, Integer.class,
				Long.class, Map.class, String.class));

	}

	protected class GraphQLField {

		public GraphQLField(String key, GraphQLField... graphQLFields) {
			this(key, new HashMap<>(), graphQLFields);
		}

		public GraphQLField(String key, List<GraphQLField> graphQLFields) {
			this(key, new HashMap<>(), graphQLFields);
		}

		public GraphQLField(
			String key, Map<String, Object> parameterMap,
			GraphQLField... graphQLFields) {

			_key = key;
			_parameterMap = parameterMap;
			_graphQLFields = Arrays.asList(graphQLFields);
		}

		public GraphQLField(
			String key, Map<String, Object> parameterMap,
			List<GraphQLField> graphQLFields) {

			_key = key;
			_parameterMap = parameterMap;
			_graphQLFields = graphQLFields;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(_key);

			if (!_parameterMap.isEmpty()) {
				sb.append("(");

				for (Map.Entry<String, Object> entry :
						_parameterMap.entrySet()) {

					sb.append(entry.getKey());
					sb.append(": ");
					sb.append(entry.getValue());
					sb.append(", ");
				}

				sb.setLength(sb.length() - 2);

				sb.append(")");
			}

			if (!_graphQLFields.isEmpty()) {
				sb.append("{");

				for (GraphQLField graphQLField : _graphQLFields) {
					sb.append(graphQLField.toString());
					sb.append(", ");
				}

				sb.setLength(sb.length() - 2);

				sb.append("}");
			}

			return sb.toString();
		}

		private final List<GraphQLField> _graphQLFields;
		private final String _key;
		private final Map<String, Object> _parameterMap;

	}

	private static final com.liferay.portal.kernel.log.Log _log =
		LogFactoryUtil.getLog(BaseInsurancePlanResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.clarityvisionsolutions.insurance.benefits.tracker.rest.resource.
			v1_0.InsurancePlanResource _insurancePlanResource;

}