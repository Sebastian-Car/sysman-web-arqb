package com.sysman.email;

/**
 * Clase encargada de gestionar un correo electronico teniendo en cuenta sus atributos principales
 * 
 * @author jeguerrero
 *
 */
public class EmailPojo {
    /**
     * Variable encargada de almacenar temporalmente la Direccion de correo electronico desde donde se va a enviar
     */
    private String from;
    /**
     * Variable encargada de almacenar temporalmente la Direccion de correo electronico hacia donde va dirigido el Email
     */
    private String to;
    /**
     * Variable encargada de almacenar temporalmente el asunto del Email
     */
    private String subject;
    /**
     * Variable encargada de almacenar temporalmente el reporte como un arreglo de bytes
     */
    private byte[] report;
    /**
     * Variable encargada de almacenar temporalmente el cuerpo del correo ya sea en formato html para darle algun formato
     */
    private String body;
    /**
     * Variable encargada de almacenar temporalmente el nombre del reporte
     */
    private String reportName;

    /**
     * 
     */
    private String ruta;

    /**
     * Obtencion del atributo from
     * 
     * @return from
     */
    public String getFrom() {
        return from;
    }

    /**
     * 
     * @param from
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * Obtencion del atributo to
     * 
     * @return to
     */
    public String getTo() {
        return to;
    }

    /**
     * 
     * @param to
     */
    public void setTo(String to) {
        this.to = to;
    }

    /**
     * obtencion del atributo
     * 
     * @return subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * 
     * @param subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * obtencion de atributo report
     * 
     * @return report
     */
    public byte[] getReport() {
        return report;
    }

    /**
     * 
     * @param report
     */
    public void setReport(byte[] report) {
        this.report = report;
    }

    /**
     * Obtencion de atributo body
     * 
     * @return body
     */
    public String getBody() {
        return body;
    }

    /**
     * 
     * @param body
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * 
     * @return
     */
    public String getReportName() {
        return reportName;
    }

    /**
     * 
     * @param reportName
     */
    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

}
