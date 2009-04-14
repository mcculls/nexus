package org.sonatype.nexus.mock.pages;

import com.thoughtworks.selenium.Selenium;
import org.sonatype.nexus.mock.components.TextField;
import org.sonatype.nexus.mock.components.Button;
import org.sonatype.nexus.mock.components.Window;
import org.sonatype.nexus.mock.models.User;

public class LoginWindow extends Window {
    private TextField username;
    private TextField password;
    private Button loginButton;

    public LoginWindow(Selenium selenium) {
        super(selenium, "window.Ext.getCmp('login-window')");

        username = new TextField(selenium, "window.Ext.getCmp('usernamefield')");
        password = new TextField(selenium, "window.Ext.getCmp('passwordfield')");
        loginButton = new Button(selenium, "window.Ext.getCmp('loginbutton')");
    }

    public LoginWindow populate(User user) {
        this.username.type(user.getUsername());
        this.password.type(user.getPassword());

        return this;
    }

    public LoginWindow login() {
        loginButton.click();

        return this;
    }

    public TextField getUsername() {
        return username;
    }

    public TextField getPassword() {
        return password;
    }

    public Button getLoginButton() {
        return loginButton;
    }

    public void loginExpectingSuccess() {
        login();
        waitForHidden();
    }
}
