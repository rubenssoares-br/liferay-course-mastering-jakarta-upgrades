package com.clarityvisionsolutions.insurance.benefits.tracker.rest.client.serdes.v1_0;

import com.clarityvisionsolutions.insurance.benefits.tracker.rest.client.dto.v1_0.InsurancePlan;
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
public class InsurancePlanSerDes {

	public static InsurancePlan toDTO(String json) {
		InsurancePlanJSONParser insurancePlanJSONParser =
			new InsurancePlanJSONParser();

		return insurancePlanJSONParser.parseToDTO(json);
	}

	public static InsurancePlan[] toDTOs(String json) {
		InsurancePlanJSONParser insurancePlanJSONParser =
			new InsurancePlanJSONParser();

		return insurancePlanJSONParser.parseToDTOs(json);
	}

	public static String toJSON(InsurancePlan insurancePlan) {
		if (insurancePlan == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (insurancePlan.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(insurancePlan.getActions()));
		}

		if (insurancePlan.getActive() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"active\": ");

			sb.append(insurancePlan.getActive());
		}

		if (insurancePlan.getAnnualContactsAllowanceCents() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"annualContactsAllowanceCents\": ");

			sb.append(insurancePlan.getAnnualContactsAllowanceCents());
		}

		if (insurancePlan.getAnnualExamAllowanceCents() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"annualExamAllowanceCents\": ");

			sb.append(insurancePlan.getAnnualExamAllowanceCents());
		}

		if (insurancePlan.getAnnualFramesAllowanceCents() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"annualFramesAllowanceCents\": ");

			sb.append(insurancePlan.getAnnualFramesAllowanceCents());
		}

		if (insurancePlan.getAnnualLensesAllowanceCents() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"annualLensesAllowanceCents\": ");

			sb.append(insurancePlan.getAnnualLensesAllowanceCents());
		}

		if (insurancePlan.getCoveragePeriodMonths() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"coveragePeriodMonths\": ");

			sb.append(insurancePlan.getCoveragePeriodMonths());
		}

		if (insurancePlan.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(String.valueOf(insurancePlan.getCreator()));
		}

		if (insurancePlan.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(insurancePlan.getDateCreated()));

			sb.append("\"");
		}

		if (insurancePlan.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					insurancePlan.getDateModified()));

			sb.append("\"");
		}

		if (insurancePlan.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(insurancePlan.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (insurancePlan.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(insurancePlan.getId());
		}

		if (insurancePlan.getPlanName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"planName\": ");

			sb.append("\"");

			sb.append(_escape(insurancePlan.getPlanName()));

			sb.append("\"");
		}

		if (insurancePlan.getProviderName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"providerName\": ");

			sb.append("\"");

			sb.append(_escape(insurancePlan.getProviderName()));

			sb.append("\"");
		}

		if (insurancePlan.getSiteId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteId\": ");

			sb.append(insurancePlan.getSiteId());
		}

		if (insurancePlan.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append(insurancePlan.getStatus());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		InsurancePlanJSONParser insurancePlanJSONParser =
			new InsurancePlanJSONParser();

		return insurancePlanJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(InsurancePlan insurancePlan) {
		if (insurancePlan == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (insurancePlan.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(insurancePlan.getActions()));
		}

		if (insurancePlan.getActive() == null) {
			map.put("active", null);
		}
		else {
			map.put("active", String.valueOf(insurancePlan.getActive()));
		}

		if (insurancePlan.getAnnualContactsAllowanceCents() == null) {
			map.put("annualContactsAllowanceCents", null);
		}
		else {
			map.put(
				"annualContactsAllowanceCents",
				String.valueOf(
					insurancePlan.getAnnualContactsAllowanceCents()));
		}

		if (insurancePlan.getAnnualExamAllowanceCents() == null) {
			map.put("annualExamAllowanceCents", null);
		}
		else {
			map.put(
				"annualExamAllowanceCents",
				String.valueOf(insurancePlan.getAnnualExamAllowanceCents()));
		}

		if (insurancePlan.getAnnualFramesAllowanceCents() == null) {
			map.put("annualFramesAllowanceCents", null);
		}
		else {
			map.put(
				"annualFramesAllowanceCents",
				String.valueOf(insurancePlan.getAnnualFramesAllowanceCents()));
		}

		if (insurancePlan.getAnnualLensesAllowanceCents() == null) {
			map.put("annualLensesAllowanceCents", null);
		}
		else {
			map.put(
				"annualLensesAllowanceCents",
				String.valueOf(insurancePlan.getAnnualLensesAllowanceCents()));
		}

		if (insurancePlan.getCoveragePeriodMonths() == null) {
			map.put("coveragePeriodMonths", null);
		}
		else {
			map.put(
				"coveragePeriodMonths",
				String.valueOf(insurancePlan.getCoveragePeriodMonths()));
		}

		if (insurancePlan.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put("creator", String.valueOf(insurancePlan.getCreator()));
		}

		if (insurancePlan.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(insurancePlan.getDateCreated()));
		}

		if (insurancePlan.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(
					insurancePlan.getDateModified()));
		}

		if (insurancePlan.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(insurancePlan.getExternalReferenceCode()));
		}

		if (insurancePlan.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(insurancePlan.getId()));
		}

		if (insurancePlan.getPlanName() == null) {
			map.put("planName", null);
		}
		else {
			map.put("planName", String.valueOf(insurancePlan.getPlanName()));
		}

		if (insurancePlan.getProviderName() == null) {
			map.put("providerName", null);
		}
		else {
			map.put(
				"providerName",
				String.valueOf(insurancePlan.getProviderName()));
		}

		if (insurancePlan.getSiteId() == null) {
			map.put("siteId", null);
		}
		else {
			map.put("siteId", String.valueOf(insurancePlan.getSiteId()));
		}

		if (insurancePlan.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put("status", String.valueOf(insurancePlan.getStatus()));
		}

		return map;
	}

	public static class InsurancePlanJSONParser
		extends BaseJSONParser<InsurancePlan> {

		@Override
		protected InsurancePlan createDTO() {
			return new InsurancePlan();
		}

		@Override
		protected InsurancePlan[] createDTOArray(int size) {
			return new InsurancePlan[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "active")) {
				return false;
			}
			else if (Objects.equals(
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
			else if (Objects.equals(
						jsonParserFieldName, "coveragePeriodMonths")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "planName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "providerName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			InsurancePlan insurancePlan, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					insurancePlan.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "active")) {
				if (jsonParserFieldValue != null) {
					insurancePlan.setActive((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "annualContactsAllowanceCents")) {

				if (jsonParserFieldValue != null) {
					insurancePlan.setAnnualContactsAllowanceCents(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "annualExamAllowanceCents")) {

				if (jsonParserFieldValue != null) {
					insurancePlan.setAnnualExamAllowanceCents(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "annualFramesAllowanceCents")) {

				if (jsonParserFieldValue != null) {
					insurancePlan.setAnnualFramesAllowanceCents(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "annualLensesAllowanceCents")) {

				if (jsonParserFieldValue != null) {
					insurancePlan.setAnnualLensesAllowanceCents(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "coveragePeriodMonths")) {

				if (jsonParserFieldValue != null) {
					insurancePlan.setCoveragePeriodMonths(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					insurancePlan.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					insurancePlan.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					insurancePlan.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					insurancePlan.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					insurancePlan.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "planName")) {
				if (jsonParserFieldValue != null) {
					insurancePlan.setPlanName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "providerName")) {
				if (jsonParserFieldValue != null) {
					insurancePlan.setProviderName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				if (jsonParserFieldValue != null) {
					insurancePlan.setSiteId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					insurancePlan.setStatus(
						Integer.valueOf((String)jsonParserFieldValue));
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