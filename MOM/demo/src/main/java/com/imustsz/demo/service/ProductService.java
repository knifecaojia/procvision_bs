package com.imustsz.demo.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface ProductService {

    @WebMethod
        // @WebParam(name = "sContent") 非常重要，这决定了客户端看到的参数名
    String uploadMesData(@WebParam(name = "sContent") String sContent);
}