# Key Manager #

## Overview ##

The Key Manager service provides secure storage, provisioning and management of secret data. This includes keying material such as symmetric keys, asymmetric keys, certificates and algorithm data.

It is a web-based key management solution that helps consolidate, control, manage, monitor, all keys generation & maintenance of key life cycle required in MOSIP. It provides secure storage, provisioning and management of secrets, such as passwords, encryption keys, etc.

Provides all the cryptographic operations like encryption/decryption & digital signature/verification making one trust store for all partner trust path validation. It manages the lifecycle of encryption/decryption keys, including generation, distribution, administration, and deletion.

## Prerequisite Software ##

There is no prerequisite software installation required to use Key Manager Service. The standard system requirements as mentioned below and an external mail server are essential for the functioning of Key Manager server and to send various notifications to users.
*	RSA algorithm & 2048-bit key size is used for all data encryption
*	AES 256-bit key is used for ZK encryption (specific design for IDA data storage)

## Managing Key Manager Service Encryption Key ##

Key Manager uses AES - 256 encryption to secure keys, certificate and other sensitive information in its database. The key used for encryption is auto-generated and key will be assigned with an unique UUID. All Master keys are stored in HSM and encryption keys (base keys) are encrypted with component specific master key and stored in DB. You can find all the keys used in the file named ```key_alias``` assigned with an unique UUID.

Key Manager stores the path of the encryption key + partner certificate (no private key) in a configuration file named ```key_store``` with an unique UUID & also has mapping of master key used. Key Manager does not allow you to store the encryption key within the Key Manager. This is done to prevent storing of both the encrypted key and encrypted data, in both live and backed-up database, together in the same place.

## Generate and Deploy New Keys ##

Key Manager Service provides an API repository which allows various applications and processes across your network to integrate with the Key Manager server and retrieve key and certificate-related information to be used in applications or databases.

### Key Mapping ###

 You can map/identify the key with mentioned two attributes:

```APPLICATION_ID (APP_ID)``` -  MOSIP component name is APP_ID

Eg: REGISTRATION

```REFERENCE_ID (REF_ID) ```- blank for master key, specific name in case of encryption key (base key)

Eg: 10001_110011

It also provides a graphical map of key-user relationships that helps you effectively track the distribution and usage of keys within your network.

### Key Generation Process ##

Key Manager helps you generate new key pairs and deploy them to endpoint servers. There are two ways that allows to generate fresh key pairs from Key Manager.
*	key-generator job, and
*	Rest API call

![](_images/keymanager_hsm_keygenerator.png)

The job generates option under ``` key-generator job ``` allows you to generate all the configured ```APP_IDs``` master keys in HSM. It associate only module keys.

Alternatively, you can also generate key using Rest API call ```/keymanager/generateMasterKey``` and simultaneously deploy them to various user accounts. This can be done with help of ```KEY_MAKER``` wherein it is not required generating key with generator job.

### Generate Encryption Keys ###

Key Manager stores the path of the encryption key in a configuration file with an unique UUID. Therefore, Encryption keys are automatically generated if it’s not available in Database. 

Once the API repository that allows call on ```getCertificate/generateCSR``` API will automatically generates the key if not available in Database and returns the certificate/CSR.

### Perform and Track Key Management Operations ###

![](_images/keymanager_chain_of_trust.png)

Once keys are associated with their user, you can perform a range of key management operations like crypto that allows:

*	Digital Signing of data using module key
*	Data encryption using encryption key/base key

Key Manager provides a set of options which you can customize/use according to your requirements:

*	MOSIP ROOT key : It is self-signed key and issue component level master key
*	Module Master key : This key issue component level encryption key

Eg: 

*	MOSIP Root key
	*	REGISTRATION module key issued by root key
		*	10001_110011 registration client key issued by REGISTRATION component

![](_images/type_key_example.PNG)

The different key in MOSIP have their own built-in validity as mentioned:

*	MOSIP root key - 5 years
*	Module key - 3 years
*	Encrytion key (base key) - 2 years

You can edit key validity before key generation by updating the required values in ```key_policy_def``` table.

### Key Revoke/Rotate ###

You can configure existing key to revoke MOSIP root key or module key invoke ```/keymanager/generateMasterKey``` API with **force** attribute as **true**. API invalidates existing key and immediately generates new key.
 
Whereas, you can configure existing key to revoke encryption/base key invoke ```/keymanager/revokeKey``` Rest API with the respective ```APP_ID``` and ```REF_ID```.

## Encryption/Decryption Process ##

![](_images/keymanager_hsm_integration.png)

We strongly recommend that you store the encryption key outside Key Manager server - preferably in any other separate machine or in any external drive (hard drive, thumb drive etc.,). And in such cases, you have to make sure that Key Manager server has full permission to access the device and the encryption key stored in it, whenever you start Key Manager service.

### Encryption Process ###

*	Random AES 256-bit key will be generated, generated random key will be used to encrypt the actual registration packet
*	Random generated key will be encrypted using the certificate received from server. Certificate contains RSA 2048 bit key
*	Certificate Thumbprint will be computed
*	Thumbprint will be prepend to encrypted random key for key identification
*	Finally encrypted random key with prepended thumbprint will be concated with encypted registration packet using #KEY_SPLITTER# as seperator

### Decryption Process ###

*	Registration packet data will be splited to get the encrypted random key, encrypted registration data, certificate thumbprint
*	Identifies the respective private key to decryption process
*	Identified private key will be decrypted with the mapped master key
*	Decrypted private key will be used to decrypt the encrypted random key
*	Decrypt the registration packet using the decrypted random key
*	Returns the decrypted data to REG_PROC

### Encryption in Reg-Client ###

*	Reg-client sends request to sync data service for the client configuration data
*	Sync Data service requests KeyManager service to provide the reg-client specific certificate. Key identifier will be APP_ID - REGISTRATION, REF_ID - CENTER-ID_MACHINE-ID
*	Keymanager service generate a new key pair, encrypts the private key with REGISTRATION master key and creates a new cerificate using same master. 
*	Returns the certificate to sync data service. If key pair already available and is valid, returns the available certificate
*	Sync data service send the certificate to reg-client
*	The registration packet will be encrypted using the certificate received from the server after collecting all required data for registration, including adding the digital signatures required to the registration data, and before saving/writing the data on the reg-client hard-disk
*	REG_PROC sends request to decrypt the data to key manager service with same app_id & ref_id







