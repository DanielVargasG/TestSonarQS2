package edn.cloud.business.dto.odata;

public class EmpRelationShipObj {
	private String relationShipType;
	private String relUserId;
	private UserObj relUserNav;

	public UserObj getRelUserNav() {
		return relUserNav;
	}

	public void setRelUserNav(UserObj relUserNav) {
		this.relUserNav = relUserNav;
	}

	public String getRelationShipType() {
		return relationShipType;
	}

	public void setRelationShipType(String relationShipType) {
		this.relationShipType = relationShipType;
	}

	public String getRelUserId() {
		return relUserId;
	}

	public void setRelUserId(String relUserId) {
		this.relUserId = relUserId;
	}
}
