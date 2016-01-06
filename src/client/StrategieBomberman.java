package client;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;


import logger.LoggerProjet;
import serveur.IArene;
import serveur.element.Caracteristique;
import serveur.element.Element;
import serveur.element.Potion;
import utilitaires.Calculs;
import utilitaires.Constantes;

public class StrategieBomberman extends StrategiePersonnage {

	public StrategieBomberman(String ipArene, int port, String ipConsole,
			String nom, String groupe,
			HashMap<Caracteristique, Integer> caracts, int nbTours,
			Point position, LoggerProjet logger, int ref) {
		super(ipArene, port, ipConsole, nom, groupe, caracts, nbTours, position, logger, ref);
		// TODO Auto-generated constructor stub
	}


		
	public void executeStrategie(HashMap<Integer, Point> voisins) throws RemoteException {
		// arene
		IArene arene = console.getArene();
		
		// reference RMI de l'element courant
		int refRMI = 0;
		
		// position de l'element courant
		Point position = null;
		
		try {
			refRMI = console.getRefRMI();
			position = arene.getPosition(refRMI);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		if (voisins.isEmpty()) { // je n'ai pas de voisins, j'erre
			console.setPhrase("J'erre...Seul");
			arene.deplace(refRMI, 0); 
			
		} else {
			int refCible = Calculs.chercheElementProche(position, voisins);
			int distPlusProche = Calculs.distanceChebyshev(position, arene.getPosition(refCible));

			Element elemPlusProche = arene.elementFromRef(refCible);
			Point pointC = arene.getPosition(refCible);
			
			if(distPlusProche <= Constantes.DISTANCE_MIN_INTERACTION) { 
				// courir ! 
				if(elemPlusProche instanceof Potion) {
					console.setPhrase("J'erre...");
					arene.deplace(refRMI, 0); 
				}
				else
				{
					console.setPhrase("Je m'éloigne de mon voisin " );
					 arene.fuir(refRMI, pointC);
				}
			} else 
			{ // si voisins, mais plus eloignes
				if(elemPlusProche instanceof Potion) {

					console.setPhrase("J'erre...");
					arene.deplace(refRMI, 0); 
					
				}
				else
				{
					pointC = arene.getPosition(refCible);
					arene.lancerPotion(refRMI, pointC);
				}
			}
		}
	}
	
}