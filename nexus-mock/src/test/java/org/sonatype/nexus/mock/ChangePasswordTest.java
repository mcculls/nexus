package org.sonatype.nexus.mock;

import org.sonatype.nexus.mock.models.User;
import org.sonatype.nexus.mock.pages.ChangePasswordWindow;
import org.sonatype.nexus.mock.pages.PasswordChangedWindow;
import org.sonatype.nexus.mock.rest.MockHelper;
import org.sonatype.nexus.rest.users.UserChangePasswordPlexusResource;
import org.sonatype.nexus.rest.model.UserChangePasswordRequest;
import org.restlet.data.Status;
import junit.framework.AssertionFailedError;

public class ChangePasswordTest extends SeleniumTest {
    public void testChangePasswordSuccess() {
        main.clickLogin().populate(User.ADMIN).loginExpectingSuccess();

        ChangePasswordWindow window = main.securityPanel().clickChangePassword();

        MockHelper.expect("/users_changepw", new MockResponse(Status.SUCCESS_NO_CONTENT, null) {
            @Override
            public void setPayload(Object payload) throws AssertionFailedError {
                UserChangePasswordRequest r = (UserChangePasswordRequest) payload;
                assertEquals("password", r.getData().getOldPassword());
                assertEquals("newPassword", r.getData().getNewPassword());
            }
        });

        PasswordChangedWindow passwordChangedWindow = window.populate("password", "newPassword", "newPassword").changePasswordExpectingSuccess();

        passwordChangedWindow.clickOk();
    }
}
