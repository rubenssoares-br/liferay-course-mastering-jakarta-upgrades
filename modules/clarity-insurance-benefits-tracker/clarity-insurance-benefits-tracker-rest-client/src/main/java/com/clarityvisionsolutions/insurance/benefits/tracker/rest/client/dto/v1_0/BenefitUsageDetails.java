package com.clarityvisionsolutions.insurance.benefits.tracker.rest.client.dto.v1_0;

import com.clarityvisionsolutions.insurance.benefits.tracker.rest.client.function.UnsafeSupplier;
import com.clarityvisionsolutions.insurance.benefits.tracker.rest.client.serdes.v1_0.BenefitUsageDetailsSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Date;
import java.util.Objects;

/**
 * @author dnebinger
 * @generated
 */
@Generated("")
public class BenefitUsageDetails implements Cloneable, Serializable {

	public static BenefitUsageDetails toDTO(String json) {
		return BenefitUsageDetailsSerDes.toDTO(json);
	}

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

	public Long getContactsUsedCents() {
		return contactsUsedCents;
	}

	public void setContactsUsedCents(Long contactsUsedCents) {
		this.contactsUsedCents = contactsUsedCents;
	}

	public void setContactsUsedCents(
		UnsafeSupplier<Long, Exception> contactsUsedCentsUnsafeSupplier) {

		try {
			contactsUsedCents = contactsUsedCentsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long contactsUsedCents;

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setEndDate(
		UnsafeSupplier<Date, Exception> endDateUnsafeSupplier) {

		try {
			endDate = endDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date endDate;

	public Integer getEnrollmentStatus() {
		return enrollmentStatus;
	}

	public void setEnrollmentStatus(Integer enrollmentStatus) {
		this.enrollmentStatus = enrollmentStatus;
	}

	public void setEnrollmentStatus(
		UnsafeSupplier<Integer, Exception> enrollmentStatusUnsafeSupplier) {

		try {
			enrollmentStatus = enrollmentStatusUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer enrollmentStatus;

	public Long getExamUsedCents() {
		return examUsedCents;
	}

	public void setExamUsedCents(Long examUsedCents) {
		this.examUsedCents = examUsedCents;
	}

	public void setExamUsedCents(
		UnsafeSupplier<Long, Exception> examUsedCentsUnsafeSupplier) {

		try {
			examUsedCents = examUsedCentsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long examUsedCents;

	public Long getFramesUsedCents() {
		return framesUsedCents;
	}

	public void setFramesUsedCents(Long framesUsedCents) {
		this.framesUsedCents = framesUsedCents;
	}

	public void setFramesUsedCents(
		UnsafeSupplier<Long, Exception> framesUsedCentsUnsafeSupplier) {

		try {
			framesUsedCents = framesUsedCentsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long framesUsedCents;

	public Long getInsurancePlanId() {
		return insurancePlanId;
	}

	public void setInsurancePlanId(Long insurancePlanId) {
		this.insurancePlanId = insurancePlanId;
	}

	public void setInsurancePlanId(
		UnsafeSupplier<Long, Exception> insurancePlanIdUnsafeSupplier) {

		try {
			insurancePlanId = insurancePlanIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long insurancePlanId;

	public Long getLensesUsedCents() {
		return lensesUsedCents;
	}

	public void setLensesUsedCents(Long lensesUsedCents) {
		this.lensesUsedCents = lensesUsedCents;
	}

	public void setLensesUsedCents(
		UnsafeSupplier<Long, Exception> lensesUsedCentsUnsafeSupplier) {

		try {
			lensesUsedCents = lensesUsedCentsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long lensesUsedCents;

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

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setStartDate(
		UnsafeSupplier<Date, Exception> startDateUnsafeSupplier) {

		try {
			startDate = startDateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date startDate;

	@Override
	public BenefitUsageDetails clone() throws CloneNotSupportedException {
		return (BenefitUsageDetails)super.clone();
	}

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
		return BenefitUsageDetailsSerDes.toJSON(this);
	}

}