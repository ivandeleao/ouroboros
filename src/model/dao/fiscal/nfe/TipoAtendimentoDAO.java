/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao.fiscal.nfe;

import java.util.List;
import javax.persistence.Query;
import model.bean.fiscal.nfe.TipoAtendimento;
import model.bootstrap.bean.nfe.TipoAtendimentoBs;
import model.bootstrap.dao.nfe.TipoAtendimentoBsDAO;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class TipoAtendimentoDAO {

    public TipoAtendimento save(TipoAtendimento tipoAtendimento) {
        try {
            em.getTransaction().begin();
            if (tipoAtendimento.getId() == null) {
                em.persist(tipoAtendimento);
            } else {
                em.merge(tipoAtendimento);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        }

        return tipoAtendimento;
    }

    
    public TipoAtendimento findById(Integer id) {
        TipoAtendimento tipoAtendimento = null;
        try {
            tipoAtendimento = em.find(TipoAtendimento.class, id);
        } catch (Exception e) {
            System.err.println(e);
        }
        return tipoAtendimento;
    }

    public List<TipoAtendimento> findAll() {
        List<TipoAtendimento> tipoAtendimentoList = null;
        try {
            Query query = em.createQuery("from TipoAtendimento t order by id");

            tipoAtendimentoList = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return tipoAtendimentoList;
    }

    public void bootstrap() {
        List<TipoAtendimentoBs> tipoAtendimentoBsList = new TipoAtendimentoBsDAO().findAll();

  
        em.getTransaction().begin();
        for (TipoAtendimentoBs tipoAtendimentoBs : tipoAtendimentoBsList) {
            TipoAtendimento tipoAtendimento = new TipoAtendimento(tipoAtendimentoBs.getId(), tipoAtendimentoBs.getNome());
            if (findById(tipoAtendimentoBs.getId()) == null) {
                em.persist(tipoAtendimento);
            }
        }
        em.getTransaction().commit();
    }

}
