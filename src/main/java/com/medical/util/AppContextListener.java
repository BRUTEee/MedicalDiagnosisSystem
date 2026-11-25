package com.medical.util;

import java.io.IOException;
import java.io.InputStream;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/** Loads WEB-INF/app.properties when the application starts. */
@WebListener
public class AppContextListener implements ServletContextListener {

    private static final String CONFIG_PATH = "/WEB-INF/app.properties";

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        AppConfig.clear();
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {

        ServletContext context = event.getServletContext();

        try (InputStream input = context.getResourceAsStream(CONFIG_PATH)) {

            if (input == null) {
                context.log(CONFIG_PATH + " not found. Copy app.properties.example "
                        + "to app.properties or set the MDS_DB_* environment variables.");
            }

            AppConfig.load(input);
            boolean requireHttps = Boolean.parseBoolean(
                    AppConfig.get("security.require-https", "false"));
            boolean secureCookie = requireHttps || Boolean.parseBoolean(
                    AppConfig.get("session.cookie.secure", "false"));
            context.getSessionCookieConfig().setSecure(secureCookie);
            context.getSessionCookieConfig().setAttribute("SameSite", "Lax");

        } catch (IOException e) {
            context.log("Unable to read " + CONFIG_PATH, e);
        }
    }
}
