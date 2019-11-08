/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.fiscal;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import model.mysql.bean.fiscal.SatEstado;
import model.bootstrap.bean.SatEstadoBs;
import model.bootstrap.dao.SatEstadoBsDAO;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class SatEstadoDAO {

    public SatEstado save(SatEstado satEstado) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            em.getTransaction().begin();
            if (satEstado.getCodigo() == null) {
                em.persist(satEstado);
            } else {
                em.merge(satEstado);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }

        return satEstado;
    }

    public SatEstado findByCodigo(Integer codigo) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        SatEstado satEstado = null;
        try {
            satEstado = em.find(SatEstado.class, codigo);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return satEstado;
    }

    public List<SatEstado> findAll() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<SatEstado> satEstadoList = null;
        try {
            Query query = em.createQuery("from SatEstado se order by codigo");

            satEstadoList = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return satEstadoList;
    }

    public void bootstrap() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<SatEstadoBs> satEstadoBsList = new SatEstadoBsDAO().findAll();

        em.getTransaction().begin();
        for (SatEstadoBs satEstadoBs : satEstadoBsList) {
            SatEstado satEstado = new SatEstado(satEstadoBs.getCodigo(), satEstadoBs.getDescricao());
            if (findByCodigo(satEstadoBs.getCodigo()) == null) {
                em.persist(satEstado);
            }else{
                em.merge(satEstado);
            }
        }
        em.getTransaction().commit();
        
        em.close();
    }
}
