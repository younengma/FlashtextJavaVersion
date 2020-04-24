import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
/*
 * FlashTextJava - An idiomatic Java port of the Python library FlashText by Vikash Singh
 * Original Python source can be found at https://github.com/vi3k6i5/flashtext
 * Based on the Aho-Corasick algorithm (https://en.wikipedia.org/wiki/Aho%E2%80%93Corasick_algorithm)
 * This Java Version written by Youneng Ma
 */

public class KeywordProcessor {
    private int numberOfWords = 0;
    private int numberOfNodes = 1;
    private TrieNode root = new TrieNode();
    public static void main(String[] args) {
        KeywordProcessor kp = new KeywordProcessor();
        kp.addKeyword("Java");
        kp.addKeyword("java", "Java");
        kp.addKeyword("j2ee", "Java");
        kp.addKeyword("J2EE", "Java");
        kp.addKeyword("jvm", "Java");
        String text = "I love java, full of fun, jvm is complex, however j2ee is easy!";
        for (ExtractedItem item: kp.extractKeywords(text)) {
            System.out.println(item);
        }
        System.out.println("after replacement:" + kp.replace(text));

    }

    public boolean addKeyword(String word, String cleanName)
    {
        TrieNode currentNode = root;
        Character c;
        for(int i=0; i<word.length(); i++)
        {
            c = word.charAt(i);
            if(currentNode.children.get(c) == null){
                currentNode.children.put(c, new TrieNode());
                this.numberOfNodes += 1;
            }
            currentNode = currentNode.children.get(c);
        }

        currentNode.cleanName = cleanName;
        if(currentNode.eot == false){
            this.numberOfWords += 1;
            currentNode.eot = true;
            return true;
        }
        return false;
    }


    public boolean addKeyword(String word)
    {
        TrieNode currentNode = root;
        Character c;
        for(int i=0; i<word.length(); i++)
        {
            c = word.charAt(i);
            if(currentNode.children.get(c) == null){
                currentNode.children.put(c, new TrieNode());
                this.numberOfNodes += 1;
            }
            currentNode = currentNode.children.get(c);
        }

        currentNode.cleanName = word;
        // if the word does not found in the current tree, add it and update word number.
        if(currentNode.eot == false){
            this.numberOfWords += 1;
            currentNode.eot = true;
            return true;
        }
        return false;

    }

    public boolean containsKeyword(String word)
    {
        TrieNode currentNode = root;
        for(int i=0; i<word.length(); i++)
        {
            currentNode = currentNode.children.get(word.charAt(i));
            if(currentNode == null){
                return false;
            }
        }
        return currentNode.eot;
    }
    
    public boolean deleteKeyword(String word){
        TrieNode deletePoint=root, currentNode = root;;
        Character c, deleteKey= new Character('\0');
        int len = word.length(), t = 0;
        for(int i=0; i<len; i++) {
            c = word.charAt(i);
            if(currentNode.children.size() > 1)
            {
                // to delete a keyword do not affect other keywords 
                deletePoint = currentNode;
                deleteKey = c;
                t = i;
            }
            if(null == currentNode.children.get(c)){
                System.out.println("word:<" + word + ">not found in the Trie.");
                return false;
            }
            currentNode = currentNode.children.get(c);;

        }
        
        if(currentNode.eot) {
            deletePoint.children.remove(deleteKey);
            this.numberOfWords -= 1;
            this.numberOfNodes  -= len - t;
            System.out.println("word: <" + word + "> deleted from Trie!");
            return true;
        }else
        {
            System.out.println("word: <" + word + "> not found in the Trie.");
            return false;
        }
    }

    public String replace(String text){
        ArrayList<ExtractedItem> items = extractKeywords(text);
        String replacedText = "";
        int currentInd = 0;
        if(items.size()>0){
            for(int i=0; i<items.size(); i++){
                ExtractedItem item = items.get(i);
                replacedText  +=  text.substring(currentInd, item.startIndex);
                replacedText   += item.cleanName;
                currentInd = item.endIndex;
            }
            if(currentInd < text.length())
                replacedText += text.substring(currentInd);
        }
        else
            {
                System.out.println("Nothing replaced");
                replacedText = text;
            }
        return replacedText;
    }

    public  ArrayList<ExtractedItem> extractKeywords(String text){
        ArrayList<ExtractedItem> extractedItems = new ArrayList<ExtractedItem>();
        TrieNode currentNode;
        int startInd=0, endInd, currentInd, len = text.length();
        String cleanName="";
        boolean foundWord;
        while(startInd < len){
            currentInd = startInd;
            endInd = currentInd + 1;
            // search from the root
            currentNode = root;
            foundWord = false;
            while(currentInd<len) {
                //if char not in the dictionary, jump out.
                if(currentNode.children.containsKey(text.charAt(currentInd)) == false){
                    break;
                }
                else{
                    currentNode = currentNode.children.get(text.charAt(currentInd));
                    if(currentNode.eot){
                        foundWord = true;
                        endInd = currentInd + 1;
                        cleanName = currentNode.cleanName;
                    }
                    currentInd += 1;
                }
            }
            if(foundWord){
                ExtractedItem extractedItem = new ExtractedItem();
                extractedItem.startIndex = startInd;
                extractedItem.endIndex = endInd;
                extractedItem.cleanName =  cleanName;
                extractedItems.add(extractedItem);
            }
            startInd = endInd;

        }
        return extractedItems;
    }

    public int size(){
        return this.numberOfWords;
    }

    public int getNumberOfNodes(){
        return this.numberOfNodes;
    }

    class TrieNode {
        private String cleanName = null;
        private boolean eot = false;
        private Map<Character, TrieNode> children = new HashMap<Character, TrieNode>();
    }

    class ExtractedItem{
        private int startIndex;
        private int endIndex;
        private String cleanName;

        @Override
        public String toString(){
            return "{ startIndex:" + startIndex + "  endIndex: "
                    + endIndex + " cleanName:" + cleanName + "} ";
        }
        
    }
    
}
