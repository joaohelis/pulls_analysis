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
@Entity(name="pullRequestEvent")
public class PullRequestEvent {
	
	@Id
	private Integer id;
	private String event;
	
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

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public PullRequest getPullRequest() {
		return pullRequest;
	}

	public void setPullRequest(PullRequest pullRequest) {
		this.pullRequest = pullRequest;
	}

	@Override
	public String toString() {
		return "PullRequestEvent [id=" + id + ", event=" + event + ", createdAt=" + createdAt + ", pullRequest="
				+ ((pullRequest == null)? null: pullRequest.getNumber()) + "]";
	}
}
