package serveur.interaction;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;

import serveur.element.Caracteristique;
import serveur.vuelement.VuePersonnage;
import utilitaires.Calculs;

/**
 * Represente le deplacement d'un personnage.
 *
 */
public class Deplacement {

	/**
	 * Vue du personnage qui veut se deplacer.
	 */
	private VuePersonnage personnage;
	
	/**
	 * References RMI et vues des voisins (calcule au prealable). 
	 */
	private HashMap<Integer, Point> voisins;
	
	/**
	 * Cree un deplacement.
	 * @param personnage personnage voulant se deplacer
	 * @param voisins voisins du personnage
	 */
	public Deplacement(VuePersonnage personnage, HashMap<Integer, Point> voisins) { 
		this.personnage = personnage;

		if (voisins == null) {
			this.voisins = new HashMap<Integer, Point>();
		} else {
			this.voisins = voisins;
		}
	}

	/**
	 * Deplace ce sujet d'une case en direction de l'element dont la reference
	 * est donnee.
	 * Si la reference est la reference de l'element courant, il ne bouge pas ;
	 * si la reference est egale a 0, il erre ;
	 * sinon il va vers le voisin correspondant (s'il existe dans les voisins).
	 * @param refObjectif reference de l'element cible
	 */    
	public void seDirigeVers(int refObjectif) throws RemoteException {
		Point pvers;

		// on ne bouge que si la reference n'est pas la notre
		if (refObjectif != personnage.getRefRMI()) {
			
			// la reference est nulle (en fait, nulle ou negative) : 
			// le personnage erre
			if (refObjectif <= 0) { 
				pvers = Calculs.positionAleatoireArene();
						
			} else { 
				// sinon :
				// la cible devient le point sur lequel se trouve l'element objectif
				pvers = voisins.get(refObjectif);
			}
	
			// on ne bouge que si l'element existe
			if(pvers != null) {
				seDirigeVers(pvers);
			}
		}
	}
	
	public void fuir(Point position, Point Cible) throws RemoteException {
	
		double X,Y;
		if( position.getX() < Cible.getX())
		{
			X = (Cible.getX() - position.getX() - position.getX());
		}
		else
		{
			X =  (position.getX()- Cible.getX()  + position.getX());
		}
		
		if( position.getY() < Cible.getY())
		{
			Y = (Cible.getY() - position.getY() - position.getY());
		}
		else
		{
			Y =  (position.getY()- Cible.getY()  + position.getY());
		}
		Cible.setLocation(X,Y);
		seDirigeVers(Cible);
	}

	/**
	 * Deplace ce sujet d'une case en direction de la case donnee.
	 * @param objectif case cible
	 * @throws RemoteException
	 */
	public void seDirigeVers(Point objectif) throws RemoteException {
		Point cible = Calculs.restreintPositionArene(objectif); 
		
		// on cherche le point voisin vide
		Point dest = Calculs.meilleurPoint(personnage.getPosition(), cible, voisins);
		
		if(dest != null) {
			personnage.setPosition(dest);
		}
	}
	
	
	
	/**
	 * Fais m�diter un moine en lui augmentant ses statistiques � chaque tour
	 * @throws RemoteException
	 */
	public void mediter () throws RemoteException {
		if (personnage.getElement().getCaract(Caracteristique.FORCE) < 40)	
			personnage.getElement().incrementeCaract(Caracteristique.FORCE, 1);
		if (personnage.getElement().getCaract(Caracteristique.ESQUIVE) < 30)
			personnage.getElement().incrementeCaract(Caracteristique.ESQUIVE, 1);
		if (personnage.getElement().getCaract(Caracteristique.DEFENSE) < 20)
			personnage.getElement().incrementeCaract(Caracteristique.DEFENSE, 1);
		if (personnage.getElement().getCaract(Caracteristique.VIE) < 100)
			personnage.getElement().incrementeCaract(Caracteristique.VIE, 1);
	}
}
