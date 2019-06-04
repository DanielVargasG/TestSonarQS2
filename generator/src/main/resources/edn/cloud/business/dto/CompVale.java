package edn.cloud.business.dto;

import java.util.Date;

public class CompVale {
	private Date compDate;
	private String compCode;
	private String compCurrency;
	private double compValue;
	private int compFreq;

	public Date getCompDate() {
		return compDate;
	}

	public void setCompDate(Date compDate) {
		this.compDate = compDate;
	}

	public String getCompCode() {
		return compCode;
	}

	public void setCompCode(String compCode) {
		this.compCode = compCode;
	}

	public String getCompCurrency() {
		return compCurrency;
	}

	public void setCompCurrency(String compCurrency) {
		this.compCurrency = compCurrency;
	}

	public double getCompValue() {
		return compValue;
	}

	public void setCompValue(double compValue) {
		this.compValue = compValue;
	}

	public int getCompFreq() {
		return compFreq;
	}

	public void setCompFreq(int compFreq) {
		this.compFreq = compFreq;
	}
}
