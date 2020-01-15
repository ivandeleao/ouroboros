/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.fiscal;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.bootstrap.bean.nfe.AnpBs;
import model.bootstrap.dao.nfe.AnpBsDAO;
import model.mysql.bean.fiscal.Anp;
import model.mysql.bean.fiscal.Cest;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;
import static ouroboros.Ouroboros.emBs;

/**
 *
 * @author ivand
 */
public class AnpDAO {

    public Anp save(Anp anp) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            em.getTransaction().begin();
            if (anp.getCodigo() == null) {
                em.persist(anp);
            } else {
                em.merge(anp);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }

        return anp;
    }

    public Anp findById(Integer id) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        Anp anp = null;
        try {
            anp = em.find(Anp.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        return anp;
    }

    public List<Anp> findAll() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<Anp> anpList = null;
        try {
            Query query = em.createQuery("from Anp a order by codigo, id");

            anpList = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return anpList;
    }
    
    
    public Anp findByCodigo(String codigoAnp){
        EntityManager em = CONNECTION_FACTORY.getConnection();
        Anp anp = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            
            CriteriaQuery<Anp> q = cb.createQuery(Anp.class);
            Root<Anp> rootAnp = q.from(Anp.class);
            
            List<Predicate> predicates = new ArrayList<>();
            
            predicates.add(cb.equal(rootAnp.get("codigo"), codigoAnp));
            
            
            q.select(rootAnp).where(cb.or(predicates.toArray(new Predicate[]{})));
            
            TypedQuery<Anp> query = em.createQuery(q);
            
            query.setMaxResults(1);
            
            anp = query.getSingleResult();
            
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return anp;
    }

    /**
     * Define os dados iniciais ao criar a tabela
     */
    public void bootstrap() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<AnpBs> anpBsList = new AnpBsDAO().findAll();
        

        em.getTransaction().begin();
        for (AnpBs anpBs : anpBsList) {
            Anp anp = new Anp(anpBs.getId(), anpBs.getCodigo(), anpBs.getDescricao());
            if (findById(anpBs.getId()) == null) {
                em.persist(anp);
            } else {
                em.merge(anp);
            }
        }
        
        
        em.getTransaction().commit();

        em.close();
    }
}
