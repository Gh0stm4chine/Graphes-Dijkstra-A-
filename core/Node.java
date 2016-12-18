package core;

import java.util.ArrayList;

import base.Descripteur;

public class Node {
	
	private int numNode;
	private float longitude;
	private float latitude;
	private ArrayList<Successeur> successeurs;
	public Node(int numNode, float longitude, float latitude) {
		this.numNode = numNode;
		this.longitude = longitude;
		this.latitude = latitude;
		successeurs = new ArrayList<Successeur>();
	}
	
	public void addSucc(int numSucc,Descripteur descripteur,int distance,int nbSegments) {
		this.successeurs.add(new Successeur(numSucc,descripteur,distance,nbSegments));
	}
	
	public void deleteSucc(Successeur succ) {
		//System.out.println(this.successeurs.toString());
		//System.out.println("Index de :" + succ.getNumSucc()+ "=> " + this.successeurs.indexOf(succ));
		this.successeurs.remove(succ);
	}
	
	public int getNumNode() {
		return numNode;
	}
	
	public float getLongitude() {
		return longitude;
	}

	public float getLatitude() {
		return latitude;
	}

	public String toString() {
		if(this == null)
			return "null";
		else
			return this.numNode + "";
	}
	
	public ArrayList<Successeur> getSuccesseurs() {
		return successeurs;
	}

}
