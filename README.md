# Account Transfer RESTful API

This is a sample application. It was made specifically to show the working concept of RESTful API for money
transfers between accounts and other basic operations.

This is a completely standalone app. Basicaly it is just an `executable JAR built with maven`.
To create RESTful web service I mainly used `Jersey` in combination with `tomcat embedded server` and `H2` 
as in-memory database. 

## Install

    mvn clean verify 

## Run 

    java -jar account-transfer-${app.version}.jar 


It loads test data into the database automatically right after the start of this application. It means that you don't need
to waste your time on putting your own custom Accounts data into the table and you can start testing money transfers right away.

So here is the list of requests you can try in this simple app:

### Retrieve list of available accounts

```
    GET localhost:8099/api/accounts/all
```
Response example:
```
    HTTP 200 
[  
   {  
      "id":1,
      "balance":100
   },
   {  
      "id":2,
      "balance":200
   },
   {  
      "id":3,
      "balance":377
   },
   {  
      "id":4,
      "balance":500
   },
   {  
      "id":5,
      "balance":323
   }
]
```
### Retrieve account by ID

```
    GET localhost:8099/api/accounts/2
```
Response example:

```
    HTTP 200 
{
    "id": 2,
    "balance": 200
}
```
... in case of a nonexistent ID:
```
    HTTP Status 404
    
```

### Transfer money from one account to another

```
    PUT localhost:8099/api/transfer
```
Payload example:
```
{
	"sourceId": "1",
	"targetId": "5",
	"amount": "23.00"
}
```
Response example:

```
    HTTP 200 
{
    "status": "SUCCESS",
    "code": 1
}
```
... in case of an insufficient balance:
```
    HTTP 200 
{
    "status": "FAIL",
    "code": 2,
    "message": "Not enough money to complete transfer operation. Current balance: 77.00, Requested amount: 223.00"
}
```
