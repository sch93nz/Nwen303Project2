/**
 * 
 */

import java.util.ArrayList;
import java.util.Scanner;

import mpi.*;
/**
 * @author Matthew Schmidt
 *
 */
public class MergeSort {



	public static void main(String[] args) throws MPIException{


		int source,dest;
		int tag=50;

		int myrank=MPI.COMM_WORLD.getRank();
		int size=MPI.COMM_WORLD.getSize();

		int left= (myrank+1)%size;
		int right =(myrank+2)%size;
		int prev=0;

		int [] message=new int[1];
		int begin=0,end=0;



		if(myrank == 0){
			ArrayList<Integer> Data = new ArrayList<Integer>();
			Scanner scan = new Scanner(args[0]);
			while(scan.hasNext()){
				Data.add(scan.nextInt());
			}
			scan.close();
			message = new int[Data.size()+2];

			for (int i=0+2;i<message.length;i++){
				message[i]=(int) Data.get(i);
			}

			begin =0;
			end =message.length;
			message[0]=begin;
			message[1]=end;
			message[2]=1;
			System.out.println(myrank+"Says : Sending first set of data to"+myrank);
			MPI.COMM_WORLD.send(message, message.length, MPI.INT, MPI.ANY_SOURCE, tag);
		}

		Status k = MPI.COMM_WORLD.recv(message, 100003, MPI.INT, MPI.ANY_SOURCE, tag);
		int length=k.getCount(MPI.INT);
		System.out.println(myrank+"Says : Recieved data from "+k.getSource());



		begin=message[0];
		end = message[1];
		if(message[3]==1){
			if(end-begin>2){
				prev=k.getSource();
				int middle = (end+begin)/2;
				int[] leftArray = new int[middle+3];
				leftArray[0]=begin;
				leftArray[1]=middle;
				for(int i= 3;i<middle+3;i++){
					leftArray[i-3]=message[i];
				}
				System.out.println(myrank+"Says : Sending left set of data to "+left);
				MPI.COMM_WORLD.send(leftArray,leftArray.length , MPI.INT, left, tag);
				int[] rightArray = new int[middle+3];


				rightArray[1]=begin;
				rightArray[0]=middle;
				int t=2;
				for(int i= middle;i<middle+2;i++){
					rightArray[t]=message[i];
					t++;
				}
				System.out.println(myrank+"Says : Sending right set of data to "+right);
				MPI.COMM_WORLD.send(rightArray, rightArray.length, MPI.INT, right, tag);

			}else{
				message[3]=0;
				System.out.println(myrank+"Says : Decleared end and returning data to "+k.getSource());
				MPI.COMM_WORLD.send(message, length, MPI.INT, k.getSource(), tag);
			}
		}else{
			int[] second = new int[(length-3/2)+3];
			Status j=null;
			if(k.getSource()==left){
				System.out.println(myrank+"Says : Recieving data from "+right);
				j = MPI.COMM_WORLD.recv(second, 100003, MPI.INT, right, tag);
				message = merge(message,length,second,j.getCount(MPI.INT));
			}else{
				System.out.println(myrank+"Says : Recieving data from "+left);
				j = MPI.COMM_WORLD.recv(second, 100003, MPI.INT, left, tag);
				message = merge(second,j.getCount(MPI.INT),message,length);
			}
			System.out.println(myrank+"Says : Decleared end and returning sorted data to "+prev);
			MPI.COMM_WORLD.send(message, message.length, MPI.INT, prev, tag);

		}

	}

	private static int[] merge(int[] left, int leftLength, int[] right, int rightLength) {
		if(left[2]!=right[2])System.out.println("wow shit something has gone wrong here.");
		int[] result = new int[leftLength-3+rightLength];
		/*
		 * Passed information from node to node;
		 */
		if(left[0]<=right[0])result[0]=left[0]; else result[0]=right[0];
		if(right[1]>=left[1])result[1]=right[1]; else result[1]=left[1];
		result[2]=0;
		//remember to ignore the first 3 elements;
		int l=3,r=3,i=3;
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
