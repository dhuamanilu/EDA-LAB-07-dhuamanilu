package com.example.project;

import java.util.Vector;
//Implemente una clase BNodeGeneric<T> donde T es un tipo genérico
public class BNodeGeneric<T extends Comparable<T>> {

    Vector<T> keys; // : la información almacenada en el nodo
    int MinDeg; // Grado mínimo de nodo de árbol B
    Vector<BNodeGeneric<T>> children; // una referencia a los hijos del nodo.

    int num; // Número de claves del nodo
    boolean isLeaf; // Verdadero cuando es un nodo hoja

    // Constructor
    public BNodeGeneric(int deg,boolean isLeaf){

        this.MinDeg = deg;
        this.isLeaf = isLeaf;
        this.keys=new Vector<T>();// El nodo tiene 2*MinDeg-1 claves como máximo
        for(int i=0;i<2*MinDeg-1;i++) {
        	keys.add(null);
        }
        this.children=new Vector<BNodeGeneric<T>>(); 
        for(int i=0;i<2*MinDeg;i++) {
        	children.add(null);
        }
        this.num = 0;
    }

    // Encuentra el primer índice mayor igual que la clave
    public int findKey(T key){

        int idx = 0;
        // Las condiciones para salir del bucle son: 
        // 1.idx == num, es decir, haber recorrido todo
        // 2. IDX < num, es decir, clave encontrada o mayor que la clave
        while (idx < num && keys.elementAt(idx).compareTo(key)<0 )
            ++idx;
        return idx;
    }


    public void remove(T key){

        int idx = findKey(key);
        if (idx < num && keys.elementAt(idx).compareTo(key)==0){ // Clave encontrada
            if (isLeaf) // Si la clave es un nodo hoja
                removeFromLeaf(idx);
            else // la clave no está en un nodo hoja
                removeFromNonLeaf(idx);
        }
        else{
            if (isLeaf){ // Si el nodo es un nodo hoja, entonces el nodo no está en el árbol B
                System.out.printf("La clave %d no existe en el árbol\n",key);
                return;
            }

            // De lo contrario, la clave a eliminar existe en el subárbol con el nodo como raíz

            // Esta bandera indica si la clave existe en el subárbol cuya raíz es el último hijo del nodo
            // Cuando idx es igual a num, se compara todo el nodo y el indicador es verdadero
            boolean flag = idx == num; 
            
            
            // Cuando el nodo secundario del nodo no está lleno, llénelo primero
            if (children.elementAt(idx).num < MinDeg) 
                fill(idx);
            
            //Si el último nodo secundario se fusionó, debe haberse fusionado con el nodo secundario
            //anterior, por lo que recurrimos al nodo secundario (idx-1).
            // De lo contrario, recurrimos al nodo secundario (idx), que ahora tiene al menos 
            //las claves del grado mínimo
            if (flag && idx > num)
                children.elementAt(idx-1).remove(key);
            else
                children.elementAt(idx).remove(key);
        }
    }

    public void removeFromLeaf(int idx){

    	// Shift from idx
        for (int i = idx +1;i < num;++i)
            keys.set(i-1,keys.elementAt(i));
        num--;
    }

    public void removeFromNonLeaf(int idx){

        T key = keys.elementAt(idx);

     
        if (children.elementAt(idx).num >= MinDeg){
            T pred = getPred(idx);
            keys.set(idx,pred);
            children.elementAt(idx).remove(pred);
        }
        // Si children[idx] tiene menos claves que MinDeg, verifique children[idx+1]
        // Si children[idx+1] tiene x lo menos el numero minimo de claves
        //en el subárbol cuya raíz es children[idx+1]
        // Entonces Encontramos el sucesor de la clave 'succ' y eliminamos
        //recursivamente succ en children[idx+1]
        else if (children.elementAt(idx+1).num >= MinDeg){
            T succ = getSucc(idx);
            keys.set(idx,succ);
            
            children.elementAt(idx+1).remove(succ);
        }
        else{
        	// Si el número de claves de children[idx] y children[idx+1] es menor que MinDeg
            // Entonces la clave y children[idx+1] se combinan en children[idx]
            // Ahora children[idx] contiene la clave 2t-1
            // Liberar children[idx+1] y eliminamos recursivamente la clave en children[idx]
            merge(idx);
            children.elementAt(idx).remove(key);
        }
    }
    // El nodo predecesor es el nodo que siempre encuentra el nodo más a la derecha del subárbol izquierdo
    public T getPred(int idx){ 

    	// Mover al nodo más a la derecha hasta llegar al nodo hoja
        BNodeGeneric<T> cur = children.elementAt(idx);
        while (!cur.isLeaf)
        	cur=cur.children.elementAt(cur.num);
        
        return cur.keys.elementAt(cur.num-1);
     
    }
    // El nodo posterior es el que esta mas a la izquierda del subarbol derecho
    public T getSucc(int idx){ 

    	// Continúe moviendo el nodo más a la izquierda desde 
    	//children[idx+1] hasta que alcance el nodo hoja
        BNodeGeneric<T> cur = children.elementAt(idx+1);
        while (!cur.isLeaf)
            cur = cur.children.elementAt(0);
        return cur.keys.elementAt(0);
    }

    // Rellenar children[idx] con claves inferiores a MinDeg
    public void fill(int idx){

    	// Si el nodo secundario anterior tiene mas claves que  MinDeg-1, tome "prestadas" de ahi
        if (idx != 0 && children.elementAt(idx-1).num >= MinDeg)
            borrowFromPrev(idx);
        // Sino si el siguiente elemento tiene mas claves que el minimo ,las tomamos prestadas de ahi
        else if (idx != num && children.elementAt(idx+1).num >= MinDeg)
            borrowFromNext(idx);
        else{
        	// Combinar children[idx] y sus hermanos
            // Si children[idx] es el último nodo hijo
            // Entonces se fusiona con el nodo secundario anterior o fusionarlo con su próximo hermano
            if (idx != num)
                merge(idx);
            else
                merge(idx-1);
        }
    }

    // Tomar prestada una clave de children[idx-1] e insertarla en children[idx]
    public void borrowFromPrev(int idx){

        BNodeGeneric<T> child = children.elementAt(idx);
        BNodeGeneric<T> sibling = children.elementAt(idx-1);

        // La última clave de children[idx-1] se desborda al nodo principal
        // El subdesbordamiento key[idx-1] del nodo principal se inserta como la primera clave en children[idx]
        // Por lo tanto, "sibling" disminuye en uno y "children" aumenta en uno
        for (int i = child.num-1; i >= 0; --i) // children[idx] move forward
            child.keys.set(i+1, child.keys.elementAt(i));

        if (!child.isLeaf){ // Mover children[idx] hacia adelante cuando no son nodos hoja
            for (int i = child.num; i >= 0; --i)
            	child.children.set(i+1,child.children.elementAt(i));
        }

        // Establecer la primera clave del nodo hijo en la clave del nodo actual [idx-1]
        child.keys.set(0, keys.elementAt(idx-1));
        
        if (!child.isLeaf) // Tomar al último hijo de "sibling" como el primer hijo de children[idx]
        	child.children.set(0,sibling.children.elementAt(sibling.num));

        // Por ultimo se mueve la última clave de "sibling" hasta la última clave del nodo actual
        keys.set(idx-1, sibling.keys.elementAt(sibling.num-1));
     
        child.num += 1;
        sibling.num -= 1;
    }

    // Esto es totalmente Simétrico con borowfromprev
    public void borrowFromNext(int idx){

        BNodeGeneric<T> child = children.elementAt(idx);
        BNodeGeneric<T> sibling = children.elementAt(idx+1);
        child.keys.set(child.num, keys.elementAt(idx));
       

        if (!child.isLeaf)
        	child.children.set(child.num+1, sibling.children.elementAt(0));
            
        keys.set(idx, sibling.keys.elementAt(0));

        for (int i = 1; i < sibling.num; ++i)
        	sibling.keys.set(i-1, sibling.keys.elementAt(i));

        if (!sibling.isLeaf){
            for (int i= 1; i <= sibling.num;++i)
            	sibling.children.set(i-1, sibling.children.elementAt(i));
        }
        child.num += 1;
        sibling.num -= 1;
    }

    // Combinar children[idx+1] con children[idx]
    public void merge(int idx){

        BNodeGeneric<T> child = children.elementAt(idx);
        BNodeGeneric<T> sibling = children.elementAt(idx+1);

        // Inserta la última clave del nodo actual en la posición MinDeg-1 del nodo hijo
        child.keys.set(MinDeg-1, keys.elementAt(idx));

        // keys: children[idx+1] copiado a children[idx]
        for (int i =0 ; i< sibling.num; ++i)
        	child.keys.set(i+MinDeg,sibling.keys.elementAt(i));

        // children: children[idx+1] copiado a children[idx]
        if (!child.isLeaf){
            for (int i = 0;i <= sibling.num; ++i)
            	child.children.set(i+MinDeg, sibling.children.elementAt(i));
        }

        // Mueva las claves hacia "adelante", no "la brecha" causada por mover keys[idx] a childrn[idx]
        for (int i = idx+1; i<num; ++i)
        	keys.set(i-1, keys.elementAt(i));
     
        // Mover el nodo hijo que corresponde hacia adelante
        for (int i = idx+2;i<=num;++i)
        	children.set(i-1, children.elementAt(i));

        child.num += sibling.num + 1;
        num--;
    }

    
    public void insertNotFull(T key){

        int i = num -1; // Inicializar i como el índice más a la derecha

        if (isLeaf){ // Si es un nodo hoja
        	// Encontramos la ubicación donde se debe insertar la nueva clave
            while (i >= 0 && keys.elementAt(i).compareTo(key)>0){
            	keys.set(i+1,keys.elementAt(i)); // Se mueven todas las claves hacia "atrás"	 
                i--;
            }
            //Se inserta
            keys.set(i+1,key);
            //Se actualiza el atributo num,pues acabamos de poner un nuevo elemetno
            num = num +1;
        }
        else{
        	// Sino entonces encontramos la ubicación del nodo hijo que debe insertarse
            while (i >= 0 && keys.elementAt(i).compareTo(key)>0)
                i--;
            if (children.elementAt(i+1).num == 2*MinDeg - 1){ //Si el nodo hijo esta lleno
            	
            	//Llamamos a la funcion que dividira
                splitChild(i+1,children.elementAt(i+1));
                // Después de dividir, la clave en el medio del nodo secundario se mueve hacia arriba
                //y el nodo hijo se divide en dos
                if (keys.elementAt(i+1).compareTo(key)<0)
                    i++;
            }
            
            children.elementAt(i+1).insertNotFull(key);
        }
    }


    public void splitChild(int i ,BNodeGeneric<T> y){

    	// Primero, creamos un nodo que tendra la clave MinDeg-1 de "y" 
    	//es decir del elemento que recibimos como parametro
        BNodeGeneric<T> z = new BNodeGeneric<T>(y.MinDeg,y.isLeaf);
        z.num = MinDeg - 1;

        // Pasamos las propiedades de "y" a z
        for (int j = 0; j < MinDeg-1; j++)
        	z.keys.set(j,y.keys.elementAt(j+MinDeg));
       
        if (!y.isLeaf){
            for (int j = 0; j < MinDeg; j++)
            	z.children.set(j, y.children.elementAt(j+MinDeg));
        }
        y.num = MinDeg-1;

        // Insertamos z en los hijos
        for (int j = num; j >= i+1; j--)
        	children.set(j+1, children.elementAt(j));
        
        children.set(i+1, z);

        // Movemos la clave en  "y"  a este nodo
        for (int j = num-1;j >= i;j--)
        	keys.set(j+1, keys.elementAt(j));
        
        keys.set(i,y.keys.elementAt(MinDeg-1));

        //Actualizamos la propiedad num 
        num = num + 1;
    }


    public void traverse(){
        int i;
        for (i = 0; i< num; i++){
        	//Si no es hoja
            if (!isLeaf)
                children.elementAt(i).traverse(); //Recursivamente llamamos a traverse 
            //Si es hoja, se imprime
            System.out.printf(" %d",keys.elementAt(i));
        }
        //Si no es hoja 
        if (!isLeaf){
            children.elementAt(i).traverse(); //Llamamos recursivamente
        }
    }


    public BNodeGeneric<T> search(T key){
        int i = 0;
        while (i < num && key.compareTo(keys.elementAt(i)) >0 )
            i++;

        if (keys.elementAt(i).compareTo(key)==0)
            return this;
        if (isLeaf)
            return null;
        return children.elementAt(i).search(key);
    }
}