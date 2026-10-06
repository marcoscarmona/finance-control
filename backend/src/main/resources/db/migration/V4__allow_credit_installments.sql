ALTER TABLE expense_installments DROP CONSTRAINT expense_installments_amount_check;
ALTER TABLE expense_installments ADD CONSTRAINT expense_installments_amount_check CHECK (amount <> 0);
