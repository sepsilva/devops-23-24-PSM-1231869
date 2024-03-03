# Report for Class assignment 1 - Luís Silva 1231869

## Report Structure
- [Description of assignment](#description-of-assignment)
- [Tasks](#tasks)
  - [Task 1](#task-1)
  - [Task 2](#task-2)
  - [Task 3](#task-3)
  - [Task 4](#task-4)
- [Conclusions and issues](#conclusions-and-issues)

## Description of assignment
This assignment used as a base the [basic project](tut-react-and-spring-data-rest/basic) of the [Spring Data REST tutorial](https://spring.io/guides/tutorials/react-and-spring-data-rest) to see some functionalities of the Spring framework and how this can help develop a REST application.  
The goal for this assignment was to see how the project worked, its components responsible for both the backend and frontend of the application, and how we could add some new functionalities to it.

## Tasks
[1.](#task-1) Start up the application and verify the available nodes and the data they hold;  
[2.](#task-2) Add the ability to store the number of years the employee has worked for and their job type. Verify the parameters when creating a new Employee and create some unit tests for this class;  
[3.](#task-3) Add a new employee that is always loaded up when the application starts;  
[4.](#task-4) Create a new employee entry while the application is running;  

## Task 1)
- To run the application we must make sure we are in the root of the [basic project](tut-react-and-spring-data-rest/basic) using preferably a bash terminal such as git bash;
- Run the `./mvnw spring-boot:run command` to start the application;
- By using the browser we can see the list of employees and their information by going to `localhost:8080`. We could also visit the rest of the application nodes and see the JPA information. For example when browsing to `localhost:8080/api` we can see:
  ```json
  {
  "_links" : {
    "employees" : {
      "href" : "http://localhost:8080/api/employees"
      },
    "profile" : {
      "href" : "http://localhost:8080/api/profile"
      }
    }   
  }
  ```

- By using the inspect tools in browsers like Chrome we can also see the sections created by the `render()` method in the [app.js file](tut-react-and-spring-data-rest/basic/src/main/js/app.js);

## Task 2)
- To add new fields to the [Employee object](tut-react-and-spring-data-rest/basic/src/main/java/com/greglturnquist/payroll/Employee.java) we must add the following
    - Add the jobYears and jobTitle to class attributes:   
    ```java
    private String jobTitle;  
    private int jobYears; 
    ```
    - Add these as parameters in the object constructor signature and define them:  
    ```java
    public Employee(String firstName, String lastName, String description, String jobTitle, int jobYears)`  
    ```
    ```java
    this.jobTitle = jobTitle;  
    this.jobYears = jobYears;
    ```
- Add a method to verifiy the attributes used when attempting to create a new Employee object. Strings shouldn't be blank and Job years shouldn't be negative:
```java
private boolean validConstructorArguments (String firstName, String lastName, String description, String jobTitle, int jobYears) {
  if (firstName.isBlank() || lastName.isBlank() || description.isBlank() || jobTitle.isBlank()) {
      return false;
  }
  if (jobYears < 0) {
      return false;
  }
      return true;
}
```
The functionality of this method is assured by unit [tests](tut-react-and-spring-data-rest/basic/src/test), for example:
```java
private String validEmployeeFirstName = "John";
private String validEmployeeLastName = "Man";
private String validJobDescription = "Man with family";
private String validJobTitle = "Family man";
private int validJobYears = 10;

@Test
void constructEmployeeWithValidAttributes () {
    assertDoesNotThrow(() ->new Employee(validEmployeeFirstName, validEmployeeLastName, validJobDescription, validJobTitle, validJobYears));
}
```
- We must make sure the application renders these new entries:
  - The toString method is responsible for passing the objects attributes to string to transmit data. We must add the new fields so they can be displayed and rendered:
  ```java
  @Override
  public String toString() {
		return "Employee{" +
				"id=" + id +
				", firstName='" + firstName + '\'' +
				", lastName='" + lastName + '\'' +
				", description='" + description + '\'' +
				", jobTitle='" + jobTitle + '\'' +
				", jobYears='" + jobYears + '\'' +
				'}';
  } 
  ```
  - Add the new table headers (EmployeeList class) and the data to fill in those headers (Employee class), to the render methods in the [apps.js](tut-react-and-spring-data-rest/basic/src/main/js/app.js) file:
  ```javascript
  class EmployeeList extends React.Component{
    render() {
        const employees = this.props.employees.map(employee =>
        <Employee key={employee._links.self.href} employee={employee}/>
        );
        return (
            <table>
                <tbody>
                    <tr>
                        <th>First Name</th>
                        <th>Last Name</th>
                        <th>Description</th>
                        <th>Job Title</th>
                        <th>Job Years</th>
                    </tr>
                    {employee}
                </tbody>
            </table>
        )
    }
  }
  ```
  ```javascript
  class Employee extends React.Component{
    render() {
      return (
        <tr>
          <td>{this.props.employee.firstName}</td>
          <td>{this.props.employee.lastName}</td>
          <td>{this.props.employee.description}</td>
          <td>{this.props.employee.jobTitle}</td>
          <td>{this.props.employee.jobYears}</td>
        </tr>
      )
    }
  }
  ```
- Upon reloading the aplication, like in [task 1](#task-1), we see that our table now updated with the headers showing a column for Job years and Job title.
- We verify that everything is running and commit our project with the tag ` v1.2.0`
## Task 3)
- The java class [DatabaseLoader](tut-react-and-spring-data-rest/basic/src/main/java/com/greglturnquist/payroll/DatabaseLoader.java) is responsible for loading items into our application;  
  - First we populate the new fields added in [task 2](#task-2) for the already existing employee:
  ```java
  @Override
  public void run(String... strings) throws Exception { // <4>
    this.repository.save(new Employee("Frodo", "Baggins", "ring bearer", "Unemployed", 0));
  }
  ```
  - Then we add a new employee to be loaded upon application startup:
  ```java
    this.repository.save(new Employee("Bilbo", "Baggins", "burglar", "Retired", 1));
  ```
- We reload the application again and see that the table is now populated for all headers and we see the new employee entry.

## Task 4)
- Since this is a REST application we can retrieve, add, remove and update using known HTTP methods like GET, POST, DELETE and PATCH. This allows us to manipulate information as the application is running;
- We can go to our browser and go to `localhost:8080` and we see the entries added in [task 3](#task-3);
- With our application running, we can try adding a new employee to our list by running a POST method in our bash terminal. We must make sure that the information that we want to add is already in the JPA format, so it can be properly processed.  
`curl -X POST [DESTINATION] -d [INFORMATION FORMATED IN THE PROPER FORMAT]`  
```bash
curl -X POST localhost:8080/api/employees -d "{\"firstName\": \"Samwise\", \"lastName\": \"Gamgee\", \"description\": \"Friend\", \"jobTitle\": \"Friend of Frodo\" , \"jobYears\": \"50\"}" -H "Content-Type:application/json"
```
  - In our terminal we see a confirmation that our data was successfuly added  
  ```json
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
  100   447    0   326  100   121   4618   1714 --:--:-- --:--:-- --:--:--  6385{
    "firstName" : "Samwise",
    "lastName" : "Gamgee",
    "description" : "Friend",
    "jobTitle" : "Friend of Frodo",
    "jobYears" : 50,
    "_links" : {
      "self" : {
        "href" : "http://localhost:8080/api/employees/3"
      },
      "employee" : {
        "href" : "http://localhost:8080/api/employees/3"
      }
    }
  }
  ```
- We can refresh refresh our browser and now we see that Samwise Gamgee is now a table entry

## Conclusions and issues
We see how version control systems like Git allow us to keep track of changes in our projects and how we can flag important versions with the use of tags and easily keep track of problems/to-do items using issues in platforms like Github along with proper commit messages.  
We can also see how frameworks like Spring facilitate the creation of an application, allowing us to focus on our application's core and automating many of the more mundane tasks that were once needed required to be created by hand (example: methods to add items like what was done in [task 4](#task-4)).  

One problem that was noticed in [task 4](#task-4) was that, despite the restrictions added in [task 3](#task-3), running a POST command with incorrect information still allows the application to display the data. More restrictions would be required to avoid the application being populated with incorrect data.  
Example of POST with empty `firstName` and negative `jobYears` fields:
```bash
curl -X POST localhost:8080/api/employees -d "{\"firstName\": \"\", \"lastName\": \"Gamgee\", \"description\": \"Friend\", \"jobTitle\": \"Friend of Frodo\" , \"jobYears\": \"-50\"}" -H "Content-Type:application/json"
```

