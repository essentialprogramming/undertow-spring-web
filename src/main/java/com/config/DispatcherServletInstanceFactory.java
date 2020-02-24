package com.config;

import io.undertow.servlet.api.InstanceFactory;
import io.undertow.servlet.api.InstanceHandle;
import org.springframework.web.servlet.DispatcherServlet;

public class DispatcherServletInstanceFactory implements InstanceFactory<DispatcherServlet> {


    public InstanceHandle<DispatcherServlet> createInstance() {
        return new DispatcherServletInstanceHandle();
    }

    class DispatcherServletInstanceHandle implements InstanceHandle<DispatcherServlet> {

        @Override
        public DispatcherServlet getInstance() {

            return new DispatcherServlet(SupportAssistApplicationContext.INSTANCE.getServletContext());
        }

        @Override
        public void release() {
            // TODO Auto-generated method stub
        }
    }

}
