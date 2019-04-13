/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bootstrap.dao.nfe;

import java.util.List;
import javax.persistence.Query;
import model.bootstrap.bean.nfe.TipoAtendimentoBs;
import static ouroboros.Ouroboros.emBs;

/**
 *
 * @author ivand
 */
public class TipoAtendimentoBsDAO {
    public List<TipoAtendimentoBs> findAll() {
        List<TipoAtendimentoBs> tiposAtendimento = null;
        try {
            Query query = emBs.createQuery("from TipoAtendimentoBs t");

            tiposAtendimento = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return tiposAtendimento;
    }
}
