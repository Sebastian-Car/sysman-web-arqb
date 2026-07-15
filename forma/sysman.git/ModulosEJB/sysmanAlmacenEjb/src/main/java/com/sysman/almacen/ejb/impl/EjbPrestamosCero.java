package com.sysman.almacen.ejb.impl;

import com.sysman.almacen.ejb.EjbPrestamosCeroLocal;
import com.sysman.almacen.ejb.EjbPrestamosCeroRemote;
import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class PrestamosCero
 *
 * @version 2, 10/06/2017 jrodriguezr Se realiza refactorización de
 * concatenados
 */
@Stateless
@LocalBean
public class EjbPrestamosCero
                implements EjbPrestamosCeroRemote, EjbPrestamosCeroLocal {
    /**
     * Default constructor.
     */
    public EjbPrestamosCero() {
        // Sin Sentencias
    }

    @Override
    public void generarExclusion(
        String compania,
        Date fechaini,
        Date fechafin,
        String elemento,
        int serie,
        int limpia)
                        throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_FECHAINI          =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaini),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FECHAFIN          =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechafin),
                                    "','DD/MM/YYYY'), ",
                                    "UN_ELEMENTO          =>'", elemento, "', ",
                                    "UN_SERIE             =>",
                                    Integer.toString(serie), ", ",
                                    "UN_LIMPIA            =>",
                                    Integer.toString(limpia), "" };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PRESTAMOS.PR_GENERAREXCLUSION",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void generarUnicos(
        String compania,
        Date fechaini,
        Date fechafin,
        String elemento,
        int serie,
        String elementopad,
        int seriepad,
        int estodos)
                        throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_FECHAINI          =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaini),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FECHAFIN          =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechafin),
                                    "','DD/MM/YYYY'), ",
                                    "UN_ELEMENTO          =>'", elemento, "', ",
                                    "UN_SERIE             =>",
                                    Integer.toString(serie), ", ",
                                    "UN_ELEMENTOPAD       =>'", elementopad,
                                    "', ", "UN_SERIEPAD          =>",
                                    Integer.toString(seriepad), ", ",
                                    "UN_ESTODOS           =>",
                                    Integer.toString(estodos), "" };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PRESTAMOS.PR_GENERAUNICOS",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void generarFestivos(
        String compania,
        Date fechaini,
        Date fechafin,
        String elemento,
        int serie,
        String elementopad,
        int seriepad,
        int estodos)
                        throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_FECHAINI          =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaini),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FECHAFIN          =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechafin),
                                    "','DD/MM/YYYY'), ",
                                    "UN_ELEMENTO          =>'", elemento, "', ",
                                    "UN_SERIE             =>",
                                    Integer.toString(serie), ", ",
                                    "UN_ELEMENTOPAD       =>'", elementopad,
                                    "', ", "UN_SERIEPAD          =>",
                                    Integer.toString(seriepad), ", ",
                                    "UN_ESTODOS           =>",
                                    Integer.toString(estodos), "" };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PRESTAMOS.PR_GENERAFESTIVOS",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void generarUnDia(
        String compania,
        Date fechaini,
        Date fechafin,
        String elemento,
        int serie,
        String elementopad,
        int seriepad,
        int estodos)
                        throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_FECHAINI          =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaini),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FECHAFIN          =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechafin),
                                    "','DD/MM/YYYY'), ",
                                    "UN_ELEMENTO          =>'", elemento, "', ",
                                    "UN_SERIE             =>",
                                    Integer.toString(serie), ", ",
                                    "UN_ELEMENTOPAD       =>'", elementopad,
                                    "', ", "UN_SERIEPAD          =>",
                                    Integer.toString(seriepad), ", ",
                                    "UN_ESTODOS           =>",
                                    Integer.toString(estodos), "" };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PRESTAMOS.PR_GENERAUNDIA",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void generarTodosDias(
        String compania,
        Date fechaini,
        Date fechafin,
        String elemento,
        int serie,
        String elementopad,
        int seriepad,
        int estodos)
                        throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_FECHAINI          =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaini),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FECHAFIN          =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechafin),
                                    "','DD/MM/YYYY'), ",
                                    "UN_ELEMENTO          =>'", elemento, "', ",
                                    "UN_SERIE             =>",
                                    Integer.toString(serie), ", ",
                                    "UN_ELEMENTOPAD       =>'", elementopad,
                                    "', ", "UN_SERIEPAD          =>",
                                    Integer.toString(seriepad), ", ",
                                    "UN_ESTODOS           =>",
                                    Integer.toString(estodos), "" };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PRESTAMOS.PR_GENERATODOSDIAS",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public int hacerPase(
        String compania,
        String tipoorigen,
        int codigoorigen,
        String tipodestino,
        Date fechapase)
                        throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_TIPOORIGEN        =>'", tipoorigen,
                                    "', ", "UN_CODIGOORIGEN      =>",
                                    Integer.toString(codigoorigen), ", ",
                                    "UN_TIPODESTINO       =>'", tipodestino,
                                    "', ", "UN_FECHAPASE         =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechapase),
                                    "','DD/MM/YYYY')" };
            return (int) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PRESTAMOS.FC_HACERPASE",
                            SysmanFunciones.concatenar(parametros),
                            Types.INTEGER);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void generarPrestamos(
        String compania,
        Date fechaini,
        Date fechafin,
        String elemento,
        int serie,
        String elementopad,
        int seriepad)
                        throws SystemException {
        try {
            String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                    "UN_FECHAINI          =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechaini),
                                    "','DD/MM/YYYY'), ",
                                    "UN_FECHAFIN          =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fechafin),
                                    "','DD/MM/YYYY'), ",
                                    "UN_ELEMENTO          =>'", elemento, "', ",
                                    "UN_SERIE             =>",
                                    Integer.toString(serie), ", ",
                                    "UN_ELEMENTOPAD       =>'", elementopad,
                                    "', ", "UN_SERIEPAD          =>",
                                    Integer.toString(seriepad), "" };
            AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_PRESTAMOS.PR_GENERAPRESTADOS",
                            SysmanFunciones.concatenar(parametros));
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }
}