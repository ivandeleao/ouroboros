/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.fiscal;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import model.mysql.bean.fiscal.ProdutoOrigem;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class ProdutoOrigemDAO {
    /**
     * Define os dados iniciais ao criar a tabela
     */
    public void bootstrap() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<ProdutoOrigem> poLista = new ArrayList<>();
        poLista.add(new ProdutoOrigem(0, "Nacional - Exceto as indicadas nos códigos 3 a 5"));
        poLista.add(new ProdutoOrigem(1, "Estrangeira - Importação Direta, exceto a indicada no código 6"));
        poLista.add(new ProdutoOrigem(2, "Estrangeira - Adquirida no Mercado Interno, exceto indicada no código 7"));
        poLista.add(new ProdutoOrigem(3, "Nacional - Mercadoria ou bem com conteúdo de Importação superior a 40%"));
        poLista.add(new ProdutoOrigem(4, "Nacional - Cuja produção tenha sido feita em conformidade com os processos produtivos básicos de que tratam as legislações citadas nos Ajustes"));
        poLista.add(new ProdutoOrigem(5, "Nacional - Mercadoria ou beminferior ou igual a 40%"));
        poLista.add(new ProdutoOrigem(6, "Estrangeira - Importação direta, sem similar nacional, constante em lista da CAMEX"));
        poLista.add(new ProdutoOrigem(7, "Estrangeira - Adquirida no mercado interno, sem similar nacional, constante em lista da CAMEX"));
        poLista.add(new ProdutoOrigem(8, "Nacional, mercadoria ou bem com Conteúdo de Importação superior a 70%"));
        
        em.getTransaction().begin();
        for(ProdutoOrigem produtoOrigem : poLista){
            if(findById(produtoOrigem.getId()) == null){
                em.persist(produtoOrigem);
            }
        }
        em.getTransaction().commit();

        em.close();
    }
    
    public ProdutoOrigem save(ProdutoOrigem produtoOrigem) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        try {
            em.getTransaction().begin();
            if (produtoOrigem.getId() == null) {
                em.persist(produtoOrigem);
            } else {
                em.merge(produtoOrigem);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }

        return produtoOrigem;
    }

    public ProdutoOrigem findById(Integer id) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        ProdutoOrigem produtoOrigem = null;
        try {
            produtoOrigem = em.find(ProdutoOrigem.class, id);
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return produtoOrigem;
    }
    
    public List<ProdutoOrigem> findAll() {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        List<ProdutoOrigem> poLista = null;
        try {
            Query query = em.createQuery("from ProdutoOrigem produtoOrigem order by id");

            poLista = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            em.close();
        }
        
        return poLista;
    }
}
