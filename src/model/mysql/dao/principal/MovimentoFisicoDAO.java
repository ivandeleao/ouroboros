/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.dao.principal;

import model.mysql.dao.principal.catalogo.ProdutoComponenteDAO;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.Coalesce;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import model.mysql.bean.principal.MovimentoFisicoTipo;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.catalogo.Produto;
import model.mysql.bean.principal.catalogo.ProdutoComponente;
import model.mysql.bean.principal.catalogo.ProdutoComponenteId;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.dao.principal.catalogo.ProdutoDAO;
import static ouroboros.Ouroboros.CONNECTION_FACTORY;

/**
 *
 * @author ivand
 */
public class MovimentoFisicoDAO {

    
    public MovimentoFisico save(MovimentoFisico movimentoFisico) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
        em.getTransaction().begin();
        
        if (movimentoFisico.getId() == null) {
            movimentoFisico = deepPersist(movimentoFisico);
            em.persist(movimentoFisico);
        } else {
            movimentoFisico = deepMerge(movimentoFisico);
            em.merge(movimentoFisico);
        }
        
        em.getTransaction().commit();
        em.close();
        
        //alimentar campo de cache - tem que ser após gravar o movimentoFisico pra contabilizar ele também
        if(movimentoFisico.getProduto() != null) {
            movimentoFisico.getProduto().setEstoqueAtual();
            new ProdutoDAO().save(movimentoFisico.getProduto());
            
        }
        
        return movimentoFisico;
    }

    private MovimentoFisico deepPersist(MovimentoFisico mfOrigem) {
        //Gerar MovimentoFisico para cada componente
        if(mfOrigem.getProduto() != null) {
            //System.out.println("***---------------------------------------------------***");
            //System.out.println("deep persist..." + mfOrigem.getProduto().getNome());
            //System.out.println("mfOrigem Id: " + mfOrigem.getId());

            List<ProdutoComponente> listPc = mfOrigem.getProduto().getListProdutoComponente();

            for (ProdutoComponente pc : listPc) {
                System.out.println("componente de " + mfOrigem.getProduto().getNome() + ": " + pc.getComponente().getNome());

                Produto componente = pc.getComponente();
                //BigDecimal proporcao = pc.getQuantidade();

                //2020-01-24 aumentei de 3 para 10
                BigDecimal proporcao = pc.getQuantidade().divide(componente.getConteudoQuantidade(), 10, RoundingMode.HALF_UP);

                //System.out.println("proporcao: " + proporcao);
                //System.out.println("mfOrigem.getEntrada(): " + mfOrigem.getEntrada());

                MovimentoFisico mfComponente = new MovimentoFisico(
                        componente, 
                        componente.getCodigo(), 
                        componente.getNome(),
                        componente.getProdutoTipo(),
                        mfOrigem.getEntrada().multiply(proporcao), 
                        mfOrigem.getSaida().multiply(proporcao), 
                        componente.getValorVenda(), 
                        mfOrigem.getDescontoPercentual(), 
                        componente.getUnidadeComercialVenda(), MovimentoFisicoTipo.VENDA, null);

                mfComponente.setEstornoOrigem(null);
                mfComponente.setDataEntradaPrevista(mfOrigem.getDataEntradaPrevista());
                mfComponente.setDataSaidaPrevista(mfOrigem.getDataSaidaPrevista());

                mfComponente.setDataEntrada(mfOrigem.getDataEntrada());
                mfComponente.setDataSaida(mfOrigem.getDataSaida());

                mfComponente.setMovimentoFisicoTipo(mfOrigem.getMovimentoFisicoTipo());
                mfComponente.setVenda(mfOrigem.getVenda());
                mfComponente.setDevolucaoOrigem(mfOrigem.getDevolucaoOrigem());


                //System.out.println("Comp clone: " + mfComponente.getProduto().getNome());

                mfComponente = deepPersist(mfComponente); //recursivo

                mfOrigem.addMovimentoFisicoComponente(mfComponente);

            }


            //System.out.println(mfOrigem.getProduto().getNome() + " - mfs Componente criados: ");
            /*for(MovimentoFisico mf : mfOrigem.getMovimentosFisicosComponente()) {
                System.out.println("mf comp: " + mf.getProduto().getNome());
            }*/
            //System.out.println("-----------------------------------------------------------");
        }
        
        return mfOrigem;
    }

    /**
     * Copy type and dates from its origins
     *
     * @param mfOrigem
     */
    private MovimentoFisico deepMerge(MovimentoFisico mfOrigem) {
        if(mfOrigem.getProduto() != null) {
            //System.out.println("deep merge..." + mfOrigem.getProduto().getNome());

            List<MovimentoFisico> mfs = new ArrayList<>();
            for (MovimentoFisico mfComponente : mfOrigem.getMovimentosFisicosComponente()) {
                /*MovimentoFisico mf = new MovimentoFisico();
                mf = mfComponente;
                mf.setMovimentoFisicoTipo(mfOrigem.getMovimentoFisicoTipo());
                mf.setDataEntrada(mfOrigem.getDataEntrada());
                mf.setDataEntradaPrevista(mfOrigem.getDataEntradaPrevista());
                mf.setDataSaida(mfOrigem.getDataSaida());
                mf.setDataSaidaPrevista(mfOrigem.getDataSaidaPrevista());

                //mfComponente = mf;

                mf = deepMerge(mf);

                mfs.add(mf);*/


                //mfOrigem.addMovimentoFisicoComponente(mf);

                mfComponente.setMovimentoFisicoTipo(mfOrigem.getMovimentoFisicoTipo());
                mfComponente.setDataEntrada(mfOrigem.getDataEntrada());
                mfComponente.setDataEntradaPrevista(mfOrigem.getDataEntradaPrevista());
                mfComponente.setDataSaida(mfOrigem.getDataSaida());
                mfComponente.setDataSaidaPrevista(mfOrigem.getDataSaidaPrevista());

                deepMerge(mfComponente);

                /*mfOrigem.addMovimentoFisicoComponente(mfComponente);*/
            }

            //for (MovimentoFisico mfComponente : mfOrigem.getMovimentosFisicosComponente()) {

                //deepMerge(mfComponente);

                //mfOrigem.addMovimentoFisicoComponente(mfComponente);
            //}
            /*
            for(MovimentoFisico mf : mfs) {
                mfOrigem.addMovimentoFisicoComponente(mf);
            }*/
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
                mfEstornado.getProduto(), 
                mfEstornado.getCodigo(), 
                mfEstornado.getDescricao(),
                mfEstornado.getProdutoTipo(),
                mfEstornado.getSaida(), mfEstornado.getEntrada(), 
                mfEstornado.getValor(), mfEstornado.getDescontoPercentual(),
                mfEstornado.getUnidadeComercialVenda(), 
                mfEstornado.getMovimentoFisicoTipo(), null);
        
        mfEstorno.setVenda(mfEstornado.getVenda()); //para não aparecer no estoque quando orçamento
        mfEstorno.setDataAndamento(mfEstornado.getDataAndamento());
        mfEstorno.setDataEntrada(mfEstornado.getDataEntrada());
        mfEstorno.setDataEntradaPrevista(mfEstornado.getDataEntradaPrevista());
        mfEstorno.setDataPronto(mfEstornado.getDataPronto());
        mfEstorno.setDataProntoPrevista(mfEstornado.getDataProntoPrevista());
        mfEstorno.setDataSaida(mfEstornado.getDataSaida());
        mfEstorno.setDataSaidaPrevista(mfEstornado.getDataSaidaPrevista());
        
        
        /*for (MovimentoFisico mfComponenteEstornado : mfEstornado.getMovimentosFisicosComponente()) {
            System.out.println("recursivo?");
            //remove(mfComponenteEstornado); //recursivo
        }*/
        //mfEstorno = save(mfEstorno); //2019-01-24 Para não duplicar estornos
        mfEstornado.addEstorno(mfEstorno);
        mfEstorno = save(mfEstorno); //2020-01-17
        
        //2019-07-17 Causava centenas de consultas ao movimentoFisico
        //Aparentemente o estoque está refletindo normalmente mesmo sem isso
        //mfEstornado.getProduto().addMovimentoFisico(mfEstorno); //2019-06-10 atualizar estoque
        
        
        //GERAR ESTORNO DA DEVOLUÇÃO TAMBÉM
        if(mfEstornado.getDevolucao() != null) {
            remove(mfEstornado.getDevolucao());
        }
        
        //Estornar itens de produto montado
        if(!mfEstornado.getMontagemItens().isEmpty()) {
            for(MovimentoFisico mf : mfEstornado.getMontagemItens()) {
                remove(mf);
            }
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
                itemDevolver.getProduto(), 
                itemDevolver.getProduto().getCodigo(), 
                itemDevolver.getDescricao(),
                itemDevolver.getProdutoTipo(),
                itemDevolver.getSaida(), itemDevolver.getEntrada(), 
                itemDevolver.getValor(), itemDevolver.getDescontoPercentual(),
                itemDevolver.getUnidadeComercialVenda(), 
                itemDevolver.getMovimentoFisicoTipo(), null);
            
            mfDevolucao.setDataEntradaPrevista(dataEntradaPrevista);
            mfDevolucao.setDataSaidaPrevista(null);
            mfDevolucao.setMovimentoFisicoTipo(MovimentoFisicoTipo.DEVOLUCAO_ALUGUEL);
            
        }
        
        //mfDevolucao = save(mfDevolucao);
        
        //---
        
        //2019-02-18 Removido pois salva pelo pai(venda) ???
        //mfDevolucao = save(mfDevolucao);

        mfDevolucao = save(mfDevolucao);
        
        itemDevolver.addDevolucao(mfDevolucao);
        
        itemDevolver = save(itemDevolver);
        
        //iterar filhos
        for (MovimentoFisico mfComponente : itemDevolver.getMovimentosFisicosComponente()) {
            System.out.println("mfComponente: " + mfComponente.getId() + " - " + mfComponente.getProduto().getNome());
            //gerarDevolucaoPrevista(mfComponente, dataEntradaPrevista);
        }
        
        return itemDevolver;
    }
    

    public BigDecimal getSaldoAnterior(MovimentoFisico movimentoFisico) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
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
        } finally {
            em.close();
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
     * compostos). Ignora orçamento e cancelado
     */
    protected List<MovimentoFisico> findPorIntervalo(Produto produto, Timestamp dataInicial, Timestamp dataFinal) {
        EntityManager em = CONNECTION_FACTORY.getConnection();
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
            
            predicates.add(cb.isNull(rootJoin.get("cancelamento")));

            if (dataInicial != null) {
                predicates.add(cb.greaterThanOrEqualTo(rootMovimentoFisico.get("vencimento"), (Comparable) dataInicial));
            }

            if (dataFinal != null) {
                predicates.add(cb.lessThanOrEqualTo(rootMovimentoFisico.get("vencimento"), (Comparable) dataFinal));
            }
            
            //List<Predicate> predicatesJoin = new ArrayList<>();
            
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
        } finally {
            em.close();
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
        EntityManager em = CONNECTION_FACTORY.getConnection();
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
        } finally {
            em.close();
        }
        
        return listMovimentoFisico;
    }
    
    

}
