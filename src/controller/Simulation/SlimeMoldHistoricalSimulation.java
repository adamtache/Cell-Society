package src.controller.Simulation;

import src.Model.Cell;

import java.util.*;
import src.Model.*;
import src.Model.Grid;

public class SlimeMoldHistoricalSimulation extends Simulation{
	
	private List<Actor> actors;
	private Grid myGrid;
	private int emptyCell = 0;
	private int amoebaCell = 1;
	private double dropRate = 2.0;
	private double evaporationRate = -0.9;
	private double diffusionRate = 1.0;
	private double sniffThreshold = 1.0;
	private double sniffAngle = 45.0;
	private double wiggleAngle = 45.0;
	private double wiggleBias = 5;
	
	public void setSniffThresholdParameter(double givenSniffThreshold){
		sniffThreshold = givenSniffThreshold;
	}
	public void setSniffAngleParameter(double givenSniffAngle){
		sniffAngle = givenSniffAngle;
	}
	public void setWiggleAngleParameter(double givenWiggleAngle){
		wiggleAngle = givenWiggleAngle;
	}
	public void setWiggleBiasParameter(double givenWiggleBias){
		wiggleBias = givenWiggleBias;
	}
	public SlimeMoldHistoricalSimulation(Grid grid) {
		super(grid);
		myGrid = grid;
		actors = new ArrayList<Actor>();
		for(Cell cell : myGrid.getCells()){
			if(cell.getState() == amoebaCell){
				Actor amoeba = new Actor(cell.getCenterX(), cell.getCenterY());
				actors.add(amoeba);
				cell.setActor(amoeba);
			}
		}
	}
	
	public SlimeMoldHistoricalSimulation() {
	}
	
	public Cell updateCellState(Cell cell) {
		List<Cell> sniffNeighbors = new ArrayList<Cell>();
		List<Cell> wiggleNeighbors = new ArrayList<Cell>();
		List<Cell> emptyCells = findEmptyCells(cell);
		evaporateAndDiffuse(cell);
		if(cell.isState(amoebaCell)){
			for(Actor amoeba : cell.getActors()){
				double maxConcentration = Integer.MIN_VALUE;
				Cell maxConcentrationCell = null;
				lookForNeighbors(sniffNeighbors, wiggleNeighbors, emptyCells, amoeba);
			
					for(Cell neighborCell : sniffNeighbors){
						double neighborConcentration = neighborCell.getGround().getPheromones().get(0);
						if(neighborConcentration > maxConcentration){
							maxConcentration = neighborConcentration;
							maxConcentrationCell = neighborCell;
				}
			}
					if(maxConcentration > sniffThreshold){
						maxConcentrationCell.setState(amoebaCell);
						amoeba.move(maxConcentrationCell);
						cell.setState(emptyCell);
			}
					else{
						wiggleToCell(wiggleNeighbors, amoeba);
			}
					cell.getGround().increasePheromone(dropRate, 0);
		}
	}
		return null;
	}
	public void evaporateAndDiffuse(Cell cell) {
		PatchOfGround ground = cell.getGround();
		if(ground.numberOfPheromones() > 0){
			ground.increasePheromone(evaporationRate, 0);
			ground.diffusePheromones(cell.getAllNeighbors(), diffusionRate);
		}
	}
	public List<Cell> findEmptyCells(Cell cell) {
		List<Cell> emptyCells = new ArrayList<Cell>();
		for(Cell neighborCell: cell.getAllNeighbors()){
			if(neighborCell.isState(emptyCell)){
			emptyCells.add(neighborCell);
		}
		}
		return emptyCells;
	}
	
	public void wiggleToCell(List<Cell> wiggleNeighbors, Actor amoeba){
		Cell currentCell = amoeba.getCell();
		List<Cell> leftNeighbors = new ArrayList<Cell>();
		List<Cell> rightNeighbors = new ArrayList<Cell>();
		for(Cell cell : wiggleNeighbors){
			if(cell.getCenterX() < currentCell.getCenterX()){
				leftNeighbors.add(cell);
			}
			else if(cell.getCenterX() > currentCell.getCenterX()){
				rightNeighbors.add(cell);
			}
		}
		if(wiggleBias == 0){
			Cell straightAheadCell = myGrid.getCell(amoeba.getNextCellX(), amoeba.getNextCellY());
			amoeba.move(straightAheadCell);
		}
		Random rand = new Random();
		int randomInt = rand.nextInt(10);
		if(wiggleBias > 0){
			wiggle(randomInt, rightNeighbors, rand, amoeba);
		}
		else if(wiggleBias < 0){
			wiggle(randomInt * -1, leftNeighbors, rand, amoeba);
		}
	}
	
	public void wiggle(int randomInt, List<Cell> neighbors, Random rand, Actor amoeba){
		if(randomInt >= wiggleBias){
			int randomIndex = rand.nextInt(neighbors.size());
			amoeba.move(neighbors.get(randomIndex));
		}
	}
	
	public void lookForNeighbors(List<Cell> sniffNeighbors, List<Cell> wiggleNeighbors, List<Cell> emptyCells, Actor amoeba) {
		lookForNeighborsUsingAngle(sniffAngle, sniffNeighbors, emptyCells, amoeba);
		lookForNeighborsUsingAngle(wiggleAngle, wiggleNeighbors, emptyCells, amoeba);
	}
	
	public void lookForNeighborsUsingAngle(double angle, List<Cell> neighbors, List<Cell> emptyCells, Actor amoeba){
		double angleDecrement = angle / 4;
		double anglesToUse = angle;
		double yDifference = amoeba.getCellMovedFrom().getCenterY() - amoeba.getCell().getCenterY();
		boolean isMovingUp = (yDifference > 0);
		while(anglesToUse * -1 != angle){
			Cell nextCell = amoeba.findCellGivenAngle(anglesToUse, isMovingUp, myGrid);
			if(!emptyCells.contains(nextCell)){
				if(!neighbors.contains(nextCell)){
					neighbors.add(nextCell);
			}
			}
			anglesToUse -= angleDecrement;
		}
	}
	public void createOrRemovePerStep() {
		// TODO Auto-generated method stub
	}
	public String returnTitle() {
		return "Slime Mold";
	}
	public void setParameters(ArrayList<Double> params){
		double DEFAULT_VALUE = getDefaultVal();
		if(params.get(0) != DEFAULT_VALUE)
			dropRate = params.get(0);
		if(params.get(1) != DEFAULT_VALUE)
			evaporationRate = params.get(1);
		if(params.get(2) != DEFAULT_VALUE)
			diffusionRate = params.get(2);
		if(params.get(3) != DEFAULT_VALUE)
			sniffThreshold = params.get(3);
		if(params.get(4) != DEFAULT_VALUE)
			sniffAngle = params.get(4);
		if(params.get(5) != DEFAULT_VALUE)
			wiggleAngle = params.get(5);
		if(params.get(6) != DEFAULT_VALUE)
			wiggleBias = params.get(6);
	}
	public ArrayList<String> getParameters() {
		ArrayList<String> params = new ArrayList<String>();
		params.add("dropRate");
		params.add("evaporationRate");
		params.add("diffusionRate");
		params.add("sniffThreshold");
		params.add("sniffAngle");
		params.add("wiggleAngle");
		params.add("wiggleBias");
		return params;
	}
}
