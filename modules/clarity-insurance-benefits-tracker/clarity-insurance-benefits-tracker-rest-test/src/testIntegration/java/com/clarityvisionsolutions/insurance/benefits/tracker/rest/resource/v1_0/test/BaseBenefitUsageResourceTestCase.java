package com.clarityvisionsolutions.insurance.benefits.tracker.rest.resource.v1_0.test;

import com.clarityvisionsolutions.insurance.benefits.tracker.rest.client.dto.v1_0.BenefitUsage;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.client.http.HttpInvoker;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.client.pagination.Page;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.client.resource.v1_0.BenefitUsageResource;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.client.serdes.v1_0.BenefitUsageSerDes;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
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
public abstract class BaseBenefitUsageResourceTestCase {

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

		_benefitUsageResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		benefitUsageResource = BenefitUsageResource.builder(
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

		BenefitUsage benefitUsage1 = randomBenefitUsage();

		String json = objectMapper.writeValueAsString(benefitUsage1);

		BenefitUsage benefitUsage2 = BenefitUsageSerDes.toDTO(json);

		Assert.assertTrue(equals(benefitUsage1, benefitUsage2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		BenefitUsage benefitUsage = randomBenefitUsage();

		String json1 = objectMapper.writeValueAsString(benefitUsage);
		String json2 = BenefitUsageSerDes.toJSON(benefitUsage);

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

		BenefitUsage benefitUsage = randomBenefitUsage();

		benefitUsage.setBenefitType(regex);
		benefitUsage.setExternalReferenceCode(regex);
		benefitUsage.setNotes(regex);
		benefitUsage.setPlanEnrollmentERC(regex);
		benefitUsage.setReference(regex);
		benefitUsage.setSourceReference(regex);
		benefitUsage.setSourceType(regex);

		String json = BenefitUsageSerDes.toJSON(benefitUsage);

		Assert.assertFalse(json.contains(regex));

		benefitUsage = BenefitUsageSerDes.toDTO(json);

		Assert.assertEquals(regex, benefitUsage.getBenefitType());
		Assert.assertEquals(regex, benefitUsage.getExternalReferenceCode());
		Assert.assertEquals(regex, benefitUsage.getNotes());
		Assert.assertEquals(regex, benefitUsage.getPlanEnrollmentERC());
		Assert.assertEquals(regex, benefitUsage.getReference());
		Assert.assertEquals(regex, benefitUsage.getSourceReference());
		Assert.assertEquals(regex, benefitUsage.getSourceType());
	}

	@Test
	public void testDeleteBenefitUsage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		BenefitUsage benefitUsage = testDeleteBenefitUsage_addBenefitUsage();

		assertHttpResponseStatusCode(
			204,
			benefitUsageResource.deleteBenefitUsageHttpResponse(
				benefitUsage.getId()));

		assertHttpResponseStatusCode(
			404,
			benefitUsageResource.getBenefitUsageHttpResponse(
				benefitUsage.getId()));
		assertHttpResponseStatusCode(
			404, benefitUsageResource.getBenefitUsageHttpResponse(0L));
	}

	protected BenefitUsage testDeleteBenefitUsage_addBenefitUsage()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteBenefitUsage() throws Exception {

		// No namespace

		BenefitUsage benefitUsage1 =
			testGraphQLDeleteBenefitUsage_addBenefitUsage();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteBenefitUsage",
						new HashMap<String, Object>() {
							{
								put("benefitUsageId", benefitUsage1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteBenefitUsage"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"benefitUsage",
					new HashMap<String, Object>() {
						{
							put("benefitUsageId", benefitUsage1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace clarityInsuranceBenefitsTracker_v1_0

		BenefitUsage benefitUsage2 =
			testGraphQLDeleteBenefitUsage_addBenefitUsage();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"clarityInsuranceBenefitsTracker_v1_0",
						new GraphQLField(
							"deleteBenefitUsage",
							new HashMap<String, Object>() {
								{
									put(
										"benefitUsageId",
										benefitUsage2.getId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/clarityInsuranceBenefitsTracker_v1_0",
				"Object/deleteBenefitUsage"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"clarityInsuranceBenefitsTracker_v1_0",
					new GraphQLField(
						"benefitUsage",
						new HashMap<String, Object>() {
							{
								put("benefitUsageId", benefitUsage2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected BenefitUsage testGraphQLDeleteBenefitUsage_addBenefitUsage()
		throws Exception {

		return testGraphQLBenefitUsage_addBenefitUsage();
	}

	@Test
	public void testGetBenefitUsage() throws Exception {
		BenefitUsage postBenefitUsage = testGetBenefitUsage_addBenefitUsage();

		BenefitUsage getBenefitUsage = benefitUsageResource.getBenefitUsage(
			postBenefitUsage.getId());

		assertEquals(postBenefitUsage, getBenefitUsage);
		assertValid(getBenefitUsage);
	}

	protected BenefitUsage testGetBenefitUsage_addBenefitUsage()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetBenefitUsage() throws Exception {
		BenefitUsage benefitUsage =
			testGraphQLGetBenefitUsage_addBenefitUsage();

		// No namespace

		Assert.assertTrue(
			equals(
				benefitUsage,
				BenefitUsageSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"benefitUsage",
								new HashMap<String, Object>() {
									{
										put(
											"benefitUsageId",
											benefitUsage.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/benefitUsage"))));

		// Using the namespace clarityInsuranceBenefitsTracker_v1_0

		Assert.assertTrue(
			equals(
				benefitUsage,
				BenefitUsageSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"clarityInsuranceBenefitsTracker_v1_0",
								new GraphQLField(
									"benefitUsage",
									new HashMap<String, Object>() {
										{
											put(
												"benefitUsageId",
												benefitUsage.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/clarityInsuranceBenefitsTracker_v1_0",
						"Object/benefitUsage"))));
	}

	@Test
	public void testGraphQLGetBenefitUsageNotFound() throws Exception {
		Long irrelevantBenefitUsageId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"benefitUsage",
						new HashMap<String, Object>() {
							{
								put("benefitUsageId", irrelevantBenefitUsageId);
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
							"benefitUsage",
							new HashMap<String, Object>() {
								{
									put(
										"benefitUsageId",
										irrelevantBenefitUsageId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected BenefitUsage testGraphQLGetBenefitUsage_addBenefitUsage()
		throws Exception {

		return testGraphQLBenefitUsage_addBenefitUsage();
	}

	@Test
	public void testGetSiteBenefitUsageByExternalReferenceCode()
		throws Exception {

		BenefitUsage postBenefitUsage =
			testGetSiteBenefitUsageByExternalReferenceCode_addBenefitUsage();

		BenefitUsage getBenefitUsage =
			benefitUsageResource.getSiteBenefitUsageByExternalReferenceCode(
				postBenefitUsage.getSiteId(),
				postBenefitUsage.getExternalReferenceCode());

		assertEquals(postBenefitUsage, getBenefitUsage);
		assertValid(getBenefitUsage);
	}

	protected BenefitUsage
			testGetSiteBenefitUsageByExternalReferenceCode_addBenefitUsage()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSiteBenefitUsageByExternalReferenceCode()
		throws Exception {

		BenefitUsage benefitUsage =
			testGraphQLGetSiteBenefitUsageByExternalReferenceCode_addBenefitUsage();

		// No namespace

		Assert.assertTrue(
			equals(
				benefitUsage,
				BenefitUsageSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"benefitUsageByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"siteKey",
											"\"" + benefitUsage.getSiteId() +
												"\"");
										put(
											"externalReferenceCode",
											"\"" +
												benefitUsage.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/benefitUsageByExternalReferenceCode"))));

		// Using the namespace clarityInsuranceBenefitsTracker_v1_0

		Assert.assertTrue(
			equals(
				benefitUsage,
				BenefitUsageSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"clarityInsuranceBenefitsTracker_v1_0",
								new GraphQLField(
									"benefitUsageByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"siteKey",
												"\"" +
													benefitUsage.getSiteId() +
														"\"");
											put(
												"externalReferenceCode",
												"\"" +
													benefitUsage.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/clarityInsuranceBenefitsTracker_v1_0",
						"Object/benefitUsageByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetSiteBenefitUsageByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"benefitUsageByExternalReferenceCode",
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
							"benefitUsageByExternalReferenceCode",
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

	protected BenefitUsage
			testGraphQLGetSiteBenefitUsageByExternalReferenceCode_addBenefitUsage()
		throws Exception {

		return testGraphQLSiteBenefitUsage_addBenefitUsage();
	}

	@Test
	public void testPatchBenefitUsage() throws Exception {
		BenefitUsage postBenefitUsage = testPatchBenefitUsage_addBenefitUsage();

		BenefitUsage randomPatchBenefitUsage = randomPatchBenefitUsage();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		BenefitUsage patchBenefitUsage = benefitUsageResource.patchBenefitUsage(
			postBenefitUsage.getId(), randomPatchBenefitUsage);

		BenefitUsage expectedPatchBenefitUsage = postBenefitUsage.clone();

		BeanTestUtil.copyProperties(
			randomPatchBenefitUsage, expectedPatchBenefitUsage);

		BenefitUsage getBenefitUsage = benefitUsageResource.getBenefitUsage(
			patchBenefitUsage.getId());

		assertEquals(expectedPatchBenefitUsage, getBenefitUsage);
		assertValid(getBenefitUsage);
	}

	protected BenefitUsage testPatchBenefitUsage_addBenefitUsage()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutBenefitUsage() throws Exception {
		BenefitUsage postBenefitUsage = testPutBenefitUsage_addBenefitUsage();

		BenefitUsage randomBenefitUsage = randomBenefitUsage();

		BenefitUsage putBenefitUsage = benefitUsageResource.putBenefitUsage(
			postBenefitUsage.getId(), randomBenefitUsage);

		assertEquals(randomBenefitUsage, putBenefitUsage);
		assertValid(putBenefitUsage);

		BenefitUsage getBenefitUsage = benefitUsageResource.getBenefitUsage(
			putBenefitUsage.getId());

		assertEquals(randomBenefitUsage, getBenefitUsage);
		assertValid(getBenefitUsage);
	}

	protected BenefitUsage testPutBenefitUsage_addBenefitUsage()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected BenefitUsage testGraphQLBenefitUsage_addBenefitUsage()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected BenefitUsage testGraphQLSiteBenefitUsage_addBenefitUsage()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		BenefitUsage benefitUsage, List<BenefitUsage> benefitUsages) {

		boolean contains = false;

		for (BenefitUsage item : benefitUsages) {
			if (equals(benefitUsage, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			benefitUsages + " does not contain " + benefitUsage, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		BenefitUsage benefitUsage1, BenefitUsage benefitUsage2) {

		Assert.assertTrue(
			benefitUsage1 + " does not equal " + benefitUsage2,
			equals(benefitUsage1, benefitUsage2));
	}

	protected void assertEquals(
		List<BenefitUsage> benefitUsages1, List<BenefitUsage> benefitUsages2) {

		Assert.assertEquals(benefitUsages1.size(), benefitUsages2.size());

		for (int i = 0; i < benefitUsages1.size(); i++) {
			BenefitUsage benefitUsage1 = benefitUsages1.get(i);
			BenefitUsage benefitUsage2 = benefitUsages2.get(i);

			assertEquals(benefitUsage1, benefitUsage2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<BenefitUsage> benefitUsages1, List<BenefitUsage> benefitUsages2) {

		Assert.assertEquals(benefitUsages1.size(), benefitUsages2.size());

		for (BenefitUsage benefitUsage1 : benefitUsages1) {
			boolean contains = false;

			for (BenefitUsage benefitUsage2 : benefitUsages2) {
				if (equals(benefitUsage1, benefitUsage2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				benefitUsages2 + " does not contain " + benefitUsage1,
				contains);
		}
	}

	protected void assertValid(BenefitUsage benefitUsage) throws Exception {
		boolean valid = true;

		if (benefitUsage.getDateCreated() == null) {
			valid = false;
		}

		if (benefitUsage.getDateModified() == null) {
			valid = false;
		}

		if (benefitUsage.getId() == null) {
			valid = false;
		}

		if (!Objects.equals(benefitUsage.getSiteId(), testGroup.getGroupId())) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (benefitUsage.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("amountUsedCents", additionalAssertFieldName)) {
				if (benefitUsage.getAmountUsedCents() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("benefitType", additionalAssertFieldName)) {
				if (benefitUsage.getBenefitType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (benefitUsage.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (benefitUsage.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("notes", additionalAssertFieldName)) {
				if (benefitUsage.getNotes() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"planEnrollmentERC", additionalAssertFieldName)) {

				if (benefitUsage.getPlanEnrollmentERC() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("planEnrollmentId", additionalAssertFieldName)) {
				if (benefitUsage.getPlanEnrollmentId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("reference", additionalAssertFieldName)) {
				if (benefitUsage.getReference() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("serviceDate", additionalAssertFieldName)) {
				if (benefitUsage.getServiceDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("sourceReference", additionalAssertFieldName)) {
				if (benefitUsage.getSourceReference() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("sourceType", additionalAssertFieldName)) {
				if (benefitUsage.getSourceType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (benefitUsage.getStatus() == null) {
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

	protected void assertValid(Page<BenefitUsage> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<BenefitUsage> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<BenefitUsage> benefitUsages = page.getItems();

		int size = benefitUsages.size();

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

	protected String[] getAdditionalAssertFieldNames() {
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
						dto.v1_0.BenefitUsage.class)) {

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
		BenefitUsage benefitUsage1, BenefitUsage benefitUsage2) {

		if (benefitUsage1 == benefitUsage2) {
			return true;
		}

		if (!Objects.equals(
				benefitUsage1.getSiteId(), benefitUsage2.getSiteId())) {

			return false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)benefitUsage1.getActions(),
						(Map)benefitUsage2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("amountUsedCents", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						benefitUsage1.getAmountUsedCents(),
						benefitUsage2.getAmountUsedCents())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("benefitType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						benefitUsage1.getBenefitType(),
						benefitUsage2.getBenefitType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						benefitUsage1.getCreator(),
						benefitUsage2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						benefitUsage1.getDateCreated(),
						benefitUsage2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						benefitUsage1.getDateModified(),
						benefitUsage2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						benefitUsage1.getExternalReferenceCode(),
						benefitUsage2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						benefitUsage1.getId(), benefitUsage2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("notes", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						benefitUsage1.getNotes(), benefitUsage2.getNotes())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"planEnrollmentERC", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						benefitUsage1.getPlanEnrollmentERC(),
						benefitUsage2.getPlanEnrollmentERC())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("planEnrollmentId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						benefitUsage1.getPlanEnrollmentId(),
						benefitUsage2.getPlanEnrollmentId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("reference", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						benefitUsage1.getReference(),
						benefitUsage2.getReference())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("serviceDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						benefitUsage1.getServiceDate(),
						benefitUsage2.getServiceDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("sourceReference", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						benefitUsage1.getSourceReference(),
						benefitUsage2.getSourceReference())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("sourceType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						benefitUsage1.getSourceType(),
						benefitUsage2.getSourceType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						benefitUsage1.getStatus(), benefitUsage2.getStatus())) {

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

		if (!(_benefitUsageResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_benefitUsageResource;

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
		EntityField entityField, String operator, BenefitUsage benefitUsage) {

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

		if (entityFieldName.equals("amountUsedCents")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("benefitType")) {
			Object object = benefitUsage.getBenefitType();

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

		if (entityFieldName.equals("creator")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = benefitUsage.getDateCreated();

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

				sb.append(_format.format(benefitUsage.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = benefitUsage.getDateModified();

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

				sb.append(_format.format(benefitUsage.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = benefitUsage.getExternalReferenceCode();

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

		if (entityFieldName.equals("notes")) {
			Object object = benefitUsage.getNotes();

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

		if (entityFieldName.equals("planEnrollmentERC")) {
			Object object = benefitUsage.getPlanEnrollmentERC();

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

		if (entityFieldName.equals("planEnrollmentId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("reference")) {
			Object object = benefitUsage.getReference();

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

		if (entityFieldName.equals("serviceDate")) {
			if (operator.equals("between")) {
				Date date = benefitUsage.getServiceDate();

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

				sb.append(_format.format(benefitUsage.getServiceDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("siteId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("sourceReference")) {
			Object object = benefitUsage.getSourceReference();

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

		if (entityFieldName.equals("sourceType")) {
			Object object = benefitUsage.getSourceType();

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

		if (entityFieldName.equals("status")) {
			sb.append(String.valueOf(benefitUsage.getStatus()));

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

	protected BenefitUsage randomBenefitUsage() throws Exception {
		return new BenefitUsage() {
			{
				amountUsedCents = RandomTestUtil.randomLong();
				benefitType = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				notes = StringUtil.toLowerCase(RandomTestUtil.randomString());
				planEnrollmentERC = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				planEnrollmentId = RandomTestUtil.randomLong();
				reference = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				serviceDate = RandomTestUtil.nextDate();
				siteId = testGroup.getGroupId();
				sourceReference = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				sourceType = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				status = RandomTestUtil.randomInt();
			}
		};
	}

	protected BenefitUsage randomIrrelevantBenefitUsage() throws Exception {
		BenefitUsage randomIrrelevantBenefitUsage = randomBenefitUsage();

		randomIrrelevantBenefitUsage.setSiteId(irrelevantGroup.getGroupId());

		return randomIrrelevantBenefitUsage;
	}

	protected BenefitUsage randomPatchBenefitUsage() throws Exception {
		return randomBenefitUsage();
	}

	protected BenefitUsageResource benefitUsageResource;
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
		LogFactoryUtil.getLog(BaseBenefitUsageResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.clarityvisionsolutions.insurance.benefits.tracker.rest.resource.
			v1_0.BenefitUsageResource _benefitUsageResource;

}