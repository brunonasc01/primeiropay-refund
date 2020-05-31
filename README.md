# Refund
image:https://img.shields.io/badge/vert.x-3.9.1-purple.svg[link="https://vertx.io"]

This application was generated using http://start.vertx.io

## Building

To package your application:
```
./mvnw clean package
```
To run your application:
```
./mvnw clean compile exec:java
```

## Summary
This microservice offer an endpoint for PrimeiroPay Refund service

**URL** : `/refund/{id}`

**Method** : `POST`

**Auth required** : Bearer

Provide payment.id (path), amount and currency data to do the request.
```json
{
	"entity_id": "[hash key]",
	"amount":"[string]",
	"currency":"[string]"
}
```
**Data example** All fields must be sent.
```json
{
	"entity_id": "8ac7a4ca6db97ef1016dbe9214e70aac",
	"amount":"10.00",
	"currency":"EUR"
}
```

## Success Response

**Condition** : If everything is OK.

**Code** : `200 OK`

**Content example**
```json
{
	"id": "8ac7a4a2725fb8f401726b539d872299",
	"result": {
		"code": "000.100.110",
		"description": "Request successfully processed"
	}
}
```