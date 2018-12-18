/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao.endereco;

import model.bean.endereco.Endereco;
import static ouroboros.Ouroboros.emBs;

/**
 * dados provenientes do sqLite
 * @author ivand
 */
public class EnderecoDAO {
    public Endereco findByCep(String cep){
        Endereco endereco = null;
        
        try {
            endereco = emBs.find(Endereco.class, cep);
        } catch (Exception e) {
            System.err.println(e);
        }
        
        return endereco;
    }
}
