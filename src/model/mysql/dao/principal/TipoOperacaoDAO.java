/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import model.mysql.bean.principal.documento.TipoOperacao;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class TipoOperacaoDAO {
    
    /**
     * Define os dados iniciais ao criar a tabela
     */
    public void bootstrap() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<TipoOperacao> tipoOperacaos = new ArrayList<>();
        tipoOperacaos.add(TipoOperacao.ENTRADA);
        tipoOperacaos.add(TipoOperacao.SAIDA);
        
        
        em.getTransaction().begin();
        for(TipoOperacao tipoOperacao : tipoOperacaos){
            if(findById(tipoOperacao.getId()) == null){
                em.persist(tipoOperacao);
            } else {
                em.merge(tipoOperacao);
            }
        }
        em.getTransaction().commit();

        em.close();
    }
    
    public TipoOperacao save(TipoOperacao tipoOperacao) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            em.getTransaction().begin();
            if (tipoOperacao.getId() == null) {
                em.persist(tipoOperacao);
            } else {
                em.merge(tipoOperacao);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }

        return tipoOperacao;
    }

    public TipoOperacao findById(Integer id) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        TipoOperacao tipoOperacao = null;
        try {
            tipoOperacao = em.find(TipoOperacao.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return tipoOperacao;
    }
    
    public List<TipoOperacao> findAll() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<TipoOperacao> tipoOperacaos = null;
        try {
            Query query = em.createQuery("from TipoOperacao tipoOperacao order by id");

            tipoOperacaos = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return tipoOperacaos;
    }
    
    
}
