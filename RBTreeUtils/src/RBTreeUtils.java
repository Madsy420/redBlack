

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class RBTreeUtils {
    private static String DUMMY_NODE_KEY = "dummyNode";
    private static String BLACK = "B";
    private static String RED = "R";
    private static String DUMMY_PRINTABLE_NODE = "  ";
    public static LinkedHashMap<Object, Object> getInOrderList(RBTree rbTree) {
        LinkedHashMap<Object, Object> sortedList = new LinkedHashMap<>();
        RBTree.RBNode root = rbTree.getRootNode();
        createInOrderSubtree(root, sortedList);
        return sortedList;
    }

    public static void createInOrderSubtree(RBTree.RBNode root, LinkedHashMap<Object, Object> sortedList)
    {
        RBTree.RBNode leftNode = root.getLeftNode();
        RBTree.RBNode rightNode = root.getRightNode();
        if(leftNode != null)
        {
            createInOrderSubtree(leftNode, sortedList);
        }
        sortedList.put(root.getKey(), root.getVal());
        if(rightNode != null)
        {
            createInOrderSubtree(rightNode, sortedList);
        }
    }

    /**
     * Works only for RBTrees with single letter string keys.
     * @param rbTree
     */
    public static void printRBTree(RBTree rbTree)
    {
        ArrayList<HashMap<Object, Integer>> coordinateList =  new ArrayList<>();
        RBTree.RBNode root = rbTree.getRootNode();
        int treeHeight = getHeightOfTree(rbTree);
        createCoordinateFortSubtree(root, coordinateList, 0, treeHeight);

        ArrayList<String> treeString = new ArrayList<>();
        int rowLength = (int)Math.pow(2,treeHeight)-1;
        int spaceWidth = 2;
        StringBuffer placeHolderSpaceRow = new StringBuffer();

        for(int i = 0; i<rowLength*spaceWidth; i++)
        {
            placeHolderSpaceRow.append(" ");
        }

        for(int row = 0; row < treeHeight; row++)
        {
            treeString.add(placeHolderSpaceRow.toString());
        }

        for(int i = 0; i < coordinateList.size(); i++)
        {
            HashMap<Object, Integer> coordinate = coordinateList.get(i);
            Object objectKey = coordinate.keySet().toArray()[0];

            String nodeString;
            int y = (int)coordinate.values().toArray()[0];
            if(objectKey instanceof String)
            {
                nodeString = DUMMY_PRINTABLE_NODE;
            }
            else
            {
                RBTree.RBNode node = (RBTree.RBNode) objectKey;
                RBColor nodeColor = node.getNodeColor();
                String colorString = RBColor.RED.equals(nodeColor)? RED: BLACK;
                String keyString = (String)node.getKey();
                nodeString = colorString + keyString;
            }

            treeString.set(y, treeString.get(y).substring(0, i*2) + nodeString + treeString.get(y).substring((i*2)+2));
        }

        for(String treeStringRow : treeString)
        {
            System.out.println(treeStringRow);
        }


    }

    private static void createCoordinateFortSubtree(RBTree.RBNode root, ArrayList<HashMap<Object, Integer>> coordinateList, int y, int treeHeight)
    {
        if(root != null) {
            RBTree.RBNode leftNode = root.getLeftNode();
            RBTree.RBNode rightNode = root.getRightNode();

            createCoordinateFortSubtree(leftNode, coordinateList, y + 1, treeHeight);

            HashMap<Object, Integer> coordinate = new HashMap<>();
            coordinate.put(root, y);
            coordinateList.add(coordinate);

            createCoordinateFortSubtree(rightNode, coordinateList, y + 1, treeHeight);
        }
        else
        {
            getBTHeightList(treeHeight-y, y, coordinateList);
        }

    }

    public static void getBTHeightList(int height, int y,
                                                     ArrayList<HashMap<Object, Integer>> coordinateList)
    {
        if(height > 0)
        {
            getBTHeightList(height - 1, y+1, coordinateList);

            HashMap<Object, Integer> coordinate = new HashMap<>();
            coordinate.put(DUMMY_NODE_KEY, y);
            coordinateList.add(coordinate);

            getBTHeightList(height - 1, y+1, coordinateList);
        }
    }

    public static int getHeightOfTree(RBTree rbTree)
    {
        RBTree.RBNode root = rbTree.getRootNode();
        if(root == null)
        {
            return 0;
        }
        else
        {
            return getDepthFromNode(root, 1);
        }
    }

    private static int getDepthFromNode(RBTree.RBNode node, int y)
    {
        RBTree.RBNode leftNode = node.getLeftNode();
        RBTree.RBNode rightNode = node.getRightNode();
        int leftTreeDepth = 0;
        int rightTreeDepth = 0;
        if(leftNode != null)
        {
            leftTreeDepth = getDepthFromNode(leftNode, y + 1);
        }
        if(rightNode != null)
        {
            rightTreeDepth = getDepthFromNode(rightNode, y + 1);
        }
        if(leftNode == null && rightNode == null)
        {
            return y;
        }
        return Math.max(leftTreeDepth, rightTreeDepth);

    }

}