/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import model.mysql.bean.principal.documento.OSTransporte;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class OSTransporteDAO {

    public OSTransporte save(OSTransporte osTransporte) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            em.getTransaction().begin();
            
            osTransporte.setTotal();
            
            if (osTransporte.getId() == null) {
                em.persist(osTransporte);
            } else {
                em.merge(osTransporte);
            }
            
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Erro em OSTransporteDAO.save " + e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }

        return osTransporte;
    }

    public OSTransporte findById(Integer id) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        OSTransporte osTransporte = null;
        try {
            osTransporte = em.find(OSTransporte.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return osTransporte;
    }

    public List<OSTransporte> findAll() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<OSTransporte> osTransportes = null;
        try {
            Query query = em.createQuery("from OSTransporte ost order by criacao desc");

            osTransportes = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return osTransportes;
    }

}
