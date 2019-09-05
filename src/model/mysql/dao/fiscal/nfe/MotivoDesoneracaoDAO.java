/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.fiscal.nfe;

import java.util.List;
import javax.persistence.Query;
import model.mysql.bean.fiscal.nfe.MotivoDesoneracao;
import model.bootstrap.bean.nfe.MotivoDesoneracaoBs;
import model.bootstrap.dao.nfe.MotivoDesoneracaoBsDAO;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class MotivoDesoneracaoDAO {

    public MotivoDesoneracao save(MotivoDesoneracao motivoDesoneracao) {
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
        }

        return motivoDesoneracao;
    }

    
    public MotivoDesoneracao findById(Integer id) {
        MotivoDesoneracao motivoDesoneracao = null;
        try {
            motivoDesoneracao = em.find(MotivoDesoneracao.class, id);
        } catch (Exception e) {
            System.err.println(e);
        }
        return motivoDesoneracao;
    }

    public List<MotivoDesoneracao> findAll() {
        List<MotivoDesoneracao> modalidades = null;
        try {
            Query query = em.createQuery("from " + MotivoDesoneracao.class.getSimpleName() + " m order by id");

            modalidades = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return modalidades;
    }

    public void bootstrap() {
        List<MotivoDesoneracaoBs> motivoDesoneracaoBsList = new MotivoDesoneracaoBsDAO().findAll();

  
        em.getTransaction().begin();
        for (MotivoDesoneracaoBs motivoDesoneracaoBs : motivoDesoneracaoBsList) {
            MotivoDesoneracao motivoDesoneracao = new MotivoDesoneracao(motivoDesoneracaoBs.getId(), motivoDesoneracaoBs.getNome());
            if (findById(motivoDesoneracaoBs.getId()) == null) {
                em.persist(motivoDesoneracao);
            }
        }
        em.getTransaction().commit();
    }

}
