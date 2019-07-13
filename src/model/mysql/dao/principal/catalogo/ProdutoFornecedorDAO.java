/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal.catalogo;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.mysql.bean.principal.catalogo.ProdutoFornecedor;
import model.mysql.bean.principal.pessoa.Pessoa;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class ProdutoFornecedorDAO {
    public static ProdutoFornecedor save(ProdutoFornecedor produtoFornecedor) {
        try {
            em.getTransaction().begin();
            if (produtoFornecedor.getId() == null) {
                em.persist(produtoFornecedor);
            } else {
                em.merge(produtoFornecedor);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        }
        return produtoFornecedor;
    }
    
    public static ProdutoFornecedor remove(ProdutoFornecedor produtoFornecedor) {
        try {
            produtoFornecedor = em.find(ProdutoFornecedor.class, produtoFornecedor.getId());
            em.getTransaction().begin();
            em.remove(produtoFornecedor);
            em.getTransaction().commit();
            
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        }
        return produtoFornecedor;
    }

    public ProdutoFornecedor findById(Integer id) {
        ProdutoFornecedor produtoFornecedor = null;
        try {
            produtoFornecedor = em.find(ProdutoFornecedor.class, id);
        } catch (Exception e) {
            System.err.println(e);
        }
        return produtoFornecedor;
    }
    
    public static ProdutoFornecedor getByFornecedor(Pessoa fornecedor, String codigoNoFornecedor){
        try {
            return findByCriteria(fornecedor, codigoNoFornecedor).get(0);
        } catch(Exception e) {
            return null;
        }
    }
    
    public List<ProdutoFornecedor> findAll(){
        return findByCriteria(null, null);
    }
    
    private static List<ProdutoFornecedor> findByCriteria(Pessoa fornecedor, String codigoNoFornecedor){
        List<ProdutoFornecedor> categorias = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            
            CriteriaQuery<ProdutoFornecedor> q = cb.createQuery(ProdutoFornecedor.class);
            Root<ProdutoFornecedor> rootProdutoFornecedor = q.from(ProdutoFornecedor.class);
            
            List<Predicate> predicates = new ArrayList<>();
            
            if(fornecedor != null){
                predicates.add(cb.equal(rootProdutoFornecedor.get("fornecedor"), fornecedor));
            }
            
            if(codigoNoFornecedor != null){
                predicates.add(cb.equal(rootProdutoFornecedor.get("codigoNoFornecedor"), codigoNoFornecedor));
            }
            
            
            q.select(rootProdutoFornecedor).where(cb.or(predicates.toArray(new Predicate[]{})));
            
            TypedQuery<ProdutoFornecedor> query = em.createQuery(q);
            
            categorias = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return categorias;
    }
}
