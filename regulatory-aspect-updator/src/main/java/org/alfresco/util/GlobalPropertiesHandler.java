package org.alfresco.util;

import org.alfresco.behaviour.RegulatoryAspectUpdatorBehaviour;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GlobalPropertiesHandler {
    private static Log logger = LogFactory.getLog(GlobalPropertiesHandler.class);
    private static String alfrescoHostName;
    private static String alfrescoUserName;
    private static String alfrescoPassword;
    private static String boeingNamespace;
    private static String boeingAspectName;
    private static String regulatoryAspectListPropertyName;

    public String getAlfrescoHostName() {
        return this.alfrescoHostName;
    }
    public void setAlfrescoHostName(String hostName) {
        this.alfrescoHostName = hostName;
    }


    public String getAlfrescoUserName() {
        return this.alfrescoUserName;
    }
    public void setAlfrescoUserName(String userName) {
        this.alfrescoUserName = userName;
    }


    public String getAlfrescoPassword() {
        return this.alfrescoPassword;
    }
    public void setAlfrescoPassword(String pwd) {
        this.alfrescoPassword = pwd;
    }


    public String getBoeingNamespace() {
        return this.boeingNamespace;
    }
    public void setBoeingNamespace(String ns) {
        this.boeingNamespace = ns;
    }


    public String getBoeingAspectName() {
        return this.boeingAspectName;
    }
    public void setBoeingAspectName(String aspectName) {
        this.boeingAspectName = aspectName;
    }


    public String getRegulatoryAspectListPropertyName() {
        return this.regulatoryAspectListPropertyName;
    }
    public void setRegulatoryAspectListPropertyName(String propName) {
        this.regulatoryAspectListPropertyName = propName;
    }

}
