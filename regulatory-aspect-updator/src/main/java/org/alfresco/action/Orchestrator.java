package org.alfresco.action;

import java.util.*;

import org.alfresco.behaviour.RegulatoryAspectUpdatorBehaviour;
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

    public Orchestrator() {
        RegulatoryAspectUpdatorBehaviour behaviour = new RegulatoryAspectUpdatorBehaviour();
        this.nodeService = behaviour.getNodeService();
    }

    public ArrayList<String> executeCalls(final NodeRef nodeRef, String nodeId, Boolean isAlreadyStampAssociation){
        ArrayList<String> stampNodeIdList = new ArrayList<String>();

        System.out.println("NODE ID Inside executeCalls() >>> "+nodeId);

        if(isAlreadyStampAssociation == true){
            System.out.println(" Node ID "+nodeId+" is already a Stamp ASSOCIATION");
            stampNodeIdList.add(nodeId);
        }else {
            System.out.println(" Node ID "+nodeId+" is NOT A Stamp ASSOCIATION");
            stampNodeIdList = invoker.getStampAssociations(nodeId);
        }
        String stampNodeId = "";

        System.out.println(" Stamp Nodes List Size = "+stampNodeIdList.size());
        System.out.println(" Invoking FOR LOOP to Get Content of Stamp ASSOCIATION ");
        if(stampNodeIdList.size() > 0){
            for(var i=0; i<stampNodeIdList.toArray().length; i++) {
                stampNodeId = (String) stampNodeIdList.toArray()[i];
                System.out.println(" Iteration # "+i+" to Get Content of Stamp ASSOCIATION >>> StampNodeId = "+stampNodeId);
                this.stampSubjectList = new InvokeREST().callGET(stampNodeId);
            }
        }

        return this.stampSubjectList;

    }



}
