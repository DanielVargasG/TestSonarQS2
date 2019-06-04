package edn.cloud.sfactor.persistence.entities;

import java.util.Collection;

public class TreeEntities {

	private Collection<TreeEntities> children;
	private String name;
	private String url;
	private String relation;

	public TreeEntities(Collection<TreeEntities> children, String name, String url, String relation) {
		super();
		this.children = children;
		this.name = name;
		this.url = url;
		this.relation = relation;
	}

	public Collection<TreeEntities> getChildren() {
		return children;
	}

	public void setChildren(Collection<TreeEntities> children) {
		this.children = children;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

}
