package com.nvisia.examples.camel.orderrouter;

import java.util.*;

import org.apache.camel.*;
import org.apache.camel.component.servlet.*;
import org.apache.camel.model.rest.*;
import org.apache.camel.processor.aggregate.*;
import org.apache.camel.spring.boot.*;
import org.apache.camel.swagger.servlet.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.context.embedded.*;
import org.springframework.context.annotation.*;

/**
 * Spring boot application that defines the routes available for order routing.
 * 
 * @author Michael Hoffman, NVISIA
 *
 */
@SpringBootApplication
public class OrderRouter extends FatJarRouter {

   @Override
   public void configure() {
      // Start by building an instance of RestConfigurationDefinition. Need to
      // specify the component we are going to use for enabling REST endpoints,
      // specifically CamelServlet in this case. Set the binding mode to JSON.
      restConfiguration().
            // Leverage the CamelServlet component for the REST DSL
            component("servlet").
            // Bind using JSON
            bindingMode(RestBindingMode.json).
            // I like pretty things...
            dataFormatProperty("prettyPrint", "true").
            // This is the context path to be used for Swagger API documentation
            apiContextPath("api-doc").
            // Properties for Swagger
            // Title of the API
      apiProperty("api.title", "Order Management API").
            // Version of the API
            apiProperty("api.version", "1.0.0").
            // CORS (resource sharing) enablement
            apiProperty("cors", "true").
            // Use localhost for calls
            apiProperty("host", "localhost:8083").
            // Set base path
            apiProperty("base.path", "nvisia-order-router-camel-service/api");

      // Definition of the post order endpoint
      rest("/orderRouter").
            // This is a POST method call for routing an order using the
            // order form
      post().
            // Description of what the method does
            description("Routes a new order to the order management service").
            // Define the type used for input
            type(OrderForm.class).
            // Define the type used for output, in this case the order
            outType(String.class).
            // Next, define where the message is routed to, first transformation
            to("bean:orderRouterService?method=transformOrderFormToOrder(${body})")
            .to("direct:enrichOrder");

      // Definition of the enrich order endpoint
      from("direct:enrichOrder").
            // Use the Content Enricher EIP to aggregate customer info in the
            // order.
      enrich(
            "http4://localhost:8081/nvisia-customer-camel-service/api/customer/${body.customerId}",
            new AggregationStrategy() {
               @Override
               public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
                  Order originalBody = (Order) oldExchange.getIn().getBody();
                  Customer resourceResponse = (Customer) newExchange.getIn().getBody();
                  originalBody.setCustomer(resourceResponse);
                  if (oldExchange.getPattern().isOutCapable()) {
                     oldExchange.getOut().setBody(originalBody);
                  } else {
                     oldExchange.getIn().setBody(originalBody);
                  }
                  return oldExchange;
               }
            }).
            // Use the Content Enricher EIP to aggregate catalog info in the
            // order.
      enrich(
            "http4://localhost:8080/nvisia-catalog-camel-service/api/customer/${body.catalogItemId}",
            new AggregationStrategy() {
               @Override
               public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
                  Order originalBody = (Order) oldExchange.getIn().getBody();
                  CatalogItem resourceResponse = (CatalogItem) newExchange.getIn()
                        .getBody();
                  originalBody.setCatalogItem(resourceResponse);
                  if (oldExchange.getPattern().isOutCapable()) {
                     oldExchange.getOut().setBody(originalBody);
                  } else {
                     oldExchange.getIn().setBody(originalBody);
                  }
                  return oldExchange;
               }
            }).to("direct:sendOrder");

      // Definition of the send order endpoint
      from("direct:sendOrder").
            // Need to define the content type on the header
            setHeader(org.apache.camel.Exchange.CONTENT_TYPE,
                  constant("application/json"))
            .
            // Be safe and define this as a post
            setHeader(Exchange.HTTP_METHOD,
                  constant(org.apache.camel.component.http4.HttpMethods.POST))
            .
            // Finally, send the order to be managed and get back the order ID
            to("http4://localhost:8082/nvisia-order-management-camel-service/api/order");
   }

   @Bean
   public ServletRegistrationBean camelServletRegistrationBean() {
      ServletRegistrationBean registration = new ServletRegistrationBean(
            new CamelHttpTransportServlet(), "/api/*");
      registration.setName("CamelServlet");
      return registration;
   }

   @Bean
   public ServletRegistrationBean swaggerServletRegistrationBean() {
      ServletRegistrationBean registration = new ServletRegistrationBean(
            new RestSwaggerServlet(), "/api-docs/*");
      registration.setName("SwaggerServlet");
      return registration;
   }

   @Bean
   public FilterRegistrationBean filterRegistrationBean() {
      FilterRegistrationBean filterBean = new FilterRegistrationBean();
      filterBean.setFilter(new RestSwaggerCorsFilter());
      List<String> urlPatterns = new ArrayList<String>();
      urlPatterns.add("/nvisia-order-router-camel-service/api-docs/*");
      urlPatterns.add("/nvisia-order-router-camel-service/api/*");
      filterBean.setUrlPatterns(urlPatterns);
      return filterBean;
   }

}
