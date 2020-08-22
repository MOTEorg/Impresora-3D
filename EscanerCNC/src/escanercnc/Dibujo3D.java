package escanercnc;

import org.math.array.DoubleArray.*;
import org.math.plot.Plot3DPanel;                    //Libreria que nos permite obtener la grafica 3D.
import javax.swing.JFrame;                           //Libreria que nos permite crear una panel para insertar la figura de la imagen escaneada.

public class Dibujo3D {

   
    /**
     * @param args the command line arguments
     */
   double [] arrayAlturas ;        
        double [] coordenadasMediasX; 
        double [] coordenadasMediasY;
        double [][] matrizCoordenadas;
        double [][]matrizCompletaConAlturas ;
        double [][] matrizAnchoXY;
        
    public Dibujo3D() {
       

    }
        
    public void Dibujar (double[] posicionesX,double[] posicionesY,double[][] posicionesZ,double pmedio){
    arrayAlturas = obtenerArrayAlturas(posicionesZ, posicionesX, posicionesY);        
        coordenadasMediasX = calcularCoordenadasMediasX(posicionesX,pmedio); 
        coordenadasMediasY = calcularCoordenadasMediasY(posicionesY,pmedio);
        matrizCoordenadas = generarMatrizCoordenadas(coordenadasMediasX, coordenadasMediasY);
        matrizCompletaConAlturas = generarMatrizCompleta(matrizCoordenadas, arrayAlturas);
        matrizAnchoXY = calcularAnchosCoordenas(matrizCompletaConAlturas);
        
        
        Plot3DPanel grafica3D = new Plot3DPanel ("South");
        grafica3D.addHistogramPlot("Escaneado",matrizCompletaConAlturas,matrizAnchoXY);       
        JFrame frame = new JFrame("Grafica 3D ESCANEADA");
        frame.setSize(500, 500);
        frame.setContentPane(grafica3D);
        frame.setVisible(true);
    }
 //Primero convertimos la matriz de altura en un array para mayor facilidad al momentos de incluir el valor de la altura para cada coordenada (x,y) en la matriz obtenida anteriormente.
     public static double[] obtenerArrayAlturas(double [][] posZ, double [] posX, double []posY){
       int auxiliar = 0;
       double [] auxArrayAlturas = new double[(posX.length -1)*(posY.length -1)];
            for(int i = 0; i < (posX.length -1); i++){
                for(int j = 0; j < (posY.length -1); j++){
                    System.out.println("Dato: "+posZ[i][j]+"  en i: " +i+ "  en j: "+j);
                     auxArrayAlturas[auxiliar] = posZ[i][j];
                     auxiliar = auxiliar + 1;
                }
            } 
       return auxArrayAlturas;     
     }   
     
 //Metodo que me permite calcular el valor medio entres dos coordenadas X subsiguientes.    
     public static double[] calcularCoordenadasMediasX(double [] x, double aux){
 
        double[] posicionesMediaX = new double [x.length - 1];
        double variableAuxiliar = aux;                
            for(int i = 0; i < posicionesMediaX.length; i++){             
                    if(i == 0){
                        posicionesMediaX[i] = aux;
                        
                    }else{
                        variableAuxiliar = variableAuxiliar + 2*aux;
                        posicionesMediaX[i] = variableAuxiliar;
                    }
                    System.out.println("PosicionMediaX  "+ i +" "+ posicionesMediaX[i]);
            }
        return posicionesMediaX;      
     } 
  
 //Metodo que me permite calcular el valor medio entres dos coordenadas Y subsiguientes.    
     public static double[] calcularCoordenadasMediasY(double [] y,double aux){
 
        double[] posicionesMediaY = new double [y.length-1];
        double variableAuxiliar = aux;                
            for(int i = 0; i < posicionesMediaY.length; i++){             
                    if(i == 0){
                        posicionesMediaY[i] = aux;
                    }else{
                        variableAuxiliar = variableAuxiliar + 2*aux;
                        posicionesMediaY[i] = variableAuxiliar;
                    }
                    System.out.println("PosicionMediaY  "+ i +" "+ posicionesMediaY[i]);
            }
        return posicionesMediaY;      
     } 

//Metodo que me permite generar una matriz con las coordenas sobre las cuales se trabajaran las graficas considerando que la altura aun es 0.     
     public static double[][] generarMatrizCoordenadas(double [] coordenadasX, double [] coordenadasY){

        double [][] auxMatrizCoordenadas = new double [coordenadasX.length*coordenadasY.length][3];
        int variableAuxiliar1 = 0;
            for(int i = 0; i < coordenadasX.length*coordenadasY.length; i++){
                            
                        auxMatrizCoordenadas[i][0] = coordenadasX[variableAuxiliar1];
                        variableAuxiliar1 = variableAuxiliar1 + 1;
                            if(variableAuxiliar1 > coordenadasX.length - 1){
                                variableAuxiliar1 = 0;
                            }
                  System.out.println("Matriz Auxiliar "+ i +" 0 :"+ auxMatrizCoordenadas[i][0]);
            
            }
            
            
        int variableAuxiliar2 = 0;
        int auxiliar = 0;
            for(int i = 0; i < coordenadasX.length*coordenadasY.length; i++){
                if(auxiliar > coordenadasY.length-1){
                    auxiliar = 0;
                    variableAuxiliar2 = variableAuxiliar2 + 1;
                }
                        auxMatrizCoordenadas[i][1] = coordenadasY[variableAuxiliar2];
                            if(variableAuxiliar2 > coordenadasY.length - 1){
                                variableAuxiliar2 = 0;
                            }
                System.out.println("Matriz Auxiliar "+ i +" 1 :"+ auxMatrizCoordenadas[i][1]); 
                auxiliar = auxiliar + 1;
            }
        return auxMatrizCoordenadas;
     }
     
     
 //Metodo que me permite unificar las coordenas mas la altura en cada posicion.  
     public static double [][] generarMatrizCompleta(double [][] matriz, double []altura){
     
         double auxMatrizCompletaConAlturas [][] = new double [matriz.length][3];
            for(int i = 0; i < matriz.length; i++){
                for(int j = 0; j < 3; j++){
                     if(j == 2){
                         auxMatrizCompletaConAlturas[i][j] = altura[i];    
                     }else{
                         auxMatrizCompletaConAlturas[i][j] = matriz[i][j];
                     }    
                System.out.println("Matriz Completa "+ i +" "+j+" :"+ auxMatrizCompletaConAlturas[i][j]);
                }
            }
         return auxMatrizCompletaConAlturas;   
         
     }
     
     
 //Metodo para calcular los anchos correspondientes para cada punto medio.
     public static double[][] calcularAnchosCoordenas(double [][]matrizCompleta){
     
        double [][] matrizAnchoPuntos = new double [matrizCompleta.length][2];
                for(int i = 0; i < matrizCompleta.length; i++){
                    for (int j = 0; j < 2; j++){
                         matrizAnchoPuntos[i][j] = 5.0;
                    }
                }
        return matrizAnchoPuntos;   
     }
     

}
