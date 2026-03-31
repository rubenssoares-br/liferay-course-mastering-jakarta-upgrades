package com.clarityvisionsolutions.insurance.benefits.tracker.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author dnebinger
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Aggregated benefit usage details for a plan enrollment, including allowance caps from the insurance plan and total consumed amounts per benefit category.",
	value = "BenefitUsageDetails"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "BenefitUsageDetails")
public class BenefitUsageDetails implements Serializable {

	public static BenefitUsageDetails toDTO(String json) {
		return ObjectMapperUtil.readValue(BenefitUsageDetails.class, json);
	}

	public static BenefitUsageDetails unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			BenefitUsageDetails.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getAnnualContactsAllowanceCents() {
		if (_annualContactsAllowanceCentsSupplier != null) {
			annualContactsAllowanceCents =
				_annualContactsAllowanceCentsSupplier.get();

			_annualContactsAllowanceCentsSupplier = null;
		}

		return annualContactsAllowanceCents;
	}

	public void setAnnualContactsAllowanceCents(
		Long annualContactsAllowanceCents) {

		this.annualContactsAllowanceCents = annualContactsAllowanceCents;

		_annualContactsAllowanceCentsSupplier = null;
	}

	@JsonIgnore
	public void setAnnualContactsAllowanceCents(
		UnsafeSupplier<Long, Exception>
			annualContactsAllowanceCentsUnsafeSupplier) {

		_annualContactsAllowanceCentsSupplier = () -> {
			try {
				return annualContactsAllowanceCentsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long annualContactsAllowanceCents;

	@JsonIgnore
	private Supplier<Long> _annualContactsAllowanceCentsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getAnnualExamAllowanceCents() {
		if (_annualExamAllowanceCentsSupplier != null) {
			annualExamAllowanceCents = _annualExamAllowanceCentsSupplier.get();

			_annualExamAllowanceCentsSupplier = null;
		}

		return annualExamAllowanceCents;
	}

	public void setAnnualExamAllowanceCents(Long annualExamAllowanceCents) {
		this.annualExamAllowanceCents = annualExamAllowanceCents;

		_annualExamAllowanceCentsSupplier = null;
	}

	@JsonIgnore
	public void setAnnualExamAllowanceCents(
		UnsafeSupplier<Long, Exception>
			annualExamAllowanceCentsUnsafeSupplier) {

		_annualExamAllowanceCentsSupplier = () -> {
			try {
				return annualExamAllowanceCentsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long annualExamAllowanceCents;

	@JsonIgnore
	private Supplier<Long> _annualExamAllowanceCentsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getAnnualFramesAllowanceCents() {
		if (_annualFramesAllowanceCentsSupplier != null) {
			annualFramesAllowanceCents =
				_annualFramesAllowanceCentsSupplier.get();

			_annualFramesAllowanceCentsSupplier = null;
		}

		return annualFramesAllowanceCents;
	}

	public void setAnnualFramesAllowanceCents(Long annualFramesAllowanceCents) {
		this.annualFramesAllowanceCents = annualFramesAllowanceCents;

		_annualFramesAllowanceCentsSupplier = null;
	}

	@JsonIgnore
	public void setAnnualFramesAllowanceCents(
		UnsafeSupplier<Long, Exception>
			annualFramesAllowanceCentsUnsafeSupplier) {

		_annualFramesAllowanceCentsSupplier = () -> {
			try {
				return annualFramesAllowanceCentsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long annualFramesAllowanceCents;

	@JsonIgnore
	private Supplier<Long> _annualFramesAllowanceCentsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getAnnualLensesAllowanceCents() {
		if (_annualLensesAllowanceCentsSupplier != null) {
			annualLensesAllowanceCents =
				_annualLensesAllowanceCentsSupplier.get();

			_annualLensesAllowanceCentsSupplier = null;
		}

		return annualLensesAllowanceCents;
	}

	public void setAnnualLensesAllowanceCents(Long annualLensesAllowanceCents) {
		this.annualLensesAllowanceCents = annualLensesAllowanceCents;

		_annualLensesAllowanceCentsSupplier = null;
	}

	@JsonIgnore
	public void setAnnualLensesAllowanceCents(
		UnsafeSupplier<Long, Exception>
			annualLensesAllowanceCentsUnsafeSupplier) {

		_annualLensesAllowanceCentsSupplier = () -> {
			try {
				return annualLensesAllowanceCentsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long annualLensesAllowanceCents;

	@JsonIgnore
	private Supplier<Long> _annualLensesAllowanceCentsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getContactsUsedCents() {
		if (_contactsUsedCentsSupplier != null) {
			contactsUsedCents = _contactsUsedCentsSupplier.get();

			_contactsUsedCentsSupplier = null;
		}

		return contactsUsedCents;
	}

	public void setContactsUsedCents(Long contactsUsedCents) {
		this.contactsUsedCents = contactsUsedCents;

		_contactsUsedCentsSupplier = null;
	}

	@JsonIgnore
	public void setContactsUsedCents(
		UnsafeSupplier<Long, Exception> contactsUsedCentsUnsafeSupplier) {

		_contactsUsedCentsSupplier = () -> {
			try {
				return contactsUsedCentsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long contactsUsedCents;

	@JsonIgnore
	private Supplier<Long> _contactsUsedCentsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Date getEndDate() {
		if (_endDateSupplier != null) {
			endDate = _endDateSupplier.get();

			_endDateSupplier = null;
		}

		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;

		_endDateSupplier = null;
	}

	@JsonIgnore
	public void setEndDate(
		UnsafeSupplier<Date, Exception> endDateUnsafeSupplier) {

		_endDateSupplier = () -> {
			try {
				return endDateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date endDate;

	@JsonIgnore
	private Supplier<Date> _endDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Integer getEnrollmentStatus() {
		if (_enrollmentStatusSupplier != null) {
			enrollmentStatus = _enrollmentStatusSupplier.get();

			_enrollmentStatusSupplier = null;
		}

		return enrollmentStatus;
	}

	public void setEnrollmentStatus(Integer enrollmentStatus) {
		this.enrollmentStatus = enrollmentStatus;

		_enrollmentStatusSupplier = null;
	}

	@JsonIgnore
	public void setEnrollmentStatus(
		UnsafeSupplier<Integer, Exception> enrollmentStatusUnsafeSupplier) {

		_enrollmentStatusSupplier = () -> {
			try {
				return enrollmentStatusUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Integer enrollmentStatus;

	@JsonIgnore
	private Supplier<Integer> _enrollmentStatusSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getExamUsedCents() {
		if (_examUsedCentsSupplier != null) {
			examUsedCents = _examUsedCentsSupplier.get();

			_examUsedCentsSupplier = null;
		}

		return examUsedCents;
	}

	public void setExamUsedCents(Long examUsedCents) {
		this.examUsedCents = examUsedCents;

		_examUsedCentsSupplier = null;
	}

	@JsonIgnore
	public void setExamUsedCents(
		UnsafeSupplier<Long, Exception> examUsedCentsUnsafeSupplier) {

		_examUsedCentsSupplier = () -> {
			try {
				return examUsedCentsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long examUsedCents;

	@JsonIgnore
	private Supplier<Long> _examUsedCentsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getFramesUsedCents() {
		if (_framesUsedCentsSupplier != null) {
			framesUsedCents = _framesUsedCentsSupplier.get();

			_framesUsedCentsSupplier = null;
		}

		return framesUsedCents;
	}

	public void setFramesUsedCents(Long framesUsedCents) {
		this.framesUsedCents = framesUsedCents;

		_framesUsedCentsSupplier = null;
	}

	@JsonIgnore
	public void setFramesUsedCents(
		UnsafeSupplier<Long, Exception> framesUsedCentsUnsafeSupplier) {

		_framesUsedCentsSupplier = () -> {
			try {
				return framesUsedCentsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long framesUsedCents;

	@JsonIgnore
	private Supplier<Long> _framesUsedCentsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getInsurancePlanId() {
		if (_insurancePlanIdSupplier != null) {
			insurancePlanId = _insurancePlanIdSupplier.get();

			_insurancePlanIdSupplier = null;
		}

		return insurancePlanId;
	}

	public void setInsurancePlanId(Long insurancePlanId) {
		this.insurancePlanId = insurancePlanId;

		_insurancePlanIdSupplier = null;
	}

	@JsonIgnore
	public void setInsurancePlanId(
		UnsafeSupplier<Long, Exception> insurancePlanIdUnsafeSupplier) {

		_insurancePlanIdSupplier = () -> {
			try {
				return insurancePlanIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long insurancePlanId;

	@JsonIgnore
	private Supplier<Long> _insurancePlanIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getLensesUsedCents() {
		if (_lensesUsedCentsSupplier != null) {
			lensesUsedCents = _lensesUsedCentsSupplier.get();

			_lensesUsedCentsSupplier = null;
		}

		return lensesUsedCents;
	}

	public void setLensesUsedCents(Long lensesUsedCents) {
		this.lensesUsedCents = lensesUsedCents;

		_lensesUsedCentsSupplier = null;
	}

	@JsonIgnore
	public void setLensesUsedCents(
		UnsafeSupplier<Long, Exception> lensesUsedCentsUnsafeSupplier) {

		_lensesUsedCentsSupplier = () -> {
			try {
				return lensesUsedCentsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long lensesUsedCents;

	@JsonIgnore
	private Supplier<Long> _lensesUsedCentsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getPlanEnrollmentId() {
		if (_planEnrollmentIdSupplier != null) {
			planEnrollmentId = _planEnrollmentIdSupplier.get();

			_planEnrollmentIdSupplier = null;
		}

		return planEnrollmentId;
	}

	public void setPlanEnrollmentId(Long planEnrollmentId) {
		this.planEnrollmentId = planEnrollmentId;

		_planEnrollmentIdSupplier = null;
	}

	@JsonIgnore
	public void setPlanEnrollmentId(
		UnsafeSupplier<Long, Exception> planEnrollmentIdUnsafeSupplier) {

		_planEnrollmentIdSupplier = () -> {
			try {
				return planEnrollmentIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long planEnrollmentId;

	@JsonIgnore
	private Supplier<Long> _planEnrollmentIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getPlanName() {
		if (_planNameSupplier != null) {
			planName = _planNameSupplier.get();

			_planNameSupplier = null;
		}

		return planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;

		_planNameSupplier = null;
	}

	@JsonIgnore
	public void setPlanName(
		UnsafeSupplier<String, Exception> planNameUnsafeSupplier) {

		_planNameSupplier = () -> {
			try {
				return planNameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String planName;

	@JsonIgnore
	private Supplier<String> _planNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getProviderName() {
		if (_providerNameSupplier != null) {
			providerName = _providerNameSupplier.get();

			_providerNameSupplier = null;
		}

		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;

		_providerNameSupplier = null;
	}

	@JsonIgnore
	public void setProviderName(
		UnsafeSupplier<String, Exception> providerNameUnsafeSupplier) {

		_providerNameSupplier = () -> {
			try {
				return providerNameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String providerName;

	@JsonIgnore
	private Supplier<String> _providerNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Date getStartDate() {
		if (_startDateSupplier != null) {
			startDate = _startDateSupplier.get();

			_startDateSupplier = null;
		}

		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;

		_startDateSupplier = null;
	}

	@JsonIgnore
	public void setStartDate(
		UnsafeSupplier<Date, Exception> startDateUnsafeSupplier) {

		_startDateSupplier = () -> {
			try {
				return startDateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date startDate;

	@JsonIgnore
	private Supplier<Date> _startDateSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof BenefitUsageDetails)) {
			return false;
		}

		BenefitUsageDetails benefitUsageDetails = (BenefitUsageDetails)object;

		return Objects.equals(toString(), benefitUsageDetails.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		Long annualContactsAllowanceCents = getAnnualContactsAllowanceCents();

		if (annualContactsAllowanceCents != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"annualContactsAllowanceCents\": ");

			sb.append(annualContactsAllowanceCents);
		}

		Long annualExamAllowanceCents = getAnnualExamAllowanceCents();

		if (annualExamAllowanceCents != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"annualExamAllowanceCents\": ");

			sb.append(annualExamAllowanceCents);
		}

		Long annualFramesAllowanceCents = getAnnualFramesAllowanceCents();

		if (annualFramesAllowanceCents != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"annualFramesAllowanceCents\": ");

			sb.append(annualFramesAllowanceCents);
		}

		Long annualLensesAllowanceCents = getAnnualLensesAllowanceCents();

		if (annualLensesAllowanceCents != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"annualLensesAllowanceCents\": ");

			sb.append(annualLensesAllowanceCents);
		}

		Long contactsUsedCents = getContactsUsedCents();

		if (contactsUsedCents != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contactsUsedCents\": ");

			sb.append(contactsUsedCents);
		}

		Date endDate = getEndDate();

		if (endDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"endDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(endDate));

			sb.append("\"");
		}

		Integer enrollmentStatus = getEnrollmentStatus();

		if (enrollmentStatus != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"enrollmentStatus\": ");

			sb.append(enrollmentStatus);
		}

		Long examUsedCents = getExamUsedCents();

		if (examUsedCents != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"examUsedCents\": ");

			sb.append(examUsedCents);
		}

		Long framesUsedCents = getFramesUsedCents();

		if (framesUsedCents != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"framesUsedCents\": ");

			sb.append(framesUsedCents);
		}

		Long insurancePlanId = getInsurancePlanId();

		if (insurancePlanId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"insurancePlanId\": ");

			sb.append(insurancePlanId);
		}

		Long lensesUsedCents = getLensesUsedCents();

		if (lensesUsedCents != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"lensesUsedCents\": ");

			sb.append(lensesUsedCents);
		}

		Long planEnrollmentId = getPlanEnrollmentId();

		if (planEnrollmentId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"planEnrollmentId\": ");

			sb.append(planEnrollmentId);
		}

		String planName = getPlanName();

		if (planName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"planName\": ");

			sb.append("\"");

			sb.append(_escape(planName));

			sb.append("\"");
		}

		String providerName = getProviderName();

		if (providerName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"providerName\": ");

			sb.append("\"");

			sb.append(_escape(providerName));

			sb.append("\"");
		}

		Date startDate = getStartDate();

		if (startDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"startDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(startDate));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.clarityvisionsolutions.insurance.benefits.tracker.rest.dto.v1_0.BenefitUsageDetails",
		name = "x-class-name"
	)
	public String xClassName;

	private static String _escape(Object object) {
		return StringUtil.replace(
			String.valueOf(object), _JSON_ESCAPE_STRINGS[0],
			_JSON_ESCAPE_STRINGS[1]);
	}

	private static boolean _isArray(Object value) {
		if (value == null) {
			return false;
		}

		Class<?> clazz = value.getClass();

		return clazz.isArray();
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
			sb.append(_escape(entry.getKey()));
			sb.append("\": ");

			Object value = entry.getValue();

			if (_isArray(value)) {
				sb.append("[");

				Object[] valueArray = (Object[])value;

				for (int i = 0; i < valueArray.length; i++) {
					if (valueArray[i] instanceof Map) {
						sb.append(_toJSON((Map<String, ?>)valueArray[i]));
					}
					else if (valueArray[i] instanceof String) {
						sb.append("\"");
						sb.append(valueArray[i]);
						sb.append("\"");
					}
					else {
						sb.append(valueArray[i]);
					}

					if ((i + 1) < valueArray.length) {
						sb.append(", ");
					}
				}

				sb.append("]");
			}
			else if (value instanceof Map) {
				sb.append(_toJSON((Map<String, ?>)value));
			}
			else if (value instanceof String) {
				sb.append("\"");
				sb.append(_escape(value));
				sb.append("\"");
			}
			else {
				sb.append(value);
			}

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}