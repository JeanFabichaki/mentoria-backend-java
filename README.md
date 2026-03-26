📑 Especificação Técnica: Motor de Split de Pagamentos (Project PaySplit)
-------------------------------------------------------------------------

### 1\. Visão Geral

O sistema deve atuar como um motor de processamento em lote (Batch Processing) para decompor transações financeiras brutas em lançamentos de liquidação e obrigações tributárias, garantindo que cada transação seja processada exatamente uma vez (**Idempotência**) e que o somatório das partes sempre seja igual ao todo (**Integridade Vertical**).

### 2\. Dicionário de Dados (Interface de Entrada)

O arquivo de entrada deve ser um CSV padrão RFC 4180 (UTF-8, delimitador `;`).

| **Nome do Campo** | **Tipo de Dado** | **Restrições / Validações** |
| --- | --- | --- |
| `external_id` | **UUID** | Chave primária de origem. Obrigatório. |
| `merchant_name` | String | Razão Social do recebedor. Máx 150 chars. |
| `payer_document` | String (CNPJ) | Somente números (14 dígitos). Validar dígito verificador. |
| `amount_gross` | BigDecimal | Valor total da transação. Deve ser > 0. |
| `amount_tax` | BigDecimal | Valor retido para impostos. Deve ser >= 0 e < `amount_gross`. |
| `legal_invoice_id` | Long | Número da Nota Fiscal (NF-e). |
| `receiver_document` | String (CNPJ) | CNPJ do destinatário dos fundos. |
| `receiver_bank_code` | String (3) | Código BACEN (ex: 001, 237, 341). |
| `receiver_agency` | String (4) | Agência sem dígito. |
| `receiver_account` | String (12) | Conta com dígito. |

Use o script em python que está no projeto para gerar os dados.
* * * * *

### 3\. Regras de Negócio de Missão Crítica

#### 3.1. O Protocolo de Idempotência

O sistema não deve confiar apenas na existência do `external_id` no banco de dados.

-   **Caso A (Novo ID):** Criar os registros nas tabelas de `LIQUIDACAO` e `IMPOSTO`.

-   **Caso B (ID Existente - Match de Dados):** Se o `external_id` já existe e todos os valores financeiros coincidem, o sistema deve ignorar o processamento (Status: `SKIPPED`).

-   **Caso C (ID Existente - Divergência):** Se o `external_id` existe, mas os valores no CSV são diferentes do banco de dados, o sistema deve:

    1.  Registrar o erro de integridade em log.

    2.  Realizar o **Estorno (Reversal)** ou **Ajuste** dos valores antigos para os novos.

    3.  Marcar como `PROCESSED_WITH_ADJUSTMENT`.

#### 3.2. Regra de Split (Cálculo)

-   **Fórmula:** $AmountNet = AmountGross - AmountTax$

-   A soma de `AmountNet` + `AmountTax` deve ser obrigatoriamente igual a `AmountGross`. Qualquer divergência de centavos (Rounding) deve ser abortada.

* * * * *

### 4\. Entregáveis (Output)

#### 4.1. Persistência

As tabelas devem registrar o `created_at` (Data/Hora do processamento).

-   **Table `settlement_transfers`:** Registra o valor líquido que vai para a conta bancária.

-   **Table `tax_obligations`:** Registra o valor que será provisionado para o governo.

#### 4.2. Relatórios de Compliance (CSV em `/outputs`)

1.  **Relatório Analítico:** Um espelho do processamento com colunas extras: `status_processamento`, `data_hora_processamento`, `valor_liquido_calculado`.

2.  **Relatório Sintético (Batimento):**

    -   Total de registros processados com sucesso.

    -   Soma total de Gross vs. Soma total de Net + Tax (deve bater zero).

    -   Total de transações novas vs. Total de ajustes efetuados.

* * * * *

### 5\. Fases de Implementação (Roadmap)

-   **Fase 1: Ingestão e Validação.** Criar o parser de CSV e as validações de campos (CNPJ, UUID, Valores Negativos).

-   **Fase 2: Camada de Persistência e Idempotência.** Implementar o JPA/Hibernate com a lógica de checagem de duplicidade por `external_id`.

-   **Fase 3: Service de Split e Ajustes.** Desenvolver a lógica de cálculo e o mecanismo de correção de transações divergentes.

-   **Fase 4: Observabilidade e Reporting.** Gerar os arquivos CSV de saída e logs de erro detalhados.

* * * * *

### 6\.Critério de Aceite (Definition of Done)

> "O sistema deve processar um arquivo de 10.000 linhas em menos de 30 segundos, garantindo que se o mesmo arquivo for reprocessado, nenhum centavo extra seja gerado nas tabelas de liquidação, apenas os logs de 'skip'."
---

## 7. Especificação Técnica (Architecture)

### 7.1. Stack Tecnológica
* **Runtime:** Java 21 (LTS)
* **Framework:** Spring Boot 3.x + Spring Batch 5.x
* **Database:** PostgreSQL 16
* **Infra:** Docker & Docker Compose
* **Migration:** Flyway ou Liquibase

### 7.2. Modelo de Dados (Entidades Principais)
* **`payments`**: Tabela mestre que controla o `external_id`, o status atual e o `data_hash` (SHA-256 da linha original para detecção de mudanças).
* **`settlement_records`**: Registros de crédito para o recebedor (valor líquido + dados bancários).
* **`tax_records`**: Registros de provisão tributária para o governo.

---

## 8. Entregáveis de Saída (Reports)
Ao final de cada execução, o sistema deve gerar dois arquivos CSV na pasta `/outputs`:

1. **Relatório Analítico (`analitico_YYYYMMDD_HHMM.csv`):** Detalhamento de cada item processado, incluindo colunas de `status_processamento` e `data_hora_execucao`.
2. **Relatório Sintético (`sintetico_YYYYMMDD_HHMM.csv`):** Resumo financeiro totalizado:
    * Total Bruto Processado.
    * Total Líquido a Pagar.
    * Total Impostos Retidos.
    * Quantitativo de Transações (Novas vs. Ajustadas vs. Ignoradas).
---

## 9. Como Executar
1. Certifique-se de ter o **Docker** e **Docker Compose** instalados.
2. Coloque o arquivo de entrada em `./data/input/pagamentos.csv`.
3. Execute o comando:
   ```bash
   docker-compose up --build

* * * * *

### 10. Para que eu possa acompanhar sua evolução e revisar seu código da melhor forma, siga este fluxo:

1.  **Faça um Fork:** Acesse o meu repositório e clique no botão **'Fork'** no canto superior direito. Isso criará uma cópia do projeto na sua conta.

2.  **Clone o seu Fork:** Baixe o código para sua máquina local.

3.  **Desenvolva em Sprints:** Como dividimos o projeto em fases, tente realizar commits organizados para cada etapa (Ex: *'feat: setup infra e banco com docker'*, *'feat: implementação do spring batch reader'*).

4.  **Crie Pull Requests (PRs):** Sempre que concluir uma fase, abra um PR do seu repositório para o meu. É através desse PR que eu farei o **Code Review**, deixando comentários diretamente nas linhas de código, exatamente como fazemos no dia a dia de uma empresa.
