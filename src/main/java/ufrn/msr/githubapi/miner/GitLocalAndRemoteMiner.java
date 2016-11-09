/**
 * 
 */
package ufrn.msr.githubapi.miner;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.kohsuke.github.GHDirection;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHPullRequestQueryBuilder;
import org.kohsuke.github.GHPullRequestQueryBuilder.Sort;
import org.kohsuke.github.GHRateLimit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedIterable;
import org.kohsuke.github.PagedIterator;

import ufrn.msr.githubapi.dao.PullRequestDAO;
import ufrn.msr.githubapi.dao.hibernate.HibernatePullRequestDAO;
import ufrn.msr.githubapi.exceptions.GitHubWrapperException;
import ufrn.msr.githubapi.models.PullRequest;
import ufrn.msr.githubapi.models.Release;
import ufrn.msr.githubapi.models.Repository;
import ufrn.msr.githubapi.utils.GitLocalAndRemoteUtil;
import ufrn.msr.githubapi.wrapper.GitHubWrapper;
import ufrn.msr.githubapi.wrapper.GitWrapper;

/**
 * @author Joao Helis Bernardo
 *
 *         2016
 */
public class GitLocalAndRemoteMiner {

	public static void releaseInformationMiner(Repository repo) {
		String repoPath = "repositories/"
				+ repo.getFullName().substring(repo.getFullName().indexOf('/') + 1, repo.getFullName().length());
		try {
			List<Release> releases = repo.getReleases();
			Collections.sort(releases, (r1, r2) -> r1.getPublishedAt().compareTo(r2.getPublishedAt()));
			GitWrapper.pullRequestAndCommitsByReleases(releases, repoPath, repo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	enum BestTokenOption {
		REMAING, DATE
	}

	private static List<String> bestToken(Map<String, GHRateLimit> tokens, BestTokenOption option) {
		List<String> types = null;
		switch (option) {
		case REMAING:
			types = tokens.entrySet().stream()
					.sorted((t1, t2) -> new Integer(t2.getValue().remaining)
							.compareTo(new Integer(t1.getValue().remaining)))
					.map(Map.Entry::getKey).collect(Collectors.toList());
			break;
		case DATE:
			types = tokens.entrySet().stream()
					.sorted((t1, t2) -> new Integer(t1.getValue().remaining)
							.compareTo(new Integer(t2.getValue().remaining)))
					.map(Map.Entry::getKey).collect(Collectors.toList());
			break;
		}
		return types;
	}

	private static void updateTokens(Map<String, GHRateLimit> tokens) {
		for (String token : tokens.keySet()) {
			try {
				tokens.put(token, GitHub.connectUsingOAuth(token).getRateLimit());
			} catch (IOException e) {
			}
		}
	}
	
	public static void releasesInformationMiner(Repository repo, String oAuthToken){
		GitHubWrapper ghwrapper = new GitHubWrapper(oAuthToken);		
		try {
			List<Release> releases = ghwrapper.getReleasesFromRepository(repo);
			
			Collections.sort(releases, (r1, r2) -> r2.getPublishedAt().compareTo(r1.getPublishedAt()));
			
			for(int i = 0; i < releases.size() - 1; i++)
				releases.get(i).setStartedAt(new Date(releases.get(i+1).getPublishedAt().getTime() + 1000));
//			releases = releases.subList(0, releases.size() - 1);
			
			releases.forEach(r -> System.out.println(r));
			GitLocalAndRemoteUtil.saveReleases(releases, repo);
		} catch (GitHubWrapperException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void pullRequestsInformationMiner(Repository repo, Map<String, GHRateLimit> tokens) {

		PullRequestDAO pullDAO = new HibernatePullRequestDAO();
				
		Map<Integer, PullRequest> pullsMAP = new HashMap<Integer, PullRequest>();	
		repo.getPullRequests().forEach(p -> pullsMAP.put(p.getNumber(), p));
		
		System.out.println(pullsMAP.size());

		try {
			
			int count = 0;
			int remaining = 0;
			String token = null;

			GitHub github = null;
			GitHubWrapper ghwrapper = null;

			updateTokens(tokens);
			System.out.println("GitHub - The token's information were updated!");
			token = bestToken(tokens, BestTokenOption.REMAING).get(0);
			github = GitHub.connectUsingOAuth(token);
			GHRepository ghRepo = github.getRepository(repo.getFullName());

			GHPullRequestQueryBuilder pullQB = ghRepo.queryPullRequests();

			pullQB.state(GHIssueState.ALL);
			pullQB.sort(Sort.CREATED);
			pullQB.direction(GHDirection.DESC);

			PagedIterable<GHPullRequest> pagedIterable = pullQB.list();
			pagedIterable = pagedIterable.withPageSize(100);
			
			PagedIterator<GHPullRequest> pagedIterator = pagedIterable._iterator(200);
						
			pullDAO.beginTransaction();
			
			while(pagedIterator.hasNext()){		

				for (GHPullRequest ghPullRequest: pagedIterator.nextPage()) {			
	
					if (remaining < 100) {
	
						System.out.println(
								"=====================================================================================================");
	
						updateTokens(tokens);
						token = bestToken(tokens, BestTokenOption.REMAING).get(0);
	
						if (tokens.get(token).remaining < 100) {
							token = bestToken(tokens, BestTokenOption.DATE).get(0);
	
							GHRateLimit rateInfo = tokens.get(token);
	
							try {
								Date timeToReset = new Date(
										rateInfo.getResetDate().getTime() - new Date().getTime() + 2000);
	
								System.out.println(
										"-------------------------------------------------------------------------------------------------");
								System.out.println("GITHUB MINER are going to wait " + timeToReset.getMinutes()
										+ " minutes and " + timeToReset.getSeconds()
										+ " seconds to reset limit rate! Please, be a bit patient.");
								System.out.println("GitHub Rate Limit will be reseted at: "
										+ new Date(rateInfo.getResetDate().getTime() + 5000));
								System.out.println("Search Remaing: " + rateInfo.remaining);
								System.out.println(
										"-------------------------------------------------------------------------------------------------");
	
								Thread.sleep(timeToReset.getTime());
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
	
						updateTokens(tokens);
						token = bestToken(tokens, BestTokenOption.REMAING).get(0);
	
						System.out.print("ACCESSING GITHUB --- ");
						github = GitHub.connectUsingOAuth(token);
						ghwrapper = new GitHubWrapper(token);
						System.out.println("DONE!");
	
						ghRepo = github.getRepository(repo.getFullName());
						remaining = tokens.get(token).remaining - 2;
					}
					try {
	
						System.out.print(++count + " --- [Remaing=" + remaining + "] ---");															
						
						if(pullsMAP.containsKey(ghPullRequest.getNumber())){
							System.out.println(repo.getFullName() + " --- " + pullsMAP.get(ghPullRequest.getNumber()) + " --- DONE!");
							if (count % 100 == 0) {
								pullDAO.commitTransaction();
								pullDAO.beginTransaction();
							}
							continue;
						}
												
						PullRequest pullRequest = null;
						try{
							remaining--; // <<<<<<<<<<<<<<<<<
							pullRequest = ghwrapper.getPullRequest(ghPullRequest.getNumber(), repo.getFullName());
						}catch(GitHubWrapperException ghException){
							ghException.printStackTrace();
							continue;
						}
						
						pullRequest.setRepo(repo);
	
						remaining--; // <<<<<<<<<<<<<<<<<
						pullRequest.setEvents(ghwrapper.getEvents(pullRequest));				
	
						if(pullRequest.getReviewComments() != 0){
							remaining--; // <<<<<<<<<<<<<<<<<
							pullRequest.setComments(ghwrapper.getComments(pullRequest));
						}
						
						pullDAO.save(pullRequest);
						pullsMAP.put(pullRequest.getNumber(), pullRequest);
	
						System.out.println(repo.getFullName() + " --- " + pullRequest + " --- DONE!");					
	
					} catch (Exception e) {
						System.out.println(" --- FAILED!");
						e.printStackTrace();
					}
	
					if (count % 100 == 0) {
						pullDAO.commitTransaction();
						pullDAO.beginTransaction();
						remaining--; // <<<<<<<<<<<<<<<<<
					}
				}
				remaining--;
			}			
			if (count % 100 != 0) {
				pullDAO.commitTransaction();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}