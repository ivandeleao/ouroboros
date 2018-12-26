/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao.principal;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.Coalesce;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.bean.principal.MovimentoFisicoTipo;
import model.bean.principal.MovimentoFisico;
import model.bean.principal.Produto;
import model.bean.principal.ProdutoComponente;
import model.bean.principal.ProdutoComponenteId;
import model.bean.principal.Venda;
import static ouroboros.Ouroboros.em;

/**
 *
 * @author ivand
 */
public class MovimentoFisicoDAO {

    
    public MovimentoFisico save(MovimentoFisico movimentoFisico) {
        //em = CONNECTION_FACTORY.getConnection();
        em.getTransaction().begin();
        if (movimentoFisico.getId() == null) {
            movimentoFisico = deepPersist(movimentoFisico);
            em.persist(movimentoFisico);
        } else {
            movimentoFisico = deepMerge(movimentoFisico);
            em.merge(movimentoFisico);
        }
        
        em.getTransaction().commit();
        //em.close();
        return movimentoFisico;
    }

    private MovimentoFisico deepPersist(MovimentoFisico mfOrigem) {
        //Gerar MovimentoFisico para cada componente
        System.out.println("***---------------------------------------------------***");
        System.out.println("deep persist..." + mfOrigem.getProduto().getNome());
        System.out.println("mfOrigem Id: " + mfOrigem.getId());

        List<ProdutoComponente> listPc = mfOrigem.getProduto().getListProdutoComponente();

        for (ProdutoComponente pc : listPc) {
            System.out.println("componente de " + mfOrigem.getProduto().getNome() + ": " + pc.getComponente().getNome());
            
            Produto componente = pc.getComponente();
            BigDecimal proporcao = pc.getQuantidade();
            
            System.out.println("proporcao: " + proporcao);
            System.out.println("mfOrigem.getEntrada(): " + mfOrigem.getEntrada());
            
            MovimentoFisico mfComponente = new MovimentoFisico(
                    componente, componente.getCodigo(), 
                    mfOrigem.getEntrada().multiply(proporcao), 
                    mfOrigem.getSaida().multiply(proporcao), 
                    componente.getValorVenda(), 
                    componente.getUnidadeComercialVenda(), MovimentoFisicoTipo.VENDA, null);
            
            mfComponente.setEstornoOrigem(null);

            System.out.println("Comp clone: " + mfComponente.getProduto().getNome());
            
            mfComponente = deepPersist(mfComponente); //recursivo

            mfOrigem.addMovimentoFisicoComponente(mfComponente);
            
        }
        
        
        System.out.println(mfOrigem.getProduto().getNome() + " - mfs Componente criados: ");
        for(MovimentoFisico mf : mfOrigem.getMovimentosFisicosComponente()) {
            System.out.println("mf comp: " + mf.getProduto().getNome());
        }
        System.out.println("-----------------------------------------------------------");
        
        return mfOrigem;
    }

    /**
     * Copy type and dates from its origins
     *
     * @param mfOrigem
     */
    private MovimentoFisico deepMerge(MovimentoFisico mfOrigem) {
        for (MovimentoFisico mfComponente : mfOrigem.getMovimentosFisicosComponente()) {
            mfComponente.setMovimentoFisicoTipo(mfOrigem.getMovimentoFisicoTipo());
            mfComponente.setDataEntrada(mfOrigem.getDataEntrada());
            mfComponente.setDataEntradaPrevista(mfOrigem.getDataEntradaPrevista());
            mfComponente.setDataSaida(mfOrigem.getDataSaida());
            mfComponente.setDataSaidaPrevista(mfOrigem.getDataSaidaPrevista());

            deepMerge(mfComponente);

            mfOrigem.addMovimentoFisicoComponente(mfComponente);
        }
        
        return mfOrigem;
    }

    
    /**
     * Marca o item como excluído e lança movimento oposto para balancear o
     * estoque
     *
     * @param mfEstornado -> movimentação a ser estornada
     * @return
     */
    public MovimentoFisico remove(MovimentoFisico mfEstornado) {
        
        MovimentoFisico mfEstorno = new MovimentoFisico(
                mfEstornado.getProduto(), mfEstornado.getProduto().getCodigo(), 
                mfEstornado.getSaida(), mfEstornado.getEntrada(), 
                mfEstornado.getValor(), mfEstornado.getUnidadeComercialVenda(), 
                mfEstornado.getMovimentoFisicoTipo(), null);
        
        
        for (MovimentoFisico mfComponenteEstornado : mfEstornado.getMovimentosFisicosComponente()) {
            remove(mfComponenteEstornado); //recursivo
        }
        
        mfEstornado.addEstorno(mfEstorno);
        
        
        //GERAR ESTORNO DA DEVOLUÇÃO TAMBÉM
        if(mfEstornado.getDevolucao() != null) {
            System.out.println("estornar devolução: " + mfEstornado.getDevolucao().getProduto().getNome());
            System.out.println("id: " + mfEstornado.getDevolucao().getId());
            remove(mfEstornado.getDevolucao());
        }
        
        return mfEstornado;
    }

    
    public MovimentoFisico gerarDevolucaoPrevista(MovimentoFisico itemDevolver, LocalDateTime dataEntradaPrevista) {
        MovimentoFisico mfDevolucao;

        //Verificar se já existe devolução
        if (itemDevolver.getDevolucao() != null) {
            mfDevolucao = itemDevolver.getDevolucao();
            mfDevolucao.setDataEntradaPrevista(dataEntradaPrevista);

        } else {
            mfDevolucao = new MovimentoFisico(
                itemDevolver.getProduto(), itemDevolver.getProduto().getCodigo(), 
                itemDevolver.getSaida(), itemDevolver.getEntrada(), 
                itemDevolver.getValor(), itemDevolver.getUnidadeComercialVenda(), 
                itemDevolver.getMovimentoFisicoTipo(), null);
            
            mfDevolucao.setDataEntradaPrevista(dataEntradaPrevista);
            mfDevolucao.setDataSaidaPrevista(null);
            mfDevolucao.setMovimentoFisicoTipo(MovimentoFisicoTipo.DEVOLUCAO_ALUGUEL);
            
        }
        
        //iterar filhos
        for (MovimentoFisico mfComponente : itemDevolver.getMovimentosFisicosComponente()) {
            gerarDevolucaoPrevista(mfComponente, dataEntradaPrevista);
        }

        itemDevolver.addDevolucao(mfDevolucao);

        return itemDevolver;
    }
    

    public BigDecimal getSaldoAnterior(MovimentoFisico movimentoFisico) {
        try {
            Query q = em.createNativeQuery("select sum(entrada - saida) as saldo from " + MovimentoFisico.class.getSimpleName() + " where id < :id and produtoId = :produtoId");
            q.setParameter("id", movimentoFisico.getId());
            q.setParameter("produtoId", movimentoFisico.getProduto().getId());

            if (q.getSingleResult() != null) {
                return (BigDecimal) q.getSingleResult();
            } else {
                return BigDecimal.ZERO;
            }

        } catch (Exception e) {
            System.err.println("Erro em getSaldoAnterior " + e);
        }
        return null;
    }

    
    private BigDecimal getSaldoAtual(MovimentoFisico movimentoFisico) {
        //System.out.println("movimentoFisico id: " + movimentoFisico.getId());
        BigDecimal saldoAnterior = getSaldoAnterior(movimentoFisico);
        //System.out.println("saldoAnterior: " + saldoAnterior);

        return saldoAnterior.add(movimentoFisico.getEntrada()).subtract(movimentoFisico.getSaida());
    }

    
    /**
     *
     * @param produto
     * @param dataInicial
     * @param dataFinal
     * @return Lista com todos os registros (normais e derivados de compostos)
     */
    public List<MovimentoFisico> findTotalPorDatas(Produto produto, Timestamp dataInicial, Timestamp dataFinal) {
        List<MovimentoFisico> listMf = findPorIntervalo(produto, dataInicial, dataFinal);
        /*
        List<MovimentoFisico> listComposto = findProdutoCompostoPorPeriodo(produto, dataInicial, dataFinal);
        if (listComposto != null) {
            listMf.addAll(listComposto);
        }
        
        listMf.sort(Comparator.comparing(MovimentoFisico::getDataRelevante));
         */
        return listMf;

    }

    /**
     *
     * @param produto
     * @param dataInicial
     * @param dataFinal
     * @return Lista com apenas os registros nativos (não derivados de
     * compostos)
     */
    protected List<MovimentoFisico> findPorIntervalo(Produto produto, Timestamp dataInicial, Timestamp dataFinal) {
        List<MovimentoFisico> listMovimentoFisico = new ArrayList<>();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<MovimentoFisico> cq = cb.createQuery(MovimentoFisico.class);
            Root<MovimentoFisico> rootMovimentoFisico = cq.from(MovimentoFisico.class);
            
            Join<MovimentoFisico, Venda> rootJoin = rootMovimentoFisico.join("venda", JoinType.LEFT);
            cq.multiselect(rootMovimentoFisico, rootJoin);

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(rootMovimentoFisico.get("produto"), produto));
            
            predicates.add(cb.or(
                    cb.isFalse(rootJoin.get("orcamento")),
                    cb.isNull(rootJoin.get("orcamento")))
            );

            if (dataInicial != null) {
                predicates.add(cb.greaterThanOrEqualTo(rootMovimentoFisico.get("vencimento"), (Comparable) dataInicial));
            }

            if (dataFinal != null) {
                predicates.add(cb.lessThanOrEqualTo(rootMovimentoFisico.get("vencimento"), (Comparable) dataFinal));
            }
            
            List<Predicate> predicatesJoin = new ArrayList<>();
            
            //predicates.add(cb.lessThanOrEqualTo(rootJoin.get("dataSaidaPrevista"), (Comparable) dataFinal));
            ////predicatesJoin.add(cb.isFalse(rootJoin.get("orcamento")));
            ////rootJoin.on(predicatesJoin.toArray(new Predicate[]{}));

            cq.select(rootMovimentoFisico).where(predicates.toArray(new Predicate[]{}));

            //Extrair o primeiro campo com data não nula e ordenar
            //Ex: Se houver dataEntrada usa ela, se não, se houver dataEntradaPrevista usa ela, etc...
            //https://www.isostech.com/blogs/software-development/hibernate-criteria-query-order-two-columns-simultaneously/
            Coalesce<MovimentoFisico> coalesce = cb.coalesce();
            coalesce.value(rootMovimentoFisico.get("dataEntrada"));
            coalesce.value(rootMovimentoFisico.get("dataEntradaPrevista"));
            coalesce.value(rootMovimentoFisico.get("dataSaida"));
            coalesce.value(rootMovimentoFisico.get("dataSaidaPrevista"));
            coalesce.value(rootMovimentoFisico.get("criacao"));

            cq.orderBy(cb.asc(coalesce), cb.asc(rootMovimentoFisico.get("id")));

            TypedQuery<MovimentoFisico> query = em.createQuery(cq);

            //listMovimentoFisico.addAll( new TreeSet<MovimentoFisico>(query.getResultList()) );
            listMovimentoFisico.addAll( query.getResultList() );
        } catch (Exception e) {
            System.err.println("Erro em MovimentoFisicoDAO.findPorIntervalo " + e);
        }

        return listMovimentoFisico;
    }

    /**
     *
     * @param componente
     * @param dataInicial
     * @param dataFinal
     * @return Lista de MovimentoFisico dos produtos que contêm este componente
     */
    protected List<MovimentoFisico> findProdutoCompostoPorPeriodo(Produto componente, Timestamp dataInicial, Timestamp dataFinal) {
        List<MovimentoFisico> listMovimentoFisico = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<MovimentoFisico> cq = cb.createQuery(MovimentoFisico.class);
            Root<MovimentoFisico> rootMovimentoFisico = cq.from(MovimentoFisico.class);

            List<Predicate> predicates = new ArrayList<>();

            if (dataInicial != null) {
                predicates.add(cb.greaterThanOrEqualTo(rootMovimentoFisico.get("vencimento"), (Comparable) dataInicial));
            }

            if (dataFinal != null) {
                predicates.add(cb.lessThanOrEqualTo(rootMovimentoFisico.get("vencimento"), (Comparable) dataFinal));
            }

            if (!componente.getListProdutoComposto().isEmpty()) {

                predicates.add(rootMovimentoFisico.get("produto").in(componente.getListProdutoComposto()));

                cq.select(rootMovimentoFisico).where(predicates.toArray(new Predicate[]{}));

                //Extrair o primeiro campo com data não nula e ordenar
                //Ex: Se houver dataEntrada usa ela, se não, se houver dataEntradaPrevista usa ela, etc...
                //https://www.isostech.com/blogs/software-development/hibernate-criteria-query-order-two-columns-simultaneously/
                Coalesce<MovimentoFisico> coalesce = cb.coalesce();
                coalesce.value(rootMovimentoFisico.get("dataEntrada"));
                coalesce.value(rootMovimentoFisico.get("dataEntradaPrevista"));
                coalesce.value(rootMovimentoFisico.get("dataSaida"));
                coalesce.value(rootMovimentoFisico.get("dataSaidaPrevista"));
                coalesce.value(rootMovimentoFisico.get("criacao"));

                cq.orderBy(cb.asc(coalesce));

                TypedQuery<MovimentoFisico> query = em.createQuery(cq);

                listMovimentoFisico = (List<MovimentoFisico>) query.getResultList();

                List<MovimentoFisico> temp = new ArrayList<>();

                for (MovimentoFisico mf : listMovimentoFisico) {
                    MovimentoFisico derivado = mf.deepClone();

                    ProdutoComponenteId pcId = new ProdutoComponenteId(mf.getProduto().getId(), componente.getId());
                    BigDecimal proporcao = new ProdutoComponenteDAO().findById(pcId).getQuantidade();
                    BigDecimal entrada = derivado.getEntrada().multiply(proporcao);
                    BigDecimal saida = derivado.getSaida().multiply(proporcao);

                    derivado.setEntrada(entrada);
                    derivado.setSaida(saida);
                    derivado.setObservacao("Componente de: " + mf.getProduto().getNome());

                    temp.add(derivado);
                }

                return temp;
            }
        } catch (Exception e) {
            System.err.println("Erro em findProdutoCompostoPorPeriodo " + e);
        }
        return listMovimentoFisico;
    }

}
