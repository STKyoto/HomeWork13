package org.example.CustomerService;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

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

public class UserService {
    private final static String BASE_URL = "https://jsonplaceholder.typicode.com/users";

    public ObjectMapper objectMapper(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
    public void createUser(Customer customer){
        try(HttpClient httpClient = HttpClient.newHttpClient()) {
            String requestBody = objectMapper().writeValueAsString(customer);

            HttpRequest httpRequest = HttpRequest.newBuilder(new URI(BASE_URL))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .header("Content-type", "application/json")
                    .build();

            HttpResponse<String> stringHttpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println("HTTP Status Code: " + stringHttpResponse.statusCode());
            System.out.println("Response Body: " + stringHttpResponse.body());
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public void updateUser(Customer customer){
        try(HttpClient httpClient = HttpClient.newHttpClient();) {
            String requestBody = objectMapper().writeValueAsString(customer);

            HttpRequest httpRequest = HttpRequest.newBuilder(new URI(BASE_URL + "/" + 1))
                    .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                    .header("Content-type", "application/json")
                    .build();
            HttpResponse<String> stringHttpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println("HTTP Status Code: " + stringHttpResponse.statusCode());
            System.out.println("Response Body: " + stringHttpResponse.body());
        }catch (URISyntaxException | IOException | InterruptedException e){
            throw new RuntimeException(e);
        }
    }
    public void deleteUser(int deleteID){
        try(HttpClient httpClient = HttpClient.newHttpClient();) {
            HttpRequest httpRequest = HttpRequest.newBuilder(new URI(BASE_URL + "/" + deleteID))
                    .DELETE()
                    .build();
            HttpResponse<String> stringHttpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println("HTTP Status Code: " + stringHttpResponse.statusCode());
            System.out.println("Response Body: " + stringHttpResponse.body());
        }catch (URISyntaxException | IOException | InterruptedException e){
            throw new RuntimeException(e);
        }
    }
    public void allUsersInformation(){
        try(HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest httpRequest = HttpRequest.newBuilder(new URI(BASE_URL))
                    .GET()
                    .build();
            HttpResponse<String> stringHttpResponse =  httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            stringHttpResponse.body();
            System.out.println(stringHttpResponse.statusCode());
            List<Customer> responceList = objectMapper().readValue(stringHttpResponse.body(),
                    objectMapper().getTypeFactory().constructCollectionType(List.class, Customer.class));
            responceList.forEach(System.out::println);
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public void userByID(int userID){
        try(HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest httpRequest = HttpRequest.newBuilder(new URI(BASE_URL + "/" + userID))
                    .GET()
                    .build();
            HttpResponse<String> stringHttpResponse =  httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println("HTTP Status Code: " + stringHttpResponse.statusCode());
            System.out.println("Response Body: " + stringHttpResponse.body());
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public void userByUserName(String username){
        try(HttpClient httpClient = HttpClient.newHttpClient()){
            HttpRequest httpRequest = HttpRequest.newBuilder(new URI(BASE_URL + "?username=" + username))
                    .GET()
                    .build();

            HttpResponse<String> stringHttpResponse =  httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            int statusCode = stringHttpResponse.statusCode();

            if (statusCode == 200) {
                System.out.println("HTTP Status Code: " + statusCode);
                System.out.println("Response Body: " + stringHttpResponse.body());
            } else if (statusCode == 404) {
                System.out.println("User with username '" + username + "' not found.");
            } else {
                System.out.println("Unexpected HTTP Status Code: " + statusCode);
            }

        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public void lastPostComments(int userID){
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
    public void openTasks(int userId){
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
                        .filter(isCompleated -> !isCompleated.isCompleted())
                        .forEach(System.out::println);
            }
        }catch (URISyntaxException | IOException | InterruptedException e){
            throw new RuntimeException();
        }
    }
}
