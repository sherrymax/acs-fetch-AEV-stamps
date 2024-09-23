package org.alfresco.behaviour;

import org.alfresco.action.Orchestrator;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;

import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GlobalPropertiesHandler;
import org.springframework.beans.factory.annotation.Value;


import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class RegulatoryAspectUpdatorBehaviour implements NodeServicePolicies.OnUpdatePropertiesPolicy {

    private PolicyComponent policyComponent;
    private NodeService nodeService;
    private ContentService contentService;

    public ArrayList<String> stampSubjectList;

    private GlobalPropertiesHandler globalProperties = new GlobalPropertiesHandler();



    //  FETCHING VALUES FROM alfresco-global.properties - START
    @Value("${boeing.alfresco.hostname}")
    private String ACS_HOSTNAME;

    @Value("${boeing.alfresco.username}")
    private String ACS_USERNAME;

    @Value("${boeing.alfresco.password}")
    private String ACS_PASSWORD;
    @Value("${boeing.namespace}")
    private String NAMESPACE_BOEING;
    @Value("${boeing.aspect.name}")
    private String ASPECT_BOEING_ONEPPPM;
    @Value("${boeing.regulatory-aspect-list.property}")
    private String PROP_REGULATORY_ASPECT_LIST;

    //  FETCHING VALUES FROM alfresco-global.properties - END

    public void init() {

        GlobalPropertiesHandler globalPropertiesHandler = new GlobalPropertiesHandler();
        globalPropertiesHandler.setAlfrescoHostName(this.ACS_HOSTNAME);
        globalPropertiesHandler.setAlfrescoUserName(this.ACS_USERNAME);
        globalPropertiesHandler.setAlfrescoPassword(this.ACS_PASSWORD);
        globalPropertiesHandler.setBoeingNamespace(this.NAMESPACE_BOEING);
        globalPropertiesHandler.setBoeingAspectName(this.ASPECT_BOEING_ONEPPPM);
        globalPropertiesHandler.setRegulatoryAspectListPropertyName(this.PROP_REGULATORY_ASPECT_LIST);

        System.out.println("*** **** **** NODE SERVICE >> "+this.nodeService);

        //On Property Update
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME, ContentModel.TYPE_CONTENT,
                new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

    }

    @Override
    public void onUpdateProperties(final NodeRef nodeRef, Map<QName, Serializable> beforeValues, Map<QName, Serializable> afterValues) {

        AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Object>() {
            public Object doWork() throws Exception {

                if (nodeService.exists(nodeRef)) {
                    try {

                        String nodeId = nodeRef.getId();
                        System.out.println("NODE ID : "+nodeId);
                        String fileName = nodeService.getProperty(nodeRef, ContentModel.PROP_NAME).toString();
                        System.out.println("FILE NAME : "+fileName.toString());

                        if (fileName.trim().indexOf(".docx") != -1) {

                            System.out.println(">>>> ***** >>>>> START OF onUpdateProperties() >>>> ***** >>>>> ");
                            System.out.println("-------- PROPERTIES (BEFORE UPDATING) : START -------");
                            System.out.println(beforeValues);
                            System.out.println("-------- PROPERTIES (BEFORE UPDATING) : END -------");
                            System.out.println("-------- PROPERTIES (AFTER UPDATING) : START -------");
                            System.out.println(afterValues);
                            System.out.println("-------- PROPERTIES (AFTER UPDATING) : END -------");
                            System.out.println(">>>> ***** >>>>> END OF onUpdateProperties() >>>> ***** >>>>> ");

                            //Sleep for 5 seconds
                            TimeUnit.SECONDS.sleep(5);
                            ArrayList<String> stampSubjectList = new Orchestrator().executeCalls(nodeRef, nodeId);

                            System.out.println("DocNodeId >> " + nodeId + " >> stamp subject >> " + String.join(",", stampSubjectList));
                            System.out.println("stampSubjectList.size() = "+stampSubjectList.size());

                            if(stampSubjectList.size() > 0){
                                new RegulatoryAspectUpdatorBehaviour().applyWebPublishedAspect(nodeService, nodeRef, stampSubjectList);
                            }
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }


                return null;
            }

        }, AuthenticationUtil.getSystemUserName());


    }

    public void applyWebPublishedAspect(NodeService nodeService, final NodeRef nodeRef, ArrayList<String> stampSubjectList) {

        System.out.println(" Step 1 ");
//        Map<QName, Serializable> aspectProperties = new HashMap<QName, Serializable>();
        Map<QName, Serializable> aspectProperties = new HashMap<QName, Serializable>();

        System.out.println(" Step 2 ");
        String nameSpace = globalProperties.getBoeingNamespace();

        System.out.println(" Step 3 ");
        QName QN_ASPECT_BOEING_ONEPPPM = QName.createQName(nameSpace, globalProperties.getBoeingAspectName());
        System.out.println(" Step 4 ");
        QName QN_PROP_REGULATORY_ASPECT_LIST = QName.createQName(nameSpace, globalProperties.getRegulatoryAspectListPropertyName());

        System.out.println(" Step 5 ");
        System.out.println(" aspectProperties "+aspectProperties);
        System.out.println(" QN_PROP_REGULATORY_ASPECT_LIST "+QN_PROP_REGULATORY_ASPECT_LIST);
        System.out.println(" this.stampSubjectList "+stampSubjectList);
        aspectProperties.put(QN_PROP_REGULATORY_ASPECT_LIST, String.join(",", stampSubjectList)); //Comma Separated Reference Values
        System.out.println(" aspectProperties "+aspectProperties);
        System.out.println(" QN_ASPECT_BOEING_ONEPPPM "+QN_ASPECT_BOEING_ONEPPPM);
        System.out.println(">>> nodeRef >>> "+nodeRef);
        System.out.println(">>> NODE SERVICE >>> "+new RegulatoryAspectUpdatorBehaviour().getNodeService());

        nodeService.addAspect(nodeRef, QN_ASPECT_BOEING_ONEPPPM, aspectProperties);


        System.out.println("ASPECT SAVED SUCCESSFULLY >>> "+String.join(",", stampSubjectList));

    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public NodeService getNodeService() {
        return this.nodeService;
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

}
