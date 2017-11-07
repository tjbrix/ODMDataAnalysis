/**
 * ODM Data Analysis - a tool for the automatic validation, monitoring and
 * generation of generic descriptive statistics of clinical data.
 * 
 * Copyright (c) 2017 Institut für Medizinische Informatik, Münster
 *
 * ODM Data Analysis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, version 3.
 *
 * ODM Data Analysis is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for 
 * more details.
 *
 * You should have received a copy of the GNU General Public License in the file
 * COPYING along with ODM Data Analysis. If not, see <http://www.gnu.org/licenses/>.
 */
package de.unimuenster.imi.odmda.dao;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional; 

/**
 * Generic data access object used for all entities.
 * 
 * @author Tobais Brix
 */
@Repository
@Transactional(propagation = Propagation.REQUIRED)
public abstract class GenericDao<T extends Object> {

	/** The needed session factory. */
	@Autowired
	protected SessionFactory sessionFactory;  

	@PersistenceContext
	protected EntityManager em;
	
	/** Member to access the class type at runtime. */
	protected Class<T> daoType;

	/** Constructor, also defining the class type of the DAO. */
	public GenericDao() {		
		daoType = (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(), GenericDao.class);
	}
	
	public void saveOrUpdate(T entity) { sessionFactory.getCurrentSession().saveOrUpdate(entity); }
	public Serializable save(T entity) { return sessionFactory.getCurrentSession().save(entity); }
	public void update(T entity) { sessionFactory.getCurrentSession().update(entity); }
	public void delete(T entity) {sessionFactory.getCurrentSession().delete(entity);}
	public T get(long id) { return (T) sessionFactory.getCurrentSession().get(daoType,id); }
	public List<T> getAllEntities() { return sessionFactory.getCurrentSession().createQuery("FROM " + daoType.getSimpleName(), daoType).getResultList(); }
	public long getNumEntities() { return (long) sessionFactory.getCurrentSession().createQuery("SELECT COUNT(*) FROM " + daoType.getSimpleName()).uniqueResult(); }
}
