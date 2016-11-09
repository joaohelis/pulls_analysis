/**
 * 
 */
package ufrn.msr.githubapi.models;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author Joao Helis Bernardo
 *
 * 2016
 */

@Entity(name="repository")
public class Repository {
	
	@Id
	private Integer id;
	private String fullName;
	private String defaltBranch;
	private Integer watchers;
	private String url;
	private Integer tagsCount;	
	private int releasesCount;
	private Integer mergedPullsBeforeCICount;
	private Integer mergedPullsAfterCICount;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date startedWithTravis;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;
	private String language;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy = "repository")
	private List<Release> releases;	
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy = "repo")
	private List<PullRequest> pullRequests;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy = "repo")
	private List<TravisBuild> travisBuilds;

	/**
	 * @param fullName
	 */
	public Repository(String fullName) {
		this.fullName = fullName;
	}
	
	public Repository(){}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getDefaltBranch() {
		return defaltBranch;
	}

	public void setDefaltBranch(String defaltBranch) {
		this.defaltBranch = defaltBranch;
	}

	public Integer getWatchers() {
		return watchers;
	}

	public void setWatchers(Integer watchers) {
		this.watchers = watchers;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public int getReleasesCount() {
		return releasesCount;
	}

	public void setReleasesCount(int releasesCount) {
		this.releasesCount = releasesCount;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Date getStartedWithTravis() {
		return startedWithTravis;
	}

	public void setStartedWithTravis(Date startedWithTravis) {
		this.startedWithTravis = startedWithTravis;
	}

	public Integer getTagsCount() {
		return tagsCount;
	}

	public void setTagsCount(int tagsCount) {
		this.tagsCount = tagsCount;
	}

	public List<Release> getReleases() {
		return releases;
	}

	public void setReleases(List<Release> releases) {
		this.releases = releases;
	}

	public List<PullRequest> getPullRequests() {
		return pullRequests;
	}

	public void setPullRequests(List<PullRequest> pullRequests) {
		this.pullRequests = pullRequests;
	}

	public List<TravisBuild> getTravisBuilds() {
		return travisBuilds;
	}

	public void setTravisBuilds(List<TravisBuild> travisBuilds) {
		this.travisBuilds = travisBuilds;
	}

	public Integer getMergedPullsBeforeCICount() {
		return mergedPullsBeforeCICount;
	}

	public void setMergedPullsBeforeCICount(Integer mergedPullsBeforeCICount) {
		this.mergedPullsBeforeCICount = mergedPullsBeforeCICount;
	}

	public Integer getMergedPullsAfterCICount() {
		return mergedPullsAfterCICount;
	}

	public void setMergedPullsAfterCICount(Integer mergedPullsAfterCICount) {
		this.mergedPullsAfterCICount = mergedPullsAfterCICount;
	}
	
	@Override
	public String toString() {
		return "Repository [id=" + id + ", fullName=" + fullName + ", watchers=" + watchers
				+ ", mergedPullsBeforeCICount=" + mergedPullsBeforeCICount + ", mergedPullsAfterCICount="
				+ mergedPullsAfterCICount + ", language=" + language + "]";
	}	
}
