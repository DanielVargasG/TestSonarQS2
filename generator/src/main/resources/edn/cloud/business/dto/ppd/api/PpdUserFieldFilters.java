package edn.cloud.business.dto.ppd.api;

public class PpdUserFieldFilters 
{
    private String custom_field_id;
    private String operator;
    private String value;
    
	public String getCustom_field_id() {
		return custom_field_id;
	}
	public void setCustom_field_id(String custom_field_id) {
		this.custom_field_id = custom_field_id;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}