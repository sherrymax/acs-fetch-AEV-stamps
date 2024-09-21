package org.alfresco.behaviour;

import org.alfresco.action.Orchestrator;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.ContentServicePolicies;
import org.alfresco.repo.node.NodeServicePolicies;

import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GlobalPropertiesHandler;
import org.springframework.beans.factory.annotation.Value;


import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class RegulatoryAspectUpdatorBehaviour implements NodeServicePolicies.OnUpdatePropertiesPolicy {

    private PolicyComponent policyComponent;
    private NodeService nodeService;
    private ContentService contentService;


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


        //On Property Update
        policyComponent.bindClassBehaviour(NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME, ContentModel.TYPE_CONTENT,
                new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.TRANSACTION_COMMIT));

    }

    @Override
    public void onUpdateProperties(final NodeRef nodeRef, Map<QName, Serializable> beforeValues, Map<QName, Serializable> afterValues) {
        if (nodeService.exists(nodeRef)) {
            try {

                System.out.println(">>>> ***** >>>>> START OF onUpdateProperties() >>>> ***** >>>>> ");
                String nodeId = nodeRef.getId();
                Serializable fileName = nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
                System.out.println("NODE ID : "+nodeId);
                System.out.println("FILE NAME : "+fileName.toString());

                System.out.println("-------- PROPERTIES (BEFORE UPDATING) : START -------");
                System.out.println(beforeValues);
                System.out.println("-------- PROPERTIES (BEFORE UPDATING) : END -------");
                System.out.println("-------- PROPERTIES (AFTER UPDATING) : START -------");
                System.out.println(afterValues);
                System.out.println("-------- PROPERTIES (AFTER UPDATING) : END -------");

                System.out.println(">>>> ***** >>>>> END OF onUpdateProperties() >>>> ***** >>>>> ");

                //Sleep for 10 seconds
                TimeUnit.SECONDS.sleep(5);

                new Orchestrator().executeCalls(nodeRef, nodeId);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

    public void setPolicyComponent(PolicyComponent policyComponent) {
        this.policyComponent = policyComponent;
    }

}
