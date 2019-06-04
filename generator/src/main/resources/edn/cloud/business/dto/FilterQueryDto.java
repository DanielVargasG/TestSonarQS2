package edn.cloud.business.dto;

import java.util.Date;
import java.util.HashMap;

public class FilterQueryDto {
	
	private String user;
	private String date;
	private String order;
	private String maxResult;
	private String id;
	private String typeDate;
	private String dateFinish;
	private String status;
	private String page;
	
	private HashMap<String,String> item;
		
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public String getMaxResult() {
		return maxResult;
	}
	public void setMaxResult(String maxResult) {
		this.maxResult = maxResult;
	}
	public String getId() {
		return id;
	}
	public String getDateFinish() {
		return dateFinish;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setDateFinish(String dateFinish) {
		this.dateFinish = dateFinish;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public HashMap<String, String> getItem() {
		return item;
	}
	public void setItem(HashMap<String, String> item) {
		this.item = item;
	}
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}
	public String getTypeDate() {
		return typeDate;
	}
	public void setTypeDate(String typeDate) {
		this.typeDate = typeDate;
	}
}
