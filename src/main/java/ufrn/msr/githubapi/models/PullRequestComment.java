/**
 * 
 */
package ufrn.msr.githubapi.models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author Joao Helis Bernardo
 *
 * 2016
 */
@Entity(name="pullRequestComment")
public class PullRequestComment {
	
	@Id
	private Integer id;
	private String userName;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;
	
	@ManyToOne
	private PullRequest pullRequest;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public PullRequest getPullRequest() {
		return pullRequest;
	}

	public void setPullRequest(PullRequest pullRequest) {
		this.pullRequest = pullRequest;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public String toString() {
		return "PullRequestComment [id=" + id + ", userName=" + userName + ", createdAt=" + createdAt + ", pullRequest="
				+ ((pullRequest == null)? null: pullRequest.getNumber()) + "]";
	}
}
