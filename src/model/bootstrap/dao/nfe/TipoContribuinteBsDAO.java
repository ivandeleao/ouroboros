/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bootstrap.dao.nfe;

import java.util.List;
import javax.persistence.Query;
import model.bootstrap.bean.nfe.TipoContribuinteBs;
import static ouroboros.Ouroboros.emBs;

/**
 *
 * @author ivand
 */
public class TipoContribuinteBsDAO {
    public List<TipoContribuinteBs> findAll() {
        List<TipoContribuinteBs> tipoContribuinte = null;
        try {
            Query query = emBs.createQuery("from " + TipoContribuinteBs.class.getSimpleName() + " t");

            tipoContribuinte = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return tipoContribuinte;
    }
}
