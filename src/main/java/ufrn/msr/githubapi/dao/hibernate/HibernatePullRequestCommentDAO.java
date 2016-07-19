package ufrn.msr.githubapi.dao.hibernate;

import ufrn.msr.githubapi.dao.PullRequestCommentDAO;
import ufrn.msr.githubapi.models.PullRequestComment;

public class HibernatePullRequestCommentDAO extends HibernateDAO<PullRequestComment, Integer> implements PullRequestCommentDAO {

	public HibernatePullRequestCommentDAO(){
		// we passing the entity for super class
		super(PullRequestComment.class);
	}
}