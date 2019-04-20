/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bootstrap.dao.nfe;

import java.util.List;
import javax.persistence.Query;
import model.bootstrap.bean.nfe.ConsumidorFinalBs;
import static ouroboros.Ouroboros.emBs;

/**
 *
 * @author ivand
 */
public class ConsumidorFinalBsDAO {
    public List<ConsumidorFinalBs> findAll() {
        List<ConsumidorFinalBs> consumidorFinal = null;
        try {
            Query query = emBs.createQuery("from " + ConsumidorFinalBs.class.getSimpleName() + " f");

            consumidorFinal = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return consumidorFinal;
    }
}
