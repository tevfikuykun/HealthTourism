package com.healthtourism.appointmentcalendarservice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
@SpringBootApplication
@EnableEurekaClient
public class AppointmentCalendarServiceApplication {
    public static void main(String[] args) { SpringApplication.run(AppointmentCalendarServiceApplication.class, args); }
}

