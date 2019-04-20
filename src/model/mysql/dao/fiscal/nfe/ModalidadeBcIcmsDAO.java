/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.fiscal.nfe;

import java.util.List;
import javax.persistence.Query;
import model.mysql.bean.fiscal.nfe.ModalidadeBcIcms;
import model.bootstrap.bean.nfe.ModalidadeBcIcmsBs;
import model.bootstrap.dao.nfe.ModalidadeBcIcmsBsDAO;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class ModalidadeBcIcmsDAO {

    public ModalidadeBcIcms save(ModalidadeBcIcms modalidadeBcIcms) {
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
        }

        return modalidadeBcIcms;
    }

    
    public ModalidadeBcIcms findById(Integer id) {
        ModalidadeBcIcms modalidadeBcIcms = null;
        try {
            modalidadeBcIcms = em.find(ModalidadeBcIcms.class, id);
        } catch (Exception e) {
            System.err.println(e);
        }
        return modalidadeBcIcms;
    }

    public List<ModalidadeBcIcms> findAll() {
        List<ModalidadeBcIcms> modalidades = null;
        try {
            Query query = em.createQuery("from " + ModalidadeBcIcms.class.getSimpleName() + " m order by id");

            modalidades = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return modalidades;
    }

    public void bootstrap() {
        List<ModalidadeBcIcmsBs> modalidadeBcIcmsBsList = new ModalidadeBcIcmsBsDAO().findAll();

  
        em.getTransaction().begin();
        for (ModalidadeBcIcmsBs modalidadeBcIcmsBs : modalidadeBcIcmsBsList) {
            ModalidadeBcIcms modalidadeBcIcms = new ModalidadeBcIcms(modalidadeBcIcmsBs.getId(), modalidadeBcIcmsBs.getNome());
            if (findById(modalidadeBcIcmsBs.getId()) == null) {
                em.persist(modalidadeBcIcms);
            }
        }
        em.getTransaction().commit();
    }

}
