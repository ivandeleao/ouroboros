/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao.principal;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import model.bean.principal.CaixaItemTipo;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class CaixaItemTipoDAO {
    
    /**
     * Define os dados iniciais ao criar a tabela
     */
    public void bootstrap() {
        List<CaixaItemTipo> cits = new ArrayList<>();
        cits.add(CaixaItemTipo.LANCAMENTO_MANUAL);
        cits.add(CaixaItemTipo.RECEBIMENTO_DE_VENDA);
        cits.add(CaixaItemTipo.ESTORNO);
        cits.add(CaixaItemTipo.TROCO_DE_VENDA);
        
        cits.add(CaixaItemTipo.SUPRIMENTO);
        cits.add(CaixaItemTipo.SANGRIA);
        
        
        em.getTransaction().begin();
        for(CaixaItemTipo cit : cits){
            if(findById(cit.getId()) == null){
                em.persist(cit);
            } else {
                em.merge(cit);
            }
        }
        em.getTransaction().commit();

    }
    
    public CaixaItemTipo save(CaixaItemTipo caixaItemTipo) {
        try {
            em.getTransaction().begin();
            if (caixaItemTipo.getId() == null) {
                em.persist(caixaItemTipo);
            } else {
                em.merge(caixaItemTipo);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        }

        return caixaItemTipo;
    }

    public CaixaItemTipo findById(Integer id) {
        CaixaItemTipo caixaItemTipo = null;
        try {
            caixaItemTipo = em.find(CaixaItemTipo.class, id);
        } catch (Exception e) {
            System.err.println(e);
        }
        return caixaItemTipo;
    }
    
    public List<CaixaItemTipo> findAll() {
        List<CaixaItemTipo> cits = null;
        try {
            Query query = em.createQuery("from CaixaItemTipo cit order by id");

            cits = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return cits;
    }
    
    public List<CaixaItemTipo> findAllEnabled() {
        List<CaixaItemTipo> cits = null;
        try {
            Query query = em.createQuery("from CaixaItemTipo cit where habilitado = true order by id");

            cits = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return cits;
    }
}
