/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bootstrap.dao.nfe;

import java.util.List;
import javax.persistence.Query;
import model.bootstrap.bean.nfe.MotivoDesoneracaoBs;
import static ouroboros.Ouroboros.emBs;

/**
 *
 * @author ivand
 */
public class MotivoDesoneracaoBsDAO {
    public List<MotivoDesoneracaoBs> findAll() {
        List<MotivoDesoneracaoBs> motivoDesoneracao = null;
        try {
            Query query = emBs.createQuery("from " + MotivoDesoneracaoBs.class.getSimpleName() + " m");     

            motivoDesoneracao = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return motivoDesoneracao;
    }
}
