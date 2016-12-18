package core ;

/**
 *   Classe representant un graphe.
 *   A vous de completer selon vos choix de conception.
 */

import java.io.* ;
import java.util.ArrayList;

import base.* ;

public class Graphe {

	// Nom de la carte utilisee pour construire ce graphe
	private final String nomCarte ;

	// Fenetre graphique
	private final Dessin dessin ;

	// Version du format MAP utilise'.
	private static final int version_map = 4 ;
	private static final int magic_number_map = 0xbacaff ;

	// Version du format PATH.
	private static final int version_path = 1 ;
	private static final int magic_number_path = 0xdecafe ;

	// Identifiant de la carte
	private int idcarte ;

	// Numero de zone de la carte
	private int numzone ;

	/*
	 * Ces attributs constituent une structure ad-hoc pour stocker les informations du graphe.
	 * Vous devez modifier et ameliorer ce choix de conception simpliste.
	 */
	private Descripteur[] descripteurs ;
	public ArrayList<Node> nodes;


	// Deux malheureux getters.
	public Dessin getDessin() { return dessin ; }
	public int getZone() { return numzone ; }

	public Graphe inverse() {
		ArrayList<Integer> nodeMarque = new ArrayList<Integer>();
		Graphe grapheInverse = this;
		for(Node n : grapheInverse.nodes) {
			//System.out.println("Node :" + n);
			int i = 0;
			while(i < n.getSuccesseurs().size()) {
				Successeur succ = n.getSuccesseurs().get(i) ;
				if(succ.getDescripteur().isSensUnique() && !nodeMarque.contains(succ.getNumSucc())) {
					n.deleteSucc(succ);
					grapheInverse.nodes.get(succ.getNumSucc()).addSucc(n.getNumNode(), succ.getDescripteur(), succ.getDistance(), succ.getNbSegments());
				}
				i++;
			}
			/*for(Successeur succ : n.getSuccesseurs()) {
				System.out.println("Succ :" + succ.getNumSucc());
				if(succ.getDescripteur().isSensUnique() && !nodeMarque.contains(succ.getNumSucc())) {
					n.deleteSucc(succ);
					grapheInverse.nodes.get(succ.getNumSucc()).addSucc(n.getNumNode(), succ.getDescripteur(), succ.getDistance(), succ.getNbSegments());
				}
			}*/
			nodeMarque.add(n.getNumNode());
		}
		return grapheInverse;
	}
	
	
	
	
	
	// Le constructeur cree le graphe en lisant les donnees depuis le DataInputStream
	public Graphe (String nomCarte, DataInputStream dis, Dessin dessin) {

		this.nodes = new ArrayList<Node>();	

		this.nomCarte = nomCarte ;
		this.dessin = dessin ;
		Utils.calibrer(nomCarte, dessin) ;

		// Lecture du fichier MAP. 
		// Voir le fichier "FORMAT" pour le detail du format binaire.
		try {

			// Nombre d'aretes
			int edges = 0 ;

			// Verification du magic number et de la version du format du fichier .map
			int magic = dis.readInt () ;
			int version = dis.readInt () ;
			Utils.checkVersion(magic, magic_number_map, version, version_map, nomCarte, ".map") ;

			// Lecture de l'identifiant de carte et du numero de zone, 
			this.idcarte = dis.readInt () ;
			this.numzone = dis.readInt () ;

			// Lecture du nombre de descripteurs, nombre de noeuds.
			int nb_descripteurs = dis.readInt () ;
			int nb_nodes = dis.readInt () ;

			// Nombre de successeurs enregistrés dans le fichier.
			int[] nsuccesseurs_a_lire = new int[nb_nodes] ;

			// En fonction de vos choix de conception, vous devrez certainement adapter la suite.
			//this.longitudes = new float[nb_nodes] ;
			//this.latitudes = new float[nb_nodes] ;
			this.descripteurs = new Descripteur[nb_descripteurs] ;

			float longi,lati;
			// Lecture des noeuds
			for (int num_node = 0 ; num_node < nb_nodes ; num_node++) {
				// Lecture du noeud numero num_node
				longi = ((float)dis.readInt ()) / 1E6f ;
				lati = ((float)dis.readInt ()) / 1E6f ;
				//Creation du node num_node
				nodes.add(new Node(num_node,longi,lati));
				nsuccesseurs_a_lire[num_node] = dis.readUnsignedByte() ;
			}

			Utils.checkByte(255, dis) ;

			// Lecture des descripteurs
			for (int num_descr = 0 ; num_descr < nb_descripteurs ; num_descr++) {
				// Lecture du descripteur numero num_descr
				descripteurs[num_descr] = new Descripteur(dis) ;

				// On affiche quelques descripteurs parmi tous.
				if (0 == num_descr % (1 + nb_descripteurs / 400))
					System.out.println("Descripteur " + num_descr + " = " + descripteurs[num_descr]) ;
			}

			Utils.checkByte(254, dis) ;

			// Lecture des successeurs
			for (int num_node = 0 ; num_node < nb_nodes ; num_node++) {
				// Lecture de tous les successeurs du noeud num_node
				for (int num_succ = 0 ; num_succ < nsuccesseurs_a_lire[num_node] ; num_succ++) {
					// zone du successeur
					int succ_zone = dis.readUnsignedByte() ;

					// numero de noeud du successeur
					int dest_node = Utils.read24bits(dis) ;

					// descripteur de l'arete
					int descr_num = Utils.read24bits(dis) ;

					// longueur de l'arete en metres
					int longueur  = dis.readUnsignedShort() ;

					// Nombre de segments constituant l'arete
					int nb_segm   = dis.readUnsignedShort() ;

					//Ajout des successeurs
					nodes.get(num_node).addSucc(dest_node, descripteurs[descr_num], longueur, nb_segm);
					//Si route a double sens, ajoute de l'autre sens !
					if(!descripteurs[descr_num].isSensUnique())
						nodes.get(dest_node).addSucc(num_node, descripteurs[descr_num], longueur, nb_segm);
					edges++ ;

					Couleur.set(dessin, descripteurs[descr_num].getType()) ;

					float current_long = nodes.get(num_node).getLongitude();
					float current_lat  = nodes.get(num_node).getLatitude() ;

					// Chaque segment est dessine'
					for (int i = 0 ; i < nb_segm ; i++) {
						float delta_lon = (dis.readShort()) / 2.0E5f ;
						float delta_lat = (dis.readShort()) / 2.0E5f ;
						dessin.drawLine(current_long, current_lat, (current_long + delta_lon), (current_lat + delta_lat)) ;
						current_long += delta_lon ;
						current_lat  += delta_lat ;
					}

					// Le dernier trait rejoint le sommet destination.
					// On le dessine si le noeud destination est dans la zone du graphe courant.
					if (succ_zone == numzone) {
						dessin.drawLine(current_long, current_lat, nodes.get(dest_node).getLongitude(), nodes.get(dest_node).getLatitude()) ;
					}
				}
			}

			Utils.checkByte(253, dis) ;

			System.out.println("Fichier lu : " + nb_nodes + " sommets, " + edges + " aretes, " 
					+ nb_descripteurs + " descripteurs.") ;

		} catch (IOException e) {
			e.printStackTrace() ;
			System.exit(1) ;
		}

	}

	// Rayon de la terre en metres
	private static final double rayon_terre = 6378137.0 ;

	/**
	 *  Calcule de la distance orthodromique - plus court chemin entre deux points a la surface d'une sphere
	 *  @param long1 longitude du premier point.
	 *  @param lat1 latitude du premier point.
	 *  @param long2 longitude du second point.
	 *  @param lat2 latitude du second point.
	 *  @return la distance entre les deux points en metres.
	 *  Methode écrite par Thomas Thiebaud, mai 2013
	 */
	public static double distance(double long1, double lat1, double long2, double lat2) {
		double sinLat = Math.sin(Math.toRadians(lat1))*Math.sin(Math.toRadians(lat2));
		double cosLat = Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2));
		double cosLong = Math.cos(Math.toRadians(long2-long1));
		return rayon_terre*Math.acos(sinLat+cosLat*cosLong);
	}

	/**
	 *  Attend un clic sur la carte et affiche le numero de sommet le plus proche du clic.
	 *  A n'utiliser que pour faire du debug ou des tests ponctuels.
	 *  Ne pas utiliser automatiquement a chaque invocation des algorithmes.
	 */
	public void situerClick() {

		System.out.println("Allez-y, cliquez donc.") ;

		if (dessin.waitClick()) {
			float lon = dessin.getClickLon() ;
			float lat = dessin.getClickLat() ;

			System.out.println("Clic aux coordonnees lon = " + lon + "  lat = " + lat) ;

			// On cherche le noeud le plus proche. O(n)
			float minDist = Float.MAX_VALUE ;
			int   noeud   = 0 ;

			for (int num_node = 0 ; num_node < nodes.size() ; num_node++) {
				float londiff = (nodes.get(num_node).getLongitude() - lon) ;
				float latdiff = (nodes.get(num_node).getLatitude() - lat) ;
				float dist = londiff*londiff + latdiff*latdiff ;
				if (dist < minDist) {
					noeud = num_node ;
					minDist = dist ;
				}
			}

			System.out.println("Noeud le plus proche : " + noeud) ;
			System.out.println() ;
			dessin.setColor(java.awt.Color.red) ;
			dessin.drawPoint(nodes.get(noeud).getLongitude(), nodes.get(noeud).getLatitude(), 5) ;
		}
	}

	/**
	 *  Charge un chemin depuis un fichier .path (voir le fichier FORMAT_PATH qui decrit le format)
	 *  Verifie que le chemin est empruntable et calcule le temps de trajet.
	 */
	public void verifierChemin(DataInputStream dis, String nom_chemin) {

		ArrayList<Node> noeudsChemin = new ArrayList<Node>();
		try {

			// Verification du magic number et de la version du format du fichier .path
			int magic = dis.readInt () ;
			int version = dis.readInt () ;
			Utils.checkVersion(magic, magic_number_path, version, version_path, nom_chemin, ".path") ;

			// Lecture de l'identifiant de carte
			int path_carte = dis.readInt () ;

			if (path_carte != this.idcarte) {
				System.out.println("Le chemin du fichier " + nom_chemin + " n'appartient pas a la carte actuellement chargee." ) ;
				System.exit(1) ;
			}

			int nb_noeuds = dis.readInt () ;

			// Origine du chemin
			int first_zone = dis.readUnsignedByte() ;
			int first_node = Utils.read24bits(dis) ;

			// Destination du chemin
			int last_zone  = dis.readUnsignedByte() ;
			int last_node = Utils.read24bits(dis) ;

			System.out.println("Chemin de " + first_zone + ":" + first_node + " vers " + last_zone + ":" + last_node) ;

			int current_zone = 0 ;
			int current_node = 0 ;

			// Tous les noeuds du chemin
			for (int i = 0 ; i < nb_noeuds ; i++) {
				current_zone = dis.readUnsignedByte() ;
				current_node = Utils.read24bits(dis) ;
				System.out.println(" --> " + current_zone + ":" + current_node) ;
				//Ajout des noeuds du chemin
				noeudsChemin.add(nodes.get(current_node));
			}
			//Dessin du chemin
			for(int i = 1 ; i < noeudsChemin.size();i++) {
				Node currentNode = noeudsChemin.get(i-1);
				Node nextNode = noeudsChemin.get(i);
				dessin.setColor(java.awt.Color.blue) ;
				dessin.drawPoint(currentNode.getLongitude(), currentNode.getLatitude(), 5);
				dessin.drawLine(currentNode.getLongitude(), currentNode.getLatitude(), nextNode.getLongitude(), nextNode.getLatitude());
			}
			//On trace le dernier sommet du chemin
			dessin.drawPoint(noeudsChemin.get(noeudsChemin.size()-1).getLongitude(), noeudsChemin.get(noeudsChemin.size()-1).getLatitude(), 5);
			if ((current_zone != last_zone) || (current_node != last_node)) {
				System.out.println("Le chemin " + nom_chemin + " ne termine pas sur le bon noeud.") ;
				System.exit(1) ;
			} else {
				
				//Calcul du temps de trajet
				float tempsTrajet = 0;
				float minTemps;
				for(int i = 1 ; i < noeudsChemin.size() ; i++) {
					minTemps = 999999;
					Node currentNode = noeudsChemin.get(i-1);
					Node nextNode = noeudsChemin.get(i);
					System.out.println("Node num : "+ currentNode.getNumNode());
					for(Successeur succ : currentNode.getSuccesseurs()) {
						//System.out.println("\t Successeur " + succ.getNumSucc());
						float temps = (float) ((float)((float)succ.getDistance()/(float)succ.getDescripteur().vitesseMax())*0.06);
						//System.out.println("ici temps = " + temps);
						if(succ.getNumSucc() == nextNode.getNumNode() && minTemps > temps) {
							minTemps = temps;
						}
					}
					if(minTemps == 999999)
						System.out.println("Erreur PATH pas de successeurs pour le node : " + currentNode.getNumNode()) ;
					else
						tempsTrajet += minTemps;
				}
				System.out.println(tempsTrajet + " minutes");
			}

		} catch (IOException e) {
			e.printStackTrace() ;
			System.exit(1) ;
		}

	}

	
	public void testAfficherSuccesseurs() {
		for(Node node : this.nodes) {
			System.out.println("Le node numero " + node.getNumNode() +  " a " + node.getSuccesseurs().size() + " successeurs :");
			for(Successeur succ : node.getSuccesseurs()) {
				System.out.println("\t " + succ.getDescripteur().toString());
			}
		}
	}

}
