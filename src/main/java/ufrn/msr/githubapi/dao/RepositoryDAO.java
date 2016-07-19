package ufrn.msr.githubapi.dao;

import ufrn.msr.githubapi.models.Repository;

public interface RepositoryDAO extends GenericDAO<Repository, Integer> {
	
	Repository getRepositoryByName(String fullName);

}