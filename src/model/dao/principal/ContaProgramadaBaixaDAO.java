/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao.principal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.Query;
import model.bean.fiscal.MeioDePagamento;
import model.bean.principal.Caixa;
import model.bean.principal.CaixaItem;
import model.bean.principal.CaixaItemTipo;
import model.bean.principal.ContaProgramada;
import model.bean.principal.ContaProgramadaBaixa;
import model.bean.principal.ContaPagar;
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
    
    public void baixar(ContaPagar contaProgramadaView, BigDecimal valorBaixa) {
        /*  crio e salvo caixaItem
            crio contaProgramadaBaixa
            adiciono caixaItem na contaProgramadaBaixa
            salvo contaProgramadaBaixa */
        
        CaixaItemDAO caixaItemDAO = new CaixaItemDAO();
        
        Caixa caixa = new CaixaDAO().getLastCaixa();
        
        //crio e salvo caixaItem - sem indicar o pai (ContaProgramadaBaixa)
        CaixaItem caixaItem = new CaixaItem(caixa, CaixaItemTipo.RECEBIMENTO_DE_VENDA, MeioDePagamento.DINHEIRO, "observacao", valorBaixa, BigDecimal.ZERO);
        caixaItem = caixaItemDAO.save(caixaItem);
        
        //crio contaProgramadaBaixa
        ContaProgramadaBaixa baixa = new ContaProgramadaBaixa();
        //adiciono caixaItem na contaProgramadaBaixa
        baixa.setContaProgramada(contaProgramadaView.getContaProgramada());
        baixa.addCaixaItem(caixaItem);
        baixa.setVencimento(contaProgramadaView.getVencimento());
        
        
        
        
    }
    
}
