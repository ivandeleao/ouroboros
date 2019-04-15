/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.fiscal;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import model.mysql.bean.fiscal.Cfop;
import model.mysql.bean.fiscal.Icms;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class IcmsDAO {

    public Icms save(Icms icms) {
        try {
            em.getTransaction().begin();
            if (icms.getCodigo() == null) {
                em.persist(icms);
            } else {
                em.merge(icms);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        }

        return icms;
    }

    public Icms findByCodigo(String codigo) {
        Icms icms = null;
        try {
            icms = em.find(Icms.class, codigo);
        } catch (Exception e) {
            System.err.println(e);
        }
        return icms;
    }

    public List<Icms> findAll() {
        List<Icms> icmsList = null;
        try {
            Query query = em.createQuery("from Icms i order by codigo");

            icmsList = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return icmsList;
    }

    /**
     * Define os dados iniciais ao criar a tabela
     */
    public void bootstrap() {
        List<Icms> icmsList = new ArrayList<>();

        icmsList.add(new Icms("00", "Tributada integralmente"));
        icmsList.add(new Icms("20", "Com redução de base de cálculo"));
        icmsList.add(new Icms("90", "Outros"));
        icmsList.add(new Icms("40", "Isenta"));
        icmsList.add(new Icms("41", "Não Tributada"));
        icmsList.add(new Icms("60", "ICMS cobrado anteriormente por substituição tributária"));
        icmsList.add(new Icms("102", "Tributada pelo Simples Nacional sem permissão de crédito"));
        icmsList.add(new Icms("300", "Imune"));
        icmsList.add(new Icms("400", "Não tributada"));
        icmsList.add(new Icms("500", "ICMS cobrado anteriormente por substituição tributária (substituído) ou por antecipação"));
        icmsList.add(new Icms("900", "Outros"));

        em.getTransaction().begin();
        for (Icms icms : icmsList) {
            if (findByCodigo(icms.getCodigo()) == null) {
                icms.setDescricao(icms.getDescricao());
                em.persist(icms);
            }
        }
        em.getTransaction().commit();

    }
}
