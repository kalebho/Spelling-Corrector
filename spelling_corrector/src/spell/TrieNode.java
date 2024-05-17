package spell;

public class TrieNode implements INode {

    private int value;
    private INode [] children;

    public TrieNode() {
        value = 0;
        children = new INode[26];
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public void incrementValue() {
        value++;
    }

    @Override
    public INode[] getChildren() {
        return children;
    }
}
