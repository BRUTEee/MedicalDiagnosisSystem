package com.medical.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.medical.dao.UserDAO;
import com.medical.model.User;
import com.medical.util.Flash;
import com.medical.util.PasswordUtil;

/**
 * Account settings.
 *
 * GET  /settings
 * POST /settings action=profile | password
 */
@WebServlet("/settings")
public class SettingsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/WEB-INF/views/settings.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        User user = (User) request.getSession().getAttribute("user");

        if ("password".equals(request.getParameter("action"))) {
            changePassword(request, user);
        } else {
            updateProfile(request, user);
        }

        response.sendRedirect(request.getContextPath() + "/settings");
    }

    private void updateProfile(HttpServletRequest request, User user) {

        String name = request.getParameter("name");
        name = name == null ? "" : name.trim();

        if (name.isEmpty() || name.length() > 100) {
            Flash.error(request,
                "Name is required and must be 100 characters or fewer.");
            return;
        }

        userDAO.updateName(user.getId(), name);

        // Replace the session copy so the new name shows immediately.
        request.getSession().setAttribute("user",
                new User(user.getId(), name, user.getEmail(), user.getRole()));

        Flash.success(request, "Profile updated.");
    }

    private void changePassword(HttpServletRequest request, User user) {

        String current = request.getParameter("currentPassword");
        String next = request.getParameter("newPassword");
        String confirm = request.getParameter("confirmPassword");

        if (current == null || next == null || confirm == null) {
            Flash.error(request, "All password fields are required.");
            return;
        }

        if (next.length() < PasswordUtil.MIN_LENGTH
                || next.length() > PasswordUtil.MAX_LENGTH) {
            Flash.error(request, "The new password must be between "
                    + PasswordUtil.MIN_LENGTH + " and "
                    + PasswordUtil.MAX_LENGTH + " characters.");
            return;
        }

        if (!next.equals(confirm)) {
            Flash.error(request, "The new passwords do not match.");
            return;
        }

        if (next.equals(current)) {
            Flash.error(request,
                "The new password must be different from the current one.");
            return;
        }

        if (!userDAO.changePassword(user.getId(), current, next)) {
            Flash.error(request, "The current password is incorrect.");
            return;
        }

        Flash.success(request, "Password changed.");
    }
}
