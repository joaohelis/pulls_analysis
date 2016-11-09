/**
 * 
 */
package ufrn.msr.githubapi.wrapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ufrn.msr.githubapi.dao.PullRequestDAO;
import ufrn.msr.githubapi.dao.ReleaseDAO;
import ufrn.msr.githubapi.dao.hibernate.HibernatePullRequestDAO;
import ufrn.msr.githubapi.dao.hibernate.HibernateReleaseDAO;
import ufrn.msr.githubapi.models.PullRequest;
import ufrn.msr.githubapi.models.Release;
import ufrn.msr.githubapi.models.Repository;

/**
 * @author Joao Helis Bernardo
 *
 * 2016
 */
public class GitWrapper {
	
	private String repoPath;

	/**
	 * @param repoPath
	 */
	public GitWrapper(String repoPath) {
		this.repoPath = repoPath;
	}
	
	public static List<Release> getReleases(String repoPath, Repository repo){
		
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss Z");
		
		BufferedReader br;
		
		List<Release> releases = new ArrayList<Release>();
		
		try {			
			String[] command = new String[] { "git", "log",
					"--no-walk",
					"--tags",
					"--pretty=format:%H %ad %D", 
					"--date=iso8601"};
		
			br = new BufferedReader(new InputStreamReader(executeGitCommand(command, repoPath)));		
			
			String line = null;
			
			while ((line = br.readLine()) != null){
				if(isReleaseLine(line)){
					System.out.println(line);
					String[] attributes = line.split(" +");										
					Release release = new Release();					
					release.setSha(attributes[0]);
					release.setTitle(attributes[5]);
					release.setPublishedAt(ft.parse(attributes[1] +  " " +  attributes[2] + " " + attributes[3]));
					release.setRepository(repo);					
					releases.add(release);
				}								
			}			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		Collections.sort(releases, (r1, r2) -> r2.getPublishedAt().compareTo(r1.getPublishedAt()));
		
		// updating release started date
		for(int i = 0; i < releases.size() - 1; i++)
			releases.get(i).setStartedAt(new Date(releases.get(i+1).getPublishedAt().getTime() + 1000));
		releases = releases.subList(0, releases.size() - 1);
				
		releases.forEach(r -> System.out.println(r));
		
//		 save releases		
//		GitLocalAndRemoteUtil.saveReleases(releases, repo);		
		return releases;
	}
	
	public static void pullRequestAndCommitsByReleases(List<Release> releases, String repoPath, Repository repo){
		
		List<PullRequest> pulls = repo.getPullRequests();
		Map<Integer, PullRequest> pullsMap = new HashMap<Integer, PullRequest>();
		pulls.forEach(p -> pullsMap.put(p.getNumber(), p));
		
		Set<Integer> processedPulls = new HashSet<Integer>();
		
//		PullRequestDAO pullDAO = new HibernatePullRequestDAO();
		
		ReleaseDAO releaseDAO = new HibernateReleaseDAO();
		
		BufferedReader br;
		
		for(int i = 0; i < releases.size() - 1; i++){
			
			int commits = 0;
			
			Release base = releases.get(i),
					head = releases.get(i+1);
			
			String[] command = new String[] {"git", "log",
					"--pretty=oneline",
					base.getTitle().trim()+ ".." +head.getTitle().trim()};
			
			List<PullRequest> headPullRequests = new ArrayList<PullRequest>();
			
			try {
				System.out.println(repo.getFullName() + " --- git log --pretty=oneline " + base.getTitle().trim() + ".." +head.getTitle().trim());
				br = new BufferedReader(new InputStreamReader(executeGitCommand(command, repoPath)));			
				String line = null;				
												
				while ((line = br.readLine()) != null){
					
					commits++;					
					Matcher pullRequestMatcher = Pattern.compile("[mM]erge pull request #(\\d+)").matcher(line);
					
					if(pullRequestMatcher.find()){
						Integer pullRequestNumber = Integer.parseInt(pullRequestMatcher.group(1));
						if(pullsMap.containsKey(pullRequestNumber) && !processedPulls.contains(pullRequestNumber)){
							PullRequest pullRequest = pullsMap.get(pullRequestNumber);
							pullRequest.setRelease(head);
							processedPulls.add(pullRequestNumber);
							pulls.add(pullRequest);
							headPullRequests.add(pullRequest);
						}
					}
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
			
//			pullDAO.commitTransaction();
			
			head.setPullRequests(headPullRequests);
			head.setCommits(commits);
			
			System.out.println(head);
			headPullRequests.forEach(p -> System.out.println(p));			
			
			releaseDAO.beginTransaction();
			releaseDAO.save(head);
			releaseDAO.commitTransaction();						
		}
		System.out.println("Total of PullRequests: " + processedPulls.size());		
	}
	
	public static void pullRequestByReleasesMiner(List<Release> releases, String repoPath, Repository repo){
		// git log --pretty=oneline --merges --grep="Merge pull request" v5.0.0.beta4..v5.0.0.rc1
		
		BufferedReader br;
		
		Set<Integer> pullRequests = new HashSet<Integer>();
		
		PullRequestDAO pullDAO = new HibernatePullRequestDAO();
		
		for(int i = 0; i < releases.size() - 1; i++){						
			System.out.println(releases.get(i));
			String[] command = new String[] {"git", "log",
					"--pretty=oneline",
					"--merges",
					"--grep=Merge pull request",
					releases.get(i+1).getTitle()+ ".." +releases.get(i).getTitle()};
			
			List<PullRequest> pullRequestsByRelease = new ArrayList<PullRequest>();
			
			try {
				System.out.println(releases.get(i+1).getTitle()+ ".." +releases.get(i).getTitle());
				br = new BufferedReader(new InputStreamReader(executeGitCommand(command, repoPath)));			
				String line = null;
				
				pullDAO.beginTransaction();
												
				while ((line = br.readLine()) != null){
					
					Integer pullRequestNumber = Integer.parseInt(line.split(" +")[4].replace("#", ""));
					
					if(pullRequests.contains(pullRequestNumber))
						continue;
					pullRequests.add(pullRequestNumber);
					
					PullRequest pullRequest;
					
					try{
						pullRequest = pullDAO.getByNumber(pullRequestNumber, repo);
					}catch(Exception e){
						continue;
					}
					
					if(pullRequest == null) 
						pullRequest = new PullRequest();
					
					pullRequest.setNumber(pullRequestNumber);
					pullRequest.setRepo(repo);
					
					pullRequest.setRelease(releases.get(i));
										
					pullDAO.save(pullRequest);										
					
					pullRequestsByRelease.add(pullRequest);					
														
					System.out.println(pullRequest);
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			pullDAO.commitTransaction();
			
			releases.get(i+1).setPullRequests(pullRequestsByRelease);
		}
		System.out.println("Total of PullRequests: " + pullRequests.size());
	}
	
	private static boolean isReleaseLine(String line){
		// expected input 6ac6daa43e1c5b7388f8fd69f8117eb7668887c7 2015-06-25 18:28:43 -0300 tag: v4.2.3				
		String[] attributes = line.split(" +");		
		return attributes.length == 6 && attributes[4].equals("tag:");
	}
	
	private static InputStream executeGitCommand(String[] command, String repoPath) throws IOException {
		Process p = Runtime.getRuntime().exec(command, null, new File(repoPath));
		return p.getInputStream();
	}

	public String getRepoPath() {
		return repoPath;
	}

	public void setRepoPath(String repoPath) {
		this.repoPath = repoPath;
	}
}
