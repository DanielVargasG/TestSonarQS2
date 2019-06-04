package edn.cloud.business.dto.integration;

import java.util.Collection;
import java.util.List;

import edn.cloud.sfactor.persistence.entities.FolderUser;

public class FolderDTO {

	private Long id;
	private String title;
	private List<TemplateInfoDto> nodes;
	private List<FolderDTO> nodesFolders;
	private Collection<FolderUser> users;
	public Boolean catSeeEdit = true;
	public Integer levelFolder = 0; 
	public Boolean catSeeEnter = false; 
	public Boolean catSeeNothing = false;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<TemplateInfoDto> getNodes() {
		return nodes;
	}

	public void setNodes(List<TemplateInfoDto> nodes) {
		this.nodes = nodes;
	}

	public List<FolderDTO> getNodesFolders() {
		return nodesFolders;
	}

	public void setNodesFolders(List<FolderDTO> nodesFolders) {
		this.nodesFolders = nodesFolders;
	}

	public Integer getLevelFolder() {
		return levelFolder;
	}

	public void setLevelFolder(Integer levelFolder) {
		this.levelFolder = levelFolder;
	}

	public Collection<FolderUser> getUsers() {
		return users;
	}

	public void setUsers(Collection<FolderUser> users) {
		this.users = users;
	}

	
	
	
}
