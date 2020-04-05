/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal.catalogo;

import java.util.List;
import javax.persistence.EntityManager;
import model.mysql.bean.principal.catalogo.Subcategoria;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class SubcategoriaDAO {
    
    public Subcategoria save(Subcategoria subcategoria) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            em.getTransaction().begin();
            if (subcategoria.getId() == null) {
                em.persist(subcategoria);
            } else {
                em.merge(subcategoria);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        
        return subcategoria;
    }

    public Subcategoria findById(Integer id) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        Subcategoria subcategoria = null;
        try {
            subcategoria = em.find(Subcategoria.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return subcategoria;
    }
    
    public List<Subcategoria> findAll(){
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<Subcategoria> subcategorias = null;
        try {
            subcategorias = em.createQuery("from Subcategoria s").getResultList();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return subcategorias;
    }
    

}
