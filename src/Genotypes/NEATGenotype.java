package Genotypes;

import HelperClasses.NodeType;
import HelperClasses.Pair;
import Genotypes.Genotype;
import NEATGenes.ConnectionGene;
import NEATGenes.NodeGene;
import org.ejml.simple.SimpleMatrix;

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
        nodeGenes.sort((a,b) -> a.getLayer() - b.getLayer());
    }

    public Pair<SimpleMatrix[], SimpleMatrix[]> toPhenotype(){
        return null;
        //TODO: Implement
    }
}
