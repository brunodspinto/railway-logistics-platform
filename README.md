### sem3-pi-25-26-g054-repo

Este repositório contém o desenvolvimento do **Projeto Integrador do 3º semestre** das UC:  
- **ARQCP** (Arquitetura de Computadores)  
- **BDDAD** (Bases de Dados)  
- **ESINF** (Estruturas de Informação)  
- **FSIAP** (Física Aplicada)  
- **LAPR3** (Laboratório de Projeto III)  

O objetivo é criar uma solução informática de apoio à **gestão logística ferroviária**, abordando aspetos como gestão de armazéns, despacho de cargas, segurança de tráfego e integração multimodal.

---

## Como atualizar entre PC e GitHub

Quando trabalhas com Git e GitHub, existem duas operações principais:  
- **Enviar alterações do teu PC para o GitHub (push)**  
- **Receber alterações do GitHub no teu PC (pull)**  

---

### Do PC para o GitHub (push)

1. Abrir o terminal e entrar na pasta do repositório:  
   ```bash
   cd caminho/para/o/repositorio

   git status 

   git add .

   git commit -m "mensagem das alterações"

   git push origin main
 
---

### Do GitHub para o PC (pull)

1. Abrir o terminal e entrar na pasta do repositório:  
   ```bash
   cd caminho/para/o/repositorio
   
   git pull origin main

---

## 📂 Estrutura do Repositório


| Pasta             | Conteúdo                                                                 |
|-------------------|--------------------------------------------------------------------------|
| **`.build/`**     | Scripts e ficheiros de automação de build/integração contínua (CI/CD). Ex.: pipelines, configuração de testes automáticos, ficheiros de compilação. |
| **`doc/`**        | Documentação do projeto. <br>• `auth/`: autenticação e permissões. <br>• `global-artifacts/`: diagramas UML, relatórios, glossário e artefactos comuns. <br>• `USXX/`: documentação das *User Stories* (descrição, critérios de aceitação, evidências). |
| **`freightMngt/`** | Código relacionado com **gestão de fretes**: operações de planeamento de cargas, cálculo de tempos de viagem, alocação de locomotivas e vagões. |
| **`res/`**        | Recursos auxiliares: datasets CSV fornecidos (`wagons.csv`, `items.csv`, `orders.csv`, `returns.csv`), imagens, mapas e outros ficheiros de suporte. |
| **`sql-scripts/`** | Scripts SQL (PL/SQL): <br>• **DDL** (criação do modelo físico da BD). <br>• **DML** (inserção de dados). <br>• Queries para responder às user stories de **BDDAD**. |
| **`warehouseMngt/`** | Código para **gestão de armazéns**: algoritmos FIFO/FEFO, alocação e picking de pedidos, planeamento de percursos no armazém, gestão de devoluções (quarentena, restock, descarte). |

---

## 🛠️ Tecnologias
- **Java** → aplicação principal  
- **PL/SQL** → criação e gestão da base de dados  
- **C/Assembly** → interação com estações ferroviárias e componentes de baixo nível  
- **Visual Paradigm / PlantUML** → modelação conceptual e diagramas UML  
- **JUnit** → testes automatizados  

---

## 🚀 Metodologia
- Abordagem **ágil (SCRUM)** com 3 sprints de 4 semanas  
- Desenvolvimento **iterativo e incremental**  
- **TDD (Test-Driven Development)** como prática de implementação  

---

## ✅ Requisitos Não Funcionais
- Boas práticas de programação (CamelCase, modularidade, OOP)  
- Documentação automática com **Javadoc**  
- Diagramas e imagens em **SVG**  
- Integração contínua com base de dados remota  

---

## 👥 Equipa
- **Scrum Master rotativo** por sprint  
- Repositório privado na organização **Departamento de Engenharia Informática**  
- Professores adicionados com acesso *Reader*  

---

