-- Database: expensesdb

-- vat : store VAT rate values
CREATE TABLE IF NOT EXISTS vat_rate (
    rateid          INT         PRIMARY KEY,
    rate            REAL        DEFAULT 20 NOT NULL,
    enabled         BOOLEAN     DEFAULT TRUE NOT NULL,
    created_on      TIMESTAMP   DEFAULT now() NOT NULL,
    updated_on      TIMESTAMP   DEFAULT now() NOT NULL
);

-- expenses : store user expenses
CREATE TABLE IF NOT EXISTS expenses (
    expId           INT         PRIMARY KEY,
    rateid          INT         REFERENCES vat_rate(rateid),
    expvalue        REAL        NOT NULL,
    expdate         DATE        NOT NULL,
    expreason       TEXT        NOT NULL,
    created_on      TIMESTAMP   DEFAULT now() NOT NULL,
    updated_on      TIMESTAMP   DEFAULT now() NOT NULL
);