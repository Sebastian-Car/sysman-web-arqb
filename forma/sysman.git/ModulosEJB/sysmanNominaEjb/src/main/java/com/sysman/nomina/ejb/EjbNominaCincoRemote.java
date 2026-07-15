package com.sysman.nomina.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Remote;

@Remote
public interface EjbNominaCincoRemote {

    BigDecimal obtenerMiMesada(
        String compania,
        int idempleado)
                    throws SystemException;

    boolean verificarConceptoFactorSS(
        String compania,
        int concepto)
                    throws SystemException;

    void sumarRetroactivosCinco(
        String compania,
        int anio)
                    throws SystemException;

    String generarDisco(
        String compania,
        int tipoliquidacion,
        int procesonomina,
        int anionomina,
        int mesnomina,
        int periodonomina,
        String estructura,
        String planilla,
        boolean correccion,
        String numcorreccion,
        Date fechacorreccion,
        int aniocorreccion,
        int mescorreccion,
        String nitcompania,
        boolean retroactivo,
        int periodoretro,
        Date fechaauto,
        String numradicacion,
        int orden,
        int empleado,
        String usuario)
                    throws SystemException;
    
    String generarDiscoFoncepFavidi(
			String compania, 
			int proceso, 
			String usuario, 
			int periodo, 
			String opcion, 
			int mes,
			int ano
	) throws SystemException;
	
    String generarDisPlanoBBogota(
			String compania, 
			int ano, 
			int mes, 
			int periodo,
			String referencia
			) throws SystemException;

	String cargarBasesNovedades(
			String compania,
			String anio,
			String mes,
			String cadena,
			String usuario) throws SystemException;
}