

/**
 * FibonacciHeap
 *
 * An implementation of a Fibonacci Heap over integers.
 */
public class FibonacciHeap
{
	private HeapNode min;
	private HeapNode first;
	private int size;
	static final double GOLDEN_RATIO = (1 + Math.sqrt(5))/2;
	private int marked_count;
	private int num_of_trees;
	static int total_cuts;
	static int total_links;
	
	public FibonacciHeap()
	{
		this.first = null;
		this.min = null;
		this.size = 0;
		this.marked_count = 0;
	}

   /**
    * public boolean isEmpty()
    *
    * Returns true if and only if the heap is empty.
    * Time Complexity: O(1)
    */
    public boolean isEmpty()
    {
    	return first == null; 
    }
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
    * The added key is assumed not to already belong to the heap.  
    * 
    * Returns the newly created node.
	* Time Complexity: O(1)
    */
    public HeapNode insert(int key)
    {    
    	this.num_of_trees++;
    	HeapNode newNode = new HeapNode(key);
    	if(isEmpty()) //insert node to empty heap
    	{
    		this.first = newNode;
    		this.first.next = this.first;
    		this.first.prev = this.first;
    		this.min = newNode;
    	}
    	else //heap wasn't empty
    	{
    		addAsFirst(newNode);
        	if(newNode.getKey() < this.min.getKey())
        		this.min=newNode;
    	}
    	
    	this.size++;
    	return this.first;
    }
    	
    

   /**
    * public void deleteMin()
    *
    * Deletes the node containing the minimum key.
    * Time Complexity: O(n)
    */
    public void deleteMin()
    {
    	if(isEmpty()) //if ther's nothing to delete
    		return;
    	this.num_of_trees --;
    	if(this.size == 1) // if min is the last node
    	{
    		this.min = null;
    		this.first = null;
    		this.size= 0;
    		return;
    	}
    	HeapNode first_child = null;
    	HeapNode prev_min = this.min.prev;
		HeapNode next_min = this.min.next;
		this.min.setNext(null);
		this.min.setPrev(null);
		
    	if(this.min.child != null) // min have children
    	{
    		first_child = this.min.child;
    		// disconnect the deleted Node from his children
    		this.min.setChild(null);
    		HeapNode pointer = first_child;
    		do {
    			this.num_of_trees ++;
    			if(pointer.getMark() == true)
    			{
    				pointer.setMark(false);
        			this.marked_count --;
    			}
    			pointer.setParent(null);
    			pointer = pointer.getNext();
    		}while(pointer != first_child); 
    		
    		
    		if(next_min == this.min)//the node we deleted didnt  have brothers
        	{
        		this.first = first_child;
        	}
        	else
        	{
    			HeapNode prev_first_child = first_child.prev;
    			first_child.setPrev(prev_min);
        		prev_min.setNext(first_child);
        		prev_first_child.setNext(next_min);
        		next_min.setPrev(prev_first_child);
        		if( this.first == this.min) // the node we deleted was this.first
        			this.first = first_child;
        	}
    		
    	}
    	else // min doesnt have children
    	{
    		prev_min.setNext(next_min);
    		next_min.setPrev(prev_min);
    		if(this.first == this.min)
    			this.first = next_min;
    	}
    	
    	this.size --;
    	this.consolidation();
    }
    /**
     *public void consolidation()
     * Function that considilate the heap according what we saw in class.
     * Time Complexity: O(n)
     */
    public void consolidation() 
    {
    	
    	int arr_size = (int)(Math.ceil(Math.log(size))/Math.log(GOLDEN_RATIO))+1;
    	HeapNode[] ranks= new HeapNode [arr_size]; 
    	this.first.getPrev().next = null; // disconect the last tree from the first, in order of not getting to inf loop
    	HeapNode curr = this.first;
    	HeapNode next = curr.next;
    	while(curr != null) // enter all trees to the array ranks
    	{
    		curr.setPrev(null);
    		curr.setNext(null);
    		int curr_rank = curr.getRank();
    		if(ranks[curr_rank] == null) 
    		{
    			ranks[curr_rank] = curr;
    			curr = next;
    			if(next != null)
    				next = next.getNext();
    		}
    		else
    		{
    			
    			curr = link(ranks[curr_rank],curr);
    			this.num_of_trees --;
    			ranks[curr_rank] = null;
    		}
    	}
    	// connect all trees
    	HeapNode smallest_node = null;
    	boolean found_smallest_node = false;
    	curr = null;
    	HeapNode prev = null;
    	HeapNode minNode = null;
    	for(int i=0; i<arr_size;i++)
    	{
    		if(ranks[i] == null)
    			continue;
    		if(!found_smallest_node)
    		{
    			smallest_node = ranks[i];
    			found_smallest_node = true;
    			prev = smallest_node;
    			minNode = smallest_node;
    			continue;
    		}
    		next = ranks[i];
    		if(next.getKey() < minNode.getKey())
    			minNode = next;
    		next.setPrev(prev);
    		prev.setNext(next);
    		prev = next;
    	}
    	prev.setNext(smallest_node);
    	smallest_node.setPrev(prev);
  
    	this.first = smallest_node;
    	this.min = minNode;
    	
    	
    }
    
    /**
     *public HeapNode link(HeapNode a, HeapNode b)
     *
     *merge 2 trees, connect the bigger node as the smaller's child
     *@pre: a,b != null && a.rank == b.rank
	 * updates total links
	 * Time Complexity: O(1)
     */
    public HeapNode link(HeapNode smaller, HeapNode bigger) 
    {
    	if(smaller.getKey() > bigger.getKey())
    	{
    		return link(bigger,smaller);
    	}
    	total_links ++;
    	if(smaller.getChild() != null) // smaller,bigger have children
    	{
    		HeapNode smaller_first_child = smaller.getChild();
        	HeapNode smaller_last_child = smaller_first_child.prev;
        	smaller.setChild(bigger);
        	bigger.setParent(smaller);
        	bigger.setNext(smaller_first_child);
        	smaller_first_child.setPrev(bigger);
        	bigger.setPrev(smaller_last_child);
        	smaller_last_child.setNext(bigger);
    	}
    	else // smaller,bigger don't have children
    	{
    		smaller.setChild(bigger);
        	bigger.setParent(smaller);
        	bigger.setNext(bigger);
        	bigger.setPrev(bigger);
    	}
    	smaller.setRank(smaller.getRank() + 1);
    	
    	return smaller;
    	
    }
    
  

   /**
    * public HeapNode findMin()
    *
    * Returns the node of the heap whose key is minimal, or null if the heap is empty.
    * Time Complexity: O(1)
    */
    public HeapNode findMin()
    {
    	return this.min;
    } 
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Melds heap2 with the current heap.
    * Time Complexity: O(1)
    */
    public void meld (FibonacciHeap heap2)
    {
    	if(heap2.isEmpty())
    		return;
    	if(this.isEmpty())// (&& !heap2.isEmpty)
    	{
    		this.first = heap2.first;
    		this.min = heap2.min;
    		this.size = heap2.size;
    		this.marked_count = heap2.marked_count;
    		this.num_of_trees = heap2.num_of_trees;
    		return;
    	}
    	
    	// update fields
    	this.num_of_trees = this.num_of_trees + heap2.num_of_trees;
    	if(heap2.min.getKey() < this.min.getKey()) 
    		  this.min = heap2.min;
    	this.size = this.size + heap2.size; 
    	this.marked_count = this.marked_count + heap2.marked_count; 
    	
    	//connect between the heaps
    	HeapNode first_heap2 = heap2.first;
    	HeapNode last_heap2 = heap2.first.getPrev();
    	HeapNode last_this = this.first.getPrev();
    	this.first.setPrev(last_heap2);
    	last_heap2.setNext(this.first);
    	last_this.setNext(first_heap2);
    	first_heap2.setPrev(last_this);
    }

   /**
    * public int size()
    *
    * Returns the number of elements in the heap.
    * Time Complexity: O(1)
    */
    public int size()
    {
    	return this.size; 
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return an array of counters. The i-th entry contains the number of trees of order i in the heap.
    * Note: The size of of the array depends on the maximum order of a tree, and an empty heap returns an empty array.
    * Time Complexity: O(n)
    */
    public int[] countersRep()
    {
    	if(isEmpty()) // if heap empty
    		return new int[0];
    	int max_rank = this.first.getRank();
    	HeapNode pointer = this.first.next;
    	while(pointer != this.first) //checks what's the maximum rank
    	{
    		if(pointer.getRank() > max_rank)
    			max_rank = pointer.getRank();
    		pointer = pointer.getNext();
    	}
    	int[] arr = new int[max_rank + 1];
    	pointer = this.first;
    	do { // add trees to array
    		arr[pointer.getRank()] ++;
    		pointer = pointer.getNext();
    		
    	}while(pointer != this.first);
    	
    	
        return arr; 
    }
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap.
	* It is assumed that x indeed belongs to the heap.
    * Time Complexity: O(n)
    */
    public void delete(HeapNode x) 
    { 
    	decreaseKey(x, x.getKey() - (this.min.getKey()-1));
    	deleteMin(); // size,first,min,num_of_trees updates inside this function
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
    * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
	* Time Complexity: O(n)
    */
    public void decreaseKey(HeapNode x, int delta)
    {    
    	x.setKey(x.getKey()-delta);
    	if(x.getKey() < this.min.getKey()) // update min if needed
    		this.min = x;
    	if(x.getParent() == null) // x doesnt have parent 
    	{
    		return;
    	}
    	
    	if(x.getParent().getKey() < x.getKey()) //x's parent smaller then him as needed:)
    		return;
    	
    	cascadingCut(x,x.getParent()); 
    }
    
    
    /**
     * public void cut(HeapNode x,HeapNode y) 
     *
     *
     *pre: y!= null
     * disconnect x from it's parent y and add x as first
     * updates total_cuts,marked_count,num_of_trees fields. 
     * unmarks x if needed
	 * Time Complexity: O(1)
     */

    public void cut(HeapNode x,HeapNode y) 
    {
    	total_cuts ++;
    	x.setParent(null);
    	if(x.getMark()) 
    		marked_count --;
    	x.setMark(false);
    	y.setRank(y.getRank() - 1);
    	if(x.getNext() == x) //x dosen't have brothers
    		y.setChild(null);
    	else
    	{
    		if(y.getChild() == x) //x was y's child
    			y.setChild(x.getNext());
    		//disconnect x
    		x.getPrev().setNext(x.getNext());
    		x.getNext().setPrev(x.getPrev());
    		x.setNext(null);
    		x.setPrev(null);
    	}
    	addAsFirst(x); // add first as this.first
    	this.num_of_trees ++;
    }
    
    /**
     * public void cascadingCut(HeapNode x,HeapNode y)
     *
     *
     * pre: y!= null
     * perform a cascading cut progress starting at x
	 * Time Complexity: O(n)
     */
    
    public void cascadingCut(HeapNode x,HeapNode y) //y is x's parent
    {
    	cut(x,y);
    	if(y.parent != null) // y isn't root
    	{
    		if(y.mark == false)
    		{
    			y.setMark(true);
        		this.marked_count ++;
    		}
    		else
    		{
    			cascadingCut(y,y.parent); //continue cascading cut progress
    		}
    	}
    }
    
    /**
     * public void addAsFirst(HeapNode x) 
     *
     *
     *pre: x!= null
     *
     * 
     * add x to the beginning.
     * (doesn't mark/unmark && doesn't update fileds exept of this.first)
	 * Time Complexity: O(1)
     */
 
    public void addAsFirst(HeapNode x) 
    {
    	HeapNode first_prev = this.first.getPrev();
    	HeapNode old_first = this.first;
    	this.first = x;
    	x.setNext(old_first);
    	old_first.setPrev(this.first);
    	this.first.setPrev(first_prev);
    	first_prev.setNext(this.first);
    	
    }

   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * 
    * In words: The potential equals to the number of trees in the heap
    * plus twice the number of marked nodes in the heap.
	* Time Complexity: O(1)
    */
    public int potential() 
    {    
    	return this.num_of_trees + 2*this.marked_count; 
    }

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the
    * run-time of the program. A link operation is the operation which gets as input two
    * trees of the same rank, and generates a tree of rank bigger by one, by hanging the
    * tree which has larger value in its root under the other tree.
	* Time Complexity: O(1)
    */
    public static int totalLinks()
    {    
    	return total_links;// should be replaced by student code
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the
    * run-time of the program. A cut operation is the operation which disconnects a subtree
    * from its parent (during decreaseKey/delete methods).
	* Time Complexity: O(1)
    */
    public static int totalCuts()
    {    
    	return total_cuts; // should be replaced by student code
    }

     /**
    * public static int[] kMin(FibonacciHeap H, int k) 
    *
    * This static function returns the k smallest elements in a Fibonacci heap that contains a single tree.
    * The function should run in O(k*deg(H)). (deg(H) is the degree of the only tree in H.)
    *   Time Complexity: O(K*deg(H))
    * ###CRITICAL### : you are NOT allowed to change H.
    */
    public static int[] kMin(FibonacciHeap H, int k)
    {   
    	if(k <= 0 || H.isEmpty() || k > H.size)
    		return new int[0];
    	FibonacciHeap kMin_heap = new FibonacciHeap(); //heap with potential minimum nodes 
    	HeapNode curr_min = H.min; 
    	HeapNode inserted = kMin_heap.insert(curr_min.key); //insert the first min
    	inserted.setNodeReference(curr_min);
    	HeapNode curr_min_child = curr_min.getChild();
    	int[] res = new int[k]; 
    	int index = 0;
    	while(index < k) { // add all k keys
    		curr_min = kMin_heap.min.node_reference;
    		curr_min_child = curr_min.getChild();
    		res[index] = curr_min.key;
    		for(int i=0 ; i< curr_min.rank ; i++) // add all the children of the last deleted node
    		{
    			inserted = kMin_heap.insert(curr_min_child.key);
    			inserted.setNodeReference(curr_min_child); 
    			curr_min_child = curr_min_child.next;
    		}
    		kMin_heap.deleteMin();
    		index ++;
    	}
        return res; 
    }
    
   /**
    * public class HeapNode
    * 
    * 
    *  
    */
    public static class HeapNode{

    	public int key;
    	private int rank; // number of children
    	private boolean mark;
    	private HeapNode child;
    	private HeapNode next;
    	private HeapNode prev;
    	private HeapNode parent;
    	private HeapNode node_reference; //needed for kmin
    	
    	
    	public HeapNode(int key) {
    		this.key = key;
    		this.child = null;
    		this.next = null;
    		this.prev = null;
    		this.parent = null;
    		this.rank = 0; 
    	}
    	
    	
    	public void setNodeReference(HeapNode i)
    	{
    		this.node_reference = i;
    	}
    	public HeapNode getNodeReference()
    	{
    		return this.node_reference;
    	}
    	public int getKey() 
    	{
    		return this.key;
    	}
    	
    	public void setKey(int k)
    	{
    		this.key=k;
    	}
   
    	public int getRank()
    	{
    		return this.rank;
    	}
    	public boolean getMark() 
    	{
    		return this.mark;
    	}
    	public HeapNode getChild()
    	{
    		return this.child;
    	}
    	public HeapNode getNext()
    	{
    		return this.next;
    	}
    	public HeapNode getPrev()
    	{
    		return this.prev;
    	}
    	public HeapNode getParent()
    	{
    		return this.parent;
    	}
    	
    	public void setRank(int rank)
    	{
    		this.rank = rank;
    	}
    	public void setMark(boolean mark)
    	{
    		this.mark = mark;
    	}
    	public void setChild(HeapNode child)
    	{
    		this.child = child;
    	}
    	public void setNext(HeapNode next)
    	{
    		this.next = next;
    	}
    	public void setPrev(HeapNode prev)
    	{
    		this.prev = prev;
    	}
    	public void setParent(HeapNode parent)
    	{
    		this.parent = parent;
    	}
    	
    	
    	
    }
    
}
