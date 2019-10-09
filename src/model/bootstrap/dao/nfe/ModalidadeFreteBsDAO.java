/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bootstrap.dao.nfe;

import java.util.List;
import javax.persistence.Query;
import model.bootstrap.bean.nfe.ModalidadeFreteBs;
import static ouroboros.Ouroboros.emBs;

/**
 *
 * @author ivand
 */
public class ModalidadeFreteBsDAO {
    public List<ModalidadeFreteBs> findAll() {
        List<ModalidadeFreteBs> modalidadeFrete = null;
        try {
            Query query = emBs.createQuery("from " + ModalidadeFreteBs.class.getSimpleName() + " m");

            modalidadeFrete = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return modalidadeFrete;
    }
}
