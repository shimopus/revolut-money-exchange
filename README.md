# Transfer Money Application

##Preface
The implementation of the test task from Revolut on Java position

##About
A RESTful API that allows transfer money from one Bank Account to another in any currency.

It uses just to entities:
* _transaction_ - the money transfer transaction used to initialize the transaction
* _bank account_ - the bank account which has balance in the specified currency

Right now currency conversion is implemented only in a simple constant mode with specific conversion rates. 

The API was developed using Java 8 with embeded Grizzly server and H2 database.

This API guaranties the data consistency in any case. Even if it will be a huge amount concurrent users. 
This ability was achieved by using of `select ... for update` database feature which helps to lock the object until all related objects will be updated/created 
 
## Requires
* Java 8
* Maven

## How to start

Once the application is fetched from git it can be built with maven

    mvn clean install
    
This will fetch dependencies and run all tests

To run the app execute:

    mvn exec:java
or

    java -jar /target/revolute-money-exchange-0.0.1-jar-with-dependencies.jar

The application will start on the `localhost` and will be listening to the port `8080`

## API Definition

### Bank Account
The bank account entity which has balance in the specified currency and could transfer the money
if there is enough money.

#### Structure
    {
        "id": <number>,
        "ownerName": <string>,
        "balance": <double>,
        "blockedAmount": <double>,
        "currency": <string - one from "RUB", "USD", "EUR">
    }

#### Create Bank Account

The following creates bank account and returns the created entity with `ID` specified

    POST /bankAccounts
    {
        "ownerName": "Sergey Babinskiy",
        "balance": 12.6,
        "blockedAmount": 0,
        "currency": "RUB"
    }

Example response:

    HTTP 200 OK
    POST /bankAccounts
    {
        "id": 1,
        "ownerName": "Sergey Babinskiy",
        "balance": 12.6,
        "blockedAmount": 0,
        "currency": "RUB"
    }
    
#### List all Bank Accounts

The following gets all the bank accounts that exist in the system

    GET /bankAccounts

Example response:


    HTTP 200 OK
    [{
         "id": 1,
         "ownerName": "Sergey Babinskiy",
         "balance": 12.6,
         "blockedAmount": 0,
         "currency": "RUB"
    }]

#### Get Bank Account details

The following gets the particular account if it exists in the system

    GET /bankAccounts/1

Example response:

    HTTP 200 OK
    {
        "id": 1,
        "ownerName": "Sergey Babinskiy",
        "balance": 12.6,
        "blockedAmount": 0,
        "currency": "RUB"
    }

#### Update Bank Account details

The following updates the details of the particular account if it exists in the system
You can not update any field except "ownerName"

    PUT /bankAccounts/1
    {
        "id": 1,
        "ownerName": "Sergey",
    }

Example response:

    HTTP 200 OK
    {
        "id": 1,
        "ownerName": "Sergey",
        "balance": 12.6,
        "blockedAmount": 0,
        "currency": "RUB"
    }
        
### Transaction
The money transfer transaction used to initialize the transaction. Once created
will be executed automatically. If transaction can not be created by some reason the Error(HTTP 500 Internal Error) 
will be returned with details in the body.
You can not update transaction object as it is controversial to the logic that transaction can
not be modified once created.  

#### Structure
    {
        "id": <number>,
        "fromBankAccountId": <number>,
        "toBankAccountId": <number>,
        "amount": <double>,
        "currency": <string - one from "RUB", "USD", "EUR">,
        "creationDate": <timestamp>,
        "updateDate": <timestamp>,
        "status": <string - one from "PLANNED", "PROCESSING", "FAILED", "SUCCEED">,
        "failMessage": <string>
    }
    
#### Create a transaction

The following creates a new transaction if possible (valid Bank Accounts and parameters should be provided).
Once `id`, `creationDate`, `updateDate` or `status` provided they  will be ignored. 
You can obtain the generated values of these fields in the response of this call. 

    POST /transactions
    {
        "fromBankAccountId": 1,
        "toBankAccountId": 2,
        "amount": 16.1,
        "currency": "EUR"
    }
    
Example response:

    HTTP 200 OK
    {
        "id": 1,
        "fromBankAccountId": 1,
        "toBankAccountId": 2,
        "amount": 16.1,
        "currency": "EUR",
        "creationDate": 1537303715995,
        "updateDate": 1537303715995,
        "status": "PLANNED",
        "failMessage": ""
    }

#### Get all transactions

    GET /transactions

Example response:

    HTTP 200 OK    
    [{
        "id": 1,
        "fromBankAccountId": 1,
        "toBankAccountId": 2,
        "amount": 16.1,
        "currency": "EUR",
        "creationDate": 1537303715995,
        "updateDate": 1537303715995,
        "status": "PLANNED",
        "failMessage": ""
    }]
    
#### Get a specific transaction by its ID

    GET /transactions/1

Example response:

    HTTP 200 OK    
    {
        "id": 1,
        "fromBankAccountId": 1,
        "toBankAccountId": 2,
        "amount": 16.1,
        "currency": "EUR",
        "creationDate": 1537303715995,
        "updateDate": 1537303715995,
        "status": "PLANNED",
        "failMessage": ""
    }
    
### Exception Handing
If any error will be thrown by some reason the Error (HTTP 500 Internal Error) 
will be returned with details in the body.

Example response:

    HTTP 500 Internal Error
    {
        "type": "OBJECT_IS_NOT_FOUND",
        "name": "The entity with provided ID has not been found",
        "message": "Some details",
    }    