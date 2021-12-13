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
    private GameStatus s;
    CellType jugador ;
    private int profunditat;
    Boolean time_out;
    Point desde = new Point(0, 0);
    Point finsa = new Point(0, 0);
    
    public GambitodeLOA(int profunditat){
        this.nom =  nom;
        this.profunditat = profunditat;
    
    }

    @Override
    public void timeout() {
       time_out = true;
    }

  @Override
    public String getName() {
        return "Gambito(" + nom + ")";
    }
    
    public Move move(GameStatus s){
    jugador = s.getCurrentPlayer();
    this.s = s;
    int puntuacioMax = Integer.MIN_VALUE;
    GameStatus aux = new GameStatus(s);
    
    int res = minimax(aux, this.profunditat, true);
    aux.movePiece(desde, finsa);
    if (res > puntuacioMax){
    puntuacioMax = res;
    }
    
        System.out.println("desde: "+desde+ "finsa:"+finsa);
 
    return new Move(desde, finsa, 0, 0, SearchType.MINIMAX);
    }
    
    public int minimax(GameStatus s, int profunditat, boolean maxi) {
         //jugador = s.getCurrentPlayer();  
         //jugador = CellType.opposite(jugador);
         
         if(profunditat == 0 || s.isGameOver()){
             jugador = s.getCurrentPlayer();
            int valor = 0;
            int number_pieces = s.getNumberOfPiecesPerColor(jugador);
            
            
            for (int i = 0; i < number_pieces; i++) {
           
                desde = s.getPiece(jugador, i);
                ArrayList<Point> movPossibles = s.getMoves(desde);
                 for (int movIt = 0; movIt < movPossibles.size(); movIt++) {
                    finsa = movPossibles.get(movIt);
                    GameStatus aux = new GameStatus(s);
                    
                    valor = heuristica(s);
               
                }
            }
             return valor;          
            
        }

        if (maxi == true) {
           
            jugador = s.getCurrentPlayer();
            int puntuacioMax = Integer.MIN_VALUE;
            
            int number_pieces = s.getNumberOfPiecesPerColor(jugador);
            
            
            for (int i = 0; i < number_pieces; i++) {
           
                desde = s.getPiece(jugador, i);
                ArrayList<Point> movPossibles = s.getMoves(desde);
                 for (int movIt = 0; movIt < movPossibles.size(); movIt++) {
                    finsa = movPossibles.get(movIt);
                    GameStatus aux = new GameStatus(s);
                    aux.movePiece(desde, finsa);
                  
                    int res = minimax(aux, profunditat-1, false);
                    if (puntuacioMax < res) puntuacioMax = res;
                }
            }
            return puntuacioMax;
            
        } else {
           
            jugador = CellType.opposite(jugador);
            int puntuacioMin = Integer.MAX_VALUE;
            
            int number_pieces = s.getNumberOfPiecesPerColor(jugador);
            
            
            for (int i = 0; i < number_pieces; i++) {
                desde = s.getPiece(jugador, i);
                ArrayList<Point> movPossibles = s.getMoves(desde);
                 for (int movIt = 0; movIt < movPossibles.size(); movIt++) {
                    finsa = movPossibles.get(movIt);
                    GameStatus aux = new GameStatus(s);
                    aux.movePiece(desde, finsa);
                  
                    int res = minimax(aux, profunditat-1, true);
                    if (puntuacioMin > res) puntuacioMin = res;
                }
            }
            return puntuacioMin;
           
        }
    }
 public int heuristica(GameStatus taulerAmbMovimentFet) {
        int puntuacio = 0;
        int distancies = 0;
       
        for (int i = 0; i < taulerAmbMovimentFet.getSize(); i++) {
            for (int j = 0; j < taulerAmbMovimentFet.getSize(); j++) {
                Point analitzant = new Point(i, j);
                CellType color = taulerAmbMovimentFet.getPos(analitzant);
                if (color == jugador) {
                    //desde=analitzant;
                    int qn = taulerAmbMovimentFet.getNumberOfPiecesPerColor(jugador);
                    for (int k = 0; k < qn; k++) {
                        Point altraFitxa = s.getPiece(jugador, k);
                        //finsa=altraFitxa;
                        distancies += (int) analitzant.distance(altraFitxa);
                    }
                }
            }
        }

        return puntuacio - distancies;
       
    }
    
}
