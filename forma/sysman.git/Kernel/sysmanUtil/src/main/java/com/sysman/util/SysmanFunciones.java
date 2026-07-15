/*
 * To change this license header, choose License Headers in Project
 * Properties. To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sysman.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysman.util.persistencia.ConectorPool;

import co.com.sysman.comun.excepcion.NegocioExcepcion;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.NamingException;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.sf.jasperreports.engine.JRDefaultScriptlet;

/**
 *
 * @author sdaza
 * 
 * @author ybecerra Creación metodo initCap
 * 
 * @author jgomez
 * @version 3, 22/08/2018 Se crea el metodo colocarComillasTexto
 */
public class SysmanFunciones extends JRDefaultScriptlet {

    public static final String FORMATO_FECHA_ESTANDAR = "dd/MM/yyyy";
    
    public static double redondear(double numero, int decimales) {
        double redondeado;
        if (numero < 0) {
            redondeado = -Math.floor(
                            (-numero * Math.pow(10, decimales)) + 0.49999999999)
                / Math.pow(10, decimales);
        }
        else {
            redondeado = Math.floor(
                            (numero * Math.pow(10, decimales)) + 0.49999999999)
                / Math.pow(10, decimales);
        }

        return redondeado;
    }

    public static BigDecimal redondear(BigDecimal numero, int decimales) {
        BigDecimal rta;
        if (numero.compareTo(BigDecimal.ZERO) < 0) {
            rta = BigDecimal.ONE.negate()
                            .multiply((numero.multiply(BigDecimal.ONE.negate()))
                                            .multiply(BigDecimal.TEN
                                                            .pow(decimales))
                                            .add(BigDecimal.valueOf(
                                                            0.49999999999))
                                            .setScale(0, BigDecimal.ROUND_FLOOR)
                                            .divide(BigDecimal.TEN
                                                            .pow(decimales)));
        }
        else {
            rta = numero.multiply(BigDecimal.TEN
                            .pow(decimales)).add(BigDecimal.valueOf(
                                            0.49999999999))
                            .setScale(0, BigDecimal.ROUND_FLOOR)
                            .divide(BigDecimal.TEN
                                            .pow(decimales));
        }

        return rta;
    }

    public static String formatearFecha(Date fecha) {
        if (fecha == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String aplicaDbo = esBdSqlServer() ? "dbo." : "";
        return aplicaDbo + "TO_DATE('" + sdf.format(fecha)
            + "','DD/MM/YYYY HH24:mi:ss')";
    }

    public static String formatearFechaCadena(Date fecha, String formato) {
        if (fecha == null) {
            return null;
        }

        String formatoAux = formato.equals("DD/MM/YYYY HH24:mi:ss")
            ? "dd/MM/yyyy HH:mm:ss"
            : "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(formatoAux);
        String aplicaDbo = esBdSqlServer() ? "dbo." : "";
        return aplicaDbo + "TO_DATE('" + sdf.format(fecha) + "','" + formato
            + "')";
    }

    public static Date convertirAFecha(String fecha) throws ParseException {
        return convertirAFecha(fecha, FORMATO_FECHA_ESTANDAR);
    }

    public static Date convertirAFecha(String fecha, String formato)
                    throws ParseException {
        if (fecha == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(formato);
        return sdf.parse(fecha);
    }

    public static Date convertirAFechaHora(String fecha) throws ParseException {
        if (fecha == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.parse(fecha);
    }

    public static Date pasarHoraDia(Date hora, Date dia) {
        Calendar calHora = Calendar.getInstance();
        calHora.setTime(hora);
        Calendar calDia = Calendar.getInstance();
        calDia.setTime(dia);
        calHora.set(calDia.get(Calendar.YEAR), calDia.get(Calendar.MONTH),
                        calDia.get(Calendar.DAY_OF_MONTH));
        return calHora.getTime();
    }

    /**
     * Metodo que remplaza las variables de la descripcion del correo
     */
    public static String remplazarVariableCorreo(String descripcion,
        Map<String, Object> parametros) {
        String salida = descripcion;

        for (Map.Entry<String, Object> entry : parametros.entrySet()) {
            salida = salida.replace(
                            SysmanFunciones.concatenar("s$", entry.getKey(),
                                            "$s"),
                            SysmanFunciones.toString(entry.getValue()));
        }

        return salida;
    }

    public static Date pasarHoraDia(Date hora, String dia)
                    throws ParseException {

        return pasarHoraDia(hora, convertirAFecha(dia));
    }

    public static String convertirAFechaCadena(Date fecha, String formato)
                    throws ParseException {
        if (fecha == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(formato,
                        new Locale("es", "ES"));
        return sdf.format(fecha);
    }

    /**
     *
     * @param fecha
     * @return Fecha de tipo cadena en formato dd/mm/yyyy
     * @throws ParseException
     */
    public static String convertirAFechaCadena(Date fecha)
                    throws ParseException {
        return convertirAFechaCadena(fecha, FORMATO_FECHA_ESTANDAR);
    }

    /**
     *
     * @param fecha
     * @return Hora de tipo cadena en formato HH:mm:ss
     * @throws ParseException
     */
    public static String convertirAHoraCadena(Date fecha)
                    throws ParseException {
        if (fecha == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(fecha);
    }

    /**
     *
     * @param hora
     * @return Hora de tipo Date en formato HH:mm:ss
     * @throws ParseException
     */
    public static Date convertirAHora(String hora) throws ParseException {
        if (hora == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.parse(hora);
    }

    public static Date sumarRestarDiasFecha(Date fecha, int dias) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha); // Configuramos la fecha que se
        // recibe
        calendar.add(Calendar.DAY_OF_YEAR, dias); // numero de d�as a
        // a�adir, o
        // restar en caso de
        // d�as<0
        return calendar.getTime(); // Devuelve el objeto Date con los
        // nuevos
        // d�as a�adidos

    }

    public static Date sumarRestarMesesFecha(Date fecha, int meses) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha); // Configuramos la fecha que se
        // recibe
        calendar.add(Calendar.MONTH, meses); // numero de meses a
        // a�adir, o
        // restar en caso de
        // d�as<0
        return calendar.getTime(); // Devuelve el objeto Date con los
        // nuevos
        // d�as a�adidos
    }

    public static int calcularDiferenciaDias(Date fechaInicial, Date fechaFinal)
                    throws ParseException {
        Calendar calendarioUno = new GregorianCalendar();
        calendarioUno.setTime(fechaInicial);
        Calendar calendarioDos = new GregorianCalendar();
        calendarioDos.setTime(fechaFinal);
        long unaHora = 60 * 60 * 1000L;
        int mult = 1;
        if (calendarioUno.getTime().compareTo(calendarioDos.getTime()) > 0) {
            Calendar aux = calendarioUno;
            calendarioUno = calendarioDos;
            calendarioDos = aux;
            mult = -1;
        }
        return mult * (int) (((calendarioDos.getTime().getTime()
            - calendarioUno.getTime().getTime()) + unaHora)
            / (unaHora * 24));

    }

    public static int calcularDiferenciaMeses(Date fechaInicial,
        Date fechaFinal) {

        Calendar calendarioUno = new GregorianCalendar();
        calendarioUno.setTime(fechaInicial);
        Calendar calendarioDos = new GregorianCalendar();
        calendarioDos.setTime(fechaFinal);
        int startMes = (calendarioUno.get(Calendar.YEAR) * 12)
            + calendarioUno.get(Calendar.MONTH);
        int endMes = (calendarioDos.get(Calendar.YEAR) * 12)
            + calendarioDos.get(Calendar.MONTH);
        // Diferencia en meses entre las dos fechas
        return endMes - startMes;

    }

    public static int calcularDiferenciaAnios(Date fechaInicial,
        Date fechaFinal) {

        Calendar calendarioUno = new GregorianCalendar();
        calendarioUno.setTime(fechaInicial);
        Calendar calendarioDos = new GregorianCalendar();
        calendarioDos.setTime(fechaFinal);
        int startAnio = calendarioUno.get(Calendar.YEAR);
        int endAnio = calendarioDos.get(Calendar.YEAR);
        return startAnio - endAnio;

    }

    public static Date ultimoDiaDate(Date fecha) throws ParseException {
        Calendar c = Calendar.getInstance();
        c.setTime(fecha);
        int mes = c.get(Calendar.MONTH) + 1;
        int ano = c.get(Calendar.YEAR);
        mes++;
        if (mes > 12) {
            ano++;
            mes = 1;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy");
        String fechaCadena = "1-" + mes + "-" + ano;

        return SysmanFunciones.sumarRestarDiasFecha(sdf.parse(fechaCadena), -1);
    }

    public static int ultimoDiaInt(Date fecha) throws ParseException {
        Date dia = ultimoDiaDate(fecha);
        Calendar d = Calendar.getInstance();
        d.setTime(dia);

        return d.get(Calendar.DAY_OF_MONTH);
    }

    public static String padl(String cadena, int largo, String caracter) {
        StringBuilder r = new StringBuilder();
        String cad = cadena;
        if (cad == null) {
            cad = "";
        }
        if (cad.length() > largo) {
            cad = cad.substring(0, largo);
        }
        for (int i = 0; i < (largo - (cad.length())); i++) {
            r.append(caracter);
        }
        return r.append(cad).toString();
    }

    public static String padr(String cadena, int largo, String caracter) {
        StringBuilder r = new StringBuilder();
        String cad = cadena;
        if (cad == null) {
            cad = "";
        }
        if (cad.length() > largo) {
            cad = cad.substring(0, largo);
        }
        for (int i = 0; i < (largo - (cad.length())); i++) {
            r.append(caracter);
        }
        return r.insert(0, cad).toString();
    }

    public static String strZero(String text, int largo) {
        String ret;

        if (text == null) {
            ret = replicate("0", largo);
        }
        else if (text.length() > largo) {
            return text.substring(0, largo);
        }
        else {
            ret = replicate("0", largo - text.trim().length());
            ret += text.trim();
        }
        return ret;

    }

    public static String replicate(String text, int largo) {

        StringBuilder ret = new StringBuilder("");

        for (int i = 0; i < largo; i++) {
            ret.append(text);
        }

        return ret.toString();

    }

    public static int getParteFecha(Date fecha, int parte) {
        if (fecha == null) {
            return -1;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        return cal.get(parte);
    }

    public static int ano(Date fecha) {
        return getParteFecha(fecha, Calendar.YEAR);
    }

    public static int mes(Date fecha) {
        return getParteFecha(fecha, Calendar.MONTH) + 1;
    }

    public static int dia(Date fecha) {
        return getParteFecha(fecha, Calendar.DAY_OF_MONTH);
    }

    public static int getParteFechaMes(Date fecha) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        return cal.get(Calendar.MONTH) + 1;
    }

    public static int getParteFecha(String fecha, String formato, int parte)
                    throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(convertirAFecha(fecha, formato));
        return cal.get(parte);
    }

    public static String moneda(double numero, Object ctvs) 
    {
        double numt;
        long mil; // N�mero de unidades de mil del par�metro
        long nnu; // N�mero de unidades del par�metro
        StringBuilder letras = new StringBuilder(); // Cadena de
                                                    // caracteres de
                                                    // resultado
        int parte; // N�mero de iteraciones
        long cant = 0; // Valor de cada tripla poren por cada
        // iteracion
        long uCien;// Cebtenas de cada iteracion
        long uDiez;// Decenas de cada iteracion
        long unid; // Unidades de cada iteracion
        long millon; // Unidades de millon del par�metro
        long billon; // Unidades de billon del par�metro
        long dect; // Decimales dentro de cada tripla
        int sd; // Posicion dentro de una cadena
        int sw; // Indicador de iteracion LOOP
        final long kBillon = 1000000000;
        final long kMillon = 1000000;
        final long kMil = 1000;
        final long kCien = 100;
        final long kDiez = 10;
        numt = numero;
        if (Double.doubleToRawLongBits(numt) == 0) {
            numt = 0;
        }
        if (numt < 0) {
            numt *= -1;
        }
        numt = SysmanFunciones.redondear(numt, 2);
 
        HashMap<Long, String> un = new HashMap(); // Cadena de
        // caracteres para
        // las unidades
        un.put(1L, "UN");
        un.put(2L, "DOS");
        un.put(3L, "TRES");
        un.put(4L, "CUATRO");
        un.put(5L, "CINCO");
        un.put(6L, "SEIS");
        un.put(7L, "SIETE");
        un.put(8L, "OCHO");
        un.put(9L, "NUEVE");
        un.put(10L, "DIEZ");
 
        HashMap<Long, String> dec = new HashMap(); // Cadena de
        // caracteres para
        // las decenas
        dec.put(11L, "ONCE");
        dec.put(12L, "DOCE");
        dec.put(13L, "TRECE");
        dec.put(14L, "CATORCE");
        dec.put(15L, "QUINCE");
        //JM INI 26/11/2024
        dec.put(16L, "DIECISÉIS");
        dec.put(17L, "DIECISIETE");
        dec.put(18L, "DIECIOCHO");
        dec.put(19L, "DIECINUEVE");
        //JM FIN 26/11/2024
        dec.put(10L, "DIEZ");
        dec.put(20L, "VENTI");
        dec.put(30L, "TREINTA");
        dec.put(40L, "CUARENTA");
        dec.put(50L, "CINCUENTA");
        dec.put(60L, "SESENTA");
        dec.put(70L, "SETENTA");
        dec.put(80L, "OCHENTA");
        dec.put(90L, "NOVENTA");
 
        HashMap<Long, String> cen = new HashMap(); // Cadena de
        // caracteres para
        // las centenas
        cen.put(1L, "CIENTO");
        cen.put(2L, "DOSCIENTOS");
        cen.put(3L, "TRESCIENTOS");
        cen.put(4L, "CUATROCIENTOS");
        cen.put(5L, "QUINIENTOS");
        cen.put(6L, "SEISCIENTOS");
        cen.put(7L, "SETECIENTOS");
        cen.put(8L, "OCHOCIENTOS");
        cen.put(9L, "NOVECIENTOS");
 
        billon = (int) (numt / kBillon);
        millon = (int) ((numt - (billon * kBillon)) / kMillon);
        mil = (int) ((numt - (billon * kBillon) - (millon * kMillon)) / kMil);
        nnu = (long) (numt - (billon * kBillon) - (millon * kMillon)
            - (mil * kMil));
        parte = 0;
        if (((long) numt) == 0) {
            letras.append("CERO");
            parte = 4;
        }
        while (parte < 4) {
            sd = 0;
            sw = 1;
            parte += 1;
            switch (parte) {
            case 1:
                cant = billon;
                break;
            case 2:
                cant = millon;
                break;
            case 3:
                cant = mil;
                break;
            case 4:
                cant = nnu;
                break;
            default:
                break;
            }
            if (cant == 0) {
                sw = 0;
            }
            if (sw != 0) {
                uCien = cant / kCien;
                uDiez = (cant - (uCien * kCien)) / kDiez;
                unid = cant - (uCien * kCien) - (uDiez * kDiez);
                //mroser 11/02/2025
                if(cant == billon) {
                	   letras.append("MIL MILLONES ");
                }
                if (cant == kCien) {
                    letras.append("CIEN ");
                }
                if (cant != kCien) {
                    if (uCien != 0) {
                        letras.append(cen.get(uCien)).append(" ");
                    }
              //mrosero 07/04/2025     
                if (cant == mil && cant>1) {
                    letras.append(" MIL ");
                }    
                    dect = 0;
                    if (uDiez != 0) {
                        dect = (uDiez * kDiez) + unid;
                        if (dect == 20) {
                            letras.append("VEINTE ");
                        }
                        else {
                            if ((dect > kDiez) && (dect <= 19)) { //JM  26/11/2024
                                letras.append(dec.get(dect)).append(" ");
                            }
                            else {
                                letras.append(dec.get(uDiez * kDiez))
                                                .append(" ");
                            }
                            sd = 0;
                            if (((dect > 10) && (dect <= 19))   //JM  26/11/2024
                                || ((((dect / 10) * 10) == dect)
&& (dect > 0))) {
                                letras.append(" ");
                                sd = 1;
                            }
                            if (sd == 0 && uDiez != 2) {
                                letras.append("Y ");
                            }
                        }
                    }
                    if (dect != 20 && sd == 0 && unid != 0) {
                        if ((parte == 4) && (unid == 1)) {
                            letras.append("UN ");
                        }
                        else {
                            //JM  26/11/2024
                        	//mroser 11/02/2025
                        	letras.length();
                        	if (parte != 2 && (letras.toString().equalsIgnoreCase("UN") || 
                                    letras.toString().equalsIgnoreCase("") ) || 
                                	(billon==1 && letras.toString().equalsIgnoreCase("MIL MILLONES ")) ) {
                                    letras.delete(0, letras.length());
                            }else{
                                letras.append(un.get(unid)).append(" ");
                            }
                            
                            
                        }
 
                    }
                }
                if (parte == 1) {
                	//mroser 11/02/2025
                	if (cant > 1) {
                	replaceString(letras, "MIL MILLONES", " ");
                    letras.append("MIL MILLONES ");
                	}
                    else {
                        letras.append(" MIL MILLONES ");
                    }
                }
                if (parte == 2) {
                    if (cant > 1) {
                        replaceString(letras, " MILLONES", " ");
                        letras.append(" MILLONES ");
                    }
                    else {
                        letras.append(" MILLON ");
                    }
                }
                if (parte == 3) {
                	// mroser 11/02/2025
					if (cant > 1) {
						int lastIndex = letras.lastIndexOf("MIL ");
						if (lastIndex != -1) {
							letras.replace(lastIndex, lastIndex + 4, "");
						}
						letras.append("MIL ");
					} else {
						letras.append(" MIL ");
					}

                }
            }
 
        }
        if (((billon >= 1) || (millon >= 1)) && (mil == 0) && (nnu == 0)) {
            letras.append(" de ");
        }
 
        replaceString(letras, "  ", " ");
        trim(letras);
        if (ctvs instanceof Integer) {
            if ((int) ctvs == 0) {
                letras.append(" PESOS ");
            }
            else {
                letras.append(" ");
            }
        }
        else {
            letras.append(" PESOS ");
        }
        if (Double.doubleToRawLongBits(numt - (long) numt) != 0) {
            letras.append("CON ").append(moneda((numt - (long) numt) * 100, -1))
                            .append(" CTVS. ");
        }
        if (ctvs instanceof Integer) {
            if ((int) ctvs == 0) {
                letras.append("MC. ");
            }
        }
        else {
            letras.append("MC. ");
        }
        return letras.toString().trim();
    }

    /**
     * Extrae el nit de una cadena que contener puntos o el digito de
     * verificación antecedido por un guión.
     *
     * @param nit
     * cadena que contiene el número de identificación tributaria
     * @return NIT sin puntos ni digito de verificación
     */
    public static String extraerNIT(String nit) {
        String nitFinal = nit.replace(".", "");
        if (nitFinal.contains("-")) {
            int posicion = nitFinal.indexOf(45);
            nitFinal = nitFinal.substring(0, posicion);
        }
        return nitFinal;
    }

    /**
     * Extrae el digito de verificación de una cadena que contiene el
     * NIT con el digito de verificaci�n antecedido por un guión.
     *
     * @param nit
     * cadena que contiene el número de identificación tributaria
     * @return digito de verificación
     */
    public static String extraerDigitoVerificacion(String nit) {
        String dv = "";
        if (nit.contains("-")) {
            int posicion = nit.indexOf(45);
            dv = nit.substring(posicion + 1, nit.length());
        }
        return dv;
    }
    
    public static String monedaDolar(double numero, Object ctvs, boolean dolares) 
    {
    	if(dolares)
    	{
	        double numt;
	        long mil; // N mero de unidades de mil del par metro
	        long nnu; // N mero de unidades del par metro
	        StringBuilder letras = new StringBuilder(); // Cadena de
	                                                    // caracteres de
	                                                    // resultado
	        int parte; // N mero de iteraciones
	        long cant = 0; // Valor de cada tripla poren por cada
	        // iteracion
	        long uCien;// Cebtenas de cada iteracion
	        long uDiez;// Decenas de cada iteracion
	        long unid; // Unidades de cada iteracion
	        long millon; // Unidades de millon del par metro
	        long billon; // Unidades de billon del par metro
	        long dect; // Decimales dentro de cada tripla
	        int sd; // Posicion dentro de una cadena
	        int sw; // Indicador de iteracion LOOP
	        final long kBillon = 1000000000;
	        final long kMillon = 1000000;
	        final long kMil = 1000;
	        final long kCien = 100;
	        final long kDiez = 10;
	        numt = numero;
	        if (Double.doubleToRawLongBits(numt) == 0) {
	            numt = 0;
	        }
	        if (numt < 0) {
	            numt *= -1;
	        }
	        numt = SysmanFunciones.redondear(numt, 2);
	
	        HashMap<Long, String> un = new HashMap(); // Cadena de
	        // caracteres para
	        // las unidades
	        un.put(1L, "ONE");
	        un.put(2L, "TWO");
	        un.put(3L, "THREE");
	        un.put(4L, "FOUR");
	        un.put(5L, "FIVE");
	        un.put(6L, "SIX");
	        un.put(7L, "SEVEN");
	        un.put(8L, "EIGHT");
	        un.put(9L, "NINE");
	        un.put(10L, "TEN");
	
	        HashMap<Long, String> dec = new HashMap(); // Cadena de
	        // caracteres para
	        // las decenas
	        dec.put(11L, "ELEVEN");
	        dec.put(12L, "TWELVE");
	        dec.put(13L, "THIRTEEN");
	        dec.put(14L, "FOURTEEN");
	        dec.put(15L, "FIFTEEN");
	        dec.put(10L, "TEN");
	        dec.put(20L, "TWENTY");
	        dec.put(30L, "THIRTY");
	        dec.put(40L, "FORTY");
	        dec.put(50L, "FIFTY");
	        dec.put(60L, "SIXTY");
	        dec.put(70L, "SEVENTY");
	        dec.put(80L, "EIGHTY");
	        dec.put(90L, "NINETY");
	
	        HashMap<Long, String> cen = new HashMap(); // Cadena de
	        // caracteres para
	        // las centenas
	        cen.put(1L, "ONE HUNDRED");
	        cen.put(2L, "TWO HUNDRED");
	        cen.put(3L, "THREE HUNDRED");
	        cen.put(4L, "FOUR HUNDRED");
	        cen.put(5L, "FIVE HUNDRED");
	        cen.put(6L, "SIX HUNDRED");
	        cen.put(7L, "SEVEN HUNDRED");
	        cen.put(8L, "EIGHT HUNDRED");
	        cen.put(9L, "NINE HUNDRED");
	
	        billon = (int) (numt / kBillon);
	        millon = (int) ((numt - (billon * kBillon)) / kMillon);
	        mil = (int) ((numt - (billon * kBillon) - (millon * kMillon)) / kMil);
	        nnu = (long) (numt - (billon * kBillon) - (millon * kMillon)
	            - (mil * kMil));
	        parte = 0;
	        if (((long) numt) == 0) {
	            letras.append("ZERO");
	            parte = 4;
	        }
	        while (parte < 4) {
	            sd = 0;
	            sw = 1;
	            parte += 1;
	            switch (parte) {
	            case 1:
	                cant = billon;
	                break;
	            case 2:
	                cant = millon;
	                break;
	            case 3:
	                cant = mil;
	                break;
	            case 4:
	                cant = nnu;
	                break;
	            default:
	                break;
	            }
	            if (cant == 0) {
	                sw = 0;
	            }
	            if (sw != 0) {
	                uCien = cant / kCien;
	                uDiez = (cant - (uCien * kCien)) / kDiez;
	                unid = cant - (uCien * kCien) - (uDiez * kDiez);
	                if (cant == kCien) {
	                    letras.append("ONE HUNDRED ");
	                }
	                if (cant != kCien) {
	                    if (uCien != 0) {
	                        letras.append(cen.get(uCien)).append(" ");
	                    }
	                    dect = 0;
	                    if (uDiez != 0) {
	                        dect = (uDiez * kDiez) + unid;
	                        if (dect == 20) {
	                            letras.append("TWENTY ");
	                        }
	                        else {
	                            if ((dect > kDiez) && (dect < 16)) {
	                                letras.append(dec.get(dect)).append(" ");
	                            }
	                            else {
	                                letras.append(dec.get(uDiez * kDiez))
	                                                .append(" ");
	                            }
	                            sd = 0;
	                            if (((dect > 10) && (dect < 16))
	                                || ((((dect / 10) * 10) == dect)
	                                    && (dect > 0))) {
	                                letras.append(" ");
	                                sd = 1;
	                            }
	                            if (sd == 0 && uDiez != 2) {
	                                letras.append("AND ");
	                            }
	                        }
	                    }
	                    if (dect != 20 && sd == 0 && unid != 0) {
	                        if ((parte == 4) && (unid == 1)) {
	                            letras.append("ONE ");
	                        }
	                        else {
	                            letras.append(un.get(unid)).append(" ");
	                        }
	
	                    }
	                }
	                if (parte == 1) {
	                    letras.append("BILLION ");
	                }
	                if (parte == 2) {
	                    if (cant > 1) {
	                        replaceString(letras, " MILLIONS", " ");
	                        letras.append(" MILLONS ");
	                    }
	                    else {
	                        letras.append(" MILLION ");
	                    }
	                }
	                if (parte == 3) {
	                    letras.append("ONE THOUSAND ");
	                }
	            }
	
	        }
	        if (((billon >= 1) || (millon >= 1)) && (mil == 0) && (nnu == 0)) {
	            letras.append(" of ");
	        }
	
	        replaceString(letras, "  ", " ");
	        trim(letras);
	        if (ctvs instanceof Integer) {
	            if ((int) ctvs == 0) {
	                letras.append(" DOLLARS ");
	            }
	            else {
	                letras.append(" ");
	            }
	        }
	        else {
	            letras.append(" DOLLARS ");
	        }
	        if (Double.doubleToRawLongBits(numt - (long) numt) != 0) {
	            letras.append("WITH ").append(moneda((numt - (long) numt) * 100, -1))
	                            .append("CTVS. ");
	        }
	        if (ctvs instanceof Integer) {
	            if ((int) ctvs == 0) {
	                letras.append("MC. ");
	            }
	        }
	        else {
	            letras.append("MC. ");
	        }
	        return letras.toString().trim();
    	}
    	else
    	{
    		return moneda(numero, ctvs);
    	}
    	
    }

    /**
     * Separa cada uno de los caracteres de la cadena ingresados por
     * parametro por el caracter concatenado con el simbolo ingresado
     * por parametro. similar a la funcion StrPorParametro disponible
     * en Access.
     *
     * @param cadena
     * cadena a convertir
     * @param simbolo
     * separador de caracteres
     * @return
     */
    public static String separarCaracteres(String cadena, String simbolo) {
        return cadena.replaceAll(".(?=.)", "$0" + simbolo);
    }

    /**
     * Reemplaza todos los caracteres (excepto el �ltimo) por si mismo
     * seguido de un espacio en blanco
     *
     * @param cadena
     * cadena a convertir
     * @return cadena con espacios entre cada caracter
     */
    public static String espaciarCaracteres(String cadena) {
        return separarCaracteres(cadena, " ");
    }

    /**
     * Genera el nombre de la columna utilizado para calculos de
     * archivos excel
     *
     * @param columna
     * @return nombre de la columnna de acuerdo al n�mero ingresado
     */
    public static String nombreColumna(int columna) {
        String nombreCol = null;
        if ((columna <= 26) && (columna > 0)) {
            nombreCol = Character.toString((char) (columna + 64));
        }
        else if ((columna > 26) && (columna <= 52)) {
            nombreCol = "A" + (((char) columna - 26) + 64);
        }
        else if ((columna > 52) && (columna <= 78)) {
            nombreCol = "B" + (((char) columna - 52) + 64);
        }
        else if ((columna > 78) && (columna <= 104)) {
            nombreCol = "C" + (((char) columna - 78) + 64);
        }
        else if ((columna > 104) && (columna <= 130)) {
            nombreCol = "D" + (((char) columna - 104) + 64);
        }
        else if ((columna > 130) && (columna <= 156)) {
            nombreCol = "E" + (((char) columna - 130) + 64);
        }
        else if ((columna > 156) && (columna <= 182)) {
            nombreCol = "F" + (((char) columna - 156) + 64);
        }
        else if ((columna > 182) && (columna <= 208)) {
            nombreCol = "G" + (((char) columna - 182) + 64);
        }
        else if ((columna > 208) && (columna <= 234)) {
            nombreCol = "H" + (((char) columna - 208) + 64);
        }
        else if ((columna > 234) && (columna <= 260)) {
            nombreCol = "I" + (((char) columna - 234) + 64);
        }

        return nombreCol;
    }

    /**
     * Compara 2 fechas. Retorna true si la fecha inicial es menor a
     * la fecha final. Retorna false si la fecha final es mayor a la
     * fecha inicial.
     *
     * @param fechaInicial
     * Fecha Inicial a comparar.
     * @param fechaFinal
     * Fecha final a comparar.
     * @return true si la fecha inicial es menor a la fecha final.
     * false si la fecha final es mayor a la fecha inicial.
     */
    public static boolean comparaFechas(Date fechaInicial, Date fechaFinal) {
        return fechaInicial.before(fechaFinal);
    }

    /**
     * @author dmaldonado
     * @param fecha
     * Fecha a validar. Debe ingresarse en el formato dd/mm/yyyy.
     * @return true si la fecha es v�lida, false si no lo es.
     */
    public static boolean validarFecha(String fecha) {
        SimpleDateFormat sdf = new SimpleDateFormat(FORMATO_FECHA_ESTANDAR);
        sdf.setLenient(false);
        try {
            sdf.parse(fecha.trim());
        }
        catch (ParseException e) {
            return false;
        }
        return true;
    }

    /**
     * @author dmaldonado
     * @param fecha
     * fecha a la cual se le extraeron los datos
     * @return (Date) Dia primero del mes y a�o del par�metro fecha .
     * @throws ParseException
     */
    public static Date primeroDeMesFecha(Date fecha) throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        String fechaCadena = "01/" + (cal.get(Calendar.MONTH) + 1) + "/"
            + (cal.get(Calendar.YEAR));
        return convertirAFecha(fechaCadena, FORMATO_FECHA_ESTANDAR);
    }

    /**
     * @author dmaldonado
     * @param fecha
     * fecha a la cual se le extraer�n los datos
     * @return (String) Dia primero del mes y a�o del par�metro fecha
     * .
     * @throws ParseException
     */
    public static String primeroDeMesCadena(Date fecha) throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        return "01/" + (cal.get(Calendar.MONTH) + 1) + "/"
            + (cal.get(Calendar.YEAR));
    }

    /**
     * @author dmaldonado
     * @param fecha
     * fecha a la cual se le extraer�n los datos
     * @param formato
     * formato de salida de la cadena conteniendo la fecha
     * @return (String) Dia primero del mes y a�o del par�metro fecha.
     * @throws ParseException
     */
    public static String primeroDeMesCadena(Date fecha, String formato)
                    throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        String fechaCadena = "01/" + (cal.get(Calendar.MONTH) + 1) + "/"
            + (cal.get(Calendar.YEAR));
        SimpleDateFormat sdf = new SimpleDateFormat(formato,
                        new Locale("es", "ES"));
        return sdf.format(convertirAFecha(fechaCadena, FORMATO_FECHA_ESTANDAR));
    }

    /**
     * Equivalente de la funci�n Nz en Access o Nvl en PL/SQL para
     * cadenas
     *
     * @author dmaldonado
     * @param cadena
     * La cadena a la cual se le va a evaluar su estado de nulidad.
     * @param valor
     * El valor a retornar en caso de que cadena sea nulo.
     * @return El valor de la cadena, ya sea el original o el valor
     * cuando es nulo
     */
    public static String nvlStr(String cadena, String valor) {
        return (cadena == null) || "".equals(cadena) ? valor : cadena;
    }

    /**
     * @author dmaldonado
     * @param numero
     * El número al cual se le va a evaluar su estado de nulidad.
     * @param valor
     * El valor a retornar en caso de que numero sea nulo.
     * @return El valor del n�mero, ya sea el original o el valor
     * cuando es nulo
     */
    public static double nvlDbl(Object numero, double valor) {
        return (numero == null) || "".equals(numero) ? valor
            : Double.valueOf(numero.toString());
    }

    /**
     * En access, ColocarComillas, en el m�dulo de Contabilidad:
     * SysmanCT2016.02.06
     *
     * @author dmaldonado
     * @param strCadena
     * @return La cadena ingresada, con sus palabras separadas por
     * coma y entrecomilladas.
     * @author jrodriguezr --10/06/2017 Se ajusta el metodo para que
     * funcione a traves de un StringBuilder
     */
    public static String colocarComillas(String strCadena) {
        String cadena = strCadena.replace(" ", "");
        StringBuilder builder = new StringBuilder();
        builder.append("'").append(cadena).append("'");
        return builder.toString().replace(",", "','");
    }

    /**
     * Permite colocar una comilla sencilla a la derecha y la
     * izquierda del texto dado
     * 
     * @author jgomez
     * @param strTexto
     * @return Retorna el texto dado dentro de comillas sencillas
     */
    public static String colocarComillasTexto(String strTexto) {
        String cadena = strTexto;
        StringBuilder builder = new StringBuilder();
        builder.append("'").append(cadena).append("'");
        return builder.toString();
    }

    /**
     * @author dmaldonado
     * @param hora
     * permite ingresar la hora a convertir en formato HH24:mi (Horas
     * en formato de 24 horas, y minutos)
     * @return La hora convertida a un formato de tipo fecha, con
     * fecha 30/12/1899 seg�n el est�ndar.
     * @throws java.text.ParseException
     */
    public static Date convertirHoraAFecha(String hora) throws ParseException {
        return convertirAFechaHora("30/12/1899 " + hora + ":00");
    }

    /**
     * @author sdaza - 07/03/2017
     * @param fecha
     * permite ingresar la fecha a adicionar la hora maxima del dia
     * @return La fecha ingresada por parametro hasta la hora final
     * del dia para usar en informes
     * @throws java.text.ParseException
     */
    public static Date convertirFechaFinalDia(String fecha)
                    throws ParseException {
        return convertirAFechaHora(fecha + " 23:59:59");
    }

    /**
     * @author dmaldonado
     * @param objeto
     * El objeto al cual se le va a evaluar su estado de nulidad.
     * @param valor
     * El valor a retornar en caso de que objeto sea nulo.
     * @return El valor del objeto, ya sea el original o el valor
     * cuando es nulo
     */
    
  //JM se modifica y realiza sobrecarga del metodo ya que en algunas ocaciones esta sirviendo para 3 cosas
    
    public static <T> T nvl(T objeto, T valor) {
    	if (objeto == null) return valor;
        if (objeto instanceof String && ((String) objeto).isEmpty()) return valor;
        return objeto;
    }
    
    public static String nvl(String objeto, String valor) {
        return (objeto == null || objeto.isEmpty()) ? valor : objeto;
    }
    
    

    public static String getMD5Code(String entrada)
                    throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] messageDigest = md.digest(entrada.getBytes());
        BigInteger number = new BigInteger(1, messageDigest);
        StringBuilder hashtext = new StringBuilder(number.toString(16));
        while (hashtext.length() < 32) {
            hashtext.insert(0, "0");
        }
        return hashtext.toString().toUpperCase();
    }

    /**
     * Convierte una cadena que contiene claves y valores separados
     * por comas, a un objeto de tipo HashMap.
     *
     * @param campos
     * cadena que contiene los campos con sus respectivos valores
     * separados por comas.
     * @return <code>HashMap</code> con sus respectivas claves y
     * valores
     * @author jrodrigueza
     */
    public static Map<String, Object> convertStringToHashMap(
        String campos) {
        String camposAux = campos.replaceAll(", ", ",");
        camposAux = camposAux.replace("{", "");
        camposAux = camposAux.replace("}", "");
        HashMap<String, Object> map = new HashMap<>();
        String[] elementos = camposAux.split(",");
        for (String elemento : elementos) {
            String[] valores = elemento.split("=");
            map.put(valores[0], valores[1]);
        }
        return map;
    }

    /**
     * Une la fecha y hora ingresados por par�metro.
     *
     * @param date
     * fecha (día, mes, año)
     * @param time
     * tiempo (hora y minutos)
     * @return fecha y hora
     * @author jrodrigueza
     */
    public static Date getDatetime(Date date, Date time) {
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(date);

        Calendar timeCalendar = Calendar.getInstance();
        timeCalendar.setTime(time);

        Calendar datetime = Calendar.getInstance();
        datetime.set(dateCalendar.get(Calendar.YEAR),
                        dateCalendar.get(Calendar.MONTH),
                        dateCalendar.get(Calendar.DATE),
                        timeCalendar.get(Calendar.HOUR_OF_DAY),
                        timeCalendar.get(Calendar.MINUTE), 0);

        return datetime.getTime();
    }

    /**
     *
     * @param date
     * una fecha
     * @return fecha sin la parte del tiempo
     * @author jrodrigueza
     */
    public static Date truncarFecha(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    /**
     * Obtiene la fecha actual.
     *
     * @return <code>Calendar</code> con la fecha de hoy
     * @author jrodrigueza
     */
    public static Calendar hoy() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DATE),
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        calendar.get(Calendar.SECOND));

        return calendar;
    }

    /**
     * Crea un archivo con el nombre y ruta especificada.
     * <strong>¡Utilizar únicamente para realizar pruebas!</strong>
     *
     * @param binario
     * Arreglo de bytes que contiene el contenido del archivo.
     * @param rutaNombreArchivo
     * Ruta y Nombre del archivo incluyendo la extensión.
     * @throws IOException
     * Excepción cuando se presenta algun problema al generar el
     * archivo.
     * @author jrodrigueza
     */
    public static void crearArchivo(byte[] binario, String rutaNombreArchivo) {
        if (binario != null) {
            try (OutputStream out = new FileOutputStream(rutaNombreArchivo)) {
                out.write(binario);
                out.close();
            }
            catch (IOException ex) {
                Logger.getLogger(SysmanFunciones.class.getName())
                                .log(Level.SEVERE, null, ex);
            }
        }
    }

    public static Map<String, Object> jsonToMap(String json)
                    throws JsonParseException, JsonMappingException,
                    IOException {
        return new ObjectMapper().readValue(json,
                        HashMap.class);
    }

    public static boolean validarCampoVacio(Map<String, Object> campos,
        String var) {
        return (campos.get(var) == null)
            || campos.get(var).toString().isEmpty();
    }

    public static boolean validarCampoVacioB(Map<String, Object> campos,
            String var) {
            return Boolean.parseBoolean( String.valueOf(campos.get(var)));
        }
    
    public static boolean validarVariableVacio(String var) {
        return (var == null)
            || var.isEmpty();
    }

    public static String toString(Object var) {
        return (var == null) ? null : var.toString();
    }

    public static int nvlZero(Object var) {
        return ((var == null) || !(var instanceof Integer)) ? 0 : (int) var;
    }

    public static String calcularEdad(Date fechaNacimiento) {
        Calendar today = Calendar.getInstance();
        Calendar fechaNac = new GregorianCalendar();
        fechaNac.setTime(fechaNacimiento);
        int diffYear = today.get(Calendar.YEAR) - fechaNac.get(Calendar.YEAR);
        int diffMonth = today.get(Calendar.MONTH)
            - fechaNac.get(Calendar.MONTH);
        int diffDay = today.get(Calendar.DAY_OF_MONTH)
            - fechaNac.get(Calendar.DAY_OF_MONTH);
        if ((diffMonth < 0) || ((diffMonth == 0) && (diffDay < 0))) {
            diffYear = diffYear - 1;
        }
        return Integer.toString(diffYear);
    }

    public static String concatenar(String... cadena) {
        StringBuilder concatenado = new StringBuilder();
        for (String parte : cadena) {
            concatenado.append(parte);
        }
        return concatenado.toString();
    }

    /**
     * Utility method to replace the string from StringBuilder.
     * 
     * @param sb
     * the StringBuilder object.
     * @param toReplace
     * the String that should be replaced.
     * @param replacement
     * the String that has to be replaced by.
     * 
     */
    public static void replaceString(StringBuilder sb,
        String toReplace,
        String replacement) {
        int index;
        while ((index = sb.lastIndexOf(toReplace)) != -1) {
            sb.replace(index, index + toReplace.length(), replacement);
        }
    }

    private static void trim(StringBuilder letras) {

        int start;
        int end;

        // find the first character which is not space
        for (start = 0; start < letras.length(); start++) {
            if (letras.charAt(start) != ' ')
                break;
        }

        // find the last character which is not space
        for (end = letras.length(); end > start; end--) {
            if (letras.charAt(end - 1) != ' ')
                break;
        }

        letras.delete(0, start);
        letras.delete(end, letras.length());

    }

    /**
     * Elimina las tildes y asentos atipicos de las vocales del texto
     * ingresado por parametro
     * 
     * @param texto
     * @return texto sin acentos
     */
    public static String eliminarTildes(String texto) {
        String valor = texto;
        String limpio = null;
        // Normalizar texto para eliminar acentos, dieresis, cedillas
        // y tildes
        limpio = Normalizer.normalize(valor, Normalizer.Form.NFD);
        // Quitar caracteres no ASCII excepto la enie, interrogacion
        // que abre, exclamacion que abre, grados,
        limpio = limpio.replaceAll(
                        "[^\\p{ASCII}(N\u0303)(n\u0303)(\u00A1)(\u00BF)(\u00B0)]",
                        "");
        // Regresar a la forma compuesta, para poder comparar la enie
        // con la tabla de valores
        limpio = Normalizer.normalize(limpio, Normalizer.Form.NFC);
        return limpio;
    }

    /**
     * Genera los valores por defecto de combos y cuadros de texto en
     * base a la constante para textos
     * 
     * @param largo:
     * cantidad de repeticiones del caracter
     * @param valorFinal:
     * si es verdadero trae el valor por defecto final, de lo
     * contrario trae el inicial
     * @return texto que representa el valor por defecto para un largo
     * determinado
     */
    public static String defectoValorTexto(int largo, Boolean valorFinal) {
        String retorno = "";
        String cadena;
        if (valorFinal) {
            cadena = SysmanConstantes.DEFECTOFINAL_STRING;
        }
        else {
            cadena = SysmanConstantes.DEFECTOINICIAL_STRING;
        }
        for (int i = 0; i < largo; i++) {
            retorno = concatenar(new String[] { retorno, cadena });
        }
        return retorno;
    }

    /**
     * Genera los valores por defecto de combos y cuadros de texto en
     * base a la constante para numeros
     * 
     * @param largo:
     * cantidad de repeticiones del caracter
     * @param valorFinal:
     * si es verdadero trae el valor por defecto final, de lo
     * contrario trae el inicial
     * @return texto que representa el valor por defecto para un largo
     * determinado como numero
     */
    public static String defectoValorNumero(int largo, Boolean valorFinal) {
        String retorno = "";
        String cadena;
        int valor;
        int factor = 0;
        if (valorFinal) {
            valor = Integer.parseInt(SysmanConstantes.DEFECTOFINAL_NUMBER);
        }
        else {
            valor = Integer.parseInt(SysmanConstantes.DEFECTOINICIAL_NUMBER);
        }
        if (valor < 0) {
            factor = -1;
            valor = valor * factor;
        }
        cadena = Integer.toString(valor);
        for (int i = 0; i < largo; i++) {
            retorno = concatenar(new String[] { retorno, cadena });
        }
        retorno = concatenar(
                        new String[] { factor == 0 ? "" : "-", retorno });
        return retorno;
    }

    /**
     * Genera los valores por defecto de combos y cuadros de texto en
     * base a la constante para fechas
     * 
     * @param valorFinal:
     * si es verdadero trae el valor por defecto final, de lo
     * contrario trae el inicial
     * @return texto que representa el valor por defecto para un largo
     * determinado como date
     */
    public static String defectoValorFecha(Boolean valorFinal) {
        if (valorFinal) {
            return SysmanConstantes.DEFECTOFINAL_DATE;
        }
        else {
            return SysmanConstantes.DEFECTOINICIAL_DATE;
        }
    }

    /**
     * Cambia a mayuscula la primera letra del texto ingresado por
     * parametro y las demas las deja en minusculas.
     * 
     * @param texto
     * @return retorna el texto ingresado por parametro con la primera
     * letra en mayuscula y las demas en minuscula
     */
    public static String initCap(String texto) {

        return SysmanFunciones.concatenar(texto.substring(0, 1).toUpperCase(),
                        texto.substring(1).toLowerCase());
    }

    /**
     * Evalua que una cadena de caracteres sea un email valido
     * 
     * @param email
     * - Cadena a validar
     * @return - true si es valido o false si no
     */

    public static boolean validarEmail(String email) {

        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

    /**
     * Evalua que la contrasena cumple con las politicas de privacidad
     * 
     * @param contrasena
     * - Cadena a validar
     * @return - true si es valido o false si no
     */

    public static boolean validarContrasena(String contrasena, int numeros,
        int letras, int simbolos) {

        int cantidadNumeros = 0;
        int cantidadLetras = 0;
        int cantidadSignos = 0;

        for (int x = 0; x < contrasena.length(); x++) {

            if ((contrasena.charAt(x) > 32 && contrasena.charAt(x) < 48)
                || (contrasena.charAt(x) > 57 && contrasena.charAt(x) < 65)) { // Contar
                // signos
                cantidadSignos++;
            }
            if ((contrasena.charAt(x) > 47 && contrasena.charAt(x) < 58)) { // Contar
                                                                            // numeros
                cantidadNumeros++;
            }

            if ((contrasena.charAt(x) > 64 && contrasena.charAt(x) < 91)) { // Contar
                                                                            // letras
                                                                            // Mayusculas
                cantidadLetras++;
            }

            if ((contrasena.charAt(x) > 96 && contrasena.charAt(x) < 123)) { // Contar
                                                                             // letras
                                                                             // Minusculas
                cantidadLetras++;
            }

        }

        if (cantidadSignos < simbolos || cantidadNumeros < numeros ||

            cantidadLetras < letras) {

            return false;
        }

        return true;
    }

    /**
     * Convierte una cadena con formato ISO 8601 en un valor tipo
     * <code>Calendar</code>. En caso de que no se haya especificado
     * la zona horaria se emplea UTC-5 o COT para hacer la
     * conversi&oacute;n.
     * 
     * @param iso8601CompliantString
     * @return valor <code>Calendar</code> representado por la cadena
     * ingresada por par&aacute;metro.
     */
    public static Calendar parseIso8601(String iso8601CompliantString) {
        String lexicalXSDDate = iso8601CompliantString.replace("Z", "-05:00");
        return DatatypeConverter.parseDate(lexicalXSDDate);
    }

    /**
     * Obtiene la representación en ISO 8601 de una fecha, para la
     * zona horaria UTC-5 o COT.
     * 
     * @param fecha
     * fecha que se desea convertir.
     * @return cadena que representa la fecha en formato ISO 8601
     * (RFC3339),
     */
    public static String convertirAIso8601(Date fecha) {
        ZoneId zone = ZoneId.of("America/Bogota");
        DateTimeFormatter formatter = DateTimeFormatter
                        .ofPattern(SysmanConstantes.ISO_8601_DATE_TIME_PATTERN)
                        .withZone(zone);
        String iso8601CompliantString = formatter.format(fecha.toInstant());
        iso8601CompliantString = iso8601CompliantString.replace("Z", "-05:00");
        return iso8601CompliantString;
    }

    /**
     * Obtiene la parte de la fecha definida en el calendar,
     * reseteando la parte del tiempo (hora, minutos, segundos y
     * milisegundos) en ceros.
     * 
     * @param calendar
     * @return fecha sin parte del tiempo.
     */
    public static Date obtenerParteFecha(Calendar calendar) {
        Calendar fecha = calendar;
        fecha.set(Calendar.HOUR_OF_DAY, 0);
        fecha.set(Calendar.MINUTE, 0);
        fecha.set(Calendar.SECOND, 0);
        fecha.set(Calendar.MILLISECOND, 0);
        return fecha.getTime();
    }

    /**
     * Obtiene la parte del tiempo definida en el calendar, reseteando
     * la parte de la fecha (d&iacute;a, mes y año) en 1 de Enero de
     * 1970.
     * 
     * @param calendar
     * @return tiempo con la fecha predeterminada.
     */
    public static Date obtenerParteTiempo(Calendar calendar) {
        Calendar tiempo = calendar;
        tiempo.set(Calendar.DAY_OF_MONTH, 1);
        tiempo.set(Calendar.MONTH, 1);
        tiempo.set(Calendar.YEAR, 1970);
        return tiempo.getTime();
    }

    public static boolean validarXmlFile(String strArchivo) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();

            builder.setErrorHandler(new SimpleErrorHandler());
            // the "parse" method also validates XML, will throw an
            // exception if misformatted
            File file = new File(strArchivo);
            org.w3c.dom.Document document = builder.parse(file);
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Valida si el archivo contiene una sintaxis XML correcta.
     * 
     * @param strArchivo
     * ruta absoluta del archivo XML.
     * @return mensaje con la respuesta de la validacion.
     * @author jrodrigueza
     * @author hpuerto
     * @since 18/09/2018
     */
    public static String validarXmlFileWithMSg(String strArchivo) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();

            builder.setErrorHandler(new SimpleErrorHandler());
            // the "parse" method also validates XML, will throw an
            // exception if misformatted
            File file = new File(strArchivo);
            org.w3c.dom.Document document = builder.parse(file);
        }
        catch (Exception e) {
            return "Validación XML INCORRECTA del archivo " + strArchivo + " "
                + e.getMessage();
        }
        return "Validación XML Correcta del archivo " + strArchivo;
    }

    /**
     * Valida si el archivo contiene una sintaxis XML correcta.
     * 
     * @param arrayOutputStream
     * archivo XML como arreglo de bytes
     * @return mensaje con la respuesta de la validacion.
     * @author jrodrigueza
     * @author hpuerto
     * @since 18/09/2018
     */
    public static boolean validarXmlOutputStream(
        ByteArrayOutputStream arrayOutputStream) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new SimpleErrorHandler());
            // the "parse" method also validates XML, will throw an
            // exception if misformatted
            InputStream inputStream = new ByteArrayInputStream(
                            arrayOutputStream.toByteArray());
            org.w3c.dom.Document document = builder.parse(inputStream);
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Valida si el archivo contiene una sintaxis XML correcta.
     * 
     * @param contenidoXML
     * cadena que contiene la estructura XML
     * @return verdadero si la sintasix es correcta
     */
    public static boolean validarXmlCustomXWPFDocument(
        String contenidoXML) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new SimpleErrorHandler());
            // the "parse" method also validates XML, will throw an
            // exception if misformatted

            InputStream inputStream = new ByteArrayInputStream(
                            contenidoXML.getBytes());

            org.w3c.dom.Document document = builder.parse(inputStream);
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Valida si el valor de fórmula agregada es valida.
     * 
     * @return verdadero si la sintasix es correcta
     */
    public static boolean strValidarFormula(String strFormula, String strOrden,
        boolean detalle) {

        int intI = 0;

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");

        strFormula = strFormula.replace("SALDOA", "0.0");
        strFormula = strFormula.replaceAll("SALDO", "0.0");
        strFormula = strFormula.replaceAll("NETOA", "0.0");
        strFormula = strFormula.replaceAll("NETO", "0.0");
        strFormula = strFormula.replace("ROUND(", "Math.round(");

        if (!strOrden.isEmpty()) {
            if (detalle) {
                for (intI = 65; intI <= strOrden.charAt(0); intI++) {
                    strFormula = strFormula.replaceAll(
                                    String.valueOf((char) intI), "0.0");
                }

            }
            else {
                for (intI = 65; intI < strOrden.charAt(0); intI++) {
                    strFormula = strFormula.replaceAll(
                                    String.valueOf((char) intI), "0.0");
                }
            }
        }

        strFormula = strFormula.trim();
        try {
            // Permite ejecutar la operación
            Object result = engine.eval(strFormula);
            return true;
        }
        catch (Exception Ex) {
            return false;
        }
    }

    /**
     * Identifica el nombre de producto de base de datos al que se
     * est&aacute; conectando.
     * 
     * @param conexion
     * @return
     */
    public static String traerNombreProductoBD(Connection conexion) {
        String strDb = SysmanConstantes.ORACLE;
        DatabaseMetaData dbm;
        try {
            dbm = conexion.getMetaData();
            strDb = dbm.getDatabaseProductName();
            if (strDb.toUpperCase().contains(SysmanConstantes.MICROSOFT)) {
                strDb = SysmanConstantes.MICROSOFT;
            }
            if (strDb.toUpperCase().contains(SysmanConstantes.ORACLE)) {
                strDb = SysmanConstantes.ORACLE;
            }
        }
        catch (Exception ex) {
            strDb = "TIPO DE BASE DE DATOS NO DEFINIDA " + ex.toString();
        }
        return strDb;
    }

    /**
     * Metodo para extraer el ambiente desde el cual se esta
     * accediendo (BASE DE DATOS)
     * 
     * @param nombreConexion
     * @return
     * @throws NamingException
     * @throws SQLException
     */
    public static String traerNombreProductoBD(String nombreConexion)
                    throws NamingException, SQLException {
        String databaseProduct = null;
        ConectorPool conectorPool = new ConectorPool();
        try {
            conectorPool.conectar(nombreConexion);
            databaseProduct = traerNombreProductoBD(
                            conectorPool.getConection());
        }
        finally {
            conectorPool.getConection().close();
        }
        return databaseProduct;
    }

    /**
     * Identifica si la aplicaci&oacute;n est&aacute; conectada a una
     * base de datos MS SQL Server.
     * 
     * @return verdadero si es base de datos MS SQL Server.
     */
    public static boolean esBdSqlServer() {
        boolean rta = false;
        try {
            String tipoBaseDeDatos = traerNombreProductoBD(
                            ConectorPool.ESQUEMA_SYSMAN);
            rta = SysmanConstantes.MICROSOFT.equals(tipoBaseDeDatos);
        }
        catch (NamingException | SQLException e) {
            Logger.getLogger(SysmanFunciones.class.getName()).log(Level.SEVERE,
                            null, e);
        }
        return rta;
    }

    /**
     * Identifica si un objeto es de tipo de dato fecha.
     * 
     * @param object
     * objeto a evaluar
     * @return verdadero si corresponde a un objeto tipo fecha
     */
    public static boolean esFecha(Object object) {
        String classname = object.getClass().getName();
        if (Arrays.asList("oracle.sql.TIMESTAMPLTZ", "oracle.sql.TIMESTAMPTZ",
                        "oracle.sql.TIMESTAMP").contains(classname)) {
            return true;
        }
        else if ("microsoft.sql.DateTimeOffset".equals(classname)) {
            return true;
        }
        else {
            return object instanceof Timestamp || object instanceof Date;
        }
    }

    /**
     * Permite resolver los par&aacute;metros de una url
     * (m&eacute;todo GET) reemplazando los par&aacute;metros
     * asignados en la urlInicial entre numerales
     * 
     * @Ejemplo de Reemplazo
     * <ul>
     * <li><code>localhost:8080?compania=#idCompania#</code></li>
     * <li><code>localhost:8080?compania=001</code></li>
     * </ul>
     * @param urlInicial
     * : Tezto de la url que tienen los par&aacute;metros entre
     * numerales para reemplazar
     * @param parametros
     * : Mapa de par&aacute;metros que se reemplazan en el key
     * estar&aacute; el valor a reemplazar sin numerales y el valor
     * ser&aacute; el dato con el cual se reemplaza
     * @return Url con los par&aacute;metros reemplazos por los
     * par&aacute;metros dados
     * @author jgomez
     * @version 1.0; 03/12/2020
     */
    public static String resolverUrlGet(String urlInicial,
        Map<String, Object> parametros) {
        String urlFinal = urlInicial;
        if (parametros != null && parametros.size() > 0) {
            for (Map.Entry<String, Object> entry : parametros.entrySet()) {
                urlFinal = urlFinal.replace(
                                SysmanFunciones.concatenar("#", entry.getKey(),
                                                "#"),
                                SysmanFunciones.toString(entry.getValue()));
            }
        }
        return urlFinal;
    }
    
    public static String peticionMultipart(String url, 
    		HashMap<String, String> headers,
    		HashMap<String, Object> body) {
    	
    	String respuesta = null;
    	HashMap<String , String> param  = headers != null?headers:new HashMap<String, String>();
    	HashMap<String , Object> param1 = body != null?body:new HashMap<String, Object>();
    	
    	try {
			HttpPostMultipart multipart = new HttpPostMultipart(url, param);

			for (Map.Entry<String, Object> campo : param1.entrySet()) {
				
				if(campo.getValue() instanceof File) {
					multipart.addFilePart(campo.getKey(), (File) campo.getValue());
					
				}else {
					multipart.addFormField(campo.getKey(), (String) campo.getValue());
				}	
			}
			respuesta = multipart.finish();
			
		} catch (IOException | NegocioExcepcion e) {
			 Logger.getLogger(SysmanFunciones.class.getName()).log(Level.SEVERE,
                     null, e);
		}
    	   	
    	return respuesta;
    }
}
