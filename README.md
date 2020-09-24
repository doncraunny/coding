# Transactions Microservice

A simple microwebservice to demonstrate the use of Spring Boot and JPA is designing a web application. 

This application handles generic "Transaction". The following operations are implemented
- GetAllTransactions: `/api/transactions`
- GetTransactionById: `/api/transactions/{id}`
- CreateTransaction: `/api/transactions/{id}`
- UpdateTransaction: `/api/transactions/`
- DeleteTransaction: `/api/transactions/{id}`

## Sample CreateTransaction JSON
{
  "clientId": "9d078bd3-ec13-4728-b484-8dbc2191c31b",
  "cardNumber": "1234-5678-9012-1231",
  "transactionAmount": "1002.929",
  "transactionDate": "2020-01-13T18:59:34+0000"
}
