package net.codejava;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import jdk.jshell.JShell;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfigBuilder;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MonitoringMode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoredItemCreateRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoringParameters;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author athil
 */
public class ParserOPCUA {
    //attributes
    public OpcUaClient client;
    public ParserOPCUA() {
    }
    public void connect() {
        try
        {
            List<EndpointDescription> endpoints = DiscoveryClient.getEndpoints("opc.tcp://127.0.0.1").get();

            OpcUaClientConfigBuilder cfg = new OpcUaClientConfigBuilder();
            cfg.setEndpoint(endpoints.get(0));

            client = OpcUaClient.create(cfg.build());
            client.connect().get();

        }
        catch(Throwable ex)
        {
            ex.printStackTrace();
        }

    }
    public int read (String nodeIdString) throws ExecutionException, InterruptedException {
        NodeId nodeId = NodeId.parse(nodeIdString);

        DataValue dataValue = client.readValue(0, TimestampsToReturn.Both, nodeId).get();

        Variant variant = dataValue.getValue();
        int r = (int) variant.getValue();
        return r;
    }
    public void monitor (String nodeIdString) throws ExecutionException, InterruptedException {
        // suscribtion starts here
            NodeId nodeId = NodeId.parse(nodeIdString);

            // what to read
            ReadValueId readValueId = new ReadValueId(nodeId, AttributeId.Value.uid(), null, null);

            // create a subscription @ 1000ms
            UaSubscription subscription = client.getSubscriptionManager().createSubscription(1000.0).get();

            // important: client handle must be unique per item
            UInteger clientHandle = subscription.getSubscriptionId();
            MonitoringParameters parameters = new MonitoringParameters(
                    clientHandle,
                    1000.0,     // sampling interval
                    null,       // filter, null means use default
                    Unsigned.uint(10),   // queue size
                    true        // discard oldest
            );

            // creation request
            MonitoredItemCreateRequest request = new MonitoredItemCreateRequest(readValueId, MonitoringMode.Reporting, parameters);


            // setting the consumer after the subscription creation
            UaSubscription.ItemCreationCallback onItemCreated =  (item, id) -> item.setValueConsumer(Main::onSubscriptionValueChanged);



            List<UaMonitoredItem> items = subscription.createMonitoredItems(TimestampsToReturn.Both, Arrays.asList(request), onItemCreated).get();

            for (UaMonitoredItem item : items) {
                if (item.getStatusCode().isGood()) {
                    System.out.println("item created for nodeId=" + item.getReadValueId().getNodeId());
                } else{
                    System.out.println("failed to create item for nodeId=" + item.getReadValueId().getNodeId() + " (status=" + item.getStatusCode() + ")");
                }
            }

            // let the example run for 50 seconds then terminate
            Thread.sleep(50000);
    }
    }
