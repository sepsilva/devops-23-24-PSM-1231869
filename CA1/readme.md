# Report for Class assignment 1 - Luís Silva 1231869

## Report Structure
- [Description of assignment](#description-of-assignment)
- [Part 1 - Version control](#Part-1---Version control)
- [Part 2 - Spring framework](#Part-2---Spring framework)
  - [Task 1](#task-1)
  - [Task 2](#task-2)
  - [Task 3](#task-3)
  - [Task 4](#task-4)
- [Part 2.1 - Adding new features with branches](#Part-2.1---Adding-new-features-with-branches)
  - [Task 5](#task-5)
  - [Task 6](#task-6)
- [Conclusions and issues](#conclusions-and-issues)

## Description of assignment
This assignment is composed of two parts.  
The [first](#Part-1---Version control) focuses on version control systems and the differences between Git and other offers.   
The [second](#Part-2---Spring framework) part focuses on the Spring framework and using as a base the [basic project](tut-react-and-spring-data-rest/basic) of the [Spring Data REST tutorial](https://spring.io/guides/tutorials/react-and-spring-data-rest) to see some functionalities of the Spring framework and how this can help develop a REST application.

The goal for this assignment was to see how a project can be maintained and shared using VCS and how using frameworks like Spring can help in the development of an application.


## Part 1 - Version control
This project utilizes Git as a version control system, however there are other offers like Mercurial and HelixCore. Their use in a project depend on the project's requirements, level of user familiarity and even the project's budget.  

[Git](https://git-scm.com/about/branching-and-merging) is a widely used due to its flexibility, speed and due to it being free. It allows users to keep a full version of the project locally and work on it, adding features and branching out to test new ideas. It is mostly hassle free managing these branches and keeping track of changes in the project's data automatically.
Git also has a large community with lots of helpful resources and a wide range of tools that can be used to manage the project like GitHub or Bitbucket that can easily keep an online record of these projects.   
[Mercurial](https://www.mercurial-scm.org/about) is another VCS that is similar to Git, that also allows for users to keep a full copy of the project and allows the use of branches. However it seems to be less flexible.
Its commands are similar to Git like `hg init` to create a new repository and `hg commit` to commit changes, however it seems to be less [flexible](https://importantshock.wordpress.com/2008/08/07/git-vs-mercurial/) and has a smaller community. Online repositories like GitHub also support Mercurial however Git is the preferred choice for these platforms.

A while Git and Mercurial seem to be very similar, a problem shared by both is that they keep track of text based files. Files such as images, 3D models or videos are not handled by these VCS and, depending on the project these files must be tracked. Solutions like [HelixCore](https://www.perforce.com/products/helix-core) try to tackle this and target big operations like enterprises though it comes at a price.  
This software stores all the files in a central server and monitors changes while also allowing for branching however, due to its dimension it is not very friendly to beginners. 
## Part 2 - Spring framework
[1.](#task-1) Start up the application and verify the available nodes and the data they hold;  
[2.](#task-2) Add the ability to store the number of years the employee has worked for and their job type. Verify the parameters when creating a new Employee and create some unit tests for this class;  
[3.](#task-3) Add a new employee that is always loaded up when the application starts;  
[4.](#task-4) Create a new employee entry while the application is running;  

### Task 1)
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

### Task 2)
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
### Task 3)
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

### Task 4)
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

## Part 2.1 - Adding new features with branches
A very useful feature of version control systems like Git is the ability to create branches. This allows us to create new features, fix bugs, or generally test out something new without this affecting our main working branch (tipically named main/master).  
We already saw how tags can help us highlight important points in our project's development and how issues help to hightlight various tasks that need to be done.  
Branches are another tool that allow us to focus on the development of these new features and keep a clear history of its development without running the risk of affecting our main branch.  

[5.](#task-5)  Create a new branch and add a new field for an employee's email. When done, merge with main branch and tag the new version.  
[6.](#task-6)  Create a new branch and add a check to make sure that the email is contains the `@` character. When done, merge with main branch and tag the new version (minor number).
### Task 5)
- To create a new branch we run the command `git branch [branch name]`. We can then switch to this branch using `git checkout [branch name]`.  
We make sure we are on the correct branch by using `git branch` and we can see the one we're on by the `*` symbol.
```git
~/devops-23-24-PSM-1231869/CA1 (email-field)
$ git branch
* email-field
  main
```
- Similar to [task 2](#task-2) we add the new field to the Employee class and the add to the existing objects in the DatabaseLoader class. We also add the new field to the render method in the app.js file;
- We can verify everything is working correctly by running our application and checking `localhost:8080`, similar to [task 1](#task-1);
- When working on a branch, instead of committing and  pushing as we do normally, `git commit -m [MESSAGE]` followed by `git push` (which pushes to our remote repository assuming everything is configured properly), we commit our content normally and then push it to our remote repository using the command `git push origin email-field`;
- Since our new feature is working properly, we must now merge our new branch with our main branch. We start by going to our main branch using `git checkout main` but since we want to conserve our branch's history we won't simply run the `git merge email-field` command, running instead `git merge --no-ff email-field`, then we run `git push` to update our remote repository;
- By checking our network using `git log --graph` or online with Github, we see these two branches in parallel and then their merge.

### Task 6)
- Branches should be descriptive of their purpose. We want to add a check, to see if the employee email-field contains the `@` character. We repeat the process of creating a new branch named `fix-invalid-email` similar to [task 5](#task-5) and add the new check to the Employee class and some tests;
- We then merge our feature branch with our `main` and push it to Github;

## Conclusions and issues
Version control systems are critical when developing any kind of software as they allow the stakeholders and developers to keep track of changes, test out new features, highlight important milestones and keep a list of ongoing issues to be solved.  
Git seems to be a widespread choice, however there are competitors out there, focusing on different niches. We checked some of the following features that Git offers:
- Version control - Git allows us to keep track of changes in our project and easily revert to previous versions if needed;
- Tagging - Git allows us to highlight important versions of our project;
- Branching - Git allows us to create new branches to experiment with our code without fear of affecting our currently working code;  
- Issues - Git allows us to keep track of problems and to-do items in our project;  

We faced some issues, especially when it came to merging different branches. During [task 5](#task-5), an accidental merge was done without the `--no-ff` flag, which caused the branch's history to be lost. This was fixed by reverting to a previous commit with `git reset --hard` to commit `d014760`


We also explored the Spring framework and how it can help us develop applicaitons.
- Spring facilites the creation of an application, allowing us to focus on our application's core and automating many of the more mundane tasks that were once needed required to be created by hand (example: methods to add items like what was done in [task 4](#task-4)).

One problem that was noticed in [task 4](#task-4) was that, despite the restrictions added in [task 3](#task-3), running a POST command with incorrect information still allows the application to display the data. More restrictions would be required to avoid the application being populated with incorrect data.  
Example of POST with empty `firstName` and negative `jobYears` fields:
```bash
curl -X POST localhost:8080/api/employees -d "{\"firstName\": \"\", \"lastName\": \"Gamgee\", \"description\": \"Friend\", \"jobTitle\": \"Friend of Frodo\" , \"jobYears\": \"-50\"}" -H "Content-Type:application/json"
```
This is something that should be addressed in future class assignments, so our application is flexible and not so prone to external errors.


