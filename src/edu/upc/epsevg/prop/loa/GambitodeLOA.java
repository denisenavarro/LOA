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
    CellType jugadorActual;

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

    @Override
    public void timeout() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Move move(GameStatus s) {

        jugadorActual = s.getCurrentPlayer();
        this.s = s;
        int qn = s.getNumberOfPiecesPerColor(jugadorActual);

        // Iterem aleatòriament per les peces fins que trobem una que es pot moure.
        Point queenTo = null;
        Point queenFrom = null;
        int puntuacioMax = Integer.MIN_VALUE;
        for (int i = 0; i < qn; i++) {
            Point posicioIniciCandidat = s.getPiece(jugadorActual, i);
            ArrayList<Point> movPossibles = s.getMoves(posicioIniciCandidat);
            for (int movIt = 0; movIt < movPossibles.size(); movIt++) {
                Point posicioFinalCandidat = movPossibles.get(movIt);

                GameStatus taulerAmbMovimentFet = new GameStatus(s);
                taulerAmbMovimentFet.movePiece(posicioIniciCandidat, posicioFinalCandidat);
                int puntuacio = heuristica(taulerAmbMovimentFet);

                if (puntuacio > puntuacioMax) {
                    puntuacioMax = puntuacio;
                    queenFrom = posicioIniciCandidat;
                    queenTo = posicioFinalCandidat;
                }
            }
        }

        return new Move(queenFrom, queenTo, 0, 0, SearchType.RANDOM);
    }

    public int heuristica(GameStatus taulerAmbMovimentFet) {
        int puntuacio = 0;
        int distancies = 0;

        for (int i = 0; i < taulerAmbMovimentFet.getSize(); i++) {
            for (int j = 0; j < taulerAmbMovimentFet.getSize(); j++) {
                Point analitzant = new Point(i, j);
                CellType color = taulerAmbMovimentFet.getPos(analitzant);
                if (color == jugadorActual) {
                    int qn = taulerAmbMovimentFet.getNumberOfPiecesPerColor(jugadorActual);
                    for (int k = 0; k < qn; k++) {
                        Point altraFitxa = s.getPiece(jugadorActual, k);
                        distancies += (int) analitzant.distance(altraFitxa);
                    }
                }
            }
        }

        return puntuacio - distancies;
    }
}
