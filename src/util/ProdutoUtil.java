/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.swing.JOptionPane;
import ouroboros.Ouroboros;
import view.Toast;

/**
 *
 * @author ivand
 */
public class ProdutoUtil {

    public static BigDecimal calcularValorVenda(BigDecimal valorCompra, BigDecimal margemLucro) {
        BigDecimal valorVenda = BigDecimal.ZERO;

        if (margemLucro.compareTo(new BigDecimal(100)) > 0) {
            new Toast("Atenção: Impossível margem de lucro maior que 100", 3000l);

        } else if (margemLucro.compareTo(new BigDecimal(100)) == 0) {
            if (valorCompra.compareTo(BigDecimal.ZERO) > 0) {
                new Toast("Atenção: Só é possível 100% de lucro se o valor de compra for 0.", 3000l);
            }

        } else if (margemLucro.compareTo(BigDecimal.ZERO) > 0) {
            valorVenda = new BigDecimal(-100).multiply(valorCompra).divide(margemLucro.subtract(new BigDecimal(100)), 2, RoundingMode.HALF_UP);

        } else {
            valorVenda = valorCompra;
        }

        return valorVenda;

    }

    public static BigDecimal calcularMargemLucro(BigDecimal valorCompra, BigDecimal valorVenda) {

        BigDecimal margemLucro = (valorVenda.subtract(valorCompra)).multiply(new BigDecimal(100));
        if (valorVenda.compareTo(BigDecimal.ZERO) != 0) {
            margemLucro = margemLucro.divide(valorVenda, 2, RoundingMode.HALF_UP);

        } else {
            margemLucro = BigDecimal.ZERO;
        }

        return margemLucro;
    }

}
