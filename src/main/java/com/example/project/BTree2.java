package com.example.project;

class BTree2 <T extends Comparable<T>>{
    BNodeGeneric<T> root;
    int MinDeg;

    // Constructor
    public BTree2(int deg){
        this.root = null;
        this.MinDeg = deg;
    }

    public void traverse(){
        if (root != null){
            root.traverse();
        }
    }

    // Funcion para encontrar una clave
    public BNodeGeneric<T> search(T key){
        return root == null ? null : root.search(key);
    }

    public void insert(T key){

        if (root == null){

            root = new BNodeGeneric<T>(MinDeg,true);
            root.keys.set(0,key);
            root.num = 1;
        }
        else {
        	// Cuando el nodo raíz esté lleno, el árbol crecerá 1 altura
            if (root.num == 2*MinDeg-1){
                BNodeGeneric<T> s = new BNodeGeneric<T>(MinDeg,false);
                // El antiguo nodo raíz se convierte en hijo del nuevo nodo raíz
                s.children.set(0, root);
                // Separamos el antiguo nodo raíz y damos una clave al nuevo nodo
                s.splitChild(0,root);
                // El nuevo nodo raíz tiene 2 nodos secundarios. Movemos el antiguo ahi
                int i = 0;
                if (s.keys.elementAt(0).compareTo(key)<0)
                    i++;
                s.children.elementAt(i).insertNotFull(key);
                root = s;
            }
            else
                root.insertNotFull(key);
        }
    }

    public void remove(T key){
        if (root == null){
            System.out.println("El arbol esta vacio");
            return;
        }

        root.remove(key);

        if (root.num == 0){ // Si el nodo raíz tiene 0 claves
        	// Si tiene un hijo, su primer hijo se toma como la nueva raíz,
            // De lo contrario, establezca el nodo raíz en nulo
            if (root.isLeaf)
                root = null;
            else
                root = root.children.elementAt(0);
        }
    }

    public static void main(String[] args) {

        BTree2<Integer> t = new BTree2<Integer>(2); // Un árbol B con grado mínimo 2
        t.insert(1);
        t.insert(3);
        t.insert(7);
        t.insert(10);
        t.insert(11);
        t.insert(13);
        t.insert(14);
        t.insert(15);
        t.insert(18);
        t.insert(16);
        t.insert(19);
        t.insert(24);
        t.insert(25);
        t.insert(26);
        t.insert(21);
        t.insert(4);
        t.insert(5);
        t.insert(20);
        t.insert(22);
        t.insert(2);
        t.insert(17);
        t.insert(12);
        t.insert(6);

        System.out.println("Traversal of tree constructed is");
        t.traverse();
        System.out.println();

        t.remove(6);
        System.out.println("Traversal of tree after removing 6");
        t.traverse();
        System.out.println();

        t.remove(13);
        System.out.println("Traversal of tree after removing 13");
        t.traverse();
        System.out.println();

        t.remove(7);
        System.out.println("Traversal of tree after removing 7");
        t.traverse();
        System.out.println();

        t.remove(4);
        System.out.println("Traversal of tree after removing 4");
        t.traverse();
        System.out.println();

        t.remove(2);
        System.out.println("Traversal of tree after removing 2");
        t.traverse();
        System.out.println();

        t.remove(16);
        System.out.println("Traversal of tree after removing 16");
        t.traverse();
        System.out.println();
        //INTENTANDO REMOVER ALGO QUE NO EXISTE
        t.remove(1000);
        //INSERTAR DUPLICADO
        t.insert(1);
        System.out.println("Traversal of tree despues de insertar 1");
        t.traverse();
        System.out.println();
        
        t.remove(1);
        
        System.out.println("Traversal of tree despues de elminiar 1");
        t.traverse();
        System.out.println();
    }

}