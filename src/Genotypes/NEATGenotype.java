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
}
