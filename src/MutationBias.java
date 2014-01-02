import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class MutationBias {
	
	public final static int POPULATION_SIZE = 25;
	public final static int GENERATIONS = 600;
	
	Random rand = new Random();
	
	/* Graph variables */
	XYSeriesCollection dataset = new XYSeriesCollection();
    XYSeries series1 = new XYSeries("Population 1");
    XYSeries series2 = new XYSeries("Population 2");
	
	public ArrayList<Individual> pop1 = new ArrayList<Individual>();
	public ArrayList<Individual> pop2 = new ArrayList<Individual>();
	
	public double[] pop1Fitness = new double[GENERATIONS];
	public double[] pop2Fitness = new double[GENERATIONS];

	public static void main(String args[]) {		
		MutationBias mb = new MutationBias();
		
		mb.init();
		mb.iterate();
		
		mb.buildGraph();
	}
	
	public void init() {
		int[] pop1InitArray = new int[100];
		Arrays.fill(pop1InitArray, 0);
		
		int[] pop2InitArray = new int[100];
		Arrays.fill(pop1InitArray, 1);
		
		for (int counter = 0; counter < POPULATION_SIZE; ++counter) {
			Individual i = new Individual();
			i.setValue(pop1InitArray);
			pop1.add(i);
			
			i = new Individual();
			i.setValue(pop2InitArray);
			pop2.add(i);
		}
	}
	
	public void iterate() {
		
		for (int counter = 0; counter < GENERATIONS; ++counter) {
			/* Carry out the mutations */
			for (int counter2 = 0; counter2 < POPULATION_SIZE; ++counter2) {
				pop1.get(counter2).mutate();
				pop2.get(counter2).mutate();
			}
			
			/* Get the average fitness for the populations */
			int totalPop1Count = 0;
			int totalPop2Count = 0;
			for (int counter2 = 0; counter2 < POPULATION_SIZE; ++counter2) {
				totalPop1Count += pop1.get(counter2).getCount();
				totalPop2Count += pop2.get(counter2).getCount();
			}
			
			pop1Fitness[counter] = totalPop1Count / (POPULATION_SIZE);
			pop2Fitness[counter] = totalPop2Count / (POPULATION_SIZE);
			
		}
	}
	
	public void buildGraph() {
		for (int counter = 0; counter < GENERATIONS; ++counter) {
			series1.add(counter, pop1Fitness[counter]);
			series2.add(counter, pop2Fitness[counter]);
		}
		dataset.addSeries(series1);
		dataset.addSeries(series2);
        JFreeChart chart = ChartFactory.createXYLineChart(
            "Mutation Bias",
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

class Individual {

	private int[] value = new int[100];
	
	public void setValue(int[] val) {
		System.arraycopy(val, 0, value, 0, val.length);
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
