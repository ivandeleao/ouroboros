/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal.financeiro;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import model.mysql.bean.principal.financeiro.CaixaPeriodo;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class CaixaPeriodoDAO {
    public CaixaPeriodo save(CaixaPeriodo caixaPeriodo) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            em.getTransaction().begin();
            if (caixaPeriodo.getId() == null) {
                em.persist(caixaPeriodo);
            } else {
                em.merge(caixaPeriodo);
            }
            //em.flush();
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        
        return caixaPeriodo;
    }

    public CaixaPeriodo findById(Integer id) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        CaixaPeriodo caixaPeriodo = null;
        try {
            caixaPeriodo = em.find(CaixaPeriodo.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return caixaPeriodo;
    }

    public List<CaixaPeriodo> findAll() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<CaixaPeriodo> caixaPeriodos = null;
        try {
            Query query = em.createQuery("from CaixaPeriodo cp order by id");

            caixaPeriodos = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return caixaPeriodos;
    }
    
    public void bootstrap(){
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<CaixaPeriodo> cps = new ArrayList<>();
        cps.add(new CaixaPeriodo(1, "PERÍODO ÚNICO"));
        
        em.getTransaction().begin();
        for(CaixaPeriodo cp : cps){
            if(findById(cp.getId()) == null){
                em.persist(cp);
            }
        }
        em.getTransaction().commit();
        
        em.close();
    }
}
