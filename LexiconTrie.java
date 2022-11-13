import structure5.*;
import java.util.Iterator;
import java.util.Scanner;

public class LexiconTrie implements Lexicon {
  //holds the root node and SLL of children
  //where each child is a sub-trie
  protected LexiconNode root;
  //word count to make numWords() quick
  protected int count;

  //static variable to make it easy to avoid null pointer exceptions
  //uses '~' as a placeholder because it's a very uncommon character
  public static final LexiconNode NOT_FOUND = new LexiconNode('~', false);

  /** Constructs an empty LexiconTrie (the value of the root node is
  * '^' because it is a little-used character)
  */
  public LexiconTrie() {
    root = new LexiconNode('^', false);
    count = 0;
  }

  /**
  * determines whether `find` is in the given trie by checking each successive
  * node's children against the characters of `find`
  * if `find` is in the trie, returns the node at the end of `find`
  * (otherwise, returns NOT_FOUND)
  */
  protected LexiconNode locate(String find, LexiconNode current) {
    if (current.getChildren().size() == 0) {
      //current is a leaf
      return NOT_FOUND;
    } else if (find.length() == 0) {
      //if we recurse until the string is empty and have not yet returned,
      //a good location was not found
      return NOT_FOUND;
    }

    //child will be null if it is not in the list
    //child will return an integer if it is.
    char c = find.charAt(0);
    LexiconNode child = current.getChild(c);

    if (find.length() == 1) {
      if (child.compareTo(NOT_FOUND) != 0) {
        //if child has been found, return it
        return child;
      } else {
        return NOT_FOUND;
      }
    }

    return locate(find.substring(1), child);
  }

  /** Adds the specified word to this lexicon.
  *
  * @param word the word to add to the lexicon
  * @return true if `word` was added to the lexicon, false otherwise
  * (including if the word was already in the lexicon)
  * @pre word contains only upper and lowercase letters.
  * @post if word was not already in the lexicon,
  * word is added to the lexicon and count is incremented,
  * and true is returned.
  */
  public boolean addWord(String word) {
    //case does not matter
    word = word.toLowerCase();

    if (containsWord(word)) {
      //if the word is already in the lexicon
      return false;
    }

    addWordHelper(word, root);
    count++;
    return true;
  }

  /** searches for the proper place to add a word and, if found, adds the
  * parts of the word that are not already in the Trie
  */
  protected void addWordHelper(String str, LexiconNode current) {
    if (str.isEmpty()) {
      return;
    }

    LexiconNode child = current.getChild(str.charAt(0));

    //the current node does not have a child of the desired letter,
    //so add our word here
    if (child.compareTo(NOT_FOUND) == 0) {
      LexiconNode word = createWord(str);
      current.addChild(word);
      return;
    }
    //otherwise, keep searching
    addWordHelper(str.substring(1), child);
  }

  /** Creates and returns a trie of the given string, to be connected in
  * the place found in addWordHelper()
  */
  protected LexiconNode createWord(String str) {

    if (str.length() == 1) {
      //this node is the last character of the word, so its word flag is true
      return new LexiconNode(str.charAt(0), true);
    }
    //create a node for the first character in the given string
    LexiconNode result = new LexiconNode(str.charAt(0), false);
    //attatch the chain of the rest of the characters in the string
    LexiconNode tail = createWord(str.substring(1));
    result.addChild(tail);
    return result;
  }

  /** Given a file of words, add all the words in the file to the Trie.
  *
  * @param filename a string representing a file in
  * the same folder as the executable
  * @return the number of new words added to the Trie
  * @pre filename has one word per line, and is in the same folder as
  * the executable to be found.
  * @post all the words in filename are added to the Trie, and the number
  * of words is returned.
  */
  public int addWordsFromFile(String filename) {
    Scanner sc = new Scanner(new FileStream(filename));
    //store the count before the words are added in case the Trie is nonempty
    int preCount = count;
    while (sc.hasNextLine()) {
      addWord(sc.nextLine());
      //addWord() increments count for each word added
    }
    return count - preCount;
  }

  /** Given a word, removes the word from the lexicon by turning off its
  * "isWord" flag.
  *
  * @param word the word to be removed from the lexicon
  * @return true if word was successfully removed, false if it was not
  * contained in the lexicon
  * @post if word is in lexicon, it is removed, true is returned, and the
  * count of words is decremented; otherwise, the lexicon remains unchanged
  * and false is returned
  */
  public boolean removeWord(String word) {
    LexiconNode remove = locate(word, root);
    if (remove.compareTo(NOT_FOUND) == 0 || !remove.isCompleteWord()) {
      return false;
    }

    remove.setIsWord(false);
    count--;
    return true;
  }

  /** Returns the number of words in the lexcion
  *
  * @return count the number of words in the lexicon
  * @post the number of words in the lexicon is returned
  */
  public int numWords() {
    return count;
  }

  /** determines if the Trie contains the string that is passed and if
  * that string is a word
  *
  * @param word: the word to be found
  * @return boolean: true if word is in the trie and a complete word; false otherwise
  *
  * @post uses locate to find closest area to potential word. returns true if
  * word is marked with the isCompleteWord flag
  */
  public boolean containsWord(String word) {
    LexiconNode found = locate(word, root);
    if (found.compareTo(NOT_FOUND) == 0) {
      return false;
    }
    return found.isCompleteWord();
  }

  /** returns whether or not a prefix is contained in the Trie
  *
  * @param prefix: the prefix might be in the Trie
  * @return boolean: True if the prefix is in the Trie and connected to a word
  * @return booleanL false if the prefix is not at all in the trie, or
  * in the trie but technically removed
  *
  * @post finds out of prefix is in trie and connected to a complete word.
  */
  public boolean containsPrefix(String prefix) {
    LexiconNode found = locate(prefix, root);
    //locate can also give us the ideal location for what it is passed
    //so this double checks that found is equal to what locate returns
    if (found.compareTo(NOT_FOUND) == 0) {
      return false;
    }
    //if word flag is false, keep going until you find a word
    //if it is a word, just returns true.
    if (found.isCompleteWord()) {
      return true;
    }

    //Case: even if a word is removed, the prefix still exists in the Trie
    Vector<String> words = new Vector<String>();
    //makes an iterator of words below the prefix node
    words = makeWords(words, "", found);
    //if there are words underneath the prefix
    if (words.size() > 0) {
      //then it still is a prefix and returns true
      return true;
    }
    //if there are no words underneath, this means that this is a removed node
    //and technically should not exist in our trie.
    return false;
  }

  /** creates an iterator for the trie
  * The iterator is not a Tree iterator, but a vector iterator which was ordered in
  * pre-order traversal in the method makeWords().
  *
  * @return Iterator<String>
  * @post  Iterator<String> of a vector that contains all the completed words in the trie.
  */
  public Iterator<String> iterator() {
    Vector<String> words = new Vector<String>();
    words = makeWords(words, "", root);

    return words.iterator();
  }

  /** Recursive code that adds the words in the trie to an external vector
  *
  * @param wordList: the final vector that will hold all the complete words
  * @param str: This string will be altered throughout every for loop so that
  * each word will be included in the final Vector
  * @param current: the node holds the value of its children and notes which character to
  * add to the string next
  * @return Vector<String>: a vector containing all of the words in trie
  *
  * @post a Vector<String> is created
  */
  protected Vector<String> makeWords(Vector<String> wordList, String str, LexiconNode current) {
    //gets list of characters from the LexiconNode being passed
    SinglyLinkedList<LexiconNode> children = current.getChildren();
    //iterates through each child of SLL
    for (int i = 0; i < children.size(); i++) {
      //gets the node of the child
      LexiconNode currentChild = children.get(i);
      //gets the letter associated with that node and adds it to the passed string
      String currentWord = str + currentChild.getLetter();
      //if that specific node which was just added to the string is also a word
      //then it adds that string to the vector
      if (currentChild.isCompleteWord()) {
        wordList.add(currentWord);
      }
      //calls itself but with the updated string and the child of original passed node: current
      //this ensures that it is moving in post order traversal (down all the way and then to the left)
      makeWords(wordList, currentWord, currentChild);
    }

    //Since this recursive funtion is in a for loop, there is no need for a base case.
    //in all circumstances, wordList will be returned.
    return wordList;
  }

  /** Main method used for initial testing
  *
  * @param
  * @pre must be passed a file (ex: inputs/small.txt)
  * @post tests out many methods and will iterate through all words added
  * alphabetically
  */
  public static void main(String[] args) {
    LexiconTrie test = new LexiconTrie();
    //prints true twice
    System.out.println(test.addWord("help"));
    System.out.println(test.addWord("hold"));
    //prints false
    System.out.println(test.addWord("help"));

    Assert.pre(args.length > 0, "Usage: java LexiconTrie <filename>");

    test.addWordsFromFile(args[0]);
    System.out.println("add complete");

    test.removeWord("help");
    test.removeWord("a");

    Iterator<String> t = test.iterator();
    while (t.hasNext()) {
      System.out.println(t.next());
    }

  }

  /**
  * Opted out of implementing this method
  */
  public Set<String> suggestCorrections(String target, int maxDistance) {
    return null;
  }

  /**
  * Opted out of implementing this method
  */
  public Set<String> matchRegex(String pattern) {
    return null;
  }
}
