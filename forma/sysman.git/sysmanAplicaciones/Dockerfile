FROM jboss/wildfly:11.0.0.Final

ARG ARTIFACT_NAME
ARG CONTEXT_ROOT_URL

COPY oracle/ /opt/jboss/wildfly/modules/system/layers/base/com/oracle/

# Copiar el EAR al directorio de almacenamiento en el contenedor para crear el enlace simbolico
COPY target/${ARTIFACT_NAME} /opt/jboss/artifact/

# Crear enlace simbolico del artefacto a la carpeta deployments para estandarizar el context-root de la URL
RUN ln -s /opt/jboss/artifact/${ARTIFACT_NAME} /opt/jboss/wildfly/standalone/deployments/${CONTEXT_ROOT_URL}

#Reescribir el standalone.conf configurando la asignación de memoria para la JVM
COPY standalone.conf /opt/jboss/wildfly/bin/standalone.conf

# Exponer los puertos
EXPOSE 8080 9990

CMD ["/bin/sh", "-c", "/opt/jboss/wildfly/bin/standalone.sh -c standalone-full.xml -b 0.0.0.0 -bmanagement 0.0.0.0"]
