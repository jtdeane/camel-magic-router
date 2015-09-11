package ws.cogito.magic;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.XPathBuilder;

/**
 * Magic Route Builder
 */
public class MagicRouteBuilder extends RouteBuilder {
	
	public static String splitXpath = "//orders/order";

    public void configure() {
    	
    	/*
    	 * Route errors to DLQ after one retry and one second delay
    	 */
    	errorHandler(deadLetterChannel("activemq:emagic.dead").
    			maximumRedeliveries(1).redeliveryDelay(1000));
    	
		XPathBuilder splitXPath = new XPathBuilder (splitXpath);
		
    	/*
    	 * Splitter - xpath expression
    	 */
    	from("activemq:emagic.orders").
    		split(splitXPath).
    		parallelProcessing().
    	to("activemq:emagic.order");

    	/*
    	 * Content Based Routing - simple expression
    	 */
    	from("activemq:emagic.order").
    	choice().
    		when().simple("${in.body} contains 'Houdini'").
    			to("activemq:priority.order").
    		otherwise().
    			to("activemq:magic.order");
    }
}