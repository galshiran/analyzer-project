package analyzedClass;

public class FindMaxElement_B {

	public int findMaxElement(int[] arr) {

		// bubble sorting
		int temp = 0;
		for (int i = 0; i < arr.length; i++) {
			for (int j = 1; j < arr.length - i; j++) {
				if (arr[j - 1] > arr[j]) {
					// swap elements
					temp = arr[j - 1];
					arr[j - 1] = arr[j];
					arr[j] = temp;
				}
			}
			int m = 0;
			while(m < 10) {
				m++;
			}
		}
		int i = 0;
		while(i < arr.length) {
			i++;
		}

		return arr[arr.length - 1];
	}

}
