/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.fiscal.nfe;

import java.util.List;
import javax.persistence.Query;
import model.mysql.bean.fiscal.nfe.FinalidadeEmissao;
import model.bootstrap.bean.nfe.FinalidadeEmissaoBs;
import model.bootstrap.dao.nfe.FinalidadeEmissaoBsDAO;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class FinalidadeEmissaoDAO {

    public FinalidadeEmissao save(FinalidadeEmissao finalidadeEmissao) {
        try {
            em.getTransaction().begin();
            if (finalidadeEmissao.getId() == null) {
                em.persist(finalidadeEmissao);
            } else {
                em.merge(finalidadeEmissao);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        }

        return finalidadeEmissao;
    }

    
    public FinalidadeEmissao findById(Integer id) {
        FinalidadeEmissao finalidadeEmissao = null;
        try {
            finalidadeEmissao = em.find(FinalidadeEmissao.class, id);
        } catch (Exception e) {
            System.err.println(e);
        }
        return finalidadeEmissao;
    }

    public List<FinalidadeEmissao> findAll() {
        List<FinalidadeEmissao> tiposEmissao = null;
        try {
            Query query = em.createQuery("from " + FinalidadeEmissao.class.getSimpleName() + " n order by id");

            tiposEmissao = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return tiposEmissao;
    }

    public void bootstrap() {
        List<FinalidadeEmissaoBs> finalidadeEmissaoBsList = new FinalidadeEmissaoBsDAO().findAll();

  
        em.getTransaction().begin();
        for (FinalidadeEmissaoBs finalidadeEmissaoBs : finalidadeEmissaoBsList) {
            FinalidadeEmissao finalidadeEmissao = new FinalidadeEmissao(finalidadeEmissaoBs.getId(), finalidadeEmissaoBs.getNome());
            if (findById(finalidadeEmissaoBs.getId()) == null) {
                em.persist(finalidadeEmissao);
            }
        }
        em.getTransaction().commit();
    }

}
