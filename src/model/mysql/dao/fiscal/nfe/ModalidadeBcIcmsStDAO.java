/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.fiscal.nfe;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import model.mysql.bean.fiscal.nfe.ModalidadeBcIcmsSt;
import model.bootstrap.bean.nfe.ModalidadeBcIcmsStBs;
import model.bootstrap.dao.nfe.ModalidadeBcIcmsStBsDAO;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class ModalidadeBcIcmsStDAO {

    public ModalidadeBcIcmsSt save(ModalidadeBcIcmsSt modalidadeBcIcmsSt) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            em.getTransaction().begin();
            if (modalidadeBcIcmsSt.getId() == null) {
                em.persist(modalidadeBcIcmsSt);
            } else {
                em.merge(modalidadeBcIcmsSt);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }

        return modalidadeBcIcmsSt;
    }

    
    public ModalidadeBcIcmsSt findById(Integer id) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        ModalidadeBcIcmsSt modalidadeBcIcmsSt = null;
        try {
            modalidadeBcIcmsSt = em.find(ModalidadeBcIcmsSt.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return modalidadeBcIcmsSt;
    }

    public List<ModalidadeBcIcmsSt> findAll() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<ModalidadeBcIcmsSt> modalidades = null;
        try {
            Query query = em.createQuery("from " + ModalidadeBcIcmsSt.class.getSimpleName() + " m order by id");

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
        List<ModalidadeBcIcmsStBs> modalidadeBcIcmsStBsList = new ModalidadeBcIcmsStBsDAO().findAll();

  
        em.getTransaction().begin();
        for (ModalidadeBcIcmsStBs modalidadeBcIcmsStBs : modalidadeBcIcmsStBsList) {
            ModalidadeBcIcmsSt modalidadeBcIcmsSt = new ModalidadeBcIcmsSt(modalidadeBcIcmsStBs.getId(), modalidadeBcIcmsStBs.getNome());
            if (findById(modalidadeBcIcmsStBs.getId()) == null) {
                em.persist(modalidadeBcIcmsSt);
            }
        }
        em.getTransaction().commit();
        
        em.close();
    }

}
