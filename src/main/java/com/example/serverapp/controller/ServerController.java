package com.example.serverapp.controller;

import com.example.serverapp.communication.ServerCommunication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RestController
@RequestMapping("/server")
public class ServerController {

    @Autowired
    private ServerCommunication serverCommunication;

    @Value("${GoodReadsAPIKey}")
    private String devKey;

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
        if ( searchTerm == null || searchTerm.isBlank()) {
            return " You must enter something for the search to work";
        }

        // Need to write a get request to GoodReads
        try {

            HttpClient httpClient = HttpClient.newHttpClient();

            // generate get to GoodReads
            HttpRequest GETRequest = HttpRequest.newBuilder()
                    .uri(new URI("https://www.goodreads.com/search/index.xml?q="+searchTerm+"&key=" + devKey))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(GETRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println(response);
            System.out.println(response.body());
            // send request
        } catch (Exception e) {
            e.printStackTrace();
        }
        // gets data back from good reads.


        return searchTerm;
    }
}
