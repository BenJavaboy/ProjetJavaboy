package serveur.interaction;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.Random;
import java.util.logging.Level;

import serveur.Arene;
import serveur.element.Caracteristique;
import serveur.element.Personnage;
import serveur.vuelement.VuePersonnage;
import utilitaires.Calculs;
import utilitaires.Constantes;

/**
 * Represente un duel entre deux personnages.
 *
 */
public class Duel extends Interaction<VuePersonnage> {
	
	/**
	 * Cree une interaction de duel.
	 * @param arene arene
	 * @param attaquant attaquant
	 * @param defenseur defenseur
	 */
	public Duel(Arene arene, VuePersonnage attaquant, VuePersonnage defenseur) {
		super(arene, attaquant, defenseur);
	}
	
	@Override
	public void interagit() {
		try {
			Personnage pAttaquant = attaquant.getElement();
			int forceAttaquant = 0;
			int baisseArmure = 0;
			int ralentissement = 0;
			int spell = 0;
			if ((pAttaquant.getNom().equals("Mage")))
			{
				spell = new Random().nextInt(3) + 1;
				switch (spell)
				{
					case 1 : forceAttaquant = pAttaquant.getCaract(Caracteristique.FORCE) * 2;
							 break;
					
					case 2 : forceAttaquant = pAttaquant.getCaract(Caracteristique.FORCE);
							 baisseArmure = 20;
							 break;
						
					case 3 : forceAttaquant = pAttaquant.getCaract(Caracteristique.FORCE);
							 ralentissement = 30;
							 break;
				}
			}
			else
			{
				forceAttaquant = pAttaquant.getCaract(Caracteristique.FORCE);
			}
			int r = new Random().nextInt(100) + 1;
			//m�canique d'esquive
			if (r > defenseur.getElement().getCaract(Caracteristique.ESQUIVE))
			{
				//m�canique de la d�fense
				int perteVie = forceAttaquant - defenseur.getElement().getCaract(Caracteristique.DEFENSE);
				if (perteVie <= 0)
					perteVie = 1;
				
				
				/* Si l'attaquant est un archer alors l'adversaire ne recule pas, ... */
				if (!(pAttaquant.getNom().equals("Archer")) && !(pAttaquant.getNom().equals("Mage")))
				{
					Point positionEjection = positionEjection(defenseur.getPosition(), attaquant.getPosition(), forceAttaquant);
		
					// ejection du defenseur
					defenseur.setPosition(positionEjection);
				}
	
				arene.incrementeCaractElement(defenseur, Caracteristique.VIE, - perteVie);
				if (pAttaquant.getNom().equals("Mage"))
				{
					if (spell == 1)
					{
						logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " lance une boule de feu ("
								+ perteVie + " points de degats) a " + Constantes.nomRaccourciClient(defenseur));
					}
					else if (spell == 2)
					{
						arene.incrementeCaractElement(defenseur, Caracteristique.DEFENSE, - baisseArmure);
						logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " lance un �clair ("
								+ perteVie + " points de degats) a " + Constantes.nomRaccourciClient(defenseur) + " et lui baisse son armure de 20");
					}
					else
					{
						arene.incrementeCaractElement(defenseur, Caracteristique.INITIATIVE, - ralentissement);
						logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " lance un �clair de givre ("
								+ perteVie + " points de degats) a " + Constantes.nomRaccourciClient(defenseur) + " et le ralentit de 30");
					}
					
				}
				else
				{
					logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " colle une beigne ("
						+ perteVie + " points de degats) a " + Constantes.nomRaccourciClient(defenseur));
				}
				
				if (pAttaquant.getNom().equals("Sanguinaire"))
				{
					pAttaquant.incrementeCaract(Caracteristique.VIE, perteVie / 2);
					logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " regagne " + perteVie/2);
				}
					
			}
			//s'il y a eu une esquive le texte est chang�
			else
			{
				logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " loupe de justesse " 
						+ Constantes.nomRaccourciClient(defenseur));
			}
			// initiative
			incrementeInitiative(defenseur);
			decrementeInitiative(attaquant);
				
		} 
		catch (RemoteException e) {
			logs(Level.INFO, "\nErreur lors d'une attaque : " + e.toString());
		}
	}

	/**
	 * Incremente l'initiative du defenseur en cas de succes de l'attaque. 
	 * @param defenseur defenseur
	 * @throws RemoteException
	 */
	private void incrementeInitiative(VuePersonnage defenseur) throws RemoteException {
		arene.incrementeCaractElement(defenseur, Caracteristique.INITIATIVE, 
				Constantes.INCR_DECR_INITIATIVE_DUEL);
	}
	
	/**
	 * Decremente l'initiative de l'attaquant en cas de succes de l'attaque. 
	 * @param attaquant attaquant
	 * @throws RemoteException
	 */
	private void decrementeInitiative(VuePersonnage attaquant) throws RemoteException {
		arene.incrementeCaractElement(attaquant, Caracteristique.INITIATIVE, 
				-Constantes.INCR_DECR_INITIATIVE_DUEL);
	}

	
	/**
	 * Retourne la position ou le defenseur se retrouvera apres ejection.
	 * @param posDefenseur position d'origine du defenseur
	 * @param positionAtt position de l'attaquant
	 * @param forceAtt force de l'attaquant
	 * @return position d'ejection du personnage
	 */
	private Point positionEjection(Point posDefenseur, Point positionAtt, int forceAtt) {
		int distance = forceVersDistance(forceAtt);
		
		// abscisses 
		int dirX = posDefenseur.x - positionAtt.x;
		
		if (dirX > 0) {
			dirX = distance;
		}
		
		if (dirX < 0) {
			dirX = -distance;
		}
		
		// ordonnees
		int dirY = posDefenseur.y - positionAtt.y;
		
		if (dirY > 0) {
			dirY = distance;
		}
		
		if (dirY < 0) {
			dirY = -distance;
		}
		
		int x = posDefenseur.x + dirX;
		int y = posDefenseur.y + dirY;
		
		return Calculs.restreintPositionArene(new Point(x, y));
	}
	
	/**
	 * Calcule la distance a laquelle le defenseur est projete suite a un coup.
	 * @param forceAtt force de l'attaquant
	 * @return distance de projection
	 */
	private int forceVersDistance(int forceAtt) {
		int max = Caracteristique.FORCE.getMax();
		
		int quart = (int) (4 * ((float) (forceAtt - 1) / max)); // -1 pour le cas force = 100
		
		return Constantes.DISTANCE_PROJECTION[quart];
	}
}

