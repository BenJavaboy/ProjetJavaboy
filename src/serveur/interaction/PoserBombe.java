package serveur.interaction;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;

import serveur.Arene;
import serveur.element.Caracteristique;
import serveur.element.Potion;


public class PoserBombe implements Runnable {

	private Arene arene;
	private Point cible;
	
	public  PoserBombe( Arene arene, Point pointC)
	{
		this.arene = arene;
		this.cible = pointC;
		
		new Thread(this).start();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
HashMap<Caracteristique, Integer> caractsPotion = new HashMap<Caracteristique, Integer>();
		
		caractsPotion.put(Caracteristique.VIE, -15);
		caractsPotion.put(Caracteristique.FORCE, 0);
		caractsPotion.put(Caracteristique.INITIATIVE, 0);
		
		try {
			arene.ajoutePotion(new Potion("MegaBonus", "G7", caractsPotion), cible);
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		//Thread.interrupted();
	}
	
	
}