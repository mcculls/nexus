package org.sonatype.nexus.mock;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import org.sonatype.nexus.mock.pages.MainPage;
import org.sonatype.nexus.mock.rest.MockHelper;
import org.sonatype.nexus.mock.util.PropUtil;
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
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.Inet4Address;
import java.io.*;
import java.util.Enumeration;

@Ignore
@RunWith(SeleniumJUnitRunner.class)
public abstract class SeleniumTest extends NexusTestCase {
    protected Selenium selenium;
    protected MainPage main;
    protected Description description;

    private static String getLocalIp() throws SocketException {
        Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
        while (e.hasMoreElements()) {
            NetworkInterface ni = e.nextElement();

            if (!ni.getDisplayName().startsWith("vmnet")) {
                Enumeration<InetAddress> i = ni.getInetAddresses();
                while (i.hasMoreElements()) {
                    InetAddress ia = i.nextElement();
                    if (ia instanceof Inet4Address) {
                        if (!ia.getHostAddress().startsWith("127.0.")) {
                            return ia.getHostAddress();
                        }
                    }
                }
            }
        }

        return "localhost";
    }

    @Before
    public void seleniumSetup() throws Exception {
        final String ip = getLocalIp();
        final String seleniumServer = PropUtil.get("seleniumServer", "localhost");
        final int seleniumPort = PropUtil.get("seleniumPort", 4444);
        final String seleniumBrowser = PropUtil.get("seleniumBrowser", "*firefox");
        final Selenium original = new DefaultSelenium(seleniumServer, seleniumPort, seleniumBrowser, "http://" + ip + ":" + PropUtil.get("jettyPort", 12345));

        System.out.println(seleniumServer);
        System.out.println(seleniumServer);
        System.out.println(seleniumServer);
        System.out.println(seleniumServer);
        System.out.println(seleniumServer);
        System.out.println(seleniumServer);
        System.out.println(seleniumServer);

        selenium = (Selenium) Proxy.newProxyInstance(Selenium.class.getClassLoader(), new Class<?>[] { Selenium.class }, new InvocationHandler() {
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
