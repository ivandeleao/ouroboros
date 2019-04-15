/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.Query;
import model.mysql.bean.fiscal.MeioDePagamento;
import model.mysql.bean.principal.Caixa;
import model.mysql.bean.principal.CaixaItem;
import model.mysql.bean.principal.CaixaItemTipo;
import model.mysql.bean.principal.ContaProgramada;
import model.mysql.bean.principal.ContaProgramadaBaixa;
import model.mysql.bean.principal.ContaPagar;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class ContaProgramadaBaixaDAO {
    public ContaProgramadaBaixa save(ContaProgramadaBaixa contaProgramadaBaixa) {
        try {
            em.getTransaction().begin();
            if (contaProgramadaBaixa.getId() == null) {
                em.persist(contaProgramadaBaixa);
            } else {
                em.merge(contaProgramadaBaixa);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Erro ContaProgramadaBaixa.save " + e);
            em.getTransaction().rollback();
        }
        return contaProgramadaBaixa;
    }

    public ContaProgramadaBaixa findById(Integer id) {
        ContaProgramadaBaixa contaProgramadaBaixa = null;
        try {
            contaProgramadaBaixa = em.find(ContaProgramadaBaixa.class, id);
        } catch (Exception e) {
            System.err.println(e);
        }
        return contaProgramadaBaixa;
    }

    public List<ContaProgramadaBaixa> findAll() {
        List<ContaProgramadaBaixa> contaPagarProgramadas = null;
        try {
            Query query = em.createQuery("from ContaPagarProgramada c order by nome");

            contaPagarProgramadas = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return contaPagarProgramadas;
    }
    
    public void baixar(ContaPagar contaPagar, BigDecimal valorBaixa, MeioDePagamento meioDePagamento, String observacao) {
        /*  crio e salvo caixaItem
            crio contaProgramadaBaixa
            adiciono caixaItem na contaProgramadaBaixa
            salvo contaProgramadaBaixa */
        
        CaixaItemDAO caixaItemDAO = new CaixaItemDAO();
        
        Caixa caixa = new CaixaDAO().getLastCaixa();
        
        //crio e salvo caixaItem - sem indicar o pai (ContaProgramadaBaixa)
        CaixaItem caixaItem = new CaixaItem(caixa, CaixaItemTipo.CONTA_PROGRAMADA, meioDePagamento, observacao, BigDecimal.ZERO, valorBaixa);
        caixaItem = caixaItemDAO.save(caixaItem);
        
        //crio contaProgramadaBaixa
        ContaProgramadaBaixa baixa = new ContaProgramadaBaixa();
        
        //adiciono caixaItem na contaProgramadaBaixa
        //baixa.setContaProgramada(contaPagar.getContaProgramada());
        baixa.addCaixaItem(caixaItem);
        baixa.setVencimento(contaPagar.getVencimento());
        baixa.setValor(contaPagar.getValor()); //memorizar valor
        
        baixa = save(baixa);
        
        //Associar baixa com a contaProgramada
        contaPagar.getContaProgramada().addContaProgramadaBaixa(baixa);
        new ContaProgramadaDAO().save(contaPagar.getContaProgramada());
        
        //return contaPagar;
        
    }
    
}
