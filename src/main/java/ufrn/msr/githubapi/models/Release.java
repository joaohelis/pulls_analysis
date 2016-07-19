/**
 * 
 */
package ufrn.msr.githubapi.models;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author Joao Helis Bernardo
 *
 * 2016
 */
@Entity(name="git_release")
public class Release {
	
	@Id @GeneratedValue
	private int id;

	private String title;
	private String sha;
	private int commits;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date startedAt;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date publishedAt;
	
	@ManyToOne
	private Repository repository;
	
	@OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.LAZY, mappedBy = "release")
	private List<PullRequest> pullRequests;
	
	public Release(){}
	
	/**
	 * @param id
	 * @param title
	 * @param sha
	 * @param startedAt
	 * @param publishedAt
	 * @param repository
	 * @param pullRequests
	 * @param commits
	 */
	public Release(int id, String title, String sha, Date startedAt, Date publishedAt, Repository repository,
			List<PullRequest> pullRequests, int commits) {
		super();
		this.id = id;
		this.title = title;
		this.sha = sha;
		this.startedAt = startedAt;
		this.publishedAt = publishedAt;
		this.repository = repository;
		this.pullRequests = pullRequests;
		this.commits = commits;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSha() {
		return sha;
	}
	public void setSha(String sha) {
		this.sha = sha;
	}
	public List<PullRequest> getPullRequests() {
		return pullRequests;
	}
	public void setPullRequests(List<PullRequest> pullRequests) {
		this.pullRequests = pullRequests;
	}
	public Repository getRepository() {
		return repository;
	}
	public void setRepository(Repository repo) {
		this.repository = repo;
	}
	public Date getPublishedAt() {
		return publishedAt;
	}
	public void setPublishedAt(Date publishedAt) {
		this.publishedAt = publishedAt;
	}
	public Date getStartedAt() {
		return startedAt;
	}
	public void setStartedAt(Date startedAt) {
		this.startedAt = startedAt;
	}
	public int getCommits() {
		return commits;
	}
	public void setCommits(int commits) {
		this.commits = commits;
	}

	@Override
	public String toString(){
		return "Release [id=" + id + ", title=" + title + ", sha=" + (sha == null? null: sha.substring(0, 6)) + ", startedAt=" + startedAt + ", publishedAt="
				+ publishedAt + ", commits=" + commits +"]";
	}
}