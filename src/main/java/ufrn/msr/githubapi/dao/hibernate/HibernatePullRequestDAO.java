package ufrn.msr.githubapi.dao.hibernate;

import org.hibernate.Query;

import ufrn.msr.githubapi.dao.PullRequestDAO;
import ufrn.msr.githubapi.models.PullRequest;
import ufrn.msr.githubapi.models.Repository;

public class HibernatePullRequestDAO extends HibernateDAO<PullRequest, Integer> implements PullRequestDAO {

	public HibernatePullRequestDAO(){
		// we passing the entity for super class
		super(PullRequest.class);
	}
	
	@Override
	public PullRequest getByNumber(Integer number, Repository repo) {
		Query query = HibernateUtil.getSession().createQuery("from "+getTypeClass().getName()+" where number = :number and repo = :repo");
		query.setParameter("number", number);
		query.setParameter("repo", repo);
		return (PullRequest) query.uniqueResult();
	}
}