/**
 * 
 */
package ufrn.msr.githubapi.models;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author Joao Helis Bernardo
 *
 * 2016
 */
@Entity(name="travisBuild")
public class TravisBuild {
	
	@Id @GeneratedValue
	private Integer id;
	
	@ManyToOne(cascade=CascadeType.ALL)
	private Repository repo;	
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date startedAt;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date finishedAt;
	
	@ManyToOne
	private PullRequest pullRequest;
	
	private String branch;
	private String buildStatus;
	
	public TravisBuild(){
		
	}
	
	/**
	 * @param repo
	 * @param pull
	 * @param started_at
	 * @param finished_at
	 * @param branch
	 * @param buildStatus
	 */
	public TravisBuild(Repository repo, PullRequest pull, Date started_at, Date finished_at, String branch,
			String buildStatus) {
		super();
		this.repo = repo;
		this.pullRequest = pull;
		this.startedAt = started_at;
		this.finishedAt = finished_at;
		this.branch = branch;
		this.buildStatus = buildStatus;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Repository getRepo() {
		return repo;
	}

	public void setRepo(Repository repo) {
		this.repo = repo;
	}

	public PullRequest getPull() {
		return pullRequest;
	}

	public void setPull(PullRequest pull) {
		this.pullRequest = pull;
	}

	public Date getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(Date started_at) {
		this.startedAt = started_at;
	}

	public Date getFinishedAt() {
		return finishedAt;
	}

	public void setFinishedAt(Date finished_at) {
		this.finishedAt = finished_at;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getBuildStatus() {
		return buildStatus;
	}

	public void setBuildStatus(String buildStatus) {
		this.buildStatus = buildStatus;
	}

	@Override
	public String toString() {
		return "TravisBuild [id=" + id + ", repo=" + repo + ", pull=" + pullRequest + ", started_at=" + startedAt
				+ ", finished_at=" + finishedAt + ", branch=" + branch + ", buildStatus=" + buildStatus + "]";
	}
}