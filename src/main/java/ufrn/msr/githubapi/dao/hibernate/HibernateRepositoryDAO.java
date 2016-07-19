package ufrn.msr.githubapi.dao.hibernate;

import org.hibernate.Query;

import ufrn.msr.githubapi.dao.RepositoryDAO;
import ufrn.msr.githubapi.models.Repository;

public class HibernateRepositoryDAO extends HibernateDAO<Repository, Integer> implements RepositoryDAO {

	public HibernateRepositoryDAO(){
		// we passing the entity for super class
		super(Repository.class);
	}

	/* (non-Javadoc)
	 * @see ufrn.msr.githubapi.dao.RepositoryDAO#getRepositoryByName(java.lang.String)
	 */
	@Override
	public Repository getRepositoryByName(String fullName) {
		Query query = HibernateUtil.getSession().createQuery("from "+getTypeClass().getName()+" where fullName = :fullName");
		query.setParameter("fullName", fullName);
		return (Repository) query.uniqueResult();
	}
}