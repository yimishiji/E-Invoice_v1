

/**
 * SajtIssueInvoiceService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.7.3  Built on : May 30, 2016 (04:08:57 BST)
 */

    package sajt.shdzfp.sl.service;

    /*
     *  SajtIssueInvoiceService java interface
     */

    public interface SajtIssueInvoiceService {
          

        /**
          * Auto generated method signature
          * 
                    * @param eiInterface0
                
         */

         
                     public sajt.shdzfp.sl.service.EiInterfaceResponse eiInterface(

                        sajt.shdzfp.sl.service.EiInterface eiInterface0)
                        throws java.rmi.RemoteException
             ;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
                * @param eiInterface0
            
          */
        public void starteiInterface(

            sajt.shdzfp.sl.service.EiInterface eiInterface0,

            final sajt.shdzfp.sl.service.SajtIssueInvoiceServiceCallbackHandler callback)

            throws java.rmi.RemoteException;

     

        
       //
       }
    