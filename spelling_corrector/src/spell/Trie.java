package spell;

import java.util.Dictionary;

public class Trie implements ITrie {

    private INode root;
    private int wordCount;
    private int nodeCount;

    public Trie() {             //*************
        root = new TrieNode();
        wordCount = 0;
        nodeCount = 1;
    }

    @Override
    public void add(String word) {
        addHelper(word, root);
    }

    private void addHelper(String word, INode currNode) {

        if (word.isEmpty()) {
            if (currNode.getValue() < 1) {
                wordCount++;
            }
            currNode.incrementValue();
        }
        else {
            //Get children nodes
            INode [] children = currNode.getChildren();
            //Get index of first letter
            int index = word.charAt(0) - 'a';
            if (children[index] == null) {      //if node has letter in it already
                children[index] = new TrieNode();
                nodeCount++;
            }
            addHelper(word.substring(1), children[index]);
        }

    }

    @Override
    public INode find(String word) {

        return findHelper(word, root);
    }

    private INode findHelper(String word, INode currNode) {

        if (word.isEmpty()) {
            if (currNode.getValue() >= 1) {
                return currNode;
            }
            else {
                return null;
            }
        }
        else {
            //get children nodes in array
            INode [] children = currNode.getChildren();
            //get index of the char in the word
            int index = word.charAt(0) - 'a';
            if (index > 26 || index < 0) {
                return null;
            }
            if (children[index] == null) {
                return null;
            }
            return findHelper(word.substring(1), children[index]);      //**********
        }


    }


    @Override
    public int getWordCount() {
        return wordCount;
    }

    @Override
    public int getNodeCount() {
        return nodeCount;
    }

    @Override
    public String toString() {
        StringBuilder currWord = new StringBuilder();
        StringBuilder words = new StringBuilder();
        toStringHelper(root, currWord, words);
        return words.toString();
    }

    private void toStringHelper(INode node, StringBuilder currWord, StringBuilder words) {
        if (node.getValue() > 0) {
            //append word
            words.append(currWord.toString());
            words.append("\n");
        }

        //Create children array
        INode [] children = node.getChildren();

        for (int i = 0; i < children.length; i++) {
            //make child var and get child in ith position
            INode child = node.getChildren()[i];
            if (child != null) {
                char childLetter = (char)('a'+i);           //********
                //Update the current word
                currWord.append(childLetter);
                //Then recurse through until you find full word
                toStringHelper(children[i], currWord, words);
                currWord.deleteCharAt(currWord.length()-1);
            }
        }

    }

    @Override
    public boolean equals(Object o) {

        //check if o is null; if yes then return false
        if (o == null) {
            return false;
        }
        //Does this and o have same class
        if (o == this) {
            return true;
        }
        //check if the classes are the same
        if (o.getClass() != this.getClass()) {
            return false;
        }
        Trie t = (Trie)o; //make the object a trie

        //check if this and the trie have the same word count and node count
        if ((this.getWordCount() != t.getWordCount()) || (this.getNodeCount() != t.getNodeCount())) {
            return false;
        }

        return equalsHelper(this.root, t.root);
    }

    private boolean equalsHelper(INode n1, INode n2) {
        if (n1.getValue() != n2.getValue()) {
            return false;
        }
        INode [] n1Children = n1.getChildren();
        INode [] n2Children = n2.getChildren();
        for (int i = 0; i < n1Children.length; i++) {
            if ((n1Children[i] != null) && (n2Children[i] != null)) {
                //Compare those children that are both not null
                if (!equalsHelper(n1Children[i], n2Children[i])) {
                    return false;
                }
            }
            else if ((n1Children[i] != null) || (n2Children[i] != null)) {
                return false;
            }
        }
        return true;        //*********
    }



    @Override
    public int hashCode() {

        int nonNullIndex = 0;
        INode [] children = root.getChildren();
        for (int i = 0; i < children.length; i++){
            if (children[i] != null) {
                nonNullIndex = i ^ nonNullIndex;        //***********
            }
        }
        return nonNullIndex + wordCount * nodeCount;
    }

}
