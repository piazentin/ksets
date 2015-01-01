package com.piazentin.ml.jkset;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

public class Utils {

	public static ArrayList<double[]> readTable(String filename) throws FileNotFoundException {
		File f = new File(filename);
		ArrayList<double[]> data = new ArrayList<double[]>();
		
		Scanner fr = new Scanner(f);
		while(fr.hasNextLine()) {
			String line = fr.nextLine();
			Scanner lr = new Scanner(line);
			lr.useLocale(Locale.US);
			
			ArrayList<Double> dl = new ArrayList<Double>();

			while (lr.hasNextDouble()) {
				dl.add(lr.nextDouble());
			}
			
			data.add(toArray(dl));
			lr.close();
		}
		fr.close();
		
		return data;
	}
	
	private static double[] toArray(ArrayList<Double> dl) {
		double[] dd = new double[dl.size()];
		
		for (int i = 0; i < dl.size(); ++i) {
			dd[i] = dl.get(i);
		}
		
		return dd;
	}
	
	public static double[][] toMatrix(ArrayList<double[]> data) {
		double[][] matrix = new double[data.size()][];
		
		for (int i = 0; i < data.size(); ++i) {
			matrix[i] = Arrays.copyOf(data.get(i), data.get(i).length);
		}
		
		return matrix;
	}
	
	public static ArrayList<double[]> toArrayList(double[][] data) {
		ArrayList<double[]> list = new ArrayList<double[]>();
		
		for (int i = 0; i < data.length; ++i) {
			double[] doubleArray = Arrays.copyOf(data[i], data[i].length);
			list.add(i, doubleArray);
		}
		
		return list;
	}
	
	public static void saveMatrix(double[][] matrix, String filename) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(filename, "UTF-8");
			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < matrix[i].length; j++) {
					writer.print(matrix[i][j] + "\t");
				}
				writer.println("");
			}
			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
