/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.fiscal.nfe;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author ivand
 */
@Entity
public class ConsumidorFinal implements Serializable {
    @Id
    private Integer id;
    private String nome;
    
    
    public ConsumidorFinal(){}
    
    public ConsumidorFinal(Integer id, String nome){
        this.id = id;
        this.nome = nome;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return getId() + " - " + getNome();
    }
    
    
}
