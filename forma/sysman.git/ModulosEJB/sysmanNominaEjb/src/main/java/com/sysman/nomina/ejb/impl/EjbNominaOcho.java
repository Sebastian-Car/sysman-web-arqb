package com.sysman.nomina.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.nomina.ejb.EjbNominaOchoLocal;
import com.sysman.nomina.ejb.EjbNominaOchoRemote;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class NominaOcho
 */
@Stateless
@LocalBean
public class EjbNominaOcho implements EjbNominaOchoRemote, EjbNominaOchoLocal
{
    /**
     * Default constructor.
     */
    public EjbNominaOcho()
    {
    }

    @Override
    public void identificarNovedadesAntesDeLiquidar(
        String compania,
        int proceso,
        int ano,
        int mes,
        int periodo,
        String ini,
        String fin,
        String usuario)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PROCESO           =>",
                                Integer.toString(proceso), ", ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_MES               =>",
                                Integer.toString(mes), ", ",
                                "UN_PERIODO           =>",
                                Integer.toString(periodo), ", ",
                                "UN_INICIAL           =>'", ini, "', ",
                                "UN_FINAL             =>'", fin, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM8.PR_NOVEDADESANTESDELIQUIDAR",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public boolean calcularNetos(
        String compania,
        int proceso,
        int anioFinal,
        int mesFinal,
        int periodoFin,
        boolean indicador,
        String usuario)
                    throws SystemException
    {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_PROCESO           =>",
                                Integer.toString(proceso), ", ",
                                "UN_ANIOFINAL         =>",
                                Integer.toString(anioFinal), ", ",
                                "UN_MESFINAL          =>",
                                Integer.toString(mesFinal), ", ",
                                "UN_PERIODOFIN        =>",
                                Integer.toString(periodoFin), ", ",
                                "UN_INDICADOR         =>",
                                indicador ? "-1" : "0", ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_NOMINA_COM8.FC_CALCULANETOS",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public String generarInformacionSiif(
        String compania,
        int anioInicial,
        int mesInicial,
        int periodoInicial,
        int anioFinal,
        int mesFinal,
        int periodoFinal,
        Date fecha,
        String tipoDoc,
        String numeroDoc,
        String codigoExp,
        String texto,
        String nivel,
        int esBeneficio)
                    throws SystemException
    {
        try
        {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ANIOINICIAL       =>",
                                    Integer.toString(anioInicial), ", ",
                                    "UN_MESINICIAL        =>",
                                    Integer.toString(mesInicial), ", ",
                                    "UN_PERIODOINICIAL    =>",
                                    Integer.toString(periodoInicial), ", ",
                                    "UN_ANIOFINAL         =>",
                                    Integer.toString(anioFinal), ", ",
                                    "UN_MESFINAL          =>",
                                    Integer.toString(mesFinal), ", ",
                                    "UN_PERIODOFINAL      =>",
                                    Integer.toString(periodoFinal), ", ",
                                    "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_TIPODOC           =>'", tipoDoc, "', ",
                                    "UN_NUMERODOC         =>'", numeroDoc,
                                    "', ", "UN_CODIGOEXP         =>'",
                                    codigoExp, "', ",
                                    "UN_TEXTO             =>'", texto, "', ",
                                    "UN_NIVEL             => ", nivel, ", ",
                                    "UN_ESBENEFICIO       => ",
                                    Integer.toString(esBeneficio)
            };
            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_NOMINA_COM8.FC_GENERAR_INFORMACION_SIIF",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (ParseException | IOException | SQLException e)
        {
            throw new SystemException(e);
        }
    }

    @Override
    public String informacionProvisiones(
        String compania,
        int anioIni,
        int mesIni,
        int periodoIni,
        int anioFin,
        int mesFin,
        int periodoFin,
        String nivel,
        Date fechaSolicit,
        String docSoporte,
        String numSoporte,
        String codExpedidor,
        String txtJustif)
                    throws SystemException
    {
        try
        {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ANIOINI           =>",
                                    Integer.toString(anioIni), ", ",
                                    "UN_MESINI            =>",
                                    Integer.toString(mesIni), ", ",
                                    "UN_PERIODOINI        =>",
                                    Integer.toString(periodoIni), ", ",
                                    "UN_ANIOFIN           =>",
                                    Integer.toString(anioFin), ", ",
                                    "UN_MESFIN            =>",
                                    Integer.toString(mesFin), ", ",
                                    "UN_PERIODOFIN        =>",
                                    Integer.toString(periodoFin), ", ",
                                    "UN_NIVEL             =>", nivel, ", ",
                                    "UN_FECHASOLICIT      =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaSolicit),
                                    "','DD/MM/YYYY'), ",
                                    "UN_DOCSOPORTE        =>'", docSoporte,
                                    "', ", "UN_NUMSOPORTE        =>'",
                                    numSoporte, "', ",
                                    "UN_CODEXPEDIDOR      =>'", codExpedidor,
                                    "', ", "UN_TXTJUSTIF         =>'",
                                    txtJustif, "'"
            };
            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_NOMINA_COM8.FC_INFORMACIONPROVISIONES",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (ParseException | IOException | SQLException e)
        {
            throw new SystemException(e);
        }
    }

    @Override
    public String generarInformacionSiifPatronal(
        String compania,
        int anioInicial,
        int mesInicial,
        int periodoInicial,
        int anioFinal,
        int mesFinal,
        int periodoFinal,
        Date fecha,
        String tipoDoc,
        String numeroDoc,
        String codigoExp,
        String texto,
        String nivel,
        String usuario)
                    throws SystemException
    {
        try
        {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ANIOINICIAL       =>",
                                    Integer.toString(anioInicial), ", ",
                                    "UN_MESINICIAL        =>",
                                    Integer.toString(mesInicial), ", ",
                                    "UN_PERIODOINICIAL    =>",
                                    Integer.toString(periodoInicial), ", ",
                                    "UN_ANIOFINAL         =>",
                                    Integer.toString(anioFinal), ", ",
                                    "UN_MESFINAL          =>",
                                    Integer.toString(mesFinal), ", ",
                                    "UN_PERIODOFINAL      =>",
                                    Integer.toString(periodoFinal), ", ",
                                    "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY'), ",
                                    "UN_TIPODOC           =>'", tipoDoc, "', ",
                                    "UN_NUMERODOC         =>'", numeroDoc,
                                    "', ", "UN_CODIGOEXP         =>'",
                                    codigoExp, "', ",
                                    "UN_TEXTO             =>'", texto, "', ",
                                    "UN_NIVEL             =>'", nivel, "', ",
                                    "UN_USUARIO           =>'", usuario, "'"
            };
            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_NOMINA_COM8.FC_GENERARINFO_SIIF_PATRONAL",
                            SysmanFunciones.concatenar(parametros),
                            Types.CLOB));
        }
        catch (ParseException | IOException | SQLException e)
        {
            throw new SystemException(e);
        }
    }

    @Override
    public String cargarExcelOccred(
        String compania,
        int proceso,
        int anio,
        int mes,
        int periodo,
        String banco,
        String consecutivo,
        Date fechareporte,
        boolean version,
        int informe,
        String comprobante)
                    throws SystemException
    {
        try
        {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_PROCESO           =>",
                                    Integer.toString(proceso), ", ",
                                    "UN_ANIO              =>",
                                    Integer.toString(anio), ", ",
                                    "UN_MES               =>",
                                    Integer.toString(mes), ", ",
                                    "UN_PERIODO           =>",
                                    Integer.toString(periodo), ", ",
                                    "UN_BANCO             =>'", banco, "', ",
                                    "UN_CONSECUTIVO       =>'", consecutivo,
                                    "', ", "UN_FECHAREPORTE      =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechareporte),
                                    "','DD/MM/YYYY'), ",
                                    "UN_VERSION           =>",
                                    version ? "-1" : "0", ", ",
                                    "UN_INFORME           =>",
                                    Integer.toString(informe), ", ",
                                    "UN_COMPROBANTE       => '",
                                    comprobante, "'"
            };
            if (informe == 2)
            {
                return Acciones.clobToStringSalto(
                                (Clob) AccionesImp.ejecutarFuncion(
                                                ConectorPool.ESQUEMA_SYSMAN,
                                                "PCK_NOMINA_COM8.FC_CARGAREXCELOCCIRED",
                                                SysmanFunciones.concatenar(
                                                                parametros),
                                                Types.CLOB));
            }
            else
            {
                return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                                ConectorPool.ESQUEMA_SYSMAN,
                                "PCK_NOMINA_COM8.FC_CARGAREXCELOCCIRED",
                                SysmanFunciones.concatenar(parametros),
                                Types.CLOB));
            }

        }
        catch (ParseException | IOException | SQLException e)
        {
            throw new SystemException(e);
        }
    }

    @Override
    public String planoContabilizarNomina(
        String compania,
        int opcionPlano,
        int proceso,
        int ano,
        int mes,
        int periodo,
        Date fechaInter,
        String tipoComprobante,
        BigInteger numeroComprobante,
        boolean manejaCentroCosto,
        boolean auxTercero,
        BigDecimal porContribucion,
        String cuentadbt,
        String cuentacrd,
        boolean unicocomprobante,
        boolean porPeriodo,
        String usuario)
                    throws SystemException
    {
        try
        {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_OPCIONPLANO       =>",
                                    Integer.toString(opcionPlano), ", ",
                                    "UN_PROCESO           =>",
                                    Integer.toString(proceso), ", ",
                                    "UN_ANO               =>",
                                    Integer.toString(ano), ", ",
                                    "UN_MES               =>",
                                    Integer.toString(mes), ", ",
                                    "UN_PERIODO           =>",
                                    Integer.toString(periodo), ", ",
                                    "UN_FECHAINTER        =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaInter),
                                    "','DD/MM/YYYY'), ",
                                    "UN_TIPOCOMPROBANTE   =>'", tipoComprobante,
                                    "', ",
                                    "UN_NUMEROCOMPROBANTE =>",
                                    numeroComprobante.toString(), ", ",
                                    "UN_MANEJACENTROCOSTO =>",
                                    manejaCentroCosto ? "-1" : "0", ", ",
                                    "UN_AUXTERCERO        =>",
                                    auxTercero ? "-1" : "0", ", ",
                                    "UN_PORCONTRIBUCION   =>",
                                    porContribucion.toString(), ", ",
                                    "UN_CUENTADBT         =>'", cuentadbt,
                                    "', ",
                                    "UN_CUENTACRD         =>'", cuentacrd,
                                    "', ",
                                    "UN_UNICOCOMPROBANTE  =>",
                                    unicocomprobante ? "-1" : "0", ", ",
                                    "UN_PORPERIODO  =>",
                                    porPeriodo ? "-1" : "0", ", ",
                                    "UN_USUARIO           =>'", usuario, "'"
            };

            return Acciones.clobToStringSalto((Clob) AccionesImp
                            .ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_NOMINA_COM8.FC_PLANOCONTABILIZARNOMINA",
                                            SysmanFunciones.concatenar(
                                                            parametros),
                                            Types.CLOB));
        }
        catch (ParseException | IOException | SQLException e)
        {
            throw new SystemException(e);
        }
    }
    
       		
    @Override
    public String actualizaActividadesCiiu(
        String compania,
        String actividadesCIIU,
        String usuario
        ) throws SystemException
    {
    	try
    	{
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_ACTIVIDADES_CIIU  =>", actividadesCIIU, ", ",
                                    "UN_USUARIO           =>'", usuario, "'"
	        };
            
            return Acciones.clobToStringSalto(
                                (Clob) AccionesImp.ejecutarFuncion(
                                                ConectorPool.ESQUEMA_SYSMAN,
                                                "PCK_NOMINA_COM8.FC_ACTACTIVIDADESCIIU",
                                                SysmanFunciones.concatenar(
                                                                parametros),
                                                Types.CLOB));
    	}
    	catch (IOException | SQLException e)
        {
            throw new SystemException(e);
        }
       
    }

}