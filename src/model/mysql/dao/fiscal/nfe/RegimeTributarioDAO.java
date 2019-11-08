/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.fiscal.nfe;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import model.mysql.bean.fiscal.nfe.RegimeTributario;
import model.bootstrap.bean.nfe.RegimeTributarioBs;
import model.bootstrap.dao.nfe.RegimeTributarioBsDAO;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class RegimeTributarioDAO {

    public RegimeTributario save(RegimeTributario regimeTributario) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            em.getTransaction().begin();
            if (regimeTributario.getId() == null) {
                em.persist(regimeTributario);
            } else {
                em.merge(regimeTributario);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }

        return regimeTributario;
    }

    
    public RegimeTributario findById(Integer id) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        RegimeTributario regimeTributario = null;
        try {
            regimeTributario = em.find(RegimeTributario.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return regimeTributario;
    }

    public List<RegimeTributario> findAll() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<RegimeTributario> regimeTributarioList = null;
        try {
            Query query = em.createQuery("from RegimeTributario r order by id");

            regimeTributarioList = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return regimeTributarioList;
    }

    public void bootstrap() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<RegimeTributarioBs> regimeTributarioBsList = new RegimeTributarioBsDAO().findAll();

  
        em.getTransaction().begin();
        for (RegimeTributarioBs regimeTributarioBs : regimeTributarioBsList) {
            RegimeTributario regimeTributario = new RegimeTributario(regimeTributarioBs.getId(), regimeTributarioBs.getNome());
            if (findById(regimeTributarioBs.getId()) == null) {
                em.persist(regimeTributario);
            }
        }
        em.getTransaction().commit();
        
        em.close();
    }

}
