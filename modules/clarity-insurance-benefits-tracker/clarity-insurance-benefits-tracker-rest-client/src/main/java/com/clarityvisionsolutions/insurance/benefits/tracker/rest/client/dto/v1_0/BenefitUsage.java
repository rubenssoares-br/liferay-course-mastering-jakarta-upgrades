package com.clarityvisionsolutions.insurance.benefits.tracker.rest.client.dto.v1_0;

import com.clarityvisionsolutions.insurance.benefits.tracker.rest.client.function.UnsafeSupplier;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.client.serdes.v1_0.BenefitUsageSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * @author dnebinger
 * @generated
 */
@Generated("")
public class BenefitUsage implements Cloneable, Serializable {

	public static BenefitUsage toDTO(String json) {
		return BenefitUsageSerDes.toDTO(json);
	}

	public Map<String, Map<String, String>> getActions() {
		return actions;
	}

	public void setActions(Map<String, Map<String, String>> actions) {
		this.actions = actions;
	}

	public void setActions(
		UnsafeSupplier<Map<String, Map<String, String>>, Exception>
			actionsUnsafeSupplier) {

		try {
			actions = actionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, Map<String, String>> actions;

	public Long getAmountUsedCents() {
		return amountUsedCents;
	}

	public void setAmountUsedCents(Long amountUsedCents) {
		this.amountUsedCents = amountUsedCents;
	}

	public void setAmountUsedCents(
		UnsafeSupplier<Long, Exception> amountUsedCentsUnsafeSupplier) {

		try {
			amountUsedCents = amountUsedCentsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long amountUsedCents;

	public String getBenefitType() {
		return benefitType;
	}

	public void setBenefitType(String benefitType) {
		this.benefitType = benefitType;
	}

	public void setBenefitType(
		UnsafeSupplier<String, Exception> benefitTypeUnsafeSupplier) {

		try {
			benefitType = benefitTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String benefitType;

	public Creator getCreator() {
		return creator;
	}

	public void setCreator(Creator creator) {
		this.creator = creator;
	}

	public void setCreator(
		UnsafeSupplier<Creator, Exception> creatorUnsafeSupplier) {

		try {
			creator = creatorUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Creator creator;

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public void setDateCreated(
		UnsafeSupplier<Date, Exception> dateCreatedUnsafeSupplier) {

		try {
			dateCreated = dateCreatedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date dateCreated;

	public Date getDateModified() {
		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}

	public void setDateModified(
		UnsafeSupplier<Date, Exception> dateModifiedUnsafeSupplier) {

		try {
			dateModified = dateModifiedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date dateModified;

	public String getExternalReferenceCode() {
		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;
	}

	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		try {
			externalReferenceCode = externalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String externalReferenceCode;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long id;

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public void setNotes(
		UnsafeSupplier<String, Exception> notesUnsafeSupplier) {

		try {
			notes = notesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String notes;

	public String getPlanEnrollmentERC() {
		return planEnrollmentERC;
	}

	public void setPlanEnrollmentERC(String planEnrollmentERC) {
		this.planEnrollmentERC = planEnrollmentERC;
	}

	public void setPlanEnrollmentERC(
		UnsafeSupplier<String, Exception> planEnrollmentERCUnsafeSupplier) {

		try {
			planEnrollmentERC = planEnrollmentERCUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String planEnrollmentERC;

	public Long getPlanEnrollmentId() {
		return planEnrollmentId;
	}

	public void setPlanEnrollmentId(Long planEnrollmentId) {
		this.planEnrollmentId = planEnrollmentId;
	}

	public void setPlanEnrollmentId(
		UnsafeSupplier<Long, Exception> planEnrollmentIdUnsafeSupplier) {

		try {
			planEnrollmentId = planEnrollmentIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long planEnrollmentId;

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public void setReference(
		UnsafeSupplier<String, Exception> referenceUnsafeSupplier) {

		try {
			reference = referenceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String reference;

	public Date getServiceDate() {
		return serviceDate;
	}

	public void setServiceDate(Date serviceDate) {
		this.serviceDate = serviceDate;
	}

	public void setServiceDate(
		UnsafeSupplier<Date, Exception> serviceDateUnsafeSupplier) {

		try {
			serviceDate = serviceDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date serviceDate;

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public void setSiteId(
		UnsafeSupplier<Long, Exception> siteIdUnsafeSupplier) {

		try {
			siteId = siteIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long siteId;

	public String getSourceReference() {
		return sourceReference;
	}

	public void setSourceReference(String sourceReference) {
		this.sourceReference = sourceReference;
	}

	public void setSourceReference(
		UnsafeSupplier<String, Exception> sourceReferenceUnsafeSupplier) {

		try {
			sourceReference = sourceReferenceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String sourceReference;

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public void setSourceType(
		UnsafeSupplier<String, Exception> sourceTypeUnsafeSupplier) {

		try {
			sourceType = sourceTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String sourceType;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public void setStatus(
		UnsafeSupplier<Integer, Exception> statusUnsafeSupplier) {

		try {
			status = statusUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer status;

	@Override
	public BenefitUsage clone() throws CloneNotSupportedException {
		return (BenefitUsage)super.clone();
	}

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
		return BenefitUsageSerDes.toJSON(this);
	}

}