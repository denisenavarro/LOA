/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upc.epsevg.prop.loa;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

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
    Point mejorOrigenIDS = null;
    Point mejorDestinoIDS = null;
    int alpha;
    int beta;
    
    //Taula de zobrist per representar el taulell
    long[][][] ZobristTable;
    /**
     * Inicianilització de variables, si no ens passen tipus de partida establim per defecte la 0 (minimax simple)
     * @param profunditat -> profunditatInicial
     */
    public GambitodeLOA(int profunditat) {
        this.nom = nom;
        this.profunditatInicial = profunditat;
        this.tipoPartida = 0;
        
        //inicializació i generació de la taula zobrist
        this.ZobristTable = new long[8][8][3];
        generaTaulaHash();
    }
    /**
     * Inicializació de variables
     * @param profunditat ->profunditatInicial
     * @param tipo -> tipoPartida
     */
    public GambitodeLOA(int profunditat, int tipo) {
        this.nom = nom;
        this.profunditatInicial = profunditat;
        this.tipoPartida = tipo;
        
        //inicializació i generació de la taula zobrist
        this.ZobristTable = new long[8][8][3];
        generaTaulaHash();
    }
    /**
     * Funció que posa la variable time_out = true
     */
    @Override
    public void timeout() {
        time_out = true;
    }
    /**
     * Funció per obtenir el nom
     * @return "Gambito(" + nom + ")"
     */
    @Override
    public String getName() {
        return "Gambito(" + nom + ")";
    }

    /*public long random() {

        Random r = new Random();
        int high = ((int) Math.pow(2, 64)) - 1;
        int result = r.nextInt(high);
        return result;
    }*/

    /*public int index(GameStatus s, CellType ficha) {
        CellType actual = s.currentPlayer;
        CellType oponent = CellType.opposite(actual);
        if (ficha == actual) {
            return 0;
        } else if(ficha == oponent){
            return 1;
        }else return -1;

    }*/
    
    /**
     * Funció que genera la Zobrist Table que representarà totes les configuracions del taulell
     */
    public void generaTaulaHash() {
        
        Random rand = new Random();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                for (int k = 0; k < 3; k++) {
                    this.ZobristTable[i][j][k] = rand.nextLong();
                }
            }
        }
    }

    /**
     * Funció que itera sobre tot el taulell i retorna el valor hash que representa l'estat del tauler
     * @param s
     * @return h
     */
    public long recalculaHash(GameStatus s){
    long h = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
               if(s.getPos(i, j) != CellType.EMPTY){
                   CellType ficha = s.getPos(i, j);
                   //int ficha = index(s, s.getPos(i, j));
                   //System.out.println("index"+ficha);
                   h ^= this.ZobristTable[i][j][ficha.ordinal()];
               }
            }
        }
        return h;
    }
    /**
     * Funció move en la qual triem entre 3 opcions (0, 1, i 2) i cridarem a minimax, minimax_alfabeta i minimax_iteratiu
     * respectivament
     * @param s
     * @return new Move(desdeResultado, finsaResultado, nodesVisitats, profunditatMax, SearchType.MINIMAX_IDS)
     */
    public Move move(GameStatus s) {
        jugador = s.getCurrentPlayer();
        //generaTaulaHash();
        GameStatus board = new GameStatus(s);
        long hash;
        hash = recalculaHash(board);
        if (tipoPartida == 0) {
            minimax(board, this.profunditatInicial, true);
        } else if (tipoPartida == 1) {
            minimax_alfabeta(board, this.profunditatInicial, true, Integer.MIN_VALUE, Integer.MAX_VALUE);
        } else if (tipoPartida == 2) {
            time_out = false;
            this.profunditatInicial = 1;
            while (!time_out) {
                minimax_alfabeta(board, this.profunditatInicial, true, Integer.MIN_VALUE, Integer.MAX_VALUE);
                this.profunditatInicial++;

                if (!time_out) {

                    mejorOrigenIDS = this.desdeResultado;
                    mejorDestinoIDS = this.finsaResultado;
                }
            }
            this.desdeResultado = mejorOrigenIDS;
            this.finsaResultado = mejorDestinoIDS;

        }
        hash ^= this.ZobristTable[desdeResultado.x][desdeResultado.y][s.getCurrentPlayer().ordinal()];
        hash ^= this.ZobristTable[finsaResultado.x][finsaResultado.y][s.getCurrentPlayer().ordinal()];
        //System.out.println("hashvalor: "+hash);
        return new Move(desdeResultado, finsaResultado, nodesVisitats, profunditatMax, SearchType.MINIMAX_IDS);
    }
    /**
     * 
     * @param tablero
     * @param profRestant
     * @param maxi
     * @return 
     */
    public int minimax(GameStatus tablero, int profRestant, boolean maxi) {
        if (profRestant == 0 || tablero.isGameOver()) {
            if (profunditatMax < (this.profunditatInicial - profRestant)) {
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
    
    /**
     * 
     * @param tablero
     * @param profRestant
     * @param maxi
     * @param alpha
     * @param beta
     * @return 
     */
    public int minimax_alfabeta(GameStatus tablero, int profRestant, boolean maxi, int alpha, int beta) {
        if (profRestant == 0 || tablero.isGameOver()) {
            if (profunditatMax < (this.profunditatInicial - profRestant)) {
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

                    int res = minimax_alfabeta(aux, profRestant - 1, false, alpha, beta);

                    if (puntuacioMax < res) {
                        puntuacioMax = res;
                        // actualizo alfa
                        alpha = Math.max(alpha, res);

                        if (profRestant == this.profunditatInicial) {
                            desdeResultado = desde;
                            finsaResultado = finsa;
                        }
                    }

                    
                    if (beta <= alpha) {
                        break;
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

                    int res = minimax_alfabeta(aux, profRestant - 1, true, alpha, beta);
                    if (puntuacioMin > res) {
                        puntuacioMin = res;
                        //actualizo beta
                        beta = Math.min(beta, res);
                    }

                    // break si alfa < beta
                    if (beta <= alpha) {
                        break;
                    }
                }

            }
            return puntuacioMin;

        }
    }
    
    /**
     * Funció heurística per obtenir la punuacio per al millor moviment
     * @param tauler
     * @return (puntuacio-distaciesjo) o (puntuacio-distanciesop)
     */
    public int heuristica(GameStatus tauler) {
        int puntuacio = 0;
        int distanciesjo = 0;
        int distanciesop = 0;
        int contador_jugades = 0;
        CellType oponent = CellType.opposite(jugador);

        int jo = tauler.getNumberOfPiecesPerColor(jugador);
        int op = tauler.getNumberOfPiecesPerColor(oponent);
        if(contador_jugades < 10){
            distanciesjo*=10;
        }
            for (int i = 0; i < jo; i++) {
                Point fitxa = tauler.getPiece(jugador, i);
                for (int j = i; j < jo; j++) {
                    Point altraFitxa = tauler.getPiece(jugador, j);
                    distanciesjo += (int) fitxa.distance(altraFitxa);
                    contador_jugades++;
                }
            }
            for (int a = 0; a < op; a++) {
                Point fitxa = tauler.getPiece(oponent, a);
                for (int b = a; b < op; b++) {
                    Point otraFitxa = tauler.getPiece(oponent, b);
                    distanciesop += (int) fitxa.distance(otraFitxa);
                    contador_jugades++;
                }
            }
        //System.out.println("contador: "+contador_jugades+"distanciesjo"+distanciesjo);
        if (distanciesjo > distanciesop) {
            return puntuacio - distanciesjo;
        } else {
            return puntuacio - distanciesop;
        }

    }

}
