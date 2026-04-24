CREATE TABLE categories (
    id UUID PRIMARY KEY,
    name VARCHAR(120) NOT NULL UNIQUE,
    monthly_limit NUMERIC(14,2),
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE transactions (
    id UUID PRIMARY KEY,
    transaction_date DATE NOT NULL,
    month_ref VARCHAR(7) NOT NULL,
    bank VARCHAR(80) NOT NULL,
    card VARCHAR(80),
    description TEXT NOT NULL,
    merchant VARCHAR(160),
    category_id UUID REFERENCES categories(id),
    amount NUMERIC(14,2) NOT NULL,
    type VARCHAR(20) NOT NULL,
    installment_number INT,
    installment_total INT,
    source VARCHAR(80),
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE merchant_rules (
    id UUID PRIMARY KEY,
    keyword VARCHAR(160) NOT NULL,
    category_id UUID NOT NULL REFERENCES categories(id),
    priority INT NOT NULL DEFAULT 100,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE subscriptions (
    id UUID PRIMARY KEY,
    name VARCHAR(160) NOT NULL,
    bank VARCHAR(80),
    expected_amount NUMERIC(14,2),
    category_id UUID REFERENCES categories(id),
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE installments (
    id UUID PRIMARY KEY,
    description TEXT NOT NULL,
    bank VARCHAR(80) NOT NULL,
    total_installments INT NOT NULL,
    current_installment INT NOT NULL,
    installment_amount NUMERIC(14,2) NOT NULL,
    category_id UUID REFERENCES categories(id),
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);
