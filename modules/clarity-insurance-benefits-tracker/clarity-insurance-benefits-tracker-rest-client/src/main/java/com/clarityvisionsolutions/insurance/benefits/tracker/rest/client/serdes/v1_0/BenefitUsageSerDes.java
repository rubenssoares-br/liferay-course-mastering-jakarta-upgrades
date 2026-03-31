package com.clarityvisionsolutions.insurance.benefits.tracker.rest.client.serdes.v1_0;

import com.clarityvisionsolutions.insurance.benefits.tracker.rest.client.dto.v1_0.BenefitUsage;
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
public class BenefitUsageSerDes {

	public static BenefitUsage toDTO(String json) {
		BenefitUsageJSONParser benefitUsageJSONParser =
			new BenefitUsageJSONParser();

		return benefitUsageJSONParser.parseToDTO(json);
	}

	public static BenefitUsage[] toDTOs(String json) {
		BenefitUsageJSONParser benefitUsageJSONParser =
			new BenefitUsageJSONParser();

		return benefitUsageJSONParser.parseToDTOs(json);
	}

	public static String toJSON(BenefitUsage benefitUsage) {
		if (benefitUsage == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (benefitUsage.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(benefitUsage.getActions()));
		}

		if (benefitUsage.getAmountUsedCents() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"amountUsedCents\": ");

			sb.append(benefitUsage.getAmountUsedCents());
		}

		if (benefitUsage.getBenefitType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"benefitType\": ");

			sb.append("\"");

			sb.append(_escape(benefitUsage.getBenefitType()));

			sb.append("\"");
		}

		if (benefitUsage.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(String.valueOf(benefitUsage.getCreator()));
		}

		if (benefitUsage.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(benefitUsage.getDateCreated()));

			sb.append("\"");
		}

		if (benefitUsage.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(benefitUsage.getDateModified()));

			sb.append("\"");
		}

		if (benefitUsage.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(benefitUsage.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (benefitUsage.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(benefitUsage.getId());
		}

		if (benefitUsage.getNotes() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"notes\": ");

			sb.append("\"");

			sb.append(_escape(benefitUsage.getNotes()));

			sb.append("\"");
		}

		if (benefitUsage.getPlanEnrollmentERC() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"planEnrollmentERC\": ");

			sb.append("\"");

			sb.append(_escape(benefitUsage.getPlanEnrollmentERC()));

			sb.append("\"");
		}

		if (benefitUsage.getPlanEnrollmentId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"planEnrollmentId\": ");

			sb.append(benefitUsage.getPlanEnrollmentId());
		}

		if (benefitUsage.getReference() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"reference\": ");

			sb.append("\"");

			sb.append(_escape(benefitUsage.getReference()));

			sb.append("\"");
		}

		if (benefitUsage.getServiceDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"serviceDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(benefitUsage.getServiceDate()));

			sb.append("\"");
		}

		if (benefitUsage.getSiteId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteId\": ");

			sb.append(benefitUsage.getSiteId());
		}

		if (benefitUsage.getSourceReference() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sourceReference\": ");

			sb.append("\"");

			sb.append(_escape(benefitUsage.getSourceReference()));

			sb.append("\"");
		}

		if (benefitUsage.getSourceType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sourceType\": ");

			sb.append("\"");

			sb.append(_escape(benefitUsage.getSourceType()));

			sb.append("\"");
		}

		if (benefitUsage.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append(benefitUsage.getStatus());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		BenefitUsageJSONParser benefitUsageJSONParser =
			new BenefitUsageJSONParser();

		return benefitUsageJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(BenefitUsage benefitUsage) {
		if (benefitUsage == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (benefitUsage.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(benefitUsage.getActions()));
		}

		if (benefitUsage.getAmountUsedCents() == null) {
			map.put("amountUsedCents", null);
		}
		else {
			map.put(
				"amountUsedCents",
				String.valueOf(benefitUsage.getAmountUsedCents()));
		}

		if (benefitUsage.getBenefitType() == null) {
			map.put("benefitType", null);
		}
		else {
			map.put(
				"benefitType", String.valueOf(benefitUsage.getBenefitType()));
		}

		if (benefitUsage.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put("creator", String.valueOf(benefitUsage.getCreator()));
		}

		if (benefitUsage.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(benefitUsage.getDateCreated()));
		}

		if (benefitUsage.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(benefitUsage.getDateModified()));
		}

		if (benefitUsage.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(benefitUsage.getExternalReferenceCode()));
		}

		if (benefitUsage.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(benefitUsage.getId()));
		}

		if (benefitUsage.getNotes() == null) {
			map.put("notes", null);
		}
		else {
			map.put("notes", String.valueOf(benefitUsage.getNotes()));
		}

		if (benefitUsage.getPlanEnrollmentERC() == null) {
			map.put("planEnrollmentERC", null);
		}
		else {
			map.put(
				"planEnrollmentERC",
				String.valueOf(benefitUsage.getPlanEnrollmentERC()));
		}

		if (benefitUsage.getPlanEnrollmentId() == null) {
			map.put("planEnrollmentId", null);
		}
		else {
			map.put(
				"planEnrollmentId",
				String.valueOf(benefitUsage.getPlanEnrollmentId()));
		}

		if (benefitUsage.getReference() == null) {
			map.put("reference", null);
		}
		else {
			map.put("reference", String.valueOf(benefitUsage.getReference()));
		}

		if (benefitUsage.getServiceDate() == null) {
			map.put("serviceDate", null);
		}
		else {
			map.put(
				"serviceDate",
				liferayToJSONDateFormat.format(benefitUsage.getServiceDate()));
		}

		if (benefitUsage.getSiteId() == null) {
			map.put("siteId", null);
		}
		else {
			map.put("siteId", String.valueOf(benefitUsage.getSiteId()));
		}

		if (benefitUsage.getSourceReference() == null) {
			map.put("sourceReference", null);
		}
		else {
			map.put(
				"sourceReference",
				String.valueOf(benefitUsage.getSourceReference()));
		}

		if (benefitUsage.getSourceType() == null) {
			map.put("sourceType", null);
		}
		else {
			map.put("sourceType", String.valueOf(benefitUsage.getSourceType()));
		}

		if (benefitUsage.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put("status", String.valueOf(benefitUsage.getStatus()));
		}

		return map;
	}

	public static class BenefitUsageJSONParser
		extends BaseJSONParser<BenefitUsage> {

		@Override
		protected BenefitUsage createDTO() {
			return new BenefitUsage();
		}

		@Override
		protected BenefitUsage[] createDTOArray(int size) {
			return new BenefitUsage[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "amountUsedCents")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "benefitType")) {
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
			else if (Objects.equals(jsonParserFieldName, "notes")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "planEnrollmentERC")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "planEnrollmentId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "reference")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "serviceDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "sourceReference")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "sourceType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			BenefitUsage benefitUsage, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					benefitUsage.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "amountUsedCents")) {
				if (jsonParserFieldValue != null) {
					benefitUsage.setAmountUsedCents(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "benefitType")) {
				if (jsonParserFieldValue != null) {
					benefitUsage.setBenefitType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					benefitUsage.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					benefitUsage.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					benefitUsage.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					benefitUsage.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					benefitUsage.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "notes")) {
				if (jsonParserFieldValue != null) {
					benefitUsage.setNotes((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "planEnrollmentERC")) {
				if (jsonParserFieldValue != null) {
					benefitUsage.setPlanEnrollmentERC(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "planEnrollmentId")) {
				if (jsonParserFieldValue != null) {
					benefitUsage.setPlanEnrollmentId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "reference")) {
				if (jsonParserFieldValue != null) {
					benefitUsage.setReference((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "serviceDate")) {
				if (jsonParserFieldValue != null) {
					benefitUsage.setServiceDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				if (jsonParserFieldValue != null) {
					benefitUsage.setSiteId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sourceReference")) {
				if (jsonParserFieldValue != null) {
					benefitUsage.setSourceReference(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sourceType")) {
				if (jsonParserFieldValue != null) {
					benefitUsage.setSourceType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					benefitUsage.setStatus(
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