/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upc.epsevg.prop.loa;

/**
 *
 * @author Denise NAvarro i Irina Gómez
 */
public class ElMeuStatus extends GameStatus{
    
    public ElMeuStatus(int [][] tauler){
        super(tauler);
    }

    public ElMeuStatus(GameStatus gs){
        super(gs);
    }

    public int getheuristica(){
        return 0;
    }
}


