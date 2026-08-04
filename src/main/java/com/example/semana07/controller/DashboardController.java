package com.example.semana07.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard/admin")
    public String adminDashboard() {
        return "dashboard-admin";
    }

    @GetMapping("/dashboard/subadmin")
    public String subadminDashboard() {
        return "dashboard-subadmin";
    }

    @GetMapping("/dashboard/user")
    public String userDashboard() {
        return "dashboard-user";
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }
    @GetMapping("/personajes")
    public String personajes() {
        return "personajes";
    }
    @GetMapping("/fanarts")
    public String fanarts() {
        return "fanarts";
    }

    @GetMapping("/arte")
    public String arte() {
        return "arte";
    }

    @GetMapping("/descartados")
    public String descartados() {
        return "descartados";
    }
}