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
    int nodesNoExplorats;
    private int profunditat_inicial;
    int hashfEXACT = 0;
    int hashfALPHA = 1;
    int hashfBETA = 2;

    class HASHE {
    
    int depth;
    int flags;
    int value;
    

}; 
    public GambitodeLOA(String name) {
        this.name = name;
        nodesNoExplorats = 0;
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
                //int puntuacio = heuristica(taulerAmbMovimentFet); //revisar
                MinimaxResultat resultat = minimax(taulerAmbMovimentFet, this.profunditat_inicial, Integer.MIN_VALUE, Integer.MAX_VALUE, false, posicioIniciCandidat, posicioFinalCandidat);
                if (resultat.valor > puntuacioMax) {
                    puntuacioMax = resultat.valor;
                    queenFrom = posicioIniciCandidat; //d'on vinc
                    queenTo = posicioFinalCandidat;   //on vull anar
                }
            }
        }

        //GameStatus taux = new GameStatus(s);
        
        Move m = new Move(queenFrom, queenTo, 0, 0, SearchType.MINIMAX_IDS);
        String info = "Profunditat màxima:" + m.getMaxDepthReached() + "\n";
        info += "Node explorats:    " + m.getNumerOfNodesExplored();
        System.out.println(info);

        return new Move(queenFrom, queenTo, 0, 0, SearchType.MINIMAX_IDS);
    }
    
    public class MinimaxResultat {
        int valor;
        Point desde;
        Point finsa;
        MinimaxResultat(int valor, Point desde, Point finsa){
            this.valor = valor;
            this.desde = desde;
            this.finsa = finsa;
        }
    }
    
     public MinimaxResultat minimax(GameStatus t, int profunditat, int alpha, int beta, boolean maximitzant, Point queenFrom, Point queenTo) {
        if(profunditat == 0 ){
            int valor = heuristica(t);
            return new MinimaxResultat(valor, queenFrom, queenTo);
        }
        
        if(maximitzant){            
            int puntuacioMax = Integer.MIN_VALUE;
            Point finsaMax = null;
            Point desdeMax = null;
            
            // Mirem tots els moviments possibles
            int qn = t.getNumberOfPiecesPerColor(jugadorActual);
            Boolean continuar = true; // Boolean per poda alfa-beta
            for (int i = 0; i < qn; i++) {
                Point posicioIniciCandidat = t.getPiece(jugadorActual, i);
                ArrayList<Point> movPossibles = t.getMoves(posicioIniciCandidat);
                for (int movIt = 0; movIt < movPossibles.size(); movIt++) {
                    // El moviment és vàlid, l'afegim al tauler
                    GameStatus taux = new GameStatus(t);
                    taux.movePiece(queenFrom, queenTo);
                    
                    // Fem minimax i agafem la jugada amb valor max
                    MinimaxResultat resultat = minimax(taux, profunditat-1, alpha, beta, false, queenFrom, queenTo);
                    if(puntuacioMax < resultat.valor){
                        puntuacioMax = resultat.valor;
                        desdeMax = resultat.desde;
                        finsaMax = resultat.finsa;
                        alpha = resultat.valor;
                    }
                    
                    // Poda alfa-beta
                    
                    if (beta <= alpha) {
                        continuar = false;
                        // Calcul de nodes no explorats
                        int movRestantsAquestaIteracio = t.getNumberOfPiecesPerColor(jugadorActual) - movIt;
                        // Valor inicial: Assumim que tots eren fulla
                        int nodesAdicionals = movRestantsAquestaIteracio;
                        if (profunditat >= 2) {
                            // Cada node restant generarà t.getMida() nodes adicionals
                            nodesAdicionals = (int) movRestantsAquestaIteracio * t.getSize();
                            // Cada node successor generarà t.getMida() nodes per cada profunditat restant
                            nodesAdicionals += (int) Math.pow(t.getSize(), profunditat - 1);
                        }
                        System.out.println("Evitada la exploració de " + nodesAdicionals + " nodes aproximadament");

                        // Sumatori de nodes no explorats
                        nodesNoExplorats += nodesAdicionals;
                }
                
            }
            
            }
            
            return new MinimaxResultat(puntuacioMax, desdeMax, finsaMax);
        } else {            
            int puntuacioMin = Integer.MAX_VALUE;
            Point desdeMin = null;
            Point finsaMin = null;
            
            // Mirem tots els moviments possibles
            Boolean continuar = true; // Boolean per poda alfa-beta
            int qn = t.getNumberOfPiecesPerColor(jugadorActual);
            for (int i = 0; i < qn; i++) {
                Point posicioIniciCandidat = t.getPiece(jugadorActual, i);
                ArrayList<Point> movPossibles = t.getMoves(posicioIniciCandidat);
                for (int movIt = 0; movIt < movPossibles.size(); movIt++) {
                    // El moviment és vàlid, l'afegim al tauler
                    GameStatus taux = new GameStatus(t);
                    taux.movePiece(queenFrom, queenTo);
                    
                    // Fem minimax i agafem la jugada amb valor max
                    MinimaxResultat resultat = minimax(taux, profunditat-1, alpha, beta, true, queenFrom, queenTo);
                    if(resultat.valor < puntuacioMin){
                        puntuacioMin = resultat.valor;
                        desdeMin = resultat.desde;
                        finsaMin = resultat.finsa;
                        beta = resultat.valor;
                    }
                    
                    // Poda alfa-beta
                    if (beta <= alpha) {
                        continuar = false;
                        // Calcul de nodes no explorats
                        int movRestantsAquestaIteracio = t.getNumberOfPiecesPerColor(jugadorActual) - movIt;
                        // Valor inicial: Assumim que tots eren fulla
                        int nodesAdicionals = movRestantsAquestaIteracio;
                        if (profunditat >= 2) {
                            // Cada node restant generarà t.getMida() nodes adicionals
                            nodesAdicionals = (int) movRestantsAquestaIteracio * t.getSize();
                            // Cada node successor generarà t.getMida() nodes per cada profunditat restant
                            nodesAdicionals += (int) Math.pow(t.getSize(), profunditat - 1);
                        }
                        System.out.println("Evitada la exploració de " + nodesAdicionals + " nodes aproximadament");

                        // Sumatori de nodes no explorats
                        nodesNoExplorats += nodesAdicionals;
                    }
                }
            }
            return new MinimaxResultat(puntuacioMin, desdeMin, finsaMin);
        }
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
    
    //int putejarOponent(GameStatus taulerAmbMovimentFet){
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
                    //return 0;
    //}
}
