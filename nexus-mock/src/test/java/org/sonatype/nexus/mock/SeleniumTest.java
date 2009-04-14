package org.sonatype.nexus.mock;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import org.sonatype.nexus.mock.pages.MainPage;

public abstract class SeleniumTest extends NexusTestCase {
    protected Selenium selenium;
    protected MainPage main;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        selenium = new DefaultSelenium("localhost", 4444, "*firefox", "http://localhost:12345");
        selenium.start();
        main = new MainPage(selenium);
    }

    @Override
    protected void tearDown() throws Exception {
        selenium.stop();

        super.tearDown();
    }
}
