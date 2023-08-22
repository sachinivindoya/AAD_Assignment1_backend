package com.example.aad_assignment1_backend.filters;


import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;

import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebFilter(filterName = "CORSFilter",urlPatterns = "/*")
public class CORSFilter  extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        System.out.println("CROS Filter");
        String origin = req.getHeader("origin");
        if (origin!=null && origin.contains("http://localhost:63342")){
            System.out.println("CROS OK");
            res.setHeader("Access-Control-Allow-Origin","*");
            res.setHeader("Access-Control-Allow-Methods","GET,POST,PUT,DELETE,HEADER");
            res.setHeader("Access-Control-Allow-Headers", "Content-Type");
            res.setHeader("Access-Control-Expose-Headers","Content-Type");
            res.setContentType("application/json");//MIME Types
        }
        chain.doFilter(req,res);
    }
}
