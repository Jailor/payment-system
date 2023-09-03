# Payment System :currency_exchange:

## About :thought_balloon:
For my internship project, my team was tasked to create a payment system that allows for payments between accounts and managing the user data of the application. I was also tasked with creating an enhancement to the payment system, and I chose to use machine learning in order to detect fraudulent payments.

## Features :white_check_mark:
The application has a complex flow, with lots of entities: accounts, users, profiles, customers and payemnts. Users can log in to the system, and each suer can have a profile. The profile determines what rights inside the system that specific user has. Entities can be viewed with a filter, and everything that happens inside the system is logged and audited. The systems also performs fraud detection using a neural network. 
- **CRUD**: the system allows for CRUD for all entites in the system, according to the complex business rules that involve authorization, history, audit, approval and so on.
- **Users and Profiles**: all users of the system can log in, while their profiles contain information about what they can do inside the system. The user passwords are hashed.
- **Accounts and payments**: accounts belong to customers, which may or may not be users of the system. Payments must be verified and approved before they are processed, can be canceled at any time. The payments can be done in multiple currencies using exchange rates.
- **Audit**:  The list of operations performed on a certain entity, allows for quick lookup of who did what, a very important aspect of a financial system.
- **History**: All entities in the system have a history, a list of intermediary states that show the status of the object accross time.
- **4-eyes-check and approval**: A single user cannot both create and approve an entity, 2 users (2 sets of eyes) are necessary. The concept is extended for payments into an n-eyes check system.
- **Authentication**: Users log into the system in order to access it, while passwords are encrypted. Users can also change their passwords.
- **Authorization**: Users are only allowed to do what their profile allows them to do, and their UI menu is dynamically generated in accordance to their rights.
- **Fraud prevention** : the system makes use of machine learning in order to decide, based on payment information, if that payment is fraudulent or not. It checks features such as:
  - ratio to median transaction size
  - distance from home and last transaction
  - transaction frequency
  - transaction time

## Tech showcase :hammer:
- Java Web Application – Apache Tomcat as embedded application server
- Spring Boot REST API - The backend works as a spring boot REST api, responding with necessary data when it receives a request from the frontend
- Persistence - Hibernate ORM (JPA)
- Spring Security - The application uses spring security for authenticating the users.
- Frontend - React framework, boostrap, HTML, CSS, Javascript
- Misc - Log4j for logging
- **Tensorflow** - the model was trained using tensorflow (in google colab) then used as a REST api with flask that receives the data tensor and outputs if the payment is fraudulent or not.
## GUI :computer: 

