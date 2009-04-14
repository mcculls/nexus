package org.sonatype.nexus.mock;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import org.sonatype.nexus.mock.pages.MainPage;
import org.sonatype.nexus.mock.rest.MockHelper;

import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public abstract class SeleniumTest extends NexusTestCase {
    protected Selenium selenium;
    protected MainPage main;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        selenium = (Selenium) Proxy.newProxyInstance(Selenium.class.getClassLoader(), new Class<?>[] { Selenium.class }, new InvocationHandler() {
            Selenium original = new DefaultSelenium("localhost", 4444, "*firefox", "http://localhost:12345");

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // check assertions on every remote call we do!
                MockHelper.checkAssertions();
                return method.invoke(original, args);
            }
        });
        selenium.start();
        main = new MainPage(selenium);
    }

    @Override
    protected void tearDown() throws Exception {
        selenium.stop();

        super.tearDown();
    }
}
