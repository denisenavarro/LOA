package edu.upc.epsevg.prop.loa;

import edu.upc.epsevg.prop.loa.players.GambitodeLOA;
import edu.upc.epsevg.prop.loa.Level;
import edu.upc.epsevg.prop.loa.IPlayer;
import edu.upc.epsevg.prop.loa.players.*;

import javax.swing.SwingUtilities;

/**
 * Lines Of Action: el joc de taula.
 *
 * @author bernat
 */
public class Game {

    /**
     * @param args
     */
    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                // IPlayer player2 = new HumanPlayer("Octopus");
                //IPlayer player2 = new RandomPlayer("Crazy Chris");
                // IPlayer player1 = new RandomPlayer("Crazy Ivan");
                IPlayer player2 = new MCCloudPlayer();
                IPlayer player1 = new GambitodeLOA(4, 0);

                new Board(player1, player2, 1, Level.DIFFICULT);
            }
        });
    }
}
