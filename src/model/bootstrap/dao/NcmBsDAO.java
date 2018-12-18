/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bootstrap.dao;

import java.util.List;
import javax.persistence.Query;
import model.bootstrap.bean.NcmBs;
import static ouroboros.Ouroboros.emBs;

/**
 *
 * @author ivand
 */
public class NcmBsDAO {
    public List<NcmBs> findAll() {
        List<NcmBs> ncmBsList = null;
        try {
            Query query = emBs.createQuery("from NcmBs n");

            ncmBsList = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return ncmBsList;
    }
}
