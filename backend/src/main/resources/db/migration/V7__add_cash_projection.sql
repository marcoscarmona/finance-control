ALTER TABLE investment_positions ADD COLUMN available_for_payments BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE receivables ALTER COLUMN due_date DROP NOT NULL;
CREATE TABLE income_forecasts (id UUID PRIMARY KEY, user_id UUID NOT NULL REFERENCES users(id), source_name VARCHAR(255) NOT NULL, reference_month VARCHAR(7) NOT NULL, amount NUMERIC(19,2) NOT NULL CHECK(amount > 0), UNIQUE(user_id, source_name, reference_month));
CREATE TABLE recurring_expense_occurrences (id UUID PRIMARY KEY, recurring_expense_id UUID NOT NULL REFERENCES recurring_expenses(id), reference_month VARCHAR(7) NOT NULL, status VARCHAR(20) NOT NULL DEFAULT 'PLANNED', expense_id UUID REFERENCES expenses(id), UNIQUE(recurring_expense_id, reference_month));
