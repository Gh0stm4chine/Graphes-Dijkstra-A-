package core;

import base.Descripteur;

public class Successeur {

	private int numSucc;
	private Descripteur descripteur;
	private int distance;
	private int nbSegments;
	public Successeur(int numSucc, Descripteur descripteur, int distance, int nbSegments) {
		super();
		this.numSucc = numSucc;
		this.descripteur = descripteur;
		this.distance = distance;
		this.nbSegments = nbSegments;
	}
	public int getNumSucc() {
		return numSucc;
	}
	public Descripteur getDescripteur() {
		return descripteur;
	}
	public int getDistance() {
		return distance;
	}
	public int getNbSegments() {
		return nbSegments;
	}
	
	public String toString() {
		return this.numSucc +"";
	}
	
	
}
