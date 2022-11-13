import structure5.*;
import java.util.Iterator;

public class LexiconNode implements Comparable<LexiconNode> {
    /* single letter stored in this node */
    protected char letter;

    /* true if this node ends some path that defines a valid word */
    protected boolean isWord;

    /* SinglyLinkedList for keeping track of the children of this LexiconNode */
    protected SinglyLinkedList<LexiconNode> children;

    /* Static node to use in searches to avoid a null pointer exception
     * uses '~' because it is an uncommon character */
    public static final LexiconNode NOT_FOUND = new LexiconNode('~', false);

    /** Constructs a LexiconNode given a letter value and whether or not that
    * letter is the end of a word
    *
    * @param letter the letter value of the node
    * @param isWord flag to denote whether the letter is the end of a word
    * @post node is created with the given letter and isWord value
    * and an empty list of children
    */
    public LexiconNode(char letter, boolean isWord) {
      this.letter = letter;
      this.isWord = isWord;
      children = new SinglyLinkedList<LexiconNode>();
    }

    /** Returns the node's letter value
    * @return letter the node's letter value
    * @post letter is returned
    */
    public char getLetter() {
      return letter;
    }

    /** Returns whether or not the node is the end of a valid word
    * @return isWord the flag denoting whether the node is the end of a word
    * @post letter is returned
    */
    public boolean isCompleteWord() {
      return isWord;
    }

    /** Sets the value of the node's isWord flag
    * @param b the new value of the isWord flag
    * @post isWord's value is set to b
    */
    public void setIsWord(boolean b) {
      isWord = b;
    }

    /** Returns the node's list of child nodes
    * @return children the list of child nodes
    */
    public SinglyLinkedList<LexiconNode> getChildren() {
      return children;
    }

    /** Returns a positive number if this node's letter value is
    * later in the alphabet than `o`'s letter value.
    *
    * @param o the value that this node should be compared to
    * @return is positive if this node's letter value is later in
    * the alphabet than the value of `o`, 0 if they are equal, and
    * negative otherwise
    * @pre the value of `o` is not null
    * @post the difference between this node's and `o`'s ASCII values
    * is returned
    */
    public int compareTo(LexiconNode o) {
      int other = o.getLetter();
      return letter - other;
    }

    /** Helper method to find a location to add or remove a child
    */
    protected int locate(LexiconNode ln) {
      //if children array is empty return -1
      if (children.size() == 0) {
        return -1;
      }
      //traverses through list of children
      for (int i = 0; i < children.size(); i++) {
        LexiconNode finger = children.get(i);
        //if ln = value or is alphabetically earlier than value,
        //then this location is where we should add it, so return
        if (ln.compareTo(finger) <= 0) {
          return i;
        }
      }
      //if no appropriate location was found, return the end of the list
      return children.size();
    }

    /** Add LexiconNode child to correct position in list of children
    *
    * @param ln the child to be added to the list of children
    * @post if ln is not in children, then it is added to children
    */
    public void addChild(LexiconNode ln) {
      int i = locate(ln);
      if (i == -1) {
        //if list is empty, add node to head of list
        children.addFirst(ln);
      } else if (i == children.size()) {
        //if no approprate spot exists, add to end of list
        children.addLast(ln);
      } else if (ln.compareTo(children.get(i)) < 0) {
        //if appropriate spot exists and value is not already in that spot,
        //add value at that spot
        children.add(i, ln);
      }
      //if the value from locate = zero, then nothing is done
      //because ln is already in list of children
    }

    /** Get LexiconNode child with value 'ch' out of list of children
    *
    * @param ch the child node to be found
    * @return LexiconNode whose value is 'ch' (Child of LexiconNode being called)
    * @post passes back LexiconNode whose value is 'ch'
    * will return NOT_FOUND if LexiconNode does not contain the child.
    */
    public LexiconNode getChild(char ch) {

      LexiconNode find = new LexiconNode(ch, false);
      int index = locate(find);
      //prevents out of bound error: locate passes ideal location, not exact location
      if (index == -1 || index >= children.size()) {
        //not found
        return NOT_FOUND;

        //checks that ideal location contains the same character as 'ch'
      } else if (children.get(index).compareTo(find) == 0) {
        return children.get(index);
      }

      return NOT_FOUND;
    }

    /**  Turns off isWord flag for `ch`
    *
    * @param ch the character that isWord will be changed to false for
    * @pre preferable if passed a ch that can actually be removed.
    * Will do nothing if this is not the case.
    * @post the boolean isWord will be turned to false if the ch is included in
    * SLL of children.
    */
    public void removeChild(char ch) {
      //if child has children, do this
      LexiconNode temp = new LexiconNode(ch, false);
      int i = locate(temp);
      if (i == -1 || i == children.size()) {
        //the character is not in the list of children
        return;
      }
      LexiconNode child = getChild(ch);
      child.setIsWord(false);
    }

    /** create an Iterator that iterates over children in
    * alphabetical order
    *
    * @return Iterator<LexiconNode> that iterates over the children in alphabetical order
    * @post iterator of the SLL children is made and dispenses in alphabetical order
    */
    public Iterator<LexiconNode> iterator() {
      return children.iterator();
    }

    /** Main method used for initial testing
    *
    * @param args
    * @post tests out many methods of the LexiconNode class
    */
    public static void main(String[] args) {
      LexiconNode test = new LexiconNode('t', false);
      LexiconNode k = new LexiconNode('k', true);
      LexiconNode a = new LexiconNode('a', true);
      LexiconNode s = new LexiconNode('s', true);
      LexiconNode z = new LexiconNode('z', true);
      int compare = test.compareTo(k);
      System.out.println(compare);

      test.addChild(k);
      test.addChild(a);
      test.addChild(s);
      test.addChild(s);
      test.addChild(z);

      System.out.println("Test addChild:");
      SinglyLinkedList<LexiconNode> kids = test.getChildren();
      for (int i = 0; i < kids.size(); i++) {
        System.out.println(kids.get(i).getLetter());
      }

      System.out.println("Test getChild: 'k'");
      System.out.println(test.getChild('k').getLetter());
      test.removeChild('k');
      System.out.println("Test removeChild:");
      System.out.println(k.isCompleteWord());

      System.out.println("Test iterator:");
      Iterator<LexiconNode> it = test.iterator();
      while (it.hasNext()) {
        System.out.println(it.next());
      }
    }
}
