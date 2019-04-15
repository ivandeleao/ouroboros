/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bootstrap.dao.nfe;

import java.util.List;
import javax.persistence.Query;
import model.bootstrap.bean.nfe.DestinoOperacaoBs;
import static ouroboros.Ouroboros.emBs;

/**
 *
 * @author ivand
 */
public class DestinoOperacaoBsDAO {
    public List<DestinoOperacaoBs> findAll() {
        List<DestinoOperacaoBs> destinosOperacaoBs = null;
        try {
            Query query = emBs.createQuery("from DestinoOperacaoBs t");

            destinosOperacaoBs = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return destinosOperacaoBs;
    }
}
