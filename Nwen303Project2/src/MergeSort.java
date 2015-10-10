import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 */

/**
 * @author Matthew Schmidt
 *
 */
public class MergeSort {

	static long start;

	private static String outPut;
	private static long finish;

	public static void main(String [] args){
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
		System.out.println("start MG");
		start = System.currentTimeMillis();
		WriteOut(Merge_Sort(message));
		
	}

	private static String nameFormat(String string) {
		int last = string.lastIndexOf('.');
		String end =string.substring(last);
		String begin= string.substring(0, last);

		return begin+"-MG-"+end;
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
	
	private static int[] Merge_Sort(int[] message) {
		if(message.length<=1) return message;

		int[] leftArray = new int[(message.length/2)];
		System.arraycopy(message, 0, leftArray, 0, leftArray.length);
		leftArray=Merge_Sort(leftArray);
		int[] rightArray = new int[(message.length-(message.length/2))];
		System.arraycopy(message, message.length/2, rightArray, 0, rightArray.length);
		rightArray=Merge_Sort(rightArray);

		return merge(leftArray,rightArray);

	}
	private static int[] merge(int [] locLeft,int[] locRight) {

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
		System.arraycopy(locLeft, iFirst, result, j, locLeft.length - iFirst);
		System.arraycopy(locRight, iSecond, result, j, locRight.length - iSecond);

		return result;
	}

}
