package ws.cogito.magic;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.XPathBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.commons.io.IOUtils;
import org.junit.Test;


public class MagicRouteBuilderTest extends CamelTestSupport  {
	
	@Override
	protected CamelContext createCamelContext() throws Exception {
		
		CamelContext context = super.createCamelContext();	

		//setup routes to Mock Endpoints
		context.addRoutes(new RouteBuilder() {
			
			public void configure() {
		    	
				//test xpath split
		    	from ("direct:split").
		    		split(new XPathBuilder (MagicRouteBuilder.splitXpath)).
		    	to("mock:order");
		    	
		    	from("direct:cbr").
		    		choice().
		    			when().simple("${in.body} contains 'Houdini'").
		    				to("mock:normal").
	    				otherwise().
	    					to("mock:priority");		    	
			}
		});		

		return context;
	}	


	@Test
	public void testSplitLogic() throws Exception {
		
		//Set expectations
		MockEndpoint order = getMockEndpoint("mock:order");
		order.expectedMessageCount(3);			
		
		//read in a small batch
		String xml = IOUtils.toString(this.getClass().
				getResourceAsStream("batch.xml"), "UTF-8");
		
		//execute the test
		template.sendBody("direct:split", xml);
		
		//should be three orders in the batch
		order.assertIsSatisfied();
	}
	
	@Test
	public void testChoiceLogic() throws Exception {
		
		//Set expectations
		MockEndpoint priority = getMockEndpoint("mock:priority");
		priority.expectedMessageCount(1);

		MockEndpoint normal = getMockEndpoint("mock:normal");
		normal.expectedMessageCount(2);			
		
		String xml1 = IOUtils.toString(this.getClass().
				getResourceAsStream("order1.xml"), "UTF-8");
		
		String xml2 = IOUtils.toString(this.getClass().
				getResourceAsStream("order2.xml"), "UTF-8");
	
		String xml3 = IOUtils.toString(this.getClass().
				getResourceAsStream("order3.xml"), "UTF-8");
		
		template.sendBody("direct:cbr", xml1);
		template.sendBody("direct:cbr", xml2);
		template.sendBody("direct:cbr", xml3);
		
		normal.assertIsSatisfied();
		priority.assertIsSatisfied();
	}
}