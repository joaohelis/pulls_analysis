/**
 * 
 */
package ufrn.msr.githubapi.models;

/**
 * @author Joao Helis Bernardo
 *
 * 2016
 */
public class GitStats {
	
	private int filesChanged;
	private int insertions;
	private int deletions;

	public GitStats() {
		super();
	}
	
	/**
	 * @param filesChanged
	 * @param insertions
	 * @param deletions
	 */
	public GitStats(int filesChanged, int insertions, int deletions) {
		super();
		this.filesChanged = filesChanged;
		this.insertions = insertions;
		this.deletions = deletions;
	}
	
	public int getFilesChanged() {
		return filesChanged;
	}
	public void setFilesChanged(int filesChanged) {
		this.filesChanged = filesChanged;
	}
	public int getInsertions() {
		return insertions;
	}
	public void setInsertions(int insertions) {
		this.insertions = insertions;
	}
	public int getDeletions() {
		return deletions;
	}
	public void setDeletions(int deletions) {
		this.deletions = deletions;
	}
	@Override
	public String toString() {
		return "CommitStats [filesChanged=" + filesChanged + ", insertions=" + insertions + ", deletions=" + deletions
				+ "]";
	}
}
