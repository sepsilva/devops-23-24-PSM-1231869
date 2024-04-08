Task 1
Issue #13
Create branch using git branch
Go to branch using checkout
Generate project base using spring io
Copy over to folder
Run ./gradlew tasks and ./gradlew tasks --all to see all tasks
Exclude called folders
Change import in employee.class from javax to jakarta
We build the application using ./gradlew bootrun
Check localhost:8080 to see it is running/empty


Task 2
Issue #14
Added to build.gradle
id 'org.siouan.frontend-jdk17' version '8.0.0'

frontend {
nodeVersion = "16.20.2"
assembleScript = "run build"
cleanScript = "run clean"
checkScript = "run check"
}


Added to webpack.config.js
scripts: {
webpack: 'webpack',
build: 'npm run webpack',
check: 'echo Checking frontend',
clean: 'echo Cleaning frontend',
lint: 'echo Linting frontend',
test: 'echo Testing frontend'
}

Add fix provided by prof to package.json file
run gradlew build
run gradlew bootrun

branch merge done accidentally

Task 3
Issue #16
Added copy task to build.gradle
task archiveCopy (type:Copy) {
group = "CA2/Part2"

	description = "Create a copy of generated .jar file"

	def jarfile = file('./build/libs/react-and-spring-data-rest-basic-0.0.1-SNAPSHOT.jar')

	def destination = file('./dist')

	from jarfile into destination
}
