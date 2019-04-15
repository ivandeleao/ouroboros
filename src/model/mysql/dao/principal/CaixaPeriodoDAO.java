/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import model.mysql.bean.principal.CaixaPeriodo;
import model.mysql.bean.principal.CaixaPeriodo;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class CaixaPeriodoDAO {
    public CaixaPeriodo save(CaixaPeriodo caixaPeriodo) {
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
        }
        return caixaPeriodo;
    }

    public CaixaPeriodo findById(Integer id) {
        CaixaPeriodo caixaPeriodo = null;
        try {
            caixaPeriodo = em.find(CaixaPeriodo.class, id);
        } catch (Exception e) {
            System.err.println(e);
        }
        return caixaPeriodo;
    }

    public List<CaixaPeriodo> findAll() {
        List<CaixaPeriodo> caixaPeriodos = null;
        try {
            Query query = em.createQuery("from CaixaPeriodo cp order by id");

            caixaPeriodos = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return caixaPeriodos;
    }
    
    public void bootstrap(){
        List<CaixaPeriodo> cps = new ArrayList<>();
        cps.add(new CaixaPeriodo(1, "PERÍODO ÚNICO"));
        
        em.getTransaction().begin();
        for(CaixaPeriodo cp : cps){
            if(findById(cp.getId()) == null){
                em.persist(cp);
            }
        }
        em.getTransaction().commit();
    }
}
