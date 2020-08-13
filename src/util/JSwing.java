/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import util.Document.MonetarioDocument;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.Document;
import util.Document.CepDocument;
import util.Document.CnpjDocument;
import util.Document.CpfCnpjDocument;
import util.Document.CpfDocument;
import util.Document.DataDocument;
import util.Document.DiaMesDocument;
import util.Document.HoraMinutoDocument;
import util.Document.InteiroDocument;
import util.Document.PlacaDocument;
import util.Document.TelefoneDocument;

/**
 *
 * @author ivand
 */
public class JSwing {

    /**
     * Define formatação, máscara de entrada e outros itens dos componentes na
     * tela
     *
     * @param container
     */
    public static void startComponentsBehavior(Container container) {

        CaretListener caretListener = new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                JFormattedTextField thisTxt = (JFormattedTextField) e.getSource();

                if (thisTxt.getText().length() > 0 && thisTxt.getCaretPosition() < thisTxt.getText().length()) {
                    thisTxt.setCaretPosition(thisTxt.getText().length());
                }
            }
        };
        
        
        
        FocusListener focusListener = new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                JTextField thisTxt = (JTextField) e.getComponent();
                
                thisTxt.setSelectionStart(0);
                thisTxt.setSelectionEnd(thisTxt.getText().length());
            }

            @Override
            public void focusLost(FocusEvent e) {
                JTextField thisTxt = (JTextField) e.getComponent();
                
                int parInicio = thisTxt.getName().indexOf("(");
                int parFim = thisTxt.getName().indexOf(")");
                
                if(parInicio > -1 && parFim > -1) {
                    int decimais = Integer.parseInt(thisTxt.getName().substring(parInicio + 1, parFim));
                    //System.out.println("decimais: " + decimais);
                //2020-07-15
                    //thisTxt.setText(Decimal.toString(Decimal.fromString(thisTxt.getText()), decimais));
                    //2020-08-05
                    thisTxt.setText(Decimal.toStringAjustarDecimais(Decimal.fromString(thisTxt.getText()), decimais));
                    
                } else {
                    thisTxt.setText(Decimal.toStringAjustarDecimais(Decimal.fromString(thisTxt.getText()), 2));
                    
                }//
            }
        };

        /*for (Component c : findComponentByName(container, "decimal")) {
            ((JFormattedTextField) c).setDocument(new MonetarioDocument());
            ((JFormattedTextField) c).addCaretListener(caretListener);
            ((JFormattedTextField) c).setText("0");
            ((JFormattedTextField) c).setHorizontalAlignment(SwingConstants.RIGHT);
        }*/
        
        for (Component c : findComponentByName(container, "decimal")) {
            JFormattedTextField thisTxt = (JFormattedTextField) c;
            thisTxt.setDocument(new MonetarioDocument());
            thisTxt.addFocusListener(focusListener);
            thisTxt.setText("0");
            thisTxt.setHorizontalAlignment(SwingConstants.RIGHT);
            
            int parInicio = thisTxt.getName().indexOf("(");
            int parFim = thisTxt.getName().indexOf(")");
            int decimais = 2;

            if(parInicio > -1 && parFim > -1) {
                decimais = Integer.parseInt(thisTxt.getName().substring(parInicio + 1, parFim));
                System.out.println("decimais: " + decimais);
            }

            Decimal.fromString(thisTxt.getText());
            thisTxt.setText(Decimal.toString(Decimal.fromString(thisTxt.getText()), decimais));
            
            
        }

        /*for (Component c : findComponentByName(container, "decimal3")) {
            ((JFormattedTextField) c).setDocument(new MonetarioDocument(3));
            ((JFormattedTextField) c).addCaretListener(caretListener);
            ((JFormattedTextField) c).setText("0");
            ((JFormattedTextField) c).setHorizontalAlignment(SwingConstants.RIGHT);
        }*/
        /*
        for (Component c : findComponentByName(container, "decimal(3)")) {
            ((JFormattedTextField) c).setDocument(new MonetarioDocument());
            ((JFormattedTextField) c).addFocusListener(focusListener);
            ((JFormattedTextField) c).setText("0");
            ((JFormattedTextField) c).setHorizontalAlignment(SwingConstants.RIGHT);
        }*/

        for (Component c : findComponentByName(container, "cep")) {
            ((JFormattedTextField) c).setDocument(new CepDocument());
            ((JFormattedTextField) c).addCaretListener(caretListener);
            ((JFormattedTextField) c).setHorizontalAlignment(SwingConstants.RIGHT);
        }

        for (Component c : findComponentByName(container, "cnpj")) {
            ((JFormattedTextField) c).setDocument(new CnpjDocument());
            ((JFormattedTextField) c).addCaretListener(caretListener);
            ((JFormattedTextField) c).setHorizontalAlignment(SwingConstants.RIGHT);
        }

        for (Component c : findComponentByName(container, "cpf")) {
            ((JFormattedTextField) c).setDocument(new CpfDocument());
            ((JFormattedTextField) c).addCaretListener(caretListener);
            ((JFormattedTextField) c).setHorizontalAlignment(SwingConstants.RIGHT);
        }
        
        for (Component c : findComponentByName(container, "cpfCnpj")) {
            ((JFormattedTextField) c).setDocument(new CpfCnpjDocument());
            ((JFormattedTextField) c).addCaretListener(caretListener);
            ((JFormattedTextField) c).setHorizontalAlignment(SwingConstants.RIGHT);
        }

        for (Component c : findComponentByName(container, "data")) {
            ((JFormattedTextField) c).setDocument(new DataDocument());
            ((JFormattedTextField) c).addCaretListener(caretListener);
            ((JFormattedTextField) c).setHorizontalAlignment(SwingConstants.RIGHT);
        }
        
        for (Component c : findComponentByName(container, "diaMes")) {
            ((JFormattedTextField) c).setDocument(new DiaMesDocument());
            ((JFormattedTextField) c).addCaretListener(caretListener);
            ((JFormattedTextField) c).setHorizontalAlignment(SwingConstants.RIGHT);
        }
        
        for (Component c : findComponentByName(container, "horaMinuto")) {
            ((JFormattedTextField) c).setDocument(new HoraMinutoDocument());
            ((JFormattedTextField) c).addCaretListener(caretListener);
            ((JFormattedTextField) c).setText("0");
            ((JFormattedTextField) c).setHorizontalAlignment(SwingConstants.RIGHT);
        }

        for (Component c : findComponentByName(container, "telefone")) {
            ((JFormattedTextField) c).setDocument(new TelefoneDocument());
            ((JFormattedTextField) c).addCaretListener(caretListener);
            ((JFormattedTextField) c).setHorizontalAlignment(SwingConstants.RIGHT);
        }
        
        for (Component c : findComponentByName(container, "inteiro")) {
            ((JFormattedTextField) c).setDocument(new InteiroDocument());
            ((JFormattedTextField) c).addCaretListener(caretListener);
            ((JFormattedTextField) c).setText("0");
            ((JFormattedTextField) c).setHorizontalAlignment(SwingConstants.RIGHT);
        }
        
        for (Component c : findComponentByName(container, "ano")) {
            ((JFormattedTextField) c).setDocument(new InteiroDocument(4));
            ((JFormattedTextField) c).addCaretListener(caretListener);
            ((JFormattedTextField) c).setHorizontalAlignment(SwingConstants.RIGHT);
        }
        
        for (Component c : findComponentByName(container, "placa")) {
            ((JFormattedTextField) c).setDocument(new PlacaDocument());
            ((JFormattedTextField) c).addCaretListener(caretListener);
            //((JFormattedTextField) c).setHorizontalAlignment(SwingConstants.RIGHT);
        }

    }

    public static List<Component> findComponentByName(Container container, String componentName) {
        //Reference: https://technology.amis.nl/2005/12/04/implementing-getcomponentbyname-method-for-swing/

        List<Component> components = new ArrayList<>();

        for (Component component : container.getComponents()) {
            //if(component.getName() != null){

            if (component instanceof JRootPane) {
                JRootPane nestedJRootPane = (JRootPane) component;
                List<Component> internalComps = findComponentByName(nestedJRootPane.getContentPane(), componentName);
                if (internalComps != null) {
                    components.addAll(internalComps);
                }
            } else if (component instanceof JPanel) {
                // JPanel found. Recursing into this panel.
                JPanel nestedJPanel = (JPanel) component;
                List<Component> internalComps = findComponentByName(nestedJPanel, componentName);
                if (internalComps != null) {
                    components.addAll(internalComps);
                }
            } else if (component instanceof JTabbedPane) {
                // JTabbedPane found. Recursing into this panel.
                JTabbedPane nestedJTabbedPanel = (JTabbedPane) component;
                List<Component> internalComps = findComponentByName(nestedJTabbedPanel, componentName);
                if (internalComps != null) {
                    components.addAll(internalComps);
                }
            } else if (component.getName() != null) {
                //encontra apenas palavra inteira
                //componentName = componentName.replace("(", "\\(").replace(")", "\\)");
                String regex = "\\b" + componentName;
                //String regex = "\\b" + componentName + "\\b";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(component.getName());
                if (matcher.find()) {
                    //System.out.println("found: " + component.toString());
                    components.add(component);
                }
            }
            //}
        }
        return components;
    }

    public static List<Component> findComponents(Container container) {
        List<Component> components = new ArrayList<>();

        for (Component component : container.getComponents()) {
            if (component instanceof JRootPane) {
                JRootPane nestedJRootPane = (JRootPane) component;
                List<Component> internalComps = findComponents(nestedJRootPane.getContentPane());
                if (internalComps != null) {
                    components.addAll(internalComps);
                }
            } else if (component instanceof JPanel) {
                // JPanel found. Recursing into this panel.
                JPanel nestedJPanel = (JPanel) component;
                List<Component> internalComps = findComponents(nestedJPanel);
                if (internalComps != null) {
                    components.addAll(internalComps);
                }
            } else if (component instanceof JTabbedPane) {
                // JTabbedPane found. Recursing into this panel.
                JTabbedPane nestedJTabbedPanel = (JTabbedPane) component;
                List<Component> internalComps = findComponents(nestedJTabbedPanel);
                if (internalComps != null) {
                    components.addAll(internalComps);
                }
            } else {

                components.add(component);

            }
        }
        return components;
    }
    
    public static void setComponentesHabilitados(Container container, boolean enabled) {
        List<Component> comps = findComponents(container);
        for(Component c : comps) {
            //System.out.println("habilitar componente " + c.toString() + ": " + enabled);
            c.setEnabled(enabled);
        }
    
    }
}
