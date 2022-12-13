package com.example.serverapp.controller;

import com.example.serverapp.communication.ServerCommunication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;

@RestController
@RequestMapping("/server")
public class ServerController {

    @Autowired
    private ServerCommunication serverCommunication;

    @GetMapping("/test")
    public String getData() {
        return "from SERVER-SERVICE " + serverCommunication.getConsoleInfo();
    }

    @GetMapping("/host")
    public String geHostData() {
        System.out.println("getHostData Method being called");

        InetAddress ip = null;
        String hostname = "";
        try {
            ip = InetAddress.getLocalHost();
            hostname = ip.getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "Host Name of Server is : " + hostname + "ip of Server is : " + ip ;
    }
    @GetMapping("/search/{searchTerm}")
    public String getSearchTerm( @PathVariable String searchTerm) {

        // needs to take in the searchTerm passed in from the CLI app.

        // Then needs to call the goodreads app with the term.

        // gets data back from good reads.


        return searchTerm;
    }
}
