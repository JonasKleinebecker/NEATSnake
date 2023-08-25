package Genotypes;

import HelperClasses.NodeType;
import HelperClasses.Pair;
import NEATGenes.ConnectionGene;
import NEATGenes.NodeGene;
import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NEATGenotype extends Genotype {

    List<ConnectionGene> connectionGenes;
    List<ConnectionGene> enabledConnectionGenes;
    List<NodeGene> nodeGenes;
    Random randomGenerator;

    public NEATGenotype(List<NodeGene> nodeGenes, List<ConnectionGene> connectionGenes, Random randomGenerator){
        this.nodeGenes = nodeGenes;
        this.connectionGenes = connectionGenes;
        this.randomGenerator = randomGenerator;
        initializeEnabledConnectionList();
        updateNodeLocations();
    }

    public NEATGenotype(List<NodeGene> initialGenes, Random randomGenerator){
        this.nodeGenes = initialGenes;
        this.randomGenerator = randomGenerator;
        this.connectionGenes = new ArrayList<>();
        for(NodeGene inputGene : initialGenes){
            if(inputGene.getType() == NodeType.HIDDEN){
                throw new IllegalArgumentException("Initial Genes must not contain any hidden nodes");
            }
            else if(inputGene.getType() == NodeType.INPUT){
                for(NodeGene outputGene : initialGenes){
                    if(outputGene.getType() == NodeType.OUTPUT){
                        connectionGenes.add(new ConnectionGene(inputGene, outputGene, randomGenerator.nextDouble(1), true, InnovationCounter.getNextInnovationNumber(inputGene, outputGene))); //TODO: Should the weight be decided outside this class?
                    }
                }
            }
        }
        initializeEnabledConnectionList();
        updateNodeLocations();
    }

    private void initializeEnabledConnectionList(){
        enabledConnectionGenes = new ArrayList<>();
        for(ConnectionGene connectionGene : connectionGenes){
            if(connectionGene.isEnabled()){
                enabledConnectionGenes.add(connectionGene);
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
            }
            else if(connectionGene.getOutNode().getLayer() <= connectionGene.getInNode().getLayer()){
                connectionGene.getOutNode().setLayer(connectionGene.getInNode().getLayer() + 1);
            }
            maxLayer = Math.max(maxLayer, connectionGene.getOutNode().getLayer());
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

    public ConnectionGene getRandomEnabledConnectionGene(){
        if(enabledConnectionGenes.size() == 0){
            throw new IllegalArgumentException("There are no enabled connections");
        }
        return enabledConnectionGenes.get(randomGenerator.nextInt(enabledConnectionGenes.size()));
    }

    public NodeGene getRandomNodeGene(){
        return nodeGenes.get(randomGenerator.nextInt(nodeGenes.size()));
    }
    public void createConnection(NodeGene inNode, NodeGene outNode, double weight){
        ConnectionGene newConnection = new ConnectionGene(inNode, outNode, weight, true, InnovationCounter.getNextInnovationNumber(inNode, outNode)); 
        connectionGenes.add(newConnection);
        enabledConnectionGenes.add(newConnection);
    }
    public void splitConnection(ConnectionGene connectionGene){
        NodeGene inNode = connectionGene.getInNode();
        NodeGene outNode = connectionGene.getOutNode();
        connectionGene.setEnabled(false);
        enabledConnectionGenes.remove(connectionGene);
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
    public void sortConnectionGenesByInnovationNumber(){
        connectionGenes.sort((a,b) -> {
            return a.getInnovationNumber() - b.getInnovationNumber();
        });
    }

    public int getNumberOfConnectionGenes() {
        return connectionGenes.size();
    }

    public Integer getNumberOfNodeGenes() {
        return nodeGenes.size();
    }

    public List<ConnectionGene> getConnectionGenes() {
        return connectionGenes;
    }

    public List<NodeGene> getNodeGenes() {
        return nodeGenes;
    }

    public Random getRandomGenerator() {
        return randomGenerator;
    }
}
