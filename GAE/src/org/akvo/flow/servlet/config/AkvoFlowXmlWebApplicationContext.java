
package org.akvo.flow.servlet.config;

import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.web.context.support.XmlWebApplicationContext;

import com.google.appengine.api.utils.SystemProperty;

public class AkvoFlowXmlWebApplicationContext extends XmlWebApplicationContext {

    @Override
    protected void initBeanDefinitionReader(XmlBeanDefinitionReader beanDefinitionReader) {
        super.initBeanDefinitionReader(beanDefinitionReader);
        if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
            beanDefinitionReader.setValidating(false);
            beanDefinitionReader.setNamespaceAware(true);
        }
    }
}
