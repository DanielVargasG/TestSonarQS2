package edn.cloud.business.dto.integration;

import edn.cloud.business.dto.odata.JobRequisition;

public class SFRecUser {

	private JobRequisition jobRequisition;
	private String applicationId;
	private String lastName;
	private String firstName;

	public JobRequisition getJobRequisition() {
		return jobRequisition;
	}

	public void setJobRequisition(JobRequisition jobRequisition) {
		this.jobRequisition = jobRequisition;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	

	public SFRecUser() {

	}


}
