/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bootstrap.dao;

import java.util.List;
import javax.persistence.Query;
import model.bootstrap.bean.IbptBs;
import static ouroboros.Ouroboros.emBs;

/**
 *
 * @author ivand
 */
public class IbptBsDAO {
    public List<IbptBs> findAll() {
        List<IbptBs> ibptBsList = null;
        try {
            Query query = emBs.createQuery("from IbptBs i");

            ibptBsList = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return ibptBsList;
    }
}
