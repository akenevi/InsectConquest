package com.avilysal.insectConquest.util;

import com.avilysal.insectConquest.entities.Entity;

/**
 *   Collection type a bit like ArrayList but does not preserve the order
 *   of its entities, speedwise it is very good, especially suited for games.
 */

/* modified version Bag given in the link below to work over Entities instead of Objects.
 * http://www.java-gaming.org/topics/the-bag-fast-Entity-collection/24203/view.html
 * author: Kappa of java-gaming.org
 */

public class Bag {
   
   private Entity[] data;
   private int size = 0;
   
   /**
    * Constructs an empty Bag with an initial capacity of ten.
    *
    */
   public Bag() {
      this(10);
   }
   
   /**
     * Constructs an empty Bag with the specified initial capacity.
     *
     * @param capacity the initial capacity of Bag
     */
   public Bag(int capacity) {
      data = new Entity[capacity];
   }
   
   /** Finds and returns specified Entity removing it from the bag, if it's not present, null is returned.
    * @param o {@link Entity} to get.
    * @return {@link Entity} */
   public Entity get(Entity o){
	   int desiredEnt = o.hashCode();
	   for(int i = 0; i < size; i++){
		   if(data[i] != null && desiredEnt == data[i].hashCode()){
			   return remove(i);
		   }
	   }
	   return null;
   }
   
   /**
    * Removes the element at the specified position in this Bag.
    * does this by overwriting it was last element then removing 
    * last element
    * 
    * @param index the index of element to be removed
    * @return element that was removed from the Bag
    */
   public Entity remove(int index) {
      Entity o = data[index]; // make copy of element to remove so it can be returned
      data[index] = data[--size]; // overwrite item to remove with last element
      data[size] = null; // null last element, so gc can do its work
      return o;
   }
   
   /**
    * Removes the first occurrence of the specified element from this Bag,
     * if it is present.  If the Bag does not contain the element, it is
     * unchanged. does this by overwriting it was last element then removing 
    * last element
    * 
    * @param o element to be removed from this list, if present
     * @return <tt>true</tt> if this list contained the specified element
    */
   public boolean remove(Entity o) {
      for (int i = 0; i < size; i++) {
         if (o == data[i]) {
            data[i] = data[--size]; // overwrite item to remove with last element
            data[size] = null; // null last element, so gc can do its work
            return true;
         }
      }
      
      return false;
   }
   
   /**
     * Removes from this Bag all of its elements that are contained in the
     * specified Bag.
     *
     * @param bag Bag containing elements to be removed from this Bag
     * @return {@code true} if this Bag changed as a result of the call
     */
    public boolean removeAll(Bag bag) {
       boolean modified = false;
       
       for (int i = 0; i < bag.size(); i++) {
          Entity o1 = bag.get(i);
          
          for (int j = 0; j < size; j++) {
             Entity o2 = data[j];
             
             if (o1 == o2) {
                remove(j);
                j--;
                modified = true;
                break;
             }
          }
       }
       
       return modified;
    }
   
    /**
     * Returns the element at the specified position in Bag.
     *
     * @param  index index of the element to return
     * @return the element at the specified position in bag
     */
   public Entity get(int index) {
      return data[index];
   }

   /**
     * Returns the number of elements in this bag.
     *
     * @return the number of elements in this bag
     */
    public int size() {
       return size;
    }

    /**
     * Returns true if this list contains no elements.
     *
     * @return true if this list contains no elements
     */
    public boolean isEmpty() {
       return size == 0;
    }

    /**
     * Adds the specified element to the end of this bag.
     * if needed also increases the capacity of the bag.
     *
     * @param o element to be added to this list
     */
   public void add(Entity o) {   
      // if size greater than data capacity increase capacity
      if(size == data.length) {
         grow();
      }
      
      data[size++] = o;
   }
   
    private void grow() {
      Entity []oldData = data;
      int newCapacity = (oldData.length * 3) / 2 + 1;
      data = new Entity[newCapacity];
      System.arraycopy(oldData, 0, data, 0, oldData.length);
    }
    
   /**
     * Removes all of the elements from this bag. The bag will
     * be empty after this call returns.
     */
    public void clear() {
       // null all elements so gc can clean up
       for (int i = 0; i < size; i++) {
    	   data[i].dispose();
    	   data[i] = null;
       }

       size = 0;
    }
}


/*

findNearest(pos, maxRange)
{
  cellRange = maxRange  >> cellSizeInBits

  posCellX = pos.x >> cellSizeInBits
  posCellY = pos.y >> cellSizeInBits

  nearestEntity = null

  for( cellX = posCellX - cellRange; cellX < posCellX + cellRange; posCellX++ )
  { 
    for( cellY = posCellY - cellRange; cellY < posCellY + cellRange; posCellY++ )
    { 
      foreach entity in grid[cellX][cellY]
      {
         nearestEntity = compareAndReturnNearest(pos, entity, nearestEntity)
      }
    }
  }
  return nearestEntity
}
*/
