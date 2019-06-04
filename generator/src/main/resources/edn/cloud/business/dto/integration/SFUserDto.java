package edn.cloud.business.dto.integration;

import edn.cloud.business.dto.odata.AddressList;
import edn.cloud.business.dto.odata.EmailList;
import edn.cloud.business.dto.odata.EmploymentList;
import edn.cloud.business.dto.odata.PersonalInfoList;
import edn.cloud.sfactor.persistence.entities.Employee;

public class SFUserDto {

	private PersonalInfoList personalInfoNav;
	private EmploymentList employmentNav;
	private EmailList emailNav;
	private AddressList homeAddressNavDEFLT;

	private String personIdExternal;
	private String dateOfBirth;
	private String countryOfBirth;
	private String regionOfBirth;
	private String defaultLanguage;

	public SFUserDto() {

	}

	public void write(Employee user) {

		user.setUserId(this.callUserId());
		user.setFirstName(this.callFirstName());
		user.setLastName(this.callLastName());
		user.setEmail(this.callEmail());

		if (this.callTitle() == null || this.callTitle().equals("")) {
			user.setTitle(this.callPosition());
		} else {
			user.setTitle(this.callTitle());
		}
		user.setHireDate(this.callHireDate());
		user.setLocation(this.callLocation());
		user.setDefaultLanguage(this.callDefLG());
	}

	public PersonalInfoList getPersonalInfoNav() {
		return personalInfoNav;
	}

	public void setPersonalInfoNav(PersonalInfoList personalInfoNav) {
		this.personalInfoNav = personalInfoNav;
	}

	public EmploymentList getEmploymentNav() {
		return employmentNav;
	}

	public void setEmploymentNav(EmploymentList employmentNav) {
		this.employmentNav = employmentNav;
	}

	public EmailList getEmailNav() {
		return emailNav;
	}

	public void setEmailNav(EmailList emailNav) {
		this.emailNav = emailNav;
	}

	public AddressList getHomeAddressNavDEFLT() {
		return homeAddressNavDEFLT;
	}

	public void setHomeAddressNavDEFLT(AddressList homeAddressNavDEFLT) {
		this.homeAddressNavDEFLT = homeAddressNavDEFLT;
	}

	public String getPersonIdExternal() {
		return personIdExternal;
	}

	public void setPersonIdExternal(String personIdExternal) {
		this.personIdExternal = personIdExternal;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getCountryOfBirth() {
		return countryOfBirth;
	}

	public void setCountryOfBirth(String countryOfBirth) {
		this.countryOfBirth = countryOfBirth;
	}

	public String getRegionOfBirth() {
		return regionOfBirth;
	}

	public void setRegionOfBirth(String regionOfBirth) {
		this.regionOfBirth = regionOfBirth;
	}

	public String callFirstName() {
		if (this.getPersonalInfoNav().getResults().length > 0) {
			return this.getPersonalInfoNav().getResults()[0].getFirstName();
		}
		return "";
	}

	public String callLastName() {
		if (this.getPersonalInfoNav().getResults().length > 0) {
			return this.getPersonalInfoNav().getResults()[0].getLastName();
		}
		return "";
	}

	public String callGender() {
		if (this.getPersonalInfoNav().getResults().length > 0) {
			return this.getPersonalInfoNav().getResults()[0].getGender();
		}
		return "";
	}

	public String callNationality() {
		if (this.getPersonalInfoNav().getResults().length > 0) {
			return this.getPersonalInfoNav().getResults()[0].getNationality();
		}
		return "";
	}

	public String callUserId() {
		return this.personIdExternal;
	}

	public String callEmail() {
		if (this.getEmailNav().getResults().length > 0) {
			return this.getEmailNav().getResults()[0].getEmailAddress();
		}
		return "";
	}

	public String callTitle() {
		if (this.getEmploymentNav().getResults().length > 0) {
			if (this.getEmploymentNav().getResults()[0].getJobInfoNav().getResults().length > 0) {
				return this.getEmploymentNav().getResults()[0].getJobInfoNav().getResults()[0].getJobTitle();
			}
			return "";
		}
		return "";
	}

	public String callPosition() {
		if (this.getEmploymentNav() != null) {
			if (this.getEmploymentNav().getResults().length > 0) {
				if (this.getEmploymentNav().getResults()[0].getJobInfoNav().getResults().length > 0) {
					if (this.getEmploymentNav().getResults()[0].getJobInfoNav().getResults()[0].getPositionNav() != null) {
						return this.getEmploymentNav().getResults()[0].getJobInfoNav().getResults()[0].getPositionNav().getExternalName_defaultValue();
					}
				}
			}
			return "";
		}
		return "";
	}

	public String callHireDate() {
		if (this.getEmploymentNav().getResults().length > 0) {
			return this.getEmploymentNav().getResults()[0].getStartDate();
		}
		return "";
	}

	public String callLocation() {
		if (this.getEmploymentNav().getResults().length > 0) {
			if (this.getEmploymentNav().getResults()[0].getJobInfoNav().getResults().length > 0) {
				return this.getEmploymentNav().getResults()[0].getJobInfoNav().getResults()[0].getLocation();
			}
			return "";
		}
		return "";
	}

	public String callHr() {
		if (this.getEmploymentNav().getResults().length > 0) {
			if (this.getEmploymentNav().getResults()[0].getEmpJobRelationshipNav().getResults().length > 0) {
				return this.getEmploymentNav().getResults()[0].getEmpJobRelationshipNav().getResults()[0].getRelUserId();
			}
			return "";
		}
		return "";
	}

	public String callDefLG() {
		if (this.getEmploymentNav().getResults().length > 0) {
			return this.getEmploymentNav().getResults()[0].getUserNav().getDefaultLocale();
		}
		return "";
	}

	public String getDefaultLanguage() {
		return defaultLanguage;
	}

	public void setDefaultLanguage(String defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}

}
