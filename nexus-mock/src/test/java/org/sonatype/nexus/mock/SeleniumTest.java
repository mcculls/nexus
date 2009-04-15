package org.sonatype.nexus.mock;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import org.sonatype.nexus.mock.pages.MainPage;
import org.sonatype.nexus.mock.rest.MockHelper;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.Ignore;
import org.junit.Before;
import org.junit.After;
import org.apache.commons.codec.binary.Base64;

import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.io.*;

@Ignore
@RunWith(SeleniumJUnitRunner.class)
public abstract class SeleniumTest extends NexusTestCase {
    protected Selenium selenium;
    protected MainPage main;
    protected Description description;

    @Before
    public void seleniumSetup() throws Exception {
        selenium = (Selenium) Proxy.newProxyInstance(Selenium.class.getClassLoader(), new Class<?>[] { Selenium.class }, new InvocationHandler() {
            String ip = InetAddress.getLocalHost().getHostAddress();
            Selenium original = new DefaultSelenium("localhost", 4444, "*firefox", "http://" + ip + ":12345");

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // check assertions on every remote call we do!
                MockHelper.checkAssertions();
                return method.invoke(original, args);
            }
        });
        selenium.start("captureNetworkTraffic=true");
        selenium.getEval("window.moveTo(1,1); window.resizeTo(1021,737);");
        main = new MainPage(selenium);
    }

    @After
    public void seleniumCleanup() throws Exception {
        selenium.stop();
    }

    /**
     * Sets the JUnit description for the currently running test.
     *
     * @param description The JUnit description.
     * @see SeleniumJUnitRunner
     */
    public void setDescription(Description description) {
        this.description = description;
    }

    /**
     * Takes a screenshot of the browser and saves it to the target/screenshots directory. The exact name of the file is
     * based on the currently executing test class and method name plus the line number of the source code that called
     * this method.
     *
     * @throws java.io.IOException If the screenshot could not be taken.
     */
    protected void takeScreenshot() throws IOException {
        @SuppressWarnings({"ThrowableInstanceNeverThrown"})
        StackTraceElement ste = new Exception().getStackTrace()[1];
        takeScreenshot("line-" + ste.getLineNumber());
    }

    /**
     * Takes a screenshot of the browser and saves it to the target/screenshots directory. The name is a combination
     * of the currently executing test class and method name, plus the name parameterized supplied when calling this
     * method.
     *
     * @param name A specific name to append to the screenshot file name.
     * @throws IOException If the screenshot could not be taken.
     */
    protected void takeScreenshot(String name) throws IOException {
        File parent = new File("target/screenshots/");
        //noinspection ResultOfMethodCallIgnored
        parent.mkdirs();

        String screen = selenium.captureScreenshotToString();
        FileOutputStream fos = new FileOutputStream(new File(parent, description.getDisplayName() + "-" + name + ".png"));
        fos.write(Base64.decodeBase64(screen.getBytes()));
        fos.close();
    }

    public void captureNetworkTraffic() {
        try {
            File parent = new File("target/network-traffic/");
            //noinspection ResultOfMethodCallIgnored
            parent.mkdirs();

            FileOutputStream fos = new FileOutputStream(new File(parent, description.getDisplayName() + ".txt"));
            fos.write(selenium.captureNetworkTraffic("TODO").getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
