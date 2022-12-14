package com.example.serverapp.controller;

import com.example.serverapp.communication.ServerCommunication;
import com.example.serverapp.model.Book;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.json.JSONArray;
import org.json.JSONObject;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

            // gets data back from good reads.
            HttpResponse<String> response = httpClient.send(GETRequest, HttpResponse.BodyHandlers.ofString());

            // convert into json format Jackson
            XmlMapper xmlMapper = new XmlMapper();
            JsonNode node = xmlMapper.readTree(response.body());

            ObjectMapper jsonMapper = new ObjectMapper();
            jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            String json = jsonMapper.writeValueAsString(node);

            // moving into nested objects.
            JSONObject jsonObject = new JSONObject(json);
            JSONObject jsonSearchObject = jsonObject.getJSONObject("search");
            JSONObject jsonResultsObject = jsonSearchObject.getJSONObject("results");
            JSONArray workArray = jsonResultsObject.getJSONArray("work");

            List<Book> bookList = new ArrayList<>();
            // grabs each object in array
            for (Object object:workArray) {
                // grab the best_book object
                String objectS = object.toString();
                JSONObject temp = new JSONObject(objectS);
                JSONObject jsonBestBookObject = temp.getJSONObject("best_book");
                // grab author obj
                JSONObject jsonAuth = jsonBestBookObject.getJSONObject("author");

                //populate book object and list
                Book book = new Book();
                book.setTitle((String) jsonBestBookObject.get("title"));
                book.setAuthor((String) jsonAuth.get("name"));
                book.setImage_url((String) jsonBestBookObject.get("image_url"));
                bookList.add(book);
            }

            System.out.println(bookList);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return searchTerm;
    }
}
