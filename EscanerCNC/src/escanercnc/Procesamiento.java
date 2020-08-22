/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package escanercnc;
import javax.swing.JFrame;
import org.math.plot.Plot3DPanel;

/**
 *
 * @author Felipe
 */
public class Procesamiento {
    public Matriz matriz_alturas, matriz_procesada, matriz_salida;
    public int filas, columnas;
    public int[] vector_capas;
    
    public Procesamiento(Matriz matriz_entrada){
    this.matriz_alturas=matriz_entrada;
    this.filas=matriz_entrada.filas;
    this.columnas=matriz_entrada.columnas;
    }
    
    public Matriz RetornarSuavizada(int puntos_interpolacion){
        this.Suavizar(puntos_interpolacion);
        return this.matriz_procesada; 
    }
    
    public Matriz RetornarRigurizada(int error, int altura ){
        this.Rigurizar(error, altura);
        return this.matriz_salida; 
    }
    
    public Matriz RetornarProcesadaTotal(int puntos_interpolacion,int error, int altura){
        this.Suavizar(puntos_interpolacion);
         this.Rigurizar(error, altura);
          return this.matriz_salida; 
    }
    
    public void Dibujar(double[] x, double[] y, double[][] z, String titulo){
    
//      x[] = {0.0, 0.5, 1.0}; // x = 0.0:0.1:1.0
//	y[] = {0.0, 0.5, 1.0};// y = 0.0:0.05:1.0
//	z[] = {{1,2,2},{3,2,2},{4,5,6}};
 
	// create your PlotPanel (you can use it as a JPanel) with a legend at SOUTH
	Plot3DPanel plot = new Plot3DPanel("SOUTH");
 
	// add grid plot to the PlotPanel
	plot.addGridPlot("Dibujo", x, y, z);
		
	// put the PlotPanel in a JFrame like a JPanel
	JFrame frame = new JFrame("un panel de dibujo");
	frame.setSize(600, 600);
	frame.setContentPane(plot);
	frame.setVisible(true);
        
    
    }
    
    public void Suavizar(int puntos_interpolacion){
    matriz_procesada= new Matriz(puntos_interpolacion*filas-(puntos_interpolacion-1),puntos_interpolacion*columnas-(puntos_interpolacion-1),"Matriz de procesamiento");
                   
                   //LLENAR CON VALORES DE MATRZ ORIGINAL
                   for (int x=0; x < matriz_procesada.filas; x++) {
                        for (int y=0; y < matriz_procesada.columnas; y++) {
                            if(x%puntos_interpolacion==0 && y%puntos_interpolacion==0 ){
                              matriz_procesada.ColocarDato(matriz_alturas.ObtenerDato(x/puntos_interpolacion, y/puntos_interpolacion), x, y); //+matriz_procesada[1][1])/2;   
                              
                            }
                        }
                   }
                   
                   
                   //PROMEDIADO RECORRIENDO FILAS
                   for (int x=0; x < matriz_procesada.filas; x++) {
                       for (int y=0; y < matriz_procesada.columnas; y++) {
                           if(x%puntos_interpolacion==0 && y%puntos_interpolacion!=0){
                              matriz_procesada.ColocarDato( (matriz_procesada.ObtenerDato(x, y-1)+matriz_procesada.ObtenerDato(x,y+1))/2,x, y); 
                           }
                           System.out.println("Matriz Procesada"+ x +" "+y+" :"+ matriz_procesada.datos[x][y]);
                       }                  
                   }
                   
                   //PROMEDIADO RECORRIENDO COLUMNAS
                   for (int x=1; x < matriz_procesada.filas; x++) {
                       for (int y=0; y < matriz_procesada.columnas; y++) {
                           if(x%puntos_interpolacion!=0){
                               matriz_procesada.ColocarDato((matriz_procesada.ObtenerDato(x-1,y)+matriz_procesada.ObtenerDato(x+1, y))/2,x,y); 
                           }
                           System.out.println("Matriz Procesada"+ x +" "+y+" :"+ matriz_procesada.datos[x][y]);
                       }                  
                   }
    }
    
    public int[] Rigurizar(int error, int altura ){
        int capas = altura*10/error  +1;
                    System.out.println("Capas necesarias: "+capas);
                   
                    //VECTOR CON VALOR DE CADA CAPA
                    this.vector_capas = new int[capas];
                    for( int cap =0; cap < capas-1; cap++){
                        vector_capas[cap]= cap*error;
                        System.out.println("Capa"+ cap +" :"+vector_capas[cap]);
                    }
                    vector_capas[capas-1]=altura*10;
                    
                    //MOSTRAR EL VECTOR DE CAPAS
                    /*for (int x=0; x < vector_capas.length; x++) {
                        System.out.print (vector_capas[x]);
                        System.out.print("\t");
                    }
                    System.out.print("\n");
                    */
                    
                    matriz_salida= new Matriz(matriz_procesada.filas,matriz_procesada.columnas,"Matriz de Procesamiento");
                   
                    for (int x=0; x < matriz_salida.filas; x++) {
                       for (int y=0; y < matriz_salida.columnas; y++) {
                           int auxiliar = (int)((matriz_procesada.ObtenerDato(x, y)*1000)%(error*100));
                           int division = (int)(matriz_procesada.ObtenerDato(x, y)*1000)/(error*100);
                           
                           if(auxiliar!=0){
                               matriz_salida.ColocarDato(Redondear((double) (division*error/10.0),3), x, y); //(matriz_alturas[x][y]-auxiliar*matriz_alturas[x][y]); 
                               if( (double)(auxiliar*0.01/error) >= 0.445){
                                   matriz_salida.ColocarDato(Redondear(matriz_salida.ObtenerDato(x, y)+(error/10.0),3), x, y);
                               }
                               System.out.println("Matriz Salida"+ x +" "+y+" :"+ matriz_salida.datos[x][y]);
                           }
                           else{
                               matriz_salida.ColocarDato(matriz_procesada.ObtenerDato(x, y), x, y);
                           }
                       }                  
                   }
    return vector_capas;
    }
    
    public float Redondear(double numero,int digitos){
    
          int cifras=(int) Math.pow(10,digitos);
          return (float)Math.rint(numero*cifras)/cifras;
    }
    
    
}
