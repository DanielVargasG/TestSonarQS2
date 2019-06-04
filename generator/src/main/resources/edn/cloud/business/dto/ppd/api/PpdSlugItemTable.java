package edn.cloud.business.dto.ppd.api;

import java.util.ArrayList;

import edn.cloud.business.dto.integration.SlugItem;

public class PpdSlugItemTable 
{
	private String slug;
	private ArrayList<SlugItem> value;
	
	public String getSlug() {
		return slug;
	}
	public void setSlug(String slug) {
		this.slug = slug;
	}
	public ArrayList<SlugItem> getValue() {
		return value;
	}
	public void setValue(ArrayList<SlugItem> value) {
		this.value = value;
	}
}
