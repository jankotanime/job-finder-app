--
-- PostgreSQL database dump
--

\restrict 2QlPEFVczb6FFVIxXNobyvlMOWzCXYzizXcM0Cdx1zhSlfDUbprNwSbx85ceydi

-- Dumped from database version 16.10
-- Dumped by pg_dump version 16.10

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Data for Name: contracts; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.contracts (id, created_at, file_name, file_size, mime_type, storage_key, updated_at, contractor_acceptance) VALUES ('e23e3491-f839-44ad-acd1-1dbde5843477', '2026-01-05 22:43:38.503057', 'certyfikat oc.pdf', 93778, 'PDF', 'documents/contracts/3fe9d487-3a63-46a4-a10e-a4bbbf4d5cc1-certyfikat oc.pdf', '2026-01-05 22:43:38.50308', 0);
INSERT INTO public.contracts (id, created_at, file_name, file_size, mime_type, storage_key, updated_at, contractor_acceptance) VALUES ('2f7a3739-bcce-4565-b85d-184424dae6d0', '2026-01-05 22:48:00.537666', 'dokument.docx', 17925, 'DOCX', 'documents/contracts/7b03e19d-8865-434e-8780-a681d0b78449-dokument.docx', '2026-01-05 22:48:00.537687', 0);


--
-- Data for Name: profile_photo; Type: TABLE DATA; Schema: public; Owner: admin
--



--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.users (id, created_at, email, first_name, google_id, last_name, password_hash, phone_number, profile_description, role, updated_at, username, profile_photo_id) VALUES ('6380e192-6e0e-460c-ae65-5caf69903e66', '2025-12-21 20:03:01.707664', 'email@gmail.com', 'Janusz', NULL, 'Pierwszy', '{cfvghjkl}$2a$10$htdsmb7c2hs0t6LqutjYo.uW45Bx0/2rgfTNzPf0VwDS2iKNHx782', 123456789, 'asdasdasdasdasda', 'USER', '2025-12-29 19:01:44.42659', 'user', NULL);
INSERT INTO public.users (id, created_at, email, first_name, google_id, last_name, password_hash, phone_number, profile_description, role, updated_at, username, profile_photo_id) VALUES ('44fadc16-c788-446c-a7d7-ceba96b6b74d', '2025-12-21 20:05:34.14962', 'email2@gmail.com', 'Janusz', NULL, 'Drugi', '{cfvghjkl}$2a$10$xA6PpKUQp6/8lPwzV6uNNuKBED4LKFTvBKTp.Nt1dpKtOVIC060Sq', 223456789, 'asdaasdasda', 'USER', '2025-12-29 19:02:50.342035', 'user2', NULL);
INSERT INTO public.users (id, created_at, email, first_name, google_id, last_name, password_hash, phone_number, profile_description, role, updated_at, username, profile_photo_id) VALUES ('5110bd94-58d5-4ea4-af58-669ebf4c8976', '2025-12-21 20:06:22.307042', 'email3@gmail.com', 'Zbyszek', NULL, 'Pierwszy', '{cfvghjkl}$2a$10$o771RcnKKs.cri29DihLtenyp69ebukEczS.gC3LT773auSPBGb8O', 323456789, 'gsfggsdasda', 'USER', '2025-12-29 19:03:20.060871', 'user3', NULL);
INSERT INTO public.users (id, created_at, email, first_name, google_id, last_name, password_hash, phone_number, profile_description, role, updated_at, username, profile_photo_id) VALUES ('a88e2a4a-53dd-40d2-960c-cd59cb452de2', '2025-12-21 20:07:02.984003', 'email4@gmail.com', 'Zbyszek', NULL, 'Drugi', '{cfvghjkl}$2a$10$xw4A48UHBG7WO90KRKRJkeeb.1R06r.jH1WDDcA5feHWggJ6q0zkO', 423456789, 'gsfgafdfagsdasda', 'USER', '2025-12-29 19:03:48.608836', 'user4', NULL);
INSERT INTO public.users (id, created_at, email, first_name, google_id, last_name, password_hash, phone_number, profile_description, role, updated_at, username, profile_photo_id) VALUES ('0b6cc9bc-2369-4008-933d-3f1270dbd1ea', '2025-12-21 20:07:31.323406', 'email5@gmail.com', 'Zbyszek', NULL, 'Trzeci', '{cfvghjkl}$2a$10$xT0QUxsU83dPbr/1BvcG...KzTG4RyVknOyk0gAEuKOoQKKUib0Du', 523456789, 'gfgafdfagsdasda', 'USER', '2025-12-29 19:04:21.853363', 'user5', NULL);
INSERT INTO public.users (id, created_at, email, first_name, google_id, last_name, password_hash, phone_number, profile_description, role, updated_at, username, profile_photo_id) VALUES ('d3e6ae82-18c0-4412-b5f9-3ce86a9d9106', '2026-01-05 21:00:16.589091', 'majami.technology@gmail.com', 'Admin', NULL, 'Admin', '{cfvghjkl}$2a$10$JohQ4.IWAD9MgrwRiExDZewkSe9ChuLchjyc/YiENwNvrjliEA9jW', 725636136, 'admin user jestem', 'ADMIN', '2026-01-05 21:00:16.58914', 'Admin', NULL);


--
-- Data for Name: cvs; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.cvs (id, created_at, file_name, file_size, mime_type, storage_key, updated_at, user_id) VALUES ('6840cbee-f618-4810-943e-6524ee525e3a', '2025-12-21 20:31:31.79351', 'dokument.docx', 17925, 'DOCX', 'documents/083a2ec9-d853-40df-ab1c-73e15cf8c675-dokument.docx', '2025-12-21 20:31:31.79352', '5110bd94-58d5-4ea4-af58-669ebf4c8976');
INSERT INTO public.cvs (id, created_at, file_name, file_size, mime_type, storage_key, updated_at, user_id) VALUES ('325e8c0b-a150-4eee-872c-10f7307449bc', '2025-12-21 20:32:02.646543', 'list motywacyjny.docx', 37188, 'DOCX', 'documents/e2607ed8-fa82-415e-95c6-8e9e176078dd-list motywacyjny.docx', '2025-12-21 20:32:02.646554', '5110bd94-58d5-4ea4-af58-669ebf4c8976');
INSERT INTO public.cvs (id, created_at, file_name, file_size, mime_type, storage_key, updated_at, user_id) VALUES ('9cdbef3a-d3b4-4c0f-b2eb-724a09c20078', '2025-12-21 20:32:58.458137', 'mvp.docx', 15577, 'DOCX', 'documents/33ea42d6-e2d2-45f2-b1f0-a86069108c9f-mvp.docx', '2025-12-21 20:32:58.458145', 'a88e2a4a-53dd-40d2-960c-cd59cb452de2');
INSERT INTO public.cvs (id, created_at, file_name, file_size, mime_type, storage_key, updated_at, user_id) VALUES ('d2e79a57-9890-45ab-8e0b-6e5d1d489a34', '2025-12-21 20:34:14.604545', 'cv.docx', 171906, 'DOCX', 'documents/95d99444-8b01-41ae-9a07-23d82e515a7f-cv.docx', '2025-12-21 20:34:14.604553', 'a88e2a4a-53dd-40d2-960c-cd59cb452de2');
INSERT INTO public.cvs (id, created_at, file_name, file_size, mime_type, storage_key, updated_at, user_id) VALUES ('c27a9cf1-b1cf-46c0-ad4a-ec71687b5c5f', '2025-12-21 20:34:51.714254', 'dokument.docx', 17925, 'DOCX', 'documents/d5e63827-2d2f-4a81-828e-d2b684f7f95e-dokument.docx', '2025-12-21 20:34:51.714263', '0b6cc9bc-2369-4008-933d-3f1270dbd1ea');
INSERT INTO public.cvs (id, created_at, file_name, file_size, mime_type, storage_key, updated_at, user_id) VALUES ('9aea2718-d9c9-4ea0-b30b-9687b8d9ddfd', '2025-12-21 20:35:03.146694', 'mvp_po_skonczeniu_kursu.docx', 15126, 'DOCX', 'documents/0a65b871-86b9-45e6-a76e-19e5b751814b-mvp_po_skonczeniu_kursu.docx', '2025-12-21 20:35:03.146703', '0b6cc9bc-2369-4008-933d-3f1270dbd1ea');


--
-- Data for Name: offer_photo; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.offer_photo (id, created_at, file_name, file_size, mime_type, storage_key, updated_at) VALUES ('588b9294-246d-44b9-9434-bb911ad769f1', '2026-01-05 22:36:02.924788', '52ebb5ba-7a40-4b8e-822a-d5a7884eac8f.jpg', 192514, 'JPG', 'photos/offer-photos/f0af8500-d2c2-4159-8ac9-2e891e3310d4-52ebb5ba-7a40-4b8e-822a-d5a7884eac8f.jpg', '2026-01-05 22:36:02.924824');


--
-- Data for Name: offers; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.offers (id, created_at, date_and_time, description, max_applications, salary, status, title, updated_at, chosen_candidate_id, owner_id, photo_id, contract_id) VALUES ('9ecc5fb9-822a-499c-92ca-63a7f095507b', '2025-12-21 20:24:46.607882', '2026-02-15 10:15:00', 'Praca z React, TypeScript i REST API. Zdalnie lub hybrydowo.', 5, 600, 'OPEN', 'Frontend Developer', '2025-12-21 20:24:46.607893', NULL, '6380e192-6e0e-460c-ae65-5caf69903e66', NULL, NULL);
INSERT INTO public.offers (id, created_at, date_and_time, description, max_applications, salary, status, title, updated_at, chosen_candidate_id, owner_id, photo_id, contract_id) VALUES ('afda2738-ffc8-4106-8dc5-80031ae99b70', '2025-12-21 20:25:07.751177', '2026-03-01 09:00:00', 'API, bazy danych, Node.js lub Python. Praca zdalna.', 4, 700, 'OPEN', 'Backend Developer', '2025-12-21 20:25:07.751186', NULL, '6380e192-6e0e-460c-ae65-5caf69903e66', NULL, NULL);
INSERT INTO public.offers (id, created_at, date_and_time, description, max_applications, salary, status, title, updated_at, chosen_candidate_id, owner_id, photo_id, contract_id) VALUES ('a9aacb09-2f91-4c66-9ebb-1da310837e84', '2025-12-21 20:25:19.951114', '2026-03-10 08:30:00', 'CI/CD, kontenery, automatyzacja infrastruktury.', 3, 750, 'OPEN', 'DevOps Engineer', '2025-12-21 20:25:19.951125', NULL, '6380e192-6e0e-460c-ae65-5caf69903e66', NULL, NULL);
INSERT INTO public.offers (id, created_at, date_and_time, description, max_applications, salary, status, title, updated_at, chosen_candidate_id, owner_id, photo_id, contract_id) VALUES ('cb3c1253-a6f8-4531-a321-949a9f120ad4', '2025-12-21 20:25:33.345467', '2026-04-05 11:00:00', 'Analiza danych, uczenie maszynowe, Python, pandas.', 6, 800, 'OPEN', 'Data Scientist', '2025-12-21 20:25:33.345476', NULL, '6380e192-6e0e-460c-ae65-5caf69903e66', NULL, NULL);
INSERT INTO public.offers (id, created_at, date_and_time, description, max_applications, salary, status, title, updated_at, chosen_candidate_id, owner_id, photo_id, contract_id) VALUES ('2705c257-a829-432d-a1fc-5bc7201c7777', '2025-12-21 20:25:44.868388', '2026-01-20 14:00:00', 'Monitorowanie zabezpieczeń, audyty bezpieczeństwa, SOC.', 4, 650, 'OPEN', 'Cybersecurity Analyst', '2025-12-21 20:25:44.868398', NULL, '6380e192-6e0e-460c-ae65-5caf69903e66', NULL, NULL);
INSERT INTO public.offers (id, created_at, date_and_time, description, max_applications, salary, status, title, updated_at, chosen_candidate_id, owner_id, photo_id, contract_id) VALUES ('c03602e5-59d9-4a64-9e98-ad7e01872d4a', '2025-12-21 20:26:33.843141', '2026-05-12 08:00:00', 'Prace remontowe w mieszkaniach i domach, doświadczenie mile widziane.', 8, 420, 'OPEN', 'Specjalista ds. Remontów', '2025-12-21 20:26:33.843153', NULL, '44fadc16-c788-446c-a7d7-ceba96b6b74d', NULL, NULL);
INSERT INTO public.offers (id, created_at, date_and_time, description, max_applications, salary, status, title, updated_at, chosen_candidate_id, owner_id, photo_id, contract_id) VALUES ('dca29d3c-b419-4639-9d33-843ea08ff30f', '2025-12-21 20:26:44.014283', '2026-05-20 07:30:00', 'Pielęgnacja ogrodów, nasadzenia, sezonowa praca.', 10, 300, 'OPEN', 'Ogrodnik / Konserwator Zieleni', '2025-12-21 20:26:44.014295', NULL, '44fadc16-c788-446c-a7d7-ceba96b6b74d', NULL, NULL);
INSERT INTO public.offers (id, created_at, date_and_time, description, max_applications, salary, status, title, updated_at, chosen_candidate_id, owner_id, photo_id, contract_id) VALUES ('9a48b62d-f137-4e13-8bb5-bab794521960', '2025-12-21 20:26:51.931365', '2026-06-01 09:30:00', 'Wsparcie w laboratorium, przygotowywanie próbek, proste analizy.', 4, 380, 'OPEN', 'Asystent badań biologicznych', '2025-12-21 20:26:51.931373', NULL, '44fadc16-c788-446c-a7d7-ceba96b6b74d', NULL, NULL);
INSERT INTO public.offers (id, created_at, date_and_time, description, max_applications, salary, status, title, updated_at, chosen_candidate_id, owner_id, photo_id, contract_id) VALUES ('af19d830-c4fe-470d-a785-09fb6076b8d4', '2025-12-21 20:27:01.356713', '2026-06-15 17:00:00', 'Korepetycje z fizyki dla uczniów i studentów, przygotowanie do egzaminów.', 1, 45, 'OPEN', 'Korepetytor Fizyki', '2025-12-21 20:27:01.356724', NULL, '44fadc16-c788-446c-a7d7-ceba96b6b74d', NULL, NULL);
INSERT INTO public.offers (id, created_at, date_and_time, description, max_applications, salary, status, title, updated_at, chosen_candidate_id, owner_id, photo_id, contract_id) VALUES ('2fc518ac-bd1e-43da-9491-438b9b24f2ec', '2025-12-21 20:27:15.542414', '2026-07-10 09:00:00', 'Koordynacja wydarzeń naukowych i biznesowych, kontakt z prelegentami.', 6, 500, 'OPEN', 'Organizator Konferencji', '2025-12-21 20:27:15.542425', NULL, '44fadc16-c788-446c-a7d7-ceba96b6b74d', NULL, NULL);
INSERT INTO public.offers (id, created_at, date_and_time, description, max_applications, salary, status, title, updated_at, chosen_candidate_id, owner_id, photo_id, contract_id) VALUES ('a2f67516-4c7e-461f-9294-e3ed09a43c7a', '2025-12-21 20:27:23.049997', '2026-07-18 10:00:00', 'Przygotowanie ekspozycji, współpraca z artystami i instytucjami.', 3, 520, 'OPEN', 'Kurator Wystaw', '2025-12-21 20:27:23.050003', NULL, '44fadc16-c788-446c-a7d7-ceba96b6b74d', NULL, NULL);
INSERT INTO public.offers (id, created_at, date_and_time, description, max_applications, salary, status, title, updated_at, chosen_candidate_id, owner_id, photo_id, contract_id) VALUES ('a455505c-6d63-4e43-b3ad-f536a85736eb', '2025-12-21 20:27:34.311694', '2026-08-01 07:00:00', 'Pomoc przy opiece pacjentów, podstawowe czynności pielęgniarskie, praca w zespole.', 5, 430, 'OPEN', 'Asystent opieki medycznej', '2025-12-21 20:27:34.311702', NULL, '44fadc16-c788-446c-a7d7-ceba96b6b74d', NULL, NULL);
INSERT INTO public.offers (id, created_at, date_and_time, description, max_applications, salary, status, title, updated_at, chosen_candidate_id, owner_id, photo_id, contract_id) VALUES ('cb430bc1-8fc3-4252-82e9-c02976c14586', '2025-12-21 20:27:42.413224', '2026-08-05 09:00:00', 'Opieka domowa nad osobami starszymi, wsparcie w codziennych czynnościach.', 2, 380, 'OPEN', 'Opiekun Seniora', '2025-12-21 20:27:42.413233', NULL, '44fadc16-c788-446c-a7d7-ceba96b6b74d', NULL, NULL);
INSERT INTO public.offers (id, created_at, date_and_time, description, max_applications, salary, status, title, updated_at, chosen_candidate_id, owner_id, photo_id, contract_id) VALUES ('a4482327-d6c2-4a58-84c7-dcc90301ee5e', '2025-12-21 20:27:52.960733', '2026-09-10 16:00:00', 'Warsztaty dla licealistów i studentów — praktyczne zastosowania matematyki.', 15, 350, 'OPEN', 'Prowadzący warsztaty matematyczne', '2025-12-21 20:27:52.960741', NULL, '44fadc16-c788-446c-a7d7-ceba96b6b74d', NULL, NULL);
INSERT INTO public.offers (id, created_at, date_and_time, description, max_applications, salary, status, title, updated_at, chosen_candidate_id, owner_id, photo_id, contract_id) VALUES ('b38cb7d2-f348-454b-b53e-1ea386741b9b', '2025-12-21 20:28:01.333407', '2026-09-20 09:00:00', 'Pomoc przy stoiskach, obsługa zwiedzających, praca przy wydarzeniu edukacyjnym.', 20, 0, 'OPEN', 'Wolontariusz na targi naukowe', '2025-12-21 20:28:01.333414', NULL, '44fadc16-c788-446c-a7d7-ceba96b6b74d', NULL, NULL);
INSERT INTO public.offers (id, created_at, date_and_time, description, max_applications, salary, status, title, updated_at, chosen_candidate_id, owner_id, photo_id, contract_id) VALUES ('8c123554-94d8-44c1-a564-3dcac3cc2fb0', '2026-01-05 22:36:02.932727', '2026-02-20 09:00:00', 'estteststetstststsetestset', 10, 1000, 'OPEN', 'Test', '2026-01-05 22:43:38.505255', '0b6cc9bc-2369-4008-933d-3f1270dbd1ea', '44fadc16-c788-446c-a7d7-ceba96b6b74d', '588b9294-246d-44b9-9434-bb911ad769f1', 'e23e3491-f839-44ad-acd1-1dbde5843477');


--
-- Data for Name: applications; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.applications (id, applied_at, status, updated_at, candidate_id, chosen_cv_id, offer_id) VALUES ('58a96859-45ed-49dd-8038-33e073d82e26', '2026-01-05 22:38:54.837197', 'SENT', '2026-01-05 22:38:54.837226', '0b6cc9bc-2369-4008-933d-3f1270dbd1ea', 'c27a9cf1-b1cf-46c0-ad4a-ec71687b5c5f', '8c123554-94d8-44c1-a564-3dcac3cc2fb0');


--
-- Data for Name: approval_photo; Type: TABLE DATA; Schema: public; Owner: admin
--



--
-- Data for Name: job_dispatcher; Type: TABLE DATA; Schema: public; Owner: admin
--



--
-- Data for Name: approval; Type: TABLE DATA; Schema: public; Owner: admin
--



--
-- Data for Name: categories; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.categories (id, color, created_at, name, updated_at) VALUES ('7d9caf17-5dcb-46b7-ae52-21cdbe7bd533', 2, '2025-12-21 20:08:39.711024', 'Dom', '2025-12-21 20:08:39.711041');
INSERT INTO public.categories (id, color, created_at, name, updated_at) VALUES ('55935584-7a71-4fb0-ae7b-1b20f8132b87', 1, '2025-12-21 20:08:54.492448', 'Technologia', '2025-12-21 20:08:54.492465');
INSERT INTO public.categories (id, color, created_at, name, updated_at) VALUES ('b0622b50-6958-4b1c-a0fd-4cbbeed0615e', 3, '2025-12-21 20:09:14.282483', 'Nauka', '2025-12-21 20:09:14.282499');
INSERT INTO public.categories (id, color, created_at, name, updated_at) VALUES ('e465c1a5-658b-4ba0-931e-ab053eb35811', 0, '2025-12-21 20:09:29.667899', 'Wydarzenia', '2025-12-21 20:09:29.667915');
INSERT INTO public.categories (id, color, created_at, name, updated_at) VALUES ('8ef97dfe-30db-482a-9b61-c6b652cfa8cb', 5, '2025-12-21 20:10:11.327969', 'Opieka', '2025-12-21 20:10:11.327984');


--
-- Data for Name: job_photo; Type: TABLE DATA; Schema: public; Owner: admin
--



--
-- Data for Name: jobs; Type: TABLE DATA; Schema: public; Owner: admin
--



--
-- Data for Name: tags; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.tags (id, created_at, name, updated_at, category_id) VALUES ('9fb0e55c-61e8-4f01-9c3f-4a96d41a5927', '2025-12-21 20:14:35.784457', 'Remont', '2025-12-21 20:14:35.784485', '7d9caf17-5dcb-46b7-ae52-21cdbe7bd533');
INSERT INTO public.tags (id, created_at, name, updated_at, category_id) VALUES ('c5f00bf5-fbfe-4e80-8c50-ceb9ed7ae82d', '2025-12-21 20:14:45.287983', 'Ogrodnictwo', '2025-12-21 20:14:45.288', '7d9caf17-5dcb-46b7-ae52-21cdbe7bd533');
INSERT INTO public.tags (id, created_at, name, updated_at, category_id) VALUES ('1e76aa28-4dad-4290-b93b-4a999ccf9f50', '2025-12-21 20:14:54.355686', 'Programowanie', '2025-12-21 20:14:54.355701', '55935584-7a71-4fb0-ae7b-1b20f8132b87');
INSERT INTO public.tags (id, created_at, name, updated_at, category_id) VALUES ('80ef37ed-5ce8-4bbb-a206-55c5cda83d45', '2025-12-21 20:15:04.712036', 'SztucznaInteligencja', '2025-12-21 20:15:04.712048', '55935584-7a71-4fb0-ae7b-1b20f8132b87');
INSERT INTO public.tags (id, created_at, name, updated_at, category_id) VALUES ('a067d74e-5d34-4433-a140-606583955558', '2025-12-21 20:15:11.952786', 'Cyberbezpieczenstwo', '2025-12-21 20:15:11.952801', '55935584-7a71-4fb0-ae7b-1b20f8132b87');
INSERT INTO public.tags (id, created_at, name, updated_at, category_id) VALUES ('7ae052d5-5b6a-48de-81c0-a10296d39fd1', '2025-12-21 20:15:18.057325', 'Biologia', '2025-12-21 20:15:18.057341', 'b0622b50-6958-4b1c-a0fd-4cbbeed0615e');
INSERT INTO public.tags (id, created_at, name, updated_at, category_id) VALUES ('a11e2209-a019-4cdc-a58b-0f108f992425', '2025-12-21 20:15:23.702857', 'Fizyka', '2025-12-21 20:15:23.70287', 'b0622b50-6958-4b1c-a0fd-4cbbeed0615e');
INSERT INTO public.tags (id, created_at, name, updated_at, category_id) VALUES ('799f1817-a07b-4423-be28-296affc1f7c1', '2025-12-21 20:15:32.16447', 'Matematyka', '2025-12-21 20:15:32.164488', 'b0622b50-6958-4b1c-a0fd-4cbbeed0615e');
INSERT INTO public.tags (id, created_at, name, updated_at, category_id) VALUES ('bd077af5-effb-4c60-b8b9-d7ba58fffe5f', '2025-12-21 20:15:38.729319', 'Konferencje', '2025-12-21 20:15:38.729336', 'e465c1a5-658b-4ba0-931e-ab053eb35811');
INSERT INTO public.tags (id, created_at, name, updated_at, category_id) VALUES ('01391a74-3964-4708-a50d-bb139c23da03', '2025-12-21 20:15:45.71392', 'Wystawy', '2025-12-21 20:15:45.713935', 'e465c1a5-658b-4ba0-931e-ab053eb35811');
INSERT INTO public.tags (id, created_at, name, updated_at, category_id) VALUES ('398475af-3435-42f8-be03-5475c2dfc3f2', '2025-12-21 20:15:52.418259', 'Zdrowie', '2025-12-21 20:15:52.418273', '8ef97dfe-30db-482a-9b61-c6b652cfa8cb');
INSERT INTO public.tags (id, created_at, name, updated_at, category_id) VALUES ('c4cc43d7-cfe7-48be-8f63-8c5022e88c4e', '2025-12-21 20:16:03.298479', 'OpiekaSeniora', '2025-12-21 20:16:03.298493', '8ef97dfe-30db-482a-9b61-c6b652cfa8cb');


--
-- Data for Name: jobs_tags; Type: TABLE DATA; Schema: public; Owner: admin
--



--
-- Data for Name: offers_applications; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.offers_applications (offer_id, applications_id) VALUES ('8c123554-94d8-44c1-a564-3dcac3cc2fb0', '58a96859-45ed-49dd-8038-33e073d82e26');


--
-- Data for Name: offers_tags; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.offers_tags (offer_id, tags_id) VALUES ('9ecc5fb9-822a-499c-92ca-63a7f095507b', '80ef37ed-5ce8-4bbb-a206-55c5cda83d45');
INSERT INTO public.offers_tags (offer_id, tags_id) VALUES ('9ecc5fb9-822a-499c-92ca-63a7f095507b', '1e76aa28-4dad-4290-b93b-4a999ccf9f50');
INSERT INTO public.offers_tags (offer_id, tags_id) VALUES ('afda2738-ffc8-4106-8dc5-80031ae99b70', 'a067d74e-5d34-4433-a140-606583955558');
INSERT INTO public.offers_tags (offer_id, tags_id) VALUES ('afda2738-ffc8-4106-8dc5-80031ae99b70', '1e76aa28-4dad-4290-b93b-4a999ccf9f50');
INSERT INTO public.offers_tags (offer_id, tags_id) VALUES ('a9aacb09-2f91-4c66-9ebb-1da310837e84', 'a067d74e-5d34-4433-a140-606583955558');
INSERT INTO public.offers_tags (offer_id, tags_id) VALUES ('a9aacb09-2f91-4c66-9ebb-1da310837e84', '1e76aa28-4dad-4290-b93b-4a999ccf9f50');
INSERT INTO public.offers_tags (offer_id, tags_id) VALUES ('cb3c1253-a6f8-4531-a321-949a9f120ad4', '799f1817-a07b-4423-be28-296affc1f7c1');
INSERT INTO public.offers_tags (offer_id, tags_id) VALUES ('cb3c1253-a6f8-4531-a321-949a9f120ad4', '80ef37ed-5ce8-4bbb-a206-55c5cda83d45');
INSERT INTO public.offers_tags (offer_id, tags_id) VALUES ('2705c257-a829-432d-a1fc-5bc7201c7777', 'a067d74e-5d34-4433-a140-606583955558');
INSERT INTO public.offers_tags (offer_id, tags_id) VALUES ('2705c257-a829-432d-a1fc-5bc7201c7777', '1e76aa28-4dad-4290-b93b-4a999ccf9f50');
INSERT INTO public.offers_tags (offer_id, tags_id) VALUES ('c03602e5-59d9-4a64-9e98-ad7e01872d4a', '9fb0e55c-61e8-4f01-9c3f-4a96d41a5927');
INSERT INTO public.offers_tags (offer_id, tags_id) VALUES ('c03602e5-59d9-4a64-9e98-ad7e01872d4a', 'c5f00bf5-fbfe-4e80-8c50-ceb9ed7ae82d');
INSERT INTO public.offers_tags (offer_id, tags_id) VALUES ('dca29d3c-b419-4639-9d33-843ea08ff30f', 'c5f00bf5-fbfe-4e80-8c50-ceb9ed7ae82d');
INSERT INTO public.offers_tags (offer_id, tags_id) VALUES ('9a48b62d-f137-4e13-8bb5-bab794521960', '7ae052d5-5b6a-48de-81c0-a10296d39fd1');
INSERT INTO public.offers_tags (offer_id, tags_id) VALUES ('9a48b62d-f137-4e13-8bb5-bab794521960', '799f1817-a07b-4423-be28-296affc1f7c1');
INSERT INTO public.offers_tags (offer_id, tags_id) VALUES ('af19d830-c4fe-470d-a785-09fb6076b8d4', 'a11e2209-a019-4cdc-a58b-0f108f992425');
INSERT INTO public.offers_tags (offer_id, tags_id) VALUES ('af19d830-c4fe-470d-a785-09fb6076b8d4', '799f1817-a07b-4423-be28-296affc1f7c1');
INSERT INTO public.offers_tags (offer_id, tags_id) VALUES ('2fc518ac-bd1e-43da-9491-438b9b24f2ec', 'bd077af5-effb-4c60-b8b9-d7ba58fffe5f');
INSERT INTO public.offers_tags (offer_id, tags_id) VALUES ('2fc518ac-bd1e-43da-9491-438b9b24f2ec', '01391a74-3964-4708-a50d-bb139c23da03');
INSERT INTO public.offers_tags (offer_id, tags_id) VALUES ('a2f67516-4c7e-461f-9294-e3ed09a43c7a', '01391a74-3964-4708-a50d-bb139c23da03');
INSERT INTO public.offers_tags (offer_id, tags_id) VALUES ('a2f67516-4c7e-461f-9294-e3ed09a43c7a', 'bd077af5-effb-4c60-b8b9-d7ba58fffe5f');
INSERT INTO public.offers_tags (offer_id, tags_id) VALUES ('a455505c-6d63-4e43-b3ad-f536a85736eb', '398475af-3435-42f8-be03-5475c2dfc3f2');
INSERT INTO public.offers_tags (offer_id, tags_id) VALUES ('a455505c-6d63-4e43-b3ad-f536a85736eb', 'c4cc43d7-cfe7-48be-8f63-8c5022e88c4e');
INSERT INTO public.offers_tags (offer_id, tags_id) VALUES ('cb430bc1-8fc3-4252-82e9-c02976c14586', 'c4cc43d7-cfe7-48be-8f63-8c5022e88c4e');
INSERT INTO public.offers_tags (offer_id, tags_id) VALUES ('cb430bc1-8fc3-4252-82e9-c02976c14586', '398475af-3435-42f8-be03-5475c2dfc3f2');
INSERT INTO public.offers_tags (offer_id, tags_id) VALUES ('a4482327-d6c2-4a58-84c7-dcc90301ee5e', '799f1817-a07b-4423-be28-296affc1f7c1');
INSERT INTO public.offers_tags (offer_id, tags_id) VALUES ('a4482327-d6c2-4a58-84c7-dcc90301ee5e', 'a11e2209-a019-4cdc-a58b-0f108f992425');
INSERT INTO public.offers_tags (offer_id, tags_id) VALUES ('b38cb7d2-f348-454b-b53e-1ea386741b9b', 'bd077af5-effb-4c60-b8b9-d7ba58fffe5f');
INSERT INTO public.offers_tags (offer_id, tags_id) VALUES ('b38cb7d2-f348-454b-b53e-1ea386741b9b', '7ae052d5-5b6a-48de-81c0-a10296d39fd1');
INSERT INTO public.offers_tags (offer_id, tags_id) VALUES ('8c123554-94d8-44c1-a564-3dcac3cc2fb0', 'bd077af5-effb-4c60-b8b9-d7ba58fffe5f');


--
-- Data for Name: users_cvs; Type: TABLE DATA; Schema: public; Owner: admin
--



--
-- PostgreSQL database dump complete
--

\unrestrict 2QlPEFVczb6FFVIxXNobyvlMOWzCXYzizXcM0Cdx1zhSlfDUbprNwSbx85ceydi

