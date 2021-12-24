package edu.upc.epsevg.prop.loa.players;

import edu.upc.epsevg.prop.loa.CellType;
import edu.upc.epsevg.prop.loa.GameStatus;
import edu.upc.epsevg.prop.loa.IAuto;
import edu.upc.epsevg.prop.loa.IPlayer;
import edu.upc.epsevg.prop.loa.Move;
import edu.upc.epsevg.prop.loa.SearchType;
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

    private int profunditatInicial; //profunditat que ens passen desde fora
    private int nodesVisitats; //nombre de nodes que hem visitat en total
    private int profunditatMax; //profunditat màxima a la que s'ha aconseguit arribar
    private int tipoPartida; //pot ser un nombre entre 0, 1 i 2 (aquest número determina quina funció de minimax es farà servir)
    Boolean time_out;
    Point desdeResultado; //Point que indica desde el punt on estem
    Point finsaResultado; //Point que indica fins al punt que ens volem moure
    Point mejorOrigenIDS; //Point que farem servir en la part de IDS per determinar el millor origen
    Point mejorDestinoIDS; //Point que farem servir en la part de IDS per determinar el millor destí
    int alpha;
    int beta;

    //Taula de zobrist per representar el tauler
    long[][][] ZobristTable;

    /**
     * Descripció: Constructor parametritzat amb una profunditat determinada que
     * inhabilita el temporitzador (time_out)
     *
     * @param profunditat  El valor de la profunditat cal que sigui major a 0
     */
    public GambitodeLOA(int profunditat) {
        //Ininicialització de tots els points necessaris
        this.mejorDestinoIDS = null;
        this.mejorOrigenIDS = null;
        this.finsaResultado = null;
        this.desdeResultado = null;

        this.nom = nom;
        this.profunditatInicial = profunditat; //La profunditat es fixa
        this.tipoPartida = 0; //com no hem especificat quin tipus de partida volem jugar, la per defecte serà tipus 0 (minimax simple)
        this.time_out = false;
        this.nodesVisitats = 0;
        this.profunditatMax = 0;

        //inicializació i generació de la taula zobrist
        this.ZobristTable = new long[8][8][3];
        generaTaulaHash();
    }

    /**
     * Descripció: Constructor parametritzat amb una profunditat determinada que
     * inhabilita el temporitzador (time_out). A més, amb un tipus, que
     * correspon al tipus de partida que volem jugar 0, 1 o 2 corresponent
     * respectivament a minimax, minimax_alfabeta o minimax iteratiu.
     *
     * @param profunditat  El valor de la profunditat cal que sigui major a 0
     * @param tipo  tipoPartida
     */
    public GambitodeLOA(int profunditat, int tipo) {
        //Ininicialització de tots els points necessaris
        this.mejorDestinoIDS = null;
        this.mejorOrigenIDS = null;
        this.finsaResultado = null;
        this.desdeResultado = null;

        this.nom = nom;
        this.profunditatInicial = profunditat; //La profunditat es fixa
        this.time_out = false;
        this.nodesVisitats = 0;
        this.profunditatMax = 0;

        //inicializació i generació de la taula zobrist
        this.ZobristTable = new long[8][8][2];
        generaTaulaHash();
    }

    /**
     * Descripció: Funció que ens diu que cal parar la cerca en curs perquè s'ha exaurit el
     * temps de joc, es a dir, posa la variable time_out a true.
     */
    @Override
    public void timeout() {
        time_out = true;
    }

    /**
     * Descripció: Funció per obtenir el nom del jugador que fa servir per 
     * visualització a la UI.
     *
     * @return Nom del jugador
     */
    @Override
    public String getName() {
        return "Gambito(" + nom + ")";
    }

    /**
     * Descripció: Funció que genera la Zobrist Table que representarà totes les
     * configuracions del tauler.
     */
    public void generaTaulaHash() {

        Random rand = new Random();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                for (int k = 0; k < 2; k++) {
                    this.ZobristTable[i][j][k] = rand.nextLong();
                }
            }
        }
    }

    /**
     * Descripció: Funció que itera sobre tot el tauler i retorna el valor hash que
     * representa l'estat del tauler en qüestió.
     *
     * @param board  Estat del tauler en aquests moments
     * @return h  Retorna el valor de hash que s'ha obtingut i que representaria l'estat del tauler.
     */
    public long recalculaHash(GameStatus board) {
        long h = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                //Anirem realizant la operació XOR entre totes les fitxes que siguin buides del tauler
                if (board.getPos(i, j) != CellType.EMPTY) {
                    CellType ficha = board.getPos(i, j);
                    h ^= this.ZobristTable[i][j][ficha.ordinal()-1];
                }
            }
        }
        return h;
    }

    /**
     * Descripció: Funció que retorna el millor moviment que s'hagi trobat en una profunditat
     * fixada determinada donat un tauler amb el seu estat corresponent, i fent servir l'algorisme de minimax.
     * L'algorisme de minimax a fer servir, com hem dit abans es divideix en 3 tipus de partida (minimax, minimax_alfabeta
     * o minimax iteratiu).
     * 
     * @param s  Estat en aquells moments del tauler.
     * @return Millor moviment fins a una profunditat fixada.
     */
    public Move move(GameStatus s) {
        //Guardem a la variable jugador, el jugador actual.
        jugador = s.getCurrentPlayer();
        //Inicialitzem per a cada moviment sempre el time_out a false.
        this.time_out = false;
        //Generem un GameStatus auxiliar del que ens passen per paràmetre.
        GameStatus board = new GameStatus(s);
        //Calculem el valor del hash en l'estat del tauler en aquests moments.
        long hash = recalculaHash(board);
        
        //Segons el tipus de Partida que ens hagin indicat farem un dels 3 condicionants.
        //Tipus de Partida 0  minimax
        //Tipus de Partida 1  minimax_alfabeta
        //Tipus de Partida 2  minimax_alfabeta iteratiu
        if (tipoPartida == 0) {
            minimax(board, this.profunditatInicial, true);
        } else if (tipoPartida == 1) {
            //Quan fem la crida a minimax_alfabeta passem alpha que correpon a Integer.MIN_VALUE
            //I passem beta que correspon a Integer.MAX_VALUE.
            minimax_alfabeta(board, this.profunditatInicial, true, Integer.MIN_VALUE, Integer.MAX_VALUE);
        } else if (tipoPartida == 2) {
            //Quan la profunditat és iterada, començarem amb profunditatInicial = 1
            this.profunditatInicial = 1;
            //Mentre no hi hagi time_out fem...
            while (!time_out) {
                //Aprofitem el codi que tenim de minimax_alfabeta per fer-ho de manera iterativa
                minimax_alfabeta(board, this.profunditatInicial, true, Integer.MIN_VALUE, Integer.MAX_VALUE);
                this.profunditatInicial++; //Augmentem la profunditat en 1 unitat.

                if (!time_out) { //Mentre no hi hagi time_out ens guardarem el millorOrigen i el millorDesti IDS

                    mejorOrigenIDS = this.desdeResultado;
                    mejorDestinoIDS = this.finsaResultado;
                }
            }
            //Un cop salta el time_out perquè s'ha exhaurit el temps de joc, cal que actualitzem la informació de desdeResultado i finsaResultado
            //Amb l'informació de mejorOrigenIDS i mejorDestinoIDS, ja que serà necessari per després retorna el millor moviment possible
            this.desdeResultado = mejorOrigenIDS;
            this.finsaResultado = mejorDestinoIDS;

        }
        
        //Aprofitem per realitzar les diferets XORS per obtenir el valor hash 
        hash ^= this.ZobristTable[desdeResultado.x][desdeResultado.y][s.getCurrentPlayer().ordinal()-1];
        hash ^= this.ZobristTable[finsaResultado.x][finsaResultado.y][s.getCurrentPlayer().ordinal()-1];
        
        //Depenent de quin tipus de Partida sigui necessitem que el SearchType sigui MINIMAX o MINIMAX_IDS
        //Per tant, farem un return o un altre segons quines siguien les condicions.
        if (tipoPartida == 0 || tipoPartida == 1) {
            return new Move(desdeResultado, finsaResultado, nodesVisitats, profunditatMax, SearchType.MINIMAX);
        } else {
            return new Move(desdeResultado, finsaResultado, nodesVisitats, profunditatMax, SearchType.MINIMAX_IDS);
        }
    }

    /**
     * Descripció: Executa l'algoritme minimax simple
     * 
     * @param tablero  Estat determinat del tauler.
     * @param profRestant  Profunditat actual de l'algoritme.
     * @param maxi  Determina si volem el resultat màxim o mínim
     * @return retornar el valor heurístic
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
     * Descripció: Executa l'algoritme minimax amb poda alfa-beta
     *  
     * @param tablero  Estat actual del tauler
     * @param profRestant  Profunditat actual de l'algoritme.
     * @param maxi  Determina si volem el resultat màxim o mínim
     * @param alpha Es tracta del valor que contindrà el màxim  heurístic actual en les diferents crides.
     * @param beta  Es el valor que contindrà el mínim en les diferents crides.
     * @return retorna el valor heurísitic
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
     * Descripció: Funció heurística per obtenir la puntuacio per al millor moviment
     *
     * @param tauler  Estat actual del tauler
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
        
        if (distanciesjo > distanciesop) {
            return puntuacio - distanciesjo;
        } else {
            return puntuacio - distanciesop;
        }

    }

}
