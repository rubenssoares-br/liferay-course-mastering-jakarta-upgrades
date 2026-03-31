package com.clarityvisionsolutions.insurance.benefits.tracker.rest.client.dto.v1_0;

import com.clarityvisionsolutions.insurance.benefits.tracker.rest.client.function.UnsafeSupplier;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.client.serdes.v1_0.InsurancePlanSerDes;

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
public class InsurancePlan implements Cloneable, Serializable {

	public static InsurancePlan toDTO(String json) {
		return InsurancePlanSerDes.toDTO(json);
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

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public void setActive(
		UnsafeSupplier<Boolean, Exception> activeUnsafeSupplier) {

		try {
			active = activeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean active;

	public Long getAnnualContactsAllowanceCents() {
		return annualContactsAllowanceCents;
	}

	public void setAnnualContactsAllowanceCents(
		Long annualContactsAllowanceCents) {

		this.annualContactsAllowanceCents = annualContactsAllowanceCents;
	}

	public void setAnnualContactsAllowanceCents(
		UnsafeSupplier<Long, Exception>
			annualContactsAllowanceCentsUnsafeSupplier) {

		try {
			annualContactsAllowanceCents =
				annualContactsAllowanceCentsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long annualContactsAllowanceCents;

	public Long getAnnualExamAllowanceCents() {
		return annualExamAllowanceCents;
	}

	public void setAnnualExamAllowanceCents(Long annualExamAllowanceCents) {
		this.annualExamAllowanceCents = annualExamAllowanceCents;
	}

	public void setAnnualExamAllowanceCents(
		UnsafeSupplier<Long, Exception>
			annualExamAllowanceCentsUnsafeSupplier) {

		try {
			annualExamAllowanceCents =
				annualExamAllowanceCentsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long annualExamAllowanceCents;

	public Long getAnnualFramesAllowanceCents() {
		return annualFramesAllowanceCents;
	}

	public void setAnnualFramesAllowanceCents(Long annualFramesAllowanceCents) {
		this.annualFramesAllowanceCents = annualFramesAllowanceCents;
	}

	public void setAnnualFramesAllowanceCents(
		UnsafeSupplier<Long, Exception>
			annualFramesAllowanceCentsUnsafeSupplier) {

		try {
			annualFramesAllowanceCents =
				annualFramesAllowanceCentsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long annualFramesAllowanceCents;

	public Long getAnnualLensesAllowanceCents() {
		return annualLensesAllowanceCents;
	}

	public void setAnnualLensesAllowanceCents(Long annualLensesAllowanceCents) {
		this.annualLensesAllowanceCents = annualLensesAllowanceCents;
	}

	public void setAnnualLensesAllowanceCents(
		UnsafeSupplier<Long, Exception>
			annualLensesAllowanceCentsUnsafeSupplier) {

		try {
			annualLensesAllowanceCents =
				annualLensesAllowanceCentsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long annualLensesAllowanceCents;

	public Integer getCoveragePeriodMonths() {
		return coveragePeriodMonths;
	}

	public void setCoveragePeriodMonths(Integer coveragePeriodMonths) {
		this.coveragePeriodMonths = coveragePeriodMonths;
	}

	public void setCoveragePeriodMonths(
		UnsafeSupplier<Integer, Exception> coveragePeriodMonthsUnsafeSupplier) {

		try {
			coveragePeriodMonths = coveragePeriodMonthsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer coveragePeriodMonths;

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

	public String getPlanName() {
		return planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

	public void setPlanName(
		UnsafeSupplier<String, Exception> planNameUnsafeSupplier) {

		try {
			planName = planNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String planName;

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public void setProviderName(
		UnsafeSupplier<String, Exception> providerNameUnsafeSupplier) {

		try {
			providerName = providerNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String providerName;

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
	public InsurancePlan clone() throws CloneNotSupportedException {
		return (InsurancePlan)super.clone();
	}

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
		return InsurancePlanSerDes.toJSON(this);
	}

}