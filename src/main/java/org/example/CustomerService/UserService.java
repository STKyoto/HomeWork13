package org.example.CustomerService;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.dto.Customer;
import org.example.dto.Post;
import org.example.dto.Task;

import java.io.File;
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
import java.util.stream.Collectors;

public class UserService {
    private final static String BASE_URL = "https://jsonplaceholder.typicode.com";

    public ObjectMapper objectMapper(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
    public Customer createUser(Customer customer){
        try(HttpClient httpClient = HttpClient.newHttpClient()) {
            String requestBody = objectMapper().writeValueAsString(customer);

            HttpRequest httpRequest = HttpRequest.newBuilder(new URI(BASE_URL + "/users"))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .header("Content-type", "application/json")
                    .build();

            HttpResponse<String> stringHttpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (stringHttpResponse.statusCode() == 201 || stringHttpResponse.statusCode() == 200){
                return objectMapper().readValue(stringHttpResponse.body(), Customer.class);
            }else {
                System.out.println("Status code: " + stringHttpResponse.statusCode());
                return null;
            }
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public Customer updateUser(Customer customer){
        try(HttpClient httpClient = HttpClient.newHttpClient();) {
            String requestBody = objectMapper().writeValueAsString(customer);

            HttpRequest httpRequest = HttpRequest.newBuilder(new URI(BASE_URL + "/users/" + customer.getId()))
                    .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                    .header("Content-type", "application/json")
                    .build();
            HttpResponse<String> stringHttpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (stringHttpResponse.statusCode() == 200){
                return objectMapper().readValue(stringHttpResponse.body(), Customer.class);
            }else {
                System.out.println("Status code: " + stringHttpResponse.statusCode());
                return null;
            }
        }catch (URISyntaxException | IOException | InterruptedException e){
            throw new RuntimeException(e);
        }
    }
    public boolean deleteUser(int deleteID){
        try(HttpClient httpClient = HttpClient.newHttpClient();) {
            HttpRequest httpRequest = HttpRequest.newBuilder(new URI(BASE_URL+ "/users/" + deleteID))
                    .DELETE()
                    .build();
            HttpResponse<String> stringHttpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (stringHttpResponse.statusCode() == 200){
                System.out.println("User was successfully deleted");
                return true;
            }else{
                System.out.println("Status code: " + stringHttpResponse.statusCode());
                return false;
            }
        }catch (URISyntaxException | IOException | InterruptedException e){
            throw new RuntimeException(e);
        }
    }
    public List<Customer> allUsersInformation(){
        try(HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest httpRequest = HttpRequest.newBuilder(new URI(BASE_URL + "/users"))
                    .GET()
                    .build();
            HttpResponse<String> stringHttpResponse =  httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            stringHttpResponse.body();
            System.out.println(stringHttpResponse.statusCode());
            if (stringHttpResponse.statusCode() == 201 || stringHttpResponse.statusCode() == 200) {
                return objectMapper().readValue(stringHttpResponse.body(),
                        objectMapper().getTypeFactory().constructCollectionType(List.class, Customer.class));
            }else {
                System.out.println("Status code: " + stringHttpResponse.statusCode());
                return null;
            }
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public Customer userByID(int userID){
        try(HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest httpRequest = HttpRequest.newBuilder(new URI(BASE_URL + "/users/" + userID))
                    .GET()
                    .build();
            HttpResponse<String> stringHttpResponse =  httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (stringHttpResponse.statusCode() == 200){
                return objectMapper().readValue(stringHttpResponse.body(), Customer.class);
            }else {
                System.out.println("Status code: " + stringHttpResponse.statusCode());
                return null;
            }
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public List<Customer> userByUserName(String username){
        try(HttpClient httpClient = HttpClient.newHttpClient()){
            HttpRequest httpRequest = HttpRequest.newBuilder(new URI(BASE_URL + "/users?username=" + username))
                    .GET()
                    .build();

            HttpResponse<String> stringHttpResponse =  httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            int statusCode = stringHttpResponse.statusCode();

            if (statusCode == 200) {
                List<Customer> customers = objectMapper().readValue(stringHttpResponse.body(), new TypeReference<List<Customer>>() {});
                if (!customers.isEmpty()) {
                    return customers;
                } else {
                    System.out.println("User with username '" + username + "' not found.");
                    return null;
                }
            } else if (statusCode == 404) {
                System.out.println("User with username '" + username + "' not found.");
                return null;
            } else {
                System.out.println("Unexpected HTTP Status Code: " + statusCode);
                return null;
            }

        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public void lastPostComments(int userID){
        ObjectMapper objectMapper = new ObjectMapper();
        try(HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest postsRequest = HttpRequest.newBuilder(new URI(BASE_URL + "/users/" + userID + "/posts"))
                    .GET()
                    .build();
            HttpResponse<String> listPosts = httpClient.send(postsRequest, HttpResponse.BodyHandlers.ofString());
            if (listPosts.statusCode() == 200) {
                List<Post> postList = objectMapper.readValue(listPosts.body(), new TypeReference<List<Post>>() {});
                Optional<Post> postWithMaxID = postList.stream().max(Comparator.comparingInt(Post::getId));

                if (postWithMaxID.isPresent()) {
                    int maxPostId = postWithMaxID.get().getId();
                    HttpRequest commentsRequest = HttpRequest.newBuilder(new URI(BASE_URL + "/posts/" + maxPostId + "/comments"))
                            .GET()
                            .build();
                    HttpResponse<String> listComments = httpClient.send(commentsRequest, HttpResponse.BodyHandlers.ofString());

                    File file = new File("user-" + userID + "-post-" + maxPostId + "-comments.json");
                    try (FileWriter fileWriter = new FileWriter(file)) {
                        fileWriter.write(listComments.body());
                        System.out.println("File created");
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
    public List<Task> openTasks(int userId){
        ObjectMapper objectMapper = new ObjectMapper();

        try(HttpClient httpClient = HttpClient.newHttpClient()){
            HttpRequest tasksRequest = HttpRequest.newBuilder(new URI(BASE_URL + "/users/" + userId + "/todos"))
                    .GET()
                    .build();
            HttpResponse<String> listOfTasks = httpClient.send(tasksRequest, HttpResponse.BodyHandlers.ofString());

            if(listOfTasks.statusCode() == 200){
                List<Task> taskList = objectMapper.readValue(listOfTasks.body(), new TypeReference<List<Task>>() {});
                return taskList.stream()
                        .filter(isCompleated -> !isCompleated.isCompleted())
                        .collect(Collectors.toList());
            }else {
                System.out.println("Status code: " + listOfTasks.statusCode());
                return null;
            }
        }catch (URISyntaxException | IOException | InterruptedException e){
            throw new RuntimeException();
        }
    }
}
