package edn.cloud.business.dto;

import java.util.ArrayList;
import java.util.Map;

public class StructureInfo {

	Map<String, Object> flattenedJsonMap;
	String[] countVar;
	ArrayList<Map<String, String>> finalMap;

	public Map<String, Object> getFlattenedJsonMap() {
		return flattenedJsonMap;
	}

	public void setFlattenedJsonMap(Map<String, Object> flattenedJsonMap) {
		this.flattenedJsonMap = flattenedJsonMap;
	}
 
	public String[] getCountVar() {
		return countVar;
	}

	public void setCountVar(String[] countVar) {
		this.countVar = countVar;
	}

	public ArrayList<Map<String, String>> getFinalMap() {
		return finalMap;
	}

	public void setFinalMap(ArrayList<Map<String, String>> finalMap) {
		this.finalMap = finalMap;
	}
}
