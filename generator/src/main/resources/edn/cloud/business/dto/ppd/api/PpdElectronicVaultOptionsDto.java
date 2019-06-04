package edn.cloud.business.dto.ppd.api;

public class PpdElectronicVaultOptionsDto 
{
	Boolean electronic_vault_subscription_enabled;
	Boolean paper_documents_distribution_enabled;
	Boolean electronic_documents_distribution_enabled;
	Boolean electronic_payslips_opted_out;
	String id;
	String electronic_vault_id;
	String electronic_vault_state;
	String electronic_payslips_choice_updated_at;
	String electronic_payslips_choice_origin;
		
	
	public Boolean getElectronic_vault_subscription_enabled() {
		return electronic_vault_subscription_enabled;
	}
	public void setElectronic_vault_subscription_enabled(Boolean electronic_vault_subscription_enabled) {
		this.electronic_vault_subscription_enabled = electronic_vault_subscription_enabled;
	}
	public Boolean getPaper_documents_distribution_enabled() {
		return paper_documents_distribution_enabled;
	}
	public void setPaper_documents_distribution_enabled(Boolean paper_documents_distribution_enabled) {
		this.paper_documents_distribution_enabled = paper_documents_distribution_enabled;
	}
	public Boolean getElectronic_documents_distribution_enabled() {
		return electronic_documents_distribution_enabled;
	}
	public void setElectronic_documents_distribution_enabled(Boolean electronic_documents_distribution_enabled) {
		this.electronic_documents_distribution_enabled = electronic_documents_distribution_enabled;
	}
	public Boolean getElectronic_payslips_opted_out() {
		return electronic_payslips_opted_out;
	}
	public void setElectronic_payslips_opted_out(Boolean electronic_payslips_opted_out) {
		this.electronic_payslips_opted_out = electronic_payslips_opted_out;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getElectronic_vault_id() {
		return electronic_vault_id;
	}
	public void setElectronic_vault_id(String electronic_vault_id) {
		this.electronic_vault_id = electronic_vault_id;
	}
	public String getElectronic_vault_state() {
		return electronic_vault_state;
	}
	public void setElectronic_vault_state(String electronic_vault_state) {
		this.electronic_vault_state = electronic_vault_state;
	}
	public String getElectronic_payslips_choice_updated_at() {
		return electronic_payslips_choice_updated_at;
	}
	public void setElectronic_payslips_choice_updated_at(String electronic_payslips_choice_updated_at) {
		this.electronic_payslips_choice_updated_at = electronic_payslips_choice_updated_at;
	}
	public String getElectronic_payslips_choice_origin() {
		return electronic_payslips_choice_origin;
	}
	public void setElectronic_payslips_choice_origin(String electronic_payslips_choice_origin) {
		this.electronic_payslips_choice_origin = electronic_payslips_choice_origin;
	}
}