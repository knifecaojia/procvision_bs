package com.imustsz.demo.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

@WebService(targetNamespace = "http://www.pgxxgc.com/IF_Service")
public interface IF_Service {

    @WebMethod(operationName = "IFService")
    @WebResult(name = "IFServiceReturn")
    String IFService(@WebParam(name = "sContent", targetNamespace = "http://www.pgxxgc.com/IF_Service")
                     String sContent);
}