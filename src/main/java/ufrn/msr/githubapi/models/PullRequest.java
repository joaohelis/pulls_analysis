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

import org.hibernate.annotations.Type;

/**
 * @author Joao Helis Bernardo
 *
 * 2016
 */
@Entity(name="pullRequest")
public class PullRequest {
	
	@Id @GeneratedValue
	private Integer id;
	
	private Integer number;
	
	@Type(type="text")
	private String title;
	
	@Type(type="text")
	private String description;
	
	private Integer additions;
	private Integer deletions;
	private Integer changedFiles;
	private Integer commentsCount;
	private Integer reviewComments;
	
	private Date createdAt;
	private Date closedAt;
	private Date mergedAt;
	
	private String user;
	private String assignee;
	private String closedBy;
	
	private boolean isMerged;
	private String state;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy = "pullRequest")
	private List<PullRequestEvent> events;	
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy = "pullRequest")
	private List<PullRequestComment> comments;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy = "pullRequest")
	private List<TravisBuild> travisBuilds;
	
	@ManyToOne
	private Release release;
	
	@ManyToOne(cascade=CascadeType.ALL)
	private Repository repo;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getAdditions() {
		return additions;
	}
	public void setAdditions(Integer additions) {
		this.additions = additions;
	}
	public Integer getDeletions() {
		return deletions;
	}
	public void setDeletions(Integer deletions) {
		this.deletions = deletions;
	}
	public Integer getChangedFiles() {
		return changedFiles;
	}
	public void setChangedFiles(Integer changedFiles) {
		this.changedFiles = changedFiles;
	}
	public Integer getCommentsCount() {
		return commentsCount;
	}
	public void setCommentsCount(Integer commentsCount) {
		this.commentsCount = commentsCount;
	}
	public Integer getReviewComments() {
		return reviewComments;
	}
	public void setReviewComments(Integer reviewComments) {
		this.reviewComments = reviewComments;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public Date getClosedAt() {
		return closedAt;
	}
	public void setClosedAt(Date closedAt) {
		this.closedAt = closedAt;
	}
	public Date getMergedAt() {
		return mergedAt;
	}
	public void setMergedAt(Date mergedAt) {
		this.mergedAt = mergedAt;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getAssignee() {
		return assignee;
	}
	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}
	public String getClosedBy() {
		return closedBy;
	}
	public void setClosedBy(String closedBy) {
		this.closedBy = closedBy;
	}
	public List<PullRequestEvent> getEvents() {
		return events;
	}
	public void setEvents(List<PullRequestEvent> events) {
		this.events = events;
	}
	public List<PullRequestComment> getComments() {
		return comments;
	}
	public void setComments(List<PullRequestComment> comments) {
		this.comments = comments;
	}
	public Release getRelease() {
		return release;
	}
	public void setRelease(Release release) {
		this.release = release;
	}
	public Repository getRepo() {
		return repo;
	}
	public void setRepo(Repository repo) {
		this.repo = repo;
	}
	public boolean isMerged() {
		return isMerged;
	}
	public void setMerged(boolean isMerged) {
		this.isMerged = isMerged;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public List<TravisBuild> getTravisBuilds() {
		return travisBuilds;
	}
	public void setTravisBuilds(List<TravisBuild> travisBuilds) {
		this.travisBuilds = travisBuilds;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PullRequest [id=");
		builder.append(id);
		builder.append(", number=");
		builder.append(number);
		builder.append(", title=");
		builder.append(title);
		builder.append(", changedFiles=");
		builder.append(changedFiles);
		builder.append(", reviewComments=");
		builder.append(reviewComments);
		builder.append(", events=");		
		builder.append(events == null || events.isEmpty()? 0: events.size());
		builder.append(", release=");
		builder.append(release == null? null: release.getTitle());
		builder.append("]");
		return builder.toString();
	}
}