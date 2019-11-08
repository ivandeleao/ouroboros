/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.fiscal.nfe;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import model.mysql.bean.fiscal.nfe.NaturezaOperacao;
import model.bootstrap.bean.nfe.NaturezaOperacaoBs;
import model.bootstrap.dao.nfe.NaturezaOperacaoBsDAO;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class NaturezaOperacaoDAO {

    public NaturezaOperacao save(NaturezaOperacao naturezaOperacao) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            em.getTransaction().begin();
            if (naturezaOperacao.getId() == null) {
                em.persist(naturezaOperacao);
            } else {
                em.merge(naturezaOperacao);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }

        return naturezaOperacao;
    }

    
    public NaturezaOperacao findById(Integer id) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        NaturezaOperacao naturezaOperacao = null;
        try {
            naturezaOperacao = em.find(NaturezaOperacao.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return naturezaOperacao;
    }

    public List<NaturezaOperacao> findAll() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<NaturezaOperacao> tiposEmissao = null;
        try {
            Query query = em.createQuery("from " + NaturezaOperacao.class.getSimpleName() + " n order by id");

            tiposEmissao = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return tiposEmissao;
    }

    public void bootstrap() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<NaturezaOperacaoBs> naturezaOperacaoBsList = new NaturezaOperacaoBsDAO().findAll();

  
        em.getTransaction().begin();
        for (NaturezaOperacaoBs naturezaOperacaoBs : naturezaOperacaoBsList) {
            NaturezaOperacao naturezaOperacao = new NaturezaOperacao(naturezaOperacaoBs.getId(), naturezaOperacaoBs.getNome());
            if (findById(naturezaOperacaoBs.getId()) == null) {
                em.persist(naturezaOperacao);
            }
        }
        em.getTransaction().commit();
        
        em.close();
    }

}
