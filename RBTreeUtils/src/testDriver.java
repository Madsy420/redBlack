
public class testDriver {
    public static void main(String[] args)
    {
        try {
            RBTree rbTree = new RBTree();
            rbTree.put("c", 1);
            RBTreeUtils.printRBTree(rbTree);
            rbTree.put("v", 1);
            RBTreeUtils.printRBTree(rbTree);
            rbTree.put("d", 1);
            RBTreeUtils.printRBTree(rbTree);
            rbTree.put("a", 1);
            RBTreeUtils.printRBTree(rbTree);
            rbTree.put("m", 1);
            RBTreeUtils.printRBTree(rbTree);
            rbTree.put("w", 1);
            RBTreeUtils.printRBTree(rbTree);
            rbTree.put("r", 1);
            RBTreeUtils.printRBTree(rbTree);
            rbTree.put("b", 1);
            RBTreeUtils.printRBTree(rbTree);
            rbTree.put("o", 1);
            RBTreeUtils.printRBTree(rbTree);
            rbTree.put("h", 1);
            RBTreeUtils.printRBTree(rbTree);
            rbTree.put("g", 1);
            RBTreeUtils.printRBTree(rbTree);
            rbTree.put("t", 1);
            RBTreeUtils.printRBTree(rbTree);
            rbTree.remove("w");
            RBTreeUtils.printRBTree(rbTree);
            rbTree.remove("c");
            RBTreeUtils.printRBTree(rbTree);
            rbTree.remove("g");
            RBTreeUtils.printRBTree(rbTree);
            rbTree.remove("h");
            RBTreeUtils.printRBTree(rbTree);
            rbTree.remove("o");
            RBTreeUtils.printRBTree(rbTree);
        }
        catch (Exception e)
        {
            System.out.print(e);

        }
    }
}

