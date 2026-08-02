# PromoGamer

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_4-6DB33F?style=flat&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=flat&logo=postgresql&logoColor=white)
![H2](https://img.shields.io/badge/H2-blue?style=flat)
![OpenFeign](https://img.shields.io/badge/OpenFeign-6DB33F?style=flat&logo=springboot&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-CC0200?style=flat&logo=flyway&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI-85EA2D?style=flat&logo=swagger&logoColor=white)
![JUnit5](https://img.shields.io/badge/JUnit5-25A162?style=flat&logo=junit5&logoColor=white)
![Status](https://img.shields.io/badge/status-em_desenvolvimento-yellow?style=flat)

API REST que caça promoções de jogos com boa nota na Steam e transforma isso em mensagens prontas para envio, automaticamente.

> Projeto em desenvolvimento. O fluxo principal (buscar → registrar → enriquecer → montar mensagem) já funciona de ponta a ponta e é coberto por testes.

## Sumário

- [O problema](#o-problema)
- [Como funciona](#como-funciona)
- [Exemplo de mensagem gerada](#exemplo-de-mensagem-gerada)
- [Stack](#stack)
- [Arquitetura](#arquitetura)
- [Estrutura](#estrutura)
- [Rodando](#rodando)
- [Profiles e variáveis de ambiente](#profiles-e-variáveis-de-ambiente)
- [Endpoints](#endpoints)
- [Ciclo de vida de uma deal](#ciclo-de-vida-de-uma-deal)
- [Tratamento de erros](#tratamento-de-erros)
- [Testes](#testes)
- [Roadmap](#roadmap)


## O problema

Ficar de olho em promoção de jogo bom é trabalho manual: abrir site, comparar preço, checar nota, escrever o texto pra divulgar. Se depender de garimpar isso à mão, a promoção boa já passou até você postar. O PromoGamer automatiza essa rotina inteira:

1. busca promoções com nota alta na Steam através da API pública da CheapShark;
2. filtra o que já foi visto antes, pra nunca repetir uma deal;
3. consulta a API oficial da Steam pra pegar preço atualizado, desconto e imagem;
4. monta uma mensagem já formatada (com emojis, preço convertido pra real e link da loja), pronta pra disparo em um grupo.

Tudo isso roda sozinho, sem precisar abrir navegador nenhum.


## Como funciona

```
                    ┌─────────────────────────┐
                    │   CheapShark API         │
                    │   (deals com desconto e  │
                    │   nota Steam ≥ 85%)      │
                    └────────────┬─────────────┘
                                 │
                                 ▼
                  filtra deals já existentes no banco
                                 │
                                 ▼
                  salva as novas deals (status PENDENTE)
                                 │
                                 ▼
                    ┌─────────────────────────┐
                    │      Steam API           │
                    │  tenta como jogo (app);  │
                    │  se falhar, tenta como   │
                    │  pacote (sub/package)    │
                    └────────────┬─────────────┘
                                 │
                                 ▼
              monta a mensagem (preço, desconto, imagem, link)
                                 │
                                 ▼
              persiste a Message (status PENDENTE de envio)
                                 │
                                 ▼
                   deal passa de PENDENTE → PROCESSADO
```

Duas vezes por dia (**8h e 18h, horário de Brasília**), um job agendado (`DealScheduledService`) dispara a busca de novas promoções automaticamente para a loja Steam, sem precisar de nenhuma chamada manual.


## Exemplo de mensagem gerada

A partir de uma deal processada, o `MessageTemplateBuilder` monta uma legenda pronta pra envio, com preço já convertido para real:

```
🎮 Bora jogar gastando pouco! 🕹️

*Elden Ring*

🔥 De R$ 249,90 por R$ 99,96 (60% OFF)

🛒 Ver oferta na Steam:
https://store.steampowered.com/app/1245620
```

Se a promoção for de um pacote (bundle) em vez de um jogo avulso, a mensagem é montada num formato equivalente, sinalizando que se trata de um pacote especial.


## Stack

- **Java 21** + **Spring Boot 4**
- **Spring Data JPA** + Hibernate
- **Flyway** — versionamento e controle do schema do banco
- **PostgreSQL** (produção) / **H2** (testes e dev local)
- **OpenFeign** — clients declarativos para CheapShark API e Steam API
- **MapStruct** — mapeamento DTO ↔ entidade
- **Bean Validation** (Jakarta)
- **SpringDoc OpenAPI + Swagger UI** — documentação interativa dos endpoints
- **Spring Scheduling** — job automático de busca de deals (cron)
- **JUnit 5 + Mockito** — testes unitários de serviço
- **Lombok**
- **Maven**


## Arquitetura

O fluxo é dividido em três responsabilidades bem separadas, cada uma isolada em seu próprio serviço:

| Camada | Responsável | O que faz |
|---|---|---|
| Origem das deals | `CheapSharkApiService` / `CheapSharkDealService` | consulta a CheapShark, filtra deals sem `steamAppId` |
| Registro | `DealService` / `DealScheduledService` | evita duplicatas, persiste novas deals, roda no cron |
| Enriquecimento | `SteamApiService` / `SteamService` | busca detalhes reais na Steam (preço, imagem, nome) |
| Mensagem | `MessageTemplateBuilder` / `MessageService` | monta o texto final e persiste a mensagem |

A integração com APIs externas é feita via **Feign**, o que mantém os clients declarativos e testáveis, sem código de HTTP manual espalhado pelos serviços.

Como nem todo `steamAppId` retornado pela CheapShark corresponde a um jogo (às vezes é um pacote/bundle), o `SteamService` tenta resolver primeiro como aplicativo e, se a Steam não reconhecer, tenta novamente como pacote antes de desistir da deal.


## Estrutura

```
src/main/java/com/PedroNunesDev/PromoGamer/
├── controller/       # endpoints REST
│   ├── DealController
│   ├── CheapSharkController
│   └── MessageController
├── enums/            # DealEnumStatus, DealSourceType, MessageStatus
├── service/          # lógica de negócio
│   ├── CheapSharkApiService      # Feign client da CheapShark
│   ├── CheapSharkDealService     # busca e filtra deals na CheapShark
│   ├── DealService                # registra novas deals no banco
│   ├── DealScheduledService       # job agendado (8h/18h)
│   ├── SteamApiService            # Feign client da Steam
│   ├── SteamService                # resolve app/pacote e monta a Message
│   ├── MessageTemplateBuilder     # monta o texto da mensagem
│   └── MessageService              # persiste a mensagem pronta pra envio
├── repository/       # Spring Data JPA
├── model/            # entidades JPA (Deal, Message)
├── dto/              # DTOs de request/response e integrações externas
├── mapper/           # MapStruct
└── exception/        # tratamento global de erros

src/main/resources/
├── application.properties
├── application-test.properties
├── application-prod.properties
└── db/migration/     # scripts Flyway (V1...)

src/test/java/com/PedroNunesDev/PromoGamer/
└── service/          # testes unitários (JUnit 5 + Mockito)
```


## Rodando

```bash
git clone <url-do-repositorio>
cd PromoGamer
./mvnw spring-boot:run
```

Por padrão a aplicação sobe com o profile `test`, usando banco H2 em memória.

Antes de rodar, defina a variável de ambiente do número do grupo (usada na montagem da mensagem):

```properties
NUMBER_GROUP=<numero-do-grupo>
```

API em `http://localhost:8080` · Swagger em `http://localhost:8080/swagger-ui.html`


## Profiles e variáveis de ambiente

| Profile | Banco | Uso |
|--------|-------|-----|
| `test` (padrão) | H2 em memória | Desenvolvimento local |
| `prod` | PostgreSQL | Produção |

Variáveis usadas pela aplicação:

```properties
# obrigatória em qualquer profile — número do grupo pra onde a mensagem é montada
NUMBER_GROUP=<numero-do-grupo>

# opcional — usado no User-Agent das chamadas à CheapShark (tem valor padrão)
MY_EMAIL=seu_email@gmail.com
```

Para rodar com o profile `prod`, defina também as credenciais de conexão com o PostgreSQL:

```properties
spring.profiles.active=prod
spring.datasource.url=jdbc:postgresql://<host>:<porta>/<database>
spring.datasource.username=<usuario>
spring.datasource.password=<senha>
```


## Endpoints

| Método | Rota | O que faz |
|--------|------|-----------|
| `GET` | `/api/deals?storeId=&pageNumber=` | busca promoções direto na CheapShark, sem persistir |
| `POST` | `/deals?storeId=` | registra no banco as novas promoções encontradas para uma loja |
| `GET` | `/deals/status?statusType=` | lista promoções filtradas por status (`PENDENTE`, `PROCESSADO`, `CONCLUIDO`, `IGNORADO`) |
| `POST` | `/message/deals` | pega a próxima deal `PENDENTE`, resolve os detalhes na Steam e monta/salva a mensagem |

Documentação completa e interativa no Swagger depois de subir a API.


## Ciclo de vida de uma deal

```
PENDENTE ──► PROCESSADO ──► CONCLUIDO
                  │
                  └──► IGNORADO
```

- **PENDENTE** — deal recém-registrada, ainda sem mensagem montada
- **PROCESSADO** — mensagem já foi construída a partir dessa deal
- **CONCLUIDO** — fluxo finalizado (ex: mensagem enviada)
- **IGNORADO** — deal descartada (ex: `steamAppId` inválido tanto como app quanto como pacote)

A `Message`, por sua vez, tem seu próprio status (`MessageStatus`): `PENDENTE` → `ENVIADA` ou `FALHA`, controlado pelos métodos `markAsSent()` e `markAsFailed()` da entidade.


## Tratamento de erros

Toda falha nas integrações externas é capturada centralmente pelo `GlobalExceptionHandler` e traduzida em uma resposta padronizada (`ErrorResponse` com timestamp, status, mensagem e path):

| Cenário | Status retornado |
|---|---|
| Recurso não encontrado na CheapShark | `404 Not Found` |
| Rate limit da CheapShark atingido | `429 Too Many Requests` |
| Timeout na chamada externa | `504 Gateway Timeout` |
| Outro erro HTTP não mapeado do Feign | `502 Bad Gateway` |
| Argumento inválido (ex: status inexistente) | `400 Bad Request` |
| Erro inesperado | `500 Internal Server Error` |


## Testes

Os serviços centrais têm cobertura de testes unitários com **JUnit 5 + Mockito**, incluindo casos como:

- busca de deals retornando resultados e retornando lista vazia;
- registro de novas deals evitando duplicatas já existentes no banco;
- paginação automática quando a página atual só tem deals repetidas;
- interrupção da busca após um limite de páginas sem novas deals.

```bash
./mvnw test
```

## Roadmap

- [ ] Integração de envio real da mensagem 
- [ ] Endpoint para consultar mensagens por status
- [ ] Retry automático para deals que falharam na resolução Steam
- [ ] Deploy containerizado (Docker)

---

## Licença

Projeto privado — uso pessoal.

---

<div align="center">

### ⭐ Se este projeto foi útil para você, considere dar uma estrela!

**Desenvolvido com ☕ e ❤️ por Pedro Nunes**

</div>