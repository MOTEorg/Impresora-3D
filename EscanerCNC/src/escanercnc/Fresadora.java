/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package escanercnc;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author FELIPE
 */
public class Fresadora extends Thread{
     public Matriz DatosObtenidos = new Matriz(4, 4, "Datos");
    public Matriz Auxiliar;
    public int[] vector;
    public double[] posicionesx;
    public double[] posicionesy;
    public double[] posicionesnuevasx;
    public double[] posicionesnuevasy;
    public CommPortIdentifier puerto;
    public SerialPort puertoSerie;
    public boolean banderaFresaArriba = false;
    public InputStream entrada;
    public OutputStream salida;
    // En mm
    public double tamaniopasosMotorX=0.21;
    public double tamaniopasosMotorY=0.0125;
    public double tamaniopasosMotorZ=0.01;
    public double tamaniopasoFresadora=2;
    public int filasauxiliar;
    public int columnasauxiliar;
    public int escalaxauxiliar;
    public int escalayauxiliar;
    public double escalax;
    public double escalay;
    //Constructor
    
    public Fresadora(String selPuerto,String nombre){
            //Enumera los puertos Encontrados    
    try {
        Enumeration listaPuertos = CommPortIdentifier.getPortIdentifiers();
        CommPortIdentifier idPuerto = null;
        while (listaPuertos.hasMoreElements()) {
            idPuerto = (CommPortIdentifier) listaPuertos.nextElement();
            if (idPuerto.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                //Para este caso, simulacion, se espera que este en el puerto COM1
                if (idPuerto.getName().equals(selPuerto)) {
                    puerto = idPuerto;
                }
            }
        }
        //Para iniciar, el puerto no tiene que estar siendo ocupado por otro programa
        if (puerto.isCurrentlyOwned()) {
            System.out.println("No se puede abrir un puerto ya ocupado");
        } else {
            
                //Abre el puerto
                puertoSerie = (SerialPort) puerto.open(nombre, 2000);
                System.out.println("Puerto " + puerto.getName() + " abierto y Pertenece a " + puerto.getCurrentOwner());
               
                try {
                    //Configura el puerto
                    puertoSerie.setSerialPortParams(9600,//Velocidad
                            SerialPort.DATABITS_8, //Bits por trama
                            SerialPort.STOPBITS_1, //Bits de Parada
                            SerialPort.PARITY_NONE);           //Paridad
                } catch (UnsupportedCommOperationException ex) {
                    
                }

                //Inicia la lectura y escritura 
                entrada = puertoSerie.getInputStream();
                salida = puertoSerie.getOutputStream();
                
        }
    } catch (PortInUseException ex) {
               
            } catch (IOException ex) {
               JOptionPane.showMessageDialog(null, "Error creando canal de comunicacion "); 
            }
    catch (NullPointerException ex) {
            JOptionPane.showMessageDialog(null, "No hay ningun dispositivo conectado en " + selPuerto);
                
            }
        
        
        
        
        //Matriz de prueba
 
        
        
    }
    public void DarDatos(Matriz datos, int[] alturas,double[] posx, double[] posy){
    this.DatosObtenidos=datos;
    this.vector=alturas;
    this.posicionesx=posx;
    this.posicionesy=posy;
    for (int i=0;i<this.vector.length;i++){
    this.vector[i]=this.vector[vector.length-1-i];
    }
    }
     @Override
    public void run(){
    //Obtiene las alturas posibles, falta hacer que no se repitan altruras
   //RECORDAR: DESCOMENTAR EL MOVIMIENTO DE LOS MOTORES 
        EnviarCaracter('g');
        System.out.println("Moviendo motores al origen ");
        EnviarCaracter('o');
        while (!RecibirCaracter('n')) {
                System.out.println("Esperando");
            }
            System.out.println("Recibido n");
              try {
                  Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(Escaner3D.class.getName()).log(Level.SEVERE, null, ex);
            }
    escalaxauxiliar= (int)(Math.round((posicionesx[1]-posicionesx[0])/tamaniopasoFresadora));
    escalayauxiliar=(int)(Math.round((posicionesy[1]-posicionesy[0])/tamaniopasoFresadora));
    filasauxiliar=escalaxauxiliar*posicionesx.length;
    columnasauxiliar=escalayauxiliar*posicionesy.length;
    
    //llena los nuevos vectoresde las posiciones
    escalax=(posicionesx[posicionesx.length-1]-posicionesx[0])/(filasauxiliar-1);
    posicionesnuevasx=new double[filasauxiliar];
    for (int i=0; i<filasauxiliar; i++){
    posicionesnuevasx[i]=i*escalax;
    System.out.println("Posicion nueva x "+i+" :"+posicionesnuevasx[i]);
    }
    escalay=(posicionesy[posicionesy.length-1]-posicionesy[0])/(columnasauxiliar-1);
    posicionesnuevasy=new double[columnasauxiliar];
    for (int i=0; i<columnasauxiliar; i++){
    posicionesnuevasy[i]=i*escalay;
    System.out.println("Posicion nueva y "+i+" :"+posicionesnuevasy[i]);
    }
    
    //Muestra cuantas filas y columnas se va a usar en la matriz auxiliar
    System.out.println("numero de filas"+filasauxiliar);
    System.out.println("numero de columnas"+columnasauxiliar);
    //Se inicia en la posicion x=0, y=0, z=zmax
    System.out.println("Posicion z actual "+vector[0]);
    System.out.println("Moviendo motor z a la maxima altura  ");
    
    //MoverMotorZ(CalcularPasosMotor(0, vector[0]+1, tamaniopasosMotorZ),10);
    
    banderaFresaArriba=true;
      try {
                  Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Escaner3D.class.getName()).log(Level.SEVERE, null, ex);
            }
    EnviarCaracter('b');
    try {
                  Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Escaner3D.class.getName()).log(Level.SEVERE, null, ex);
            }
    //Inicilaiza la matriz auxiliar, llena de 0s
    Auxiliar=new Matriz(filasauxiliar, columnasauxiliar, "Auxiliar");
    for (int k = 0; k < vector.length; k++) {
            System.out.println("Altura actual " + vector[k]);
            //Rellena la matriz auxiliar
            int m=0;
            int n=0;
            for (int i = 0; i < filasauxiliar; i++) {
                for (int j = 0; j < columnasauxiliar; j++) {
                    if (DatosObtenidos.ObtenerDato(n,m)*10 >= vector[k]) {
                        Auxiliar.ColocarDato(1, i, j); //Si es mayor o igual a la altura actual se pone un 1 para que la fresa suba
                    }
                    if(j==escalayauxiliar*(m+1)-1){m++;}
                }
                m=0;
                if(i==escalaxauxiliar*(n+1)-1){n++;}
            }
            //Se inicia el movimiento, priemro en y y luego en x
            for (int j = 0; j < filasauxiliar; j++) {
                if (j>0){// No pasa nada al principio
                if(banderaFresaArriba){
                    System.out.println("Moviendo motor Y a "+posicionesnuevasy[j]);
                    MoverMotorY(CalcularPasosMotor(posicionesnuevasy[j-1], posicionesnuevasy[j], tamaniopasosMotorY),10);//luego de la primera fila en x se mueve a la siguiente posicion en y
                }
                else
                {   System.out.println("Moviendo motor Y a "+posicionesnuevasy[j]);
                    MoverMotorY(CalcularPasosMotor(posicionesnuevasy[j-1], posicionesnuevasy[j], tamaniopasosMotorY),20);
                }
                }
                for (int i = 0; i < filasauxiliar; i++) {
                    System.out.print(Auxiliar.ObtenerDato(i, j) + "\t");
                    // En el primer dato comprueba si la fresa debe bajarse o no
                    if(j==0 && i==0 && k==0){
                        if (Auxiliar.ObtenerDato(i, j)==0){
                        System.out.println("Moviendo motor Z a "+vector[0]);    
                        MoverMotorZ(CalcularPasosMotor(vector[0]+1, vector[0], tamaniopasosMotorZ),20);
                        banderaFresaArriba=false;
                        }
                    }
                    else{// En los siguinete comprueba si se debe subir o bajar la fresa
                    if (Auxiliar.ObtenerDato(i, j) == 1) {//comprueba si el dato en la matriz auxiliar es 1, entonces alza la fresa
                        if (banderaFresaArriba == false) {
                            banderaFresaArriba = true;
                            System.out.println("Moviendo motor Z a "+vector[0]+1);
                            MoverMotorZ(CalcularPasosMotor(vector[k], vector[0]+1, tamaniopasosMotorZ),20);
                        }
                    } else {
                        if(banderaFresaArriba==true){//Si el dato es 0 y la fresa estaba alzada entonces le baja.
                        banderaFresaArriba = false;
                        System.out.println("Moviendo motor Z a "+vector[k]);
                        MoverMotorZ(CalcularPasosMotor(vector[0]+1, vector[k], tamaniopasosMotorZ),20);
                        }
                    }
                       
                   }
                    if (i<filasauxiliar-1){
                        if(banderaFresaArriba){
                            System.out.println("Moviendo motor X a "+posicionesnuevasx[i+1]);
                    MoverMotorX(CalcularPasosMotor(posicionesnuevasx[i], posicionesnuevasx[i+1], tamaniopasosMotorX),10);  
                    }
                        else
                        {
                           System.out.println("Moviendo motor X a "+posicionesnuevasx[i+1]);
                        MoverMotorX(CalcularPasosMotor(posicionesnuevasx[i], posicionesnuevasx[i+1], tamaniopasosMotorX),20);
                        }
                    }
                }
                if (banderaFresaArriba==false){
                MoverMotorZ(CalcularPasosMotor(vector[k], vector[0]+1, tamaniopasosMotorZ),20);
                banderaFresaArriba=true;
                }
                System.out.println(" ");
                EnviarCaracter('l');
                while(!RecibirCaracter('c')){
                System.out.println("Esperando");
                }
               System.out.println("Recicbido c"); 
               try {
                  Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(Escaner3D.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
        }

    this.puertoSerie.close();
    
    }
    public void EnviarCaracter(char car) {
        try {
            int mensaje = (int) car;
            salida.flush();
            salida.write(mensaje);
            salida.flush();
        } catch (IOException ex) {
           
        }
    }
         public long CalcularPasosMotor(double posAnterior, double posNueva, double tamanioPaso ) {
            long pasos;
            pasos=Math.round((posNueva-posAnterior)/tamanioPaso);
            return pasos;   
    
        }
         public void MoverMotorX(long pasos,int delay ) {
            if (pasos>=0)
            {
            EnviarCaracter('X');
            }
            else
            {
            EnviarCaracter('x');
            }
            pasos=Math.abs(pasos);
            for (int i=0; i<pasos; i++){
            EnviarCaracter('1');
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ex) {
                   
                }
            } 
            
    
        }
         public void MoverMotorY(long pasos, int delay ) {
            if (pasos>=0)
            {
            EnviarCaracter('Y');
            }
            else
            {
            EnviarCaracter('y');
            }
            pasos=Math.abs(pasos);
            for (int i=0; i<pasos; i++){
            EnviarCaracter('2');
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ex) {
                    
                }
              
    
        }
         
        }
         public void MoverMotorZ(long pasos, int delay) {
            if (pasos>=0)
            {
            EnviarCaracter('Z');
            }
            else
            {
            EnviarCaracter('z');
            }
            pasos=Math.abs(pasos);
            for (int i=0; i<pasos; i++){
            EnviarCaracter('3');
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ex) {
                    
                }
            }
              
    
        }
         public boolean RecibirCaracter(char car ){
         boolean recibido=false;
         int rec;
             try {
                  
             rec=(int)entrada.read();
             while (rec!=(int)car){
               rec=(int)entrada.read();
             }
             recibido=true;
             
         } catch (IOException ex) {
             
         }
         return recibido;
         }
}
