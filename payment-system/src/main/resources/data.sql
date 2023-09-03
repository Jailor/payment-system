INSERT INTO exchange_rate_table (id, version, source_currency, destination_currency, from_date, ratio, status)
VALUES (11882, 0, 'USD', 'EUR', '2023-08-21 14:05:23.000000', 0.85, 'ACTIVE');

INSERT INTO exchange_rate_table (id, version, source_currency, destination_currency, from_date, ratio, status)
VALUES (11883, 0, 'EUR', 'USD', '2023-08-21 14:05:24.000000', 1.18, 'ACTIVE');

INSERT INTO exchange_rate_table (id, version, source_currency, destination_currency, from_date, ratio, status)
VALUES (11884, 0, 'USD', 'RON', '2023-08-21 14:05:25.000000', 4.13, 'ACTIVE');

INSERT INTO exchange_rate_table (id, version, source_currency, destination_currency, from_date, ratio, status)
VALUES (11885, 0, 'RON', 'USD', '2023-08-21 14:05:26.000000', 0.24, 'ACTIVE');

INSERT INTO exchange_rate_table (id, version, source_currency, destination_currency, from_date, ratio, status)
VALUES (11886, 0, 'EUR', 'RON', '2023-08-21 14:05:27.000000', 4.86, 'ACTIVE');

INSERT INTO exchange_rate_table (id, version, source_currency, destination_currency, from_date, ratio, status)
VALUES (11887, 0, 'RON', 'EUR', '2023-08-21 14:05:28.000000', 0.21, 'ACTIVE');


INSERT into profile_table (id, version, name, status, profile_type, rights) VALUES (
                                                                                       11876, 0, 'admin', 'ACTIVE', 'ADMINISTRATOR', 'LIST_USER,LIST_ACCOUNT,LIST_PROFILE,CREATE_USER,CREATE_ACCOUNT,CREATE_PROFILE,MODIFY_USER,MODIFY_ACCOUNT,MODIFY_PROFILE,BLOCK_USER,UNBLOCK_USER,REMOVE_USER, REMOVE_ACCOUNT, REMOVE_PROFILE,APPROVE_USER,APPROVE_ACCOUNT,APPROVE_PROFILE,REJECT_USER,REJECT_ACCOUNT,REJECT_PROFILE,LIST_PAYMENT,CREATE_PAYMENT, REPAIR_PAYMENT, VERIFY_PAYMENT, APPROVE_PAYMENT, CANCEL_PAYMENT, AUTHORIZE_PAYMENT');

INSERT into user_table
(id, profile_id, version, address, email, full_name, password, status, username)
VALUES
    (11877, 11876, 0, 'admin address', 'admin@gmail.com', 'admin full rights', '$31$16$cpAd4a85d-xIZDkWXswk0DxO4JB1q_f92Mfa9a145uU','ACTIVE', 'admin');

INSERT into user_table
(id, profile_id, version, address, email, full_name, password, status, username)
VALUES
    (11976, 11876, 0, 'andrei address', 'andrei@gmail.com', 'andrei full rights', '$31$16$cpAd4a85d-xIZDkWXswk0DxO4JB1q_f92Mfa9a145uU','ACTIVE', 'andrei');

INSERT into user_table
(id, profile_id, version, address, email, full_name, password, status, username)
VALUES
    (11927, 11876, 0, 'mobile app', 'mobileapp@gmail.com', 'mobile app rights', '$31$16$cpAd4a85d-xIZDkWXswk0DxO4JB1q_f92Mfa9a145uU','ACTIVE', 'mobile');

INSERT into customer_table
(id, version, name, address, country,state, city, phone_number, email, status)
VALUES
    (11879, 0, 'admin customer', 'Ceahlau 78, Cluj-Napoca, Romania','Romania', 'Cluj County', 'Cluj-Napoca','0724582770', 'admin@gmail.com', 'ACTIVE');

INSERT into customer_table
(id, version, name, address, country,state, city,phone_number, email, status)
VALUES
    (11880, 0, 'andrei pelle', 'Ceahlau 78, Cluj-Napoca, Romania','Romania', 'Cluj County', 'Cluj-Napoca', '0724582771', 'andrei@gmail.com', 'ACTIVE');

INSERT into account_table
(id, version, account_number, currency, status, account_status, owner_id)
VALUES
    (11878, 0, 'ACC57293336312', 'USD', 'ACTIVE', 'OPEN', 11879);

INSERT into account_table
(id, version, account_number, currency, status, account_status, owner_id)
VALUES
    (11879, 0, 'ACC92685165732', 'USD', 'ACTIVE', 'OPEN', 11880);

INSERT into balance
(id, version, account_id, time_stamp, ACA, ADA, ACC, ADC, PCA, PDA, PCC, PDC)
VALUES
    (11880, 0, 11878, '2020-01-01 20:15:17.184045', 1200000, 200000, 0, 0, 0, 0, 0, 0);

INSERT into balance
(id, version, account_id, time_stamp, ACA, ADA, ACC, ADC, PCA, PDA, PCC, PDC)
VALUES
    (11881, 0, 11879, '2020-08-08 20:15:17.184045', 800000, 200000, 0, 0, 0, 0, 0, 0);
