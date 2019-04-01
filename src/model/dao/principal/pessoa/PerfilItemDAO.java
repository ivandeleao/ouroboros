/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao.principal.pessoa;

import java.util.List;
import javax.persistence.Query;
import model.bean.principal.pessoa.PerfilItem;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class PerfilItemDAO {
    public PerfilItem save(PerfilItem perfilItem){
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
        }
        
        return perfilItem;
    }

    public PerfilItem findById(Integer id){
        PerfilItem perfilItem = null;
        
        try {
            perfilItem = em.find(PerfilItem.class, id);
        } catch (Exception e) {
            System.err.println(e);
        }
        
        return perfilItem;
    }
    
    
    
    
    public List<PerfilItem> findAll(){
        List<PerfilItem> perfilItens = null;
        try {
            Query query = em.createQuery("from " + PerfilItem.class.getSimpleName() + " pi");
            perfilItens = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return perfilItens;
    }
    
    
}
