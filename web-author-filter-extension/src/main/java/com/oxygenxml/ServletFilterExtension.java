package com.oxygenxml;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ro.sync.exml.plugin.PluginExtension;

public class ServletFilterExtension implements javax.servlet.Filter, PluginExtension {

  private static final String REDIRECT_URL = "https://www.oxygenxml.com/";

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest)request;
    String requestURL = httpRequest.getRequestURL().toString();
    
    if(requestURL.endsWith("oxygen.html") && httpRequest.getParameter("url") == null) {
      // redirect to another URL
      ((HttpServletResponse)response).sendRedirect(REDIRECT_URL);
    } else {
      chain.doFilter(request, response);
    }
  }

  @Override
  public void destroy() {
  }

}
