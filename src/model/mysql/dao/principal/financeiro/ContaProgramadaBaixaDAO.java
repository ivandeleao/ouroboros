/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal.financeiro;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import model.mysql.bean.fiscal.MeioDePagamento;
import model.mysql.bean.principal.financeiro.Caixa;
import model.mysql.bean.principal.financeiro.CaixaItem;
import model.mysql.bean.principal.financeiro.CaixaItemTipo;
import model.mysql.bean.principal.financeiro.Conta;
import model.mysql.bean.principal.financeiro.ContaProgramada;
import model.mysql.bean.principal.financeiro.ContaProgramadaBaixa;
import model.mysql.bean.principal.financeiro.ContaPagar;
import model.nosql.ContaTipoEnum;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class ContaProgramadaBaixaDAO {
    public ContaProgramadaBaixa save(ContaProgramadaBaixa contaProgramadaBaixa) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
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
        } finally {
            em.close();
        }
        
        return contaProgramadaBaixa;
    }

    public ContaProgramadaBaixa findById(Integer id) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        ContaProgramadaBaixa contaProgramadaBaixa = null;
        try {
            contaProgramadaBaixa = em.find(ContaProgramadaBaixa.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return contaProgramadaBaixa;
    }

    public List<ContaProgramadaBaixa> findAll() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<ContaProgramadaBaixa> contaPagarProgramadas = null;
        try {
            Query query = em.createQuery("from ContaPagarProgramada c order by nome");

            contaPagarProgramadas = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return contaPagarProgramadas;
    }
    
    public void baixar(ContaPagar contaPagar, BigDecimal valorBaixa, MeioDePagamento meioDePagamento, String observacao, Conta conta) {
        /*  crio e salvo caixaItem
            crio contaProgramadaBaixa
            adiciono caixaItem na contaProgramadaBaixa
            salvo contaProgramadaBaixa */
        
        CaixaItemDAO caixaItemDAO = new CaixaItemDAO();
        
        Caixa caixa = conta.getLastCaixa(); //2020-02-28
        
        //crio e salvo caixaItem - sem indicar o pai (ContaProgramadaBaixa)
        //CaixaItem caixaItem = new CaixaItem(caixa, CaixaItemTipo.CONTA_PROGRAMADA, meioDePagamento, observacao, BigDecimal.ZERO, valorBaixa);
        CaixaItem caixaItem = new CaixaItem(CaixaItemTipo.CONTA_PROGRAMADA, meioDePagamento, observacao, BigDecimal.ZERO, valorBaixa);
        
        
        if(conta.getContaTipo().equals(ContaTipoEnum.CAIXA)) {
            System.out.println("caixa");
            caixa.addCaixaItem(caixaItem);
        } else {
            System.out.println("conta");
            conta.addCaixaItem(caixaItem);
        }
        
        //crio contaProgramadaBaixa
        ContaProgramadaBaixa baixa = new ContaProgramadaBaixa();
        
        //adiciono caixaItem na contaProgramadaBaixa
        //baixa.setContaProgramada(contaPagar.getContaProgramada());
        baixa.addCaixaItem(caixaItem);
        baixa.setVencimento(contaPagar.getVencimento());
        baixa.setValor(contaPagar.getValor()); //memorizar valor
        
        baixa = save(baixa);
        
        caixaItem = caixaItemDAO.save(caixaItem);
        
        //Associar baixa com a contaProgramada
        contaPagar.getContaProgramada().addContaProgramadaBaixa(baixa);
        new ContaProgramadaDAO().save(contaPagar.getContaProgramada());
        
        //return contaPagar;
        
    }
    
}
