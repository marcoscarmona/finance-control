ALTER TABLE expenses DROP CONSTRAINT expenses_total_amount_check;
ALTER TABLE expenses ADD CONSTRAINT expenses_total_amount_check CHECK (total_amount <> 0);

CREATE TABLE merchant_category_rules (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    merchant_key VARCHAR(255) NOT NULL,
    category_id UUID NOT NULL REFERENCES categories(id),
    UNIQUE(user_id, merchant_key)
);
