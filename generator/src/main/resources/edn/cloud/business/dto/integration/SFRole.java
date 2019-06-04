package edn.cloud.business.dto.integration;

public class SFRole {

	private String groupID;
	private String groupName;
	private String groupType;
	private String activeMembershipCount;

	public String getGroupID() {
		return groupID;
	}

	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupType() {
		return groupType;
	}

	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}

	public String getActiveMembershipCount() {
		return activeMembershipCount;
	}

	public void setActiveMembershipCount(String activeMembershipCount) {
		this.activeMembershipCount = activeMembershipCount;
	}

}
