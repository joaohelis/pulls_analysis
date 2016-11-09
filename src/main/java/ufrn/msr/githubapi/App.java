/**
 * 
 */
package ufrn.msr.githubapi;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.kohsuke.github.GHRateLimit;

import ufrn.msr.githubapi.dao.RepositoryDAO;
import ufrn.msr.githubapi.dao.hibernate.HibernateRepositoryDAO;
import ufrn.msr.githubapi.miner.GitLocalAndRemoteMiner;
import ufrn.msr.githubapi.models.Repository;

/**
 * @author Joao Helis Bernardo
 *
 * 2016
 */
public class App {
	
	private static int indexOf(String repoName, List<Repository> repositories){
		for(int i = 0; i < repositories.size(); i++)
			if(repositories.get(i).getFullName().equals(repoName))
				return i;
		return -1;
	}
	
	public static void main(String[] args) {
		
		RepositoryDAO repoDAO = new HibernateRepositoryDAO();
		
		Map<String, GHRateLimit> tokens = new HashMap<String, GHRateLimit>();
		tokens.put("PUT YOUR TOKEN HERE", null);
		tokens.put("PUT YOUR TOKEN HERE", null);	
		tokens.put("PUT YOUR TOKEN HERE", null);
		
		List<String> excludedRepositories = Arrays.asList(new String[]{"maraujop/django-crispy-forms"});
		
		List<Repository> repositories = repoDAO.listAll().stream().filter(r -> r.getStartedWithTravis() != null && 
																		  !excludedRepositories.contains(r.getFullName()) &&
																		  r.getMergedPullsBeforeCICount() > 140 &&
																		  r.getMergedPullsAfterCICount() > 140
																		  ).collect(Collectors.toList());
		
		Collections.sort(repositories, (r1, r2) -> r2.getWatchers().compareTo(r1.getWatchers()));				
		
//		System.out.println(indexOf("kilimchoi/engineering-blogs", repositories));
		
//		repositories = repositories.subList(indexOf("kilimchoi/engineering-blogs", repositories), repositories.size());
		
		System.out.println(repositories.size());
		
		for(Repository repo: repositories){
			GitLocalAndRemoteMiner.pullRequestsInformationMiner(repo, tokens);
		}
		System.exit(0);
	}
}
