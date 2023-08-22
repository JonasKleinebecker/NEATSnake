package NEATGenes;

public class ConnectionGene {
    double weight;
    NodeGene inNode;
    NodeGene outNode;
    boolean enabled;

    boolean isRecurrent;
    int innovationNumber;

    public ConnectionGene(NodeGene inNode, NodeGene outNode, double weight, boolean enabled, int innovationNumber) {
        this.weight = weight;
        this.inNode = inNode;
        this.outNode = outNode;
        this.enabled = enabled;
        this.innovationNumber = innovationNumber;
    }

    public double getWeight(){
        return weight;
    }

    public NodeGene getInNode(){
        return inNode;
    }

    public NodeGene getOutNode(){
        return outNode;
    }

    public boolean isEnabled(){
        return enabled;
    }

    public int getInnovationNumber(){
        return innovationNumber;
    }

    public boolean isRecurrent(){
        return isRecurrent;
    }

    public void setWeight(double weight){
        this.weight = weight;
    }

    public void setEnabled(boolean enabled){
        this.enabled = enabled;
    }
}
