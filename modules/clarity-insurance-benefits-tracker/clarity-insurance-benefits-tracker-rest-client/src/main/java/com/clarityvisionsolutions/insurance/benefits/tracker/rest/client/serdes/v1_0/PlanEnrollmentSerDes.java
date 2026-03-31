package com.clarityvisionsolutions.insurance.benefits.tracker.rest.client.serdes.v1_0;

import com.clarityvisionsolutions.insurance.benefits.tracker.rest.client.dto.v1_0.PlanEnrollment;
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
public class PlanEnrollmentSerDes {

	public static PlanEnrollment toDTO(String json) {
		PlanEnrollmentJSONParser planEnrollmentJSONParser =
			new PlanEnrollmentJSONParser();

		return planEnrollmentJSONParser.parseToDTO(json);
	}

	public static PlanEnrollment[] toDTOs(String json) {
		PlanEnrollmentJSONParser planEnrollmentJSONParser =
			new PlanEnrollmentJSONParser();

		return planEnrollmentJSONParser.parseToDTOs(json);
	}

	public static String toJSON(PlanEnrollment planEnrollment) {
		if (planEnrollment == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (planEnrollment.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(planEnrollment.getActions()));
		}

		if (planEnrollment.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(String.valueOf(planEnrollment.getCreator()));
		}

		if (planEnrollment.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					planEnrollment.getDateCreated()));

			sb.append("\"");
		}

		if (planEnrollment.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					planEnrollment.getDateModified()));

			sb.append("\"");
		}

		if (planEnrollment.getEndDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"endDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(planEnrollment.getEndDate()));

			sb.append("\"");
		}

		if (planEnrollment.getEnrollmentStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"enrollmentStatus\": ");

			sb.append(planEnrollment.getEnrollmentStatus());
		}

		if (planEnrollment.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(planEnrollment.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (planEnrollment.getGroupNumber() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"groupNumber\": ");

			sb.append("\"");

			sb.append(_escape(planEnrollment.getGroupNumber()));

			sb.append("\"");
		}

		if (planEnrollment.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(planEnrollment.getId());
		}

		if (planEnrollment.getInsurancePlanERC() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"insurancePlanERC\": ");

			sb.append("\"");

			sb.append(_escape(planEnrollment.getInsurancePlanERC()));

			sb.append("\"");
		}

		if (planEnrollment.getInsurancePlanId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"insurancePlanId\": ");

			sb.append(planEnrollment.getInsurancePlanId());
		}

		if (planEnrollment.getMember() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"member\": ");

			sb.append(String.valueOf(planEnrollment.getMember()));
		}

		if (planEnrollment.getMemberId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"memberId\": ");

			sb.append("\"");

			sb.append(_escape(planEnrollment.getMemberId()));

			sb.append("\"");
		}

		if (planEnrollment.getNotes() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"notes\": ");

			sb.append("\"");

			sb.append(_escape(planEnrollment.getNotes()));

			sb.append("\"");
		}

		if (planEnrollment.getSiteId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteId\": ");

			sb.append(planEnrollment.getSiteId());
		}

		if (planEnrollment.getStartDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"startDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(planEnrollment.getStartDate()));

			sb.append("\"");
		}

		if (planEnrollment.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append(planEnrollment.getStatus());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PlanEnrollmentJSONParser planEnrollmentJSONParser =
			new PlanEnrollmentJSONParser();

		return planEnrollmentJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(PlanEnrollment planEnrollment) {
		if (planEnrollment == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (planEnrollment.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(planEnrollment.getActions()));
		}

		if (planEnrollment.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put("creator", String.valueOf(planEnrollment.getCreator()));
		}

		if (planEnrollment.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					planEnrollment.getDateCreated()));
		}

		if (planEnrollment.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(
					planEnrollment.getDateModified()));
		}

		if (planEnrollment.getEndDate() == null) {
			map.put("endDate", null);
		}
		else {
			map.put(
				"endDate",
				liferayToJSONDateFormat.format(planEnrollment.getEndDate()));
		}

		if (planEnrollment.getEnrollmentStatus() == null) {
			map.put("enrollmentStatus", null);
		}
		else {
			map.put(
				"enrollmentStatus",
				String.valueOf(planEnrollment.getEnrollmentStatus()));
		}

		if (planEnrollment.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(planEnrollment.getExternalReferenceCode()));
		}

		if (planEnrollment.getGroupNumber() == null) {
			map.put("groupNumber", null);
		}
		else {
			map.put(
				"groupNumber", String.valueOf(planEnrollment.getGroupNumber()));
		}

		if (planEnrollment.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(planEnrollment.getId()));
		}

		if (planEnrollment.getInsurancePlanERC() == null) {
			map.put("insurancePlanERC", null);
		}
		else {
			map.put(
				"insurancePlanERC",
				String.valueOf(planEnrollment.getInsurancePlanERC()));
		}

		if (planEnrollment.getInsurancePlanId() == null) {
			map.put("insurancePlanId", null);
		}
		else {
			map.put(
				"insurancePlanId",
				String.valueOf(planEnrollment.getInsurancePlanId()));
		}

		if (planEnrollment.getMember() == null) {
			map.put("member", null);
		}
		else {
			map.put("member", String.valueOf(planEnrollment.getMember()));
		}

		if (planEnrollment.getMemberId() == null) {
			map.put("memberId", null);
		}
		else {
			map.put("memberId", String.valueOf(planEnrollment.getMemberId()));
		}

		if (planEnrollment.getNotes() == null) {
			map.put("notes", null);
		}
		else {
			map.put("notes", String.valueOf(planEnrollment.getNotes()));
		}

		if (planEnrollment.getSiteId() == null) {
			map.put("siteId", null);
		}
		else {
			map.put("siteId", String.valueOf(planEnrollment.getSiteId()));
		}

		if (planEnrollment.getStartDate() == null) {
			map.put("startDate", null);
		}
		else {
			map.put(
				"startDate",
				liferayToJSONDateFormat.format(planEnrollment.getStartDate()));
		}

		if (planEnrollment.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put("status", String.valueOf(planEnrollment.getStatus()));
		}

		return map;
	}

	public static class PlanEnrollmentJSONParser
		extends BaseJSONParser<PlanEnrollment> {

		@Override
		protected PlanEnrollment createDTO() {
			return new PlanEnrollment();
		}

		@Override
		protected PlanEnrollment[] createDTOArray(int size) {
			return new PlanEnrollment[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
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
			else if (Objects.equals(jsonParserFieldName, "endDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "enrollmentStatus")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "groupNumber")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "insurancePlanERC")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "insurancePlanId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "member")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "memberId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "notes")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "startDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PlanEnrollment planEnrollment, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					planEnrollment.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					planEnrollment.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					planEnrollment.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					planEnrollment.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "endDate")) {
				if (jsonParserFieldValue != null) {
					planEnrollment.setEndDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "enrollmentStatus")) {
				if (jsonParserFieldValue != null) {
					planEnrollment.setEnrollmentStatus(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					planEnrollment.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "groupNumber")) {
				if (jsonParserFieldValue != null) {
					planEnrollment.setGroupNumber((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					planEnrollment.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "insurancePlanERC")) {
				if (jsonParserFieldValue != null) {
					planEnrollment.setInsurancePlanERC(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "insurancePlanId")) {
				if (jsonParserFieldValue != null) {
					planEnrollment.setInsurancePlanId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "member")) {
				if (jsonParserFieldValue != null) {
					planEnrollment.setMember(
						MemberSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "memberId")) {
				if (jsonParserFieldValue != null) {
					planEnrollment.setMemberId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "notes")) {
				if (jsonParserFieldValue != null) {
					planEnrollment.setNotes((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				if (jsonParserFieldValue != null) {
					planEnrollment.setSiteId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "startDate")) {
				if (jsonParserFieldValue != null) {
					planEnrollment.setStartDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					planEnrollment.setStatus(
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