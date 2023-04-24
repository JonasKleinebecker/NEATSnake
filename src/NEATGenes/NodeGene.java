package NEATGenes;

import HelperClasses.NodeType;

public class NodeGene {
    int id;
    NodeType type;

    boolean isRCC; //Recurrent Connection Checkpoint

    Integer layer; //Integer so that we can check if it is null

    int positionInLayer;

    boolean isBias;

    public NodeGene(int id) {
        this.id = id;
        this.type = NodeType.HIDDEN;
    }

    public NodeGene(int id, NodeType type) {
        this.id = id;
        this.type = type;
    }

    public NodeType getType(){
        return type;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public Integer getLayer(){
        return layer;
    }

    public boolean isBias() {
        return isBias;
    }

    public int getId() {
        return id;
    }

    public void setPositionInLayer(int positionInLayer) {
        this.positionInLayer = positionInLayer;
    }

    public int getPositionInLayer() {
        return positionInLayer;
    }
}
