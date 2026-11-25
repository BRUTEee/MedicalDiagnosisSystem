<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.medical.model.User" %>
<%@ page import="com.medical.util.Csrf" %>
<%@ page import="com.medical.util.PasswordUtil" %>
<%@ page import="com.medical.util.View" %>
<%
    request.setAttribute("pageTitle", "Settings");
    request.setAttribute("pageSubtitle", "Manage your profile and password.");
    request.setAttribute("activePage", "settings");
%>
<%@ include file="partials/header.jsp" %>

<div class="card">

    <h2>Profile</h2>

    <form method="post" action="<%= ctx %>/settings">

        <%= Csrf.field(request) %>
        <input type="hidden" name="action" value="profile">

        <div class="form-grid">

            <div class="form-group">
                <label for="name">Full Name</label>
                <input type="text" id="name" name="name" maxlength="100" required
                       value="<%= View.esc(user.getName()) %>">
            </div>

            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" id="email" value="<%= View.esc(user.getEmail()) %>" readonly>
            </div>

        </div>

        <div class="form-footer">
            <button type="submit" class="btn">Save Profile</button>
        </div>

    </form>

</div>

<div class="card">

    <h2>Change Password</h2>

    <form method="post" action="<%= ctx %>/settings">

        <%= Csrf.field(request) %>
        <input type="hidden" name="action" value="password">

        <div class="form-grid">

            <div class="form-group full">
                <label for="currentPassword">Current Password</label>
                <input type="password" id="currentPassword" name="currentPassword"
                       autocomplete="current-password" required>
            </div>

            <div class="form-group">
                <label for="newPassword">New Password</label>
                <input type="password" id="newPassword" name="newPassword"
                       minlength="<%= PasswordUtil.MIN_LENGTH %>"
                       maxlength="<%= PasswordUtil.MAX_LENGTH %>"
                       autocomplete="new-password" required>
            </div>

            <div class="form-group">
                <label for="confirmPassword">Confirm New Password</label>
                <input type="password" id="confirmPassword" name="confirmPassword"
                       minlength="<%= PasswordUtil.MIN_LENGTH %>"
                       maxlength="<%= PasswordUtil.MAX_LENGTH %>"
                       autocomplete="new-password" required>
            </div>

        </div>

        <div class="form-footer">
            <button type="submit" class="btn">Change Password</button>
        </div>

    </form>

</div>

<%@ include file="partials/footer.jsp" %>
