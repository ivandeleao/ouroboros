/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.fiscal;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.mysql.bean.fiscal.SatCupom;
import model.mysql.bean.fiscal.SatCupom;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class SatCupomDAO {
    public SatCupom save(SatCupom satCupom){
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
        }
        
        return satCupom;
    }

    public SatCupom findById(Integer id){
        SatCupom satCupom = null;
        
        try {
            satCupom = em.find(SatCupom.class, id);
        } catch (Exception e) {
            System.err.println("Erro em satCupom.findById " + e);
        }
        
        return satCupom;
    }
    
    public SatCupom findByChave(String chave){
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
        }
        return satCupom;
    }
    
    
    public List<SatCupom> findAll(){
        List<SatCupom> satCupons = null;
        try {
            Query query = em.createQuery("from " + SatCupom.class.getSimpleName() + " c");
            satCupons = query.getResultList();
        } catch (Exception e) {
            System.err.println("Erro em satCupom.findAll " + e);
        }
        return satCupons;
    }
    
    
    
    
    
    
}
