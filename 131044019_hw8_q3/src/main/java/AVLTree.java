/**
 * Created by GozdeDogan on 8.05.2017.
 */
/** Self-balancing binary search tree using the algorithm defined
 *  by Adelson-Velskii and Landis.
 *  @author Koffman and Wolfgang
 */

public class AVLTree < E extends Comparable < E >> extends BinarySearchTreeWithRotate < E > {
    private boolean increase; //tree'nin yuksekligi artarsa true, artmazsa false
    private boolean decrease; //tree'nin yuksekligi azalirsa true, azalmazsa false

    /**
     * BinaryTree.Node'u extend eden inner class.
     * left, right ve root'a sahip, BinaryTree.Node'dan
     * balance'a sahip AVLNode'dan
     */
    private static class AVLNode < E > extends Node < E > {
        public static final int LEFT_HEAVY = -1;//indicate left-heavy
        public static final int BALANCED = 0; //Constant to indicate balanced
        public static final int RIGHT_HEAVY = 1; //indicate right-heavy
        private int balance; //right subtree height – left subtree height

        /**
         * @param item root'un degeri
         */
        public AVLNode(E item) {
            super(item); //BinaryTree.Node'un constructor'u
            balance = BALANCED;
        }

        /**
         * @return balance degeri ve tree'nin kendisi
         */
        public String toString() {
            return balance + ": " + super.toString();
        }
    } //The end of AVLNode class

    /**
     * Verilen elemani ekler. Daha dogrusu helper metodunu cagirir.
     * @param item eklenecek eleman
     * @return ekleme durumuna gore BinarySearchTree'den gelen addReturn degisir.
     * @throws ClassCastException item Comparable degil ise
     */
    public boolean add(E item) {
        increase = false;
        root = add( (AVLNode < E > ) root, item);
        return addReturn;
    }

    /**
     * Recursive add method.
     * @param localRoot eklenecek tree'nin root'u (subtree'nin root'u da olabiliyor.)
     * @param item eklenecek eleman
     * @return elemannin eklendigi yeni tree (subtree) return edilir. Aslýnda node return edilir.
     */
    private AVLNode < E > add(AVLNode < E > localRoot, E item) {
        if (localRoot == null) { //eger null gorduysem elemani ekleyebilecegim yere gelmisim demektir.
            addReturn = true;
            increase = true;
            System.out.println("null - add " + item.toString());
            return new AVLNode < E > (item);
        }

        if (item.compareTo(localRoot.data) == 0) { //eger tree de item'dan varsa eleman eklenemez.
            increase = false;
            addReturn = false;
            System.out.println("not add " + item.toString());
            return localRoot;
        }

        else if (item.compareTo(localRoot.data) < 0) { //item<data ise left tree'ye gidilir.

            System.out.println("add left " + item.toString());
            localRoot.left = add( (AVLNode < E > ) localRoot.left, item);

            if (increase) { //treenin yuksekligi artmis ise decrementBalance yapar.
                decrementBalance(localRoot);
                if (localRoot.balance < AVLNode.LEFT_HEAVY) {
                    //increase = false;
                    return rebalanceLeft(localRoot);
                }
            }
            return localRoot;
        }
        else { //item > data
            System.out.println("add right " + item.toString());
            localRoot.right = add( (AVLNode < E > ) localRoot.right, item);
            if (increase) {
                incrementBalance(localRoot);
                if (localRoot.balance > AVLNode.RIGHT_HEAVY)
                    return rebalanceRight(localRoot);
                return localRoot;
            }
            else
                return localRoot;
        }
    }

    /**
     * Delete starter method.
     * @param item - silinecek eleman
     * @return silinen eleman return edilir.
     *          eleman silinemediyse null retrun edilir.
     */
    public E delete(E item) {
        decrease = false;
        root = delete( (AVLNode < E > ) root, item);
        return deleteReturn;
    }

    /**
     * Recursive delete method.
     * @param localRoot treenin rootu
     * @param item silinecek eleman
     * @return elemanin silindigi tree'yi return eder
     */
    private AVLNode < E > delete(AVLNode < E > localRoot, E item) {
        if (localRoot == null) { // item tree de yok ise silinemez
            deleteReturn = null;
            return localRoot;
        }
        if (item.compareTo(localRoot.data) == 0) { // item tree de ise silinir ve yerine left sub tree'nin en buyugu gelir.
            deleteReturn = localRoot.data;
            return findReplacementNode(localRoot);
        }
        else if (item.compareTo(localRoot.data) < 0) { //item kucuk ise lefte bakilir.
            localRoot.left = delete( (AVLNode < E > ) localRoot.left, item);
            if (decrease) {
                incrementBalance(localRoot);
                if (localRoot.balance > AVLNode.RIGHT_HEAVY)
                    return rebalanceRightLeft((AVLNode<E>) localRoot);
                return localRoot;
            }
            else
                return localRoot;
        }
        else {// item buyuk ise right'a bakilir.
            localRoot.right = delete( (AVLNode < E > ) localRoot.right, item);
            if (decrease) {
                decrementBalance(localRoot);
                if (localRoot.balance < AVLNode.LEFT_HEAVY)
                    return rebalanceLeftRight(localRoot);
                else
                    return localRoot;
            }
            else
                return localRoot;
        }
    }

    /**
     * left sub tree'nin en buyuk elemanini bulur !
     * node ile degerlerini degidtirir.
     * @param node en kucuk elemani bulunacak ve bulunan eleman ile replace edilecek sub tree
     * @return left null ise right child return edilir.
     * right null ise left child return edilir.
     * ikisi de null degilde left sub tree'nin en buyuk elemani bulunur.
     */
    private AVLNode < E > findReplacementNode(AVLNode < E > node) {
        if (node.left == null) {
            decrease = true;
            return (AVLNode < E > ) node.right;
        }
        else if (node.right == null) {
            decrease = true;
            return (AVLNode < E > ) node.left;
        }
        else {
            if (node.left.right == null) {
                node.data = node.left.data;
                node.left = node.left.left;
                incrementBalance(node);
                return node;
            }
            else {
                node.data = findLargestChild( (AVLNode < E > ) node.left);
                if ( ( (AVLNode < E > ) node.left).balance < AVLNode.LEFT_HEAVY)
                    node.left = rebalanceLeft( (AVLNode < E > ) node.left);
                if (decrease)
                    incrementBalance(node);
                return node;
            }
        }
    }

    /**
     * verilen root'un sahip oldugu sub tree deki en buyuk elemani bulur.
     * BinarySearchTree'deki findLargestChild metodundan esinlenerek yazildi.
     * @param parent - root (parent)
     * @return bulunan node return edilir.
     *          null return edilme ihtimali yok!
     */
    private E findLargestChild(AVLNode < E > parent) {
        if (parent.right.right == null) {
            E returnValue = parent.right.data;
            parent.right = parent.right.left;
            decrementBalance(parent);
            return returnValue;
        }
        else {
            E returnValue = findLargestChild( (AVLNode < E > ) parent.right);
            if ( ( (AVLNode < E > ) parent.right).balance < AVLNode.LEFT_HEAVY)
                parent.right = rebalanceLeft( (AVLNode < E > ) parent.right);
            if (decrease)
                decrementBalance(parent);
            return returnValue;
        }
    }

    /**
     * gelen node'un balance degerine gore increase ve decrease degerlerini degistirir
     * @param node The AVL node whose balance is to be incremented
     */
    private void incrementBalance(AVLNode < E > node) {
        node.balance++;
        if (node.balance > AVLNode.BALANCED) { // left heavy ise
            increase = true;
            decrease = false;
        }
        else {// right heavy ise
            increase = false;
            decrease = true;
        }
    }

    /**
     * rebalanceRight
     * heavy durumuna gore rotate yapar
     * @param localRoot rotate edilecek root
     * @return rotate edilmis root
     */
    private AVLNode < E > rebalanceRight(AVLNode < E > localRoot) {
        AVLNode < E > rightChild = (AVLNode < E > ) localRoot.right;

        if (rightChild.balance < AVLNode.BALANCED) {
            AVLNode < E > rightLeftChild = (AVLNode < E > ) rightChild.left;

            if (rightLeftChild.balance > AVLNode.BALANCED) {
                rightChild.balance = AVLNode.BALANCED;
                rightLeftChild.balance = AVLNode.BALANCED;
                localRoot.balance = AVLNode.LEFT_HEAVY;
            }
            else if (rightLeftChild.balance < AVLNode.BALANCED) {
                rightChild.balance = AVLNode.RIGHT_HEAVY;
                rightLeftChild.balance = AVLNode.BALANCED;
                localRoot.balance = AVLNode.BALANCED;
            }
            else {
                rightChild.balance = AVLNode.BALANCED;
                rightLeftChild.balance = AVLNode.BALANCED;
                localRoot.balance = AVLNode.BALANCED;
            }
            increase = false;
            decrease = true;
            // right left heavy tree ise
            localRoot.right = rotateRight(rightChild);
            return (AVLNode < E > ) rotateLeft(localRoot);
        }
        else {// right heavy ise
            rightChild.balance = AVLNode.BALANCED;
            localRoot.balance = AVLNode.BALANCED;
            increase = false;
            decrease = true;

            return (AVLNode < E > ) rotateLeft(localRoot);
        }
    }

    /**
     * rebalanceLeftRight
     * @pre localRoot is the root of an AVL subtree that is
     * more than one left heavy
     * @post balance is restored and increase is set false
     * @param localRoot Root of the AVL subtree that needs rebalancing
     * @return a new localRoot
     */
    private AVLNode < E > rebalanceLeftRight(AVLNode < E > localRoot) {
        AVLNode < E > leftChild = (AVLNode < E > ) localRoot.left;
        if (leftChild.balance > AVLNode.BALANCED) {
            AVLNode < E > leftRightChild = (AVLNode < E > ) leftChild.right;

            if (leftRightChild.balance < AVLNode.BALANCED) {
                leftChild.balance = AVLNode.LEFT_HEAVY;
                leftRightChild.balance = AVLNode.BALANCED;
                localRoot.balance = AVLNode.BALANCED;
            }
            else if (leftRightChild.balance > AVLNode.BALANCED) {
                leftChild.balance = AVLNode.BALANCED;
                leftRightChild.balance = AVLNode.BALANCED;
                localRoot.balance = AVLNode.RIGHT_HEAVY;
            }
            else {
                leftChild.balance = AVLNode.BALANCED;
                localRoot.balance = AVLNode.BALANCED;
            }
            increase = false;
            decrease = true;
            //left-right heavy tree ise
            localRoot.left = rotateLeft(leftChild);
            return (AVLNode < E > ) rotateRight(localRoot);
        }
        if (leftChild.balance < AVLNode.BALANCED) {
            leftChild.balance = AVLNode.BALANCED;
            localRoot.balance = AVLNode.BALANCED;
            increase = false;
            decrease = true;
        }
        else {
            leftChild.balance = AVLNode.RIGHT_HEAVY;
            localRoot.balance = AVLNode.LEFT_HEAVY;
        }
        //left heavy tree ise
        return (AVLNode < E > ) rotateRight(localRoot);
    }

    /**
     * rebalanceRightLeft
     *
     * @param localRoot rotate yapilacak treenin root'u
     * @return rotate edilmis tree
     */
    private AVLNode < E > rebalanceRightLeft(AVLNode < E > localRoot) {
        AVLNode < E > rightChild = (AVLNode < E > ) localRoot.right;
        if (rightChild.balance < AVLNode.BALANCED) { //left heavy
            AVLNode < E > rightLeftChild = (AVLNode < E > ) rightChild.left;

            if (rightLeftChild.balance > AVLNode.BALANCED) { //right heavy  -> left right
                rightChild.balance = AVLNode.RIGHT_HEAVY;
                rightLeftChild.balance = AVLNode.BALANCED;
                localRoot.balance = AVLNode.BALANCED;
            }
            else if (rightLeftChild.balance < AVLNode.BALANCED) { //left heavy -> left left
                rightChild.balance = AVLNode.BALANCED;
                rightLeftChild.balance = AVLNode.BALANCED;
                localRoot.balance = AVLNode.LEFT_HEAVY;
            }
            else { //left heavy
                rightChild.balance = AVLNode.BALANCED;
                localRoot.balance = AVLNode.BALANCED;
            }
            increase = false;
            decrease = true;
            // right left heavy ise
            localRoot.right = rotateRight(rightChild);
            return (AVLNode < E > ) rotateLeft(localRoot);
        }
        if (rightChild.balance > AVLNode.BALANCED) { // right heavy
            rightChild.balance = AVLNode.BALANCED;
            localRoot.balance = AVLNode.BALANCED;
            increase = false;
            decrease = true;
        }
        else {
            rightChild.balance = AVLNode.LEFT_HEAVY;
            localRoot.balance = AVLNode.RIGHT_HEAVY;
        }
        // right heavy ise
        return (AVLNode < E > ) rotateLeft(localRoot);
    }

    /**
     * Method to rebalance left.
     *
     * @param localRoot rotate yapilacak sub tree'nin root'u
     * @return rotate edilmis sub tree'nin root'u
     */
    private AVLNode < E > rebalanceLeft(AVLNode < E > localRoot) {
        AVLNode < E > leftChild = (AVLNode < E > ) localRoot.left;
        if (leftChild.balance > AVLNode.BALANCED) {
            AVLNode < E > leftRightChild = (AVLNode < E > ) leftChild.right;

            if (leftRightChild.balance < AVLNode.BALANCED) {
                leftChild.balance = AVLNode.BALANCED;
                leftRightChild.balance = AVLNode.BALANCED;
                localRoot.balance = AVLNode.RIGHT_HEAVY;
            }
            else {
                leftChild.balance = AVLNode.LEFT_HEAVY;
                leftRightChild.balance = AVLNode.BALANCED;
                localRoot.balance = AVLNode.BALANCED;
            }
            //right heavy ise
            localRoot.left = rotateLeft(leftChild);
        }
        else { //Left-Left case
            leftChild.balance = AVLNode.BALANCED;
            localRoot.balance = AVLNode.BALANCED;
        }
        // left left heavy ise
        return (AVLNode < E > ) rotateRight(localRoot);
    }

    private void decrementBalance(AVLNode < E > node) {
        node.balance--;
        if (node.balance == AVLNode.BALANCED)
            increase = false;
    }
}

