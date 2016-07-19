package ufrn.msr.githubapi.dao;

import ufrn.msr.githubapi.models.Release;
import ufrn.msr.githubapi.models.Repository;

public interface ReleaseDAO extends GenericDAO<Release, Integer> {
	
	Release getByTitle(String title, Repository repo);

}