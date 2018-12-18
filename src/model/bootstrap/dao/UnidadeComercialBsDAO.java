/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bootstrap.dao;

import java.util.List;
import model.bootstrap.bean.UnidadeComercialBs;

import static ouroboros.Ouroboros.emBs;

/**
 *
 * @author ivand
 */
public class UnidadeComercialBsDAO {
    
    public UnidadeComercialBs findById(Integer id) {
        UnidadeComercialBs unidadeComercial = null;
        try {
            unidadeComercial = emBs.find(UnidadeComercialBs.class, id);
        } catch (Exception e) {
            System.err.println(e);
        }
        return unidadeComercial;
    }
    
    public List<UnidadeComercialBs> findAll(){
        List<UnidadeComercialBs> unidades = null;
        try {
            unidades = emBs.createQuery("from UnidadeComercialBs uCbs").getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return unidades;
    }
    
}
