# Försäkrings-API - Spring Boot Backend för Bolåneskydd

En Spring Boot-applikation som fungerar som backend för ett digitalt köpflöde av försäkringstypen Bolåneskydd/Betalskydd.

## Projektöversikt

Denna applikation hanterar:
- Skapande och hantering av försäkringsofferter
- Prissättning baserat på försäkrat belopp
- Teckning av försäkringar
- Statistik och konverteringsanalys
- GDPR-anpassad datahantering

## Funktionalitet

### 1. Skapa Offert
- **Endpoint**: `POST /api/offer`
- **Funktion**: Skapar en ny offert baserat på personnummer, bolånelista och månadsbelopp
- **Prissättning**: 3,8% av det totala försäkrade beloppet

### 2. Uppdatera Offert
- **Endpoint**: `PUT /api/offer/{id}`
- **Funktion**: Uppdaterar befintlig offert och beräknar ny premie

### 3. Acceptera Offert
- **Endpoint**: `POST /api/offer/{id}/accept`
- **Funktion**: Markerar offert som tecknad (försäkring köpt)

### 4. Statistik och Konvertering
- **Endpoint**: `GET /api/stats/conversion`
- **Funktion**: Visar antal skapade/accepterade offerter och konverteringsgrad

## Affärsregler

- **Giltighet**: Offerter är giltiga i 30 dagar
- **GDPR-anpassning**: Personlig data raderas automatiskt efter att giltigheten passerat
- **Konverteringsanalys**: Beräkning av hur många offerter som accepteras inom giltighetstiden

## Installation och Setup

### Förutsättningar
- Java 17 eller senare
- Maven 3.6+

### Steg för att köra applikationen

1. **Klona repositoriet**:
   ```bash
   git clone <https://github.com/alimohseni99/insurance-api.git>
   cd insurance-api
   ```

2. **Bygg projektet**:
   ```bash
   ./mvnw clean install
   ```

3. **Starta applikationen**:
   ```bash
   ./mvnw spring-boot:run
   ```

Applikationen startar på `http://localhost:8080`

## API-dokumentation

### Endpoints

| Metod | Endpoint | Beskrivning |
|-------|----------|-------------|
| POST | `/api/offer` | Skapa ny offert |
| PUT | `/api/offer/{id}` | Uppdatera befintlig offert |
| POST | `/api/offer/{id}/accept` | Acceptera offert |
| GET | `/api/stats/conversion` | Hämta konverteringsstatistik |

### Exempel på Request Body för POST /api/offer
```json
{
   "personalNumber": "19901010-1234",
   "loans": [
      5000, 10000, 25000
   ],
   "monthlyPayment": 950
}
```

## Teknisk Implementation

### Teknikstack
- **Spring Boot 3.5.3**
- **Spring Data JPA** för datahantering
- **H2 Database** (in-memory för utveckling)
- **Spring Web** för REST API
- **SpringDoc OpenAPI** för API-dokumentation
- **Java 17**

### Databasschema
Applikationen använder en in-memory H2-databas som automatiskt skapas vid uppstart.

### API-dokumentation (Swagger)
När applikationen körs kan du komma åt Swagger UI på:
`http://localhost:8080/swagger-ui.html`

### H2 Console
För att inspektera databasen:
`http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Användarnamn: `sa`
- Lösenord: (lämna tomt)

## Testning

Kör tester med:
```bash
./mvnw test
```

## Utvecklingsanteckningar

- Koden följer Spring Boot best practices
- Implementerar GDPR-krav genom automatisk datarensning
- Använder DTO:er för API-kommunikation
- Innehåller grundläggande felhantering

