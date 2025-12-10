# 🌊 PAiNEL SHA - Sistema de Monitoramento de Hidrômetros

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![Maven](https://img.shields.io/badge/Maven-3.8+-red?style=for-the-badge&logo=apache-maven)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

**Sistema inteligente para monitoramento automático de consumo de água através de hidrômetros digitais**

[Características](#-características) • [Instalação](#-instalação) • [Uso](#-uso) • [Arquitetura](#-arquitetura) • [Progresso](#-progresso-do-projeto)

</div>

---

## 📊 Progresso do Projeto

![Progresso](https://progress-bar.dev/100/?title=Concluído&width=400&color=00ff00)

### ✅ Funcionalidades Implementadas

#### 🎯 RF01 - CRUD de Usuários e Contas (100%)
- [x] Cadastro completo de usuários com validação de CPF
- [x] Sistema de perfis (Admin/Operador) com controle de acesso
- [x] CRUD completo de contas de água
- [x] Vinculação e desvinculação de SHAs a contas
- [x] Padrão State para gestão de estados da conta (Ativa/Suspensa/Inadimplente/Cancelada)
- [x] Validação de operações permitidas por estado
- [x] Persistência de dados em JSON

#### 📡 RF02 - Monitoramento de Consumo (100%)
- [x] Leitura de consumo **exclusivamente por imagem** (conforme restrição R2)
- [x] Padrão Bridge com LeitorImplementador (Simulado e OCR)
- [x] Monitoramento periódico automatizado por intervalo configurável
- [x] Cálculo de consumo individual por SHA
- [x] Cálculo de consumo agregado por conta
- [x] Padrão State para monitoramento (Iniciado/Pausado/Parado/Erro)
- [x] Pausa e retomada de monitoramento
- [x] Tratamento de erros de leitura de imagem

#### 🔔 RF03 - Sistema de Alertas (100%)
- [x] Padrão Observer para notificação de alertas
- [x] Detecção automática de excesso de consumo
- [x] Configuração de limites personalizados por conta
- [x] Padrão Strategy para múltiplas estratégias de notificação
- [x] Padrão Factory para criação de estratégias
- [x] Estratégias implementadas: Email, SMS, Push, Concessionária, Painel Interno
- [x] Gerenciamento de alertas (lidos/pendentes)
- [x] Configuração dinâmica de estratégias por conta

#### 📝 RF04 - Sistema de Log e Rastreabilidade (100%)
- [x] Sistema de logs centralizado (SistemaLog)
- [x] Registro de todas as operações críticas
- [x] Níveis de log (INFO, WARN, ERROR, DEBUG)
- [x] Logs persistidos em arquivo (painel.log)
- [x] Consulta de logs por quantidade via Fachada
- [x] Auditoria completa de ações do sistema

#### 💾 RF05 - Persistência Flexível (100%)
- [x] Padrão Bridge para abstração de persistência
- [x] Interface PersistenciaImplementador
- [x] Implementação em arquivos JSON (PersistenciaArquivoImpl)
- [x] Arquitetura preparada para implementação em BD
- [x] Troca de implementação sem alterar regras de negócio

#### ↩️ RF06 - Operações Reversíveis (100%)
- [x] Padrão Command para operações críticas
- [x] Sistema completo de Undo/Redo
- [x] GerenciadorComandos com pilhas de histórico
- [x] Comandos implementados: VincularSHA, RemoverConta, AlterarEstadoConta, SuspenderConta
- [x] Histórico completo de comandos executados
- [x] Timestamps e descrições de comandos

#### 🏗️ Padrões de Projeto GoF (100%)
- [x] **Singleton** - Instância única de gerenciadores e Fachada
- [x] **Facade** - FachadaPainel como ponto único de acesso
- [x] **Bridge** - Leitura de imagens e Persistência
- [x] **Observer** - Sistema de alertas e notificações
- [x] **Strategy** - Estratégias de notificação dinâmicas
- [x] **Factory** - Criação de estratégias de notificação
- [x] **State** - Estados de conta e monitoramento
- [x] **Command** - Operações reversíveis com histórico
- [x] **DTO** - Transferência de dados entre camadas

#### 🔒 Restrições Críticas Atendidas (100%)
- [x] **R1** - Painel não acessa funcionalidades do SHA diretamente
- [x] **R2** - Leitura de consumo **somente por imagem**
- [x] Desacoplamento completo entre Painel e SHA
- [x] Comunicação apenas via arquivos de imagem

#### 🔄 Sincronização com Simuladores (100%)
- [x] Descoberta automática de simuladores externos
- [x] Sincronização periódica em background
- [x] Configuração persistente em arquivo
- [x] Controle de intervalo de sincronização
- [x] Thread dedicada para sincronização

#### 💻 Interface do Usuário (100%)
- [x] Menu interativo completo via console (CLI)
- [x] Todas as funcionalidades acessíveis via Fachada
- [x] Mensagens de feedback detalhadas
- [x] Validação de entradas do usuário
- [x] Controle de acesso por perfil
- [x] Navegação intuitiva por menus

---

## 🚀 Características

### 🎨 Padrões de Projeto Implementados

O projeto faz uso extensivo de padrões de projeto GoF e arquiteturais:

| Padrão | Uso | Localização |
|--------|-----|-------------|
| **Singleton** | Instância única de gerenciadores | Todos os subsistemas |
| **Facade** | Interface simplificada para o sistema | `FachadaPainel` |
| **Observer** | Notificação de alertas de consumo | `SistemaAlertas`, `AlertaObserver` |
| **Strategy** | Estratégias de notificação dinâmicas | `EstrategiaNotificacao`, `FabricaEstrategiaNotificacao` |
| **Command** | Operações reversíveis (Undo/Redo) | `Comando`, `GerenciadorComandos` |
| **State** | Estados de conta e monitoramento | `EstadoConta`, `EstadoMonitoramento` |
| **Bridge** | Abstração de leitores de hidrômetro | `LeitorImplementador`, `MonitorConsumo` |
| **DTO** | Transferência de dados entre camadas | `UsuarioDTO`, `ContaAguaDTO`, `AlertaDTO` |

### 🔑 Funcionalidades Principais

#### 👥 Gestão de Usuários
- Cadastro com perfis diferenciados (Admin/Operador)
- Validação de CPF único
- Atualização completa de dados
- Remoção segura com verificações

#### 💧 Gestão de Contas
- Criação vinculada a usuários
- Múltiplos SHAs por conta
- Estados configuráveis com regras de transição
- Limites personalizados de consumo

#### 📊 Monitoramento Inteligente
- Leitura automática periódica
- Suporte a múltiplos hidrômetros
- Cálculo agregado de consumo
- Pausa e retomada de monitoramento
- Estados: Iniciado → Pausado → Retomado → Parado

#### 🔔 Sistema de Alertas
- Detecção automática de excesso
- Notificações configuráveis por tipo
- Alertas pendentes e histórico
- Integração com observer pattern

#### ↩️ Operações Reversíveis
- Desfazer operações críticas
- Refazer operações desfeitas
- Histórico completo de comandos
- Memento pattern implícito

#### 🔄 Sincronização com Simuladores
- Descoberta automática de simuladores
- Sincronização periódica em background
- Configuração persistente
- Controle de intervalo

---

## 📋 Pré-requisitos

- **Java JDK 17** ou superior
- **Maven 3.8+** para gerenciamento de dependências
- **Git** para controle de versão

### Dependências do Projeto

```xml
<!-- OCR para leitura de hidrômetros -->
<dependency>
    <groupId>net.sourceforge.tess4j</groupId>
    <artifactId>tess4j</artifactId>
    <version>5.11.0</version>
</dependency>

<!-- Processamento de imagens -->
<dependency>
    <groupId>org.openpnp</groupId>
    <artifactId>opencv</artifactId>
    <version>4.9.0-0</version>
</dependency>

<!-- Testes unitários -->
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.13.2</version>
</dependency>
```

---

## 🛠️ Instalação

### 1️⃣ Clone o Repositório

```bash
git clone https://github.com/PedroHenriqueRolimCordeiro/PainelHidrometro.git
cd PainelHidrometro
```

### 2️⃣ Compile o Projeto

```bash
mvn clean compile
```

### 3️⃣ Execute os Testes

```bash
mvn test
```

### 4️⃣ Gere o JAR

```bash
mvn package
```

### 5️⃣ Execute o Sistema

```bash
java -cp target/painel-sha-1.0-SNAPSHOT.jar Main
```

Ou simplesmente:

```bash
mvn exec:java -Dexec.mainClass="Main"
```

---

## 💻 Uso

### Menu Principal

Ao iniciar o sistema, você verá o menu interativo:

```
╔═══════════════════════════════════════════════════════════╗
║   PAINEL DE MONITORAMENTO DE HIDRÔMETROS (PAiNEL SHA)    ║
╚═══════════════════════════════════════════════════════════╝

═══════════════════════════════════════════════════════════
                    MENU PRINCIPAL
═══════════════════════════════════════════════════════════
 1. Gestão de Usuários
 2. Gestão de Contas de Água
 3. Monitoramento de Consumo
 4. Alertas e Notificações
 5. Operações Reversíveis (Undo/Redo)
 6. Consultar Logs do Sistema
 7. Sincronização de Simuladores
 0. Sair
═══════════════════════════════════════════════════════════
```

### Exemplo de Uso Rápido

#### Criar Usuário
```
Menu Principal → 1 (Usuários) → 1 (Cadastrar)
- CPF: 12345678900
- Nome: João Silva
- Email: joao@email.com
- Telefone: (85) 99999-9999
- Endereço: Rua A, 123
- Perfil: 1 (Admin)
```

#### Criar Conta e Vincular SHA
```
Menu Principal → 2 (Contas) → 1 (Criar Conta)
- Número: 555-2025
- CPF: 12345678900

Menu Principal → 2 (Contas) → 4 (Vincular SHA)
- Número da conta: 555-2025
- ID do SHA: 1001
```

#### Configurar Monitoramento
```
Menu Principal → 4 (Alertas) → 1 (Configurar Limite)
- Número da conta: 555-2025
- Limite: 50.0 m³

Menu Principal → 3 (Monitoramento) → 3 (Iniciar)
- Número da conta: 555-2025
- Intervalo: 10 segundos
```

#### Desfazer Operação
```
Menu Principal → 5 (Undo/Redo) → 1 (Desfazer)
✅ Operação desfeita com sucesso!
```

---

## 🏗️ Arquitetura

### Estrutura de Diretórios

```
PainelHidrometro/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── Main.java                    # Ponto de entrada
│   │   │   ├── dto/                         # Data Transfer Objects
│   │   │   │   ├── UsuarioDTO.java
│   │   │   │   ├── ContaAguaDTO.java
│   │   │   │   └── AlertaDTO.java
│   │   │   ├── excecoes/                    # Exceções customizadas
│   │   │   ├── fachada/                     # Padrão Facade
│   │   │   │   └── FachadaPainel.java
│   │   │   ├── modelo/                      # Entidades de domínio
│   │   │   │   ├── Usuario.java
│   │   │   │   ├── ContaAgua.java
│   │   │   │   ├── Alerta.java
│   │   │   │   └── enums/
│   │   │   └── subsistemas/                 # Subsistemas especializados
│   │   │       ├── alertas/                 # Observer pattern
│   │   │       ├── comandos/                # Command pattern
│   │   │       ├── contas/                  # State pattern
│   │   │       ├── log/                     # Sistema de logs
│   │   │       ├── monitoramento/           # Bridge + State
│   │   │       ├── notificacoes/            # Strategy pattern
│   │   │       ├── persistencia/            # Bridge pattern
│   │   │       ├── sincronizacao/           # Thread sincronização
│   │   │       └── usuarios/                # CRUD usuários
│   │   └── resources/
│   │       └── config_sincronizacao.txt     # Config simuladores
│   └── test/
│       └── java/                            # Testes unitários
├── dados/                                   # Dados persistidos
│   ├── alertas/                             # JSONs de alertas
│   └── contas/                              # JSONs de contas
├── logs/
│   └── painel.log                           # Logs do sistema
├── saida/                                   # Imagens processadas
├── pom.xml                                  # Configuração Maven
└── README.md                                # Este arquivo
```

### Diagrama de Camadas

```
┌─────────────────────────────────────────────────────────┐
│                    INTERFACE (Main)                     │
│              Menu Interativo + Scanner                  │
└─────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────┐
│                  FACHADA (Facade)                       │
│         Interface simplificada para subsistemas         │
└─────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────┐
│                    SUBSISTEMAS                          │
│  ┌─────────────┬─────────────┬─────────────┬─────────┐ │
│  │  Usuários   │   Contas    │   Alertas   │  Logs   │ │
│  └─────────────┴─────────────┴─────────────┴─────────┘ │
│  ┌─────────────┬─────────────┬─────────────┬─────────┐ │
│  │ Monitoring  │ Notificações│  Comandos   │Persist. │ │
│  └─────────────┴─────────────┴─────────────┴─────────┘ │
└─────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────┐
│                  MODELO DE DOMÍNIO                      │
│          Usuario | ContaAgua | Alerta | Enums          │
└─────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────┐
│                   PERSISTÊNCIA                          │
│              Arquivos JSON (Bridge Pattern)             │
└─────────────────────────────────────────────────────────┘
```

### Fluxo de Monitoramento

```
┌──────────────┐       ┌──────────────┐       ┌──────────────┐
│ Iniciar      │       │ Leitura      │       │ Verificação  │
│ Monitoramento├──────>│ Periódica    ├──────>│ de Limite    │
└──────────────┘       └──────────────┘       └──────┬───────┘
                                                      │
                               ┌──────────────────────┼────────────────┐
                               │ Excedeu?             │                │
                               ↓ Sim                  ↓ Não            │
                        ┌──────────────┐      ┌──────────────┐        │
                        │ Criar Alerta │      │  Continuar   │        │
                        └──────┬───────┘      └──────────────┘        │
                               │                                       │
                               ↓                                       │
                        ┌──────────────┐                               │
                        │ Notificar    │                               │
                        │ Observers    │                               │
                        └──────┬───────┘                               │
                               │                                       │
                               ↓                                       │
                        ┌──────────────┐                               │
                        │ Enviar       │                               │
                        │ Notificações │                               │
                        └──────────────┘                               │
                               │                                       │
                               └───────────────────────────────────────┘
```

---

## 📝 Logs

Os logs do sistema são armazenados em:
- `logs/painel.log` - Log principal do sistema
- `src/main/java/logs/painel.log` - Log de desenvolvimento

Formato de log:
```
[2025-12-10 14:30:45] [INFO] Usuário cadastrado: João Silva (CPF: 12345678900)
[2025-12-10 14:31:20] [ALERTA] Conta 555-2025 excedeu limite: 52.5 m³ > 50.0 m³
[2025-12-10 14:32:10] [INFO] Comando executado: VincularSHA (conta=555-2025, sha=1001)
```

---

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

---

## 👨‍💻 Autor

**Pedro Henrique Rolim Cordeiro**

- GitHub: [@PedroHenriqueRolimCordeiro](https://github.com/PedroHenriqueRolimCordeiro)
- Projeto: [PainelHidrometro](https://github.com/PedroHenriqueRolimCordeiro/PainelHidrometro)

---

## 📞 Suporte

Para reportar bugs ou solicitar features, abra uma [issue](https://github.com/PedroHenriqueRolimCordeiro/PainelHidrometro/issues) no GitHub.

Para dúvidas sobre uso, consulte a documentação ou entre em contato.

