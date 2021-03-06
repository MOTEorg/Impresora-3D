char uart_rd;
char *output;
unsigned int adc_value;
float temperatura;
int temp;
char Str_temperatura[15];
char Str_temp[7];

char direccionX=1;
char direccionY=1;
char direccionZ=1;

char pasoActualX = 1;
char pasoActualY = 1;
char pasoActualZ = 1;

sbit MAX at RD0_bit;
sbit MBX at RD1_bit;
sbit MCX at RD2_bit;
sbit MDX at RD3_bit;

sbit MAY at RD4_bit;
sbit MBY at RD5_bit;
sbit MCY at RD6_bit;
sbit MDY at RD7_bit;

sbit MAZ at RC0_bit;
sbit MBZ at RC1_bit;
sbit MCZ at RC2_bit;
sbit MDZ at RC3_bit;

void motorPasoAdelanteX();
void motorPasoAtrasX();
void motorPasoAdelanteY();
void motorPasoAtrasY();
void motorPasoAdelanteZ();
void motorPasoAtrasZ();


void main() {
     TRISD=0;
     TRISB=0xFF;
     TRISC0_bit=0;
     TRISC1_bit=0;
     TRISC2_bit=0;
     TRISC3_bit=0;
     TRISC4_bit=0;
     ANSELH=0;  // Configure AN pins as digital  ANSELH = 0;
     ANSEL  = 0x01;
     PORTB=0;
     WPUB=0xFF;
     NOT_RBPU_bit=0;
     MAX=1;
     MBX=1;
     MCX=0;
     MDX=0;
     MAY=1;
     MBY=1;
     MCY=0;
     MDY=0;
     MAZ=1;
     MBZ=1;
     MCZ=0;
     MDZ=0;
     UART1_Init(9600);               // Initialize UART module at 9600 bps
                      // Wait for UART module to stabilize
     Delay_ms(200);
     UART1_Write_Text("Listo");
     UART1_Write(10);
     UART1_Write(13);
     TRISE0_bit=0;
     RE0_bit=1;
     ADC_Init();


  while (1) {                     // Endless loop
    
    if (UART1_Data_Ready()) {     // If data is received,
      
      uart_rd = UART1_Read();
      delay_ms(1);     // read the received data,
      UART1_Write(uart_rd);
      if (uart_rd=='X') direccionX=1;
      if (uart_rd=='x') direccionX=0;
      if (uart_rd=='1') {
         if(direccionX){
             motorPasoAdelanteX();
         }
         else{
             motorPasoAtrasX();
         }
      }

      if (uart_rd=='Y') direccionY=1;
      if (uart_rd=='y') direccionY=0;
      if (uart_rd=='2') {
         if(direccionY){
             motorPasoAdelanteY();
         }
         else{
             motorPasoAtrasY();
         }

       }

      if (uart_rd=='Z') direccionZ = 1;
      if (uart_rd=='z') direccionZ =0;
      if (uart_rd=='3') {
         if(direccionZ){
             motorPasoAdelanteZ();
         }
         else{
             motorPasoAtrasZ();
         }
      
      }

      if (uart_rd=='E') RC4_bit=1;
      if (uart_rd=='e') RC4_bit=0;
      if (uart_rd=='4') {
       RC4_bit=1;
       delay_ms(30);
       RC4_bit=0;
       delay_ms(30);
                         }
       if (uart_rd=='t'){
       adc_value = ADC_Read(0);
       temperatura=adc_value*(0.48875);
       temp=temperatura;
       if (FloatToStr(temperatura,Str_temperatura)==0){;
       UART1_Write_Text(Str_temperatura);
       UART1_Write(10);
       UART1_Write(13);
       }else
        UART1_Write_Text("no se convirtio");
      }
      }
      
      
       if (RB0_bit==0){
       delay_ms(100);
       UART1_Write_Text("Fin de Carrera Eje Y");
       UART1_Write_Text("i");
       UART1_Write(10);
       UART1_Write(13);
                      }

       if (RB1_bit==0){
       delay_ms(100);
       UART1_Write_Text("Fin de Carrera Eje Y");
       UART1_Write_Text("j");
       UART1_Write(10);
       UART1_Write(13);

                      }

       if (RB2_bit==0){
       delay_ms(100);
       UART1_Write_Text("Fin de Carrera Eje Z");
       UART1_Write_Text("k");
       UART1_Write(10);
       UART1_Write(13);

                      }
       if (RB3_bit==0){
       delay_ms(100);
       UART1_Write_Text("Fin de Carrera Eje Z");
       UART1_Write_Text("l");
       UART1_Write(10);
       UART1_Write(13);
                      }
       }
       }
       
void motorPasoAdelanteX(){
     pasoActualX++;
     if( pasoActualX == 5) pasoActualX=1;
     switch (pasoActualX){
        case 1:
          MAX = 1;
          MBX = 1;
          MCX = 0;
          MDX = 0;
          break;
        case 2:
          MAX = 0;
          MBX = 1;
          MCX = 1;
          MDX = 0;
          break;
        case 3:
          MAX = 0;
          MBX = 0;
          MCX = 1;
          MDX = 1;
          break;
        case 4:
          MAX = 1;
          MBX = 0;
          MCX = 0;
          MDX = 1;
          break;
     }
}

void motorPasoAtrasX(){
          pasoActualX--;
     if( pasoActualX == 0) pasoActualX=4;
     switch (pasoActualX){
        case 1:
          MAX = 1;
          MBX = 1;
          MCX = 0;
          MDX = 0;
          break;
        case 2:
          MAX = 0;
          MBX = 1;
          MCX = 1;
          MDX = 0;
          break;
        case 3:
          MAX = 0;
          MBX = 0;
          MCX = 1;
          MDX = 1;
          break;
        case 4:
          MAX = 1;
          MBX = 0;
          MCX = 0;
          MDX = 1;
          break;
     }
}

void motorPasoAdelanteY(){
     pasoActualY++;
     if( pasoActualY == 5) pasoActualY=1;
     switch (pasoActualY){
        case 1:
          MAY = 1;
          MBY = 1;
          MCY = 0;
          MDY = 0;
          break;
        case 2:
          MAY = 0;
          MBY = 1;
          MCY = 1;
          MDY = 0;
          break;
        case 3:
          MAY = 0;
          MBY = 0;
          MCY = 1;
          MDY = 1;
          break;
        case 4:
          MAY = 1;
          MBY = 0;
          MCY = 0;
          MDY = 1;
          break;
     }
}

void motorPasoAtrasY(){
     pasoActualY--;
     if( pasoActualY == 0) pasoActualY=4;
     switch (pasoActualY){
        case 1:
          MAY = 1;
          MBY = 1;
          MCY = 0;
          MDY = 0;
          break;
        case 2:
          MAY = 0;
          MBY = 1;
          MCY = 1;
          MDY = 0;
          break;
        case 3:
          MAY = 0;
          MBY = 0;
          MCY = 1;
          MDY = 1;
          break;
        case 4:
          MAY = 1;
          MBY = 0;
          MCY = 0;
          MDY = 1;
          break;
     }
}

void motorPasoAdelanteZ(){
     pasoActualZ++;
     if( pasoActualZ == 5) pasoActualZ=1;
     switch (pasoActualZ){
        case 1:
          MAZ = 1;
          MBZ = 1;
          MCZ = 0;
          MDZ = 0;
          break;
        case 2:
          MAZ = 0;
          MBZ = 1;
          MCZ = 1;
          MDZ = 0;
          break;
        case 3:
          MAZ = 0;
          MBZ = 0;
          MCZ = 1;
          MDZ = 1;
          break;
        case 4:
          MAZ = 1;
          MBZ = 0;
          MCZ = 0;
          MDZ = 1;
          break;
     }
}

void motorPasoAtrasZ(){
     pasoActualZ--;
     if( pasoActualZ == 0) pasoActualZ=4;
     switch (pasoActualZ){
        case 1:
          MAZ = 1;
          MBZ = 1;
          MCZ = 0;
          MDZ = 0;
          break;
        case 2:
          MAZ = 0;
          MBZ = 1;
          MCZ = 1;
          MDZ = 0;
          break;
        case 3:
          MAZ = 0;
          MBZ = 0;
          MCZ = 1;
          MDZ = 1;
          break;
        case 4:
          MAZ = 1;
          MBZ = 0;
          MCZ = 0;
          MDZ = 1;
          break;
     }
}