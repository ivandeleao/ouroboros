/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao.fiscal;

import java.util.List;
import javax.persistence.Query;
import model.bean.fiscal.Ibpt;
import model.bootstrap.bean.IbptBs;
import model.bootstrap.dao.IbptBsDAO;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class IbptDAO {

    public Ibpt save(Ibpt ibpt) {
        try {
            em.getTransaction().begin();
            if (ibpt.getCodigo() == null) {
                em.persist(ibpt);
            } else {
                em.merge(ibpt);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        }

        return ibpt;
    }

    
    public Ibpt findByCodigo(String strCodigo) {
        Integer codigo = Integer.parseInt(strCodigo);
        return findByCodigo(codigo);
    }
    
    public Ibpt findByCodigo(Integer codigo) {
        Ibpt ibpt = null;
        try {
            ibpt = em.find(Ibpt.class, codigo);
        } catch (Exception e) {
            System.err.println(e);
        }
        return ibpt;
    }

    public List<Ibpt> findAll() {
        List<Ibpt> ibptList = null;
        try {
            Query query = em.createQuery("from Ibpt i order by codigo");

            ibptList = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return ibptList;
    }

    public void bootstrap() {
        List<IbptBs> ibptBsList = new IbptBsDAO().findAll();

  
        em.getTransaction().begin();
        for (IbptBs ibptBs : ibptBsList) {
            Ibpt ibpt = new Ibpt(ibptBs.getCodigo(), ibptBs.getEx(), ibptBs.getTabela(), ibptBs.getAliqNac(), ibptBs.getAliqImp());
            if (findByCodigo(ibptBs.getCodigo()) == null) {
                em.persist(ibpt);
            }
        }
        em.getTransaction().commit();
    }

}
