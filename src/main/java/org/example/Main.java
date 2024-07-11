package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Main {
    private final static String BASE_URL = "https://jsonplaceholder.typicode.com/users";

    public static void main(String[] args) {
        // Task 2
//        allComents(1);

        // Task 3
//        openTasks(1);

        // Task 1
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        Customer customer = new Customer();
        customer.setName("John");
        customer.setUsername("John101");
        customer.setEmail("John101@gmail.com");
        Customer.Address address = new Customer.Address();
        address.setStreet("Main");
        address.setSuite("Xoxo");
        address.setCity("Kyiv");
        address.setZipcode("101-101");
        Customer.Geo geo = new Customer.Geo();
        geo.setLat("111-222");
        geo.setLng("222-111");
        address.setGeo(geo);
        customer.setAddress(address);
        customer.setPhone("888-777-666");
        customer.setWebsite("John.com");
        Customer.Company company = new Customer.Company();
        company.setName("JonhComoany");
        company.setCatchPhrase("Mine");
        company.setBs("best company");
        customer.setCompany(company);

        //TODO створення нового об'єкта
//        try(HttpClient httpClient = HttpClient.newHttpClient()) {
//            String requestBody = objectMapper.writeValueAsString(customer);
//
//            HttpRequest httpRequest = HttpRequest.newBuilder(new URI(BASE_URL))
//                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
//                    .header("Content-type", "application/json")
//                    .build();
//
//            HttpResponse<String> stringHttpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
//            System.out.println("HTTP Status Code: " + stringHttpResponse.statusCode());
//            System.out.println("Response Body: " + stringHttpResponse.body());
//        } catch (URISyntaxException | IOException | InterruptedException e) {
//            throw new RuntimeException(e);
//        }

        //TODO оновлення об'єкту
//        try(HttpClient httpClient = HttpClient.newHttpClient();) {
//            String requestBody = objectMapper.writeValueAsString(customer);
//
//            HttpRequest httpRequest = HttpRequest.newBuilder(new URI(BASE_URL + "/" + 1))
//                    .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
//                    .header("Content-type", "application/json")
//                    .build();
//            HttpResponse<String> stringHttpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
//            System.out.println("HTTP Status Code: " + stringHttpResponse.statusCode());
//            System.out.println("Response Body: " + stringHttpResponse.body());
//        }catch (URISyntaxException | IOException | InterruptedException e){
//            throw new RuntimeException(e);
//        }

        //TODO видалення об'єкта

//            try(HttpClient httpClient = HttpClient.newHttpClient();) {
//                HttpRequest httpRequest = HttpRequest.newBuilder(new URI(BASE_URL + "/" + 1))
//                        .DELETE()
//                        .build();
//                HttpResponse<String> stringHttpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
//                System.out.println("HTTP Status Code: " + stringHttpResponse.statusCode());
//                System.out.println("Response Body: " + stringHttpResponse.body());
//            }catch (URISyntaxException | IOException | InterruptedException e){
//                throw new RuntimeException(e);
//            }

        //TODO отримання інформації про всіх користувачів
//        try(HttpClient httpClient = HttpClient.newHttpClient()) {
//            HttpRequest httpRequest = HttpRequest.newBuilder(new URI(BASE_URL))
//                    .GET()
//                    .build();
//            HttpResponse<String> stringHttpResponse =  httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
//            stringHttpResponse.body();
//            System.out.println(stringHttpResponse.statusCode());
//            List<Customer> responceList = objectMapper.readValue(stringHttpResponse.body(),
//                    objectMapper.getTypeFactory().constructCollectionType(List.class, Customer.class));
//            responceList.forEach(System.out::println);
//        } catch (URISyntaxException | IOException | InterruptedException e) {
//            throw new RuntimeException(e);
//        }

        //TODO отримання інформації про користувача за id
//        try(HttpClient httpClient = HttpClient.newHttpClient()) {
//            HttpRequest httpRequest = HttpRequest.newBuilder(new URI(BASE_URL + "/" + 2))
//                    .GET()
//                    .build();
//            HttpResponse<String> stringHttpResponse =  httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
//            System.out.println("HTTP Status Code: " + stringHttpResponse.statusCode());
//            System.out.println("Response Body: " + stringHttpResponse.body());
//        } catch (URISyntaxException | IOException | InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        //TODO отримання інформації про користувача за username
        String username = "Karianne";

//        try(HttpClient httpClient = HttpClient.newHttpClient()){
//            HttpRequest httpRequest = HttpRequest.newBuilder(new URI(BASE_URL + "?username=" + username))
//                    .GET()
//                    .build();
//
//            HttpResponse<String> stringHttpResponse =  httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
//            int statusCode = stringHttpResponse.statusCode();
//
//            if (statusCode == 200) {
//                System.out.println("HTTP Status Code: " + statusCode);
//                System.out.println("Response Body: " + stringHttpResponse.body());
//            } else if (statusCode == 404) {
//                System.out.println("User with username '" + username + "' not found.");
//            } else {
//                System.out.println("Unexpected HTTP Status Code: " + statusCode);
//            }
//
//        } catch (URISyntaxException | IOException | InterruptedException e) {
//            throw new RuntimeException(e);
//        }
    }
    //TODO метод, що буде виводити всі коментарі до останнього поста певного користувача і записувати їх у файл.
    public static void allComents(int userID){
        String baseURI = "https://jsonplaceholder.typicode.com";
        ObjectMapper objectMapper = new ObjectMapper();
        try(HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest postsRequest = HttpRequest.newBuilder(new URI(BASE_URL + "/" + userID + "/posts"))
                    .GET()
                    .build();
            HttpResponse<String> listPosts = httpClient.send(postsRequest, HttpResponse.BodyHandlers.ofString());
            if (listPosts.statusCode() == 200) {
                List<Post> postList = objectMapper.readValue(listPosts.body(), new TypeReference<List<Post>>() {});
                Optional<Post> postWithMaxID = postList.stream().max(Comparator.comparingInt(Post::getId));

                if (postWithMaxID.isPresent()) {
                    int maxPostId = postWithMaxID.get().getId();
                HttpRequest commentsRequest = HttpRequest.newBuilder(new URI(baseURI + "/posts/" + maxPostId + "/comments"))
                        .GET()
                        .build();
                HttpResponse<String> listComments = httpClient.send(commentsRequest, HttpResponse.BodyHandlers.ofString());

                File file = new File("user-" + userID + "-post-" + maxPostId + "-comments.json");
                try (FileWriter fileWriter = new FileWriter(file)) {
                    fileWriter.write(listComments.body());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                }else {
                    System.out.println("No posts found for user with ID: " + userID);
                }
            }else {
                System.out.println("HTTP Status Code: " + listPosts.statusCode());
            }
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    //TODO метод, що буде виводити всі відкриті задачі для користувача з ідентифікатором X
    public static void openTasks(int userId){
        String tasksURI = "https://jsonplaceholder.typicode.com/users/" + userId + "/todos";
        ObjectMapper objectMapper = new ObjectMapper();

        try(HttpClient httpClient = HttpClient.newHttpClient()){
            HttpRequest tasksRequest = HttpRequest.newBuilder(new URI(tasksURI))
                    .GET()
                    .build();
            HttpResponse<String> listOfTasks = httpClient.send(tasksRequest, HttpResponse.BodyHandlers.ofString());

            if(listOfTasks.statusCode() == 200){
                List<Task> taskList = objectMapper.readValue(listOfTasks.body(), new TypeReference<List<Task>>() {});
                taskList.stream()
                        .filter(isCompleated -> !isCompleated.completed)
                        .forEach(System.out::println);
            }
        }catch (URISyntaxException | IOException | InterruptedException e){
            throw new RuntimeException();
        }
    }
}