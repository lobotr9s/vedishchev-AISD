import java.util.*;

public class BStarTree {
    private Node root;
    private final int t; // минимальная степень
    private int operationCount;

    public BStarTree(int t) {
        this.root = new Node(true);
        this.t = t;
        this.operationCount = 0;
    }

    // Класс узла дерева
    private class Node {
        List<Integer> keys;
        List<Node> children;
        boolean isLeaf;

        Node(boolean isLeaf) {
            this.keys = new ArrayList<>();
            this.children = new ArrayList<>();
            this.isLeaf = isLeaf;
        }
    }

    // Добавление элемента
    public void insert(int key) {
        operationCount = 0;
        Node r = root;

        // Если корень заполнен более чем на 2/3
        if (r.keys.size() == (2 * t - 1)) {
            Node s = new Node(false);
            root = s;
            s.children.add(r);
            splitChild(s, 0);
            operationCount++;
            insertNonFull(s, key);
        } else {
            insertNonFull(r, key);
        }
    }

    // Вставка в незаполненный узел
    private void insertNonFull(Node node, int key) {
        operationCount++;
        int i = node.keys.size() - 1;

        if (node.isLeaf) {
            // Вставка в лист
            while (i >= 0 && key < node.keys.get(i)) {
                operationCount++;
                i--;
            }
            node.keys.add(i + 1, key);
        } else {
            // Вставка во внутренний узел
            while (i >= 0 && key < node.keys.get(i)) {
                operationCount++;
                i--;
            }
            i++;

            // Проверка на необходимость перераспределения
            if (node.children.get(i).keys.size() == (2 * t - 1)) {
                // B*-дерево: пытаемся перераспределить с соседями
                boolean redistributed = tryRedistribute(node, i);
                if (!redistributed) {
                    splitChild(node, i);
                    operationCount++;
                    if (key > node.keys.get(i)) {
                        i++;
                    }
                }
            }
            insertNonFull(node.children.get(i), key);
        }
    }

    // Перераспределение ключей между соседними узлами (особенность B*-дерева)
    private boolean tryRedistribute(Node parent, int index) {
        operationCount++;
        Node current = parent.children.get(index);

        // Попытка перераспределения с левым соседом
        if (index > 0) {
            Node leftSibling = parent.children.get(index - 1);
            if (leftSibling.keys.size() < (2 * t - 1) * 2 / 3) {
                return false;
            }

            // Перемещение ключа от родителя в текущий узел
            current.keys.add(0, parent.keys.get(index - 1));
            parent.keys.set(index - 1, leftSibling.keys.remove(leftSibling.keys.size() - 1));

            if (!leftSibling.isLeaf) {
                current.children.add(0, leftSibling.children.remove(leftSibling.children.size() - 1));
            }
            return true;
        }

        // Попытка перераспределения с правым соседом
        if (index < parent.keys.size()) {
            Node rightSibling = parent.children.get(index + 1);
            if (rightSibling.keys.size() < (2 * t - 1) * 2 / 3) {
                return false;
            }

            // Перемещение ключа от родителя в текущий узел
            current.keys.add(parent.keys.get(index));
            parent.keys.set(index, rightSibling.keys.remove(0));

            if (!rightSibling.isLeaf) {
                current.children.add(rightSibling.children.remove(0));
            }
            return true;
        }

        return false;
    }

    // Разделение дочернего узла
    private void splitChild(Node parent, int index) {
        operationCount++;
        Node child = parent.children.get(index);
        Node newChild = new Node(child.isLeaf);

        int mid = (child.keys.size() - 1) / 2;

        // Перемещение ключей и детей в новый узел
        for (int j = 0; j < child.keys.size() - mid - 1; j++) {
            operationCount++;
            newChild.keys.add(child.keys.remove(mid + 1));
        }

        if (!child.isLeaf) {
            for (int j = 0; j < child.keys.size() - mid; j++) {
                operationCount++;
                newChild.children.add(child.children.remove(mid + 1));
            }
        }

        int midKey = child.keys.remove(mid);
        parent.keys.add(index, midKey);
        parent.children.add(index + 1, newChild);
    }

    // Поиск элемента
    public boolean search(int key) {
        operationCount = 0;
        return searchInNode(root, key);
    }

    private boolean searchInNode(Node node, int key) {
        operationCount++;
        int i = 0;

        while (i < node.keys.size() && key > node.keys.get(i)) {
            operationCount++;
            i++;
        }

        if (i < node.keys.size() && key == node.keys.get(i)) {
            return true;
        }

        if (node.isLeaf) {
            return false;
        }

        return searchInNode(node.children.get(i), key);
    }

    // Удаление элемента
    public void delete(int key) {
        operationCount = 0;
        deleteKey(root, key);

        // Если корень пуст, делаем первый дочерний узел новым корнем
        if (root.keys.isEmpty() && !root.isLeaf) {
            root = root.children.get(0);
        }
    }

    private void deleteKey(Node node, int key) {
        operationCount++;
        int idx = findKey(node, key);

        if (idx < node.keys.size() && node.keys.get(idx) == key) {
            if (node.isLeaf) {
                // Случай 1: Ключ находится в листе
                node.keys.remove(idx);
            } else {
                // Случай 2: Ключ находится во внутреннем узле
                deleteFromInternalNode(node, idx);
            }
        } else {
            // Ключ отсутствует в текущем узле
            if (node.isLeaf) {
                return; // Ключ не найден
            }

            boolean isLastChild = (idx == node.keys.size());

            // Убеждаемся, что дочерний узел имеет достаточно ключей
            if (node.children.get(idx).keys.size() < t) {
                fill(node, idx);
            }

            if (isLastChild && idx > node.keys.size()) {
                deleteKey(node.children.get(idx - 1), key);
            } else {
                deleteKey(node.children.get(idx), key);
            }
        }
    }

    private int findKey(Node node, int key) {
        operationCount++;
        int idx = 0;
        while (idx < node.keys.size() && node.keys.get(idx) < key) {
            operationCount++;
            idx++;
        }
        return idx;
    }

    private void deleteFromInternalNode(Node node, int idx) {
        operationCount++;
        int key = node.keys.get(idx);

        if (node.children.get(idx).keys.size() >= t) {
            int pred = getPredecessor(node, idx);
            node.keys.set(idx, pred);
            deleteKey(node.children.get(idx), pred);
        } else if (node.children.get(idx + 1).keys.size() >= t) {
            int succ = getSuccessor(node, idx);
            node.keys.set(idx, succ);
            deleteKey(node.children.get(idx + 1), succ);
        } else {
            merge(node, idx);
            deleteKey(node.children.get(idx), key);
        }
    }

    private int getPredecessor(Node node, int idx) {
        operationCount++;
        Node current = node.children.get(idx);
        while (!current.isLeaf) {
            operationCount++;
            current = current.children.get(current.keys.size());
        }
        return current.keys.get(current.keys.size() - 1);
    }

    private int getSuccessor(Node node, int idx) {
        operationCount++;
        Node current = node.children.get(idx + 1);
        while (!current.isLeaf) {
            operationCount++;
            current = current.children.get(0);
        }
        return current.keys.get(0);
    }

    private void fill(Node node, int idx) {
        operationCount++;
        if (idx != 0 && node.children.get(idx - 1).keys.size() >= t) {
            borrowFromPrev(node, idx);
        } else if (idx != node.keys.size() && node.children.get(idx + 1).keys.size() >= t) {
            borrowFromNext(node, idx);
        } else {
            if (idx != node.keys.size()) {
                merge(node, idx);
            } else {
                merge(node, idx - 1);
            }
        }
    }

    private void borrowFromPrev(Node node, int idx) {
        operationCount++;
        Node child = node.children.get(idx);
        Node sibling = node.children.get(idx - 1);

        child.keys.add(0, node.keys.get(idx - 1));

        if (!child.isLeaf) {
            child.children.add(0, sibling.children.remove(sibling.children.size() - 1));
        }

        node.keys.set(idx - 1, sibling.keys.remove(sibling.keys.size() - 1));
    }

    private void borrowFromNext(Node node, int idx) {
        operationCount++;
        Node child = node.children.get(idx);
        Node sibling = node.children.get(idx + 1);

        child.keys.add(node.keys.get(idx));

        if (!child.isLeaf) {
            child.children.add(sibling.children.remove(0));
        }

        node.keys.set(idx, sibling.keys.remove(0));
    }

    private void merge(Node node, int idx) {
        operationCount++;
        Node child = node.children.get(idx);
        Node sibling = node.children.get(idx + 1);

        child.keys.add(node.keys.remove(idx));

        for (int i = 0; i < sibling.keys.size(); i++) {
            operationCount++;
            child.keys.add(sibling.keys.get(i));
        }

        if (!child.isLeaf) {
            for (int i = 0; i < sibling.children.size(); i++) {
                operationCount++;
                child.children.add(sibling.children.get(i));
            }
        }

        node.children.remove(idx + 1);
    }

    public int getOperationCount() {
        return operationCount;
    }
}