package com.nvisia.examples.camel.orderrouter;

import java.util.*;

/**
 * Bean for an order
 * 
 * @author Michael Hoffman, NVISIA
 *
 */
public class Order {

   private Date orderDate; 
   private int customerId;
   private Customer customer; 
   private int catalogItemId;
   private CatalogItem catalogItem; 

   /**
    * Default constructor
    */
   public Order() {
      super();
   }

   public Order(int customerId, int catalogItemId) {
      super(); 
      this.customerId = customerId;
      this.catalogItemId = catalogItemId;
   }
   
   /**
    * Full constructor
    * 
    * @param orderDate
    * @param customerId
    * @param customer
    * @param catalogItemId
    * @param catalogItem
    */
   public Order(Date orderDate, int customerId,
         Customer customer, int catalogItemId, CatalogItem catalogItem) {
      super();
      this.orderDate = orderDate;
      this.customerId = customerId;
      this.customer = customer;
      this.catalogItemId = catalogItemId;
      this.catalogItem = catalogItem;
   }

   /**
    * @return the orderDate
    */
   public Date getOrderDate() {
      return orderDate;
   }

   /**
    * @param orderDate the orderDate to set
    */
   public void setOrderDate(Date orderDate) {
      this.orderDate = orderDate;
   }

   /**
    * @return the customerId
    */
   public int getCustomerId() {
      return customerId;
   }

   /**
    * @param customerId the customerId to set
    */
   public void setCustomerId(int customerId) {
      this.customerId = customerId;
   }

   /**
    * @return the customer
    */
   public Customer getCustomer() {
      return customer;
   }

   /**
    * @param customer the customer to set
    */
   public void setCustomer(Customer customer) {
      this.customer = customer;
   }

   /**
    * @return the catalogItemId
    */
   public int getCatalogItemId() {
      return catalogItemId;
   }

   /**
    * @param catalogItemId the catalogItemId to set
    */
   public void setCatalogItemId(int catalogItemId) {
      this.catalogItemId = catalogItemId;
   }

   /**
    * @return the catalogItem
    */
   public CatalogItem getCatalogItem() {
      return catalogItem;
   }

   /**
    * @param catalogItem the catalogItem to set
    */
   public void setCatalogItem(CatalogItem catalogItem) {
      this.catalogItem = catalogItem;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("Order [");
      if (orderDate != null) {
         builder.append("orderDate=");
         builder.append(orderDate);
         builder.append(", ");
      }
      builder.append("customerId=");
      builder.append(customerId);
      builder.append(", ");
      if (customer != null) {
         builder.append("customer=");
         builder.append(customer);
         builder.append(", ");
      }
      builder.append("catalogItemId=");
      builder.append(catalogItemId);
      builder.append(", ");
      if (catalogItem != null) {
         builder.append("catalogItem=");
         builder.append(catalogItem);
      }
      builder.append("]");
      return builder.toString();
   }

}
