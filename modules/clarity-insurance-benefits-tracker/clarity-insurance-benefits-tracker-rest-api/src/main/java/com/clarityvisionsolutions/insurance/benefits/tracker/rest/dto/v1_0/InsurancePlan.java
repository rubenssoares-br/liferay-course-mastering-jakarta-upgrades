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

import jakarta.validation.Valid;

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
@GraphQLName("InsurancePlan")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "InsurancePlan")
public class InsurancePlan implements Serializable {

	public static InsurancePlan toDTO(String json) {
		return ObjectMapperUtil.readValue(InsurancePlan.class, json);
	}

	public static InsurancePlan unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(InsurancePlan.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, Map<String, String>> getActions() {
		if (_actionsSupplier != null) {
			actions = _actionsSupplier.get();

			_actionsSupplier = null;
		}

		return actions;
	}

	public void setActions(Map<String, Map<String, String>> actions) {
		this.actions = actions;

		_actionsSupplier = null;
	}

	@JsonIgnore
	public void setActions(
		UnsafeSupplier<Map<String, Map<String, String>>, Exception>
			actionsUnsafeSupplier) {

		_actionsSupplier = () -> {
			try {
				return actionsUnsafeSupplier.get();
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
	protected Map<String, Map<String, String>> actions;

	@JsonIgnore
	private Supplier<Map<String, Map<String, String>>> _actionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getActive() {
		if (_activeSupplier != null) {
			active = _activeSupplier.get();

			_activeSupplier = null;
		}

		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;

		_activeSupplier = null;
	}

	@JsonIgnore
	public void setActive(
		UnsafeSupplier<Boolean, Exception> activeUnsafeSupplier) {

		_activeSupplier = () -> {
			try {
				return activeUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean active;

	@JsonIgnore
	private Supplier<Boolean> _activeSupplier;

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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long annualLensesAllowanceCents;

	@JsonIgnore
	private Supplier<Long> _annualLensesAllowanceCentsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Integer getCoveragePeriodMonths() {
		if (_coveragePeriodMonthsSupplier != null) {
			coveragePeriodMonths = _coveragePeriodMonthsSupplier.get();

			_coveragePeriodMonthsSupplier = null;
		}

		return coveragePeriodMonths;
	}

	public void setCoveragePeriodMonths(Integer coveragePeriodMonths) {
		this.coveragePeriodMonths = coveragePeriodMonths;

		_coveragePeriodMonthsSupplier = null;
	}

	@JsonIgnore
	public void setCoveragePeriodMonths(
		UnsafeSupplier<Integer, Exception> coveragePeriodMonthsUnsafeSupplier) {

		_coveragePeriodMonthsSupplier = () -> {
			try {
				return coveragePeriodMonthsUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer coveragePeriodMonths;

	@JsonIgnore
	private Supplier<Integer> _coveragePeriodMonthsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The role's creator."
	)
	@Valid
	public Creator getCreator() {
		if (_creatorSupplier != null) {
			creator = _creatorSupplier.get();

			_creatorSupplier = null;
		}

		return creator;
	}

	public void setCreator(Creator creator) {
		this.creator = creator;

		_creatorSupplier = null;
	}

	@JsonIgnore
	public void setCreator(
		UnsafeSupplier<Creator, Exception> creatorUnsafeSupplier) {

		_creatorSupplier = () -> {
			try {
				return creatorUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The role's creator.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Creator creator;

	@JsonIgnore
	private Supplier<Creator> _creatorSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Date getDateCreated() {
		if (_dateCreatedSupplier != null) {
			dateCreated = _dateCreatedSupplier.get();

			_dateCreatedSupplier = null;
		}

		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;

		_dateCreatedSupplier = null;
	}

	@JsonIgnore
	public void setDateCreated(
		UnsafeSupplier<Date, Exception> dateCreatedUnsafeSupplier) {

		_dateCreatedSupplier = () -> {
			try {
				return dateCreatedUnsafeSupplier.get();
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
	protected Date dateCreated;

	@JsonIgnore
	private Supplier<Date> _dateCreatedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Date getDateModified() {
		if (_dateModifiedSupplier != null) {
			dateModified = _dateModifiedSupplier.get();

			_dateModifiedSupplier = null;
		}

		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;

		_dateModifiedSupplier = null;
	}

	@JsonIgnore
	public void setDateModified(
		UnsafeSupplier<Date, Exception> dateModifiedUnsafeSupplier) {

		_dateModifiedSupplier = () -> {
			try {
				return dateModifiedUnsafeSupplier.get();
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
	protected Date dateModified;

	@JsonIgnore
	private Supplier<Date> _dateModifiedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getExternalReferenceCode() {
		if (_externalReferenceCodeSupplier != null) {
			externalReferenceCode = _externalReferenceCodeSupplier.get();

			_externalReferenceCodeSupplier = null;
		}

		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;

		_externalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		_externalReferenceCodeSupplier = () -> {
			try {
				return externalReferenceCodeUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getId() {
		if (_idSupplier != null) {
			id = _idSupplier.get();

			_idSupplier = null;
		}

		return id;
	}

	public void setId(Long id) {
		this.id = id;

		_idSupplier = null;
	}

	@JsonIgnore
	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		_idSupplier = () -> {
			try {
				return idUnsafeSupplier.get();
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
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String providerName;

	@JsonIgnore
	private Supplier<String> _providerNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The ID of the site to which this insurance plan is scoped."
	)
	public Long getSiteId() {
		if (_siteIdSupplier != null) {
			siteId = _siteIdSupplier.get();

			_siteIdSupplier = null;
		}

		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;

		_siteIdSupplier = null;
	}

	@JsonIgnore
	public void setSiteId(
		UnsafeSupplier<Long, Exception> siteIdUnsafeSupplier) {

		_siteIdSupplier = () -> {
			try {
				return siteIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "The ID of the site to which this insurance plan is scoped."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long siteId;

	@JsonIgnore
	private Supplier<Long> _siteIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Integer getStatus() {
		if (_statusSupplier != null) {
			status = _statusSupplier.get();

			_statusSupplier = null;
		}

		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;

		_statusSupplier = null;
	}

	@JsonIgnore
	public void setStatus(
		UnsafeSupplier<Integer, Exception> statusUnsafeSupplier) {

		_statusSupplier = () -> {
			try {
				return statusUnsafeSupplier.get();
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
	protected Integer status;

	@JsonIgnore
	private Supplier<Integer> _statusSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof InsurancePlan)) {
			return false;
		}

		InsurancePlan insurancePlan = (InsurancePlan)object;

		return Objects.equals(toString(), insurancePlan.toString());
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

		Map<String, Map<String, String>> actions = getActions();

		if (actions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(actions));
		}

		Boolean active = getActive();

		if (active != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"active\": ");

			sb.append(active);
		}

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

		Integer coveragePeriodMonths = getCoveragePeriodMonths();

		if (coveragePeriodMonths != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"coveragePeriodMonths\": ");

			sb.append(coveragePeriodMonths);
		}

		Creator creator = getCreator();

		if (creator != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(String.valueOf(creator));
		}

		Date dateCreated = getDateCreated();

		if (dateCreated != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(dateCreated));

			sb.append("\"");
		}

		Date dateModified = getDateModified();

		if (dateModified != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(dateModified));

			sb.append("\"");
		}

		String externalReferenceCode = getExternalReferenceCode();

		if (externalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(externalReferenceCode));

			sb.append("\"");
		}

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
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

		Long siteId = getSiteId();

		if (siteId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteId\": ");

			sb.append(siteId);
		}

		Integer status = getStatus();

		if (status != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append(status);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.clarityvisionsolutions.insurance.benefits.tracker.rest.dto.v1_0.InsurancePlan",
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