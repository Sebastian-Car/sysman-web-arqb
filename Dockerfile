FROM isim/wso2dss:latest

#Se copian drivers para tenerlos al interior de la imagen
COPY dropins/ /opt/wso2dss-3.5.1/repository/components/dropins/

#Se copian los servicios
#COPY servicios/ /opt/wso2dss-3.5.1/repository/deployment/server/dataservices/

#Se configuran las conexiones de bases de datos
COPY master-datasources.xml /opt/wso2dss-3.5.1/repository/conf/datasources/master-datasources.xml

#Dar permisos de ejecución a los .dbs
RUN chmod -R +x /opt/wso2dss-3.5.1/repository/deployment/server/dataservices
