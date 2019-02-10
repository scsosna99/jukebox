package com.buddhadata.projects.junkebox.codecard.controller;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by scsosna on 2/9/19.
 */
//@Component
public class CodeCardFilter implements Filter {

    @Override
    public void doFilter (ServletRequest req,
                          ServletResponse res,
                          FilterChain chain) throws IOException, ServletException {

        HttpServletRequest requesti = (HttpServletRequest) req;
        System.out.println (req.getProtocol());
        HttpServletResponse response = (HttpServletResponse) res;
        System.out.println (res.toString());
    }


    @Override
    public void destroy() {}

    @Override
    public void init (FilterConfig arg0) throws ServletException {
        return;
    }

}
