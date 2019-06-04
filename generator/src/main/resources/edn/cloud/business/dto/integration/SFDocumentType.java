package edn.cloud.business.dto.integration;

public class SFDocumentType extends SlugItem {
	private String folder_id;// (integer): ID of the Folder under which the Documents of this Document Type
								// will be filed ,
	private String expiry_date_policy_type;// (string, optional): Define the Expiry Policy applied by default to the
											// Documents uploaded with this Document Type. Can be one of: specific_date,
											// document_upload_date, document_metadata, no_limit ,
	private String[] employee_access_permissions;
	private DocInfoDto[] employeeDocs;
	private localizedLabel[] localized_labels;
	private int countItems;
	private boolean visible = false;
	public boolean ntFinalDoc = false;

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public DocInfoDto[] getEmployeeDocs() {
		return employeeDocs;
	}

	public void setEmployeeDocs(DocInfoDto[] employeeDocs) {
		this.employeeDocs = employeeDocs;
	}

	public String[] getEmployee_access_permissions() {
		return employee_access_permissions;
	}

	public void setEmployee_access_permissions(String[] employee_access_permissions) {
		this.employee_access_permissions = employee_access_permissions;
	}

	public String getFolder_id() {
		return folder_id;
	}

	public void setFolder_id(String folder_id) {
		this.folder_id = folder_id;
	}

	public String getExpiry_date_policy_type() {
		return expiry_date_policy_type;
	}

	public void setExpiry_date_policy_type(String expiry_date_policy_type) {
		this.expiry_date_policy_type = expiry_date_policy_type;
	}

	public int getCountItems() {
		return countItems;
	}

	public void setCountItems(int countItems) {
		this.countItems = countItems;
	}

	public localizedLabel[] getLocalized_labels() {
		return localized_labels;
	}

	public void setLocalized_labels(localizedLabel[] localized_labels) {
		this.localized_labels = localized_labels;
	}
}
