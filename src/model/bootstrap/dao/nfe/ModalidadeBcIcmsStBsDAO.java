/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bootstrap.dao.nfe;

import java.util.List;
import javax.persistence.Query;
import model.bootstrap.bean.nfe.ModalidadeBcIcmsStBs;
import static ouroboros.Ouroboros.emBs;

/**
 *
 * @author ivand
 */
public class ModalidadeBcIcmsStBsDAO {
    public List<ModalidadeBcIcmsStBs> findAll() {
        List<ModalidadeBcIcmsStBs> modalidadeBcIcmsStBs = null;
        try {
            Query query = emBs.createQuery("from " + ModalidadeBcIcmsStBs.class.getSimpleName() + " m");

            modalidadeBcIcmsStBs = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return modalidadeBcIcmsStBs;
    }
}
