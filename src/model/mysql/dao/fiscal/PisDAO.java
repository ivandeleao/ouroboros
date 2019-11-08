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
import model.bootstrap.bean.nfe.PisBs;
import model.bootstrap.dao.nfe.PisBsDAO;
import model.mysql.bean.fiscal.Pis;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class PisDAO {

    public Pis save(Pis pis) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            em.getTransaction().begin();
            if (pis.getCodigo() == null) {
                em.persist(pis);
            } else {
                em.merge(pis);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }

        return pis;
    }

    public Pis findById(Integer id) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        Pis pis = null;
        try {
            pis = em.find(Pis.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return pis;
    }

    public List<Pis> findAll() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<Pis> pisList = null;
        try {
            Query query = em.createQuery("from Pis p order by codigo, id");

            pisList = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return pisList;
    }
    
    public List<Pis> listarSimplesNacional() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Pis> q = cb.createQuery(Pis.class);
            Root<Pis> rootPis = q.from(Pis.class);

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.lessThan(rootPis.get("codigo"), 100));

            q.select(rootPis).where(cb.and(predicates.toArray(new Predicate[]{})));

            TypedQuery<Pis> query = em.createQuery(q);

            return query.getResultList();
            
        } catch (NoResultException e) {
            //System.err.println("Erro em pis.findByCodigo " + e);
        } finally {
            em.close();
        }
        
        return null;
    }
    
    public List<Pis> listarTributacaoNormal() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<Pis> q = cb.createQuery(Pis.class);
            Root<Pis> rootPis = q.from(Pis.class);

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.greaterThan(rootPis.get("codigo"), 100));

            q.select(rootPis).where(cb.and(predicates.toArray(new Predicate[]{})));

            TypedQuery<Pis> query = em.createQuery(q);

            return query.getResultList();
            
        } catch (NoResultException e) {
            //System.err.println("Erro em pis.findByCodigo " + e);
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
        List<PisBs> pisBsList = new PisBsDAO().findAll();
        

        em.getTransaction().begin();
        for (PisBs pisBs : pisBsList) {
            Pis pis = new Pis(pisBs.getId(), pisBs.getCodigo(), pisBs.getDescricao());
            if (findById(pisBs.getId()) == null) {
                em.persist(pis);
            } else {
                em.merge(pis);
            }
        }
        
        
        em.getTransaction().commit();

        em.close();
    }
}
