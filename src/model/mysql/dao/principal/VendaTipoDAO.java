/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import model.mysql.bean.principal.VendaTipo;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class VendaTipoDAO {
    
    /**
     * Define os dados iniciais ao criar a tabela
     */
    public void bootstrap() {
        List<VendaTipo> vendaTipos = new ArrayList<>();
        vendaTipos.add(VendaTipo.VENDA);
        vendaTipos.add(VendaTipo.PEDIDO);
        vendaTipos.add(VendaTipo.COMANDA);
        vendaTipos.add(VendaTipo.ORDEM_DE_SERVICO);
        vendaTipos.add(VendaTipo.LOCAÇÃO);
        vendaTipos.add(VendaTipo.COMPRA);
        
        
        em.getTransaction().begin();
        for(VendaTipo vendaTipo : vendaTipos){
            if(findById(vendaTipo.getId()) == null){
                em.persist(vendaTipo);
            } else {
                em.merge(vendaTipo);
            }
        }
        em.getTransaction().commit();

    }
    
    public VendaTipo save(VendaTipo caixaItemTipo) {
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

    public VendaTipo findById(Integer id) {
        VendaTipo caixaItemTipo = null;
        try {
            caixaItemTipo = em.find(VendaTipo.class, id);
        } catch (Exception e) {
            System.err.println(e);
        }
        return caixaItemTipo;
    }
    
    public List<VendaTipo> findAll() {
        List<VendaTipo> vendaTipos = null;
        try {
            Query query = em.createQuery("from VendaTipo vendaTipo order by id");

            vendaTipos = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return vendaTipos;
    }
    
    
}
