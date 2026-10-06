INSERT INTO income_forecasts (id, user_id, source_name, reference_month, amount)
SELECT '619ab482-a7ad-4f9c-a2a3-d3eef9c3d101', id, 'BTG', '2026-10', 5000.00
FROM users
WHERE email = 'mtcarmona50@gmail.com'
ON CONFLICT (user_id, source_name, reference_month) DO NOTHING;

INSERT INTO income_forecasts (id, user_id, source_name, reference_month, amount)
SELECT seed.id, users.id, 'Stella', seed.reference_month, 10730.00
FROM users
CROSS JOIN (
    VALUES
        ('b36b1081-b11e-445a-91ca-3566613d8101'::UUID, '2026-10'),
        ('b36b1081-b11e-445a-91ca-3566613d8102'::UUID, '2026-11'),
        ('b36b1081-b11e-445a-91ca-3566613d8103'::UUID, '2026-12')
) AS seed(id, reference_month)
WHERE users.email = 'mtcarmona50@gmail.com'
ON CONFLICT (user_id, source_name, reference_month) DO NOTHING;

INSERT INTO personal_loans (
    id, user_id, person_name, description, original_amount, installment_amount,
    installments_total, installments_paid, start_date, status
)
SELECT seed.id, users.id, seed.person_name, seed.description, seed.original_amount,
       seed.installment_amount, seed.installments_total, seed.installments_paid,
       DATE '2026-10-01', 'ACTIVE'
FROM users
CROSS JOIN (
    VALUES
        ('1f235e4d-5f67-4330-9cf0-2ed58a5aa101'::UUID, 'Carol', 'Dívida pessoal', 1024.36::NUMERIC, 512.18::NUMERIC, 20, 16),
        ('1f235e4d-5f67-4330-9cf0-2ed58a5aa102'::UUID, 'Mãe', 'Dívida pessoal', 1425.00::NUMERIC, 475.00::NUMERIC, 8, 5),
        ('1f235e4d-5f67-4330-9cf0-2ed58a5aa103'::UUID, 'Mãe', 'Presente Mãe', 4731.13::NUMERIC, 473.113::NUMERIC, 12, 2)
) AS seed(id, person_name, description, original_amount, installment_amount, installments_total, installments_paid)
WHERE users.email = 'mtcarmona50@gmail.com';
