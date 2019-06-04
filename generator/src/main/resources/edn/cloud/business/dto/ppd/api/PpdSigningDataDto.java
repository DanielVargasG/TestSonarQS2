package edn.cloud.business.dto.ppd.api;

import java.util.ArrayList;

public class PpdSigningDataDto 
{
	ArrayList<PpdSigningHeadDto> data;
		
	public PpdSigningDataDto() {
		super();
	}

	/**
	 * @return the data
	 */
	public ArrayList<PpdSigningHeadDto> getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(ArrayList<PpdSigningHeadDto> data) {
		this.data = data;
	}
}
