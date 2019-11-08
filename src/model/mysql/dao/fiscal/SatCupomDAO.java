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
import model.mysql.bean.fiscal.SatCupom;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class SatCupomDAO {
    public SatCupom save(SatCupom satCupom){
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            em.getTransaction().begin();
            if(satCupom.getId() == null){
                em.persist(satCupom);
            }else{
                em.merge(satCupom);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println( "Erro em satCupom.save " + e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        
        return satCupom;
    }

    public SatCupom findById(Integer id){
        EntityManager em = CONNECTION_FACTORY.getConnection();
        SatCupom satCupom = null;
        
        try {
            satCupom = em.find(SatCupom.class, id);
        } catch (Exception e) {
            System.err.println("Erro em satCupom.findById " + e);
        } finally {
            em.close();
        }
        
        return satCupom;
    }
    
    public SatCupom findByChave(String chave){
        EntityManager em = CONNECTION_FACTORY.getConnection();
        SatCupom satCupom = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            
            CriteriaQuery<SatCupom> q = cb.createQuery(SatCupom.class);
            Root<SatCupom> rootSatCupom = q.from(SatCupom.class);
            
            List<Predicate> predicates = new ArrayList<>();
            
            predicates.add(cb.like(rootSatCupom.get("chave"), chave));
            
            q.select(rootSatCupom).where(predicates.toArray(new Predicate[]{}));
            
            TypedQuery<SatCupom> query = em.createQuery(q);
            
            satCupom = query.getSingleResult();
        } catch (Exception e) {
            System.err.println(e);
        }finally {
            em.close();
        }
        
        return satCupom;
    }
    
    
    public List<SatCupom> findAll(){
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<SatCupom> satCupons = null;
        try {
            Query query = em.createQuery("from " + SatCupom.class.getSimpleName() + " c");
            satCupons = query.getResultList();
        } catch (Exception e) {
            System.err.println("Erro em satCupom.findAll " + e);
        } finally {
            em.close();
        }
        return satCupons;
    }
    
    
    
    
    
    
}
