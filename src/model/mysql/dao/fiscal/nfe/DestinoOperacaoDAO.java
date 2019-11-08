/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.fiscal.nfe;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import model.bootstrap.bean.nfe.DestinoOperacaoBs;
import model.bootstrap.dao.nfe.DestinoOperacaoBsDAO;
import model.mysql.bean.fiscal.nfe.DestinoOperacao;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class DestinoOperacaoDAO {

    public DestinoOperacao save(DestinoOperacao destinoOperacao) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            em.getTransaction().begin();
            if (destinoOperacao.getId() == null) {
                em.persist(destinoOperacao);
            } else {
                em.merge(destinoOperacao);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }

        return destinoOperacao;
    }

    
    public DestinoOperacao findById(Integer id) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        DestinoOperacao destinoOperacao = null;
        try {
            destinoOperacao = em.find(DestinoOperacao.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        return destinoOperacao;
    }

    public List<DestinoOperacao> findAll() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<DestinoOperacao> tiposEmissao = null;
        try {
            Query query = em.createQuery("from DestinoOperacao d order by id");

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
        List<DestinoOperacaoBs> destinoOperacaoBsList = new DestinoOperacaoBsDAO().findAll();

  
        em.getTransaction().begin();
        for (DestinoOperacaoBs destinoOperacaoBs : destinoOperacaoBsList) {
            DestinoOperacao destinoOperacao = new DestinoOperacao(destinoOperacaoBs.getId(), destinoOperacaoBs.getNome());
            if (findById(destinoOperacaoBs.getId()) == null) {
                em.persist(destinoOperacao);
            }
        }
        em.getTransaction().commit();
        
        em.close();
    }

}
