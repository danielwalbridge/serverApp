package com.example.serverapp.controller;

import com.example.serverapp.communication.ServerCommunication;
import com.example.serverapp.model.Book;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

        String jsonResultString = "";
        // Need to write a get request to GoodReads
        try {

            searchTerm = searchTerm.replaceAll("\\s", "");

            HttpClient httpClient = HttpClient.newHttpClient();

            // generate get to GoodReads
            HttpRequest GETRequest = HttpRequest.newBuilder()
                    .uri(new URI("https://www.goodreads.com/search/index.xml?q="+searchTerm+"&key=" + devKey))
                    .GET()
                    .build();

            // gets data back from good reads.
            HttpResponse<String> response = httpClient.send(GETRequest, HttpResponse.BodyHandlers.ofString());

            // convert into json format
            XmlMapper xmlMapper = new XmlMapper();
            JsonNode node = xmlMapper.readTree(response.body());

            ObjectMapper jsonMapper = new ObjectMapper();
            jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            String json = jsonMapper.writeValueAsString(node);

            // moving into nested objects.
            JSONArray workArray = populateJSONArrayAfterNesting(json);
            // populate book list
            List<Book> bookList = populateBookListFromJSONArray(workArray);
            // create pretty result.
            jsonResultString = createPrettyJsonResult(bookList);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonResultString;
    }
    @GetMapping("/search/{searchTerm}/{sort}")
    public String getSearchTermWithSort( @PathVariable String searchTerm, @PathVariable String sort) {

        // needs to take in the searchTerm passed in from the CLI app.

        System.out.println(searchTerm);
        System.out.println(sort);
        if ( searchTerm == null || searchTerm.isBlank()) {
            return " You must enter something for the search to work";
        }

        String jsonResultString = "";
        // Need to write a get request to GoodReads
        try {

            searchTerm = searchTerm.replaceAll("\\s", "");

            HttpClient httpClient = HttpClient.newHttpClient();

            // generate get to GoodReads
            HttpRequest GETRequest = HttpRequest.newBuilder()
                    .uri(new URI("https://www.goodreads.com/search/index.xml?q="+searchTerm+"&key=" + devKey +"&search=" + sort))
                    .GET()
                    .build();

            // gets data back from good reads.
            HttpResponse<String> response = httpClient.send(GETRequest, HttpResponse.BodyHandlers.ofString());

            // convert into json format
            XmlMapper xmlMapper = new XmlMapper();
            JsonNode node = xmlMapper.readTree(response.body());

            ObjectMapper jsonMapper = new ObjectMapper();
            jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            String json = jsonMapper.writeValueAsString(node);

            // moving into nested objects.
            JSONArray workArray = populateJSONArrayAfterNesting(json);
            // populate book list
            List<Book> bookList = populateBookListFromJSONArray(workArray);
            // create pretty result.
            jsonResultString = createPrettyJsonResult(bookList);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonResultString;
    }

    public String createPrettyJsonResult(List<Book> bookList) {
        if (bookList.isEmpty()) {
            return "no results found";
        }
        else {
            Gson gs = new GsonBuilder()
                    .setPrettyPrinting()
                    .disableHtmlEscaping()
                    .create();
            return gs.toJson(bookList);
        }
    }

    public List<Book> populateBookListFromJSONArray(JSONArray jsonArray) {
        if (jsonArray.isEmpty()) {
            return new ArrayList<>();
        }

        List<Book> bookList = new ArrayList<>();

        for (Object object:jsonArray) {
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
        return bookList;
    }

    public JSONArray populateJSONArrayAfterNesting(String json) {
        // nests down into the objects to get to the working array

        JSONArray jsonArray = null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject jsonSearchObject = jsonObject.getJSONObject("search");
            JSONObject jsonResultsObject = jsonSearchObject.getJSONObject("results");
            jsonArray = jsonResultsObject.getJSONArray("work");
        }
       catch (Exception e) {
          e.printStackTrace();
       }
        return jsonArray;
    }
}
