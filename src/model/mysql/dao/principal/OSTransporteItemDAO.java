/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.mysql.bean.principal.documento.OSTransporteItem;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class OSTransporteItemDAO {

    public OSTransporteItem save(OSTransporteItem osTransporteItem) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            em.getTransaction().begin();
            
            if (osTransporteItem.getId() == null) {
                em.persist(osTransporteItem);
            } else {
                em.merge(osTransporteItem);
            }
            
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Erro em OSTransporteItemDAO.save " + e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }

        return osTransporteItem;
    }

    public OSTransporteItem remove(OSTransporteItem OSTransporteItem) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        
        try {
            OSTransporteItem = em.find(OSTransporteItem.class, OSTransporteItem.getId());
            em.getTransaction().begin();
            em.remove(OSTransporteItem);
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        } finally {
             em.close();
        }
        
        return OSTransporteItem;
    }
    
    public OSTransporteItem findById(Integer id) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        OSTransporteItem osTransporteItem = null;
        try {
            osTransporteItem = em.find(OSTransporteItem.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return osTransporteItem;
    }

    public List<OSTransporteItem> findAll() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<OSTransporteItem> osTransportes = null;
        try {
            Query query = em.createQuery("from OSTransporteItem osti order by criacao desc");

            osTransportes = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return osTransportes;
    }


}
