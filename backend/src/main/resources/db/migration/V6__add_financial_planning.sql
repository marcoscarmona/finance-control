CREATE TABLE investment_positions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    institution VARCHAR(255) NOT NULL,
    reference_month VARCHAR(7) NOT NULL,
    amount NUMERIC(19,2) NOT NULL CHECK (amount >= 0),
    UNIQUE(user_id, institution, reference_month)
);
CREATE TABLE investment_goals (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE REFERENCES users(id),
    target_amount NUMERIC(19,2) NOT NULL CHECK (target_amount > 0),
    target_date DATE NOT NULL
);
CREATE TABLE receivables (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    person_name VARCHAR(255) NOT NULL,
    description VARCHAR(500) NOT NULL,
    amount NUMERIC(19,2) NOT NULL CHECK (amount > 0),
    due_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    received_at DATE
);
CREATE TABLE recurring_expenses (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    category_id UUID NOT NULL REFERENCES categories(id),
    credit_card_id UUID REFERENCES credit_cards(id),
    account_id UUID REFERENCES accounts(id),
    name VARCHAR(255) NOT NULL,
    amount NUMERIC(19,2) NOT NULL CHECK (amount > 0),
    kind VARCHAR(30) NOT NULL,
    frequency VARCHAR(20) NOT NULL,
    charge_day SMALLINT NOT NULL CHECK (charge_day BETWEEN 1 AND 31),
    start_date DATE NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);
