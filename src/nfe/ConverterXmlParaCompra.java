/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nfe;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import model.mysql.bean.fiscal.MeioDePagamento;
import model.mysql.bean.principal.MovimentoFisico;
import model.mysql.bean.principal.MovimentoFisicoTipo;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.principal.documento.TipoOperacao;
import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.principal.documento.VendaTipo;
import model.mysql.bean.principal.pessoa.Pessoa;
import model.mysql.dao.principal.pessoa.PessoaDAO;
import nfe.bean.Det;
import nfe.bean.Dup;
import nfe.bean.Emit;
import nfe.bean.Ide;
import nfe.bean.InfNFe;
import nfe.bean.NFe;
import nfe.bean.Prod;
import util.DateTime;
import util.Decimal;
import util.MwString;

/**
 *
 * @author ivand
 */
public class ConverterXmlParaCompra {

    public static Venda nfe(NFe nfe) {
        
        InfNFe infNfe = nfe.getInfNFe();
        Ide ide = nfe.getInfNFe().getIde();
        
        Venda documento = new Venda(VendaTipo.COMPRA);
        
        documento.setTipoOperacao(TipoOperacao.ENTRADA);
        documento.setPessoa(emit(infNfe.getEmit()));
        
        documento.setSerieNfe(Integer.valueOf(ide.getSerie()));
        documento.setNumeroNfe(Integer.valueOf(ide.getnNF()));
        documento.setDataHoraEmissaoNfe(DateTime.fromStringToLDTOffsetZone(ide.getDhEmi()));
        documento.setDataHoraSaidaEntradaNfe(DateTime.fromStringToLDTOffsetZone(ide.getDhSaiEnt()));

        
        //itens
        for(MovimentoFisico mf : dets(infNfe.getDets())) {
            documento.addMovimentoFisico(mf);
        }
        
        /*acrescimoMonetarioProdutos;
        acrescimoPercentualProdutos;
        descontoMonetarioProdutos;
        descontoPercentualProdutos;

        acrescimoMonetarioServicos;
        acrescimoPercentualServicos;
        descontoMonetarioServicos;
        descontoPercentualServicos;*/

        
        //parcelas
        for(Parcela p : dups(infNfe.getCobr().getDups())) {
            documento.addParcela(p);
        }
        
        
        return documento;
    }
    
    
    
    public static Pessoa emit(Emit emit) {
        
        //Verificar se já existe cadastro
        Pessoa pessoa = new PessoaDAO().findByCpfCnpj(MwString.formatarCnpj(emit.getCnpj()));
        if(pessoa != null) {
            return pessoa;
        }

        //Criar novo cadastro se não existir
        pessoa = new Pessoa();
        
        pessoa.setCliente(false);
        pessoa.setFornecedor(true);

        pessoa.setNome(emit.getxNome());
        pessoa.setNomeFantasia(emit.getxFant());
        pessoa.setCnpj(MwString.formatarCnpj(emit.getCnpj()));
        pessoa.setIe(emit.getIe());
        //ieIsento;
        //im;
        //suframa;

        pessoa.setTelefone1(emit.getEnderEmit().getFone());
        //email;
        pessoa.setCep(MwString.formatarCep(emit.getEnderEmit().getCep()));

        pessoa.setEndereco(emit.getEnderEmit().getxLgr());
        pessoa.setNumero(emit.getEnderEmit().getNro());
        pessoa.setBairro(emit.getEnderEmit().getNro());
        pessoa.setCodigoMunicipio(emit.getEnderEmit().getcMun());

        pessoa.setObservacao("importado por XML");

        return pessoa;

    }

    public static List<MovimentoFisico> dets(List<Det> dets) {

        List<MovimentoFisico> mfs = new ArrayList<>();

        for (Det det : dets) {

            Prod prod = det.getProd();

            MovimentoFisico mf = new MovimentoFisico(
                    prod.getProduto(),
                    prod.getcProd(),
                    prod.getxProd(),
                    Decimal.fromStringComPonto(prod.getqCom()),
                    BigDecimal.ZERO,
                    Decimal.fromStringComPonto(prod.getvUnCom()),
                    BigDecimal.ZERO,
                    null,
                    MovimentoFisicoTipo.COMPRA,
                    "importado por XML");

            mf.setValorFrete(Decimal.fromStringComPonto(prod.getvFrete()));
            mf.setValorSeguro(Decimal.fromStringComPonto(prod.getvSeg()));
            mf.setAcrescimoMonetario(Decimal.fromStringComPonto(prod.getvOutro()));
            mf.setDescontoMonetario(Decimal.fromStringComPonto(prod.getvDesc()));
            
            mfs.add(mf);
        }

        return mfs;
    }

    /**
     * 
     * @param dups Duplicatas
     * @return Parcelas
     */
    public static List<Parcela> dups(List<Dup> dups) {

        List<Parcela> parcelas = new ArrayList<>();

        for (Dup dup : dups) {
            Parcela p = new Parcela();

            p.setNumero(Integer.valueOf(dup.getnDup()));

            p.setVencimento(LocalDate.parse(dup.getdVenc()));

            p.setValor(Decimal.fromStringComPonto(dup.getvDup()));
            
            p.setMulta(BigDecimal.ZERO);
            
            p.setJurosMonetario(BigDecimal.ZERO);
            p.setJurosPercentual(BigDecimal.ZERO);
            
            p.setMeioDePagamento(MeioDePagamento.BOLETO_BANCARIO);

            parcelas.add(p);
        }

        return parcelas;

    }
}
