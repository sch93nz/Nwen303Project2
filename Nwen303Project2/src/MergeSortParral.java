import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import mpi.MPI;

public class MergeSortParral{

	private static long start;
	private static long finish;
	
	private static String outPut;

	private static Worker main;
	
	static MergeSortParral host;
	
	public static void main(String[] args) {
		ArrayList<Integer> Data = new ArrayList<Integer>();
		Scanner scan;
		try {
			scan = new Scanner(new File(args[0]));
		
		while(scan.hasNext()){
			Data.add(scan.nextInt());
		}
		scan.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int[] message = new int[Data.size()];

		for (int i=0;i<message.length;i++){
			message[i]= (int) Data.get(i);
		}
		start = System.currentTimeMillis();
		host = new MergeSortParral();
		main = host.new Worker(null,true,message);
		main.start();
		
	}
	
	
	
	private static void WriteOut(int[] message)  {
		FileWriter writer;
		try {
			writer = new FileWriter(outPut);
		

		writer.write("Starting time = "+start+"\r\n");
		System.out.print("Starting time = "+start+"\r\n");

		writer.write("Finished time = "+finish+"\r\n");
		System.out.print("Finished time = "+finish+"\r\n");

		long time = finish-start;
		writer.write("Time Taken = "+time+"\r\n");
		System.out.print("Time Taken = "+time+"\r\n");

		for(int i=0 ;i<message.length;i++){
			writer.write(message[i]+"\r\n");
		}
		writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private class Worker extends Thread{
		
		Worker parent,leftWorker=null,rightWorker=null;
		boolean left,running = true;
		int[] data, leftData=null,rightData=null;
		
		
		public Worker (Worker Parent, boolean Left,int[] Data){
			parent = Parent;
			left= Left;
			data = Data;
		}
		
		
		public void run(){
			while(running){
		if (data.length<=1){
				sendBack(data);
				running = false;
				}
			if( leftWorker == null){
				int[] leftArray = new int[data.length/2];
				
				leftWorker = new Worker(this,true,leftArray);
				leftWorker.start();
			}
			if(rightWorker == null){
				int[] rightArray = new int[data.length/2];
				rightWorker = new Worker(this,false,rightArray);
				rightWorker.start();
			}
			if(leftData!=null && rightData != null){
				sendBack(merge());
			}
		
		
			}
		}


		private int[] merge() {
			int [] locLeft = leftData;
			int [] locRight = rightData;
			int [] result = new int [locLeft.length+locRight.length];
	      
			//I never get this bit right so i copyied it  from
			// http://javahungry.blogspot.com/2013/06/java-sorting-program-code-merge-sort.html
			int iFirst = 0;
	        // Next element to consider in the second array
	        int iSecond = 0;
	        
	        // Next open position in the result
	        int j = 0;
	        // As long as neither iFirst nor iSecond is past the end, move the
	        // smaller element into the result.
	        while (iFirst < locLeft.length && iSecond < locRight.length) {
	            if (locLeft[iFirst] < locRight[iSecond]) {
	                result[j] = locLeft[iFirst];
	                iFirst++;
	                } else {
	                result[j] = locRight[iSecond];
	                iSecond++;
	            }
	            j++;
	        }
	        // copy what's left
	        System.arraycopy(locLeft, iFirst, result, j, leftData.length - iFirst);
	        System.arraycopy(locRight, iSecond, result, j, locRight.length - iSecond);
			
			return null;
		}


		private void sendBack(int[] data2) {
			if(parent!= null){
			if(left){
				parent.sendBackLeft(data2);
				
				
			}else{
				parent.sendBackRight(data2);
				
			}
			}else {
				running = false;
				WriteOut(data2);
			}
		}


		private void sendBackRight(int[] data2) {
			rightData= data2;
			
		}


		private void sendBackLeft(int[] data2) {
			leftData = data2;
			
		}
	}



	
}