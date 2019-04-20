/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao.principal;

import model.mysql.dao.principal.ParcelaDAO;
import model.mysql.dao.principal.pessoa.PessoaDAO;
import java.sql.Timestamp;
import java.util.List;
import model.mysql.bean.principal.pessoa.Pessoa;
import model.mysql.bean.principal.documento.Parcela;
import org.junit.Test;
import static org.junit.Assert.*;
import util.DateTime;

/**
 *
 * @author ivand
 */
public class ParcelaDAOTest {
    
    public ParcelaDAOTest() {
    }

    @Test
    public void testFindByCriteria() {
        ParcelaDAO pDAO = new ParcelaDAO();
        Pessoa cliente = new PessoaDAO().findById(1);
        Timestamp dataInicial = DateTime.fromString("2018-01-01");
        Timestamp dataFinal = DateTime.fromString("2018-08-10 23:59:59");
        
        List<Parcela> parcelas = pDAO.findByCriteria(cliente, dataInicial, dataFinal);
        
        for(Parcela p : parcelas) {
            System.out.println("p: " + p.getVencimento() + " " + p.getValor());
        }
    }
    
}
