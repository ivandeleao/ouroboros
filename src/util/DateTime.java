/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author ivand
 */
public class DateTime {

    /**
     * Converte e formata datas de String para Timestamp
     *
     * @param value nos formatos: "dd/MM/yyyy" ou "dd/MM/yyyy HH:mm:ss"
     * @return
     */
    public static Timestamp fromString(String value) {
        try {
            SimpleDateFormat formatter;
            if (value.length() == 10) {
                value += " 00:00:00";
            }
            formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = (Date) formatter.parse(value);
            java.sql.Timestamp timeStampDate = new Timestamp(date.getTime());

            return timeStampDate;
        } catch (ParseException e) {
            System.out.println("Exception :" + e);
            return null;
        }
    }
    
    public static LocalDateTime fromStringLDT(String value) {
        try {
            Locale ptBr = new Locale("pt", "BR");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", ptBr);
            if (value.length() == 10) {
                value += " 00:00:00";
            }
            System.out.println("value: " + value);
            
            return LocalDateTime.parse(value, formatter);
            
        } catch (Exception e) {
            System.err.println("Erro ao converter String para LocalDateTime");
            return null;
        }
    }
    
    public static LocalDate fromStringDateLDT(String value) {
        try {
            Locale ptBr = new Locale("pt", "BR");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", ptBr);
            
            return LocalDate.parse(value, formatter);
            
        } catch (Exception e) {
            System.err.println("Erro ao converter String para LocalDate");
            return null;
        }
    }

    /**
     *
     * @param timestamp
     * @return Data e Hora "dd/MM/yyyy HH:mm:ss"
     */
    public static String toString(Timestamp timestamp) {
        String data = "";
        if (timestamp != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            data = simpleDateFormat.format(timestamp);
        }

        return data;
    }
    
    public static String toString(LocalDateTime localDateTime) {
        String data = "";
        if (localDateTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            data = localDateTime.format(formatter);
        }

        return data;
    }

    /**
     *
     * @param timestamp
     * @return apenas data dd/MM/yyyy
     */
    public static String toStringDate(Timestamp timestamp) {
        String data = "";
        if(timestamp != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            data = simpleDateFormat.format(timestamp);
        }
        return data;
    }
    
    public static String toStringDate(java.sql.Date date) {
        String data = "";
        if (date != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            data = simpleDateFormat.format(date);
        }

        return data;
    }
    
    public static String toStringDate(LocalDateTime localDateTime) {
        String data = "";
        if (localDateTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            data = localDateTime.format(formatter);
        }

        return data;
    }
    
    public static String toStringDate(LocalDate localDate) {
        String data = "";
        if (localDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            data = localDate.format(formatter);
        }

        return data;
    }
    
    public static String toStringDataAbreviada(Timestamp timestamp) {
        String data = "";
        if(timestamp != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy");
            data = simpleDateFormat.format(timestamp);
        }
        return data;
    }
    
    public static String toStringDataAbreviadaLDT(LocalDateTime localDateTime) {
        String data = "";
        if (localDateTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
            data = localDateTime.format(formatter);
        }

        return data;
    }

    
    public static String toStringDataAbreviada(java.sql.Date date) {
        String data = "";
        if (date != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy");
            data = simpleDateFormat.format(date);
        }

        return data;
    }

    //--------------------------------------------------------------------------
    
    public static String toStringDataPorExtenso(LocalDate localDate) {
        String data = "";
        if (localDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy");
            data = localDate.format(formatter);
        }

        return data;
    }
    
    //--------------------------------------------------------------------------
    
    public static Timestamp getNow() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static java.sql.Date toSqlDate(String data) {
        if(data.isEmpty()){
            return null;
        }
        try {
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
            java.util.Date date = sdf1.parse(data);
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            return sqlDate;
        } catch (ParseException e) {
            //JOptionPane.showMessageDialog(MAIN_VIEW, e, "Erro na conversão de data", JOptionPane.ERROR_MESSAGE);
            System.err.println(e);
            return null;
        }
    }
    
    public static Long diasAteHoje(LocalDate data) {
        LocalDate hoje = LocalDate.now();
        
        // Calcula a diferença de dias entre as duas datas
        Long dias = ChronoUnit.DAYS.between(hoje, data);
        
        return dias;
    }
    
    public static Long diasEntreDatas(LocalDate dataInicial, LocalDate dataFinal) {
        LocalDate hoje = LocalDate.now();
        
        // Calcula a diferença de dias entre as duas datas
        Long dias = ChronoUnit.DAYS.between(dataFinal, dataInicial);
        
        return dias;
    }
}
