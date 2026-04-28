CREATE TABLE IF NOT EXISTS system_users (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS banks (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES system_users(id),
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50),
    type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS accounts (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES system_users(id),
    bank_id UUID NOT NULL REFERENCES banks(id),
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS credit_cards (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES system_users(id),
    bank_id UUID NOT NULL REFERENCES banks(id),
    name VARCHAR(255) NOT NULL,
    last_four_digits VARCHAR(4) NOT NULL,
    limit_amount NUMERIC(15, 2) NOT NULL,
    closing_day INTEGER NOT NULL,
    due_day INTEGER NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS categories (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES system_users(id),
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    color VARCHAR(20) NOT NULL,
    icon VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS transactions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES system_users(id),
    bank_id UUID REFERENCES banks(id),
    account_id UUID REFERENCES accounts(id),
    credit_card_id UUID REFERENCES credit_cards(id),
    category_id UUID NOT NULL REFERENCES categories(id),
    date DATE NOT NULL,
    description VARCHAR(500) NOT NULL,
    merchant VARCHAR(255),
    amount NUMERIC(15, 2) NOT NULL,
    type VARCHAR(50) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    source VARCHAR(50) NOT NULL,
    installment_number INTEGER,
    installment_total INTEGER,
    recurring BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS monthly_budgets (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES system_users(id),
    category_id UUID NOT NULL REFERENCES categories(id),
    year_month VARCHAR(7) NOT NULL,
    limit_amount NUMERIC(15, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS merchant_rules (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES system_users(id),
    pattern VARCHAR(255) NOT NULL,
    category_id UUID NOT NULL REFERENCES categories(id),
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS import_files (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES system_users(id),
    file_name VARCHAR(255) NOT NULL,
    type VARCHAR(20) NOT NULL,
    imported_at TIMESTAMP NOT NULL DEFAULT NOW(),
    total_transactions INTEGER NOT NULL DEFAULT 0
);
