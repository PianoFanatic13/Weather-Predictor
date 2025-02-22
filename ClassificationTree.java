import java.util.*;
import java.io.*;

public class ClassificationTree extends Classifier {


    private ClassificationNode root;

    //B: Builds the classification tree from scanner input, creating it from a preorder format,
    //   assuming it was written in that format in the scanner input, matching the format of the
    //   save method.
    //E: None
    //R: None
    //P: Assumes inputted scanner is non-null
    public ClassificationTree(Scanner sc) {
        this.root = buildTreeScanner(sc);
    }

    //B: Private helper method for the public scanner constructor. Uses recursion to continously
    //   read from the scanner file and add either intermediary or label nodes based on the input
    //   read
    //E: None
    //R: Returns a classification node corresponding to the root of the new tree 
    //P: Takes in scanner input, assumes it's non-null
    private ClassificationNode buildTreeScanner(Scanner sc) {
        if (!sc.hasNextLine()) {
            return null;
        }

        String line = sc.nextLine();

        //Create intermediary Node
        if (line.contains("Feature")) {
            
            String feature = line.split(" ")[1];
            double threshold = Double.parseDouble(sc.nextLine().split(" ")[1]);

            Split split = new Split(feature, threshold);

            ClassificationNode node = new ClassificationNode(split);
            node.left = buildTreeScanner(sc);
            node.right = buildTreeScanner(sc);

            return node;
        }

        //Create Label Node
        else {
            ClassificationNode node = new ClassificationNode(line);
            return node;
        }
    }

    //B: Constructs a new classification tree from data and their correct labels by correcting
    //   itself to be more accurate in the classification thresholds for labels
    //E: Throws IllegalArgumentException if either the two lists aren't of the size, or if they're
    //   empty
    //R: None
    //P: Takes in a list of classifiable data and a list for the resulting labels for those,
    //   assumes they're non-null
    public ClassificationTree(List<Classifiable> data, List<String> results) {
        if (data.size() != results.size() || data.isEmpty() || results.isEmpty()) {
            throw new IllegalArgumentException("Data and result lists are invalid inputs");
        }
        
        for (int i = 0; i < data.size(); i++) {
            this.root = twoListHelper(root, data.get(i), results.get(i));
        }
    }

    //B: Private helper method for the public version that used recursion to construct the 
    //   classification tree from classifiable data and it's corresponding label, which also
    //   corrects the tree by creating and altering the splits in the tree to better classify
    //   the data being fed into make the tree
    //E: None
    //R: Returns a classification node
    //P: Takes in the current classification the method is at, the classifiable data being
    //   evaluated/added and the correct label for the classifiable data
    private ClassificationNode twoListHelper(ClassificationNode curr, Classifiable input, String result) {
        if (curr == null) {
            return new ClassificationNode(input, result);
        }
        
        else if (curr.split == null) {
            
            if (curr.label.equals(result)) { //If only 1 node in tree, a label that's correct
                return curr;
            }
            else { //Create new split, since current 1 label is incorrect
                Split split = curr.data.partition(input);
                ClassificationNode node = new ClassificationNode(split);

                if (split.evaluate(input)) { //Correct label is on the left
                    node.left = new ClassificationNode(input, result);
                    node.right = curr;
                }
                else { //Correct label is on the right
                    node.right = new ClassificationNode(input, result);
                    node.left = curr;
                } 

                return node;
            }
        }
        
        //Need to traverse more
        if (curr.split.evaluate(input)) { //Go left
            curr.left = twoListHelper(curr.left, input, result);
        }
        else { //Go right
            curr.right = twoListHelper(curr.right, input, result);
        }

        return curr;
    }

    //B: Checks if data can be classified by the current classification tree if the tree has
    //   all the valid features the classifiable data has
    //E: None
    //R: Returns a boolean corresponding to whether or not the data can be classified
    //P: Takes in classifiable data, assumes it's non-null
    @Override
    public boolean canClassify(Classifiable input) {
        return canClassify(input.getFeatures(), this.root);

    }

    //B: Private helper method for the public version that uses recursion see if all the features
    //   of the tree are contained within the set of features of the classifiable data.
    //E: None
    //R: Returns a boolean corresponding to whether the data can be classified or not
    //P: Takes in a set of the features of the classifiable data, and the current node being
    //   visited
    private boolean canClassify(Set<String> features, ClassificationNode curr) {
        if (curr.split != null) {
            return features.contains(curr.split.getFeature()) && canClassify(features, curr.left);
        }
        return true;
    }

    //B: Classifies input data, if it can be classified, returning the label for the data
    //   based on the values and features of the data.
    //E: Throws IllegalArgumentException if the data can't be classified
    //R: Returns a string for the label for how the data would be classified
    //P: Takes in classifiable data, assumes it's non-null
    @Override
    public String classify(Classifiable input) {
        if (!canClassify(input)) {
            throw new IllegalArgumentException("Input data is not classifiable");
        }
        return classify(input, this.root);
    }

    //B: Private helper method for the public version that uses recursion to find the correct
    //   label for the data, based on how the data's feature values compare to that of the tree
    //E: None
    //R: Returns a string for the label of the data
    //P: Takes in classifiable data, and the ClassificationNode being visited currently
    private String classify(Classifiable input, ClassificationNode curr) {
        if (curr.split == null) { //At leaf node past last intermediary node
            return curr.label;
        }

        if (curr.split.evaluate(input)) { //Less than threshold
            return classify(input, curr.left);
        }
        else { //Greater than threshold
            return classify(input, curr.right);
        }
        
    }

    //B: Prints the information of the classification tree, including the feature and threshold
    //   of the intermediary nodes, and the labels for the leaf nodes.
    //E: None
    //R: Returns nothing
    //P: Takes in a prinststream object to be printed to, asumes non-null
    @Override
    public void save(PrintStream ps) {
        save(ps, this.root);
    }

    //B: Private helper for the public version that uses recursion to print all the nodes'
    //   info in a preorder format
    //E: None
    //R: None
    //P: Takes in a prinstream object to print to, assuming non null, and takes the current
    //   root in the tree being evaluated
    private void save(PrintStream ps, ClassificationNode curr) {
        if (curr != null) {   
            if (curr.label != null) {
                ps.println(curr.label);
            }
            else {
                ps.println(curr.split.toString());
            }

            save(ps, curr.left);
            save(ps, curr.right);
        }
    }
    
    //This class is used to represent and creates the types of nodes used in the classification
    //tree, and they can contain labels, splits, and links to classifiable pieces of data
    private static class ClassificationNode {

        public String label;
        public Split split;
        public Classifiable data;
        public ClassificationNode left, right;

        //B: Creates a new classification node, meant to be a leaf node, with a specified label
        //E: None
        //R: None
        //P: Takes in a string corressponding to the label of the node
        public ClassificationNode(String label) {
            this.label = label;
        }

        //B: Creates an intermediary node containing the inputted split
        //E: None
        //R: None
        //P: Takes in a split, assumes it's non-null
        public ClassificationNode(Split split) {
            this.split = split;
        }

        //B: Creates a node with and inputted label and classifiable data
        //E: None
        //R: None
        //P: Takes in classifiable data, assumes it's non-null, and a string for the label of
        //   the node
        public ClassificationNode(Classifiable data, String label) {
            this.data = data;
            this.label = label;
        }
    
    }
}
