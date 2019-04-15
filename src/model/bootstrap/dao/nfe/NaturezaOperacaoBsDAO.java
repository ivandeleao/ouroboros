/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bootstrap.dao.nfe;

import java.util.List;
import javax.persistence.Query;
import model.bootstrap.bean.nfe.NaturezaOperacaoBs;
import static ouroboros.Ouroboros.emBs;

/**
 *
 * @author ivand
 */
public class NaturezaOperacaoBsDAO {
    public List<NaturezaOperacaoBs> findAll() {
        List<NaturezaOperacaoBs> naturezasOperacao = null;
        try {
            Query query = emBs.createQuery("from " + NaturezaOperacaoBs.class.getSimpleName() + " n");

            naturezasOperacao = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return naturezasOperacao;
    }
}
