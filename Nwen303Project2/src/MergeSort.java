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
		
				
		int source,dest,tag=50;
		
		int myrank=MPI.COMM_WORLD.getRank();
		int size=MPI.COMM_WORLD.getSize();
		
		int next= (myrank+1)%size;
		//int prev=(myrank+size-1)%size;
		
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
		MPI.COMM_WORLD.send(message, message.length+3, MPI.INT, next, tag);
		}
		
		Status k = MPI.COMM_WORLD.recv(message, 100003, MPI.INT, MPI.ANY_SOURCE, tag);
		int length=k.getCount(MPI.INT);
		
		
		
		
		begin=message[0];
		end = message[1];
		if(message[3]==1){
		if(end-begin>2){
			
			int middle = (end+begin)/2;
			int[] left = new int[middle+2];
			left[0]=begin;
			left[1]=middle;
			for(int i= 2;i<middle+2;i++){
				left[i-2]=message[i];
			}
			MPI.COMM_WORLD.send(left,length , MPI.INT, next, tag);
			int[] right = new int[middle+2];
			
			int nextnext =(myrank+2)%size;
			right[1]=begin;
			right[0]=middle;
			int t=2;
			for(int i= middle;i<middle+2;i++){
				right[t]=message[i];
				t++;
			}
			MPI.COMM_WORLD.send(right, length, MPI.INT, nextnext, tag);
			
		}else{
			message[3]=0;
			MPI.COMM_WORLD.send(message, length, MPI.INT, next, tag);
		}
		}else{
			
		}
		
		

		
		
		
		
		
	}
	
	
	
	
}
