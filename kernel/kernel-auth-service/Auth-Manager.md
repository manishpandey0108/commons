# AuthManager #

## Overview ##

MOSIP AuthManager is a multi-factor authentication solution that verifies authentication requests and centrally administers authentication policies for enterprise networks.

The AuthManager provides an easy way to manage user authentication (logging in and out) and authorization (keeping track of tokens, sessions, and groups) for MOSIP users. The AuthManager is most useful for MOSIP team with an access level of Authenticated. These are accessed by end users who are members/enrolled with MOSIP.

### Key Concept ###

The MOSIP platform handles authentication and authorization in a centralized location. The standards are based on the OAuth 2.0 authorisation framework, with JSON Web Token (JWT) serving as a means for implementing the framework.

*	A centralized AUTH SERVER (Auth Service with an IAM example Keycloak) handles the authorization request from the platform
*	Once authenticated, the Auth Server sends back an AuthToken
*	Auth token contains the information about the authenticated user and the meta data such as the expiration time, subject, issuer etc.
*	The Tokens are stored in the IAM for an individual user. In case of force logout scenario, this record will be deleted from this datastore

### Epicenter Channel Manager ###

The Epicenter platform provides a push channel, which allows you to publish and subscribe to messages within MOSIP:

*	No resource in the MOSIP can be accessed without Authentication and Authorization
*	All the auth requests will go via the Auth Server
*	The user data stores are abstracted behind the IAM
*	The user data stores should be pluggable(If supported by configured IAM)
*	The platform's authentication method should support the heterogenous technologies and authentication should happen seamlessly

## AuthManager Process Flow ##

![](_images/kernel-auth-service/token_generation.png)

