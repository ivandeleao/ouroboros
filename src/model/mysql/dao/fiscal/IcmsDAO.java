/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.fiscal;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.bootstrap.bean.nfe.IcmsBs;
import model.bootstrap.dao.nfe.IcmsBsDAO;
import model.mysql.bean.fiscal.Icms;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class IcmsDAO {

    public Icms save(Icms icms) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            em.getTransaction().begin();
            if (icms.getCodigo() == null) {
                em.persist(icms);
            } else {
                em.merge(icms);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }

        return icms;
    }

    public Icms findById(Integer id) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        Icms icms = null;
        try {
            icms = em.find(Icms.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return icms;
    }

    public List<Icms> findAll() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<Icms> icmsList = null;
        try {
            Query query = em.createQuery("from Icms i order by codigo, id");

            icmsList = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return icmsList;
    }
    
    public List<Icms> listarSimplesNacional() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Icms> q = cb.createQuery(Icms.class);
            Root<Icms> rootIcms = q.from(Icms.class);

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.lessThan(rootIcms.get("codigo"), 100));

            q.select(rootIcms).where(cb.and(predicates.toArray(new Predicate[]{})));

            TypedQuery<Icms> query = em.createQuery(q);

            return query.getResultList();
            
        } catch (NoResultException e) {
            //System.err.println("Erro em icms.findByCodigo " + e);
        } finally {
            em.close();
        }
        
        return null;
    }
    
    public List<Icms> listarTributacaoNormal() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Icms> q = cb.createQuery(Icms.class);
            Root<Icms> rootIcms = q.from(Icms.class);

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.greaterThan(rootIcms.get("codigo"), 100));

            q.select(rootIcms).where(cb.and(predicates.toArray(new Predicate[]{})));

            TypedQuery<Icms> query = em.createQuery(q);

            return query.getResultList();
            
        } catch (NoResultException e) {
            //System.err.println("Erro em icms.findByCodigo " + e);
        } finally {
            em.close();
        }
        
        return null;
    }

    /**
     * Define os dados iniciais ao criar a tabela
     */
    public void bootstrap() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<IcmsBs> icmsBsList = new IcmsBsDAO().findAll();
        

        em.getTransaction().begin();
        for (IcmsBs icmsBs : icmsBsList) {
            Icms icms = new Icms(icmsBs.getId(), icmsBs.getCodigo(), icmsBs.getDescricao());
            if (findById(icmsBs.getId()) == null) {
                em.persist(icms);
            } else {
                em.merge(icms);
            }
        }
        
        
        em.getTransaction().commit();

        em.close();
    }
}
