# Wisepenny API Specification

Reference for the Wisepenny backend (`:server` module, Ktor + Netty). This document
describes the routes actually implemented in the repository. The API is **simulated**:
it serves demo data seeded locally and establishes no connection to any real bank.

- Base URL (local): `http://localhost:8080`
- Listening port: `8080` by default, overridable with the `PORT` environment variable
- Content type: `application/json` for every request and response
- The server tolerates unknown JSON fields sent by clients (`ignoreUnknownKeys = true`)

---

## Money representation

Monetary amounts always cross the wire as **strings** (for example `"1250.75"`), never
as JSON numbers or floating point. This preserves exact cents end to end and mirrors the
`DECIMAL(12,2)` storage on the server. Clients must parse and format these strings as
decimal values, not doubles.

Transaction amounts are signed: **negative for an expense, positive for income**.

---

## Authentication

Authentication uses a Bearer JWT.

1. Call `POST /auth/token` with valid credentials to obtain an access token.
2. Send that token on every protected route in the `Authorization` header:
   `Authorization: Bearer <accessToken>`

Token properties (signed with HMAC-256):

| Property        | Value                                   |
| --------------- | --------------------------------------- |
| Issuer          | `wisepenny`                             |
| Audience        | `wisepenny-app`                         |
| Subject (`sub`) | the user id (UUID)                      |
| Custom claim    | `email`                                 |
| Lifetime        | `3600` seconds (1 hour)                 |

A request to a protected route without a valid token (bad signature, wrong issuer or
audience, expired, or missing subject) is rejected with `401`.

### Demo credentials (dev environment only)

When the server runs with `APP_ENV=dev` (the default), a demo dataset is seeded on first
start into an empty database:

- Email: `demo@wisepenny.fr`
- Password: `demo1234`

The seeder is disabled when `APP_ENV=prod`.

---

## Error envelope

Every error response shares the same shape:

```json
{
  "code": "VALIDATION",
  "message": "amount must be greater than 0"
}
```

| HTTP status | `code`         | Meaning                                                                 |
| ----------- | -------------- | ----------------------------------------------------------------------- |
| 400         | `BAD_REQUEST`  | Malformed request (for example a missing or unparseable JSON body)      |
| 401         | `UNAUTHORIZED` | Missing or invalid credentials / token                                  |
| 404         | `NOT_FOUND`    | Resource does not exist, or the caller is not allowed to know it exists |
| 422         | `VALIDATION`   | Request is well-formed but fails a validation or business rule          |
| 500         | `INTERNAL`     | Unexpected server error                                                 |

For ownership violations the server deliberately returns `404` rather than `403`, so it
never reveals whether an id belongs to another user (OWASP A01 — Broken Access Control).

---

## Endpoints

### `GET /health`

Liveness probe. Public, no authentication.

Response `200`:

```json
{ "status": "UP" }
```

---

### `POST /auth/token`

Exchange credentials for an access token. Public.

Request body:

```json
{ "email": "demo@wisepenny.fr", "password": "demo1234" }
```

Response `200`:

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiI...",
  "expiresIn": 3600
}
```

Errors:

- `401 UNAUTHORIZED` — unknown email or wrong password. The message is identical in both
  cases to avoid user enumeration.

Example:

```bash
curl -s -X POST http://localhost:8080/auth/token \
  -H "Content-Type: application/json" \
  -d '{"email":"demo@wisepenny.fr","password":"demo1234"}'
```

---

### `GET /accounts`

List the authenticated user's accounts. Requires authentication.

Response `200` — array of accounts:

```json
[
  {
    "id": "0f4c...",
    "label": "Compte courant",
    "ibanMasked": "FR76 •••• •••• •••• 4821",
    "balance": "1250.75",
    "currency": "EUR"
  }
]
```

Only accounts belonging to the caller are returned.

---

### `GET /accounts/{id}/transactions`

Paginated transactions for one of the caller's accounts. Requires authentication.

Path parameter:

- `id` — the account UUID.

Query parameters:

| Name   | Type | Default | Constraint         |
| ------ | ---- | ------- | ------------------ |
| `page` | int  | `0`     | `>= 0`             |
| `size` | int  | `20`    | between `1` and `100` |

Transactions are ordered by date descending (most recent first).

Response `200`:

```json
{
  "items": [
    {
      "id": "3a91...",
      "date": "2026-07-15",
      "label": "Lidl",
      "amount": "-23.40",
      "category": "ALIMENTATION"
    }
  ],
  "page": 0,
  "total": 137
}
```

`total` is the total number of transactions on the account (across all pages).

`category` is one of: `ALIMENTATION`, `TRANSPORT`, `LOISIRS`, `ABONNEMENTS`,
`LOGEMENT`, `SANTE`, `AUTRE`.

Errors:

- `404 NOT_FOUND` — the account does not exist or does not belong to the caller.
- `422 VALIDATION` — `id` is not a valid UUID, or `page` / `size` is out of range.

---

### `GET /balances`

Aggregated balances for the caller. Requires authentication.

Response `200`:

```json
{
  "total": "1571.15",
  "byAccount": [
    { "accountId": "0f4c...", "balance": "1250.75" },
    { "accountId": "9ab2...", "balance": "320.40" }
  ]
}
```

`total` is the sum of the caller's account balances. Only the caller's accounts are
included.

---

### `POST /savings/transfer`

Move money from one of the caller's accounts into one of the caller's savings goals.
Requires authentication.

Request body:

```json
{
  "fromAccountId": "0f4c...",
  "amount": "50.00",
  "goalId": "7d10..."
}
```

Behaviour:

- Debits `amount` from the source account balance.
- Credits `amount` to the goal's current amount.
- Records the movement in the `transfers` table.

Response `200`:

```json
{
  "transferId": "c2f8...",
  "newBalance": "1200.75"
}
```

`newBalance` is the source account balance after the transfer.

Errors:

- `404 NOT_FOUND` — the source account or the goal does not exist or does not belong to
  the caller.
- `422 VALIDATION` — `amount` is not a valid number, `amount` is not greater than 0,
  `fromAccountId` / `goalId` is not a valid UUID, or the account has insufficient balance.

---

## Authentication and authorization summary

| Route                               | Auth required | Scoped to caller |
| ----------------------------------- | ------------- | ---------------- |
| `GET /health`                       | No            | —                |
| `POST /auth/token`                  | No            | —                |
| `GET /accounts`                     | Yes           | Yes              |
| `GET /accounts/{id}/transactions`   | Yes           | Yes              |
| `GET /balances`                     | Yes           | Yes              |
| `POST /savings/transfer`            | Yes           | Yes              |
