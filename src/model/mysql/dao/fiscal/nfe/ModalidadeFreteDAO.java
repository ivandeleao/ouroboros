/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.fiscal.nfe;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import model.mysql.bean.fiscal.nfe.ModalidadeFrete;
import model.bootstrap.bean.nfe.ModalidadeFreteBs;
import model.bootstrap.dao.nfe.ModalidadeFreteBsDAO;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class ModalidadeFreteDAO {

    public ModalidadeFrete save(ModalidadeFrete modalidadeBcIcms) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            em.getTransaction().begin();
            if (modalidadeBcIcms.getId() == null) {
                em.persist(modalidadeBcIcms);
            } else {
                em.merge(modalidadeBcIcms);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }

        return modalidadeBcIcms;
    }

    
    public ModalidadeFrete findById(Integer id) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        ModalidadeFrete modalidadeBcIcms = null;
        try {
            modalidadeBcIcms = em.find(ModalidadeFrete.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return modalidadeBcIcms;
    }

    public List<ModalidadeFrete> findAll() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<ModalidadeFrete> modalidades = null;
        try {
            Query query = em.createQuery("from " + ModalidadeFrete.class.getSimpleName() + " m order by id");

            modalidades = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return modalidades;
    }

    public void bootstrap() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<ModalidadeFreteBs> modalidadeBcIcmsBsList = new ModalidadeFreteBsDAO().findAll();

  
        em.getTransaction().begin();
        for (ModalidadeFreteBs modalidadeBcIcmsBs : modalidadeBcIcmsBsList) {
            ModalidadeFrete modalidadeBcIcms = new ModalidadeFrete(modalidadeBcIcmsBs.getId(), modalidadeBcIcmsBs.getNome());
            if (findById(modalidadeBcIcmsBs.getId()) == null) {
                em.persist(modalidadeBcIcms);
            }
        }
        em.getTransaction().commit();
        
        em.close();
    }

}
