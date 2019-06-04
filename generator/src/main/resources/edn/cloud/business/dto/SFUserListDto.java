package edn.cloud.business.dto;

import java.util.List;

import com.google.gson.annotations.Expose;

public class SFUserListDto {

    @Expose
    List<SFUserPhotoDto> results;

	public List<SFUserPhotoDto> getResults() {
		return results;
	}

	public void setResults(List<SFUserPhotoDto> results) {
		this.results = results;
	}
}
