/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bootstrap.dao;

import java.util.List;
import javax.persistence.Query;
import model.bootstrap.bean.SatErroOuAlertaBs;
import static ouroboros.Ouroboros.emBs;

/**
 *
 * @author ivand
 */
public class SatErroOuAlertaBsDAO {
    public List<SatErroOuAlertaBs> findAll() {
        List<SatErroOuAlertaBs> SatErroOuAlertaBsList = null;
        try {
            Query query = emBs.createQuery("from SatErroOuAlertaBs sea");

            SatErroOuAlertaBsList = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return SatErroOuAlertaBsList;
    }
}
