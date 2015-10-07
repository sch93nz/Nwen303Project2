/**
 *
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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

		try {
			MPI.Init(args);


		int source,dest;
		int startPoint=-1,endPoint=-1;
		int tag=50;

		int myrank=MPI.COMM_WORLD.getRank();
		int size=MPI.COMM_WORLD.getSize();

		int left= (myrank+1)%size;
		int right =(myrank+2)%size;
		int prev=0;
		int[] Control = new int[4];
		int [] message=new int[1000000];
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
			Control[1]=end;
			Control[2]=1;
			Control[3]=message.length;
			MPI.COMM_WORLD.send(Control, 4, MPI.INT, left, tag);
			MPI.COMM_WORLD.send(message, message.length, MPI.INT, left, tag);
			System.out.println(""+myrank+" Says : Sending first set of data to "+ left+"");
		}
		boolean run =true;
		while(run){
			Status k =MPI.COMM_WORLD.recv(Control, 4, MPI.INT, MPI.ANY_SOURCE, tag);
			message=new int[Control[3]];
			System.out.println(""+myrank+" Says : Recieved "+Control[3]+ " data from "+k.getSource()+"");
			k = MPI.COMM_WORLD.recv(message, Control[3], MPI.INT, MPI.ANY_SOURCE, tag);
			if(k.getError()!=0){
				System.out.println(""+myrank+"Error");
				System.out.println(""+myrank+"  :  "+message+"   ");
				System.out.println(""+myrank+"   :  "+k.getSource());
			}

			System.out.println(""+myrank+" Says : Recieved data from "+k.getSource()+"");



			begin=Control[0];
			end = Control[1];
			int length = Control[3];
			if(myrank==0 && Control[3]==0 && begin == startPoint && end==endPoint){
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
				if(end-begin>2){
					prev=k.getSource();
					int middle = length/2;
					int[] leftArray = new int[middle];

					for(int i= 0;i<middle;i++){
						leftArray[i]=message[i];
					}
					Control[0]=begin;
					Control[1]=middle;
					Control[2]=1;
					Control[3]=leftArray.length;

					System.out.println("L "+myrank+"    :  "+middle+"  &&  "+Control[3]+"  &&  "+leftArray.length+"    ->  "+left+"");

					System.out.println(""+myrank+" Says : Sending Control to left set of data to "+left+"");
					MPI.COMM_WORLD.send(Control, 4, MPI.INT, left, tag);
					System.out.println(""+myrank+" Says : Sending message to left set of data to "+left+"");
					MPI.COMM_WORLD.send(leftArray,leftArray.length , MPI.INT, left, tag);

					int[] rightArray = new int[middle];
					int t=0;
					for(int i= middle;i<middle;i++){
						rightArray[t]=message[i];
						t++;
					}
					Control[0]=middle;
					Control[1]=end;
					Control[2]=1;
					Control[3]=rightArray.length;

					System.out.println("R "+myrank+"    :  "+middle+"  &&  "+Control[3]+"  &&  "+rightArray.length+"    ->  "+right+"");
					System.out.println(""+myrank+" Says : Sending Control to right set of data to "+right+"");
					MPI.COMM_WORLD.send(Control, 4, MPI.INT, right, tag);
					System.out.println(""+myrank+" Says : Sending message to right set of data to "+right+"");
					MPI.COMM_WORLD.send(rightArray, rightArray.length, MPI.INT, right, tag);

				}else{
					Control[2]=0;
					System.out.println(""+myrank+" Says : Decleared end and returning data to "+k.getSource()+"");
					MPI.COMM_WORLD.send(Control, 4, MPI.INT, k.getSource(), tag);
					MPI.COMM_WORLD.send(message, length, MPI.INT, k.getSource(), tag);
				}
			}else{


				Status j=null;
				int [] BControl= new int[4];
				if(k.getSource()==left){
					System.out.println(""+myrank+" Says : Recieving Sort Data data from "+right+"");
					MPI.COMM_WORLD.recv(BControl, 4, MPI.INT, MPI.ANY_SOURCE, tag);
					int[] second = new int[BControl[3]];
					j = MPI.COMM_WORLD.recv(second, BControl[3], MPI.INT, right, tag);

					//	j = MPI.COMM_WORLD.recv(second, 100003, MPI.INT, right, tag);
					message = merge(message,length,second,BControl[3]);
				}else{
					System.out.println(""+myrank+" Says : Recieving Sort Data  data from "+left+"");
					MPI.COMM_WORLD.recv(BControl, 4, MPI.INT, MPI.ANY_SOURCE, tag);
					int[] second = new int[BControl[3]];
					j = MPI.COMM_WORLD.recv(second, BControl[3], MPI.INT, left, tag);
					message = merge(second,BControl[3],message,length);

					Control[3] = message.length;
				}

				if(Control[0]>BControl[0])Control[0]= BControl[0];
				if(Control[1]<BControl[1])Control[1]=BControl[1];

				System.out.println(""+myrank+" Says : Decleared end and returning sorted data to "+prev+"");
				MPI.COMM_WORLD.send(Control, 4, MPI.INT, prev, tag);
				MPI.COMM_WORLD.send(message, message.length, MPI.INT, prev, tag);
				run=false;

			}
	     }
			if(myrank!=0)
			MPI.Finalize();
		} catch (MPIException e1) {
		System.out.println("What the Fuck");
			e1.printStackTrace();
		}
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

	private static int[] merge(int[] left, int leftLength, int[] right, int rightLength) {
		if(left[2]!=right[2])System.out.println("wow shit something has gone wrong here.");
		int[] result = new int[leftLength+rightLength];
		/*
		 * Passed information from node to node;
		 */


		//remember to ignore the first 3 elements;
		int l=0,r=0,i=0;
		while(l<leftLength &&r<rightLength){
			if(left[l]<=right[r]){
				result[i]=left[l];
				l++;
			}else{
				result[i]=right[r];
				r++;
			}
			i++;
		}
		while(l<leftLength){
			result[i]=left[l];
			l++;
			i++;
		}
		while (r<rightLength){
			result[i]=right[r];
			r++;
			i++;
		}
		return result;


	}




}
