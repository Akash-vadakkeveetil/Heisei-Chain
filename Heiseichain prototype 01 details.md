# Planning

# Heisei-Chain Project Documentation Plan

## Overview

Create comprehensive documentation explaining the Heisei-Chain blockchain project - a Spring Boot-based donation and commodity tracking system. The documentation will explain what the project does, how it works architecturally, and how it's configured as a REST API layer over the original Java blockchain implementation.

## Target Audience

Developers or stakeholders who want to understand the complete project architecture, features, and the layering approach used to convert a standalone Java blockchain into a Spring Boot web service.

## Documentation Format

Comprehensive markdown document with embedded diagrams, combining:

- Visual architecture diagrams
- Written explanations of each component
- Flow diagrams showing how features work
- Code references with file paths and line numbers

## Current State (From Research)

Research has identified:

- Project is described as "Conversion of java blockchain to spring boot" (pom.xml:15)
- Core blockchain models (Block, Transaction, Wallet, UTXO) represent original implementation
- Spring Boot layer adds REST API, service layer, security, and web framework
- Use case: Donation tracking with 3 roles (donors, volunteers, camp coordinators)
- Technology: Spring Boot 3.4.1, Java 21, BouncyCastle cryptography, UTXO-based blockchain
- In-memory storage with no external database

## Desired End State

A complete markdown documentation file that enables readers to:

1. Understand what Heisei-Chain does (features, use cases, user roles)
2. Understand the complete architecture (all components and relationships)
3. Understand how the Spring Boot layer wraps the original blockchain
4. Understand the data flow for key operations (registration, donations, transactions)
5. Understand the cryptographic security mechanisms
6. Know which files contain which functionality (with references)

## Documentation Depth

**Technical Detail Level:** Deep technical documentation

- Include algorithm explanations with code snippets
- Explain UTXO model with implementation details
- Show cryptographic operations (ECDSA signatures, SHA-256 hashing)
- Reference actual code from the repository
- Explain transaction processing logic step-by-step

---

## Documentation Structure

### Section 1: Project Overview

**Purpose:** High-level introduction to what Heisei-Chain is

**Content to include:**

- Project description: Donation and commodity tracking blockchain
- Use case: Relief/humanitarian aid distribution tracking
- Three user roles: Donors, Volunteers, Camp Coordinators
- Key feature: Tracks which donor contributed to each commodity through UTXO chain
- Technology stack: Spring Boot 3.4.1, Java 21, Maven, BouncyCastle cryptography
- Deployment: Dockerized application on port 8080

**Reference files:**

- `pom.xml:15` - Project description
- `README.md` (if exists)
- `application.properties` - Configuration

### Section 2: The Layering Architecture

**Purpose:** Explain how Spring Boot wraps the original Java blockchain

**Content to include:**

- Visual diagram showing two layers (Spring Boot layer + Original blockchain core)
- Explanation of "Conversion of java blockchain to spring boot" concept
- What the original project contained (core blockchain models)
- What Spring Boot added (REST API, security, web framework)

**Diagram specification:**

- Create architecture diagram with two distinct layers:
- Spring Boot Layer (blue): Controllers, Services, Configuration
- Original Core (orange): Block, Transaction, Wallet, UTXO, Cryptography utilities
- Show arrows indicating how Spring Boot components call original blockchain code

**Key integration points:**

1. `AppConfig.java` creates Blockchain as Spring bean
2. `BlockchainService` (Spring) calls `Blockchain` methods (original)
3. Controllers expose original blockchain operations via REST endpoints
4. Static UTXO storage accessed by both layers

**Reference files:**

- `config/AppConfig.java` - Spring bean configuration
- `service/BlockchainService.java` - Service layer integration
- `controller/BlockchainController.java` - REST API layer
- `model/Blockchain.java` - Original blockchain core

### Section 3: REST API Endpoints

**Purpose:** Document all available API endpoints and their usage

**Content to include:**
For each endpoint, specify:

- HTTP method and path
- Request format (parameters, body structure)
- Response format (success and error cases)
- Security mechanisms (RSA encryption/signing)
- Example usage

**Endpoints to document (from BlockchainController.java):**

1. **GET /api/blockchain/display**

- Purpose: Retrieve entire blockchain data
- Response: HTML representation of blockchain
- File reference: `BlockchainController.java:50-54`

2. **GET /api/blockchain/validate**

- Purpose: Validate blockchain integrity
- Response: Boolean indicating if chain is valid
- File reference: `BlockchainController.java:56-60`

3. **POST /api/blockchain/register**

- Purpose: Register new user (donor/volunteer/camp coordinator)
- Request body: User registration data (encrypted)
- Creates wallet with ECDSA keys
- Adds RegistrationTransaction to blockchain
- File reference: `BlockchainController.java:62-76`

4. **POST /api/blockchain/creation**

- Purpose: Create transaction request (phase 1 of 2-phase transaction)
- Stores pending transaction with 5:30 hour timeout
- Returns transaction ID
- File reference: `BlockchainController.java:78-90`

5. **POST /api/blockchain/confirm**

- Purpose: Confirm and process transaction (phase 2)
- Retrieves pending transaction
- Processes transaction with signature verification
- Adds to blockchain
- File reference: `BlockchainController.java:92-106`

6. **POST /api/blockchain/generateReport**

- Purpose: Generate CSV report with date range filtering
- Request: Start and end dates
- Response: CSV file download
- File reference: `BlockchainController.java:108-115`

7. **GET /api/blockchain/displayWallet**

- Purpose: Display wallet holdings (volunteer inventory)
- Response: HTML view of volunteer commodities
- File reference: `BlockchainController.java:117-122`

8. **GET /api/blockchain/donationPercentage**

- Purpose: Calculate distribution statistics
- Response: Percentage to volunteers vs camp coordinators
- File reference: `BlockchainController.java:124-128`

**Security layer detail:**

- RSA encryption: `encryptData()` method at line 130-137
- RSA decryption: `decryptData()` method at line 139-148
- Digital signature verification: `verifySignature()` method at line 150-164
- Hardcoded keys for Java backend and webapp communication

**Reference files:**

- `controller/BlockchainController.java` - All endpoints

### Section 4: Core Blockchain Components

**Purpose:** Deep technical explanation of blockchain internals

**Content to include:**

#### 4.1 Block Structure (`model/Block.java`)

**Detailed explanation:**

- `hash`: SHA-256 hash serving as block identifier
- `previousHash`: Links to previous block (creates chain)
- `transactions`: List of Transaction objects in this block
- `timestamp`: Creation timestamp with +5:30 timezone offset

**Hash calculation algorithm (line 45-50):**

```
hash = SHA256(previousHash + timestamp + concatenated_transaction_IDs)
```

Explain:

- Uses StringUtil.applySha256() method
- Combines previousHash, timestamp, and all transaction IDs
- Creates immutable block identifier

**Code snippet to include (from Block.java:45-50):**
Show actual calculateHash() implementation

**Reference file:** `model/Block.java`

#### 4.2 Blockchain Management (`model/Blockchain.java`)

**Detailed explanation:**

**Initialization:**

- Genesis block created automatically on instantiation (line 24-26)
- Chain stored as `List<Block>` in memory
- No external database persistence

**Key operations:**

1. **addTransaction() (line 32-39)**

- Accepts Transaction object
- Creates new Block containing the transaction
- Calls addBlock() to append to chain
- Explain transaction batching vs single transaction per block

2. **addBlock() (line 41-45)**

- Links new block via previousHash
- Sets previousHash to last block's hash
- Adds to chain list
- Show code snippet

3. **isChainValid() (line 47-62)**

- Validates entire chain integrity
- Checks each block's hash matches calculated hash
- Verifies previousHash links are correct
- Validates all transaction signatures
- Returns boolean
- Show validation algorithm with code

4. **processUTXOs integration:**

- Calls HeiseiChain.processUTXOs() after adding transaction
- Updates global UTXO set
- Explain relationship

**HTML/CSV generation:**

- `displayHTML()` (line 68-102): Generates web view
- `generateCSVReport()` (line 113-194): Creates downloadable reports with date filtering

**Reference file:** `model/Blockchain.java`

#### 4.3 Transaction Model (`model/Transaction.java`)

**Purpose:** Most complex component - deep technical detail required

**Transaction structure:**

- `transactionId`: Unique identifier (SHA-256 hash)
- `sender`: PublicKey of sender
- `reciepient`: PublicKey of recipient [note typo in original]
- `value`: Amount being transferred
- `signature`: ECDSA digital signature
- `inputs`: List<TransactionInput> (UTXOs being spent)
- `outputs`: List<TransactionOutput> (new UTXOs created)
- `metadata`: Additional information
- `timeStamp`: Transaction timestamp

**ECDSA Signature Generation (line 77-85):**
Detailed explanation:

- Uses sender's private key
- Signs data: sender + recipient + value + metadata
- Algorithm: ECDSA with BouncyCastle provider
- Signature ensures transaction authenticity

**Code to include:** Show generateSignature() implementation

**ECDSA Signature Verification (line 88-96):**
Detailed explanation:

- Uses sender's public key
- Verifies signature matches transaction data
- Prevents transaction tampering
- Returns boolean

**Code to include:** Show verifySignature() implementation

**Transaction Processing Algorithm (line 99-179) - CRITICAL SECTION:**

This is the core of the blockchain. Detailed step-by-step explanation needed:

**Step 1: Validate sender (line 106-109)**

- Check sender owns the transaction
- Verify signature
- Return false if invalid

**Step 2: Check minimum value (line 112-115)**

- Minimum transaction: 0.1f
- Reject if below minimum

**Step 3: Gather and lock UTXOs (line 118-146)**

- Retrieve sender's UTXOs filtered by commodity type
- Lock UTXOs in HeiseiChain.pendingUTXOs (prevents double-spending)
- Accumulate total value
- Track donor contributions through UTXO chain
- If insufficient funds: Release locks and return false

**Algorithm detail:**

```
For each UTXO:
  - Lock UTXO (add to pendingUTXOs)
  - Add to transaction inputs
  - Accumulate value
  - Preserve donor tracking map
  - Stop when total >= required amount
```

**Step 4: Create outputs (line 149-163)**

- Create recipient output (transaction amount)
- Preserve donor attribution in output
- Create change output if needed (return excess to sender)

**Step 5: Process in blockchain (line 166-177)**

- Add to blockchain via blockchain.addTransaction()
- Update global UTXOs (remove spent, add new)
- Release UTXO locks
- Generate transaction ID

**Edge cases explained:**

- Exact amount vs change scenarios
- Donor tracking preservation
- UTXO locking prevents concurrent spending
- Failure rollback (release locks)

**Code snippets to include:** Show key sections from processTransaction()

**Reference file:** `model/Transaction.java`

#### 4.4 UTXO Management (`model/HeiseiChain.java`)

**Purpose:** Global UTXO storage and double-spend prevention

**Static storage:**

- `UTXOs`: HashMap<String, TransactionOutput> - All unspent outputs
- `pendingUTXOs`: Set<TransactionOutput> - Locked outputs (transaction in progress)
- `minimumTransaction`: 0.1f constant

**Key methods:**

1. **processUTXOs() (line 19-31)**

- Removes spent inputs from UTXOs map
- Adds new outputs to UTXOs map
- Called after each transaction processed
- Show algorithm with code

2. **getUTXO(String id)**

- Retrieves specific UTXO by ID
- Used for transaction validation

**Double-spend prevention mechanism:**

- pendingUTXOs Set locks UTXOs during transaction processing
- Prevents concurrent transactions from using same UTXO
- Locks released after transaction completes or fails
- Explain with example scenario

**Reference file:** `model/HeiseiChain.java`

#### 4.5 Wallet (`model/Wallet.java`)

**Purpose:** Key management and UTXO querying

**Wallet creation:**

- Generates ECDSA key pair using BouncyCastle
- Key size: 256-bit elliptic curve
- Uses "BC" (BouncyCastle) security provider
- Stores publicKey, privateKey, role

**Role types:**

- "donor": Can create genesis donations (no input UTXOs required)
- "volunteer": Receives and redistributes commodities
- "campcoordinator": Receives commodities for camps

**Key methods:**

1. **getUTXOs() (line 44-58)**

- Retrieves all UTXOs belonging to this wallet
- Filters by commodity type
- Searches global HeiseiChain.UTXOs map
- Returns ArrayList of available UTXOs

2. **getCommodityQuantities() (line 60-70)**

- Aggregates holdings by commodity type
- Returns HashMap<String, Float> (commodity → quantity)
- Used for inventory display

**Code to include:** Show key generation and UTXO retrieval logic

**Reference file:** `model/Wallet.java`

#### 4.6 TransactionOutput (`model/TransactionOutput.java`)

**Purpose:** UTXO structure with donor tracking

**Structure:**

- `id`: SHA-256 hash (UTXO identifier)
- `recipient`: PublicKey of owner
- `value`: Commodity quantity
- `parentTransactionId`: Links to creating transaction
- `commodity`: Type (e.g., "rice", "medicine")
- `donor`: HashMap<String, Float> - Tracks original donor contributions
- `date`: Timestamp

**Donor tracking feature:**

- Each output maintains map of donor → contributed amount
- Preserves through transaction chain
- Enables full donation attribution
- Example: If donor A gave 10kg rice, split into 2 outputs of 5kg each, both track "A: 5kg"

**ID calculation:**
Uses SHA-256 of: recipient + value + parentTransactionId

**Reference file:** `model/TransactionOutput.java`

#### 4.7 Cryptographic Utilities (`util/StringUtil.java`)

**Purpose:** Cryptographic functions used throughout system

**Functions to document:**

1. **applySha256(String input) (line 14-27)**

- SHA-256 hashing algorithm
- Returns hex-encoded hash string
- Used for: block hashes, transaction IDs, UTXO IDs
- Show implementation code

2. **applyECDSASig(PrivateKey privateKey, String input) (line 30-42)**

- ECDSA signature generation
- Uses BouncyCastle provider
- Algorithm: "ECDSA" with "BC" provider
- Returns byte array signature
- Show implementation code

3. **verifyECDSASig(PublicKey publicKey, String data, byte[] signature) (line 45-57)**

- ECDSA signature verification
- Returns boolean
- Show implementation code

4. **getStringFromKey(Key key) (line 60-62)**

- Base64 encodes public/private keys
- Used for key serialization

5. **getMerkleRoot(ArrayList<Transaction> transactions) (line 78-95)**

- Calculates Merkle root from transaction list
- Recursive binary tree hashing
- Used in block hash calculation
- Explain Merkle tree algorithm
- Show implementation code

**Reference file:** `util/StringUtil.java`

### Section 5: Service Layer Integration

**Purpose:** Explain how BlockchainService bridges Spring Boot and original blockchain

**Content to include:**

#### BlockchainService Overview (`service/BlockchainService.java`)

**Architecture role:**

- Annotated with @Service for Spring dependency injection
- Acts as intermediary between controller and blockchain core
- Manages in-memory wallet database
- Handles pending transactions with timeout
- Provides business logic layer

**Key components:**

1. **Wallet Database Management**

- `walletDatabase`: HashMap<String, Wallet>
- Stores all user wallets in memory
- Key: User identifier, Value: Wallet object
- No persistence - resets on restart

2. **Pending Transaction Management**

- `pendingTransactions`: HashMap storing incomplete transactions
- Timeout: 5 hours 30 minutes (5:30)
- Supports two-phase transaction creation
- Structure: Transaction data stored temporarily until confirmation

**Key methods to document:**

1. **createWallet(String role)**

- Creates new Wallet with specified role
- Generates ECDSA key pair
- Returns Wallet object
- Used during user registration

2. **registerUser(String userId, String role)**

- Creates wallet for new user
- Stores in walletDatabase
- Creates RegistrationTransaction
- Adds transaction to blockchain
- Returns success/failure

3. **createTransactionRequest(...)**

- Phase 1 of transaction creation
- Validates sender has sufficient UTXOs
- Creates pending transaction entry
- Sets timeout (5:30 hours from creation)
- Returns transaction ID for later confirmation

4. **retreiveTransactionInputs(String transactionId)**

- Phase 2 of transaction creation
- Retrieves pending transaction by ID
- Validates timeout hasn't expired
- Removes from pending storage
- Returns transaction data or null if expired/not found

5. **displayWallets()**

- Generates HTML view of all volunteer wallets
- Shows commodity holdings per volunteer
- Calls wallet.getCommodityQuantities() for each volunteer
- Returns formatted HTML string

6. **displayPercentage()**

- Calculates distribution statistics
- Percentage to volunteers vs camp coordinators
- Iterates through all UTXOs
- Groups by role
- Returns formatted percentages

**Integration with blockchain core:**

- Injects Blockchain bean (created by AppConfig)
- Calls blockchain.addTransaction() for new transactions
- Queries HeiseiChain.UTXOs for UTXO data
- Creates Transaction objects and passes to blockchain

**Reference file:** `service/BlockchainService.java`

### Section 6: Complete User Flows

**Purpose:** Show end-to-end scenarios with sequence diagrams

**Content to include:**

#### Flow 1: User Registration

**Sequence diagram specification:**

```
User → Controller → Service → Blockchain Core
1. POST /api/blockchain/register with user data (encrypted)
2. Controller decrypts data, calls service.registerUser()
3. Service creates Wallet with ECDSA keys
4. Service creates RegistrationTransaction
5. Blockchain adds transaction and creates block
6. Response returns success with public key
```

**Detailed steps:**

- User submits registration form (donor/volunteer/camp coordinator role)
- Data encrypted with RSA before transmission
- Backend decrypts using private key
- New wallet created with ECDSA key pair (256-bit)
- Wallet stored in service's walletDatabase
- RegistrationTransaction added to blockchain
- User receives their public key (wallet address)

**Files involved:**

- `BlockchainController.java:62-76` - Endpoint
- `BlockchainService.java` - registerUser() method
- `Wallet.java` - Key generation
- `Blockchain.java` - Add transaction

#### Flow 2: Donor Creates Donation (Genesis Transaction)

**Sequence diagram specification:**

```
Donor → API → Service → Transaction Processing → Blockchain
1. Donor creates transaction (no input UTXOs needed)
2. Transaction processed without input validation
3. Creates output UTXO with donor attribution
4. UTXO added to global HeiseiChain.UTXOs
5. Transaction added to blockchain
```

**Special characteristics:**

- Donors don't need existing UTXOs (genesis transactions)
- Donor's public key recorded in output.donor map
- Creates new commodities in the system
- Sets commodity type (food, medicine, etc.)

**Code detail:**

- Transaction.processTransaction() checks if sender is donor
- Skips input UTXO gathering for donors
- Directly creates output UTXO
- Donor attribution: output.donor.put(donorPublicKey, value)

#### Flow 3: Volunteer Redistributes Commodities (Standard Transaction)

**Sequence diagram specification:**

```
Volunteer → API (Phase 1) → Service → Pending Storage
↓
Volunteer → API (Phase 2) → Service → Transaction Processing → Blockchain
1. POST /api/blockchain/creation (request transaction)
2. Service validates volunteer has sufficient UTXOs
3. Creates pending transaction with 5:30 timeout
4. Returns transaction ID
5. POST /api/blockchain/confirm (confirm transaction)
6. Retrieves pending transaction
7. Processes transaction with UTXO locking
8. Creates outputs (recipient + change)
9. Updates global UTXOs
10. Adds to blockchain
```

**Detailed transaction processing:**

**Phase 1 (Creation):**

- Volunteer specifies: recipient, commodity type, quantity
- System queries volunteer's UTXOs filtered by commodity
- Validates sufficient balance
- Stores pending transaction with timeout
- Returns transaction ID

**Phase 2 (Confirmation):**

- Volunteer submits transaction ID with signature
- System retrieves pending transaction
- Validates timeout hasn't expired
- Processes transaction:
- Locks required UTXOs (add to pendingUTXOs)
- Validates signature
- Creates recipient output
- Creates change output if needed
- Preserves donor attribution through outputs
- Updates global UTXO set
- Releases locks
- Adds to blockchain

**UTXO transformation example:**

```
Before:
Volunteer has: UTXO1 (10kg rice, donor: {Alice: 10})

Transaction: Send 6kg to Camp Coordinator

After:
Camp Coordinator: UTXO2 (6kg rice, donor: {Alice: 6})
Volunteer: UTXO3 (4kg rice, donor: {Alice: 4})
UTXO1 removed from global UTXOs
```

**Files involved:**

- `BlockchainController.java:78-106` - Both endpoints
- `BlockchainService.java` - createTransactionRequest(), retreiveTransactionInputs()
- `Transaction.java:99-179` - processTransaction()
- `HeiseiChain.java` - UTXO management

#### Flow 4: Blockchain Validation

**Sequence diagram specification:**

```
User → API → Blockchain → Validation Loop
1. GET /api/blockchain/validate
2. Blockchain.isChainValid() called
3. For each block:
   - Verify hash matches calculated hash
   - Verify previousHash links correctly
   - Verify all transaction signatures
4. Return boolean result
```

**Validation checks:**

- Hash integrity: Each block's hash must equal calculateHash()
- Chain continuity: Each block's previousHash must match previous block's hash
- Transaction validity: All signatures must verify
- Genesis block: First block has previousHash = "0"

**Reference:** `Blockchain.java:47-62`

#### Flow 5: Report Generation

**Sequence diagram specification:**

```
User → API → Blockchain → CSV Generation → Download
1. POST /api/blockchain/generateReport with date range
2. Blockchain.generateCSVReport(startDate, endDate)
3. Iterates through all blocks
4. Filters transactions by date range
5. Generates CSV with transaction details
6. Returns CSV file for download
```

**CSV columns:**

- Transaction ID
- Timestamp
- Sender public key
- Recipient public key
- Commodity type
- Quantity
- Donor attribution

**Reference:** `Blockchain.java:113-194`

### Section 7: Configuration and Deployment

**Purpose:** Document deployment setup and configuration

**Content to include:**

#### Application Configuration (`application.properties`)

**Settings:**

- `spring.application.name=HeiseiChainSpringBoot`
- `server.port=8080` (or from PORT environment variable)
- Management endpoints exposed for monitoring
- No database configuration (in-memory only)

**Environment variables:**

- `PORT`: Override server port (default 8080)
- No secret keys in environment (hardcoded - security concern)

#### Docker Deployment (`Dockerfile`)

**Multi-stage build:**

**Stage 1: Build**

- Base image: maven:3.9.6-eclipse-temurin-21
- Copies pom.xml and source code
- Runs `mvn clean package`
- Produces JAR file

**Stage 2: Runtime**

- Base image: eclipse-temurin:21-jdk-alpine
- Copies JAR from build stage
- Exposes port 8080
- Command: `java -jar HeiseiChain-0.0.1-SNAPSHOT.jar`

**Build command:**

```bash
docker build -t heisei-chain .
```

**Run command:**

```bash
docker run -p 8080:8080 heisei-chain
```

**Reference file:** `Dockerfile`

#### Maven Configuration (`pom.xml`)

**Key dependencies:**

- Spring Boot Starter Web (REST API)
- Spring Boot Starter Thymeleaf (HTML templates)
- BouncyCastle Provider (cryptography)
- Apache Commons Codec (encoding)
- JSON processing libraries

**Build configuration:**

- Java version: 21
- Spring Boot version: 3.4.1
- Maven compiler plugin configured for Java 21

**Reference file:** `pom.xml`

### Section 8: Security Architecture

**Purpose:** Document security mechanisms

**Content to include:**

#### RSA Encryption Layer (API Security)

**Location:** `BlockchainController.java:130-164`

**Purpose:** Secure communication between frontend and backend

**Components:**

1. **Hardcoded RSA Keys**

- Java backend private key (for decryption)
- Java backend public key
- Webapp public key (for verification)
- Located in controller code
- **Security concern:** Keys should be in secure configuration

2. **encryptData() method (line 130-137)**

- Encrypts response data with RSA
- Uses backend's private key
- Returns encrypted string

3. **decryptData() method (line 139-148)**

- Decrypts incoming request data
- Uses backend's private key
- Returns plaintext data

4. **verifySignature() method (line 150-164)**

- Verifies request signature from webapp
- Uses webapp's public key
- Ensures request authenticity
- Prevents request tampering

**Flow:**

```
Frontend → RSA Encrypt with backend public key → Backend decrypts
Backend → RSA Encrypt response → Frontend decrypts
Frontend → Sign request with webapp private key → Backend verifies
```

#### ECDSA Signature Security (Transaction Security)

**Purpose:** Ensure transaction authenticity and prevent tampering

**Key generation:**

- ECDSA elliptic curve cryptography
- 256-bit key size
- BouncyCastle provider
- Each wallet has unique key pair

**Transaction signing:**

- Sender signs transaction with private key
- Signature covers: sender + recipient + value + metadata
- Signature included in Transaction object

**Transaction verification:**

- Blockchain verifies signature before processing
- Uses sender's public key
- Invalid signature = transaction rejected

**Double-spend prevention:**

- UTXO locking via pendingUTXOs Set
- Locks acquired before processing
- Released after completion/failure
- Prevents concurrent spending of same UTXO

**Reference files:**

- `Wallet.java` - Key generation
- `Transaction.java:77-96` - Signing and verification
- `util/StringUtil.java:30-57` - Cryptographic primitives

### Section 9: Data Structures and Storage

**Purpose:** Document data models and storage architecture

**Content to include:**

#### In-Memory Storage Architecture

**No database persistence:**

- All data stored in memory (JVM heap)
- Data lost on application restart
- Suitable for demonstration/development
- Production would need persistence layer

**Storage components:**

1. **Blockchain Chain** (`Blockchain.java`)

- `List<Block> chain`
- Sequential block storage
- Grows with each transaction

2. **Global UTXO Set** (`HeiseiChain.java`)

- `HashMap<String, TransactionOutput> UTXOs`
- Static storage (shared across all instances)
- Key: UTXO ID (SHA-256 hash)
- Value: TransactionOutput object

3. **Pending UTXOs** (`HeiseiChain.java`)

- `Set<TransactionOutput> pendingUTXOs`
- Temporary locks during transaction processing
- Prevents double-spending

4. **Wallet Database** (`BlockchainService.java`)

- `HashMap<String, Wallet> walletDatabase`
- Stores all user wallets
- Key: User identifier
- Value: Wallet object (with keys)

5. **Pending Transactions** (`BlockchainService.java`)

- `HashMap pendingTransactions`
- Temporary storage for two-phase transactions
- Entries expire after 5:30 hours

#### Data Flow Through System

**Transaction lifecycle:**

```
1. User initiates → Pending transaction created
2. User confirms → Transaction object created
3. Transaction.processTransaction() → UTXOs locked
4. Validation passes → Outputs created
5. Blockchain.addTransaction() → Block created
6. Block added to chain → UTXOs updated
7. Locks released → Transaction complete
```

**UTXO lifecycle:**

```
1. Created as transaction output
2. Added to HeiseiChain.UTXOs (unspent)
3. Selected for spending → Added to pendingUTXOs (locked)
4. Transaction completes → Removed from UTXOs (spent)
5. New outputs created → Added to UTXOs
```

### Section 10: Donor Tracking Feature

**Purpose:** Explain unique donor attribution system

**Content to include:**

**Feature overview:**
The blockchain maintains full traceability of which donor contributed to each commodity throughout the distribution chain.

**How it works:**

1. **Initial donation (donor creates genesis transaction):**

```
   Donor Alice donates 100kg rice
   → Creates UTXO: {value: 100, commodity: "rice", donor: {Alice: 100}}
```

2. **First redistribution (volunteer receives):**

```
   Volunteer Bob receives 60kg, Alice keeps 40kg change
   → Output 1: {value: 60, commodity: "rice", donor: {Alice: 60}}
   → Output 2: {value: 40, commodity: "rice", donor: {Alice: 40}}
```

3. **Multiple donor scenario:**

```
   Donor Carol donates 50kg rice
   → UTXO: {value: 50, commodity: "rice", donor: {Carol: 50}}
   
   Volunteer Bob combines Alice's 60kg + Carol's 50kg = 110kg total
   Bob sends 80kg to Camp Coordinator
   → Output: {value: 80, commodity: "rice", donor: {Alice: ~43.6, Carol: ~36.4}}
   → Change: {value: 30, commodity: "rice", donor: {Alice: ~16.4, Carol: ~13.6}}
```

**Implementation details:**

**Data structure:** Each TransactionOutput contains:

```java
HashMap<String, Float> donor
// Key: Donor public key
// Value: Amount contributed by that donor
```

**Preservation algorithm (in Transaction.processTransaction()):**

- When gathering input UTXOs, accumulate all donor maps
- When creating output UTXOs, distribute donor attributions proportionally
- Maintains accurate tracking through entire chain

**Use cases:**

- Full donation transparency
- Donor recognition
- Impact tracking (see where donations went)
- Compliance reporting
- Statistics: "X% of camp supplies from donor Y"

**Reference files:**

- `TransactionOutput.java` - Donor map structure
- `Transaction.java:118-163` - Donor tracking preservation logic

### Section 11: Key Differences: Original vs Spring Boot

**Purpose:** Highlight what changed in the conversion

**Content to include:**

**Original Java Blockchain (assumed):**

- Standalone Java application
- Direct method invocations
- Command-line or library usage
- No web interface
- Local execution only

**Spring Boot Conversion (current):**

- Web service architecture
- REST API endpoints
- HTTP-based communication
- Dockerized deployment
- Network accessible
- RSA encryption for API security
- Added service layer for business logic
- Added wallet database management
- Added pending transaction system
- Added HTML/CSV reporting features

**What stayed the same (original blockchain core):**

- Block structure and hashing
- Transaction model with UTXO
- ECDSA signatures
- Cryptographic utilities
- Blockchain validation logic
- Donor tracking feature

**Architecture transformation:**

```
Before: CLI/Library → Blockchain Core Methods
After: HTTP API → Controller → Service → Blockchain Core Methods
```

**Benefits of Spring Boot conversion:**

- Remote access via HTTP
- Multiple clients can connect
- Web-based interfaces possible
- Containerized deployment
- Spring ecosystem benefits (dependency injection, autoconfiguration)
- Easier integration with other services

**Tradeoffs:**

- Added complexity (more layers)
- Network latency for operations
- Need to secure API communication
- Hardcoded RSA keys (should use proper key management)

### Section 12: Limitations and Considerations

**Purpose:** Document known limitations

**Content to include:**

1. **No Persistence**

- All data in memory
- Lost on restart
- Not suitable for production
- Would need database integration

2. **Security Concerns**

- Hardcoded RSA keys in source code
- No authentication/authorization beyond signatures
- No rate limiting
- No HTTPS enforcement

3. **Scalability**

- In-memory storage limits blockchain size
- No pruning mechanism
- UTXO set grows indefinitely
- Single server (no distributed nodes)

4. **Concurrency**

- UTXO locking prevents some concurrency issues
- But in-memory structures not thread-safe everywhere
- Could have race conditions under high load

5. **Error Handling**

- Limited error responses in API
- No detailed validation messages
- Client must interpret failures

6. **Not a True Blockchain**

- Single server (centralized)
- No proof-of-work or consensus
- No distributed network
- More like "blockchain data structure" than "blockchain network"

### Section 13: Running and Testing the Application

**Purpose:** Provide instructions for running the project

**Content to include:**

#### Prerequisites

- Java 21 JDK
- Maven 3.9+
- Docker (optional, for containerized deployment)

#### Build and Run Locally

```bash
# Clone repository
git clone https://github.com/Akash-vadakkeveetil/Heisei-Chain.git
cd Heisei-Chain

# Build with Maven
mvn clean package

# Run the application
java -jar target/HeiseiChain-0.0.1-SNAPSHOT.jar

# Application starts on http://localhost:8080
```

#### Build and Run with Docker

```bash
# Build Docker image
docker build -t heisei-chain .

# Run container
docker run -p 8080:8080 heisei-chain

# Access at http://localhost:8080
```

#### Testing Endpoints

**Using curl:**

1. **Register a donor:**

```bash
curl -X POST http://localhost:8080/api/blockchain/register \
  -H "Content-Type: application/json" \
  -d '{"role": "donor", "userId": "donor1"}'
```

2. **Display blockchain:**

```bash
curl http://localhost:8080/api/blockchain/display
```

3. **Validate blockchain:**

```bash
curl http://localhost:8080/api/blockchain/validate
```

**Note:** Actual requests require RSA encryption (frontend implementation needed)

#### Accessing Reports

- Navigate to `/report` endpoint for CSV generation UI
- Thymeleaf template provides form interface
- Select date range and generate report

---

## Implementation Guidance

### Document Format Output

**File to create:** `HEISEI_CHAIN_DOCUMENTATION.md` in repository root

**Document structure:**

```markdown
# Heisei-Chain: Complete Project Documentation

## Table of Contents
[Links to all sections]

## 1. Project Overview
[Content from Section 1]

## 2. Architecture: Spring Boot Layering
[Content from Section 2 with diagrams]

## 3. REST API Reference
[Content from Section 3]

... [All sections in order]

## Appendices
### Appendix A: File Structure
### Appendix B: Complete Class Diagram
### Appendix C: Glossary
```

### Diagram Specifications

**Diagram 1: Layered Architecture**

- Mermaid graph showing two layers
- Spring Boot components (blue boxes)
- Original core components (orange boxes)
- Arrows showing dependencies
- Save as: layered\_architecture.png

**Diagram 2: Transaction Flow**

- Sequence diagram
- Two-phase transaction creation
- Shows UTXO locking mechanism
- Includes all actors: User, API, Service, Transaction, UTXO
- Save as: transaction\_flow.png

**Diagram 3: UTXO Lifecycle**

- Flowchart showing UTXO states
- Created → Unspent → Locked → Spent → Removed
- Save as: utxo\_lifecycle.png

**Diagram 4: Donor Tracking Example**

- Visual representation of donor attribution through transactions
- Shows how donor map propagates
- Multiple donors merging example
- Save as: donor\_tracking\_example.png

### Code Snippet Guidelines

**When including code:**

- Reference exact file and line numbers
- Include relevant context (5-10 lines around key logic)
- Add inline comments explaining key steps
- Use syntax highlighting (\`\`\`java)
- Attribute to original repository

**Example format:**

```markdown
### Transaction Signature Verification

From `Transaction.java:88-96`:

```java
public boolean verifySignature() {
    String data = StringUtil.getStringFromKey(sender) + 
                  StringUtil.getStringFromKey(reciepient) + 
                  Float.toString(value) + metadata;
    return StringUtil.verifyECDSASig(sender, data, signature);
}
```

This method verifies the transaction signature by:

1. Reconstructing the exact data that was signed
2. Using the sender's public key to verify the signature
3. Returning true if signature is valid, false otherwise

```
### Cross-References
**Throughout the documentation:**
- Link between sections when concepts relate
- Reference file paths for all code mentions
- Include line numbers for specific implementations
- Create glossary for technical terms
- Link external resources (Spring Boot docs, BouncyCastle docs)

### Visual Elements
**Beyond diagrams:**
- Use tables for comparing concepts
- Use code blocks for algorithms
- Use blockquotes for important notes
- Use lists for step-by-step processes
- Use emoji sparingly for visual markers (⚠️ for warnings, 💡 for tips)

---

## Validation Checklist

Before considering documentation complete, verify:

✅ **Completeness:**
- All 13 sections fully documented
- All REST endpoints explained
- All core components covered
- All flows diagrammed
- Code snippets for key algorithms

✅ **Accuracy:**
- File paths verified against repository
- Line numbers checked
- Code snippets tested/verified
- Technical details accurate

✅ **Clarity:**
- Technical depth appropriate (deep but understandable)
- Diagrams clear and labeled
- Examples provided for complex concepts
- Jargon defined in glossary

✅ **Usability:**
- Table of contents with links
- Code snippets have context
- Cross-references work
- Can be read sequentially or by section

✅ **Layering Explanation:**
- Original vs Spring Boot clearly distinguished
- Integration points documented
- Conversion benefits explained
- Architecture transformation visualized
```