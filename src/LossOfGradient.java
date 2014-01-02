import java.util.ArrayList;
import java.util.Random;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class LossOfGradient {
	
	public final static int SAMPLE_SIZE = 1;
	public final static int POPULATION_SIZE = 25;
	public final static int GENERATIONS = 600;
	
	private Random rand = new Random();
	
	/* Graph variables */
	XYSeriesCollection dataset = new XYSeriesCollection();
    XYSeries series1 = new XYSeries("Population 1");
    XYSeries series2 = new XYSeries("Population 2");
	
	public int[] objectiveFitness1 = new int[GENERATIONS];
	public int[] objectiveFitness2 = new int[GENERATIONS];
	public int[] pop1Fitness = new int[POPULATION_SIZE]; //Subjective Fitness
	public int[] pop2Fitness = new int[POPULATION_SIZE]; //Subjective Fitness
	public ArrayList<ScalarIndividual> pop1 = new ArrayList<ScalarIndividual>();
	public ArrayList<ScalarIndividual> pop2 = new ArrayList<ScalarIndividual>();

	public static void main(String args[]) {
		LossOfGradient lg = new LossOfGradient();
		
		lg.init();
		
		for (int counter = 0; counter < GENERATIONS; ++counter) {
			
			/* Calculate the subjective fitness for all individuals in each population */
			for (int counter2 = 0; counter2 < POPULATION_SIZE; ++counter2) {
				lg.pop1Fitness[counter2] = lg.subjectiveFitnessFunction(lg.pop1.get(counter2), lg.pop2);
				lg.pop2Fitness[counter2] = lg.subjectiveFitnessFunction(lg.pop2.get(counter2), lg.pop1);
			}
			
			/* Store the objective fitness of each individual */
			/*
			for (int counter2 = 0; counter2 < POPULATION_SIZE; ++counter2) {
				lg.objectiveFitness1[counter][counter2] = lg.pop1.get(counter2).getCount();
				lg.objectiveFitness2[counter][counter2] = lg.pop2.get(counter2).getCount();
			}*/
			
			/* Calculate the average objective fitness for each population */
			int pop1FitnessSum = 0;
			int pop2FitnessSum = 0;
			for (int counter2 = 0; counter2 < POPULATION_SIZE; ++counter2) {
				pop1FitnessSum += lg.pop1.get(counter2).getCount();
				pop2FitnessSum += lg.pop2.get(counter2).getCount();
			}
			
			lg.objectiveFitness1[counter] = pop1FitnessSum / POPULATION_SIZE;
			lg.objectiveFitness2[counter] = pop2FitnessSum / POPULATION_SIZE;
			
			/* Reproduce individuals in populations */
			lg.reproduce();
		}
		
		/* Draw graph */
		lg.buildGraph();
	}
	
	public void init() {
		for (int counter = 0; counter < POPULATION_SIZE; ++counter) {
			pop1.add(new ScalarIndividual());
			pop2.add(new ScalarIndividual());
		}
	}
	
	public ScalarIndividual selectIndividual(ArrayList<ScalarIndividual> population, int[] subjectiveFitness) {
		ArrayList<ScalarIndividual> selectionList = new ArrayList<ScalarIndividual>();
		
		for (int counter = 0; counter < POPULATION_SIZE; ++counter) {
			for (int counter2 = 0; counter2 < subjectiveFitness[counter] + 1; ++counter2) {
				selectionList.add(population.get(counter));
			}
		}

		/* Return one of the individuals at random */
		return selectionList.get(rand.nextInt(selectionList.size()));
	}
	
	public int subjectiveFitnessFunction(ScalarIndividual si, ArrayList<ScalarIndividual> population) {
	
		ArrayList<ScalarIndividual> competitors = new ArrayList<ScalarIndividual>();
		/* Select SAMPLE_SIZE of the population to compete against */
		for (int counter = 0; counter < SAMPLE_SIZE; ++counter) {
			competitors.add(population.get(rand.nextInt(population.size())));
		}
		
		int result = 0;
		/* Compete to get subjective fitness */
		for (ScalarIndividual competitor : competitors) {
			if (si.getCount() > competitor.getCount()) {
				++result;
			}
		}
		System.out.println("Result: " + result);
		return result;
	}
	
	public void reproduce() {
		for (int counter = 0; counter < POPULATION_SIZE; ++counter) {
			ScalarIndividual child1 = new ScalarIndividual(selectIndividual(pop1, pop1Fitness));
			child1.mutate();
			pop1.get(counter).setValue(child1.getValue());
			
			ScalarIndividual child2 = new ScalarIndividual(selectIndividual(pop2, pop2Fitness));
			child2.mutate();
			pop2.get(counter).setValue(child2.getValue());
		}

	}
	
	public void buildGraph() {
		for (int counter = 0; counter < GENERATIONS; ++counter) {
			series1.add(counter, objectiveFitness1[counter]);
			series2.add(counter, objectiveFitness2[counter]);	
		}
		dataset.addSeries(series1);
		dataset.addSeries(series2);
        JFreeChart chart = ChartFactory.createXYLineChart(
            "Loss of Graident, S = 15",
            "Generations",
            "Objective Fitness",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        //Create and display a frame...
        ChartFrame frame = new ChartFrame("First", chart);
        frame.pack();
        frame.setVisible(true);
	}
	
}

class ScalarIndividual {
	private int[] value = new int[100];
	//private Random rand = new Random();
	
	public ScalarIndividual() {
		for (int counter = 0; counter < value.length; ++counter) {
			//value[counter] = rand.nextInt(2);
			value[counter] = 0;
		}
	}
	
	public ScalarIndividual(ScalarIndividual si) {
		this.setValue(si.getValue());
	}
	
	public void setValue(int[] val) {
		System.arraycopy(val, 0, value, 0, val.length);
	}
	
	public int[] getValue() {
		return value;
	}
	
	public int getCount() {
		int result = 0;
		
		for (int counter = 0; counter < value.length; ++counter) {
			if (value[counter] == 1) {
				++result;
			}
		}
		
		return result;
	}
	
	public void mutate() {
		Random rand = new Random();
		for (int counter = 0; counter < value.length; ++counter) {
			if (rand.nextInt(1000) < 5) { //Mutate the bit
				if (value[counter] == 1) {
					value[counter] = 0;
				} else {
					value[counter] = 1;
				}
			}
		}
	}
}