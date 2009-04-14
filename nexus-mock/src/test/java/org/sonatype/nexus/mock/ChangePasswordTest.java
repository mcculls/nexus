package org.sonatype.nexus.mock;

import org.sonatype.nexus.mock.models.User;
import org.sonatype.nexus.mock.pages.ChangePasswordWindow;
import org.sonatype.nexus.mock.rest.MockHelper;
import org.restlet.data.Status;

public class ChangePasswordTest extends SeleniumTest {
    public void testChangePasswordSuccess() {
        main.clickLogin().populate(User.ADMIN).loginExpectingSuccess();

        ChangePasswordWindow window = main.securityPanel().clickChangePassword();

        MockHelper.getResponseMap().put("/users_changepw", new MockResponse(Status.CLIENT_ERROR_BAD_REQUEST, null));

        window.populate("password", "newPassword", "newPassword").changePasswordExpectingSuccess();
    }
}
