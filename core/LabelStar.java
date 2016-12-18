package core;

public class LabelStar extends Label {
	
	private double coutEstime;

	public LabelStar(boolean marquage, int cout, Node pere, Node currentNode, float temps, double coutEstime) {
		super(marquage, cout, pere, currentNode, temps);
		this.coutEstime = coutEstime;
	}
	
//	public int compareTo(LabelStar arg0) 
//	{
//		System.out.println("Ok");
//		if(methodeCalcul == 0) {
//			//On compare la somme des deux couts
//			if(this.cout + this.coutEstime < arg0.cout + arg0.getCoutEstime())
//				return -1;
//			else if(this.cout + this.coutEstime > arg0.cout + arg0.getCoutEstime())
//				return 1;
//			//Si c'est égal on compare les couts estimés
//			else if(this.coutEstime > arg0.getCoutEstime())
//				return 1;
//			else
//				return -1;
//		} else {
//			if(this.temps + this.coutEstime < arg0.temps + arg0.getCoutEstime())
//				return -1;
//			else if(this.temps + this.coutEstime > arg0.temps + arg0.getCoutEstime())
//				return 1;
//			else if(this.coutEstime > arg0.getCoutEstime())
//				return 1;
//			else
//				return -1;
//		}
//
//	}

	public double getCoutEstime() {
		return coutEstime;
	}

	public void setCoutEstime(double coutEstime) {
		this.coutEstime = coutEstime;
	}
	
	public String toString() {
		return "Je suis un Label Star";
	}
}