package com.config;

import org.springframework.web.context.ContextLoaderListener;
import io.undertow.servlet.api.InstanceFactory;
import io.undertow.servlet.api.InstanceHandle;

public class ContextLoaderListenerInstanceFactory implements
        InstanceFactory<ContextLoaderListener> {

    public InstanceHandle<ContextLoaderListener> createInstance() {
        ContextLoaderListenerInstanceHandle contextLoaderListenerInstanceHandle = new ContextLoaderListenerInstanceHandle();
        return contextLoaderListenerInstanceHandle;
    }

    class ContextLoaderListenerInstanceHandle implements
            InstanceHandle<ContextLoaderListener> {

        @Override
        public ContextLoaderListener getInstance() {
            return new ContextLoaderListener(
                    SupportAssistApplicationContext.INSTANCE.getAppContext());
        }

        @Override
        public void release() {
            // TODO Auto-generated method stub
        }

    }

}