package com.sysman.util.rest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.rest.RipsPrincipal;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

public class RipsServicio {

	private final ObjectMapper objectMapper;
	
    private static final String SEPARADOR_COL = ",.COL.,"; 
    private static final String SEPARADOR_REG = ",.REG.,"; 

    private static final SimpleDateFormat DATETIME_FORMAT_YYYY_MM_DD_HH_MM = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final SimpleDateFormat DATETIME_FORMAT_YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");
    
    /*
     * En esta clase se hacen 3 procesos fundamentales para el procesamiento de los JSON
     * 1)parseRipsJsonStream: este metodo lee el json para luego poder validar sus valores individualmente -->rips = objectMapper.readValue(inputStream, RipsPrincipal.class);
     * 2)validarConsultas: en cada uno de los metodos  de validacion, se verifica la informacion que traiga el JSON segun los reglamentos de la Resolucion 2275 de 2023 y se devuelve una lista de errores
     * si la informacion no cumple con las especificaciones
     * 3)UsuariosClob: en cada uno de los metodosClob se mapea cada uno de los datos y se va anexando a un CLOB que se enviará a la Base de Datos
     */
    
	public RipsServicio() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	/**
     * Lee un archivo JSON desde una ruta de archivo y lo convierte en un objeto RipsPrincipal.
     * @param jsonFilePath La ruta completa al archivo JSON.
     * @return Un objeto RipsPrincipal mapeado desde el JSON.
     * @throws IOException Si hay un error al leer el archivo o al deserializar el JSON.
     */
    public RipsPrincipal parseRipsJsonFile(String jsonFilePath) throws IOException {
        File jsonFile = new File(jsonFilePath);
        return objectMapper.readValue(jsonFile, RipsPrincipal.class);
    }

    /**
     * Lee un JSON desde un String y lo convierte en un objeto RipsPrincipal.
     * @param jsonString El contenido del JSON como String.
     * @return Un objeto RipsPrincipal mapeado desde el JSON.
     * @throws IOException Si hay un error al deserializar el JSON.
     */
    public RipsPrincipal parseRipsJsonString(String jsonString) throws IOException {
        return objectMapper.readValue(jsonString, RipsPrincipal.class);
    }

    
    public ResultadoValidacionFactura parseFEVJsonStream(InputStream inputStream) throws IOException {
    	 ObjectMapper objectMapper = new ObjectMapper();
    	// Paso 1: leer JSON crudo (compatible con Java 8)
    	 ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	 byte[] buffer = new byte[1024];
    	 int length;
    	 while ((length = inputStream.read(buffer)) != -1) {
    	     baos.write(buffer, 0, length);
    	 }
    	 String rawJson = new String(baos.toByteArray(), java.nio.charset.StandardCharsets.UTF_8);

    	    // Paso 2: normalizar todas las claves a MAYÚSCULAS
    	    String upperCaseJson = JsonKeyNormalizer.normalizeKeysToUpper(rawJson);

    	    // Paso 3: trabajar con JsonNode en Jackson para homologar FECHARADICACION ↔ FECHAVALIDACION
    	    JsonNode rootNode = objectMapper.readTree(upperCaseJson);

    	    // Paso 4: convertir de nuevo a JSON string
    	    String homogenizedJson = objectMapper.writeValueAsString(rootNode);

    	    // Paso 5: deserializar al objeto final
    	    return objectMapper.readValue(homogenizedJson, ResultadoValidacionFactura.class);
    }
    
    /**
     * Lee un JSON desde un InputStream (útil para recursos o streams de red) y lo convierte en un objeto RipsPrincipal.
     * @param inputStream El InputStream que contiene los datos JSON.
     * @return Un objeto RipsPrincipal mapeado desde el JSON.
     * @throws IOException Si hay un error al leer el stream o al deserializar el JSON.
     */
    public RipsPrincipal parseRipsJsonStream(InputStream inputStream) throws IOException {
    	 
        List<String> errores = new ArrayList<>();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;

        while ((length = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }

        String rawJson = new String(baos.toByteArray(), java.nio.charset.StandardCharsets.UTF_8);

        // 2. Normalizar claves a MAYÚSCULAS
        String upperCaseJson = JsonKeyNormalizer.normalizeKeysToUpper(rawJson);

        // 3. Parsear como árbol JSON
        JsonNode rootNode = objectMapper.readTree(upperCaseJson);

        // 4. Volver a JSON String
        String normalizedJson = objectMapper.writeValueAsString(rootNode);

        // 5. Deserializar a RipsPrincipal
        RipsPrincipal rips;
        try {
            objectMapper.setTimeZone(TimeZone.getTimeZone("America/Bogota"));
            rips = objectMapper.readValue(normalizedJson, RipsPrincipal.class);

            if (rips.getUsuarios() != null) {
                for (Usuario usuario : rips.getUsuarios()) {

                    errores.addAll(validarUsuarios(Arrays.asList(usuario)));

                    if (usuario.getServicios() != null) {

                        if (usuario.getServicios().getConsultas() != null) {
                            errores.addAll(validarConsultas(usuario.getServicios().getConsultas()));
                        }

                        if (usuario.getServicios().getMedicamentos() != null) {
                            errores.addAll(validarMedicamentos(usuario.getServicios().getMedicamentos()));
                        }

                        if (usuario.getServicios().getProcedimientos() != null) {
                            errores.addAll(validarProcedimientos(usuario.getServicios().getProcedimientos()));
                        }

                        if (usuario.getServicios().getUrgencias() != null) {
                            errores.addAll(validarUrgencias(usuario.getServicios().getUrgencias()));
                        }

                        if (usuario.getServicios().getHospitalizacion() != null) {
                            errores.addAll(validarHospitalizacion(usuario.getServicios().getHospitalizacion()));
                        }

                        if (usuario.getServicios().getRecienNacidos() != null) {
                            errores.addAll(validarRecienNacido(usuario.getServicios().getRecienNacidos()));
                        }

                        if (usuario.getServicios().getOtrosServicios() != null) {
                            errores.addAll(validarOtrosServicios(usuario.getServicios().getOtrosServicios()));
                        }
                    }
                }
            }

        } catch (JsonParseException e) {
            throw new IOException("Error de sintaxis en el JSON: " + e.getOriginalMessage(), e);
        } catch (JsonMappingException e) {
            throw new IOException("La estructura del JSON no coincide con RipsPrincipal: " + e.getOriginalMessage(), e);
        }
        
        if (!errores.isEmpty()) {
            String mensaje = errores.stream().collect(Collectors.joining("\n"));
            throw new IOException("Errores de validación en el JSON RIPS:\n" + mensaje);
        }
        
        return rips;
        
    }
    
    
    private String formatDate(Date date, SimpleDateFormat formatter) {
        return (date != null) ? formatter.format(date) : "";
    }
    
    public String UsuariosClob(RipsPrincipal ripsData) {
        if (ripsData == null || ripsData.getUsuarios() == null || ripsData.getUsuarios().isEmpty()) {
            return "";
        }
        StringBuilder clob = new StringBuilder();
        for (Usuario u : ripsData.getUsuarios()) {
            clob.append(u.getTipoDocumentoIdentificacion() != null ? u.getTipoDocumentoIdentificacion() : "").append(SEPARADOR_COL);
            clob.append(u.getNumDocumentoIdentificacion() != null ? u.getNumDocumentoIdentificacion() : "").append(SEPARADOR_COL);
            clob.append(u.getTipoUsuario() != null ? u.getTipoUsuario() : "").append(SEPARADOR_COL);
            clob.append(formatDate(u.getFechaNacimiento(), DATETIME_FORMAT_YYYY_MM_DD)).append(SEPARADOR_COL);
            clob.append(u.getCodSexo() != null ? u.getCodSexo() : "").append(SEPARADOR_COL);
            clob.append(u.getCodPaisResidencia() != null ? u.getCodPaisResidencia() : "").append(SEPARADOR_COL);
            clob.append(u.getCodMunicipioResidencia() != null ? u.getCodMunicipioResidencia() : "").append(SEPARADOR_COL);
            clob.append(u.getCodZonaTerritorialResidencia() != null ? u.getCodZonaTerritorialResidencia() : "").append(SEPARADOR_COL);
            clob.append(u.getIncapacidad() != null ? u.getIncapacidad() : "").append(SEPARADOR_COL);
            clob.append(u.getConsecutivo() != null ? u.getConsecutivo().toString() : "").append(SEPARADOR_COL);
            clob.append(u.getCodPaisOrigen() != null ? u.getCodPaisOrigen() : "").append(SEPARADOR_COL);
            clob.append(ripsData.getNumFactura()).append(SEPARADOR_COL);
            clob.append(SEPARADOR_REG);
        }
        return clob.toString();
    }
    
    
    public String ConsultasClob(RipsPrincipal ripsData) {
        StringBuilder clob = new StringBuilder();
        if (ripsData != null && ripsData.getUsuarios() != null) {
            for (Usuario u : ripsData.getUsuarios()) {
                if (u.getServicios() != null && u.getServicios().getConsultas() != null && !u.getServicios().getConsultas().isEmpty()) {
                    for (Consulta c : u.getServicios().getConsultas()) {
                        clob.append(c.getCodPrestador() != null ? c.getCodPrestador() : "").append(SEPARADOR_COL);
                        clob.append(c.getTipoDocumentoIdentificacion() != null ? c.getTipoDocumentoIdentificacion() : "").append(SEPARADOR_COL);
                        clob.append(c.getNumDocumentoIdentificacion() != null ? c.getNumDocumentoIdentificacion() : "").append(SEPARADOR_COL);
                        clob.append(formatDate(c.getFechaInicioAtencion(), DATETIME_FORMAT_YYYY_MM_DD_HH_MM)).append(SEPARADOR_COL);
                        clob.append(c.getNumAutorizacion() != null ? c.getNumAutorizacion() : "").append(SEPARADOR_COL);
                        clob.append(c.getCodConsulta() != null ? c.getCodConsulta() : "").append(SEPARADOR_COL);
                        clob.append(c.getModalidadGrupoServicioTecSal() != null ? c.getModalidadGrupoServicioTecSal() : "").append(SEPARADOR_COL);
                        clob.append(c.getGrupoServicios() != null ? c.getGrupoServicios() : "").append(SEPARADOR_COL);
                        clob.append(c.getCodServicio() != null ? c.getCodServicio().toString() : "").append(SEPARADOR_COL);
                        clob.append(c.getFinalidadTecnologiaSalud() != null ? c.getFinalidadTecnologiaSalud() : "").append(SEPARADOR_COL);
                        clob.append(c.getCausaMotivoAtencion() != null ? c.getCausaMotivoAtencion() : "").append(SEPARADOR_COL);
                        clob.append(c.getCodDiagnosticoPrincipal() != null ? c.getCodDiagnosticoPrincipal() : "").append(SEPARADOR_COL);
                        clob.append(c.getCodDiagnosticoRelacionado1() != null ? c.getCodDiagnosticoRelacionado1() : "").append(SEPARADOR_COL);
                        clob.append(c.getCodDiagnosticoRelacionado2() != null ? c.getCodDiagnosticoRelacionado2() : "").append(SEPARADOR_COL);
                        clob.append(c.getCodDiagnosticoRelacionado3() != null ? c.getCodDiagnosticoRelacionado3() : "").append(SEPARADOR_COL);
                        clob.append(c.getTipoDiagnosticoPrincipal() != null ? c.getTipoDiagnosticoPrincipal() : "").append(SEPARADOR_COL);
                        clob.append(c.getVrServicio() != null ? c.getVrServicio().toPlainString() : "").append(SEPARADOR_COL);
                        clob.append(c.getConceptoRecaudo() != null ? c.getConceptoRecaudo() : "").append(SEPARADOR_COL);
                        clob.append(c.getValorPagoModerador() != null ? c.getValorPagoModerador().toPlainString() : "").append(SEPARADOR_COL);
                        clob.append(c.getNumFEVPagoModerador() != null ? c.getNumFEVPagoModerador() : "").append(SEPARADOR_COL);
                        clob.append(c.getConsecutivo() != null ? c.getConsecutivo().toString() : "").append(SEPARADOR_COL);
                        clob.append(ripsData.getNumFactura()).append(SEPARADOR_COL);
                        clob.append(SEPARADOR_REG);
                    }
                }
            }
        }
        return clob.toString();
    }

   
    public String ProcedimientosClob(RipsPrincipal ripsData) {
        StringBuilder clob = new StringBuilder();
        if (ripsData != null && ripsData.getUsuarios() != null) {
            for (Usuario u : ripsData.getUsuarios()) {
                if (u.getServicios() != null && u.getServicios().getProcedimientos() != null && !u.getServicios().getProcedimientos().isEmpty()) {
                    for (Procedimiento p : u.getServicios().getProcedimientos()) {
                        clob.append(p.getCodPrestador() != null ? p.getCodPrestador() : "").append(SEPARADOR_COL);
                        clob.append(formatDate(p.getFechaInicioAtencion(), DATETIME_FORMAT_YYYY_MM_DD_HH_MM)).append(SEPARADOR_COL);
                        clob.append(p.getIdMIPRES() != null ? p.getIdMIPRES() : "").append(SEPARADOR_COL);
                        clob.append(p.getNumAutorizacion() != null ? p.getNumAutorizacion() : "").append(SEPARADOR_COL);
                        clob.append(p.getCodProcedimiento() != null ? p.getCodProcedimiento() : "").append(SEPARADOR_COL);
                        clob.append(p.getViaIngresoServicioSalud() != null ? p.getViaIngresoServicioSalud() : "").append(SEPARADOR_COL); 
                        clob.append(p.getModalidadGrupoServicioTecSal() != null ? p.getModalidadGrupoServicioTecSal() : "").append(SEPARADOR_COL);
                        clob.append(p.getGrupoServicios() != null ? p.getGrupoServicios() : "").append(SEPARADOR_COL);
                        clob.append(p.getCodServicio() != null ? p.getCodServicio().toString() : "").append(SEPARADOR_COL);
                        clob.append(p.getFinalidadTecnologiaSalud() != null ? p.getFinalidadTecnologiaSalud() : "").append(SEPARADOR_COL); 
                        clob.append(p.getTipoDocumentoIdentificacion() != null ? p.getTipoDocumentoIdentificacion() : "").append(SEPARADOR_COL); 
                        clob.append(p.getNumDocumentoIdentificacion() != null ? p.getNumDocumentoIdentificacion() : "").append(SEPARADOR_COL); 
                        clob.append(p.getCodDiagnosticoPrincipal() != null ? p.getCodDiagnosticoPrincipal() : "").append(SEPARADOR_COL);
                        clob.append(p.getCodDiagnosticoRelacionado() != null ? p.getCodDiagnosticoRelacionado() : "").append(SEPARADOR_COL);
                        clob.append(p.getCodComplicacion() != null ? p.getCodComplicacion() : "").append(SEPARADOR_COL);
                        clob.append(p.getVrServicio() != null ? p.getVrServicio().toPlainString() : "").append(SEPARADOR_COL);
                        clob.append(p.getConceptoRecaudo() != null ? p.getConceptoRecaudo() : "").append(SEPARADOR_COL);
                        clob.append(p.getValorPagoModerador() != null ? p.getValorPagoModerador().toPlainString() : "").append(SEPARADOR_COL);
                        clob.append(p.getNumFEVPagoModerador() != null ? p.getNumFEVPagoModerador() : "").append(SEPARADOR_COL);
                        clob.append(p.getConsecutivo() != null ? p.getConsecutivo().toString() : "").append(SEPARADOR_COL);
                        clob.append(ripsData.getNumFactura()).append(SEPARADOR_COL);
                        clob.append(SEPARADOR_REG); 
                    }
                }
            }
        }
        return clob.toString();
    }
    
    
    public String MedicamentosClob(RipsPrincipal ripsData) {
        StringBuilder clob = new StringBuilder();
        if (ripsData != null && ripsData.getUsuarios() != null) {
            for (Usuario u : ripsData.getUsuarios()) {
                if (u.getServicios() != null && u.getServicios().getMedicamentos() != null && !u.getServicios().getMedicamentos().isEmpty()) {
                    for (Medicamento m : u.getServicios().getMedicamentos()) {
                        clob.append(m.getCodPrestador() != null ? m.getCodPrestador() : "").append(SEPARADOR_COL);
                        clob.append(m.getNumAutorizacion() != null ? m.getNumAutorizacion() : "").append(SEPARADOR_COL);
                        clob.append(m.getIdMIPRES() != null ? m.getIdMIPRES() : "").append(SEPARADOR_COL);
                        clob.append(formatDate(m.getFechaDispensAdmon(), DATETIME_FORMAT_YYYY_MM_DD_HH_MM)).append(SEPARADOR_COL);
                        clob.append(m.getCodDiagnosticoPrincipal() != null ? m.getCodDiagnosticoPrincipal() : "").append(SEPARADOR_COL);
                        clob.append(m.getCodDiagnosticoRelacionado() != null ? m.getCodDiagnosticoRelacionado() : "").append(SEPARADOR_COL);
                        clob.append(m.getTipoMedicamento() != null ? m.getTipoMedicamento() : "").append(SEPARADOR_COL);
                        clob.append(m.getCodTecnologiaSalud() != null ? m.getCodTecnologiaSalud() : "").append(SEPARADOR_COL);
                        clob.append(m.getNomTecnologiaSalud() != null ? m.getNomTecnologiaSalud() : "").append(SEPARADOR_COL);
                        clob.append(m.getConcentracionMedicamento() != null ? m.getConcentracionMedicamento().toString() : "").append(SEPARADOR_COL);
                        clob.append(m.getUnidadMedida() != null ? m.getUnidadMedida().toString() : "").append(SEPARADOR_COL);
                        clob.append(m.getFormaFarmaceutica() != null ? m.getFormaFarmaceutica() : 0).append(SEPARADOR_COL);
                        clob.append(m.getUnidadMinDispensa() != null ? m.getUnidadMinDispensa().toString() : "").append(SEPARADOR_COL);
                        clob.append(m.getCantidadMedicamento() != null ? m.getCantidadMedicamento().toString() : "").append(SEPARADOR_COL);
                        clob.append(m.getDiasTratamiento() != null ? m.getDiasTratamiento().toString() : "").append(SEPARADOR_COL);
                        clob.append(m.getTipoDocumentoIdentificacion() != null ? m.getTipoDocumentoIdentificacion() : "").append(SEPARADOR_COL);
                        clob.append(m.getNumDocumentoIdentificacion() != null ? m.getNumDocumentoIdentificacion() : "").append(SEPARADOR_COL);
                        clob.append(m.getVrUnitMedicamento() != null ? m.getVrUnitMedicamento().toPlainString() : "").append(SEPARADOR_COL);
                        clob.append(m.getVrServicio() != null ? m.getVrServicio().toPlainString() : "").append(SEPARADOR_COL);
                        clob.append(m.getConceptoRecaudo() != null ? m.getConceptoRecaudo() : "").append(SEPARADOR_COL);
                        clob.append(m.getValorPagoModerador() != null ? m.getValorPagoModerador().toPlainString() : "").append(SEPARADOR_COL);
                        clob.append(m.getNumFEVPagoModerador() != null ? m.getNumFEVPagoModerador() : "").append(SEPARADOR_COL);
                        clob.append(
                        	    m.getCodTecnologiaSalud() != null && m.getCodTecnologiaSalud().contains("-")
                        	        ? m.getCodTecnologiaSalud().substring(m.getCodTecnologiaSalud().indexOf("-") + 1)
                        	        : ""
                        	).append(SEPARADOR_COL);
                        clob.append(ripsData.getNumFactura()).append(SEPARADOR_COL);
                        clob.append(SEPARADOR_REG);
                    }
                }
            }
        }
        return clob.toString();
    }

    
    public String UrgenciasClob(RipsPrincipal ripsData) {
        StringBuilder clob = new StringBuilder();
        if (ripsData != null && ripsData.getUsuarios() != null) {
            for (Usuario u : ripsData.getUsuarios()) {
                if (u.getServicios() != null && u.getServicios().getUrgencias() != null && !u.getServicios().getUrgencias().isEmpty()) {
                    for (Urgencia ug : u.getServicios().getUrgencias()) {
                        clob.append(ug.getCodPrestador() != null ? ug.getCodPrestador() : "").append(SEPARADOR_COL);
                        clob.append(formatDate(ug.getFechaInicioAtencion(), DATETIME_FORMAT_YYYY_MM_DD_HH_MM)).append(SEPARADOR_COL);
                        clob.append(ug.getCausaMotivoAtencion() != null ? ug.getCausaMotivoAtencion() : "").append(SEPARADOR_COL);
                        clob.append(ug.getCodDiagnosticoPrincipal() != null ? ug.getCodDiagnosticoPrincipal() : "").append(SEPARADOR_COL);
                        clob.append(ug.getCodDiagnosticoPrincipalE() != null ? ug.getCodDiagnosticoPrincipalE() : "").append(SEPARADOR_COL);
                        clob.append(ug.getCodDiagnosticoRelacionadoE1() != null ? ug.getCodDiagnosticoRelacionadoE1() : "").append(SEPARADOR_COL);
                        clob.append(ug.getCodDiagnosticoRelacionadoE2() != null ? ug.getCodDiagnosticoRelacionadoE2() : "").append(SEPARADOR_COL);
                        clob.append(ug.getCodDiagnosticoRelacionadoE3() != null ? ug.getCodDiagnosticoRelacionadoE3() : "").append(SEPARADOR_COL);
                        clob.append(ug.getCondicionDestinoUsuarioEgreso() != null ? ug.getCondicionDestinoUsuarioEgreso() : "").append(SEPARADOR_COL);
                        clob.append(ug.getCodDiagnosticoCausaMuerte() != null ? ug.getCodDiagnosticoCausaMuerte() : "").append(SEPARADOR_COL);
                        clob.append(formatDate(ug.getFechaEgreso(), DATETIME_FORMAT_YYYY_MM_DD_HH_MM)).append(SEPARADOR_COL);
                        clob.append(ug.getConsecutivo() != null ? ug.getConsecutivo().toString() : "").append(SEPARADOR_COL);
                        clob.append(ripsData.getNumFactura()).append(SEPARADOR_COL);
                        clob.append(SEPARADOR_REG); 
                    }
                }
            }
        }
        return clob.toString();
    }
    
    
    public String RecienNacidosClob(RipsPrincipal ripsData) {
        StringBuilder clob = new StringBuilder();
        if (ripsData != null && ripsData.getUsuarios() != null) {
            for (Usuario u : ripsData.getUsuarios()) {
                if (u.getServicios() != null && u.getServicios().getRecienNacidos() != null && !u.getServicios().getRecienNacidos().isEmpty()) {
                    for (RecienNacido rn : u.getServicios().getRecienNacidos()) {
                        clob.append(rn.getCodPrestador() != null ? rn.getCodPrestador() : "").append(SEPARADOR_COL);
                        clob.append(rn.getTipoDocumentoIdentificacion() != null ? rn.getTipoDocumentoIdentificacion() : "").append(SEPARADOR_COL);
                        clob.append(rn.getNumDocumentoIdentificacion() != null ? rn.getNumDocumentoIdentificacion() : "").append(SEPARADOR_COL);
                        clob.append(formatDate(rn.getFechaNacimiento(), DATETIME_FORMAT_YYYY_MM_DD_HH_MM)).append(SEPARADOR_COL);
                        clob.append(rn.getEdadGestacional() != null ? rn.getEdadGestacional().toString() : "").append(SEPARADOR_COL);
                        clob.append(rn.getNumConsultasCPrenatal() != null ? rn.getNumConsultasCPrenatal().toString() : "").append(SEPARADOR_COL);
                        clob.append(rn.getCodSexoBiologico() != null ? rn.getCodSexoBiologico() : "").append(SEPARADOR_COL);
                        clob.append(rn.getPeso() != null ? rn.getPeso().toString() : "").append(SEPARADOR_COL);
                        clob.append(rn.getCodDiagnosticoPrincipal() != null ? rn.getCodDiagnosticoPrincipal() : "").append(SEPARADOR_COL);
                        clob.append(rn.getCondicionDestinoUsuarioEgreso() != null ? rn.getCondicionDestinoUsuarioEgreso() : "").append(SEPARADOR_COL);
                        clob.append(rn.getCodDiagnosticoCausaMuerte() != null ? rn.getCodDiagnosticoCausaMuerte() : "").append(SEPARADOR_COL);
                        clob.append(formatDate(rn.getFechaEgreso(), DATETIME_FORMAT_YYYY_MM_DD_HH_MM)).append(SEPARADOR_COL);
                        clob.append(rn.getConsecutivo() != null ? rn.getConsecutivo().toString() : "").append(SEPARADOR_COL);
                        clob.append(ripsData.getNumFactura()).append(SEPARADOR_COL);
                        clob.append(SEPARADOR_REG); 
                    }
                }
            }
        }
        return clob.toString();
    }
    
    
    public String OtrosServiciosClob(RipsPrincipal ripsData) {
        StringBuilder clob = new StringBuilder();
        if (ripsData != null && ripsData.getUsuarios() != null) {
            for (Usuario u : ripsData.getUsuarios()) {
                if (u.getServicios() != null && u.getServicios().getOtrosServicios() != null && !u.getServicios().getOtrosServicios().isEmpty()) {
                    for (OtroServicio os : u.getServicios().getOtrosServicios()) {
                        clob.append(os.getCodPrestador() != null ? os.getCodPrestador() : "").append(SEPARADOR_COL);
                        clob.append(os.getNumAutorizacion() != null ? os.getNumAutorizacion() : "").append(SEPARADOR_COL);
                        clob.append(os.getIdMIPRES() != null ? os.getIdMIPRES() : "").append(SEPARADOR_COL);
                        clob.append(formatDate(os.getFechaSuministroTecnologia(), DATETIME_FORMAT_YYYY_MM_DD_HH_MM)).append(SEPARADOR_COL);
                        clob.append(os.getTipoOS() != null ? os.getTipoOS() : "").append(SEPARADOR_COL);
                        clob.append(os.getCodTecnologiaSalud() != null ? os.getCodTecnologiaSalud() : "").append(SEPARADOR_COL);
                        clob.append(os.getNomTecnologiaSalud() != null ? os.getNomTecnologiaSalud() : "").append(SEPARADOR_COL);
                        clob.append(os.getCantidadOS() != null ? os.getCantidadOS().toString() : "").append(SEPARADOR_COL);
                        clob.append(os.getTipoDocumentoIdentificacion() != null ? os.getTipoDocumentoIdentificacion() : "").append(SEPARADOR_COL);
                        clob.append(os.getNumDocumentoIdentificacion() != null ? os.getNumDocumentoIdentificacion() : "").append(SEPARADOR_COL);
                        clob.append(os.getVrUnitOS() != null ? os.getVrUnitOS().toPlainString() : "").append(SEPARADOR_COL);
                        clob.append(os.getVrServicio() != null ? os.getVrServicio().toPlainString() : "").append(SEPARADOR_COL);
                        clob.append(os.getConceptoRecaudo() != null ? os.getConceptoRecaudo() : "").append(SEPARADOR_COL);
                        clob.append(os.getValorPagoModerador() != null ? os.getValorPagoModerador().toPlainString() : "").append(SEPARADOR_COL);
                        clob.append(os.getNumFEVPagoModerador() != null ? os.getNumFEVPagoModerador() : "").append(SEPARADOR_COL);
                        clob.append(os.getConsecutivo() != null ? os.getConsecutivo().toString() : "").append(SEPARADOR_COL);
                        clob.append(ripsData.getNumFactura()).append(SEPARADOR_COL);
                        clob.append(SEPARADOR_REG);
                    }
                }
            }
        }
        return clob.toString();
    }

    
    public String HospitalizacionClob(RipsPrincipal ripsData) {
        StringBuilder clob = new StringBuilder();
        if (ripsData != null && ripsData.getUsuarios() != null) {
            for (Usuario u : ripsData.getUsuarios()) {
                if (u.getServicios() != null && u.getServicios().getHospitalizacion() != null && !u.getServicios().getHospitalizacion().isEmpty()) {
                    for (Hospitalizacion h : u.getServicios().getHospitalizacion()) {
                        clob.append(h.getCodPrestador() != null ? h.getCodPrestador() : "").append(SEPARADOR_COL);
                        clob.append(h.getViaIngresoServicioSalud() != null ? h.getViaIngresoServicioSalud() : "").append(SEPARADOR_COL);
                        clob.append(formatDate(h.getFechaInicioAtencion(), DATETIME_FORMAT_YYYY_MM_DD_HH_MM)).append(SEPARADOR_COL);
                        clob.append(h.getNumAutorizacion() != null ? h.getNumAutorizacion() : "").append(SEPARADOR_COL);
                        clob.append(h.getCausaMotivoAtencion() != null ? h.getCausaMotivoAtencion() : "").append(SEPARADOR_COL);
                        clob.append(h.getCodDiagnosticoPrincipal() != null ? h.getCodDiagnosticoPrincipal() : "").append(SEPARADOR_COL);
                        clob.append(h.getCodDiagnosticoPrincipalE() != null ? h.getCodDiagnosticoPrincipalE() : "").append(SEPARADOR_COL);
                        clob.append(h.getCodDiagnosticoRelacionadoE1() != null ? h.getCodDiagnosticoRelacionadoE1() : "").append(SEPARADOR_COL);
                        clob.append(h.getCodDiagnosticoRelacionadoE2() != null ? h.getCodDiagnosticoRelacionadoE2() : "").append(SEPARADOR_COL);
                        clob.append(h.getCodDiagnosticoRelacionadoE3() != null ? h.getCodDiagnosticoRelacionadoE3() : "").append(SEPARADOR_COL);
                        clob.append(h.getCodComplicacion() != null ? h.getCodComplicacion() : "").append(SEPARADOR_COL);
                        clob.append(h.getCondicionDestinoUsuarioEgreso() != null ? h.getCondicionDestinoUsuarioEgreso() : "").append(SEPARADOR_COL);
                        clob.append(h.getCodDiagnosticoCausaMuerte() != null ? h.getCodDiagnosticoCausaMuerte() : "").append(SEPARADOR_COL);
                        clob.append(formatDate(h.getFechaEgreso(), DATETIME_FORMAT_YYYY_MM_DD_HH_MM)).append(SEPARADOR_COL);
                        clob.append(h.getConsecutivo() != null ? h.getConsecutivo().toString() : "").append(SEPARADOR_COL);
                        clob.append(ripsData.getNumFactura()).append(SEPARADOR_COL);
                        clob.append(SEPARADOR_REG); 
                    }
                }
            }
        }
        return clob.toString();
    }

    public String ValidacionFacturaClob(ResultadoValidacionFactura data) {
        if (data == null) return "";

        StringBuilder clob = new StringBuilder();
        clob.append(data.isResultState()).append(SEPARADOR_COL);
        clob.append(data.getProcesoId() != null ? data.getProcesoId() : "").append(SEPARADOR_COL);
        clob.append(data.getNumFactura() != null ? data.getNumFactura() : "").append(SEPARADOR_COL);
        clob.append(data.getCodigoUnicoValidacion() != null ? data.getCodigoUnicoValidacion() : "").append(SEPARADOR_COL);
        clob.append((data.getFechaValidacion() != null || data.getFechaRadicacion() != null)
        	        ? formatDate(data.getFechaValidacion() != null ? data.getFechaValidacion() : data.getFechaRadicacion(),DATETIME_FORMAT_YYYY_MM_DD) : ""	);
        clob.append(SEPARADOR_REG);

        return clob.toString();
    }
    
    public String ValidacionFacturaDetalleClob(ResultadoValidacionFactura data) {
        if (data == null || data.getResultadosValidacion() == null || data.getResultadosValidacion().isEmpty()) {
            return "";
        }

        StringBuilder clob = new StringBuilder();
        for (ResultadoValidacionDetalle r : data.getResultadosValidacion()) {
            clob.append(r.getClase() != null ? r.getClase() : "").append(SEPARADOR_COL);
            clob.append(r.getCodigo() != null ? r.getCodigo() : "").append(SEPARADOR_COL);
            clob.append(r.getDescripcion() != null ? r.getDescripcion().replace("'", "\"") : "").append(SEPARADOR_COL);
            clob.append(r.getObservaciones() != null ? r.getObservaciones() : "").append(SEPARADOR_COL);
            clob.append(r.getPathFuente() != null ? r.getPathFuente() : "").append(SEPARADOR_COL);
            clob.append(r.getFuente() != null ? r.getFuente() : "").append(SEPARADOR_COL);
            clob.append(data.getNumFactura()).append(SEPARADOR_COL);
            clob.append(SEPARADOR_REG);
        }

        return clob.toString();
    }

    
    private List<String> validarUsuarios(List<Usuario> usuarios) {
        List<String> errores = new ArrayList<>();

        if (usuarios == null || usuarios.isEmpty()) {
            errores.add("El archivo RIPS no contiene información de usuarios o la lista de usuarios está vacía.");
            return errores;
        }

	        for (int i = 0; i < usuarios.size(); i++) {
	            Usuario usuario = usuarios.get(i);
	            int usuarioIndex = i + 1; 
	
	            // U01: tipoDocumentoIdentificacion 
	            if (usuario.getTipoDocumentoIdentificacion().length() != 2) {
	                errores.add(String.format("U01 - Usuario %d: El tipo de documento de identificación debe tener 2 caracteres. Valor: '%s'", usuarioIndex, usuario.getTipoDocumentoIdentificacion()));
	            }
	            // U02: numDocumentoIdentificacion 
	            if (usuario.getNumDocumentoIdentificacion().length() < 4 || usuario.getNumDocumentoIdentificacion().length() > 20) {
	                errores.add(String.format("U02 - Usuario %d: El número de documento de identificación debe tener entre 4 y 20 caracteres. Valor: '%s'", usuarioIndex, usuario.getNumDocumentoIdentificacion()));
	            }
	            // U03: tipoUsuario 
	            if (usuario.getTipoUsuario().length() != 2) {
	                errores.add(String.format("U03 - Usuario %d: El tipo de usuario debe tener 2 caracteres. Valor: '%s'", usuarioIndex, usuario.getTipoUsuario()));
	            }
	            // U04: fechaNacimiento 
	            if (usuario.getFechaNacimiento() == null) {
	                errores.add(String.format("U04 - Usuario %d: La fecha de nacimiento no puede ser nula o vacía.", usuarioIndex));
	            }
	            // U05: codSexo
	            if (usuario.getCodSexo().length() != 1) {
	                errores.add(String.format("U05 - Usuario %d: El código de sexo debe tener 1 caracter. Valor: '%s'", usuarioIndex, usuario.getCodSexo()));
	            // U06: codPaisResidencia 
	            if (usuario.getCodPaisResidencia().length() != 3) {
	                errores.add(String.format("U06 - Usuario %d: El código de país de residencia debe tener 3 caracteres (ISO 3166-1). Valor: '%s'", usuarioIndex, usuario.getCodPaisResidencia()));
	            }
	            // U07: codMunicipioResidencia
	            if (usuario.getCodMunicipioResidencia().length() > 5) {
	                errores.add(String.format("U07 - Usuario %d: El código de municipio de residencia debe tener 0 o 5 caracteres. Valor: '%s'", usuarioIndex, usuario.getCodMunicipioResidencia()));
	            }
	            // U08: codZonaTerritorialResidencia
	            if (usuario.getCodZonaTerritorialResidencia().length() > 2) {
	                errores.add(String.format("U08 - Usuario %d: El código de zona territorial de residencia debe tener 0 o 2 caracteres. Valor: '%s'", usuarioIndex, usuario.getCodZonaTerritorialResidencia()));
	            }
	            // U09: incapacidad
	            if (usuario.getIncapacidad().length() != 2) {
	                errores.add(String.format("U09 - Usuario %d: El identificador de incapacidad debe tener 2 caracteres. Valor: '%s'", usuarioIndex, usuario.getIncapacidad()));
	            }
	            // U10: consecutivo
	            if (usuario.getConsecutivo() < 1) {
	                errores.add(String.format("U10 - Usuario %d: El número consecutivo debe ser mayor a 0. Valor: %d", usuarioIndex, usuario.getConsecutivo()));
	            }
	            // U11: codPaisOrigen
	            if (usuario.getCodPaisOrigen().length() != 3) {
	                errores.add(String.format("U11 - Usuario %d: El código de país de origen debe tener 3 caracteres (ISO 3166-1). Valor: '%s'", usuarioIndex, usuario.getCodPaisOrigen()));
	            }
	        }
	      }
        return errores;
    
    }

    private List<String> validarConsultas(List<Consulta> consultas) {
        List<String> errores = new ArrayList<>();

        // Verificar si la lista de consultas es nula o está vacía
        if (consultas == null || consultas.isEmpty()) {
            errores.add("El archivo de consultas no contiene información o la lista de consultas está vacía.");
            return errores;
        }

        for (int i = 0; i < consultas.size(); i++) {
            Consulta consulta = consultas.get(i);
            int consultaIndex = i + 1;

            // C01: codPrestador
            if (consulta.getCodPrestador() != null && consulta.getCodPrestador().length() > 12) {
                errores.add(String.format("C01 - Consulta %d: El código de prestador no puede tener más de 12 caracteres. Valor: '%s'", consultaIndex, consulta.getCodPrestador()));
            }
            // C02: fechaInicioAtencion
            if (consulta.getFechaInicioAtencion() == null) {
                errores.add(String.format("C02 - Consulta %d: La fecha de inicio de atención no puede ser nula.", consultaIndex));
            }
            // C03: numAutorizacion
            if (consulta.getNumAutorizacion() != null && consulta.getNumAutorizacion().length() > 30) {
                errores.add(String.format("C03 - Consulta %d: El número de autorización no puede tener más de 30 caracteres. Valor: '%s'", consultaIndex, consulta.getNumAutorizacion()));
            }
            // C04: codConsulta
            if (consulta.getCodConsulta() != null && consulta.getCodConsulta().length() > 6) {
                errores.add(String.format("C04 - Consulta %d: El código de consulta no puede tener más de 6 caracteres. Valor: '%s'", consultaIndex, consulta.getCodConsulta()));
            }
            // C05: modalidadGrupoServicioTecSal
            if (consulta.getModalidadGrupoServicioTecSal() != null && consulta.getModalidadGrupoServicioTecSal().length() > 2) {
                errores.add(String.format("C05 - Consulta %d: La modalidad del grupo de servicio técnico de salud no puede tener más de 2 caracteres. Valor: '%s'", consultaIndex, consulta.getModalidadGrupoServicioTecSal()));
            }
            // C06: grupoServicios
            if (consulta.getGrupoServicios() != null && consulta.getGrupoServicios().length() > 2) {
                errores.add(String.format("C06 - Consulta %d: El grupo de servicios no puede tener más de 2 caracteres. Valor: '%s'", consultaIndex, consulta.getGrupoServicios()));
            }
            // C07: codServicio
            if (consulta.getCodServicio() == null || (SysmanFunciones.toString(consulta.getCodServicio()).length() < 3 || SysmanFunciones.toString(consulta.getCodServicio()).length() > 4 )) {
                errores.add(String.format("C07 - Consulta %d: El código de servicio debe tener entre 3 y 4 caracteres. Valor: %d", consultaIndex, consulta.getCodServicio()));
            }
            // C08: finalidadTecnologiaSalud
            if (consulta.getFinalidadTecnologiaSalud() != null && consulta.getFinalidadTecnologiaSalud().length() > 2) {
                errores.add(String.format("C08 - Consulta %d: La finalidad de la tecnología de salud no puede tener más de 2 caracteres. Valor: '%s'", consultaIndex, consulta.getFinalidadTecnologiaSalud()));
            }
            // C09: causaMotivoAtencion
            if (consulta.getCausaMotivoAtencion() != null && consulta.getCausaMotivoAtencion().length() > 2) {
                errores.add(String.format("C09 - Consulta %d: La causa o motivo de atención no puede tener más de 2 caracteres. Valor: '%s'", consultaIndex, consulta.getCausaMotivoAtencion()));
            }
            // C10: codDiagnosticoPrincipal
            if (consulta.getCodDiagnosticoPrincipal() != null && !consulta.getCodDiagnosticoPrincipal().equals("0") && (consulta.getCodDiagnosticoPrincipal().length() < 4 || consulta.getCodDiagnosticoPrincipal().length() > 25)) {
                errores.add(String.format("C10 - Consulta %d: El código de diagnóstico principal debe tener entre 4 y 25 caracteres. Valor: '%s'", consultaIndex, consulta.getCodDiagnosticoPrincipal()));
            }
            // C11: codDiagnosticoRelacionado1
            if (consulta.getCodDiagnosticoRelacionado1() != null && !consulta.getCodDiagnosticoRelacionado1().equals("0") && (consulta.getCodDiagnosticoRelacionado1().length() < 4 || consulta.getCodDiagnosticoRelacionado1().length() > 25)) {
                errores.add(String.format("C11 - Consulta %d: El código de diagnóstico relacionado 1 debe tener entre 4 y 25 caracteres. Valor: '%s'", consultaIndex, consulta.getCodDiagnosticoRelacionado1()));
            }
            // C12: codDiagnosticoRelacionado2
            if (consulta.getCodDiagnosticoRelacionado2() != null && !consulta.getCodDiagnosticoRelacionado2().equals("0") && (consulta.getCodDiagnosticoRelacionado2().length() < 4 || consulta.getCodDiagnosticoRelacionado2().length() > 25)) {
                errores.add(String.format("C12 - Consulta %d: El código de diagnóstico relacionado 2 debe tener entre 4 y 25 caracteres. Valor: '%s'", consultaIndex, consulta.getCodDiagnosticoRelacionado2()));
            }
            // C13: codDiagnosticoRelacionado3
            if (consulta.getCodDiagnosticoRelacionado3() != null && !consulta.getCodDiagnosticoRelacionado3().equals("0") && (consulta.getCodDiagnosticoRelacionado3().length() < 4 || consulta.getCodDiagnosticoRelacionado3().length() > 25)) {
                errores.add(String.format("C13 - Consulta %d: El código de diagnóstico relacionado 3 debe tener entre 4 y 25 caracteres. Valor: '%s'", consultaIndex, consulta.getCodDiagnosticoRelacionado3()));
            }
            // C14: tipoDiagnosticoPrincipal
            if (consulta.getTipoDiagnosticoPrincipal() != null && consulta.getTipoDiagnosticoPrincipal().length() > 2) {
                errores.add(String.format("C14 - Consulta %d: El tipo de diagnóstico principal no puede tener más de 2 caracteres. Valor: '%s'", consultaIndex, consulta.getTipoDiagnosticoPrincipal()));
            }
            // C15: tipoDocumentoIdentificacion
            if (consulta.getTipoDocumentoIdentificacion() != null && consulta.getTipoDocumentoIdentificacion().length() > 2) {
                errores.add(String.format("C15 - Consulta %d: El tipo de documento de identificación no puede tener más de 2 caracteres. Valor: '%s'", consultaIndex, consulta.getTipoDocumentoIdentificacion()));
            }
            // C16: numDocumentoIdentificacion
            if (consulta.getNumDocumentoIdentificacion() != null && consulta.getNumDocumentoIdentificacion().length() > 20) {
                errores.add(String.format("C16 - Consulta %d: El número de documento de identificación no puede tener más de 20 caracteres. Valor: '%s'", consultaIndex, consulta.getNumDocumentoIdentificacion()));
            }
            // C18: conceptoRecaudo
            if (consulta.getConceptoRecaudo() != null && consulta.getConceptoRecaudo().length() > 2) {
                errores.add(String.format("C18 - Consulta %d: El concepto de recaudo no puede tener más de 2 caracteres. Valor: '%s'", consultaIndex, consulta.getConceptoRecaudo()));
            }
            // C21: consecutivo
            if (consulta.getConsecutivo() == null || consulta.getConsecutivo() < 1) {
                errores.add(String.format("C21 - Consulta %d: El número consecutivo debe ser mayor a 1. Valor: %d", consultaIndex, consulta.getConsecutivo()));
            }
        }
        return errores;
    }

    private List<String> validarMedicamentos(List<Medicamento> medicamentos) {
        List<String> errores = new ArrayList<>();

        // Verificar si la lista de medicamentos es nula o está vacía
        if (medicamentos == null || medicamentos.isEmpty()) {
            errores.add("El archivo de medicamentos no contiene información o la lista de medicamentos está vacía.");
            return errores;
        }

        for (int i = 0; i < medicamentos.size(); i++) {
            Medicamento medicamento = medicamentos.get(i);
            int medicamentoIndex = i + 1;

            // M01: codPrestador
            if (medicamento.getCodPrestador() != null && medicamento.getCodPrestador().length() > 12) {
                errores.add(String.format("M01 - Medicamento %d: El código del prestador no puede tener más de 12 caracteres. Valor: '%s'", medicamentoIndex, medicamento.getCodPrestador()));
            }
            // M05: codDiagnosticoPrincipal
            if (medicamento.getCodDiagnosticoPrincipal() != null && !(medicamento.getCodDiagnosticoPrincipal().equals("0")) && 
                (medicamento.getCodDiagnosticoPrincipal().length() < 4 || medicamento.getCodDiagnosticoPrincipal().length() > 25)) {
                errores.add(String.format("M05 - Medicamento %d: El código de diagnóstico principal debe tener entre 4 y 25 caracteres. Valor: '%s'", medicamentoIndex, medicamento.getCodDiagnosticoPrincipal()));
            }
            // M07: tipoMedicamento
            if (medicamento.getTipoMedicamento() != null && medicamento.getTipoMedicamento().length() != 2) {
                errores.add(String.format("M07 - Medicamento %d: El tipo de medicamento debe tener 2 carácter. Valor: '%s'", medicamentoIndex, medicamento.getTipoMedicamento()));
            }
            // M12: unidadMedida
            if (medicamento.getUnidadMedida() == null || (SysmanFunciones.toString(medicamento.getUnidadMedida()).length() < 1 || SysmanFunciones.toString(medicamento.getUnidadMedida()).length() > 20)) {
                errores.add(String.format("M12 - Medicamento %d: La cantidad de unidades debe estar entre 1 y 20 caracteres. Valor: %d", medicamentoIndex, medicamento.getUnidadMedida()));
            }
            // M13: valorUnitarioMedicamento
            if (medicamento.getVrUnitMedicamento() == null || medicamento.getVrUnitMedicamento().compareTo(BigDecimal.ZERO) <= 0) {
                errores.add(String.format("M13 - Medicamento %d: El valor unitario del medicamento debe ser mayor a cero. Valor: %s", medicamentoIndex, medicamento.getVrUnitMedicamento()));
            }
            // M14: valorTotalMedicamento
            if (medicamento.getVrServicio() == null || medicamento.getVrServicio().compareTo(BigDecimal.ZERO) <= 0) {
                errores.add(String.format("M14 - Medicamento %d: El valor total del medicamento debe ser mayor a cero. Valor: %s", medicamentoIndex, medicamento.getVrServicio()));
            } 
	        // M27: consecutivo
            if (medicamento.getConsecutivo() == null || 
                (SysmanFunciones.toString(medicamento.getConsecutivo()).length() < 1 || SysmanFunciones.toString(medicamento.getConsecutivo()).length() > 7)) {
                errores.add(String.format("M27 - Medicamento %d: El número consecutivo debe tener entre 1 y 7. Valor: %d", medicamentoIndex, medicamento.getConsecutivo()));
            }
        }
	        
        return errores;
    }

    private List<String> validarProcedimientos(List<Procedimiento> procedimientos) {
        List<String> errores = new ArrayList<>();

        // Verificar si la lista de procedimientos es nula o está vacía
        if (procedimientos == null || procedimientos.isEmpty()) {
            errores.add("El archivo de procedimientos no contiene información o la lista de procedimientos está vacía.");
            return errores;
        }

        for (int i = 0; i < procedimientos.size(); i++) {
            Procedimiento procedimiento = procedimientos.get(i);
            int procedimientoIndex = i + 1;

            // P01: codPrestador
            if (procedimiento.getCodPrestador() != null && procedimiento.getCodPrestador().length() > 12) {
                errores.add(String.format("P01 - Procedimiento %d: El código del prestador no puede tener más de 12 caracteres. Valor: '%s'", procedimientoIndex, procedimiento.getCodPrestador()));
            }
            // P03: idMIPRES
            if (procedimiento.getIdMIPRES() != null && procedimiento.getIdMIPRES().length() > 15) {
                errores.add(String.format("P03 - Procedimiento %d: El ID MIPRES no puede tener más de 15 caracteres. Valor: '%s'", procedimientoIndex, procedimiento.getIdMIPRES()));
            }
            // P04: numAutorizacion
            if (procedimiento.getNumAutorizacion() != null && procedimiento.getNumAutorizacion().length() > 30) {
                errores.add(String.format("P04 - Procedimiento %d: El número de autorización no puede tener más de 30 caracteres. Valor: '%s'", procedimientoIndex, procedimiento.getNumAutorizacion()));
            }
            // P05: codProcedimiento
            if (procedimiento.getCodProcedimiento() == null || procedimiento.getCodProcedimiento().length() != 6) {
                errores.add(String.format("P05 - Procedimiento %d: El código del procedimiento debe tener 6 caracteres. Valor: '%s'", procedimientoIndex, procedimiento.getCodProcedimiento()));
            }
            // P06: viaIngresoServicioSalud
            if (procedimiento.getViaIngresoServicioSalud() != null && procedimiento.getViaIngresoServicioSalud().length() != 2) {
                errores.add(String.format("P06 - Procedimiento %d: La vía de ingreso al servicio de salud debe tener 2 caracteres. Valor: '%s'", procedimientoIndex, procedimiento.getViaIngresoServicioSalud()));
            }
            // P07: modalidadGrupoServicioTecSal
            if (procedimiento.getModalidadGrupoServicioTecSal() != null && procedimiento.getModalidadGrupoServicioTecSal().length() != 2) {
                errores.add(String.format("P07 - Procedimiento %d: La modalidad del grupo de servicio técnico de salud debe tener 2 caracteres. Valor: '%s'", procedimientoIndex, procedimiento.getModalidadGrupoServicioTecSal()));
            }
            // P08: grupoServicios
            if (procedimiento.getGrupoServicios() != null && procedimiento.getGrupoServicios().length() != 2) {
                errores.add(String.format("P08 - Procedimiento %d: El grupo de servicios debe tener 2 caracteres. Valor: '%s'", procedimientoIndex, procedimiento.getGrupoServicios()));
            }
            // P09: codServicio
            if (procedimiento.getCodServicio() == null || (SysmanFunciones.toString(procedimiento.getCodServicio()).length() < 3 || SysmanFunciones.toString(procedimiento.getCodServicio()).length() > 4)) {
                errores.add(String.format("P09 - Procedimiento %d: El código del servicio debe tener entre 3 y 4 dígitos. Valor: %d", procedimientoIndex, procedimiento.getCodServicio()));
            }
            // P10: finalidadTecnologiaSalud
            if (procedimiento.getFinalidadTecnologiaSalud() != null && procedimiento.getFinalidadTecnologiaSalud().length() != 2) {
                errores.add(String.format("P10 - Procedimiento %d: La finalidad de la tecnología de salud debe tener 2 caracteres. Valor: '%s'", procedimientoIndex, procedimiento.getFinalidadTecnologiaSalud()));
            }
            // P11: tipoDocumentoIdentificacion
            if (procedimiento.getTipoDocumentoIdentificacion() != null && procedimiento.getTipoDocumentoIdentificacion().length() != 2) {
                errores.add(String.format("P11 - Procedimiento %d: El tipo de documento de identificación debe tener 2 caracteres. Valor: '%s'", procedimientoIndex, procedimiento.getTipoDocumentoIdentificacion()));
            }
            // P12: numDocumentoIdentificacion
            if (procedimiento.getNumDocumentoIdentificacion() != null && 
                (procedimiento.getNumDocumentoIdentificacion().length() < 4 || procedimiento.getNumDocumentoIdentificacion().length() > 20)) {
                errores.add(String.format("P12 - Procedimiento %d: El número de documento de identificación debe tener entre 4 y 20 caracteres. Valor: '%s'", procedimientoIndex, procedimiento.getNumDocumentoIdentificacion()));
            }
            // P13: codDiagnosticoPrincipal
            if (procedimiento.getCodDiagnosticoPrincipal() != null && 
                (procedimiento.getCodDiagnosticoPrincipal().length() < 4 || procedimiento.getCodDiagnosticoPrincipal().length() > 25)) {
                errores.add(String.format("P13 - Procedimiento %d: El código de diagnóstico principal debe tener entre 4 y 25 caracteres. Valor: '%s'", procedimientoIndex, procedimiento.getCodDiagnosticoPrincipal()));
            }
            // P14: codDiagnosticoRelacionado
            if (procedimiento.getCodDiagnosticoRelacionado() != null && 
                !(procedimiento.getCodDiagnosticoRelacionado().equals("0")) && 
                (procedimiento.getCodDiagnosticoRelacionado().length() < 4 || procedimiento.getCodDiagnosticoRelacionado().length() > 25)) {
                errores.add(String.format("P14 - Procedimiento %d: El código de diagnóstico relacionado no puede tener menos de 4 o más de 25 caracteres. Valor: '%s'", procedimientoIndex, procedimiento.getCodDiagnosticoRelacionado()));
            }
            // P15: codComplicacion
            if (procedimiento.getCodComplicacion() != null && 
                !(procedimiento.getCodComplicacion().equals("0")) && 
                (procedimiento.getCodComplicacion().length() < 4 || procedimiento.getCodComplicacion().length() > 25)) {
                errores.add(String.format("P15 - Procedimiento %d: El código de complicación no puede tener menos de 4 o más de 25 caracteres. Valor: '%s'", procedimientoIndex, procedimiento.getCodComplicacion()));
            }
            // P16: vrServicio
            if (procedimiento.getVrServicio() == null || procedimiento.getVrServicio().compareTo(BigDecimal.ZERO) <= 0) {
                errores.add(String.format("P16 - Procedimiento %d: El valor del servicio debe ser mayor a cero. Valor: %s", procedimientoIndex, procedimiento.getVrServicio()));
            }
            // P17: conceptoRecaudo
            if (procedimiento.getConceptoRecaudo() != null && procedimiento.getConceptoRecaudo().length() != 2) {
                errores.add(String.format("P17 - Procedimiento %d: El concepto de recaudo debe tener 2 caracteres. Valor: '%s'", procedimientoIndex, procedimiento.getConceptoRecaudo()));
            }
            // P20: consecutivo
            if (procedimiento.getConsecutivo() == null || procedimiento.getConsecutivo() < 1) {
                errores.add(String.format("P20 - Procedimiento %d: El número consecutivo debe ser mayor a 0. Valor: %d", procedimientoIndex, procedimiento.getConsecutivo()));
            }
        }
        return errores;
    }

    private List<String> validarUrgencias(List<Urgencia> urgencias) {
        List<String> errores = new ArrayList<>();

        // Verificar si la lista de urgencias es nula o está vacía
        if (urgencias == null || urgencias.isEmpty()) {
            errores.add("El archivo de urgencias no contiene información o la lista de urgencias está vacía.");
            return errores;
        }

        for (int i = 0; i < urgencias.size(); i++) {
            Urgencia urgencia = urgencias.get(i);
            int urgenciaIndex = i + 1;

            // R01: codPrestador
            if (urgencia.getCodPrestador() != null && urgencia.getCodPrestador().length() > 12) {
                errores.add(String.format("R01 - Urgencia %d: El código del prestador no puede tener más de 12 caracteres. Valor: '%s'", urgenciaIndex, urgencia.getCodPrestador()));
            }
            // R03: causaMotivoAtencion
            if (urgencia.getCausaMotivoAtencion() != null && urgencia.getCausaMotivoAtencion().length() != 2) {
                errores.add(String.format("R03 - Urgencia %d: La causa de motivo de atención debe tener 2 caracteres. Valor: '%s'", urgenciaIndex, urgencia.getCausaMotivoAtencion()));
            }
            // R04: codDiagnosticoPrincipal
            if (urgencia.getCodDiagnosticoPrincipal() != null && 
                (urgencia.getCodDiagnosticoPrincipal().length() < 4 || urgencia.getCodDiagnosticoPrincipal().length() > 25)) {
                errores.add(String.format("R04 - Urgencia %d: El código de diagnóstico principal debe tener entre 4 y 25 caracteres. Valor: '%s'", urgenciaIndex, urgencia.getCodDiagnosticoPrincipal()));
            }
            // R05: codDiagnosticoPrincipalE
            if (urgencia.getCodDiagnosticoPrincipalE() != null && 
                (urgencia.getCodDiagnosticoPrincipalE().length() < 4 || urgencia.getCodDiagnosticoPrincipalE().length() > 25)) {
                errores.add(String.format("R05 - Urgencia %d: El código de diagnóstico principal de egreso debe tener entre 4 y 25 caracteres. Valor: '%s'", urgenciaIndex, urgencia.getCodDiagnosticoPrincipalE()));
            }
            // R06: codDiagnosticoRelacionadoE1
            if (urgencia.getCodDiagnosticoRelacionadoE1() != null && 
                !(urgencia.getCodDiagnosticoRelacionadoE1().equals("0")) && 
                (urgencia.getCodDiagnosticoRelacionadoE1().length() < 4 || urgencia.getCodDiagnosticoRelacionadoE1().length() > 25)) {
                errores.add(String.format("R06 - Urgencia %d: El código de diagnóstico relacionado 1 de egreso no puede tener menos de 4 o más de 25 caracteres. Valor: '%s'", urgenciaIndex, urgencia.getCodDiagnosticoRelacionadoE1()));
            }
            // R07: codDiagnosticoRelacionadoE2
            if (urgencia.getCodDiagnosticoRelacionadoE2() != null && 
                !(urgencia.getCodDiagnosticoRelacionadoE2().equals("0")) && 
                (urgencia.getCodDiagnosticoRelacionadoE2().length() < 4 || urgencia.getCodDiagnosticoRelacionadoE2().length() > 25)) {
                errores.add(String.format("R07 - Urgencia %d: El código de diagnóstico relacionado 2 de egreso no puede tener menos de 4 o más de 25 caracteres. Valor: '%s'", urgenciaIndex, urgencia.getCodDiagnosticoRelacionadoE2()));
            }
            // R08: codDiagnosticoRelacionadoE3
            if (urgencia.getCodDiagnosticoRelacionadoE3() != null && 
                !(urgencia.getCodDiagnosticoRelacionadoE3().equals("0")) && 
                (urgencia.getCodDiagnosticoRelacionadoE3().length() < 4 || urgencia.getCodDiagnosticoRelacionadoE3().length() > 25)) {
                errores.add(String.format("R08 - Urgencia %d: El código de diagnóstico relacionado 3 de egreso no puede tener menos de 4 o más de 25 caracteres. Valor: '%s'", urgenciaIndex, urgencia.getCodDiagnosticoRelacionadoE3()));
            }
            // R09: condicionDestinoUsuarioEgreso
            if (urgencia.getCondicionDestinoUsuarioEgreso() != null && urgencia.getCondicionDestinoUsuarioEgreso().length() != 2) {
                errores.add(String.format("R09 - Urgencia %d: La condición de destino del usuario al egreso debe tener 2 caracteres. Valor: '%s'", urgenciaIndex, urgencia.getCondicionDestinoUsuarioEgreso()));
            }
            // R10: codDiagnosticoCausaMuerte
            if (urgencia.getCodDiagnosticoCausaMuerte() != null && 
                !(urgencia.getCodDiagnosticoCausaMuerte().equals("0")) && 
                (urgencia.getCodDiagnosticoCausaMuerte().length() < 4 || urgencia.getCodDiagnosticoCausaMuerte().length() > 25)) {
                errores.add(String.format("R10 - Urgencia %d: El código de diagnóstico de causa de muerte no puede tener menos de 4 o más de 25 caracteres. Valor: '%s'", urgenciaIndex, urgencia.getCodDiagnosticoCausaMuerte()));
            }
            // R12: consecutivo
            if (urgencia.getConsecutivo() == null || urgencia.getConsecutivo() < 1) {
                errores.add(String.format("R12 - Urgencia %d: El número consecutivo debe ser mayor a 0. Valor: %d", urgenciaIndex, urgencia.getConsecutivo()));
            }
        }
        return errores;
    }

    private List<String> validarHospitalizacion(List<Hospitalizacion> hospitalizaciones) {
        List<String> errores = new ArrayList<>();

        // Verificar si la lista de hospitalizaciones es nula o está vacía
        if (hospitalizaciones == null || hospitalizaciones.isEmpty()) {
            errores.add("El archivo de hospitalizaciones no contiene información o la lista de hospitalizaciones está vacía.");
            return errores;
        }

        for (int i = 0; i < hospitalizaciones.size(); i++) {
            Hospitalizacion hospitalizacion = hospitalizaciones.get(i);
            int hospitalizacionIndex = i + 1;

            // H01: codPrestador
            if (hospitalizacion.getCodPrestador() != null && hospitalizacion.getCodPrestador().length() > 12) {
                errores.add(String.format("H01 - Hospitalización %d: El código del prestador no puede tener más de 12 caracteres. Valor: '%s'", hospitalizacionIndex, hospitalizacion.getCodPrestador()));
            }
            // H02: viaIngresoServicioSalud
            if (hospitalizacion.getViaIngresoServicioSalud() != null && hospitalizacion.getViaIngresoServicioSalud().length() != 2) {
                errores.add(String.format("H02 - Hospitalización %d: La vía de ingreso al servicio de salud debe tener 2 caracteres. Valor: '%s'", hospitalizacionIndex, hospitalizacion.getViaIngresoServicioSalud()));
            }
            // H04: numAutorizacion
            if (hospitalizacion.getNumAutorizacion() != null && hospitalizacion.getNumAutorizacion().length() > 30) {
                errores.add(String.format("H04 - Hospitalización %d: El número de autorización no puede tener más de 30 caracteres. Valor: '%s'", hospitalizacionIndex, hospitalizacion.getNumAutorizacion()));
            }
            // H05: causaMotivoAtencion
            if (hospitalizacion.getCausaMotivoAtencion() != null && hospitalizacion.getCausaMotivoAtencion().length() != 2) {
                errores.add(String.format("H05 - Hospitalización %d: La causa de motivo de atención debe tener 2 caracteres. Valor: '%s'", hospitalizacionIndex, hospitalizacion.getCausaMotivoAtencion()));
            }
            // H06: codDiagnosticoPrincipal
            if (hospitalizacion.getCodDiagnosticoPrincipal() != null && 
                (hospitalizacion.getCodDiagnosticoPrincipal().length() < 4 || hospitalizacion.getCodDiagnosticoPrincipal().length() > 25)) {
                errores.add(String.format("H06 - Hospitalización %d: El código de diagnóstico principal debe tener entre 4 y 25 caracteres. Valor: '%s'", hospitalizacionIndex, hospitalizacion.getCodDiagnosticoPrincipal()));
            }
            // H07: codDiagnosticoPrincipalE
            if (hospitalizacion.getCodDiagnosticoPrincipalE() != null && 
                (hospitalizacion.getCodDiagnosticoPrincipalE().length() < 4 || hospitalizacion.getCodDiagnosticoPrincipalE().length() > 25)) {
                errores.add(String.format("H07 - Hospitalización %d: El código de diagnóstico principal de egreso debe tener entre 4 y 25 caracteres. Valor: '%s'", hospitalizacionIndex, hospitalizacion.getCodDiagnosticoPrincipalE()));
            }
            // H08: codDiagnosticoRelacionadoE1
            if (hospitalizacion.getCodDiagnosticoRelacionadoE1() != null && 
                !(hospitalizacion.getCodDiagnosticoRelacionadoE1().equals("0")) && 
                (hospitalizacion.getCodDiagnosticoRelacionadoE1().length() < 4 || hospitalizacion.getCodDiagnosticoRelacionadoE1().length() > 25)) {
                errores.add(String.format("H08 - Hospitalización %d: El código de diagnóstico relacionado 1 de egreso no puede tener menos de 4 o más de 25 caracteres. Valor: '%s'", hospitalizacionIndex, hospitalizacion.getCodDiagnosticoRelacionadoE1()));
            }
            // H09: codDiagnosticoRelacionadoE2
            if (hospitalizacion.getCodDiagnosticoRelacionadoE2() != null && 
                !(hospitalizacion.getCodDiagnosticoRelacionadoE2().equals("0")) && 
                (hospitalizacion.getCodDiagnosticoRelacionadoE2().length() < 4 || hospitalizacion.getCodDiagnosticoRelacionadoE2().length() > 25)) {
                errores.add(String.format("H09 - Hospitalización %d: El código de diagnóstico relacionado 2 de egreso no puede tener menos de 4 o más de 25 caracteres. Valor: '%s'", hospitalizacionIndex, hospitalizacion.getCodDiagnosticoRelacionadoE2()));
            }
            // H10: codDiagnosticoRelacionadoE3
            if (hospitalizacion.getCodDiagnosticoRelacionadoE3() != null && 
                !(hospitalizacion.getCodDiagnosticoRelacionadoE3().equals("0")) && 
                (hospitalizacion.getCodDiagnosticoRelacionadoE3().length() < 4 || hospitalizacion.getCodDiagnosticoRelacionadoE3().length() > 25)) {
                errores.add(String.format("H10 - Hospitalización %d: El código de diagnóstico relacionado 3 de egreso no puede tener menos de 4 o más de 25 caracteres. Valor: '%s'", hospitalizacionIndex, hospitalizacion.getCodDiagnosticoRelacionadoE3()));
            }
            // H11: codComplicacion
            if (hospitalizacion.getCodComplicacion() != null && 
                !(hospitalizacion.getCodComplicacion().equals("0")) && 
                (hospitalizacion.getCodComplicacion().length() < 4 || hospitalizacion.getCodComplicacion().length() > 25)) {
                errores.add(String.format("H11 - Hospitalización %d: El código de complicación no puede tener menos de 4 o más de 25 caracteres. Valor: '%s'", hospitalizacionIndex, hospitalizacion.getCodComplicacion()));
            }
            // H12: condicionDestinoUsuarioEgreso
            if (hospitalizacion.getCondicionDestinoUsuarioEgreso() != null && hospitalizacion.getCondicionDestinoUsuarioEgreso().length() != 2) {
                errores.add(String.format("H12 - Hospitalización %d: La condición de destino del usuario al egreso debe tener 2 caracteres. Valor: '%s'", hospitalizacionIndex, hospitalizacion.getCondicionDestinoUsuarioEgreso()));
            }
            // H13: codDiagnosticoCausaMuerte
            if (hospitalizacion.getCodDiagnosticoCausaMuerte() != null && 
                !(hospitalizacion.getCodDiagnosticoCausaMuerte().equals("0")) && 
                (hospitalizacion.getCodDiagnosticoCausaMuerte().length() < 4 || hospitalizacion.getCodDiagnosticoCausaMuerte().length() > 25)) {
                errores.add(String.format("H13 - Hospitalización %d: El código de diagnóstico de causa de muerte no puede tener menos de 4 o más de 25 caracteres. Valor: '%s'", hospitalizacionIndex, hospitalizacion.getCodDiagnosticoCausaMuerte()));
            }
            // H15: consecutivo
            if (hospitalizacion.getConsecutivo() == null || hospitalizacion.getConsecutivo() < 1) {
                errores.add(String.format("H15 - Hospitalización %d: El número consecutivo debe ser mayor a 0. Valor: %d", hospitalizacionIndex, hospitalizacion.getConsecutivo()));
            }
        }
        return errores;
    }

    private List<String> validarRecienNacido(List<RecienNacido> recienNacidos) {
        List<String> errores = new ArrayList<>();

        // Verificar si la lista de recién nacidos es nula o está vacía
        if (recienNacidos == null || recienNacidos.isEmpty()) {
            errores.add("El archivo de recién nacidos no contiene información o la lista de recién nacidos está vacía.");
            return errores;
        }

        for (int i = 0; i < recienNacidos.size(); i++) {
            RecienNacido recienNacido = recienNacidos.get(i);
            int recienNacidoIndex = i + 1;

            // N01: codPrestador
            if (recienNacido.getCodPrestador() != null && recienNacido.getCodPrestador().length() > 12) {
                errores.add(String.format("N01 - Recién Nacido %d: El código del prestador no puede tener más de 12 caracteres. Valor: '%s'", recienNacidoIndex, recienNacido.getCodPrestador()));
            }
            // N02: tipoDocumentoIdentificacion
            if (recienNacido.getTipoDocumentoIdentificacion() != null && recienNacido.getTipoDocumentoIdentificacion().length() != 2) {
                errores.add(String.format("N02 - Recién Nacido %d: El tipo de documento de identificación debe tener 2 caracteres. Valor: '%s'", recienNacidoIndex, recienNacido.getTipoDocumentoIdentificacion()));
            }
            // N03: numDocumentoIdentificacion
            if (recienNacido.getNumDocumentoIdentificacion() != null && recienNacido.getNumDocumentoIdentificacion().length() > 20) {
                errores.add(String.format("N03 - Recién Nacido %d: El número de documento de identificación no puede tener más de 20 caracteres. Valor: '%s'", recienNacidoIndex, recienNacido.getNumDocumentoIdentificacion()));
            }
            // N05: edadGestacional
            if (recienNacido.getEdadGestacional() == null || (recienNacido.getEdadGestacional() < 20 || recienNacido.getEdadGestacional() > 46)) {
                errores.add(String.format("N05 - Recién Nacido %d: La edad gestacional debe estar entre 20 y 46 semanas. Valor: %d", recienNacidoIndex, recienNacido.getEdadGestacional()));
            }
            // N06: numConsultasCPrenatal
            if (recienNacido.getNumConsultasCPrenatal() == null || (recienNacido.getNumConsultasCPrenatal() < 0 || recienNacido.getNumConsultasCPrenatal() > 99)) {
                errores.add(String.format("N06 - Recién Nacido %d: El número de consultas para el cuidado prenatal debe ser un número positivo. Valor: %d", recienNacidoIndex, recienNacido.getNumConsultasCPrenatal()));
            }
            // N07: codSexoBiologico
            if (recienNacido.getCodSexoBiologico() != null && recienNacido.getCodSexoBiologico().length() != 2) {
                errores.add(String.format("N07 - Recién Nacido %d: El código del sexo biológico debe tener 2 caracteres. Valor: '%s'", recienNacidoIndex, recienNacido.getCodSexoBiologico()));
            }
            // N08: peso
            if (recienNacido.getPeso() == null || (recienNacido.getPeso() < 500 || recienNacido.getPeso() > 5000)) {
                errores.add(String.format("N08 - Recién Nacido %d: El peso debe estar entre 500 y 5000 gramos. Valor: %.2f", recienNacidoIndex, recienNacido.getPeso()));
            }
            // N09: codDiagnosticoPrincipal
            if (recienNacido.getCodDiagnosticoPrincipal() != null && 
                (recienNacido.getCodDiagnosticoPrincipal().length() < 4 || recienNacido.getCodDiagnosticoPrincipal().length() > 25)) {
                errores.add(String.format("N09 - Recién Nacido %d: El código de diagnóstico principal debe tener entre 4 y 25 caracteres. Valor: '%s'", recienNacidoIndex, recienNacido.getCodDiagnosticoPrincipal()));
            }
            // N10: condicionDestinoUsuarioEgreso
            if (recienNacido.getCondicionDestinoUsuarioEgreso() != null && recienNacido.getCondicionDestinoUsuarioEgreso().length() != 2) {
                errores.add(String.format("N10 - Recién Nacido %d: La condición de destino del usuario al egreso debe tener 2 caracteres. Valor: '%s'", recienNacidoIndex, recienNacido.getCondicionDestinoUsuarioEgreso()));
            }
            // N11: codDiagnosticoCausaMuerte
            if (recienNacido.getCodDiagnosticoCausaMuerte() != null && 
                !(recienNacido.getCodDiagnosticoCausaMuerte().equals("0")) && 
                (recienNacido.getCodDiagnosticoCausaMuerte().length() < 4 || recienNacido.getCodDiagnosticoCausaMuerte().length() > 25)) {
                errores.add(String.format("N11 - Recién Nacido %d: El código de diagnóstico de causa de muerte no puede tener menos de 4 o más de 25 caracteres. Valor: '%s'", recienNacidoIndex, recienNacido.getCodDiagnosticoCausaMuerte()));
            }
            // N13: consecutivo
            if (recienNacido.getConsecutivo() == null || recienNacido.getConsecutivo() < 1) {
                errores.add(String.format("N13 - Recién Nacido %d: El número consecutivo debe ser mayor a 0. Valor: %d", recienNacidoIndex, recienNacido.getConsecutivo()));
            }
        }
        return errores;
    }

    private List<String> validarOtrosServicios(List<OtroServicio> otrosServicios) {
        List<String> errores = new ArrayList<>();

        // Verificar si la lista de otros servicios es nula o está vacía
        if (otrosServicios == null || otrosServicios.isEmpty()) {
            errores.add("El archivo de otros servicios no contiene información o la lista de otros servicios está vacía.");
            return errores;
        }

        for (int i = 0; i < otrosServicios.size(); i++) {
        	OtroServicio servicio = otrosServicios.get(i);
            int servicioIndex = i + 1;

            // S01: codPrestador
            if (servicio.getCodPrestador() != null && servicio.getCodPrestador().length() > 12) {
                errores.add(String.format("S01 - Otros Servicios %d: El código del prestador no puede tener más de 12 caracteres. Valor: '%s'", servicioIndex, servicio.getCodPrestador()));
            }
            // S02: numAutorizacion
            if (servicio.getNumAutorizacion() != null && servicio.getNumAutorizacion().length() > 30) {
                errores.add(String.format("S02 - Otros Servicios %d: El número de autorización no puede tener más de 30 caracteres. Valor: '%s'", servicioIndex, servicio.getNumAutorizacion()));
            }
            // S03: idMIPRES
            if (servicio.getIdMIPRES() != null && servicio.getIdMIPRES().length() > 15) {
                errores.add(String.format("S03 - Otros Servicios %d: El ID MIPRES no puede tener más de 15 caracteres. Valor: '%s'", servicioIndex, servicio.getIdMIPRES()));
            }
            // S05: tipoOS
            if (servicio.getTipoOS() != null && servicio.getTipoOS().length() != 2) {
                errores.add(String.format("S05 - Otros Servicios %d: El tipo de otros servicios debe tener 2 caracteres. Valor: '%s'", servicioIndex, servicio.getTipoOS()));
            }
            // S06: codTecnologiaSalud
            if (servicio.getCodTecnologiaSalud() != null && servicio.getCodTecnologiaSalud().length() > 20) {
                errores.add(String.format("S06 - Otros Servicios %d: El código de tecnología de salud no puede tener más de 20 caracteres. Valor: '%s'", servicioIndex, servicio.getCodTecnologiaSalud()));
            }
            // S07: nomTecnologiaSalud
            if (servicio.getNomTecnologiaSalud() != null && servicio.getNomTecnologiaSalud().length() > 60) {
                errores.add(String.format("S07 - Otros Servicios %d: El nombre de la tecnología de salud no puede tener más de 60 caracteres. Valor: '%s'", servicioIndex, servicio.getNomTecnologiaSalud()));
            }
            // S08: cantidadOS
            if (servicio.getCantidadOS() == null || servicio.getCantidadOS() < 1) {
                errores.add(String.format("S08 - Otros Servicios %d: La cantidad de otros servicios debe ser mayor a cero. Valor: %d", servicioIndex, servicio.getCantidadOS()));
            }
            // S09: tipoDocumentoIdentificacion
            if (servicio.getTipoDocumentoIdentificacion() != null && servicio.getTipoDocumentoIdentificacion().length() != 2) {
                errores.add(String.format("S09 - Otros Servicios %d: El tipo de documento de identificación debe tener 2 caracteres. Valor: '%s'", servicioIndex, servicio.getTipoDocumentoIdentificacion()));
            }
            // S10: numDocumentoIdentificacion
            if (servicio.getNumDocumentoIdentificacion() != null && servicio.getNumDocumentoIdentificacion().length() > 20) {
                errores.add(String.format("S10 - Otros Servicios %d: El número de documento de identificación no puede tener más de 20 caracteres. Valor: '%s'", servicioIndex, servicio.getNumDocumentoIdentificacion()));
            }
            // S11: vrUnitOS
            if (servicio.getVrUnitOS() == null || servicio.getVrUnitOS().compareTo(BigDecimal.ZERO) <= 0) {
                errores.add(String.format("S11 - Otros Servicios %d: El valor unitario de otros servicios debe ser mayor a cero. Valor: %s", servicioIndex, servicio.getVrUnitOS()));
            }
            // S12: vrServicio
            if (servicio.getVrServicio() == null || servicio.getVrServicio().compareTo(BigDecimal.ZERO) <= 0) {
                errores.add(String.format("S12 - Otros Servicios %d: El valor total de otros servicios debe ser mayor a cero. Valor: %s", servicioIndex, servicio.getVrServicio()));
            }
            // S13: conceptoRecaudo
            if (servicio.getConceptoRecaudo() != null && servicio.getConceptoRecaudo().length() != 2) {
                errores.add(String.format("S13 - Otros Servicios %d: El concepto de recaudo debe tener 2 caracteres. Valor: '%s'", servicioIndex, servicio.getConceptoRecaudo()));
            }
            // S15: numFEVPagoModerador
            if (servicio.getNumFEVPagoModerador() != null && servicio.getNumFEVPagoModerador().length() > 30) {
                errores.add(String.format("S15 - Otros Servicios %d: El número de factura electrónica de venta no puede tener más de 30 caracteres. Valor: '%s'", servicioIndex, servicio.getNumFEVPagoModerador()));
            }
            // S16: consecutivo
            if (servicio.getConsecutivo() == null || servicio.getConsecutivo() < 1) {
                errores.add(String.format("S16 - Otros Servicios %d: El número consecutivo debe ser mayor a 0. Valor: %d", servicioIndex, servicio.getConsecutivo()));
            }
        }
        return errores;
    }

}

