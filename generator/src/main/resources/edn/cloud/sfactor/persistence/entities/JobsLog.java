package edn.cloud.sfactor.persistence.entities;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "JOBSLOG_V1", uniqueConstraints = { @UniqueConstraint(columnNames = {"NAME_JOB", "CODE"}) })
public class JobsLog implements IDBEntity

{
	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;
	
	@Basic
	@Column(name = "ID_JOB")
	private Long idJob;	
	
	@Basic
	@Column(name = "NAME_JOB")
	private String nameJob;

	@Basic
	@Column(name = "CODE")
	private String code;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ADDED_ON")
	private Date addedOn;	
	
	public JobsLog() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIdJob() {
		return idJob;
	}

	public void setIdJob(Long idJob) {
		this.idJob = idJob;
	}


	public Date getAddedOn() {
		return addedOn;
	}

	public void setAddedOn(Date addedOn) {
		this.addedOn = addedOn;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getNameJob() {
		return nameJob;
	}

	public void setNameJob(String nameJob) {
		this.nameJob = nameJob;
	}
}
