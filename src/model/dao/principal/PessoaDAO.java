/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao.principal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import model.bean.principal.Pessoa;
import model.bean.principal.PessoaTipo;
import model.bean.principal.Produto;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class PessoaDAO {
    public Pessoa save(Pessoa pessoa){
        try {
            em.getTransaction().begin();
            if(pessoa.getId() == null){
                em.persist(pessoa);
            }else{
                em.merge(pessoa);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println(e);
            em.getTransaction().rollback();
        }
        
        return pessoa;
    }

    public Pessoa findById(Integer id){
        Pessoa pessoa = null;
        
        try {
            pessoa = em.find(Pessoa.class, id);
        } catch (Exception e) {
            System.err.println(e);
        }
        
        return pessoa;
    }
    
    public List<Pessoa> findByNome(String nome, PessoaTipo pessoaTipo){
        return findByCriteria(nome, null, pessoaTipo, false);
    }
    
    public Pessoa findByCpfCnpj(String cpfCnpj){
        if(findByCriteria(null, cpfCnpj, null, false).isEmpty()) {
            return null;
        } else {
            return findByCriteria(null, cpfCnpj, null, false).get(0);
        }
    }
    
    public List<Pessoa> findByCriteria(String nome, String cpfCnpj, PessoaTipo pessoaTipo, boolean exibirExcluidos){
        
        List<Pessoa> listPessoa = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            
            CriteriaQuery<Pessoa> q = cb.createQuery(Pessoa.class);
            Root<Pessoa> rootPessoa = q.from(Pessoa.class);
            
            List<Predicate> predicates = new ArrayList<>();
            
           
            if(nome != null){
                //achar por partes diversas do nome
                nome = nome.replaceAll(" ", "%");
                predicates.add(
                        cb.or(
                                cb.like(rootPessoa.get("nome"), "%"+nome+"%"),
                                cb.like(rootPessoa.get("nomeFantasia"), "%"+nome+"%")
                        )
                );
            }
            
            if(cpfCnpj != null) {
                predicates.add(
                        cb.or(
                                cb.equal(rootPessoa.get("cpf"), cpfCnpj),
                                cb.equal(rootPessoa.get("cnpj"), cpfCnpj)
                        )
                );
            }
            
            System.out.println("pessoaTipo: " + pessoaTipo);
            if(pessoaTipo != null) {
                switch(pessoaTipo) {
                    case CLIENTE:
                        predicates.add(cb.isTrue(rootPessoa.get("cliente")));
                        break;
                    case FORNECEDOR:
                        predicates.add(cb.isTrue(rootPessoa.get("fornecedor")));
                        break;
                }
            }
            
            Predicate predicateExclusao = null;
            if (!exibirExcluidos) {
                predicateExclusao = (cb.isNull(rootPessoa.get("exclusao")));
            }
            
            //predicates.add(cb.greaterThanOrEqualTo(pessoa.get("valor"), (Comparable) menorValor));
            //predicates.add(cb.lessThanOrEqualTo(pessoa.get("valor"), (Comparable) maiorValor));
            
            
            List<Order> o = new ArrayList<>();
            o.add(cb.asc(rootPessoa.get("nome")));
            
            
            //https://stackoverflow.com/questions/18389378/jpa-criteria-query-api-and-order-by-two-columns
            q.select(rootPessoa).where(cb.and(predicates.toArray(new Predicate[]{})), predicateExclusao);
            
            q.orderBy(o);
            
            TypedQuery<Pessoa> query = em.createQuery(q);
            
            //query.setMaxResults(50);
            
            //query.setParameter(parNome, nome);
            
            listPessoa = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return listPessoa;
    }
    
    public List<Pessoa> findAll(){
        List<Pessoa> listPessoa = null;
        try {
            Query query = em.createQuery("from " + Pessoa.class.getSimpleName() + " c");
            query.setMaxResults(50);
            listPessoa = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return listPessoa;
    }
    
    public List<Pessoa> findAllFornecedor() {
        List<Pessoa> listPessoa = null;
        try {
            Query query = em.createQuery("from " + Pessoa.class.getSimpleName() + " p where fornecedor = :fornecedor");
            query.setParameter("fornecedor", true);
            listPessoa = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return listPessoa;
    }
    
    public Pessoa delete(Pessoa pessoa) {
        pessoa.setExclusao(LocalDateTime.now());

        return save(pessoa);
    }
}
