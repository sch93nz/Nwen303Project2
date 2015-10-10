import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class QuickSort {

	private static long start;
	private static String outPut;
	private static int[] data;
	private static long finish;

	public static void main(String[] args) {
		if(args.length>1)outPut=nameFormat(args[1]);
		else outPut = nameFormat(args[0]);
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
		System.out.println("start Quick");
		data=message;
		start = System.currentTimeMillis();
	
		Quick(0,data.length-1);

		WriteOut(data);
	}

	private static String nameFormat(String string) {
		int last = string.lastIndexOf('.');
		String end =string.substring(last);
		String begin= string.substring(0, last);

		return begin+"-Quick-"+end;
	}

	
	private static void WriteOut(int[] message)  {
		finish = System.currentTimeMillis();
		FileWriter writer;
		try {
			File file = new File(outPut);
			if(!file.exists())file.createNewFile();
			writer = new FileWriter(file);
		

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
		System.out.println("Finished");

		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void Quick( int lo, int hi) {
		if( lo >= hi)
			return;
		
		int pivot = data[hi];
		
		int partition = Partition(lo,hi,pivot);
		Quick(0,partition-1);
		
		Quick(partition+1,hi);
		
	}
	// i stuffed up this method something terrible so i copied code from.
	//http://examples.javacodegeeks.com/core-java/quicksort-algorithm-in-java-code-example/
	private static int Partition(int left,int right,int pivot){
		int leftCursor = left-1;
		int rightCursor = right;
		while(leftCursor < rightCursor){
                while(data[++leftCursor] < pivot);
                while(rightCursor > 0 && data[--rightCursor] > pivot);
			if(leftCursor >= rightCursor){
				break;
			}else{
				swap(leftCursor, rightCursor,data);
			}
		}
		swap(leftCursor, right,data);
		return leftCursor;
	}

	private static void swap(int loC, int hiC, int[] data) {
		int temp =data[loC];
		data[loC] = data[hiC];
		data[hiC]=temp;
	}
	
}
