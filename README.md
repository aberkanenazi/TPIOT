# Projet scolaire IoT

## Description

Ce projet vise à utiliser un ESP32 pour collecter des données de température et d'humidité via un capteur DHT22, et les afficher sur un tableau de bord Node-RED. Le projet intègre également un téléphone Samsung S20 pour interagir avec le système et visualiser les données.

## Matériaux et Logiciels Utilisés

### Matériaux
- ESP32 (avec LED et capteur DHT22)
- Samsung S20
- Ordinateur

### Logiciels
- Arduino IDE
- Ubuntu 18.04 LTS
- Android Studio
- MQTT (Mosquitto)
- Node-RED

## Structure du Projet

1. **Connexion ESP32**: L'ESP32 se connecte au routeur via Wi-Fi pour communiquer avec le serveur MQTT.
2. **Connexion Téléphone**: Le téléphone se connecte au routeur et au serveur MQTT pour récupérer et afficher les données de température et d'humidité. Il permet également de modifier les paramètres de l'ESP32.
3. **Tableau de Bord Node-RED**: Un tableau de bord Node-RED est utilisé pour visualiser les données collectées.

## Installation

### Installation MQTT sur le Serveur
Instructions pour installer MQTT (Mosquitto) sur le serveur.

### Installation Node-RED
Instructions pour installer Node-RED et lancer l'application.

### Ajout de Librairies à Node-RED
Ajouter la librairie suivante à Node-RED via l'onglet Palette.

## Code Arduino
![Image3](https://github.com/aberkanenazi/TPIOT/assets/35194684/4981e0c1-6d3f-4d28-a6e2-14705a0b4193)
![Image4](https://github.com/aberkanenazi/TPIOT/assets/35194684/d6cdda85-5ba7-4b22-9515-2f83d04662eb)

### Lecture de la Température et Publication

Ce code récupère la température à intervalles fixes (par défaut, 10 secondes). Si la connexion au serveur MQTT est établie, les données de température et d'humidité sont publiées. Une LED s'allume si la température dépasse un certain seuil.

### Gestion des Messages Bluetooth
Ce code reçoit les messages Bluetooth, interprétés pour régler les paramètres de l'ESP32 (Wi-Fi, connexion au serveur MQTT, intervalle de temps, alerte).

## Code Android
![Image5](https://github.com/aberkanenazi/TPIOT/assets/35194684/f5dba2d1-5e86-41d0-818b-20fcdef44391)
![Image6](https://github.com/aberkanenazi/TPIOT/assets/35194684/bed26888-3d24-4454-b147-423309147f4c)

### Gestion du Bluetooth
Ce code gère les actions Bluetooth lors du lancement de l'application, y compris la découverte des périphériques et l'activation/désactivation du Bluetooth.

### Envoi de Messages Bluetooth
![Image7](https://github.com/aberkanenazi/TPIOT/assets/35194684/32e30410-ba01-46c3-82a7-481c88591853)

Gestion de l'envoi des messages Bluetooth via un socket initialisé entre l'ESP32 et le smartphone.

## Télémétrie et Visualisation
![Image8](https://github.com/aberkanenazi/TPIOT/assets/35194684/96c59ed7-2d02-4f24-99c3-929f1eda8e72)

### Télémétrie du Capteur
Données de température et d'humidité collectées sur 24h, affichées sous forme graphique sur le tableau de bord Node-RED.

### Diagramme Node-RED
![Image9](https://github.com/aberkanenazi/TPIOT/assets/35194684/76b096bc-3ab0-4ab6-9e6d-dc8ca39aa77b)

Le diagramme Node-RED présente les flux de données et les interactions entre les composants du projet.

### Affichage sur Tableau de Bord Node-RED
![Image10](https://github.com/aberkanenazi/TPIOT/assets/35194684/a28aeea6-ba2d-40de-aa63-26ec045813ff)

Visualisation des données sous forme de graphiques et contrôles de la LED.

### Affichage sur Samsung S20
Exemple d'affichage des données sur le téléphone Samsung S20.

## Limitations
Le SSL n'a pas pu être mis en place en raison de la faible capacité de stockage de l'ESP32 (erreur: out memory)
