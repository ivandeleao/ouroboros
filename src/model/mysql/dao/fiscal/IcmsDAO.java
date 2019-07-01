/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.fiscal;

import java.util.List;
import javax.persistence.Query;
import model.bootstrap.bean.nfe.IcmsBs;
import model.bootstrap.dao.nfe.IcmsBsDAO;
import model.mysql.bean.fiscal.Icms;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class IcmsDAO {

    public Icms save(Icms icms) {
        try {
            em.getTransaction().begin();
            if (icms.getCodigo() == null) {
                em.persist(icms);
            } else {
                em.merge(icms);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        }

        return icms;
    }

    public Icms findById(Integer id) {
        Icms icms = null;
        try {
            icms = em.find(Icms.class, id);
        } catch (Exception e) {
            System.err.println(e);
        }
        return icms;
    }

    public List<Icms> findAll() {
        List<Icms> icmsList = null;
        try {
            Query query = em.createQuery("from Icms i order by codigo, id");

            icmsList = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return icmsList;
    }

    /**
     * Define os dados iniciais ao criar a tabela
     */
    public void bootstrap() {
        List<IcmsBs> icmsBsList = new IcmsBsDAO().findAll();
        

        em.getTransaction().begin();
        for (IcmsBs icmsBs : icmsBsList) {
            Icms icms = new Icms(icmsBs.getId(), icmsBs.getCodigo(), icmsBs.getDescricao());
            if (findById(icmsBs.getId()) == null) {
                em.persist(icms);
            } else {
                em.merge(icms);
            }
        }
        
        
        em.getTransaction().commit();

    }
}
