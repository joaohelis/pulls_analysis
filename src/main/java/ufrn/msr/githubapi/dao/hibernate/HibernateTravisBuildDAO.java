package ufrn.msr.githubapi.dao.hibernate;

import ufrn.msr.githubapi.dao.TravisBuildDAO;
import ufrn.msr.githubapi.models.TravisBuild;

public class HibernateTravisBuildDAO extends HibernateDAO<TravisBuild, Integer> implements TravisBuildDAO {

	public HibernateTravisBuildDAO(){
		// we passing the entity for super class
		super(TravisBuild.class);
	}
}