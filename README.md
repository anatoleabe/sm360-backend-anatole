# SM360 Backend Tech Assignment - By Anatole
This is a Java Spring Boot project that serves as the backend for SM360 and implements a REST service for managing listings for online advertising services.

# Features
The SM360 backend project includes the following features:

Create a listing with a draft by default. 
Update a listing;
Get all listings of a dealer with a given state;
Publish a listing;
Unpublish a listing.
Integration tests for all endpoints

# Installation
To install and run the SM360 backend project, follow these steps:

Clone the project from the GitHub repository:
```
git clone https://github.com/anatoleabe/sm360-backend-anatole.git
```
Navigate to the root directory of the project:
```
cd sm360-backend-anatole
```
Build and run the project using Maven:
```
mvn spring-boot:run
```

# Usage
Once the SM360 backend project is running, you can access the API endpoints using a tool like Postman. You can import the **SM360.postman_collection.json** file, which is located in the root directory of the project, to get a collection of pre-configured requests for the API.

The service will be a RESTful web service that provides endpoints for managing listings for a dealer with tier limits. The application will be available on port 9000 with the root path of /api.

Clients can access the service by making HTTP requests to the appropriate endpoints, using standard HTTP methods such as GET, POST, PUT, and DELETE.

For example, to create a new listing, a client might make a POST request to the following endpoint:
```
POST http://localhost:9000/api/listings
```

To get all of the listings for a particular dealer with a given state, a client might make a GET request to the following endpoint:

```
GET http://localhost:9000/api/listings/dealer/:dealerId?state={state}
```
To publish a listing, a client might make a PUT request to the following endpoint:

```
PUT http://localhost:9000/api/listings/{listingId}/publish
```
To unpublish a listing, a client might make a PUT request to the following endpoint:

```
PUT http://localhost:9000/api/listings/{listingId}/unpublish
