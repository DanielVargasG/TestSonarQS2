package edn.cloud.web.auth;

public enum AppRole {
	ANALYZER("Analyzer"); //$NON-NLS-1$

	private String roleName;

	private AppRole(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleName() {
		return this.roleName;
	}
}
