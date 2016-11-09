/**
 * 
 */
package ufrn.msr.githubapi.miner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.kohsuke.github.GHIssueSearchBuilder;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHRepositorySearchBuilder;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedIterable;
import org.kohsuke.github.PagedIterator;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ufrn.msr.githubapi.dao.RepositoryDAO;
import ufrn.msr.githubapi.dao.hibernate.HibernateRepositoryDAO;
import ufrn.msr.githubapi.exceptions.TravisAPIException;
import ufrn.msr.githubapi.models.Repository;

/**
 * @author Joao Helis Bernardo
 *
 * 2016
 */
public class GitHubRepoWithTravisMiner {
	
	private static Date getDateOfFirtBuldWithTravis(String repoFullName) throws TravisAPIException{
		
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		System.out.println();
		for(int buildNumber = 1; buildNumber <= 5; buildNumber++){
			try {
				URL url = new URL("https://api.travis-ci.org/repos/"+ repoFullName +"/builds?number="+buildNumber);				
				System.out.println(buildNumber);
				//				System.out.println(url);
	
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	
				if (conn.getResponseCode() != 200) 
					throw new TravisAPIException("Error: " + conn.getResponseCode());		
	
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				Gson gson = new Gson();
				JsonObject json = gson.fromJson(reader, JsonArray.class).get(0).getAsJsonObject();
				
				Date startedAt = json.get("started_at").isJsonNull()? null: ft.parse(json.get("started_at").getAsString());
				conn.disconnect();
				
				return startedAt;			
			} catch (Exception e) {}
		}
		throw new TravisAPIException("Error while the system was catching the date of the first build");
	}
	
	@SuppressWarnings("unused")
	private static void getTravisStartedDateOfRepositories(List<Repository> repositories) {		
		RepositoryDAO repoDAO = new HibernateRepositoryDAO();
		int count = 0;
		for(Repository repo: repositories){
			count++;
			System.out.print(count+"/"+repositories.size() + " --- " + repo.getFullName());			
			
			Date startedWithTravis = null;
			
			try {
				startedWithTravis = getDateOfFirtBuldWithTravis(repo.getFullName());
			} catch (TravisAPIException e) {
				System.out.println(" --- FAIL!");
				continue;
			}
			repo.setStartedWithTravis(startedWithTravis);
			repoDAO.beginTransaction();
			repoDAO.save(repo);
			repoDAO.commitTransaction();
			
			System.out.println(" --- DONE!");
		}		
	}
	
	private static void getNumbersOfPullRequests(List<Repository> repositories, List<String> tokens){
		
		RepositoryDAO repoDAO = new HibernateRepositoryDAO();
		
		try {			
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			GitHub github = null;
			
			int count = 0;
			int tokenControl = 0;
			
			for(Repository repo: repositories){
				
				if(count % 15 == 0){
					System.out.println("============== TOKEN " + (tokenControl % tokens.size()) + " ==============");
					github = GitHub.connectUsingOAuth(tokens.get(tokenControl % tokens.size()));
					tokenControl++;
				}
				
				count++;
				
				System.out.println(count+"/"+repositories.size() + " --- " + repo.getFullName());
				
				GHIssueSearchBuilder searchPullsBeforeCI = github.searchIssues(),
									 searchPullsAfterCI = github.searchIssues();
				String qTermPullsBeforeCI = "type:pr repo:" + repo.getFullName() + " is:merged "
						 + "created:<" + df.format(repo.getStartedWithTravis());
				searchPullsBeforeCI.q(qTermPullsBeforeCI);
				System.out.println(qTermPullsBeforeCI);
				
				String qTermPullsAfterCI = "type:pr repo:" + repo.getFullName() + " is:merged "
							 + "created:>=" + df.format(repo.getStartedWithTravis());
				searchPullsAfterCI.q(qTermPullsAfterCI);
				System.out.println(qTermPullsAfterCI);
				
				repoDAO.beginTransaction();
				repo.setMergedPullsBeforeCICount(searchPullsBeforeCI.list().getTotalCount());
				repo.setMergedPullsAfterCICount(searchPullsAfterCI.list().getTotalCount());
				repoDAO.save(repo);
				repoDAO.commitTransaction();
				
				System.out.println(repo);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
	private static void getGitHubRepositoriesWithTravis(String GHoAuthToken) {
		
		RepositoryDAO repoDAO = new HibernateRepositoryDAO();
		
		HashMap<Integer, Repository> repositoriesMap = new HashMap<Integer, Repository>();		
		repoDAO.listAll().forEach(r -> repositoriesMap.put(r.getId(), r));
				 
		try {
			
			System.out.print("Connecting to GitHub --- ");
			GitHub github = GitHub.connectUsingOAuth(GHoAuthToken);
			System.out.println("Done!");
			
			System.out.print("Searching repositories");
			GHRepositorySearchBuilder searchRepo = github.searchRepositories();			
			
			searchRepo.sort(GHRepositorySearchBuilder.Sort.STARS);

			searchRepo.q("stars:1..2172 language:Ruby language:java language:python language:php language:javascript");	
			
			PagedIterable<GHRepository> pagedIterable = searchRepo.list();
			
			pagedIterable.withPageSize(100);
			
			System.out.println(" --- Done!");
			
			PagedIterator<GHRepository> pagedIterator = pagedIterable._iterator(1000);			
			
			int count = 0;			
			
			while(pagedIterator.hasNext() && count < 2500){
				
				List<GHRepository> repositories = pagedIterator.nextPage();
				
				for(GHRepository repo: repositories){									
					
					Repository repository = new Repository();	
					
					if(repositoriesMap.containsKey(repo.getId())){
						repository = repositoriesMap.get(repo.getId());
					}																									
						
					repoDAO.beginTransaction();																	
					repository.setId(repo.getId());
					repository.setFullName(repo.getFullName());
					repository.setDefaltBranch(repo.getDefaultBranch());				
					repository.setWatchers(repo.getWatchers());
					repository.setUrl((repo.getUrl() != null)? repo.getUrl().toString(): null);
					repository.setCreatedAt(repo.getCreatedAt());			
					repository.setLanguage(repo.getLanguage());
					
					repoDAO.save(repository);					
					
					repoDAO.commitTransaction();
					
					repositoriesMap.put(repo.getId(), repository);
					
					System.out.println(String.format("#%-5s Repo: #%-5s %-35s Language: %-10s Star: %-5s" , count+1, 
							repo.getId(), (repo.getFullName().length() < 35)? repo.getFullName(): repo.getFullName().substring(0, 35),
							repo.getLanguage(), repo.getWatchers()));
					
					count++;
					
					if(count >= 2500)
						break;
				}
			}			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public static void main(String[] args) {
		
		List<String> excludedRepositories = Arrays.asList(new String[]{
		"maraujop/django-crispy-forms"});
		
		RepositoryDAO repoDAO = new HibernateRepositoryDAO();	
		List<Repository> repositories = repoDAO.listAll().stream().filter(r -> r.getStartedWithTravis() != null).collect(Collectors.toList());
		Collections.sort(repositories, (r1, r2) -> r2.getWatchers().compareTo(r1.getWatchers()));
		repositories = repositories.stream().filter(r -> r.getMergedPullsBeforeCICount() == null && 
											             r.getMergedPullsAfterCICount() == null && 
											             !excludedRepositories.contains(r.getFullName())).collect(Collectors.toList());;
//		getTravisStartedDateOfRepositories(repositories);										
		
		List<String> tokens = Arrays.asList(new String[]{
				"0aa5705e81a356221dab91ffddcea1af806b3a47",
				"1a25a06db8bcf4a81a6b83a0fffc47a8f12cfd8a",
				"ccf65e8eeedae6892bbd1a3900fc8985c878395d"}); 
				
		getNumbersOfPullRequests(repositories, tokens);
		
		System.exit(0);
	}
}
