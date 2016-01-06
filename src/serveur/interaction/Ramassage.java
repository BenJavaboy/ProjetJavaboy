package serveur.interaction;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.logging.Level;

import serveur.Arene;
import serveur.element.Caracteristique;
import serveur.vuelement.VuePersonnage;
import serveur.vuelement.VuePotion;
import utilitaires.Calculs;
import utilitaires.Constantes;

/**
 * Represente le ramassage d'une potion par un personnage.
 *
 */
public class Ramassage extends Interaction<VuePotion> {

	/**
	 * Cree une interaction de ramassage.
	 * @param arene arene
	 * @param ramasseur personnage ramassant la potion
	 * @param potion potion a ramasser
	 */
	public Ramassage(Arene arene, VuePersonnage ramasseur, VuePotion potion) {
		super(arene, ramasseur, potion);
	}

	@Override
	public void interagit() {
		try {
			logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " essaye de rammasser " + 
					Constantes.nomRaccourciClient(defenseur));
			
			// si le personnage est vivant
			if(attaquant.getElement().estVivant()) 
			{

				/* Si la potion est une potion de téléportation */
				if(defenseur.getElement().getNom().equals("Téléportation"))
				{
					/* On prend des valeurs aléatoires */
					int x = Calculs.nombreAleatoire(Constantes.XMIN_ARENE, Constantes.XMAX_ARENE);
					int y = Calculs.nombreAleatoire(Constantes.YMIN_ARENE, Constantes.YMAX_ARENE);

					/* Puis on téléporte l'attaquant sur ces nouvelles coordonnées */
					Point p1 = new Point (x,y);
					attaquant.setPosition(p1);
					
					/* Suppression de la potion */
					arene.ejectePotion(defenseur.getRefRMI());
				}
				
				/* Si la potion est une potion de freeze */
				else if(defenseur.getElement().getNom().equals("Freeze"))
				{
					/* On freeze l'attaquant */
					attaquant.getElement().freezer();
					
					/* Suppression de la potion */
					arene.ejectePotion(defenseur.getRefRMI());
				}
				
				else
				{
					// caracteristiques de la potion
					HashMap<Caracteristique, Integer> valeursPotion = defenseur.getElement().getCaracts();
					
					boolean valeurNegativeDansPotion = false;
					
					/* Tant que je n'ai pas regardé toutes les valeurs de la potions, ... */
					for(Caracteristique c : valeursPotion.keySet()) {
						
						/* Si la valeur est une valeur négative, ... */
						if(valeursPotion.get(c) < 0)
						{
							valeurNegativeDansPotion = true;
						}
					}
					
					for(Caracteristique c : valeursPotion.keySet()) {
						
						/* Si l'attaquant possède un bouclier qui le protège contre les dégats de potion
						   ET que la potion fait des dégats, ... */
						if(attaquant.getElement().getCaract(Caracteristique.BOUCLIER) == 1 && valeurNegativeDansPotion)
						{
							/* Si les effets sont positifs et que ce n'est pas la caractéristique bouclier, ... */
							if((valeursPotion.get(c) > 0) && !(c.equals(Caracteristique.BOUCLIER)))
							{
								arene.incrementeCaractElement(attaquant, c, valeursPotion.get(c));
							}
						}
						
						else
						{
							arene.incrementeCaractElement(attaquant, c, valeursPotion.get(c));
						}
					}
					
					/* Si on avait un bouclier ET qu'on devait prendre des dégats alors on l'enlève */
					if(attaquant.getElement().getCaract(Caracteristique.BOUCLIER) == 1 && valeurNegativeDansPotion)
					{
						arene.incrementeCaractElement(attaquant, Caracteristique.BOUCLIER, -1);
					}
					
					
					logs(Level.INFO, "Potion bue !");
					
					// test si mort
					if(!attaquant.getElement().estVivant()) {
						arene.setPhrase(attaquant.getRefRMI(), "Je me suis empoisonne, je meurs ");
						logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " vient de boire un poison... Mort >_<");
					}
	
					// suppression de la potion
					arene.ejectePotion(defenseur.getRefRMI());
				}
			} else {
				logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " ou " + 
						Constantes.nomRaccourciClient(defenseur) + " est deja mort... Rien ne se passe");
			}
		} catch (RemoteException e) {
			logs(Level.INFO, "\nErreur lors d'un ramassage : " + e.toString());
		}
	}
}