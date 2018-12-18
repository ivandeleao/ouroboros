/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao.fiscal;

import java.util.List;
import javax.persistence.Query;
import model.bean.fiscal.SatEstado;
import model.bootstrap.bean.SatEstadoBs;
import model.bootstrap.dao.SatEstadoBsDAO;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class SatEstadoDAO {

    public SatEstado save(SatEstado satEstado) {
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
        }

        return satEstado;
    }

    public SatEstado findByCodigo(Integer codigo) {
        SatEstado satEstado = null;
        try {
            satEstado = em.find(SatEstado.class, codigo);
        } catch (Exception e) {
            System.err.println(e);
        }
        return satEstado;
    }

    public List<SatEstado> findAll() {
        List<SatEstado> satEstadoList = null;
        try {
            Query query = em.createQuery("from SatEstado se order by codigo");

            satEstadoList = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return satEstadoList;
    }

    public void bootstrap() {
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
    }
}
