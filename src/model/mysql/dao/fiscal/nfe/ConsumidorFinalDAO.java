/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.fiscal.nfe;

import java.util.List;
import javax.persistence.Query;
import model.mysql.bean.fiscal.nfe.ConsumidorFinal;
import model.bootstrap.bean.nfe.ConsumidorFinalBs;
import model.bootstrap.dao.nfe.ConsumidorFinalBsDAO;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class ConsumidorFinalDAO {

    public ConsumidorFinal save(ConsumidorFinal consumidorFinal) {
        try {
            em.getTransaction().begin();
            if (consumidorFinal.getId() == null) {
                em.persist(consumidorFinal);
            } else {
                em.merge(consumidorFinal);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        }

        return consumidorFinal;
    }

    
    public ConsumidorFinal findById(Integer id) {
        ConsumidorFinal consumidorFinal = null;
        try {
            consumidorFinal = em.find(ConsumidorFinal.class, id);
        } catch (Exception e) {
            System.err.println(e);
        }
        return consumidorFinal;
    }

    public List<ConsumidorFinal> findAll() {
        List<ConsumidorFinal> tiposEmissao = null;
        try {
            Query query = em.createQuery("from " + ConsumidorFinal.class.getSimpleName() + " c order by id");

            tiposEmissao = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return tiposEmissao;
    }

    public void bootstrap() {
        List<ConsumidorFinalBs> consumidorFinalBsList = new ConsumidorFinalBsDAO().findAll();

  
        em.getTransaction().begin();
        for (ConsumidorFinalBs consumidorFinalBs : consumidorFinalBsList) {
            ConsumidorFinal consumidorFinal = new ConsumidorFinal(consumidorFinalBs.getId(), consumidorFinalBs.getNome());
            if (findById(consumidorFinalBs.getId()) == null) {
                em.persist(consumidorFinal);
            }
        }
        em.getTransaction().commit();
    }

}
