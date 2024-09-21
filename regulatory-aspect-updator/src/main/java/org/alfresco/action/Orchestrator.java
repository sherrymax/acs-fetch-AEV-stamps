package org.alfresco.action;

import java.util.*;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.util.GlobalPropertiesHandler;

import java.io.Serializable;

public class Orchestrator {

    private InvokeREST invoker = new InvokeREST();
    private ArrayList<String> stampSubjectList = new ArrayList<>();

    private GlobalPropertiesHandler globalProperties = new GlobalPropertiesHandler();


    private NodeService nodeService;


    public static void main(String[] args) {
//        String nodeId = "90582a4d-5b54-4bdb-8ff7-00e08073a435";
//        new Orchestrator().executeCalls(nodeId);
    }

    public void executeCalls(final NodeRef nodeRef, String nodeId){
        ArrayList<String> stampNodeIdList = invoker.getStampAssociations(nodeId);
        String stampNodeId = "";

        if(stampNodeIdList.size() > 0){
            for(var i=0; i<stampNodeIdList.toArray().length; i++) {
                stampNodeId = (String) stampNodeIdList.toArray()[i];
                this.stampSubjectList = new InvokeREST().callGET(stampNodeId);
            }
        }

        System.out.println("DocNodeId >> " + nodeId + " >> stampNodeId >> " + stampNodeId + " >> stamp subject >> " + String.join(",", this.stampSubjectList));

        if(this.stampSubjectList.size() > 0){
            this.applyWebPublishedAspect(nodeRef);
        }



    }

    public void applyWebPublishedAspect(final NodeRef nodeRef) {

        Map<QName, Serializable> aspectProperties = new HashMap<QName, Serializable>();
        String nameSpace = globalProperties.getBoeingNamespace();

        QName QN_ASPECT_BOEING_ONEPPPM = QName.createQName(nameSpace, globalProperties.getBoeingAspectName());
        QName QN_PROP_REGULATORY_ASPECT_LIST = QName.createQName(nameSpace, globalProperties.getRegulatoryAspectListPropertyName());

        aspectProperties.put(QN_PROP_REGULATORY_ASPECT_LIST, String.join(",", this.stampSubjectList)); //Comma Separated Reference Values

        nodeService.addAspect(nodeRef, QN_ASPECT_BOEING_ONEPPPM, aspectProperties);
    }

}
