package com.sysman.almacen.ejb.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.sysman.almacen.ejb.EjbAlmacenCeroGeneralLocal;
import com.sysman.almacen.ejb.EjbAlmacenCeroGeneralRemote;
import com.sysman.exception.SystemException;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

@Stateless
@LocalBean
public class EjbAlmacenCeroGeneral implements EjbAlmacenCeroGeneralRemote, EjbAlmacenCeroGeneralLocal{
	/**
     * Default constructor.
     */
    public EjbAlmacenCeroGeneral()
    {
    }

    @Override
    public String hallarPredecesor(
        String compania,
        String campo,
        String valorCampo,
        String tabla,
        int opcion)
                    throws SystemException
    {
        String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                               "UN_CAMPO             =>'", campo, "', ",
                               "UN_VALOR_CAMPO       =>'", valorCampo, "', ",
                               "UN_TABLA             =>'", tabla, "', ",
                               "UN_OPCION            =>",
                               Integer.toString(opcion)
        };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN.FC_HALLAPREDECESOR",
                        SysmanFunciones.concatenar(parametro),
                        Types.VARCHAR);
    }

    @Override
    public String verificarNombreDevolutivo(
        int opcion,
        int concodigo,
        String codigo,
        String compania)
                    throws SystemException
    {
        String[] parametro = { "UN_OPCION    =>", Integer.toString(opcion),
                               ", ", "UN_CONCODIGO =>",
                               Integer.toString(concodigo), ", ",
                               "UN_CODIGO    =>'", codigo, "', ",
                               "UN_COMPANIA  =>'", compania, "'"
        };

        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN.FC_VERIFICAR_NOMBRE_DEVOLUTIVO",
                        SysmanFunciones.concatenar(parametro),
                        Types.VARCHAR);
    }

    @Override
    public String verificarPlaca(
        String compania)
                    throws SystemException
    {
        try
        {
            String[] parametro = { "UN_COMPANIA  =>'", compania, "'"
            };
            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_ALMACEN.FC_VERIFICAR_PLACA",
                            SysmanFunciones.concatenar(parametro),
                            Types.CLOB));
        }
        catch (IOException | SQLException e)
        {
            throw new SystemException(e);
        }
    }

    @Override
    public String generarConsecutivoPolizas(
        String nombretabla,
        String condicion,
        String nombrecampo,
        String inicial)
                    throws SystemException
    {
        String[] parametro = { "UN_NOMBRETABLA =>'", nombretabla, "', ",
                               "UN_CONDICION   =>'", condicion, "', ",
                               "UN_NOMBRECAMPO =>'", nombrecampo, "', ",
                               "UN_INICIAL     =>'", inicial, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN.FC_GENCONSECUTIVOPOLIZAS",
                        SysmanFunciones.concatenar(parametro),
                        Types.VARCHAR);
    }

    @Override
    public BigDecimal guardarPoliza(
        String compania,
        String aseguradora,
        String numpoliza,
        String sucursal,
        Date fechai,
        Date fechaf,
        String grupo,
        String elemento,
        String placa,
        String riesgo,
        String usuario)
                    throws SystemException
    {
        try
        {
            String[] parametro = { "UN_COMPANIA     =>'", compania, "', ",
                                   "UN_ASEGURADORA  =>'", aseguradora, "', ",
                                   "UN_NUMPOLIZA    =>'", numpoliza, "', ",
                                   "UN_SUCURSAL     =>'", sucursal, "', ",
                                   "UN_FECHAI       =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(
                                                   fechai),
                                   "','DD/MM/YYYY'), ",
                                   "UN_FECHAF       =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(
                                                   fechaf),
                                   "','DD/MM/YYYY'), ",
                                   "UN_GRUPO        =>", grupo, ", ",
                                   "UN_ELEMENTO     =>", elemento, ", ",
                                   "UN_PLACA        =>", placa, ", ",
                                   "UN_RIESGO       =>'", riesgo, "',",
                                   "UN_USUARIO      =>'", usuario, "' "
            };
            return (BigDecimal) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_ALMACEN.FC_GUARDARPOLIZA",
                            SysmanFunciones.concatenar(parametro),
                            Types.DECIMAL);
        }
        catch (ParseException e)
        {
            throw new SystemException(e);
        }
    }

    @Override
    public String actualizarElementoInventario(
        String compania,
        String codigoant,
        String codigonuevo,
        String tipocambio)
                    throws SystemException
    {
        String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                               "UN_CODIGOANT         =>'", codigoant, "', ",
                               "UN_CODIGONUEVO       =>'", codigonuevo, "', ",
                               "UN_TIPOCAMBIO        =>'", tipocambio, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN.FC_ACTUALIZAELEMENTOINVENTARIO",
                        SysmanFunciones.concatenar(parametro),
                        Types.VARCHAR);
    }

    @Override
    public void revisarAfectacionMovimiento(
        String compania,
        String tipo,
        long numero)
                    throws SystemException
    {
        String[] parametro = { "UN_COMPANIA  =>'", compania, "', ",
                               "UN_TIPO      =>'", tipo, "', ",
                               "UN_NUMERO    =>", Long.toString(numero)
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN.PR_REVISARAFECTACIONMOV",
                        SysmanFunciones.concatenar(parametro));
    }

    @Override
    public void reversarRequisicion(
        String compania,
        long numero,
        String usuario)
                    throws SystemException
    {
        String[] parametro = { "UN_COMPANIA  =>'", compania, "', ",
                               "UN_NUMERO    =>", Long.toString(numero), ", ",
                               "UN_USUARIO   => '", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN.PR_REVERSAREQUISICION",
                        SysmanFunciones.concatenar(parametro));
    }

    @Override
    public void cambiarHoraMovimiento(
        String compania,
        String tipomovimiento,
        long numero,
        String horanueva,
        String formatohora,
        String usuario)
                    throws SystemException
    {
        String[] parametro = { "UN_COMPANIA        =>'", compania, "', ",
                               "UN_TIPOMOVIMIENTO=>'", tipomovimiento, "', ",
                               "UN_NUMERO        =>", Long.toString(numero),
                               ", ",
                               "UN_HORANUEVA     =>'", horanueva, "', ",
                               "UN_FORMATOHORA   =>'", formatohora, "',",
                               "UN_USUARIO       =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN.PR_CAMBIARHORAMOVIMIENTO",
                        SysmanFunciones.concatenar(parametro));
    }

    @Override
    public void cambiarFechaMovimiento(
        String compania,
        String tipomovimiento,
        long numero,
        Date fechaactual,
        String usuario)
                    throws SystemException
    {
        try
        {
            String[] parametro = { "UN_COMPANIA       =>'", compania, "', ",
                                   "UN_TIPOMOVIMIENTO =>'", tipomovimiento,
                                   "', ", "UN_NUMERO         =>",
                                   Long.toString(numero), ", ",
                                   "UN_FECHAACTUAL    =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(
                                                   fechaactual),
                                   "','DD/MM/YYYY'),", "UN_USUARIO        =>'",
                                   usuario, "'"
            };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_ALMACEN.PR_CAMBIARFECHAMOVIMIENTO",
                            SysmanFunciones.concatenar(parametro));
        }
        catch (ParseException e)
        {
            throw new SystemException(e);
        }
    }

    @Override
    public void revisarDevolutivo(
        String compania,
        long placainicial,
        long placafinal)
                    throws SystemException
    {
        String[] parametro = { "UN_COMPANIA     =>'", compania, "', ",
                               "UN_PLACAINICIAL =>",
                               Long.toString(placainicial), ", ",
                               "UN_PLACAFINAL   =>", Long.toString(placafinal)
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN.PR_REVISADEVOLUTIVO",
                        SysmanFunciones.concatenar(parametro));
    }

    @Override
    public String retornarResonsable(
        String compania,
        String dependencia,
        int modulo)
                    throws SystemException
    {
        String[] parametro = { "UN_COMPANIA    =>'", compania, "', ",
                               "UN_DEPENDENCIA =>'", dependencia, "', ",
                               "UN_MODULO      =>", Integer.toString(modulo), ""
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN.FC_RETRESPONSABLE",
                        SysmanFunciones.concatenar(parametro),
                        Types.VARCHAR);
    }

    @Override
    public String consultarNombreInventario(
        String compania,
        String elemento,
        boolean opcion,
        boolean nomCorto)
                    throws SystemException
    {
        String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                               "UN_ELEMENTO          =>'", elemento, "', ",
                               "UN_OPCION            =>", opcion ? "-1" : "0",
                               "UN_NOMCORTO          =>", nomCorto ? "-1" : "0"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN.FC_NOMBREINVENTARIO",
                        SysmanFunciones.concatenar(parametro),
                        Types.VARCHAR);
    }

    @Override
    public Date consultarUltimoDeFechaSalida(
        String compania,
        String elemento,
        Date fecha)
                    throws SystemException
    {
        try
        {
            String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                                   "UN_ELEMENTO          =>'", elemento, "', ",
                                   "UN_FECHA             =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(fecha),
                                   "','DD/MM/YYYY')"
            };
            return (Date) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_ALMACEN.FC_ULTIMODEFECHASALIDA",
                            SysmanFunciones.concatenar(parametro),
                            Types.DATE);
        }
        catch (ParseException e)
        {
            throw new SystemException(e);
        }
    }

    @Override
    public boolean validarTransaccionAlmHW(
        String compania,
        String bodegaorigen,
        String bodegadestino)
                    throws SystemException
    {
        String[] parametro = { "UN_COMPANIA      =>'", compania, "', ",
                               "UN_BODEGAORIGEN  =>'", bodegaorigen, "', ",
                               "UN_BODEGADESTINO =>'", bodegadestino, "'"
        };
        byte rta = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN.FC_VALIDATRANSACCIONALMHW",
                        SysmanFunciones.concatenar(parametro),
                        Types.TINYINT);
        return rta != 0;
    }

    @Override
    public void grabarProveedoresGeneral(
        String compania,
        String tipomovimiento,
        String numero)
                    throws SystemException
    {
        String[] parametro = { "UN_COMPANIA       =>'", compania, "', ",
                               "UN_TIPOMOVIMIENTO =>'", tipomovimiento, "', ",
                               "UN_NUMERO         =>'", numero, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN.PR_GRABAPROVEEDORESGRAL",
                        SysmanFunciones.concatenar(parametro));
    }

    @Override
    public boolean crearInterfaceAlmacenUnoAUno(
        String compania,
        String tipomovimiento,
        long numero,
        Date fecha,
        String descripcion,
        int digitos,
        String tercero,
        String sucursal,
        String centrocosto,
        String creador,
        BigDecimal valordocumento)
                    throws SystemException
    {
        try
        {
            String[] parametro = { "UN_COMPANIA       =>'", compania, "', ",
                                   "UN_TIPOMOVIMIENTO =>'", tipomovimiento,
                                   "', ",
                                   "UN_NUMERO   =>", Long.toString(numero),
                                   ", ",
                                   "UN_FECHA          =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(fecha),
                                   "','DD/MM/YYYY'), ",
                                   "UN_DESCRIPCION    =>'", descripcion, "', ",
                                   "UN_DIGITOS        =>",
                                   Integer.toString(digitos), ", ",
                                   "UN_TERCERO        =>'", tercero, "', ",
                                   "UN_SUCURSAL       =>'", sucursal, "', ",
                                   "UN_CENTROCOSTO    =>'", centrocosto, "', ",
                                   "UN_CREADOR        =>'", creador, "', ",
                                   "UN_VALORDOCUMENTO =>",
                                   valordocumento.toString()
            };
            byte rta = (byte) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_ALMACEN.FC_INTERFACEALMACENUNOAUNO",
                            SysmanFunciones.concatenar(parametro),
                            Types.TINYINT);
            return rta != 0;
        }
        catch (ParseException e)
        {
            throw new SystemException(e);
        }
    }

    @Override
    public boolean grabarDevolutivos(
        String compania,
        String tipomovimiento,
        long numero,
        String tipo,
        String dependenciaDestino,
        String responsableDestino,
        String sucursalResponsable,
        boolean inventarioInicial,
        Date fecha)
                    throws SystemException
    {
        try
        {
            String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                                   "UN_TIPOMOVIMIENTO    =>'", tipomovimiento,
                                   "', ", "UN_NUMERO            =>",
                                   Long.toString(numero), ", ",
                                   "UN_TIPO              =>'", tipo, "', ",
                                   "UN_DEPENDENCIA_DESTI =>'",
                                   dependenciaDestino, "', ",
                                   "UN_RESPONSABLE_DESTI =>'",
                                   responsableDestino, "', ",
                                   "UN_SUCURSAL_RESPONSA =>'",
                                   sucursalResponsable, "', ",
                                   "UN_INVENTARIO_INICIA =>",
                                   inventarioInicial ? "-1" : "0", ", ",
                                   "UN_FECHA             =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(fecha),
                                   "','DD/MM/YYYY')"
            };
            byte rta = (byte) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_ALMACEN.FC_GRABADEVOLUTIVOS",
                            SysmanFunciones.concatenar(parametro),
                            Types.TINYINT);
            return rta != 0;
        }
        catch (ParseException e)
        {
            throw new SystemException(e);
        }
    }

    @Override
    public String revisarPEPs(
        String compania,
        Date fechainicial,
        Date fechafinal,
        String elementoinicial,
        long placainicial,
        boolean actualizar,
        boolean informar)
                    throws SystemException
    {
        try
        {
            String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                                   "UN_FECHAINICIAL      =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(
                                                   fechainicial),
                                   "','DD/MM/YYYY'), ",
                                   "UN_FECHAFINAL        =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(
                                                   fechafinal),
                                   "','DD/MM/YYYY'), ",
                                   "UN_ELEMENTOINICIAL   =>'", elementoinicial,
                                   "', ",
                                   "UN_PLACAINICIAL      =>",
                                   Long.toString(placainicial),
                                   ", ",
                                   "UN_ACTUALIZAR  =>", actualizar ? "-1" : "0",
                                   ", ",
                                   "UN_INFORMAR   =>", informar ? "-1" : "0"
            };
            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_ALMACEN.FC_REVISARPEPS",
                            SysmanFunciones.concatenar(parametro),
                            Types.CLOB));
        }
        catch (ParseException | IOException | SQLException e)
        {
            throw new SystemException(e);
        }
    }

    @Override
    public boolean revisarSiHayMovimiento(
        String compania,
        String tipomovimiento,
        String tipomov,
        long movimiento)
                    throws SystemException
    {
        String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                               "UN_TIPOMOVIMIENTO    =>'", tipomovimiento,
                               "', ", "UN_TIPOMOV           =>'", tipomov,
                               "', ", "UN_MOVIMIENTO        =>",
                               Long.toString(movimiento)
        };
        byte rta = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN.FC_REVISASIHAYMOVIMIENTO",
                        SysmanFunciones.concatenar(parametro),
                        Types.TINYINT);
        return rta != 0;

    }

    @Override
    public String consultarDatosBodegaH(
        String compania,
        int opcion)
                    throws SystemException
    {
        String[] parametro = { "UN_COMPANIA   =>'", compania, "', ",
                               "UN_OPCION     =>'", Long.toString(opcion)
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN.FC_DATOSBODEGAH",
                        SysmanFunciones.concatenar(parametro),
                        Types.VARCHAR);
    }

    @Override
    public String consultarAlCambiarValorMovimiento(
        String compania,
        String tipomov,
        long nummov)
                    throws SystemException
    {
        String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                               "UN_TIPOMOV           =>'", tipomov, "', ",
                               "UN_NUMMOV            =>", Long.toString(nummov)
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN.FC_ALCAMBIAR_VLRDMOV",
                        SysmanFunciones.concatenar(parametro),
                        Types.VARCHAR);
    }

    @Override
    public void revisarPEPsPRocedimiento(
        String compania,
        Date fechainicial,
        Date fechafinal,
        String elementoinicial,
        BigDecimal saldoq)
                    throws SystemException
    {
        try
        {
            String[] parametro = { "UN_COMPANIA        =>'", compania, "', ",
                                   "UN_FECHAINICIAL    =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(
                                                   fechainicial),
                                   "','DD/MM/YYYY'), ",
                                   "UN_FECHAFINAL      =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(
                                                   fechafinal),
                                   "','DD/MM/YYYY'), ",
                                   "UN_ELEMENTOINICIAL =>'", elementoinicial,
                                   "', ", "UN_SALDOQ          =>",
                                   saldoq.toString()
            };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_ALMACEN.PR_REVISARPEPS",
                            SysmanFunciones.concatenar(parametro));
        }
        catch (ParseException e)
        {
            throw new SystemException(e);
        }
    }

    // ACA VOY
    @Override
    public String insertarDetalles(
        String compania,
        Date fechainicial,
        Date fechafinal,
        String elementoinicial,
        BigDecimal cantidad,
        long codigo,
        long serie,
        String tipomovimiento,
        long movimiento,
        String especificacion,
        String tercero,
        String sucursal,
        BigDecimal valorunitario,
        BigDecimal valortotal,
        BigDecimal porciva,
        BigDecimal valorunitarioAntesiva)
                    throws SystemException
    {
        try
        {
            String[] parametro = { "UN_COMPANIA     =>'", compania, "', ",
                                   "UN_FECHAINICIAL =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(
                                                   fechainicial),
                                   "','DD/MM/YYYY'), ",
                                   "UN_FECHAFINAL        =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(
                                                   fechafinal),
                                   "','DD/MM/YYYY'), ",
                                   "UN_ELEMENTOINICIAL =>'", elementoinicial,
                                   "', ", "UN_CANTIDAD       =>",
                                   cantidad.toString(), ", ",
                                   "UN_CODIGO         =>",
                                   Long.toString(codigo), ", ",
                                   "UN_SERIE          =>", Long.toString(serie),
                                   ", ", "UN_TIPOMOVIMIENTO =>'",
                                   tipomovimiento, "', ",
                                   "UN_MOVIMIENTO     =>",
                                   Long.toString(movimiento), ", ",
                                   "UN_ESPECIFICACION =>'", especificacion,
                                   "', ", "UN_TERCERO        =>'", tercero,
                                   "', ", "UN_SUCURSAL       =>'", sucursal,
                                   "', ", "UN_VALORUNITARIO  =>",
                                   valorunitario.toString(), ", ",
                                   "UN_VALORTOTAL    =>", valortotal.toString(),
                                   ", ", "UN_PORCIVA       =>",
                                   porciva.toString(), ", ",
                                   "UN_VALORUNITARIO_ANT =>",
                                   valorunitarioAntesiva.toString()
            };
            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_ALMACEN.FC_INSERTARDETALLES",
                            SysmanFunciones.concatenar(parametro),
                            Types.VARCHAR);
        }
        catch (ParseException e)
        {
            throw new SystemException(e);
        }
    }

    @Override
    public BigDecimal buscarExistencia(
        String compania,
        String elemento)
                    throws SystemException
    {
        String[] parametro = { "UN_COMPANIA     =>'", compania, "', ",
                               "UN_ELEMENTO     =>'", elemento, "'"
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN.FC_BUSCAEXISTENCIA",
                        SysmanFunciones.concatenar(parametro),
                        Types.DECIMAL);
    }

    @Override
    public String devolverSaldoPEPS(
        String compania,
        String tipomovimiento,
        String tipomovimientoAfect,
        String elemento,
        long movimiento,
        long codigo,
        BigDecimal cantidadNueva,
        BigDecimal cantidadAfectada,
        long serie)
                    throws SystemException
    {
        String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                               "UN_TIPOMOVIMIENTO    =>'", tipomovimiento,
                               "', ", "UN_TIPOMOVIMIENTO_AF =>'",
                               tipomovimientoAfect, "', ",
                               "UN_ELEMENTO          =>'", elemento, "', ",
                               "UN_MOVIMIENTO        =>",
                               Long.toString(movimiento), ", ",
                               "UN_CODIGO            =>", Long.toString(codigo),
                               ", ", "UN_CANTIDAD_NUEVA    =>",
                               cantidadNueva.toString(), ", ",
                               "UN_CANTIDAD_AFECTADA =>",
                               cantidadAfectada.toString(), ", ",
                               "UN_SERIE             =>", Long.toString(serie)
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN.FC_DEVUELVESALDOPEPS",
                        SysmanFunciones.concatenar(parametro),
                        Types.VARCHAR);
    }

    @Override
    public String ejecutarCorreccionDeValor(
        String compania,
        String tipomovimiento,
        long movimiento)
                    throws SystemException
    {
        String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                               "UN_TIPOMOVIMIENTO    =>'", tipomovimiento,
                               "', ", "UN_MOVIMIENTO        =>",
                               Long.toString(movimiento)
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN.FC_CORRECCIONDEVALOR",
                        SysmanFunciones.concatenar(parametro),
                        Types.VARCHAR);
    }

    @Override
    public void generarKardexPorSaldoPEPS(
        String compania)
                    throws SystemException
    {
        String[] parametro = { "UN_COMPANIA          =>'", compania, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN.PR_KARDEXSALDOPEPS",
                        SysmanFunciones.concatenar(parametro));
    }

    @Override
    public boolean controlarMovimientosPosteriores(
        String compania,
        String tipomovimiento,
        Date fecha,
        String elemento,
        long serie)
                    throws SystemException
    {
        try
        {
            String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                                   "UN_TIPOMOVIMIENTO    =>'", tipomovimiento,
                                   "', ", "UN_FECHA             =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(fecha),
                                   "','DD/MM/YYYY'), ",
                                   "UN_ELEMENTO          =>'", elemento, "', ",
                                   "UN_SERIE             =>",
                                   Long.toString(serie)
            };
            byte rta = (byte) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_ALMACEN.FC_MOVIMIENTOSPOSTERIORES",
                            SysmanFunciones.concatenar(parametro),
                            Types.TINYINT);
            return rta != 0;
        }
        catch (ParseException e)
        {
            throw new SystemException(e);
        }
    }

    @Override
    public boolean actualizarDevolutivo(
        String compania,
        String elemento,
        long serie)
                    throws SystemException
    {
        String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                               "UN_ELEMENTO          =>'", elemento, "', ",
                               "UN_SERIE             =>", Long.toString(serie)
        };
        byte rta = (byte) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN.FC_ACTUALIZADEVOLUTIVO",
                        SysmanFunciones.concatenar(parametro),
                        Types.DECIMAL);
        return rta != 0;
    }

    @Override
    public BigDecimal actualizarValorTotal(
        String compania,
        String tipomovimiento,
        long movimiento,
        BigDecimal porciva,
        BigDecimal valorunitarioAntesiva,
        BigDecimal cantidadNueva,
        long codigo)
                    throws SystemException
    {
        String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                               "UN_TIPOMOVIMIENTO    =>'", tipomovimiento,
                               "', ", "UN_MOVIMIENTO        =>",
                               Long.toString(movimiento), ", ",
                               "UN_PORCIVA           =>", porciva.toString(),
                               ", ", "UN_VALORUNITARIO_ANT =>",
                               valorunitarioAntesiva.toString(), ", ",
                               "UN_CANTIDAD_NUEVA    =>",
                               cantidadNueva.toString(), ", ",
                               "UN_CODIGO            =>", Long.toString(codigo)
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN.FC_ACTUALIZAVALORTOTAL",
                        SysmanFunciones.concatenar(parametro),
                        Types.DECIMAL);
    }

    @Override
    public String reversarMovimiento(
        String compania,
        String tipomovimiento,
        long movimiento)
                    throws SystemException
    {
        try
        {
            String[] parametro = { "UN_COMPANIA          =>'", compania, "', ",
                                   "UN_TIPOMOVIMIENTO    =>'", tipomovimiento,
                                   "', ", "UN_MOVIMIENTO        =>",
                                   Long.toString(movimiento)
            };
            return Acciones.clobToString((Clob) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_ALMACEN.FC_REVERSARMOVIMIENTO",
                            SysmanFunciones.concatenar(parametro),
                            Types.CLOB));
        }
        catch (IOException | SQLException e)
        {
            throw new SystemException(e);
        }
    }

    @Override
    public String verificarSaldoDisponible(
        String clase,
        String compania,
        int anio,
        Date fecha,
        String codigo,
        String tercero,
        String sucursal,
        String auxiliar,
        String centro,
        String referencia,
        String fuenterecurso,
        BigDecimal debitoant,
        BigDecimal creditoant,
        BigDecimal debito,
        BigDecimal credito)
                    throws SystemException
    {
        try
        {
            String[] parametro = { "UN_CLASE         =>'", clase, "', ",
                                   "UN_COMPANIA      =>'", compania, "', ",
                                   "UN_ANIO         =>", Integer.toString(anio),
                                   ", ",
                                   "UN_FECHA         =>TO_DATE('",
                                   SysmanFunciones.convertirAFechaCadena(fecha),
                                   "','DD/MM/YYYY'), ",
                                   "UN_CODIGO        =>'", codigo, "', ",
                                   "UN_TERCERO       =>'", tercero, "', ",
                                   "UN_SUCURSAL      =>'", sucursal, "', ",
                                   "UN_AUXILIAR      =>'", auxiliar, "', ",
                                   "UN_CENTRO        =>'", centro, "', ",
                                   "UN_REFERENCIA    =>'", referencia, "', ",
                                   "UN_FUENTERECURSO =>'", fuenterecurso, "', ",
                                   "UN_DEBITOANT     =>", debitoant.toString(),
                                   ", ",
                                   "UN_CREDITOANT    =>", creditoant.toString(),
                                   ", ",
                                   "UN_DEBITO        =>", debito.toString(),
                                   ", ",
                                   "UN_CREDITO       =>", credito.toString()
            };
            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_ALMACEN.FC_PRUEBA",
                            SysmanFunciones.concatenar(parametro),
                            Types.VARCHAR);
        }
        catch (ParseException e)
        {
            throw new SystemException(e);
        }
    }

    @Override
    public boolean verificarEstadoAlmacen(
        String compania,
        int ano,
        int mes)
                    throws SystemException
    {
        byte salida;
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANO               =>",
                                Integer.toString(ano), ", ",
                                "UN_MES               =>",
                                Integer.toString(mes), ""
        };
        salida = (byte) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN.FC_VERIFICAESTADOALM",
                        SysmanFunciones.concatenar(parametros),
                        Types.TINYINT);
        return salida == 0 ? false : true;
    }

    @Override
    public String actualizarSaldoPeps(
        String compania,
        int anioinicial,
        int mesinicial,
        int aniofinal,
        int mesfinal,
        String elementoinicial,
        String elementofinal)
                    throws SystemException
    {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_ANOINICIAL        =>", Integer.toString(anioinicial), ", ",
                                "UN_MESINICIAL        =>", Integer.toString(mesinicial), ", ",
                                "UN_ANOFINAL          =>", Integer.toString(aniofinal), ", ",
                                "UN_MESFINAL          =>", Integer.toString(mesfinal), ", ",
                                "UN_ELEMENTOINICIAL   =>'", elementoinicial, "', ",
                                "UN_ELEMENTOFINAL     =>'", elementofinal, "' "
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN.FC_ACTUALIZAR_SALDOPEPS",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }
    
    
    @Override
    public BigDecimal buscarExistencia(
        String compania,
        String elemento,
        String lote,
        String fuente)
                    throws SystemException
    {
        String[] parametro = { "UN_COMPANIA     =>'", compania, "', ",
				        	   "UN_ELEMENTO     =>'", elemento, "', ",
				        	   "UN_LOTE         =>'", lote, "', ",
                               "UN_FUENTE       =>'", fuente, "'"
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_ALMACEN.FC_BUSCAEXISTENCIAFL",
                        SysmanFunciones.concatenar(parametro),
                        Types.DECIMAL);
    }

}
