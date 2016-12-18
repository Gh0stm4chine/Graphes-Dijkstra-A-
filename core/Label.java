package core;

public class Label implements Comparable<Label>{

	private boolean marquage;
	protected int cout;
	protected Node pere;
	protected Node currentNode;
	protected float temps;
	protected static int methodeCalcul;
	
	public Label(boolean marquage, int cout, Node pere, Node currentNode,float temps) {
		this.marquage = marquage;
		this.cout = cout;
		this.pere = pere;
		this.currentNode = currentNode;
		this.temps = temps;
	}

	public static int getMethodeCalcul() {
		return methodeCalcul;
	}

	public static void setMethodeCalcul(int methode) {
		methodeCalcul = methode;
	}

	public void setTemps(float temps) {
		this.temps = temps;
	}
	public float getTemps() {
		return this.temps;
	}
	
	public boolean isMarquage() {
		return marquage;
	}

	public void setMarquage(boolean marquage) {
		this.marquage = marquage;
	}

	public int getCout() {
		return cout;
	}

	public void setCout(int cout) {
		this.cout = cout;
	}

	public Node getPere() {
		return pere;
	}

	public void setPere(Node pere) {
		this.pere = pere;
	}

	public Node getCurrentNode() {
		return currentNode;
	}

	public void setCurrentNode(Node currentNode) {
		this.currentNode = currentNode;
	}

	public String toString() {
		if(this.pere != null)
			return "Numero Node : " + this.currentNode.toString()
					+ "  Numero Nodepère : " + this.pere.toString() + " cout : " + this.cout;
		else
			return "Numero Node : " + this.currentNode.toString() + " pere null ^^";
	}
	
	@Override
	public int compareTo(Label arg0) 
	{
		if(this instanceof LabelStar && arg0 instanceof LabelStar ) {
			if(methodeCalcul == 0) {
				//On compare la somme des deux couts
				if(((LabelStar)this).cout + ((LabelStar)this).getCoutEstime() < ((LabelStar)arg0).cout + ((LabelStar)arg0).getCoutEstime())
					return -1;
				else if(((LabelStar)this).cout + ((LabelStar)this).getCoutEstime() > ((LabelStar)arg0).cout + ((LabelStar)arg0).getCoutEstime())
					return 1;
				//Si c'est égal on compare les couts estimés
				else if(((LabelStar)this).getCoutEstime() > ((LabelStar)arg0).getCoutEstime())
					return 1;
				else
					return -1;
			} else {
				if(((LabelStar)this).temps + (((LabelStar)this).getCoutEstime())/(130000/60) < ((LabelStar)arg0).temps + (((LabelStar)arg0).getCoutEstime())/(130000/60))
					return -1;
				else if(((LabelStar)this).temps + (((LabelStar)this).getCoutEstime())/(130000/60) > ((LabelStar)arg0).temps + (((LabelStar)arg0).getCoutEstime())/(130000/60))
					return 1;
				else if(((LabelStar)this).getCoutEstime() > ((LabelStar)arg0).getCoutEstime())
					return 1;
				else
					return -1;
			}
			
		} else {
			if(methodeCalcul == 0) {
				if(this.cout < arg0.cout)
					return -1;
				else if(this.cout > arg0.cout)
					return 1;
				else
					return 0;
			} else {
				if(this.temps < arg0.temps)
					return -1;
				else if(this.temps > arg0.temps)
					return 1;
				else
					return 0;
			}
		}
	}

	
}
