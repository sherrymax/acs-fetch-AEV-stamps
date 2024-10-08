package org.alfresco.action;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.util.GlobalPropertiesHandler;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class InvokeREST {

    private static Log logger = LogFactory.getLog(InvokeREST.class);

    private GlobalPropertiesHandler globalProperties;
    private String ACS_HOSTNAME;
    private ServiceRegistry serviceRegistry;


    public InvokeREST(){
        logger.debug(">>>>>> InvokeREST CONSTRUCTOR <<<<<<<<");
        this.globalProperties = new GlobalPropertiesHandler();
        this.ACS_HOSTNAME = globalProperties.getAlfrescoHostName();
        logger.debug(">>>>>> this.ACS_HOSTNAME <<<<<<<< "+this.ACS_HOSTNAME);
    }


    private String getACSAuthenticationHeader() {
        String username = this.globalProperties.getAlfrescoUserName();
        String password = this.globalProperties.getAlfrescoPassword();
        String credentials = username + ":" + password;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        return "Basic " + encodedCredentials;
    }

    public static void main(String[] args) {
//    	InvokeREST invokeREST = new InvokeREST();
//		invokeREST.getStampAssociations("90582a4d-5b54-4bdb-8ff7-00e08073a435");
//        invokeREST.callGET("4ded98ec-60d7-4bf1-89bf-5e871bef34e9");
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public ArrayList<String> callGET(ContentService contentService, String nodeId) {

        logger.debug(">>>>>> Node ID Inside callGET <<<<<<<< "+nodeId);

        ArrayList<String> stampSubjectList = new ArrayList<>();
        NodeRef nodeRef = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, nodeId);

        try{
            ContentReader reader = contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
            if (reader.exists()) {
                System.out.println(">>>>>> INSIDE callGET.reader.exists() <<<<<<<< "+nodeId);
                InputStream inputStream = reader.getContentInputStream();
                stampSubjectList = new QueryXMLAttributes().getSubjectFromStamp(inputStream);
                System.out.println(">>>>>> stampSubjectList <<<<<<<< "+String.join(",", stampSubjectList));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stampSubjectList;
    }

    public ArrayList<String> callGET_old(String nodeId) {

        logger.debug(">>>>>> Node ID Inside callGET <<<<<<<< "+nodeId);


        ArrayList<String> stampSubjectList = new ArrayList<>();

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {

            String url = this.ACS_HOSTNAME + "/alfresco/api/-default-/public/alfresco/versions/1/nodes/"+nodeId+"/content?attachment=true";
            HttpGet getRequest = new HttpGet(url);
            logger.debug("$$$$$ AUTH HEADER >>> "+this.getACSAuthenticationHeader());
            getRequest.setHeader("Authorization", this.getACSAuthenticationHeader());
            getRequest.setHeader("Cache-Control", "no-cache");

            logger.debug(">>>>>> callGET URL <<<<<<<< "+url);


            HttpResponse response = httpClient.execute(getRequest);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
            }

            InputStream inputStream = response.getEntity().getContent();

            stampSubjectList = new QueryXMLAttributes().getSubjectFromStamp(inputStream);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stampSubjectList;
    }


    public ArrayList<String> getStampAssociations(String nodeId) {

        String requestURL = this.ACS_HOSTNAME + "/alfresco/api/-default-/public/alfresco/versions/1/nodes/" + nodeId
                + "/targets?include=properties&where=(assocType='oa:annotates')";
        logger.debug(requestURL);

        Gson gson = new Gson();

        ArrayList<String> stampNodeList = new ArrayList<>();

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {

            HttpGet getRequest = new HttpGet(requestURL);
            getRequest.setHeader("Authorization", this.getACSAuthenticationHeader());
            getRequest.setHeader("Cache-Control", "no-cache");

//            TimeUnit.SECONDS.sleep(1);
            HttpResponse response = httpClient.execute(getRequest);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

            String apiResponse;

            while ((apiResponse = br.readLine()) != null) {
                logger.debug("API Response from Server ....");
                logger.debug(apiResponse);

                // Extraction Step 1 : Convert API Response String to JsonElement
                JsonElement je = gson.fromJson(apiResponse, JsonElement.class);

                // Extraction Step 2 : Convert JsonElement to JsonObject
                JsonObject jo = je.getAsJsonObject();

                // Extraction Step 3 : Get the property from JsonObject
                JsonObject list = jo.getAsJsonObject("list");

                // Get the list of entries from JsonObject
                JsonArray entriesArr = list.getAsJsonArray("entries");
                logger.debug(entriesArr.size());

                for(var i=0; i<entriesArr.size(); i++){
                    JsonObject jo_entry = gson.fromJson(entriesArr.get(i), JsonObject.class);
                    JsonElement je_entry = jo_entry.asMap().get("entry");
                    JsonObject jo_entry_obj = je_entry.getAsJsonObject();

                    stampNodeList.add(jo_entry_obj.get("id").getAsString());
                }
            }

            } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stampNodeList;
    }
}
