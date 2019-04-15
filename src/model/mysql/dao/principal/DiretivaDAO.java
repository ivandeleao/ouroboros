/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.mysql.bean.principal.Diretiva;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class DiretivaDAO {
    public Diretiva save(Diretiva diretiva){
        try {
            em.getTransaction().begin();
            if(diretiva.getId() == null){
                em.persist(diretiva);
            }else{
                em.merge(diretiva);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println( "Erro em diretiva.save " + e);
            em.getTransaction().rollback();
        }
        
        return diretiva;
    }

    public Diretiva findById(Integer id){
        Diretiva diretiva = null;
        
        try {
            diretiva = em.find(Diretiva.class, id);
        } catch (Exception e) {
            System.err.println("Erro em diretiva.findById " + e);
        }
        
        return diretiva;
    }
    
    
    public List<Diretiva> findAll(){
        List<Diretiva> listDiretiva = null;
        try {
            Query query = em.createQuery("from " + Diretiva.class.getSimpleName() + " d");
            listDiretiva = query.getResultList();
        } catch (Exception e) {
            System.err.println("Erro em diretiva.findAll " + e);
        }
        return listDiretiva;
    }
    
}
