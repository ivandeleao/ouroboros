/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.endereco;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.mysql.bean.endereco.Endereco;
import static ouroboros.Ouroboros.em;
import static ouroboros.Ouroboros.emBs;

/**
 * dados provenientes do sqLite
 * @author ivand
 */
public class EnderecoDAO {
    public Endereco findByCep(String cep){
        Endereco endereco = null;
        
        try {
            endereco = emBs.find(Endereco.class, cep);
        } catch (Exception e) {
            System.err.println(e);
        }
        
        return endereco;
    }
    
    public List<Endereco> findByCriteria(String endereco){
        
        List<Endereco> enderecos = new ArrayList<>();
        try {
            CriteriaBuilder cb = emBs.getCriteriaBuilder();
            
            CriteriaQuery<Endereco> q = cb.createQuery(Endereco.class);
            Root<Endereco> rootEndereco = q.from(Endereco.class);
            
            List<Predicate> predicates = new ArrayList<>();
            
           
            if(endereco != null){
                //achar por partes diversas do endereco
                endereco = endereco.replaceAll(" ", "%");
                predicates.add(cb.like(rootEndereco.get("enderecoCompleto"), "%"+endereco+"%"));
                //Não existe uma função para ignorar acentos no sqlite
                /*predicates.add(cb.like(cb.function("unaccent",
                        String.class, cb.lower(rootEndereco.get("enderecoCompleto"))), 
                        "%"+endereco+"%"));*/
            }
            
            
            List<Order> o = new ArrayList<>();
            o.add(cb.asc(rootEndereco.get("enderecoCompleto")));
            
            
            //https://stackoverflow.com/questions/18389378/jpa-criteria-query-api-and-order-by-two-columns
            q.select(rootEndereco).where(predicates.toArray(new Predicate[]{}));
            
            q.orderBy(o);
            
            
            TypedQuery<Endereco> query = emBs.createQuery(q);
            
            query.setMaxResults(50);
            
            enderecos = query.getResultList();
        } catch (Exception e) {
            System.err.println(e);
        }
        return enderecos;
    }
}
