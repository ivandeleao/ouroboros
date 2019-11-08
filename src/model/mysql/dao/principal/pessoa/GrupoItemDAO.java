/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal.pessoa;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import model.mysql.bean.principal.pessoa.GrupoItem;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class GrupoItemDAO {
    public GrupoItem save(GrupoItem grupoItem){
        EntityManager em = CONNECTION_FACTORY.getConnection();
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
        } finally {
            em.close();
        }
        
        return grupoItem;
    }

    public GrupoItem findById(Integer id){
        EntityManager em = CONNECTION_FACTORY.getConnection();
        GrupoItem grupoItem = null;
        
        try {
            grupoItem = em.find(GrupoItem.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return grupoItem;
    }
    
    
    
    
    public List<GrupoItem> findAll(){
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<GrupoItem> grupoItens = null;
        try {
            Query query = em.createQuery("from " + GrupoItem.class.getSimpleName() + " gi");
            grupoItens = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return grupoItens;
    }
    
    
}
