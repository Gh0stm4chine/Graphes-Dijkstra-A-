package base ;

/*
 * Ce programme propose de lancer divers algorithmes sur les graphes
 * a partir d'un menu texte, ou a partir de la ligne de commande (ou des deux).
 *
 * A chaque question posee par le programme (par exemple, le nom de la carte), 
 * la reponse est d'abord cherchee sur la ligne de commande.
 *
 * Pour executer en ligne de commande, ecrire les donnees dans l'ordre. Par exemple
 *   "java base.Launch insa 1 1 /tmp/sortie 0"
 * ce qui signifie : charge la carte "insa", calcule les composantes connexes avec une sortie graphique,
 * ecrit le resultat dans le fichier '/tmp/sortie', puis quitte le programme.
 */

import core.* ;

import java.io.* ;
import java.util.Scanner;

public class Launch {

	private final Readarg readarg ;

	public Launch(String[] args) {
		this.readarg = new Readarg(args) ;
	}

	public void afficherMenu () {
		System.out.println () ;
		System.out.println ("MENU") ;
		System.out.println () ;
		System.out.println ("0 - Quitter") ;
		System.out.println ("1 - Composantes Connexes") ;
		System.out.println ("2 - Plus court chemin standard") ;
		System.out.println ("3 - Plus court chemin A-star") ;
		System.out.println ("4 - Cliquer sur la carte pour obtenir un numero de sommet.") ;
		System.out.println ("5 - Charger un fichier de chemin (.path) et le verifier.") ;
		System.out.println ("6 - Application covoiturage");
		System.out.println () ;
	}

	public static void main(String[] args) {
		Launch launch = new Launch(args) ;
		launch.go () ;
	}

	public void go() {

		try {
			System.out.println ("**") ;
			System.out.println ("** Programme de test des algorithmes de graphe.");
			System.out.println ("**") ;
			System.out.println () ;

			// On obtient ici le nom de la carte a utiliser.
			String nomcarte = this.readarg.lireString ("Nom du fichier .map a utiliser ? ") ;
			DataInputStream mapdata = Openfile.open (nomcarte) ;

			boolean display = (1 == this.readarg.lireInt ("Voulez-vous une sortie graphique (0 = non, 1 = oui) ? ")) ;	    
			Dessin dessin = (display) ? new DessinVisible(800,600) : new DessinInvisible() ;

			Graphe graphe = new Graphe(nomcarte, mapdata, dessin) ;
			//graphe.testAfficherSuccesseurs();

			// Boucle principale : le menu est accessible 
			// jusqu'a ce que l'on quitte.
			boolean continuer = true ;
			int choix ;
			int dest = 0 ;
			int originePieton = 0;
			int origineVoiture = 0;
			while (continuer) {
				this.afficherMenu () ;
				choix = this.readarg.lireInt ("Votre choix ? ") ;

				// Algorithme a executer
				Algo algo = null ;
				Algo algo1 = null;
				Algo algo2 = null;

				// Le choix correspond au numero du menu.
				switch (choix) {
				case 0 : continuer = false ; break ;

				case 1 : algo = new Connexite(graphe, this.fichierSortie (), this.readarg) ; break ;

				case 2 : algo = new Pcc(graphe, this.fichierSortie (), this.readarg) ; break ;

				case 3 : algo = new PccStar(graphe, this.fichierSortie (), this.readarg) ; break ;

				case 4 : graphe.situerClick() ; break ;

				case 5 :
					String nom_chemin = this.readarg.lireString ("Nom du fichier .path contenant le chemin ? ") ;
					graphe.verifierChemin(Openfile.open (nom_chemin), nom_chemin) ;
					break ;
					
				case 6 :
					Pcc.setDessiner(false);
					Pcc.setCovoit(true);
					System.out.println("Départ du piéton et destination ? ");
					algo = new Pcc(graphe,null,this.readarg);
					((Pcc)algo).setPieton(true);
					dest = ((Pcc)algo).getDestination();
					originePieton = ((Pcc)algo).getOrigine();
					System.out.println("Depart de la voiture ? ");
					algo1 = new Pcc(graphe,null,this.readarg);
					System.out.println("Djisktra depuis la destination");
					origineVoiture = ((Pcc)algo1).getOrigine();
					//Graphe grapheInverse = graphe.inverse();
					algo2 = new Pcc(graphe,null,null);
					((Pcc)algo2).setDestination(originePieton);
					((Pcc)algo2).setOrigine(dest);
					break;

				default:
					System.out.println ("Choix de menu incorrect : " + choix) ;
					System.exit(1) ;
				}

				if (algo != null) {
					algo.run() ;
				} 
				if(algo1 != null && algo2 != null) {
					Scanner s = new Scanner(System.in);
					s.nextInt();
					Covoit c = new Covoit(graphe,dest,originePieton,origineVoiture);
					Covoit.setHash1(((Pcc)algo).getHash());
					algo1.run();
					c.setHash2(((Pcc)algo1).getHash());
					((Pcc)algo2).setInverse(true);
					s.nextInt();
					algo2.run();
					c.setHash3(((Pcc)algo2).getHash());
					s.nextInt();
					c.run();
					
				}
			}

			System.out.println ("Programme termine.") ;
			System.exit(0) ;


		} catch (Throwable t) {
			t.printStackTrace() ;
			System.exit(1) ;
		}
	}

	// Ouvre un fichier de sortie pour ecrire les reponses
	public PrintStream fichierSortie () {
		PrintStream result = System.out ;

		String nom = this.readarg.lireString ("Nom du fichier de sortie ? ") ;

		if ("".equals(nom)) { nom = "/dev/null" ; }

		try { result = new PrintStream(nom) ; }
		catch (Exception e) {
			System.err.println ("Erreur a l'ouverture du fichier " + nom) ;
			System.exit(1) ;
		}

		return result ;
	}

}
