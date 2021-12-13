/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upc.epsevg.prop.loa;

import edu.upc.epsevg.prop.loa.Board;
import java.awt.Point;
import java.util.ArrayList;

/**
 *
 * @author Denise Navarro i Irina Gómez
 */
public class GambitodeLOA implements IPlayer, IAuto {

    private String nom;
    CellType jugador;
    private int profunditatInicial;
    private int nodesVisitats;
    private int profunditatMax;
    private int tipoPartida;
    Boolean time_out;
    Point desdeResultado = null;
    Point finsaResultado = null;

    public GambitodeLOA(int profunditat) {
        this.nom = nom;
        this.profunditatInicial = profunditat;
        this.tipoPartida = 0;
    }
    
    public GambitodeLOA(int profunditat, int tipo) {
        this.nom = nom;
        this.profunditatInicial = profunditat;
        this.tipoPartida = tipo;
    }

    @Override
    public void timeout() {
        time_out = true;
    }

    @Override
    public String getName() {
        return "Gambito(" + nom + ")";
    }

    public Move move(GameStatus s) {
        jugador = s.getCurrentPlayer();
        GameStatus board = new GameStatus(s);
        if(tipoPartida == 0)
            minimax(board, this.profunditatInicial, true);
        else if(tipoPartida == 1)
            minimax_alfabeta(board, this.profunditatInicial, true);  
        else if(tipoPartida == 2){
            Point mejorOrigenIDS = null;
            Point mejorDestinoIDS = null;
            this.profunditatInicial = 1;
            while(!time_out){
                //minimax_ids(board, this.profunditatInicial, true);
                this.profunditatInicial++;
                
                if(!time_out){
                    mejorOrigenIDS = this.desdeResultado;
                    mejorDestinoIDS = this.finsaResultado;
                }
            }
            
            this.desdeResultado = mejorOrigenIDS;
            this.finsaResultado = mejorDestinoIDS;
        }
        
        return new Move(desdeResultado, finsaResultado, nodesVisitats, profunditatMax, SearchType.MINIMAX);
    }

    public int minimax(GameStatus tablero, int profRestant, boolean maxi) {
        if (profRestant == 0 || tablero.isGameOver()) {
            if(profunditatMax < (this.profunditatInicial - profRestant)){
                profunditatMax = (this.profunditatInicial - profRestant);
            }

            return heuristica(tablero);
        }

        if (maxi) {
            int puntuacioMax = Integer.MIN_VALUE;

            int number_pieces = tablero.getNumberOfPiecesPerColor(tablero.getCurrentPlayer());
            for (int i = 0; i < number_pieces; i++) {
                Point desde = tablero.getPiece(tablero.getCurrentPlayer(), i);

                ArrayList<Point> movPossibles = tablero.getMoves(desde);
                for (int movIt = 0; movIt < movPossibles.size(); movIt++) {
                    Point finsa = movPossibles.get(movIt);
                    
                    GameStatus aux = new GameStatus(tablero);
                    aux.movePiece(desde, finsa);
                    nodesVisitats++;
                    
                    int res = minimax(aux, profRestant - 1, false);

                    if (puntuacioMax < res) {
                        puntuacioMax = res;
                        if (profRestant == this.profunditatInicial) {
                            desdeResultado = desde;
                            finsaResultado = finsa;
                        }
                    }
                }
            }
            return puntuacioMax;

        } else {
            int puntuacioMin = Integer.MAX_VALUE;

            int number_pieces = tablero.getNumberOfPiecesPerColor(tablero.getCurrentPlayer());
            for (int i = 0; i < number_pieces; i++) {
                Point desde = tablero.getPiece(tablero.getCurrentPlayer(), i);
                
                ArrayList<Point> movPossibles = tablero.getMoves(desde);
                for (int movIt = 0; movIt < movPossibles.size(); movIt++) {
                    Point finsa = movPossibles.get(movIt);
                    
                    GameStatus aux = new GameStatus(tablero);
                    aux.movePiece(desde, finsa);
                    nodesVisitats++;

                    int res = minimax(aux, profRestant - 1, true);
                    if (puntuacioMin > res) {
                        puntuacioMin = res;
                    }
                }
            }
            return puntuacioMin;

        }
    }
    
    public int minimax_alfabeta(GameStatus tablero, int profRestant, boolean maxi) {
        if (profRestant == 0 || tablero.isGameOver()) {
            if(profunditatMax < (this.profunditatInicial - profRestant)){
                profunditatMax = (this.profunditatInicial - profRestant);
            }

            return heuristica(tablero);
        }

        if (maxi) {
            int puntuacioMax = Integer.MIN_VALUE;

            int number_pieces = tablero.getNumberOfPiecesPerColor(tablero.getCurrentPlayer());
            for (int i = 0; i < number_pieces; i++) {
                Point desde = tablero.getPiece(tablero.getCurrentPlayer(), i);

                ArrayList<Point> movPossibles = tablero.getMoves(desde);
                for (int movIt = 0; movIt < movPossibles.size(); movIt++) {
                    Point finsa = movPossibles.get(movIt);
                    
                    GameStatus aux = new GameStatus(tablero);
                    aux.movePiece(desde, finsa);
                    nodesVisitats++;
                    
                    int res = minimax(aux, profRestant - 1, false);

                    if (puntuacioMax < res) {
                        puntuacioMax = res;
                        // actualizo alfa

                        if (profRestant == this.profunditatInicial) {
                            desdeResultado = desde;
                            finsaResultado = finsa;
                        }
                    }
                    
                    // break si alfa < beta  
                }
                
                // break si alfa < beta
            }
            return puntuacioMax;

        } else {
            int puntuacioMin = Integer.MAX_VALUE;

            int number_pieces = tablero.getNumberOfPiecesPerColor(tablero.getCurrentPlayer());
            for (int i = 0; i < number_pieces; i++) {
                Point desde = tablero.getPiece(tablero.getCurrentPlayer(), i);
                
                ArrayList<Point> movPossibles = tablero.getMoves(desde);
                for (int movIt = 0; movIt < movPossibles.size(); movIt++) {
                    Point finsa = movPossibles.get(movIt);
                    
                    GameStatus aux = new GameStatus(tablero);
                    aux.movePiece(desde, finsa);
                    nodesVisitats++;

                    int res = minimax(aux, profRestant - 1, true);
                    if (puntuacioMin > res) {
                        puntuacioMin = res;
                        //actualizo beta
                    }
                    
                    // break si alfa < beta
                }

                // break si alfa < beta
            }
            return puntuacioMin;

        }
    }

    public int minimax_ids(GameStatus tablero, int profRestant, boolean maxi) {
        if (profRestant == 0 || tablero.isGameOver()) {
            if(profunditatMax < (this.profunditatInicial - profRestant)){
                profunditatMax = (this.profunditatInicial - profRestant);
            }

            return heuristica(tablero);
        }

        if (maxi) {
            int puntuacioMax = Integer.MIN_VALUE;

            int number_pieces = tablero.getNumberOfPiecesPerColor(tablero.getCurrentPlayer());
            for (int i = 0; i < number_pieces && !time_out; i++) {
                Point desde = tablero.getPiece(tablero.getCurrentPlayer(), i);

                ArrayList<Point> movPossibles = tablero.getMoves(desde);
                for (int movIt = 0; movIt < movPossibles.size() && !time_out; movIt++) {
                    Point finsa = movPossibles.get(movIt);
                    
                    GameStatus aux = new GameStatus(tablero);
                    aux.movePiece(desde, finsa);
                    nodesVisitats++;
                    
                    int res = minimax(aux, profRestant - 1, false);

                    if (puntuacioMax < res) {
                        puntuacioMax = res;
                        // actualizo alfa

                        if (profRestant == this.profunditatInicial) {
                            desdeResultado = desde;
                            finsaResultado = finsa;
                        }
                    }
                    
                    // break si alfa < beta  
                }
                
                // break si alfa < beta
            }
            return puntuacioMax;

        } else {
            int puntuacioMin = Integer.MAX_VALUE;

            int number_pieces = tablero.getNumberOfPiecesPerColor(tablero.getCurrentPlayer());
            for (int i = 0; i < number_pieces && !time_out; i++) {
                Point desde = tablero.getPiece(tablero.getCurrentPlayer(), i);
                
                ArrayList<Point> movPossibles = tablero.getMoves(desde);
                for (int movIt = 0; movIt < movPossibles.size() && !time_out; movIt++) {
                    Point finsa = movPossibles.get(movIt);
                    
                    GameStatus aux = new GameStatus(tablero);
                    aux.movePiece(desde, finsa);
                    nodesVisitats++;

                    int res = minimax(aux, profRestant - 1, true);
                    if (puntuacioMin > res) {
                        puntuacioMin = res;
                        //actualizo beta
                    }
                    
                    // break si alfa < beta
                }

                // break si alfa < beta
            }
            return puntuacioMin;

        }
    }
    
    public int heuristica(GameStatus tauler) {
        int puntuacio = 0;
        int distancies = 0;
        
        int qn = tauler.getNumberOfPiecesPerColor(jugador);
        for (int i = 0; i < qn; i++) {
            Point fitxa = tauler.getPiece(jugador, i);
            for(int j = i; j < qn; j++){
                Point altraFitxa = tauler.getPiece(jugador, j);
                distancies += (int) fitxa.distance(altraFitxa);            
            }
        }
        
        return puntuacio - distancies;

    }

}
