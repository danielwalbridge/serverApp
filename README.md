# serverApp


The original README.md can be found in the same directory as this, and is now called READMEOG.md ( in case you want to use it as a reference).

I have packaged 3 microservices together to get this app to work.
You should see 4 packages:
ConsolApp
EurekaServer
serverApp
MicroserviceDD ( only really has the read me files)

# TO RUN THE APPS #
Please start the apps in the following order.

EurekaServer
Navigate to : src/main/java/com/example/eurekaserver/EurekaServerApplication.java
Run the class to start the EurekaServer

serverApp
Navigate to: src/main/java/com/example/serverapp/ServerAppApplication.java
Run class to start the server app

ConsolApp
Navigate to:
src/main/java/com/example/consolapp/ConsoleAppApplication.java
Run the class to start the console app

# Overview of the apps #
EurekaServer
- There to help the server app and console app communicate with each other.

Console app
- a basic CLI that takes in user input, and sends it to the server app to communicate with the GoodReads API, then displays what it gets back.
- This is the app that you will be interacting with and typing into the CLI. You should be able to type to it once it’s started ( please make sure the other apps are running first)

Server app
- takes information sent to it from the console app, passes it forward to goodreads, formats the data and sends it back to the console to be displayed.


# Things of note/ to consider: #
Because of the use of the @PostContrct method in the console app, once in a while ( normally close to the start of the run) you will see an error that the time stamp is off from the Eureka app. After the error pops up once, it should not again.
- In a real world situation, I’d fix this, or we most likely would not be using a command line app, so it would not be a problem to start with.
  In the real world, I would have test cases, but since it was a non requirement I chose to skip them and get the code back to you sooner.
  In the real world, I would not have so many // comments in the code, since the code can easily be updated while leaving comments behind…making all sorts of messes…I have them in here since this is a one and done thing/ it should help you understand it quicker.
  I’d put more validations in methods in the real world, but for this I mostly went down the ‘happy developing path’
  In the real world, there would probably be some way to stop the app from running.


# Assumptions I made.#
The information passed to me led me to make some assumptions about the functional requirements. Here are some of them
- Should I pass the dev key from the client side? ( I choose to just have it on the server side for simplicity)
- Does the sort function from the functional requirements want to sort already brought back results, or get new results with that additional parameter? ( I choose to get new results with the additional parameter)
  Many more, but this is getting long already and I don’t want to bore you. I’d be happy to speak about any of them at length at the next interview. 


