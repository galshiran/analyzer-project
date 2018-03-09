package analyzedClass;

public class FindMaxElement_A {
	public int findMaxElement(int[] arr) {
		//int length = arr.length;
		int max = Integer.MIN_VALUE;

		int size = arr.length + 5;
		size = 5 + arr.length;
		int n = size;
		/*if((n = 5) == 1) {
			System.out.println("test");
		}*/
		// how we treat this case?
		n = arr.length - 3;
		// probably the algorithm won't work
		n = size - size + 5;
		size = 5;
		for (int i = 0; i < arr.length; i++) // this is comment
			i++;

		for(int i = 0; i < arr.length; i++) if(n > 1 )
			for(int j = 0; j < arr.length; j++)
				n = 5;

		if(n > 1)
			for(int i = 0 ;i < size;i++) if(n > 1)
				n=5;

		// need to handle this case: n = arr.length (alignment in comment)
		//this case not handle
		size = (size > 5) ?  arr.length : 0;
		//also this case
		for (int i = arr.length; i > 0  ; i--) {
			System.out.println("test");
		}

		for(int i = 0; i < arr.length; i++) {
			if(max < arr[i]) {
				max = arr[i];
			}
		}
		int p = 5;

		for ( p = arr.length; p > 0; p--) {
			System.out.println("Offierrrr");
		}

		//p = 0;		
		return max;
	}
}