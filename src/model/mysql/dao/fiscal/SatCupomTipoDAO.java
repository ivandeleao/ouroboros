/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.fiscal;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import model.mysql.bean.fiscal.SatCupomTipo;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class SatCupomTipoDAO {
    
    /**
     * Define os dados iniciais ao criar a tabela
     */
    public void bootstrap() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<SatCupomTipo> satCupomTipos = new ArrayList<>();
        satCupomTipos.add(SatCupomTipo.EMISSAO);
        satCupomTipos.add(SatCupomTipo.CANCELAMENTO);
        
        
        em.getTransaction().begin();
        for(SatCupomTipo satCupomTipo : satCupomTipos){
            if(findById(satCupomTipo.getId()) == null){
                em.persist(satCupomTipo);
            } else {
                em.merge(satCupomTipo);
            }
        }
        em.getTransaction().commit();

        em.close();
    }
    
    public SatCupomTipo save(SatCupomTipo satCupomTipo) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            em.getTransaction().begin();
            if (satCupomTipo.getId() == null) {
                em.persist(satCupomTipo);
            } else {
                em.merge(satCupomTipo);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }

        return satCupomTipo;
    }

    public SatCupomTipo findById(Integer id) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        SatCupomTipo satCupomTipo = null;
        try {
            satCupomTipo = em.find(SatCupomTipo.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return satCupomTipo;
    }
    
    public List<SatCupomTipo> findAll() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<SatCupomTipo> satCupomTipos = null;
        try {
            Query query = em.createQuery("from SatCupomTipo satCupomTipo order by id");

            satCupomTipos = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return satCupomTipos;
    }
    
    
}
