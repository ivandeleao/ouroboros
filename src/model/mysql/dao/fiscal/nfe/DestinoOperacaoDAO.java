/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.fiscal.nfe;

import java.util.List;
import javax.persistence.Query;
import model.bootstrap.bean.nfe.DestinoOperacaoBs;
import model.bootstrap.dao.nfe.DestinoOperacaoBsDAO;
import model.mysql.bean.fiscal.nfe.DestinoOperacao;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class DestinoOperacaoDAO {

    public DestinoOperacao save(DestinoOperacao destinoOperacao) {
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
        }

        return destinoOperacao;
    }

    
    public DestinoOperacao findById(Integer id) {
        DestinoOperacao destinoOperacao = null;
        try {
            destinoOperacao = em.find(DestinoOperacao.class, id);
        } catch (Exception e) {
            System.err.println(e);
        }
        return destinoOperacao;
    }

    public List<DestinoOperacao> findAll() {
        List<DestinoOperacao> tiposEmissao = null;
        try {
            Query query = em.createQuery("from DestinoOperacao d order by id");

            tiposEmissao = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return tiposEmissao;
    }

    public void bootstrap() {
        List<DestinoOperacaoBs> destinoOperacaoBsList = new DestinoOperacaoBsDAO().findAll();

  
        em.getTransaction().begin();
        for (DestinoOperacaoBs destinoOperacaoBs : destinoOperacaoBsList) {
            DestinoOperacao destinoOperacao = new DestinoOperacao(destinoOperacaoBs.getId(), destinoOperacaoBs.getNome());
            if (findById(destinoOperacaoBs.getId()) == null) {
                em.persist(destinoOperacao);
            }
        }
        em.getTransaction().commit();
    }

}
