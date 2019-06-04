package edn.cloud.web.soap;

public class EenUser {
	private String startDate;
	private String userId;
	private String seqNumber;
	
	private String personIdExternal;
	private String perPersonUuid;

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSeqNumber() {
		return seqNumber;
	}

	public void setSeqNumber(String seqNumber) {
		this.seqNumber = seqNumber;
	}

	public String getPersonIdExternal() {
		return personIdExternal;
	}

	public void setPersonIdExternal(String personIdExternal) {
		this.personIdExternal = personIdExternal;
	}

	public String getPerPersonUuid() {
		return perPersonUuid;
	}

	public void setPerPersonUuid(String perPersonUuid) {
		this.perPersonUuid = perPersonUuid;
	}
}
