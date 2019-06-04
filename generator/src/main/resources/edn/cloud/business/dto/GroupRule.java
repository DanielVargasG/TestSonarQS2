package edn.cloud.business.dto;

import java.util.ArrayList;
import java.util.List;

public class GroupRule {
	private List<GroupRuleValue> include;
	private List<GroupRuleValue> exclude;

	public GroupRule() {
		include = new ArrayList<GroupRuleValue>();
		exclude = new ArrayList<GroupRuleValue>();
	}

	public List<GroupRuleValue> getExclude() {
		return exclude;
	}

	public void setExclude(GroupRuleValue a) {
		this.exclude.add(a);
	}

	public List<GroupRuleValue> getInclude() {
		return include;
	}

	public void setInclude(GroupRuleValue b) {
		this.include.add(b);
	}

}
