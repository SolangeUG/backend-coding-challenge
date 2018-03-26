-- Database: expensesdb

-- initial VAT rate data
-- NB: only one entry should have the ENABLED value set to TRUE

INSERT INTO vat_rate (rate, enabled, created_on, updated_on) VALUES (20, TRUE, now(), now());

INSERT INTO vat_rate (rate, enabled, created_on, updated_on) VALUES (17.5, FALSE, now(), now());