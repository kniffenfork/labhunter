--
-- BASIC USER TABLE WITH APPLIED SECURITY
--

DROP TABLE IF EXISTS users CASCADE;
CREATE TABLE users (
                                    id BIGSERIAL NOT NULL PRIMARY KEY,
                                    first_name VARCHAR(255) NOT NULL,
                                    last_name VARCHAR(255) NOT NULL,
                                    phone_number VARCHAR(255),
                                    email VARCHAR(255) NOT NULL,
                                    vk VARCHAR(255),
                                    password VARCHAR(255) NOT NULL,
                                    status VARCHAR(255) NOT NULL DEFAULT 'ACTIVE',
                                    role VARCHAR(255) NOT NULL,
                                    accepted_mailing BOOLEAN NOT NULL DEFAULT FALSE,
                                    is_verificated BOOLEAN NOT NULL DEFAULT FALSE,
                                    UNIQUE(email)
);

CREATE INDEX ON users(first_name, last_name);

INSERT INTO users(first_name, last_name, phone_number, email, vk, password, status, role, accepted_mailing, is_verificated) VALUES ('Alexey', 'Fomin', null, 'fomin.as@phystech.edu',
                          null, '$2y$12$QmyAPAiEXhlop4D.XB8wlex6fUTzfF0zcsOU8snTjphaFNbIuc4A2',
                          'ACTIVE', 'ADMIN', true, true);
--
-- BASIC TABLES FOR COMPANIES AND VACANCIES
--

DROP TABLE IF EXISTS companies CASCADE;
CREATE TABLE companies (
                                            id BIGSERIAL NOT NULL PRIMARY KEY,
                                            name VARCHAR(255) NOT NULL,
                                            description VARCHAR(255),
                                            website VARCHAR(255)
);

CREATE INDEX ON companies(name);

DROP TABLE IF EXISTS vacancies CASCADE;
CREATE TABLE vacancies (
                                         id BIGSERIAL NOT NULL PRIMARY KEY,
                                         description VARCHAR(255),
                                         schedule VARCHAR(255),
                                         experience VARCHAR(255) NOT NULL,
                                         archived BOOLEAN DEFAULT false,
                                         name VARCHAR(255) NOT NULL,
                                         company_id BIGINT NOT NULL REFERENCES companies(id),
                                         employer_id BIGINT NOT NULL REFERENCES users(id)
);

CREATE INDEX ON vacancies(name);

DROP TABLE IF EXISTS vacancies_salaries CASCADE;
CREATE TABLE vacancies_salaries (
                                                  salary_to BIGINT,
                                                  salary_from BIGINT,
                                                  gross BOOLEAN NOT NULL,
                                                  vacancy_id BIGINT NOT NULL PRIMARY KEY REFERENCES vacancies(id)
);

DROP TABLE IF EXISTS vacancies_skills CASCADE;
CREATE TABLE vacancies_skills (
                                                name VARCHAR(255) NOT NULL,
                                                vacancy_id BIGINT NOT NULL REFERENCES vacancies(id),
                                                id BIGSERIAL NOT NULL PRIMARY KEY
);

DROP TABLE IF EXISTS vacancies_addresses CASCADE;
CREATE TABLE vacancies_addresses (
                                                   city VARCHAR(255) NOT NULL,
                                                   street VARCHAR(255) NOT NULL,
                                                   building VARCHAR(255) NOT NULL,
                                                   description VARCHAR(255),
                                                   vacancy_id BIGINT NOT NULL PRIMARY KEY REFERENCES vacancies(id)
);

DROP TABLE IF EXISTS employers_companies CASCADE;
CREATE TABLE employers_companies (
                                              employer_id BIGINT NOT NULL REFERENCES users(id),
                                              company_id BIGINT NOT NULL REFERENCES companies(id),
                                              PRIMARY KEY(employer_id, company_id)
);

--
-- BASIC TABLES FOR EMPLOYEES
--

DROP TABLE IF EXISTS CVS CASCADE;
CREATE TABLE IF NOT EXISTS CVs (
                                   id BIGSERIAL NOT NULL PRIMARY KEY,
                                   employee_id BIGINT NOT NULL REFERENCES users(id),
                                   description VARCHAR(255),
                                   schedule VARCHAR(255) NOT NULL,
                                   experience VARCHAR(255) NOT NULL,
                                   archived BOOLEAN NOT NULL DEFAULT false,
                                   name VARCHAR(255) NOT NULL,
                                   salary INTEGER
);

CREATE INDEX ON CVs(name);

DROP TABLE IF EXISTS CV_key_skills CASCADE;
CREATE TABLE CV_key_skills (
                                             id BIGSERIAL NOT NULL PRIMARY KEY,
                                             name VARCHAR(255) NOT NULL,
                                             CV_id BIGINT NOT NULL REFERENCES CVs(id)
);

DROP TABLE IF EXISTS CV_courses CASCADE;
CREATE TABLE CV_courses (
                                          id BIGSERIAL NOT NULL PRIMARY KEY,
                                          date_of_completion DATE NOT NULL,
                                          name VARCHAR(255) NOT NULL,
                                          organization VARCHAR(255) NOT NULL,
                                          description VARCHAR(255),
                                          CV_id BIGINT NOT NULL REFERENCES CVs(id)
);
