/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.fiscal;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import model.mysql.bean.fiscal.SatErroOuAlerta;
import model.bootstrap.bean.SatErroOuAlertaBs;
import model.bootstrap.dao.SatErroOuAlertaBsDAO;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class SatErroOuAlertaDAO {

    public SatErroOuAlerta save(SatErroOuAlerta satErroOuAlerta) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            em.getTransaction().begin();
            if (satErroOuAlerta.getCodigo() == null) {
                em.persist(satErroOuAlerta);
            } else {
                em.merge(satErroOuAlerta);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }

        return satErroOuAlerta;
    }
/*
    public SatErroOuAlerta findByCodigo(String strCodigo) {
        Integer codigo = Integer.parseInt(strCodigo);
        return findByCodigo(codigo);
    }*/

    public SatErroOuAlerta findByCodigo(String codigo) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        SatErroOuAlerta satErroOuAlerta = null;
        try {
            satErroOuAlerta = em.find(SatErroOuAlerta.class, codigo);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return satErroOuAlerta;
    }

    public List<SatErroOuAlerta> findAll() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<SatErroOuAlerta> satErroOuAlertaList = null;
        try {
            Query query = em.createQuery("from SatErroOuAlerta sea order by codigo");

            satErroOuAlertaList = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return satErroOuAlertaList;
    }

    public void bootstrap() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<SatErroOuAlertaBs> satErroOuAlertaBsList = new SatErroOuAlertaBsDAO().findAll();

        em.getTransaction().begin();
        for (SatErroOuAlertaBs satErroOuAlertaBs : satErroOuAlertaBsList) {
            SatErroOuAlerta satErroOuAlerta = new SatErroOuAlerta(satErroOuAlertaBs.getCodigo(), satErroOuAlertaBs.getTipo(), satErroOuAlertaBs.getDescricao());
            if (findByCodigo(satErroOuAlertaBs.getCodigo()) == null) {
                em.persist(satErroOuAlerta);
            }else{
                em.merge(satErroOuAlerta);
            }
        }
        em.getTransaction().commit();
        
        em.close();
    }
}
