
public class RBTree<keyClass extends Comparable<keyClass>, valueClass>  {
    /**
     * The root node of the RedBlack(RB) Tree
     */
    RBNode root;

    /**
     * Standard constructor to initialise an empty tree with root variables.
     */
    public RBTree()
    {
        root = null;
    }

    /**
     * Initialise a RB Tree with
     * @param key
     * @param val
     */
    public RBTree(keyClass key, valueClass val)
    {
        try {
            put(key, val);
        }
        catch (Exception e)
        {
            //placeholder exception catch
        }
    }

    /**
     * Used to add key/value pairs into an initialised RB Tree
     * @param key
     * @param val
     * @throws Exception
     */
    public void put(keyClass key, valueClass val) throws Exception
    {
        //New nodes are initialised with red color by default.
        RBNode node = new RBNode(RBColor.RED, key, val);

        //Inserted into tree using standard Binary Search Tree(BST) Insertion
        bstInsertNode(node);

        //Reordering and recoloring of nodes after insertion
        reorderAndRecolorForInsert(node);
    }

    /**
     * Used to remove a particular key, if available in the tree.
     * @param key
     * @throws Exception
     */
    public void remove(keyClass key) throws Exception
    {
        RBNode node = findKey(key);
        if(node != null) {
            deleteNode(node);
        }

    }

    /**
     * Use the function to delete a given node, helper function to "remove function"
     * @param node
     * @throws Exception
     */
    public void deleteNode(RBNode node) throws Exception
    {
        RBDirection nodeDirection = node.getDirectionFromParent();
        RBNode leftNode = node.getLeftNode();
        RBNode rightNode = node.getRightNode();
        RBNode parentNode = node.getParentNode();
        boolean isLeftNodeNull = leftNode == null;
        boolean isRightNodeNull = rightNode == null;


        RBNode doubleBlackNode = null;

        //If both the child nodes are null:
        if(isLeftNodeNull && isRightNodeNull)
        {
            if(RBDirection.LEFT.equals(nodeDirection))
            {
                //node deletion, removing all instances:
                parentNode.setLeftNode(null);
                node.setParentNode(null);

                //if it was a black node, we would change the black height of the particular branch
                //so we create a node representing null node and pass it fro reordering.
                if(RBColor.BLACK.equals(node.getNodeColor()))
                {
                    //null node creation, we also assign it to the double black node for later reordering/recoloring
                    doubleBlackNode = new RBNode(parentNode, null, null, null);
                    //connecting it to the parent node
                    parentNode.setLeftNode(doubleBlackNode);
                }
            }
            else // if node to be deleted is a RIGHT node, mirror of LEFT
            {
                parentNode.setRightNode(null);
                if(RBColor.BLACK.equals(node.getNodeColor()))
                {
                    doubleBlackNode = new RBNode(parentNode, null, null, null);
                    parentNode.setRightNode(doubleBlackNode);
                }
            }
        }
        // if only one of the side has a child
        else if(isLeftNodeNull || isRightNodeNull)
        {
            // find which side has the child and assign the childe to tempNode for further operation
            RBNode tempNode = isLeftNodeNull ? rightNode : leftNode;
            // assign the child node to the parent node of the node to be deleted (effectively cutting it off from the tree)
            if(RBDirection.LEFT.equals(nodeDirection))
            {
                parentNode.setLeftNode(tempNode);
            }
            else
            {
                parentNode.setRightNode(tempNode);
            }
            tempNode.setParentNode(parentNode);

            // if both the node to be deleted and the child node are black nodes, a double black node is formed
            // assign it to the doubleBlackNode variable for further reordering and recoloring
            if(RBColor.BLACK.equals(tempNode.getNodeColor()) && RBColor.BLACK.equals(node.getNodeColor()))
            {
                doubleBlackNode = tempNode;
            }
            else // else, if only one of the nodes is BLACK, just make sure the child node is BLACK
            {
                tempNode.setColor(RBColor.BLACK);
            }
        }
        else // if the node to be deleted has both the children as not null.
        {
            // find the inorder successor of the node
            RBNode inOrderSuccessorNode = inOrderSuccessor(node);

            // we replace the values of the node to be deleted with the values of the in order successor
            inOrderSuccessorNode.copyTo(node);

            // then delete the actual inorder successor node
            deleteNode(inOrderSuccessorNode);
        }

        // if the doubleBlackNode is assigned a value, we still have recolouring and reordering to do.
        if(doubleBlackNode != null)
        {
            reorderAndRecolorForDelete(doubleBlackNode);
        }

    }

    /**
     * The helper function to reorder and recolor the RB tree once a node is deleted and double black node exists.
     * @param doubleBlacknode
     * @throws Exception
     */
    private void reorderAndRecolorForDelete(RBNode doubleBlacknode) throws Exception
    {
        // make sure the node passed is not root node, as root nodes can have
        if(root != null && !root.equals(doubleBlacknode))
        {
            RBNode parentNode = doubleBlacknode.getParentNode();
            RBNode siblingNode = getSiblingNode(doubleBlacknode);
            if(siblingNode != null) {
                RBColor siblingColor = siblingNode.getNodeColor();

                // if sibling was BLACK
                if (RBColor.BLACK.equals(siblingColor)) {
                    RBNode siblingRightNode = siblingNode.getRightNode();
                    RBNode siblingLeftNode = siblingNode.getLeftNode();
                    RBDirection siblingDirection = siblingNode.getDirectionFromParent();

                    // if atleast one of the children node is RED(null is considered BLACK)
                    if ((siblingLeftNode != null && RBColor.RED.equals(siblingLeftNode.getNodeColor())) ||
                            (siblingRightNode != null && RBColor.RED.equals(siblingRightNode.getNodeColor())))
                    {
                        //assign the RED child to the variable:
                        RBNode redChild = (siblingLeftNode != null && RBColor.RED.equals(siblingLeftNode.getNodeColor())) ?
                                siblingLeftNode : siblingRightNode;
                        RBDirection redChildDirection = redChild.getDirectionFromParent();

                        // LEFT LEFT case:
                        if (siblingDirection.equals(RBDirection.LEFT) && redChildDirection.equals(RBDirection.LEFT))
                        {
                            //rotate right with respect to parent node.
                            rightRotate(parentNode);

                            //assign BLACK to child node
                            redChild.setColor(RBColor.BLACK);

                            //if parent node was RED
                            if (RBColor.RED.equals(parentNode.getNodeColor())) {

                                //set parent node as BLACK
                                parentNode.setColor(RBColor.BLACK);

                                //set sibling node as RED
                                siblingNode.setColor(RBColor.RED);
                            }
                        }
                        // LEFT RIGHT case:
                        else if (siblingDirection.equals(RBDirection.LEFT) && redChildDirection.equals(RBDirection.RIGHT))
                        {
                            //left rotate with respect to the sibling node
                            leftRotate(siblingNode);

                            //right rotate with respect to the parent node
                            rightRotate(parentNode);

                            //the red child should be assigned the colour of the (sibling's) parent node
                            redChild.setColor(parentNode.getNodeColor());

                            //assign BLACK to parent node
                            parentNode.setColor(RBColor.BLACK);
                        }
                        // RIGHT RIGHT case: (mirror of LEFT LEFT case)
                        else if (siblingDirection.equals(RBDirection.RIGHT) && redChildDirection.equals(RBDirection.RIGHT))
                        {
                            leftRotate(parentNode);
                            redChild.setColor(RBColor.BLACK);
                            if (RBColor.RED.equals(parentNode.getNodeColor())) {
                                parentNode.setColor(RBColor.BLACK);
                                siblingNode.setColor(RBColor.RED);
                            }
                        }
                        // RIGHT LEFT case: (mirror of RIGHT LEFT case)
                        else
                        {
                            rightRotate(siblingNode);
                            leftRotate(parentNode);
                            redChild.setColor(parentNode.getNodeColor());
                            parentNode.setColor(RBColor.BLACK);
                        }
                    }
                    // no red children
                    else
                    {
                        // assign RED to the sibling node
                        siblingNode.setColor(RBColor.RED);

                        // if parent node is RED
                        if (RBColor.RED.equals(parentNode.getNodeColor()))
                        {
                            // set parent as BLACK
                            parentNode.setColor(RBColor.BLACK);
                        }
                        else
                        {
                            // remove dummy null nodes from the tree
                            if(doubleBlacknode.getNodeColor() == null)
                            {
                                if (RBDirection.LEFT.equals(doubleBlacknode.getDirectionFromParent()))
                                {
                                    parentNode.setLeftNode(null);
                                }
                                else
                                {
                                    parentNode.setRightNode(null);
                                }
                                doubleBlacknode.setParentNode(null);
                            }

                            //recur with the parent node
                            reorderAndRecolorForDelete(parentNode);
                        }
                    }
                }
                // if sibling was RED
                else
                {
                    // set parent node as RED
                    parentNode.setColor(RBColor.RED);

                    // set sibling node as BLACK
                    siblingNode.setColor(RBColor.BLACK);

                    // if sibling was parent's RIGHT child
                    if(RBDirection.RIGHT.equals(siblingNode.getDirectionFromParent()))
                    {
                        // rotate left w.r.t parent node
                        leftRotate(parentNode);
                    }
                    else // sibling was LEFT child
                    {
                        // rotate RIGHT w.r.t parent node
                        rightRotate(parentNode);
                    }

                    //recur with the double black node from start
                    reorderAndRecolorForDelete(doubleBlacknode);
                }
            }
            else // if no sibling nodes exists:
            {
                // delete the double black node if it was a dummy node created to represent a null node
                if(doubleBlacknode.getNodeColor() == null)
                {
                    disconnectNode(doubleBlacknode);
                }

                // recur the function call for its parent
                reorderAndRecolorForDelete(parentNode);
            }
        }

        //if passed node is a dummy representation of a null node, delete it
        if(doubleBlacknode.getNodeColor() == null)
        {
            disconnectNode(doubleBlacknode);
        }
    }

    /**
     * Used to disconnect a node from its parent.(helper function)
     * @param node
     */
    private void disconnectNode(RBNode node)
    {
        RBNode parentNode = node.getParentNode();
        if(parentNode != null)
        {
            if(RBDirection.LEFT.equals(node.getDirectionFromParent()))
            {
                parentNode.setLeftNode(null);
            }
            else
            {
                parentNode.setRightNode(null);
            }
            node.setParentNode(null);
        }
    }

    /**
     * returns inorder succesor of a node (null if no succesor).
     * @param node
     * @return
     */
    public RBNode inOrderSuccessor(RBNode node)
    {
        RBNode rightNode = node.getRightNode();

        // if right node exists
        if(rightNode != null)
        {
            RBNode tempNode = rightNode;

            // go left of the node until the end and save the node
            while(tempNode.getLeftNode() != null)
            {
                tempNode = tempNode.getLeftNode();
            }

            // return the left most node
            return tempNode;
        }
        // if no right node exists
        else
        {
            // then its parent is the in order successor, if its a LEFT node
            if(RBDirection.LEFT.equals(node.getDirectionFromParent()))
            {
                return node.getParentNode();
            }
            // if its a RIGHT node
            else
            {
                RBNode tempNode = node.getParentNode();

                // go up the parent node until the node is no longer a RIGHT node
                while(RBDirection.RIGHT.equals(tempNode.getDirectionFromParent()))
                {
                    tempNode = tempNode.getParentNode();

                    // if the parent node doesnt exist(it means, it is the greatest element in the tree)
                    if(tempNode == null)
                    {
                        return null;
                    }
                }

                // if the following node is a LEFT node, its parent is the in order successor
                return tempNode.getParentNode();
            }
        }
    }

    /**
     * Normal BST key search.
     * @param key
     * @return
     */
    public RBNode findKey(keyClass key)
    {
        RBNode tempNode = null;
        if(root != null) {
            tempNode = root;
            boolean keyFound = false;
            while(!keyFound)
            {
                if(tempNode == null)
                {
                    break;
                }
                int equalityStatus = key.compareTo(tempNode.getKey());
                if(equalityStatus > 0)
                {
                    tempNode = tempNode.getRightNode();
                }
                else if(equalityStatus < 0)
                {
                    tempNode = tempNode.getLeftNode();
                }
                else
                {
                    keyFound = true;
                }
            }
        }
        return tempNode;
    }

    public RBNode getRootNode()
    {
        return root;
    }

    /**
     * Normal BST insertion.
     * @param node
     * @throws Exception
     */
    private void bstInsertNode(RBNode node) throws Exception
    {
        if(root == null)
        {
            root = node;
        }
        else
        {
            boolean inserted = false;
            RBNode tempNode = root;
            while(!inserted) {
                int comparisonStatus = node.getKey().compareTo(tempNode.getKey());
                if (comparisonStatus < 0) {
                    if (tempNode.getLeftNode() == null) {
                        tempNode.setLeftNode(node);
                        node.setParentNode(tempNode);
                        inserted = true;
                    }
                    else
                    {
                        tempNode = tempNode.getLeftNode();
                    }
                }
                else if(comparisonStatus > 0)
                {
                    if(tempNode.getRightNode() == null)
                    {
                        tempNode.setRightNode(node);
                        node.setParentNode(tempNode);
                        inserted = true;
                    }
                    else
                    {
                        tempNode = tempNode.getRightNode();
                    }
                }
                else
                {
                    throw new Exception("Key already exists in tree.");
                }
            }
        }
    }

    /**
     * Reordering and recolouring we need to do after addition of a a node(passed to this method)
     * @param node
     * @throws Exception
     */
    private void reorderAndRecolorForInsert(RBNode node) throws Exception
    {
        // if the node is RED
        if(RBColor.RED.equals(node.getNodeColor()))
        {
            // if it's a root node, color BLACK
            if(root.equals(node))
            {
                node.setColor(RBColor.BLACK);
            }
            else
            {
                RBNode parentNode = node.getParentNode();
                RBColor parentColor = parentNode.getNodeColor();

                // if parent is a RED node
                if(RBColor.RED.equals(parentColor))
                {
                    RBNode uncleNode = getSiblingNode(parentNode);
                    RBColor uncleColor =  uncleNode == null? RBColor.BLACK : uncleNode.getNodeColor();
                    RBNode grandFatherNode = parentNode.getParentNode();
                    RBColor grandFatherColor = grandFatherNode.getNodeColor();

                    // if uncle is a RED node
                    if(RBColor.RED.equals(uncleColor))
                    {
                        // set parent node as BLACK
                        parentNode.setColor(RBColor.BLACK);

                        // set uncle as BLACK
                        uncleNode.setColor(RBColor.BLACK);

                        // if grandfather is not root
                        if(!grandFatherNode.equals(root))
                        {
                            //set grandfather as RED
                            grandFatherNode.setColor(RBColor.RED);

                            //recur with grandfather node
                            reorderAndRecolorForInsert(grandFatherNode);
                        }
                    }
                    //uncle is a BLACK node
                    else
                    {
                        RBDirection directionFromGToP = parentNode.getDirectionFromParent();
                        RBDirection directionFromParent = node.getDirectionFromParent();

                        // LEFT LEFT case:
                        if(RBDirection.LEFT.equals(directionFromGToP) &&
                           RBDirection.LEFT.equals(directionFromParent))
                        {
                            // right rotate w.r.t grandfather
                            rightRotate(grandFatherNode);

                            // swap parent and grandparent colour
                            parentNode.setColor(grandFatherColor);
                            grandFatherNode.setColor(parentColor);
                        }
                        // LEFT RIGHT case:
                        else if(RBDirection.LEFT.equals(directionFromGToP) &&
                                RBDirection.RIGHT.equals(directionFromParent))
                        {
                            // left rotate w.r.t parent node
                            leftRotate(parentNode);

                            // right rotate w.r.t grand father node
                            rightRotate(grandFatherNode);

                            //swap node and grandfather colour
                            RBColor nodeColor = node.getNodeColor();
                            node.setColor(grandFatherColor);
                            grandFatherNode.setColor(nodeColor);
                        }
                        // RIGHT RIGHT case: (mirror of LEFT LEFT case)
                        else if(RBDirection.RIGHT.equals(directionFromGToP) &&
                                RBDirection.RIGHT.equals(directionFromParent))
                        {
                            leftRotate(grandFatherNode);
                            parentNode.setColor(grandFatherColor);
                            grandFatherNode.setColor(parentColor);
                        }
                        // RIGHT LEFT case: (mirror of LEFT RIGHT case)
                        else
                        {
                            rightRotate(parentNode);
                            leftRotate(grandFatherNode);
                            RBColor nodeColor = node.getNodeColor();
                            node.setColor(grandFatherColor);
                            grandFatherNode.setColor(nodeColor);
                        }
                    }
                }
            }
        }
        // throw error if the new node is not RED
        else
        {
            throw new Exception("Only red nodes can be inserted.");
        }
    }

    /**
     * returns the sibling node if any, else returns null, throws error, if no parent
     * @param node
     * @return
     * @throws Exception
     */
    private RBNode getSiblingNode(RBNode node) throws Exception
    {
        RBNode parentNode = node.getParentNode();
        if(parentNode != null)
        {
            if(parentNode.getLeftNode() != null &&
               parentNode.getRightNode() != null)
            {
               if(RBDirection.RIGHT.equals(node.getDirectionFromParent()))
               {
                   return parentNode.getLeftNode();
               }
               else
               {
                   return parentNode.getRightNode();
               }
            }
            return null;
        }
        throw new Exception("Node has no parent node");
    }

    /**
     * left rotate a node w.r.t its connection with child nodes
     * @param node
     */
    private void leftRotate(RBNode node)
    {
        RBNode parentNode = node.getParentNode();
        RBDirection nodeDirection = node.getDirectionFromParent();
        RBNode pivot = node.getRightNode();
        RBNode pivotLeftNode = pivot.getLeftNode();

        node.setRightNode(pivotLeftNode);
        if(pivotLeftNode != null)
        {
            pivotLeftNode.setParentNode(node);
        }

        pivot.setLeftNode(node);
        node.setParentNode(pivot);

        pivot.setParentNode(parentNode);
        if(parentNode == null)
        {
            root = pivot;
        }
        else
        {
            if(RBDirection.LEFT.equals(nodeDirection))
            {
                parentNode.setLeftNode(pivot);
            }
            else
            {
                parentNode.setRightNode(pivot);
            }
        }
    }

    /**
     * Right rotates a node w.r.t its child nodes
     * @param node
     */
    private void rightRotate(RBNode node)
    {
        RBNode parentNode = node.getParentNode();
        RBDirection nodeDirection = node.getDirectionFromParent();
        RBNode pivot = node.getLeftNode();
        RBNode pivotRightNode = pivot.getRightNode();

        node.setLeftNode(pivotRightNode);
        if(pivotRightNode != null)
        {
            pivotRightNode.setParentNode(node);
        }

        pivot.setRightNode(node);
        node.setParentNode(pivot);

        pivot.setParentNode(parentNode);
        if(parentNode == null)
        {
            root = pivot;
        }
        else
        {
            if(RBDirection.LEFT.equals(nodeDirection))
            {
                parentNode.setLeftNode(pivot);
            }
            else
            {
                parentNode.setRightNode(pivot);
            }
        }

    }

    //Representation of a node in the tree
    public class RBNode{
        private RBColor nodeColor;
        private RBNode parentNode;
        private RBNode leftNode;
        private RBNode rightNode;
        private RBDirection directionFromParent;
        keyClass key;
        valueClass val;
        private RBNode(RBNode rbNode, RBColor nodeColor, keyClass key, valueClass val)
        {
            this.parentNode = rbNode;
            this.nodeColor = nodeColor;
            this.key = key;
            this.val = val;
        }

        private RBNode(RBColor nodeColor, keyClass key, valueClass val)
        {
            this(null, nodeColor, key, val);
        }

        private void copyTo(RBNode node)
        {
            node.setVal(getVal());
            node.setKey(getKey());
        }

        private void setVal(valueClass val)
        {
            this.val = val;
        }

        private void setKey(keyClass key)
        {
            this.key = key;
        }

        private void setParentNode(RBNode parentNode)
        {
            this.parentNode = parentNode;
        }

        private void setLeftNode(RBNode leftNode)
        {
            if(leftNode != null)
            {
                leftNode.setDirectionFromParent(RBDirection.LEFT);
            }
            this.leftNode = leftNode;
        }

        private void setRightNode(RBNode rightNode)
        {
            if(rightNode != null)
            {
                rightNode.setDirectionFromParent(RBDirection.RIGHT);
            }
            this.rightNode = rightNode;
        }

        private void setColor(RBColor nodeColor)
        {
            this.nodeColor = nodeColor;
        }

        private void setDirectionFromParent(RBDirection directionFromParent)
        {
            this.directionFromParent = directionFromParent;
        }

        public RBNode getParentNode()
        {
            return parentNode;
        }

        public RBNode getLeftNode()
        {
            return leftNode;
        }

        public RBNode getRightNode()
        {
            return rightNode;
        }

        public RBColor getNodeColor()
        {
            return nodeColor;
        }

        public keyClass getKey()
        {
            return key;
        }

        public valueClass getVal()
        {
            return val;
        }

        public RBDirection getDirectionFromParent()
        {
            return directionFromParent;
        }
    }
}