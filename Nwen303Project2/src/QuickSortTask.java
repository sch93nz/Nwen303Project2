import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
/**
 *  Stolen I have never understood quick sort its total magic to me.
 * @author http://www.mahesht.com/?p=261
 *
 */
public class QuickSortTask extends SortTask {
	private static String outPut;
	private static long start;
	private static long finish;
	private final int SPLIT_THRESHOLD = 100000;
	private int sortArray [];
	private int iStart = 0;
	private int iEnd  =0;
	ExecutorService threadPool;
	List futureList;

	public QuickSortTask(ExecutorService threadPool, List futureList, int[] inList,int start, int end){
		this.sortArray = inList;
		this.iStart = start;
		this.iEnd   = end;
		this.threadPool =threadPool;
		this.futureList =futureList;
	}

	@Override
	public boolean isReadyToProcess() {
		return true;
	}

	public void run() {
		sort(sortArray, iStart, iEnd) ;
	}

	@SuppressWarnings("unchecked")
	private   void sort(final int inList[], int start, int end) {
		int pivot = inList[start]; // consider this as  hole at inList[start],
		int leftPointer = start;
		int rightPointer = end;
		final int LEFT = 1;
		final int RIGHT = -1;
		int pointerSide = RIGHT; //  we start with right as pivot is from left

		while (leftPointer != rightPointer) {
			if (pointerSide == RIGHT) {
				if (inList[rightPointer] < pivot) {           inList[leftPointer] = inList[rightPointer];           leftPointer++;           pointerSide = LEFT;         } else {           rightPointer--;         }       } else if (pointerSide == LEFT) {         if (inList[leftPointer] > pivot) {
					inList[rightPointer] = inList[leftPointer];
					rightPointer--;
					pointerSide = RIGHT;
				} else {
					leftPointer++;
				}
				}
		}

		//put the pivot where leftPointer and rightPointer collide i.e. leftPointer == rightPointer
		inList[leftPointer]=pivot;

		if((leftPointer - start) > 1){
			if ((leftPointer - start) > SPLIT_THRESHOLD){
				futureList.add(threadPool.submit(new QuickSortTask(threadPool,futureList,inList, start, leftPointer-1)));
			}else {
				sort(inList, start, leftPointer-1);
			}
		}

		if((end - leftPointer) > 1){
			if ((end - leftPointer) > SPLIT_THRESHOLD ){
				futureList.add(threadPool.submit(new QuickSortTask(threadPool,futureList,inList, leftPointer+1, end)));
			}  else {
				sort(inList, leftPointer+1, end);
			}
		}

	}
	@SuppressWarnings("unchecked")
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
		int[] sortArray = new int[Data.size()];

		for (int i=0;i<sortArray.length;i++){
			sortArray[i]= (int) Data.get(i);
		}
		final ExecutorService executor = Executors.newFixedThreadPool(10);
		@SuppressWarnings("rawtypes")
		List futures = new Vector();
		QuickSortTask rootTask = new QuickSortTask (executor,futures,sortArray,0,sortArray.length-1)  ;
		System.out.println("Start Quick Parral");
		start = System.currentTimeMillis();
		futures.add(executor.submit(rootTask));
		while(!futures.isEmpty()){
			//     System.out.println("Future size " +futures.size());
			@SuppressWarnings("rawtypes")
			Future topFeature = (Future) futures.remove(0);
			try{
				if(topFeature!=null)topFeature.get();
			}catch(InterruptedException ie){
				ie.printStackTrace();
			}catch(ExecutionException ie){
				ie.printStackTrace();
			}
		}
		WriteOut(sortArray);
		executor.shutdown();
	}

	private static void WriteOut(int[] message)  {
		finish = System.currentTimeMillis();
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
			System.out.println("Finished");
			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String nameFormat(String string) {
		int last = string.lastIndexOf('.');
		String end =string.substring(last);
		String begin= string.substring(0, last);

		return begin+"-QP-"+end;
	}

}