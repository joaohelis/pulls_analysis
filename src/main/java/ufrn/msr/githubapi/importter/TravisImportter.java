/**
 * 
 */
package ufrn.msr.githubapi.importter;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import ufrn.msr.githubapi.dao.PullRequestDAO;
import ufrn.msr.githubapi.dao.RepositoryDAO;
import ufrn.msr.githubapi.dao.TravisBuildDAO;
import ufrn.msr.githubapi.dao.hibernate.HibernatePullRequestDAO;
import ufrn.msr.githubapi.dao.hibernate.HibernateRepositoryDAO;
import ufrn.msr.githubapi.dao.hibernate.HibernateTravisBuildDAO;
import ufrn.msr.githubapi.models.PullRequest;
import ufrn.msr.githubapi.models.Repository;
import ufrn.msr.githubapi.models.TravisBuild;

/**
 * @author Joao Helis Bernardo
 *
 * 2016
 */

public class TravisImportter {
		
	public static void pullRequestBuildStatusImporter(String filepath){
		
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
		
		Reader in;
		int count = 0;
		
		try {			
			RepositoryDAO repoDAO = new HibernateRepositoryDAO();
			PullRequestDAO pullRequestDAO = new HibernatePullRequestDAO();
			TravisBuildDAO travisBuildDAO = new HibernateTravisBuildDAO();
			travisBuildDAO.beginTransaction();
						
			in = new FileReader(filepath);
			
			System.out.println(" ___________________________________________________________________________________________________________");
			System.out.println("| #     |         project         | pull_num |   started_at  |  finished_at  |   branch   | build_status    |");
			System.out.println("|_______|_________________________|__________|_______________|_______________|____________|_________________|");
			
			Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().withNullString("").parse(in);			
			for (CSVRecord record : records) {
				
				TravisBuild travisBuild = new TravisBuild();
				
				Repository repo =  null;
				PullRequest pull = null;
				
				try{
					repo = repoDAO.getRepositoryByName(record.get("project"));
					Integer pullNumber = Integer.parseInt(record.get("pull_number"));
					pull = pullRequestDAO.getByNumber(pullNumber, repo);
				}catch(Exception e){
					e.printStackTrace();
				}
				
				if(repo == null || pull == null) continue;
				
				travisBuild.setRepo(repo);
				travisBuild.setPull(pull);
				try {
					Date startedAt = ft.parse(record.get("started_at"));
					travisBuild.setStartedAt(startedAt);
					Date finishedAt = ft.parse(record.get("finished_at"));
					travisBuild.setFinishedAt(finishedAt);
				} catch (Exception e) {
					e.printStackTrace();
				}
			
				travisBuild.setBranch(record.get("branch"));
				travisBuild.setBuildStatus(record.get("build_status"));
				
				try{
					travisBuildDAO.save(travisBuild);
				}catch(Exception e){
					e.printStackTrace();
					continue;
				}
				
				System.out.println(String.format("| %-5s |  %-20s |  %-7s | %-12s | %-12s | %-12s | %-10s |" , ++count,
						travisBuild.getRepo().getFullName(), travisBuild.getPull().getNumber(), travisBuild.getStartedAt(),
						travisBuild.getFinishedAt(), travisBuild.getBranch(), travisBuild.getBuildStatus()));
				
				if(count % 100 == 0){
					travisBuildDAO.commitTransaction();
					travisBuildDAO.beginTransaction();
				}
			}						
			System.out.println(" ___________________________________________________________________________________________________");
			
			travisBuildDAO.commitTransaction();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e){			
			e.printStackTrace();
		}	
	}
		
	public static void main(String[] args) {
		String path = "/home/joaohelis/Desktop/builds_result.csv";
		pullRequestBuildStatusImporter(path);
		System.exit(0);
	}
}

