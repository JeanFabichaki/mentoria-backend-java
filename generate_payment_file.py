import csv
import uuid
import random

# Configurações do Gerador
TOTAL_ROWS = 20000
OUTPUT_FILE = '/Users/amandasantos/Projetos/mentoria-backend-java/springboot-pay-split/src/main/resources/data/input/input_pagamentos.csv'

def generate_cnpj():
    return ''.join([str(random.randint(0, 9)) for _ in range(14)])

def generate_data():
    data = []
    # Criamos alguns UUIDs duplicados propositalmente para testar a idempotência
    duplicate_uuids = [str(uuid.uuid4()) for _ in range(50)]
    
    for i in range(TOTAL_ROWS):
        # 1% de chance de ser um UUID duplicado (para testar a regra de negócio)
        if random.random() < 0.01:
            external_id = random.choice(duplicate_uuids)
        else:
            external_id = str(uuid.uuid4())

        gross = round(random.uniform(100.0, 50000.0), 2)
        tax_rate = random.choice([0.05, 0.10, 0.15]) # 5%, 10% ou 15%
        tax_amount = round(gross * tax_rate, 2)
        
        row = {
            'external_id': external_id,
            'merchant_name': f'Empresa Parceira {random.randint(100, 999)} LTDA',
            'payer_document': generate_cnpj(),
            'amount_gross': f"{gross:.2f}",
            'amount_tax': f"{tax_amount:.2f}",
            'legal_invoice_id': random.randint(1000, 999999),
            'receiver_document': generate_cnpj(),
            'receiver_bank': random.choice(['001', '237', '341', '033', '077']),
            'receiver_agency': str(random.randint(1000, 9999)).zfill(4),
            'receiver_account': f"{random.randint(10000, 99999)}-{random.randint(0, 9)}"
        }
        data.append(row)
    return data

# Escrita do CSV
headers = [
    'external_id', 'merchant_name', 'payer_document', 'amount_gross', 
    'amount_tax', 'legal_invoice_id', 'receiver_document', 
    'receiver_bank', 'receiver_agency', 'receiver_account'
]

with open(OUTPUT_FILE, 'w', newline='', encoding='utf-8') as f:
    writer = csv.DictWriter(f, fieldnames=headers, delimiter=';')
    writer.writeheader()
    writer.writerows(generate_data())

print(f"✅ Arquivo {OUTPUT_FILE} gerado com sucesso com {TOTAL_ROWS} linhas.")
