package Genotypes;

import HelperClasses.NodeType;
import HelperClasses.Pair;
import Genotypes.Genotype;
import NEATGenes.ConnectionGene;
import NEATGenes.NodeGene;
import org.ejml.simple.SimpleMatrix;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class NEATGenotype extends Genotype {

    List<ConnectionGene> connectionGenes;
    List<NodeGene> nodeGenes;

    public NEATGenotype(List<NodeGene> nodeGenes, List<ConnectionGene> connectionGenes){
        this.nodeGenes = nodeGenes;
        this.connectionGenes = connectionGenes;
    }

    public NEATGenotype(List<NodeGene> initialGenes){
        this.nodeGenes = initialGenes;
        this.connectionGenes = new ArrayList<>();
        for(NodeGene inputGene : initialGenes){
            if(inputGene.getType() == NodeType.HIDDEN){
                throw new IllegalArgumentException("Initial Genes must not contain any hidden nodes");
            }
            else if(inputGene.getType() == NodeType.INPUT){
                for(NodeGene outputGene : initialGenes){
                    if(outputGene.getType() == NodeType.OUTPUT){
                        connectionGenes.add(new ConnectionGene(Math.random(), inputGene, outputGene, true, InnovationCounter.getInnovationNumber(inputGene, outputGene))); //TODO: Should the weight be decided outside this class?
                    }
                }
            }
        }
    }
    public void updateNodeLocations(){
        int maxLayer = 0;

        for(NodeGene nodeGene : nodeGenes){
            if(nodeGene.getType() == NodeType.INPUT){
                nodeGene.setLayer(0);
            }
        }
        connectionGenes.sort((a,b) -> { //sort Genes by InNode Layer, sorting null values to the end
            if(a.getInNode().getLayer() != null && b.getInNode().getLayer() != null){
                return a.getInNode().getLayer() - b.getInNode().getLayer();
            }
            else if(a.getInNode().getLayer() != null){
                return -1;
            }
            else if(b.getInNode().getLayer() != null){
                return 1;
            }
            else{
                return 0;
            }
        });
        for(ConnectionGene connectionGene : connectionGenes){
            if(connectionGene.getOutNode().getLayer() == null){
                connectionGene.getOutNode().setLayer(connectionGene.getInNode().getLayer() + 1);
                maxLayer = Math.max(maxLayer, connectionGene.getOutNode().getLayer());
            }
            else if(connectionGene.getOutNode().getLayer() <= connectionGene.getInNode().getLayer()){
                connectionGene.getOutNode().setLayer(connectionGene.getInNode().getLayer() + 1);
                maxLayer = Math.max(maxLayer, connectionGene.getOutNode().getLayer());
            }
        }
        for(NodeGene nodeGene : nodeGenes){
            if(nodeGene.getType() == NodeType.OUTPUT){
                nodeGene.setLayer(maxLayer);
            }
        }
        nodeGenes.sort((a,b) -> { //sort Genes by Layer, then by ID
            if(a.getLayer() == b.getLayer()){
                return a.getId() - b.getId();
            }
            else {
                return a.getLayer() - b.getLayer();
            }
        });
    }

    public Pair<SimpleMatrix[], SimpleMatrix[]> toPhenotype(){
        int maxLayer = nodeGenes.get(nodeGenes.size() - 1).getLayer();
        int []layerSizes = new int[maxLayer + 1];
        int []extraNodes = new int[maxLayer + 1];
        SimpleMatrix[] weights = new SimpleMatrix[maxLayer];
        SimpleMatrix[] biases = new SimpleMatrix[maxLayer];

        for(int i = 0; i < nodeGenes.size(); i++){
            NodeGene nodeGene = nodeGenes.get(i);
            if(!nodeGene.isBias()){
                nodeGene.setPositionInLayer(layerSizes[nodeGene.getLayer()]);
                layerSizes[nodeGene.getLayer()]++;
            }
        }
        for(ConnectionGene connectionGene : connectionGenes){
            int inLayer = connectionGene.getInNode().getLayer();
            int outLayer = connectionGene.getOutNode().getLayer();
            for(int i = inLayer + 1; i < outLayer; i++){
                layerSizes[i]++;
                extraNodes[i]++;
            }
        }
        for(int i = 0; i < maxLayer; i++){
            weights[i] = new SimpleMatrix(layerSizes[i+1], layerSizes[i]);
            weights[i].zero();
            biases[i] = new SimpleMatrix(layerSizes[i+1], 1);
            biases[i].zero();
        }

        for(int i = 0; i < connectionGenes.size(); i++){
            ConnectionGene connectionGene = connectionGenes.get(i);
            int inLayer = connectionGene.getInNode().getLayer();
            int outLayer = connectionGene.getOutNode().getLayer();
            int inPosition = connectionGene.getInNode().getPositionInLayer();
            int outPosition = connectionGene.getOutNode().getPositionInLayer();
            if(inLayer == outLayer - 1){
                weights[inLayer].set(outPosition, inPosition, connectionGene.getWeight());
            }
            else{
                double weight = connectionGene.getWeight();
                for(int j = inLayer; j < outLayer - 1; j++){
                    int tempOutPosition = layerSizes[j+1] - extraNodes[j+1];
                    extraNodes[j+1]--;
                    weights[j].set(tempOutPosition, inPosition, weight);
                    weight = 1; //only the first connection has the actual weight
                    inPosition = tempOutPosition;
                }
                weights[outLayer - 1].set(outPosition, inPosition, 1);
            }
        }
        //TODO: Add biases
        //TODO: Add recurrent connections
        return new Pair<>(weights, biases);
    }

    public ConnectionGene getRandomConnectionGene(){
        return connectionGenes.get((int)(Math.random() * connectionGenes.size()));
    }

    public NodeGene getRandomNodeGene(){
        return nodeGenes.get((int)(Math.random() * nodeGenes.size()));
    }
    public void createConnection(NodeGene inNode, NodeGene outNode, double weight){
        connectionGenes.add(new ConnectionGene(weight, inNode, outNode, true, InnovationCounter.getInnovationNumber(inNode, outNode)));
    }
    public void splitConnection(ConnectionGene connectionGene){
        NodeGene inNode = connectionGene.getInNode();
        NodeGene outNode = connectionGene.getOutNode();
        connectionGene.setEnabled(false);
        NodeGene newNode = new NodeGene(-1, NodeType.HIDDEN); //TODO: what ID to set?
        nodeGenes.add(newNode);
        createConnection(inNode, newNode, 1);
        createConnection(newNode, outNode, connectionGene.getWeight());
    }

    public boolean connectionExists(NodeGene inNode, NodeGene outNode) {
        for(ConnectionGene connectionGene : connectionGenes){
            if(connectionGene.getInNode() == inNode && connectionGene.getOutNode() == outNode){
                return true;
            }
        }
        return false;
    }

    public ConnectionGene getFirstEnabledConnectionFromOffset(int offset) {
        if(offset < 0 || offset >= connectionGenes.size()) {
            throw new IllegalArgumentException("tried to get connection from offset " + offset + " but there are only " + connectionGenes.size() + " connections");
        }
        for(int i = offset; i < connectionGenes.size(); i++){
            if(connectionGenes.get(i).isEnabled()){
                return connectionGenes.get(i);
            }
        }
        for(int i = 0; i < offset; i++){
            if(connectionGenes.get(i).isEnabled()){
                return connectionGenes.get(i);
            }
        }
        return null;
    }

    public ConnectionGene getFirstDisabledConnectionFromOffset(int offset) {
        if(offset < 0 || offset >= connectionGenes.size()) {
            throw new IllegalArgumentException("tried to get connection from offset " + offset + " but there are only " + connectionGenes.size() + " connections");
        }
        for(int i = offset; i < connectionGenes.size(); i++){
            if(!connectionGenes.get(i).isEnabled()){
                return connectionGenes.get(i);
            }
        }
        for(int i = 0; i < offset; i++){
            if(!connectionGenes.get(i).isEnabled()){
                return connectionGenes.get(i);
            }
        }
        return null;
    }

    public int getNumberOfConnectionGenes() {
        return connectionGenes.size();
    }
}
