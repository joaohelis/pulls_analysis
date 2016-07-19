/**
 * 
 */
package ufrn.msr.githubapi.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ufrn.msr.githubapi.dao.ReleaseDAO;
import ufrn.msr.githubapi.dao.hibernate.HibernateReleaseDAO;
import ufrn.msr.githubapi.models.Release;
import ufrn.msr.githubapi.models.Repository;

/**
 * @author Joao Helis Bernardo
 *
 * 2016
 */
public class GitLocalAndRemoteUtil {
	
	public static void saveReleases(List<Release> releases, Repository repo){
		ReleaseDAO releaseDAO = new HibernateReleaseDAO();		
		Set<String> repoReleases = new HashSet<String>();
		repo.getReleases().forEach(r -> repoReleases.add(r.getTitle()));;
		releaseDAO.beginTransaction();
		for(Release release: releases){			
			if(repoReleases.contains(release.getTitle()))
				continue;
			releaseDAO.save(release);			
		}
		releaseDAO.commitTransaction();
	}

}
