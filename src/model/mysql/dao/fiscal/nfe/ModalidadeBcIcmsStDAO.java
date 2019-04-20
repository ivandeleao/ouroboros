/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.fiscal.nfe;

import java.util.List;
import javax.persistence.Query;
import model.mysql.bean.fiscal.nfe.ModalidadeBcIcmsSt;
import model.bootstrap.bean.nfe.ModalidadeBcIcmsStBs;
import model.bootstrap.dao.nfe.ModalidadeBcIcmsStBsDAO;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class ModalidadeBcIcmsStDAO {

    public ModalidadeBcIcmsSt save(ModalidadeBcIcmsSt modalidadeBcIcmsSt) {
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
        }

        return modalidadeBcIcmsSt;
    }

    
    public ModalidadeBcIcmsSt findById(Integer id) {
        ModalidadeBcIcmsSt modalidadeBcIcmsSt = null;
        try {
            modalidadeBcIcmsSt = em.find(ModalidadeBcIcmsSt.class, id);
        } catch (Exception e) {
            System.err.println(e);
        }
        return modalidadeBcIcmsSt;
    }

    public List<ModalidadeBcIcmsSt> findAll() {
        List<ModalidadeBcIcmsSt> modalidades = null;
        try {
            Query query = em.createQuery("from " + ModalidadeBcIcmsSt.class.getSimpleName() + " m order by id");

            modalidades = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return modalidades;
    }

    public void bootstrap() {
        List<ModalidadeBcIcmsStBs> modalidadeBcIcmsStBsList = new ModalidadeBcIcmsStBsDAO().findAll();

  
        em.getTransaction().begin();
        for (ModalidadeBcIcmsStBs modalidadeBcIcmsStBs : modalidadeBcIcmsStBsList) {
            ModalidadeBcIcmsSt modalidadeBcIcmsSt = new ModalidadeBcIcmsSt(modalidadeBcIcmsStBs.getId(), modalidadeBcIcmsStBs.getNome());
            if (findById(modalidadeBcIcmsStBs.getId()) == null) {
                em.persist(modalidadeBcIcmsSt);
            }
        }
        em.getTransaction().commit();
    }

}
