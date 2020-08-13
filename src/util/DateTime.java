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
import java.time.LocalTime;
import java.time.MonthDay;
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
            } else if (value.length() == 16) {
                value += ":00";
            }
            
            System.out.println("value: " + value);
            
            return LocalDateTime.parse(value, formatter);
            
        } catch (Exception e) {
            //System.err.println("Erro ao converter String para LocalDateTime");
            return null;
        }
    }
    
    /**
     * 
     * @param value no formato YYYY-MM-DD
     * @return 
     */
    public static LocalDate fromStringIsoToLocalDate(String value) {
        try {
            return LocalDate.parse(value, DateTimeFormatter.ISO_DATE);
            
        } catch (Exception e) {
            System.err.println("Erro fromStringIsoToLdt");
            return null;
        }
    }
    
    /**
     * 
     * @param value no formato yyyy-MM-ddThh:mm:ss+hh:mm
     * @return 
     */
    public static LocalDateTime fromStringToLDTOffsetZone(String value) {
        try {
            return LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME);
            
        } catch (Exception e) {
            System.err.println("Erro fromStringToLDTOffsetZone");
            return null;
        }
    }
    
    public static LocalDate fromStringToLocalDate(String value) {
        try {
            Locale ptBr = new Locale("pt", "BR");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", ptBr);
            
            return LocalDate.parse(value, formatter);
            
        } catch (Exception e) {
            //System.err.println("Erro ao converter String para LocalDate");
            return null;
        }
    }
    
    public static LocalTime fromStringToLocalTime(String value) {
        try {
            if(value.length() < 6) {
                value += ":00";
            }
            Locale ptBr = new Locale("pt", "BR");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss", ptBr);
            
            
            return LocalTime.parse(value, formatter);
            
        } catch (Exception e) {
            System.err.println("Erro ao converter String para LocalTime");
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
    
    /**
     * 
     * @param localDateTime
     * @return dd/MM/yyyy HH:mm
     */
    public static String toString(LocalDateTime localDateTime) {
        String data = "";
        if (localDateTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            data = localDateTime.format(formatter);
        }

        return data;
    }
    
    public static String toStringHoraMinuto(LocalDateTime localDateTime) {
        String data = "";
        if (localDateTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            data = localDateTime.format(formatter);
        }

        return data;
    }
    
    public static String toStringHoraMinuto(LocalTime localTime) {
        String data = "";
        if (localTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            data = localTime.format(formatter);
        }

        return data;
    }

    /**
     *
     * @param timestamp
     * @return dd/MM/yyyy
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
    
    /**
     * 
     * @param localDateTime
     * @return dd/MM/yyyy
     */
    public static String toStringDate(LocalDateTime localDateTime) {
        String data = "";
        if (localDateTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            data = localDateTime.format(formatter);
        }

        return data;
    }
    
    public static String toString(LocalDate localDate) {
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
    
    public static String toStringDataAbreviada(LocalDateTime localDateTime) {
        String data = "";
        if (localDateTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
            data = localDateTime.format(formatter);
        }

        return data;
    }
    
    public static String toStringDataAbreviada(LocalDate localDate) {
        String data = "";
        if (localDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
            data = localDate.format(formatter);
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
    
    public static String getNowHoraMinuto() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
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
    /**
     * 
     * @param data
     * @return número de dias após hoje, ou negativo, se dia anterior a hoje
     */
    public static Long diasDepoisDeHoje(LocalDate data) {
        LocalDate hoje = LocalDate.now();
        
        // Calcula a diferença de dias entre as duas datas
        Long dias = ChronoUnit.DAYS.between(hoje, data);
        
        return dias;
    }
    
    public static Long diasEntreDatas(LocalDate dataInicial, LocalDate dataFinal) {
        // Calcula a diferença de dias entre as duas datas
        Long dias = ChronoUnit.DAYS.between(dataFinal, dataInicial);
        
        return dias;
    }
    
    public static MonthDay fromStringDiaMes(String diaMes) {
        
        if(diaMes == null || diaMes.length() < 5){
            return null;
        }
        
        String dia = diaMes.substring(0, 2);
        String mes = diaMes.substring(3, 5);
        
        return MonthDay.of(Integer.valueOf(mes), Integer.valueOf(dia));
    }
    
}
