/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.fiscal.nfe;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import model.mysql.bean.fiscal.nfe.MotivoDesoneracao;
import model.bootstrap.bean.nfe.MotivoDesoneracaoBs;
import model.bootstrap.dao.nfe.MotivoDesoneracaoBsDAO;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class MotivoDesoneracaoDAO {

    public MotivoDesoneracao save(MotivoDesoneracao motivoDesoneracao) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            em.getTransaction().begin();
            if (motivoDesoneracao.getId() == null) {
                em.persist(motivoDesoneracao);
            } else {
                em.merge(motivoDesoneracao);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }

        return motivoDesoneracao;
    }

    
    public MotivoDesoneracao findById(Integer id) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        MotivoDesoneracao motivoDesoneracao = null;
        try {
            motivoDesoneracao = em.find(MotivoDesoneracao.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return motivoDesoneracao;
    }

    public List<MotivoDesoneracao> findAll() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<MotivoDesoneracao> modalidades = null;
        try {
            Query query = em.createQuery("from " + MotivoDesoneracao.class.getSimpleName() + " m order by id");

            modalidades = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return modalidades;
    }

    public void bootstrap() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<MotivoDesoneracaoBs> motivoDesoneracaoBsList = new MotivoDesoneracaoBsDAO().findAll();

  
        em.getTransaction().begin();
        for (MotivoDesoneracaoBs motivoDesoneracaoBs : motivoDesoneracaoBsList) {
            MotivoDesoneracao motivoDesoneracao = new MotivoDesoneracao(motivoDesoneracaoBs.getId(), motivoDesoneracaoBs.getNome());
            if (findById(motivoDesoneracaoBs.getId()) == null) {
                em.persist(motivoDesoneracao);
            }
        }
        em.getTransaction().commit();
        
        em.close();
    }

}
