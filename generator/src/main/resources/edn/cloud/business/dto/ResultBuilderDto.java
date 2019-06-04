package edn.cloud.business.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ResultBuilderDto {
	private String url;
	private String type;
	private String status;
	private String function1;
	private String function2;
	private String function3;
	private String order;
	private String result;
	private ArrayList<ArrayList<String>> resultArray;
	private Map<String, String> node = new HashMap<String, String>();

	public ResultBuilderDto(String a, String b, String c) {
		this.url = a;
		this.type = b;
		this.result = c;

	}

	public String getUrl() {
		return url;
	}
	
	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public ArrayList<ArrayList<String>> getResultArray() {
		return resultArray;
	}

	public void setResultArray(ArrayList<ArrayList<String>> resultArray) {
		this.resultArray = resultArray;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Map<String, String> getNode() {
		return node;
	}

	public void setNode(Map<String, String> node) {
		this.node = node;
	}

	public String getFunction1() {
		return function1;
	}

	public void setFunction1(String function1) {
		this.function1 = function1;
	}

	public String getFunction2() {
		return function2;
	}

	public void setFunction2(String function2) {
		this.function2 = function2;
	}

	public String getFunction3() {
		return function3;
	}

	public void setFunction3(String function3) {
		this.function3 = function3;
	}
}
