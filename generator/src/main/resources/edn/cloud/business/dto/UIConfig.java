package edn.cloud.business.dto;

public class UIConfig {
	private boolean isAdministrator = false;
	private boolean isSuperAdministrator = false;
	private boolean isHasRightToSee = false;
	private boolean isSimpleUser = true;
	private boolean isMngHr = false;
	private boolean seesPerso = false;
	private boolean seesMngHr = false;
	private boolean seesRecruiting = false;
	private boolean seesTemplates = false;
	private boolean seesTeam = false;
	private boolean seesTeamTemplate = false;
	private boolean seesTeamNoTemplate = false;
	private boolean seesAllPop = false;
	private boolean hasTemplates = false;
	private String url = "";
	private String defaultLanguage = "en_US";
	public String userid = "";
	public String admin = "Administration";
	private int countMng;
	private int countHr;

	public UIConfig() {

	}

	public void initAdminConfiguration() {
		// logger.warn("You Are AN ADMIN");
		init(true, false);
	}

	public void initSuperAdminConfiguration() {
		// logger.warn("You Are A SUPER ADMIN");
		init(true, true);
	}

	public void initEmployeeConfiguration() {
		// logger.warn("You Are A User/Manager");
		init(false, false);
	}

	private void init(boolean administrator, boolean superAdministrator) {

		this.isAdministrator = administrator;
		this.isSuperAdministrator = superAdministrator;
	}

	public boolean isAdministrator() {
		return isAdministrator;
	}

	public boolean isSuperAdministrator() {
		return isSuperAdministrator;
	}

	public boolean isHasRightToSee() {
		return isHasRightToSee;
	}

	public void setIsHasRightToSee(boolean hasRightToSee) {
		this.isHasRightToSee = hasRightToSee;
	}

	public int getCountMng() {
		return countMng;
	}

	public void setCountMng(int countMng) {
		this.countMng = countMng;
	}

	public int getCountHr() {
		return countHr;
	}

	public void setCountHr(int countHr) {
		this.countHr = countHr;
	}

	public boolean isSimpleUser() {
		return isSimpleUser;
	}

	public void setSimpleUser(boolean isSimpleUser) {
		this.isSimpleUser = isSimpleUser;
	}

	public boolean isMngHr() {
		return isMngHr;
	}

	public void setMngHr(boolean isMngHr) {
		this.isMngHr = isMngHr;
	}

	public boolean isSeesPerso() {
		return seesPerso;
	}

	public void setSeesPerso(boolean seesPerso) {
		this.seesPerso = seesPerso;
	}

	public boolean isSeesMngHr() {
		return seesMngHr;
	}

	public void setSeesMngHr(boolean seesMngHr) {
		this.seesMngHr = seesMngHr;
	}

	public boolean isSeesRecruiting() {
		return seesRecruiting;
	}

	public void setSeesRecruiting(boolean seesRecruiting) {
		this.seesRecruiting = seesRecruiting;
	}

	public boolean isSeesTemplates() {
		return seesTemplates;
	}

	public void setSeesTemplates(boolean seesTemplates) {
		this.seesTemplates = seesTemplates;
	}

	public boolean isSeesAllPop() {
		return seesAllPop;
	}

	public void setSeesAllPop(boolean seesAllPop) {
		this.seesAllPop = seesAllPop;
	}

	public boolean isSeesTeam() {
		return seesTeam;
	}

	public void setSeesTeam(boolean seesTeam) {
		this.seesTeam = seesTeam;
	}

	public String getDefaultLanguage() {
		return defaultLanguage;
	}

	public void setDefaultLanguage(String defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isHasTemplates() {
		return hasTemplates;
	}

	public void setHasTemplates(boolean hasTemplates) {
		this.hasTemplates = hasTemplates;
	}

	public boolean isSeesTeamTemplate() {
		return seesTeamTemplate;
	}

	public void setSeesTeamTemplate(boolean seesTeamTemplate) {
		this.seesTeamTemplate = seesTeamTemplate;
	}

	public boolean isSeesTeamNoTemplate() {
		return seesTeamNoTemplate;
	}

	public void setSeesTeamNoTemplate(boolean seesTeamNoTemplate) {
		this.seesTeamNoTemplate = seesTeamNoTemplate;
	}
}
