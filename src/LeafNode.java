
public class LeafNode implements Node {
	
	private boolean classification;
	
	public LeafNode(boolean classification){
		this.classification = classification;
	}
	
	public boolean getClassification(){
		return this.classification;
	}
	
	public String toString(){
		return this.classification + "";
	}
}
