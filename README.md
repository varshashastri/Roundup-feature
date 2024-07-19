Project Title: Roundup service API

Project description: The Roundup Service API provides endpoints for calculating and retrieving roundup amounts for customer accounts and then transfering the roundup amounts into savings goal space.
For simplicity in this project we assume there is one savings goal per account. Each customer can have multiple accounts and each account can have one savings space.

How to run it locally:
Uses java 21.
Import as maven project in any IDE and run as Spring boot application.

Endpoints:
GET: /getRoundUps/allAccounts: Retrieves roundup amounts for all customer accounts.
GET: /getRoundUps/{accountUid}: Retrieves the roundup amount for a specific account identified by accountUid.
PUT: /transferToSavings/all: Transfers roundup amounts in all accounts to respective savings goal
PUT: /transferToSavings/{accountUid}: Transfers roundup amounts in specific account of the customer to the savings goal associated with that account.

Future code improvements:
1. Improve tests to cover more negative scenarios especially in util classes.
2. Improve logging and exception handling and provide more context in the log messages.
3. Add javadocs to functions in controller and service

