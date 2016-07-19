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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHRepositorySearchBuilder;
import org.kohsuke.github.GitHub;

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
		
		for(int buildNumber = 1; buildNumber <= 20; buildNumber++){
			try {
				URL url = new URL("https://api.travis-ci.org/repos/"+ repoFullName +"/builds?number="+buildNumber);
				System.out.println(url);
	
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
	
	private static void getTravisStartedDateOfRepositories(List<Repository> repositories) {		
		RepositoryDAO repoDAO = new HibernateRepositoryDAO();
		int count = 0;
		for(Repository repo: repositories){
			Date startedWithTravis = null;
			count++;
			try {
				startedWithTravis = getDateOfFirtBuldWithTravis(repo.getFullName());
			} catch (TravisAPIException e) {				
				e.printStackTrace();
				continue;
			}
			repo.setStartedWithTravis(startedWithTravis);
			repoDAO.beginTransaction();
			repoDAO.save(repo);
			repoDAO.commitTransaction();
			System.out.println(count+"/"+repositories.size()+" --- "+repo+ "--- DONE!");
		}		
	}	
	
	@SuppressWarnings("unused")
	private static void getGitHubRepositoriesWithTravis(String GHoAuthToken) {
		
		RepositoryDAO repoDAO = new HibernateRepositoryDAO();
				 
		try {
			
			System.out.print("Connecting to GitHub --- ");
			GitHub github = GitHub.connectUsingOAuth(GHoAuthToken);
			System.out.println("Done!");
			
			System.out.print("Searching repositories --- ");
			GHRepositorySearchBuilder searchRepo = github.searchRepositories();			
			
			searchRepo.sort(GHRepositorySearchBuilder.Sort.STARS);	

			searchRepo.in("filename:.travis.yml language:ruby language:java language:python");
			
			List<GHRepository> repositories = searchRepo.list().asList();
			System.out.println(repositories.size() + " --- Done!");
			
			for(int i = 0; i < repositories.size() && i < 1000; i++){
				
				GHRepository repo = repositories.get(i);
				
				repoDAO.beginTransaction();
				
				Repository repository = new Repository();
				
				repository.setId(repo.getId());
				repository.setFullName(repo.getFullName());
				repository.setDefaltBranch(repo.getDefaultBranch());				
				repository.setWatchers(repo.getWatchers());				
				repository.setUrl((repo.getUrl() != null)? repo.getUrl().toString(): null);
				repository.setCreatedAt(repo.getCreatedAt());			
				repository.setLanguage(repo.getLanguage());	
				
				repoDAO.save(repository);
				
				repoDAO.commitTransaction();
				
				System.out.println(String.format("#%-5s Repo: #%-5s %-35s Language: %-10s Star: %-5s" , i+1, 
						repo.getId(), (repo.getFullName().length() < 35)? repo.getFullName(): repo.getFullName().substring(0, 35),
						repo.getLanguage(), repo.getWatchers()));				
			}			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public static void main(String[] args) {
		
		RepositoryDAO repoDAO = new HibernateRepositoryDAO();		
		List<Repository> repositories = repoDAO.listAll().stream().filter(r -> r.getStartedWithTravis() == null).collect(Collectors.toList());;		
		getTravisStartedDateOfRepositories(repositories);
		
		System.exit(0);
	}
}
