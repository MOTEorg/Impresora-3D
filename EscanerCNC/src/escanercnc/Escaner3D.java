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
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.math.plot.Plot3DPanel;

/**
 *
 * @author FELIPE
 */
public class Escaner3D extends Thread {

    public CommPortIdentifier puerto;
    public SerialPort puertoSerie;
    public boolean banderaFresaArriba = false;
    public InputStream entrada;
    public OutputStream salida;
    // En mm
    public double tamaniopasosMotorX = 0.21;
    public double tamaniopasosMotorY = 0.0125;
    public double tamaniopasosMotorZ = 0.01;
    public double tamaniopasoFresadora = 2;
    //Variables de control
    public double[] posicionesx;
    public double[] posicionesy;
    public Matriz Datos;
    public int tamanioventanax;
    public int tamanioventanay;
    public double posActualx;
    public double posActualy;
    public double posActualz;
    public String datox = "";
    Dibujo3D dibujante;
    public int bandera_fin;

    public Escaner3D(int tamanioventanx, int tamanioventany, String puert) {
        this.tamanioventanax = tamanioventanx;
        this.tamanioventanay = tamanioventany;
        Enumeration listaPuertos = CommPortIdentifier.getPortIdentifiers();
        CommPortIdentifier idPuerto = null;
        while (listaPuertos.hasMoreElements()) {
            idPuerto = (CommPortIdentifier) listaPuertos.nextElement();
            if (idPuerto.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                //Para este caso, simulacion, se espera que este en el puerto COM1
                if (idPuerto.getName().equals(puert)) {
                    puerto = idPuerto;
                }
            }
        }
        //Para iniciar, el puerto no tiene que estar siendo ocupado por otro programa
        if (puerto.isCurrentlyOwned()) {
            System.out.println("No se puede abrir un puerto ya ocupado");
        } else {
            try {
                //Abre el puerto
                puertoSerie = (SerialPort) puerto.open("Escaner", 2000);
                System.out.println("Puerto " + puerto.getName() + " abierto y Pertenece a " + puerto.getCurrentOwner());

                try {
                    //Configura el puerto
                    puertoSerie.setSerialPortParams(9600,//Velocidad
                            SerialPort.DATABITS_8, //Bits por trama
                            SerialPort.STOPBITS_1, //Bits de Parada
                            SerialPort.PARITY_NONE);           //Paridad
                } catch (UnsupportedCommOperationException ex) {
                    System.out.println("comando no soportado");
                }

                //Inicia la lectura y escritura 
                entrada = puertoSerie.getInputStream();
                salida = puertoSerie.getOutputStream();
            } catch (PortInUseException ex) {
                System.out.println("Puerto ya en uso");
            } catch (IOException ex) {
                System.out.println("Error creando canales de comunicacion");
            }
            catch (NullPointerException e){
                  JOptionPane.showMessageDialog(null, "No hay dispositivos conectados en " + puert);
            }

        }

    }

    @Override
    public void run() {

        
         
      
        
        //Se coloca a los motores en la posicion de origen.
        bandera_fin = 0;
        EnviarCaracter('g');
        /*EnviarCaracter('o');
        while (!RecibirCaracter('n')) {
                System.out.println("Esperando");
            }
            System.out.println("Recibido n");
              try {
                  Thread.sleep(50);
            } catch (InterruptedException ex) {
                Logger.getLogger(Escaner3D.class.getName()).log(Level.SEVERE, null, ex);
            }*/
        System.out.println("Moviendo motor Z a posicion correcta del sensor");
       //MoverMotorZ(CalcularPasosMotor(0, 35, tamaniopasosMotorZ), 10);
         try {
                  Thread.sleep(50);
            } catch (InterruptedException ex) {
                Logger.getLogger(Escaner3D.class.getName()).log(Level.SEVERE, null, ex);
            }
        posicionesx = new double[(int) tamanioventanax * 2 + 1];
        posicionesy = new double[(int) tamanioventanay * 2 + 1];
        System.out.println("Posiciones x " + posicionesx.length);
        System.out.println("Posiciones y " + posicionesy.length);
        for (int i = 0; i < posicionesx.length; i++) {
            posicionesx[i] = 5 * i;
            System.out.println("Posiciones x"+i+" "+posicionesx[i]);
        }
        for (int i = 0; i < posicionesy.length; i++) {
            posicionesy[i] = 5 * i;
            System.out.println("Posiciones x"+i+" "+posicionesx[i]);
        }
        Datos = new Matriz(posicionesx.length, posicionesy.length, "Datos");
        //Inicia el movimiento para y
       dibujante=new Dibujo3D(); 
       //RECORDAR: DESCOMENTAR EL MOVIMIENTO DE LOS MOTORES
        for (int j = 0; j < posicionesy.length; j++) {
            EnviarCaracter('a');
            try {
                  Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(Escaner3D.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (j > 0) {
                System.out.println("Moviendo motor Y a "+posicionesy[j]);
               MoverMotorY(CalcularPasosMotor(posicionesy[j - 1], posicionesy[j], tamaniopasosMotorY), 12);//luego de la primera fila en x se mueve a la siguiente posicion en y
               
            }
            EnviarCaracter('l');
            while (!RecibirCaracter('c')) {
                System.out.println("Esperando");
            }
            System.out.println("Recicibido c");
              try {
                  Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(Escaner3D.class.getName()).log(Level.SEVERE, null, ex);
            }
            EnviarCaracter((char) posicionesx.length);
            //inicia el movimineto en x
            for (int i = 0; i < posicionesx.length - 1; i++) {
                try {
                  Thread.sleep(50);
            } catch (InterruptedException ex) {
                Logger.getLogger(Escaner3D.class.getName()).log(Level.SEVERE, null, ex);
            }
                if (i > 0) {
                   System.out.println("Moviendo motor X a "+posicionesx[i]);
                    // MoverMotorX(CalcularPasosMotor(posicionesx[i - 1], posicionesx[i], tamaniopasosMotorX), 10);//luego de cada medicion se mueve a la siguiente posicion en x
                    
                }
                
                EnviarCaracter('f');//envia f para que lean el sensor
                 
                while (!RecibirCaracter('e')) {//recibe e cuando el pic acaba de procesar los datos  
                    System.out.println("Esperando");
                    
                    
                }
                System.out.println("Recicibido e");
            }
                System.out.println("Moviendo motor X a "+posicionesx[posicionesx.length-1]);
                //MoverMotorX(CalcularPasosMotor(posicionesx[posicionesx.length - 2], posicionesx[posicionesx.length - 1], tamaniopasosMotorX), 10);
              try {
                  Thread.sleep(50);
            } catch (InterruptedException ex) {
                Logger.getLogger(Escaner3D.class.getName()).log(Level.SEVERE, null, ex);
            }
            EnviarCaracter('f');
            while (!RecibirCaracter('p')) {// En la ultima posicion el pic envia 'p'
                System.out.println("Esperando");
            }
            System.out.println("Recicbido p");

            for (int k = 0; k < posicionesx.length; k++) {
                Datos.ColocarDato(Double.valueOf(RecibirCadena(7)), k, j);//Recibe los datos y los coloca en una columna
                
            }
            
        }
        dibujante.Dibujar(posicionesx, posicionesy, Datos.datos,2.5);
        System.out.print("Escaneo finalizado");

        bandera_fin = 1;
       

    }

    public void EnviarCaracter(char car) {
        try {
            int mensaje = (int) car;
            salida.flush();
            salida.write(mensaje);
            salida.flush();
        } catch (IOException ex) {
            System.out.println("Error enviando caracter");
        }
    }

    public boolean RecibirCaracter(char car) {
        boolean recibido = false;
        int rec;
        try {
            rec = (int) entrada.read();
            while (rec != (int) car) {
                rec = (int) entrada.read();
            }
            recibido = true;

        } catch (IOException ex) {
            System.out.println("Error recibiendo caracter");
        }
        return recibido;
    }

    public String RecibirCadena(int largo) {
        String rec = "";
        byte[] b = new byte[largo];
        int b1;
        try {
            for (int i = 0; i < largo; i++) {
                b1 = entrada.read();
                if (b1 > 0) {
                    b[i] = (byte) b1;
                } else {
                    i--;
                }
            }
            rec = new String(b);
            System.out.println("Recibido " + rec);
        } catch (IOException ex) {
            System.out.println("Error recibiendo datos");
        }

        return rec;
    }

    public long CalcularPasosMotor(double posAnterior, double posNueva, double tamanioPaso) {
        long pasos;
        pasos = Math.round((posNueva - posAnterior) / tamanioPaso);
        return pasos;

    }

    public void MoverMotorX(long pasos, int delay) {
        if (pasos >= 0) {
            EnviarCaracter('X');
        } else {
            EnviarCaracter('x');
        }
        pasos = Math.abs(pasos);
        for (int i = 0; i < pasos; i++) {
            EnviarCaracter('1');
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ex) {
                System.out.println("Error moviendo motor X");
            }
        }


    }

    public void MoverMotorY(long pasos, int delay) {
        if (pasos >= 0) {
            EnviarCaracter('Y');
        } else {
            EnviarCaracter('y');
        }
        pasos = Math.abs(pasos);
        for (int i = 0; i < pasos; i++) {
            EnviarCaracter('2');
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ex) {
                System.out.println("Error moviendo motor Y");
            }


        }

    }

    public void MoverMotorZ(long pasos, int delay) {
        if (pasos >= 0) {
            EnviarCaracter('Z');
        } else {
            EnviarCaracter('z');
        }
        pasos = Math.abs(pasos);
        for (int i = 0; i < pasos; i++) {
            EnviarCaracter('3');
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ex) {
                System.out.println("Error moviendo motor Z");
            }
        }
    }
}
