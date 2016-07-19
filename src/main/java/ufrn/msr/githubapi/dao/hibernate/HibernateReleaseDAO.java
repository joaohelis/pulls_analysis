package ufrn.msr.githubapi.dao.hibernate;

import org.hibernate.Query;

import ufrn.msr.githubapi.dao.ReleaseDAO;
import ufrn.msr.githubapi.models.Release;
import ufrn.msr.githubapi.models.Repository;

public class HibernateReleaseDAO extends HibernateDAO<Release, Integer> implements ReleaseDAO {

	public HibernateReleaseDAO(){
		// we passing the entity for super class
		super(Release.class);
	}

	/* (non-Javadoc)
	 * @see ufrn.msr.githubapi.dao.ReleaseDAO#getByTitle(java.lang.String, ufrn.msr.githubapi.models.Repository)
	 */
	@Override
	public Release getByTitle(String title, Repository repository) {
		Query query = HibernateUtil.getSession().createQuery("from "+getTypeClass().getName()+" where title = :title and repository = :repository");
		query.setParameter("title", title);
		query.setParameter("repository", repository);
		return (Release) query.uniqueResult();
	}
}