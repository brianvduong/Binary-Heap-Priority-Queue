//Brian Duong
//cssc1468

package data_structures;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
public class BinaryHeapPriorityQueue<E extends Comparable<E>> implements PriorityQueue<E> {

	
	protected int currentSize;
	protected Wrapper<E>[] storage;
	protected long modificationCounter;
	protected int entryNumber;
	
	@SuppressWarnings("unchecked")
	public BinaryHeapPriorityQueue() {
		currentSize = 0;
		this.storage = new Wrapper[DEFAULT_MAX_CAPACITY];
		modificationCounter = 0;
		entryNumber = 0;
	}
	
	@SuppressWarnings("unchecked")
	public BinaryHeapPriorityQueue(int maxCapacity) {
		currentSize = 0;
		this.storage = new Wrapper[maxCapacity];
		modificationCounter = 0;
		entryNumber = 0;
	}
	
	@Override
	public boolean insert(E object) {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Wrapper<E> obj = new Wrapper(object);
		if(isFull()) {
			return false;
		}
		currentSize++;
		modificationCounter++;
		int index = currentSize - 1;
		storage[index] = obj;
		trickleUp();
		return true;
	}

	@Override
	public E remove() {
		E temp = peek();
		if(isEmpty()) {
			return null;
		}
		modificationCounter++;
		trickleDown(0);
		currentSize--;
		return temp;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean delete(E obj) {
		int tempSize = currentSize;
		int i = 0;
		while(i < currentSize) {
			if(((Comparable)obj).compareTo(storage[i].data) == 0) {
				modificationCounter++;
				trickleDown(i);
				currentSize--;
			}
			else
				i++;
		}
		return (tempSize != currentSize);					//Returns true if changes have been made, false otherwise
	}

	@Override
	public E peek() {
		if(isEmpty()) {
			return null;
		}
		return storage[0].data;
	}

	@Override
	public boolean contains(E obj) {
		for(int i = 0; i < currentSize; i++) {
			if(((Comparable<E>)obj).compareTo(storage[i].data) == 0) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int size() {
		return currentSize;
	}

	@Override
	public void clear() {
		Arrays.fill(storage, null);
		modificationCounter++;
		currentSize = 0;
	}

	@Override
	public boolean isEmpty() {
		return (size() == 0 || storage[0] == null);
	}

	@Override
	public boolean isFull() {
		return (size() == storage.length);
	}
	
	@Override
	public Iterator<E> iterator() {
		return new IteratorHelper();
	}
	
	private class IteratorHelper implements Iterator<E>
	{
		Wrapper<E>[] sortedArray;
		int count,index;
		long stateCheck;
		
		public IteratorHelper() {
			index = 0;
			stateCheck = modificationCounter;
			sortedArray = mergeSort(storage);
		}
		@Override
		public boolean hasNext() {
			if(stateCheck != modificationCounter) {
				throw new ConcurrentModificationException();
			}
			return count != size();
		}
		
		@Override
		public E next() {
			if(!hasNext())
				throw new NoSuchElementException();
			E tmp = sortedArray[index++].data;
			count++;
			return tmp;
		}
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	private Wrapper<E>[] mergeSort(Wrapper<E>[] array){
		Wrapper<E>[] aux = new Wrapper[currentSize];
		for(int i = 0; i < currentSize; i++) {
			aux[i] = storage[i];
		}
		mergeSortHelper(aux, 0, currentSize - 1);
		return aux;
	}

	private void mergeSortHelper(Wrapper<E>[] n, int low, int hi) {
		if(hi-low < 1) return;
		int mid = (low+hi)/2;
		mergeSortHelper(n, low, mid);
		mergeSortHelper(n, mid+1, hi);
		merge(n, low, mid, hi);
	}
	
	private void merge(Wrapper<E>[] n, int low, int mid, int hi) {
		@SuppressWarnings("unchecked")
		Wrapper<E>[] aux = new Wrapper[hi-low+1];
		int i = low;
		int j = mid+1;
		int k = 0;
		while(i <= mid && j <= hi) {
			if(n[i].compareTo(n[j]) <= 0)
				aux[k] = n[i++];
			else
				aux[k] = n[j++];
			k++;
		}
		if(i <= mid && j > hi) {
			while(i <= mid)
				aux[k++] = n[i++];
		}
			else {
				while(j <= hi)
					aux[k++] = n[j++];
			}
		for(k = 0; k < aux.length; k++) {
			n[k+low] = aux[k];
		}
	}
	
	@SuppressWarnings("hiding")
	protected class Wrapper<E> implements Comparable<Wrapper<E>>{
		long number;
		E data;
		
		public Wrapper(E d) {
			number = entryNumber++;
			data = d;
		}
		
		@SuppressWarnings("unchecked")
		public int compareTo(Wrapper<E> o) {
			if(((Comparable<E>)data).compareTo(o.data) == 0)
				return (int) (number - o.number);
			return ((Comparable<E>)data).compareTo(o.data);
		}
	}
	
	private void trickleUp() {
		int newIndex = currentSize - 1;
		int parentIndex = (newIndex - 1) >> 1;
		Wrapper<E> newValue = storage[newIndex];
		while(parentIndex >= 0 && 
		newValue.compareTo(storage[parentIndex]) < 0) {
			storage[newIndex] = storage[parentIndex];
			newIndex = parentIndex;
			parentIndex = (parentIndex - 1) >> 1;
		}
		storage[newIndex] = newValue;
	}
	
	private void trickleDown(int i) {
		int current = i;
		int child = getNextChild(current);
		while(child != -1 && 
				storage[current].compareTo(storage[child]) < 0 && 
				storage[child].compareTo(storage[currentSize-1]) < 0) {
			storage[current] = storage[child];
			current = child;
			child = getNextChild(current);
		}
		storage[current] = storage[currentSize-1];
	}
	
	private int getNextChild(int current) {
		int left = (current << 1) + 1;
		int right = left + 1;
		if(right < currentSize) {								// 2 children
			if(storage[left].compareTo(storage[right]) < 0) {
				return left;
			}
			return right;
		}
		if(left < currentSize) {								// 1 child
			return left;
		}
		return -1;												// no children
	}
}