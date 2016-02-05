package src.controller.Simulation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import src.Model.Actor;
import src.Model.Cell;
import src.View.Grid;
import src.controller.CellIterator;

public class WaTorSimulation extends Simulation{
	public class Fish extends Actor{
		
		public Fish(int x, int y, double actorEnergy, double depletionRate){
			super(x, y, actorEnergy, depletionRate);
		}
	}
	public class Shark extends Actor{

		public Shark(int x, int y, double actorEnergy, double depletionRate) {
			super(x, y, actorEnergy, depletionRate);
		}
	}
	//Maps X Location -> Y Location -> Actor
	private HashMap<Integer, HashMap<Integer, Actor>> cellToActorMap;
	private double lastRunTime;
	private double chronon = 1000 / 60;
	private double fishReproductionTime = 100 * chronon;
	private double sharkReproductionTime = 100 * chronon;
	private double sharkDepletionRate = 0.1;
	private double eatFishRegenerationRate = 1.0;
	private double sharkEnergy = 100;
	private int emptyCell = 0;
	private int fishCell = 1;
	private int sharkCell = 2;
	
	public void setChrononParameter(double time){
		chronon = time;
	}
	public void setFishReproductionTimeParameter(double time){
		fishReproductionTime = time;
	}
	public void setSharkReproductionTimeParameter(double time){
		sharkReproductionTime = time;
	}
	public void setSharkDepletionRateParameter(double rate){
		sharkDepletionRate = rate;
	}
	public void setEatFishRegenerationRateParameter(double rate){
		eatFishRegenerationRate = rate;
	}
	public void setSharkEnergyParamter(double energy){
		sharkEnergy = energy;
	}
	public void setEmptyCellParameter(int empty){
		emptyCell = empty;
	}
	public void setFishCellParameter(int fish){
		fishCell = fish;
	}
	public void setSharkCellParameter(int shark){
		sharkCell = shark;
	}
	
	public WaTorSimulation(Grid grid) {
		super(grid);
		cellToActorMap = new HashMap<Integer, HashMap<Integer, Actor>>();
		lastRunTime = System.currentTimeMillis();
	}
	@Override
	public Grid step(){
		Grid myGrid = getGrid();
		CellIterator cellIt = myGrid.getCellIterator();
		while(cellIt.iterator().hasNext()){
			myGrid.setCell(updateCellState(cellIt.iterator().next()));
		}
		return myGrid;
	}
	@Override
	public void initialize(ArrayList<Integer> cellStates){
		int statesListIndex = 0;
		CellIterator cellIt = getGrid().getCellIterator();
		while(cellIt.iterator().hasNext()){
			int cellState = cellStates.get(statesListIndex++);
			Cell nextCell = cellIt.iterator().next();
			nextCell.setState(cellState);
		
			if(cellState == fishCell){
				addToMap(new Fish(nextCell.getX(), nextCell.getY(), 0, 0));
			}
			if(cellState == sharkCell){
				addToMap(new Shark(nextCell.getX(), nextCell.getY(), sharkEnergy, sharkDepletionRate));
			}
		}
	}
	@Override
	public Cell updateCellState(Cell cell) {
		if(System.currentTimeMillis() - lastRunTime >= chronon){
			lastRunTime = System.currentTimeMillis();
			Actor cellActor = cellToActorMap.get(cell.getX()).get(cell.getY());
		
		if(cellActor instanceof Shark){
			Shark shark = (Shark) cellActor;
			shark.depleteEnergy();
			if(shark.isDead()){
				removeFromMap(shark);
				cell.setState(emptyCell);
				return cell;
			}
			reproduceIfAllowed(shark, false);
			ArrayList<Cell> emptyCells = new ArrayList<Cell>();
			ArrayList<Fish> neighborFish = new ArrayList<Fish>();
			
			for(Cell neighborCell : getGrid().getNonDiagonalNeighbors(cell)){
				Actor neighborActor = cellToActorMap.get(neighborCell.getX()).get(neighborCell.getY());
				if(neighborActor != null){
					if(neighborActor instanceof Fish){
						neighborFish.add((Fish) neighborActor);
					}
				}
				else if(!(neighborActor instanceof Shark)){
					emptyCells.add(neighborCell);
				}
			}
			if(neighborFish.size() == 0 && emptyCells.size() > 0){
				moveAtRandom(shark, emptyCells);
			}
			else if(neighborFish.size() > 0){
				//Eat Fish at random
				Random rand = new Random();
				int randomFishIndex = rand.nextInt(neighborFish.size());
				Fish randomFish = neighborFish.get(randomFishIndex);
				removeFromMap(randomFish);
				removeFromMap(shark);
				shark.setEnergy(shark.getEnergy() + eatFishRegenerationRate);
				shark.move(randomFish.getX(), randomFish.getY());
				cell.setState(emptyCell);
				Cell newSharkCell = getGrid().getCell(shark.getX(), shark.getY());
				newSharkCell.setState(sharkCell);
				addToMap(shark);
			}
		}
		if(cellActor instanceof Fish){
			Fish fish = (Fish) cellActor;
			reproduceIfAllowed(fish, true);
			ArrayList<Cell> emptyCells = new ArrayList<Cell>();
			for(Cell neighborCell : getGrid().getNonDiagonalNeighbors(cell)){
				Actor neighborActor = cellToActorMap.get(neighborCell.getX()).get(neighborCell.getY());
				if(neighborActor == null){
					emptyCells.add(neighborCell);
				}
			}
			moveAtRandom(fish, emptyCells);
		}
		}
		return cell;
	}
	public void reproduceIfAllowed(Actor actor, boolean useFishReproductionTime){
		Actor newActor;
		double reproductionTime = sharkReproductionTime;
		if(useFishReproductionTime){
			reproductionTime = fishReproductionTime;
		}
		if(actor.getTimeSinceReproduced() >= reproductionTime){
			if(useFishReproductionTime){
				newActor = (Fish) actor.reproduce();
			}
			else{
				newActor = (Shark) actor.reproduce();
			}
			addToMap(newActor);
		}
		else{
			actor.updateTimeSinceReproduced(chronon);
		}
	}
	public void moveAtRandom(Actor actor, ArrayList<Cell> emptyCells){
		Random rand = new Random();
		int randomCell = rand.nextInt(emptyCells.size());
		Cell newLocation = emptyCells.get(randomCell);
		
		getGrid().getCell(actor.getX(), actor.getY()).setState(emptyCell);
		removeFromMap(actor);
		
		actor.move(newLocation.getX(), newLocation.getY());
		if(actor instanceof Shark){
			newLocation.setState(sharkCell);
		}
		if(actor instanceof Fish){
			newLocation.setState(fishCell);
		}
		addToMap(actor);
	}
	public void removeFromMap(Actor actor){
		cellToActorMap.get(actor.getX()).put(actor.getY(), null);
	}
	public void addToMap(Actor actor){
		cellToActorMap.get(actor.getX()).put(actor.getY(), actor);
	}
}