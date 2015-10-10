
public class mainTC {

	static String[] Tests= {"med.3.killer.1000.txt","rand.dups.1000.txt",
			"rand.no.dups.1000.txt","rand.steps.1000.txt",
			"rev.partial.1000.txt","rev.saw.1000.txt",
			"seq.partial.1000.txt","seq.saw.1000.txt"};
	
	
	public static void main(String[] args) {
		for(String i : Tests){
			System.out.println(i);
			String[] test = {i};
		System.out.println("Starting Testing suit");
		MergeSort a = new MergeSort();
		a.main(test);
		// QuickSort b = new QuickSort();
		//b.main(test);
		
		MergeSortParral c = new MergeSortParral();
		c.main(test);
		QuickSortTask d = new QuickSortTask();
		d.main(test);
		System.out.println("Finishing Testing suit");
		}
	}

}
 