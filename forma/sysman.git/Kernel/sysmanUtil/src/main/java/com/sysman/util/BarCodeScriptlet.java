/**
 * Clase: BarCodeScriptlet.java
 *
 * Descripción TODO
 */

package com.sysman.util;

import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.renderers.BatikRenderer;

public class BarCodeScriptlet
                extends JRDefaultScriptlet
{
    static final int ctA = 203;
    static final int ctB = 204;
    static final int ctC = 205;
    static final int ctShift = 198;

    public static void main(String[] args)
    {

    }

    public static String test(String parametro)
    {
        String[] parametros = parametro.split("-");
        String binario = parametros[0];
        int width = Integer.parseInt(parametros[1]);
        int height = Integer.parseInt(parametros[2]);
        String Etapa = "";
        String strSvg = "";
        try
        {
            Etapa = "calculando Ancho linea";
            float anchoLinea = (float) width / (float) binario.length();
            anchoLinea = Math.round(anchoLinea * 100.0f) / 100.0f;
            Etapa = "creando encabezado de la imagen";
            strSvg = "<?xml version='1.0' standalone='no'?><svg xmlns='http://www.w3.org/2000/svg'   width='" + width + "' height='"
                            + height + "'><g id='barra' stroke='black' stroke-width='" + anchoLinea + "'>";
            if (anchoLinea <= 0.5)
            {
                strSvg = strSvg + "<text style='font-size: 10; font-weight: 900; fill: red; '> <tspan x='10' y='10'>Error en el ancho Min: 280</tspan></text>";
            }
            else
            {
                Etapa = "creando cuerpo de la imagen";
                for (int x = 0; x < binario.length(); ++x)
                {
                    if (!binario.substring(x, x + 1).equals("1"))
                        continue;
                    strSvg = strSvg + "<line x1='" + anchoLinea * x + "' y1='0' x2='" + anchoLinea * x + "' y2='" + height + "'  />";
                }
            }
            strSvg = strSvg + "</g></svg>";
            Etapa = "renderizando la imagen";
        }
        catch (Exception e)
        {
            return Etapa;
        }
        return strSvg;
    }

    public static BatikRenderer getCode128(String binario, int width, int height)
    {
        String strSvg = "";
        float anchoLinea = (float) width / (float) binario.length();
        anchoLinea = Math.round(anchoLinea * 100.0f) / 100.0f;
        strSvg = "<?xml version='1.0' standalone='no'?><svg xmlns='http://www.w3.org/2000/svg'   width='" + width + "' height='" + height
                        + "'><g id='barra' stroke='black' stroke-width='" + anchoLinea + "'>";
        if (anchoLinea <= 0.5)
        {
            strSvg = strSvg + "<text style='font-size: 10; font-weight: 900; fill: red; '> <tspan x='10' y='10'>Error en el ancho Min: 280</tspan></text>";
        }
        else
        {
            for (int x = 0; x < binario.length(); x++)
            {
                if (!binario.substring(x, x + 1).equals("1"))
                    continue;
                strSvg = strSvg + "<line x1='" + anchoLinea * x + "' y1='0' x2='" + anchoLinea * x + "' y2='" + height + "'  />";
            }
        }
        strSvg = strSvg + "</g></svg>";
        BatikRenderer objRender = new BatikRenderer(strSvg, null);
        return objRender;
    }

}