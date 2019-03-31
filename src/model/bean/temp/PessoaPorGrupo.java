/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bean.temp;

import model.bean.principal.*;
import java.io.Serializable;

/**
 *
 * @author ivand
 */
public class PessoaPorGrupo implements Serializable {

    private Pessoa pessoa;
    private Perfil perfil; //perfil filtrado - apenas um, não a lista da pessoa
    private Parcela parcela; //última parcela lançada

    protected PessoaPorGrupo() {
        
    }
    
    public PessoaPorGrupo(Pessoa pessoa, Perfil perfil, Parcela parcela) {
        this.pessoa = pessoa;
        this.perfil = perfil;
        this.parcela = parcela;
    }
    
    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    public Parcela getParcela() {
        return parcela;
    }

    public void setParcela(Parcela parcela) {
        this.parcela = parcela;
    }

    
    
    
    
}
