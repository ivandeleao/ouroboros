/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nfe.bean;

import java.util.List;

/**
 *
 * @author ivand
 */
public class InfNFe {
    
    private Ide ide;
    private Emit emit;
    //private Dest dest ...;
    private Cobr cobr;
    
    List<Det> dets;

    public Ide getIde() {
        return ide;
    }

    public void setIde(Ide ide) {
        this.ide = ide;
    }

    public Emit getEmit() {
        return emit;
    }

    public void setEmit(Emit emit) {
        this.emit = emit;
    }

    public List<Det> getDets() {
        return dets;
    }

    public void setDets(List<Det> dets) {
        this.dets = dets;
    }

    public Cobr getCobr() {
        return cobr;
    }

    public void setCobr(Cobr cobr) {
        this.cobr = cobr;
    }

    
    
}
