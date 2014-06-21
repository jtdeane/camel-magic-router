package ws.cogito.magic;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.XPathBuilder;
import org.apache.camel.dataformat.xmljson.XmlJsonDataFormat;

/**
 * Magic Route Builder
 */
public class MagicRouteBuilder extends RouteBuilder {
	
	public static String splitXpath = "//orders/order";

    public void configure() {
    	
		XPathBuilder splitXPath = new XPathBuilder (splitXpath);
		
		XmlJsonDataFormat xmlJsonFormat = new XmlJsonDataFormat();
		xmlJsonFormat.setForceTopLevelObject(true);
		
    	/**
    	 * Splitter - xpath expression
    	 */
    	from("activemq:emagic.orders").
    		split(splitXPath).
    		parallelProcessing().
    	to("activemq:emagic.order");

    	/**
    	 * Content Based Routing - simple expression - V1
    	 */
    	from("activemq:emagic.order").
    	choice().
    		when().simple("${in.body} contains 'Houdini'").
    			to("activemq:priority.order").
    		otherwise().
    			to("activemq:magic.order");

    	/**
    	 * Content Based Routing - Mediation, simple expression
    	 
    	from("activemq:emagic.order").
    	choice().
    		when().simple("${in.body} contains 'Houdini'").
				process(new Processor() {
	        		public void process(Exchange exchange) {	
	        			exchange.getIn().setHeader("VIP", "true");
	        		}
	        	}).
    			to("activemq:priority.order").
    		otherwise().
    			marshal(xmlJsonFormat).
    			transform(body().regexReplaceAll("@", "")).
    			to("activemq:magic.order");*/
    	
    	/**
    	 * Content Based Routing - Wire-Tap to ActiveMQ Topic 
    	 	 
    	from("activemq:emagic.order").
    		wireTap("direct:ministry").
    	to("activemq:magic.order");
    	
    	from("direct:ministry").
			choice().
				when().simple("${in.body} contains 'Elder Wand'").
					log("ILLEGAL MAGIC ALERT").
					to("activemq:topic:magic.alerts").		
				otherwise().
					log("...off into the ether");*/
    }
}