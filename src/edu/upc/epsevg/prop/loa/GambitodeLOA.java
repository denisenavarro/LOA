/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upc.epsevg.prop.loa;

import edu.upc.epsevg.prop.loa.Board;
import edu.upc.epsevg.prop.loa.GameStatus;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Denise Navarro i Irina Gómez
 */
public class GambitodeLOA implements IPlayer, IAuto {
    private String name;
    private GameStatus s;

    public GambitodeLOA(String name) {
        this.name = name;
    }
    /**
     * Jugador Gambito
     * 
     */
    public String getName() {
        return "Gambito(" + name + ")";
    }
     public Move move(GameStatus s) {

        CellType color = s.getCurrentPlayer();
        this.s = s;
        int qn = s.getNumberOfPiecesPerColor(color);
        ArrayList<Point> pendingAmazons = new ArrayList<>();
        for (int q = 0; q < qn; q++) {
            pendingAmazons.add(s.getPiece(color, q));
        }
        
        // Iterem aleatòriament per les peces fins que trobem una que es pot moure.
        Point queenTo = null;
        Point queenFrom = null;
        int puntuacionMax = Integer.MIN_VALUE;
        for(int i = 0; i < pendingAmazons.size(); i++){
            ArrayList<Point> movPossibles = s.getMoves(pendingAmazons.get(i));
            for(Point mov : movPossibles){
                GameStatus saux = new GameStatus(s);
                saux.movePiece(pendingAmazons.get(i), mov);
                int puntuacio = heuristica(saux);
                
                // Comprovar si la punt > puntMax
                // Guardarme a on vaig, d'on vinc/fitxa que es mou
            }
            
        }
        while (queenTo == null) {
            Random rand = new Random();
            int q = rand.nextInt(pendingAmazons.size());
            queenFrom = pendingAmazons.remove(q);
            queenTo = posicioRandomAmazon(s, queenFrom);
        }

        // "s" és una còpia del tauler, per es pot manipular sense perill
        s.movePiece(queenFrom, queenTo);

        return new Move(queenFrom, queenTo, 0, 0, SearchType.RANDOM);
    }

    @Override
    public void timeout() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

