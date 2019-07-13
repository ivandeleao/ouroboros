/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nfe;

import model.mysql.bean.principal.pessoa.Pessoa;
import model.nosql.nfe.Emit;

/**
 *
 * @author ivand
 */
public class Converter {
    
    
    public static Pessoa emit(Emit emit) {
        Pessoa pessoa = new Pessoa();
        
        pessoa.setCliente(false);
        pessoa.setFornecedor(true);

        pessoa.setNome(emit.getxNome());
        pessoa.setNomeFantasia(emit.getxFant());
        pessoa.setCnpj(emit.getCnpj());
        pessoa.setIe(emit.getIe());
        //ieIsento;
        //im;
        //suframa;

        pessoa.setTelefone1(emit.getEnderEmit().getFone());
        //email;
        //pessoa.setCep(emit.getCep);
        
        pessoa.setEndereco(emit.getEnderEmit().getxLgr());
        pessoa.setNumero(emit.getEnderEmit().getNro());
        pessoa.setBairro(emit.getEnderEmit().getNro());
        pessoa.setCodigoMunicipio(emit.getEnderEmit().getcMun());

        pessoa.setObservacao("importado por XML");
        
        return pessoa;
        
    }
}
