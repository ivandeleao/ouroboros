/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal.pessoa;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.mysql.bean.principal.pessoa.GrupoItem;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class GrupoItemDAO {
    public GrupoItem save(GrupoItem grupoItem){
        try {
            em.getTransaction().begin();
            if(grupoItem.getId() == null){
                em.persist(grupoItem);
            }else{
                em.merge(grupoItem);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        }
        
        return grupoItem;
    }

    public GrupoItem findById(Integer id){
        GrupoItem grupoItem = null;
        
        try {
            grupoItem = em.find(GrupoItem.class, id);
        } catch (Exception e) {
            System.err.println(e);
        }
        
        return grupoItem;
    }
    
    
    
    
    public List<GrupoItem> findAll(){
        List<GrupoItem> grupoItens = null;
        try {
            Query query = em.createQuery("from " + GrupoItem.class.getSimpleName() + " gi");
            grupoItens = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return grupoItens;
    }
    
    
}
