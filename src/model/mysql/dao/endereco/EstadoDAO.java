/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.endereco;

import java.util.List;
import javax.persistence.Query;
import model.mysql.bean.endereco.Estado;
import static ouroboros.Ouroboros.emBs;

/**
 * dados provenientes do sqLite
 * @author ivand
 */
public class EstadoDAO {

    public Estado findById(Integer id) {
        Estado estado = null;

        try {
            estado = emBs.find(Estado.class, id);
        } catch (Exception e) {
            System.err.println(e);
        }

        return estado;
    }
    
    public List<Estado> findAll() {
        List<Estado> estado = null;
        try {
            Query query = emBs.createQuery("from " + Estado.class.getSimpleName() + " e order by sigla");

            estado = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        
        return estado;
    }
}
