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
@GraphQLName("BenefitUsage")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "BenefitUsage")
public class BenefitUsage implements Serializable {

	public static BenefitUsage toDTO(String json) {
		return ObjectMapperUtil.readValue(BenefitUsage.class, json);
	}

	public static BenefitUsage unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(BenefitUsage.class, json);
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
	public Long getAmountUsedCents() {
		if (_amountUsedCentsSupplier != null) {
			amountUsedCents = _amountUsedCentsSupplier.get();

			_amountUsedCentsSupplier = null;
		}

		return amountUsedCents;
	}

	public void setAmountUsedCents(Long amountUsedCents) {
		this.amountUsedCents = amountUsedCents;

		_amountUsedCentsSupplier = null;
	}

	@JsonIgnore
	public void setAmountUsedCents(
		UnsafeSupplier<Long, Exception> amountUsedCentsUnsafeSupplier) {

		_amountUsedCentsSupplier = () -> {
			try {
				return amountUsedCentsUnsafeSupplier.get();
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
	protected Long amountUsedCents;

	@JsonIgnore
	private Supplier<Long> _amountUsedCentsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getBenefitType() {
		if (_benefitTypeSupplier != null) {
			benefitType = _benefitTypeSupplier.get();

			_benefitTypeSupplier = null;
		}

		return benefitType;
	}

	public void setBenefitType(String benefitType) {
		this.benefitType = benefitType;

		_benefitTypeSupplier = null;
	}

	@JsonIgnore
	public void setBenefitType(
		UnsafeSupplier<String, Exception> benefitTypeUnsafeSupplier) {

		_benefitTypeSupplier = () -> {
			try {
				return benefitTypeUnsafeSupplier.get();
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
	protected String benefitType;

	@JsonIgnore
	private Supplier<String> _benefitTypeSupplier;

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
	public String getNotes() {
		if (_notesSupplier != null) {
			notes = _notesSupplier.get();

			_notesSupplier = null;
		}

		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;

		_notesSupplier = null;
	}

	@JsonIgnore
	public void setNotes(
		UnsafeSupplier<String, Exception> notesUnsafeSupplier) {

		_notesSupplier = () -> {
			try {
				return notesUnsafeSupplier.get();
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
	protected String notes;

	@JsonIgnore
	private Supplier<String> _notesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getPlanEnrollmentERC() {
		if (_planEnrollmentERCSupplier != null) {
			planEnrollmentERC = _planEnrollmentERCSupplier.get();

			_planEnrollmentERCSupplier = null;
		}

		return planEnrollmentERC;
	}

	public void setPlanEnrollmentERC(String planEnrollmentERC) {
		this.planEnrollmentERC = planEnrollmentERC;

		_planEnrollmentERCSupplier = null;
	}

	@JsonIgnore
	public void setPlanEnrollmentERC(
		UnsafeSupplier<String, Exception> planEnrollmentERCUnsafeSupplier) {

		_planEnrollmentERCSupplier = () -> {
			try {
				return planEnrollmentERCUnsafeSupplier.get();
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
	protected String planEnrollmentERC;

	@JsonIgnore
	private Supplier<String> _planEnrollmentERCSupplier;

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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long planEnrollmentId;

	@JsonIgnore
	private Supplier<Long> _planEnrollmentIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getReference() {
		if (_referenceSupplier != null) {
			reference = _referenceSupplier.get();

			_referenceSupplier = null;
		}

		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;

		_referenceSupplier = null;
	}

	@JsonIgnore
	public void setReference(
		UnsafeSupplier<String, Exception> referenceUnsafeSupplier) {

		_referenceSupplier = () -> {
			try {
				return referenceUnsafeSupplier.get();
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
	protected String reference;

	@JsonIgnore
	private Supplier<String> _referenceSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Date getServiceDate() {
		if (_serviceDateSupplier != null) {
			serviceDate = _serviceDateSupplier.get();

			_serviceDateSupplier = null;
		}

		return serviceDate;
	}

	public void setServiceDate(Date serviceDate) {
		this.serviceDate = serviceDate;

		_serviceDateSupplier = null;
	}

	@JsonIgnore
	public void setServiceDate(
		UnsafeSupplier<Date, Exception> serviceDateUnsafeSupplier) {

		_serviceDateSupplier = () -> {
			try {
				return serviceDateUnsafeSupplier.get();
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
	protected Date serviceDate;

	@JsonIgnore
	private Supplier<Date> _serviceDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The ID of the site to which this benefit usage is scoped."
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
		description = "The ID of the site to which this benefit usage is scoped."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long siteId;

	@JsonIgnore
	private Supplier<Long> _siteIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getSourceReference() {
		if (_sourceReferenceSupplier != null) {
			sourceReference = _sourceReferenceSupplier.get();

			_sourceReferenceSupplier = null;
		}

		return sourceReference;
	}

	public void setSourceReference(String sourceReference) {
		this.sourceReference = sourceReference;

		_sourceReferenceSupplier = null;
	}

	@JsonIgnore
	public void setSourceReference(
		UnsafeSupplier<String, Exception> sourceReferenceUnsafeSupplier) {

		_sourceReferenceSupplier = () -> {
			try {
				return sourceReferenceUnsafeSupplier.get();
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
	protected String sourceReference;

	@JsonIgnore
	private Supplier<String> _sourceReferenceSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getSourceType() {
		if (_sourceTypeSupplier != null) {
			sourceType = _sourceTypeSupplier.get();

			_sourceTypeSupplier = null;
		}

		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;

		_sourceTypeSupplier = null;
	}

	@JsonIgnore
	public void setSourceType(
		UnsafeSupplier<String, Exception> sourceTypeUnsafeSupplier) {

		_sourceTypeSupplier = () -> {
			try {
				return sourceTypeUnsafeSupplier.get();
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
	protected String sourceType;

	@JsonIgnore
	private Supplier<String> _sourceTypeSupplier;

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

		if (!(object instanceof BenefitUsage)) {
			return false;
		}

		BenefitUsage benefitUsage = (BenefitUsage)object;

		return Objects.equals(toString(), benefitUsage.toString());
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

		Long amountUsedCents = getAmountUsedCents();

		if (amountUsedCents != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"amountUsedCents\": ");

			sb.append(amountUsedCents);
		}

		String benefitType = getBenefitType();

		if (benefitType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"benefitType\": ");

			sb.append("\"");

			sb.append(_escape(benefitType));

			sb.append("\"");
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

		String notes = getNotes();

		if (notes != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"notes\": ");

			sb.append("\"");

			sb.append(_escape(notes));

			sb.append("\"");
		}

		String planEnrollmentERC = getPlanEnrollmentERC();

		if (planEnrollmentERC != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"planEnrollmentERC\": ");

			sb.append("\"");

			sb.append(_escape(planEnrollmentERC));

			sb.append("\"");
		}

		Long planEnrollmentId = getPlanEnrollmentId();

		if (planEnrollmentId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"planEnrollmentId\": ");

			sb.append(planEnrollmentId);
		}

		String reference = getReference();

		if (reference != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"reference\": ");

			sb.append("\"");

			sb.append(_escape(reference));

			sb.append("\"");
		}

		Date serviceDate = getServiceDate();

		if (serviceDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"serviceDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(serviceDate));

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

		String sourceReference = getSourceReference();

		if (sourceReference != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sourceReference\": ");

			sb.append("\"");

			sb.append(_escape(sourceReference));

			sb.append("\"");
		}

		String sourceType = getSourceType();

		if (sourceType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sourceType\": ");

			sb.append("\"");

			sb.append(_escape(sourceType));

			sb.append("\"");
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
		defaultValue = "com.clarityvisionsolutions.insurance.benefits.tracker.rest.dto.v1_0.BenefitUsage",
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