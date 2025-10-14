--
-- PostgreSQL database dump
--

\restrict mczkkRvkwFmoJF5VbbqpzrlMQZUxU9P0AdexsGgKRGhtRCjNAma5VssildsLcoP

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
-- Data for Name: locations; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.locations (id, city, coordinates, created_at) VALUES ('33333333-3333-3333-3333-333333333333', 0, '{54.352,18.6466}', '2025-10-13');


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.users (id, balance, created_at, email, password_hash, phone_number, profile_description, rating, updated_at, username) VALUES ('11111111-1111-1111-1111-111111111111', 100, '2025-10-13', 'owner1@example.com', 'hash1', 123456789, 'Opis właściciela', 4.5, '2025-10-13 20:22:04', 'owner1');
INSERT INTO public.users (id, balance, created_at, email, password_hash, phone_number, profile_description, rating, updated_at, username) VALUES ('22222222-2222-2222-2222-222222222222', 50, '2025-10-13', 'contractor1@example.com', 'hash2', 987654321, 'Opis wykonawcy', 4.8, '2025-10-13 20:22:04', 'contractor1');


--
-- Data for Name: archived_jobs; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.archived_jobs (id, created_at, description, salary, start_time, title, updated_at, contractor_id, location_id, owner_id) VALUES ('77777777-7777-7777-7777-777777777777', '2025-10-13', 'Opis archiwalny', 80, '2025-10-12 09:00:00', 'Archiwum 1', '2025-10-13 20:22:04', '22222222-2222-2222-2222-222222222222', '33333333-3333-3333-3333-333333333333', '11111111-1111-1111-1111-111111111111');


--
-- Data for Name: tags; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.tags (id, created_at, name) VALUES ('44444444-4444-4444-4444-444444444444', '2025-10-13', 'Sprzątanie');
INSERT INTO public.tags (id, created_at, name) VALUES ('55555555-5555-5555-5555-555555555555', '2025-10-13', 'Ogród');
INSERT INTO public.tags (id, created_at, name) VALUES ('99555555-5555-5555-5555-555555555555', '2025-10-14', 'Korepetycje');

--
-- Data for Name: archived_jobs_tags; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.archived_jobs_tags (archived_job_id, tags_id) VALUES ('77777777-7777-7777-7777-777777777777', '44444444-4444-4444-4444-444444444444');


--
-- Data for Name: jobs; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.jobs (id, created_at, description, salary, start_time, status, title, updated_at, contractor_id, location_id, owner_id) VALUES ('66666666-6666-6666-6666-666666666666', '2025-10-13', 'Opis zadania 1', 120, '2025-10-14 10:00:00', 0, 'Job 1', '2025-10-13 20:22:04', '22222222-2222-2222-2222-222222222222', '33333333-3333-3333-3333-333333333333', '11111111-1111-1111-1111-111111111111');


--
-- Data for Name: jobs_tags; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.jobs_tags (job_id, tags_id) VALUES ('66666666-6666-6666-6666-666666666666', '44444444-4444-4444-4444-444444444444');
INSERT INTO public.jobs_tags (job_id, tags_id) VALUES ('66666666-6666-6666-6666-666666666666', '55555555-5555-5555-5555-555555555555');


--
-- Data for Name: ratings; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.ratings (id, created_at, description, rate, updated_at, author_id, receiver_id) VALUES ('88888888-8888-8888-8888-888888888888', '2025-10-13', 'Super współpraca!', 5, '2025-10-13 20:22:04', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222');


--
-- PostgreSQL database dump complete
--

\unrestrict mczkkRvkwFmoJF5VbbqpzrlMQZUxU9P0AdexsGgKRGhtRCjNAma5VssildsLcoP

