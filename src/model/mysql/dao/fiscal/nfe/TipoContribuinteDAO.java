/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.fiscal.nfe;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import model.mysql.bean.fiscal.nfe.TipoContribuinte;
import model.bootstrap.bean.nfe.TipoContribuinteBs;
import model.bootstrap.dao.nfe.TipoContribuinteBsDAO;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class TipoContribuinteDAO {

    public TipoContribuinte save(TipoContribuinte tipoContribuinte) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            em.getTransaction().begin();
            if (tipoContribuinte.getId() == null) {
                em.persist(tipoContribuinte);
            } else {
                em.merge(tipoContribuinte);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }

        return tipoContribuinte;
    }

    
    public TipoContribuinte findById(Integer id) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        TipoContribuinte tipoContribuinte = null;
        try {
            tipoContribuinte = em.find(TipoContribuinte.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return tipoContribuinte;
    }

    public List<TipoContribuinte> findAll() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<TipoContribuinte> tiposContribuinte = null;
        try {
            Query query = em.createQuery("from " + TipoContribuinte.class.getSimpleName() + " t order by id");

            tiposContribuinte = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return tiposContribuinte;
    }

    public void bootstrap() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<TipoContribuinteBs> tipoContribuinteBsList = new TipoContribuinteBsDAO().findAll();

  
        em.getTransaction().begin();
        for (TipoContribuinteBs tipoContribuinteBs : tipoContribuinteBsList) {
            TipoContribuinte tipoContribuinte = new TipoContribuinte(tipoContribuinteBs.getId(), tipoContribuinteBs.getNome());
            if (findById(tipoContribuinteBs.getId()) == null) {
                em.persist(tipoContribuinte);
            }
        }
        em.getTransaction().commit();
        
        em.close();
    }

}
