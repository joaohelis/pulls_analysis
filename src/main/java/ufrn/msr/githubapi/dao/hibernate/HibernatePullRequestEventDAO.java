package ufrn.msr.githubapi.dao.hibernate;

import ufrn.msr.githubapi.dao.PullRequestEventDAO;
import ufrn.msr.githubapi.models.PullRequestEvent;

public class HibernatePullRequestEventDAO extends HibernateDAO<PullRequestEvent, Integer> implements PullRequestEventDAO {

	public HibernatePullRequestEventDAO(){
		// we passing the entity for super class
		super(PullRequestEvent.class);
	}
}