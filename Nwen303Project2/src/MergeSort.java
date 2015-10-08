/**
 *
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import mpi.MPI;
import mpi.MPIException;
import mpi.Status;

/**
 * @author Matthew Schmidt
 *
 */
public class MergeSort {

	static long start;
	static long finished;



	public static void main(String[] args) throws FileNotFoundException{
		int myrank=0;
		long[] Control = new long[5];
		int [] message=null;
		Status k = null;
		try {

			MPI.Init(args);
			MPI.COMM_WORLD.setErrhandler(MPI.ERRORS_RETURN);

			int source,dest;
			int startPoint=-1,endPoint=-1;
			int tag=50;

			myrank=MPI.COMM_WORLD.getRank();
			int size=MPI.COMM_WORLD.getSize();
			int from = (myrank + size - 1) % size;


			int left= (myrank+1)%size;
			int right =(myrank+2)%size;
			int prev=0;


			int begin=0,end=0;

			if(myrank == 0){

				ArrayList<Integer> Data = new ArrayList<Integer>();
				Scanner scan = new Scanner(new File(args[0]));
				while(scan.hasNext()){
					Data.add(scan.nextInt());
				}
				scan.close();
				message = new int[Data.size()];

				for (int i=0;i<message.length;i++){
					message[i]= (int) Data.get(i);
				}
				start = System.currentTimeMillis();
				begin =0;
				end =message.length;
				if(startPoint==-1)startPoint=begin;
				if(endPoint==-1)endPoint=end;

				Control[0]=begin;
				Control[1]=end /2;
				Control[2]=1;
				Control[3]=message.length;
				Control[4]= myrank;
				MPI.COMM_WORLD.send(Control, 5, MPI.LONG, left, tag);
				MPI.COMM_WORLD.send(message, message.length, MPI.INT, left, tag);
				System.out.println(""+myrank+" Says : Sending first set of data to "+ left+"");
			}
			
			
			boolean run =true;
			while(run){
				
				StringBuffer st = new StringBuffer();
				
				k =MPI.COMM_WORLD.recv(Control, 5, MPI.LONG, MPI.ANY_SOURCE, tag);
				
				for(long i : Control){
					st.append(i+" ");
				}
				System.out.println(""+myrank+" Says: Control Res = "+st.toString());
				message=new int[(int) Control[3]];
				st = new StringBuffer();
				System.out.println(""+myrank+" Says : Recieved "+Control[3]+ " data from "+k.getSource()+"");
				
				k = MPI.COMM_WORLD.recv(message, (int) Control[3], MPI.INT, (int)Control[4], tag);
				
				for(long i : message){
					st.append(i+" ");
				}
				System.out.println(""+myrank+" Says: Message Res = "+st.toString());

				System.out.println(""+myrank+" Says : Recieved data from "+k.getSource()+"");



				begin=(int) Control[0];
				
				int length = (int) Control[3];
				if(myrank==0 && Control[2]==0 &&begin == startPoint ){
					try {
						finished = System.currentTimeMillis();
						if(args.length<1)WriteOut(length,message,args[1]);
						else WriteOut(length,message,nameFormat(args[0]));

					} catch (IOException e) {
						MPI.Finalize();
						e.printStackTrace();
					}
					System.out.println(""+myrank+" Death");
					MPI.Finalize();
				}

				if(Control[2]==1){
					if(Control[3]>2){
						prev=k.getSource();
						int middle = length/2;
						doLeft(myrank, Control, message, tag, left, begin,
								middle);

						doRight(myrank, Control, message, tag, right, end,
								length, middle);

					}else{
						
						declearEnd(myrank, Control, message, k, tag, length);
					}
				}else{

					Status j=null;

					long [] BControl= Arrays.copyOf(Control, Control.length);

					if(myrank!=0&& Control[1]==Control[3]&& Control[2]==0){
					
						
						
					}else if(k.getSource()==left){
						
						System.out.println(""+myrank+" Says : Recieving Control Sort Data data from "+right+"");
						MPI.COMM_WORLD.recv(BControl, 5, MPI.LONG, MPI.ANY_SOURCE, tag);
						int[] second = new int[(int) BControl[3]];
						st = new StringBuffer();
						for(long i : BControl){
							st.append(i+" ");
						}
						System.out.println(""+myrank+" Says: Control Res = "+st.toString());
						System.out.println(""+myrank+" Says : Recieving sizeof "+BControl[3]+" Sort Data data from "+BControl[4]+"");
						j = MPI.COMM_WORLD.recv(second, (int) BControl[3], MPI.INT,(int) BControl[4], tag);
						
						message = merge(myrank,message,length,second,(int) BControl[3]);

						Control[3] = message.length;

					}else{

						System.out.println(""+myrank+" Says : Recieving Control Sort Data data from "+left+"");
						MPI.COMM_WORLD.recv(BControl, 5, MPI.LONG, MPI.ANY_SOURCE, tag);
						int[] second = new int[(int) BControl[3]];
						st = new StringBuffer();
						for(long i : BControl){
							st.append(i+" ");
						}
						System.out.println(""+myrank+" Says: Control Res = "+st.toString());
						System.out.println(""+myrank+" Says : Recieving sizeof "+BControl[3]+" Sort Data data from "+BControl[4]+"");
						j = MPI.COMM_WORLD.recv(second, (int) BControl[3], MPI.INT,(int) BControl[4], tag);
						
						message = merge(myrank,second,(int) BControl[3],message,length);

						Control[3] = message.length;

					}

					if(Control[0]>BControl[0])Control[0]= BControl[0];
					
					Control[4]=prev;
					System.out.println(""+myrank+" Says : Decleared end and returning Sorted data sizeof "+Control[3]+" to "+prev+"");
					System.out.println(""+myrank+" Says : Decleared end and returning sorted data to "+prev+"");
					MPI.COMM_WORLD.send(Control, 5, MPI.LONG, prev, tag);
					MPI.COMM_WORLD.send(message, message.length, MPI.INT, prev, tag);
					//if(myrank!=0)
					//	run= false;


				}
			}
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>DEATH<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
				MPI.Finalize();

		} catch (MPIException e1) {
			try {
				System.out.println("||"+myrank+" Says : Recieved "+Control[3]+
						" data from "+k.getSource()+"||");
				System.out.println("||"+myrank+"||"+k.getCount(MPI.LONG)+">>>"
						+Control.length+"-->"+k.getSource());
			} catch (MPIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


			e1.printStackTrace();

			try {
				MPI.Finalize();
			} catch (MPIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	/**
	 * @param myrank
	 * @param Control
	 * @param message
	 * @param k
	 * @param tag
	 * @param length
	 * @throws MPIException
	 */
	private static void declearEnd(int myrank, long[] Control, int[] message,
			Status k, int tag, int length) throws MPIException {
		Control[2]=0;
		Control[4]=myrank;
		MPI.COMM_WORLD.send(Control, 5, MPI.LONG, k.getSource(), tag);
		System.out.println(""+myrank+" Says : Decleared end and returning data sizeof -"+Control[3]+"- to "+k.getSource()+"");
		MPI.COMM_WORLD.send(message, length, MPI.INT, k.getSource(), tag);
	}

	/**
	 * @param myrank
	 * @param Control
	 * @param message
	 * @param tag
	 * @param right
	 * @param end
	 * @param length
	 * @param middle
	 * @throws MPIException
	 */
	private static void doRight(int myrank, long[] Control, int[] message,
			int tag, int right, int end, int length, int middle)
			throws MPIException {
		StringBuffer st;
		int[] rightArray = new int[length-middle];
		int t=0;
		st = new StringBuffer();

		for(int i= middle;i<length ;i++){
			st.append(message[i]+" ");
			rightArray[t]=message[i];
			t++;
		}
		System.out.println("R "+myrank+" Data>>"+st.toString());
		Control[0]=middle;
		
		Control[2]=1;
		Control[3]=rightArray.length;
		Control[4] =myrank;
		System.out.println("R rank="+myrank+"    :  middle="+middle+"  &&  length="+Control[3]+
				"  &&  arrayLength="+rightArray.length+"    ->  "+right+"");
		System.out.println(""+myrank+" Says : Sending Control to right set of data to "+right+"");
		MPI.COMM_WORLD.send(Control, 5, MPI.LONG, right, tag);
		System.out.println(""+myrank+" Says : Sending message to right set of data to "+right+"");
		MPI.COMM_WORLD.send(rightArray, rightArray.length, MPI.INT, right, tag);
	}

	/**
	 * @param myrank
	 * @param Control
	 * @param message
	 * @param tag
	 * @param left
	 * @param begin
	 * @param middle
	 * @throws MPIException
	 */
	private static void doLeft(int myrank, long[] Control, int[] message,
			int tag, int left, int begin, int middle) throws MPIException {
		StringBuffer st;
		int[] leftArray = new int[middle];
		st = new StringBuffer();
		for(int i= 0;i<middle;i++){
			st.append(message[i]+" ");
			leftArray[i]=message[i];
		}
		System.out.println("L "+myrank+" Data>>"+st.toString());

		Control[0]=begin;
		
		Control[2]=1;
		Control[3]=leftArray.length;
		Control[4] = myrank;
System.out.println("L rank="+myrank+"    :  middle="+middle+"  &&  length="+Control[3]+
				"  &&  ArrayLength="+leftArray.length+"    ->  "+left+"");

		System.out.println(""+myrank+" Says : Sending Control to left set of data to "+left+"");
		MPI.COMM_WORLD.send(Control, 5, MPI.LONG, left, tag);
		System.out.println(""+myrank+" Says : Sending message to left set of data to "+left+"");
		MPI.COMM_WORLD.send(leftArray,leftArray.length , MPI.INT, left, tag);
	}

	private static String nameFormat(String string) {
		int last = string.lastIndexOf('.');
		String end =string.substring(last);
		String begin= string.substring(0, last);

		return begin+"-sorted-"+end;
	}

	private static void WriteOut(int length, int[] message, String args) throws IOException {
		FileWriter writer = new FileWriter(args);

		writer.write("Starting time = "+start+"\r\n");
		System.out.print("Starting time = "+start+"\r\n");

		writer.write("Finished time = "+finished+"\r\n");
		System.out.print("Finished time = "+finished+"\r\n");

		long time = finished-start;
		writer.write("Time Taken = "+time+"\r\n");
		System.out.print("Time Taken = "+time+"\r\n");

		for(int i=0 ;i<length;i++){
			writer.write(message[i]+"\r\n");
		}
		writer.close();
	}

	private static int[] merge(int myrank, int[] left, int leftLength, int[] right, int rightLength) {
		StringBuffer st =new StringBuffer();
		int[] result = new int[leftLength+rightLength];
		int k=0;
		for (int i :left){
			result[k]=i;
			st.append(i+" ");
			k++;
		}
		System.out.println("L "+myrank+"  >><<"+st.toString());

		st =new StringBuffer();
		for (int i :right){
			result[k]=i;
			st.append(i+" ");
			k++;
		}
		System.out.println("R "+myrank+" >><<"+st.toString());


		for (int i=1 ;i<result.length;i++){
			int temp = result[i];
			int j;
			for (j=i-1; j>=0 && temp<result[j];j--){
				result[j+1]= result[j];
			}
			result[j+1] = temp;
		}

		return result;


	}




}
