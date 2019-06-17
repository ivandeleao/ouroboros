/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.persistence.Query;
import model.mysql.bean.principal.financeiro.CaixaItemTipo;
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
        cits.add(CaixaItemTipo.DOCUMENTO);
        cits.add(CaixaItemTipo.ESTORNO);
        cits.add(CaixaItemTipo.TROCO);
        
        cits.add(CaixaItemTipo.SUPRIMENTO);
        cits.add(CaixaItemTipo.SANGRIA);
        
        cits.add(CaixaItemTipo.CONTA_PROGRAMADA);
        //cits.add(CaixaItemTipo.PAGAMENTO_DOCUMENTO); 2019-06-10 generalizado com tipo 2
        
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
        List<CaixaItemTipo> cits = new ArrayList<>();
        try {
            Query query = em.createQuery("from CaixaItemTipo cit order by id");

            cits = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        cits.sort(Comparator.comparing(CaixaItemTipo::getNome));
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
