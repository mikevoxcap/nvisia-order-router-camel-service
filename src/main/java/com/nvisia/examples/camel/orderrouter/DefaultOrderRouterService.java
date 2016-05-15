package com.nvisia.examples.camel.orderrouter;

import org.springframework.stereotype.*;

/**
 * Default implementation for order routing.
 * 
 * @author Michael Hoffman
 *
 */
@Service("bean:orderRouterService")
public class DefaultOrderRouterService implements OrderRouterService {

   @Override
   public Order transformOrderFormToOrder(OrderForm orderForm) {
      return new Order(orderForm.getCustomerId(), orderForm.getCatalogItemId()); 
   }


}
