/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.fiscal.nfe;

import java.util.List;
import javax.persistence.Query;
import model.mysql.bean.fiscal.nfe.TipoEmissao;
import model.bootstrap.bean.nfe.TipoEmissaoBs;
import model.bootstrap.dao.nfe.TipoEmissaoBsDAO;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class TipoEmissaoDAO {

    public TipoEmissao save(TipoEmissao tipoEmissao) {
        try {
            em.getTransaction().begin();
            if (tipoEmissao.getId() == null) {
                em.persist(tipoEmissao);
            } else {
                em.merge(tipoEmissao);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        }

        return tipoEmissao;
    }

    
    public TipoEmissao findById(Integer id) {
        TipoEmissao tipoEmissao = null;
        try {
            tipoEmissao = em.find(TipoEmissao.class, id);
        } catch (Exception e) {
            System.err.println(e);
        }
        return tipoEmissao;
    }

    public List<TipoEmissao> findAll() {
        List<TipoEmissao> tiposEmissao = null;
        try {
            Query query = em.createQuery("from TipoEmissao t order by id");

            tiposEmissao = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return tiposEmissao;
    }

    public void bootstrap() {
        List<TipoEmissaoBs> tipoEmissaoBsList = new TipoEmissaoBsDAO().findAll();

  
        em.getTransaction().begin();
        for (TipoEmissaoBs tipoEmissaoBs : tipoEmissaoBsList) {
            TipoEmissao tipoEmissao = new TipoEmissao(tipoEmissaoBs.getId(), tipoEmissaoBs.getNome());
            if (findById(tipoEmissaoBs.getId()) == null) {
                em.persist(tipoEmissao);
            }
        }
        em.getTransaction().commit();
    }

}
