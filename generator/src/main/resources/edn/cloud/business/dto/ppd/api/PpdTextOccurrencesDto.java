package edn.cloud.business.dto.ppd.api;

public class PpdTextOccurrencesDto 
{
	String keyword;
	Integer page_number;
	Double left;
	Double top;
	Double width;
	Double height;
	
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public Integer getPage_number() {
		return page_number;
	}
	public void setPage_number(Integer page_number) {
		this.page_number = page_number;
	}
	public Double getLeft() {
		return left;
	}
	public void setLeft(Double left) {
		this.left = left;
	}
	public Double getTop() {
		return top;
	}
	public void setTop(Double top) {
		this.top = top;
	}
	public Double getWidth() {
		return width;
	}
	public void setWidth(Double width) {
		this.width = width;
	}
	public Double getHeight() {
		return height;
	}
	public void setHeight(Double height) {
		this.height = height;
	}	
}
