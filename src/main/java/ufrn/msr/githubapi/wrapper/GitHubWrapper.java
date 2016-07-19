/**
 * 
 */
package ufrn.msr.githubapi.wrapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kohsuke.github.GHPullRequest;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ufrn.msr.githubapi.exceptions.GitHubWrapperException;
import ufrn.msr.githubapi.models.PullRequest;
import ufrn.msr.githubapi.models.PullRequestComment;
import ufrn.msr.githubapi.models.PullRequestEvent;
import ufrn.msr.githubapi.models.Release;
import ufrn.msr.githubapi.models.Repository;

/**
 * @author Joao Helis Bernardo
 *
 * 2016
 */
public class GitHubWrapper {
	
	private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	private String oAuthToken;
	
	
	public GitHubWrapper(String oAuthToken){
		this.oAuthToken = oAuthToken;
	}
	
	public int getAmountOfTags(Repository repo) throws GitHubWrapperException{
		int count = 0;
		try{
			int page = 1;			
			JsonArray tags = null;			
			do{
				URL url = new URL("https://api.github.com/repos/"+
							      repo.getFullName()+"/tags?per_page=100&page=" + page++ + "&access_token=" + oAuthToken);														
				
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	
				if (conn.getResponseCode() != 200) 
					throw new GitHubWrapperException("Error: " + conn.getResponseCode());
	
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				Gson gson = new Gson();
				tags = gson.fromJson(reader, JsonArray.class);
				count+= tags.size();
			}while(tags != null && tags.size() != 0);
		}catch(Exception e){
			throw new GitHubWrapperException(e);
		}				
		return count;
	}
	
	public List<PullRequest> updateMergedPullRequestInfoByReleaseInterval(Repository repo, Map<Integer, 
			PullRequest> pullsMap, Set<Integer> processedPulls, Release base, Release head) throws GitHubWrapperException{
		try{
			URL url = new URL("https://api.github.com/repos/"+ repo.getFullName() +
					          "/compare/" + base.getTitle().trim() + "..." + head.getTitle().trim() + "?access_token=" + oAuthToken);														
	
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
			System.out.println(url);
			
			if (conn.getResponseCode() != 200) 
				throw new GitHubWrapperException("Error: " + conn.getResponseCode());
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			Gson gson = new Gson();
			JsonObject compareJson = gson.fromJson(reader, JsonObject.class);
			
			head.setCommits(compareJson.get("total_commits").getAsInt());
			
			JsonArray commitsJson = compareJson.get("commits").getAsJsonArray();
			
			System.out.println("Total_Commits="+compareJson.get("total_commits").getAsInt());
			System.out.println(commitsJson.size());
			
			List<PullRequest> pulls = new ArrayList<PullRequest>();
			
			for(int i = 0;  i < commitsJson.size(); i++){
		
				JsonObject json = commitsJson.get(i).getAsJsonObject();
				
				System.out.println(json);
				
				String message = json.get("message") == null? null: json.get("message").getAsString();				
				
				if(message != null){
					
					System.out.println("Commit [message="+message.substring(0, 30)+"...]");
					
					Matcher pullRequestMatcher = Pattern.compile("[mM]erge pull request #(\\d+)").matcher(message);
					
					if(pullRequestMatcher.find()){
						Integer pullRequestNumber = Integer.parseInt(pullRequestMatcher.group(1));
						if(pullsMap.containsKey(pullRequestNumber) && !processedPulls.contains(pullRequestNumber)){
							PullRequest pullRequest = pullsMap.get(pullRequestNumber);
							pullRequest.setRelease(head);
							processedPulls.add(pullRequestNumber);
							pulls.add(pullRequest);
						}
					}
				}
			}
			return pulls;
		}catch (Exception e) {
			throw new GitHubWrapperException(e);
		}	
	}			
	
	public List<Release> getReleasesFromRepository(Repository repo) throws GitHubWrapperException{
		List<Release> releases = new ArrayList<Release>();
		try{
			int page = 1;			
			JsonArray tags = null;			
			do{
				URL url = new URL("https://api.github.com/repos/"+
							      repo.getFullName()+"/tags?per_page=100&page=" + page++ + "&access_token=" + oAuthToken);														
				
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	
				if (conn.getResponseCode() != 200) 
					throw new GitHubWrapperException("Error: " + conn.getResponseCode());
	
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				Gson gson = new Gson();
				tags = gson.fromJson(reader, JsonArray.class);
				
				System.out.println(url);
				
				for(int i = 0; tags != null && i < tags.size(); i++){
				
					Release release = new Release();
					JsonObject json = tags.get(i).getAsJsonObject();
					
					System.out.println(i+1 + " --- " +repo.getFullName() + " --- Release [name="+json.get("name").getAsString()+"]");
					
					release.setTitle(json.get("name").getAsString());
					release.setSha(json.get("commit").getAsJsonObject().get("sha").getAsString());
					String commitURL = json.get("commit").getAsJsonObject().get("url").getAsString();
					release.setPublishedAt(getDateFromCommit(commitURL, oAuthToken));
					release.setRepository(repo);
					
					releases.add(release);				
				}			
			}while(tags != null && tags.size() != 0);
		}catch(Exception e){
			throw new GitHubWrapperException(e);
		}
		return releases;
	}
	
	private Date getDateFromCommit(String commitURL, String oAuthToken) throws GitHubWrapperException{
		try{
			URL url = new URL(commitURL + "?access_token=" + oAuthToken);
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
			if (conn.getResponseCode() != 200) 
				throw new GitHubWrapperException("Error: " + conn.getResponseCode());
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			Gson gson = new Gson();
			JsonObject commitJson = gson.fromJson(reader, JsonObject.class);
			
			String commitDate = commitJson.get("commit").getAsJsonObject().get("committer").
					getAsJsonObject().get("date").getAsString();
			
			return DATE_FORMAT.parse(commitDate);
			
		}catch(Exception e){
			throw new GitHubWrapperException(e);
		}	
	}
	
	public List<PullRequestEvent> getEvents(PullRequest p) throws GitHubWrapperException{
		List<PullRequestEvent> events = null;
		try{						
			URL url = new URL("https://api.github.com/repos/"+
						      p.getRepo().getFullName()+"/issues/" +
						      p.getNumber()+"/events?per_page=1000&access_token=" + oAuthToken);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
			if (conn.getResponseCode() != 200) 
				throw new GitHubWrapperException("Error: " + conn.getResponseCode());

			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			Gson gson = new Gson();
			JsonArray eventsJson = gson.fromJson(reader, JsonArray.class);
			
			events = new ArrayList<PullRequestEvent>();
			
			for(int i = 0; i < eventsJson.size(); i++){
				
				PullRequestEvent event = new PullRequestEvent();				
				JsonObject json = eventsJson.get(i).getAsJsonObject();
				
				event.setId(Integer.parseInt(json.get("id").getAsString()));
				event.setEvent(json.get("event").getAsString());										
				event.setCreatedAt((json.get("created_at").isJsonNull())? null: DATE_FORMAT.parse(json.get("created_at").getAsString()));
				event.setPullRequest(p);
				
				events.add(event);				
			}									
		}catch(Exception e){
			throw new GitHubWrapperException(e);
		}		
		return events;
	}	
	
	public List<PullRequestComment> getComments(PullRequest p) throws GitHubWrapperException{
		List<PullRequestComment> comments = null;
		try{						
			URL url = new URL("https://api.github.com/repos/"+
						      p.getRepo().getFullName()+"/pulls/" +
						      p.getNumber()+"/comments?per_page=1000&access_token=" + oAuthToken);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			if (conn.getResponseCode() != 200) 
				throw new GitHubWrapperException("Error: " + conn.getResponseCode());

			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			Gson gson = new Gson();
			JsonArray eventsJson = gson.fromJson(reader, JsonArray.class);
			
			comments = new ArrayList<PullRequestComment>();
			
			for(int i = 0; i < eventsJson.size(); i++){
				
				PullRequestComment comment = new PullRequestComment();				
				JsonObject json = eventsJson.get(i).getAsJsonObject();
				try{
					comment.setUserName(json.get("user").getAsJsonObject().get("login").getAsString());
				}catch(Exception e){}
				comment.setId(Integer.parseInt(json.get("id").getAsString()));								
				comment.setCreatedAt((json.get("created_at").isJsonNull())? null: DATE_FORMAT.parse(json.get("created_at").getAsString()));
				comment.setPullRequest(p);
				
				comments.add(comment);				
			}									
		}catch(Exception e){
			throw new GitHubWrapperException(e);
		}		
		return comments;
	}
	
	public static void pullRequestInfoTransfer(PullRequest pullRequest, GHPullRequest ghPull) throws GitHubWrapperException{
		try {
			pullRequest.setAdditions(ghPull.getAdditions());
			pullRequest.setAssignee(ghPull.getAssignee() == null? null: ghPull.getAssignee().getLogin());
			pullRequest.setChangedFiles(ghPull.getChangedFiles());
			pullRequest.setClosedAt(ghPull.getClosedAt());
			pullRequest.setClosedBy(ghPull.getClosedBy() == null? null: ghPull.getClosedBy().getLogin());
			pullRequest.setCommentsCount(ghPull.getCommentsCount());
			pullRequest.setCreatedAt(ghPull.getCreatedAt());
			pullRequest.setDeletions(ghPull.getDeletions());
			pullRequest.setMergedAt(ghPull.getMergedAt());
			pullRequest.setNumber(ghPull.getNumber());
			pullRequest.setReviewComments(ghPull.getReviewComments());
			pullRequest.setTitle(ghPull.getTitle());
			pullRequest.setUser(ghPull.getUser().getLogin());	
			pullRequest.setDescription(ghPull.getBody());
			pullRequest.setMerged(ghPull.isMerged());
			pullRequest.setState(ghPull.getState().toString());
		} catch (Exception e) {
			throw new GitHubWrapperException(e);
		}		
	}
	
	public String getoAuthToken() {
		return oAuthToken;		
	}

	public void setoAuthToken(String oAuthToken) {
		this.oAuthToken = oAuthToken;
	}

}
