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
    CellType jugadorOponent;
 

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
                    queenFrom = posicioIniciCandidat; //d'on vinc
                    queenTo = posicioFinalCandidat;   //on vull anar
                }
            }
        }

        return new Move(queenFrom, queenTo, 0, 0, SearchType.RANDOM);
    }

    public int heuristica(GameStatus taulerAmbMovimentFet) {
        int puntuacio = 0;
        int distanciesGambito = 0;
        int distanciesOponent = 0;

        for (int i = 0; i < taulerAmbMovimentFet.getSize(); i++) {
            for (int j = 0; j < taulerAmbMovimentFet.getSize(); j++) {
                Point analitzant = new Point(i, j);
                CellType color = taulerAmbMovimentFet.getPos(analitzant);
                if (color == jugadorActual) {
                    int qn = taulerAmbMovimentFet.getNumberOfPiecesPerColor(jugadorActual);
                    for (int k = 0; k < qn; k++) {
                        Point altraFitxa = s.getPiece(jugadorActual, k);
                        distanciesGambito += (int) analitzant.distance(altraFitxa);
                    }
                }else if(color!=jugadorActual){
                   //Comptar  la distancia que tenen les fitxes del meu oponent
                   int qn = taulerAmbMovimentFet.getNumberOfPiecesPerColor(jugadorActual);
                    for (int k = 0; k < qn; k++) {
                        Point altraFitxa = s.getPiece(jugadorActual, k);
                        distanciesOponent += (int) analitzant.distance(altraFitxa);
                    } 
                }
            }
        }
        if(distanciesGambito<distanciesOponent) return puntuacio - distanciesGambito;
        else return puntuacio - distanciesOponent;
    }
    
    int putejarOponent(GameStatus taulerAmbMovimentFet){
        /*
        //Bloquejar els camins, interposant fitxes propies. Heurística 1
        if(l'oponent té tantes posicions per avançar com per a juntar-se amb un grupet o fitxa)
            if (tinc tantes fitxes com la distancia que te la fitxa del oponent per a juntar-se amb un grupet)
                blokeja el camí;
            else if(Si una fitxa meua, pot menjar-se una fitxa que uneix un grup)
                me la menjo;
            else if(si tinc una fitxa al camí de l'oponent') 
                la trek   */     
        //Capturar las fichas que unen, es decir, fichas sin las cuales un grupo queda dividido.        
        //Cambiar el número de fichas sobre una línea para modificar así los movimientos posibles.                   
                    return 0;
    }
}
