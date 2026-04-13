#!/usr/bin/env python3
"""
Seed PostgreSQL database completely from JSON file.
Supports all entities: categories, tags, users, contracts, CVs, offer photos, offers, applications.
Requires psycopg2: pip install psycopg2-binary
"""

import json
import os
import sys
from pathlib import Path
from datetime import datetime
import uuid
import traceback


import psycopg2
from psycopg2.extras import execute_batch


CONTRACT_STATUS_VALUES = {
    "WAITING": 0,
    "ACCEPTED": 1,
    "DECLINED": 2,
}


def get_db_connection():
    """Create PostgreSQL connection using environment variables."""
    connection = psycopg2.connect(
        host=os.getenv("PGHOST", "localhost"),
        port=os.getenv("PGPORT", "5432"),
        user=os.getenv("PGUSER", "admin"),
        password=os.getenv("PGPASSWORD", "admin"),
        database=os.getenv("PGDATABASE", "jobFinderApp"),
    )
    return connection


def load_seed_data():
    """Load JSON seed data from file."""
    base_dir = Path(__file__).parent.parent
    seed_file = base_dir / "seed_data.json"
    
    if not seed_file.exists():
        raise FileNotFoundError(f"Seed file not found: {seed_file}")
    
    with open(seed_file, "r", encoding="utf-8") as f:
        return json.load(f)


def seed_categories(cursor, categories_data):
    """Insert categories."""
    print(f"Seeding {len(categories_data)} categories...")
    
    now = datetime.now().isoformat()
    
    for category in categories_data:
        cursor.execute("""
            INSERT INTO categories (id, name, color, created_at, updated_at)
            VALUES (%s, %s, %s, %s, %s)
            ON CONFLICT (id) DO NOTHING
        """, (
            category["id"],
            category["name"],
            category["color"],
            now,
            now,
        ))
    print(f"Categories done")


def seed_tags(cursor, tags_data):
    """Insert tags (depends on categories)."""
    print(f"Seeding {len(tags_data)} tags...")
    
    now = datetime.now().isoformat()
    
    for tag in tags_data:
        cursor.execute("""
            INSERT INTO tags (id, name, category_id, created_at, updated_at)
            VALUES (%s, %s, %s, %s, %s)
            ON CONFLICT (id) DO NOTHING
        """, (
            tag["id"],
            tag["name"],
            tag.get("categoryId"),
            now,
            now,
        ))
    print(f"Tags done")


def seed_users(cursor, users_data):
    """Insert users."""
    print(f"Seeding {len(users_data)} users...")
    
    now = datetime.now().isoformat()
    
    for user in users_data:
        cursor.execute("""
            INSERT INTO users 
            (id, email, first_name, last_name, username, password_hash, 
             phone_number, profile_description, role, created_at, updated_at)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
            ON CONFLICT (id) DO NOTHING
        """, (
            user.get("id", str(uuid.uuid4())),
            user["email"],
            user["firstName"],
            user["lastName"],
            user["username"],
            user["passwordHash"],
            user.get("phoneNumber"),
            user.get("profileDescription"),
            user.get("role", "USER"),
            now,
            now,
        ))
    print(f"Users done")


def seed_contracts(cursor, contracts_data):
    """Insert contracts."""
    print(f"Seeding {len(contracts_data)} contracts...")
    
    now = datetime.now().isoformat()
    
    for contract in contracts_data:
        contractor_acceptance = contract.get("contractorAcceptance", "WAITING")
        if isinstance(contractor_acceptance, str):
            contractor_acceptance = CONTRACT_STATUS_VALUES[contractor_acceptance]

        cursor.execute("""
            INSERT INTO contracts 
            (id, file_name, file_size, mime_type, storage_key, 
             contractor_acceptance, created_at, updated_at)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
            ON CONFLICT (id) DO NOTHING
        """, (
            contract["id"],
            contract["fileName"],
            contract["fileSize"],
            contract["mimeType"],
            contract["storageKey"],
            contractor_acceptance,
            now,
            now,
        ))
    print(f"Contracts done")


def seed_cvs(cursor, cvs_data, user_ids):
    """Insert CVs (depends on users)."""
    print(f"Seeding {len(cvs_data)} CVs...")
    
    now = datetime.now().isoformat()
    
    for cv in cvs_data:
        username = cv.get("userUsername")
        if not username or username not in user_ids:
            print(f"Skipping CV '{cv['fileName']}' - user '{username}' not found")
            continue
        
        cursor.execute("""
            INSERT INTO cvs 
            (id, file_name, file_size, mime_type, storage_key, user_id, created_at, updated_at)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
            ON CONFLICT (id) DO NOTHING
        """, (
            cv["id"],
            cv["fileName"],
            cv["fileSize"],
            cv["mimeType"],
            cv["storageKey"],
            user_ids[username],
            now,
            now,
        ))
    print(f"CVs done")


def seed_offer_photos(cursor, photos_data):
    """Insert offer photos."""
    print(f"Seeding {len(photos_data)} offer photos...")
    
    now = datetime.now().isoformat()
    
    for photo in photos_data:
        cursor.execute("""
            INSERT INTO offer_photo 
            (id, file_name, file_size, mime_type, storage_key, created_at, updated_at)
            VALUES (%s, %s, %s, %s, %s, %s, %s)
            ON CONFLICT (id) DO NOTHING
        """, (
            photo["id"],
            photo["fileName"],
            photo["fileSize"],
            photo["mimeType"],
            photo["storageKey"],
            now,
            now,
        ))
    print(f"Offer photos done")


def seed_offers(cursor, offers_data, user_ids):
    """Insert offers (depends on users, contracts, offer photos)."""
    print(f"Seeding {len(offers_data)} offers...")
    
    now = datetime.now().isoformat()
    offer_map = {}  # Map title -> id for later use
    
    for offer in offers_data:
        if not offer.get("ownerUsername"):
            print(f"Skipping offer '{offer['title']}' - no owner specified")
            continue
        
        owner_username = offer["ownerUsername"]
        if owner_username not in user_ids:
            print(f"Skipping offer '{offer['title']}' - user '{owner_username}' not found")
            continue
        
        offer_id = offer.get("id", str(uuid.uuid4()))
        
        cursor.execute("""
            INSERT INTO offers 
            (id, title, description, date_and_time, salary, status, 
             max_applications, owner_id, photo_id, contract_id, created_at, updated_at)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
            ON CONFLICT (id) DO NOTHING
        """, (
            offer_id,
            offer["title"],
            offer.get("description", ""),
            offer.get("dateAndTime"),
            offer.get("salary", 0.0),
            offer.get("status", "OPEN"),
            offer.get("maxApplications", 5),
            user_ids[owner_username],
            offer.get("photoId"),
            offer.get("contractId"),
            now,
            now,
        ))
        
        offer_map[offer["title"]] = (offer_id, owner_username)
    
    print(f"Offers done")
    return offer_map


def seed_applications(cursor, applications_data, user_ids, offer_map, cv_ids):
    """Insert applications (depends on users, offers, CVs)."""
    print(f"Seeding {len(applications_data)} applications...")
    
    now = datetime.now().isoformat()
    
    for app in applications_data:
        candidate_username = app.get("candidateUsername")
        offer_title = app.get("offerTitle")
        cv_filename = app.get("cvFileName")
        
        if not candidate_username or candidate_username not in user_ids:
            print(f"Skipping application - candidate '{candidate_username}' not found")
            continue
        
        if not offer_title or offer_title not in offer_map:
            print(f"Skipping application - offer '{offer_title}' not found")
            continue
        
        if cv_filename not in cv_ids:
            print(f"Skipping application - CV '{cv_filename}' not found")
            continue
        
        offer_id, _ = offer_map[offer_title]
        
        cursor.execute("""
            INSERT INTO applications 
            (id, candidate_id, offer_id, chosen_cv_id, status, applied_at, updated_at)
            VALUES (%s, %s, %s, %s, %s, %s, %s)
            ON CONFLICT (id) DO NOTHING
        """, (
            app.get("id", str(uuid.uuid4())),
            user_ids[candidate_username],
            offer_id,
            cv_ids[cv_filename],
            app.get("status", "SENT"),
            now,
            now,
        ))
    
    print(f"Applications done")


def seed_offer_tags(cursor, offer_tags_data, offer_map, tag_ids):
    """Insert offer-tag associations."""
    print(f"Seeding offer-tag associations...")
    
    count = 0
    for ot in offer_tags_data:
        offer_title = ot.get("offerTitle")
        tag_names = ot.get("tagNames", [])
        
        if offer_title not in offer_map:
            print(f"Offer '{offer_title}' not found")
            continue
        
        offer_id, _ = offer_map[offer_title]
        
        for tag_name in tag_names:
            if tag_name not in tag_ids:
                print(f"Tag '{tag_name}' not found")
                continue
            
            tag_id = tag_ids[tag_name]
            
            cursor.execute("""
                INSERT INTO offers_tags (offer_id, tags_id)
                VALUES (%s, %s)
                ON CONFLICT DO NOTHING
            """, (offer_id, tag_id))
            
            count += 1
    
    print(f"Offer-tag associations done ({count} links)")


def seed_jobs(cursor, jobs_data, user_ids):
    """Insert jobs (depends on users, contracts, job photo, job dispatcher)."""
    print(f"Seeding {len(jobs_data)} jobs...")

    now = datetime.now().isoformat()
    job_map = {}

    for job in jobs_data:
        owner_id = user_ids[job["ownerUsername"]]
        contractor_id = user_ids[job["contractorUsername"]]

        job_id = job.get("id", str(uuid.uuid4()))

        cursor.execute(
            """
            INSERT INTO jobs
            (id, title, description, date_and_time, salary, status,
             owner_id, contractor_id, contract_id, photo_id, job_dispatcher_id,
             created_at, updated_at)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
            ON CONFLICT (id) DO NOTHING
        """,
            (
                job_id,
                job["title"],
                job.get("description", ""),
                job.get("dateAndTime"),
                job.get("salary", 0.0),
                job.get("status", "READY"),
                owner_id,
                contractor_id,
                job.get("contractId"),
                job.get("photoId"),
                job.get("jobDispatcherId"),
                now,
                now,
            ),
        )

        job_map[job["title"]] = job_id

    print("Jobs done")
    return job_map


def seed_job_tags(cursor, job_tags_data, job_map, tag_ids):
    """Insert job-tag associations."""
    print("Seeding job-tag associations...")

    count = 0
    for jt in job_tags_data:
        job_title = jt.get("jobTitle")
        tag_names = jt.get("tagNames", [])

        if job_title not in job_map:
            print(f"Job '{job_title}' not found")
            continue

        job_id = job_map[job_title]

        for tag_name in tag_names:
            if tag_name not in tag_ids:
                print(f"Tag '{tag_name}' not found")
                continue

            tag_id = tag_ids[tag_name]

            cursor.execute(
                """
                INSERT INTO jobs_tags (job_id, tags_id)
                VALUES (%s, %s)
                ON CONFLICT DO NOTHING
            """,
                (job_id, tag_id),
            )

            count += 1

    print(f"Job-tag associations done ({count} links)")


def build_lookup_maps(seed_data):
    """Build lookup maps for foreign key resolution."""
    user_ids = {}
    tag_ids = {}
    cv_ids = {}
    
    # Build user lookup
    for user in seed_data.get("users", []):
        user_ids[user["username"]] = user.get("id", "")
    
    # Build tag lookup
    for tag in seed_data.get("tags", []):
        tag_ids[tag["name"]] = tag.get("id", "")
    
    # Build CV lookup
    for cv in seed_data.get("cvs", []):
        cv_ids[cv["fileName"]] = cv.get("id", "")
    
    return user_ids, tag_ids, cv_ids


def seed():
    """Main seeding function."""
    try:
        print("Starting complete database seeding from JSON...\n")
        
        # Load data
        seed_data = load_seed_data()
        
        # Connect to database
        conn = get_db_connection()
        cursor = conn.cursor()
        
        try:
            # Order matters: foreign keys must be seeded first
            
            # 1. Categories (no dependencies)
            if "categories" in seed_data:
                seed_categories(cursor, seed_data["categories"])
            
            # 2. Tags (depend on categories)
            if "tags" in seed_data:
                seed_tags(cursor, seed_data["tags"])
            
            # 3. Users (no dependencies)
            if "users" in seed_data:
                seed_users(cursor, seed_data["users"])
            
            # 4. Contracts (no dependencies)
            if "contracts" in seed_data:
                seed_contracts(cursor, seed_data["contracts"])
            
            # Build lookup maps
            user_ids, tag_ids, cv_ids = build_lookup_maps(seed_data)
            
            # 5. CVs (depend on users)
            if "cvs" in seed_data:
                seed_cvs(cursor, seed_data["cvs"], user_ids)
            
            # 6. Offer photos (no dependencies)
            if "offerPhotos" in seed_data:
                seed_offer_photos(cursor, seed_data["offerPhotos"])
            
            # 7. Offers (depend on users, contracts, offer photos)
            offer_map = {}
            if "offers" in seed_data:
                offer_map = seed_offers(cursor, seed_data["offers"], user_ids)
            
            # 8. Applications (depend on users, offers, CVs)
            if "applications" in seed_data:
                seed_applications(cursor, seed_data["applications"], user_ids, offer_map, cv_ids)
            
            # 9. Offer-tag associations (depend on offers and tags)
            if "offerTags" in seed_data:
                seed_offer_tags(cursor, seed_data["offerTags"], offer_map, tag_ids)

            # 10. Jobs (depend on users, optional contract/photo/dispatcher)
            job_map = {}
            if "jobs" in seed_data:
                job_map = seed_jobs(cursor, seed_data["jobs"], user_ids)

            # 11. Job-tag associations (depend on jobs and tags)
            if "jobTags" in seed_data:
                seed_job_tags(cursor, seed_data["jobTags"], job_map, tag_ids)
            
            conn.commit()
            print("\nDatabase seeding completed successfully!")
            
        except Exception as e:
            conn.rollback()
            print(f"\nError during seeding: {e}", file=sys.stderr)
            traceback.print_exc()
            raise
        finally:
            cursor.close()
            conn.close()
            
    except Exception as e:
        print(f"\nFailed to seed database: {e}", file=sys.stderr)
        sys.exit(1)


if __name__ == "__main__":
    seed()