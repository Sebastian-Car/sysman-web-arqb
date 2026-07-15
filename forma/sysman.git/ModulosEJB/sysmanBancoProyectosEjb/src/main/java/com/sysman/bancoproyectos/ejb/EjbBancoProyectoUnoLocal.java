package com.sysman.bancoproyectos.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.ejb.Local;

@Local
public interface EjbBancoProyectoUnoLocal {

    boolean existeNivelPlanIndicativoporDigito(
        String compania,
        int anio,
        int digito)
                    throws SystemException;

    BigDecimal copiarActividadesBancoProyecto(
        String compania,
        String proyecto,
        String tipocomponente,
        String componente,
        String nombrecomponente,
        int vigencia,
        String usuario)
                    throws SystemException;

    BigDecimal eliminarProgramacionActividad(
        String compania,
        String proyecto,
        String tipocomponente,
        String codigocomponente,
        String codigoactividad,
        int vigencia,
        String usuario)
                    throws SystemException;

    double calcularPorcentaje(
        String compania,
        String tipoComponente,
        String codigoComponente,
        String proyecto,
        int vigencia,
        String valorAprobado,
        String valorProgramado,
        String tipoEstado,
        BigDecimal valorTotal,
        String valor,
        String valorAnt,
        int periodoProyecto,
        String tipotQueAprueba,
        String tipotQueApruebaProg,
        String clasetQueAprueba,
        String clasetQueApruebaProg,
        BigInteger codigoQueApruebaProg,
        BigInteger codigoQueAprueba,
        BigInteger codigoitemQueApruebaProg,
        BigInteger codigoItemQueAprueba,
        String dependenciaQueApruebaProg,
        String dependenciaQueAprueba,
        String codigoActividad,
        String nomActividad,
        String codigoProg,
        String cantidadProg,
        BigDecimal valorTotalProg,
        String usuario) throws SystemException;

     String cargarRubrosProyecto(
            String compania,
            String cadena,
            String usuario)
            throws SystemException;
     
     String actualizarActivXProyecto(
    		 String compania,
    		 String cadena,
    		 String usuario,
    		 String vigencia)
    				 throws SystemException;

	String cargarArmonizacionPd(
			String compania,
			String cadena,
			String usuario) throws SystemException;
}