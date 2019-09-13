
public class Instance {
	public double[] features;
	public int target;
	
	public Instance(double[] feature, int target) {
		this.features = feature;
		this.target = target;
	}
	
	@Override
	public String toString() {
		String[] buffer = new String[this.features.length+1];
		for(int i=0; i<this.features.length; i++) {
			buffer[i] = String.format("%.3f", this.features[i]);
		}
		buffer[this.features.length] = String.format("%d", this.target);
		return String.join(", ", buffer);
	}
}
