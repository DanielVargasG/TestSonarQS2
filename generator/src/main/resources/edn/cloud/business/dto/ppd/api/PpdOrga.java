package edn.cloud.business.dto.ppd.api;

public class PpdOrga {

	public String organization_code = "100";
	public String registration_number;
	public Boolean active = true;

	public PpdOrga(String str) {
		registration_number = organization_code + "_" + str;
	}
}
