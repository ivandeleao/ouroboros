/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal.pessoa;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import model.mysql.bean.principal.pessoa.PerfilItem;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class PerfilItemDAO {
    public PerfilItem save(PerfilItem perfilItem){
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            em.getTransaction().begin();
            if(perfilItem.getId() == null){
                em.persist(perfilItem);
            }else{
                em.merge(perfilItem);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        
        return perfilItem;
    }

    public PerfilItem findById(Integer id){
        EntityManager em = CONNECTION_FACTORY.getConnection();
        PerfilItem perfilItem = null;
        
        try {
            perfilItem = em.find(PerfilItem.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return perfilItem;
    }
    
    public List<PerfilItem> findAll(){
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<PerfilItem> perfilItens = null;
        try {
            Query query = em.createQuery("from " + PerfilItem.class.getSimpleName() + " pi");
            perfilItens = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return perfilItens;
    }
    
    
}
