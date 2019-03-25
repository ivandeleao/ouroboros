/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao.fiscal;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import model.bean.fiscal.SatCupomTipo;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class SatCupomTipoDAO {
    
    /**
     * Define os dados iniciais ao criar a tabela
     */
    public void bootstrap() {
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

    }
    
    public SatCupomTipo save(SatCupomTipo satCupomTipo) {
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
        }

        return satCupomTipo;
    }

    public SatCupomTipo findById(Integer id) {
        SatCupomTipo satCupomTipo = null;
        try {
            satCupomTipo = em.find(SatCupomTipo.class, id);
        } catch (Exception e) {
            System.err.println(e);
        }
        return satCupomTipo;
    }
    
    public List<SatCupomTipo> findAll() {
        List<SatCupomTipo> satCupomTipos = null;
        try {
            Query query = em.createQuery("from SatCupomTipo satCupomTipo order by id");

            satCupomTipos = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return satCupomTipos;
    }
    
    
}
