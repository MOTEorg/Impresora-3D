/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package escanercnc;

/**
 *
 * @author FELIPE
 */
public class Matriz {
    public int filas;
    public int columnas;
    public double[][] datos;
    public String tipo;
    
    public Matriz(int m, int n, String tipo ){
    this.filas=m;
    this.columnas=n;
    this.datos=new double[filas][columnas];
    for(int i=0; i<filas; i++){
    for(int j=0; j<columnas;j++){
        this.datos[i][j]=0;
    }
    } 
    
    this.tipo=tipo;
    }
    
    
    //Obtiene un dato de la matriz
    public double ObtenerDato(int posx, int posy){
    return this.datos[posx][posy];
    }
    //Coloca un dato en la matriz
    public void ColocarDato(double dat, int posx, int posy){
    this.datos[posx][posy]=dat;
    }
    
    
    //Devuelve los datos ordenados de mayor a menor en un vector
    public int[] DevolverVectorAlturas(){
    int [] alturas=new int[this.filas*this.columnas];
    int k=0;
    int aux;
    //double mayor;
    for (int i=0; i<this.filas; i++){
    for (int j=0; j<this.columnas; j++){
    alturas[k++]=(int)this.datos[i][j];
    }
    }
    for(int l=0; l<k; l++){
    for(int m=l+1; m<k; m++){
    if(alturas[m]>alturas[l]){
        aux=alturas[l];
        alturas[l]=alturas[m];
        alturas[m]=aux;}
    }
    }
    
    return alturas;
    
    }
}
