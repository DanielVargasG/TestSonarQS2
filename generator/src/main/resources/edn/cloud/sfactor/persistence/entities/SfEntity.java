package edn.cloud.sfactor.persistence.entities;

import static edn.cloud.sfactor.persistence.entities.DBQueries.GET_ENTITY_BY_NAME;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "SFENTITY", uniqueConstraints = { @UniqueConstraint(columnNames = { "ID" }) })
@NamedQueries({ @NamedQuery(name = GET_ENTITY_BY_NAME, query = "select u from SfEntity u where u.name = :name") })
public class SfEntity implements IDBEntity {

	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;
	
	@Basic
	@Column(name = "PRIMKEY")
	private String primkey;

	@Basic
	@Column(name = "NAME", unique = true, length=1024)
	private String name;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "PROP_ID")
	private List<SfProperty> properties;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "NAVPROD_ID")
	private List<SfNavProperty> navproperties;

	public SfEntity() {

	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrimkey() {
		return primkey;
	}

	public void setPrimkey(String primkey) {
		this.primkey = primkey;
	}

	public List<SfProperty> getProperties() {
		if (this.properties == null) {
			this.properties = new ArrayList<>();
		}
		return properties;
	}

	public void setProperties(List<SfProperty> properties) {
		this.properties = properties;
	}

	public void addProperties(SfProperty property) {
		getProperties().add(property);
	}

	public List<SfNavProperty> getNavproperties() {
		if (this.navproperties == null) {
			this.navproperties = new ArrayList<>();
		}
		return navproperties;
	}

	public void setNavproperties(List<SfNavProperty> navproperties) {
		this.navproperties = navproperties;
	}

	public void addNavproperties(SfNavProperty navproperties) {
		getNavproperties().add(navproperties);
	}

}