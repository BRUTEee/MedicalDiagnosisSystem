<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.medical.model.User" %>
<%@ page import="com.medical.util.Csrf" %>
<%@ page import="com.medical.util.Flash" %>
<%@ page import="com.medical.util.View" %>
<%
    String ctx = request.getContextPath();

    User user = (User) session.getAttribute("user");

    String pageTitle = (String) request.getAttribute("pageTitle");

    if (pageTitle == null) {
        pageTitle = "Dashboard";
    }

    String pageSubtitle = (String) request.getAttribute("pageSubtitle");

    if (pageSubtitle == null) {
        pageSubtitle = "Welcome back, " + (user == null ? "" : user.getName());
    }

    String activePage = (String) request.getAttribute("activePage");

    if (activePage == null) {
        activePage = "";
    }

    String[] flash = Flash.consume(session);

    String[][] navItems = new String[][] {
        { "dashboard", "/dashboard", "Dashboard" },
        { "patients", "/patients", "Patients" },
        { "appointments", "/appointments", "Appointments" },
        { "diagnosis", "/diagnosis", "Assessments" },
        { "reports", "/reports", "Reports" },
        { "settings", "/settings", "Settings" }
    };
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="csrf-token" content="<%= View.esc(Csrf.token(session)) %>">
    <title><%= View.esc(pageTitle) %> - Medical Assist</title>
    <link rel="stylesheet" href="<%= ctx %>/css/app.css">
</head>
<body>
<div class="app">

    <aside class="sidebar">

        <div class="logo">MEDICAL <span>ASSIST</span></div>

        <ul class="nav">
<%
    for (String[] item : navItems) {
        boolean active = item[0].equals(activePage);
%>
            <li>
                <a href="<%= ctx + item[1] %>"<%= active ? " class=\"active\"" : "" %>><%= item[2] %></a>
            </li>
<%
    }
%>
        </ul>

        <form method="post" action="<%= ctx %>/logout" class="logout-form">
            <%= Csrf.field(request) %>
            <button type="submit" class="logout-btn">Logout</button>
        </form>

    </aside>

    <main class="main">

        <div class="topbar">
            <div>
                <h1><%= View.esc(pageTitle) %></h1>
                <p><%= View.esc(pageSubtitle) %></p>
            </div>

            <div class="doctor-info">
                <strong><%= View.esc(user == null ? "" : user.getName()) %></strong>
                &middot; <%= View.esc(user == null ? "" : user.getRole()) %>
            </div>
        </div>

<%
    if (flash != null) {
%>
        <div class="alert alert-<%= flash[0] %>"><%= View.esc(flash[1]) %></div>
<%
    }
%>

        <div class="page">
