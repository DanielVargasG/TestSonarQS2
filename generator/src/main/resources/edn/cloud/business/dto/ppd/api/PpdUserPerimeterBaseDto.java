package edn.cloud.business.dto.ppd.api;

import java.util.List;

public class PpdUserPerimeterBaseDto 
{
	private String operator;
	private String organization_id;
	private String organization_group_id;
	private List<PpdUserFieldFilters> custom_field_filters;
	
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getOrganization_id() {
		return organization_id;
	}
	public void setOrganization_id(String organization_id) {
		this.organization_id = organization_id;
	}
	public String getOrganization_group_id() {
		return organization_group_id;
	}
	public void setOrganization_group_id(String organization_group_id) {
		this.organization_group_id = organization_group_id;
	}
	public List<PpdUserFieldFilters> getCustom_field_filters() {
		return custom_field_filters;
	}
	public void setCustom_field_filters(List<PpdUserFieldFilters> custom_field_filters) {
		this.custom_field_filters = custom_field_filters;
	}
}