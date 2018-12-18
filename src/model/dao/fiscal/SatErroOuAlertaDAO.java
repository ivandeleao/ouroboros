/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao.fiscal;

import java.util.List;
import javax.persistence.Query;
import model.bean.fiscal.Ncm;
import model.bean.fiscal.SatErroOuAlerta;
import model.bootstrap.bean.NcmBs;
import model.bootstrap.bean.SatErroOuAlertaBs;
import model.bootstrap.dao.NcmBsDAO;
import model.bootstrap.dao.SatErroOuAlertaBsDAO;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class SatErroOuAlertaDAO {

    public SatErroOuAlerta save(SatErroOuAlerta satErroOuAlerta) {
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
        }

        return satErroOuAlerta;
    }
/*
    public SatErroOuAlerta findByCodigo(String strCodigo) {
        Integer codigo = Integer.parseInt(strCodigo);
        return findByCodigo(codigo);
    }*/

    public SatErroOuAlerta findByCodigo(String codigo) {
        SatErroOuAlerta satErroOuAlerta = null;
        try {
            satErroOuAlerta = em.find(SatErroOuAlerta.class, codigo);
        } catch (Exception e) {
            System.err.println(e);
        }
        return satErroOuAlerta;
    }

    public List<SatErroOuAlerta> findAll() {
        List<SatErroOuAlerta> satErroOuAlertaList = null;
        try {
            Query query = em.createQuery("from SatErroOuAlerta sea order by codigo");

            satErroOuAlertaList = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return satErroOuAlertaList;
    }

    public void bootstrap() {
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
    }
}
