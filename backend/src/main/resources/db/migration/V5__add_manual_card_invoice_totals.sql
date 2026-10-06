CREATE TABLE card_invoice_manual_totals (
    id UUID PRIMARY KEY,
    credit_card_id UUID NOT NULL REFERENCES credit_cards(id) ON DELETE CASCADE,
    reference_month VARCHAR(7) NOT NULL,
    total_amount NUMERIC(19, 2) NOT NULL CHECK (total_amount >= 0),
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT card_invoice_manual_totals_card_month_unique UNIQUE (credit_card_id, reference_month)
);
