/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bootstrap.dao.nfe;

import java.util.List;
import javax.persistence.Query;
import model.bootstrap.bean.nfe.ModalidadeBcIcmsBs;
import static ouroboros.Ouroboros.emBs;

/**
 *
 * @author ivand
 */
public class ModalidadeBcIcmsBsDAO {
    public List<ModalidadeBcIcmsBs> findAll() {
        List<ModalidadeBcIcmsBs> modalidadeBcIcms = null;
        try {
            Query query = emBs.createQuery("from " + ModalidadeBcIcmsBs.class.getSimpleName() + " m");

            modalidadeBcIcms = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return modalidadeBcIcms;
    }
}
