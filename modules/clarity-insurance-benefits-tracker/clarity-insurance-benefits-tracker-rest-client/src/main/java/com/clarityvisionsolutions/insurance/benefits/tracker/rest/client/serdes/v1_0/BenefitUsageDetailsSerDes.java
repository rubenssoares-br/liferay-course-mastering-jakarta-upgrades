package com.clarityvisionsolutions.insurance.benefits.tracker.rest.client.serdes.v1_0;

import com.clarityvisionsolutions.insurance.benefits.tracker.rest.client.dto.v1_0.BenefitUsageDetails;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author dnebinger
 * @generated
 */
@Generated("")
public class BenefitUsageDetailsSerDes {

	public static BenefitUsageDetails toDTO(String json) {
		BenefitUsageDetailsJSONParser benefitUsageDetailsJSONParser =
			new BenefitUsageDetailsJSONParser();

		return benefitUsageDetailsJSONParser.parseToDTO(json);
	}

	public static BenefitUsageDetails[] toDTOs(String json) {
		BenefitUsageDetailsJSONParser benefitUsageDetailsJSONParser =
			new BenefitUsageDetailsJSONParser();

		return benefitUsageDetailsJSONParser.parseToDTOs(json);
	}

	public static String toJSON(BenefitUsageDetails benefitUsageDetails) {
		if (benefitUsageDetails == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (benefitUsageDetails.getAnnualContactsAllowanceCents() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"annualContactsAllowanceCents\": ");

			sb.append(benefitUsageDetails.getAnnualContactsAllowanceCents());
		}

		if (benefitUsageDetails.getAnnualExamAllowanceCents() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"annualExamAllowanceCents\": ");

			sb.append(benefitUsageDetails.getAnnualExamAllowanceCents());
		}

		if (benefitUsageDetails.getAnnualFramesAllowanceCents() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"annualFramesAllowanceCents\": ");

			sb.append(benefitUsageDetails.getAnnualFramesAllowanceCents());
		}

		if (benefitUsageDetails.getAnnualLensesAllowanceCents() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"annualLensesAllowanceCents\": ");

			sb.append(benefitUsageDetails.getAnnualLensesAllowanceCents());
		}

		if (benefitUsageDetails.getContactsUsedCents() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contactsUsedCents\": ");

			sb.append(benefitUsageDetails.getContactsUsedCents());
		}

		if (benefitUsageDetails.getEndDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"endDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					benefitUsageDetails.getEndDate()));

			sb.append("\"");
		}

		if (benefitUsageDetails.getEnrollmentStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"enrollmentStatus\": ");

			sb.append(benefitUsageDetails.getEnrollmentStatus());
		}

		if (benefitUsageDetails.getExamUsedCents() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"examUsedCents\": ");

			sb.append(benefitUsageDetails.getExamUsedCents());
		}

		if (benefitUsageDetails.getFramesUsedCents() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"framesUsedCents\": ");

			sb.append(benefitUsageDetails.getFramesUsedCents());
		}

		if (benefitUsageDetails.getInsurancePlanId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"insurancePlanId\": ");

			sb.append(benefitUsageDetails.getInsurancePlanId());
		}

		if (benefitUsageDetails.getLensesUsedCents() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"lensesUsedCents\": ");

			sb.append(benefitUsageDetails.getLensesUsedCents());
		}

		if (benefitUsageDetails.getPlanEnrollmentId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"planEnrollmentId\": ");

			sb.append(benefitUsageDetails.getPlanEnrollmentId());
		}

		if (benefitUsageDetails.getPlanName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"planName\": ");

			sb.append("\"");

			sb.append(_escape(benefitUsageDetails.getPlanName()));

			sb.append("\"");
		}

		if (benefitUsageDetails.getProviderName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"providerName\": ");

			sb.append("\"");

			sb.append(_escape(benefitUsageDetails.getProviderName()));

			sb.append("\"");
		}

		if (benefitUsageDetails.getStartDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"startDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					benefitUsageDetails.getStartDate()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		BenefitUsageDetailsJSONParser benefitUsageDetailsJSONParser =
			new BenefitUsageDetailsJSONParser();

		return benefitUsageDetailsJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		BenefitUsageDetails benefitUsageDetails) {

		if (benefitUsageDetails == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (benefitUsageDetails.getAnnualContactsAllowanceCents() == null) {
			map.put("annualContactsAllowanceCents", null);
		}
		else {
			map.put(
				"annualContactsAllowanceCents",
				String.valueOf(
					benefitUsageDetails.getAnnualContactsAllowanceCents()));
		}

		if (benefitUsageDetails.getAnnualExamAllowanceCents() == null) {
			map.put("annualExamAllowanceCents", null);
		}
		else {
			map.put(
				"annualExamAllowanceCents",
				String.valueOf(
					benefitUsageDetails.getAnnualExamAllowanceCents()));
		}

		if (benefitUsageDetails.getAnnualFramesAllowanceCents() == null) {
			map.put("annualFramesAllowanceCents", null);
		}
		else {
			map.put(
				"annualFramesAllowanceCents",
				String.valueOf(
					benefitUsageDetails.getAnnualFramesAllowanceCents()));
		}

		if (benefitUsageDetails.getAnnualLensesAllowanceCents() == null) {
			map.put("annualLensesAllowanceCents", null);
		}
		else {
			map.put(
				"annualLensesAllowanceCents",
				String.valueOf(
					benefitUsageDetails.getAnnualLensesAllowanceCents()));
		}

		if (benefitUsageDetails.getContactsUsedCents() == null) {
			map.put("contactsUsedCents", null);
		}
		else {
			map.put(
				"contactsUsedCents",
				String.valueOf(benefitUsageDetails.getContactsUsedCents()));
		}

		if (benefitUsageDetails.getEndDate() == null) {
			map.put("endDate", null);
		}
		else {
			map.put(
				"endDate",
				liferayToJSONDateFormat.format(
					benefitUsageDetails.getEndDate()));
		}

		if (benefitUsageDetails.getEnrollmentStatus() == null) {
			map.put("enrollmentStatus", null);
		}
		else {
			map.put(
				"enrollmentStatus",
				String.valueOf(benefitUsageDetails.getEnrollmentStatus()));
		}

		if (benefitUsageDetails.getExamUsedCents() == null) {
			map.put("examUsedCents", null);
		}
		else {
			map.put(
				"examUsedCents",
				String.valueOf(benefitUsageDetails.getExamUsedCents()));
		}

		if (benefitUsageDetails.getFramesUsedCents() == null) {
			map.put("framesUsedCents", null);
		}
		else {
			map.put(
				"framesUsedCents",
				String.valueOf(benefitUsageDetails.getFramesUsedCents()));
		}

		if (benefitUsageDetails.getInsurancePlanId() == null) {
			map.put("insurancePlanId", null);
		}
		else {
			map.put(
				"insurancePlanId",
				String.valueOf(benefitUsageDetails.getInsurancePlanId()));
		}

		if (benefitUsageDetails.getLensesUsedCents() == null) {
			map.put("lensesUsedCents", null);
		}
		else {
			map.put(
				"lensesUsedCents",
				String.valueOf(benefitUsageDetails.getLensesUsedCents()));
		}

		if (benefitUsageDetails.getPlanEnrollmentId() == null) {
			map.put("planEnrollmentId", null);
		}
		else {
			map.put(
				"planEnrollmentId",
				String.valueOf(benefitUsageDetails.getPlanEnrollmentId()));
		}

		if (benefitUsageDetails.getPlanName() == null) {
			map.put("planName", null);
		}
		else {
			map.put(
				"planName", String.valueOf(benefitUsageDetails.getPlanName()));
		}

		if (benefitUsageDetails.getProviderName() == null) {
			map.put("providerName", null);
		}
		else {
			map.put(
				"providerName",
				String.valueOf(benefitUsageDetails.getProviderName()));
		}

		if (benefitUsageDetails.getStartDate() == null) {
			map.put("startDate", null);
		}
		else {
			map.put(
				"startDate",
				liferayToJSONDateFormat.format(
					benefitUsageDetails.getStartDate()));
		}

		return map;
	}

	public static class BenefitUsageDetailsJSONParser
		extends BaseJSONParser<BenefitUsageDetails> {

		@Override
		protected BenefitUsageDetails createDTO() {
			return new BenefitUsageDetails();
		}

		@Override
		protected BenefitUsageDetails[] createDTOArray(int size) {
			return new BenefitUsageDetails[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(
					jsonParserFieldName, "annualContactsAllowanceCents")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "annualExamAllowanceCents")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "annualFramesAllowanceCents")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "annualLensesAllowanceCents")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "contactsUsedCents")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "endDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "enrollmentStatus")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "examUsedCents")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "framesUsedCents")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "insurancePlanId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "lensesUsedCents")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "planEnrollmentId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "planName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "providerName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "startDate")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			BenefitUsageDetails benefitUsageDetails, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(
					jsonParserFieldName, "annualContactsAllowanceCents")) {

				if (jsonParserFieldValue != null) {
					benefitUsageDetails.setAnnualContactsAllowanceCents(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "annualExamAllowanceCents")) {

				if (jsonParserFieldValue != null) {
					benefitUsageDetails.setAnnualExamAllowanceCents(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "annualFramesAllowanceCents")) {

				if (jsonParserFieldValue != null) {
					benefitUsageDetails.setAnnualFramesAllowanceCents(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "annualLensesAllowanceCents")) {

				if (jsonParserFieldValue != null) {
					benefitUsageDetails.setAnnualLensesAllowanceCents(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "contactsUsedCents")) {
				if (jsonParserFieldValue != null) {
					benefitUsageDetails.setContactsUsedCents(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "endDate")) {
				if (jsonParserFieldValue != null) {
					benefitUsageDetails.setEndDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "enrollmentStatus")) {
				if (jsonParserFieldValue != null) {
					benefitUsageDetails.setEnrollmentStatus(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "examUsedCents")) {
				if (jsonParserFieldValue != null) {
					benefitUsageDetails.setExamUsedCents(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "framesUsedCents")) {
				if (jsonParserFieldValue != null) {
					benefitUsageDetails.setFramesUsedCents(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "insurancePlanId")) {
				if (jsonParserFieldValue != null) {
					benefitUsageDetails.setInsurancePlanId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "lensesUsedCents")) {
				if (jsonParserFieldValue != null) {
					benefitUsageDetails.setLensesUsedCents(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "planEnrollmentId")) {
				if (jsonParserFieldValue != null) {
					benefitUsageDetails.setPlanEnrollmentId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "planName")) {
				if (jsonParserFieldValue != null) {
					benefitUsageDetails.setPlanName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "providerName")) {
				if (jsonParserFieldValue != null) {
					benefitUsageDetails.setProviderName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "startDate")) {
				if (jsonParserFieldValue != null) {
					benefitUsageDetails.setStartDate(
						toDate((String)jsonParserFieldValue));
				}
			}
		}

	}

	private static String _escape(Object object) {
		String string = String.valueOf(object);

		for (String[] strings : BaseJSONParser.JSON_ESCAPE_STRINGS) {
			string = string.replace(strings[0], strings[1]);
		}

		return string;
	}

	private static String _toJSON(Map<String, ?> map) {
		StringBuilder sb = new StringBuilder("{");

		@SuppressWarnings("unchecked")
		Set set = map.entrySet();

		@SuppressWarnings("unchecked")
		Iterator<Map.Entry<String, ?>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, ?> entry = iterator.next();

			sb.append("\"");
			sb.append(entry.getKey());
			sb.append("\": ");

			Object value = entry.getValue();

			sb.append(_toJSON(value));

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static String _toJSON(Object value) {
		if (value == null) {
			return "null";
		}

		if (value instanceof Map) {
			return _toJSON((Map)value);
		}

		Class<?> clazz = value.getClass();

		if (clazz.isArray()) {
			StringBuilder sb = new StringBuilder("[");

			Object[] values = (Object[])value;

			for (int i = 0; i < values.length; i++) {
				sb.append(_toJSON(values[i]));

				if ((i + 1) < values.length) {
					sb.append(", ");
				}
			}

			sb.append("]");

			return sb.toString();
		}

		if (value instanceof String) {
			return "\"" + _escape(value) + "\"";
		}

		return String.valueOf(value);
	}

}