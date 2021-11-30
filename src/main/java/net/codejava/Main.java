package net.codejava;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfigBuilder;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutionException;
@Component
public class Main {
public static Machine brewingMachine;
public static Main main;
public static ParserOPCUA parser;
public static int test = 5;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        brewingMachine = Machine.getInstance();
        main = new Main();
        parser = new ParserOPCUA();
        parser.connect();

        System.out.println(brewingMachine.toString());
        System.out.println(main.toString());
        System.out.println(parser.toString());

        //read amount produced one time
        brewingMachine.setProductsProduced(parser.read("ns=6;s=::Program:Cube.Admin.ProdProcessedCount"));
        System.out.println("Amount produced= " + brewingMachine.getProductsProduced() + " beers");
        System.out.println(brewingMachine.toString());

        //read machine status(
        brewingMachine.setCurentState(parser.read ("ns=6;s=::Program:Cube.Status.StateCurrent"));
        System.out.println("Curent state= " + brewingMachine.getCurentState() );
        System.out.println(brewingMachine.toString());

        //monitor the amount produced
        parser.monitor("ns=6;s=::Program:Cube.Admin.ProdProcessedCount");
    }
    static void onSubscriptionValueChanged(UaMonitoredItem item, DataValue value) {
        int productsProducedLive=(int)value.getValue().getValue();
        System.out.println("Products produced live " + productsProducedLive);
        brewingMachine.setProductsProduced(productsProducedLive);
        System.out.println("Product count on machine" +  brewingMachine.getProductsProduced());
        System.out.println(brewingMachine.toString());
    }

        public static String getStuff(){
        return ("Variable given to controller "+brewingMachine.toString());
    }

    public static Machine getBrewingMachine() {
        return brewingMachine;
    }
}

