package core ;
import java.util.*;
import java.awt.Color;
import java.io.* ;

import base.Dessin;
import base.Readarg ;


public class Pcc extends Algo {

	
	private static boolean covoit;
	private static boolean dessiner;
	private static boolean normale;
	private float endurance;
	private boolean pieton;
	private boolean inverse;
	private float tempsTrajet;
	// Numero des sommets origine et destination
	protected int zoneOrigine ;
	protected int origine ;
	protected int zoneDestination ;
	protected int destination ;
	protected BinaryHeap<Label> tas;
	private boolean finale;
	//private ArrayList<Integer> enregistre;
	protected Map<Integer,Label> hash;
	public Map<Integer, Label> getHash() {
		return hash;
	}

	public Pcc(Graphe gr, PrintStream sortie, Readarg readarg) {
		super(gr, sortie, readarg) ;
		if(readarg != null) {
			this.zoneOrigine = gr.getZone () ;
			this.origine = readarg.lireInt ("Numero du sommet d'origine ? ") ;
			// Demander la zone et le sommet destination.
			this.zoneOrigine = gr.getZone () ;
			this.destination = readarg.lireInt ("Numero du sommet destination ? ");
		}
		this.tas = new BinaryHeap<Label>();
		this.hash = new HashMap<Integer,Label>();
		this.pieton = false;
		this.endurance = 60;
		this.finale = false;
		this.inverse = false;
	}
	
	public float getTempsTrajet() {
		return this.tempsTrajet;
	}
	
	public static void setNormale(boolean sn) {
		normale = sn;
	}
	
	public static void setDessiner(boolean dess) {
		dessiner = dess;
	}
	
	public void setInverse(boolean inverse) {
		this.inverse = true;
	}
	
	public void setFinale(boolean finale) {
		this.finale = finale;
	}
	
	public void setPieton(boolean pieton) {
		this.pieton = pieton;
	}
	
	public void setEndurance(float endurance) {
		this.endurance = endurance;
	}
	
	public static void setCovoit(boolean covoiturage) {
		covoit = covoiturage;
	}
	



	public void ajouter(Boolean marquage, int cout, Node pere, Node current,float temps) {
		Label aux = new Label(marquage,cout,pere,current,temps);
		this.tas.insert(aux);
		this.hash.put(current.getNumNode(),aux);
	}
	
	public Label creerLabel(Boolean marquage, int cout, Node pere, Node current,float temps) {
		return new Label(marquage,cout,pere,current,temps);
	}

	public Label getLabel(int num) {
		return this.hash.get(num);
	}

	private boolean conditionArret(boolean pietonOuPas, float temps, Map<Integer,Label> hashPieton,boolean covoit) {
		if(finale)
			return false;
		if(pietonOuPas) {
			if(temps <= endurance)
				return false;
			else
				return true;
		} else if(covoit){
			for(Integer i : hashPieton.keySet()) {
				if(!this.hash.containsKey(i)) {
					return false;
				}
			}
		} else {
			return false;
		}
		return true;
	}
	
	
	public void run() {
		Dessin dessin = graphe.getDessin();
		if(!inverse) 
			System.out.println("Graphe normal");
		else
			System.out.println("Graphe Inverse");
		if(pieton) {
			System.out.println("Je suis un piéton");
			dessin.setColor(Color.DARK_GRAY);
			dessin.drawPoint(graphe.nodes.get(origine).getLongitude(), graphe.nodes.get(origine).getLatitude(), 5);
		}
		else if(!inverse){
			System.out.println("Djisktra Normal");
			dessin.setColor(Color.DARK_GRAY);
			dessin.drawPoint(graphe.nodes.get(origine).getLongitude(), graphe.nodes.get(origine).getLatitude(), 5);
		}
		
		if(origine == destination) {
			System.out.println("Chemin Trouvé :" );
		}
		int maxTas = 0;
		//Initialisation des couts du trajets
		float temps = 0;
		int cout = 0;
		int nbSommetsExplores = 0;
		int nbSommetsMarques = 0;
		Scanner sc = new Scanner(System.in);
		int rep = 0 ;
		if(!covoit && !finale && normale) {
			System.out.println("En distance(0) ou Temps(1) ? :) ");
			rep = sc.nextInt();
			if(rep == 0) {
				Label.setMethodeCalcul(0);
			} else {
				Label.setMethodeCalcul(1);
			}
		} else {
			Label.setMethodeCalcul(1);
		}
		//final Node
		Node finalNode = graphe.nodes.get(destination);
		Label labelFinal = null;
		
		//currentNode = Node d'origine au départ
		Node currentNode = graphe.nodes.get(origine);
		Label currentLabel;
		
		System.out.println("Run PCC de " + zoneOrigine + ":" + origine + " vers " + zoneDestination + ":" + destination) ;

		// on met dans le tas, le noeud de depart dont le cout est de 0
		ajouter(true,cout,null,currentNode,temps);
		maxTas ++;

		boolean trouve=false;
		int currentCout;
		float currentTemps = 0;
		System.out.println("Covoiturage ?? " + covoit);
		System.out.println("Finale ?? " + this.finale);
		while(!tas.isEmpty() && !conditionArret(pieton,currentTemps,Covoit.getHash1(),covoit)) {
			//tas.print();
			currentLabel = tas.deleteMin();
			currentCout = currentLabel.getCout();
			currentTemps = currentLabel.getTemps();
			//System.out.println("Current Temps " + currentTemps);
			currentNode = currentLabel.getCurrentNode();
			//System.out.println("Current Node : " + currentNode.getNumNode() + " Nombre sucesseurs : " + currentNode.getSuccesseurs().size());
			if(dessiner) {
				if(rep==0)
					dessin.setColor(java.awt.Color.green) ;
				else
					dessin.setColor(java.awt.Color.pink) ;
				dessin.drawPoint(currentNode.getLongitude(), currentNode.getLatitude(), 5);
			}
			for(Successeur succ : currentNode.getSuccesseurs()){					
				int numSucc = succ.getNumSucc();
				float succTemps=(float) ((float)((float)succ.getDistance()/(float)succ.getDescripteur().vitesseMax())*0.06);
				//System.out.println("Succ temps : " + succTemps);
				//System.out.println("----\t Succ : "+ numSucc); 
				if(dessiner) {
					if(rep==0)
						dessin.setColor(java.awt.Color.green) ;
					else
						dessin.setColor(java.awt.Color.pink) ;
					dessin.drawPoint(graphe.nodes.get(succ.getNumSucc()).getLongitude(), graphe.nodes.get(succ.getNumSucc()).getLatitude(), 5);
				}
				//si c'est un pieton et que la vitesse de la route dépasse 110km/h, on ignore ce chemin
				if(!(pieton && succ.getDescripteur().vitesseMax() > 110) && !(inverse && succ.getDescripteur().isSensUnique())) {
					//System.out.println("Chemin ok");
					if(numSucc == destination && !covoit){
						nbSommetsExplores++;
						nbSommetsMarques++;
						//On arrete la recherche
						labelFinal = new Label(true,currentCout + succ.getDistance(),currentNode,graphe.nodes.get(numSucc),currentTemps + succTemps);
						cout = labelFinal.getCout();
						temps = labelFinal.getTemps();
						trouve = true;
						System.out.println("Chemin trouvé");
						break;
					}
					else if(!this.hash.containsKey(numSucc)) {
						//Si le Label n'existe pas 
						nbSommetsExplores++;
						nbSommetsMarques++;
						if(!pieton) 
							ajouter(true,currentCout + succ.getDistance(),currentNode,graphe.nodes.get(numSucc),currentTemps + succTemps);
						else {
							ajouter(true,currentCout + succ.getDistance(),currentNode,graphe.nodes.get(numSucc),currentTemps + succ.getDistance()/(4000/60));
						}				
						maxTas ++;
					} else {
						Label aux;
						if(!pieton) {
							aux = creerLabel(true,currentCout + succ.getDistance(),currentNode,graphe.nodes.get(numSucc),currentTemps + succTemps);				
						} else {
							aux = creerLabel(true,currentCout + succ.getDistance(),currentNode,graphe.nodes.get(numSucc),currentTemps + succ.getDistance()/(4000/60));				
						}
							if(getLabel(numSucc).compareTo(aux) == 1){
							//if(distance && getLabel(numSucc).getCout() > currentCout + succ.getDistance() || !distance && getLabel(numSucc).getTemps() > currentTemps + succTemps) {
							//Le nouveau cout est plus petit
							this.hash.remove(numSucc);
							this.tas.getArray().remove(getLabel(numSucc));
							if(!pieton)
								ajouter(true,currentCout + succ.getDistance(),currentNode,graphe.nodes.get(numSucc),currentTemps + succTemps);
							else
								ajouter(true,currentCout + succ.getDistance(),currentNode,graphe.nodes.get(numSucc),currentTemps + succ.getDistance()/(4000/60));
							nbSommetsExplores++;
							nbSommetsMarques++;

						} else {
							nbSommetsMarques++;
						}
					}
				} else {
					//System.out.println("On ignore ce chemin");
				}
			}
			//On sort de la boucle while
			if(trouve)
				break;
		}
//		if(covoit && !tas.isEmpty()) {
////			System.out.println("Endurance atteinte : " + currentTemps);
////			if(Covoit.getHash1() != null) {
////				System.out.println(Covoit.getHash1());
////				for(Integer i : this.hash.keySet()) {
////					if(Covoit.getHash1().containsKey(i)) {
////						System.out.print("Contiens " + i + "  ");
////					}
////				}
////			}
////			if(Covoit.getHeap() != null) {
////				Label departCovoiturage = Covoit.getHeap().findMin();
////				Algo algo = new PccStar(graphe,null,null);
////				((Pcc)algo).setOrigine(departCovoiturage.getCurrentNode().getNumNode());
////				((Pcc)algo).setDestination(this.origine);
////			}
//		} else {
//			System.out.println("Tas Vide ");
//		}
//		
		if(!covoit && trouve) {
			
			ArrayList<Node> chemin = new ArrayList<Node>();
			System.out.println("Affichage du chemin");
			//System.out.println("Node Destination : " + destination);
			chemin.add(graphe.nodes.get(destination));
			Node current = labelFinal.getPere();
			chemin.add(current);
			//System.out.println(this.hash.toString());
			while(current != null) {
				labelFinal = this.hash.get(current.getNumNode());
				//System.out.println("current : " + current.getNumNode());
				//System.out.println("Label de current : " + labelFinal.toString());
				current = labelFinal.getPere();
				//System.out.println("pere : " + current.getNumNode());
				if(current != null)
					chemin.add(current);

			}
			System.out.println("Distance totale : " + cout);
			System.out.println("Temps total : " + temps);
			this.tempsTrajet = temps;
			System.out.println("Nombre de sommets explores : " + nbSommetsExplores);
			System.out.println("Nombre de sommets marques : " + nbSommetsMarques);
			System.out.println("Nombre de label maximum dans le tas : " + maxTas);
			
			//Dessin du chemin
			
			//Dessin dessin = graphe.getDessin();
			Collections.reverse(chemin);
			System.out.println(chemin);
			for(int i = 1 ; i < chemin.size();i++) {
				Node node = chemin.get(i-1);
				Node nextNode = chemin.get(i);
				if(finale)
					dessin.setColor(java.awt.Color.blue) ;
				else if(pieton)
					dessin.setColor(java.awt.Color.green) ;
				else 
					dessin.setColor(java.awt.Color.black) ;
				dessin.drawPoint(node.getLongitude(), node.getLatitude(), 5);
				dessin.drawLine(node.getLongitude(), node.getLatitude(), nextNode.getLongitude(), nextNode.getLatitude());
			}
			//On trace le dernier sommet du chemin
			dessin.drawPoint(chemin.get(chemin.size()-1).getLongitude(), chemin.get(chemin.size()-1).getLatitude(), 5);
			
			
			
		} else if(!covoit) {
			System.out.println("Chemin pas trouvé");
		}

	}

	public void setTas(BinaryHeap<Label> tas) {
		this.tas = tas;
	}

	public void setHash(Map<Integer, Label> hash) {
		this.hash = hash;
	}

	public int getOrigine() {
		return origine;
	}

	public int getDestination() {
		return destination;
	}

	public void setOrigine(int origine) {
		this.origine = origine;
	}

	public void setDestination(int destination) {
		this.destination = destination;
	}

}
