package ufrn.msr.githubapi.dao;

import ufrn.msr.githubapi.models.PullRequest;
import ufrn.msr.githubapi.models.Repository;

public interface PullRequestDAO extends GenericDAO<PullRequest, Integer> {
	
	PullRequest getByNumber(Integer number, Repository repo);

}