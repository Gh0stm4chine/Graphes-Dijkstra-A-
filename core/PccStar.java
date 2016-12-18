package core ;

import java.io.* ;

import base.Readarg ;

public class PccStar extends Pcc {

	private double longitudeDestination;
	private double latitudeDestination;

	public PccStar(Graphe gr, PrintStream sortie, Readarg readarg) {
		super(gr, sortie, readarg) ;
		this.longitudeDestination = graphe.nodes.get(this.destination).getLongitude();
		this.latitudeDestination = graphe.nodes.get(this.destination).getLatitude();
	}

	public void ajouter(Boolean marquage, int cout, Node pere, Node current,float temps) {
		LabelStar aux = creerLabelStar(marquage,cout,pere,current,temps);
		this.tas.insert(aux);
		this.hash.put(current.getNumNode(),aux);
		//System.out.println("Ajout du LabelStar du node : " + current.getNumNode());
	}

	public LabelStar creerLabelStar(Boolean marquage, int cout, Node pere, Node current,float temps) {
		//System.out.println("Creation nouveau LabelStar");
		double nodeLongitude = current.getLongitude();
		double nodeLatitude = current.getLatitude();
		double coutEstime = Graphe.distance(this.longitudeDestination,this.latitudeDestination,nodeLongitude,nodeLatitude);
		return new LabelStar(marquage,cout,pere,current,temps,coutEstime);
	}
	
	public Label creerLabel(Boolean marquage, int cout, Node pere, Node current,float temps) {
		return creerLabelStar(marquage,cout,pere,current,temps);
	}

	public Label getLabel(int num) {
		return this.hash.get(num);
	}

	public void run() {
		System.out.println("Run PCC-Star de " + zoneOrigine + ":" + origine + " vers " + zoneDestination + ":" + destination) ;
		super.run();
		// A vous d'implementer la recherche de plus court chemin A*
	}

}
