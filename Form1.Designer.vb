Imports System
Imports System.Threading




Public Class Form1
    Dim hilo As Thread
    Dim m As String
    Dim buf() As Byte
    Dim Puerto As String
    Dim PuertoSerie As IO.Ports.SerialPort = Nothing
    Delegate Sub Escribir_Texto(ByVal txtbox As TextBox, texto As String)
    Dim conectado As Boolean
    Dim temperatura As Double
    Dim modo As Boolean
    Private Sub Form1_Load(sender As System.Object, e As System.EventArgs) Handles MyBase.Load

        txtFichero.Text = "/pruebas.gcode"
        temperatura = 0
        modo = False
        btPosicionar.BackColor = Color.Beige
        vel = 4000

    End Sub
    Private Sub Button1_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles btLeer.Click
        If conectado = True Then
            txtEntradaPuerto.Clear()
            m = PuertoSerie.ReadExisting()
            txtEntradaPuerto.AppendText(m)
        Else
            MsgBox("No se ha conectado a ningun puerto", MsgBoxStyle.Information)
        End If


    End Sub

    Private Sub Button2_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles btCmpcomunicacion.Click
        If conectado Then
            PuertoSerie.Write("Conectado")

            m = PuertoSerie.ReadExisting()
            txtEntradaPuerto.AppendText(m)

            PuertoSerie.DiscardInBuffer()
            PuertoSerie.DiscardOutBuffer()
        Else
            MsgBox("No se ha conectado a ningun puerto", MsgBoxStyle.Information)
        End If

    End Sub

    Private Sub Button3_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles btPasoE.Click
        
        If conectado Then
            If modo Then
                MsgBox("Se encuentra en Modo Impresión, cambiar a Modo Posicionar", MsgBoxStyle.Information)
            Else
                motorEpaso(Convert.ToInt16(cmbPasosE.SelectedItem))
            End If
        Else
            MsgBox("No se ha conectado a ningun puerto", MsgBoxStyle.Information)
        End If

    End Sub

    Private Sub Button4_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles cmdArchivo.Click
        Dim oFD As New OpenFileDialog
        With oFD
            .Title = "Seleccionar fichero"
            .Filter = "Ficheros de texto (*.txt;*.gcode)|*.txt;*.gcode" & _
                      "|Todos los ficheros (*.*)|*.*"
            .FileName = txtFichero.Text
            If .ShowDialog = System.Windows.Forms.DialogResult.OK Then
                txtFichero.Text = .FileName
            End If
        End With
    End Sub


    Dim longitud As Integer
    Dim primerletra As String
    Dim segundaletra As String
    Dim car2inst As Integer
    Dim car3inst As Integer
    Public feedrate As Double
    Dim posX As Double
    Dim posY As Double
    Dim posE As Double
    Dim posZ As Double
    Dim posX0 As Double
    Dim posY0 As Double
    Dim posZ0 As Double
    Dim posE0 As Double
    Public cpX As Integer
    Public cpY As Integer
    Public cpZ As Integer
    Public cpE As Integer
    Dim numeropasosX As Integer
    Dim numeropasosY As Integer
    Dim numeropasosZ As Integer
    Dim numeropasosE As Integer
    Public pasosX As Integer
    Public pasosY As Integer
    Public pasosZ As Integer
    Public pasosE As Integer
    Dim distanciapasoX As Double
    Dim distanciapasoY As Double
    Dim distanciapasoZ As Double
    Dim distanciapasoE As Double
    Dim salidas As String
    Public wait As Boolean
    Public vel As Integer
    Public contpasos As Integer
    Public contpasos1 As Integer
    Public pasosrestantes As Integer
    Public menor As Boolean
    Private Sub Button5_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles cmdImprimir.Click
        If conectado = True Then
            If modo Then
                If temperatura = 0 Then
                    MsgBox("Por seguridad realize primero una lectura de la temperatura del extrusor", MsgBoxStyle.Information)
                ElseIf temperatura < 40 Then
                    MsgBox("Aseguresé que el extrusor este conectado o esperé un momento, no se puede imprimir a temperaturas tan bajas", MsgBoxStyle.Information)
                Else
                    Imprimir()
                End If
            Else
                MsgBox("Se encuentra en Modo Posicionar, cambiar a Modo Impresión", MsgBoxStyle.Information)
            End If
        Else
            MsgBox("No se ha conectado a ningún puerto", MsgBoxStyle.Information)
        End If

    End Sub
    Private Sub Imprimir()
        posX = 100
        posY = 100
        posZ = 0
        posE = 0
        posX0 = 0
        posY0 = 0
        posZ0 = 0
        posE0 = 0
        feedrate = 0
        numeropasosX = 200
        numeropasosY = 180
        numeropasosZ = 200
        numeropasosE = 48
        distanciapasoX = 42 / numeropasosX
        distanciapasoY = 2.25 / numeropasosY
        distanciapasoZ = 2 / numeropasosZ
        distanciapasoE = 6 / numeropasosE
        
        wait = 1
        ' Leer el contenido y asignar cada línea a los controles
        ' Comprobar que existe
        If System.IO.File.Exists(txtFichero.Text) = False Then
            MessageBox.Show("Debes indicar un fichero que exista", _
                        "Leer fichero", _
                        MessageBoxButtons.OK, MessageBoxIcon.Exclamation)
            Exit Sub
        End If

        ' Borrar el contenido previo de los controles
        ComboBox1.Items.Clear()

        ' Leer el fichero usando la codificación de Windows
        ' pero pudiendo usar la marca de tipo de fichero (BOM)
        Dim sr As New System.IO.StreamReader( _
                    Me.txtFichero.Text, _
                    System.Text.Encoding.Default, _
                    True)

        ' Leer el contenido mientras no se llegue al final
        While sr.Peek() <> -1
            posX0 = posX
            posY0 = posY
            posZ0 = posZ
            posE0 = posE
            cpX = 0
            cpZ = 0
            cpY = 0
            cpE = 0
            ' Leer una líena del fichero
            Dim lineaactual As String = sr.ReadLine()
            ' Si no está vacía, añadirla al control
            ' Si está vacía, continuar el bucle
            If String.IsNullOrEmpty(lineaactual) Then
                Continue While
            End If
            car2inst = 1
            car3inst = 4
            'Me.listBoxContenido.Items.Add(s)
            'Me.comboBoxContenido.Items.Add(s)
            'Me.listViewContenido.Items.Add(s)
            longitud = lineaactual.Length()
            primerletra = Mid(lineaactual, 1, 1)
            'comboBoxLongitud.Items.Add(primerletra)


            If primerletra = "G" Then 'Comprueba que el primer caracter de la linea sea G
                segundaletra = Mid(lineaactual, 2, 1)
                If segundaletra = "1" Then 'Comprueba si el caracter es 1, emtonces es un comando de motores
                    If Mid(lineaactual, 4, 1) = "X" Then 'Comprueba si es un comando del motor del eje x
                        For i = 6 To 8
                            If Mid(lineaactual, i, 1) = "." Then 'Busca la columna en la cual se encuentra el punto decimal
                                posX = Convert.ToDouble(Replace(Mid(lineaactual, 5, i - 1), ".", ",")) 'Convierte el dato de posicion a double 
                                TextBox1.AppendText(Convert.ToString(posX) + " ")
                                car2inst = 5 + i
                                Exit For
                            End If
                        Next i
                    ElseIf Mid(lineaactual, 4, 1) = "Y" Then 'Comprueba si es un comando del motor del eje y
                        For i = 6 To 8
                            If Mid(lineaactual, i, 1) = "." Then
                                posY = Convert.ToDouble(Replace(Mid(lineaactual, 5, i - 1), ".", ",")) 'Convierte el dato de posicion a double 
                                TextBox2.AppendText(Convert.ToString(posY) + " ")
                                car2inst = 5 + i
                                Exit For
                            End If
                        Next i
                    ElseIf Mid(lineaactual, 4, 1) = "Z" Then 'Comprueba si es un comando del motor del eje z
                        For i = 6 To 8
                            If Mid(lineaactual, i, 1) = "." Then
                                posZ = Convert.ToDouble(Replace(Mid(lineaactual, 5, i - 1), ".", ",")) 'Convierte el dato de posicion a double 
                                TextBox3.AppendText(Convert.ToString(posZ) + " ")
                                car2inst = 5 + i
                                Exit For
                            End If
                        Next i
                    ElseIf Mid(lineaactual, 4, 1) = "F" Then 'Comprueba si es un comando de velocidad
                        For i = 6 To 9
                            If Mid(lineaactual, i, 1) = "." Then
                                feedrate = Convert.ToDouble(Replace(Mid(lineaactual, 5, i - 1), ".", ",")) 'Convierte el dato de posicion a double 
                                TextBox4.AppendText(Convert.ToString(feedrate) + " ")
                                car2inst = 5 + i
                                Exit For
                            End If
                        Next i
                    ElseIf Mid(lineaactual, 4, 1) = "E" Then 'Comprueba si es un comando del motor del extrusor
                        For i = 6 To 8
                            If Mid(lineaactual, i, 1) = "." Then
                                posE = Convert.ToDouble(Replace(Mid(lineaactual, 5, i - 1), ".", ",")) 'Convierte el dato de posicion a double 
                                TextBox5.AppendText(Convert.ToString(posE) + " ")
                                car2inst = 5 + i
                                Exit For
                            End If
                        Next i
                    End If
                    'Segunda instruccion de la línea
                    If Mid(lineaactual, car2inst, 1) = "Y" Then 'Comprueba si es un comando del motor del eje Y
                        For i = car2inst + 2 To car2inst + 5
                            If Mid(lineaactual, i, 1) = "." Then
                                posY = Convert.ToDouble(Replace(Mid(lineaactual, car2inst + 1, i + 3 - car2inst), ".", ",")) 'Convierte el dato de posicion a double 
                                TextBox2.AppendText(Convert.ToString(posY) + " ")
                                car3inst = i + 5
                                Exit For
                            End If
                        Next i
                    ElseIf Mid(lineaactual, car2inst, 1) = "F" Then 'Comprueba si es un comando de velocidad
                        For i = car2inst + 2 To car2inst + 5
                            If Mid(lineaactual, i, 1) = "." Then
                                feedrate = Convert.ToDouble(Replace(Mid(lineaactual, car2inst + 1, i + 3 - car2inst), ".", ",")) 'Convierte el dato de posicion a double 
                                TextBox4.AppendText(Convert.ToString(feedrate) + " ")
                                car3inst = i + 5
                                Exit For
                            End If
                        Next i
                    ElseIf Mid(lineaactual, car2inst, 1) = "E" Then 'Comprueba si es un comando del extrusor
                        For i = car2inst + 2 To car2inst + 5
                            If Mid(lineaactual, i, 1) = "." Then
                                posE = Convert.ToDouble(Replace(Mid(lineaactual, car2inst + 1, i + 3 - car2inst), ".", ",")) 'Convierte el dato de posicion a double 
                                TextBox5.AppendText(Convert.ToString(posE) + "")
                                car3inst = i + 5
                                Exit For
                            End If
                        Next i
                    End If
                    'Tercera instruccion de la línea
                    If Mid(lineaactual, car3inst, 1) = "E" Then 'Comprueba si es un comando del motor del extrusor
                        For i = car3inst + 2 To car3inst + 5
                            If Mid(lineaactual, i, 1) = "." Then
                                posE = Convert.ToDouble(Replace(Mid(lineaactual, car3inst + 1, i + 3 - car3inst), ".", ",")) 'Convierte el dato de posicion a double 
                                TextBox5.AppendText(Convert.ToString(posE) + " ")
                                'car4inst = i + 5
                                Exit For
                            End If
                        Next i
                    End If
                End If

            End If
            pasosX = (posX - posX0) / distanciapasoX
            pasosY = (posY - posY0) / distanciapasoY
            pasosZ = (posZ - posZ0) / distanciapasoZ
            pasosE = (posE - posE0) / distanciapasoE
            salidas = pasosX.ToString + " " + pasosY.ToString + " " + pasosZ.ToString + " " + pasosE.ToString + " " + feedrate.ToString
            vel = velocidad(feedrate)
            If pasosX <> 0 Then
                If pasosX > 0 Then
                    MotorXadelante()
                Else
                    pasosX = pasosX * -1
                    MotorXatras()
                End If
            End If
            If pasosY <> 0 Then
                If pasosY > 0 Then
                    MotorYadelante()
                Else
                    pasosY = pasosY * -1
                    MotorYatras()
                End If
            End If

            If pasosX > pasosY Then
                contpasos = pasosX
                contpasos1 = pasosY
                menor = False
            Else
                contpasos = pasosY
                contpasos1 = pasosX
                menor = True
            End If
            If menor Then
                For i = 1 To contpasos1
                    motorXpaso(pasosX)
                    For k = 1 To 16
                        motorYpaso(pasosY)
                    Next
                Next
                pasosrestantes = contpasos - (contpasos1 * 16)
                For k = 1 To pasosrestantes
                    motorYpaso(pasosY)
                Next
            Else
                For i = 1 To contpasos1
                    motorXpaso(pasosX)
                    For k = 1 To 16
                        motorYpaso(pasosY)
                        i = i + 1
                    Next
                Next
                pasosrestantes = contpasos - contpasos1
                For k = 1 To pasosrestantes
                    motorXpaso(pasosX)
                Next
            End If


            PuertoSerie.DiscardInBuffer()
            PuertoSerie.DiscardOutBuffer()


            'Pasos en Z

            If pasosZ > 0 Then
                If MotorZadelante() = 1 Then
                    motorZpaso(pasosZ)
                End If
            Else
                pasosZ = pasosZ * -1
                If MotorZatras() = 1 Then
                    motorZpaso(pasosZ)
                End If
            End If
            'Pasos en el Extrusor
            If pasosE > 0 Then
                motorEpaso(pasosE)
            End If


            ComboBox1.Items.Add(salidas)
            If MousePosition.Equals(Me) Then
                Return
            End If
            m = PuertoSerie.ReadExisting()
            txtEntradaPuerto.AppendText(m)
        End While
        ' Cerrar el fichero
        sr.Close()
        PuertoSerie.DiscardInBuffer()
        PuertoSerie.DiscardOutBuffer()

    End Sub
    Private Sub cmdConectar_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles cmdConectar.Click

        If cmdConectar.Text = "Conectar" Then
            Puerto = cmbCom.SelectedItem 'Tomar el puerto seleccionado en el cmbCOM y guardarlo en la variable Puerto 
            Try
                PuertoSerie = My.Computer.Ports.OpenSerialPort(Puerto) 'establece puerto serie COM-X y ABRE EL PUERTO SERIE 
                PuertoSerie.BaudRate = 4800
                conectado = True
                cmdConectar.Text = "Desconectar" 'REEMPLAZA ETIQUETA DE BOTON CONECTAR POR DESCONECTAR
                shpONOFF.FillColor = Color.ForestGreen 'COLOCAR COLOR VERDE CUANDO ESTA CONECTADO 
                PuertoSerie.Write("I")
                PuertoSerie.DiscardInBuffer()
            Catch ex As Exception
                MsgBox("EL puerto " + Puerto + " no existe", MsgBoxStyle.Information)
            End Try
        Else
            If cmdConectar.Text = "Desconectar" Then
                PuertoSerie.Write("O")
                PuertoSerie.DiscardInBuffer()
                shpONOFF.FillColor = Color.Silver 'COLOCA COLOR PLOMO CUANDO ESTA DESCONECTADO 
                PuertoSerie.Close() 'CIERRA EL PUERTO 
                cmdConectar.Text = "Conectar" 'REEMPLAZA ETIQUETA DE BOTON DESCONECTAR POR CONECTAR 
                conectado = False
            End If
        End If

    End Sub
    Function MotorXadelante() As Integer
        retardo(vel)
        PuertoSerie.Write("X")
        PuertoSerie.DiscardInBuffer()
        btXpositivo.BackColor = Color.Blue
        btXnegativo.BackColor = Color.LightGray


        Return 1
    End Function
    Function MotorXatras() As Integer
        retardo(vel)
        PuertoSerie.Write("x")
        PuertoSerie.DiscardInBuffer()
        btXpositivo.BackColor = Color.LightGray
        btXnegativo.BackColor = Color.OrangeRed
        Return 1
    End Function
    Function motorXpaso(pasX As Integer) As Integer
        For h = 1 To 6
            retardo(vel)
        Next
        PuertoSerie.Write("1")
        PuertoSerie.DiscardInBuffer()
        pasX = pasX - 1
        Return pasX
    End Function
    Function MotorYadelante() As Integer
        retardo(vel)
        PuertoSerie.Write("Y")
        PuertoSerie.DiscardInBuffer()
        btYpositivo.BackColor = Color.SteelBlue
        btYnegativo.BackColor = Color.LightGray
        Return 1
    End Function
    Function MotorYatras() As Integer
        retardo(vel)
        PuertoSerie.Write("y")
        PuertoSerie.DiscardInBuffer()
        btYpositivo.BackColor = Color.LightGray
        btYnegativo.BackColor = Color.DarkOrange
        Return 1
    End Function
    Function motorYpaso(pasY As Integer) As Integer
        retardo(vel)
        retardo(vel)

        PuertoSerie.Write("2")
        PuertoSerie.DiscardInBuffer()
        pasY = pasY - 1
        Return pasY
    End Function
    Function motorEpaso(pasE As Integer) As Integer
        
        While pasE > 0
            For l = 1 To 5
                retardo(vel)
                retardo(vel)
            Next
            PuertoSerie.Write("4")
            PuertoSerie.DiscardInBuffer()
            pasE = pasE - 1
        End While
        Return pasE
    End Function
    Function MotorZadelante() As Integer
        retardo(vel)
        PuertoSerie.Write("z")
        PuertoSerie.DiscardInBuffer()
        btZpositivo.BackColor = Color.LightBlue
        btZNegativo.BackColor = Color.LightGray
        Return 1
    End Function
    Function MotorZatras() As Integer
        retardo(vel)
        PuertoSerie.Write("Z")
        PuertoSerie.DiscardInBuffer()
        btZpositivo.BackColor = Color.LightGray
        btZNegativo.BackColor = Color.Tomato
        Return 1
    End Function
    Function motorZpaso(pasZ As Integer) As Integer
        While pasZ > 0
            retardo(vel)
            retardo(vel)
            PuertoSerie.Write("3")
            PuertoSerie.DiscardInBuffer()
            pasZ = pasZ - 1
        End While
        Return 1
    End Function
    Function velocidad(vel As Double) As Integer
        velocidad = (11000 - feedrate)
    End Function
    Function retardo(ml As Integer) As Integer
        Dim n As Integer
        While n <= ml * 800
            n = n + 1
        End While
        Return 1
    End Function
   

    Private Sub Button6_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles btXpositivo.Click
        If conectado Then
            If modo Then
                MsgBox("Se encuentra en Modo Impresión, cambiar a Modo Posicionar", MsgBoxStyle.Information)
            Else
                MotorXadelante()
            End If
        Else
            MsgBox("No se ha conectado a ningún puerto", MsgBoxStyle.Information)
        End If
    End Sub

    Private Sub Button7_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles btXnegativo.Click

        If conectado Then
            If modo Then
                MsgBox("Se encuentra en Modo Impresión, cambiar a Modo Posicionar", MsgBoxStyle.Information)
            Else
                MotorXatras()
            End If
        Else
            MsgBox("No se ha conectado a ningún puerto", MsgBoxStyle.Information)
        End If
    End Sub

    Private Sub Button12_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles btPasoX.Click
        If conectado Then
            If modo Then
                MsgBox("Se encuentra en Modo Impresión, cambiar a Modo Posicionar", MsgBoxStyle.Information)
            Else
                For i = 1 To Convert.ToInt16(cmbPasosX.SelectedItem)
                    motorXpaso(1)
                Next
            End If
        Else
            MsgBox("No se ha conectado a ningún puerto", MsgBoxStyle.Information)

        End If

    End Sub

    Private Sub Button8_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles btYpositivo.Click
        If conectado Then
            If modo Then
                MsgBox("Se encuentra en Modo Impresión, cambiar a Modo Posicionar", MsgBoxStyle.Information)
            Else
                MotorYadelante()
            End If
        Else
            MsgBox("No se ha conectado a ningún puerto", MsgBoxStyle.Information)
        End If

    End Sub

    Private Sub Button9_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles btYnegativo.Click
        If conectado Then
            If modo Then
                MsgBox("Se encuentra en Modo Impresión, cambiar a Modo Posicionar", MsgBoxStyle.Information)
            Else
                MotorYatras()
            End If
        Else
            MsgBox("No se ha conectado a ningún puerto", MsgBoxStyle.Information)
        End If
    End Sub

    Private Sub Button13_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles btPasoY.Click
        If conectado Then
            If modo Then
                MsgBox("Se encuentra en Modo Impresión, cambiar a Modo Posicionar", MsgBoxStyle.Information)
            Else
                For i = 1 To Convert.ToInt16(cmbPasosY.SelectedItem)
                    motorYpaso(0)
                Next
            End If
        Else
            MsgBox("No se ha conectado a ningún puerto", MsgBoxStyle.Information)
        End If
    End Sub

    Private Sub Button10_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles btZpositivo.Click
        If conectado Then
            If modo Then
                MsgBox("Se encuentra en Modo Impresión, cambiar a Modo Posicionar", MsgBoxStyle.Information)
            Else
                MotorZadelante()
            End If
        Else
            MsgBox("No se ha conectado a ningún puerto", MsgBoxStyle.Information)
        End If
    End Sub

    Private Sub Button11_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles btZNegativo.Click
        If conectado Then
            If modo Then
                MsgBox("Se encuentra en Modo Impresión, cambiar a Modo Posicionar", MsgBoxStyle.Information)
            Else
                MotorZatras()
            End If
        Else
            MsgBox("No se ha conectado a ningún puerto", MsgBoxStyle.Information)
        End If
    End Sub

    Private Sub Button14_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles btPasoZ.Click
        If conectado Then
            If modo Then
                MsgBox("Se encuentra en Modo Impresión, cambiar a Modo Posicionar", MsgBoxStyle.Information)
            Else
                motorZpaso(Convert.ToInt16(cmbPasosZ.SelectedItem))
            End If
        Else
            MsgBox("No se ha conectado a ningún puerto", MsgBoxStyle.Information)
        End If
    End Sub

    Private Sub Button15_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles btRapido.Click
        vel = 1800
    End Sub

    Private Sub Button16_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles btMedio.Click
        vel = 3600
    End Sub

    Private Sub Button17_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles btLento.Click
        vel = 7800

    End Sub

    Private Sub btTemperatura_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles btTemperatura.Click
        If conectado Then
            PuertoSerie.Write("t")

            m = PuertoSerie.ReadExisting()
            txtTemperatura.Clear()
            Try
                temperatura = Convert.ToDouble(Replace(Mid(m, 2, 5), ".", ","))
                txtTemperatura.AppendText(temperatura.ToString + " C")
                PuertoSerie.DiscardOutBuffer()
                PuertoSerie.DiscardInBuffer()
            Finally
            End Try

        Else
            MsgBox("No se ha conectado a ningun puerto", MsgBoxStyle.Information)
        End If
    End Sub

    Private Sub btBorrar_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles btBorrar.Click
        TextBox1.Clear()
        TextBox2.Clear()
        TextBox3.Clear()
        TextBox4.Clear()
        TextBox5.Clear()
    End Sub

    Private Sub btPosicionar_Click(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles btPosicionar.Click
        If btPosicionar.Text = "Modo Impresión" Then
            modo = False
            btPosicionar.Text = "Modo Posicionar"
            btPosicionar.BackColor = Color.Beige
            vel = 3600
        Else
            modo = True
            btPosicionar.Text = "Modo Impresión"
            btPosicionar.BackColor = Color.Coral
        End If
    End Sub

    Private Sub Button1_Click_1(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles Button1.Click
        temperatura = 41
    End Sub

    Private Sub cmbPasosE_SelectedIndexChanged(ByVal sender As System.Object, ByVal e As System.EventArgs) Handles cmbPasosE.SelectedIndexChanged

    End Sub
End Class


