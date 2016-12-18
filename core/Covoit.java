package core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import base.Readarg;

public class Covoit {

	private static Map<Integer,Label> hash1;
	private Map<Integer,Label> hash2;
	private Map<Integer,Label> hash3;
	private static BinaryHeap<Label> heap;
	private Graphe graphe;
	private int dest;
	private int originePieton;
	private int origineVoiture;
	private float tempsTrajet;
	
	public Covoit(Graphe gr, int destination, int originePieton,int origineVoiture) {
		hash1 = new HashMap<Integer,Label>();
		this.hash2 = new HashMap<Integer,Label>();
		this.hash3 = new HashMap<Integer,Label>();
		heap = new BinaryHeap<Label>();
		this.graphe = gr;
		this.dest = destination;
		this.originePieton = originePieton;
		this.origineVoiture = origineVoiture; 
	}

	public  static void setHash1(Map<Integer, Label> hash) {
		hash1 = hash;
	}
		
	public static BinaryHeap<Label> getHeap() {
		return heap;
	}

	public static Map<Integer,Label> getHash1() {
		return hash1;
	}

	public void setHash2(Map<Integer, Label> hash2) {
		this.hash2 = hash2;
	}

	public void setHash3(Map<Integer, Label> hash3) {
		this.hash3 = hash3;
	}

	//temp = Math.min(Math.max(entry.getValue().getTemps(), entry.getValue().getTemps()), entry.getValue().getTemps());	
	public void run () {
		Pcc.setNormale(false);
		System.out.println("Run Covoit");
		Label l;
		//Pour tous les noeuds contenus dans le chemin : pieton -> Dest
		for(Map.Entry<Integer, Label> entry : hash1.entrySet()) {
			System.out.println(entry);
			if(hash2.containsKey(entry.getKey()) && hash3.containsKey(entry.getKey())) {
				float coutP = entry.getValue().getTemps();
				float coutV = hash2.get(entry.getKey()).getTemps();
				float coutD = hash3.get(entry.getKey()).getTemps(); 
				l = entry.getValue();
				l.setTemps(Math.min(Math.max(coutP, coutV), coutD));
				heap.insert(l);
				System.out.println("PRINTF");
			} else {
				continue;
			}
			
		}
		Label departCovoiturage = heap.findMin();
		Algo algo = new PccStar(this.graphe,null,null);
		((Pcc)algo).setOrigine(departCovoiturage.getCurrentNode().getNumNode());
		((Pcc)algo).setDestination(this.dest);
		((Pcc)algo).setFinale(true);
		Pcc.setCovoit(false);
		algo.run();
		float tempsCovoiturage = ((Pcc)algo).getTempsTrajet();
		this.tempsTrajet = tempsCovoiturage;
		algo = new PccStar(this.graphe,null,null);
		((Pcc)algo).setOrigine(originePieton);
		((Pcc)algo).setDestination(departCovoiturage.getCurrentNode().getNumNode());
		((Pcc)algo).setFinale(false);
		((Pcc)algo).setPieton(true);
		algo.run();
		float tempsPieton = ((Pcc)algo).getTempsTrajet();
		algo = new PccStar(this.graphe,null,null);
		((Pcc)algo).setOrigine(origineVoiture);
		((Pcc)algo).setDestination(departCovoiturage.getCurrentNode().getNumNode());
		((Pcc)algo).setFinale(false);
		algo.run();
		float tempsVoiture = ((Pcc)algo).getTempsTrajet();
		this.tempsTrajet += Math.max(tempsVoiture, tempsPieton);
		System.out.println("Temps Total du Trajet = " + tempsTrajet + "dont " + tempsCovoiturage + " effectué ensemble.");
		System.out.println("Temps Pieton + " + tempsPieton + " et Temps Voiture " + tempsVoiture);
	}
	
}
