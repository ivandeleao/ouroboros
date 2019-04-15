/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao.endereco;

import model.mysql.dao.endereco.CidadeDAO;
import java.util.List;
import model.mysql.bean.endereco.Cidade;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ivand
 */
public class CidadeDAOTest {

    public CidadeDAOTest() {
    }

    @Test
    public void testFindByCodigoIbge() {
        CidadeDAO cidadeDAO = new CidadeDAO();
        Cidade cidade = cidadeDAO.findByCodigoIbge("3509502");

        System.out.println("cidade: " + cidade.getNome());
    }

}
