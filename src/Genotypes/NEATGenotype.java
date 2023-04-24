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

    public void updateNodeLocations(){
        List<ConnectionGene> connGenesToUpdate = connectionGenes;
        List<NodeGene> nodeGenesToUpdate = nodeGenes;
        for(NodeGene nodeGene : nodeGenes){
            if(nodeGene.getType() == NodeType.INPUT){
                nodeGene.setLayer(0);
            }
        }
        connectionGenes.sort((a,b) -> { //sort Genes by FromNode Layer, sorting null values to the end
            if(a.getFromNode().getLayer() != null && b.getFromNode().getLayer() != null){
                return a.getFromNode().getLayer() - b.getFromNode().getLayer();
            }
            else if(a.getFromNode().getLayer() != null){
                return -1;
            }
            else if(b.getFromNode().getLayer() != null){
                return 1;
            }
            else{
                return 0;
            }
        });
        for(ConnectionGene connectionGene : connGenesToUpdate){
            if(connectionGene.getToNode().getLayer() <= connectionGene.getFromNode().getLayer()){
                connectionGene.getToNode().setLayer(connectionGene.getFromNode().getLayer() + 1);
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
            int fromLayer = connectionGene.getFromNode().getLayer();
            int toLayer = connectionGene.getToNode().getLayer();
            for(int i = fromLayer + 1; i < toLayer; i++){
                layerSizes[i]++;
                extraNodes[i]++;
            }
        }
        for(int i = 0; i < maxLayer; i++){
            weights[i] = new SimpleMatrix(layerSizes[i], layerSizes[i+1]);
            weights[i].zero();
            biases[i] = new SimpleMatrix(layerSizes[i+1], 1);
            biases[i].zero();
        }

        for(int i = 0; i < connectionGenes.size(); i++){
            ConnectionGene connectionGene = connectionGenes.get(i);
            int fromLayer = connectionGene.getFromNode().getLayer();
            int toLayer = connectionGene.getToNode().getLayer();
            int fromPosition = connectionGene.getFromNode().getPositionInLayer();
            int toPosition = connectionGene.getToNode().getPositionInLayer();
            if(fromLayer == toLayer - 1){
                weights[fromLayer].set(toPosition, fromPosition, connectionGene.getWeight());
            }
            else{
                for(int j = fromLayer; j < toLayer - 1; j++){
                    int tempToPosition = layerSizes[j] - extraNodes[j] + i;
                    weights[j].set(tempToPosition, fromPosition, 1);
                    fromPosition = tempToPosition;
                }
                weights[toLayer - 1].set(toPosition, fromPosition, 1);
            }
        }
        //TODO: Add biases
        //TODO: Add recurrent connections
        return new Pair<>(weights, biases);
    }
}
