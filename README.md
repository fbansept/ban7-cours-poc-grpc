## But du projet ##

Présenter un Proof Of Concept d'une communication gRCP entre 2 serveurs
Le but est de simuler une architecture micro-service

Le premier serveur que l'on nommera "client" (note : c'est un serveur mais il effectue une requête vers le second)
Il permet d'effectuer une requête REST pour valider un panier fictif
Puis il effectue la requête gRCP sur le second serveur

Le second serveur reçoit la réponse et répond au client

## Structure du projet ##
1 projet parent, basé sur Spring boot 3

2 modules :
- client 
- serveur

## Tester le projet ##

### Etape 1 ###

Vous aurez certainement des erreurs dans le pom liées à la variable : ${os.detected.classifier}
Effectuer un "Reload All maven project" pour la corriger

### Etape 2 ###

Un certain nombre de classe sont définies et créées via les fichiers .proto 

Il est donc obligatoire d'exécuter les plugins maven appropriés avant de compiler l'application

sur le module serveur et sur le module client : exécuter la phase clean puis compile

Vous obtiendrez surement des erreurs "Cannot find symbol" pendant 2 secondes, puis les erreur disparaissent

### Etape 3 ###

Vous pouvez alors lancer le serveur en premier, puis le client en deuxième

Si vous avez une erreur "Could not find or load main class", effectuez un clic droit sur le module dans l'arborescence du projet, puis "Rebuild Module ..."

### Etape 4 ###

importer le projet .jetclient (installez au préalable l'extension jetClient)
Et testez la requête enregistrée

ou effectuer la requete sur un client (Postman, Curl, Thunderclient...) :

POST http://localhost:8080/api/cart/validate

JSON body :

    {
        "cartId":"cart-123",
        "amount": 99.99,
        "currency": "EUR",
        "paymentMethod": "CREDIT_CARD",
        "customer": {
            "customerId": "cust-001",
            "email": "test@example.com",
            "name": "Jean Dupont"
        }
    }
